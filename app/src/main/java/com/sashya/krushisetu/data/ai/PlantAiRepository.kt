package com.sashya.krushisetu.data.ai

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.Base64
import com.sashya.krushisetu.BuildConfig
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL

data class PlantScanDetails(
    val cropName: String,
    val plantPart: String,
    val symptoms: String,
    val duration: String
)

data class PlantAnalysis(
    val possibleIssue: String,
    val confidence: String,
    val observedSigns: List<String>,
    val immediateActions: List<String>,
    val prevention: String,
    val expertAdvice: String,
    val disclaimer: String
)

sealed interface PlantAnalysisResult {
    data class Success(val analysis: PlantAnalysis) : PlantAnalysisResult
    data class Failure(val message: String) : PlantAnalysisResult
}

class PlantAiRepository {
    private val mainHandler = Handler(Looper.getMainLooper())

    fun analyzePlant(
        bitmap: Bitmap,
        details: PlantScanDetails,
        onResult: (PlantAnalysisResult) -> Unit
    ) {
        if (BuildConfig.PLANT_AI_API_KEY.isBlank()) {
            onResult(
                PlantAnalysisResult.Failure(
                    "Krushi AI Assist is not configured. Add your AI key to local.properties, then rebuild the app."
                )
            )
            return
        }

        Thread {
            val result = runCatching {
                val requestJson = createRequest(bitmap, details)
                val responseJson = sendRequest(requestJson)
                parseAnalysis(responseJson)
            }.fold(
                onSuccess = { PlantAnalysisResult.Success(it) },
                onFailure = {
                    PlantAnalysisResult.Failure(
                        it.message ?: "Plant analysis could not be completed. Please try again."
                    )
                }
            )
            mainHandler.post { onResult(result) }
        }.start()
    }

    private fun createRequest(bitmap: Bitmap, details: PlantScanDetails): JSONObject {
        val imageData = bitmap.toBase64Jpeg()
        val farmerDetails = listOf(
            "Crop: " + details.cropName.ifBlank { "Not specified" },
            "Plant part photographed: " + details.plantPart.ifBlank { "Not specified" },
            "Symptoms noticed: " + details.symptoms.ifBlank { "Not specified" },
            "How long symptoms have been present: " + details.duration.ifBlank { "Not specified" }
        ).joinToString(separator = "\n- ", prefix = "- ")
        val prompt = """
            You are Krushi AI Assist, an agricultural support assistant for farmers in India.
            Analyze the supplied plant photo together with the farmer's context. This is an initial visual screening, not a laboratory diagnosis.

            Farmer-provided details:
            $farmerDetails

            Instructions:
            - Identify only a possible issue. If the photo or context is unclear, say "Insufficient evidence from photo".
            - Describe only visible signs and do not invent observations.
            - Give low-risk immediate actions such as isolating affected leaves, improving observation, or consulting a local agriculture expert.
            - Do not state a definite diagnosis.
            - Do not recommend pesticide brands, exact chemical mixtures, or application doses.
            - Encourage an agriculture expert when verification or treatment is required.
            - Use simple, farmer-friendly English.
        """.trimIndent()

        val textPart = JSONObject().put("text", prompt)
        val imagePart = JSONObject().put(
            "inline_data",
            JSONObject()
                .put("mime_type", "image/jpeg")
                .put("data", imageData)
        )
        return JSONObject()
            .put(
                "contents",
                JSONArray().put(
                    JSONObject().put(
                        "parts",
                        JSONArray()
                            .put(textPart)
                            .put(imagePart)
                    )
                )
            )
            .put(
                "generationConfig",
                JSONObject()
                    .put("temperature", 0.2)
                    .put("maxOutputTokens", 700)
                    .put("responseMimeType", "application/json")
                    .put("responseSchema", responseSchema)
            )
    }

    private fun sendRequest(request: JSONObject): JSONObject {
        val connection = URL(apiUrl).openConnection() as HttpURLConnection
        return try {
            connection.requestMethod = "POST"
            connection.connectTimeout = 20_000
            connection.readTimeout = 30_000
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
            connection.setRequestProperty("x-goog-api-key", BuildConfig.PLANT_AI_API_KEY)

            connection.outputStream.bufferedWriter(Charsets.UTF_8).use { writer ->
                writer.write(request.toString())
            }

            val responseCode = connection.responseCode
            val responseBody = (if (responseCode in 200..299) connection.inputStream else connection.errorStream)
                ?.bufferedReader()
                ?.use { it.readText() }
                .orEmpty()

            if (responseCode !in 200..299) {
                val message = runCatching {
                    JSONObject(responseBody).getJSONObject("error").optString("message")
                }.getOrDefault("")
                throw IllegalStateException(
                    message.ifBlank { "AI analysis request failed with code " + responseCode + "." }
                )
            }
            JSONObject(responseBody)
        } finally {
            connection.disconnect()
        }
    }

    private fun parseAnalysis(response: JSONObject): PlantAnalysis {
        val responseText = response
            .optJSONArray("candidates")
            ?.optJSONObject(0)
            ?.optJSONObject("content")
            ?.optJSONArray("parts")
            ?.optJSONObject(0)
            ?.optString("text")

        if (responseText.isNullOrBlank()) {
            throw IllegalStateException("Krushi AI Assist did not return an analysis. Please take a clearer photo and try again.")
        }

        val analysis = JSONObject(responseText)
        return PlantAnalysis(
            possibleIssue = analysis.optString("possibleIssue", "Insufficient evidence from photo"),
            confidence = analysis.optString("confidence", "Low"),
            observedSigns = analysis.optJSONArray("observedSigns").toStringList(),
            immediateActions = analysis.optJSONArray("immediateActions").toStringList(),
            prevention = analysis.optString("prevention", "Monitor the plant and keep the area clean."),
            expertAdvice = analysis.optString("expertAdvice", "Consult a qualified agriculture expert for confirmation."),
            disclaimer = analysis.optString(
                "disclaimer",
                "This AI result is an initial screening only, not a confirmed diagnosis."
            )
        )
    }

    private fun Bitmap.toBase64Jpeg(): String {
        val output = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 82, output)
        return Base64.encodeToString(output.toByteArray(), Base64.NO_WRAP)
    }

    private fun JSONArray?.toStringList(): List<String> {
        if (this == null) return emptyList()
        return List(length()) { index -> optString(index) }.filter { it.isNotBlank() }
    }

    private companion object {
        const val apiUrl =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"

        val responseSchema = JSONObject()
            .put("type", "OBJECT")
            .put(
                "properties",
                JSONObject()
                    .put("possibleIssue", stringSchema("The possible condition, or Insufficient evidence from photo."))
                    .put("confidence", stringSchema("Low, medium, or high confidence for this initial screening."))
                    .put("observedSigns", stringListSchema("Visible signs from the image only."))
                    .put("immediateActions", stringListSchema("Low-risk next actions that do not include pesticide brands or doses."))
                    .put("prevention", stringSchema("Simple prevention or monitoring advice."))
                    .put("expertAdvice", stringSchema("When or why to contact an agriculture expert."))
                    .put("disclaimer", stringSchema("A short statement that this is not a confirmed diagnosis."))
            )
            .put(
                "required",
                JSONArray()
                    .put("possibleIssue")
                    .put("confidence")
                    .put("observedSigns")
                    .put("immediateActions")
                    .put("prevention")
                    .put("expertAdvice")
                    .put("disclaimer")
            )

        fun stringSchema(description: String) = JSONObject()
            .put("type", "STRING")
            .put("description", description)

        fun stringListSchema(description: String) = JSONObject()
            .put("type", "ARRAY")
            .put("description", description)
            .put("items", JSONObject().put("type", "STRING"))
    }
}

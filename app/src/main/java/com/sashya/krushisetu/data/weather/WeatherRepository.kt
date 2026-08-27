package com.sashya.krushisetu.data.weather

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

data class WeatherData(
    val location: String,
    val temperature: String,
    val condition: String,
    val rainChance: String,
    val humidity: String,
    val weatherIcon: String
)

class WeatherRepository {

    suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double,
        locationName: String
    ): Result<WeatherData> = withContext(Dispatchers.IO) {

        var connection: HttpURLConnection? = null

        try {

            val urlString =
                "https://api.open-meteo.com/v1/forecast" +
                        "?latitude=$latitude" +
                        "&longitude=$longitude" +
                        "&current=temperature_2m,relative_humidity_2m,weather_code" +
                        "&hourly=precipitation_probability" +
                        "&forecast_hours=1" +
                        "&timezone=auto"

            val url = URL(urlString)

            connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "GET"
            connection.connectTimeout = 15_000
            connection.readTimeout = 15_000
            connection.useCaches = false
            connection.setRequestProperty(
                "Accept",
                "application/json"
            )
            connection.setRequestProperty(
                "User-Agent",
                "KrushiSetu/1.0"
            )

            val responseCode = connection.responseCode

            if (responseCode !in 200..299) {

                val errorMessage = try {

                    connection.errorStream
                        ?.bufferedReader()
                        ?.use { it.readText() }

                } catch (exception: Exception) {
                    null
                }

                return@withContext Result.failure(
                    Exception(
                        if (!errorMessage.isNullOrBlank()) {
                            "Weather API error HTTP $responseCode: $errorMessage"
                        } else {
                            "Weather API error HTTP $responseCode"
                        }
                    )
                )
            }

            val response = connection.inputStream
                .bufferedReader()
                .use { it.readText() }

            if (response.isBlank()) {
                return@withContext Result.failure(
                    Exception("Weather API returned an empty response.")
                )
            }

            val json = JSONObject(response)

            val current = json.optJSONObject("current")
                ?: return@withContext Result.failure(
                    Exception("Weather API response has no current weather data.")
                )

            val temperature = current.optDouble(
                "temperature_2m",
                Double.NaN
            )

            if (temperature.isNaN()) {
                return@withContext Result.failure(
                    Exception("Temperature data is missing.")
                )
            }

            val humidity = current.optInt(
                "relative_humidity_2m",
                -1
            )

            if (humidity < 0) {
                return@withContext Result.failure(
                    Exception("Humidity data is missing.")
                )
            }

            val weatherCode = current.optInt(
                "weather_code",
                -1
            )

            if (weatherCode < 0) {
                return@withContext Result.failure(
                    Exception("Weather condition data is missing.")
                )
            }

            // -------------------------------------------------
            // HOURLY RAIN PROBABILITY
            // -------------------------------------------------

            val hourly = json.optJSONObject("hourly")

            val precipitationProbability =
                if (hourly != null) {

                    val precipitationArray =
                        hourly.optJSONArray(
                            "precipitation_probability"
                        )

                    if (
                        precipitationArray != null &&
                        precipitationArray.length() > 0
                    ) {
                        precipitationArray.optInt(0, 0)
                    } else {
                        0
                    }

                } else {
                    0
                }

            val condition =
                weatherCodeToCondition(weatherCode)

            val icon =
                weatherCodeToIcon(weatherCode)

            val weatherData = WeatherData(

                location = locationName,

                temperature = String.format(
                    Locale.US,
                    "%.0f°C",
                    temperature
                ),

                condition = condition,

                rainChance = "$precipitationProbability%",

                humidity = "$humidity%",

                weatherIcon = icon
            )

            Result.success(weatherData)

        } catch (exception: Exception) {

            Result.failure(
                Exception(
                    exception.message
                        ?: "Unable to connect to the weather service."
                )
            )

        } finally {

            connection?.disconnect()
        }
    }

    // ---------------------------------------------------------
    // WEATHER CONDITION
    // ---------------------------------------------------------

    private fun weatherCodeToCondition(
        weatherCode: Int
    ): String {

        return when (weatherCode) {

            0 -> "Clear sky"

            1 -> "Mainly clear"

            2 -> "Partly cloudy"

            3 -> "Overcast"

            45, 48 -> "Foggy"

            51, 53, 55 -> "Drizzle"

            56, 57 -> "Freezing drizzle"

            61, 63, 65 -> "Rain"

            66, 67 -> "Freezing rain"

            71, 73, 75, 77 -> "Snow"

            80, 81, 82 -> "Rain showers"

            85, 86 -> "Snow showers"

            95 -> "Thunderstorm"

            96, 99 -> "Thunderstorm with hail"

            else -> "Unknown"
        }
    }

    // ---------------------------------------------------------
    // WEATHER ICON
    // ---------------------------------------------------------

    private fun weatherCodeToIcon(
        weatherCode: Int
    ): String {

        return when (weatherCode) {

            0 -> "☀️"

            1 -> "🌤️"

            2 -> "⛅"

            3 -> "☁️"

            45, 48 -> "🌫️"

            51, 53, 55,
            56, 57 -> "🌦️"

            61, 63, 65,
            66, 67 -> "🌧️"

            71, 73, 75, 77,
            85, 86 -> "🌨️"

            80, 81, 82 -> "🌦️"

            95, 96, 99 -> "⛈️"

            else -> "🌤️"
        }
    }
}
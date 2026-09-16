package com.sashya.krushisetu.feature.plantscan

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.sashya.krushisetu.data.ai.PlantAiRepository
import com.sashya.krushisetu.data.ai.PlantAnalysis
import com.sashya.krushisetu.data.ai.PlantAnalysisResult
import com.sashya.krushisetu.data.ai.PlantScanDetails
import com.sashya.krushisetu.data.local.LanguageManager
import com.sashya.krushisetu.ui.components.ScreenHeader
import com.sashya.krushisetu.ui.theme.AlertOrange
import com.sashya.krushisetu.ui.theme.LightLeafGreen
import com.sashya.krushisetu.ui.theme.MutedText

@Composable
fun PlantScanScreen(
    modifier: Modifier = Modifier,
    onOpenConsultation: () -> Unit
) {
    val context = LocalContext.current
    val plantAiRepository = remember { PlantAiRepository() }
    val isHindi = LanguageManager.isHindi()

    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var cropName by remember { mutableStateOf("") }
    var plantPart by remember { mutableStateOf("") }
    var symptoms by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var isAnalyzing by remember { mutableStateOf(false) }
    var analysis by remember { mutableStateOf<PlantAnalysis?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            capturedBitmap = bitmap
            analysis = null
            errorMessage = null
        } else {
            errorMessage =
                if (isHindi) {
                    "फोटो लेना रद्द कर दिया गया। कृपया फिर से प्रयास करें।"
                } else {
                    "Photo capture was cancelled. Please try again."
                }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraLauncher.launch(null)
        } else {
            errorMessage =
                if (isHindi) {
                    "पौधे की जाँच के लिए कैमरा अनुमति आवश्यक है।"
                } else {
                    "Camera permission is needed to scan a plant."
                }
        }
    }

    fun openCamera() {
        val cameraGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (cameraGranted) {
            cameraLauncher.launch(null)
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    fun analyzePlant() {
        val image = capturedBitmap ?: run {
            errorMessage =
                if (isHindi) {
                    "जाँच शुरू करने से पहले पौधे की एक स्पष्ट फोटो लें।"
                } else {
                    "Take a clear plant photo before starting the analysis."
                }
            return
        }

        if (
            cropName.isBlank() ||
            plantPart.isBlank() ||
            symptoms.isBlank()
        ) {
            errorMessage =
                if (isHindi) {
                    "बेहतर उत्तर के लिए फसल, पौधे का भाग और दिखाई देने वाले लक्षण दर्ज करें।"
                } else {
                    "Add the crop, plant part, and visible symptoms so Krushi AI Assist can give a better answer."
                }
            return
        }

        isAnalyzing = true
        analysis = null
        errorMessage = null

        plantAiRepository.analyzePlant(
            bitmap = image,
            details = PlantScanDetails(
                cropName = cropName,
                plantPart = plantPart,
                symptoms = symptoms,
                duration = duration
            )
        ) { result ->
            isAnalyzing = false

            when (result) {
                is PlantAnalysisResult.Success -> {
                    analysis = result.analysis
                }

                is PlantAnalysisResult.Failure -> {
                    errorMessage = result.message
                }
            }
        }
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 28.dp)
    ) {

        item {
            ScreenHeader(
                title = if (isHindi) {
                    "पौधे की जाँच 📷"
                } else {
                    "Plant Scan 📷"
                },
                subtitle = if (isHindi) {
                    "AI की सहायता से पौधे की स्वास्थ्य जाँच के लिए फोटो लें।"
                } else {
                    "Capture a plant photo for an AI-assisted health check."
                }
            )
        }

        item {
            ScanGuide()
        }

        item {
            if (capturedBitmap == null) {
                CaptureCard(
                    onCapture = ::openCamera
                )
            } else {
                CapturedPhoto(
                    bitmap = capturedBitmap!!,
                    onRetake = ::openCamera
                )
            }
        }

        item {
            Text(
                text = if (isHindi) {
                    "Krushi AI Assist को फोटो समझने में मदद करें"
                } else {
                    "Help Krushi AI Assist understand the photo"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                )
            )

            Text(
                text = if (isHindi) {
                    "स्पष्ट जानकारी से परिणाम अधिक उपयोगी होगा। * से चिह्नित फ़ील्ड आवश्यक हैं।"
                } else {
                    "Clear details make the result more useful. Fields marked with * are required."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MutedText,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            PlantDetailsForm(
                cropName = cropName,
                onCropNameChange = { cropName = it },
                plantPart = plantPart,
                onPlantPartChange = { plantPart = it },
                symptoms = symptoms,
                onSymptomsChange = { symptoms = it },
                duration = duration
            ) { duration = it }
        }

        item {
            Button(
                onClick = ::analyzePlant,
                enabled = !isAnalyzing,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 16.dp
                    ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (isAnalyzing) {
                        if (isHindi) {
                            "Krushi AI Assist जाँच कर रहा है..."
                        } else {
                            "Krushi AI Assist is analysing..."
                        }
                    } else {
                        if (isHindi) {
                            "✦ Krushi AI Assist से जाँच करें"
                        } else {
                            "✦ Analyse with Krushi AI Assist"
                        }
                    },
                    modifier = Modifier.padding(vertical = 5.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (errorMessage != null) {
            item {
                ErrorCard(errorMessage!!)
            }
        }

        if (analysis != null) {
            item {
                AnalysisResultCard(
                    analysis = analysis!!,
                    onOpenConsultation = onOpenConsultation
                )
            }
        }
    }
}

@Composable
private fun ScanGuide() {
    val isHindi = LanguageManager.isHindi()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = LightLeafGreen
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                "💡",
                fontSize = 24.sp
            )

            Spacer(
                Modifier.width(10.dp)
            )

            Text(
                text = if (isHindi) {
                    "बेहतर परिणाम के लिए दिन के उजाले में प्रभावित पत्ती या पौधे के एक भाग की फोटो लें। फोटो स्पष्ट रखें और पीछे का हिस्सा साफ रखें।"
                } else {
                    "For the best result, photograph one affected leaf or plant part in daylight. Keep it in focus and avoid a busy background."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun CaptureCard(
    onCapture: () -> Unit
) {
    val isHindi = LanguageManager.isHindi()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            ),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "🌿",
                fontSize = 52.sp
            )

            Text(
                text = if (isHindi) {
                    "अभी पौधे की फोटो नहीं है"
                } else {
                    "No plant photo yet"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = if (isHindi) {
                    "प्रभावित पौधे के भाग की फोटो लेने के लिए अपने फोन के कैमरे का उपयोग करें।"
                } else {
                    "Use your phone camera to capture the affected plant part."
                },
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MutedText,
                modifier = Modifier.padding(top = 5.dp)
            )

            Button(
                onClick = onCapture,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    if (isHindi) {
                        "📷 कैमरा खोलें"
                    } else {
                        "📷 Open camera"
                    }
                )
            }
        }
    }
}

@Composable
private fun CapturedPhoto(
    bitmap: Bitmap,
    onRetake: () -> Unit
) {
    val isHindi = LanguageManager.isHindi()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            ),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = if (isHindi) {
                    "पौधे की ली गई फोटो"
                } else {
                    "Captured plant photo"
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            OutlinedButton(
                onClick = onRetake,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    if (isHindi) {
                        "फोटो दोबारा लें"
                    } else {
                        "Retake photo"
                    }
                )
            }
        }
    }
}

@Composable
private fun PlantDetailsForm(
    cropName: String,
    onCropNameChange: (String) -> Unit,
    plantPart: String,
    onPlantPartChange: (String) -> Unit,
    symptoms: String,
    onSymptomsChange: (String) -> Unit,
    duration: String,
    onDurationChange: (String) -> Unit
) {
    val isHindi = LanguageManager.isHindi()

    Column(
        modifier = Modifier.padding(
            horizontal = 20.dp,
            vertical = 14.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        OutlinedTextField(
            value = cropName,
            onValueChange = onCropNameChange,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    if (isHindi) {
                        "फसल का नाम *"
                    } else {
                        "Crop name *"
                    }
                )
            },
            placeholder = {
                Text(
                    if (isHindi) {
                        "उदाहरण: टमाटर"
                    } else {
                        "Example: Tomato"
                    }
                )
            },
            singleLine = true
        )

        OutlinedTextField(
            value = plantPart,
            onValueChange = onPlantPartChange,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    if (isHindi) {
                        "फोटो में पौधे का भाग *"
                    } else {
                        "Plant part in photo *"
                    }
                )
            },
            placeholder = {
                Text(
                    if (isHindi) {
                        "उदाहरण: नीचे की पत्तियाँ, तना, फल"
                    } else {
                        "Example: Lower leaves, stem, fruit"
                    }
                )
            },
            singleLine = true
        )

        OutlinedTextField(
            value = symptoms,
            onValueChange = onSymptomsChange,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    if (isHindi) {
                        "दिखाई देने वाले लक्षण *"
                    } else {
                        "Visible symptoms *"
                    }
                )
            },
            placeholder = {
                Text(
                    if (isHindi) {
                        "उदाहरण: पीले धब्बे, मुड़ी हुई पत्तियाँ, सफेद पाउडर"
                    } else {
                        "Example: Yellow spots, curled leaves, white powder"
                    }
                )
            },
            minLines = 3
        )

        OutlinedTextField(
            value = duration,
            onValueChange = onDurationChange,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    if (isHindi) {
                        "यह कब से शुरू हुआ?"
                    } else {
                        "When did this begin?"
                    }
                )
            },
            placeholder = {
                Text(
                    if (isHindi) {
                        "उदाहरण: लगभग 3 दिन पहले"
                    } else {
                        "Example: About 3 days ago"
                    }
                )
            },
            singleLine = true
        )
    }
}

@Composable
private fun ErrorCard(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFEDEA)
        )
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(14.dp),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun AnalysisResultCard(
    analysis: PlantAnalysis,
    onOpenConsultation: () -> Unit
) {
    val isHindi = LanguageManager.isHindi()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp,
                vertical = 12.dp
            ),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Text(
                text = if (isHindi) {
                    "Krushi AI Assist का प्रारंभिक आकलन"
                } else {
                    "Krushi AI Assist's initial assessment"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = if (isHindi) {
                    "संभावित समस्या • ${analysis.confidence.uppercase()} विश्वास स्तर"
                } else {
                    "POSSIBLE ISSUE • ${analysis.confidence.uppercase()} CONFIDENCE"
                },
                style = MaterialTheme.typography.labelSmall,
                color = AlertOrange,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp)
            )

            Text(
                text = analysis.possibleIssue,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 3.dp)
            )

            ResultSection(
                title = if (isHindi) {
                    "देखे गए लक्षण"
                } else {
                    "Observed signs"
                },
                items = analysis.observedSigns
            )

            ResultSection(
                title = if (isHindi) {
                    "तुरंत करने योग्य सुझाव"
                } else {
                    "Suggested immediate actions"
                },
                items = analysis.immediateActions
            )

            Text(
                text = if (isHindi) {
                    "रोकथाम और निगरानी"
                } else {
                    "Prevention and monitoring"
                },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 14.dp)
            )

            Text(
                text = analysis.prevention,
                style = MaterialTheme.typography.bodyMedium,
                color = MutedText,
                modifier = Modifier.padding(top = 4.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = LightLeafGreen
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = if (isHindi) {
                            "विशेषज्ञ सलाह"
                        } else {
                            "Expert advice"
                        },
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge
                    )

                    Text(
                        text = analysis.expertAdvice,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 3.dp)
                    )
                }
            }

            Button(
                onClick = onOpenConsultation,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    if (isHindi) {
                        "कृषि विशेषज्ञ से सलाह लें"
                    } else {
                        "Consult an agriculture expert"
                    }
                )
            }

            Text(
                text = analysis.disclaimer,
                style = MaterialTheme.typography.labelSmall,
                color = MutedText,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )
        }
    }
}

@Composable
private fun ResultSection(
    title: String,
    items: List<String>
) {
    if (items.isEmpty()) return

    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 14.dp)
    )

    Column(
        modifier = Modifier.padding(top = 4.dp)
    ) {
        items.forEach { item ->
            Text(
                text = "• $item",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedText,
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }
    }
}
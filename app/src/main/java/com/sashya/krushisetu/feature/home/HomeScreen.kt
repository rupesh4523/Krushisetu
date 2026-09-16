package com.sashya.krushisetu.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.sashya.krushisetu.data.crop.CropRepository
import com.sashya.krushisetu.data.local.LanguageManager
import com.sashya.krushisetu.data.local.SampleData
import com.sashya.krushisetu.data.model.Crop
import com.sashya.krushisetu.data.model.UserProfile
import com.sashya.krushisetu.data.weather.WeatherData
import com.sashya.krushisetu.data.weather.WeatherRepository
import com.sashya.krushisetu.ui.components.AdvisoryCard
import com.sashya.krushisetu.ui.components.ScreenHeader
import com.sashya.krushisetu.ui.components.SectionTitle
import com.sashya.krushisetu.ui.theme.LeafGreen
import com.sashya.krushisetu.ui.theme.MutedText

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    farmerName: String,
    userProfile: UserProfile?,
    onOpenCrops: () -> Unit,
    onOpenAdvisory: () -> Unit,
    onOpenPlantScan: () -> Unit,
    onOpenConsultation: () -> Unit
) {

    // ---------------------------------------------------------
    // WEATHER STATE
    // ---------------------------------------------------------

    var weatherData by remember {
        mutableStateOf<WeatherData?>(null)
    }

    var weatherLoading by remember {
        mutableStateOf(true)
    }

    var weatherError by remember {
        mutableStateOf<String?>(null)
    }


    // ---------------------------------------------------------
    // CROPS STATE
    // ---------------------------------------------------------

    val cropRepository = remember {
        CropRepository()
    }

    var crops by remember {
        mutableStateOf<List<Crop>>(emptyList())
    }


    // ---------------------------------------------------------
    // WEATHER REPOSITORY
    // ---------------------------------------------------------

    val weatherRepository = remember {
        WeatherRepository()
    }


    // ---------------------------------------------------------
    // LISTEN TO THE SAME FIRESTORE CROPS USED BY "MY CROPS"
    // ---------------------------------------------------------

    DisposableEffect(Unit) {

        val listener =
            cropRepository.getCrops { result ->

                result
                    .onSuccess { loadedCrops ->
                        crops = loadedCrops
                    }
                    .onFailure {
                        crops = emptyList()
                    }
            }

        onDispose {
            listener?.remove()
        }
    }


    // ---------------------------------------------------------
    // LOAD FARM WEATHER
    // ---------------------------------------------------------

    LaunchedEffect(
        userProfile?.farmLatitude,
        userProfile?.farmLongitude,
        userProfile?.farmLocation,
        userProfile?.village,
        userProfile?.district
    ) {

        weatherLoading = true
        weatherError = null
        weatherData = null


        // -----------------------------------------------------
        // Get saved farm coordinates
        // -----------------------------------------------------

        val latitude =
            userProfile?.farmLatitude

        val longitude =
            userProfile?.farmLongitude


        // -----------------------------------------------------
        // We need both coordinates.
        // -----------------------------------------------------

        if (latitude == null || longitude == null) {

            weatherLoading = false

            weatherError =
                if (userProfile == null) {

                    if (LanguageManager.isHindi()) {
                        "आपकी खेत प्रोफ़ाइल अभी लोड हो रही है।"
                    } else {
                        "Your farm profile is still loading."
                    }

                } else {

                    if (LanguageManager.isHindi()) {
                        "खेत के स्थान के निर्देशांक उपलब्ध नहीं हैं। कृपया अपने खेत का स्थान अपडेट करें।"
                    } else {
                        "Farm location coordinates are not available. Please update your farm location."
                    }
                }

            return@LaunchedEffect
        }


        // -----------------------------------------------------
        // Build a friendly farm location name
        // -----------------------------------------------------

        val farmLocationName =
            buildString {

                if (!userProfile.farmLocation.isBlank()) {
                    append(userProfile.farmLocation.trim())
                }

                if (
                    !userProfile.village.isBlank() &&
                    userProfile.village.trim()
                        .lowercase() !=
                    userProfile.farmLocation.trim()
                        .lowercase()
                ) {

                    if (isNotEmpty()) {
                        append(", ")
                    }

                    append(userProfile.village.trim())
                }

                if (!userProfile.district.isBlank()) {

                    if (isNotEmpty()) {
                        append(", ")
                    }

                    append(userProfile.district.trim())
                }

            }.ifBlank {

                if (LanguageManager.isHindi()) {
                    "आपका खेत"
                } else {
                    "Your farm"
                }
            }


        // -----------------------------------------------------
        // REQUEST WEATHER FOR FARM LOCATION
        //
        // NOT phone/device location.
        // -----------------------------------------------------

        val weatherResult =
            weatherRepository.getCurrentWeather(
                latitude = latitude,
                longitude = longitude,
                locationName = farmLocationName
            )

        weatherResult
            .onSuccess { weather ->

                weatherData = weather
                weatherError = null
            }
            .onFailure {

                weatherData = null

                weatherError =
                    if (LanguageManager.isHindi()) {
                        "आपके खेत के लिए मौसम लोड नहीं हो सका।"
                    } else {
                        "Unable to load weather for your farm."
                    }
            }

        weatherLoading = false
    }


    // ---------------------------------------------------------
    // HOME UI
    // ---------------------------------------------------------

    LazyColumn(
        modifier = modifier,
        contentPadding =
            androidx.compose.foundation.layout.PaddingValues(
                bottom = 24.dp
            )
    ) {

        item {

            ScreenHeader(

                title =
                    if (LanguageManager.isHindi()) {
                        "नमस्ते, $farmerName! 👋"
                    } else {
                        "Namaste, $farmerName! 👋"
                    },

                subtitle =
                    if (LanguageManager.isHindi()) {
                        "आज आपके खेत की जानकारी यहाँ है।"
                    } else {
                        "Here is your farm update for today."
                    }
            )
        }


        item {

            WeatherHeroCard(
                weather = weatherData,
                isLoading = weatherLoading,
                errorMessage = weatherError
            )
        }


        item {

            PlantScanBanner(
                onOpenPlantScan
            )
        }


        item {

            SectionTitle(
                if (LanguageManager.isHindi()) {
                    "त्वरित कार्य"
                } else {
                    "Quick actions"
                }
            )

            QuickActions(
                onOpenCrops,
                onOpenAdvisory,
                onOpenConsultation
            )
        }


        item {

            SectionTitle(
                if (LanguageManager.isHindi()) {
                    "आज की सलाह"
                } else {
                    "Today's advisory"
                }
            )

            SampleData.advisories.forEach { advisory ->

                AdvisoryCard(
                    advisory = advisory,
                    modifier = Modifier.padding(
                        horizontal = 20.dp,
                        vertical = 6.dp
                    )
                )
            }
        }


        item {

            Spacer(
                Modifier.height(16.dp)
            )
        }
    }
}


// =============================================================
// WEATHER CARD
// =============================================================

@Composable
private fun WeatherHeroCard(
    weather: WeatherData?,
    isLoading: Boolean,
    errorMessage: String?
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),

        shape = RoundedCornerShape(24.dp),

        colors = CardDefaults.cardColors(
            containerColor = LeafGreen
        )
    ) {

        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                if (isLoading) {

                    Text(
                        text =
                            if (LanguageManager.isHindi()) {
                                "आपका खेत"
                            } else {
                                "Your farm"
                            },

                        color = Color.White.copy(
                            alpha = 0.82f
                        ),

                        style =
                            MaterialTheme.typography.labelLarge
                    )

                    Text(
                        text =
                            if (LanguageManager.isHindi()) {
                                "लोड हो रहा है..."
                            } else {
                                "Loading..."
                            },

                        color = Color.White,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,

                        modifier = Modifier.padding(
                            top = 4.dp
                        )
                    )

                    Text(
                        text =
                            if (LanguageManager.isHindi()) {
                                "खेत का मौसम प्राप्त किया जा रहा है"
                            } else {
                                "Getting farm weather"
                            },

                        color = Color.White,

                        style =
                            MaterialTheme.typography.bodyMedium
                    )

                } else if (weather != null) {

                    Text(
                        text = weather.location,

                        color = Color.White.copy(
                            alpha = 0.82f
                        ),

                        style =
                            MaterialTheme.typography.labelLarge
                    )

                    Text(
                        text = weather.temperature,

                        color = Color.White,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,

                        modifier = Modifier.padding(
                            top = 4.dp
                        )
                    )

                    Text(
                        text = weather.condition,

                        color = Color.White,

                        style =
                            MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text =
                            if (LanguageManager.isHindi()) {
                                "बारिश की संभावना ${weather.rainChance}  •  नमी ${weather.humidity}"
                            } else {
                                "Rain chance ${weather.rainChance}  •  Humidity ${weather.humidity}"
                            },

                        color = Color.White.copy(
                            alpha = 0.82f
                        ),

                        style =
                            MaterialTheme.typography.labelMedium,

                        modifier = Modifier.padding(
                            top = 10.dp
                        )
                    )

                } else {

                    Text(
                        text =
                            if (LanguageManager.isHindi()) {
                                "खेत का मौसम"
                            } else {
                                "Farm weather"
                            },

                        color = Color.White.copy(
                            alpha = 0.82f
                        ),

                        style =
                            MaterialTheme.typography.labelLarge
                    )

                    Text(
                        text =
                            if (LanguageManager.isHindi()) {
                                "मौसम उपलब्ध नहीं है"
                            } else {
                                "Weather unavailable"
                            },

                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,

                        modifier = Modifier.padding(
                            top = 4.dp
                        )
                    )

                    Text(
                        text =
                            errorMessage
                                ?: if (LanguageManager.isHindi()) {
                                    "कृपया अपने खेत का स्थान अपडेट करें।"
                                } else {
                                    "Please update your farm location."
                                },

                        color = Color.White,

                        style =
                            MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Text(
                text = weather?.weatherIcon ?: "🌤️",
                fontSize = 62.sp
            )
        }
    }
}


// =============================================================
// PLANT SCAN BANNER
// =============================================================

@Composable
private fun PlantScanBanner(
    onOpenPlantScan: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            )
            .clickable(
                onClick = onOpenPlantScan
            ),

        shape = RoundedCornerShape(20.dp),

        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.primaryContainer
        )
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                "📷",
                fontSize = 30.sp
            )

            Spacer(
                Modifier.width(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text =
                        if (LanguageManager.isHindi()) {
                            "पौधे की जाँच"
                        } else {
                            "Plant Scan"
                        },

                    style =
                        MaterialTheme.typography.titleSmall,

                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    text =
                        if (LanguageManager.isHindi()) {
                            "AI की सहायता से पौधे की सेहत जाँचने के लिए फोटो लें।"
                        } else {
                            "Capture a plant photo for an AI-assisted health check."
                        },

                    style =
                        MaterialTheme.typography.bodySmall,

                    color = MutedText
                )
            }

            Text(
                "→",
                fontSize = 22.sp,
                color = LeafGreen
            )
        }
    }
}


// =============================================================
// QUICK ACTIONS
// =============================================================

@Composable
private fun QuickActions(
    onOpenCrops: () -> Unit,
    onOpenAdvisory: () -> Unit,
    onOpenConsultation: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),

        horizontalArrangement =
            Arrangement.spacedBy(10.dp)
    ) {

        QuickAction(
            "🌱",

            if (LanguageManager.isHindi()) {
                "मेरी फसलें"
            } else {
                "My crops"
            },

            Modifier.weight(1f),

            onOpenCrops
        )

        QuickAction(
            "✦",

            if (LanguageManager.isHindi()) {
                "सलाह लें"
            } else {
                "Get advice"
            },

            Modifier.weight(1f),

            onOpenAdvisory
        )

        QuickAction(
            "◉",

            if (LanguageManager.isHindi()) {
                "विशेषज्ञ से पूछें"
            } else {
                "Ask expert"
            },

            Modifier.weight(1f),

            onOpenConsultation
        )
    }
}


// =============================================================
// QUICK ACTION CARD
// =============================================================

@Composable
private fun QuickAction(
    emoji: String,
    label: String,
    modifier: Modifier,
    onClick: () -> Unit
) {

    Card(
        modifier = modifier.clickable(
            onClick = onClick
        ),

        shape = RoundedCornerShape(18.dp),

        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {

        Column(
            modifier = Modifier.padding(
                vertical = 14.dp,
                horizontal = 8.dp
            ),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                emoji,
                fontSize = 22.sp
            )

            Text(
                label,

                style =
                    MaterialTheme.typography.labelMedium,

                fontWeight =
                    FontWeight.SemiBold,

                modifier = Modifier.padding(
                    top = 5.dp
                )
            )
        }
    }
}


// =============================================================
// CROP SUMMARY
// =============================================================

@Composable
private fun CropSummaryCard(
    crop: Crop
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp,
                vertical = 5.dp
            ),

        shape = RoundedCornerShape(20.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Row(
            modifier = Modifier.padding(16.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                crop.healthEmoji,
                fontSize = 38.sp
            )

            Spacer(
                Modifier.width(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    crop.name,

                    style =
                        MaterialTheme.typography.titleMedium,

                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    crop.variety + " • " + crop.area,

                    style =
                        MaterialTheme.typography.bodySmall,

                    color = MutedText
                )

                Text(
                    crop.stage,

                    style =
                        MaterialTheme.typography.labelMedium,

                    color = LeafGreen,

                    modifier = Modifier.padding(
                        top = 4.dp
                    )
                )
            }

            Text(
                if (LanguageManager.isHindi()) {
                    "लगाया गया " +
                            crop.plantingDate.ifBlank {
                                "तारीख उपलब्ध नहीं"
                            }
                } else {
                    "Planted " +
                            crop.plantingDate.ifBlank {
                                "date not specified"
                            }
                },

                color = LeafGreen,

                style =
                    MaterialTheme.typography.labelSmall
            )
        }
    }
}


// =============================================================
// EMPTY CROPS CARD
// =============================================================

@Composable
private fun EmptyCropsCard(
    onOpenCrops: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable(
                onClick = onOpenCrops
            ),

        shape = RoundedCornerShape(20.dp),

        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {

        Row(
            modifier = Modifier.padding(16.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                text = "🌱",
                fontSize = 34.sp
            )

            Spacer(
                Modifier.width(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text =
                        if (LanguageManager.isHindi()) {
                            "अभी कोई फसल नहीं जोड़ी गई"
                        } else {
                            "No crops added yet"
                        },

                    style =
                        MaterialTheme.typography.titleSmall,

                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    text =
                        if (LanguageManager.isHindi()) {
                            "डैशबोर्ड पर देखने के लिए अपनी पहली फसल जोड़ें।"
                        } else {
                            "Add your first crop to see it on the dashboard."
                        },

                    style =
                        MaterialTheme.typography.bodySmall,

                    color = MutedText
                )
            }

            Text(
                text =
                    if (LanguageManager.isHindi()) {
                        "जोड़ें →"
                    } else {
                        "Add →"
                    },

                color = LeafGreen,

                style =
                    MaterialTheme.typography.labelLarge,

                fontWeight =
                    FontWeight.SemiBold
            )
        }
    }
}
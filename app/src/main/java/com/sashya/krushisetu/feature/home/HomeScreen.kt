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

import com.sashya.krushisetu.data.local.SampleData
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
    // WEATHER REPOSITORY
    // ---------------------------------------------------------

    val weatherRepository = remember {
        WeatherRepository()
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
                    "Your farm profile is still loading."
                } else {
                    "Farm location coordinates are not available. Please update your farm location."
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
                "Your farm"
            }

        // -----------------------------------------------------
        // Request WEATHER FOR FARM LOCATION
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
                    "Unable to load weather for your farm."
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
                title = "Namaste, " + farmerName + "! 👋",
                subtitle = "Here is your farm update for today."
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
                "Quick actions"
            )

            QuickActions(
                onOpenCrops,
                onOpenAdvisory,
                onOpenConsultation
            )
        }

        item {

            SectionTitle(
                "Your crops",
                action = "View all",
                onAction = onOpenCrops
            )

            CropSummaryCard()
        }

        item {

            SectionTitle(
                "Today's advisory",
                action = "View all",
                onAction = onOpenAdvisory
            )

            AdvisoryCard(
                advisory = SampleData.advisories.first(),
                modifier = Modifier.padding(
                    horizontal = 20.dp
                )
            )
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
                        text = "Your farm",
                        color = Color.White.copy(
                            alpha = 0.82f
                        ),
                        style = MaterialTheme.typography.labelLarge
                    )

                    Text(
                        text = "Loading...",
                        color = Color.White,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            top = 4.dp
                        )
                    )

                    Text(
                        text = "Getting farm weather",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )

                } else if (weather != null) {

                    Text(
                        text = weather.location,
                        color = Color.White.copy(
                            alpha = 0.82f
                        ),
                        style = MaterialTheme.typography.labelLarge
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
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text =
                            "Rain chance ${weather.rainChance}  •  Humidity ${weather.humidity}",
                        color = Color.White.copy(
                            alpha = 0.82f
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(
                            top = 10.dp
                        )
                    )

                } else {

                    Text(
                        text = "Farm weather",
                        color = Color.White.copy(
                            alpha = 0.82f
                        ),
                        style = MaterialTheme.typography.labelLarge
                    )

                    Text(
                        text = "Weather unavailable",
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
                                ?: "Please update your farm location.",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
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
                    "Plant Scan",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Capture a plant photo for an AI-assisted health check.",
                    style = MaterialTheme.typography.bodySmall,
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
            "My crops",
            Modifier.weight(1f),
            onOpenCrops
        )

        QuickAction(
            "✦",
            "Get advice",
            Modifier.weight(1f),
            onOpenAdvisory
        )

        QuickAction(
            "◉",
            "Ask expert",
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
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
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
private fun CropSummaryCard() {

    val crop =
        SampleData.crops.first()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
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
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    crop.variety + " • " + crop.area,
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText
                )

                Text(
                    crop.stage,
                    style = MaterialTheme.typography.labelMedium,
                    color = LeafGreen,
                    modifier = Modifier.padding(
                        top = 4.dp
                    )
                )
            }

            Text(
                "● " + crop.healthLabel,
                color = LeafGreen,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
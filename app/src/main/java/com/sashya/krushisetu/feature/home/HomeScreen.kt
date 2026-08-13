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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sashya.krushisetu.data.local.SampleData
import com.sashya.krushisetu.ui.components.AdvisoryCard
import com.sashya.krushisetu.ui.components.ScreenHeader
import com.sashya.krushisetu.ui.components.SectionTitle
import com.sashya.krushisetu.ui.theme.LeafGreen
import com.sashya.krushisetu.ui.theme.MutedText

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    farmerName: String,
    onOpenCrops: () -> Unit,
    onOpenAdvisory: () -> Unit,
    onOpenPlantScan: () -> Unit,
    onOpenConsultation: () -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp)
    ) {
        item {
            ScreenHeader(
                title = "Namaste, " + farmerName + "! 👋",
                subtitle = "Here is your farm update for today."
            )
        }
        item { WeatherHeroCard() }
        item { PlantScanBanner(onOpenPlantScan) }
        item {
            SectionTitle("Quick actions")
            QuickActions(onOpenCrops, onOpenAdvisory, onOpenConsultation)
        }
        item {
            SectionTitle("Your crops", action = "View all", onAction = onOpenCrops)
            CropSummaryCard()
        }
        item {
            SectionTitle("Today's advisory", action = "View all", onAction = onOpenAdvisory)
            AdvisoryCard(
                advisory = SampleData.advisories.first(),
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun PlantScanBanner(onOpenPlantScan: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .clickable(onClick = onOpenPlantScan),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("📷", fontSize = 30.sp)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Plant Scan", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(
                    "Capture a plant photo for an AI-assisted health check.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText
                )
            }
            Text("→", fontSize = 22.sp, color = LeafGreen)
        }
    }
}

@Composable
private fun WeatherHeroCard() {
    val weather = SampleData.weather
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = LeafGreen)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(weather.location, color = Color.White.copy(alpha = 0.82f), style = MaterialTheme.typography.labelLarge)
                Text(
                    weather.temperature,
                    color = Color.White,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(weather.condition, color = Color.White, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "Rain chance " + weather.rainChance + "  •  Humidity " + weather.humidity,
                    color = Color.White.copy(alpha = 0.82f),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
            Text(text = "⛅", fontSize = 62.sp)
        }
    }
}

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
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        QuickAction("🌱", "My crops", Modifier.weight(1f), onOpenCrops)
        QuickAction("✦", "Get advice", Modifier.weight(1f), onOpenAdvisory)
        QuickAction("◉", "Ask expert", Modifier.weight(1f), onOpenConsultation)
    }
}

@Composable
private fun QuickAction(emoji: String, label: String, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 22.sp)
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 5.dp)
            )
        }
    }
}

@Composable
private fun CropSummaryCard() {
    val crop = SampleData.crops.first()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(crop.healthEmoji, fontSize = 38.sp)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(crop.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(crop.variety + " • " + crop.area, style = MaterialTheme.typography.bodySmall, color = MutedText)
                Text(crop.stage, style = MaterialTheme.typography.labelMedium, color = LeafGreen, modifier = Modifier.padding(top = 4.dp))
            }
            Text("● " + crop.healthLabel, color = LeafGreen, style = MaterialTheme.typography.labelSmall)
        }
    }
}

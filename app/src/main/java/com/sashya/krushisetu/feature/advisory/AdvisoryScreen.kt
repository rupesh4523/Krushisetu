package com.sashya.krushisetu.feature.advisory

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sashya.krushisetu.data.local.SampleData
import com.sashya.krushisetu.ui.components.AdvisoryCard
import com.sashya.krushisetu.ui.components.ScreenHeader
import com.sashya.krushisetu.ui.components.SectionTitle
import com.sashya.krushisetu.ui.theme.AlertOrange
import com.sashya.krushisetu.ui.theme.MutedText

@Composable
fun AdvisoryScreen(
    modifier: Modifier = Modifier,
    onOpenPlantScan: () -> Unit
) {

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            ScreenHeader(
                title = "Smart advisory ✦",
                subtitle = "Personalized guidance for your farm."
            )
        }
        item { WeatherAlertBanner() }
        item {
            SectionTitle("Recommendations for today")
        }
        items(SampleData.advisories) { advisory ->
            AdvisoryCard(
                advisory = advisory,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
            )
        }
        item {
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onOpenPlantScan,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("📷 Open Plant Scan", modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}

@Composable
private fun WeatherAlertBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("🌧️  WEATHER ALERT", color = AlertOrange, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text(
                "Rain is expected in your area",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 5.dp)
            )
            Text(
                "Postpone fertilizer spraying until the rain has passed.",
                style = MaterialTheme.typography.bodySmall,
                color = MutedText,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

package com.sashya.krushisetu.feature.consultation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.sashya.krushisetu.data.model.Consultant
import com.sashya.krushisetu.data.model.ConsultationType
import com.sashya.krushisetu.ui.components.ScreenHeader
import com.sashya.krushisetu.ui.components.SectionTitle
import com.sashya.krushisetu.ui.theme.LightLeafGreen
import com.sashya.krushisetu.ui.theme.MutedText

@Composable
fun ConsultationScreen(modifier: Modifier = Modifier) {
    var selectedTypeName by remember { mutableStateOf(ConsultationType.VIDEO_CALL.name) }
    var bookingMessage by remember { mutableStateOf<String?>(null) }
    val selectedType = ConsultationType.valueOf(selectedTypeName)

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            ScreenHeader(
                title = "Consult an expert ◉",
                subtitle = "Get reliable support for your farm."
            )
        }
        item {
            ConsultationTypeSelector(
                selectedType = selectedType,
                onTypeSelected = { selectedTypeName = it.name }
            )
        }
        if (bookingMessage != null) {
            item {
                BookingConfirmation(
                    message = bookingMessage!!,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)
                )
            }
        }
        item {
            SectionTitle(
                if (selectedType == ConsultationType.VIDEO_CALL) "Available for video calls" else "Available for farm visits"
            )
        }
        items(SampleData.consultants) { consultant ->
            ConsultantCard(
                consultant = consultant,
                selectedType = selectedType,
                onBook = {
                    val typeLabel = if (selectedType == ConsultationType.VIDEO_CALL) "video consultation" else "farm visit"
                    bookingMessage = "Your " + typeLabel + " request with " + consultant.name + " has been added."
                }
            )
        }
    }
}

@Composable
private fun ConsultationTypeSelector(
    selectedType: ConsultationType,
    onTypeSelected: (ConsultationType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FilterChip(
            selected = selectedType == ConsultationType.VIDEO_CALL,
            onClick = { onTypeSelected(ConsultationType.VIDEO_CALL) },
            label = { Text("📹 Video call") },
            modifier = Modifier.weight(1f)
        )
        FilterChip(
            selected = selectedType == ConsultationType.FARM_VISIT,
            onClick = { onTypeSelected(ConsultationType.FARM_VISIT) },
            label = { Text("🚜 Farm visit") },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun BookingConfirmation(message: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = LightLeafGreen)
    ) {
        Text(
            text = "✓ " + message,
            modifier = Modifier.padding(14.dp),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ConsultantCard(
    consultant: Consultant,
    selectedType: ConsultationType,
    onBook: () -> Unit
) {
    val fee = if (selectedType == ConsultationType.VIDEO_CALL) consultant.videoFee else consultant.visitFee
    val action = if (selectedType == ConsultationType.VIDEO_CALL) "Book video call" else "Request farm visit"
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(consultant.avatar, fontSize = 42.sp)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(consultant.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(consultant.specialty, style = MaterialTheme.typography.bodySmall, color = MutedText)
                Text(
                    "★ " + consultant.rating + "  •  " + consultant.experience,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(consultant.languages, style = MaterialTheme.typography.labelSmall, color = MutedText)
            }
        }
        Button(
            onClick = onBook,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(action + "  •  ₹" + fee)
        }
    }
}

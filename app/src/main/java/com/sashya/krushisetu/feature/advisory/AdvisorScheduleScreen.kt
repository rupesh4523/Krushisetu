package com.sashya.krushisetu.feature.advisory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sashya.krushisetu.ui.theme.FieldCream
import com.sashya.krushisetu.ui.theme.LeafGreen
import com.sashya.krushisetu.ui.theme.MutedText

@Composable
fun AdvisorScheduleScreen(
    onBack: () -> Unit
)  {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FieldCream)
            .padding(horizontal = 20.dp)
    ) {

        TextButton(
            onClick = onBack
        ) {
            Text(
                text = "← Back to Home",
                color = LeafGreen,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        // =========================================================
        // HEADER
        // =========================================================

        Text(
            text = "Schedule",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Manage appointments and meeting records",
            style = MaterialTheme.typography.bodyMedium,
            color = MutedText,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(
            modifier = Modifier.height(22.dp)
        )

        // =========================================================
        // APPOINTMENTS
        // =========================================================

        Text(
            text = "Upcoming Appointments",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        AppointmentCard(
            date = "Today",
            time = "10:30 AM",
            farmer = "Rajesh Patil",
            purpose = "Tomato crop consultation",
            type = "Video"
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        AppointmentCard(
            date = "Today",
            time = "02:00 PM",
            farmer = "Suresh More",
            purpose = "Onion disease discussion",
            type = "Voice"
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        AppointmentCard(
            date = "Tomorrow",
            time = "11:00 AM",
            farmer = "Meena Jadhav",
            purpose = "Sugarcane irrigation advice",
            type = "Video"
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // =========================================================
        // MEETING REPORTS
        // =========================================================

        Text(
            text = "Meeting Reports",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        MeetingReportCard(
            farmer = "Rajesh Patil",
            topic = "Tomato disease management",
            date = "30 Aug 2026"
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        MeetingReportCard(
            farmer = "Vijay Shinde",
            topic = "Soybean crop planning",
            date = "28 Aug 2026"
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        MeetingReportCard(
            farmer = "Anita Pawar",
            topic = "Wheat cultivation advice",
            date = "26 Aug 2026"
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )
    }
}


// =================================================================
// APPOINTMENT CARD
// =================================================================

@Composable
private fun AppointmentCard(
    date: String,
    time: String,
    farmer: String,
    purpose: String,
    type: String
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier
                    .size(58.dp)
                    .background(
                        color = FieldCream,
                        shape = RoundedCornerShape(12.dp)
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = time,
                    color = LeafGreen,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = date,
                    color = MutedText,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }

            Spacer(
                modifier = Modifier.size(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = farmer,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = purpose,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedText,
                    modifier = Modifier.padding(top = 3.dp)
                )

                Text(
                    text = "● $type consultation",
                    style = MaterialTheme.typography.bodySmall,
                    color = LeafGreen,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 5.dp)
                )
            }

            TextButton(
                onClick = {
                    // Appointment details will be connected later.
                }
            ) {

                Text(
                    text = "View",
                    color = LeafGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


// =================================================================
// MEETING REPORT CARD
// =================================================================

@Composable
private fun MeetingReportCard(
    farmer: String,
    topic: String,
    date: String
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = farmer,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = topic,
                    color = MutedText,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 3.dp)
                )

                Text(
                    text = date,
                    color = MutedText,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }

            TextButton(
                onClick = {
                    // Report details will be connected later.
                }
            ) {

                Text(
                    text = "View",
                    color = LeafGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
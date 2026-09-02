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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sashya.krushisetu.ui.theme.FieldCream
import com.sashya.krushisetu.ui.theme.LeafGreen
import com.sashya.krushisetu.ui.theme.MutedText

@Composable
fun AdvisorConsultationsScreen(
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
            text = "Consultations",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Text(
            text = "Connect with farmers and manage consultations",
            style = MaterialTheme.typography.bodyMedium,
            color = MutedText,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // =========================================================
        // UPCOMING
        // =========================================================

        Text(
            text = "Upcoming",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        ConsultationCard(
            time = "10:30 AM",
            type = "Video Consultation",
            farmer = "Rajesh Patil",
            icon = "📹",
            actionText = "Join"
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        ConsultationCard(
            time = "02:00 PM",
            type = "Voice Consultation",
            farmer = "Suresh More",
            icon = "☎",
            actionText = "Call"
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // =========================================================
        // QUICK CONSULTATION
        // =========================================================

        Text(
            text = "Quick Consultation",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            ConsultationActionCard(
                icon = "📹",
                title = "Video Call",
                modifier = Modifier.weight(1f)
            )

            ConsultationActionCard(
                icon = "☎",
                title = "Voice Call",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(
            modifier = Modifier.height(22.dp)
        )

        // =========================================================
        // HISTORY
        // =========================================================

        Text(
            text = "Consultation History",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        ConsultationHistoryCard(
            farmer = "Rajesh Patil",
            topic = "Tomato disease",
            date = "Yesterday"
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        ConsultationHistoryCard(
            farmer = "Meena Jadhav",
            topic = "Sugarcane irrigation",
            date = "28 Aug"
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        ConsultationHistoryCard(
            farmer = "Vijay Shinde",
            topic = "Soybean crop advice",
            date = "26 Aug"
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )
    }
}


// =================================================================
// UPCOMING CONSULTATION CARD
// =================================================================

@Composable
private fun ConsultationCard(
    time: String,
    type: String,
    farmer: String,
    icon: String,
    actionText: String
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
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(LeafGreen),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = icon,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(
                modifier = Modifier.size(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = time,
                    color = LeafGreen,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = type,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Text(
                    text = farmer,
                    color = MutedText,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            TextButton(
                onClick = {
                    // Call functionality will be connected later.
                }
            ) {

                Text(
                    text = actionText,
                    color = LeafGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


// =================================================================
// QUICK ACTION CARD
// =================================================================

@Composable
private fun ConsultationActionCard(
    icon: String,
    title: String,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier.height(105.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = icon,
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}


// =================================================================
// HISTORY CARD
// =================================================================

@Composable
private fun ConsultationHistoryCard(
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

            Text(
                text = "›",
                style = MaterialTheme.typography.headlineMedium,
                color = LeafGreen
            )
        }
    }
}
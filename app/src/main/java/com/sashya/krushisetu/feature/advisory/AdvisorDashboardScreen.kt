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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
fun AdvisorDashboardScreen(
    advisorName: String,
    onLogout: () -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(FieldCream)
            .padding(horizontal = 20.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // =========================================================
        // HEADER
        // =========================================================

        item {

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {

                    Text(
                        text = "KrushiSetu",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = LeafGreen
                    )

                    Text(
                        text = "Advisor Dashboard",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MutedText
                    )
                }

                TextButton(
                    onClick = onLogout
                ) {
                    Text(
                        text = "Logout",
                        color = LeafGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }


        // =========================================================
        // WELCOME SECTION
        // =========================================================

        item {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = LeafGreen
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                )
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = "Welcome back, $advisorName 👋",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "Manage farmers, consultations and your advisory activities from one place.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Button(
                        onClick = {
                            // Consultation functionality will be connected later
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = LeafGreen
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {

                        Text(
                            text = "View Consultations",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }


        // =========================================================
        // OVERVIEW
        // =========================================================

        item {

            Text(
                text = "Today's Overview",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }


        item {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                AdvisorStatCard(
                    value = "24",
                    label = "Farmers",
                    modifier = Modifier.weight(1f)
                )

                AdvisorStatCard(
                    value = "8",
                    label = "Consultations",
                    modifier = Modifier.weight(1f)
                )
            }
        }


        item {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                AdvisorStatCard(
                    value = "5",
                    label = "Pending Queries",
                    modifier = Modifier.weight(1f)
                )

                AdvisorStatCard(
                    value = "3",
                    label = "Appointments",
                    modifier = Modifier.weight(1f)
                )
            }
        }


        // =========================================================
        // TODAY'S SCHEDULE
        // =========================================================

        item {

            Text(
                text = "Today's Schedule",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )
        }


        item {

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
                        .padding(16.dp),

                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "10:30 AM",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = LeafGreen
                    )

                    Spacer(
                        modifier = Modifier.width(16.dp)
                    )

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = "Video Consultation",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Farmer consultation",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedText
                        )
                    }
                }
            }
        }


        item {

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
                        .padding(16.dp),

                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "02:00 PM",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = LeafGreen
                    )

                    Spacer(
                        modifier = Modifier.width(16.dp)
                    )

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = "Farmer Meeting",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Advisory discussion",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedText
                        )
                    }
                }
            }
        }


        // =========================================================
        // QUICK ACTIONS
        // =========================================================

        item {

            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )
        }


        item {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                QuickActionCard(
                    icon = "👥",
                    title = "Farmers",
                    modifier = Modifier.weight(1f)
                )

                QuickActionCard(
                    icon = "📹",
                    title = "Consult",
                    modifier = Modifier.weight(1f)
                )
            }
        }


        item {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                QuickActionCard(
                    icon = "📚",
                    title = "Resources",
                    modifier = Modifier.weight(1f)
                )

                QuickActionCard(
                    icon = "📅",
                    title = "Schedule",
                    modifier = Modifier.weight(1f)
                )
            }
        }


        item {

            Spacer(
                modifier = Modifier.height(20.dp)
            )
        }
    }
}


// =============================================================
// STAT CARD
// =============================================================

@Composable
private fun AdvisorStatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier.height(110.dp),
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
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = LeafGreen
            )

            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MutedText
            )
        }
    }
}


// =============================================================
// QUICK ACTION CARD
// =============================================================

@Composable
private fun QuickActionCard(
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
                .padding(14.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = icon,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
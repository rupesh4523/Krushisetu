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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sashya.krushisetu.ui.theme.FieldCream
import com.sashya.krushisetu.ui.theme.LeafGreen
import com.sashya.krushisetu.ui.theme.MutedText

@Composable
fun AdvisorProfileScreen(
    advisorName: String,
    advisorEmail: String?,
    onLogout: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FieldCream)
            .padding(horizontal = 20.dp)
    ) {

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // =========================================================
        // HEADER
        // =========================================================

        Text(
            text = "My Profile",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Manage your advisor profile and expertise",
            style = MaterialTheme.typography.bodyMedium,
            color = MutedText,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // =========================================================
        // PROFILE HEADER
        // =========================================================

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(18.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Column(
                    modifier = Modifier
                        .size(82.dp)
                        .clip(CircleShape)
                        .background(LeafGreen),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = advisorName
                            .firstOrNull()
                            ?.uppercase()
                            ?: "A",
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = advisorName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = advisorEmail ?: "Email not available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedText,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                OutlinedButton(
                    onClick = {
                        // Edit profile functionality will be connected later.
                    }
                ) {

                    Text(
                        text = "Edit Profile",
                        color = LeafGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // =========================================================
        // PROFILE & EXPERTISE
        // =========================================================

        Text(
            text = "Profile & Expertise",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        ProfileInfoCard(
            title = "Specialization",
            value = "Crop Advisory"
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        ProfileInfoCard(
            title = "Experience",
            value = "5+ years"
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        ProfileInfoCard(
            title = "Languages",
            value = "English • Hindi • Marathi"
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // =========================================================
        // AVAILABILITY
        // =========================================================

        Text(
            text = "Availability",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Consultation Hours",
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "10:00 AM – 6:00 PM",
                        color = MutedText,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Text(
                    text = "Available",
                    color = LeafGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // =========================================================
        // ACCOUNT
        // =========================================================

        Text(
            text = "Account",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        TextButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = "Logout",
                color = LeafGreen,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )
    }
}


// =================================================================
// PROFILE INFO CARD
// =================================================================

@Composable
private fun ProfileInfoCard(
    title: String,
    value: String
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(14.dp)
    ) {

        Column(
            modifier = Modifier.padding(15.dp)
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MutedText
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 3.dp)
            )
        }
    }
}
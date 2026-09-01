package com.sashya.krushisetu.feature.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sashya.krushisetu.data.model.UserProfile
import com.sashya.krushisetu.ui.components.ScreenHeader
import com.sashya.krushisetu.ui.theme.LightLeafGreen
import com.sashya.krushisetu.ui.theme.MutedText

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    userProfile: UserProfile?,
    signedInName: String?,
    signedInEmail: String?,
    onSignOut: () -> Unit,
    onOpenLogin: () -> Unit
) {

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            bottom = 24.dp
        )
    ) {

        // ---------------------------------------------------------
        // HEADER
        // ---------------------------------------------------------

        item {

            ScreenHeader(
                title = "My profile ☺",
                subtitle = "Your farmer and farm details."
            )
        }


        // ---------------------------------------------------------
        // FARMER PROFILE CARD
        // ---------------------------------------------------------

        item {

            FarmerProfileCard(
                signedInName = signedInName,
                signedInEmail = signedInEmail
            )
        }


        // ---------------------------------------------------------
        // PERSONAL DETAILS
        // ---------------------------------------------------------

        item {

            Text(
                text = "Personal details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                )
            )
        }


        item {

            ProfileDetail(
                emoji = "📧",
                title = "Email",
                value =
                    userProfile?.email
                        ?.takeIf { it.isNotBlank() }
                        ?: signedInEmail
                        ?: "Not available"
            )
        }


        item {

            ProfileDetail(
                emoji = "☎",
                title = "Phone number",
                value =
                    userProfile?.phone
                        ?.takeIf { it.isNotBlank() }
                        ?: "Not added"
            )
        }


        // ---------------------------------------------------------
        // FARM DETAILS
        // ---------------------------------------------------------

        item {

            Text(
                text = "Farm details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                )
            )
        }


        item {

            ProfileDetail(
                emoji = "🏘️",
                title = "Village",
                value =
                    userProfile?.village
                        ?.takeIf { it.isNotBlank() }
                        ?: "Not added"
            )
        }


        item {

            ProfileDetail(
                emoji = "📍",
                title = "District",
                value =
                    userProfile?.district
                        ?.takeIf { it.isNotBlank() }
                        ?: "Not added"
            )
        }


        item {

            ProfileDetail(
                emoji = "🌾",
                title = "Farm location",
                value =
                    userProfile?.farmLocation
                        ?.takeIf { it.isNotBlank() }
                        ?: "Not added"
            )
        }


        item {

            ProfileDetail(
                emoji = "🚜",
                title = "Number of farms",
                value =
                    if (
                        userProfile != null &&
                        userProfile.numberOfFarms > 0
                    ) {
                        userProfile.numberOfFarms.toString()
                    } else {
                        "Not added"
                    }
            )
        }


        item {

            ProfileDetail(
                emoji = "📐",
                title = "Total farm area",
                value =
                    if (
                        userProfile != null &&
                        userProfile.totalAreaAcres > 0
                    ) {
                        "${userProfile.totalAreaAcres} acres"
                    } else {
                        "Not added"
                    }
            )
        }


        // ---------------------------------------------------------
        // LOCATION
        // ---------------------------------------------------------

        item {

            ProfileDetail(
                emoji = "🗺️",
                title = "Registered location",
                value =
                    userProfile?.location
                        ?.takeIf { it.isNotBlank() }
                        ?: "Not added"
            )
        }


        // ---------------------------------------------------------
        // SIGN OUT
        // ---------------------------------------------------------

        item {

            if (signedInEmail != null) {

                OutlinedButton(
                    onClick = onSignOut,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 20.dp,
                            vertical = 16.dp
                        ),
                    shape = RoundedCornerShape(14.dp)
                ) {

                    Text(
                        text = "Sign out"
                    )
                }

            } else {

                OutlinedButton(
                    onClick = onOpenLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 20.dp,
                            vertical = 16.dp
                        ),
                    shape = RoundedCornerShape(14.dp)
                ) {

                    Text(
                        text = "Sign in to save your profile"
                    )
                }
            }
        }
    }
}


// =============================================================
// FARMER PROFILE CARD
// =============================================================

@Composable
private fun FarmerProfileCard(
    signedInName: String?,
    signedInEmail: String?
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp
            ),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = LightLeafGreen
        )
    ) {

        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "👨🏽‍🌾",
                fontSize = 48.sp
            )

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column {

                Text(
                    text =
                        signedInName
                            ?.takeIf {
                                it.isNotBlank()
                            }
                            ?: "Farmer",

                    style =
                        MaterialTheme.typography.titleLarge,

                    fontWeight =
                        FontWeight.Bold
                )


                Text(
                    text =
                        signedInEmail
                            ?: "Farmer account",

                    style =
                        MaterialTheme.typography.bodyMedium,

                    color =
                        MutedText
                )
            }
        }
    }
}


// =============================================================
// PROFILE DETAIL
// =============================================================

@Composable
private fun ProfileDetail(
    emoji: String,
    title: String,
    value: String
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp,
                vertical = 5.dp
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = emoji,
                fontSize = 22.sp
            )

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column {

                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MutedText
                )

                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
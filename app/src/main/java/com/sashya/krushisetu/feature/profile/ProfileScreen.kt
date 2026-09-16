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
import com.sashya.krushisetu.data.local.LanguageManager
import com.sashya.krushisetu.data.model.UserProfile
import com.sashya.krushisetu.ui.components.ScreenHeader
import com.sashya.krushisetu.ui.theme.LightLeafGreen
import com.sashya.krushisetu.ui.theme.MutedText
import androidx.compose.ui.platform.LocalContext

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    userProfile: UserProfile?,
    signedInName: String?,
    signedInEmail: String?,
    onSignOut: () -> Unit,
    onOpenLogin: () -> Unit
) {

    val context = LocalContext.current

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
                title =
                    if (LanguageManager.isHindi()) {
                        "मेरी प्रोफ़ाइल ☺"
                    } else {
                        "My profile ☺"
                    },

                subtitle =
                    if (LanguageManager.isHindi()) {
                        "आपकी किसान और खेत की जानकारी।"
                    } else {
                        "Your farmer and farm details."
                    }
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
        // LANGUAGE
        // ---------------------------------------------------------

        item {

            Text(
                text =
                    if (LanguageManager.isHindi()) {
                        "भाषा"
                    } else {
                        "Language"
                    },

                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,

                modifier = Modifier.padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                )
            )
        }


        item {

            LanguageSelectionCard(
                context = context
            )
        }


        // ---------------------------------------------------------
        // PERSONAL DETAILS
        // ---------------------------------------------------------

        item {

            Text(
                text =
                    if (LanguageManager.isHindi()) {
                        "व्यक्तिगत जानकारी"
                    } else {
                        "Personal details"
                    },

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

                title =
                    if (LanguageManager.isHindi()) {
                        "ईमेल"
                    } else {
                        "Email"
                    },

                value =
                    userProfile?.email
                        ?.takeIf { it.isNotBlank() }
                        ?: signedInEmail
                        ?: if (LanguageManager.isHindi()) {
                            "उपलब्ध नहीं"
                        } else {
                            "Not available"
                        }
            )
        }


        item {

            ProfileDetail(
                emoji = "☎",

                title =
                    if (LanguageManager.isHindi()) {
                        "फ़ोन नंबर"
                    } else {
                        "Phone number"
                    },

                value =
                    userProfile?.phone
                        ?.takeIf { it.isNotBlank() }
                        ?: if (LanguageManager.isHindi()) {
                            "जोड़ा नहीं गया"
                        } else {
                            "Not added"
                        }
            )
        }


        // ---------------------------------------------------------
        // FARM DETAILS
        // ---------------------------------------------------------

        item {

            Text(
                text =
                    if (LanguageManager.isHindi()) {
                        "खेत की जानकारी"
                    } else {
                        "Farm details"
                    },

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

                title =
                    if (LanguageManager.isHindi()) {
                        "गाँव"
                    } else {
                        "Village"
                    },

                value =
                    userProfile?.village
                        ?.takeIf { it.isNotBlank() }
                        ?: if (LanguageManager.isHindi()) {
                            "जोड़ा नहीं गया"
                        } else {
                            "Not added"
                        }
            )
        }


        item {

            ProfileDetail(
                emoji = "📍",

                title =
                    if (LanguageManager.isHindi()) {
                        "जिला"
                    } else {
                        "District"
                    },

                value =
                    userProfile?.district
                        ?.takeIf { it.isNotBlank() }
                        ?: if (LanguageManager.isHindi()) {
                            "जोड़ा नहीं गया"
                        } else {
                            "Not added"
                        }
            )
        }


        item {

            ProfileDetail(
                emoji = "🌾",

                title =
                    if (LanguageManager.isHindi()) {
                        "खेत का स्थान"
                    } else {
                        "Farm location"
                    },

                value =
                    userProfile?.farmLocation
                        ?.takeIf { it.isNotBlank() }
                        ?: if (LanguageManager.isHindi()) {
                            "जोड़ा नहीं गया"
                        } else {
                            "Not added"
                        }
            )
        }


        item {

            ProfileDetail(
                emoji = "🚜",

                title =
                    if (LanguageManager.isHindi()) {
                        "खेतों की संख्या"
                    } else {
                        "Number of farms"
                    },

                value =
                    if (
                        userProfile != null &&
                        userProfile.numberOfFarms > 0
                    ) {
                        userProfile.numberOfFarms.toString()
                    } else {
                        if (LanguageManager.isHindi()) {
                            "जोड़ा नहीं गया"
                        } else {
                            "Not added"
                        }
                    }
            )
        }


        item {

            ProfileDetail(
                emoji = "📐",

                title =
                    if (LanguageManager.isHindi()) {
                        "कुल खेत का क्षेत्रफल"
                    } else {
                        "Total farm area"
                    },

                value =
                    if (
                        userProfile != null &&
                        userProfile.totalAreaAcres > 0
                    ) {
                        "${userProfile.totalAreaAcres} ${
                            if (LanguageManager.isHindi()) {
                                "एकड़"
                            } else {
                                "acres"
                            }
                        }"
                    } else {
                        if (LanguageManager.isHindi()) {
                            "जोड़ा नहीं गया"
                        } else {
                            "Not added"
                        }
                    }
            )
        }


        // ---------------------------------------------------------
        // LOCATION
        // ---------------------------------------------------------

        item {

            ProfileDetail(
                emoji = "🗺️",

                title =
                    if (LanguageManager.isHindi()) {
                        "पंजीकृत स्थान"
                    } else {
                        "Registered location"
                    },

                value =
                    userProfile?.location
                        ?.takeIf { it.isNotBlank() }
                        ?: if (LanguageManager.isHindi()) {
                            "जोड़ा नहीं गया"
                        } else {
                            "Not added"
                        }
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
                        text =
                            if (LanguageManager.isHindi()) {
                                "साइन आउट"
                            } else {
                                "Sign out"
                            }
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
                        text =
                            if (LanguageManager.isHindi()) {
                                "अपनी प्रोफ़ाइल सुरक्षित करने के लिए साइन इन करें"
                            } else {
                                "Sign in to save your profile"
                            }
                    )
                }
            }
        }
    }
}


// =============================================================
// LANGUAGE SELECTION CARD
// =============================================================

@Composable
private fun LanguageSelectionCard(
    context: android.content.Context
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp
            ),

        shape = RoundedCornerShape(16.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text =
                    if (LanguageManager.isHindi()) {
                        "ऐप की भाषा चुनें"
                    } else {
                        "Choose app language"
                    },

                style = MaterialTheme.typography.bodyMedium,

                color = MutedText
            )

            Spacer(
                modifier = Modifier.padding(top = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                OutlinedButton(
                    onClick = {
                        LanguageManager.setLanguage(
                            context = context,
                            language = LanguageManager.ENGLISH
                        )
                    },

                    modifier = Modifier.weight(1f),

                    shape = RoundedCornerShape(12.dp)
                ) {

                    Text(
                        text = "English"
                    )
                }

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                OutlinedButton(
                    onClick = {
                        LanguageManager.setLanguage(
                            context = context,
                            language = LanguageManager.HINDI
                        )
                    },

                    modifier = Modifier.weight(1f),

                    shape = RoundedCornerShape(12.dp)
                ) {

                    Text(
                        text = "हिन्दी"
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
                            ?: if (LanguageManager.isHindi()) {
                                "किसान"
                            } else {
                                "Farmer"
                            },

                    style =
                        MaterialTheme.typography.titleLarge,

                    fontWeight =
                        FontWeight.Bold
                )


                Text(
                    text =
                        signedInEmail
                            ?: if (LanguageManager.isHindi()) {
                                "किसान खाता"
                            } else {
                                "Farmer account"
                            },

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
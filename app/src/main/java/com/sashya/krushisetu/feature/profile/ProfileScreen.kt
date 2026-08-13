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
import com.sashya.krushisetu.ui.components.ScreenHeader
import com.sashya.krushisetu.ui.theme.LightLeafGreen
import com.sashya.krushisetu.ui.theme.MutedText

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    signedInName: String?,
    signedInEmail: String?,
    onSignOut: () -> Unit,
    onOpenLogin: () -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            ScreenHeader(
                title = "My profile ☺",
                subtitle = "Your farmer and farm details."
            )
        }
        item { FarmerProfileCard(signedInName, signedInEmail) }
        item {
            Text(
                text = "Farm details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )
        }
        item { ProfileDetail("📍", "Location", "Baramati, Pune, Maharashtra") }
        item { ProfileDetail("🌾", "Farm size", "3.5 acres") }
        item { ProfileDetail("🗣️", "Preferred language", "Marathi") }
        item { ProfileDetail("☎", "Phone number", "+91 98765 43210") }
        item {
            if (signedInEmail != null) {
                OutlinedButton(
                    onClick = onSignOut,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Sign out")
                }
            } else {
                OutlinedButton(
                    onClick = onOpenLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Sign in to save your profile")
                }
            }
        }
    }
}

@Composable
private fun FarmerProfileCard(
    signedInName: String?,
    signedInEmail: String?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = LightLeafGreen)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("👨🏽‍🌾", fontSize = 48.sp)
            Spacer(Modifier.width(14.dp))
            Column {
                Text(
                    signedInName?.takeIf { it.isNotBlank() } ?: "Suresh Patil",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    signedInEmail ?: "Guest mode • Farmer since 2008",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedText
                )
            }
        }
    }
}

@Composable
private fun ProfileDetail(emoji: String, title: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 5.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(emoji, fontSize = 22.sp)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, style = MaterialTheme.typography.labelMedium, color = MutedText)
                Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

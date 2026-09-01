package com.sashya.krushisetu.feature.advisory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
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
fun AdvisorBottomBar(
    selectedTab: String,
    onHome: () -> Unit,
    onFarmers: () -> Unit,
    onConsultations: () -> Unit,
    onSchedule: () -> Unit,
    onProfile: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(
                horizontal = 8.dp,
                vertical = 8.dp
            ),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {

        AdvisorBottomItem(
            icon = "⌂",
            label = "Home",
            selected = selectedTab == "HOME",
            onClick = onHome
        )

        AdvisorBottomItem(
            icon = "♟",
            label = "Farmers",
            selected = selectedTab == "FARMERS",
            onClick = onFarmers
        )

        AdvisorBottomItem(
            icon = "◉",
            label = "Consult",
            selected = selectedTab == "CONSULTATIONS",
            onClick = onConsultations
        )

        AdvisorBottomItem(
            icon = "▣",
            label = "Schedule",
            selected = selectedTab == "SCHEDULE",
            onClick = onSchedule
        )

        AdvisorBottomItem(
            icon = "●",
            label = "Profile",
            selected = selectedTab == "PROFILE",
            onClick = onProfile
        )
    }
}


@Composable
private fun AdvisorBottomItem(
    icon: String,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    androidx.compose.material3.TextButton(
        onClick = onClick
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = icon,
                color = if (selected) {
                    LeafGreen
                } else {
                    MutedText
                },
                fontWeight = if (selected) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
                }
            )

            Text(
                text = label,
                color = if (selected) {
                    LeafGreen
                } else {
                    MutedText
                },
                fontWeight = if (selected) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
                }
            )
        }
    }
}
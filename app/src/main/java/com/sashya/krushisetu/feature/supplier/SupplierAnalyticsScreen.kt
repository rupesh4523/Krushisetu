package com.sashya.krushisetu.feature.supplier

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
fun SupplierAnalyticsScreen(
    onBack: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FieldCream)
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {

        // HEADER

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Reports & Analytics",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = LeafGreen
                )

                Text(
                    text = "Understand your business performance",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedText
                )
            }

            TextButton(
                onClick = onBack
            ) {
                Text(
                    text = "Back",
                    color = LeafGreen,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // BUSINESS SUMMARY

        Text(
            text = "Business Overview",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            AnalyticsCard(
                modifier = Modifier.weight(1f),
                value = "₹48.5K",
                label = "Revenue"
            )

            AnalyticsCard(
                modifier = Modifier.weight(1f),
                value = "128",
                label = "Orders"
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            AnalyticsCard(
                modifier = Modifier.weight(1f),
                value = "342",
                label = "Products Sold"
            )

            AnalyticsCard(
                modifier = Modifier.weight(1f),
                value = "86",
                label = "Farmers Served"
            )
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // SALES OVERVIEW

        Text(
            text = "Sales Overview",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {

            Column(
                modifier = Modifier.padding(18.dp)
            ) {

                Text(
                    text = "Monthly Revenue",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Text(
                    text = "January       ₹32K",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "February      ₹38K",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "March         ₹41K",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "April         ₹45K",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "May           ₹43K",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "June          ₹48.5K",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // TOP PRODUCTS

        Text(
            text = "Top Products",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        AnalyticsListCard(
            title = "1. Wheat Seeds",
            value = "96 units sold"
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        AnalyticsListCard(
            title = "2. NPK Fertilizer",
            value = "78 bags sold"
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        AnalyticsListCard(
            title = "3. Rice Seeds",
            value = "64 units sold"
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // FARMER DEMAND

        Text(
            text = "Farmer Demand",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        AnalyticsListCard(
            title = "Seeds",
            value = "High demand"
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        AnalyticsListCard(
            title = "Fertilizers",
            value = "High demand"
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        AnalyticsListCard(
            title = "Pesticides",
            value = "Medium demand"
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        AnalyticsListCard(
            title = "Equipment",
            value = "Low demand"
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )
    }
}


// -------------------------------------------------------------
// ANALYTICS SUMMARY CARD
// -------------------------------------------------------------

@Composable
private fun AnalyticsCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String
) {

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = LeafGreen
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MutedText
            )
        }
    }
}


// -------------------------------------------------------------
// ANALYTICS LIST CARD
// -------------------------------------------------------------

@Composable
private fun AnalyticsListCard(
    title: String,
    value: String
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = title,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = value,
                color = MutedText
            )
        }
    }
}
package com.sashya.krushisetu.feature.supplier

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.sashya.krushisetu.ui.theme.FieldCream
import com.sashya.krushisetu.ui.theme.LeafGreen
import com.sashya.krushisetu.ui.theme.MutedText

@Composable
fun SupplierDashboardScreen(
    supplierName: String,
    onOpenProfile: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FieldCream)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {

        // ---------------------------------------------------------
        // HEADER
        // ---------------------------------------------------------

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column {

                Text(
                    text = "KrushiSetu",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = LeafGreen
                )

                Text(
                    text = "Supplier Dashboard",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedText
                )
            }

            OutlinedButton(
                onClick = {
                    // Notifications will be implemented later
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Notifications")
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // ---------------------------------------------------------
        // WELCOME
        // ---------------------------------------------------------

        Text(
            text = "Welcome back, $supplierName",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = "Manage your products, orders and deliveries.",
            style = MaterialTheme.typography.bodyMedium,
            color = MutedText
        )

        Spacer(
            modifier = Modifier.height(22.dp)
        )

        // ---------------------------------------------------------
        // BUSINESS OVERVIEW
        // ---------------------------------------------------------

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

            SupplierSummaryCard(
                modifier = Modifier.weight(1f),
                title = "PRODUCTS",
                value = "124",
                subtitle = "Active products"
            )

            SupplierSummaryCard(
                modifier = Modifier.weight(1f),
                title = "ORDERS",
                value = "8",
                subtitle = "Pending orders"
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            SupplierSummaryCard(
                modifier = Modifier.weight(1f),
                title = "DELIVERY",
                value = "5",
                subtitle = "Active deliveries"
            )

            SupplierSummaryCard(
                modifier = Modifier.weight(1f),
                title = "REVENUE",
                value = "₹48.5K",
                subtitle = "Current period"
            )
        }

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        // ---------------------------------------------------------
        // MANAGEMENT
        // ---------------------------------------------------------

        Text(
            text = "Supplier Management",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        SupplierManagementRow(
            title = "Profile & Company",
            description = "Company information, branches and contact details",
            onClick = onOpenProfile
        )

        SupplierManagementRow(
            title = "Products Management",
            description = "Manage products, prices and available stock",
            onClick = {
                // Products screen will be added next
            }
        )

        SupplierManagementRow(
            title = "Orders Management",
            description = "View new, active and completed orders",
            onClick = {
                // Orders screen will be added next
            }
        )

        SupplierManagementRow(
            title = "Delivery Management",
            description = "Dispatch orders and track deliveries",
            onClick = {
                // Delivery screen will be added next
            }
        )

        SupplierManagementRow(
            title = "Payments",
            description = "Transactions, payment history and payouts",
            onClick = {
                // Payments screen will be added next
            }
        )

        SupplierManagementRow(
            title = "Reports & Analytics",
            description = "Sales reports, top products and farmer demand",
            onClick = {
                // Analytics screen will be added next
            }
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )
    }
}


// -------------------------------------------------------------
// SUMMARY CARD
// -------------------------------------------------------------

@Composable
private fun SupplierSummaryCard(
    modifier: Modifier,
    title: String,
    value: String,
    subtitle: String
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
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MutedText
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = LeafGreen
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MutedText
            )
        }
    }
}


// -------------------------------------------------------------
// MANAGEMENT ROW
// -------------------------------------------------------------

@Composable
private fun SupplierManagementRow(
    title: String,
    description: String,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText
                )
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Text(
                text = "›",
                fontSize = 28.sp,
                color = LeafGreen
            )
        }
    }
}
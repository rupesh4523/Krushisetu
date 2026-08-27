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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun SupplierDashboardScreen() {

    var selectedSection by remember {
        mutableStateOf<String?>(null)
    }

    val supplierSections = listOf(
        SupplierSection(
            icon = "👤",
            title = "Profile & Company",
            description = "Company details, branches and contact information"
        ),
        SupplierSection(
            icon = "📦",
            title = "Products Management",
            description = "Manage seeds, fertilizers, pesticides and equipment"
        ),
        SupplierSection(
            icon = "📋",
            title = "Orders Management",
            description = "View new orders, order status and order history"
        ),
        SupplierSection(
            icon = "🚚",
            title = "Delivery Management",
            description = "Dispatch orders and track deliveries"
        ),
        SupplierSection(
            icon = "₹",
            title = "Payments",
            description = "Payment history, transactions and payouts"
        ),
        SupplierSection(
            icon = "📊",
            title = "Reports & Analytics",
            description = "Sales reports, top products and farmer demand"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FieldCream)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {

        // ---------------------------------------------------------
        // TOP BAR
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

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                OutlinedButton(
                    onClick = {
                        selectedSection = "Notifications"
                    },
                    modifier = Modifier.size(48.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "🔔",
                        fontSize = 18.sp
                    )
                }

                OutlinedButton(
                    onClick = {
                        selectedSection = "Profile & Company"
                    },
                    modifier = Modifier.size(48.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "👤",
                        fontSize = 18.sp
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(22.dp)
        )

        // ---------------------------------------------------------
        // WELCOME CARD
        // ---------------------------------------------------------

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = LeafGreen
            )
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Text(
                    text = "Welcome back, Supplier 👋",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = "Manage your agricultural products, orders and deliveries from one place.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Button(
                    onClick = {
                        selectedSection = "Products Management"
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = LeafGreen
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Manage Products",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // ---------------------------------------------------------
        // SUMMARY
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
                icon = "📦",
                value = "124",
                label = "Products"
            )

            SupplierSummaryCard(
                modifier = Modifier.weight(1f),
                icon = "📋",
                value = "8",
                label = "Pending Orders"
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
                icon = "🚚",
                value = "5",
                label = "Deliveries"
            )

            SupplierSummaryCard(
                modifier = Modifier.weight(1f),
                icon = "₹",
                value = "48.5K",
                label = "Revenue"
            )
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // ---------------------------------------------------------
        // SUPPLIER SERVICES
        // ---------------------------------------------------------

        Text(
            text = "Supplier Services",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        supplierSections.forEach { section ->

            SupplierFeatureCard(
                section = section,
                onClick = {
                    selectedSection = section.title
                }
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }

        // ---------------------------------------------------------
        // SELECTED SECTION
        // ---------------------------------------------------------

        if (selectedSection != null) {

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                )
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Text(
                        text = selectedSection!!,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = LeafGreen
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text = "This section is ready for the next implementation stage.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedText
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )
    }
}


// -------------------------------------------------------------
// SUPPLIER SECTION DATA
// -------------------------------------------------------------

private data class SupplierSection(
    val icon: String,
    val title: String,
    val description: String
)


// -------------------------------------------------------------
// SUMMARY CARD
// -------------------------------------------------------------

@Composable
private fun SupplierSummaryCard(
    modifier: Modifier = Modifier,
    icon: String,
    value: String,
    label: String
) {

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = icon,
                fontSize = 25.sp
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

            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MutedText
            )
        }
    }
}


// -------------------------------------------------------------
// FEATURE CARD
// -------------------------------------------------------------

@Composable
private fun SupplierFeatureCard(
    section: SupplierSection,
    onClick: () -> Unit
) {

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = section.icon,
                fontSize = 30.sp
            )

            Spacer(
                modifier = Modifier.size(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = section.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = section.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText
                )
            }

            Text(
                text = "›",
                fontSize = 28.sp,
                color = LeafGreen
            )
        }
    }
}
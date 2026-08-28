package com.sashya.krushisetu.feature.supplier

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
    onOpenProfile: () -> Unit,
    onOpenProducts: () -> Unit,
    onOpenOrders: () -> Unit,
    onOpenDelivery: () -> Unit,
    onOpenPayments: () -> Unit,
    onOpenAnalytics: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FieldCream)
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

            // PROFILE BUTTON
            OutlinedButton(
                onClick = {
                    onOpenProfile()
                },
                modifier = Modifier.size(48.dp),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "👤",
                    fontSize = 18.sp
                )
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
                    text = "Welcome back, $supplierName 👋",
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

                // MANAGE PRODUCTS BUTTON
                Button(
                    onClick = {
                        onOpenProducts()
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
        // ANALYTICS
        // ---------------------------------------------------------

        Text(
            text = "Business Analytics",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
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
                    text = "Sales Performance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = LeafGreen
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                // Simple visual sales bars
                AnalyticsBar(
                    month = "Jan",
                    value = "₹32K",
                    progress = 0.55f
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                AnalyticsBar(
                    month = "Feb",
                    value = "₹38K",
                    progress = 0.65f
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                AnalyticsBar(
                    month = "Mar",
                    value = "₹41K",
                    progress = 0.72f
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                AnalyticsBar(
                    month = "Apr",
                    value = "₹45K",
                    progress = 0.80f
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                AnalyticsBar(
                    month = "May",
                    value = "₹43K",
                    progress = 0.76f
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                AnalyticsBar(
                    month = "Jun",
                    value = "₹48.5K",
                    progress = 0.90f
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                TextButton(
                    onClick = {
                        onOpenAnalytics()
                    }
                ) {
                    Text(
                        text = "View Full Analytics →",
                        color = LeafGreen,
                        fontWeight = FontWeight.Bold
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
// ANALYTICS BAR
// -------------------------------------------------------------

@Composable
private fun AnalyticsBar(
    month: String,
    value: String,
    progress: Float
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = month,
            modifier = Modifier.size(width = 35.dp, height = 30.dp),
            fontWeight = FontWeight.SemiBold
        )

        Card(
            modifier = Modifier
                .weight(1f)
                .height(22.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = FieldCream
            )
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .background(
                        LeafGreen,
                        RoundedCornerShape(10.dp)
                    )
            )
        }

        Spacer(
            modifier = Modifier.size(10.dp)
        )

        Text(
            text = value,
            modifier = Modifier.size(width = 55.dp, height = 30.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = LeafGreen
        )
    }
}


// -------------------------------------------------------------
// SUPPLIER BOTTOM BAR
// -------------------------------------------------------------

@Composable
fun SupplierBottomBar(
    onHome: () -> Unit,
    onProducts: () -> Unit,
    onOrders: () -> Unit,
    onDelivery: () -> Unit,
    onPayments: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(
                horizontal = 6.dp,
                vertical = 6.dp
            ),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {

        SupplierBottomItem(
            icon = "⌂",
            label = "Home",
            onClick = onHome
        )

        SupplierBottomItem(
            icon = "📦",
            label = "Products",
            onClick = onProducts
        )

        SupplierBottomItem(
            icon = "📋",
            label = "Orders",
            onClick = onOrders
        )

        SupplierBottomItem(
            icon = "🚚",
            label = "Delivery",
            onClick = onDelivery
        )

        SupplierBottomItem(
            icon = "₹",
            label = "Payments",
            onClick = onPayments
        )
    }
}


// -------------------------------------------------------------
// BOTTOM BAR ITEM
// -------------------------------------------------------------

@Composable
private fun SupplierBottomItem(
    icon: String,
    label: String,
    onClick: () -> Unit
) {

    TextButton(
        onClick = onClick
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = icon,
                fontSize = 20.sp
            )

            Text(
                text = label,
                fontSize = 11.sp,
                color = LeafGreen,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
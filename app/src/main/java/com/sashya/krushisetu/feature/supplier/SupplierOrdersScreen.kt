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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.sashya.krushisetu.ui.theme.FieldCream
import com.sashya.krushisetu.ui.theme.LeafGreen
import com.sashya.krushisetu.ui.theme.MutedText

private data class SupplierOrder(
    val orderId: String,
    val farmerName: String,
    val product: String,
    val quantity: Int,
    val amount: String,
    val status: String
)

@Composable
fun SupplierOrdersScreen(
    onBack: () -> Unit
) {

    var selectedStatus by remember {
        mutableStateOf("All")
    }

    val orders = remember {
        listOf(
            SupplierOrder(
                orderId = "KS1024",
                farmerName = "Ramesh Patil",
                product = "Wheat Seeds",
                quantity = 4,
                amount = "₹1,800",
                status = "New"
            ),
            SupplierOrder(
                orderId = "KS1023",
                farmerName = "Suresh More",
                product = "NPK Fertilizer",
                quantity = 2,
                amount = "₹2,500",
                status = "Processing"
            ),
            SupplierOrder(
                orderId = "KS1022",
                farmerName = "Anita Sharma",
                product = "Rice Seeds",
                quantity = 5,
                amount = "₹2,600",
                status = "Dispatched"
            ),
            SupplierOrder(
                orderId = "KS1021",
                farmerName = "Vijay Pawar",
                product = "Organic Fertilizer",
                quantity = 3,
                amount = "₹2,550",
                status = "Delivered"
            )
        )
    }

    val filteredOrders = orders.filter {
        selectedStatus == "All" || it.status == selectedStatus
    }

    val statuses = listOf(
        "All",
        "New",
        "Processing",
        "Dispatched",
        "Delivered"
    )

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
                    text = "Orders",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = LeafGreen
                )

                Text(
                    text = "Manage farmer orders and their status",
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
            modifier = Modifier.height(18.dp)
        )

        // STATUS FILTERS

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            statuses.take(3).forEach { status ->

                OrderFilterButton(
                    text = status,
                    selected = selectedStatus == status,
                    onClick = {
                        selectedStatus = status
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            statuses.drop(3).forEach { status ->

                OrderFilterButton(
                    text = status,
                    selected = selectedStatus == status,
                    onClick = {
                        selectedStatus = status
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Text(
            text = "${filteredOrders.size} orders",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        // ORDER LIST

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(filteredOrders) { order ->

                SupplierOrderCard(order)
            }
        }
    }
}


// -------------------------------------------------------------
// FILTER BUTTON
// -------------------------------------------------------------

@Composable
private fun OrderFilterButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = 6.dp,
            vertical = 8.dp
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor =
                if (selected) LeafGreen else Color.White,
            contentColor =
                if (selected) Color.White else LeafGreen
        )
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.SemiBold
        )
    }
}


// -------------------------------------------------------------
// ORDER CARD
// -------------------------------------------------------------

@Composable
private fun SupplierOrderCard(
    order: SupplierOrder
) {

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
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "#${order.orderId}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = order.status,
                    color = LeafGreen,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = order.farmerName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "${order.quantity} × ${order.product}",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedText
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = order.amount,
                    fontWeight = FontWeight.Bold,
                    color = LeafGreen
                )

                TextButton(
                    onClick = {
                        // Order details will be connected next
                    }
                ) {
                    Text(
                        text = "View Order",
                        color = LeafGreen
                    )
                }
            }
        }
    }
}
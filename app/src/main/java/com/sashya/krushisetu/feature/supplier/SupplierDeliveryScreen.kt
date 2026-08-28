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

private data class SupplierDelivery(
    val orderId: String,
    val farmerName: String,
    val address: String,
    val product: String,
    val deliveryDate: String,
    val status: String
)

@Composable
fun SupplierDeliveryScreen(
    onBack: () -> Unit
) {

    var selectedStatus by remember {
        mutableStateOf("All")
    }

    val deliveries = remember {
        listOf(
            SupplierDelivery(
                orderId = "KS1022",
                farmerName = "Anita Sharma",
                address = "Nashik, Maharashtra",
                product = "Rice Seeds",
                deliveryDate = "29 Aug 2026",
                status = "Dispatched"
            ),
            SupplierDelivery(
                orderId = "KS1019",
                farmerName = "Ramesh Patil",
                address = "Pune, Maharashtra",
                product = "NPK Fertilizer",
                deliveryDate = "29 Aug 2026",
                status = "In Transit"
            ),
            SupplierDelivery(
                orderId = "KS1017",
                farmerName = "Vijay Pawar",
                address = "Satara, Maharashtra",
                product = "Organic Fertilizer",
                deliveryDate = "28 Aug 2026",
                status = "Delivered"
            ),
            SupplierDelivery(
                orderId = "KS1015",
                farmerName = "Suresh More",
                address = "Ahmednagar, Maharashtra",
                product = "Wheat Seeds",
                deliveryDate = "30 Aug 2026",
                status = "Pending"
            )
        )
    }

    val statuses = listOf(
        "All",
        "Pending",
        "Dispatched",
        "In Transit",
        "Delivered"
    )

    val filteredDeliveries = deliveries.filter {
        selectedStatus == "All" || it.status == selectedStatus
    }

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
                    text = "Delivery Management",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = LeafGreen
                )

                Text(
                    text = "Dispatch and track your deliveries",
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

                DeliveryFilterButton(
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

                DeliveryFilterButton(
                    text = status,
                    selected = selectedStatus == status,
                    onClick = {
                        selectedStatus = status
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Text(
            text = "${filteredDeliveries.size} deliveries",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        // DELIVERY LIST

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(filteredDeliveries) { delivery ->

                SupplierDeliveryCard(
                    delivery = delivery
                )
            }
        }
    }
}


// -------------------------------------------------------------
// FILTER BUTTON
// -------------------------------------------------------------

@Composable
private fun DeliveryFilterButton(
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
            horizontal = 5.dp,
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
// DELIVERY CARD
// -------------------------------------------------------------

@Composable
private fun SupplierDeliveryCard(
    delivery: SupplierDelivery
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
                    text = "#${delivery.orderId}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = delivery.status,
                    color = LeafGreen,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = delivery.farmerName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = delivery.product,
                style = MaterialTheme.typography.bodyMedium,
                color = MutedText
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = delivery.address,
                style = MaterialTheme.typography.bodyMedium,
                color = MutedText
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Expected: ${delivery.deliveryDate}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {

                TextButton(
                    onClick = {
                        // Delivery details/tracking will be connected next
                    }
                ) {
                    Text(
                        text = "Track Delivery",
                        color = LeafGreen
                    )
                }
            }
        }
    }
}
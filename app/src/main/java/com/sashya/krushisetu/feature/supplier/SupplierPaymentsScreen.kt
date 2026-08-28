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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sashya.krushisetu.ui.theme.FieldCream
import com.sashya.krushisetu.ui.theme.LeafGreen
import com.sashya.krushisetu.ui.theme.MutedText

private data class SupplierTransaction(
    val transactionId: String,
    val orderId: String,
    val description: String,
    val amount: String,
    val status: String,
    val date: String
)

@Composable
fun SupplierPaymentsScreen(
    onBack: () -> Unit
) {

    val transactions = remember {
        listOf(
            SupplierTransaction(
                transactionId = "TX1024",
                orderId = "KS1024",
                description = "Wheat Seeds",
                amount = "₹1,800",
                status = "Completed",
                date = "28 Aug 2026"
            ),
            SupplierTransaction(
                transactionId = "TX1023",
                orderId = "KS1023",
                description = "NPK Fertilizer",
                amount = "₹2,500",
                status = "Completed",
                date = "27 Aug 2026"
            ),
            SupplierTransaction(
                transactionId = "TX1022",
                orderId = "KS1022",
                description = "Rice Seeds",
                amount = "₹2,600",
                status = "Pending",
                date = "27 Aug 2026"
            ),
            SupplierTransaction(
                transactionId = "TX1021",
                orderId = "KS1021",
                description = "Organic Fertilizer",
                amount = "₹2,550",
                status = "Completed",
                date = "26 Aug 2026"
            )
        )
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
                    text = "Payments",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = LeafGreen
                )

                Text(
                    text = "Transactions and supplier payouts",
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

        // PAYMENT SUMMARY

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            PaymentSummaryCard(
                modifier = Modifier.weight(1f),
                title = "Total Earnings",
                value = "₹48,500"
            )

            PaymentSummaryCard(
                modifier = Modifier.weight(1f),
                title = "Pending",
                value = "₹8,200"
            )
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "Transaction History",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(transactions) { transaction ->

                SupplierTransactionCard(
                    transaction = transaction
                )
            }
        }
    }
}


// -------------------------------------------------------------
// PAYMENT SUMMARY CARD
// -------------------------------------------------------------

@Composable
private fun PaymentSummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String
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
                style = MaterialTheme.typography.bodySmall,
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
        }
    }
}


// -------------------------------------------------------------
// TRANSACTION CARD
// -------------------------------------------------------------

@Composable
private fun SupplierTransactionCard(
    transaction: SupplierTransaction
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

                Column {

                    Text(
                        text = transaction.transactionId,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Order #${transaction.orderId}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedText
                    )
                }

                Text(
                    text = transaction.amount,
                    fontWeight = FontWeight.Bold,
                    color = LeafGreen
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = transaction.description,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = transaction.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText
                )

                Text(
                    text = transaction.status,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = if (transaction.status == "Pending") {
                        MaterialTheme.colorScheme.error
                    } else {
                        LeafGreen
                    }
                )
            }
        }
    }
}
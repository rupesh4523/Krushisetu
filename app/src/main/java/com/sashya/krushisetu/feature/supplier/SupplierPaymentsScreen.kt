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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sashya.krushisetu.ui.theme.FieldCream
import com.sashya.krushisetu.ui.theme.LeafGreen
import com.sashya.krushisetu.ui.theme.MutedText
import java.text.SimpleDateFormat
import java.util.Locale

// =============================================================
// SUPPLIER TRANSACTION MODEL
// =============================================================

private data class SupplierTransaction(
    val transactionId: String,
    val orderId: String,
    val description: String,
    val amount: Double,
    val status: String,
    val date: String
)

// =============================================================
// SUPPLIER PAYMENTS SCREEN
// =============================================================

@Composable
fun SupplierPaymentsScreen(
    onBack: () -> Unit
) {
    val firestore = remember {
        FirebaseFirestore.getInstance()
    }

    val supplierId = remember {
        FirebaseAuth.getInstance().currentUser?.uid
    }

    // =========================================================
    // SCREEN STATE
    // =========================================================

    var transactions by remember {
        mutableStateOf<List<SupplierTransaction>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    var updatingPaymentOrderId by remember {
        mutableStateOf<String?>(null)
    }

    // =========================================================
    // LOAD SUPPLIER PAYMENTS FROM FIRESTORE
    // =========================================================

    DisposableEffect(supplierId) {

        if (supplierId == null) {

            transactions = emptyList()
            isLoading = false
            errorMessage = "Supplier account could not be identified."

            onDispose { }

        } else {

            isLoading = true
            errorMessage = null

            val listener = firestore
                .collection("orders")
                .whereEqualTo(
                    "supplierId",
                    supplierId
                )
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {

                        isLoading = false

                        errorMessage =
                            error.localizedMessage
                                ?: "Unable to load payment information."

                        return@addSnapshotListener
                    }

                    transactions =
                        snapshot
                            ?.documents
                            .orEmpty()
                            .map { document ->

                                // ---------------------------------
                                // ORDER ID
                                // ---------------------------------

                                val orderId =
                                    document.id

                                // ---------------------------------
                                // TRANSACTION ID
                                // ---------------------------------

                                val transactionId =
                                    document
                                        .getString("transactionId")
                                        ?.trim()
                                        .takeUnless {
                                            it.isNullOrBlank()
                                        }
                                        ?: "PAY-${orderId.takeLast(6)}"

                                // ---------------------------------
                                // DESCRIPTION
                                // ---------------------------------

                                val items =
                                    document
                                        .get("items")
                                            as? List<*>
                                        ?: emptyList<Any>()

                                val description =
                                    items
                                        .mapNotNull { item ->

                                            val itemMap =
                                                item as? Map<*, *>

                                            itemMap
                                                ?.get("productName")
                                                ?.toString()
                                                ?.trim()
                                                ?.takeIf {
                                                    it.isNotBlank()
                                                }
                                        }
                                        .joinToString(", ")
                                        .ifBlank {
                                            "Order payment"
                                        }

                                // ---------------------------------
                                // TOTAL AMOUNT
                                // ---------------------------------

                                val amount =
                                    (
                                            document.get("totalAmount")
                                                    as? Number
                                            )
                                        ?.toDouble()
                                        ?: 0.0

                                // ---------------------------------
                                // PAYMENT STATUS
                                // ---------------------------------

                                val paymentStatus =
                                    document
                                        .getString("paymentStatus")
                                        ?.trim()
                                        ?.uppercase()
                                        ?: "PENDING"

                                val displayStatus =
                                    when (paymentStatus) {

                                        "PAID",
                                        "COMPLETED",
                                        "RECEIVED" -> {
                                            "Received"
                                        }

                                        else -> {
                                            "Pending"
                                        }
                                    }

                                // ---------------------------------
                                // CREATED DATE
                                // ---------------------------------

                                val timestamp =
                                    document.getTimestamp("createdAt")

                                val formattedDate =
                                    formatPaymentDate(timestamp)

                                // ---------------------------------
                                // CREATE TRANSACTION
                                // ---------------------------------

                                SupplierTransaction(
                                    transactionId = transactionId,
                                    orderId = orderId,
                                    description = description,
                                    amount = amount,
                                    status = displayStatus,
                                    date = formattedDate
                                )
                            }
                            .sortedByDescending {
                                it.date
                            }

                    isLoading = false
                    errorMessage = null
                }

            onDispose {
                listener.remove()
            }
        }
    }

    // =============================================================
    // MARK PAYMENT AS RECEIVED
    // =============================================================

    fun markPaymentAsReceived(
        orderId: String
    ) {

        updatingPaymentOrderId = orderId
        errorMessage = null

        firestore
            .collection("orders")
            .document(orderId)
            .update(
                "paymentStatus",
                "PAID"
            )
            .addOnSuccessListener {

                updatingPaymentOrderId = null
            }
            .addOnFailureListener { error ->

                updatingPaymentOrderId = null

                errorMessage =
                    error.localizedMessage
                        ?: "Unable to update payment status."
            }
    }

    // =============================================================
    // PAYMENT TOTALS
    // =============================================================

    val totalEarnings =
        transactions
            .filter {
                it.status == "Received"
            }
            .sumOf {
                it.amount
            }

    val pendingAmount =
        transactions
            .filter {
                it.status == "Pending"
            }
            .sumOf {
                it.amount
            }

    // =============================================================
    // UI
    // =============================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FieldCream)
            .padding(
                horizontal = 20.dp,
                vertical = 18.dp
            )
    ) {

        // =========================================================
        // HEADER
        // =========================================================

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

        // =========================================================
        // PAYMENT SUMMARY
        // =========================================================

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            PaymentSummaryCard(
                modifier = Modifier.weight(1f),
                title = "Received",
                value = formatRupees(totalEarnings)
            )

            PaymentSummaryCard(
                modifier = Modifier.weight(1f),
                title = "Pending",
                value = formatRupees(pendingAmount)
            )
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // =========================================================
        // TRANSACTION HISTORY TITLE
        // =========================================================

        Text(
            text = "Transaction History",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // =========================================================
        // ERROR
        // =========================================================

        if (errorMessage != null) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFEDEA)
                )
            ) {

                Text(
                    text = errorMessage!!,
                    modifier = Modifier.padding(14.dp),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }

        // =========================================================
        // LOADING
        // =========================================================

        if (isLoading) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                CircularProgressIndicator(
                    color = LeafGreen
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "Loading payments...",
                    color = MutedText
                )
            }

        }

        // =========================================================
        // EMPTY STATE
        // =========================================================

        else if (
            transactions.isEmpty() &&
            errorMessage == null
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "No payments yet.",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = "Payments will appear here when farmers place orders.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedText
                )
            }

        }

        // =========================================================
        // TRANSACTION LIST
        // =========================================================

        else {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(
                    items = transactions,
                    key = {
                        it.orderId
                    }
                ) { transaction ->

                    SupplierTransactionCard(
                        transaction = transaction,
                        isUpdating =
                            updatingPaymentOrderId ==
                                    transaction.orderId,
                        onMarkAsReceived = {
                            markPaymentAsReceived(
                                transaction.orderId
                            )
                        }
                    )
                }
            }
        }
    }
}

// =============================================================
// PAYMENT SUMMARY CARD
// =============================================================

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

// =============================================================
// TRANSACTION CARD
// =============================================================

@Composable
private fun SupplierTransactionCard(
    transaction: SupplierTransaction,
    isUpdating: Boolean,
    onMarkAsReceived: () -> Unit
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

            // =====================================================
            // TOP ROW
            // =====================================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

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
                    text = formatRupees(transaction.amount),
                    fontWeight = FontWeight.Bold,
                    color = LeafGreen
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            // =====================================================
            // PRODUCT DESCRIPTION
            // =====================================================

            Text(
                text = transaction.description,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            // =====================================================
            // DATE + PAYMENT STATUS
            // =====================================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
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
                    color =
                        if (transaction.status == "Pending") {
                            MaterialTheme.colorScheme.error
                        } else {
                            LeafGreen
                        }
                )
            }

            // =====================================================
            // MARK AS RECEIVED BUTTON
            // =====================================================

            if (transaction.status == "Pending") {

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Button(
                    onClick = onMarkAsReceived,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isUpdating
                ) {

                    if (isUpdating) {

                        CircularProgressIndicator(
                            modifier = Modifier.height(18.dp),
                            strokeWidth = 2.dp
                        )

                    } else {

                        Text(
                            text = "Mark as Received"
                        )
                    }
                }
            }
        }
    }
}

// =============================================================
// FORMAT RUPEES
// =============================================================

private fun formatRupees(
    amount: Double
): String {

    return "₹%,.2f".format(
        Locale.forLanguageTag("en-IN"),
        amount
    )
}

// =============================================================
// FORMAT PAYMENT DATE
// =============================================================

private fun formatPaymentDate(
    timestamp: Timestamp?
): String {

    if (timestamp == null) {
        return "Date unavailable"
    }

    val date =
        timestamp.toDate()

    val formatter =
        SimpleDateFormat(
            "dd MMM yyyy",
            Locale.getDefault()
        )

    return formatter.format(date)
}
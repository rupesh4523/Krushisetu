package com.sashya.krushisetu.feature.supplier

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.sashya.krushisetu.ui.theme.FieldCream
import com.sashya.krushisetu.ui.theme.LeafGreen
import com.sashya.krushisetu.ui.theme.MutedText
import java.text.SimpleDateFormat
import java.util.Locale


// ================================================================
// ORDER ITEM
// ================================================================

private data class OrderItem(
    val productName: String,
    val quantity: Int,
    val packSize: String,
    val price: Double
)


// ================================================================
// SUPPLIER ORDER
// ================================================================

private data class SupplierOrder(
    val orderId: String,
    val farmerName: String,
    val items: List<OrderItem>,
    val totalAmount: Double,
    val status: String,
    val deliveryAddress: String,
    val createdAtMillis: Long
)


// ================================================================
// ORDER STATUS VALUES
// ================================================================

private const val STATUS_NEW = "New"
private const val STATUS_PROCESSING = "Processing"
private const val STATUS_DISPATCHED = "Dispatched"
private const val STATUS_DELIVERED = "Delivered"


// ================================================================
// CONVERT FIRESTORE STATUS TO DISPLAY STATUS
// ================================================================
//
// Farmer checkout currently creates:
// status = "PLACED"
//
// Supplier UI displays:
// PLACED -> New
//
// Other statuses remain as their normal display names.
// ================================================================

private fun displayStatus(rawStatus: String): String {

    return when (rawStatus.uppercase()) {

        "PLACED",
        "NEW" -> STATUS_NEW

        "PROCESSING" -> STATUS_PROCESSING

        "DISPATCHED" -> STATUS_DISPATCHED

        "DELIVERED" -> STATUS_DELIVERED

        else -> rawStatus
    }
}


// ================================================================
// CONVERT DISPLAY STATUS TO FIRESTORE STATUS
// ================================================================
//
// UI:
// New
// Processing
// Dispatched
// Delivered
//
// Firestore:
// PLACED
// PROCESSING
// DISPATCHED
// DELIVERED
// ================================================================

private fun backendStatus(displayStatus: String): String {

    return when (displayStatus) {

        STATUS_NEW -> "PLACED"

        STATUS_PROCESSING -> "PROCESSING"

        STATUS_DISPATCHED -> "DISPATCHED"

        STATUS_DELIVERED -> "DELIVERED"

        else -> displayStatus.uppercase()
    }
}


// ================================================================
// SUPPLIER ORDERS SCREEN
// ================================================================

@Composable
fun SupplierOrdersScreen(
    onBack: () -> Unit
) {

    // ============================================================
    // SELECTED FILTER
    // ============================================================

    var selectedStatus by remember {
        mutableStateOf("All")
    }


    // ============================================================
    // ORDERS
    // ============================================================

    var orders by remember {
        mutableStateOf<List<SupplierOrder>>(emptyList())
    }


    // ============================================================
    // LOADING
    // ============================================================

    var isLoading by remember {
        mutableStateOf(true)
    }


    // ============================================================
    // ERROR
    // ============================================================

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }


    // ============================================================
    // ORDER CURRENTLY BEING UPDATED
    // ============================================================

    var updatingOrderId by remember {
        mutableStateOf<String?>(null)
    }


    // ============================================================
    // CURRENT SUPPLIER
    // ============================================================

    val supplierId =
        FirebaseAuth.getInstance().currentUser?.uid


    // ============================================================
    // FIRESTORE LISTENER
    // ============================================================

    DisposableEffect(supplierId) {

        if (supplierId == null) {

            isLoading = false

            errorMessage =
                "Supplier is not logged in."

            onDispose { }

        } else {

            val listener: ListenerRegistration =
                FirebaseFirestore
                    .getInstance()
                    .collection("orders")
                    .whereEqualTo(
                        "supplierId",
                        supplierId
                    )
                    .addSnapshotListener { snapshot, error ->

                        // =================================================
                        // FIRESTORE ERROR
                        // =================================================

                        if (error != null) {

                            isLoading = false

                            errorMessage =
                                error.message
                                    ?: "Failed to load orders."

                            return@addSnapshotListener
                        }


                        // =================================================
                        // NO SNAPSHOT
                        // =================================================

                        if (snapshot == null) {

                            isLoading = false

                            orders = emptyList()

                            return@addSnapshotListener
                        }


                        // =================================================
                        // READ ORDERS
                        // =================================================

                        try {

                            val loadedOrders =
                                snapshot.documents.map { document ->


                                    // -------------------------------------
                                    // ITEMS
                                    // -------------------------------------

                                    val rawItems =
                                        document.get("items")
                                                as? List<*>
                                            ?: emptyList<Any>()


                                    val parsedItems =
                                        rawItems.mapNotNull { rawItem ->

                                            val item =
                                                rawItem as? Map<*, *>
                                                    ?: return@mapNotNull null


                                            val productName =
                                                item["productName"]
                                                    ?.toString()
                                                    ?: item["name"]
                                                        ?.toString()
                                                    ?: "Product"


                                            val quantity =
                                                (
                                                        item["quantity"]
                                                                as? Number
                                                        )
                                                    ?.toInt()
                                                    ?: 1


                                            val packSize =
                                                item["packSize"]
                                                    ?.toString()
                                                    ?: ""


                                            val price =
                                                (
                                                        item["price"]
                                                                as? Number
                                                        )
                                                    ?.toDouble()
                                                    ?: 0.0


                                            OrderItem(
                                                productName =
                                                    productName,

                                                quantity =
                                                    quantity,

                                                packSize =
                                                    packSize,

                                                price =
                                                    price
                                            )
                                        }


                                    // -------------------------------------
                                    // TOTAL
                                    // -------------------------------------

                                    val totalAmount =
                                        (
                                                document.get(
                                                    "totalAmount"
                                                ) as? Number
                                                )
                                            ?.toDouble()
                                            ?: parsedItems.sumOf {
                                                it.price *
                                                        it.quantity
                                            }


                                    // -------------------------------------
                                    // CREATED AT
                                    // -------------------------------------

                                    val createdAtMillis =
                                        document
                                            .getTimestamp(
                                                "createdAt"
                                            )
                                            ?.toDate()
                                            ?.time
                                            ?: 0L


                                    // -------------------------------------
                                    // ORDER
                                    // -------------------------------------

                                    SupplierOrder(

                                        orderId =
                                            document.id,

                                        farmerName =
                                            document
                                                .getString(
                                                    "farmerName"
                                                )
                                                ?: "Farmer",

                                        items =
                                            parsedItems,

                                        totalAmount =
                                            totalAmount,

                                        status =
                                            displayStatus(
                                                document
                                                    .getString(
                                                        "status"
                                                    )
                                                    ?: STATUS_NEW
                                            ),

                                        deliveryAddress =
                                            document
                                                .getString(
                                                    "deliveryAddress"
                                                )
                                                ?: "",

                                        createdAtMillis =
                                            createdAtMillis
                                    )
                                }


                            // =================================================
                            // SORT NEWEST FIRST
                            // =================================================

                            orders =
                                loadedOrders
                                    .sortedByDescending {
                                        it.createdAtMillis
                                    }


                            errorMessage = null

                            isLoading = false

                        } catch (exception: Exception) {

                            isLoading = false

                            errorMessage =
                                exception.message
                                    ?: "Failed to read orders."
                        }
                    }


            // =========================================================
            // REMOVE LISTENER
            // =========================================================

            onDispose {
                listener.remove()
            }
        }
    }


    // ============================================================
    // UPDATE ORDER STATUS
    // ============================================================

    fun updateOrderStatus(
        order: SupplierOrder,
        newStatus: String
    ) {

        // ------------------------------------------------------------
        // SUPPLIER LOGIN CHECK
        // ------------------------------------------------------------

        if (supplierId == null) {

            errorMessage =
                "Supplier is not logged in."

            return
        }


        // ------------------------------------------------------------
        // PREVENT MULTIPLE UPDATES
        // ------------------------------------------------------------

        if (updatingOrderId != null) {
            return
        }


        // ------------------------------------------------------------
        // SHOW LOADING
        // ------------------------------------------------------------

        updatingOrderId =
            order.orderId

        errorMessage = null


        // ------------------------------------------------------------
        // UPDATE FIRESTORE
        // ------------------------------------------------------------

        FirebaseFirestore
            .getInstance()
            .collection("orders")
            .document(order.orderId)
            .update(
                "status",
                backendStatus(newStatus)
            )
            .addOnSuccessListener {

                // Firestore listener will automatically
                // refresh the order status.

                updatingOrderId = null
            }
            .addOnFailureListener { exception ->

                updatingOrderId = null

                errorMessage =
                    exception.localizedMessage
                        ?: "Unable to update order status."
            }
    }


    // ============================================================
    // STATUS FILTERS
    // ============================================================

    val statuses = listOf(
        "All",
        STATUS_NEW,
        STATUS_PROCESSING,
        STATUS_DISPATCHED,
        STATUS_DELIVERED
    )


    // ============================================================
    // FILTER ORDERS
    // ============================================================

    val filteredOrders =
        orders.filter { order ->

            selectedStatus == "All" ||
                    order.status.equals(
                        selectedStatus,
                        ignoreCase = true
                    )
        }


    // ============================================================
    // MAIN SCREEN
    // ============================================================

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
            modifier =
                Modifier.fillMaxWidth(),

            verticalAlignment =
                Alignment.CenterVertically,

            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text = "Orders",

                    style =
                        MaterialTheme
                            .typography
                            .headlineSmall,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        LeafGreen
                )


                Text(
                    text =
                        "Manage farmer orders and their status",

                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,

                    color =
                        MutedText
                )
            }


            TextButton(
                onClick = onBack
            ) {

                Text(
                    text = "Back",

                    color =
                        LeafGreen,

                    fontWeight =
                        FontWeight.SemiBold
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(18.dp)
        )


        // =========================================================
        // STATUS FILTERS - FIRST ROW
        // =========================================================

        Row(
            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.spacedBy(6.dp)
        ) {

            statuses
                .take(3)
                .forEach { status ->

                    OrderFilterButton(

                        text =
                            status,

                        selected =
                            selectedStatus == status,

                        onClick = {

                            selectedStatus =
                                status
                        },

                        modifier =
                            Modifier.weight(1f)
                    )
                }
        }


        Spacer(
            modifier =
                Modifier.height(8.dp)
        )


        // =========================================================
        // STATUS FILTERS - SECOND ROW
        // =========================================================

        Row(
            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.spacedBy(6.dp)
        ) {

            statuses
                .drop(3)
                .forEach { status ->

                    OrderFilterButton(

                        text =
                            status,

                        selected =
                            selectedStatus == status,

                        onClick = {

                            selectedStatus =
                                status
                        },

                        modifier =
                            Modifier.weight(1f)
                    )
                }
        }


        Spacer(
            modifier =
                Modifier.height(18.dp)
        )


        // =========================================================
        // ERROR MESSAGE
        // =========================================================

        if (errorMessage != null) {

            Text(
                text =
                    errorMessage!!,

                color =
                    MaterialTheme
                        .colorScheme
                        .error,

                modifier =
                    Modifier.fillMaxWidth()
            )


            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )
        }


        // =========================================================
        // CONTENT
        // =========================================================

        when {


            // =====================================================
            // LOADING
            // =====================================================

            isLoading -> {

                Column(
                    modifier =
                        Modifier.fillMaxSize(),

                    horizontalAlignment =
                        Alignment.CenterHorizontally,

                    verticalArrangement =
                        Arrangement.Center
                ) {

                    CircularProgressIndicator(
                        color =
                            LeafGreen
                    )


                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )


                    Text(
                        text =
                            "Loading orders...",

                        color =
                            MutedText
                    )
                }
            }


            // =====================================================
            // ERROR
            // =====================================================

            errorMessage != null -> {

                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(20.dp),

                    horizontalAlignment =
                        Alignment.CenterHorizontally,

                    verticalArrangement =
                        Arrangement.Center
                ) {

                    Text(
                        text =
                            "Unable to load orders",

                        style =
                            MaterialTheme
                                .typography
                                .titleMedium,

                        fontWeight =
                            FontWeight.Bold
                    )


                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )


                    Text(
                        text =
                            errorMessage ?: "",

                        color =
                            MutedText
                    )
                }
            }


            // =====================================================
            // SUCCESS
            // =====================================================

            else -> {

                Text(
                    text =
                        "${filteredOrders.size} orders",

                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,

                    fontWeight =
                        FontWeight.Bold
                )


                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )


                // =================================================
                // EMPTY
                // =================================================

                if (filteredOrders.isEmpty()) {

                    Column(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(20.dp),

                        horizontalAlignment =
                            Alignment.CenterHorizontally,

                        verticalArrangement =
                            Arrangement.Center
                    ) {

                        Text(
                            text =
                                "No orders found",

                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium,

                            fontWeight =
                                FontWeight.Bold
                        )


                        Spacer(
                            modifier =
                                Modifier.height(6.dp)
                        )


                        Text(
                            text =
                                if (
                                    selectedStatus == "All"
                                ) {

                                    "Orders placed by farmers will appear here."

                                } else {

                                    "There are no $selectedStatus orders."
                                },

                            color =
                                MutedText
                        )
                    }


                } else {


                    // =============================================
                    // ORDER LIST
                    // =============================================

                    LazyColumn(

                        modifier =
                            Modifier.fillMaxSize(),

                        verticalArrangement =
                            Arrangement.spacedBy(12.dp),

                        contentPadding =
                            PaddingValues(
                                bottom = 20.dp
                            )
                    ) {

                        items(

                            items =
                                filteredOrders,

                            key = {
                                it.orderId
                            }

                        ) { order ->


                            SupplierOrderCard(

                                order =
                                    order,

                                isUpdating =
                                    updatingOrderId ==
                                            order.orderId,

                                onUpdateStatus = {
                                        newStatus ->

                                    updateOrderStatus(
                                        order =
                                            order,

                                        newStatus =
                                            newStatus
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


// ================================================================
// FILTER BUTTON
// ================================================================

@Composable
private fun OrderFilterButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Button(

        onClick =
            onClick,

        modifier =
            modifier,

        shape =
            RoundedCornerShape(10.dp),

        contentPadding =
            PaddingValues(
                horizontal = 6.dp,
                vertical = 8.dp
            ),

        colors =
            ButtonDefaults.buttonColors(

                containerColor =
                    if (selected) {

                        LeafGreen

                    } else {

                        Color.White
                    },

                contentColor =
                    if (selected) {

                        Color.White

                    } else {

                        LeafGreen
                    }
            )
    ) {

        Text(
            text =
                text,

            fontWeight =
                FontWeight.SemiBold
        )
    }
}


// ================================================================
// ORDER CARD
// ================================================================

@Composable
private fun SupplierOrderCard(
    order: SupplierOrder,
    isUpdating: Boolean,
    onUpdateStatus: (String) -> Unit
) {

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(16.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {

        Column(

            modifier =
                Modifier.padding(16.dp)
        ) {


            // =====================================================
            // ORDER ID + STATUS
            // =====================================================

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(

                    text =
                        "#${order.orderId}",

                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,

                    fontWeight =
                        FontWeight.Bold
                )


                Text(

                    text =
                        order.status,

                    color =
                        LeafGreen,

                    fontWeight =
                        FontWeight.Bold
                )
            }


            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )


            // =====================================================
            // FARMER
            // =====================================================

            Text(

                text =
                    order.farmerName,

                style =
                    MaterialTheme
                        .typography
                        .bodyLarge,

                fontWeight =
                    FontWeight.SemiBold
            )


            // =====================================================
            // DATE
            // =====================================================

            if (
                order.createdAtMillis > 0L
            ) {

                Spacer(
                    modifier =
                        Modifier.height(3.dp)
                )


                Text(

                    text =
                        formatOrderDate(
                            order.createdAtMillis
                        ),

                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,

                    color =
                        MutedText
                )
            }


            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )


            // =====================================================
            // PRODUCTS
            // =====================================================

            if (order.items.isEmpty()) {

                Text(

                    text =
                        "No product details available",

                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,

                    color =
                        MutedText
                )

            } else {

                order.items.forEach { item ->

                    Row(

                        modifier =
                            Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {

                        Column(

                            modifier =
                                Modifier.weight(1f)
                        ) {

                            Text(

                                text =
                                    "${item.quantity} × ${item.productName}",

                                style =
                                    MaterialTheme
                                        .typography
                                        .bodyMedium,

                                fontWeight =
                                    FontWeight.SemiBold
                            )


                            if (
                                item.packSize.isNotBlank()
                            ) {

                                Text(

                                    text =
                                        item.packSize,

                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodySmall,

                                    color =
                                        MutedText
                                )
                            }
                        }


                        Text(

                            text =
                                "₹${formatAmount(
                                    item.price *
                                            item.quantity
                                )}",

                            fontWeight =
                                FontWeight.SemiBold
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.height(6.dp)
                    )
                }
            }


            // =====================================================
            // DELIVERY ADDRESS
            // =====================================================

            if (
                order.deliveryAddress.isNotBlank()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(6.dp)
                )


                Text(

                    text =
                        "Delivery address",

                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,

                    fontWeight =
                        FontWeight.Bold
                )


                Spacer(
                    modifier =
                        Modifier.height(2.dp)
                )


                Text(

                    text =
                        order.deliveryAddress,

                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,

                    color =
                        MutedText
                )
            }


            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )


            // =====================================================
            // TOTAL
            // =====================================================

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(

                    text =
                        "Total",

                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,

                    fontWeight =
                        FontWeight.Bold
                )


                Text(

                    text =
                        "₹${formatAmount(
                            order.totalAmount
                        )}",

                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        LeafGreen
                )
            }


            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )


            // =====================================================
            // STATUS ACTION
            // =====================================================

            when (order.status) {


                // =================================================
                // NEW
                // =================================================

                STATUS_NEW -> {

                    Button(

                        onClick = {

                            onUpdateStatus(
                                STATUS_PROCESSING
                            )
                        },

                        enabled =
                            !isUpdating,

                        modifier =
                            Modifier.fillMaxWidth()
                    ) {

                        Text(

                            text =
                                if (isUpdating) {

                                    "Updating..."

                                } else {

                                    "Mark as Processing"
                                }
                        )
                    }
                }


                // =================================================
                // PROCESSING
                // =================================================

                STATUS_PROCESSING -> {

                    Button(

                        onClick = {

                            onUpdateStatus(
                                STATUS_DISPATCHED
                            )
                        },

                        enabled =
                            !isUpdating,

                        modifier =
                            Modifier.fillMaxWidth()
                    ) {

                        Text(

                            text =
                                if (isUpdating) {

                                    "Updating..."

                                } else {

                                    "Mark as Dispatched"
                                }
                        )
                    }
                }


                // =================================================
                // DISPATCHED
                // =================================================

                STATUS_DISPATCHED -> {

                    Button(

                        onClick = {

                            onUpdateStatus(
                                STATUS_DELIVERED
                            )
                        },

                        enabled =
                            !isUpdating,

                        modifier =
                            Modifier.fillMaxWidth()
                    ) {

                        Text(

                            text =
                                if (isUpdating) {

                                    "Updating..."

                                } else {

                                    "Mark as Delivered"
                                }
                        )
                    }
                }


                // =================================================
                // DELIVERED
                // =================================================

                STATUS_DELIVERED -> {

                    Text(

                        text =
                            "Order completed",

                        modifier =
                            Modifier.fillMaxWidth(),

                        color =
                            LeafGreen,

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }
    }
}


// ================================================================
// FORMAT AMOUNT
// ================================================================

private fun formatAmount(
    amount: Double
): String {

    return if (
        amount % 1.0 == 0.0
    ) {

        amount
            .toLong()
            .toString()

    } else {

        String.format(
            Locale.getDefault(),
            "%.2f",
            amount
        )
    }
}


// ================================================================
// FORMAT ORDER DATE
// ================================================================

private fun formatOrderDate(
    timeMillis: Long
): String {

    val formatter =
        SimpleDateFormat(
            "dd MMM yyyy, hh:mm a",
            Locale.getDefault()
        )

    return formatter.format(
        timeMillis
    )
}
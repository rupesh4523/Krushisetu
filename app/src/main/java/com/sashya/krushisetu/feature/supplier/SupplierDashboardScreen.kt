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
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sashya.krushisetu.ui.theme.FieldCream
import com.sashya.krushisetu.ui.theme.LeafGreen
import com.sashya.krushisetu.ui.theme.MutedText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// =============================================================
// DASHBOARD STATISTICS
// =============================================================

private data class SupplierDashboardStats(
    val productCount: Int = 0,
    val pendingOrders: Int = 0,
    val deliveries: Int = 0,
    val revenue: Double = 0.0,
    val monthlyRevenue: List<MonthlyRevenue> = emptyList()
)

// =============================================================
// MONTHLY REVENUE
// =============================================================

private data class MonthlyRevenue(
    val month: String,
    val amount: Double
)

// =============================================================
// SUPPLIER DASHBOARD
// =============================================================

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

    // =========================================================
    // FIREBASE
    // =========================================================

    val firestore = remember {
        FirebaseFirestore.getInstance()
    }

    val supplierId = remember {
        FirebaseAuth.getInstance().currentUser?.uid
    }

    // =========================================================
    // DASHBOARD STATE
    // =========================================================

    var dashboardStats by remember {
        mutableStateOf(
            SupplierDashboardStats()
        )
    }

    // =========================================================
    // FIRESTORE LISTENERS
    // =========================================================

    DisposableEffect(supplierId) {

        if (supplierId == null) {

            onDispose { }

        } else {

            // =====================================================
            // PRODUCTS LISTENER
            // =====================================================

            val productsListener =
                firestore
                    .collection("products")
                    .whereEqualTo(
                        "supplierId",
                        supplierId
                    )
                    .addSnapshotListener { snapshot, error ->

                        if (
                            error != null ||
                            snapshot == null
                        ) {
                            return@addSnapshotListener
                        }

                        dashboardStats =
                            dashboardStats.copy(
                                productCount =
                                    snapshot.documents.size
                            )
                    }

            // =====================================================
            // ORDERS LISTENER
            // =====================================================

            val ordersListener =
                firestore
                    .collection("orders")
                    .whereEqualTo(
                        "supplierId",
                        supplierId
                    )
                    .addSnapshotListener { snapshot, error ->

                        if (
                            error != null ||
                            snapshot == null
                        ) {
                            return@addSnapshotListener
                        }

                        // =================================================
                        // COUNTERS
                        // =================================================

                        var pendingOrders = 0

                        var deliveries = 0

                        var revenue = 0.0

                        // =================================================
                        // LAST SIX MONTHS
                        // =================================================

                        val monthlyRevenueMap =
                            linkedMapOf<String, Double>()

                        val monthKeys =
                            mutableListOf<String>()

                        val monthLabels =
                            mutableListOf<String>()

                        val calendar =
                            Calendar.getInstance()

                        // Start five months before current month.
                        // This gives us six months including current month.

                        calendar.add(
                            Calendar.MONTH,
                            -5
                        )

                        repeat(6) {

                            val year =
                                calendar.get(
                                    Calendar.YEAR
                                )

                            val month =
                                calendar.get(
                                    Calendar.MONTH
                                ) + 1

                            val key =
                                String.format(
                                    Locale.getDefault(),
                                    "%04d-%02d",
                                    year,
                                    month
                                )

                            val label =
                                SimpleDateFormat(
                                    "MMM",
                                    Locale.getDefault()
                                ).format(
                                    calendar.time
                                )

                            monthKeys.add(
                                key
                            )

                            monthLabels.add(
                                label
                            )

                            monthlyRevenueMap[key] =
                                0.0

                            calendar.add(
                                Calendar.MONTH,
                                1
                            )
                        }

                        // =================================================
                        // PROCESS EVERY SUPPLIER ORDER
                        // =================================================

                        snapshot.documents.forEach { document ->

                            // ---------------------------------------------
                            // ORDER STATUS
                            // ---------------------------------------------

                            val status =
                                document
                                    .getString("status")
                                    ?.uppercase()
                                    ?: ""

                            // ---------------------------------------------
                            // PAYMENT STATUS
                            // ---------------------------------------------

                            val paymentStatus =
                                document
                                    .getString("paymentStatus")
                                    ?.uppercase()
                                    ?: "PENDING"

                            // =================================================
                            // PENDING ORDERS
                            //
                            // PLACED and PROCESSING are still pending
                            // from the supplier's perspective.
                            // =================================================

                            if (
                                status == "PLACED" ||
                                status == "PROCESSING"
                            ) {
                                pendingOrders++
                            }

                            // =================================================
                            // DELIVERIES
                            //
                            // Once dispatched, the order becomes a delivery.
                            // Delivered orders remain part of delivery history.
                            // =================================================

                            if (
                                status == "DISPATCHED" ||
                                status == "DELIVERED"
                            ) {
                                deliveries++
                            }

                            // =================================================
                            // REVENUE
                            //
                            // IMPORTANT:
                            // Revenue is counted ONLY after payment is received.
                            //
                            // PENDING payment  -> NOT revenue
                            // PAID payment     -> revenue
                            // COMPLETED        -> revenue
                            // RECEIVED         -> revenue
                            // CANCELLED order  -> NOT revenue
                            // =================================================

                            val paymentReceived =
                                paymentStatus == "PAID" ||
                                        paymentStatus == "COMPLETED" ||
                                        paymentStatus == "RECEIVED"

                            if (
                                status != "CANCELLED" &&
                                paymentReceived
                            ) {

                                val orderTotal =
                                    document
                                        .getDouble(
                                            "totalAmount"
                                        )
                                        ?: 0.0

                                revenue +=
                                    orderTotal

                                // =============================================
                                // MONTHLY REVENUE
                                //
                                // Only paid/received orders are included.
                                // =============================================

                                val createdAt =
                                    document
                                        .getTimestamp(
                                            "createdAt"
                                        )

                                if (
                                    createdAt != null
                                ) {

                                    val orderCalendar =
                                        Calendar.getInstance()

                                    orderCalendar.time =
                                        createdAt.toDate()

                                    val orderYear =
                                        orderCalendar.get(
                                            Calendar.YEAR
                                        )

                                    val orderMonth =
                                        orderCalendar.get(
                                            Calendar.MONTH
                                        ) + 1

                                    val orderMonthKey =
                                        String.format(
                                            Locale.getDefault(),
                                            "%04d-%02d",
                                            orderYear,
                                            orderMonth
                                        )

                                    if (
                                        monthlyRevenueMap.containsKey(
                                            orderMonthKey
                                        )
                                    ) {

                                        monthlyRevenueMap[
                                            orderMonthKey
                                        ] =
                                            (
                                                    monthlyRevenueMap[
                                                        orderMonthKey
                                                    ] ?: 0.0
                                                    ) + orderTotal
                                    }
                                }
                            }
                        }

                        // =================================================
                        // BUILD MONTHLY REVENUE LIST
                        // =================================================

                        val monthlyRevenue =
                            monthKeys.mapIndexed {
                                    index,
                                    key ->

                                MonthlyRevenue(
                                    month =
                                        monthLabels[index],
                                    amount =
                                        monthlyRevenueMap[
                                            key
                                        ] ?: 0.0
                                )
                            }

                        // =================================================
                        // UPDATE ONLY ORDER DATA
                        //
                        // Product count is preserved.
                        // =================================================

                        dashboardStats =
                            dashboardStats.copy(
                                pendingOrders =
                                    pendingOrders,

                                deliveries =
                                    deliveries,

                                revenue =
                                    revenue,

                                monthlyRevenue =
                                    monthlyRevenue
                            )
                    }

            // =====================================================
            // REMOVE LISTENERS
            // =====================================================

            onDispose {

                productsListener.remove()

                ordersListener.remove()
            }
        }
    }

    // =============================================================
    // FORMAT REVENUE
    // =============================================================

    val formattedRevenue =
        formatRevenue(
            dashboardStats.revenue
        )

    // =============================================================
    // ANALYTICS DATA
    // =============================================================

    val monthlyRevenue =
        if (
            dashboardStats.monthlyRevenue.isEmpty()
        ) {

            listOf(
                MonthlyRevenue(
                    month = "Jan",
                    amount = 0.0
                ),

                MonthlyRevenue(
                    month = "Feb",
                    amount = 0.0
                ),

                MonthlyRevenue(
                    month = "Mar",
                    amount = 0.0
                ),

                MonthlyRevenue(
                    month = "Apr",
                    amount = 0.0
                ),

                MonthlyRevenue(
                    month = "May",
                    amount = 0.0
                ),

                MonthlyRevenue(
                    month = "Jun",
                    amount = 0.0
                )
            )

        } else {

            dashboardStats.monthlyRevenue
        }

    // =============================================================
    // FIND HIGHEST MONTHLY REVENUE
    // =============================================================

    val maximumMonthlyRevenue =
        monthlyRevenue
            .maxOfOrNull {
                it.amount
            }
            ?.takeIf {
                it > 0.0
            }
            ?: 1.0

    // =============================================================
    // MAIN UI
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

            // -----------------------------------------------------
            // PROFILE BUTTON
            // -----------------------------------------------------

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

                // -------------------------------------------------
                // MANAGE PRODUCTS BUTTON
                // -------------------------------------------------

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

        // ---------------------------------------------------------
        // FIRST ROW
        // ---------------------------------------------------------

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            SupplierSummaryCard(
                modifier = Modifier.weight(1f),
                icon = "📦",
                value =
                    dashboardStats.productCount
                        .toString(),
                label = "Products"
            )

            SupplierSummaryCard(
                modifier = Modifier.weight(1f),
                icon = "📋",
                value =
                    dashboardStats.pendingOrders
                        .toString(),
                label = "Pending Orders"
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // ---------------------------------------------------------
        // SECOND ROW
        // ---------------------------------------------------------

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            SupplierSummaryCard(
                modifier = Modifier.weight(1f),
                icon = "🚚",
                value =
                    dashboardStats.deliveries
                        .toString(),
                label = "Deliveries"
            )

            SupplierSummaryCard(
                modifier = Modifier.weight(1f),
                icon = "₹",
                value =
                    formattedRevenue,
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

                // -------------------------------------------------
                // DYNAMIC SIX MONTH SALES
                // -------------------------------------------------

                monthlyRevenue.forEachIndexed {
                        index,
                        monthData ->

                    val progress =
                        if (
                            monthData.amount <= 0.0
                        ) {

                            0f

                        } else {

                            (
                                    monthData.amount /
                                            maximumMonthlyRevenue
                                    )
                                .toFloat()
                                .coerceIn(
                                    0f,
                                    1f
                                )
                        }

                    AnalyticsBar(
                        month =
                            monthData.month,

                        value =
                            formatRevenue(
                                monthData.amount
                            ),

                        progress =
                            progress
                    )

                    if (
                        index <
                        monthlyRevenue.lastIndex
                    ) {

                        Spacer(
                            modifier =
                                Modifier.height(
                                    10.dp
                                )
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                // -------------------------------------------------
                // FULL ANALYTICS BUTTON
                // -------------------------------------------------

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

// =============================================================
// SUMMARY CARD
// =============================================================

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

// =============================================================
// ANALYTICS BAR
// =============================================================

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
            modifier = Modifier.size(
                width = 35.dp,
                height = 30.dp
            ),
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
            modifier = Modifier.size(
                width = 55.dp,
                height = 30.dp
            ),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = LeafGreen
        )
    }
}

// =============================================================
// SUPPLIER BOTTOM BAR
// =============================================================

@Composable
fun SupplierBottomBar(
    onHome: () -> Unit,
    onProducts: () -> Unit,
    onOrders: () -> Unit,
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
            icon = "₹",
            label = "Payments",
            onClick = onPayments
        )
    }
}

// =============================================================
// BOTTOM BAR ITEM
// =============================================================

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

// =============================================================
// FORMAT REVENUE
// =============================================================

private fun formatRevenue(
    amount: Double
): String {

    return when {

        amount >= 1_000_000 -> {

            String.format(
                Locale.getDefault(),
                "₹%.1fM",
                amount / 1_000_000.0
            )
        }

        amount >= 100_000 -> {

            String.format(
                Locale.getDefault(),
                "₹%.1fL",
                amount / 100_000.0
            )
        }

        amount >= 1_000 -> {

            String.format(
                Locale.getDefault(),
                "₹%.1fK",
                amount / 1_000.0
            )
        }

        else -> {

            String.format(
                Locale.getDefault(),
                "₹%.0f",
                amount
            )
        }
    }
}
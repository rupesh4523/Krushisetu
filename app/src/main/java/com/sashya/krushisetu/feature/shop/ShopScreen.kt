package com.sashya.krushisetu.feature.shop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.sashya.krushisetu.data.local.LanguageManager
import com.sashya.krushisetu.ui.theme.FieldCream
import com.sashya.krushisetu.ui.theme.LeafGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.layout.statusBarsPadding

private data class ShopSupplier(
    val uid: String,
    val name: String,
    val businessType: String,
    val location: String
)

private data class ShopProduct(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val price: Double,
    val stock: Long
)

private data class CartItem(
    val productId: String,
    val supplierId: String,
    val productName: String,
    val price: Double,
    val quantity: Long
)

private data class FarmerOrder(
    val orderId: String,
    val supplierId: String,
    val items: List<OrderHistoryItem>,
    val totalAmount: Double,
    val status: String,
    val deliveryAddress: String,
    val paymentStatus: String,
    val createdAtMillis: Long
)

private data class OrderHistoryItem(
    val productName: String,
    val quantity: Int,
    val price: Double
)

@Composable
fun ShopScreen(
    modifier: Modifier = Modifier
) {
    val firestore = remember {
        FirebaseFirestore.getInstance()
    }

    val farmerId = remember {
        FirebaseAuth.getInstance()
            .currentUser
            ?.uid
    }

    val isHindi = LanguageManager.isHindi()

    var suppliers by remember {
        mutableStateOf<List<ShopSupplier>>(emptyList())
    }

    var selectedSupplier by remember {
        mutableStateOf<ShopSupplier?>(null)
    }

    var products by remember {
        mutableStateOf<List<ShopProduct>>(emptyList())
    }

    var cartItems by remember {
        mutableStateOf<List<CartItem>>(emptyList())
    }

    var showCart by remember {
        mutableStateOf(false)
    }

    var isLoadingSuppliers by remember {
        mutableStateOf(true)
    }

    var isLoadingProducts by remember {
        mutableStateOf(false)
    }

    var isLoadingCart by remember {
        mutableStateOf(false)
    }

    var addingProductId by remember {
        mutableStateOf<String?>(null)
    }

    var updatingCartProductId by remember {
        mutableStateOf<String?>(null)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    var isPlacingOrder by remember {
        mutableStateOf(false)
    }

    var orderSuccessMessage by remember {
        mutableStateOf<String?>(null)
    }

    var farmerOrders by remember {
        mutableStateOf<List<FarmerOrder>>(emptyList())
    }

    var isLoadingOrderHistory by remember {
        mutableStateOf(false)
    }

    var orderHistoryError by remember {
        mutableStateOf<String?>(null)
    }

    val cartQuantity =
        cartItems.sumOf {
            it.quantity
        }

    // =============================================================
    // ORDER HISTORY LISTENER
    // =============================================================

    DisposableEffect(farmerId) {

        if (farmerId == null) {

            farmerOrders = emptyList()

            onDispose { }

        } else {

            isLoadingOrderHistory = true

            val registration =
                firestore
                    .collection("orders")
                    .whereEqualTo(
                        "farmerId",
                        farmerId
                    )
                    .addSnapshotListener { snapshot, error ->

                        isLoadingOrderHistory = false

                        if (error != null) {

                            orderHistoryError =
                                error.localizedMessage
                                    ?: if (isHindi) {
                                        "ऑर्डर इतिहास लोड नहीं हो सका।"
                                    } else {
                                        "Unable to load order history."
                                    }

                            return@addSnapshotListener
                        }

                        orderHistoryError = null

                        farmerOrders =
                            snapshot?.documents
                                .orEmpty()
                                .map { document ->

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
                                                    ?: return@mapNotNull null

                                            val quantity =
                                                (item["quantity"] as? Number)
                                                    ?.toInt()
                                                    ?: 0

                                            val price =
                                                (item["price"] as? Number)
                                                    ?.toDouble()
                                                    ?: 0.0

                                            OrderHistoryItem(
                                                productName = productName,
                                                quantity = quantity,
                                                price = price
                                            )
                                        }

                                    val createdAt =
                                        document.getTimestamp(
                                            "createdAt"
                                        )

                                    FarmerOrder(
                                        orderId = document.id,
                                        supplierId =
                                            document.getString(
                                                "supplierId"
                                            ) ?: "",
                                        items = parsedItems,
                                        totalAmount =
                                            document.getDouble(
                                                "totalAmount"
                                            ) ?: 0.0,
                                        status =
                                            document.getString(
                                                "status"
                                            ) ?: "PLACED",
                                        deliveryAddress =
                                            document.getString(
                                                "deliveryAddress"
                                            ) ?: "",
                                        paymentStatus =
                                            document.getString(
                                                "paymentStatus"
                                            ) ?: "PENDING",
                                        createdAtMillis =
                                            createdAt
                                                ?.toDate()
                                                ?.time
                                                ?: 0L
                                    )
                                }
                                .sortedByDescending {
                                    it.createdAtMillis
                                }
                    }

            onDispose {
                registration.remove()
            }
        }
    }

    // =============================================================
    // SUPPLIER LISTENER
    // =============================================================

    DisposableEffect(Unit) {

        isLoadingSuppliers = true

        val registration =
            firestore
                .collection("users")
                .whereEqualTo(
                    "role",
                    "SUPPLIER"
                )
                .addSnapshotListener { snapshot, error ->

                    isLoadingSuppliers = false

                    if (error != null) {

                        errorMessage =
                            error.localizedMessage
                                ?: if (isHindi) {
                                    "सप्लायर लोड नहीं हो सके।"
                                } else {
                                    "Unable to load suppliers."
                                }

                        return@addSnapshotListener
                    }

                    errorMessage = null

                    suppliers =
                        snapshot?.documents
                            .orEmpty()
                            .map { document ->

                                val name =
                                    document.getString("name")
                                        ?: document.getString(
                                            "companyName"
                                        )
                                        ?: "Supplier"

                                val businessType =
                                    document.getString(
                                        "businessType"
                                    )
                                        ?: "Agriculture Supplier"

                                val location =
                                    document.getString(
                                        "branchLocations"
                                    )
                                        ?: document.getString(
                                            "location"
                                        )
                                        ?: ""

                                ShopSupplier(
                                    uid = document.id,
                                    name = name,
                                    businessType = businessType,
                                    location = location
                                )
                            }
                }

        onDispose {
            registration.remove()
        }
    }

    // =============================================================
    // PRODUCT LISTENER
    // =============================================================

    LaunchedEffect(
        selectedSupplier?.uid
    ) {

        val supplierId =
            selectedSupplier?.uid

        if (supplierId == null) {

            products = emptyList()
            isLoadingProducts = false

            return@LaunchedEffect
        }

        isLoadingProducts = true
        errorMessage = null

        firestore
            .collection("products")
            .whereEqualTo(
                "supplierId",
                supplierId
            )
            .addSnapshotListener { snapshot, error ->

                isLoadingProducts = false

                if (error != null) {

                    errorMessage =
                        error.localizedMessage
                            ?: if (isHindi) {
                                "उत्पाद लोड नहीं हो सके।"
                            } else {
                                "Unable to load products."
                            }

                    return@addSnapshotListener
                }

                errorMessage = null

                products =
                    snapshot?.documents
                        .orEmpty()
                        .map { document ->

                            ShopProduct(
                                id = document.id,
                                name =
                                    document.getString(
                                        "productName"
                                    ) ?: "Product",
                                description =
                                    document.getString(
                                        "description"
                                    ) ?: "",
                                category =
                                    document.getString(
                                        "category"
                                    ) ?: "Other",
                                price =
                                    document.getDouble(
                                        "price"
                                    ) ?: 0.0,
                                stock =
                                    document.getLong(
                                        "stock"
                                    ) ?: 0L
                            )
                        }
            }
    }

    // =============================================================
    // CART LISTENER
    // =============================================================

    DisposableEffect(farmerId) {

        if (farmerId == null) {

            cartItems = emptyList()
            isLoadingCart = false

            onDispose { }

        } else {

            isLoadingCart = true

            val registration =
                firestore
                    .collection("users")
                    .document(farmerId)
                    .collection("cart")
                    .addSnapshotListener { snapshot, error ->

                        isLoadingCart = false

                        if (error != null) {

                            errorMessage =
                                error.localizedMessage
                                    ?: if (isHindi) {
                                        "कार्ट लोड नहीं हो सका।"
                                    } else {
                                        "Unable to load cart."
                                    }

                            return@addSnapshotListener
                        }

                        errorMessage = null

                        cartItems =
                            snapshot?.documents
                                .orEmpty()
                                .mapNotNull { document ->

                                    val quantity =
                                        document.getLong(
                                            "quantity"
                                        ) ?: 0L

                                    if (quantity <= 0L) {
                                        return@mapNotNull null
                                    }

                                    CartItem(
                                        productId =
                                            document.getString(
                                                "productId"
                                            ) ?: document.id,
                                        supplierId =
                                            document.getString(
                                                "supplierId"
                                            ) ?: "",
                                        productName =
                                            document.getString(
                                                "productName"
                                            ) ?: "Product",
                                        price =
                                            document.getDouble(
                                                "price"
                                            ) ?: 0.0,
                                        quantity = quantity
                                    )
                                }
                    }

            onDispose {
                registration.remove()
            }
        }
    }

    // =============================================================
    // ADD TO CART
    // =============================================================

    fun addToCart(
        product: ShopProduct,
        supplierId: String
    ) {

        val currentFarmerId =
            FirebaseAuth.getInstance()
                .currentUser
                ?.uid

        if (currentFarmerId == null) {

            errorMessage =
                if (isHindi) {
                    "कार्ट का उपयोग करने के लिए किसान के रूप में साइन इन करें।"
                } else {
                    "You must be signed in as a farmer to use the cart."
                }

            return
        }

        if (product.stock <= 0L) {

            errorMessage =
                if (isHindi) {
                    "यह उत्पाद स्टॉक में नहीं है।"
                } else {
                    "This product is out of stock."
                }

            return
        }

        addingProductId = product.id
        errorMessage = null

        val productRef =
            firestore
                .collection("products")
                .document(product.id)

        val cartRef =
            firestore
                .collection("users")
                .document(currentFarmerId)
                .collection("cart")
                .document(product.id)

        firestore
            .runTransaction { transaction ->

                val productSnapshot =
                    transaction.get(productRef)

                if (!productSnapshot.exists()) {

                    throw IllegalStateException(
                        "PRODUCT_NOT_FOUND"
                    )
                }

                val currentStock =
                    productSnapshot.getLong(
                        "stock"
                    ) ?: 0L

                if (currentStock <= 0L) {

                    throw IllegalStateException(
                        "OUT_OF_STOCK"
                    )
                }

                val cartSnapshot =
                    transaction.get(cartRef)

                val currentQuantity =
                    cartSnapshot.getLong(
                        "quantity"
                    ) ?: 0L

                transaction.update(
                    productRef,
                    "stock",
                    currentStock - 1L
                )

                transaction.set(
                    cartRef,
                    mapOf(
                        "productId" to product.id,
                        "supplierId" to supplierId,
                        "productName" to product.name,
                        "price" to product.price,
                        "quantity" to currentQuantity + 1L
                    ),
                    SetOptions.merge()
                )

            }
            .addOnSuccessListener {

                addingProductId = null

            }
            .addOnFailureListener { exception ->

                addingProductId = null

                errorMessage =
                    when (exception.message) {

                        "PRODUCT_NOT_FOUND" ->
                            if (isHindi) {
                                "यह उत्पाद अब उपलब्ध नहीं है।"
                            } else {
                                "This product is no longer available."
                            }

                        "OUT_OF_STOCK" ->
                            if (isHindi) {
                                "यह उत्पाद स्टॉक में नहीं है।"
                            } else {
                                "This product is out of stock."
                            }

                        else ->
                            exception.localizedMessage
                                ?: if (isHindi) {
                                    "उत्पाद को कार्ट में नहीं जोड़ा जा सका।"
                                } else {
                                    "Unable to add product to cart."
                                }
                    }
            }
    }

    // =============================================================
    // DECREASE CART QUANTITY
    // =============================================================

    fun decreaseCartQuantity(
        item: CartItem
    ) {

        val currentFarmerId =
            FirebaseAuth.getInstance()
                .currentUser
                ?.uid

        if (currentFarmerId == null) {

            errorMessage =
                if (isHindi) {
                    "आपको किसान के रूप में साइन इन करना होगा।"
                } else {
                    "You must be signed in as a farmer."
                }

            return
        }

        updatingCartProductId =
            item.productId

        errorMessage = null

        val productRef =
            firestore
                .collection("products")
                .document(item.productId)

        val cartRef =
            firestore
                .collection("users")
                .document(currentFarmerId)
                .collection("cart")
                .document(item.productId)

        firestore
            .runTransaction { transaction ->

                val productSnapshot =
                    transaction.get(productRef)

                val cartSnapshot =
                    transaction.get(cartRef)

                val currentQuantity =
                    cartSnapshot.getLong(
                        "quantity"
                    ) ?: 0L

                if (currentQuantity <= 0L) {

                    transaction.delete(cartRef)

                    return@runTransaction
                }

                val currentStock =
                    productSnapshot.getLong(
                        "stock"
                    ) ?: 0L

                transaction.update(
                    productRef,
                    "stock",
                    currentStock + 1L
                )

                if (currentQuantity == 1L) {

                    transaction.delete(cartRef)

                } else {

                    transaction.update(
                        cartRef,
                        "quantity",
                        currentQuantity - 1L
                    )
                }

            }
            .addOnSuccessListener {

                updatingCartProductId = null

            }
            .addOnFailureListener { exception ->

                updatingCartProductId = null

                errorMessage =
                    exception.localizedMessage
                        ?: if (isHindi) {
                            "कार्ट अपडेट नहीं हो सका।"
                        } else {
                            "Unable to update cart."
                        }
            }
    }

    // =============================================================
    // INCREASE CART QUANTITY
    // =============================================================

    fun increaseCartQuantity(
        item: CartItem
    ) {

        val currentFarmerId =
            FirebaseAuth.getInstance()
                .currentUser
                ?.uid

        if (currentFarmerId == null) {

            errorMessage =
                if (isHindi) {
                    "आपको किसान के रूप में साइन इन करना होगा।"
                } else {
                    "You must be signed in as a farmer."
                }

            return
        }

        updatingCartProductId =
            item.productId

        errorMessage = null

        val productRef =
            firestore
                .collection("products")
                .document(item.productId)

        val cartRef =
            firestore
                .collection("users")
                .document(currentFarmerId)
                .collection("cart")
                .document(item.productId)

        firestore
            .runTransaction { transaction ->

                val productSnapshot =
                    transaction.get(productRef)

                val cartSnapshot =
                    transaction.get(cartRef)

                if (!productSnapshot.exists()) {

                    throw IllegalStateException(
                        "PRODUCT_NOT_FOUND"
                    )
                }

                val currentStock =
                    productSnapshot.getLong(
                        "stock"
                    ) ?: 0L

                if (currentStock <= 0L) {

                    throw IllegalStateException(
                        "NO_STOCK"
                    )
                }

                val currentQuantity =
                    cartSnapshot.getLong(
                        "quantity"
                    ) ?: 0L

                transaction.update(
                    productRef,
                    "stock",
                    currentStock - 1L
                )

                transaction.update(
                    cartRef,
                    "quantity",
                    currentQuantity + 1L
                )

            }
            .addOnSuccessListener {

                updatingCartProductId = null

            }
            .addOnFailureListener { exception ->

                updatingCartProductId = null

                errorMessage =
                    when (exception.message) {

                        "PRODUCT_NOT_FOUND" ->
                            if (isHindi) {
                                "यह उत्पाद अब उपलब्ध नहीं है।"
                            } else {
                                "This product is no longer available."
                            }

                        "NO_STOCK" ->
                            if (isHindi) {
                                "और स्टॉक उपलब्ध नहीं है।"
                            } else {
                                "No more stock is available."
                            }

                        else ->
                            exception.localizedMessage
                                ?: if (isHindi) {
                                    "मात्रा बढ़ाई नहीं जा सकी।"
                                } else {
                                    "Unable to increase quantity."
                                }
                    }
            }
    }

    // =============================================================
    // PLACE ORDER
    // =============================================================

    fun placeOrder(
        deliveryAddress: String
    ) {

        val currentFarmerId =
            FirebaseAuth.getInstance()
                .currentUser
                ?.uid

        if (currentFarmerId == null) {

            errorMessage =
                if (isHindi) {
                    "ऑर्डर देने के लिए किसान के रूप में साइन इन करें।"
                } else {
                    "You must be signed in as a farmer to place an order."
                }

            return
        }

        if (cartItems.isEmpty()) {

            errorMessage =
                if (isHindi) {
                    "आपका कार्ट खाली है।"
                } else {
                    "Your cart is empty."
                }

            return
        }

        if (deliveryAddress.isBlank()) {

            errorMessage =
                if (isHindi) {
                    "कृपया डिलीवरी का पता दर्ज करें।"
                } else {
                    "Please enter a delivery address."
                }

            return
        }

        if (isPlacingOrder) {
            return
        }

        isPlacingOrder = true
        errorMessage = null
        orderSuccessMessage = null

        firestore
            .collection("users")
            .document(currentFarmerId)
            .get()
            .addOnSuccessListener { farmerDocument ->

                val farmerName =
                    farmerDocument.getString("name")
                        ?: FirebaseAuth.getInstance()
                            .currentUser
                            ?.displayName
                        ?: "Farmer"

                val ordersBySupplier =
                    cartItems.groupBy {
                        it.supplierId
                    }

                val batch =
                    firestore.batch()

                ordersBySupplier.forEach {
                        (supplierId, items) ->

                    val orderRef =
                        firestore
                            .collection("orders")
                            .document()

                    val orderItems =
                        items.map { item ->

                            mapOf(
                                "productId" to item.productId,
                                "productName" to item.productName,
                                "price" to item.price,
                                "quantity" to item.quantity
                            )
                        }

                    val totalAmount =
                        items.sumOf {
                            it.price * it.quantity
                        }

                    val orderData =
                        mapOf(
                            "farmerId" to currentFarmerId,
                            "farmerName" to farmerName,
                            "supplierId" to supplierId,
                            "items" to orderItems,
                            "totalAmount" to totalAmount,
                            "deliveryAddress" to deliveryAddress,
                            "status" to "PLACED",
                            "paymentStatus" to "PENDING",
                            "deliveryStatus" to "PENDING",
                            "createdAt" to FieldValue.serverTimestamp()
                        )

                    batch.set(
                        orderRef,
                        orderData
                    )
                }

                cartItems.forEach { item ->

                    val cartRef =
                        firestore
                            .collection("users")
                            .document(currentFarmerId)
                            .collection("cart")
                            .document(item.productId)

                    batch.delete(cartRef)
                }

                batch.commit()
                    .addOnSuccessListener {

                        isPlacingOrder = false

                        orderSuccessMessage =
                            if (ordersBySupplier.size == 1) {

                                if (isHindi) {
                                    "ऑर्डर सफलतापूर्वक दिया गया।"
                                } else {
                                    "Order placed successfully."
                                }

                            } else {

                                if (isHindi) {
                                    "${ordersBySupplier.size} सप्लायर के साथ ऑर्डर सफलतापूर्वक दिए गए।"
                                } else {
                                    "Orders placed successfully with ${ordersBySupplier.size} suppliers."
                                }
                            }
                    }
                    .addOnFailureListener { exception ->

                        isPlacingOrder = false

                        errorMessage =
                            exception.localizedMessage
                                ?: if (isHindi) {
                                    "ऑर्डर नहीं दिया जा सका।"
                                } else {
                                    "Unable to place order."
                                }
                    }
            }
            .addOnFailureListener { exception ->

                isPlacingOrder = false

                errorMessage =
                    exception.localizedMessage
                        ?: if (isHindi) {
                            "किसान की जानकारी लोड नहीं हो सकी।"
                        } else {
                            "Unable to load farmer details."
                        }
            }
    }

    // =============================================================
    // CART SCREEN
    // =============================================================

    if (showCart) {

        CartView(
            cartItems = cartItems,
            isLoadingCart = isLoadingCart,
            updatingCartProductId =
                updatingCartProductId,
            errorMessage = errorMessage,
            orderSuccessMessage =
                orderSuccessMessage,
            farmerOrders = farmerOrders,
            isLoadingOrderHistory =
                isLoadingOrderHistory,
            orderHistoryError =
                orderHistoryError,
            isPlacingOrder =
                isPlacingOrder,
            isHindi = isHindi,
            onBack = {
                showCart = false
                errorMessage = null
                orderSuccessMessage = null
            },
            onIncrease = {
                increaseCartQuantity(it)
            },
            onDecrease = {
                decreaseCartQuantity(it)
            },
            onPlaceOrder = {
                placeOrder(it)
            }
        )

        return
    }

    // =============================================================
    // SELECTED SUPPLIER
    // =============================================================

    if (selectedSupplier != null) {

        val supplier =
            selectedSupplier!!

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(FieldCream)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 14.dp
                    ),
                verticalAlignment =
                    Alignment.CenterVertically,
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    TextButton(
                        onClick = {

                            selectedSupplier = null
                            products = emptyList()
                            errorMessage = null
                        }
                    ) {

                        Text(
                            text = "←",
                            fontSize = 26.sp
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(4.dp)
                    )

                    Text(
                        text = supplier.name,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow =
                            TextOverflow.Ellipsis
                    )
                }

                OutlinedButton(
                    onClick = {
                        showCart = true
                    }
                ) {

                    Text(
                        text =
                            if (isHindi) {
                                "🛒 कार्ट $cartQuantity"
                            } else {
                                "🛒 Cart $cartQuantity"
                            }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 20.dp
                    )
            ) {

                Text(
                    text = supplier.businessType,
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                if (supplier.location.isNotBlank()) {

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = supplier.location,
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                Text(
                    text =
                        if (isHindi) {
                            "उत्पाद"
                        } else {
                            "Products"
                        },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (errorMessage != null) {

                Text(
                    text = errorMessage!!,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }

            when {

                isLoadingProducts -> {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        CircularProgressIndicator(
                            color = LeafGreen
                        )

                        Text(
                            text =
                                if (isHindi) {
                                    "उत्पाद लोड हो रहे हैं..."
                                } else {
                                    "Loading products..."
                                },
                            modifier = Modifier.padding(
                                top = 70.dp
                            ),
                            color = Color.Gray
                        )
                    }
                }

                products.isEmpty() -> {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text =
                                if (isHindi) {
                                    "इस सप्लायर ने अभी तक कोई उत्पाद सूचीबद्ध नहीं किया है।"
                                } else {
                                    "This supplier has not listed any products yet."
                                },
                            color = Color.Gray,
                            fontSize = 15.sp
                        )
                    }
                }

                else -> {

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding =
                            PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                top = 16.dp,
                                bottom = 24.dp
                            ),
                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {

                        items(
                            items = products,
                            key = { it.id }
                        ) { product ->

                            ProductCard(
                                product = product,
                                isAdding =
                                    addingProductId ==
                                            product.id,
                                isHindi = isHindi,
                                onAdd = {
                                    addToCart(
                                        product,
                                        supplier.uid
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        return
    }

    // =============================================================
    // SUPPLIER LIST
    // =============================================================

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FieldCream)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Text(
                text = "Krushi Shop",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            OutlinedButton(
                onClick = {
                    showCart = true
                }
            ) {

                Text(
                    text =
                        if (isHindi) {
                            "🛒 कार्ट $cartQuantity"
                        } else {
                            "🛒 Cart $cartQuantity"
                        }
                )
            }
        }

        Text(
            text =
                if (isHindi) {
                    "उत्पाद देखने के लिए एक सप्लायर चुनें।"
                } else {
                    "Choose a supplier to view their products."
                },
            modifier = Modifier.padding(
                horizontal = 20.dp
            ),
            color = Color.Gray,
            fontSize = 14.sp
        )

        if (errorMessage != null) {

            Text(
                text = errorMessage!!,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                color = Color.Red,
                fontSize = 14.sp
            )
        }

        when {

            isLoadingSuppliers -> {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment =
                        Alignment.Center
                ) {

                    CircularProgressIndicator(
                        color = LeafGreen
                    )

                    Text(
                        text =
                            if (isHindi) {
                                "सप्लायर लोड हो रहे हैं..."
                            } else {
                                "Loading suppliers..."
                            },
                        modifier = Modifier.padding(
                            top = 70.dp
                        ),
                        color = Color.Gray
                    )
                }
            }

            suppliers.isEmpty() -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text =
                            if (isHindi) {
                                "कोई सप्लायर उपलब्ध नहीं है।"
                            } else {
                                "No suppliers available."
                            },
                        color = Color.Gray
                    )
                }
            }

            else -> {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding =
                        PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 20.dp,
                            bottom = 24.dp
                        ),
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    items(
                        items = suppliers,
                        key = { it.uid }
                    ) { supplier ->

                        SupplierCard(
                            supplier = supplier,
                            isHindi = isHindi,
                            onClick = {
                                selectedSupplier = supplier
                                errorMessage = null
                            }
                        )
                    }
                }
            }
        }
    }
}

// =============================================================
// SUPPLIER CARD
// =============================================================

@Composable
private fun SupplierCard(
    supplier: ShopSupplier,
    isHindi: Boolean,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        onClick = onClick
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Text(
                text = supplier.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = supplier.businessType,
                fontSize = 14.sp,
                color = Color.Gray
            )

            if (supplier.location.isNotBlank()) {

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = supplier.location,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Text(
                text =
                    if (isHindi) {
                        "उत्पाद देखें →"
                    } else {
                        "View products →"
                    },
                color = LeafGreen,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// =============================================================
// PRODUCT CARD
// =============================================================

@Composable
private fun ProductCard(
    product: ShopProduct,
    isAdding: Boolean,
    isHindi: Boolean,
    onAdd: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.Top
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = product.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = product.category,
                        fontSize = 13.sp,
                        color = LeafGreen,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text =
                        "₹${String.format(Locale.US, "%.2f", product.price)}",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (product.description.isNotBlank()) {

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text = product.description,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text =
                    if (isHindi) {
                        "उपलब्ध स्टॉक: ${product.stock}"
                    } else {
                        "Available stock: ${product.stock}"
                    },
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Button(
                onClick = onAdd,
                enabled =
                    !isAdding &&
                            product.stock > 0,
                modifier = Modifier.fillMaxWidth()
            ) {

                when {

                    isAdding -> {

                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        Text(
                            text =
                                if (isHindi) {
                                    "जोड़ा जा रहा है..."
                                } else {
                                    "Adding..."
                                }
                        )
                    }

                    product.stock <= 0 -> {

                        Text(
                            text =
                                if (isHindi) {
                                    "स्टॉक में नहीं है"
                                } else {
                                    "Out of stock"
                                }
                        )
                    }

                    else -> {

                        Text(
                            text =
                                if (isHindi) {
                                    "कार्ट में जोड़ें"
                                } else {
                                    "Add to cart"
                                }
                        )
                    }
                }
            }
        }
    }
}

// =============================================================
// CART VIEW
// =============================================================

@Composable
private fun CartView(
    cartItems: List<CartItem>,
    isLoadingCart: Boolean,
    updatingCartProductId: String?,
    errorMessage: String?,
    orderSuccessMessage: String?,
    farmerOrders: List<FarmerOrder>,
    isLoadingOrderHistory: Boolean,
    orderHistoryError: String?,
    isPlacingOrder: Boolean,
    isHindi: Boolean,
    onBack: () -> Unit,
    onIncrease: (CartItem) -> Unit,
    onDecrease: (CartItem) -> Unit,
    onPlaceOrder: (String) -> Unit
) {

    var showCheckoutDialog by remember {
        mutableStateOf(false)
    }

    var showOrderHistory by remember {
        mutableStateOf(false)
    }

    var deliveryAddress by remember {
        mutableStateOf("")
    }

    if (showOrderHistory) {

        FarmerOrderHistoryView(
            orders = farmerOrders,
            isLoading = isLoadingOrderHistory,
            errorMessage = orderHistoryError,
            isHindi = isHindi,
            onBack = {
                showOrderHistory = false
            }
        )

        return
    }

    val totalAmount =
        cartItems.sumOf {
            it.price * it.quantity
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FieldCream)
            .statusBarsPadding()
            // The app's custom bottom navigation is drawn by the parent
            // and does not automatically consume Scaffold bottom padding.
            // Reserve space for it so the checkout footer stays visible.
            .padding(bottom = 96.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 14.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                TextButton(
                    onClick = onBack
                ) {

                    Text(
                        text = "←",
                        fontSize = 26.sp
                    )
                }

                Text(
                    text =
                        if (isHindi) {
                            "कार्ट"
                        } else {
                            "Cart"
                        },
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            TextButton(
                onClick = {
                    showOrderHistory = true
                }
            ) {

                Text(
                    text =
                        if (isHindi) {
                            "इतिहास"
                        } else {
                            "History"
                        }
                )
            }
        }

        if (errorMessage != null) {

            Text(
                text = errorMessage,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 6.dp
                    ),
                color = Color.Red,
                fontSize = 14.sp
            )
        }

        if (orderSuccessMessage != null) {

            Text(
                text = orderSuccessMessage,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 6.dp
                    ),
                color = LeafGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        when {

            isLoadingCart -> {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment =
                        Alignment.Center
                ) {

                    CircularProgressIndicator(
                        color = LeafGreen
                    )

                    Text(
                        text =
                            if (isHindi) {
                                "कार्ट लोड हो रहा है..."
                            } else {
                                "Loading cart..."
                            },
                        modifier = Modifier.padding(
                            top = 70.dp
                        ),
                        color = Color.Gray
                    )
                }
            }

            cartItems.isEmpty() -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Text(
                            text =
                                if (isHindi) {
                                    "आपका कार्ट खाली है।"
                                } else {
                                    "Your cart is empty."
                                },
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text =
                                if (isHindi) {
                                    "शुरू करने के लिए किसी सप्लायर से उत्पाद जोड़ें।"
                                } else {
                                    "Add products from a supplier to get started."
                                },
                            color = Color.Gray,
                            fontSize = 14.sp
                        )

                        Spacer(
                            modifier = Modifier.height(18.dp)
                        )

                        OutlinedButton(
                            onClick = {
                                showOrderHistory = true
                            }
                        ) {

                            Text(
                                text =
                                    if (isHindi) {
                                        "ऑर्डर इतिहास देखें"
                                    } else {
                                        "View Order History"
                                    }
                            )
                        }
                    }
                }
            }

            else -> {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding =
                            PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                top = 8.dp,
                                bottom = 12.dp
                            ),
                        verticalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {

                        items(
                            items = cartItems,
                            key = { it.productId }
                        ) { item ->

                            CartItemCard(
                                item = item,
                                isUpdating =
                                    updatingCartProductId ==
                                            item.productId,
                                isHindi = isHindi,
                                onIncrease = {
                                    onIncrease(item)
                                },
                                onDecrease = {
                                    onDecrease(item)
                                }
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(16.dp)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.SpaceBetween
                        ) {

                            Text(
                                text =
                                    if (isHindi) {
                                        "कुल"
                                    } else {
                                        "Total"
                                    },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text =
                                    "₹${String.format(Locale.US, "%.2f", totalAmount)}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Button(
                            onClick = {
                                showCheckoutDialog = true
                            },
                            enabled = !isPlacingOrder,
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Text(
                                text =
                                    if (isPlacingOrder) {

                                        if (isHindi) {
                                            "ऑर्डर दिया जा रहा है..."
                                        } else {
                                            "Placing order..."
                                        }

                                    } else {

                                        if (isHindi) {
                                            "चेकआउट के लिए आगे बढ़ें"
                                        } else {
                                            "Proceed to checkout"
                                        }
                                    }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCheckoutDialog) {

        AlertDialog(
            onDismissRequest = {

                if (!isPlacingOrder) {
                    showCheckoutDialog = false
                }
            },
            title = {

                Text(
                    text =
                        if (isHindi) {
                            "चेकआउट"
                        } else {
                            "Checkout"
                        }
                )
            },
            text = {

                Column {

                    Text(
                        text =
                            if (isHindi) {
                                "वह पता दर्ज करें जहाँ आप ऑर्डर की डिलीवरी चाहते हैं।"
                            } else {
                                "Enter the address where you want the order delivered."
                            }
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    OutlinedTextField(
                        value = deliveryAddress,
                        onValueChange = {
                            deliveryAddress = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {

                            Text(
                                text =
                                    if (isHindi) {
                                        "डिलीवरी का पता"
                                    } else {
                                        "Delivery address"
                                    }
                            )
                        },
                        minLines = 3
                    )
                }
            },
            confirmButton = {

                TextButton(
                    onClick = {

                        onPlaceOrder(
                            deliveryAddress
                        )

                        showCheckoutDialog = false
                    },
                    enabled =
                        deliveryAddress.isNotBlank() &&
                                !isPlacingOrder
                ) {

                    Text(
                        text =
                            if (isHindi) {
                                "ऑर्डर दें"
                            } else {
                                "Place order"
                            }
                    )
                }
            },
            dismissButton = {

                TextButton(
                    onClick = {
                        showCheckoutDialog = false
                    },
                    enabled = !isPlacingOrder
                ) {

                    Text(
                        text =
                            if (isHindi) {
                                "रद्द करें"
                            } else {
                                "Cancel"
                            }
                    )
                }
            }
        )
    }
}

// =============================================================
// ORDER HISTORY
// =============================================================

@Composable
private fun FarmerOrderHistoryView(
    orders: List<FarmerOrder>,
    isLoading: Boolean,
    errorMessage: String?,
    isHindi: Boolean,
    onBack: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FieldCream)
            .statusBarsPadding()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 14.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            TextButton(
                onClick = onBack
            ) {

                Text(
                    text = "←",
                    fontSize = 26.sp
                )
            }

            Column {

                Text(
                    text =
                        if (isHindi) {
                            "ऑर्डर इतिहास"
                        } else {
                            "Order History"
                        },
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text =
                        if (isHindi) {
                            "अपने दिए गए और पूरे हुए ऑर्डर ट्रैक करें"
                        } else {
                            "Track your placed and completed orders"
                        },
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }

        when {

            isLoading -> {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment =
                        Alignment.Center
                ) {

                    CircularProgressIndicator(
                        color = LeafGreen
                    )

                    Text(
                        text =
                            if (isHindi) {
                                "ऑर्डर इतिहास लोड हो रहा है..."
                            } else {
                                "Loading order history..."
                            },
                        modifier = Modifier.padding(
                            top = 70.dp
                        ),
                        color = Color.Gray
                    )
                }
            }

            errorMessage != null -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Text(
                            text =
                                if (isHindi) {
                                    "ऑर्डर इतिहास लोड नहीं हो सका"
                                } else {
                                    "Unable to load order history"
                                },
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            orders.isEmpty() -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Text(
                            text =
                                if (isHindi) {
                                    "अभी कोई ऑर्डर नहीं है।"
                                } else {
                                    "No orders yet."
                                },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text =
                                if (isHindi) {
                                    "चेकआउट के बाद आपके ऑर्डर यहाँ दिखाई देंगे।"
                                } else {
                                    "Your orders will appear here after checkout."
                                },
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            else -> {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding =
                        PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 8.dp,
                            bottom = 24.dp
                        ),
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    items(
                        items = orders,
                        key = { it.orderId }
                    ) { order ->

                        FarmerOrderHistoryCard(
                            order = order,
                            isHindi = isHindi
                        )
                    }
                }
            }
        }
    }
}

// =============================================================
// ORDER HISTORY CARD
// =============================================================

@Composable
private fun FarmerOrderHistoryCard(
    order: FarmerOrder,
    isHindi: Boolean
) {

    val displayStatus =
        when (order.status.uppercase()) {

            "PLACED",
            "NEW" ->
                if (isHindi) {
                    "दिया गया"
                } else {
                    "Placed"
                }

            "PROCESSING" ->
                if (isHindi) {
                    "प्रक्रिया में"
                } else {
                    "Processing"
                }

            "DISPATCHED" ->
                if (isHindi) {
                    "भेज दिया गया"
                } else {
                    "Dispatched"
                }

            "DELIVERED" ->
                if (isHindi) {
                    "पूरा हुआ"
                } else {
                    "Completed"
                }

            else ->
                order.status
        }

    val statusMessage =
        when (order.status.uppercase()) {

            "PLACED",
            "NEW" ->
                if (isHindi) {
                    "ऑर्डर सफलतापूर्वक दिया गया"
                } else {
                    "Order placed successfully"
                }

            "PROCESSING" ->
                if (isHindi) {
                    "सप्लायर आपके ऑर्डर को तैयार कर रहा है"
                } else {
                    "Supplier is processing your order"
                }

            "DISPATCHED" ->
                if (isHindi) {
                    "आपका ऑर्डर भेज दिया गया है"
                } else {
                    "Your order has been dispatched"
                }

            "DELIVERED" ->
                if (isHindi) {
                    "ऑर्डर सफलतापूर्वक डिलीवर हुआ"
                } else {
                    "Order delivered successfully"
                }

            else ->
                if (isHindi) {
                    "ऑर्डर की स्थिति अपडेट हुई"
                } else {
                    "Order status updated"
                }
        }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Text(
                    text =
                        if (isHindi) {
                            "ऑर्डर #${order.orderId}"
                        } else {
                            "Order #${order.orderId}"
                        },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = displayStatus,
                    color = LeafGreen,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = statusMessage,
                fontSize = 14.sp,
                color = Color.DarkGray
            )

            if (order.createdAtMillis > 0L) {

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text =
                        formatOrderHistoryDate(
                            order.createdAtMillis,
                            isHindi
                        ),
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            order.items.forEach { item ->

                Text(
                    text =
                        "${item.quantity} × ${item.productName}",
                    fontSize = 14.sp
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text =
                    if (isHindi) {
                        "डिलीवरी का पता"
                    } else {
                        "Delivery address"
                    },
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = order.deliveryAddress,
                fontSize = 14.sp
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text =
                    if (isHindi) {
                        "भुगतान: ${
                            formatPaymentStatus(
                                order.paymentStatus,
                                true
                            )
                        }"
                    } else {
                        "Payment: ${
                            formatPaymentStatus(
                                order.paymentStatus,
                                false
                            )
                        }"
                    },
                fontSize = 14.sp
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Text(
                    text =
                        if (isHindi) {
                            "कुल"
                        } else {
                            "Total"
                        },
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text =
                        "₹${String.format(Locale.US, "%.2f", order.totalAmount)}",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// =============================================================
// CART ITEM CARD
// =============================================================

@Composable
private fun CartItemCard(
    item: CartItem,
    isUpdating: Boolean,
    isHindi: Boolean,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {

    val itemTotal =
        item.price * item.quantity

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.Top
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = item.productName,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text =
                            "₹${
                                String.format(
                                    Locale.US,
                                    "%.2f",
                                    item.price
                                )
                            } ${
                                if (isHindi) {
                                    "प्रति यूनिट"
                                } else {
                                    "each"
                                }
                            }",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }

                Text(
                    text =
                        "₹${
                            String.format(
                                Locale.US,
                                "%.2f",
                                itemTotal
                            )
                        }",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically,
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Text(
                    text =
                        if (isHindi) {
                            "मात्रा"
                        } else {
                            "Quantity"
                        },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    TextButton(
                        onClick = onDecrease,
                        enabled = !isUpdating
                    ) {

                        Text(
                            text = "−",
                            fontSize = 24.sp
                        )
                    }

                    Text(
                        text = item.quantity.toString(),
                        modifier = Modifier.padding(
                            horizontal = 8.dp
                        ),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    TextButton(
                        onClick = onIncrease,
                        enabled = !isUpdating
                    ) {

                        Text(
                            text = "+",
                            fontSize = 22.sp
                        )
                    }
                }
            }
        }
    }
}

// =============================================================
// HELPERS
// =============================================================

private fun formatPaymentStatus(
    status: String,
    isHindi: Boolean
): String {

    return when (status.uppercase()) {

        "PAID" ->
            if (isHindi) {
                "भुगतान प्राप्त"
            } else {
                "Paid"
            }

        "PENDING" ->
            if (isHindi) {
                "लंबित"
            } else {
                "Pending"
            }

        "FAILED" ->
            if (isHindi) {
                "असफल"
            } else {
                "Failed"
            }

        else ->
            status
    }
}

private fun formatOrderHistoryDate(
    millis: Long,
    isHindi: Boolean
): String {

    val locale =
        if (isHindi) {
            Locale.Builder()
                .setLanguage("hi")
                .setRegion("IN")
                .build()
        } else {
            Locale.ENGLISH
        }

    return SimpleDateFormat(
        "dd MMM yyyy, hh:mm a",
        locale
    ).format(
        Date(millis)
    )
}
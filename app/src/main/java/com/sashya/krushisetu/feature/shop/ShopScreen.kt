package com.sashya.krushisetu.feature.shop

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.sashya.krushisetu.ui.theme.FieldCream
import com.sashya.krushisetu.ui.theme.LeafGreen
import com.sashya.krushisetu.ui.theme.MutedText

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
        mutableStateOf(true)
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

    // =============================================================
    // LOAD ALL SUPPLIERS
    // =============================================================

    DisposableEffect(Unit) {

        val listener = firestore
            .collection("users")
            .whereEqualTo("role", "SUPPLIER")
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    isLoadingSuppliers = false
                    errorMessage =
                        error.localizedMessage
                            ?: "Unable to load suppliers."
                    return@addSnapshotListener
                }

                suppliers =
                    snapshot?.documents
                        ?.mapNotNull { document ->

                            val uid = document.id

                            val name =
                                document.getString("name")
                                    ?.trim()
                                    .orEmpty()

                            val companyName =
                                document.getString("companyName")
                                    ?.trim()
                                    .orEmpty()

                            val businessType =
                                document.getString("businessType")
                                    ?.trim()
                                    .orEmpty()

                            val location =
                                document.getString("branchLocations")
                                    ?.trim()
                                    .takeUnless {
                                        it.isNullOrBlank()
                                    }
                                    ?: document.getString("location")
                                        ?.trim()
                                        .orEmpty()

                            val displayName =
                                name
                                    .takeIf {
                                        it.isNotBlank()
                                    }
                                    ?: companyName
                                        .takeIf {
                                            it.isNotBlank()
                                        }
                                    ?: "Supplier"

                            ShopSupplier(
                                uid = uid,
                                name = displayName,
                                businessType =
                                    businessType
                                        .ifBlank {
                                            "Agriculture Supplier"
                                        },
                                location = location
                            )
                        }
                        .orEmpty()

                isLoadingSuppliers = false
                errorMessage = null
            }

        onDispose {
            listener.remove()
        }
    }

    // =============================================================
    // LOAD SELECTED SUPPLIER'S PRODUCTS
    // =============================================================

    DisposableEffect(selectedSupplier?.uid) {

        val supplierId =
            selectedSupplier?.uid

        if (supplierId == null) {

            products = emptyList()
            isLoadingProducts = false

            onDispose { }

        } else {

            isLoadingProducts = true

            val listener = firestore
                .collection("products")
                .whereEqualTo(
                    "supplierId",
                    supplierId
                )
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {
                        isLoadingProducts = false
                        errorMessage =
                            error.localizedMessage
                                ?: "Unable to load products."
                        return@addSnapshotListener
                    }

                    products =
                        snapshot?.documents
                            ?.mapNotNull { document ->

                                val name =
                                    document.getString(
                                        "productName"
                                    )
                                        ?: return@mapNotNull null

                                val description =
                                    document.getString(
                                        "description"
                                    )
                                        .orEmpty()

                                val category =
                                    document.getString(
                                        "category"
                                    )
                                        ?.ifBlank {
                                            "Other"
                                        }
                                        ?: "Other"

                                val price =
                                    (document.get("price")
                                            as? Number)
                                        ?.toDouble()
                                        ?: 0.0

                                val stock =
                                    (document.get("stock")
                                            as? Number)
                                        ?.toLong()
                                        ?: 0L

                                ShopProduct(
                                    id = document.id,
                                    name = name,
                                    description = description,
                                    category = category,
                                    price = price,
                                    stock = stock
                                )
                            }
                            .orEmpty()

                    isLoadingProducts = false
                    errorMessage = null
                }

            onDispose {
                listener.remove()
            }
        }
    }

    // =============================================================
    // LOAD FARMER CART
    // =============================================================

    DisposableEffect(farmerId) {

        if (farmerId == null) {

            cartItems = emptyList()
            isLoadingCart = false

            onDispose { }

        } else {

            isLoadingCart = true

            val listener = firestore
                .collection("users")
                .document(farmerId)
                .collection("cart")
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {
                        isLoadingCart = false
                        errorMessage =
                            error.localizedMessage
                                ?: "Unable to load cart."
                        return@addSnapshotListener
                    }

                    cartItems =
                        snapshot?.documents
                            ?.mapNotNull { document ->

                                val productId =
                                    document.getString(
                                        "productId"
                                    )
                                        ?: document.id

                                val supplierId =
                                    document.getString(
                                        "supplierId"
                                    )
                                        ?: return@mapNotNull null

                                val productName =
                                    document.getString(
                                        "productName"
                                    )
                                        ?: "Product"

                                val price =
                                    (document.get("price")
                                            as? Number)
                                        ?.toDouble()
                                        ?: 0.0

                                val quantity =
                                    (document.get("quantity")
                                            as? Number)
                                        ?.toLong()
                                        ?: 0L

                                if (quantity <= 0L) {
                                    return@mapNotNull null
                                }

                                CartItem(
                                    productId = productId,
                                    supplierId = supplierId,
                                    productName = productName,
                                    price = price,
                                    quantity = quantity
                                )
                            }
                            .orEmpty()

                    isLoadingCart = false
                }

            onDispose {
                listener.remove()
            }
        }
    }

    // =============================================================
    // CART TOTAL QUANTITY
    // =============================================================

    val cartQuantity =
        cartItems.sumOf {
            it.quantity
        }

    // =============================================================
    // ADD PRODUCT TO CART
    // =============================================================

    fun addToCart(
        product: ShopProduct,
        supplierId: String
    ) {

        if (farmerId == null) {
            errorMessage =
                "You must be signed in as a farmer to use the cart."
            return
        }

        if (product.stock <= 0L) {
            errorMessage =
                "This product is out of stock."
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
                .document(farmerId)
                .collection("cart")
                .document(product.id)

        firestore.runTransaction { transaction ->

            // IMPORTANT:
            // All reads happen before writes in the transaction.

            val productSnapshot =
                transaction.get(productRef)

            val cartSnapshot =
                transaction.get(cartRef)

            if (!productSnapshot.exists()) {
                throw IllegalStateException(
                    "This product is no longer available."
                )
            }

            val currentStock =
                (productSnapshot.get("stock")
                        as? Number)
                    ?.toLong()
                    ?: 0L

            if (currentStock <= 0L) {
                throw IllegalStateException(
                    "This product is out of stock."
                )
            }

            val existingQuantity =
                (cartSnapshot.get("quantity")
                        as? Number)
                    ?.toLong()
                    ?: 0L

            // Reduce supplier inventory by exactly one.
            transaction.update(
                productRef,
                "stock",
                currentStock - 1L
            )

            // Add one unit to farmer's cart.
            transaction.set(
                cartRef,
                mapOf(
                    "productId" to product.id,
                    "supplierId" to supplierId,
                    "productName" to product.name,
                    "price" to product.price,
                    "quantity" to existingQuantity + 1L
                ),
                SetOptions.merge()
            )

        }.addOnSuccessListener {

            addingProductId = null

        }.addOnFailureListener { exception ->

            addingProductId = null

            errorMessage =
                exception.localizedMessage
                    ?: "Unable to add product to cart."
        }
    }

    // =============================================================
    // DECREASE CART QUANTITY
    // =============================================================

    fun decreaseCartQuantity(
        item: CartItem
    ) {

        if (farmerId == null) {
            errorMessage =
                "You must be signed in as a farmer."
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
                .document(farmerId)
                .collection("cart")
                .document(item.productId)

        firestore.runTransaction { transaction ->

            val productSnapshot =
                transaction.get(productRef)

            val cartSnapshot =
                transaction.get(cartRef)

            val currentQuantity =
                (cartSnapshot.get("quantity")
                        as? Number)
                    ?.toLong()
                    ?: 0L

            if (currentQuantity <= 0L) {
                transaction.delete(cartRef)
                return@runTransaction
            }

            val currentStock =
                (productSnapshot.get("stock")
                        as? Number)
                    ?.toLong()
                    ?: 0L

            // Return one unit to supplier inventory.
            transaction.set(
                productRef,
                mapOf(
                    "stock" to currentStock + 1L
                ),
                SetOptions.merge()
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

        }.addOnSuccessListener {

            updatingCartProductId = null

        }.addOnFailureListener { exception ->

            updatingCartProductId = null

            errorMessage =
                exception.localizedMessage
                    ?: "Unable to update cart."
        }
    }

    // =============================================================
    // ADD ONE MORE FROM CART
    // =============================================================

    fun increaseCartQuantity(
        item: CartItem
    ) {

        if (farmerId == null) {
            errorMessage =
                "You must be signed in as a farmer."
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
                .document(farmerId)
                .collection("cart")
                .document(item.productId)

        firestore.runTransaction { transaction ->

            val productSnapshot =
                transaction.get(productRef)

            val cartSnapshot =
                transaction.get(cartRef)

            if (!productSnapshot.exists()) {
                throw IllegalStateException(
                    "This product is no longer available."
                )
            }

            val currentStock =
                (productSnapshot.get("stock")
                        as? Number)
                    ?.toLong()
                    ?: 0L

            if (currentStock <= 0L) {
                throw IllegalStateException(
                    "No more stock is available."
                )
            }

            val currentQuantity =
                (cartSnapshot.get("quantity")
                        as? Number)
                    ?.toLong()
                    ?: 0L

            // Take one more unit from supplier stock.
            transaction.update(
                productRef,
                "stock",
                currentStock - 1L
            )

            // Add one more unit to cart.
            transaction.update(
                cartRef,
                "quantity",
                currentQuantity + 1L
            )

        }.addOnSuccessListener {

            updatingCartProductId = null

        }.addOnFailureListener { exception ->

            updatingCartProductId = null

            errorMessage =
                exception.localizedMessage
                    ?: "Unable to increase quantity."
        }
    }

    // =============================================================
    // CART VIEW
    // =============================================================

    if (showCart) {

        CartView(
            modifier = modifier,
            cartItems = cartItems,
            isLoading = isLoadingCart,
            errorMessage = errorMessage,
            updatingProductId = updatingCartProductId,
            onBack = {
                showCart = false
                errorMessage = null
            },
            onIncrease = {
                increaseCartQuantity(it)
            },
            onDecrease = {
                decreaseCartQuantity(it)
            }
        )

        return
    }

    // =============================================================
    // SUPPLIER LIST
    // =============================================================

    if (selectedSupplier == null) {

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(FieldCream)
                .padding(
                    horizontal = 20.dp,
                    vertical = 18.dp
                )
        ) {

            // -----------------------------------------------------
            // HEADER
            // -----------------------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically,
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Text(
                    text = "Krushi Shop",
                    style =
                        MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = LeafGreen
                )

                TextButton(
                    onClick = {
                        showCart = true
                        errorMessage = null
                    }
                ) {
                    Text(
                        text =
                            "🛒 Cart" +
                                    if (cartQuantity > 0) {
                                        " $cartQuantity"
                                    } else {
                                        ""
                                    },
                        color = LeafGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text =
                    "Choose a supplier to view their products.",
                style =
                    MaterialTheme.typography.bodyLarge,
                color = MutedText
            )

            Spacer(
                modifier = Modifier.height(22.dp)
            )

            if (isLoadingSuppliers) {

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    CircularProgressIndicator(
                        color = LeafGreen
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Text(
                        text = "Loading suppliers...",
                        color = MutedText
                    )
                }

            } else if (errorMessage != null) {

                Text(
                    text = errorMessage!!,
                    color =
                        MaterialTheme.colorScheme.error
                )

            } else if (suppliers.isEmpty()) {

                Text(
                    text = "No suppliers available.",
                    color = MutedText
                )

            } else {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement =
                        Arrangement.spacedBy(14.dp)
                ) {

                    items(
                        items = suppliers,
                        key = {
                            it.uid
                        }
                    ) { supplier ->

                        SupplierCard(
                            supplier = supplier,
                            onClick = {

                                selectedSupplier =
                                    supplier

                                errorMessage = null
                            }
                        )
                    }
                }
            }
        }

    } else {

        // =========================================================
        // SELECTED SUPPLIER PRODUCTS
        // =========================================================

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(FieldCream)
                .padding(
                    horizontal = 20.dp,
                    vertical = 18.dp
                )
        ) {

            // -----------------------------------------------------
            // TOP HEADER
            // -----------------------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically,
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                TextButton(
                    onClick = {

                        selectedSupplier = null
                        products = emptyList()
                        errorMessage = null
                    }
                ) {
                    Text(
                        text = "← Back",
                        color = LeafGreen,
                        fontWeight =
                            FontWeight.SemiBold
                    )
                }

                TextButton(
                    onClick = {
                        showCart = true
                        errorMessage = null
                    }
                ) {
                    Text(
                        text =
                            "🛒 Cart" +
                                    if (cartQuantity > 0) {
                                        " $cartQuantity"
                                    } else {
                                        ""
                                    },
                        color = LeafGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = selectedSupplier!!.name,
                style =
                    MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = LeafGreen
            )

            Text(
                text = selectedSupplier!!.businessType,
                style =
                    MaterialTheme.typography.titleMedium,
                color = LeafGreen
            )

            if (selectedSupplier!!.location.isNotBlank()) {

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = selectedSupplier!!.location,
                    color = MutedText
                )
            }

            Spacer(
                modifier = Modifier.height(22.dp)
            )

            Text(
                text = "Products",
                style =
                    MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            if (isLoadingProducts) {

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    CircularProgressIndicator(
                        color = LeafGreen
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Text(
                        text = "Loading products...",
                        color = MutedText
                    )
                }

            } else if (errorMessage != null) {

                Text(
                    text = errorMessage!!,
                    color =
                        MaterialTheme.colorScheme.error
                )

            } else if (products.isEmpty()) {

                Text(
                    text =
                        "This supplier has not listed any products yet.",
                    color = MutedText
                )

            } else {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    items(
                        items = products,
                        key = {
                            it.id
                        }
                    ) { product ->

                        ProductCard(
                            product = product,
                            isAdding =
                                addingProductId ==
                                        product.id,
                            onAdd = {

                                addToCart(
                                    product = product,
                                    supplierId =
                                        selectedSupplier!!.uid
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

// ================================================================
// SUPPLIER CARD
// ================================================================

@Composable
private fun SupplierCard(
    supplier: ShopSupplier,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(
                text = supplier.name,
                style =
                    MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1D2B20)
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = supplier.businessType,
                style =
                    MaterialTheme.typography.titleMedium,
                color = LeafGreen,
                fontWeight =
                    FontWeight.SemiBold
            )

            if (supplier.location.isNotBlank()) {

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = supplier.location,
                    color = MutedText
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Text(
                text = "View products →",
                color = LeafGreen,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ================================================================
// PRODUCT CARD
// ================================================================

@Composable
private fun ProductCard(
    product: ShopProduct,
    isAdding: Boolean,
    onAdd: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
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
                        text = product.name,
                        style =
                            MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = product.category,
                        color = LeafGreen,
                        fontWeight =
                            FontWeight.SemiBold
                    )
                }

                Text(
                    text =
                        "₹${String.format(
                            "%.2f",
                            product.price
                        )}",
                    fontWeight = FontWeight.Bold
                )
            }

            if (product.description.isNotBlank()) {

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = product.description,
                    color = MutedText
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text =
                    "Available stock: ${product.stock}",
                color = MutedText
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Button(
                onClick = onAdd,
                enabled =
                    product.stock > 0L &&
                            !isAdding,
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Text(
                    text =
                        when {
                            isAdding ->
                                "Adding..."

                            product.stock <= 0L ->
                                "Out of stock"

                            else ->
                                "Add to cart"
                        }
                )
            }
        }
    }
}

// ================================================================
// CART VIEW
// ================================================================

@Composable
private fun CartView(
    modifier: Modifier,
    cartItems: List<CartItem>,
    isLoading: Boolean,
    errorMessage: String?,
    updatingProductId: String?,
    onBack: () -> Unit,
    onIncrease: (CartItem) -> Unit,
    onDecrease: (CartItem) -> Unit
) {

    val totalAmount =
        cartItems.sumOf {
            it.price * it.quantity
        }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FieldCream)
            .padding(
                horizontal = 20.dp,
                vertical = 18.dp
            )
    ) {

        // ---------------------------------------------------------
        // HEADER
        // ---------------------------------------------------------

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment =
                Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            TextButton(
                onClick = onBack
            ) {
                Text(
                    text = "← Shop",
                    color = LeafGreen,
                    fontWeight =
                        FontWeight.SemiBold
                )
            }

            Text(
                text = "Cart",
                style =
                    MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = LeafGreen
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        if (errorMessage != null) {

            Text(
                text = errorMessage,
                color =
                    MaterialTheme.colorScheme.error
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )
        }

        if (isLoading) {

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                CircularProgressIndicator(
                    color = LeafGreen
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text = "Loading cart...",
                    color = MutedText
                )
            }

        } else if (cartItems.isEmpty()) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 40.dp
                    ),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text = "🛒",
                    style =
                        MaterialTheme.typography.displaySmall
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "Your cart is empty.",
                    style =
                        MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text =
                        "Add products from a supplier to get started.",
                    color = MutedText
                )
            }

        } else {

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                items(
                    items = cartItems,
                    key = {
                        it.productId
                    }
                ) { item ->

                    CartItemCard(
                        item = item,
                        isUpdating =
                            updatingProductId ==
                                    item.productId,
                        onIncrease = {
                            onIncrease(item)
                        },
                        onDecrease = {
                            onDecrease(item)
                        }
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape =
                    RoundedCornerShape(16.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor = Color.White
                    )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text = "Total",
                        style =
                            MaterialTheme.typography.titleLarge,
                        fontWeight =
                            FontWeight.Bold
                    )

                    Text(
                        text =
                            "₹${String.format(
                                "%.2f",
                                totalAmount
                            )}",
                        style =
                            MaterialTheme.typography.titleLarge,
                        fontWeight =
                            FontWeight.Bold,
                        color = LeafGreen
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text =
                    "Checkout will be added next.",
                modifier =
                    Modifier.fillMaxWidth(),
                color = MutedText
            )
        }
    }
}

// ================================================================
// CART ITEM CARD
// ================================================================

@Composable
private fun CartItemCard(
    item: CartItem,
    isUpdating: Boolean,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
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
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text = item.productName,
                        style =
                            MaterialTheme.typography.titleMedium,
                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text =
                            "₹${String.format(
                                "%.2f",
                                item.price
                            )} each",
                        color = MutedText
                    )
                }

                Text(
                    text =
                        "₹${String.format(
                            "%.2f",
                            item.price *
                                    item.quantity
                        )}",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically,
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Text(
                    text = "Quantity",
                    fontWeight =
                        FontWeight.SemiBold
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
                            style =
                                MaterialTheme.typography
                                    .headlineSmall
                        )
                    }

                    Text(
                        text = item.quantity.toString(),
                        modifier =
                            Modifier.padding(
                                horizontal = 8.dp
                            ),
                        style =
                            MaterialTheme.typography.titleMedium,
                        fontWeight =
                            FontWeight.Bold
                    )

                    TextButton(
                        onClick = onIncrease,
                        enabled = !isUpdating
                    ) {
                        Text(
                            text = "+",
                            style =
                                MaterialTheme.typography
                                    .headlineSmall
                        )
                    }
                }
            }
        }
    }
}
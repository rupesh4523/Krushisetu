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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.sashya.krushisetu.ui.theme.FieldCream
import com.sashya.krushisetu.ui.theme.LeafGreen
import com.sashya.krushisetu.ui.theme.MutedText


private data class SupplierProduct(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val packsize: String,
    val price: Double,
    val stock: Long
)


@Composable
fun SupplierProductsScreen(
    onBack: () -> Unit
) {
    val firestore = remember {
        FirebaseFirestore.getInstance()
    }

    val supplierId = FirebaseAuth.getInstance()
        .currentUser
        ?.uid

    var searchText by remember {
        mutableStateOf("")
    }

    var selectedCategory by remember {
        mutableStateOf("All")
    }

    var products by remember {
        mutableStateOf<List<SupplierProduct>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    var showProductDialog by remember {
        mutableStateOf(false)
    }

    var editingProduct by remember {
        mutableStateOf<SupplierProduct?>(null)
    }


    // =============================================================
    // LOAD ONLY THIS SUPPLIER'S PRODUCTS
    // =============================================================

    DisposableEffect(supplierId) {

        if (supplierId == null) {

            isLoading = false
            errorMessage = "Supplier account is not signed in."

            onDispose { }

        } else {

            val listener = firestore
                .collection("products")
                .whereEqualTo("supplierId", supplierId)
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {

                        isLoading = false

                        errorMessage =
                            error.localizedMessage
                                ?: "Unable to load your products."

                        return@addSnapshotListener
                    }

                    if (snapshot == null) {

                        isLoading = false
                        products = emptyList()

                        return@addSnapshotListener
                    }

                    products = snapshot.documents.mapNotNull { document ->

                        val name =
                            document.getString("productName")
                                ?: return@mapNotNull null

                        val description =
                            document.getString("description")
                                ?: ""

                        val category =
                            document.getString("category")
                                ?: "Other"

                        // -------------------------------------------------
                        // PACK SIZE
                        // -------------------------------------------------

                        val packsize =
                            document.getString("packSize")
                                ?: ""

                        val price =
                            (document.get("price") as? Number)
                                ?.toDouble()
                                ?: 0.0

                        val stock =
                            (document.get("stock") as? Number)
                                ?.toLong()
                                ?: 0L

                        SupplierProduct(
                            id = document.id,
                            name = name,
                            description = description,
                            category = category,
                            packsize = packsize,
                            price = price,
                            stock = stock
                        )
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
    // CATEGORY FILTERS
    // =============================================================

    val categories = listOf(
        "All",
        "Seeds",
        "Fertilizers",
        "Pesticides",
        "Tonic"
    )


    // =============================================================
    // FILTER PRODUCTS
    // =============================================================

    val filteredProducts = products.filter { product ->

        val matchesSearch =
            product.name.contains(
                searchText,
                ignoreCase = true
            ) ||
                    product.description.contains(
                        searchText,
                        ignoreCase = true
                    ) ||
                    product.packsize.contains(
                        searchText,
                        ignoreCase = true
                    )

        val matchesCategory =
            selectedCategory == "All" ||
                    product.category == selectedCategory

        matchesSearch && matchesCategory
    }


    // =============================================================
    // MAIN SCREEN
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
                    text = "Products",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = LeafGreen
                )

                Text(
                    text = "Manage your agricultural products",
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
            modifier = Modifier.height(16.dp)
        )


        // =========================================================
        // SEARCH
        // =========================================================

        OutlinedTextField(
            value = searchText,
            onValueChange = {
                searchText = it
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = {
                Text("Search products")
            },
            placeholder = {
                Text("Search seeds, fertilizers...")
            },
            shape = RoundedCornerShape(12.dp)
        )


        Spacer(
            modifier = Modifier.height(14.dp)
        )


        // =========================================================
        // CATEGORY FILTERS
        // =========================================================

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            categories.take(3).forEach { category ->

                Button(
                    onClick = {
                        selectedCategory = category
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            if (selectedCategory == category) {
                                LeafGreen
                            } else {
                                Color.White
                            },
                        contentColor =
                            if (selectedCategory == category) {
                                Color.White
                            } else {
                                LeafGreen
                            }
                    )
                ) {

                    Text(
                        text = category,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }


        Spacer(
            modifier = Modifier.height(8.dp)
        )


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            categories.drop(3).forEach { category ->

                Button(
                    onClick = {
                        selectedCategory = category
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            if (selectedCategory == category) {
                                LeafGreen
                            } else {
                                Color.White
                            },
                        contentColor =
                            if (selectedCategory == category) {
                                Color.White
                            } else {
                                LeafGreen
                            }
                    )
                ) {

                    Text(
                        text = category,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )
        }


        Spacer(
            modifier = Modifier.height(18.dp)
        )


        // =========================================================
        // ADD PRODUCT
        // =========================================================

        Button(
            onClick = {
                editingProduct = null
                showProductDialog = true
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = LeafGreen
            )
        ) {

            Text(
                text = "+ Add New Product",
                fontWeight = FontWeight.Bold
            )
        }


        Spacer(
            modifier = Modifier.height(18.dp)
        )


        // =========================================================
        // LOADING
        // =========================================================

        if (isLoading) {

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                CircularProgressIndicator(
                    color = LeafGreen
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Loading products...",
                    color = MutedText
                )
            }

        } else if (errorMessage != null) {

            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge
            )

        } else if (filteredProducts.isEmpty()) {

            Text(
                text = if (products.isEmpty()) {
                    "No products added yet."
                } else {
                    "No products match your search."
                },
                color = MutedText,
                style = MaterialTheme.typography.bodyLarge
            )

        } else {

            // =====================================================
            // PRODUCT LIST
            // =====================================================

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(
                    items = filteredProducts,
                    key = { it.id }
                ) { product ->

                    SupplierProductCard(
                        product = product,

                        onEdit = {
                            editingProduct = product
                            showProductDialog = true
                        },

                        onDelete = {

                            firestore
                                .collection("products")
                                .document(product.id)
                                .delete()
                                .addOnFailureListener { exception ->

                                    errorMessage =
                                        exception.localizedMessage
                                            ?: "Unable to delete product."
                                }
                        }
                    )
                }
            }
        }
    }


    // =============================================================
    // ADD / EDIT PRODUCT DIALOG
    // =============================================================

    if (showProductDialog) {

        ProductDialog(
            existingProduct = editingProduct,

            onDismiss = {
                showProductDialog = false
                editingProduct = null
            },

            onSave = {
                    name,
                    description,
                    category,
                    packsize,
                    price,
                    stock ->

                val currentSupplierId = supplierId

                if (currentSupplierId == null) {

                    errorMessage =
                        "Supplier account is not signed in."

                    showProductDialog = false

                    return@ProductDialog
                }

                val data = hashMapOf<String, Any>(
                    "supplierId" to currentSupplierId,
                    "productName" to name,
                    "description" to description,
                    "category" to category,
                    "packSize" to packsize,
                    "price" to price,
                    "stock" to stock
                )


                if (editingProduct == null) {

                    data["createdAt"] =
                        FieldValue.serverTimestamp()

                    firestore
                        .collection("products")
                        .add(data)
                        .addOnSuccessListener {

                            errorMessage = null
                            showProductDialog = false
                        }
                        .addOnFailureListener { exception ->

                            errorMessage =
                                exception.localizedMessage
                                    ?: "Unable to add product."
                        }

                } else {

                    data["updatedAt"] =
                        FieldValue.serverTimestamp()

                    firestore
                        .collection("products")
                        .document(editingProduct!!.id)
                        .update(data)
                        .addOnSuccessListener {

                            errorMessage = null
                            showProductDialog = false
                            editingProduct = null
                        }
                        .addOnFailureListener { exception ->

                            errorMessage =
                                exception.localizedMessage
                                    ?: "Unable to update product."
                        }
                }
            }
        )
    }
}


// ================================================================
// PRODUCT CARD
// ================================================================

@Composable
private fun SupplierProductCard(
    product: SupplierProduct,
    onEdit: () -> Unit,
    onDelete: () -> Unit
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
                verticalAlignment = Alignment.Top
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = product.category,
                        style = MaterialTheme.typography.bodyMedium,
                        color = LeafGreen
                    )

                    if (product.packsize.isNotBlank()) {

                        Spacer(
                            modifier = Modifier.height(2.dp)
                        )

                        Text(
                            text = "Pack size: ${product.packsize}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedText
                        )
                    }
                }

                Text(
                    text = "₹${String.format("%.2f", product.price)}",
                    fontWeight = FontWeight.Bold
                )
            }


            if (product.description.isNotBlank()) {

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedText
                )
            }


            Spacer(
                modifier = Modifier.height(10.dp)
            )


            Text(
                text = "Available stock: ${product.stock}",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedText
            )


            Spacer(
                modifier = Modifier.height(10.dp)
            )


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {

                TextButton(
                    onClick = onEdit
                ) {

                    Text(
                        text = "Edit",
                        color = LeafGreen
                    )
                }


                TextButton(
                    onClick = onDelete
                ) {

                    Text(
                        text = "Delete",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}


// ================================================================
// PRODUCT DIALOG
// ================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDialog(
    existingProduct: SupplierProduct?,
    onDismiss: () -> Unit,
    onSave: (
        name: String,
        description: String,
        category: String,
        packsize: String,
        price: Double,
        stock: Long
    ) -> Unit
) {

    // =============================================================
    // AVAILABLE CATEGORIES
    // =============================================================

    val categories = listOf(
        "Seeds",
        "Fertilizers",
        "Pesticides",
        "Tonic"
    )


    // =============================================================
    // CATEGORY NORMALIZATION
    // =============================================================

    fun normalizeCategory(value: String): String {

        return when (value.trim().lowercase()) {

            "seed",
            "seeds" -> "Seeds"

            "fertilizer",
            "fertilizers" -> "Fertilizers"

            "pesticide",
            "pesticides" -> "Pesticides"

            "tonic",
            "tonics" -> "Tonic"

            else -> ""
        }
    }


    // =============================================================
    // FORM STATE
    // =============================================================

    var name by remember(existingProduct) {
        mutableStateOf(
            existingProduct?.name ?: ""
        )
    }


    var description by remember(existingProduct) {
        mutableStateOf(
            existingProduct?.description ?: ""
        )
    }


    var category by remember(existingProduct) {
        mutableStateOf(
            normalizeCategory(
                existingProduct?.category ?: ""
            )
        )
    }


    // =============================================================
    // PACK SIZE
    // =============================================================

    var packsize by remember(existingProduct) {
        mutableStateOf(
            existingProduct?.packsize ?: ""
        )
    }


    var price by remember(existingProduct) {
        mutableStateOf(
            existingProduct?.price?.toString() ?: ""
        )
    }


    var stock by remember(existingProduct) {
        mutableStateOf(
            existingProduct?.stock?.toString() ?: ""
        )
    }


    // =============================================================
    // DROPDOWN STATE
    // =============================================================

    var categoryExpanded by remember {
        mutableStateOf(false)
    }


    var validationMessage by remember {
        mutableStateOf<String?>(null)
    }


    // =============================================================
    // DIALOG
    // =============================================================

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {

            Text(
                text =
                    if (existingProduct == null) {
                        "Add New Product"
                    } else {
                        "Edit Product"
                    },

                fontWeight = FontWeight.Bold
            )
        },


        text = {

            Column(
                modifier = Modifier
                    .verticalScroll(
                        rememberScrollState()
                    ),

                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                // =================================================
                // PRODUCT NAME
                // =================================================

                OutlinedTextField(
                    value = name,

                    onValueChange = {
                        name = it
                        validationMessage = null
                    },

                    modifier = Modifier.fillMaxWidth(),

                    label = {
                        Text("Product name")
                    },

                    placeholder = {
                        Text("Example: Premium Wheat Seeds")
                    },

                    singleLine = true
                )


                // =================================================
                // DESCRIPTION
                // =================================================

                OutlinedTextField(
                    value = description,

                    onValueChange = {
                        description = it
                    },

                    modifier = Modifier.fillMaxWidth(),

                    label = {
                        Text("Description")
                    },

                    placeholder = {
                        Text("Describe the product")
                    }
                )


                // =================================================
                // CATEGORY DROPDOWN
                // =================================================

                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,

                    onExpandedChange = {
                        categoryExpanded = !categoryExpanded
                    },

                    modifier = Modifier.fillMaxWidth()
                ) {

                    OutlinedTextField(
                        value = category,

                        onValueChange = {},

                        readOnly = true,

                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),

                        label = {
                            Text("Category")
                        },

                        placeholder = {
                            Text("Select category")
                        },

                        singleLine = true,

                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = categoryExpanded
                            )
                        }
                    )


                    ExposedDropdownMenu(
                        expanded = categoryExpanded,

                        onDismissRequest = {
                            categoryExpanded = false
                        }
                    ) {

                        categories.forEach { item ->

                            DropdownMenuItem(
                                text = {
                                    Text(item)
                                },

                                onClick = {

                                    category = item

                                    categoryExpanded = false

                                    validationMessage = null
                                }
                            )
                        }
                    }
                }


                // =================================================
                // PACK SIZE
                // =================================================

                OutlinedTextField(
                    value = packsize,

                    onValueChange = {
                        packsize = it
                        validationMessage = null
                    },

                    modifier = Modifier.fillMaxWidth(),

                    label = {
                        Text("Pack size")
                    },

                    placeholder = {
                        Text("Example: 450 ml, 1 kg, 500 g")
                    },

                    singleLine = true
                )


                // =================================================
                // PRICE
                // =================================================

                OutlinedTextField(
                    value = price,

                    onValueChange = {
                        price = it
                        validationMessage = null
                    },

                    modifier = Modifier.fillMaxWidth(),

                    label = {
                        Text("Price (₹)")
                    },

                    placeholder = {
                        Text("450")
                    },

                    singleLine = true
                )


                // =================================================
                // STOCK
                // =================================================

                OutlinedTextField(
                    value = stock,

                    onValueChange = {
                        stock = it
                        validationMessage = null
                    },

                    modifier = Modifier.fillMaxWidth(),

                    label = {
                        Text("Available stock")
                    },

                    placeholder = {
                        Text("120")
                    },

                    singleLine = true
                )


                // =================================================
                // VALIDATION
                // =================================================

                if (validationMessage != null) {

                    Text(
                        text = validationMessage!!,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },


        // =========================================================
        // SAVE
        // =========================================================

        confirmButton = {

            TextButton(
                onClick = {

                    if (name.isBlank()) {

                        validationMessage =
                            "Please enter the product name."

                        return@TextButton
                    }


                    if (category.isBlank()) {

                        validationMessage =
                            "Please select a category."

                        return@TextButton
                    }


                    if (packsize.isBlank()) {

                        validationMessage =
                            "Please enter the pack size."

                        return@TextButton
                    }


                    val parsedPrice =
                        price.toDoubleOrNull()


                    if (
                        parsedPrice == null ||
                        parsedPrice < 0
                    ) {

                        validationMessage =
                            "Please enter a valid price."

                        return@TextButton
                    }


                    val parsedStock =
                        stock.toLongOrNull()


                    if (
                        parsedStock == null ||
                        parsedStock < 0
                    ) {

                        validationMessage =
                            "Please enter valid stock."

                        return@TextButton
                    }


                    onSave(
                        name.trim(),
                        description.trim(),
                        category.trim(),
                        packsize.trim(),
                        parsedPrice,
                        parsedStock
                    )
                }
            ) {

                Text(
                    text =
                        if (existingProduct == null) {
                            "Add Product"
                        } else {
                            "Save Changes"
                        }
                )
            }
        },


        // =========================================================
        // CANCEL
        // =========================================================

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text("Cancel")
            }
        }
    )
}
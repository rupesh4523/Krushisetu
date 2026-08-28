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
import androidx.compose.material3.OutlinedTextField
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

private data class SupplierProduct(
    val name: String,
    val category: String,
    val price: String,
    val stock: String
)

@Composable
fun SupplierProductsScreen(
    onBack: () -> Unit
) {

    var searchText by remember {
        mutableStateOf("")
    }

    var selectedCategory by remember {
        mutableStateOf("All")
    }

    val products = remember {
        listOf(
            SupplierProduct(
                name = "Premium Wheat Seeds",
                category = "Seeds",
                price = "₹450 / kg",
                stock = "120 kg"
            ),
            SupplierProduct(
                name = "Hybrid Rice Seeds",
                category = "Seeds",
                price = "₹520 / kg",
                stock = "85 kg"
            ),
            SupplierProduct(
                name = "NPK Fertilizer",
                category = "Fertilizers",
                price = "₹1,250 / bag",
                stock = "60 bags"
            ),
            SupplierProduct(
                name = "Organic Fertilizer",
                category = "Fertilizers",
                price = "₹850 / bag",
                stock = "45 bags"
            ),
            SupplierProduct(
                name = "Crop Protection Pesticide",
                category = "Pesticides",
                price = "₹680 / litre",
                stock = "35 litres"
            ),
            SupplierProduct(
                name = "Agricultural Sprayer",
                category = "Equipment",
                price = "₹2,400",
                stock = "18 units"
            )
        )
    }

    val categories = listOf(
        "All",
        "Seeds",
        "Fertilizers",
        "Pesticides",
        "Equipment"
    )

    val filteredProducts = products.filter { product ->

        val matchesSearch =
            product.name.contains(searchText, ignoreCase = true)

        val matchesCategory =
            selectedCategory == "All" ||
                    product.category == selectedCategory

        matchesSearch && matchesCategory
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FieldCream)
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {

        // ---------------------------------------------------------
        // HEADER
        // ---------------------------------------------------------

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

        // ---------------------------------------------------------
        // SEARCH
        // ---------------------------------------------------------

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

        // ---------------------------------------------------------
        // CATEGORY FILTERS
        // ---------------------------------------------------------

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

        // ---------------------------------------------------------
        // ADD PRODUCT
        // ---------------------------------------------------------

        Button(
            onClick = {
                // Add Product screen will be connected next
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

        // ---------------------------------------------------------
        // PRODUCT LIST
        // ---------------------------------------------------------

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(filteredProducts) { product ->

                SupplierProductCard(
                    product = product
                )
            }
        }
    }
}


// -------------------------------------------------------------
// PRODUCT CARD
// -------------------------------------------------------------

@Composable
private fun SupplierProductCard(
    product: SupplierProduct
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
                }

                Text(
                    text = product.price,
                    fontWeight = FontWeight.Bold
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
                    onClick = {
                        // Product details/edit will be connected next
                    }
                ) {
                    Text(
                        text = "View / Edit",
                        color = LeafGreen
                    )
                }
            }
        }
    }
}
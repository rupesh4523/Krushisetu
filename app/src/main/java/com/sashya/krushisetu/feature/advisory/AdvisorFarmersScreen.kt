package com.sashya.krushisetu.feature.advisory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sashya.krushisetu.ui.theme.FieldCream
import com.sashya.krushisetu.ui.theme.LeafGreen
import com.sashya.krushisetu.ui.theme.MutedText

private data class AdvisorFarmer(
    val name: String,
    val location: String,
    val crop: String
)

@Composable
fun AdvisorFarmersScreen() {

    var searchText by remember {
        mutableStateOf("")
    }

    val farmers = listOf(
        AdvisorFarmer(
            name = "Rajesh Patil",
            location = "Pune",
            crop = "Tomato"
        ),
        AdvisorFarmer(
            name = "Suresh More",
            location = "Nashik",
            crop = "Onion"
        ),
        AdvisorFarmer(
            name = "Meena Jadhav",
            location = "Satara",
            crop = "Sugarcane"
        ),
        AdvisorFarmer(
            name = "Vijay Shinde",
            location = "Kolhapur",
            crop = "Soybean"
        ),
        AdvisorFarmer(
            name = "Anita Pawar",
            location = "Ahmednagar",
            crop = "Wheat"
        )
    )

    val filteredFarmers = farmers.filter { farmer ->

        farmer.name.contains(
            searchText,
            ignoreCase = true
        ) ||
                farmer.location.contains(
                    searchText,
                    ignoreCase = true
                ) ||
                farmer.crop.contains(
                    searchText,
                    ignoreCase = true
                )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FieldCream)
            .padding(horizontal = 20.dp)
    ) {

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // ---------------------------------------------------------
        // HEADER
        // ---------------------------------------------------------

        Text(
            text = "Farmers",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Text(
            text = "Manage and support your registered farmers",
            style = MaterialTheme.typography.bodyMedium,
            color = MutedText,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        // ---------------------------------------------------------
        // FARMER COUNT
        // ---------------------------------------------------------

        Text(
            text = "${farmers.size} Registered Farmers",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = LeafGreen
        )

        Spacer(
            modifier = Modifier.height(12.dp)
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
            placeholder = {
                Text(
                    text = "Search farmers..."
                )
            },
            leadingIcon = {
                Text(
                    text = "⌕",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // ---------------------------------------------------------
        // FARMER LIST
        // ---------------------------------------------------------

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            item {

                Text(
                    text = "All Farmers",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Spacer(
                    modifier = Modifier.height(2.dp)
                )
            }

            items(filteredFarmers) { farmer ->

                FarmerListItem(
                    farmer = farmer
                )
            }

            if (filteredFarmers.isEmpty()) {

                item {

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(
                                text = "No farmers found",
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Try a different name, location or crop.",
                                color = MutedText,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                    }
                }
            }

            item {

                Spacer(
                    modifier = Modifier.height(8.dp)
                )
            }

            item {

                FarmerQueriesCard()
            }

            item {

                Spacer(
                    modifier = Modifier.height(12.dp)
                )
            }
        }
    }
}


// =================================================================
// FARMER LIST ITEM
// =================================================================

@Composable
private fun FarmerListItem(
    farmer: AdvisorFarmer
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // -----------------------------------------------------
            // AVATAR
            // -----------------------------------------------------

            Column(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(LeafGreen),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = farmer.name.first().toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.size(12.dp)
            )

            // -----------------------------------------------------
            // DETAILS
            // -----------------------------------------------------

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = farmer.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${farmer.location} • ${farmer.crop}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedText,
                    modifier = Modifier.padding(top = 3.dp)
                )

                Text(
                    text = "Active",
                    style = MaterialTheme.typography.bodySmall,
                    color = LeafGreen,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // -----------------------------------------------------
            // ARROW
            // -----------------------------------------------------

            TextButton(
                onClick = {
                    // Farmer detail screen will be added later.
                }
            ) {

                Text(
                    text = "›",
                    style = MaterialTheme.typography.headlineSmall,
                    color = LeafGreen
                )
            }
        }
    }
}


// =================================================================
// FARMER QUERIES
// =================================================================

@Composable
private fun FarmerQueriesCard() {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = LeafGreen
        ),
        shape = RoundedCornerShape(16.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Farmer Queries",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "3 new queries need your attention",
                    color = Color.White.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Text(
                text = "›",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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

@Composable
fun SupplierProfileScreen(
    supplierName: String,
    onBack: () -> Unit
) {

    var isEditing by remember {
        mutableStateOf(false)
    }

    var companyName by remember {
        mutableStateOf("KrushiSetu Supplier")
    }

    var businessType by remember {
        mutableStateOf("Agricultural Products")
    }

    var contactPerson by remember {
        mutableStateOf(supplierName)
    }

    var phone by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    var branchAddress by remember {
        mutableStateOf("")
    }

    var city by remember {
        mutableStateOf("")
    }

    var registrationNumber by remember {
        mutableStateOf("")
    }

    var operatingArea by remember {
        mutableStateOf("")
    }

    var yearsInBusiness by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FieldCream)
            .verticalScroll(rememberScrollState())
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

            Text(
                text = "Supplier Profile",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = LeafGreen
            )

            Button(
                onClick = onBack,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = LeafGreen
                )
            ) {
                Text("Back")
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // ---------------------------------------------------------
        // PROFILE HEADER
        // ---------------------------------------------------------

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Text(
                    text = supplierName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "Supplier Account",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedText
                )
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // ---------------------------------------------------------
        // COMPANY INFORMATION
        // ---------------------------------------------------------

        Text(
            text = "Company Information",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        SupplierProfileField(
            label = "Company Name",
            value = companyName,
            enabled = isEditing,
            onValueChange = { companyName = it }
        )

        SupplierProfileField(
            label = "Business Type",
            value = businessType,
            enabled = isEditing,
            onValueChange = { businessType = it }
        )

        SupplierProfileField(
            label = "Contact Person",
            value = contactPerson,
            enabled = isEditing,
            onValueChange = { contactPerson = it }
        )

        SupplierProfileField(
            label = "Phone",
            value = phone,
            enabled = isEditing,
            onValueChange = { phone = it }
        )

        SupplierProfileField(
            label = "Email",
            value = email,
            enabled = isEditing,
            onValueChange = { email = it }
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // ---------------------------------------------------------
        // BUSINESS LOCATION
        // ---------------------------------------------------------

        Text(
            text = "Business Locations",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {

            Column(
                modifier = Modifier.padding(18.dp)
            ) {

                Text(
                    text = "Main Branch",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                SupplierProfileField(
                    label = "Branch Address",
                    value = branchAddress,
                    enabled = isEditing,
                    onValueChange = { branchAddress = it }
                )

                SupplierProfileField(
                    label = "City / District",
                    value = city,
                    enabled = isEditing,
                    onValueChange = { city = it }
                )
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // ---------------------------------------------------------
        // BUSINESS DETAILS
        // ---------------------------------------------------------

        Text(
            text = "Business Details",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        SupplierProfileField(
            label = "Registration / GST",
            value = registrationNumber,
            enabled = isEditing,
            onValueChange = { registrationNumber = it }
        )

        SupplierProfileField(
            label = "Operating Area",
            value = operatingArea,
            enabled = isEditing,
            onValueChange = { operatingArea = it }
        )

        SupplierProfileField(
            label = "Years in Business",
            value = yearsInBusiness,
            enabled = isEditing,
            onValueChange = { yearsInBusiness = it }
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // ---------------------------------------------------------
        // EDIT / SAVE
        // ---------------------------------------------------------

        Button(
            onClick = {
                isEditing = !isEditing
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = LeafGreen
            )
        ) {

            Text(
                text = if (isEditing) {
                    "Save Changes"
                } else {
                    "Edit Profile"
                },
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )
    }
}


// -------------------------------------------------------------
// PROFILE FIELD
// -------------------------------------------------------------

@Composable
private fun SupplierProfileField(
    label: String,
    value: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit
) {

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        enabled = enabled,
        singleLine = true,
        label = {
            Text(label)
        },
        shape = RoundedCornerShape(12.dp)
    )
}
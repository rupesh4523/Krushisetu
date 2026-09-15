package com.sashya.krushisetu.feature.crops

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sashya.krushisetu.data.crop.CropRepository
import com.sashya.krushisetu.data.model.Crop
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


// =============================================================
// CROPS SCREEN
// =============================================================

@Composable
fun CropsScreen(
    modifier: Modifier = Modifier
) {

    // =========================================================
    // REPOSITORY
    // =========================================================

    val cropRepository = remember {
        CropRepository()
    }

    // =========================================================
    // SCREEN STATE
    // =========================================================

    var crops by remember {
        mutableStateOf<List<Crop>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    // Controls Add Crop dialog
    var showAddCropDialog by remember {
        mutableStateOf(false)
    }

    // Stores the crop currently being edited
    var cropBeingEdited by remember {
        mutableStateOf<Crop?>(null)
    }

    // Stores the crop waiting for delete confirmation
    var cropBeingDeleted by remember {
        mutableStateOf<Crop?>(null)
    }

    // =========================================================
    // REAL-TIME FIRESTORE LISTENER
    // =========================================================

    DisposableEffect(Unit) {

        val listener = cropRepository.getCrops { result ->

            result.onSuccess { loadedCrops ->

                crops = loadedCrops
                isLoading = false
                errorMessage = null

            }.onFailure { exception ->

                crops = emptyList()
                isLoading = false

                errorMessage =
                    exception.localizedMessage
                        ?: "Unable to load your crops."
            }
        }

        onDispose {
            listener?.remove()
        }
    }

    // =========================================================
    // SCREEN
    // =========================================================

    Box(
        modifier = modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // =================================================
            // HEADER
            // =================================================

            Column(
                modifier = Modifier.padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                )
            ) {

                Text(
                    text = "My crops 🌱",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "Track your crop stage and field details.",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                // =================================================
                // ADD CROP BUTTON
                // =================================================

                Button(
                    onClick = {

                        // Make sure we are in ADD mode,
                        // not EDIT mode.
                        cropBeingEdited = null
                        showAddCropDialog = true
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),

                    shape = RoundedCornerShape(16.dp)
                ) {

                    Text(
                        text = "+  Add a crop",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(
                    modifier = Modifier.height(16.dp)
                )
            }

            // =================================================
            // LOADING
            // =================================================

            if (isLoading) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {

                    CircularProgressIndicator()
                }
            }

            // =================================================
            // ERROR
            // =================================================

            else if (errorMessage != null) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            // =================================================
            // EMPTY STATE
            // =================================================

            else if (crops.isEmpty()) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "🌱",
                            style =
                                MaterialTheme.typography.displaySmall
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text = "No crops added yet.",
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text =
                                "Add your first crop to start tracking it."
                        )
                    }
                }
            }

            // =================================================
            // REAL CROPS
            // =================================================

            else {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),

                    verticalArrangement =
                        Arrangement.spacedBy(16.dp),

                    contentPadding =
                        PaddingValues(
                            start = 20.dp,
                            end = 20.dp,
                            bottom = 24.dp
                        )
                ) {

                    items(
                        items = crops,
                        key = { crop ->
                            crop.id
                        }
                    ) { crop ->

                        CropCard(
                            crop = crop,

                            onEdit = {

                                // Put this crop into edit mode
                                cropBeingEdited = crop

                                // Open the same dialog
                                showAddCropDialog = true
                            },

                            onDelete = {

                                // Open delete confirmation
                                cropBeingDeleted = crop
                            }
                        )
                    }
                }
            }
        }
    }

    // =========================================================
    // ADD / EDIT CROP DIALOG
    // =========================================================

    if (showAddCropDialog) {

        AddCropDialog(
            initialCrop = cropBeingEdited,

            onDismiss = {

                showAddCropDialog = false
                cropBeingEdited = null
            },

            onSave = { crop ->

                // =================================================
                // EDIT EXISTING CROP
                // =================================================

                if (cropBeingEdited != null) {

                    cropRepository.updateCrop(crop) { result ->

                        result.onSuccess {

                            showAddCropDialog = false
                            cropBeingEdited = null
                            errorMessage = null

                        }.onFailure { exception ->

                            errorMessage =
                                exception.localizedMessage
                                    ?: "Unable to update the crop."
                        }
                    }

                }

                // =================================================
                // ADD NEW CROP
                // =================================================

                else {

                    cropRepository.addCrop(crop) { result ->

                        result.onSuccess {

                            showAddCropDialog = false
                            errorMessage = null

                        }.onFailure { exception ->

                            errorMessage =
                                exception.localizedMessage
                                    ?: "Unable to save the crop."
                        }
                    }
                }
            }
        )
    }

    // =========================================================
    // DELETE CONFIRMATION DIALOG
    // =========================================================

    if (cropBeingDeleted != null) {

        val crop = cropBeingDeleted!!

        AlertDialog(

            onDismissRequest = {
                cropBeingDeleted = null
            },

            title = {

                Text(
                    text = "Delete crop?",
                    fontWeight = FontWeight.Bold
                )
            },

            text = {

                Text(
                    text =
                        "Are you sure you want to delete \"${crop.name}\"?\n\n" +
                                "This action cannot be undone."
                )
            },

            confirmButton = {

                TextButton(

                    onClick = {

                        cropRepository.deleteCrop(
                            crop.id
                        ) { result ->

                            result.onSuccess {

                                cropBeingDeleted = null
                                errorMessage = null

                            }.onFailure { exception ->

                                errorMessage =
                                    exception.localizedMessage
                                        ?: "Unable to delete the crop."

                                cropBeingDeleted = null
                            }
                        }
                    }
                ) {

                    Text("Delete")
                }
            },

            dismissButton = {

                TextButton(

                    onClick = {
                        cropBeingDeleted = null
                    }
                ) {

                    Text("Cancel")
                }
            }
        )
    }
}


// =============================================================
// CROP CARD
// =============================================================

@Composable
private fun CropCard(
    crop: Crop,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {

    // Menu state for this particular crop card
    var showMenu by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(24.dp),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            // =================================================
            // CROP NAME
            // =================================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text = crop.healthEmoji,
                    fontSize = 42.sp
                )

                Spacer(
                    modifier = Modifier.width(14.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = crop.name,

                        style =
                            MaterialTheme.typography.headlineSmall,

                        fontWeight =
                            FontWeight.Bold
                    )

                    if (crop.variety.isNotBlank()) {

                        Text(
                            text = crop.variety,

                            style =
                                MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                // =================================================
                // THREE DOT MENU
                // =================================================

                Box {

                    TextButton(
                        onClick = {
                            showMenu = true
                        }
                    ) {

                        Text(
                            text = "⋮",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,

                        onDismissRequest = {
                            showMenu = false
                        }
                    ) {

                        // -----------------------------------------
                        // EDIT
                        // -----------------------------------------

                        DropdownMenuItem(

                            text = {
                                Text("Edit crop")
                            },

                            onClick = {

                                showMenu = false
                                onEdit()
                            }
                        )

                        // -----------------------------------------
                        // DELETE
                        // -----------------------------------------

                        DropdownMenuItem(

                            text = {
                                Text("Delete crop")
                            },

                            onClick = {

                                showMenu = false
                                onDelete()
                            }
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // =================================================
            // CROP DETAILS
            // =================================================

            Row(
                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(16.dp)
            ) {

                // -------------------------------------------------
                // GROWTH STAGE
                // -------------------------------------------------

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Stage",

                        style =
                            MaterialTheme.typography.labelMedium
                    )

                    Text(
                        text = crop.stage.ifBlank {
                            "Not specified"
                        },

                        fontWeight =
                            FontWeight.Medium
                    )
                }

                // -------------------------------------------------
                // AREA
                // -------------------------------------------------

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Area",

                        style =
                            MaterialTheme.typography.labelMedium
                    )

                    Text(
                        text = crop.area.ifBlank {
                            "Not specified"
                        },

                        fontWeight =
                            FontWeight.Medium
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // =================================================
            // PLANTING DATE
            // =================================================

            Column {

                Text(
                    text = "Planting date",

                    style =
                        MaterialTheme.typography.labelMedium
                )

                Text(
                    text = crop.plantingDate.ifBlank {
                        "Not specified"
                    },

                    fontWeight =
                        FontWeight.Medium
                )
            }
        }
    }
}


// =============================================================
// ADD / EDIT CROP DIALOG
// =============================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddCropDialog(
    initialCrop: Crop? = null,
    onDismiss: () -> Unit,
    onSave: (Crop) -> Unit
) {

    // =========================================================
    // FORM STATE
    // =========================================================

    var name by remember(initialCrop) {
        mutableStateOf(
            initialCrop?.name ?: ""
        )
    }

    var variety by remember(initialCrop) {
        mutableStateOf(
            initialCrop?.variety ?: ""
        )
    }

    var stage by remember(initialCrop) {
        mutableStateOf(
            initialCrop?.stage ?: ""
        )
    }

    var area by remember(initialCrop) {
        mutableStateOf(
            initialCrop?.area ?: ""
        )
    }

    var healthEmoji by remember(initialCrop) {
        mutableStateOf(
            initialCrop?.healthEmoji ?: "🌱"
        )
    }

    var plantingDate by remember(initialCrop) {
        mutableStateOf(
            initialCrop?.plantingDate ?: ""
        )
    }

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    var validationMessage by remember {
        mutableStateOf<String?>(null)
    }

    // =========================================================
    // MAIN DIALOG
    // =========================================================

    AlertDialog(

        onDismissRequest = onDismiss,

        title = {

            Text(
                text = if (initialCrop == null) {
                    "Add a crop"
                } else {
                    "Edit crop"
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
                // CROP NAME
                // =================================================

                OutlinedTextField(

                    value = name,

                    onValueChange = {

                        name = it
                        validationMessage = null
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("Crop name")
                    },

                    placeholder = {
                        Text("Example: Tomato")
                    },

                    singleLine = true
                )

                // =================================================
                // VARIETY
                // =================================================

                OutlinedTextField(

                    value = variety,

                    onValueChange = {
                        variety = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("Variety")
                    },

                    placeholder = {
                        Text("Example: Hybrid 46")
                    },

                    singleLine = true
                )

                // =================================================
                // GROWTH STAGE
                // =================================================

                OutlinedTextField(

                    value = stage,

                    onValueChange = {

                        stage = it
                        validationMessage = null
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("Growth stage")
                    },

                    placeholder = {
                        Text("Example: Flowering stage")
                    },

                    singleLine = true
                )

                // =================================================
                // AREA
                // =================================================

                OutlinedTextField(

                    value = area,

                    onValueChange = {
                        area = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("Area")
                    },

                    placeholder = {
                        Text("Example: 1.5 acres")
                    },

                    singleLine = true
                )

                // =================================================
                // PLANTING DATE
                // =================================================

                OutlinedTextField(

                    value = plantingDate,

                    onValueChange = {
                        // Read-only field.
                        // Date comes from calendar.
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("Planting date")
                    },

                    placeholder = {
                        Text("Select planting date")
                    },

                    readOnly = true,

                    singleLine = true,

                    trailingIcon = {

                        TextButton(
                            onClick = {
                                showDatePicker = true
                            }
                        ) {

                            Text("📅")
                        }
                    }
                )

                // =================================================
                // CROP EMOJI
                // =================================================

                OutlinedTextField(

                    value = healthEmoji,

                    onValueChange = {
                        healthEmoji = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("Crop emoji")
                    },

                    placeholder = {
                        Text("🌱")
                    },

                    singleLine = true
                )

                // =================================================
                // VALIDATION MESSAGE
                // =================================================

                if (validationMessage != null) {

                    Text(
                        text = validationMessage!!,

                        color =
                            MaterialTheme
                                .colorScheme
                                .error
                    )
                }
            }
        },

        // =========================================================
        // SAVE / UPDATE BUTTON
        // =========================================================

        confirmButton = {

            TextButton(

                onClick = {

                    // ---------------------------------------------
                    // VALIDATE CROP NAME
                    // ---------------------------------------------

                    if (name.isBlank()) {

                        validationMessage =
                            "Please enter the crop name."

                        return@TextButton
                    }

                    // ---------------------------------------------
                    // VALIDATE GROWTH STAGE
                    // ---------------------------------------------

                    if (stage.isBlank()) {

                        validationMessage =
                            "Please enter the growth stage."

                        return@TextButton
                    }

                    // ---------------------------------------------
                    // VALIDATE PLANTING DATE
                    // ---------------------------------------------

                    if (plantingDate.isBlank()) {

                        validationMessage =
                            "Please select the planting date."

                        return@TextButton
                    }

                    // ---------------------------------------------
                    // CREATE CROP OBJECT
                    // ---------------------------------------------

                    val crop = Crop(

                        // IMPORTANT:
                        // Keep the existing ID while editing.
                        // For a new crop the ID remains blank,
                        // and CropRepository generates one.

                        id = initialCrop?.id ?: "",

                        name =
                            name.trim(),

                        variety =
                            variety.trim(),

                        stage =
                            stage.trim(),

                        area =
                            area.trim(),

                        plantingDate =
                            plantingDate.trim(),

                        healthEmoji =
                            healthEmoji
                                .trim()
                                .ifBlank {
                                    "🌱"
                                }
                    )

                    onSave(crop)
                }
            ) {

                Text(
                    if (initialCrop == null) {
                        "Save crop"
                    } else {
                        "Update crop"
                    }
                )
            }
        },

        // =========================================================
        // CANCEL BUTTON
        // =========================================================

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text("Cancel")
            }
        }
    )

    // =========================================================
    // DATE PICKER
    // =========================================================

    if (showDatePicker) {

        val datePickerState =
            rememberDatePickerState()

        DatePickerDialog(

            onDismissRequest = {
                showDatePicker = false
            },

            confirmButton = {

                TextButton(

                    onClick = {

                        val selectedDate =
                            datePickerState.selectedDateMillis

                        if (selectedDate != null) {

                            plantingDate =
                                formatDate(
                                    selectedDate
                                )

                            validationMessage = null
                        }

                        showDatePicker = false
                    }
                ) {

                    Text("Select")
                }
            },

            dismissButton = {

                TextButton(

                    onClick = {
                        showDatePicker = false
                    }
                ) {

                    Text("Cancel")
                }
            }
        ) {

            DatePicker(
                state = datePickerState
            )
        }
    }
}


// =============================================================
// FORMAT DATE
// =============================================================

private fun formatDate(
    millis: Long
): String {

    val formatter =
        SimpleDateFormat(
            "dd MMM yyyy",
            Locale.getDefault()
        )

    formatter.timeZone =
        TimeZone.getTimeZone("UTC")

    return formatter.format(
        Date(millis)
    )
}
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
import com.sashya.krushisetu.data.local.LanguageManager
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

        val listener =
            cropRepository.getCrops { result ->

                result
                    .onSuccess { loadedCrops ->

                        crops = loadedCrops
                        isLoading = false
                        errorMessage = null

                    }
                    .onFailure { exception ->

                        crops = emptyList()
                        isLoading = false

                        errorMessage =
                            exception.localizedMessage
                                ?: if (LanguageManager.isHindi()) {
                                    "आपकी फसलें लोड नहीं हो सकीं।"
                                } else {
                                    "Unable to load your crops."
                                }
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
                    text =
                        if (LanguageManager.isHindi()) {
                            "मेरी फसलें 🌱"
                        } else {
                            "My crops 🌱"
                        },

                    style =
                        MaterialTheme.typography.headlineMedium,

                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text =
                        if (LanguageManager.isHindi()) {
                            "अपनी फसल की अवस्था और खेत की जानकारी देखें।"
                        } else {
                            "Track your crop stage and field details."
                        },

                    style =
                        MaterialTheme.typography.bodyLarge
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
                        text =
                            if (LanguageManager.isHindi()) {
                                "+  फसल जोड़ें"
                            } else {
                                "+  Add a crop"
                            },

                        fontWeight =
                            FontWeight.Bold
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

                    contentAlignment =
                        Alignment.Center
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

                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text = errorMessage!!,
                        color =
                            MaterialTheme.colorScheme.error
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

                    contentAlignment =
                        Alignment.Center
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
                            text =
                                if (LanguageManager.isHindi()) {
                                    "अभी कोई फसल नहीं जोड़ी गई है।"
                                } else {
                                    "No crops added yet."
                                },

                            fontWeight =
                                FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text =
                                if (LanguageManager.isHindi()) {
                                    "अपनी पहली फसल जोड़कर उसकी जानकारी देखना शुरू करें।"
                                } else {
                                    "Add your first crop to start tracking it."
                                }
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
                                    ?: if (LanguageManager.isHindi()) {
                                        "फसल अपडेट नहीं हो सकी।"
                                    } else {
                                        "Unable to update the crop."
                                    }
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
                                    ?: if (LanguageManager.isHindi()) {
                                        "फसल सेव नहीं हो सकी।"
                                    } else {
                                        "Unable to save the crop."
                                    }
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
                    text =
                        if (LanguageManager.isHindi()) {
                            "फसल हटाएँ?"
                        } else {
                            "Delete crop?"
                        },

                    fontWeight =
                        FontWeight.Bold
                )
            },

            text = {

                Text(
                    text =
                        if (LanguageManager.isHindi()) {
                            "\"${crop.name}\" को हटाना चाहते हैं?\n\nयह कार्रवाई वापस नहीं की जा सकती।"
                        } else {
                            "Are you sure you want to delete \"${crop.name}\"?\n\nThis action cannot be undone."
                        }
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
                                        ?: if (LanguageManager.isHindi()) {
                                            "फसल हटाई नहीं जा सकी।"
                                        } else {
                                            "Unable to delete the crop."
                                        }

                                cropBeingDeleted = null
                            }
                        }
                    }
                ) {

                    Text(
                        if (LanguageManager.isHindi()) {
                            "हटाएँ"
                        } else {
                            "Delete"
                        }
                    )
                }
            },

            dismissButton = {

                TextButton(

                    onClick = {
                        cropBeingDeleted = null
                    }
                ) {

                    Text(
                        if (LanguageManager.isHindi()) {
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

        elevation =
            CardDefaults.cardElevation(
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

                                Text(
                                    if (LanguageManager.isHindi()) {
                                        "फसल संपादित करें"
                                    } else {
                                        "Edit crop"
                                    }
                                )
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

                                Text(
                                    if (LanguageManager.isHindi()) {
                                        "फसल हटाएँ"
                                    } else {
                                        "Delete crop"
                                    }
                                )
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
                        text =
                            if (LanguageManager.isHindi()) {
                                "अवस्था"
                            } else {
                                "Stage"
                            },

                        style =
                            MaterialTheme.typography.labelMedium
                    )

                    Text(
                        text = crop.stage.ifBlank {

                            if (LanguageManager.isHindi()) {
                                "निर्दिष्ट नहीं"
                            } else {
                                "Not specified"
                            }
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
                        text =
                            if (LanguageManager.isHindi()) {
                                "क्षेत्रफल"
                            } else {
                                "Area"
                            },

                        style =
                            MaterialTheme.typography.labelMedium
                    )

                    Text(
                        text = crop.area.ifBlank {

                            if (LanguageManager.isHindi()) {
                                "निर्दिष्ट नहीं"
                            } else {
                                "Not specified"
                            }
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
                    text =
                        if (LanguageManager.isHindi()) {
                            "बुवाई की तारीख"
                        } else {
                            "Planting date"
                        },

                    style =
                        MaterialTheme.typography.labelMedium
                )

                Text(
                    text = crop.plantingDate.ifBlank {

                        if (LanguageManager.isHindi()) {
                            "निर्दिष्ट नहीं"
                        } else {
                            "Not specified"
                        }
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
                text =
                    if (initialCrop == null) {

                        if (LanguageManager.isHindi()) {
                            "फसल जोड़ें"
                        } else {
                            "Add a crop"
                        }

                    } else {

                        if (LanguageManager.isHindi()) {
                            "फसल संपादित करें"
                        } else {
                            "Edit crop"
                        }
                    },

                fontWeight =
                    FontWeight.Bold
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

                        Text(
                            if (LanguageManager.isHindi()) {
                                "फसल का नाम"
                            } else {
                                "Crop name"
                            }
                        )
                    },

                    placeholder = {

                        Text(
                            if (LanguageManager.isHindi()) {
                                "उदाहरण: टमाटर"
                            } else {
                                "Example: Tomato"
                            }
                        )
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

                        Text(
                            if (LanguageManager.isHindi()) {
                                "किस्म"
                            } else {
                                "Variety"
                            }
                        )
                    },

                    placeholder = {

                        Text(
                            if (LanguageManager.isHindi()) {
                                "उदाहरण: हाइब्रिड 46"
                            } else {
                                "Example: Hybrid 46"
                            }
                        )
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

                        Text(
                            if (LanguageManager.isHindi()) {
                                "विकास की अवस्था"
                            } else {
                                "Growth stage"
                            }
                        )
                    },

                    placeholder = {

                        Text(
                            if (LanguageManager.isHindi()) {
                                "उदाहरण: फूल आने की अवस्था"
                            } else {
                                "Example: Flowering stage"
                            }
                        )
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

                        Text(
                            if (LanguageManager.isHindi()) {
                                "क्षेत्रफल"
                            } else {
                                "Area"
                            }
                        )
                    },

                    placeholder = {

                        Text(
                            if (LanguageManager.isHindi()) {
                                "उदाहरण: 1.5 एकड़"
                            } else {
                                "Example: 1.5 acres"
                            }
                        )
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

                        Text(
                            if (LanguageManager.isHindi()) {
                                "बुवाई की तारीख"
                            } else {
                                "Planting date"
                            }
                        )
                    },

                    placeholder = {

                        Text(
                            if (LanguageManager.isHindi()) {
                                "बुवाई की तारीख चुनें"
                            } else {
                                "Select planting date"
                            }
                        )
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

                        Text(
                            if (LanguageManager.isHindi()) {
                                "फसल का इमोजी"
                            } else {
                                "Crop emoji"
                            }
                        )
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
                            if (LanguageManager.isHindi()) {
                                "कृपया फसल का नाम दर्ज करें।"
                            } else {
                                "Please enter the crop name."
                            }

                        return@TextButton
                    }


                    // ---------------------------------------------
                    // VALIDATE GROWTH STAGE
                    // ---------------------------------------------

                    if (stage.isBlank()) {

                        validationMessage =
                            if (LanguageManager.isHindi()) {
                                "कृपया फसल की विकास अवस्था दर्ज करें।"
                            } else {
                                "Please enter the growth stage."
                            }

                        return@TextButton
                    }


                    // ---------------------------------------------
                    // VALIDATE PLANTING DATE
                    // ---------------------------------------------

                    if (plantingDate.isBlank()) {

                        validationMessage =
                            if (LanguageManager.isHindi()) {
                                "कृपया बुवाई की तारीख चुनें।"
                            } else {
                                "Please select the planting date."
                            }

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

                        id =
                            initialCrop?.id ?: "",

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

                        if (LanguageManager.isHindi()) {
                            "फसल सेव करें"
                        } else {
                            "Save crop"
                        }

                    } else {

                        if (LanguageManager.isHindi()) {
                            "फसल अपडेट करें"
                        } else {
                            "Update crop"
                        }
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

                Text(
                    if (LanguageManager.isHindi()) {
                        "रद्द करें"
                    } else {
                        "Cancel"
                    }
                )
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

                    Text(
                        if (LanguageManager.isHindi()) {
                            "चुनें"
                        } else {
                            "Select"
                        }
                    )
                }
            },

            dismissButton = {

                TextButton(

                    onClick = {
                        showDatePicker = false
                    }
                ) {

                    Text(
                        if (LanguageManager.isHindi()) {
                            "रद्द करें"
                        } else {
                            "Cancel"
                        }
                    )
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
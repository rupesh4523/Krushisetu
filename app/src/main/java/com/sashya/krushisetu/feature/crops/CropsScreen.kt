package com.sashya.krushisetu.feature.crops

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.sp
import com.sashya.krushisetu.data.local.SampleData
import com.sashya.krushisetu.data.model.Crop
import com.sashya.krushisetu.ui.components.ScreenHeader
import com.sashya.krushisetu.ui.theme.LeafGreen
import com.sashya.krushisetu.ui.theme.MutedText

@Composable
fun CropsScreen(modifier: Modifier = Modifier) {
    var crops by remember { mutableStateOf(SampleData.crops) }
    var showAddCrop by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            ScreenHeader(
                title = "My crops 🌱",
                subtitle = "Track your crop stage and field health."
            )
        }
        item {
            Button(
                onClick = { showAddCrop = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("＋ Add a crop", modifier = Modifier.padding(vertical = 4.dp), fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
        }
        items(crops) { crop ->
            CropCard(crop)
        }
    }

    if (showAddCrop) {
        AddCropDialog(
            onDismiss = { showAddCrop = false },
            onSave = { name ->
                crops = crops + Crop(
                    name = name.ifBlank { "New crop" },
                    variety = "Variety not added",
                    stage = "Sowing stage",
                    area = "Area not added",
                    healthLabel = "New",
                    healthEmoji = "🌿"
                )
                showAddCrop = false
            }
        )
    }
}

@Composable
private fun CropCard(crop: Crop) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(crop.healthEmoji, fontSize = 42.sp)
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(crop.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(crop.variety, style = MaterialTheme.typography.bodyMedium, color = MutedText)
                Row(
                    modifier = Modifier.padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CropDetail(label = crop.stage)
                    CropDetail(label = crop.area)
                }
            }
        }
        Text(
            text = "● " + crop.healthLabel,
            color = if (crop.healthLabel == "Needs attention") MaterialTheme.colorScheme.tertiary else LeafGreen,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 18.dp, bottom = 16.dp)
        )
    }
}

@Composable
private fun CropDetail(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = MutedText
    )
}

@Composable
private fun AddCropDialog(onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var cropName by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add crop") },
        text = {
            Column {
                Text("Add a crop to your prototype farm profile.")
                OutlinedTextField(
                    value = cropName,
                    onValueChange = { cropName = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    singleLine = true,
                    label = { Text("Crop name") },
                    placeholder = { Text("Example: Cotton") }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSave(cropName) }) {
                Text("Save crop")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

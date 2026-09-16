package com.sashya.krushisetu.feature.consultation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.sp
import com.sashya.krushisetu.data.local.LanguageManager
import com.sashya.krushisetu.data.local.SampleData
import com.sashya.krushisetu.data.model.Consultant
import com.sashya.krushisetu.data.model.ConsultationType
import com.sashya.krushisetu.ui.components.ScreenHeader
import com.sashya.krushisetu.ui.components.SectionTitle
import com.sashya.krushisetu.ui.theme.LightLeafGreen
import com.sashya.krushisetu.ui.theme.MutedText

@Composable
fun ConsultationScreen(
    modifier: Modifier = Modifier
) {
    val isHindi = LanguageManager.isHindi()

    var selectedTypeName by remember {
        mutableStateOf(ConsultationType.VIDEO_CALL.name)
    }

    var bookingMessage by remember {
        mutableStateOf<String?>(null)
    }

    val selectedType = ConsultationType.valueOf(selectedTypeName)

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {

        item {
            ScreenHeader(
                title = if (isHindi) {
                    "विशेषज्ञ से सलाह लें ◉"
                } else {
                    "Consult an expert ◉"
                },
                subtitle = if (isHindi) {
                    "अपने खेत के लिए विश्वसनीय सहायता प्राप्त करें।"
                } else {
                    "Get reliable support for your farm."
                }
            )
        }

        item {
            ConsultationTypeSelector(
                selectedType = selectedType,
                onTypeSelected = {
                    selectedTypeName = it.name
                    bookingMessage = null
                }
            )
        }

        if (bookingMessage != null) {
            item {
                BookingConfirmation(
                    message = bookingMessage!!,
                    modifier = Modifier.padding(
                        horizontal = 20.dp,
                        vertical = 14.dp
                    )
                )
            }
        }

        item {
            SectionTitle(
                if (selectedType == ConsultationType.VIDEO_CALL) {
                    if (isHindi) {
                        "वीडियो कॉल के लिए उपलब्ध विशेषज्ञ"
                    } else {
                        "Available for video calls"
                    }
                } else {
                    if (isHindi) {
                        "खेत पर आने के लिए उपलब्ध विशेषज्ञ"
                    } else {
                        "Available for farm visits"
                    }
                }
            )
        }

        items(SampleData.consultants) { consultant ->

            ConsultantCard(
                consultant = consultant,
                selectedType = selectedType,
                onBook = {
                    val typeLabel =
                        if (selectedType == ConsultationType.VIDEO_CALL) {
                            if (isHindi) {
                                "वीडियो परामर्श"
                            } else {
                                "video consultation"
                            }
                        } else {
                            if (isHindi) {
                                "खेत पर मुलाकात"
                            } else {
                                "farm visit"
                            }
                        }

                    bookingMessage =
                        if (isHindi) {
                            "${consultant.name} के साथ आपकी $typeLabel की अनुरोध बुकिंग जोड़ दी गई है।"
                        } else {
                            "Your $typeLabel request with ${consultant.name} has been added."
                        }
                }
            )
        }
    }
}

@Composable
private fun ConsultationTypeSelector(
    selectedType: ConsultationType,
    onTypeSelected: (ConsultationType) -> Unit
) {
    val isHindi = LanguageManager.isHindi()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        FilterChip(
            selected = selectedType == ConsultationType.VIDEO_CALL,
            onClick = {
                onTypeSelected(ConsultationType.VIDEO_CALL)
            },
            label = {
                Text(
                    if (isHindi) {
                        "📹 वीडियो कॉल"
                    } else {
                        "📹 Video call"
                    }
                )
            },
            modifier = Modifier.weight(1f)
        )

        FilterChip(
            selected = selectedType == ConsultationType.FARM_VISIT,
            onClick = {
                onTypeSelected(ConsultationType.FARM_VISIT)
            },
            label = {
                Text(
                    if (isHindi) {
                        "🚜 खेत पर मुलाकात"
                    } else {
                        "🚜 Farm visit"
                    }
                )
            },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun BookingConfirmation(
    message: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = LightLeafGreen
        )
    ) {
        Text(
            text = "✓ $message",
            modifier = Modifier.padding(14.dp),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ConsultantCard(
    consultant: Consultant,
    selectedType: ConsultationType,
    onBook: () -> Unit
) {
    val isHindi = LanguageManager.isHindi()

    val fee =
        if (selectedType == ConsultationType.VIDEO_CALL) {
            consultant.videoFee
        } else {
            consultant.visitFee
        }

    val action =
        if (selectedType == ConsultationType.VIDEO_CALL) {
            if (isHindi) {
                "वीडियो कॉल बुक करें"
            } else {
                "Book video call"
            }
        } else {
            if (isHindi) {
                "खेत पर मुलाकात का अनुरोध करें"
            } else {
                "Request farm visit"
            }
        }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp,
                vertical = 6.dp
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                consultant.avatar,
                fontSize = 42.sp
            )

            Spacer(
                Modifier.width(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    consultant.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    consultant.specialty,
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText
                )

                Text(
                    "★ ${consultant.rating}  •  ${consultant.experience}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    consultant.languages,
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedText
                )
            }
        }

        Button(
            onClick = onBook,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "$action  •  ₹$fee"
            )
        }
    }
}
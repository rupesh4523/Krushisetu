package com.sashya.krushisetu.data.model

data class Crop(
    val name: String,
    val variety: String,
    val stage: String,
    val area: String,
    val healthLabel: String,
    val healthEmoji: String
)

data class Advisory(
    val title: String,
    val message: String,
    val category: String,
    val emoji: String,
    val urgency: AdvisoryUrgency = AdvisoryUrgency.NORMAL
)

enum class AdvisoryUrgency {
    NORMAL,
    IMPORTANT
}

data class WeatherInfo(
    val temperature: String,
    val location: String,
    val condition: String,
    val rainChance: String,
    val humidity: String,
    val wind: String
)

data class Consultant(
    val name: String,
    val specialty: String,
    val experience: String,
    val rating: String,
    val languages: String,
    val videoFee: Int,
    val visitFee: Int,
    val avatar: String
)

enum class ConsultationType {
    VIDEO_CALL,
    FARM_VISIT
}

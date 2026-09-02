package com.sashya.krushisetu.data.local

import com.sashya.krushisetu.data.model.Advisory
import com.sashya.krushisetu.data.model.AdvisoryUrgency
import com.sashya.krushisetu.data.model.Consultant
import com.sashya.krushisetu.data.model.Crop
import com.sashya.krushisetu.data.model.WeatherInfo

object SampleData {
    val weather = WeatherInfo(
        temperature = "29°C",
        location = "Pune, Maharashtra",
        condition = "Partly cloudy",
        rainChance = "40%",
        humidity = "64%",
        wind = "12 km/h"
    )

    val crops = listOf(
        Crop(
            name = "Tomato",
            variety = "Hybrid 46",
            stage = "Flowering stage",
            area = "1.5 acres",
            plantingDate = "12 Aug 2026",
            healthEmoji = "🍅"
        ),
        Crop(
            name = "Wheat",
            variety = "Lokwan",
            stage = "Growing stage",
            area = "2 acres",
            plantingDate = "25 Jul 2026",
            healthEmoji = "🌾"
        )
    )

    val advisories = listOf(
        Advisory(
            title = "Irrigation reminder",
            message = "Your tomato crop may need light irrigation tomorrow morning.",
            category = "Water",
            emoji = "💧"
        ),
        Advisory(
            title = "Rain expected",
            message = "Avoid fertilizer application for the next 24 hours.",
            category = "Weather",
            emoji = "🌧️",
            urgency = AdvisoryUrgency.IMPORTANT
        ),
        Advisory(
            title = "Pest prevention",
            message = "Inspect tomato leaves for early signs of whiteflies this week.",
            category = "Crop care",
            emoji = "🐞"
        )
    )

    val consultants = listOf(
        Consultant(
            name = "Dr. Anjali Patil",
            specialty = "Crop disease specialist",
            experience = "9 years' experience",
            rating = "4.9",
            languages = "Marathi, Hindi, English",
            videoFee = 149,
            visitFee = 599,
            avatar = "👩🏽‍🌾"
        ),
        Consultant(
            name = "Mr. Ramesh Kulkarni",
            specialty = "Soil and irrigation expert",
            experience = "12 years' experience",
            rating = "4.8",
            languages = "Marathi, Hindi",
            videoFee = 129,
            visitFee = 549,
            avatar = "👨🏽‍🌾"
        )
    )
}

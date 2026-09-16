package com.sashya.krushisetu.ui.navigation

import androidx.annotation.StringRes
import com.sashya.krushisetu.R

enum class AppDestination(
    @StringRes val labelRes: Int,
    val emoji: String
) {

    HOME(
        R.string.nav_home,
        "⌂"
    ),

    CROPS(
        R.string.nav_my_crops,
        "🌱"
    ),

    ADVISORY(
        R.string.nav_advisory,
        "✦"
    ),

    PLANT_SCAN(
        R.string.nav_plant_scan,
        "📷"
    ),

    CONSULTATION(
        R.string.nav_experts,
        "◉"
    ),

    SHOP(
        R.string.nav_shop,
        "🛒"
    ),

    PROFILE(
        R.string.nav_profile,
        "☺"
    )
}
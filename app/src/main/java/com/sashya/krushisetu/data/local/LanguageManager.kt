package com.sashya.krushisetu.data.local

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object LanguageManager {

    private const val PREFS_NAME = "krushisetu_preferences"
    private const val LANGUAGE_KEY = "app_language"

    const val ENGLISH = "en"
    const val HINDI = "hi"

    var currentLanguage by mutableStateOf(ENGLISH)
        private set

    private var initialized = false

    fun initialize(context: Context) {

        if (initialized) {
            return
        }

        val preferences =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        currentLanguage =
            preferences.getString(
                LANGUAGE_KEY,
                ENGLISH
            ) ?: ENGLISH

        initialized = true
    }

    fun setLanguage(
        context: Context,
        language: String
    ) {

        if (
            language != ENGLISH &&
            language != HINDI
        ) {
            return
        }

        currentLanguage = language

        context
            .getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )
            .edit()
            .putString(
                LANGUAGE_KEY,
                language
            )
            .apply()
    }

    fun isHindi(): Boolean {
        return currentLanguage == HINDI
    }

    fun isEnglish(): Boolean {
        return currentLanguage == ENGLISH
    }
}
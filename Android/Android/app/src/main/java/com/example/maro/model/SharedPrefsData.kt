package com.example.maro.model

import android.content.Context

object SharedPrefsData {

    private const val PREF_NAME = "app_preferences"
    private const val KEY_LANGUAGE = "selected_language"
    private const val KEY_LANGUAGE_CODE = "selected_language_code"

    // save user language
    fun saveLanguage(context: Context, language: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, language).apply()
    }
    // save user language code (ex.en, ko)
    fun saveLanguageCode(context: Context, code: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE_CODE, code).apply()
    }
    // get user language code (ex.en, ko)
    fun getLanguageCode(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE_CODE, "en") ?: "en"
    }
}

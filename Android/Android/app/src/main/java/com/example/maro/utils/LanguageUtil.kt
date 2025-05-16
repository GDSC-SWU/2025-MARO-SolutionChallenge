package com.example.maro.utils

import android.content.Context
import android.content.res.Configuration
import com.example.maro.model.SharedPrefsData
import java.util.*

object LanguageUtil {
    fun getSavedLanguage(context: Context): String {
        return SharedPrefsData.getLanguageCode(context) ?: "en"
    }

    fun setLocale(context: Context, langCode: String): Context {
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}

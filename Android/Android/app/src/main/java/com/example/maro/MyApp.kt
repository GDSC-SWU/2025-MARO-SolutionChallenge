package com.example.maro

import android.app.Application
import android.content.Context
import com.example.maro.utils.LanguageUtil

class MyApp : Application() {
    override fun attachBaseContext(base: Context) {
        val lang = LanguageUtil.getSavedLanguage(base)
        val newContext = LanguageUtil.setLocale(base, lang)
        super.attachBaseContext(newContext)
    }
}

package com.example.maro.view

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.maro.R
import com.example.maro.model.SharedPrefsData
import com.example.maro.utils.LanguageUtil
import com.google.android.material.card.MaterialCardView

class OnboardingLanguageActivity : AppCompatActivity() {

    private lateinit var cardMap: Map<String, Pair<MaterialCardView, ImageView>>
    private lateinit var btnContinue: MaterialCardView

    // Set app language
    override fun attachBaseContext(newBase: Context) {
        val langCode = SharedPrefsData.getLanguageCode(newBase) ?: "en"
        val context = LanguageUtil.setLocale(newBase, langCode)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding_language)

        btnContinue = findViewById(R.id.btnContinue)
        btnContinue.isEnabled = false

        cardMap = mapOf(
            "Korean" to (findViewById<MaterialCardView>(R.id.card_korean) to findViewById<ImageView>(R.id.check_korean)),
            "English" to (findViewById<MaterialCardView>(R.id.card_english) to findViewById<ImageView>(R.id.check_english)),
            "Chinese" to (findViewById<MaterialCardView>(R.id.card_chinese) to findViewById<ImageView>(R.id.check_chinese)),
            "Japanese" to (findViewById<MaterialCardView>(R.id.card_japanese) to findViewById<ImageView>(R.id.check_japanese)),
            "Vietnamese" to (findViewById<MaterialCardView>(R.id.card_vietnamese) to findViewById<ImageView>(R.id.check_vietnamese)),
            "Thai" to (findViewById<MaterialCardView>(R.id.card_thai) to findViewById<ImageView>(R.id.check_thai))
        )

        cardMap.forEach { (language, pair) ->
            val (card, checkIcon) = pair
            card.setOnClickListener {
                handleLanguageSelection(language)
            }
        }

        // Apply selected language before moving to the next activity
        btnContinue.setOnClickListener {
            val langCode = SharedPrefsData.getLanguageCode(this) ?: "en"
            val configContext = LanguageUtil.setLocale(this, langCode)

            val intent = Intent(configContext, OnboardingChildActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    // Language code mapping
    val languageCodeMap = mapOf(
        "Korean" to "ko",
        "English" to "en",
        "Chinese" to "zh",
        "Japanese" to "ja",
        "Vietnamese" to "vi",
        "Thai" to "th"
    )

    private fun handleLanguageSelection(language: String) {
        SharedPrefsData.saveLanguage(this, language)

        val langCode = languageCodeMap[language] ?: "en"
        SharedPrefsData.saveLanguageCode(this, langCode)

        // ⬇️ Immediately apply selected language to app context
        LanguageUtil.setLocale(this, langCode)

        Log.d("LanguageCheck", "Saved language: $language / Code: $langCode")

        cardMap.forEach { (lang, pair) ->
            val (card, checkIcon) = pair
            if (lang == language) {
                card.strokeColor = ContextCompat.getColor(this, R.color.Main01)
                card.alpha = 1.0f
                checkIcon.visibility = View.VISIBLE
            } else {
                card.strokeColor = Color.TRANSPARENT
                card.alpha = 0.6f
                checkIcon.visibility = View.GONE
            }
        }

        // Enable Continue button
        btnContinue.isEnabled = true
        btnContinue.setCardBackgroundColor(ContextCompat.getColor(this, R.color.Main01))
    }
}
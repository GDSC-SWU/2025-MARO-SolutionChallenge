package com.example.maro.view

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.example.maro.R
import com.example.maro.utils.LanguageUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView

class MainActivity : AppCompatActivity() {

    lateinit var chatbotButton: MaterialCardView
    private lateinit var bottomNavigationView: BottomNavigationView

    // chatbot Btn
    var shouldHideChatbotButton = false

    // set language
    override fun attachBaseContext(newBase: Context) {
        val langCode = LanguageUtil.getSavedLanguage(newBase) // SharedPreferences 등에서 가져온 언어 코드
        val context = LanguageUtil.setLocale(newBase, langCode)
        super.attachBaseContext(context)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main)

        chatbotButton = findViewById(R.id.chatbotButton)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // get color
        val activeColor = ContextCompat.getColor(this, R.color.Main01)
        val colorStateList = ColorStateList.valueOf(activeColor)

        // set background
        bottomNavigationView.itemActiveIndicatorColor = colorStateList

        // basic fragment(MainListFragment)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MainListFragment())
                .commit()
        }

        // click navi item -> change fragment
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_list -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, MainListFragment())
                        .commit()
                    shouldHideChatbotButton = false
                    chatbotButton.visibility = View.VISIBLE // 버튼 보이게 하기
                    true
                }
                R.id.navigation_location -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, MapFragment())
                        .commit()
                    true
                }
                R.id.navigation_callScript -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, CallScriptFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }

        // chatbot Btn click event
        chatbotButton.setOnClickListener {
            val intent = Intent(this, ChatbotActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onResume() {
        super.onResume()
        if (!shouldHideChatbotButton) {
            chatbotButton.visibility = View.VISIBLE
        } else {
            chatbotButton.visibility = View.GONE
        }
    }
}

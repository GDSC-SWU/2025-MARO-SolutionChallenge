package com.example.maro.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.maro.R
import com.example.maro.adapter.CallScriptAdapter
import com.example.maro.api.CallScriptRequest
import com.example.maro.api.CallScriptResponse
import com.example.maro.api.RetrofitClient
import com.example.maro.model.CallScriptItem
import com.example.maro.utils.LanguageUtil
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch
import java.util.Locale

class CallScriptActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CallScriptAdapter
    private lateinit var btnContinue: MaterialCardView

    // set Language
    override fun attachBaseContext(newBase: Context) {
        val langCode = LanguageUtil.getSavedLanguage(newBase) // SharedPreferences 등에서 가져온 언어 코드
        val context = LanguageUtil.setLocale(newBase, langCode)
        super.attachBaseContext(context)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_script)

        recyclerView = findViewById(R.id.recyclerCallScript)
        btnContinue = findViewById(R.id.btnContinue)

        setupRecyclerView()
        setupInitialInput()
        setupContinueButton()
        setupBackButton()
    }

    // Initialize RecyclerView and bind adapter
    private fun setupRecyclerView() {
        adapter = CallScriptAdapter(this) {
            updateContinueButtonState()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        Log.d("ADAPTER_ID", "setupRecyclerView: adapter = ${System.identityHashCode(adapter)}")

    }

    // Handle "Get Script" button click
    private fun setupContinueButton() {
        btnContinue.setOnClickListener {
            Log.d("ADAPTER_STATUS", "adapter init? = ${::adapter.isInitialized}")

            // 1. Get user input from the last input card
            val lastText = adapter.getLastInputText()
            Log.d("CallScript", "lastText='$lastText'")
            if (lastText.isBlank()) return@setOnClickListener

            // 2. Get language code from preferences or default
            val langCode = getLangCode()

            // 3. Send request to server and handle response
            callScriptFromServer(lastText, langCode)
        }
    }

    // Add the very first input field at launch
    private fun setupInitialInput() {
        adapter.updateItems(listOf(CallScriptItem.Input()))
    }

    // Enable or disable button based on input content
    private fun updateContinueButtonState() {
        val lastText = adapter.getLastInputText()
        val isActive = lastText.isNotBlank()
        btnContinue.isEnabled = isActive
        btnContinue.setCardBackgroundColor(
            ContextCompat.getColor(this, if (isActive) R.color.Main01 else R.color.Neutral80)
        )
    }

    // Back button → finish activity
    private fun setupBackButton() {
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }

    // Get system language to tag answers
    private fun getLangLabel(): String {
        val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return prefs.getString("language", null) ?: when (Locale.getDefault().language) {
            "ko" -> "한국어"
            "en" -> "English"
            "ja" -> "日本語"
            "zh" -> "中文"
            "vi" -> "Tiếng Việt"
            "th" -> "ภาษาไทย"
            else -> "Unknown"
        }
    }
    // Get system language to server
    private fun getLangCode(): String {
        val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return prefs.getString("language_code", "en") ?: "en"
    }
    // Call Server
    private fun callScriptFromServer(questionText: String, languageCode: String) {
        Log.d("CallScript", "Calling server with: $questionText, $languageCode")
        // Use coroutine to make async API call
        lifecycleScope.launch {
            try {
                val request = CallScriptRequest(
                    question = questionText,
                    language = languageCode
                )

                // Send POST request to server
                val response = RetrofitClient.callScriptService.getCallScript(request)

                Log.d("CALL_SCRIPT", "Server response received, calling handleScriptResponse")
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        handleScriptResponse(data)
                    } else {
                        Log.e("CallScript", "Empty response from server")
                    }
                } else {
                    Log.e("CallScript", "Server error: ${response.code()}")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("CallScript", "Request failed: ${e.message}")
            }
        }
    }

    // Set response data -> UI
    // Inside CallScriptActivity.kt

    // Set response data -> UI
    fun handleScriptResponse(apiResponse: CallScriptResponse) {
        val currentItems = adapter.getItems().toMutableList()
        Log.d("HANDLE_RESPONSE", "function entered")

        // 0. Mark previous input as non-editable
        val lastInputIndex = currentItems.indexOfLast { it is CallScriptItem.Input }
        if (lastInputIndex != -1) {
            val input = currentItems[lastInputIndex] as CallScriptItem.Input
            currentItems[lastInputIndex] = input.copy(isEditable = false)
        }

        // 1. Add question card (with English, Korean and audio)
        val userLang = getLangLabel()
        currentItems.add(
            CallScriptItem.Question(
                text = apiResponse.translatedQuestion, // Korean question text
                language = userLang,
                originalText = apiResponse.userQuestion, // English original
                audioUrl = apiResponse.questionKoAudio ?: ""
            )
        )

        val questionIndex = currentItems.size

        // 2. Add answers with keyword and audio mapping
        apiResponse.answers.forEachIndexed { index, ans ->
            currentItems.add(
                CallScriptItem.Answer(
                    textEnglish = ans.textEnglish,
                    textKorean = ans.textKorean,
                    language = userLang,
                    keywords = ans.keywordsKorean ?: emptyList(),
                    keywordAudios = ans.keywordKoreanAudios ?: emptyList(),
                    audioUrl = ans.audioKorean ?: "",
                    answerIndex = index + 1
                )
            )
        }

        // 3. Add next input card
        currentItems.add(CallScriptItem.Input())

        // 4. Update adapter
        if (::adapter.isInitialized) {
            adapter.updateItems(currentItems)
            Log.d("HANDLE_RESPONSE", "adapter updated with ${currentItems.size} items")
        } else {
            Log.e("HANDLE_RESPONSE", "adapter not initialized")
        }

        // 5. Scroll to first answer
        recyclerView.postDelayed({
            recyclerView.scrollToPosition(questionIndex + 1)
        }, 100)

        // 6. Disable continue button
        btnContinue.isEnabled = false
        btnContinue.setCardBackgroundColor(
            ContextCompat.getColor(this, R.color.Neutral80)
        )
    }

    // Activity Stop
    override fun onStop() {
        super.onStop()
        adapter.stopAudioIfPlaying()
    }

}
package com.example.maro.model

// Base sealed class for different item types in RecyclerView
sealed class CallScriptItem {

    // User input field (EditText)
    data class Input(
        val text: String = "",
        val isEditable : Boolean = true
    ) : CallScriptItem()

    // Question card shown after input is submitted
    data class Question(
        val language: String = "English",
        val text: String,
        val originalText: String,
        val audioUrl: String
    ) : CallScriptItem()

    // Answer card with language and translation info
    data class Answer(
        val textEnglish: String,
        val textKorean: String,
        val language: String = "English",
        val answerIndex : Int,
        val audioUrl : String,
        val keywords: List<String>,
        val keywordAudios: List<String>,
    ) : CallScriptItem()
}

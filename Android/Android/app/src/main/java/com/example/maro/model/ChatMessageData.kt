package com.example.maro.model

import android.net.Uri

data class ChatMessageData(
    //  chatbot massage data
    val text: String? = null,
    val imageUri: Uri? = null,
    val isUser: Boolean = false,
    val isIntro: Boolean = false
)
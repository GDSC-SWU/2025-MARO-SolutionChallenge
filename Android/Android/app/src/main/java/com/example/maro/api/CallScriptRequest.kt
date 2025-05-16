package com.example.maro.api

// Data class for request body to send question and language to server
data class CallScriptRequest(
    val question: String,
    val language: String
)
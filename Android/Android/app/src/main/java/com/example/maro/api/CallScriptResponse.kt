package com.example.maro.api

import com.google.gson.annotations.SerializedName


data class CallScriptResponse(
    @SerializedName("user_question")
    val userQuestion: String,

    @SerializedName("question_ko")
    val translatedQuestion: String,

    @SerializedName("question_ko_audio")
    val questionKoAudio: String?,

    @SerializedName("answers")
    val answers: List<CallScriptAnswer>
)
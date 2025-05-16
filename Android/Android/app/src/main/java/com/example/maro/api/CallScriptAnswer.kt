package com.example.maro.api

import com.google.gson.annotations.SerializedName

//Call Script Answer Data
data class CallScriptAnswer(
    @SerializedName("answer_native")
    val textEnglish: String,

    @SerializedName("answer_native_audio")
    val audioEnglish: String?,  // nullable

    @SerializedName("answer_ko")
    val textKorean: String,

    @SerializedName("answer_ko_audio")
    val audioKorean: String?,   // nullable

    @SerializedName("keywords_ko")
    val keywordsKorean: List<String>?,

    @SerializedName("keywords_native")
    val keywordsEnglish: List<String>?,

    @SerializedName("keywords_ko_audio")
    val keywordKoreanAudios: List<String>?,

    @SerializedName("keywords_native_audio")
    val keywordEnglishAudios: List<String>?
)
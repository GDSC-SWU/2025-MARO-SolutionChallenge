package com.example.maro.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ChatbotAPIService {

    // image + text + language
    @Multipart
    @POST("api/chat/image")
    fun sendImageAndText(
        @Part image: MultipartBody.Part,
        @Part("text") text: RequestBody,
        @Part("language") language: RequestBody
    ): Call<ServerChabotResponse>

    // text + language
    @Multipart
    @POST("api/chat/free")
    fun sendTextOnly(
        @Part("question") question: RequestBody,
        @Part("language") language: RequestBody
    ): Call<ServerChabotResponse>
}

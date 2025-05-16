package com.example.maro.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// Retrofit interface for calling the /call-script endpoint
interface CallScriptService {

    @POST("/api/callscript/help")
    suspend fun getCallScript(
        @Body request: CallScriptRequest
    ): Response<CallScriptResponse>
}
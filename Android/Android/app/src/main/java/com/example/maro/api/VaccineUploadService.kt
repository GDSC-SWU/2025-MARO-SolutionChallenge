package com.example.maro.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

// Certificate upload
interface VaccineUploadService {

    @Multipart
    @POST("api/vaccine/verify")
    fun uploadCertificate(
        @Part("vaccine_ko") vaccineKo: RequestBody,
        @Part("vaccine_en") vaccineEn: RequestBody,
        @Part("period") period: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<Boolean>
}

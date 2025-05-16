package com.example.maro.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface HospitalAPIService {

    @POST("api/hospitals/nearby")
    fun getNearbyHospitals(@Body request: LocationRequest): Call<List<HospitalDto>>

    @POST("api/hospitals/nearby/translated")
    fun getTranslatedNearbyHospitals(
        @Body request: LocationRequest,
        @Query("targetLang") targetLang: String
    ): Call<List<HospitalDto>>

    @POST("api/hospitals/details")
    fun getHospitalDetails(@Body request: PlaceIdRequest): Call<HospitalDto>

    @POST("api/hospitals/details/translated")
    fun getTranslatedHospitalDetails(
        @Body request: PlaceIdRequest,
        @Query("targetLang") targetLang: String
    ): Call<HospitalDto>
}

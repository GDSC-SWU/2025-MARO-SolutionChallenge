package com.example.maro.api

data class HospitalDto(
    // id
    val placeId: String,
    // center name
    val name: String,
    // address
    val address: String?,
    // call
    val phone: String?,
    // lat, lng
    val lat: Double,
    val lng: Double,
    // hours
    val weekday: String?,
    // list
    val vaccines: List<String>?
)

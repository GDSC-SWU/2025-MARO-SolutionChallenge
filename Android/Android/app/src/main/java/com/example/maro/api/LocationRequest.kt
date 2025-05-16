package com.example.maro.api

// vaccination center request
data class LocationRequest(
    val lat: Double,
    val lng: Double,
    val language: String
)

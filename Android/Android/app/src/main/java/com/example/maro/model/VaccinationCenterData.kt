package com.example.maro.model

// Map page, vaccination center data
class VaccinationCenterData(
    val id: String,
    val name: String,
    val distance: String,
    val hours: String,
    val latitude: Double,
    val longitude: Double,
    val rawDistance: Float = 0f
)

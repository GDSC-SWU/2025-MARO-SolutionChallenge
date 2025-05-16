package com.example.maro.model

data class VaccineScheduleItem(
    val dateRange: String, // ex "2025 - 04 - 12 ~ 2025 - 04 - 26"
    val title: String, // ex "Rotavirus (3rd dose)"
    val status: String // ex "Coming up", "Not yet", "Completed"
)

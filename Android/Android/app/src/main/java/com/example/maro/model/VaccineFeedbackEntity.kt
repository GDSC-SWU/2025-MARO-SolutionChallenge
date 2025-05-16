package com.example.maro.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vaccine_feedback")
data class VaccineFeedbackEntity(
    @PrimaryKey val vaccineId: Int, // vaccine ID (VaccineEntity 1:1)
    val emotion: String?, // "Good", "Okay", "Bad" or null
    val memo: String? // user memo
)

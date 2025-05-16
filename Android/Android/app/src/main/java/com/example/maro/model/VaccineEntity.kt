package com.example.maro.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vaccines")
data class VaccineEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val childId: Int, // childID
    val title: String, // vaccine name
    val dateRange: String, // "yyyy-MM-dd ~ yyyy-MM-dd"
    val status: String, // Coming up, Completed, Not yet
    val memo: String? = null // memo
)

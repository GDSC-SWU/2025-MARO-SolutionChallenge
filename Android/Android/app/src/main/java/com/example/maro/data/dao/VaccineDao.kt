package com.example.maro.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.maro.model.VaccineEntity

@Dao
interface VaccineDao {

    // add all list
    @Insert
    suspend fun insertVaccines(vaccines: List<VaccineEntity>)

    // select all vaccine
    @Query("SELECT * FROM vaccines")
    suspend fun getAllVaccines(): List<VaccineEntity>

    // get vaccination list for a child
    @Query("SELECT * FROM vaccines WHERE childId = :childId")
    suspend fun getVaccinesByChildId(childId: Int): List<VaccineEntity>

    // status update
    @Update
    suspend fun updateVaccine(vaccine: VaccineEntity)
}

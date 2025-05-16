package com.example.maro.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.maro.model.ChildEntity

@Dao
interface ChildDao {

    // add child → return ID
    @Insert
    suspend fun insertChild(child: ChildEntity): Long

    // get child list
    @Query("SELECT * FROM children")
    suspend fun getAllChildren(): List<ChildEntity>

    //  child ID search
    @Query("SELECT * FROM children WHERE id = :id")
    suspend fun getChildById(id: Int): ChildEntity?
}

package com.example.maro.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.maro.model.VaccineFeedbackEntity

@Dao
interface VaccineFeedbackDao {

    // save new feedback / update feedback
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(feedback: VaccineFeedbackEntity)

    // select vaccine feedback
    @Query("SELECT * FROM vaccine_feedback")
    suspend fun getAllFeedbacks(): List<VaccineFeedbackEntity>
}

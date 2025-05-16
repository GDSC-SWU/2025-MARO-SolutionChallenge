package com.example.maro.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.maro.data.dao.ChildDao
import com.example.maro.data.dao.VaccineDao
import com.example.maro.data.dao.VaccineFeedbackDao
import com.example.maro.model.ChildEntity
import com.example.maro.model.VaccineEntity
import com.example.maro.model.VaccineFeedbackEntity

@Database(
    entities = [ChildEntity::class, VaccineEntity::class, VaccineFeedbackEntity::class],
    version = 1,
    exportSchema = false
) //Room DB
abstract class AppDatabase : RoomDatabase() {
    abstract fun childDao(): ChildDao
    abstract fun vaccineDao(): VaccineDao
    abstract fun vaccineFeedbackDao(): VaccineFeedbackDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vaccine_db"
                ).build()
                android.util.Log.d("AppDatabase", "Room instance created")
                INSTANCE = instance
                instance
            }
        }
    }
}

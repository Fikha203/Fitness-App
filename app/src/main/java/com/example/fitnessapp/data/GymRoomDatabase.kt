package com.example.fitnessapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.fitnessapp.data.dao.ExerciseDao
import com.example.fitnessapp.data.dao.HistoryDao
import com.example.fitnessapp.data.dao.PlanDao
import com.example.fitnessapp.data.model.Exercise
import com.example.fitnessapp.data.model.History
import com.example.fitnessapp.data.model.Plan
import com.example.fitnessapp.util.Converters

@Database(
    entities = [Plan::class, Exercise::class,  History::class], version = 6, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class GymRoomDatabase : RoomDatabase() {

    abstract fun planDao(): PlanDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: GymRoomDatabase? = null

        fun getDatabase(context: Context): GymRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, GymRoomDatabase::class.java, "gym_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
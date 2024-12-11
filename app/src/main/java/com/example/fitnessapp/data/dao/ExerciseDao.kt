package com.example.fitnessapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.fitnessapp.data.model.Exercise

@Dao
interface ExerciseDao {
    @Insert
    suspend fun insertExercise(exercise: Exercise): Long

    @Update
    suspend fun updateExercise(exercise: Exercise)

    @Delete
    suspend fun deleteExercise(exercise: Exercise)



    @Query("SELECT * FROM Exercise WHERE id == :id")
    suspend fun getExercise(id: Int): Exercise?

    @Query("SELECT * FROM Exercise")
    suspend fun getExercises(): List<Exercise>
}
package com.example.fitnessapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.fitnessapp.data.model.Plan


@Dao
interface PlanDao {
    @Insert
    suspend fun insertPlan(plan: Plan): Long

    @Update
    suspend fun updatePlan(plan: Plan)

    @Delete
    suspend fun deletePlan(plan: Plan)

    @Query("SELECT * FROM `Plan`")
    suspend fun getAllPlans(): List<Plan>

    @Query("SELECT * FROM `Plan` WHERE id = :id")
    suspend fun getPlan(id: Int): Plan
}

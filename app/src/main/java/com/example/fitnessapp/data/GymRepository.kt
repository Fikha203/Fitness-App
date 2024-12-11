package com.example.fitnessapp.data

import com.example.fitnessapp.data.model.Exercise
import com.example.fitnessapp.data.model.History
import com.example.fitnessapp.data.model.Plan

class GymRepository(private val database: GymRoomDatabase) {

    private val planDao = database.planDao()
    private val exerciseDao = database.exerciseDao()
    private val historyDao = database.historyDao()

    // Plan
    suspend fun getAllPlans(): List<Plan> = planDao.getAllPlans()
    suspend fun getPlan(id: Int): Plan = planDao.getPlan(id)
    suspend fun savePlan(plan: Plan) {
        if (plan.id == 0) {
            planDao.insertPlan(plan)
        } else {
            planDao.updatePlan(plan)
        }
    }

    suspend fun deletePlan(plan: Plan) = planDao.deletePlan(plan)

    // Exercise
    suspend fun insertExercise(exercise: Exercise) = exerciseDao.insertExercise(exercise)
    suspend fun updateExercise(exercise: Exercise) = exerciseDao.updateExercise(exercise)
    suspend fun getExercises() = exerciseDao.getExercises()
    suspend fun getExercise(id: Int) = exerciseDao.getExercise(id)
    suspend fun deleteExercise(exercise: Exercise) = exerciseDao.deleteExercise(exercise)


    // History
    suspend fun insertHistory(history: History) = historyDao.insertHistory(history)
    suspend fun getAllHistory() = historyDao.getAllHistory()
    suspend fun deleteHistory(history: History) = historyDao.deleteHistory(history)
    suspend fun getHistory(id: Int) = historyDao.getHistory(id)


    companion object {
        @Volatile
        private var instance: GymRepository? = null

        fun getInstance(database: GymRoomDatabase): GymRepository =
            instance ?: synchronized(this) {
                GymRepository(database).apply {
                    instance = this
                }
            }
    }
}

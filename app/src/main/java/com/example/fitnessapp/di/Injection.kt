package com.example.fitnessapp.di

import android.content.Context
import com.example.fitnessapp.data.GymRepository
import com.example.fitnessapp.data.GymRoomDatabase

object Injection {
    fun provideRepository(context: Context): GymRepository {
        val database = GymRoomDatabase.getDatabase(context)

        val repository = GymRepository.getInstance(database)
        return repository
    }
}
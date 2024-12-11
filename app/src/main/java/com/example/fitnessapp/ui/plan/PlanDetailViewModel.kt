package com.example.fitnessapp.ui.plan

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fitnessapp.data.GymRepository
import com.example.fitnessapp.data.model.Exercise
import com.example.fitnessapp.data.model.Plan
import com.example.fitnessapp.di.Injection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlanDetailViewModel(private val gymRepository: GymRepository) : ViewModel() {
    private val _plan = MutableStateFlow(Plan(name = "", exerciseDetails = mutableListOf()))
    val plan: StateFlow<Plan> = _plan.asStateFlow()

    private val _exercises = MutableStateFlow<Map<Int, Exercise?>>(emptyMap())
    val exercises: StateFlow<Map<Int, Exercise?>> = _exercises.asStateFlow()

    fun getPlan(id: Int) {
        viewModelScope.launch {
            try {
                val plan = gymRepository.getPlan(id)
                _plan.value = plan
                // Panggil fungsi untuk mengambil setiap Exercise yang ada dalam plan.exerciseDetails
                getExercises(plan.exerciseDetails.map { it.exerciseId })
            } catch (e: Exception) {
                Log.e("PlanDetailViewModel", "Error fetching plan", e)
            }
        }
    }

    // Fungsi untuk mengambil Exercise berdasarkan daftar ID
    fun getExercises(exerciseIds: List<Int>) {
        viewModelScope.launch {
            val exercises = exerciseIds.associateWith { id ->
                gymRepository.getExercise(id)
            }
            _exercises.value = exercises // Simpan hasilnya di _exercises
        }
    }


    companion object {
        fun Factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlanDetailViewModel(Injection.provideRepository(context))
            }
        }
    }
}
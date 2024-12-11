package com.example.fitnessapp.ui.plan

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fitnessapp.data.GymRepository
import com.example.fitnessapp.data.model.Exercise
import com.example.fitnessapp.data.model.Plan
import com.example.fitnessapp.di.Injection
import com.example.fitnessapp.data.model.ExerciseDetails
import com.example.fitnessapp.data.model.SetDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlanEditorViewModel(private val gymRepository: GymRepository) : ViewModel() {


    private val _allExercises = MutableStateFlow<List<Exercise>>(emptyList())
    val allExercises: StateFlow<List<Exercise>> = _allExercises

    private val _currentPlan = MutableStateFlow(Plan(name = "", exerciseDetails = mutableListOf()))
    val currentPlan: StateFlow<Plan> = _currentPlan.asStateFlow()

    init {
        getAllExercises()
    }

    private fun getAllExercises() {
        viewModelScope.launch {
            val exercises = gymRepository.getExercises()
            _allExercises.value = exercises
        }
    }


    // Fungsi untuk memuat Plan jika ada ID
    fun loadPlan(planId: Int?) {
        if (planId == null) {
            // Membuat Plan baru jika ID tidak ada
            _currentPlan.value = Plan(name = "", exerciseDetails = mutableListOf())
        } else {
            viewModelScope.launch {
                val plan = gymRepository.getPlan(planId)
                _currentPlan.value = plan
            }
        }
    }

    fun getExerciseById(exerciseId: Int): Exercise? {
        return _allExercises.value.find { it.id == exerciseId }
    }

    // Fungsi untuk mengubah nama Plan
    fun updatePlanName(name: String) {
        _currentPlan.value = _currentPlan.value.copy(name = name)
    }


    // Menambahkan Exercise ID ke Plan
    fun addExercisesToPlan(newExerciseIds: List<Int>) {
        val updatedExercises = _currentPlan.value.exerciseDetails.toMutableList().apply {
            newExerciseIds.forEach { id ->
                if (none { it.exerciseId == id }) { // Hindari duplikasi latihan
                    add(ExerciseDetails(exerciseId = id))
                }
            }
        }
        _currentPlan.value = _currentPlan.value.copy(exerciseDetails = updatedExercises)
    }



    // Menambahkan Set ke Latihan
    fun addEmptySetToExercise(exerciseId: Int) {
        val updatedExercises = _currentPlan.value.exerciseDetails.map { exercise ->
            if (exercise.exerciseId == exerciseId) {
                exercise.copy(
                    sets = exercise.sets + SetDetails()
                )
            } else {
                exercise
            }
        }
        _currentPlan.value = _currentPlan.value.copy(exerciseDetails = updatedExercises.toMutableList())
    }


    // Memperbarui nilai Set (reps/weight)
    fun updateSetForExercise(exerciseId: Int, setIndex: Int, reps: Int?, weight: Int?) {
        val updatedExercises = _currentPlan.value.exerciseDetails.map { exercise ->
            if (exercise.exerciseId == exerciseId) {
                val updatedSets = exercise.sets.mapIndexed { index, set ->
                    if (index == setIndex) {
                        set.copy(
                            reps = reps ?: set.reps,
                            weight = weight ?: set.weight
                        )
                    } else {
                        set
                    }
                }
                exercise.copy(sets = updatedSets)
            } else {
                exercise
            }
        }
        _currentPlan.value = _currentPlan.value.copy(exerciseDetails = updatedExercises.toMutableList())
    }


    // Menyimpan Plan ke database
    fun saveCurrentPlan() {
        viewModelScope.launch {
            gymRepository.savePlan(_currentPlan.value)
        }
    }

    companion object {
        fun Factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlanEditorViewModel(Injection.provideRepository(context))
            }
        }
    }

}
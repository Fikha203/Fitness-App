package com.example.fitnessapp.ui.workout

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fitnessapp.data.GymRepository
import com.example.fitnessapp.data.model.Exercise
import com.example.fitnessapp.data.model.History
import com.example.fitnessapp.data.model.Plan
import com.example.fitnessapp.di.Injection
import com.example.fitnessapp.data.model.ExerciseDetails
import com.example.fitnessapp.data.model.SetDetails
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkoutViewModel(private val gymRepository: GymRepository):ViewModel() {


    private val _currentPlan = MutableStateFlow(Plan(name = "", exerciseDetails = mutableListOf()))
    val currentPlan: StateFlow<Plan> = _currentPlan.asStateFlow()

    private val _allExercises = MutableStateFlow<List<Exercise>>(emptyList())
    val allExercises: StateFlow<List<Exercise>> = _allExercises

    init {
        getAllExercises()
    }

    private fun getAllExercises() {
        viewModelScope.launch {
            val exercises = gymRepository.getExercises()
            _allExercises.value = exercises
        }
    }

    fun loadPlan(planId: Int) {
        viewModelScope.launch {
            val plan = gymRepository.getPlan(planId)
            _currentPlan.value = plan
        }
    }

    fun getExerciseById(exerciseId: Int): Exercise? {

        return _allExercises.value.find { it.id == exerciseId }
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

    fun addEmptySetToExercise(exerciseId: Int) {
        val updatedExercises = _currentPlan.value.exerciseDetails.map { exercise ->
            if (exercise.exerciseId == exerciseId) {
                exercise.copy(sets = exercise.sets + SetDetails())
            } else {
                exercise
            }
        }
        _currentPlan.value = _currentPlan.value.copy(exerciseDetails = updatedExercises.toMutableList())
    }

    fun updateSetForExercise(exerciseId: Int, setIndex: Int, reps: Int?, weight: Int?) {
        val updatedExercises = _currentPlan.value.exerciseDetails.map { exercise ->
            if (exercise.exerciseId == exerciseId) {
                val updatedSets = exercise.sets.mapIndexed { index, set ->
                    if (index == setIndex) {
                        set.copy(
                            reps = reps ?: set.reps,
                            weight = weight ?: set.weight
                        )
                    } else set
                }
                exercise.copy(sets = updatedSets)
            } else exercise
        }
        _currentPlan.value = _currentPlan.value.copy(exerciseDetails = updatedExercises.toMutableList())
    }

    fun toggleSetCompletion(exerciseId: Int, setIndex: Int) {
        val updatedExercises = _currentPlan.value.exerciseDetails.map { exercise ->
            if (exercise.exerciseId == exerciseId) {
                val updatedSets = exercise.sets.mapIndexed { index, set ->
                    if (index == setIndex) {
                        set.copy(isComplete = !set.isComplete)
                    } else set
                }
                exercise.copy(sets = updatedSets)
            } else exercise
        }
        _currentPlan.value = _currentPlan.value.copy(exerciseDetails = updatedExercises.toMutableList())
    }

    fun saveHistory() {
        viewModelScope.launch {
            val history = History(
                planId = _currentPlan.value.id,
                date = System.currentTimeMillis(),
                progress = Gson().toJson(_currentPlan.value.exerciseDetails)
            )
            gymRepository.insertHistory(history)
        }
    }


    companion object {
        fun Factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                WorkoutViewModel(Injection.provideRepository(context))
            }
        }
    }


}
package com.example.fitnessapp.ui.exercise

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fitnessapp.data.GymRepository
import com.example.fitnessapp.data.model.Exercise
import com.example.fitnessapp.di.Injection
import com.example.fitnessapp.ui.home.HomeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExerciseViewModel(private val repository: GymRepository) : ViewModel() {

    private val _exerciseList = MutableStateFlow<List<Exercise>>(emptyList())
    val exerciseList: StateFlow<List<Exercise>> get() = _exerciseList


    init {
        getExercises()
    }

    private fun getExercises() {
        viewModelScope.launch {

            _exerciseList.value = repository.getExercises()
        }
    }


    fun deleteExercise(exercise: Exercise) {
        viewModelScope.launch {
            repository.deleteExercise(exercise)
            getExercises()
        }
    }

    companion object {
        fun Factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ExerciseViewModel(Injection.provideRepository(context))
            }
        }
    }


}


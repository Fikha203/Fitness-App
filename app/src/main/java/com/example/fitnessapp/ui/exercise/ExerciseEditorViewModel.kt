package com.example.fitnessapp.ui.exercise

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fitnessapp.data.GymRepository
import com.example.fitnessapp.data.model.Exercise
import com.example.fitnessapp.di.Injection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExerciseEditorViewModel(private val gymRepository: GymRepository) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _description = MutableStateFlow<String>("")
    val description: StateFlow<String> = _description

    fun getExercise(id: Int) {
        viewModelScope.launch {
            val exercise = gymRepository.getExercise(id)
            _name.value = exercise?.name ?: ""
            _description.value = exercise?.description ?: ""
        }
    }

    fun onNameChange(newName: String) {
        _name.value = newName
    }

    fun onDescriptionChange(newDescription: String) {
        _description.value = newDescription
    }

    fun saveExercise(id: Int?, onSave: () -> Unit) {
        viewModelScope.launch {
            try {
                // Penggunaan Builder Pattern
                val exercise = Exercise.Builder()
                    .setId(id ?: 0)
                    .setName(_name.value)
                    .setDescription(_description.value)
                    .build()

                if (id == null || id == 0) {
                    gymRepository.insertExercise(exercise)
                } else {
                    gymRepository.updateExercise(exercise)
                }
                onSave()
            } catch (e: IllegalArgumentException) {
                Log.e("ExerciseEditor", e.message ?: "Error saving exercise")
            }
        }
    }

    companion object {
        fun Factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ExerciseEditorViewModel(Injection.provideRepository(context))
            }
        }
    }


}
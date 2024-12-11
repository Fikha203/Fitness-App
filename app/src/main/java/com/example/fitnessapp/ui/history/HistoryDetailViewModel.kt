package com.example.fitnessapp.ui.history

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fitnessapp.data.GymRepository
import com.example.fitnessapp.data.model.History
import com.example.fitnessapp.di.Injection
import com.example.fitnessapp.data.model.ExerciseDetails
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryDetailViewModel(private val gymRepository: GymRepository) : ViewModel() {


    private val _history = MutableStateFlow<History>(History(id = 0, planId = 0, date = 123, progress = ""))
    val history: StateFlow<History> = _history.asStateFlow()

    private val _planName = MutableStateFlow<String>("")
    val planName: StateFlow<String> = _planName.asStateFlow()

    private val _progressDetails = MutableStateFlow<List<ExerciseDetails>>(emptyList())
    val progressDetails: StateFlow<List<ExerciseDetails>> = _progressDetails.asStateFlow()

    private val _exerciseNames = MutableStateFlow<Map<Int, String>>(emptyMap())
    val exerciseNames: StateFlow<Map<Int, String>> = _exerciseNames.asStateFlow()

    private val gson = Gson()

    fun getHistory(id: Int) {
        viewModelScope.launch {
            try {
                val history = gymRepository.getHistory(id)
                _history.value = history

                // Dapatkan nama plan berdasarkan planId
                val plan = gymRepository.getPlan(history.planId)
                _planName.value = plan?.name ?: "Unknown Plan"

                // Parse progress JSON ke dalam list ProgressDetail
                val type = object : TypeToken<List<ExerciseDetails>>() {}.type
                val progressDetails = gson.fromJson<List<ExerciseDetails>>(history.progress, type)
                _progressDetails.value = progressDetails

                // Dapatkan nama exercise berdasarkan exerciseId dari progressDetails
                val exerciseNames = progressDetails.associate { detail ->
                    detail.exerciseId to (gymRepository.getExercise(detail.exerciseId)?.name ?: "Unknown Exercise")
                }
                _exerciseNames.value = exerciseNames

            } catch (e: Exception) {
                Log.e("HistoryDetailViewModel", "Error fetching history details", e)
            }
        }
    }

    companion object {
        fun Factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                HistoryDetailViewModel(Injection.provideRepository(context))
            }
        }
    }
}
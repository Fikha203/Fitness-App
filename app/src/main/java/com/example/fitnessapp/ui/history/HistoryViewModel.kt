package com.example.fitnessapp.ui.history

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fitnessapp.data.GymRepository
import com.example.fitnessapp.data.model.History
import com.example.fitnessapp.di.Injection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(private val gymRepository: GymRepository) : ViewModel() {


    private val _historyList = MutableStateFlow<List<History>>(emptyList())
    val historyList: StateFlow<List<History>> get() = _historyList

    private val _planNameMap = MutableStateFlow<Map<Int, String>>(emptyMap())
    val planNameMap: StateFlow<Map<Int, String>> get() = _planNameMap

    init {
        getAllHistory()
    }

    private fun getAllHistory() {
        viewModelScope.launch {
            val histories = gymRepository.getAllHistory()
            _historyList.value = histories

            // Load Plan Names for each Plan ID
            val planNames = histories.associate { history ->
                history.planId to (gymRepository.getPlan(history.planId)?.name ?: "Unknown Plan")
            }
            _planNameMap.value = planNames
        }
    }

    fun deleteHistory(history: History) {
        viewModelScope.launch {
            gymRepository.deleteHistory(history)
            getAllHistory()
        }
    }


    companion object {
        fun Factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                HistoryViewModel(Injection.provideRepository(context))
            }
        }
    }

}
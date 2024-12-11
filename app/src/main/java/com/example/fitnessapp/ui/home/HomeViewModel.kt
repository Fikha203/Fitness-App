package com.example.fitnessapp.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fitnessapp.data.GymRepository
import com.example.fitnessapp.data.model.Plan
import com.example.fitnessapp.di.Injection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: GymRepository) : ViewModel() {


    private val _planList = MutableStateFlow<List<Plan>>(emptyList())
    val planList: StateFlow<List<Plan>> get() = _planList


    init {
        getPlanList()
    }

    private fun getPlanList() {
        viewModelScope.launch {
            _planList.value = repository.getAllPlans()

        }
    }

    fun deletePlan(plan: Plan) {
        viewModelScope.launch {
            repository.deletePlan(plan)
            getPlanList()
        }
    }


    companion object {
        fun Factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                HomeViewModel(Injection.provideRepository(context))
            }
        }
    }
}


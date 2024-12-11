package com.example.fitnessapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SetDetails(
    val reps: Int = 0,
    val weight: Int = 0,
    val isComplete: Boolean = false
)
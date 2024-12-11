package com.example.fitnessapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ExerciseDetails(
    val exerciseId: Int,
    val sets: List<SetDetails> = emptyList()
)
package com.example.fitnessapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable


@Serializable
@Entity
data class Plan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val exerciseDetails: List<ExerciseDetails> = mutableListOf()

)


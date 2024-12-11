package com.example.fitnessapp.util

import androidx.room.TypeConverter
import com.example.fitnessapp.data.model.ExerciseDetails
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class Converters {

    @TypeConverter
    fun fromExerciseDetailsList(value: List<ExerciseDetails>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toExerciseDetailsList(value: String): List<ExerciseDetails> {
        return Json.decodeFromString(value)
    }
}


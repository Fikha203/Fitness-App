package com.example.fitnessapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class History(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val planId: Int,
    val date: Long,
    val progress: String
)


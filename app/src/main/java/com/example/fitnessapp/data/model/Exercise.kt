package com.example.fitnessapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey



// Implementasi Builder Pattern pada Entity Exercise
@Entity
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String = ""
) {
    class Builder {
        private var id: Int = 0
        private var name: String = ""
        private var description: String = ""

        fun setId(id: Int) = apply { this.id = id }
        fun setName(name: String) = apply { this.name = name }
        fun setDescription(description: String) = apply { this.description = description }

        fun build(): Exercise {
            if (name.isBlank()) throw IllegalArgumentException("Name cannot be blank")
            return Exercise(
                id = id,
                name = name,
                description = description
            )
        }
    }
}


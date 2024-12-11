package com.example.fitnessapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.fitnessapp.data.model.History


@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY date DESC")
    suspend fun getAllHistory(): List<History>

    @Query("SELECT * FROM history Where id == :id")
    suspend fun getHistory(id: Int): History

    @Insert
    suspend fun insertHistory(history: History)

    @Delete
    suspend fun deleteHistory(history: History)
}
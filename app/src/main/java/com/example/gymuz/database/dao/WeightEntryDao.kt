package com.example.gymuz.database.dao

import androidx.room.*
import com.example.gymuz.database.entity.WeightEntry

@Dao
interface WeightEntryDao {
    @Insert
    suspend fun insertWeightEntry(entry: WeightEntry): Long

    @Query("SELECT * FROM weight_entries WHERE userId = :userId ORDER BY date DESC, timestamp DESC")
    suspend fun getWeightEntriesByUserId(userId: Int): List<WeightEntry>

    @Query("SELECT * FROM weight_entries WHERE userId = :userId ORDER BY date DESC, timestamp DESC LIMIT 30")
    suspend fun getRecentWeightEntries(userId: Int): List<WeightEntry>

    @Query("SELECT * FROM weight_entries WHERE userId = :userId ORDER BY date DESC, timestamp DESC LIMIT 1")
    suspend fun getLatestWeightEntry(userId: Int): WeightEntry?

    @Query("SELECT * FROM weight_entries WHERE userId = :userId AND date >= :startDate ORDER BY date ASC")
    suspend fun getWeightEntriesFromDate(userId: Int, startDate: String): List<WeightEntry>

    @Update
    suspend fun updateWeightEntry(entry: WeightEntry)

    @Delete
    suspend fun deleteWeightEntry(entry: WeightEntry)

    @Query("DELETE FROM weight_entries WHERE userId = :userId")
    suspend fun deleteAllWeightEntriesForUser(userId: Int)
}
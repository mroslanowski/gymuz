package com.example.gymuz.database.dao

import androidx.room.*
import com.example.gymuz.database.entity.ProgressPhoto

@Dao
interface ProgressPhotoDao {
    @Insert
    suspend fun insertProgressPhoto(photo: ProgressPhoto): Long

    @Query("SELECT * FROM progress_photos WHERE userId = :userId ORDER BY date DESC, timestamp DESC")
    suspend fun getProgressPhotosByUserId(userId: Int): List<ProgressPhoto>

    @Query("SELECT * FROM progress_photos WHERE userId = :userId AND type = :type ORDER BY date DESC, timestamp DESC")
    suspend fun getProgressPhotosByType(userId: Int, type: String): List<ProgressPhoto>

    @Query("SELECT * FROM progress_photos WHERE userId = :userId AND date = :date ORDER BY timestamp DESC")
    suspend fun getProgressPhotosByDate(userId: Int, date: String): List<ProgressPhoto>

    @Query("SELECT * FROM progress_photos WHERE userId = :userId AND date >= :startDate ORDER BY date ASC")
    suspend fun getProgressPhotosFromDate(userId: Int, startDate: String): List<ProgressPhoto>

    @Update
    suspend fun updateProgressPhoto(photo: ProgressPhoto)

    @Delete
    suspend fun deleteProgressPhoto(photo: ProgressPhoto)

    @Query("DELETE FROM progress_photos WHERE userId = :userId")
    suspend fun deleteAllProgressPhotosForUser(userId: Int)
}
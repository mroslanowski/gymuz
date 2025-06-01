package com.example.gymuz.database.dao

import androidx.room.*
import com.example.gymuz.database.entity.ExerciseProgress

@Dao
interface ExerciseProgressDao {
    @Insert
    suspend fun insertExerciseProgress(progress: ExerciseProgress): Long

    @Query("SELECT * FROM exercise_progress WHERE userId = :userId ORDER BY date DESC, timestamp DESC")
    suspend fun getExerciseProgressByUserId(userId: Int): List<ExerciseProgress>

    @Query("SELECT * FROM exercise_progress WHERE userId = :userId AND exerciseName = :exerciseName ORDER BY date DESC, timestamp DESC")
    suspend fun getExerciseProgressByName(userId: Int, exerciseName: String): List<ExerciseProgress>

    @Query("SELECT DISTINCT exerciseName FROM exercise_progress WHERE userId = :userId ORDER BY exerciseName ASC")
    suspend fun getUniqueExerciseNames(userId: Int): List<String>

    @Query("SELECT * FROM exercise_progress WHERE userId = :userId AND exerciseName = :exerciseName ORDER BY date DESC, timestamp DESC LIMIT 1")
    suspend fun getLatestExerciseProgress(userId: Int, exerciseName: String): ExerciseProgress?

    @Query("SELECT * FROM exercise_progress WHERE userId = :userId AND date >= :startDate ORDER BY date ASC")
    suspend fun getExerciseProgressFromDate(userId: Int, startDate: String): List<ExerciseProgress>

    @Update
    suspend fun updateExerciseProgress(progress: ExerciseProgress)

    @Delete
    suspend fun deleteExerciseProgress(progress: ExerciseProgress)

    @Query("DELETE FROM exercise_progress WHERE userId = :userId")
    suspend fun deleteAllExerciseProgressForUser(userId: Int)
}
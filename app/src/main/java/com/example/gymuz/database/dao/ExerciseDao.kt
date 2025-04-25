package com.example.gymuz.database.dao

import androidx.room.*
import com.example.gymuz.database.entity.Exercise

@Dao
interface ExerciseDao {
    @Insert
    suspend fun insertExercise(exercise: Exercise): Long

    @Query("SELECT * FROM exercises WHERE dayId = :dayId ORDER BY `order`")
    suspend fun getExercisesByDayId(dayId: Int): List<Exercise>

    @Update
    suspend fun updateExercise(exercise: Exercise)

    @Delete
    suspend fun deleteExercise(exercise: Exercise)

    @Query("SELECT MAX(`order`) FROM exercises WHERE dayId = :dayId")
    suspend fun getMaxOrderForDay(dayId: Int): Int?
}
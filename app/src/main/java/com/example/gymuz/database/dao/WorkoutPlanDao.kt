package com.example.gymuz.database.dao

import androidx.room.*
import com.example.gymuz.database.entity.WorkoutPlan

@Dao
interface WorkoutPlanDao {
    @Insert
    suspend fun insertWorkoutPlan(workoutPlan: WorkoutPlan): Long

    @Query("SELECT * FROM workout_plans WHERE userId = :userId")
    suspend fun getWorkoutPlanByUserId(userId: Int): WorkoutPlan?

    @Update
    suspend fun updateWorkoutPlan(workoutPlan: WorkoutPlan)

    @Delete
    suspend fun deleteWorkoutPlan(workoutPlan: WorkoutPlan)
}
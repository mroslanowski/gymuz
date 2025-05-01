package com.example.gymuz.database.dao

import androidx.room.*

@Dao
interface WorkoutDayDao {
    @Insert
    suspend fun insertWorkoutDay(workoutDay: WorkoutDay): Long

    @Insert
    suspend fun insertWorkoutDays(workoutDays: List<WorkoutDay>): List<Long>

    @Query("SELECT * FROM workout_days WHERE planId = :planId ORDER BY dayOfWeek")
    suspend fun getWorkoutDaysByPlanId(planId: Int): List<WorkoutDay>

    @Query("SELECT * FROM workout_days WHERE planId = :planId AND dayOfWeek = :dayOfWeek")
    suspend fun getWorkoutDayByPlanIdAndDayOfWeek(planId: Int, dayOfWeek: Int): WorkoutDay?

    @Update
    suspend fun updateWorkoutDay(workoutDay: WorkoutDay)
}
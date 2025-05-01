package com.example.gymuz.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WorkoutRepository(
    private val workoutPlanDao: WorkoutPlanDao,
    private val workoutDayDao: WorkoutDayDao,
    private val exerciseDao: ExerciseDao
) {
    // Metody dla WorkoutPlan
    suspend fun createWorkoutPlanForUser(userId: Int): WorkoutPlan {
        return withContext(Dispatchers.IO) {
            // Sprawdź, czy użytkownik już ma plan
            val existingPlan = workoutPlanDao.getWorkoutPlanByUserId(userId)
            if (existingPlan != null) {
                return@withContext existingPlan
            }

            // Jeśli nie ma planu, stwórz nowy
            val workoutPlan = WorkoutPlan(userId = userId)
            val planId = workoutPlanDao.insertWorkoutPlan(workoutPlan).toInt()

            // Stwórz 7 dni tygodnia
            val days = listOf(
                WorkoutDay(planId = planId, dayOfWeek = 0, dayName = "Niedziela"),
                WorkoutDay(planId = planId, dayOfWeek = 1, dayName = "Poniedziałek"),
                WorkoutDay(planId = planId, dayOfWeek = 2, dayName = "Wtorek"),
                WorkoutDay(planId = planId, dayOfWeek = 3, dayName = "Środa"),
                WorkoutDay(planId = planId, dayOfWeek = 4, dayName = "Czwartek"),
                WorkoutDay(planId = planId, dayOfWeek = 5, dayName = "Piątek"),
                WorkoutDay(planId = planId, dayOfWeek = 6, dayName = "Sobota")
            )
            workoutDayDao.insertWorkoutDays(days)

            return@withContext workoutPlan.copy(id = planId)
        }
    }

    suspend fun getWorkoutPlanForUser(userId: Int): WorkoutPlan? {
        return workoutPlanDao.getWorkoutPlanByUserId(userId)
    }

    // Metody dla WorkoutDay
    suspend fun getWorkoutDays(planId: Int): List<WorkoutDay> {
        return workoutDayDao.getWorkoutDaysByPlanId(planId)
    }

    suspend fun getWorkoutDayByDayOfWeek(planId: Int, dayOfWeek: Int): WorkoutDay? {
        return workoutDayDao.getWorkoutDayByPlanIdAndDayOfWeek(planId, dayOfWeek)
    }

    suspend fun updateWorkoutDayCustomName(dayId: Int, customName: String?) {
        withContext(Dispatchers.IO) {
            val day = getWorkoutDays(dayId).firstOrNull { it.id == dayId }
            day?.let {
                workoutDayDao.updateWorkoutDay(it.copy(customDayName = customName))
            }
        }
    }

    // Metody dla Exercise
    suspend fun addExercise(dayId: Int, name: String, sets: Int, reps: Int, weight: Float): Exercise {
        return withContext(Dispatchers.IO) {
            val maxOrder = exerciseDao.getMaxOrderForDay(dayId) ?: -1
            val exercise = Exercise(
                dayId = dayId,
                name = name,
                sets = sets,
                reps = reps,
                weight = weight,
                order = maxOrder + 1
            )
            val id = exerciseDao.insertExercise(exercise).toInt()
            return@withContext exercise.copy(id = id)
        }
    }

    suspend fun getExercisesForDay(dayId: Int): List<Exercise> {
        return exerciseDao.getExercisesByDayId(dayId)
    }

    suspend fun updateExercise(exercise: Exercise) {
        exerciseDao.updateExercise(exercise)
    }

    suspend fun deleteExercise(exercise: Exercise) {
        exerciseDao.deleteExercise(exercise)
    }
}
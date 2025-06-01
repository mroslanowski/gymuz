package com.example.gymuz.database.repository

import com.example.gymuz.database.dao.ExerciseProgressDao
import com.example.gymuz.database.dao.ProgressPhotoDao
import com.example.gymuz.database.dao.WeightEntryDao
import com.example.gymuz.database.entity.ExerciseProgress
import com.example.gymuz.database.entity.ProgressPhoto
import com.example.gymuz.database.entity.WeightEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ProgressRepository(
    private val weightEntryDao: WeightEntryDao,
    private val exerciseProgressDao: ExerciseProgressDao,
    private val progressPhotoDao: ProgressPhotoDao
) {

    // Weight related methods
    suspend fun addWeightEntry(userId: Int, weight: Float, notes: String?) {
        withContext(Dispatchers.IO) {
            val entry = WeightEntry(
                userId = userId,
                weight = weight,
                date = getCurrentDate(),
                notes = notes
            )
            weightEntryDao.insertWeightEntry(entry)
        }
    }

    suspend fun getWeightEntries(userId: Int): List<WeightEntry> {
        return weightEntryDao.getWeightEntriesByUserId(userId)
    }

    suspend fun getRecentWeightEntries(userId: Int): List<WeightEntry> {
        return weightEntryDao.getRecentWeightEntries(userId)
    }

    suspend fun getLatestWeight(userId: Int): WeightEntry? {
        return weightEntryDao.getLatestWeightEntry(userId)
    }

    suspend fun getWeightEntriesFromDate(userId: Int, startDate: String): List<WeightEntry> {
        return weightEntryDao.getWeightEntriesFromDate(userId, startDate)
    }

    suspend fun deleteWeightEntry(entry: WeightEntry) {
        withContext(Dispatchers.IO) {
            weightEntryDao.deleteWeightEntry(entry)
        }
    }

    // Exercise progress related methods
    suspend fun addExerciseProgress(userId: Int, exerciseName: String, weight: Float, sets: Int, reps: Int, notes: String?) {
        withContext(Dispatchers.IO) {
            val progress = ExerciseProgress(
                userId = userId,
                exerciseName = exerciseName,
                weight = weight,
                sets = sets,
                reps = reps,
                date = getCurrentDate(),
                notes = notes
            )
            exerciseProgressDao.insertExerciseProgress(progress)
        }
    }

    suspend fun getExerciseProgress(userId: Int): List<ExerciseProgress> {
        return exerciseProgressDao.getExerciseProgressByUserId(userId)
    }

    suspend fun getExerciseProgressByName(userId: Int, exerciseName: String): List<ExerciseProgress> {
        return exerciseProgressDao.getExerciseProgressByName(userId, exerciseName)
    }

    suspend fun getUniqueExerciseNames(userId: Int): List<String> {
        return exerciseProgressDao.getUniqueExerciseNames(userId)
    }

    // NOWA METODA: Pobiera nazwy ćwiczeń z obu tabel
    suspend fun getAllAvailableExerciseNames(userId: Int): List<String> {
        return withContext(Dispatchers.IO) {
            // Pobierz nazwy z tabeli ExerciseProgress
            val progressExercises = exerciseProgressDao.getUniqueExerciseNames(userId)

            // Pobierz nazwy z tabeli Exercise (z planów treningowych)
            val planExercises = getExerciseNamesFromPlans(userId)

            // Połącz i usuń duplikaty
            (progressExercises + planExercises).distinct().sorted()
        }
    }

    // NOWA METODA: Pobiera nazwy ćwiczeń z planów treningowych
    private suspend fun getExerciseNamesFromPlans(userId: Int): List<String> {
        return try {
            // Tutaj musimy dodać dostęp do ExerciseDao
            // Tymczasowo zwracamy pustą listę - zostanie zaktualizowane
            emptyList<String>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getLatestExerciseProgress(userId: Int, exerciseName: String): ExerciseProgress? {
        return exerciseProgressDao.getLatestExerciseProgress(userId, exerciseName)
    }

    suspend fun deleteExerciseProgress(progress: ExerciseProgress) {
        withContext(Dispatchers.IO) {
            exerciseProgressDao.deleteExerciseProgress(progress)
        }
    }

    // Photo progress related methods
    suspend fun addProgressPhoto(userId: Int, photoPath: String, type: String, description: String?) {
        withContext(Dispatchers.IO) {
            val photo = ProgressPhoto(
                userId = userId,
                photoPath = photoPath,
                date = getCurrentDate(),
                type = type,
                description = description
            )
            progressPhotoDao.insertProgressPhoto(photo)
        }
    }

    suspend fun getProgressPhotos(userId: Int): List<ProgressPhoto> {
        return progressPhotoDao.getProgressPhotosByUserId(userId)
    }

    suspend fun getProgressPhotosByType(userId: Int, type: String): List<ProgressPhoto> {
        return progressPhotoDao.getProgressPhotosByType(userId, type)
    }

    suspend fun deleteProgressPhoto(photo: ProgressPhoto) {
        withContext(Dispatchers.IO) {
            progressPhotoDao.deleteProgressPhoto(photo)
        }
    }

    // Utility methods
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun getDateDaysAgo(days: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -days)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}
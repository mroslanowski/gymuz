package com.example.gymuz.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gymuz.database.dao.*
import com.example.gymuz.database.entity.*

@Database(
    entities = [
        User::class,
        WorkoutPlan::class,
        WorkoutDay::class,
        Exercise::class,
        WeightEntry::class,
        ExerciseProgress::class,
        ProgressPhoto::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun workoutPlanDao(): WorkoutPlanDao
    abstract fun workoutDayDao(): WorkoutDayDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun weightEntryDao(): WeightEntryDao
    abstract fun exerciseProgressDao(): ExerciseProgressDao
    abstract fun progressPhotoDao(): ProgressPhotoDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        private const val DATABASE_NAME = "app_db"

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration() // W fazie rozwoju aplikacji, w produkcji powinno się zaimplementować migrację
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
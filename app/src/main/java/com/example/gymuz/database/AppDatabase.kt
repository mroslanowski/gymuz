package com.example.gymuz.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gymuz.database.dao.ExerciseDao
import com.example.gymuz.database.dao.UserDao
import com.example.gymuz.database.dao.WorkoutDay
import com.example.gymuz.database.dao.WorkoutDayDao
import com.example.gymuz.database.dao.WorkoutPlanDao
import com.example.gymuz.database.entity.Exercise
import com.example.gymuz.database.entity.User
import com.example.gymuz.database.entity.WorkoutPlan

@Database(
    entities = [
        User::class,
        WorkoutPlan::class,
        WorkoutDay::class,
        Exercise::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun workoutPlanDao(): WorkoutPlanDao
    abstract fun workoutDayDao(): WorkoutDayDao
    abstract fun exerciseDao(): ExerciseDao

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
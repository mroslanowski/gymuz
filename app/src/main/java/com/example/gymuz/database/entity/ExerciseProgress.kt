package com.example.gymuz.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercise_progress",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ExerciseProgress(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val exerciseName: String,
    val weight: Float,
    val sets: Int,
    val reps: Int,
    val date: String, // format: "yyyy-MM-dd"
    val notes: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
package com.example.gymuz.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercises",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutDay::class,
            parentColumns = ["id"],
            childColumns = ["dayId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dayId: Int,
    val name: String,
    val sets: Int,
    val reps: Int,
    val weight: Float,
    val order: Int // do sortowania ćwiczeń
)
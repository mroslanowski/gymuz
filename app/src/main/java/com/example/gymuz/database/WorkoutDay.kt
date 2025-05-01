package com.example.gymuz.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_days",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutPlan::class,
            parentColumns = ["id"],
            childColumns = ["planId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WorkoutDay(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val planId: Int,
    val dayOfWeek: Int, // 0 (Niedziela) do 6 (Sobota)
    val dayName: String, // Standardowa nazwa dnia (np. "Poniedziałek")
    val customDayName: String? = null // Opcjonalna niestandardowa nazwa
)
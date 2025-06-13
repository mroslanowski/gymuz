package com.example.gymuz.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_plans",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WorkoutPlan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val name: String = "My Workout com.example.gymuz.fragments.Plan"
)
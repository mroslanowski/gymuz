package com.example.gymuz.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "progress_photos",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ProgressPhoto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val photoPath: String,
    val date: String, // format: "yyyy-MM-dd"
    val type: String, // "BEFORE", "DURING", "AFTER"
    val description: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
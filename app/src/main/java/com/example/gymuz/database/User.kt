package com.example.gymuz.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var name: String,
    var email: String,
    var password: String,
    var sex: String? = null
)
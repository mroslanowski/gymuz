package com.example.gymuz.database

import androidx.room.*

@Dao
interface UserDao {
    @Insert
    fun insertUser(user: User): Long

    @Update
    fun updateUser(user: User)

    @Delete
    fun deleteUser(user: User)

    @Query("SELECT * FROM users WHERE email = :email")
    fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: Int): User?
}
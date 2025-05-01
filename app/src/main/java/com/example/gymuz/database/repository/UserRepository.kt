package com.example.gymuz.database.repository

import com.example.gymuz.database.dao.UserDao
import com.example.gymuz.database.entity.User

class UserRepository(private val userDao: UserDao) {
    suspend fun registerUser(user: User) = userDao.insertUser(user)
    suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)
}

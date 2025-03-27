package com.example.gymuz.database

class UserRepository(private val userDao: UserDao) {
    suspend fun registerUser(user: User) = userDao.insertUser(user)
    suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)
}

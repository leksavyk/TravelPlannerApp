package com.example.travelplanner.data.repository

import com.android.identity.util.UUID
import com.example.travelplanner.utils.SecurityUtils
import com.example.travelplanner.data.local.dao.UserDao
import com.example.travelplanner.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {
    val currentUser: Flow<UserEntity?> = userDao.getCurrentUser()

    suspend fun registerUser(username: String, email: String, password: String) {
        val newUser = UserEntity(
            id = UUID.randomUUID().toString(),
            username = username,
            email = email,
            passwordHash = SecurityUtils.hashPassword(password),
            isLoggedIn = true
        )
        userDao.insertUser(newUser)
    }

    suspend fun logout(userId: String) {
        userDao.updateLoginStatus(userId, false)
    }

    suspend fun deleteAccount(userId: String) {
        userDao.deleteUser(userId)
    }

    suspend fun login(email: String, password: String): Boolean {
        val user = userDao.getUserByEmail(email)
        return if (user != null && SecurityUtils.hashPassword(password) == user.passwordHash) {
            userDao.updateLoginStatus(user.id, true)
            true
        } else {
            false
        }
    }
}
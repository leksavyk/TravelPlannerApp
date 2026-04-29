package com.example.travelplanner.data.repository

import com.example.travelplanner.data.local.dao.UserDao
import com.example.travelplanner.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {
    val currentUser: Flow<UserEntity?> = userDao.getCurrentUser()

    suspend fun registerUser(id: String, username: String, email: String) {
        val userEntity = UserEntity(id, username, email)
        userDao.insertUser(userEntity)
    }

    suspend fun logout() {
        userDao.clearAllUsers()
    }
}
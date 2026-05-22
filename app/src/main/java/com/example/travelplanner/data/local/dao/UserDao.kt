package com.example.travelplanner.data.local.dao

import androidx.room.*
import com.example.travelplanner.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    fun getCurrentUser(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("UPDATE users SET isLoggedIn = :loggedIn WHERE id = :userId")
    suspend fun updateLoginStatus(userId: String, loggedIn: Boolean)

    @Query("DELETE FROM users")
    suspend fun clearAllUsers()

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: String)
}
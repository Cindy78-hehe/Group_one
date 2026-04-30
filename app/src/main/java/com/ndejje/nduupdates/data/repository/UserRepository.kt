package com.ndejje.nduupdates.data.repository

import com.ndejje.nduupdates.data.model.UserDao
import com.ndejje.nduupdates.data.model.UserEntity
import java.security.MessageDigest

class UserRepository(private val userDao: UserDao) {

    suspend fun register(username: String, email: String, pass: String, role: String): Result<Unit> {
        return try {
            val existing = userDao.getUserByEmail(email)
            if (existing != null) return Result.failure(Exception("User already exists"))

            val passwordHash = hashPassword(pass)
            val user = UserEntity(email, username, passwordHash, role)
            userDao.insertUser(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, pass: String): Result<UserEntity> {
        return try {
            val user = userDao.getUserByEmail(email)
            if (user != null && user.passwordHash == hashPassword(pass)) {
                Result.success(user)
            } else {
                Result.failure(Exception("Invalid email or password"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}

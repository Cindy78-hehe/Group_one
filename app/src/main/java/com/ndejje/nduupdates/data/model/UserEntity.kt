package com.ndejje.nduupdates.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val email: String,
    val username: String,
    val passwordHash: String,
    val role: String // "STUDENT", "STAFF", "ADMIN"
)

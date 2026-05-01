package com.ndejje.nduupdates.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val noticeId: Int,
    val author: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

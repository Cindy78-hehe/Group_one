package com.ndejje.nduupdates.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notices")
data class NoticeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val author: String,
    val authorRole: String,
    val timestamp: Long = System.currentTimeMillis(),
    val targetRole: String = "All"
)

package com.ndejje.nduupdates.data.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE noticeId = :noticeId ORDER BY timestamp ASC")
    fun getCommentsForNotice(noticeId: Int): Flow<List<CommentEntity>>

    @Insert
    suspend fun insertComment(comment: CommentEntity)

    @Query("DELETE FROM comments WHERE noticeId = :noticeId")
    suspend fun deleteCommentsByNoticeId(noticeId: Int)
}

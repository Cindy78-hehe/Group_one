package com.ndejje.nduupdates.data.repository

import com.ndejje.nduupdates.data.model.CommentDao
import com.ndejje.nduupdates.data.model.CommentEntity
import com.ndejje.nduupdates.data.model.NoticeDao
import com.ndejje.nduupdates.data.model.NoticeEntity
import kotlinx.coroutines.flow.Flow

class NoticeRepository(
    private val noticeDao: NoticeDao,
    private val commentDao: CommentDao
) {

    suspend fun addNotice(notice: NoticeEntity): Result<Unit> {
        return try {
            noticeDao.insertNotice(notice)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getNotices(): Flow<List<NoticeEntity>> = noticeDao.getAllNotices()

    suspend fun deleteNotice(noticeId: Int): Result<Unit> {
        return try {
            noticeDao.deleteNoticeById(noticeId)
            commentDao.deleteCommentsByNoticeId(noticeId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCommentsForNotice(noticeId: Int): Flow<List<CommentEntity>> = 
        commentDao.getCommentsForNotice(noticeId)

    suspend fun addComment(comment: CommentEntity): Result<Unit> {
        return try {
            commentDao.insertComment(comment)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

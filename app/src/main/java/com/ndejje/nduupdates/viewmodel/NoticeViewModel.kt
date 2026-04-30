package com.ndejje.nduupdates.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ndejje.nduupdates.data.model.CommentEntity
import com.ndejje.nduupdates.data.model.NoticeEntity
import com.ndejje.nduupdates.data.repository.NoticeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NoticeViewModel(private val repository: NoticeRepository) : ViewModel() {

    val notices: StateFlow<List<NoticeEntity>> = repository.getNotices()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addNotice(notice: NoticeEntity) {
        viewModelScope.launch {
            repository.addNotice(notice)
        }
    }

    fun deleteNotice(noticeId: Int) {
        viewModelScope.launch {
            repository.deleteNotice(noticeId)
        }
    }

    fun getCommentsForNotice(noticeId: Int): Flow<List<CommentEntity>> =
        repository.getCommentsForNotice(noticeId)

    fun addComment(comment: CommentEntity) {
        viewModelScope.launch {
            repository.addComment(comment)
        }
    }
}

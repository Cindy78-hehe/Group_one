package com.ndejje.nduupdates.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ndejje.nduupdates.data.model.CommentEntity
import com.ndejje.nduupdates.data.model.NoticeEntity
import com.ndejje.nduupdates.data.repository.NoticeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.*

data class CreateNoticeState(
    val title: String = "",
    val content: String = "",
    val targetRole: String = "All",
    val type: String = "Notice",
    val attachmentUri: String? = null,
    val attachmentType: String? = null
)

class NoticeViewModel(private val repository: NoticeRepository) : ViewModel() {

    private val _createNoticeState = MutableStateFlow(CreateNoticeState())
    val createNoticeState: StateFlow<CreateNoticeState> = _createNoticeState.asStateFlow()

    fun onTitleChange(title: String) { _createNoticeState.update { it.copy(title = title) } }
    fun onContentChange(content: String) { _createNoticeState.update { it.copy(content = content) } }
    fun onTargetRoleChange(role: String) { _createNoticeState.update { it.copy(targetRole = role) } }
    fun onTypeChange(type: String) { _createNoticeState.update { it.copy(type = type) } }
    fun onAttachmentChange(uri: String?, type: String?) {
        _createNoticeState.update { it.copy(attachmentUri = uri, attachmentType = type) }
    }

    val notices: StateFlow<List<NoticeEntity>> = repository.getNotices()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addNotice(author: String, authorRole: String) {
        val state = _createNoticeState.value
        if (state.title.isBlank() || state.content.isBlank()) return

        viewModelScope.launch {
            val notice = NoticeEntity(
                title = state.title,
                content = state.content,
                targetRole = state.targetRole,
                author = author,
                authorRole = authorRole,
                type = state.type,
                timestamp = System.currentTimeMillis(),
                attachmentUri = state.attachmentUri,
                attachmentType = state.attachmentType
            )
            repository.addNotice(notice)
            _createNoticeState.value = CreateNoticeState() // Reset
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

package com.ndejje.nduupdates.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ndejje.nduupdates.data.repository.UserRepository
import com.ndejje.nduupdates.data.repository.NoticeRepository

class ViewModelFactory(
    private val userRepository: UserRepository? = null,
    private val noticeRepository: NoticeRepository? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(userRepository!!) as T
        }
        if (modelClass.isAssignableFrom(NoticeViewModel::class.java)) {
            return NoticeViewModel(noticeRepository!!) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

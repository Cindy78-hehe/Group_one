package com.ndejje.nduupdates.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ndejje.nduupdates.data.model.UserEntity
import com.ndejje.nduupdates.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginState(
    val email: String = "",
    val pass: String = ""
)

data class RegisterState(
    val name: String = "",
    val email: String = "",
    val pass: String = "",
    val role: String = "Student"
)

class AuthViewModel(private val repository: UserRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(RegisterState())
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    fun onLoginEmailChange(email: String) {
        _loginState.update { it.copy(email = email) }
    }

    fun onLoginPasswordChange(pass: String) {
        _loginState.update { it.copy(pass = pass) }
    }

    fun onRegisterNameChange(name: String) {
        _registerState.update { it.copy(name = name) }
    }

    fun onRegisterEmailChange(email: String) {
        _registerState.update { it.copy(email = email) }
    }

    fun onRegisterPasswordChange(pass: String) {
        _registerState.update { it.copy(pass = pass) }
    }

    fun onRegisterRoleChange(role: String) {
        _registerState.update { it.copy(role = role) }
    }

    fun login() {
        val email = _loginState.value.email
        val pass = _loginState.value.pass
        if (email.isBlank() || pass.isBlank()) {
            _uiState.value = AuthUiState.Error("Please fill in all fields")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = repository.login(email, pass)
            result.onSuccess { user ->
                _currentUser.value = user
                _uiState.value = AuthUiState.Success("Login Successful")
                _loginState.value = LoginState() // Clear state on success
            }.onFailure { e ->
                _uiState.value = AuthUiState.Error(e.message ?: "Login Failed")
            }
        }
    }

    fun register() {
        val state = _registerState.value
        if (state.name.isBlank() || state.email.isBlank() || state.pass.isBlank()) {
            _uiState.value = AuthUiState.Error("Please fill in all fields")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = repository.register(state.name, state.email, state.pass, state.role)
            result.onSuccess {
                _uiState.value = AuthUiState.Success("Registration Successful")
                _registerState.value = RegisterState() // Clear state on success
            }.onFailure { e ->
                _uiState.value = AuthUiState.Error(e.message ?: "Registration Failed")
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _uiState.value = AuthUiState.Idle
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

    fun updateProfile(name: String, profilePicUri: String?) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val updatedUser = user.copy(username = name, profilePictureUri = profilePicUri)
            val result = repository.updateUser(updatedUser)
            result.onSuccess {
                _currentUser.value = updatedUser
                _uiState.value = AuthUiState.Success("Profile Updated Successfully")
            }.onFailure { e ->
                _uiState.value = AuthUiState.Error(e.message ?: "Update Failed")
            }
        }
    }
}

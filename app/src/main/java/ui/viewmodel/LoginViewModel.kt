package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.TokenProvider
import com.example.myapplication.data.UserRepository
import com.example.myapplication.data.UserLoginResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.myapplication.data.poll.AuthRepository
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun login(username: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                _loading.value = true

                val res = repo.login(username, password)

                TokenProvider.accessToken = res.access_token
                TokenProvider.refreshToken = res.refresh_token

                onSuccess()

            } catch (e: Exception) {
                _error.value = "로그인 실패: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun signup(username: String, password: String, nickname: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                _loading.value = true

                val res = repo.signup(username, password, nickname)

                TokenProvider.accessToken = res.access_token
                TokenProvider.refreshToken = res.refresh_token

                onSuccess()

            } catch (e: Exception) {
                _error.value = "회원가입 실패: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }
}

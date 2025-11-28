package com.example.myapplication.data.poll
import com.example.myapplication.data.RetrofitClient


class AuthRepository(
    private val api: AuthApi = RetrofitClient.authApi
) {

    suspend fun login(username: String, password: String): SignInResponse {
        return api.signIn(username, password)
    }

    suspend fun signup(username: String, password: String, nickname: String): SignInResponse {
        return api.signUp(username, password, nickname)
    }
}

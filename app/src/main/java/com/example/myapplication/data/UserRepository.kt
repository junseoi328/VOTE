package com.example.myapplication.data

import com.example.myapplication.data.api.UserApi

class UserRepository(
    private val api: UserApi = RetrofitClient.userApi
) {

    suspend fun signup(username: String, password: String, nickname: String): UserLoginResponse {
        return api.signup(username, password, nickname)
    }

    suspend fun login(username: String, password: String): UserLoginResponse {
        return api.login(username, password)
    }
}

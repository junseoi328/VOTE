package com.example.myapplication.data.api

import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query
import com.example.myapplication.data.UserLoginResponse


interface UserApi {

    @POST("/signup")
    suspend fun signup(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("nickname") nickname: String
    ): UserLoginResponse

    @POST("/login")
    suspend fun login(
        @Query("username") username: String,
        @Query("password") password: String
    ): UserLoginResponse
}



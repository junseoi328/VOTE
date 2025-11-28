package com.example.myapplication.data.poll

import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {

    @POST("signin")
    suspend fun signIn(
        @Query("username") username: String,
        @Query("password") password: String
    ): SignInResponse

    @POST("signup")
    suspend fun signUp(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("nickname") nickname: String
    ): SignInResponse
}

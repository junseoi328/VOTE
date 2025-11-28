package com.example.myapplication.data

import com.google.gson.annotations.SerializedName

data class UserLoginResponse(
    @SerializedName("access_token")
    val accessToken: String?,

    @SerializedName("refresh_token")
    val refreshToken: String?
)

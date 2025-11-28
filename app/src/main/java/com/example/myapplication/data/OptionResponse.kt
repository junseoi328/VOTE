package com.example.myapplication.data.poll

data class OptionResponse(
    val id: String,
    val option_text: String,
    val user_id: String,
    val poll_id: String,
    val created_at: String
)

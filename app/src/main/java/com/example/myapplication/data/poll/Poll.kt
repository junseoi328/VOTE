package com.example.myapplication.data.poll

data class Poll(
    val id: String,
    val title: String,
    val description: String,
    val is_anonymous: Boolean,
    val is_multiple_choice: Boolean,
    val is_option_add_allowed: Boolean,
    val is_revoting_allowed: Boolean,
    val created_at: String,
    val user_id: String
)

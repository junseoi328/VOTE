package com.example.myapplication.data.poll

sealed class PollState {
    object Idle : PollState()
    object Loading : PollState()
    data class Success(val polls: List<Poll>) : PollState()
    data class Error(val message: String) : PollState()
}

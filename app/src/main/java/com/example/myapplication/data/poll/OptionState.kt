package com.example.myapplication.data.poll

sealed class OptionsState {
    object Idle : OptionsState()
    object Loading : OptionsState()
    data class Success(val options: List<OptionResponse>) : OptionsState()
    data class Error(val message: String) : OptionsState()
}


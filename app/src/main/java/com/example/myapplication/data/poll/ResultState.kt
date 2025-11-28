package com.example.myapplication.data.poll

sealed class ResultState {
    object Idle : ResultState()
    object Loading : ResultState()



    data class Success(
        val results: Map<String, List<String>>,
        val optionMap: Map<String, String>,
        val userMap: Map<String, String>,
        val isAnonymous: Boolean
    ) : ResultState()


    data class Error(val message: String) : ResultState()



}



package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.TokenProvider
import com.example.myapplication.data.poll.PollRepository
import com.example.myapplication.data.poll.ResultState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ResultViewModel(
    private val repo: PollRepository = PollRepository()
) : ViewModel() {

    private val _resultState = MutableStateFlow<ResultState>(ResultState.Idle)
    val resultState = _resultState.asStateFlow()

    fun loadResults(pollId: String, isAnonymous: Boolean) {
        viewModelScope.launch {
            try {
                _resultState.value = ResultState.Loading

                val token = TokenProvider.accessToken ?: ""

                val options = repo.getPollOptions(token, pollId)
                val optionMap = options.associate { it.id to it.option_text }

                val raw = repo.getPollResults(token, pollId)

                val clean = raw.mapValues { (_, v) ->
                    when (v) {
                        is Number -> List(v.toInt()) { "anonymous_user_$it" }
                        is List<*> -> v.filterIsInstance<String>()
                        else -> emptyList()
                    }
                }


                val userMap = mutableMapOf<String, String>()

                if (!isAnonymous) {
                    clean.values.flatten().forEach { uid ->
                        try {
                            val u = repo.getUser(token, uid)
                            userMap[uid] = u.nickname
                        } catch (_: Exception) {
                            userMap[uid] = "알 수 없음"
                        }
                    }
                }

                _resultState.value = ResultState.Success(
                    results = clean,
                    optionMap = optionMap,
                    userMap = userMap,
                    isAnonymous = isAnonymous
                )

            } catch (e: Exception) {
                _resultState.value = ResultState.Error(e.message ?: "Unknown")
            }
        }
    }
}

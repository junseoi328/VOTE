package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.TokenProvider
import com.example.myapplication.data.poll.OptionsState
import com.example.myapplication.data.poll.PollRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OptionViewModel(
    private val repo: PollRepository = PollRepository()
) : ViewModel() {

    private fun getToken(): String = TokenProvider.accessToken ?: ""

    private val _optionsState = MutableStateFlow<OptionsState>(OptionsState.Idle)
    val optionsState = _optionsState.asStateFlow()

    fun loadOptions(pollId: String) {
        viewModelScope.launch {
            try {
                _optionsState.value = OptionsState.Loading
                val options = repo.getPollOptions(getToken(), pollId)
                _optionsState.value = OptionsState.Success(options)
            } catch (e: Exception) {
                _optionsState.value = OptionsState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    fun vote(pollId: String, optionId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                repo.vote(
                    token = getToken(),
                    pollId = pollId,
                    optionIds = listOf(optionId)
                )

                onSuccess()

            } catch (e: Exception) {
                println("❌ 투표 실패: ${e.message}")
            }
        }
    }
}

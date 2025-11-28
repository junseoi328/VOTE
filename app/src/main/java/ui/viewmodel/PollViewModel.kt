package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.TokenProvider
import com.example.myapplication.data.poll.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PollState {
    object Idle : PollState()
    object Loading : PollState()
    data class Success(val polls: List<Poll>) : PollState()
    data class Error(val message: String) : PollState()
}

sealed class PollOptionsState {
    object Idle : PollOptionsState()
    object Loading : PollOptionsState()
    data class Success(val options: List<OptionResponse>) : PollOptionsState()
    data class Error(val message: String) : PollOptionsState()
}

sealed class PollResultState {
    object Idle : PollResultState()
    object Loading : PollResultState()
    data class Success(val result: Map<String, List<String>>) : PollResultState()
    data class Error(val message: String) : PollResultState()
}


class PollViewModel(
    private val repo: PollRepository = PollRepository()
) : ViewModel() {

    private fun getToken() = TokenProvider.accessToken ?: ""



    private val _pollState = MutableStateFlow<PollState>(PollState.Idle)
    val pollState = _pollState.asStateFlow()

    fun loadPolls() {
        viewModelScope.launch {
            try {
                _pollState.value = PollState.Loading

                val polls = repo.getAllPolls(
                    token = getToken(),
                    offset = 0,
                    limit = 100     // ← 여기 핵심
                )

                _pollState.value = PollState.Success(polls)

            } catch (e: Exception) {
                _pollState.value = PollState.Error("투표 목록 불러오기 오류: ${e.message}")
            }
        }
    }



    private val _optionsState = MutableStateFlow<PollOptionsState>(PollOptionsState.Idle)
    val optionsState = _optionsState.asStateFlow()

    private val optionTextMap = mutableMapOf<String, String>()

    fun loadOptions(pollId: String) {
        viewModelScope.launch {
            try {
                _optionsState.value = PollOptionsState.Loading
                val options = repo.getPollOptions(getToken(), pollId)

                // 🔥 옵션ID → 텍스트 저장
                optionTextMap.clear()
                options.forEach { opt ->
                    optionTextMap[opt.id] = opt.option_text
                }

                _optionsState.value = PollOptionsState.Success(options)
            } catch (e: Exception) {
                _optionsState.value = PollOptionsState.Error("옵션 불러오기 오류: ${e.message}")
            }
        }
    }

    fun deletePoll(
        pollId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val token = TokenProvider.accessToken ?: ""
                println("🔥 deletePoll() 호출됨: pollId=$pollId")

                val res = repo.deletePoll(token, pollId)
                println("🔥 응답코드 = ${res.code()}")

                if (res.isSuccessful) {
                    _pollState.value = PollState.Idle   // 🔥 리컴포즈 강제 유도
                    loadPolls()
                    onSuccess()

                } else {
                    val errorMsg = res.errorBody()?.string() ?: "Unknown Error"
                    println("🔥 삭제 실패 Body = $errorMsg")
                    onError("삭제 실패: ${res.code()} / $errorMsg")
                }

            } catch (e: Exception) {
                println("🔥 삭제 중 Exception = ${e.message}")
                onError("삭제 오류: ${e.message}")
            }
        }
    }


    fun vote(pollId: String, optionIds: List<String>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val token = TokenProvider.accessToken
                if (token.isNullOrEmpty()) return@launch

                repo.vote(
                    token = token,
                    pollId = pollId,
                    optionIds = optionIds
                )

                onSuccess()

            } catch (e: Exception) {
                println("❌ Vote error: ${e.message}")
            }
        }
    }


    val voterNameMap = mutableMapOf<String, String>()

    suspend fun loadUserName(userId: String): String {
        return voterNameMap[userId] ?: run {
            val user = repo.getUser(getToken(), userId)
            voterNameMap[userId] = user.nickname
            user.nickname
        }
    }


    private val _resultState = MutableStateFlow<PollResultState>(PollResultState.Idle)
    val resultState = _resultState.asStateFlow()

    fun loadResults(pollId: String) {
        viewModelScope.launch {
            try {
                _resultState.value = PollResultState.Loading

                val token = getToken()

                // 옵션 불러오기 → optionId → optionText
                val options = repo.getPollOptions(token, pollId)
                val optionMap = options.associate { it.id to it.option_text }

                // poll 정보 불러오기 → isAnonymous 확인
                val polls = repo.getAllPolls(token)
                val poll = polls.find { it.id == pollId }
                val isAnonymous = poll?.is_anonymous ?: false

                // 투표 결과 불러오기
                val raw = repo.getPollResults(token, pollId)

                // Number → emptyList 변환
                val cleanResults = raw.mapValues { (_, v) ->
                    when (v) {
                        is Number -> emptyList()
                        is List<*> -> v.filterIsInstance<String>()
                        else -> emptyList()
                    }
                }

                // userId → nickname
                val userMap = mutableMapOf<String, String>()
                cleanResults.values.flatten().forEach { userId ->
                    val user = repo.getUser(token, userId)
                    userMap[userId] = user.nickname
                }



            } catch (e: Exception) {
                _resultState.value = PollResultState.Error(e.message ?: "Unknown Error")
            }
        }
    }

}


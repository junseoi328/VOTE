package com.example.myapplication.data.poll

import com.example.myapplication.data.RetrofitClient

class PollRepository(
    private val api: PollApi = RetrofitClient.pollApi
) {

    // Poll List
    suspend fun getAllPolls(token: String, offset: Int = 0, limit: Int = 100) =
        api.getAllPolls("Bearer $token", offset, limit)


    // Poll Options
    suspend fun getPollOptions(token: String, pollId: String) =
        api.getPollOptions("Bearer $token", pollId)


    // Vote
    suspend fun vote(token: String, pollId: String, optionIds: List<String>) =
        api.vote(
            token = "Bearer $token",
            pollId = pollId,
            optionIds = optionIds
        )


    // Poll Result
    suspend fun getPollResults(token: String, pollId: String) =
        api.getPollResults("Bearer $token", pollId)

    suspend fun deletePoll(token: String, pollId: String) =
        api.deletePoll("Bearer $token", pollId)

    suspend fun getUser(token: String, userId: String) =
        api.getUser("Bearer $token", userId)
}



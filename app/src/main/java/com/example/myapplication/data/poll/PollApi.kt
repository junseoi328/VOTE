package com.example.myapplication.data.poll

import retrofit2.Response
import retrofit2.http.*

interface PollApi {
    @GET("polls/")
    suspend fun getAllPolls(
        @Header("Authorization") token: String,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 100   // 원하는 만큼
    ): List<Poll>

    @GET("polls/{poll_id}/options/")
    suspend fun getPollOptions(
        @Header("Authorization") token: String,
        @Path("poll_id") pollId: String
    ): List<OptionResponse>

    @POST("polls/{poll_id}/vote/")
    suspend fun vote(
        @Header("Authorization") token: String,
        @Path("poll_id") pollId: String,
        @Body optionIds: List<String>
    ): Response<Unit>

    @GET("polls/{poll_id}/results/")
    suspend fun getPollResults(
        @Header("Authorization") token: String,
        @Path("poll_id") pollId: String
    ): Map<String, Any>


    @GET("user/{user_id}")
    suspend fun getUser(
        @Header("Authorization") token: String,
        @Path("user_id") userId: String
    ): UserResponse   // username + nickname 정보

    @DELETE("polls/{poll_id}/")
    suspend fun deletePoll(
        @Header("Authorization") token: String,
        @Path("poll_id") pollId: String
    ): Response<Unit>

}









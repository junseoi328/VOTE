package com.example.myapplication.data

import com.example.myapplication.data.api.UserApi
import com.example.myapplication.data.poll.PollApi
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.myapplication.data.poll.AuthApi

object RetrofitClient {
    private val authInterceptor = Interceptor { chain ->
        val req = chain.request()
        val path = req.url.encodedPath

        val builder = req.newBuilder()
        builder.addHeader("Accept", "application/json")

        chain.proceed(builder.build())
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }


    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val req = chain.request().newBuilder()
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .build()
            chain.proceed(req)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://vote.sparcs.me/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    val pollApi: PollApi = retrofit.create(PollApi::class.java)
    val userApi: UserApi = retrofit.create(UserApi::class.java)

    val authApi: AuthApi = retrofit.create(AuthApi::class.java)

}

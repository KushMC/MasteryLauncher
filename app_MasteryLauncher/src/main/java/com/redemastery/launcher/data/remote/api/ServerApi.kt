package com.redemastery.launcher.data.remote.api

import com.redemastery.launcher.data.remote.dto.ServerStatusResponse
import com.redemastery.launcher.di.network.NetworkModule
import retrofit2.Response
import retrofit2.http.GET

interface ServerApi {
    @GET("status/java/jogar.redemastery.com")
    suspend fun getServerStatus(): Response<ServerStatusResponse>
}
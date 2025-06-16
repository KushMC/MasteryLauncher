package com.redemastery.launcher.data.remote.api

import com.redemastery.launcher.data.remote.dto.UpdateInfoResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface RedeMasteryApi {

    @GET("colocateujson/chorao/")
    suspend fun getUpdaterInfo(): Response<UpdateInfoResponse>

    @GET
    @Streaming
    suspend fun download(@Url fileUrl: String): Response<ResponseBody>

}
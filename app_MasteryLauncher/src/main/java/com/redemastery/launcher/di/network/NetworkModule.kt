package com.redemastery.launcher.di.network

import com.redemastery.launcher.data.remote.api.RedeMasteryApi
import com.redemastery.launcher.data.remote.api.ServerApi
import com.redemastery.launcher.di.network.qualifier.MCStatusAPI
import com.redemastery.launcher.di.network.qualifier.RedeMastery
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @MCStatusAPI
    fun provideMCStatusBaseUrl() = "https://api.mcstatus.io/"

    @Provides
    @RedeMastery
    fun provideRedeMasteryBaseUrl() = "https://drakonara.com/assets/"


    @Provides
    @Singleton
    @RedeMastery
    fun provideRedeMasterRetrofit(@RedeMastery baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    @MCStatusAPI
    fun provideMCStatusRetrofit(@MCStatusAPI baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @MCStatusAPI
    fun provideMCStatusApi(@MCStatusAPI retrofit: Retrofit): ServerApi = retrofit.create(ServerApi::class.java)

    @Provides
    @RedeMastery
    fun provideRedeMasterApi(@RedeMastery retrofit: Retrofit): RedeMasteryApi = retrofit.create(
        RedeMasteryApi::class.java)

}

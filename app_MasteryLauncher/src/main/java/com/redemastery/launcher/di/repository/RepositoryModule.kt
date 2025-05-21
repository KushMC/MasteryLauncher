package com.redemastery.launcher.di.repository

import android.app.Application
import android.content.Context
import com.redemastery.launcher.data.repository.LoginRepositoryImpl
import com.redemastery.launcher.data.repository.StorageRepositoryImpl
import com.redemastery.launcher.data.repository.UserAccountRepositoryImpl
import com.redemastery.launcher.domain.repository.login.LoginRepository
import com.redemastery.launcher.domain.repository.account.UserAccountRepository
import com.redemastery.launcher.domain.repository.storage.StorageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @Provides
    fun provideLoginRepository(
        @ApplicationContext context: Context
    ): LoginRepository = LoginRepositoryImpl(context)

    @Provides
    fun provideStorageRepository(
        @ApplicationContext context: Context
    ): StorageRepository = StorageRepositoryImpl(context)

}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModuleSingleton {

    @Provides
    @Singleton
    fun provideUserAccountRepository(
        @ApplicationContext context: Context
    ): UserAccountRepository = UserAccountRepositoryImpl(context)

}


package com.redemastery.launcher.di.usecase

import com.redemastery.launcher.domain.repository.login.LoginRepository
import com.redemastery.launcher.domain.repository.account.UserAccountRepository
import com.redemastery.launcher.domain.repository.storage.StorageRepository
import com.redemastery.launcher.domain.usecase.account.AccountUseCase
import com.redemastery.launcher.domain.usecase.account.GetUserAccountUseCase
import com.redemastery.launcher.domain.usecase.login.LoginPirateUseCase
import com.redemastery.launcher.domain.usecase.login.LoginUseCase
import com.redemastery.launcher.domain.usecase.storage.PrepareLauncherUseCase
import com.redemastery.launcher.domain.usecase.storage.StorageUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {


    @Provides
    @ViewModelScoped
    fun provideLoginUseCase(
        loginRepository: LoginRepository
    ): LoginUseCase = LoginUseCase(LoginPirateUseCase(loginRepository))

    @Provides
    @ViewModelScoped
    fun provideStorageUseCase(
        storageRepository: StorageRepository
    ): StorageUseCase = StorageUseCase(PrepareLauncherUseCase(storageRepository))

}

@Module
@InstallIn(SingletonComponent::class)
object UseCaseSingletonModule {

    @Provides
    @Singleton
    fun provideUserAccountUseCase(
        repository: UserAccountRepository
    ): AccountUseCase {
        return AccountUseCase(get = GetUserAccountUseCase(repository))
    }

}
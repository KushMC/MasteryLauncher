package com.choice.launcher.di.usecase

import com.choice.launcher.domain.repository.login.LoginRepository
import com.choice.launcher.domain.usecase.login.LoginPirateUseCase
import com.choice.launcher.domain.usecase.login.LoginUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {


    @Provides
    @ViewModelScoped
    fun provideLoginUseCase(
        loginRepository: LoginRepository
    ): LoginUseCase = LoginUseCase(LoginPirateUseCase(loginRepository))

}
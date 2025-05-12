package com.choice.launcher.di.repository

import com.choice.launcher.data.repository.LoginRepositoryImpl
import com.choice.launcher.domain.repository.login.LoginRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @Provides
    fun provideLoginRepository(): LoginRepository = LoginRepositoryImpl()

}


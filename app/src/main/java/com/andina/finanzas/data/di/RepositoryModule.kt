package com.andina.finanzas.data.di

import com.andina.finanzas.data.repository.FinanceRepositoryImpl
import com.andina.finanzas.data.repository.UserProfileRepositoryImpl
import com.andina.finanzas.domain.repository.FinanceRepository
import com.andina.finanzas.domain.repository.UserProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFinanceRepository(
        impl: FinanceRepositoryImpl
    ): FinanceRepository

    @Binds
    @Singleton
    abstract fun bindUserProfileRepository(
        impl: UserProfileRepositoryImpl
    ): UserProfileRepository
}

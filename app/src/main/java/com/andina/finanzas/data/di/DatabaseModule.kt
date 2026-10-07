package com.andina.finanzas.data.di

import android.content.Context
import com.andina.finanzas.data.local.AppDatabase
import com.andina.finanzas.data.local.dao.CategoryDao
import com.andina.finanzas.data.local.dao.LedgerDao
import com.andina.finanzas.data.local.dao.PocketDao
import com.andina.finanzas.data.local.dao.TransactionDao
import com.andina.finanzas.data.local.dao.WalletDao
import com.andina.finanzas.data.security.KeystoreHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        keystoreHelper: KeystoreHelper
    ): AppDatabase {
        val passphrase = keystoreHelper.getOrCreateDatabasePassphrase()
        return AppDatabase.buildDatabase(context, passphrase)
    }

    @Provides
    fun provideWalletDao(database: AppDatabase): WalletDao {
        return database.walletDao()
    }

    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    fun provideTransactionDao(database: AppDatabase): TransactionDao {
        return database.transactionDao()
    }

    @Provides
    fun providePocketDao(database: AppDatabase): PocketDao {
        return database.pocketDao()
    }

    @Provides
    fun provideLedgerDao(database: AppDatabase): LedgerDao {
        return database.ledgerDao()
    }
}

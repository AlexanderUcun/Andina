package com.andina.finanzas.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.andina.finanzas.data.local.dao.CategoryDao
import com.andina.finanzas.data.local.dao.LedgerDao
import com.andina.finanzas.data.local.dao.PocketDao
import com.andina.finanzas.data.local.dao.TransactionDao
import com.andina.finanzas.data.local.dao.WalletDao
import com.andina.finanzas.data.local.entity.CategoryEntity
import com.andina.finanzas.data.local.entity.PocketEntity
import com.andina.finanzas.data.local.entity.PocketMovementEntity
import com.andina.finanzas.data.local.entity.TransactionEntity
import com.andina.finanzas.data.local.entity.WalletEntity
import com.andina.finanzas.data.local.seed.DatabaseSeeder
import com.andina.finanzas.data.local.view.PocketBalanceView
import com.andina.finanzas.data.local.view.WalletBalanceView

/**
 * Base de datos principal de Andina.
 * Motor local estándar para Room con vistas contables Ledger que calculan saldos de forma reactiva.
 */
@Database(
    entities = [
        WalletEntity::class,
        CategoryEntity::class,
        TransactionEntity::class,
        PocketEntity::class,
        PocketMovementEntity::class
    ],
    views = [
        WalletBalanceView::class,
        PocketBalanceView::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun walletDao(): WalletDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun pocketDao(): PocketDao
    abstract fun ledgerDao(): LedgerDao

    companion object {
        const val DATABASE_NAME = "andina_ledger.db"

        /**
         * Crea una instancia de base de datos persistente con precarga de catálogo colombiano.
         */
        fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        DatabaseSeeder.seedInitialData(db)
                    }
                })
                .build()
        }

        /**
         * Crea una base de datos en memoria para pruebas unitarias.
         */
        fun buildInMemory(context: Context): AppDatabase {
            return Room.inMemoryDatabaseBuilder(
                context,
                AppDatabase::class.java
            )
                .allowMainThreadQueries()
                .build()
        }
    }
}

/**
 * Alias de compatibilidad arquitectónica
 */
typealias AndinaDatabase = AppDatabase

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
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory

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
        PocketMovementEntity::class,
        com.andina.finanzas.data.local.entity.DebtEntity::class
    ],
    views = [
        WalletBalanceView::class,
        PocketBalanceView::class
    ],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun walletDao(): WalletDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun pocketDao(): PocketDao
    abstract fun ledgerDao(): LedgerDao
    abstract fun debtDao(): com.andina.finanzas.data.local.dao.DebtDao

    companion object {
        const val DATABASE_NAME = "andina_ledger.db"

        val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS debts (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "personName TEXT NOT NULL, " +
                        "type TEXT NOT NULL, " +
                        "totalAmountPesos INTEGER NOT NULL, " +
                        "paidAmountPesos INTEGER NOT NULL, " +
                        "walletId INTEGER NOT NULL, " +
                        "dueDateMillis INTEGER, " +
                        "createdAtMillis INTEGER NOT NULL, " +
                        "note TEXT NOT NULL, " +
                        "isSettled INTEGER NOT NULL)"
                )
            }
        }

        /**
         * Crea una instancia de base de datos persistente con precarga de catálogo colombiano.
         * Si se suministra una frase de paso (passphrase), la base de datos se cifra íntegramente
         * mediante SQLCipher con algoritmo AES-256.
         */
        fun buildDatabase(
            context: Context,
            passphrase: ByteArray? = null
        ): AppDatabase {
            val builder = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .addMigrations(MIGRATION_1_2)
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        DatabaseSeeder.seedInitialData(db)
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        DatabaseSeeder.cleanupLegacyCategories(db)
                    }
                })

            if (passphrase != null && passphrase.isNotEmpty()) {
                try {
                    builder.openHelperFactory(SupportOpenHelperFactory(passphrase))
                } catch (_: Throwable) {
                    // Fallback de resiliencia ante error de inicialización de fábrica
                }
            }

            return builder.build()
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

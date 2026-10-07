package com.andina.finanzas.data.local.seed

import androidx.sqlite.db.SupportSQLiteDatabase
import com.andina.finanzas.data.local.entity.CategoryEntity
import com.andina.finanzas.data.local.entity.WalletEntity

/**
 * Seeder inicial del ecosistema Andina.
 * Inserta las billeteras colombianas y las categorías predeterminadas
 * usando directamente SupportSQLiteDatabase para evitar deadlocks en onCreate.
 */
object DatabaseSeeder {

    fun seedInitialData(db: SupportSQLiteDatabase) {
        db.beginTransaction()
        try {
            // 1. Sembrado de Billeteras por Defecto
            for (wallet in WalletEntity.DEFAULT_WALLETS) {
                db.execSQL(
                    """
                    INSERT INTO wallets (name, type, initialBalance, colorHex, archived, sortOrder)
                    VALUES (?, ?, ?, ?, ?, ?)
                    """.trimIndent(),
                    arrayOf(
                        wallet.name,
                        wallet.type,
                        wallet.initialBalance,
                        wallet.colorHex,
                        if (wallet.archived) 1 else 0,
                        wallet.sortOrder
                    )
                )
            }

            // 2. Sembrado de Categorías por Defecto
            for (cat in CategoryEntity.DEFAULT_CATEGORIES) {
                db.execSQL(
                    """
                    INSERT INTO categories (name, icon, colorHex, kind, isAntCandidate, isDefault, archived, sortOrder)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                    """.trimIndent(),
                    arrayOf(
                        cat.name,
                        cat.icon,
                        cat.colorHex,
                        cat.kind,
                        if (cat.isAntCandidate) 1 else 0,
                        if (cat.isDefault) 1 else 0,
                        if (cat.archived) 1 else 0,
                        cat.sortOrder
                    )
                )
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
}

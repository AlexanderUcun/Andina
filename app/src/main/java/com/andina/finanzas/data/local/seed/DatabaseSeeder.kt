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

    /**
     * Limpieza y sanitización idempotente al abrir la base de datos (Fase 1.2).
     * Reasigna cualquier transacción ligada a categorías residuales (ej. "Mujeres")
     * a la categoría estándar "Cuidado Personal" y purga el registro obsoleto.
     */
    fun cleanupLegacyCategories(db: SupportSQLiteDatabase) {
        db.beginTransaction()
        try {
            // 1. Obtener ID de la categoría destino "Cuidado Personal"
            val cursorTarget = db.query("SELECT id FROM categories WHERE name = 'Cuidado Personal' LIMIT 1")
            var targetId: Long? = null
            if (cursorTarget.moveToFirst()) {
                targetId = cursorTarget.getLong(0)
            }
            cursorTarget.close()

            // 2. Si existe la categoría residual "Mujeres", reasignar transacciones y eliminarla
            val cursorLegacy = db.query("SELECT id FROM categories WHERE name = 'Mujeres'")
            val legacyIds = mutableListOf<Long>()
            while (cursorLegacy.moveToNext()) {
                legacyIds.add(cursorLegacy.getLong(0))
            }
            cursorLegacy.close()

            if (legacyIds.isNotEmpty()) {
                for (legacyId in legacyIds) {
                    if (targetId != null) {
                        db.execSQL("UPDATE transactions SET categoryId = ? WHERE categoryId = ?", arrayOf(targetId, legacyId))
                    } else {
                        db.execSQL("UPDATE transactions SET categoryId = NULL WHERE categoryId = ?", arrayOf(legacyId))
                    }
                    db.execSQL("DELETE FROM categories WHERE id = ?", arrayOf(legacyId))
                }
            }

            db.setTransactionSuccessful()
        } catch (_: Throwable) {
            // Resiliencia silenciosa para no bloquear el inicio de la app
        } finally {
            db.endTransaction()
        }
    }
}

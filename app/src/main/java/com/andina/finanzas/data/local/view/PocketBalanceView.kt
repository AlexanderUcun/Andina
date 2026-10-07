package com.andina.finanzas.data.local.view

import androidx.room.DatabaseView

/**
 * Vista contable para bolsillos de ahorro.
 * Calcula el ahorro acumulado sumando todos los aportes (+) y restando retiros (-)
 * registrados en la tabla pocket_movements.
 */
@DatabaseView(
    viewName = "pocket_balances",
    value = """
        SELECT p.id AS pocketId,
          COALESCE((SELECT SUM(amount) FROM pocket_movements WHERE pocketId = p.id), 0) AS currentBalance
        FROM pockets p WHERE p.archived = 0
    """
)
data class PocketBalanceView(
    val pocketId: Long,
    val currentBalance: Long
)

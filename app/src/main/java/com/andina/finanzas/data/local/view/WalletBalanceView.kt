package com.andina.finanzas.data.local.view

import androidx.room.DatabaseView

/**
 * Vista contable (Ledger) que calcula el saldo disponible de cada billetera
 * de forma dinámica y reactiva en SQLite:
 *
 * Saldo = SaldoInicial
 *         + Ingresos
 *         - Gastos
 *         - Transferencias salientes
 *         + Transferencias entrantes
 *         - Aportes netos a bolsillos de ahorro (pocket_movements)
 *
 * Como Room detecta cambios en las tablas subyacentes (wallets, transactions, pocket_movements),
 * cualquier Flow conectado a esta vista emitirá el saldo actualizado al instante.
 */
@DatabaseView(
    viewName = "wallet_balances",
    value = """
        SELECT w.id AS walletId,
          w.initialBalance
          + COALESCE((SELECT SUM(amount) FROM transactions WHERE walletId = w.id AND type = 'INGRESO'), 0)
          - COALESCE((SELECT SUM(amount) FROM transactions WHERE walletId = w.id AND type = 'GASTO'), 0)
          - COALESCE((SELECT SUM(amount) FROM transactions WHERE walletId = w.id AND type = 'TRANSFERENCIA'), 0)
          + COALESCE((SELECT SUM(amount) FROM transactions WHERE toWalletId = w.id AND type = 'TRANSFERENCIA'), 0)
          - COALESCE((SELECT SUM(amount) FROM pocket_movements WHERE walletId = w.id), 0)
          AS balance
        FROM wallets w WHERE w.archived = 0
    """
)
data class WalletBalanceView(
    val walletId: Long,
    val balance: Long
)

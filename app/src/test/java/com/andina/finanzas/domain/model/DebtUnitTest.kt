package com.andina.finanzas.domain.model

import com.andina.finanzas.ui.wallets.DebtFilter
import com.andina.finanzas.ui.wallets.WalletsUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas unitarias para el modelo y lógica contable del Módulo de Deudas y Préstamos (Fase 4 y 5).
 */
class DebtUnitTest {

    @Test
    fun remainingAmount_isCalculatedCorrectly() {
        val debt = Debt(
            id = 1L,
            personName = "Carlos",
            type = DebtType.RECEIVABLE,
            totalAmount = Money(500_000L),
            paidAmount = Money(200_000L),
            walletId = 1L
        )

        assertEquals(300_000L, debt.remainingAmount.pesos)
        assertEquals(0.40f, debt.progressFraction, 0.001f)
        assertFalse(debt.isSettled)
    }

    @Test
    fun remainingAmount_whenFullyPaid_isZeroAndSettled() {
        val debt = Debt(
            id = 2L,
            personName = "Maria",
            type = DebtType.PAYABLE,
            totalAmount = Money(150_000L),
            paidAmount = Money(150_000L),
            walletId = 2L,
            isSettled = true
        )

        assertEquals(0L, debt.remainingAmount.pesos)
        assertEquals(1.0f, debt.progressFraction, 0.001f)
        assertTrue(debt.isSettled)
    }

    @Test
    fun progressFraction_whenTotalIsZero_returnsZero() {
        val debt = Debt(
            id = 3L,
            personName = "Test",
            type = DebtType.RECEIVABLE,
            totalAmount = Money(0L),
            paidAmount = Money(0L),
            walletId = 1L
        )

        assertEquals(0f, debt.progressFraction, 0.001f)
    }

    @Test
    fun walletsUiState_debtAggregationsAndFilters_areAccurate() {
        val debts = listOf(
            Debt(
                id = 1L,
                personName = "Juan",
                type = DebtType.RECEIVABLE,
                totalAmount = Money(300_000L),
                paidAmount = Money(100_000L), // Remaining: 200.000
                walletId = 1L
            ),
            Debt(
                id = 2L,
                personName = "Luisa",
                type = DebtType.RECEIVABLE,
                totalAmount = Money(500_000L),
                paidAmount = Money(0L), // Remaining: 500.000
                walletId = 1L
            ),
            Debt(
                id = 3L,
                personName = "Banco",
                type = DebtType.PAYABLE,
                totalAmount = Money(400_000L),
                paidAmount = Money(100_000L), // Remaining: 300.000
                walletId = 2L
            ),
            Debt(
                id = 4L,
                personName = "Pedro",
                type = DebtType.RECEIVABLE,
                totalAmount = Money(100_000L),
                paidAmount = Money(100_000L),
                walletId = 1L,
                isSettled = true // Saldada -> No cuenta en activo
            )
        )

        val state = WalletsUiState(debts = debts)

        // Total Receivable: Juan (200.000) + Luisa (500.000) = 700.000
        assertEquals(700_000L, state.totalReceivablePesos)
        // Total Payable: Banco (300.000) = 300.000
        assertEquals(300_000L, state.totalPayablePesos)
        // Balance Neto: 700.000 - 300.000 = +400.000
        assertEquals(400_000L, state.netDebtBalancePesos)

        // Filtro TODAS -> 4
        assertEquals(4, state.filteredDebts.size)

        // Filtro RECEIVABLE -> 2 (activas)
        val receivableState = state.copy(debtFilter = DebtFilter.RECEIVABLE)
        assertEquals(2, receivableState.filteredDebts.size)

        // Filtro PAYABLE -> 1 (activa)
        val payableState = state.copy(debtFilter = DebtFilter.PAYABLE)
        assertEquals(1, payableState.filteredDebts.size)

        // Filtro SALDADAS -> 1
        val settledState = state.copy(debtFilter = DebtFilter.SALDADAS)
        assertEquals(1, settledState.filteredDebts.size)
        assertEquals("Pedro", settledState.filteredDebts.first().personName)
    }
}

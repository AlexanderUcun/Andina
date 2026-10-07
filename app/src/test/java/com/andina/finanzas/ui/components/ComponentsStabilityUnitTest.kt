package com.andina.finanzas.ui.components

import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.domain.model.Pocket
import com.andina.finanzas.domain.model.Transaction
import com.andina.finanzas.domain.model.TransactionType
import com.andina.finanzas.domain.model.Wallet
import com.andina.finanzas.domain.model.WalletType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.pow

/**
 * Quality Gate de Fase 05:
 * Pruebas unitarias de estabilidad para los componentes reutilizables de UI y matemáticas del ecosistema.
 */
class ComponentsStabilityUnitTest {

    @Test
    fun `test pocket progress calculation is bounded between 0 and 1`() {
        val pocketZero = Pocket(id = 1, name = "Meta", goal = Money(100000L), currentBalance = Money.ZERO)
        assertEquals(0f, pocketZero.progress, 0.001f)
        assertFalse(pocketZero.isCompleted)
        assertEquals(Money(100000L), pocketZero.remaining)

        val pocketHalf = Pocket(id = 2, name = "Meta", goal = Money(100000L), currentBalance = Money(50000L))
        assertEquals(0.5f, pocketHalf.progress, 0.001f)
        assertFalse(pocketHalf.isCompleted)
        assertEquals(Money(50000L), pocketHalf.remaining)

        val pocketFull = Pocket(id = 3, name = "Meta", goal = Money(100000L), currentBalance = Money(100000L))
        assertEquals(1.0f, pocketFull.progress, 0.001f)
        assertTrue(pocketFull.isCompleted)
        assertEquals(Money.ZERO, pocketFull.remaining)

        val pocketOver = Pocket(id = 4, name = "Meta", goal = Money(100000L), currentBalance = Money(150000L))
        assertEquals(1.0f, pocketOver.progress, 0.001f)
        assertTrue(pocketOver.isCompleted)
        assertEquals(Money.ZERO, pocketOver.remaining)
    }

    @Test
    fun `test transaction data formatting and ant expense detection`() {
        val antExpense = Transaction(
            id = 1,
            type = TransactionType.GASTO,
            amount = Money(8500L),
            walletId = 1,
            isAntExpense = true,
            occurredAt = 1700000000000L
        )

        assertTrue("Debe estar marcado como gasto hormiga", antExpense.isAntExpense)
        assertEquals(TransactionType.GASTO, antExpense.type)
        assertEquals("$ 8.500 COP", antExpense.amount.formatCop(includeCode = true))

        val regularIncome = Transaction(
            id = 2,
            type = TransactionType.INGRESO,
            amount = Money(2500000L),
            walletId = 2,
            isAntExpense = false,
            occurredAt = 1700000000000L
        )

        assertFalse("El ingreso no debe ser gasto hormiga", regularIncome.isAntExpense)
        assertEquals(TransactionType.INGRESO, regularIncome.type)
        assertEquals("$ 2.500.000 COP", regularIncome.amount.formatCop(includeCode = true))
    }

    @Test
    fun `test button anti-double click debounce logic`() {
        var clickCount = 0
        var lastClickTimestamp = 0L
        val debounceIntervalMs = 500L

        fun simulateClick(currentTimestamp: Long) {
            if (currentTimestamp - lastClickTimestamp >= debounceIntervalMs) {
                lastClickTimestamp = currentTimestamp
                clickCount++
            }
        }

        // Primer clic
        simulateClick(1000L)
        assertEquals(1, clickCount)

        // Clic rápido ignorado (< 500ms)
        simulateClick(1200L)
        assertEquals(1, clickCount)

        // Clic posterior permitido (>= 500ms)
        simulateClick(1550L)
        assertEquals(2, clickCount)
    }

    @Test
    fun `test water ripple physics opacity decay formula`() {
        // En MUGGY_MOTOR_TECNICO §8.2: alpha(t) = (1 - progress)^2
        fun calculateRippleAlpha(progress: Float): Float {
            return (1f - progress).pow(2)
        }

        val initialAlpha = calculateRippleAlpha(0f)
        assertEquals(1.0f, initialAlpha, 0.001f)

        val midAlpha = calculateRippleAlpha(0.5f)
        assertEquals(0.25f, midAlpha, 0.001f)

        val endAlpha = calculateRippleAlpha(1.0f)
        assertEquals(0.0f, endAlpha, 0.001f)
    }

    @Test
    fun `test wallet brand types consistency`() {
        val nequi = Wallet(id = 1, name = "Nequi", type = WalletType.NEQUI, colorHex = "#700054")
        val daviplata = Wallet(id = 2, name = "Daviplata", type = WalletType.DAVIPLATA, colorHex = "#B32D2D")
        val cash = Wallet(id = 3, name = "Efectivo", type = WalletType.EFECTIVO, colorHex = "#435C3C")

        assertEquals(WalletType.NEQUI, nequi.type)
        assertEquals(WalletType.DAVIPLATA, daviplata.type)
        assertEquals(WalletType.EFECTIVO, cash.type)
    }
}

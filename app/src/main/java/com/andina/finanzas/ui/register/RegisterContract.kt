package com.andina.finanzas.ui.register

import androidx.compose.runtime.Immutable
import com.andina.finanzas.domain.model.Category
import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.domain.model.TransactionType
import com.andina.finanzas.domain.model.Wallet

/**
 * Estado inmutable de la pantalla / modal de Registro Rápido.
 * Diseñado bajo el patrón MVI reactivo.
 */
@Immutable
data class RegisterUiState(
    val type: TransactionType = TransactionType.GASTO,
    val amountDigits: String = "",
    val walletId: Long? = null,
    val toWalletId: Long? = null,
    val categoryId: Long? = null,
    val note: String = "",
    val occurredAt: Long = System.currentTimeMillis(),
    val isAntOverride: Boolean? = null,
    val isAntCandidate: Boolean = false,
    val wallets: List<Wallet> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedWallet: Wallet? = null,
    val selectedCategory: Category? = null,
    val toWallet: Wallet? = null,
    val amountError: String? = null,
    val isSaving: Boolean = false,
    val lastPressedDigit: Int? = null,
    val receiptUri: String? = null,
    val isLoading: Boolean = false
) {
    /**
     * Monto monetario encapsulado en el Value Object [Money].
     */
    val amountMoney: Money
        get() = Money(amountDigits.toLongOrNull() ?: 0L)

    /**
     * Monto formateado para la visualización Hero (ej. "$ 14.500 COP").
     */
    val amountFormatted: String
        get() = if (amountDigits.isEmpty()) "$ 0 COP" else amountMoney.formatCop(includeCode = true)

    /**
     * Representación verbal en palabras para accesibilidad y confirmación visual.
     * Ejemplo: "Cincuenta mil pesos".
     */
    val amountInWords: String
        get() = if (amountDigits.isEmpty()) "" else SpanishMoneyWords.toWords(amountMoney.pesos)

    /**
     * Determina si el movimiento califica como gasto hormiga (heurística o decisión del usuario).
     */
    val isAntExpense: Boolean
        get() = isAntOverride ?: (type == TransactionType.GASTO && isAntCandidate && amountMoney.pesos in 1L..25_000L)

    /**
     * Saldo proyectado en la billetera seleccionada una vez se confirme la operación.
     * Permite ver en vivo: "Te quedarán $45.000 en Nequi".
     */
    val projectedRemainingBalance: Money?
        get() {
            val wallet = selectedWallet ?: return null
            return when (type) {
                TransactionType.GASTO -> wallet.currentBalance - amountMoney
                TransactionType.INGRESO -> wallet.currentBalance + amountMoney
                TransactionType.TRANSFERENCIA -> wallet.currentBalance - amountMoney
            }
        }

    /**
     * Valida si el formulario cumple todas las condiciones para ser guardado.
     */
    val canSave: Boolean
        get() {
            val amount = amountDigits.toLongOrNull() ?: 0L
            if (amount <= 0L) return false
            if (walletId == null) return false
            if (isSaving) return false
            if (type == TransactionType.TRANSFERENCIA) {
                if (toWalletId == null || toWalletId == walletId) return false
            }
            return true
        }
}

/**
 * Acciones / intenciones del usuario en el flujo de Registro.
 */
sealed interface RegisterAction {
    data class TypeChanged(val type: TransactionType) : RegisterAction
    data class DigitPressed(val digit: Char) : RegisterAction
    data class AmountDigitsChanged(val digits: String) : RegisterAction
    data object BackspacePressed : RegisterAction
    data object ClearAmount : RegisterAction
    data class AddQuickAmount(val addition: Long) : RegisterAction
    data class WalletSelected(val walletId: Long) : RegisterAction
    data class ToWalletSelected(val walletId: Long) : RegisterAction
    data class CategorySelected(val categoryId: Long) : RegisterAction
    data class NoteChanged(val note: String) : RegisterAction
    data class DateChanged(val timestamp: Long) : RegisterAction
    data object ToggleAntExpense : RegisterAction
    data class ReceiptAttached(val uri: String?) : RegisterAction
    data object Save : RegisterAction
}

/**
 * Efectos secundarios únicos (One-shot side-effects) emitidos por el ViewModel.
 */
sealed interface RegisterEffect {
    data class Saved(val txId: Long, val message: String = "Movimiento guardado") : RegisterEffect
    data class Error(val message: String) : RegisterEffect
    data object HapticTick : RegisterEffect
}

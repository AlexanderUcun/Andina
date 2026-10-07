package com.andina.finanzas.ui.pockets

import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.domain.model.Pocket
import com.andina.finanzas.domain.model.Wallet

/**
 * Tipo de movimiento financiero sobre un bolsillo o meta de ahorro.
 */
enum class PocketMoveType {
    CONTRIBUTION, // Billetera -> Bolsillo (Aporte / Reserva)
    WITHDRAWAL    // Bolsillo -> Billetera (Retiro / Desahorro)
}

/**
 * Estado inmutable de la pantalla de Bolsillos y Metas de Ahorro.
 */
data class PocketsUiState(
    val isLoading: Boolean = false,
    val pockets: List<Pocket> = emptyList(),
    val wallets: List<Wallet> = emptyList(),
    val totalReserved: Money = Money.ZERO,
    val totalGoals: Money = Money.ZERO,
    val overallProgress: Float = 0f,
    val selectedPocketId: Long? = null,
    val isMoveSheetVisible: Boolean = false,
    val moveType: PocketMoveType = PocketMoveType.CONTRIBUTION,
    val movePocketId: Long? = null,
    val moveWalletId: Long? = null,
    val moveAmountDigits: String = "",
    val isMoving: Boolean = false,
    val moveError: String? = null,
    val isCreateSheetVisible: Boolean = false,
    val celebrationPocket: Pocket? = null
) {
    val selectedPocket: Pocket?
        get() = pockets.find { it.id == (movePocketId ?: selectedPocketId) }

    val activePocketForDetail: Pocket?
        get() = pockets.find { it.id == selectedPocketId }

    val selectedWallet: Wallet?
        get() = wallets.find { it.id == moveWalletId }

    val moveAmount: Money
        get() = Money(moveAmountDigits.toLongOrNull() ?: 0L)

    val canExecuteMove: Boolean
        get() {
            if (isMoving) return false
            val amount = moveAmount
            if (!amount.isPositive) return false
            val targetPocket = pockets.find { it.id == movePocketId } ?: return false
            val targetWallet = wallets.find { it.id == moveWalletId } ?: return false

            return when (moveType) {
                PocketMoveType.CONTRIBUTION -> targetWallet.currentBalance.pesos >= amount.pesos
                PocketMoveType.WITHDRAWAL -> targetPocket.currentBalance.pesos >= amount.pesos
            }
        }
}

/**
 * Acciones que el usuario o la UI pueden emitir hacia el ViewModel.
 */
sealed interface PocketsAction {
    data class SelectPocket(val pocketId: Long) : PocketsAction
    data object ClearSelectedPocket : PocketsAction

    data class OpenContribute(val pocketId: Long) : PocketsAction
    data class OpenWithdraw(val pocketId: Long) : PocketsAction
    data object DismissMoveSheet : PocketsAction

    data class SelectMoveWallet(val walletId: Long) : PocketsAction
    data class SetMoveAmount(val amount: Long) : PocketsAction
    data class InputMoveDigit(val digit: Char) : PocketsAction
    data object DeleteMoveDigit : PocketsAction
    data object ClearMoveAmount : PocketsAction
    data object ExecuteMove : PocketsAction

    data object OpenCreatePocket : PocketsAction
    data object DismissCreatePocket : PocketsAction
    data class CreatePocket(
        val name: String,
        val goalAmount: Long,
        val icon: String = "savings",
        val colorHex: String = "#435C3C",
        val deadline: Long? = null
    ) : PocketsAction

    data object DismissCelebration : PocketsAction
    data object Refresh : PocketsAction
}

/**
 * Efectos de un solo disparo (Side-effects).
 */
sealed interface PocketsEffect {
    data class ShowSnackbar(val message: String) : PocketsEffect
    data object PlayCoinSound : PocketsEffect
    data class ShowCelebrationConfetti(val pocketName: String) : PocketsEffect
    data class NavigateToDetail(val pocketId: Long) : PocketsEffect
}

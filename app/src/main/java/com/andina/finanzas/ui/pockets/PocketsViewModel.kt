package com.andina.finanzas.ui.pockets

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.domain.model.Pocket
import com.andina.finanzas.domain.model.Wallet
import com.andina.finanzas.domain.repository.FinanceRepository
import com.andina.finanzas.domain.usecase.ContributeToPocketUseCase
import com.andina.finanzas.domain.usecase.WithdrawFromPocketUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel del módulo de Bolsillos y Metas de Ahorro Programado.
 *
 * Características:
 * 1. Reactividad continua a la base de datos Room para bolsillos y billeteras.
 * 2. Transacciones atómicas seguras mediante casos de uso de dominio.
 * 3. Detección automática del 100% de la meta para disparar celebración festiva andina.
 * 4. Resiliencia y digitación fluida con validaciones inmediatas en pantalla.
 */
@HiltViewModel
class PocketsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: FinanceRepository,
    private val contributeToPocketUseCase: ContributeToPocketUseCase,
    private val withdrawFromPocketUseCase: WithdrawFromPocketUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PocketsUiState(isLoading = true))
    val state: StateFlow<PocketsUiState> = _state.asStateFlow()

    private val _effects = Channel<PocketsEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        observeData()
    }

    private fun observeData() {
        combine(
            repository.observePockets(),
            repository.observeWallets()
        ) { pockets, wallets ->
            val totalReserved = Money(pockets.sumOf { it.currentBalance.pesos })
            val totalGoals = Money(pockets.sumOf { it.goal.pesos })
            val overallProgress = if (totalGoals.pesos > 0L) {
                (totalReserved.pesos.toFloat() / totalGoals.pesos.toFloat()).coerceIn(0f, 1f)
            } else 0f

            _state.update { current ->
                val fallbackWalletId = current.moveWalletId ?: wallets.firstOrNull()?.id
                current.copy(
                    isLoading = false,
                    pockets = pockets,
                    wallets = wallets,
                    totalReserved = totalReserved,
                    totalGoals = totalGoals,
                    overallProgress = overallProgress,
                    moveWalletId = fallbackWalletId
                )
            }
        }.launchIn(viewModelScope)
    }

    fun processAction(action: PocketsAction) {
        when (action) {
            is PocketsAction.SelectPocket -> {
                _state.update { it.copy(selectedPocketId = action.pocketId) }
                sendEffect(PocketsEffect.NavigateToDetail(action.pocketId))
            }
            is PocketsAction.ClearSelectedPocket -> {
                _state.update { it.copy(selectedPocketId = null) }
            }
            is PocketsAction.OpenContribute -> {
                val pocket = _state.value.pockets.find { it.id == action.pocketId }
                val defaultWallet = _state.value.wallets.firstOrNull()?.id
                _state.update {
                    it.copy(
                        isMoveSheetVisible = true,
                        moveType = PocketMoveType.CONTRIBUTION,
                        movePocketId = action.pocketId,
                        moveWalletId = it.moveWalletId ?: defaultWallet,
                        moveAmountDigits = "",
                        moveError = null
                    )
                }
            }
            is PocketsAction.OpenWithdraw -> {
                val defaultWallet = _state.value.wallets.firstOrNull()?.id
                _state.update {
                    it.copy(
                        isMoveSheetVisible = true,
                        moveType = PocketMoveType.WITHDRAWAL,
                        movePocketId = action.pocketId,
                        moveWalletId = it.moveWalletId ?: defaultWallet,
                        moveAmountDigits = "",
                        moveError = null
                    )
                }
            }
            is PocketsAction.DismissMoveSheet -> {
                _state.update {
                    it.copy(
                        isMoveSheetVisible = false,
                        moveAmountDigits = "",
                        moveError = null,
                        isMoving = false
                    )
                }
            }
            is PocketsAction.SelectMoveWallet -> {
                _state.update { it.copy(moveWalletId = action.walletId, moveError = null) }
            }
            is PocketsAction.SetMoveAmount -> {
                val digits = if (action.amount > 0L) action.amount.toString() else ""
                _state.update { it.copy(moveAmountDigits = digits, moveError = null) }
            }
            is PocketsAction.InputMoveDigit -> {
                handleDigitInput(action.digit)
            }
            is PocketsAction.DeleteMoveDigit -> {
                _state.update {
                    val current = it.moveAmountDigits
                    val next = if (current.isNotEmpty()) current.dropLast(1) else ""
                    it.copy(moveAmountDigits = next, moveError = null)
                }
            }
            is PocketsAction.ClearMoveAmount -> {
                _state.update { it.copy(moveAmountDigits = "", moveError = null) }
            }
            is PocketsAction.ExecuteMove -> {
                executeMove()
            }
            is PocketsAction.OpenCreatePocket -> {
                _state.update { it.copy(isCreateSheetVisible = true) }
            }
            is PocketsAction.DismissCreatePocket -> {
                _state.update { it.copy(isCreateSheetVisible = false) }
            }
            is PocketsAction.CreatePocket -> {
                createPocket(action)
            }
            is PocketsAction.DismissCelebration -> {
                _state.update { it.copy(celebrationPocket = null) }
            }
            is PocketsAction.Refresh -> {
                // El flujo de Room reacciona automáticamente
            }
        }
    }

    private fun handleDigitInput(digit: Char) {
        if (!digit.isDigit()) return
        _state.update { current ->
            val prevDigits = current.moveAmountDigits
            if (prevDigits.length >= 11) return@update current // Máximo $99.999.999.999 COP
            val nextDigits = (prevDigits + digit).trimStart('0')
            current.copy(moveAmountDigits = nextDigits, moveError = null)
        }
    }

    private fun executeMove() {
        val currentState = _state.value
        val pocketId = currentState.movePocketId ?: return
        val walletId = currentState.moveWalletId ?: return
        val amount = currentState.moveAmount

        if (!amount.isPositive) {
            _state.update { it.copy(moveError = "Ingresa un monto mayor a cero") }
            return
        }

        val targetPocket = currentState.pockets.find { it.id == pocketId }
        val targetWallet = currentState.wallets.find { it.id == walletId }

        if (targetPocket == null || targetWallet == null) {
            _state.update { it.copy(moveError = "Bolsillo o billetera no encontrada") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isMoving = true, moveError = null) }

            when (currentState.moveType) {
                PocketMoveType.CONTRIBUTION -> {
                    if (targetWallet.currentBalance.pesos < amount.pesos) {
                        _state.update {
                            it.copy(
                                isMoving = false,
                                moveError = "Saldo insuficiente en ${targetWallet.name} (Tienes ${targetWallet.currentBalance.formatCop()})"
                            )
                        }
                        return@launch
                    }

                    val result = contributeToPocketUseCase(
                        pocketId = pocketId,
                        walletId = walletId,
                        amount = amount
                    )

                    result.fold(
                        onSuccess = {
                            val newBalance = targetPocket.currentBalance + amount
                            val justCompleted = !targetPocket.isCompleted && newBalance.pesos >= targetPocket.goal.pesos

                            _state.update {
                                it.copy(
                                    isMoving = false,
                                    isMoveSheetVisible = false,
                                    moveAmountDigits = "",
                                    celebrationPocket = if (justCompleted) targetPocket.copy(currentBalance = newBalance) else null
                                )
                            }
                            sendEffect(PocketsEffect.PlayCoinSound)
                            sendEffect(PocketsEffect.ShowSnackbar("¡Aporte de ${amount.formatCop()} a '${targetPocket.name}' exitoso!"))
                            if (justCompleted) {
                                sendEffect(PocketsEffect.ShowCelebrationConfetti(targetPocket.name))
                            }
                        },
                        onFailure = { error ->
                            _state.update {
                                it.copy(
                                    isMoving = false,
                                    moveError = error.message ?: "No se pudo realizar el aporte"
                                )
                            }
                        }
                    )
                }

                PocketMoveType.WITHDRAWAL -> {
                    if (targetPocket.currentBalance.pesos < amount.pesos) {
                        _state.update {
                            it.copy(
                                isMoving = false,
                                moveError = "Saldo insuficiente en ${targetPocket.name} (Tienes ${targetPocket.currentBalance.formatCop()})"
                            )
                        }
                        return@launch
                    }

                    val result = withdrawFromPocketUseCase(
                        pocketId = pocketId,
                        walletId = walletId,
                        amount = amount
                    )

                    result.fold(
                        onSuccess = {
                            _state.update {
                                it.copy(
                                    isMoving = false,
                                    isMoveSheetVisible = false,
                                    moveAmountDigits = ""
                                )
                            }
                            sendEffect(PocketsEffect.PlayCoinSound)
                            sendEffect(PocketsEffect.ShowSnackbar("¡Retiraste ${amount.formatCop()} de '${targetPocket.name}' a ${targetWallet.name}!"))
                        },
                        onFailure = { error ->
                            _state.update {
                                it.copy(
                                    isMoving = false,
                                    moveError = error.message ?: "No se pudo realizar el retiro"
                                )
                            }
                        }
                    )
                }
            }
        }
    }

    private fun createPocket(action: PocketsAction.CreatePocket) {
        if (action.name.isBlank()) {
            _state.update { it.copy(moveError = "El nombre de la meta es obligatorio") }
            return
        }
        if (action.goalAmount <= 0L) {
            _state.update { it.copy(moveError = "La meta debe ser mayor a $0 COP") }
            return
        }

        viewModelScope.launch {
            val newPocket = Pocket(
                id = 0,
                name = action.name.trim(),
                goal = Money(action.goalAmount),
                currentBalance = Money.ZERO,
                icon = action.icon,
                colorHex = action.colorHex,
                deadline = action.deadline
            )
            repository.createPocket(newPocket)
            _state.update { it.copy(isCreateSheetVisible = false) }
            sendEffect(PocketsEffect.ShowSnackbar("Meta '${action.name}' sembrada en tu páramo 🌿"))
        }
    }

    private fun sendEffect(effect: PocketsEffect) {
        viewModelScope.launch {
            _effects.send(effect)
        }
    }
}

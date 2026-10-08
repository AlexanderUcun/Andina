package com.andina.finanzas.ui.wallets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.domain.model.Wallet
import com.andina.finanzas.domain.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la gestión reactiva de billeteras y cuentas de Andina.
 */
@HiltViewModel
class WalletsViewModel @Inject constructor(
    private val repository: FinanceRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WalletsUiState())
    val state: StateFlow<WalletsUiState> = _state.asStateFlow()

    private val _effects = Channel<WalletsEffect>(Channel.BUFFERED)
    val effects: Flow<WalletsEffect> = _effects.receiveAsFlow()

    init {
        observeWallets()
    }

    private fun observeWallets() {
        viewModelScope.launch {
            repository.observeWallets().collect { walletList ->
                val total = walletList.sumOf { it.currentBalance.pesos }
                _state.update {
                    it.copy(
                        isLoading = false,
                        wallets = walletList,
                        totalBalance = total
                    )
                }
            }
        }
    }

    fun onEvent(event: WalletsEvent) {
        when (event) {
            is WalletsEvent.OnOpenAddWallet -> {
                _state.update { it.copy(showAddEditDialog = true, editingWallet = null) }
            }
            is WalletsEvent.OnOpenEditWallet -> {
                _state.update { it.copy(showAddEditDialog = true, editingWallet = event.wallet) }
            }
            is WalletsEvent.OnDismissDialog -> {
                _state.update { it.copy(showAddEditDialog = false, editingWallet = null) }
            }
            is WalletsEvent.OnSaveWallet -> {
                saveWallet(event)
            }
            is WalletsEvent.OnArchiveWallet -> {
                archiveWallet(event.walletId)
            }
        }
    }

    private fun saveWallet(event: WalletsEvent.OnSaveWallet) {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val currentEditing = _state.value.editingWallet

            if (currentEditing == null) {
                // Crear nueva billetera
                val newWallet = Wallet(
                    name = event.name.trim(),
                    type = event.type,
                    initialBalance = Money(event.initialBalancePesos),
                    currentBalance = Money(event.initialBalancePesos),
                    colorHex = event.colorHex
                )
                repository.createWallet(newWallet)
                _effects.send(WalletsEffect.ShowSnackbar("Billetera '${event.name}' creada exitosamente"))
            } else {
                // Actualizar billetera existente
                val updatedWallet = currentEditing.copy(
                    name = event.name.trim(),
                    type = event.type,
                    initialBalance = Money(event.initialBalancePesos),
                    colorHex = event.colorHex
                )
                repository.updateWallet(updatedWallet)
                _effects.send(WalletsEffect.ShowSnackbar("Billetera '${event.name}' actualizada"))
            }

            _state.update { it.copy(isSaving = false, showAddEditDialog = false, editingWallet = null) }
        }
    }

    private fun archiveWallet(walletId: Long) {
        viewModelScope.launch {
            repository.archiveWallet(walletId)
            _effects.send(WalletsEffect.ShowSnackbar("Billetera archivada"))
        }
    }
}

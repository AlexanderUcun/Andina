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
        observeArchivedWallets()
        observeDebts()
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

    private fun observeArchivedWallets() {
        viewModelScope.launch {
            repository.observeArchivedWallets().collect { archivedList ->
                _state.update {
                    it.copy(archivedWallets = archivedList)
                }
            }
        }
    }

    private fun observeDebts() {
        viewModelScope.launch {
            repository.observeDebts().collect { debtList ->
                _state.update {
                    it.copy(debts = debtList)
                }
            }
        }
    }

    fun onEvent(event: WalletsEvent) {
        when (event) {
            is WalletsEvent.OnSelectPatrimonioTab -> {
                _state.update { it.copy(selectedPatrimonioTab = event.tab) }
            }
            is WalletsEvent.OnSelectTab -> {
                _state.update { it.copy(selectedTab = event.tab) }
            }
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
            is WalletsEvent.OnUnarchiveWallet -> {
                unarchiveWallet(event.walletId)
            }
            is WalletsEvent.OnRequestDeleteWallet -> {
                _state.update { it.copy(walletToDelete = event.wallet) }
            }
            is WalletsEvent.OnDismissDeleteDialog -> {
                _state.update { it.copy(walletToDelete = null) }
            }
            is WalletsEvent.OnConfirmDeleteWallet -> {
                deleteWallet(event.walletId)
            }

            // Gestión de Deudas
            is WalletsEvent.OnSelectDebtFilter -> {
                _state.update { it.copy(debtFilter = event.filter) }
            }
            is WalletsEvent.OnOpenCreateDebt -> {
                _state.update { it.copy(showCreateDebtDialog = true) }
            }
            is WalletsEvent.OnDismissCreateDebt -> {
                _state.update { it.copy(showCreateDebtDialog = false) }
            }
            is WalletsEvent.OnCreateDebt -> {
                createDebt(event)
            }
            is WalletsEvent.OnOpenDebtPayment -> {
                _state.update { it.copy(debtForPayment = event.debt) }
            }
            is WalletsEvent.OnDismissDebtPayment -> {
                _state.update { it.copy(debtForPayment = null) }
            }
            is WalletsEvent.OnRegisterDebtPayment -> {
                registerDebtPayment(event)
            }
            is WalletsEvent.OnRequestDeleteDebt -> {
                _state.update { it.copy(debtToDelete = event.debt) }
            }
            is WalletsEvent.OnDismissDeleteDebtDialog -> {
                _state.update { it.copy(debtToDelete = null) }
            }
            is WalletsEvent.OnConfirmDeleteDebt -> {
                deleteDebt(event.debtId)
            }
        }
    }

    private fun createDebt(event: WalletsEvent.OnCreateDebt) {
        viewModelScope.launch {
            val newDebt = com.andina.finanzas.domain.model.Debt(
                personName = event.personName.trim(),
                type = event.type,
                totalAmount = Money(event.totalAmountPesos),
                walletId = event.walletId,
                dueDateMillis = event.dueDateMillis,
                note = event.note.trim()
            )
            val result = repository.createDebt(newDebt, event.affectWalletBalance)
            if (result.isSuccess) {
                _effects.send(WalletsEffect.ShowSnackbar("🤝 Registro guardado para ${event.personName}"))
            } else {
                _effects.send(WalletsEffect.ShowSnackbar("Error al guardar: ${result.exceptionOrNull()?.message}"))
            }
            _state.update { it.copy(showCreateDebtDialog = false) }
        }
    }

    private fun registerDebtPayment(event: WalletsEvent.OnRegisterDebtPayment) {
        viewModelScope.launch {
            val result = repository.registerDebtPayment(
                debtId = event.debtId,
                paymentAmount = Money(event.amountPesos),
                walletId = event.walletId,
                note = event.note.trim()
            )
            if (result.isSuccess) {
                _effects.send(WalletsEffect.ShowSnackbar("Abono de ${Money(event.amountPesos).formatCop()} registrado exitosamente"))
            } else {
                _effects.send(WalletsEffect.ShowSnackbar("Error al registrar abono: ${result.exceptionOrNull()?.message}"))
            }
            _state.update { it.copy(debtForPayment = null) }
        }
    }

    private fun deleteDebt(debtId: Long) {
        viewModelScope.launch {
            repository.deleteDebt(debtId)
            _state.update { it.copy(debtToDelete = null) }
            _effects.send(WalletsEffect.ShowSnackbar("Registro de deuda eliminado"))
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
            _effects.send(WalletsEffect.ShowSnackbar("Billetera archivada. Puedes verla en 'Archivadas'"))
        }
    }

    private fun unarchiveWallet(walletId: Long) {
        viewModelScope.launch {
            repository.unarchiveWallet(walletId)
            _effects.send(WalletsEffect.ShowSnackbar("Billetera restaurada a tus cuentas activas 🌿"))
        }
    }

    private fun deleteWallet(walletId: Long) {
        viewModelScope.launch {
            repository.deleteWallet(walletId)
            _state.update { it.copy(walletToDelete = null) }
            _effects.send(WalletsEffect.ShowSnackbar("Billetera eliminada definitivamente"))
        }
    }
}

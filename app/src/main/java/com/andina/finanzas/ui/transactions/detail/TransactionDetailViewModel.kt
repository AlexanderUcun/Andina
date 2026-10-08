package com.andina.finanzas.ui.transactions.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.andina.finanzas.domain.model.CategoryKind
import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.domain.model.Transaction
import com.andina.finanzas.domain.model.TransactionType
import com.andina.finanzas.domain.repository.FinanceRepository
import com.andina.finanzas.ui.navigation.TransactionDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

/**
 * ViewModel para visualizar, editar integralmente y auditar los detalles de un movimiento contable.
 */
@HiltViewModel
class TransactionDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: FinanceRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TransactionDetailUiState())
    val state: StateFlow<TransactionDetailUiState> = _state.asStateFlow()

    private val _effects = Channel<TransactionDetailEffect>(Channel.BUFFERED)
    val effects: Flow<TransactionDetailEffect> = _effects.receiveAsFlow()

    private var currentTransactionId: Long = 0L

    init {
        val extractedId = runCatching {
            savedStateHandle.toRoute<TransactionDetailRoute>().transactionId
        }.getOrNull() ?: savedStateHandle.get<Long>("transactionId")

        if (extractedId != null && extractedId > 0L) {
            loadTransaction(extractedId)
        }
        loadMetadata()
    }

    private fun loadMetadata() {
        viewModelScope.launch {
            repository.observeWallets().collect { wallets ->
                _state.update { it.copy(availableWallets = wallets) }
            }
        }
    }

    fun loadTransaction(transactionId: Long) {
        currentTransactionId = transactionId
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val tx = repository.getTransactionById(transactionId)
            if (tx != null) {
                val categoryKind = when (tx.type) {
                    TransactionType.INGRESO -> CategoryKind.INGRESO
                    TransactionType.GASTO -> CategoryKind.GASTO
                    TransactionType.TRANSFERENCIA -> null
                }
                val categories = repository.observeCategories(categoryKind).firstOrNull() ?: emptyList()

                _state.update {
                    it.copy(
                        isLoading = false,
                        transaction = tx,
                        editedAmountPesos = tx.amount.pesos,
                        editedAmountInput = tx.amount.pesos.toString(),
                        editedWalletId = tx.walletId,
                        editedToWalletId = tx.toWalletId,
                        editedCategoryId = tx.categoryId,
                        editedOccurredAt = tx.occurredAt,
                        editedNote = tx.note,
                        isAntExpense = tx.isAntExpense,
                        availableCategories = categories,
                        hasUnsavedChanges = false
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false) }
                _effects.send(TransactionDetailEffect.ShowSnackbar("No se encontró el movimiento"))
                _effects.send(TransactionDetailEffect.NavigateBack)
            }
        }
    }

    fun onEvent(event: TransactionDetailEvent) {
        when (event) {
            is TransactionDetailEvent.OnNoteChanged -> {
                _state.update {
                    val updated = it.copy(editedNote = event.newNote)
                    updated.copy(hasUnsavedChanges = checkHasChanges(updated))
                }
            }
            is TransactionDetailEvent.OnToggleAntExpense -> {
                _state.update {
                    val updated = it.copy(isAntExpense = event.isAnt)
                    updated.copy(hasUnsavedChanges = checkHasChanges(updated))
                }
            }
            is TransactionDetailEvent.OnOpenAmountDialog -> {
                _state.update {
                    it.copy(
                        showAmountEditDialog = true,
                        editedAmountInput = it.editedAmountPesos.toString()
                    )
                }
            }
            is TransactionDetailEvent.OnAmountInputChanged -> {
                val digitsOnly = event.rawDigits.filter { it.isDigit() }.take(8) // Max 8 dígitos ($99.999.999)
                _state.update { it.copy(editedAmountInput = digitsOnly) }
            }
            is TransactionDetailEvent.OnConfirmAmountEdit -> {
                val parsedPesos = _state.value.editedAmountInput.toLongOrNull() ?: _state.value.editedAmountPesos
                if (parsedPesos > 0) {
                    _state.update {
                        val updated = it.copy(
                            editedAmountPesos = parsedPesos,
                            showAmountEditDialog = false
                        )
                        updated.copy(hasUnsavedChanges = checkHasChanges(updated))
                    }
                } else {
                    _state.update { it.copy(showAmountEditDialog = false) }
                }
            }
            is TransactionDetailEvent.OnDismissAmountDialog -> {
                _state.update { it.copy(showAmountEditDialog = false) }
            }
            is TransactionDetailEvent.OnOpenWalletDialog -> {
                _state.update { it.copy(showWalletPickerDialog = true) }
            }
            is TransactionDetailEvent.OnSelectWallet -> {
                _state.update {
                    val updated = it.copy(
                        editedWalletId = event.walletId,
                        showWalletPickerDialog = false
                    )
                    updated.copy(hasUnsavedChanges = checkHasChanges(updated))
                }
            }
            is TransactionDetailEvent.OnDismissWalletDialog -> {
                _state.update { it.copy(showWalletPickerDialog = false) }
            }
            is TransactionDetailEvent.OnOpenToWalletDialog -> {
                _state.update { it.copy(showToWalletPickerDialog = true) }
            }
            is TransactionDetailEvent.OnSelectToWallet -> {
                _state.update {
                    val updated = it.copy(
                        editedToWalletId = event.walletId,
                        showToWalletPickerDialog = false
                    )
                    updated.copy(hasUnsavedChanges = checkHasChanges(updated))
                }
            }
            is TransactionDetailEvent.OnDismissToWalletDialog -> {
                _state.update { it.copy(showToWalletPickerDialog = false) }
            }
            is TransactionDetailEvent.OnOpenCategoryDialog -> {
                _state.update { it.copy(showCategoryPickerDialog = true) }
            }
            is TransactionDetailEvent.OnSelectCategory -> {
                _state.update {
                    val updated = it.copy(
                        editedCategoryId = event.categoryId,
                        showCategoryPickerDialog = false
                    )
                    updated.copy(hasUnsavedChanges = checkHasChanges(updated))
                }
            }
            is TransactionDetailEvent.OnDismissCategoryDialog -> {
                _state.update { it.copy(showCategoryPickerDialog = false) }
            }
            is TransactionDetailEvent.OnOpenDatePickerDialog -> {
                _state.update { it.copy(showDatePickerDialog = true) }
            }
            is TransactionDetailEvent.OnSelectDate -> {
                // Combinar la fecha seleccionada con la hora existente
                val calOriginal = Calendar.getInstance().apply { timeInMillis = _state.value.editedOccurredAt }
                val calNewDate = Calendar.getInstance().apply { timeInMillis = event.epochMillis }
                calNewDate.set(Calendar.HOUR_OF_DAY, calOriginal.get(Calendar.HOUR_OF_DAY))
                calNewDate.set(Calendar.MINUTE, calOriginal.get(Calendar.MINUTE))
                calNewDate.set(Calendar.SECOND, calOriginal.get(Calendar.SECOND))

                _state.update {
                    val updated = it.copy(
                        editedOccurredAt = calNewDate.timeInMillis,
                        showDatePickerDialog = false
                    )
                    updated.copy(hasUnsavedChanges = checkHasChanges(updated))
                }
            }
            is TransactionDetailEvent.OnDismissDatePickerDialog -> {
                _state.update { it.copy(showDatePickerDialog = false) }
            }
            is TransactionDetailEvent.OnOpenTimePickerDialog -> {
                _state.update { it.copy(showTimePickerDialog = true) }
            }
            is TransactionDetailEvent.OnSelectTime -> {
                val cal = Calendar.getInstance().apply { timeInMillis = _state.value.editedOccurredAt }
                cal.set(Calendar.HOUR_OF_DAY, event.hour)
                cal.set(Calendar.MINUTE, event.minute)

                _state.update {
                    val updated = it.copy(
                        editedOccurredAt = cal.timeInMillis,
                        showTimePickerDialog = false
                    )
                    updated.copy(hasUnsavedChanges = checkHasChanges(updated))
                }
            }
            is TransactionDetailEvent.OnDismissTimePickerDialog -> {
                _state.update { it.copy(showTimePickerDialog = false) }
            }
            is TransactionDetailEvent.OnSaveEdits -> {
                saveChanges()
            }
            is TransactionDetailEvent.OnRequestDelete -> {
                _state.update { it.copy(showDeleteConfirmDialog = true) }
            }
            is TransactionDetailEvent.OnDismissDeleteDialog -> {
                _state.update { it.copy(showDeleteConfirmDialog = false) }
            }
            is TransactionDetailEvent.OnConfirmDelete -> {
                deleteTransaction()
            }
        }
    }

    private fun checkHasChanges(s: TransactionDetailUiState): Boolean {
        val tx = s.transaction ?: return false
        return s.editedAmountPesos != tx.amount.pesos ||
                s.editedWalletId != tx.walletId ||
                s.editedToWalletId != tx.toWalletId ||
                s.editedCategoryId != tx.categoryId ||
                s.editedOccurredAt != tx.occurredAt ||
                s.editedNote.trim() != tx.note.trim() ||
                s.isAntExpense != tx.isAntExpense
    }

    private fun saveChanges() {
        val currentTx = _state.value.transaction ?: return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }

            val walletName = _state.value.availableWallets.find { it.id == _state.value.editedWalletId }?.name
            val toWalletName = _state.value.editedToWalletId?.let { toId ->
                _state.value.availableWallets.find { it.id == toId }?.name
            }
            val category = _state.value.editedCategoryId?.let { catId ->
                _state.value.availableCategories.find { it.id == catId }
            }

            val updated = currentTx.copy(
                amount = Money(_state.value.editedAmountPesos),
                walletId = _state.value.editedWalletId,
                toWalletId = _state.value.editedToWalletId,
                categoryId = _state.value.editedCategoryId,
                occurredAt = _state.value.editedOccurredAt,
                note = _state.value.editedNote.trim(),
                isAntExpense = _state.value.isAntExpense,
                walletName = walletName ?: currentTx.walletName,
                toWalletName = toWalletName ?: currentTx.toWalletName,
                categoryName = category?.name ?: currentTx.categoryName,
                categoryIcon = category?.icon ?: currentTx.categoryIcon,
                categoryColorHex = category?.colorHex ?: currentTx.categoryColorHex
            )

            repository.updateTransaction(updated)

            _state.update {
                it.copy(
                    isSaving = false,
                    transaction = updated,
                    hasUnsavedChanges = false
                )
            }
            _effects.send(TransactionDetailEffect.ShowSnackbar("Movimiento y saldos actualizados correctamente"))
        }
    }

    private fun deleteTransaction() {
        val currentTx = _state.value.transaction ?: return
        viewModelScope.launch {
            _state.update { it.copy(showDeleteConfirmDialog = false, isLoading = true) }
            repository.deleteTransaction(currentTx.id)
            _effects.send(TransactionDetailEffect.ShowSnackbar("Movimiento eliminado"))
            _effects.send(TransactionDetailEffect.NavigateBack)
        }
    }
}

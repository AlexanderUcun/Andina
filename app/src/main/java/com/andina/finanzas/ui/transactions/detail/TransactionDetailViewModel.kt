package com.andina.finanzas.ui.transactions.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.andina.finanzas.domain.repository.FinanceRepository
import com.andina.finanzas.ui.navigation.TransactionDetailRoute
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
 * ViewModel para visualizar, editar y auditar los detalles de un movimiento contable.
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
    }

    fun loadTransaction(transactionId: Long) {
        currentTransactionId = transactionId
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val tx = repository.getTransactionById(transactionId)
            if (tx != null) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        transaction = tx,
                        editedNote = tx.note,
                        isAntExpense = tx.isAntExpense,
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
                val originalNote = _state.value.transaction?.note.orEmpty()
                _state.update {
                    it.copy(
                        editedNote = event.newNote,
                        hasUnsavedChanges = event.newNote != originalNote || it.isAntExpense != (it.transaction?.isAntExpense == true)
                    )
                }
            }
            is TransactionDetailEvent.OnToggleAntExpense -> {
                val originalAnt = _state.value.transaction?.isAntExpense == true
                _state.update {
                    it.copy(
                        isAntExpense = event.isAnt,
                        hasUnsavedChanges = event.isAnt != originalAnt || it.editedNote != (it.transaction?.note.orEmpty())
                    )
                }
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

    private fun saveChanges() {
        val currentTx = _state.value.transaction ?: return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val updated = currentTx.copy(
                note = _state.value.editedNote.trim(),
                isAntExpense = _state.value.isAntExpense
            )
            repository.updateTransaction(updated)
            _state.update {
                it.copy(
                    isSaving = false,
                    transaction = updated,
                    hasUnsavedChanges = false
                )
            }
            _effects.send(TransactionDetailEffect.ShowSnackbar("Movimiento actualizado"))
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

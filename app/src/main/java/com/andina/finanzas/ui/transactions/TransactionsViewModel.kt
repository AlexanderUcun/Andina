package com.andina.finanzas.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.andina.finanzas.domain.model.Transaction
import com.andina.finanzas.domain.model.TransactionType
import com.andina.finanzas.domain.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel del Libro Mayor y Auditoría de Movimientos.
 *
 * Responsabilidades:
 * 1. Paginación de alto rendimiento con Paging 3 conectada a Room y filtrado reactivo.
 * 2. Filtrado multi-criterio (tipo, billetera/método, compras hormiga, rango de fechas).
 * 3. Eliminación resiliente con ventana de gracia "Deshacer" de 5 segundos.
 * 4. Navegación hacia el detalle contable individual.
 */
@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val repository: FinanceRepository
) : ViewModel() {

    private val _filter = MutableStateFlow(TransactionsFilter())
    val filter: StateFlow<TransactionsFilter> = _filter.asStateFlow()

    private val _state = MutableStateFlow(TransactionsUiState())
    val state: StateFlow<TransactionsUiState> = _state.asStateFlow()

    private val _effects = Channel<TransactionsEffect>(Channel.BUFFERED)
    val effects: Flow<TransactionsEffect> = _effects.receiveAsFlow()

    private var undoJob: Job? = null
    private var pendingDeletedTransaction: Transaction? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    val transactionsPagingFlow: Flow<PagingData<Transaction>> = _filter
        .flatMapLatest { filterCriteria ->
            val (from, to) = filterCriteria.resolveDateRangeBounds()
            repository.getTransactionsPaged(
                query = filterCriteria.query.trim(),
                type = filterCriteria.type,
                walletId = filterCriteria.walletId,
                categoryId = filterCriteria.categoryId,
                onlyAntExpense = filterCriteria.onlyAntExpense,
                fromMillis = from,
                toMillis = to
            )
        }
        .cachedIn(viewModelScope)

    init {
        observeAvailableFilters()
    }

    private fun observeAvailableFilters() {
        viewModelScope.launch {
            combine(
                repository.observeWallets(),
                repository.observeCategories(),
                _filter
            ) { wallets, categories, currentFilter ->
                Triple(wallets, categories, currentFilter)
            }.collect { (wallets, categories, currentFilter) ->
                _state.update { current ->
                    current.copy(
                        availableWallets = wallets,
                        availableCategories = categories,
                        filter = currentFilter
                    )
                }
            }
        }
    }

    fun onEvent(event: TransactionsEvent) {
        when (event) {
            is TransactionsEvent.OnQueryChanged -> {
                _filter.update { it.copy(query = event.query) }
            }
            is TransactionsEvent.OnTypeSelected -> {
                _filter.update { it.copy(type = event.type) }
            }
            is TransactionsEvent.OnWalletSelected -> {
                _filter.update { it.copy(walletId = event.walletId) }
            }
            is TransactionsEvent.OnCategorySelected -> {
                _filter.update { it.copy(categoryId = event.categoryId) }
            }
            is TransactionsEvent.OnToggleAntExpense -> {
                _filter.update { it.copy(onlyAntExpense = event.onlyAnt) }
            }
            is TransactionsEvent.OnDatePresetSelected -> {
                _filter.update {
                    it.copy(
                        datePreset = event.preset,
                        customFromMillis = null,
                        customToMillis = null
                    )
                }
            }
            is TransactionsEvent.OnCustomDateRangeSet -> {
                _filter.update {
                    it.copy(
                        datePreset = DateRangeFilterPreset.ALL,
                        customFromMillis = event.fromMillis,
                        customToMillis = event.toMillis
                    )
                }
            }
            is TransactionsEvent.OnResetFilters -> {
                _filter.value = TransactionsFilter()
            }
            is TransactionsEvent.OnSetFilterSheetVisible -> {
                _state.update { it.copy(isFilterSheetVisible = event.visible) }
            }
            is TransactionsEvent.OnDeleteTransaction -> {
                deleteTransactionWithUndo(event.transaction)
            }
            is TransactionsEvent.OnUndoDelete -> {
                undoDelete()
            }
            is TransactionsEvent.OnTransactionClick -> {
                viewModelScope.launch {
                    _effects.send(TransactionsEffect.NavigateToDetail(event.transactionId))
                }
            }
        }
    }

    private fun deleteTransactionWithUndo(transaction: Transaction) {
        undoJob?.cancel()
        pendingDeletedTransaction = transaction
        _state.update { it.copy(lastDeletedTransaction = transaction) }

        viewModelScope.launch {
            repository.deleteTransaction(transaction.id)
            val noteDescription = if (transaction.note.isNotBlank()) " \"${transaction.note}\"" else ""
            _effects.send(
                TransactionsEffect.ShowUndoSnackbar(
                    message = "Movimiento${noteDescription} eliminado",
                    actionLabel = "Deshacer"
                )
            )

            // Ventana de 5 segundos para mantener en memoria la posibilidad de restauración
            undoJob = launch {
                delay(5000L)
                pendingDeletedTransaction = null
                _state.update { it.copy(lastDeletedTransaction = null) }
            }
        }
    }

    private fun undoDelete() {
        val tx = pendingDeletedTransaction ?: return
        undoJob?.cancel()
        pendingDeletedTransaction = null
        _state.update { it.copy(lastDeletedTransaction = null) }

        viewModelScope.launch {
            repository.restoreTransaction(tx)
            _effects.send(TransactionsEffect.ShowMessage("Movimiento restaurado exitosamente"))
        }
    }
}

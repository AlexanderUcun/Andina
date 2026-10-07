package com.andina.finanzas.ui.transactions.detail

import com.andina.finanzas.domain.model.Transaction

/**
 * Estado de la UI para la pantalla de detalle y edición de movimiento.
 */
data class TransactionDetailUiState(
    val isLoading: Boolean = true,
    val transaction: Transaction? = null,
    val editedNote: String = "",
    val isAntExpense: Boolean = false,
    val isSaving: Boolean = false,
    val showDeleteConfirmDialog: Boolean = false,
    val hasUnsavedChanges: Boolean = false
)

/**
 * Eventos para la pantalla de detalle de movimiento.
 */
sealed interface TransactionDetailEvent {
    data class OnNoteChanged(val newNote: String) : TransactionDetailEvent
    data class OnToggleAntExpense(val isAnt: Boolean) : TransactionDetailEvent
    data object OnSaveEdits : TransactionDetailEvent
    data object OnRequestDelete : TransactionDetailEvent
    data object OnDismissDeleteDialog : TransactionDetailEvent
    data object OnConfirmDelete : TransactionDetailEvent
}

/**
 * Efectos de una sola vez para la pantalla de detalle de movimiento.
 */
sealed interface TransactionDetailEffect {
    data object NavigateBack : TransactionDetailEffect
    data class ShowSnackbar(val message: String) : TransactionDetailEffect
}

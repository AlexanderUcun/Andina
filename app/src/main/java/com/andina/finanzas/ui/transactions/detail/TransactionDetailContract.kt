package com.andina.finanzas.ui.transactions.detail

import com.andina.finanzas.domain.model.Category
import com.andina.finanzas.domain.model.Transaction
import com.andina.finanzas.domain.model.Wallet

/**
 * Estado de la UI para la pantalla de detalle y edición de movimiento.
 */
data class TransactionDetailUiState(
    val isLoading: Boolean = true,
    val transaction: Transaction? = null,
    val editedAmountPesos: Long = 0L,
    val editedAmountInput: String = "",
    val editedWalletId: Long = 0L,
    val editedToWalletId: Long? = null,
    val editedCategoryId: Long? = null,
    val editedOccurredAt: Long = 0L,
    val editedNote: String = "",
    val isAntExpense: Boolean = false,
    val isSaving: Boolean = false,
    val showDeleteConfirmDialog: Boolean = false,
    val showAmountEditDialog: Boolean = false,
    val showWalletPickerDialog: Boolean = false,
    val showToWalletPickerDialog: Boolean = false,
    val showCategoryPickerDialog: Boolean = false,
    val showDatePickerDialog: Boolean = false,
    val showTimePickerDialog: Boolean = false,
    val availableWallets: List<Wallet> = emptyList(),
    val availableCategories: List<Category> = emptyList(),
    val hasUnsavedChanges: Boolean = false
)

/**
 * Eventos para la pantalla de detalle de movimiento.
 */
sealed interface TransactionDetailEvent {
    data class OnNoteChanged(val newNote: String) : TransactionDetailEvent
    data class OnToggleAntExpense(val isAnt: Boolean) : TransactionDetailEvent
    data object OnOpenAmountDialog : TransactionDetailEvent
    data class OnAmountInputChanged(val rawDigits: String) : TransactionDetailEvent
    data object OnConfirmAmountEdit : TransactionDetailEvent
    data object OnDismissAmountDialog : TransactionDetailEvent
    data object OnOpenWalletDialog : TransactionDetailEvent
    data class OnSelectWallet(val walletId: Long) : TransactionDetailEvent
    data object OnDismissWalletDialog : TransactionDetailEvent
    data object OnOpenToWalletDialog : TransactionDetailEvent
    data class OnSelectToWallet(val walletId: Long) : TransactionDetailEvent
    data object OnDismissToWalletDialog : TransactionDetailEvent
    data object OnOpenCategoryDialog : TransactionDetailEvent
    data class OnSelectCategory(val categoryId: Long) : TransactionDetailEvent
    data object OnDismissCategoryDialog : TransactionDetailEvent
    data object OnOpenDatePickerDialog : TransactionDetailEvent
    data class OnSelectDate(val epochMillis: Long) : TransactionDetailEvent
    data object OnDismissDatePickerDialog : TransactionDetailEvent
    data object OnOpenTimePickerDialog : TransactionDetailEvent
    data class OnSelectTime(val hour: Int, val minute: Int) : TransactionDetailEvent
    data object OnDismissTimePickerDialog : TransactionDetailEvent
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

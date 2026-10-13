package com.andina.finanzas.ui.wallets

import com.andina.finanzas.domain.model.Wallet
import com.andina.finanzas.domain.model.WalletType

enum class WalletsTab {
    ACTIVAS,
    ARCHIVADAS
}

/**
 * Estado de UI para la pantalla de gestión de billeteras.
 */
data class WalletsUiState(
    val isLoading: Boolean = true,
    val selectedTab: WalletsTab = WalletsTab.ACTIVAS,
    val wallets: List<Wallet> = emptyList(),
    val archivedWallets: List<Wallet> = emptyList(),
    val totalBalance: Long = 0L,
    val showAddEditDialog: Boolean = false,
    val editingWallet: Wallet? = null,
    val walletToDelete: Wallet? = null,
    val isSaving: Boolean = false
)

/**
 * Eventos para la gestión de billeteras.
 */
sealed interface WalletsEvent {
    data class OnSelectTab(val tab: WalletsTab) : WalletsEvent
    data object OnOpenAddWallet : WalletsEvent
    data class OnOpenEditWallet(val wallet: Wallet) : WalletsEvent
    data object OnDismissDialog : WalletsEvent
    data class OnSaveWallet(
        val name: String,
        val type: WalletType,
        val initialBalancePesos: Long,
        val colorHex: String
    ) : WalletsEvent
    data class OnArchiveWallet(val walletId: Long) : WalletsEvent
    data class OnUnarchiveWallet(val walletId: Long) : WalletsEvent
    data class OnRequestDeleteWallet(val wallet: Wallet) : WalletsEvent
    data object OnDismissDeleteDialog : WalletsEvent
    data class OnConfirmDeleteWallet(val walletId: Long) : WalletsEvent
}

/**
 * Efectos de una sola vez para la pantalla de billeteras.
 */
sealed interface WalletsEffect {
    data class ShowSnackbar(val message: String) : WalletsEffect
}

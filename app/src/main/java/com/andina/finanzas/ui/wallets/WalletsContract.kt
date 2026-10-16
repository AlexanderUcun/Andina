package com.andina.finanzas.ui.wallets

import com.andina.finanzas.domain.model.Debt
import com.andina.finanzas.domain.model.DebtType
import com.andina.finanzas.domain.model.Wallet
import com.andina.finanzas.domain.model.WalletType

enum class WalletsTab {
    ACTIVAS,
    ARCHIVADAS
}

enum class PatrimonioTab {
    BILLETERAS, // 💳 Cuentas
    BOLSILLOS,  // 🌱 Metas
    DEUDAS      // 🤝 Deudas
}

enum class DebtFilter {
    TODAS,
    RECEIVABLE, // Me deben (Por cobrar)
    PAYABLE,    // Yo debo (Por pagar)
    SALDADAS    // Saldadas
}

/**
 * Estado de UI para la pantalla de gestión de patrimonio (Cuentas, Metas y Deudas).
 */
data class WalletsUiState(
    val isLoading: Boolean = true,
    val selectedPatrimonioTab: PatrimonioTab = PatrimonioTab.BILLETERAS,
    val selectedTab: WalletsTab = WalletsTab.ACTIVAS,
    val wallets: List<Wallet> = emptyList(),
    val archivedWallets: List<Wallet> = emptyList(),
    val totalBalance: Long = 0L,
    val showAddEditDialog: Boolean = false,
    val editingWallet: Wallet? = null,
    val walletToDelete: Wallet? = null,
    val isSaving: Boolean = false,

    // Módulo de Deudas y Préstamos ("Me deben" & "Yo debo")
    val debts: List<Debt> = emptyList(),
    val debtFilter: DebtFilter = DebtFilter.TODAS,
    val showCreateDebtDialog: Boolean = false,
    val debtForPayment: Debt? = null,
    val debtToDelete: Debt? = null
) {
    val totalReceivablePesos: Long
        get() = debts.filter { it.type == DebtType.RECEIVABLE && !it.isSettled }
            .sumOf { it.remainingAmount.pesos }

    val totalPayablePesos: Long
        get() = debts.filter { it.type == DebtType.PAYABLE && !it.isSettled }
            .sumOf { it.remainingAmount.pesos }

    val netDebtBalancePesos: Long
        get() = totalReceivablePesos - totalPayablePesos

    val filteredDebts: List<Debt>
        get() = when (debtFilter) {
            DebtFilter.TODAS -> debts
            DebtFilter.RECEIVABLE -> debts.filter { it.type == DebtType.RECEIVABLE && !it.isSettled }
            DebtFilter.PAYABLE -> debts.filter { it.type == DebtType.PAYABLE && !it.isSettled }
            DebtFilter.SALDADAS -> debts.filter { it.isSettled }
        }
}

/**
 * Eventos para la gestión de patrimonio.
 */
sealed interface WalletsEvent {
    data class OnSelectPatrimonioTab(val tab: PatrimonioTab) : WalletsEvent
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

    // Eventos de Deudas
    data class OnSelectDebtFilter(val filter: DebtFilter) : WalletsEvent
    data object OnOpenCreateDebt : WalletsEvent
    data object OnDismissCreateDebt : WalletsEvent
    data class OnCreateDebt(
        val personName: String,
        val type: DebtType,
        val totalAmountPesos: Long,
        val walletId: Long,
        val affectWalletBalance: Boolean,
        val dueDateMillis: Long?,
        val note: String
    ) : WalletsEvent
    data class OnOpenDebtPayment(val debt: Debt) : WalletsEvent
    data object OnDismissDebtPayment : WalletsEvent
    data class OnRegisterDebtPayment(
        val debtId: Long,
        val amountPesos: Long,
        val walletId: Long,
        val note: String
    ) : WalletsEvent
    data class OnRequestDeleteDebt(val debt: Debt) : WalletsEvent
    data object OnDismissDeleteDebtDialog : WalletsEvent
    data class OnConfirmDeleteDebt(val debtId: Long) : WalletsEvent
}

/**
 * Efectos de una sola vez para la pantalla de billeteras y deudas.
 */
sealed interface WalletsEffect {
    data class ShowSnackbar(val message: String) : WalletsEffect
}

package com.andina.finanzas.ui.home

import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.domain.model.Pocket
import com.andina.finanzas.domain.model.Transaction
import com.andina.finanzas.domain.model.Wallet

/**
 * Contrato de Estado, Acciones y Efectos Unidireccionales (MVI) para la Pantalla de Inicio.
 *
 * Sigue los principios de "Lujo Silencioso", Inmutabilidad estricta y reactividad con Room.
 */
data class HomeUiState(
    val userName: String = "Valentina",
    val avatarEmoji: String = "🐻",
    val greeting: String = "Buenos días",
    val userLevel: String = "Nivel Frailejón Mayor",
    val totalNetWorth: Money = Money.ZERO,
    val isPrivacyMode: Boolean = false,
    val periodIncome: Money = Money.ZERO,
    val periodExpense: Money = Money.ZERO,
    val periodNetRemaining: Money = Money.ZERO,
    val budgetTotal: Money = Money(2_500_000L),
    val budgetSpent: Money = Money.ZERO,
    val budgetProgress: Float = 0f,
    val wallets: List<Wallet> = emptyList(),
    val pockets: List<Pocket> = emptyList(),
    val recentTransactions: List<Transaction> = emptyList(),
    val antExpensesMonthTotal: Money = Money.ZERO,
    val paramoHealthLevel: String = "Nivel Páramo",
    val andinoInsight: String? = "Tus gastos hormiga representan un control óptimo este mes. ¡Gran disciplina financiera!",
    val showBudgetCard: Boolean = true,
    val showPocketsCard: Boolean = true,
    val isInsightDismissed: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoading: Boolean = false
) {
    /**
     * Devuelve el monto formateado en COP o la máscara de privacidad si está activa.
     */
    fun formattedAmount(money: Money): String {
        return if (isPrivacyMode) "••••••••" else money.formatCop()
    }
}

/**
 * Intenciones o acciones emitidas por el usuario en la UI de Inicio.
 */
sealed interface HomeAction {
    data object TogglePrivacyMode : HomeAction
    data object DismissInsight : HomeAction
    data object RefreshData : HomeAction
    data class WalletClick(val walletId: Long) : HomeAction
    data class PocketClick(val pocketId: Long) : HomeAction
    data class TransactionClick(val transactionId: Long) : HomeAction
    data object SeeAllWalletsClick : HomeAction
    data object SeeAllPocketsClick : HomeAction
    data object SeeAllTransactionsClick : HomeAction
    data object NotificationsClick : HomeAction
    data object ProfileClick : HomeAction
    data object BudgetClick : HomeAction
    data object CalendarClick : HomeAction
    data object ExportClick : HomeAction
    data object CustomizationClick : HomeAction
    data object SmsClick : HomeAction
    data object AntExpenseCardClick : HomeAction
    data object QuickRegisterClick : HomeAction
}

/**
 * Efectos secundarios de una sola emisión (navegación o snackbar).
 */
sealed interface HomeEffect {
    data class NavigateToWallet(val walletId: Long) : HomeEffect
    data class NavigateToPocket(val pocketId: Long) : HomeEffect
    data class NavigateToTransactionDetail(val transactionId: Long) : HomeEffect
    data object NavigateToWalletsTab : HomeEffect
    data object NavigateToPocketsTab : HomeEffect
    data object NavigateToTransactionsTab : HomeEffect
    data object NavigateToRegister : HomeEffect
    data object NavigateToSettings : HomeEffect
    data object NavigateToBudgets : HomeEffect
    data object NavigateToCalendar : HomeEffect
    data object NavigateToReportExport : HomeEffect
    data object NavigateToCustomization : HomeEffect
    data object NavigateToConsultorioSms : HomeEffect
    data object NavigateToAntExpenses : HomeEffect
    data object NavigateToNotifications : HomeEffect
    data class ShowSnackbar(val message: String) : HomeEffect
}

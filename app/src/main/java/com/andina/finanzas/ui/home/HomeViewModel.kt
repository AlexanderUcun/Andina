package com.andina.finanzas.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.domain.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject

/**
 * ViewModel maestro de la pantalla de Inicio (Dashboard).
 *
 * Características de ingeniería:
 * 1. Resiliencia: Almacena en [SavedStateHandle] el modo de privacidad y descarte de insight.
 * 2. Reactividad en tiempo real: Flujo combinado continuo desde [FinanceRepository].
 * 3. Cálculos limpios con [Money]: Cero errores de punto flotante en patrimonio y balances.
 * 4. Saludos y niveles orgánicos según la hora y la salud del páramo.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: FinanceRepository
) : ViewModel() {

    companion object {
        private const val KEY_PRIVACY_MODE = "home_privacy_mode"
        private const val KEY_INSIGHT_DISMISSED = "home_insight_dismissed"
    }

    private val initialPrivacyMode: Boolean =
        savedStateHandle.get<Boolean>(KEY_PRIVACY_MODE) ?: false

    private val initialInsightDismissed: Boolean =
        savedStateHandle.get<Boolean>(KEY_INSIGHT_DISMISSED) ?: false

    private val _state = MutableStateFlow(
        HomeUiState(
            greeting = calculateGreeting(LocalTime.now().hour),
            isPrivacyMode = initialPrivacyMode,
            isInsightDismissed = initialInsightDismissed,
            isLoading = true
        )
    )
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private val _effects = Channel<HomeEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        observeDashboardData()
    }

    private fun observeDashboardData() {
        val (fromMillis, toMillis) = getCurrentMonthRange()

        combine(
            repository.observeWallets(),
            repository.observePockets(),
            repository.observeRecentTransactions(limit = 5),
            repository.observeFinancialSummary(fromMillis, toMillis)
        ) { wallets, pockets, recentTxs, summary ->
            val budgetTotal = Money(2_500_000L) // Presupuesto mensual estándar o meta de gasto
            val spent = summary.monthlyExpense
            val budgetProgress = if (budgetTotal.pesos > 0L) {
                (spent.pesos.toFloat() / budgetTotal.pesos.toFloat()).coerceIn(0f, 1f)
            } else 0f

            val paramoLevel = calculateParamoLevel(summary.savingsRate)

            _state.update { current ->
                current.copy(
                    greeting = calculateGreeting(LocalTime.now().hour),
                    totalNetWorth = summary.totalNetWorth,
                    periodIncome = summary.monthlyIncome,
                    periodExpense = summary.monthlyExpense,
                    periodNetRemaining = summary.monthlyNetBalance,
                    budgetTotal = budgetTotal,
                    budgetSpent = spent,
                    budgetProgress = budgetProgress,
                    wallets = wallets,
                    pockets = pockets.take(5),
                    recentTransactions = recentTxs,
                    antExpensesMonthTotal = summary.monthlyAntExpense,
                    paramoHealthLevel = paramoLevel,
                    isLoading = false
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.TogglePrivacyMode -> {
                val newMode = !_state.value.isPrivacyMode
                savedStateHandle[KEY_PRIVACY_MODE] = newMode
                _state.update { it.copy(isPrivacyMode = newMode) }
            }

            is HomeAction.DismissInsight -> {
                savedStateHandle[KEY_INSIGHT_DISMISSED] = true
                _state.update { it.copy(isInsightDismissed = true) }
            }

            is HomeAction.RefreshData -> {
                refreshData()
            }

            is HomeAction.WalletClick -> {
                sendEffect(HomeEffect.NavigateToWallet(action.walletId))
            }

            is HomeAction.PocketClick -> {
                sendEffect(HomeEffect.NavigateToPocket(action.pocketId))
            }

            is HomeAction.TransactionClick -> {
                sendEffect(HomeEffect.NavigateToTransactionDetail(action.transactionId))
            }

            is HomeAction.SeeAllWalletsClick -> {
                sendEffect(HomeEffect.NavigateToWalletsTab)
            }

            is HomeAction.SeeAllPocketsClick -> {
                sendEffect(HomeEffect.NavigateToPocketsTab)
            }

            is HomeAction.SeeAllTransactionsClick -> {
                sendEffect(HomeEffect.NavigateToTransactionsTab)
            }

            is HomeAction.NotificationsClick -> {
                sendEffect(HomeEffect.NavigateToNotifications)
            }

            is HomeAction.ProfileClick -> {
                sendEffect(HomeEffect.NavigateToSettings)
            }

            is HomeAction.BudgetClick -> {
                sendEffect(HomeEffect.NavigateToBudgets)
            }

            is HomeAction.AntExpenseCardClick -> {
                sendEffect(HomeEffect.NavigateToAntExpenses)
            }

            is HomeAction.QuickRegisterClick -> {
                sendEffect(HomeEffect.NavigateToRegister)
            }
        }
    }

    private fun refreshData() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            // Simulación táctil de refresco para feedback del rocío
            delay(800)
            _state.update {
                it.copy(
                    isRefreshing = false,
                    greeting = calculateGreeting(LocalTime.now().hour)
                )
            }
        }
    }

    private fun sendEffect(effect: HomeEffect) {
        viewModelScope.launch {
            _effects.send(effect)
        }
    }

    private fun calculateGreeting(hour: Int): String {
        return when (hour) {
            in 5..11 -> "Buenos días"
            in 12..18 -> "Buenas tardes"
            else -> "Buenas noches"
        }
    }

    private fun calculateParamoLevel(savingsRate: Float): String {
        return when {
            savingsRate >= 0.30f -> "Nivel Páramo Sagrado"
            savingsRate >= 0.15f -> "Nivel Frailejón Mayor"
            savingsRate >= 0.05f -> "Nivel Musgo Andino"
            else -> "Nivel La Semilla"
        }
    }

    private fun getCurrentMonthRange(): Pair<Long, Long> {
        val now = YearMonth.now()
        val zone = ZoneId.systemDefault()
        val startOfMonth = now.atDay(1).atStartOfDay(zone).toInstant().toEpochMilli()
        val endOfMonth = now.atEndOfMonth().atTime(23, 59, 59, 999_000_000).atZone(zone).toInstant().toEpochMilli()
        return Pair(startOfMonth, endOfMonth)
    }
}

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
    private val repository: FinanceRepository,
    private val userProfileRepository: com.andina.finanzas.domain.repository.UserProfileRepository
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
            repository.observeFinancialSummary(fromMillis, toMillis),
            userProfileRepository.observeUserProfile()
        ) { wallets, pockets, recentTxs, summary, userProfile ->
            val budgetTotal = Money(userProfile.monthlyBudgetLimit)
            val spent = summary.monthlyExpense
            val budgetProgress = if (budgetTotal.pesos > 0L) {
                (spent.pesos.toFloat() / budgetTotal.pesos.toFloat()).coerceIn(0f, 1f)
            } else 0f

            _state.update { current ->
                current.copy(
                    userName = userProfile.name,
                    avatarEmoji = userProfile.avatarEmoji,
                    userLevel = userProfile.level,
                    greeting = calculateGreeting(LocalTime.now().hour),
                    totalNetWorth = summary.totalNetWorth,
                    periodIncome = summary.monthlyIncome,
                    periodExpense = summary.monthlyExpense,
                    periodNetRemaining = summary.monthlyNetBalance,
                    budgetTotal = budgetTotal,
                    budgetSpent = spent,
                    budgetProgress = budgetProgress,
                    wallets = wallets,
                    pockets = if (userProfile.showPocketsCard) pockets.take(5) else emptyList(),
                    showBudgetCard = userProfile.showBudgetCard,
                    showPocketsCard = userProfile.showPocketsCard,
                    recentTransactions = recentTxs,
                    antExpensesMonthTotal = summary.monthlyAntExpense,
                    paramoHealthLevel = userProfile.level,
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

            is HomeAction.CalendarClick -> {
                sendEffect(HomeEffect.NavigateToCalendar)
            }

            is HomeAction.ExportClick -> {
                sendEffect(HomeEffect.NavigateToReportExport)
            }

            is HomeAction.CustomizationClick -> {
                sendEffect(HomeEffect.NavigateToCustomization)
            }

            is HomeAction.SmsClick -> {
                sendEffect(HomeEffect.NavigateToConsultorioSms)
            }

            is HomeAction.AntExpenseCardClick -> {
                sendEffect(HomeEffect.NavigateToAntExpenses)
            }

            is HomeAction.QuickRegisterClick -> {
                sendEffect(HomeEffect.NavigateToRegister)
            }
        }
    }

    private var insightRotationIndex = 0

    private fun refreshData() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            // Breve delay fluido para que la marea andina complete su ciclo armónico
            delay(500)
            val current = _state.value
            val newInsight = generateMuggyInsight(current)
            savedStateHandle[KEY_INSIGHT_DISMISSED] = false
            _state.update {
                it.copy(
                    isRefreshing = false,
                    isInsightDismissed = false,
                    andinoInsight = newInsight,
                    greeting = calculateGreeting(LocalTime.now().hour)
                )
            }
            sendEffect(HomeEffect.ShowSnackbar("🔒 Bóveda local protegida · 100% Offline"))
        }
    }

    private fun generateMuggyInsight(state: HomeUiState): String {
        insightRotationIndex++

        val totalExpense = state.periodExpense.pesos
        val totalIncome = state.periodIncome.pesos
        val antExpense = state.antExpensesMonthTotal.pesos
        val budgetProgress = state.budgetProgress
        val remainingNet = state.periodNetRemaining.pesos

        val candidates = mutableListOf<String>()

        // 1. Diagnóstico de Gastos Hormiga
        if (antExpense > 0L && totalExpense > 0L) {
            val antPercent = ((antExpense.toDouble() / totalExpense.toDouble()) * 100).toInt()
            if (antPercent >= 10) {
                candidates.add(
                    "Muggy detectó ${state.antExpensesMonthTotal.formatCop()} en gastos hormiga ($antPercent% de tus egresos). Controla los pequeños antojos para potenciar tus reservas."
                )
            } else {
                candidates.add(
                    "¡Gran control! Tus gastos hormiga solo representan el $antPercent% de tus gastos. Tu páramo financiero se mantiene equilibrado."
                )
            }
        } else if (antExpense == 0L && totalExpense > 0L) {
            candidates.add(
                "¡Cero gastos hormiga registrados este mes! Tu nivel de disciplina financiera es digno de un Guardián del Frailejón."
            )
        }

        // 2. Diagnóstico de Presupuesto Mensual
        if (state.budgetTotal.pesos > 0L) {
            if (budgetProgress >= 0.85f) {
                val pct = (budgetProgress * 100).toInt()
                val remainingBudget = state.budgetTotal - state.budgetSpent
                candidates.add(
                    "Alerta de presupuesto: Has consumido el $pct% de tu límite mensual. Te quedan ${remainingBudget.coerceAtLeast(Money.ZERO).formatCop()} para terminar el ciclo."
                )
            } else if (budgetProgress in 0.10f..0.50f) {
                val pct = (budgetProgress * 100).toInt()
                candidates.add(
                    "Excelente ritmo: Vas al $pct% de tu presupuesto mensual. Tienes buen margen para destinar a reservas."
                )
            }
        }

        // 3. Diagnóstico de Flujo Neto y Capacidad de Ahorro
        if (totalIncome > 0L && remainingNet > 0L) {
            val savingsRate = ((remainingNet.toDouble() / totalIncome.toDouble()) * 100).toInt()
            candidates.add(
                "Tu tasa de ahorro mensual proyectada es del $savingsRate%. Considera pasar una parte a tus bolsillos de reserva."
            )
        } else if (totalExpense > totalIncome && totalIncome > 0L) {
            candidates.add(
                "Tus salidas superan tus ingresos este mes por ${Money(totalExpense - totalIncome).formatCop()}. Revisa si tienes compras diferidas o gastos no planificados."
            )
        }

        // 4. Diagnóstico de Bolsillos y Metas
        if (state.pockets.isNotEmpty()) {
            val targetPocket = state.pockets.minByOrNull { it.progress } ?: state.pockets.first()
            candidates.add(
                "Tu meta '${targetPocket.name}' va al ${(targetPocket.progress * 100).toInt()}%. Un aporte hoy te acerca más rápido a completarla."
            )
        }

        // 5. Consejos Estratégicos Andinos de Sabiduría Financiera
        val paramoWisdom = listOf(
            "Regla del Páramo 50/30/20: Destina 50% a necesidades vitales, 30% a estilo de vida y 20% a reservas inquebrantables.",
            "Antes de una compra imprevista, aplica la regla de las 48 horas. Si la necesidad persiste, evalúa si cabe en tu presupuesto.",
            "El dinero en reservas es como el agua almacenada en las raíces del frailejón: te da calma en las temporadas de sequía.",
            "Recuerda que registrar cada movimiento en el momento evita que se te escape el rastro de tus finanzas.",
            "Paga primero a tu 'yo del futuro': aparta tu cuota de ahorro al inicio del mes, no con lo que sobre al final."
        )
        candidates.addAll(paramoWisdom)

        val index = (insightRotationIndex - 1 + candidates.size) % candidates.size
        return candidates[index]
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

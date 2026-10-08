package com.andina.finanzas.ui.consultorio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andina.finanzas.core.intelligence.AndinaIntelligenceCore
import com.andina.finanzas.domain.model.FinancialSummary
import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.domain.model.Pocket
import com.andina.finanzas.domain.model.Wallet
import com.andina.finanzas.domain.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ConsultorioViewModel @Inject constructor(
    private val repository: FinanceRepository,
    private val intelligenceCore: AndinaIntelligenceCore,
    private val userProfileRepository: com.andina.finanzas.domain.repository.UserProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConsultorioUiState())
    val uiState: StateFlow<ConsultorioUiState> = _uiState.asStateFlow()

    private val _effectChannel = Channel<ConsultorioEffect>(Channel.BUFFERED)
    val effects = _effectChannel.receiveAsFlow()

    init {
        loadDataAndDiagnose()
    }

    private data class CombinedData(
        val wallets: List<Wallet>,
        val summary: FinancialSummary,
        val pockets: List<Pocket>,
        val categories: List<com.andina.finanzas.domain.model.Category>,
        val recentTxs: List<com.andina.finanzas.domain.model.Transaction>
    )

    private fun loadDataAndDiagnose() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startOfMonth = calendar.timeInMillis
        val endOfMonth = System.currentTimeMillis()

        viewModelScope.launch {
            val baseFlow = combine(
                repository.observeWallets(),
                repository.observeFinancialSummary(startOfMonth, endOfMonth),
                repository.observePockets(),
                repository.observeCategories(com.andina.finanzas.domain.model.CategoryKind.GASTO),
                repository.observeRecentTransactions(limit = 100)
            ) { wallets, summary, pockets, categories, recentTxs ->
                CombinedData(wallets, summary, pockets, categories, recentTxs)
            }

            baseFlow.combine(userProfileRepository.observeUserProfile()) { data, userProfile ->
                val wallets = data.wallets
                val summary = data.summary
                val pockets = data.pockets
                val categories = data.categories
                val recentTxs = data.recentTxs

                val totalLiquid = wallets.fold(Money.ZERO) { acc, w -> acc + w.currentBalance }
                val expenses = summary.monthlyExpense
                val antExpenses = summary.monthlyAntExpense
                val hasPockets = pockets.any { it.currentBalance.pesos > 0L }

                val spendPerCategory = recentTxs
                    .filter { it.type == com.andina.finanzas.domain.model.TransactionType.GASTO && it.categoryId != null && it.occurredAt >= startOfMonth }
                    .groupBy { it.categoryId!! }
                    .mapValues { (_, txList) -> txList.sumOf { it.amount.pesos } }

                val realTopCategories = categories.map { cat ->
                    val spent = spendPerCategory[cat.id] ?: 0L
                    Pair(cat.name, Money(spent))
                }.filter { it.second.pesos > 0L }.sortedByDescending { it.second.pesos }

                val finalCategories = if (realTopCategories.isNotEmpty()) {
                    realTopCategories
                } else {
                    categories.take(4).map { Pair(it.name, Money.ZERO) }
                }

                val dailyExpenses = recentTxs
                    .filter { it.type == com.andina.finanzas.domain.model.TransactionType.GASTO && it.occurredAt >= startOfMonth }
                    .groupBy { java.time.Instant.ofEpochMilli(it.occurredAt).atZone(java.time.ZoneId.systemDefault()).toLocalDate() }
                    .values
                    .map { dayList -> dayList.sumOf { it.amount.pesos } }
                    .filter { it > 0L }

                val diagnosis = intelligenceCore.diagnose(
                    userName = userProfile.name,
                    liquidBalance = totalLiquid,
                    totalExpensesThisMonth = expenses,
                    antExpensesTotal = antExpenses,
                    dailyExpensesHistory = if (dailyExpenses.isNotEmpty()) dailyExpenses else listOf(10_000L),
                    monthlyBudgetLimit = Money(userProfile.monthlyBudgetLimit),
                    hasActivePocketContributions = hasPockets
                )

                _uiState.update { current ->
                    current.copy(
                        totalBalance = totalLiquid,
                        totalExpensesThisMonth = expenses,
                        antExpensesTotal = antExpenses,
                        diagnosis = diagnosis,
                        topCategories = finalCategories,
                        isLoading = false
                    )
                }
            }.collect()
        }
    }


    fun onEvent(event: ConsultorioEvent) {
        when (event) {
            is ConsultorioEvent.RefreshDiagnosis -> {
                loadDataAndDiagnose()
            }
            is ConsultorioEvent.SelectTab -> {
                _uiState.update { it.copy(selectedTab = event.tab) }
            }
            is ConsultorioEvent.UpdateSmsInput -> {
                _uiState.update { it.copy(smsInputText = event.text) }
            }
            is ConsultorioEvent.ParseSms -> {
                val text = _uiState.value.smsInputText
                if (text.isNotBlank()) {
                    val parsed = intelligenceCore.parseBankSms(text)
                    _uiState.update { it.copy(parsedSms = parsed) }
                }
            }
            is ConsultorioEvent.ClearParsedSms -> {
                _uiState.update { it.copy(parsedSms = null, smsInputText = "") }
            }
            is ConsultorioEvent.OnUseParsedSmsInRegister -> {
                val parsed = _uiState.value.parsedSms
                if (parsed != null && parsed.isSuccess) {
                    viewModelScope.launch {
                        _effectChannel.send(
                            ConsultorioEffect.NavigateToRegisterWithPrefill(
                                amount = parsed.amount,
                                merchant = parsed.merchant,
                                wallet = parsed.walletName,
                                category = parsed.suggestedCategory,
                                isAnt = parsed.isAntCandidate
                            )
                        )
                    }
                }
            }
            is ConsultorioEvent.OnCreatePocketFromAntMirror -> {
                viewModelScope.launch {
                    _effectChannel.send(ConsultorioEffect.NavigateToPockets)
                }
            }
        }
    }
}

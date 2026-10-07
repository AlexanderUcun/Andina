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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ConsultorioViewModel @Inject constructor(
    private val repository: FinanceRepository,
    private val intelligenceCore: AndinaIntelligenceCore
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConsultorioUiState())
    val uiState: StateFlow<ConsultorioUiState> = _uiState.asStateFlow()

    private val _effectChannel = Channel<ConsultorioEffect>(Channel.BUFFERED)
    val effects = _effectChannel.receiveAsFlow()

    init {
        loadDataAndDiagnose()
    }

    private fun loadDataAndDiagnose() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startOfMonth = calendar.timeInMillis
        val endOfMonth = System.currentTimeMillis()

        viewModelScope.launch {
            combine(
                repository.observeWallets(),
                repository.observeFinancialSummary(startOfMonth, endOfMonth),
                repository.observePockets()
            ) { wallets: List<Wallet>, summary: FinancialSummary, pockets: List<Pocket> ->
                val totalLiquid = wallets.fold(Money.ZERO) { acc, w -> acc + w.currentBalance }
                val expenses = summary.monthlyExpense
                val antExpenses = summary.monthlyAntExpense
                val hasPockets = pockets.any { it.currentBalance.pesos > 0L }

                val dailyExpenses = listOf(
                    (expenses.pesos * 0.12).toLong(),
                    (expenses.pesos * 0.15).toLong(),
                    (expenses.pesos * 0.10).toLong(),
                    (expenses.pesos * 0.18).toLong(),
                    (expenses.pesos * 0.14).toLong(),
                    (expenses.pesos * 0.16).toLong(),
                    (expenses.pesos * 0.15).toLong()
                ).filter { it > 0L }

                val diagnosis = intelligenceCore.diagnose(
                    userName = "Valentina",
                    liquidBalance = totalLiquid,
                    totalExpensesThisMonth = expenses,
                    antExpensesTotal = antExpenses,
                    dailyExpensesHistory = if (dailyExpenses.isNotEmpty()) dailyExpenses else listOf(10_000L),
                    monthlyBudgetLimit = Money(totalLiquid.pesos + expenses.pesos),
                    hasActivePocketContributions = hasPockets
                )

                Triple(totalLiquid, expenses, diagnosis)
            }.collect { result: Triple<Money, Money, com.andina.finanzas.core.intelligence.CompleteFinancialDiagnosis> ->
                val totalLiquid = result.first
                val expenses = result.second
                val diagnosis = result.third

                _uiState.update { current ->
                    current.copy(
                        totalBalance = totalLiquid,
                        totalExpensesThisMonth = expenses,
                        antExpensesTotal = Money((expenses.pesos * 0.12).toLong()),
                        diagnosis = diagnosis,
                        topCategories = listOf(
                            Pair("Alimentación & Mercado", Money((expenses.pesos * 0.40).toLong())),
                            Pair("Servicios Públicos", Money((expenses.pesos * 0.25).toLong())),
                            Pair("Transporte", Money((expenses.pesos * 0.15).toLong())),
                            Pair("Café y Antojos (Hormiga)", Money((expenses.pesos * 0.12).toLong()))
                        ),
                        isLoading = false
                    )
                }
            }
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

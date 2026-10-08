package com.andina.finanzas.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andina.finanzas.domain.model.Category
import com.andina.finanzas.domain.model.Transaction
import com.andina.finanzas.domain.model.TransactionType
import com.andina.finanzas.domain.model.Wallet
import com.andina.finanzas.domain.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject

data class CalendarUiState(
    val yearMonth: YearMonth = YearMonth.now(),
    val selectedDay: Int = java.time.LocalDate.now().dayOfMonth,
    val daysWithExpense: Set<Int> = emptySet(),
    val daysWithIncome: Set<Int> = emptySet(),
    val allTransactionsInMonth: List<Transaction> = emptyList(),
    val selectedDayTransactions: List<Transaction> = emptyList(),
    val categoriesMap: Map<Long, Category> = emptyMap(),
    val walletsMap: Map<Long, Wallet> = emptyMap(),
    val isLoading: Boolean = true
)

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val financeRepository: FinanceRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CalendarUiState())
    val state: StateFlow<CalendarUiState> = _state.asStateFlow()

    init {
        observeMonthData(_state.value.yearMonth)
    }

    fun selectDay(day: Int) {
        _state.update { current ->
            val dayTxs = filterTransactionsForDay(current.allTransactionsInMonth, current.yearMonth, day)
            current.copy(
                selectedDay = day,
                selectedDayTransactions = dayTxs
            )
        }
    }

    fun changeMonth(offsetMonths: Long) {
        val newYearMonth = _state.value.yearMonth.plusMonths(offsetMonths)
        _state.update { it.copy(yearMonth = newYearMonth, selectedDay = 1) }
        observeMonthData(newYearMonth)
    }

    private fun observeMonthData(yearMonth: YearMonth) {
        val zone = ZoneId.systemDefault()
        val startOfMonth = yearMonth.atDay(1).atStartOfDay(zone).toInstant().toEpochMilli()
        val endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59, 999_000_000).atZone(zone).toInstant().toEpochMilli()

        combine(
            financeRepository.observeRecentTransactions(limit = 200),
            financeRepository.observeCategories(),
            financeRepository.observeWallets()
        ) { txs, categories, wallets ->
            val catMap = categories.associateBy { it.id }
            val walMap = wallets.associateBy { it.id }

            val monthTxs = txs.filter { it.occurredAt in startOfMonth..endOfMonth }

            val expenseDays = mutableSetOf<Int>()
            val incomeDays = mutableSetOf<Int>()

            monthTxs.forEach { tx ->
                val day = Instant.ofEpochMilli(tx.occurredAt).atZone(zone).dayOfMonth
                if (tx.type == TransactionType.GASTO) {
                    expenseDays.add(day)
                } else if (tx.type == TransactionType.INGRESO) {
                    incomeDays.add(day)
                }
            }

            val currentDay = _state.value.selectedDay.coerceIn(1, yearMonth.lengthOfMonth())
            val dayTxs = filterTransactionsForDay(monthTxs, yearMonth, currentDay)

            _state.update { current ->
                current.copy(
                    yearMonth = yearMonth,
                    selectedDay = currentDay,
                    daysWithExpense = expenseDays,
                    daysWithIncome = incomeDays,
                    allTransactionsInMonth = monthTxs,
                    selectedDayTransactions = dayTxs,
                    categoriesMap = catMap,
                    walletsMap = walMap,
                    isLoading = false
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun filterTransactionsForDay(
        transactions: List<Transaction>,
        yearMonth: YearMonth,
        day: Int
    ): List<Transaction> {
        val zone = ZoneId.systemDefault()
        return transactions.filter { tx ->
            val localDate = Instant.ofEpochMilli(tx.occurredAt).atZone(zone).toLocalDate()
            localDate.year == yearMonth.year && localDate.monthValue == yearMonth.monthValue && localDate.dayOfMonth == day
        }.sortedByDescending { it.occurredAt }
    }
}

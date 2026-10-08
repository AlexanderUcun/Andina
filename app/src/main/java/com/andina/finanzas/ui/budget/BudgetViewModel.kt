package com.andina.finanzas.ui.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andina.finanzas.domain.model.Category
import com.andina.finanzas.domain.model.CategoryKind
import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.domain.repository.FinanceRepository
import com.andina.finanzas.domain.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject

data class CategoryBudgetItem(
    val categoryId: Long,
    val name: String,
    val icon: String,
    val colorHex: String,
    val spent: Long,
    val limit: Long
)

data class BudgetUiState(
    val monthlyBudgetLimit: Long = 2_500_000L,
    val categories: List<CategoryBudgetItem> = emptyList(),
    val totalExecutedSpent: Long = 0L,
    val isLoading: Boolean = true
) {
    val totalAllocatedToCategories: Long
        get() = categories.sumOf { it.limit }

    val unallocatedBudget: Long
        get() = (monthlyBudgetLimit - totalAllocatedToCategories).coerceAtLeast(0L)

    val progress: Float
        get() = if (monthlyBudgetLimit > 0L) {
            (totalExecutedSpent.toFloat() / monthlyBudgetLimit.toFloat()).coerceIn(0f, 1f)
        } else 0f

    val remainingBudget: Long
        get() = (monthlyBudgetLimit - totalExecutedSpent).coerceAtLeast(0L)
}

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val financeRepository: FinanceRepository,
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BudgetUiState())
    val state: StateFlow<BudgetUiState> = _state.asStateFlow()

    // Límites por categoría guardados en memoria/preferencias
    private val categoryLimits = mutableMapOf<Long, Long>(
        1L to 300_000L,  // Café y Snacks
        2L to 900_000L,  // Alimentación
        3L to 400_000L,  // Transporte
        4L to 200_000L,  // Domicilios
        5L to 500_000L   // Servicios y Hogar
    )

    init {
        observeBudgetData()
    }

    private fun observeBudgetData() {
        val (fromMillis, toMillis) = getCurrentMonthRange()

        combine(
            financeRepository.observeCategories(CategoryKind.GASTO),
            financeRepository.observeFinancialSummary(fromMillis, toMillis),
            financeRepository.observeRecentTransactions(limit = 100),
            userProfileRepository.observeUserProfile()
        ) { categories, summary, recentTxs, userProfile ->
            // Calcular gasto real ejecutado por categoría este mes
            val spendPerCategory = recentTxs
                .filter { it.type == com.andina.finanzas.domain.model.TransactionType.GASTO && it.categoryId != null }
                .groupBy { it.categoryId!! }
                .mapValues { (_, txList) -> txList.sumOf { it.amount.pesos } }

            val budgetItems = categories.map { cat ->
                val spent = spendPerCategory[cat.id] ?: 0L
                val defaultLimit = categoryLimits[cat.id] ?: when (cat.name) {
                    "Alimentación" -> 900_000L
                    "Transporte" -> 400_000L
                    "Servicios y Hogar" -> 500_000L
                    "Café y Snacks" -> 300_000L
                    "Domicilios" -> 200_000L
                    else -> 0L
                }
                CategoryBudgetItem(
                    categoryId = cat.id,
                    name = cat.name,
                    icon = cat.icon,
                    colorHex = cat.colorHex,
                    spent = spent,
                    limit = defaultLimit
                )
            }

            _state.update { current ->
                current.copy(
                    monthlyBudgetLimit = userProfile.monthlyBudgetLimit,
                    categories = budgetItems,
                    totalExecutedSpent = summary.monthlyExpense.pesos,
                    isLoading = false
                )
            }
        }.launchIn(viewModelScope)
    }

    fun updateMonthlyBudget(newLimit: Long) {
        viewModelScope.launch {
            userProfileRepository.updateMonthlyBudgetLimit(newLimit)
        }
    }

    fun updateCategoryLimit(categoryId: Long, newLimit: Long) {
        categoryLimits[categoryId] = newLimit
        _state.update { current ->
            val updated = current.categories.map { item ->
                if (item.categoryId == categoryId) item.copy(limit = newLimit) else item
            }
            current.copy(categories = updated)
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

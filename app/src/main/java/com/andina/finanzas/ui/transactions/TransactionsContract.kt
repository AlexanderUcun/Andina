package com.andina.finanzas.ui.transactions

import com.andina.finanzas.domain.model.Category
import com.andina.finanzas.domain.model.Transaction
import com.andina.finanzas.domain.model.TransactionType
import com.andina.finanzas.domain.model.Wallet
import java.util.Calendar

/**
 * Preajustes de rango de fechas para el libro mayor andino.
 */
enum class DateRangeFilterPreset(val label: String) {
    ALL("Todo el tiempo"),
    TODAY("Hoy"),
    THIS_WEEK("Esta semana"),
    THIS_MONTH("Este mes"),
    LAST_30_DAYS("Últimos 30 días")
}

/**
 * Criterios de filtrado reactivo para la consulta paginada de movimientos.
 */
data class TransactionsFilter(
    val query: String = "",
    val type: TransactionType? = null,
    val walletId: Long? = null,
    val categoryId: Long? = null,
    val onlyAntExpense: Boolean = false,
    val datePreset: DateRangeFilterPreset = DateRangeFilterPreset.ALL,
    val customFromMillis: Long? = null,
    val customToMillis: Long? = null
) {
    val activeFilterCount: Int
        get() {
            var count = 0
            if (query.isNotBlank()) count++
            if (type != null) count++
            if (walletId != null) count++
            if (categoryId != null) count++
            if (onlyAntExpense) count++
            if (datePreset != DateRangeFilterPreset.ALL || customFromMillis != null || customToMillis != null) count++
            return count
        }

    val isDefault: Boolean
        get() = activeFilterCount == 0

    /**
     * Calcula los límites epoch [fromMillis, toMillis] según el preset o fechas personalizadas.
     */
    fun resolveDateRangeBounds(nowMillis: Long = System.currentTimeMillis()): Pair<Long?, Long?> {
        if (customFromMillis != null || customToMillis != null) {
            return Pair(customFromMillis, customToMillis)
        }

        val cal = Calendar.getInstance().apply { timeInMillis = nowMillis }
        return when (datePreset) {
            DateRangeFilterPreset.ALL -> Pair(null, null)
            DateRangeFilterPreset.TODAY -> {
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val startOfDay = cal.timeInMillis
                Pair(startOfDay, null)
            }
            DateRangeFilterPreset.THIS_WEEK -> {
                cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val startOfWeek = cal.timeInMillis
                Pair(startOfWeek, null)
            }
            DateRangeFilterPreset.THIS_MONTH -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val startOfMonth = cal.timeInMillis
                Pair(startOfMonth, null)
            }
            DateRangeFilterPreset.LAST_30_DAYS -> {
                val thirtyDaysAgo = nowMillis - (30L * 24L * 60L * 60L * 1000L)
                Pair(thirtyDaysAgo, null)
            }
        }
    }
}

enum class TransactionsViewMode {
    LISTA,
    CALENDARIO
}

/**
 * Estado general de la UI del historial de movimientos.
 */
data class TransactionsUiState(
    val isLoading: Boolean = false,
    val viewMode: TransactionsViewMode = TransactionsViewMode.LISTA,
    val filter: TransactionsFilter = TransactionsFilter(),
    val isFilterSheetVisible: Boolean = false,
    val availableWallets: List<Wallet> = emptyList(),
    val availableCategories: List<Category> = emptyList(),
    val lastDeletedTransaction: Transaction? = null
)

/**
 * Intenciones de usuario para la pantalla de transacciones.
 */
sealed interface TransactionsEvent {
    data class OnViewModeSelected(val viewMode: TransactionsViewMode) : TransactionsEvent
    data class OnQueryChanged(val query: String) : TransactionsEvent
    data class OnTypeSelected(val type: TransactionType?) : TransactionsEvent
    data class OnWalletSelected(val walletId: Long?) : TransactionsEvent
    data class OnCategorySelected(val categoryId: Long?) : TransactionsEvent
    data class OnToggleAntExpense(val onlyAnt: Boolean) : TransactionsEvent
    data class OnDatePresetSelected(val preset: DateRangeFilterPreset) : TransactionsEvent
    data class OnCustomDateRangeSet(val fromMillis: Long?, val toMillis: Long?) : TransactionsEvent
    data object OnResetFilters : TransactionsEvent
    data class OnSetFilterSheetVisible(val visible: Boolean) : TransactionsEvent
    data class OnDeleteTransaction(val transaction: Transaction) : TransactionsEvent
    data object OnUndoDelete : TransactionsEvent
    data class OnTransactionClick(val transactionId: Long) : TransactionsEvent
}

/**
 * Efectos secundarios de una sola ejecución (One-shot).
 */
sealed interface TransactionsEffect {
    data class ShowUndoSnackbar(
        val message: String,
        val actionLabel: String = "Deshacer"
    ) : TransactionsEffect
    data class NavigateToDetail(val transactionId: Long) : TransactionsEffect
    data class ShowMessage(val message: String) : TransactionsEffect
}

package com.andina.finanzas.ui.consultorio

import com.andina.finanzas.core.intelligence.CompleteFinancialDiagnosis
import com.andina.finanzas.core.intelligence.nlp.ParsedBankSms
import com.andina.finanzas.domain.model.Money

enum class ConsultorioTab {
    DIAGNOSTICO,
    ESPEJO_HORMIGA,
    SIMULADOR_BOLSILLO,
    SMS_PARSER
}

data class ConsultorioUiState(
    val userName: String = "Valentina",
    val diagnosis: CompleteFinancialDiagnosis? = null,
    val selectedTab: ConsultorioTab = ConsultorioTab.DIAGNOSTICO,
    val smsInputText: String = "",
    val parsedSms: ParsedBankSms? = null,
    val topCategories: List<Pair<String, Money>> = emptyList(),
    val totalBalance: Money = Money(0L),
    val totalExpensesThisMonth: Money = Money(0L),
    val antExpensesTotal: Money = Money(0L),
    val isLoading: Boolean = false
)

sealed interface ConsultorioEvent {
    data object RefreshDiagnosis : ConsultorioEvent
    data class SelectTab(val tab: ConsultorioTab) : ConsultorioEvent
    data class UpdateSmsInput(val text: String) : ConsultorioEvent
    data object ParseSms : ConsultorioEvent
    data object ClearParsedSms : ConsultorioEvent
    data object OnUseParsedSmsInRegister : ConsultorioEvent
    data class OnCreatePocketFromAntMirror(val pocketName: String, val amount: Long) : ConsultorioEvent
}

sealed interface ConsultorioEffect {
    data class NavigateToRegisterWithPrefill(
        val amount: Long,
        val merchant: String,
        val wallet: String,
        val category: String,
        val isAnt: Boolean
    ) : ConsultorioEffect
    data object NavigateToPockets : ConsultorioEffect
    data class ShowSnackbar(val message: String) : ConsultorioEffect
}

package com.andina.finanzas.core.intelligence

import com.andina.finanzas.core.intelligence.nlg.MuggyDialogueEngine
import com.andina.finanzas.core.intelligence.nlg.MuggyInsight
import com.andina.finanzas.core.intelligence.nlp.ColombianBankSmsParser
import com.andina.finanzas.core.intelligence.nlp.MerchantFuzzyClassifier
import com.andina.finanzas.core.intelligence.nlp.ParsedBankSms
import com.andina.finanzas.core.intelligence.stats.EwmaAnomalyDetector
import com.andina.finanzas.core.intelligence.stats.EwmaEvaluation
import com.andina.finanzas.core.intelligence.stats.QuincenaCycleAnalyzer
import com.andina.finanzas.core.intelligence.stats.QuincenaReport
import com.andina.finanzas.core.intelligence.triage.FinancialHealthScore
import com.andina.finanzas.core.intelligence.triage.FinancialHealthScorer
import com.andina.finanzas.domain.model.Money
import javax.inject.Inject
import javax.inject.Singleton

data class CompleteFinancialDiagnosis(
    val healthScore: FinancialHealthScore,
    val quincenaReport: QuincenaReport,
    val insight: MuggyInsight,
    val isAnomalyDetected: Boolean
)

/**
 * Fachada central del Motor de Inteligencia Local On-Device de Andina (ARQUITECTURA.md §10).
 * 100% Offline, Cero dependencias de nube, inferencias en < 8 ms.
 */
@Singleton
class AndinaIntelligenceCore @Inject constructor() {

    val merchantClassifier = MerchantFuzzyClassifier()
    val smsParser = ColombianBankSmsParser(merchantClassifier)
    val ewmaDetector = EwmaAnomalyDetector()
    val quincenaAnalyzer = QuincenaCycleAnalyzer()
    val healthScorer = FinancialHealthScorer()
    val dialogueEngine = MuggyDialogueEngine()

    /**
     * Ejecuta el diagnóstico integral de salud financiera en menos de 5 ms.
     */
    fun diagnose(
        userName: String,
        liquidBalance: Money,
        totalExpensesThisMonth: Money,
        antExpensesTotal: Money,
        dailyExpensesHistory: List<Long>,
        monthlyBudgetLimit: Money? = null,
        hasActivePocketContributions: Boolean = true
    ): CompleteFinancialDiagnosis {
        // 1. Detección de anomalías EWMA
        val weightedDailyAvg = ewmaDetector.calculateWeightedDailyAverage(dailyExpensesHistory)
        val ewmaEval = if (dailyExpensesHistory.size > 1) {
            ewmaDetector.evaluate(dailyExpensesHistory)
        } else {
            EwmaEvaluation(0L, weightedDailyAvg, 0.0, false, 0.0)
        }

        // 2. Análisis del ciclo quincenal
        val quincenaReport = quincenaAnalyzer.analyze(
            currentLiquidBalance = liquidBalance.pesos,
            quincenaSpentExpenses = totalExpensesThisMonth.pesos,
            weightedDailyAverage = weightedDailyAvg
        )

        // 3. Triaje ponderado (0-100)
        val budgetRatio = if (monthlyBudgetLimit != null && monthlyBudgetLimit.pesos > 0L) {
            totalExpensesThisMonth.pesos.toDouble() / monthlyBudgetLimit.pesos.toDouble()
        } else 0.50

        val antRatio = if (totalExpensesThisMonth.pesos > 0L) {
            antExpensesTotal.pesos.toDouble() / totalExpensesThisMonth.pesos.toDouble()
        } else 0.0

        val healthScore = healthScorer.calculateScore(
            budgetConsumptionRatio = budgetRatio,
            antExpenseRatio = antRatio,
            oxygenDays = quincenaReport.oxygenDays,
            hasActivePocketContributions = hasActivePocketContributions
        )

        // 4. Generación de diálogo NLG
        val insight = dialogueEngine.generateInsight(
            userName = userName,
            score = healthScore,
            liquidBalance = liquidBalance,
            antExpensesTotal = antExpensesTotal,
            oxygenDays = quincenaReport.oxygenDays
        )

        return CompleteFinancialDiagnosis(
            healthScore = healthScore,
            quincenaReport = quincenaReport,
            insight = insight,
            isAnomalyDetected = ewmaEval.isAnomaly
        )
    }

    /**
     * Parsea un SMS de un banco colombiano localmente.
     */
    fun parseBankSms(smsText: String): ParsedBankSms {
        return smsParser.parse(smsText)
    }
}

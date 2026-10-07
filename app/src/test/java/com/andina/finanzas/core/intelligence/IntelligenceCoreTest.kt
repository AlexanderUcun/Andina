package com.andina.finanzas.core.intelligence

import com.andina.finanzas.core.intelligence.nlg.DialogueActionType
import com.andina.finanzas.core.intelligence.nlg.MuggyDialogueEngine
import com.andina.finanzas.core.intelligence.nlp.ColombianBankSmsParser
import com.andina.finanzas.core.intelligence.nlp.MerchantFuzzyClassifier
import com.andina.finanzas.core.intelligence.stats.EwmaAnomalyDetector
import com.andina.finanzas.core.intelligence.stats.QuincenaCycleAnalyzer
import com.andina.finanzas.core.intelligence.stats.QuincenaHealthStatus
import com.andina.finanzas.core.intelligence.triage.FinancialHealthScorer
import com.andina.finanzas.core.intelligence.triage.TriageLevel
import com.andina.finanzas.domain.model.Money
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Calendar
import kotlin.system.measureTimeMillis

/**
 * Quality Gate exhaustivo para Andina Intelligence Core On-Device (Fase 11).
 * Valida latencia < 15 ms, 100% offline, parsers NLP, EWMA, Quincena y Triaje matemático.
 */
class IntelligenceCoreTest {

    private lateinit var classifier: MerchantFuzzyClassifier
    private lateinit var smsParser: ColombianBankSmsParser
    private lateinit var ewmaDetector: EwmaAnomalyDetector
    private lateinit var quincenaAnalyzer: QuincenaCycleAnalyzer
    private lateinit var healthScorer: FinancialHealthScorer
    private lateinit var dialogueEngine: MuggyDialogueEngine
    private lateinit var intelligenceCore: AndinaIntelligenceCore

    @Before
    fun setUp() {
        classifier = MerchantFuzzyClassifier()
        smsParser = ColombianBankSmsParser(classifier)
        ewmaDetector = EwmaAnomalyDetector()
        quincenaAnalyzer = QuincenaCycleAnalyzer()
        healthScorer = FinancialHealthScorer()
        dialogueEngine = MuggyDialogueEngine()
        intelligenceCore = AndinaIntelligenceCore()
    }

    // --- 1. NLP & MERCHANT CLASSIFIER ---

    @Test
    fun `MerchantClassifier classifies Colombian grocery merchants with high confidence`() {
        val resultD1 = classifier.classify("tiendas d1 cr 7")
        assertEquals("Tiendas D1", resultD1.normalizedMerchant)
        assertEquals("Mercado", resultD1.categoryName)
        assertFalse(resultD1.isAntCandidate)
        assertTrue(resultD1.confidence >= 0.90f)

        val resultExito = classifier.classify("exito calle 80")
        assertEquals("Éxito", resultExito.normalizedMerchant)
        assertEquals("Mercado", resultExito.categoryName)
    }

    @Test
    fun `MerchantClassifier classifies coffee shops and flags ant expenses correctly`() {
        val resultTostao = classifier.classify("tostao cafe 93")
        assertEquals("Tostao", resultTostao.normalizedMerchant)
        assertEquals("Café y Antojos", resultTostao.categoryName)
        assertTrue(resultTostao.isAntCandidate)

        val resultValdez = classifier.classify("juan valdez aeropuerto")
        assertEquals("Juan Valdez", resultValdez.normalizedMerchant)
        assertTrue(resultValdez.isAntCandidate)
    }

    @Test
    fun `MerchantClassifier handles typo tolerance with Levenshtein distance`() {
        val resultTypo = classifier.classify("tostaoo")
        assertEquals("Tostao", resultTypo.normalizedMerchant)
        assertTrue(resultTypo.confidence >= 0.60f)
    }

    // --- 2. COLOMBIAN BANK SMS PARSER ---

    @Test
    fun `ColombianBankSmsParser parses Bancolombia purchase correctly`() {
        val sms = "Bancolombia le informa compra por $45.000 en TIENDAS D1 con t.deb *1234 el 07/10/2026."
        val parsed = smsParser.parse(sms)

        assertTrue(parsed.isSuccess)
        assertEquals(45_000L, parsed.amount)
        assertEquals("Tiendas D1", parsed.merchant)
        assertEquals("Bancolombia", parsed.walletName)
        assertEquals("Mercado", parsed.suggestedCategory)
        assertFalse(parsed.isAntCandidate)
    }

    @Test
    fun `ColombianBankSmsParser parses Nequi send with ant candidate detection`() {
        val sms = "Enviaste $18.500 a Juan Valdez desde tu Nequi. Saldo disponible: $120.000."
        val parsed = smsParser.parse(sms)

        assertTrue(parsed.isSuccess)
        assertEquals(18_500L, parsed.amount)
        assertEquals("Juan Valdez", parsed.merchant)
        assertEquals("Nequi", parsed.walletName)
        assertEquals("Café y Antojos", parsed.suggestedCategory)
        assertTrue(parsed.isAntCandidate)
    }

    @Test
    fun `ColombianBankSmsParser parses DaviPlata transfer correctly`() {
        val sms = "DaviPlata: Pasaste $60.000 al 3101234567. Nuevo saldo $240.000."
        val parsed = smsParser.parse(sms)

        assertTrue(parsed.isSuccess)
        assertEquals(60_000L, parsed.amount)
        assertEquals("Daviplata", parsed.walletName)
    }

    @Test
    fun `ColombianBankSmsParser returns failure on unrecognized text without amounts`() {
        val sms = "Hola amigo, nos vemos hoy a las 5 para el café."
        val parsed = smsParser.parse(sms)

        assertFalse(parsed.isSuccess)
        assertEquals(0L, parsed.amount)
    }

    // --- 3. EWMA ANOMALY DETECTOR ---

    @Test
    fun `EwmaAnomalyDetector detects statistically anomalous expense spike`() {
        // Gastos cotidianos entre $20k y $30k
        val normalExpenses = listOf(22_000L, 25_000L, 21_000L, 24_000L, 26_000L, 23_000L, 25_000L)
        val normalEval = ewmaDetector.evaluate(normalExpenses)
        assertFalse(normalEval.isAnomaly)

        // Pico anómalo de $850.000 en el último día
        val spikedExpenses = normalExpenses + 850_000L
        val spikedEval = ewmaDetector.evaluate(spikedExpenses)
        assertTrue(spikedEval.isAnomaly)
        assertTrue(spikedEval.deviationZScore > 2.0)
    }

    // --- 4. QUINCENA CYCLE & OXYGEN RUNWAY ---

    @Test
    fun `QuincenaCycleAnalyzer projects first quincena payout day 15 accurately`() {
        val mockCal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 10)
        }
        val report = quincenaAnalyzer.analyze(
            currentLiquidBalance = 800_000L,
            quincenaSpentExpenses = 300_000L,
            weightedDailyAverage = 30_000L,
            calendar = mockCal
        )

        assertEquals(10, report.currentDayOfMonth)
        assertEquals(15, report.targetPayoutDay)
        assertEquals(5, report.daysRemainingToPayout)
        assertEquals(10, report.daysElapsedInQuincena)
        // 800.000 / 30.000 = 26 días de oxígeno
        assertEquals(26, report.oxygenDays)
        assertEquals(QuincenaHealthStatus.HOLGADO, report.status)
    }

    @Test
    fun `QuincenaCycleAnalyzer detects deficit in second quincena when burn rate is too fast`() {
        val mockCal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 25)
            set(Calendar.MONTH, Calendar.OCTOBER)
        }
        val report = quincenaAnalyzer.analyze(
            currentLiquidBalance = 50_000L,
            quincenaSpentExpenses = 800_000L, // 80.000 diarios
            weightedDailyAverage = 80_000L,
            calendar = mockCal
        )

        assertEquals(25, report.currentDayOfMonth)
        assertEquals(6, report.daysRemainingToPayout) // 31 - 25 = 6 días
        assertEquals(QuincenaHealthStatus.AJUSTADO, report.status)
    }

    // --- 5. FINANCIAL HEALTH SCORER (TRIAJE 0-100) ---

    @Test
    fun `FinancialHealthScorer assigns HOLGADO to disciplined saver`() {
        val score = healthScorer.calculateScore(
            budgetConsumptionRatio = 0.50, // 35 pts
            antExpenseRatio = 0.04,        // 25 pts
            oxygenDays = 35,               // 25 pts
            hasActivePocketContributions = true // 15 pts
        )

        assertEquals(100, score.totalScore)
        assertEquals(TriageLevel.TRIAGE_HOLGADO, score.triageLevel)
    }

    @Test
    fun `FinancialHealthScorer assigns CRITICO when liquidity and budget are broken`() {
        val score = healthScorer.calculateScore(
            budgetConsumptionRatio = 1.35, // 0 pts
            antExpenseRatio = 0.35,        // 0 pts
            oxygenDays = 3,                // 2 pts
            hasActivePocketContributions = false // 5 pts
        )

        assertTrue(score.totalScore < 40)
        assertEquals(TriageLevel.TRIAGE_CRITICO, score.triageLevel)
    }

    // --- 6. NLG PARAMUNO DIALOGUE ENGINE ---

    @Test
    fun `MuggyDialogueEngine generates warm Colombian message without guilt`() {
        val score = healthScorer.calculateScore(0.50, 0.04, 30, true)
        val insight = dialogueEngine.generateInsight(
            userName = "Valentina",
            score = score,
            liquidBalance = Money(1_500_000L),
            antExpensesTotal = Money(45_000L),
            oxygenDays = 30,
            hourOfDay = 8
        )

        assertTrue(insight.greeting.contains("¡Buenos días, Valentina!"))
        assertTrue(insight.keyMetricSummary.contains("$ 1.500.000"))
        assertEquals(DialogueActionType.CONTRIBUTE_POCKET, insight.actionType)
        assertNotNull(insight.actionButtonText)
    }

    // --- 7. COMPLETE ANDINA INTELLIGENCE CORE BENCHMARK (< 15 ms) ---

    @Test
    fun `AndinaIntelligenceCore executes full diagnosis in less than 15 ms offline`() {
        val expensesHistory = listOf(15_000L, 20_000L, 18_000L, 22_000L, 19_000L, 25_000L, 21_000L)

        // Warm-up para carga inicial de clases en JVM
        intelligenceCore.diagnose(
            userName = "Warmup",
            liquidBalance = Money(1_000_000L),
            totalExpensesThisMonth = Money(200_000L),
            antExpensesTotal = Money(20_000L),
            dailyExpensesHistory = expensesHistory,
            monthlyBudgetLimit = Money(1_000_000L),
            hasActivePocketContributions = true
        )

        // Medir tiempo de inferencia local en estado estacionario
        val executionTimeMs = measureTimeMillis {
            val diagnosis = intelligenceCore.diagnose(
                userName = "Valentina",
                liquidBalance = Money(2_400_000L),
                totalExpensesThisMonth = Money(650_000L),
                antExpensesTotal = Money(72_000L),
                dailyExpensesHistory = expensesHistory,
                monthlyBudgetLimit = Money(1_800_000L),
                hasActivePocketContributions = true
            )

            assertNotNull(diagnosis)
            assertTrue(diagnosis.healthScore.totalScore in 0..100)
            assertTrue(diagnosis.quincenaReport.oxygenDays > 0)
            assertNotNull(diagnosis.insight.diagnosis)
        }

        // SLA: Inferencia estrictamente menor a 15 milisegundos (ARQUITECTURA.md §10)
        assertTrue("La inferencia local tomó $executionTimeMs ms, superando el SLA de 15ms", executionTimeMs < 15L)
    }
}

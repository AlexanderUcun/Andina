package com.andina.finanzas.core.intelligence.nlg

import com.andina.finanzas.core.intelligence.triage.FinancialHealthScore
import com.andina.finanzas.core.intelligence.triage.TriageLevel
import com.andina.finanzas.domain.model.Money
import java.util.Calendar

enum class DialogueActionType {
    NONE,
    CREATE_BUDGET,
    CONTRIBUTE_POCKET,
    OPEN_ANT_MIRROR,
    OPEN_SMS_IMPORT
}

data class MuggyInsight(
    val title: String,
    val greeting: String,
    val diagnosis: String,
    val keyMetricSummary: String,
    val actionSuggestion: String?,
    val actionButtonText: String?,
    val actionType: DialogueActionType
)

/**
 * Motor NLG Paramuno Contextual (ARQUITECTURA.md §10 Nivel 4).
 * Genera diálogos cálidos modulares con modismos colombianos andinos, inyectando métricas exactas en pesos COP.
 */
class MuggyDialogueEngine {

    fun generateInsight(
        userName: String,
        score: FinancialHealthScore,
        liquidBalance: Money,
        antExpensesTotal: Money,
        oxygenDays: Int,
        hourOfDay: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    ): MuggyInsight {
        // 1. Saludo contextual según hora
        val greeting = when (hourOfDay) {
            in 5..11 -> "¡Buenos días, $userName! El páramo amanece despejado."
            in 12..18 -> "¡Buenas tardes, $userName! ¿Cómo marcha esa caminata?"
            else -> "Buenas noches, $userName. Hora de serenar las cuentas."
        }

        // 2. Diagnóstico empático y sugerencia de acción según nivel de triaje
        val runwayDays = if (oxygenDays == 1) "1 día" else "$oxygenDays días"

        return when (score.triageLevel) {
            TriageLevel.TRIAGE_HOLGADO -> {
                MuggyInsight(
                    title = "Salud Financiera en Plenitud 🌿",
                    greeting = greeting,
                    diagnosis = "Tus finanzas marchan con vigor andino. Tu tasa de gasto está bajo control y cuentas con $runwayDays de oxígeno disponibles.",
                    keyMetricSummary = "Tienes ${liquidBalance.formatCop()} disponibles y reservas sólidas.",
                    actionSuggestion = "¿Pasamos parte del saldo sobrante a florecer una meta de ahorro?",
                    actionButtonText = "🌱 Aportar a Bolsillo",
                    actionType = DialogueActionType.CONTRIBUTE_POCKET
                )
            }
            TriageLevel.TRIAGE_PREVENTIVO -> {
                MuggyInsight(
                    title = "Equilibrio Estable con Mirada Atenta 🧐",
                    greeting = greeting,
                    diagnosis = "El sendero está despejado, aunque los antojos y compras pequeñas sumaron ${antExpensesTotal.formatCop()} este periodo.",
                    keyMetricSummary = "Tu colchón actual te brinda $runwayDays de oxígeno cubiertos.",
                    actionSuggestion = "Revisemos el impacto de esos gastos hormiga para proteger tu cosecha.",
                    actionButtonText = "🐜 Ver Espejo Anual",
                    actionType = DialogueActionType.OPEN_ANT_MIRROR
                )
            }
            TriageLevel.TRIAGE_AJUSTE -> {
                MuggyInsight(
                    title = "Ajustemos el Paso en el Camino 🧭",
                    greeting = greeting,
                    diagnosis = "El terreno se puso un poco empinado este mes. Tus gastos se están acelerando antes de terminar la quincena.",
                    keyMetricSummary = "Dispones de ${liquidBalance.formatCop()} para los próximos $runwayDays.",
                    actionSuggestion = "Fijemos un presupuesto mensual en tus categorías principales para cuidar el caudal.",
                    actionButtonText = "📊 Fijar Presupuesto",
                    actionType = DialogueActionType.CREATE_BUDGET
                )
            }
            TriageLevel.TRIAGE_CRITICO -> {
                MuggyInsight(
                    title = "Refugio y Protección de Recursos 🛡️",
                    greeting = greeting,
                    diagnosis = "Hay neblina espesa en la liquidez inmediata ($runwayDays de oxígeno). Tranquilo, todo río encuentra su cauce con orden.",
                    keyMetricSummary = "Saldo disponible actual: ${liquidBalance.formatCop()}.",
                    actionSuggestion = "Prioriza solo los gastos indispensables y pausa salidas hasta el próximo pago.",
                    actionButtonText = "💡 Diagnóstico SOS",
                    actionType = DialogueActionType.NONE
                )
            }
        }
    }
}

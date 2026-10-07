package com.andina.finanzas.domain.usecase

import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.domain.model.TransactionType
import javax.inject.Inject

/**
 * Caso de uso para clasificar si un movimiento es un "Gasto Hormiga".
 *
 * Criterios de blindaje:
 * 1. Solo aplica para movimientos de tipo [TransactionType.GASTO].
 * 2. Si el usuario marcó explícitamente el flag [isAntOverride], se respeta su decisión absoluta.
 * 3. Si la categoría está marcada como candidata a hormiga Y el monto es menor o igual al umbral
 *    predeterminado ($25.000 COP), se sugiere como gasto hormiga.
 */
class DetectAntExpensesUseCase @Inject constructor() {

    companion object {
        val DEFAULT_ANT_THRESHOLD = Money(25_000L) // $25.000 COP
    }

    operator fun invoke(
        type: TransactionType,
        amount: Money,
        isCategoryAntCandidate: Boolean,
        isAntOverride: Boolean? = null,
        threshold: Money = DEFAULT_ANT_THRESHOLD
    ): Boolean {
        if (type != TransactionType.GASTO) return false
        if (isAntOverride != null) return isAntOverride

        return isCategoryAntCandidate && amount.pesos <= threshold.pesos
    }
}

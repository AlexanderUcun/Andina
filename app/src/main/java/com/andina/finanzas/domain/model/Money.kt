package com.andina.finanzas.domain.model

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToLong

/**
 * Value Object inmutable para representar dinero exacto en Pesos Colombianos (COP).
 *
 * Elimina cualquier problema de imprecisión por coma flotante (Double/Float)
 * encapsulando un entero de 64 bits [Long] que representa la cantidad exacta en pesos.
 */
@JvmInline
value class Money(val pesos: Long) : Comparable<Money> {

    operator fun plus(other: Money): Money = Money(pesos + other.pesos)

    operator fun minus(other: Money): Money = Money(pesos - other.pesos)

    operator fun times(factor: Int): Money = Money(pesos * factor)

    operator fun times(factor: Long): Money = Money(pesos * factor)

    operator fun times(factor: Double): Money = Money((pesos * factor).roundToLong())

    operator fun div(divisor: Long): Money {
        require(divisor != 0L) { "No se puede dividir dinero entre cero" }
        return Money(pesos / divisor)
    }

    operator fun unaryMinus(): Money = Money(-pesos)

    override fun compareTo(other: Money): Int = pesos.compareTo(other.pesos)

    val isPositive: Boolean get() = pesos > 0L
    val isZero: Boolean get() = pesos == 0L
    val isNegative: Boolean get() = pesos < 0L
    val absoluteValue: Money get() = Money(abs(pesos))

    /**
     * Formatea el monto al estándar colombiano con separador de miles con punto (.),
     * sin decimales.
     *
     * Ejemplos:
     * - `Money(14850000).formatCop()` -> "$ 14.850.000"
     * - `Money(14850000).formatCop(includeCode = true)` -> "$ 14.850.000 COP"
     * - `Money(-50000).formatCop()` -> "-$ 50.000"
     */
    fun formatCop(includeSymbol: Boolean = true, includeCode: Boolean = false): String {
        val symbols = DecimalFormatSymbols(Locale("es", "CO")).apply {
            groupingSeparator = '.'
        }
        val formatter = DecimalFormat("#,###", symbols)
        val formattedNumber = formatter.format(abs(pesos))

        val signPrefix = if (pesos < 0) "-" else ""
        val symbolPrefix = if (includeSymbol) "$ " else ""
        val codeSuffix = if (includeCode) " COP" else ""

        return "$signPrefix$symbolPrefix$formattedNumber$codeSuffix"
    }

    override fun toString(): String = formatCop(includeSymbol = true, includeCode = true)

    companion object {
        val ZERO = Money(0L)

        fun fromPesos(amount: Long): Money = Money(amount)

        /**
         * Parsea una cadena de texto eliminando símbolos monetarios, puntos y espacios.
         * Retorna `null` si no es un número entero válido.
         */
        fun fromStringOrNull(raw: String?): Money? {
            if (raw.isNullOrBlank()) return null
            val isNeg = raw.trim().startsWith("-")
            val cleanDigits = raw.replace(Regex("[^0-9]"), "")
            if (cleanDigits.isEmpty()) return null
            val parsedLong = cleanDigits.toLongOrNull() ?: return null
            return Money(if (isNeg) -parsedLong else parsedLong)
        }
    }
}

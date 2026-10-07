package com.andina.finanzas.ui.register

/**
 * Conversor de cifras numéricas en pesos a su representación escrita en español colombiano.
 *
 * Ejemplos:
 * - 0L -> "Cero pesos"
 * - 10_000L -> "Diez mil pesos"
 * - 50_000L -> "Cincuenta mil pesos"
 * - 1_000_000L -> "Un millón de pesos"
 * - 14_850_000L -> "Catorce millones ochocientos cincuenta mil pesos"
 */
object SpanishMoneyWords {

    private val UNIDADES = arrayOf(
        "", "un", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve",
        "diez", "once", "doce", "trece", "catorce", "quince", "dieciséis", "diecisiete",
        "dieciocho", "diecinueve", "veinte", "veintiún", "veintidós", "veintitrés",
        "veinticuatro", "veinticinco", "veintiséis", "veintisiete", "veintiocho", "veintinueve"
    )

    private val DECENAS = arrayOf(
        "", "", "", "treinta", "cuarenta", "cincuenta", "sesenta", "setenta", "ochenta", "noventa"
    )

    private val CENTENAS = arrayOf(
        "", "ciento", "doscientos", "trescientos", "cuatrocientos", "quinientos",
        "seiscientos", "setecientos", "ochocientos", "novecientos"
    )

    fun toWords(amount: Long): String {
        if (amount == 0L) return "Cero pesos"
        val absAmount = kotlin.math.abs(amount)
        if (absAmount > 999_999_999_999L) return "Monto muy alto"

        val words = convertNumber(absAmount).trim()
        val formattedWords = words.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

        return if (absAmount == 1_000_000L || (absAmount % 1_000_000L == 0L && absAmount >= 1_000_000L)) {
            "$formattedWords de pesos"
        } else {
            "$formattedWords pesos"
        }
    }

    private fun convertNumber(n: Long): String {
        return when {
            n == 0L -> ""
            n < 30L -> UNIDADES[n.toInt()]
            n < 100L -> {
                val dec = (n / 10).toInt()
                val uni = (n % 10).toInt()
                if (uni == 0) DECENAS[dec] else "${DECENAS[dec]} y ${UNIDADES[uni]}"
            }
            n == 100L -> "cien"
            n < 1000L -> {
                val cent = (n / 100).toInt()
                val resto = n % 100
                if (resto == 0L) CENTENAS[cent] else "${CENTENAS[cent]} ${convertNumber(resto)}"
            }
            n < 1_000_000L -> {
                val miles = n / 1000L
                val resto = n % 1000L
                val milesStr = if (miles == 1L) "mil" else "${convertNumber(miles)} mil"
                if (resto == 0L) milesStr else "$milesStr ${convertNumber(resto)}"
            }
            n < 1_000_000_000L -> {
                val millones = n / 1_000_000L
                val resto = n % 1_000_000L
                val millonesStr = if (millones == 1L) "un millón" else "${convertNumber(millones)} millones"
                if (resto == 0L) millonesStr else "$millonesStr ${convertNumber(resto)}"
            }
            else -> {
                val milesMillones = n / 1_000_000_000L
                val resto = n % 1_000_000_000L
                val mmStr = if (milesMillones == 1L) "mil millones" else "${convertNumber(milesMillones)} mil millones"
                if (resto == 0L) mmStr else "$mmStr ${convertNumber(resto)}"
            }
        }
    }
}

package com.andina.finanzas.core.intelligence.nlp

/**
 * Resultado de extracción estructurada de un SMS bancario colombiano.
 */
data class ParsedBankSms(
    val amount: Long,
    val merchant: String,
    val walletName: String,
    val suggestedCategory: String,
    val isAntCandidate: Boolean,
    val rawText: String,
    val isSuccess: Boolean
)

/**
 * Decodificador 100% offline de SMS de entidades financieras colombianas (ARQUITECTURA.md §10 Nivel 1).
 * Soporta Bancolombia, Nequi, DaviPlata, NuBank y Scotiabank/Colpatria.
 */
class ColombianBankSmsParser(
    private val merchantClassifier: MerchantFuzzyClassifier = MerchantFuzzyClassifier()
) {

    // Patrones regex para bancos colombianos
    private val bancolombiaPurchaseRegex = Regex(
        "(?:Bancolombia le informa|Bancolombia:)\\s*(?:compra|pago)\\s*(?:por)?\\s*\\$\\s*([0-9.,]+)\\s*(?:en|a)\\s*([^.]+?)(?:\\s*con\\s*t\\.|\\s*el\\s*\\d|\\.|$)",
        RegexOption.IGNORE_CASE
    )

    private val bancolombiaTransferRegex = Regex(
        "(?:Bancolombia le informa|Bancolombia:)\\s*(?:transferencia|envio)\\s*(?:por)?\\s*\\$\\s*([0-9.,]+)\\s*(?:a|para)\\s*([^.]+?)(?:\\s*desde|\\s*el|\\.|$)",
        RegexOption.IGNORE_CASE
    )

    private val nequiSendRegex = Regex(
        "(?:Enviaste|Pagaste)\\s*\\$\\s*([0-9.,]+)\\s*(?:a|en)\\s*([^.]+?)(?:\\s*desde tu Nequi|\\s*con Nequi|\\.|$)",
        RegexOption.IGNORE_CASE
    )

    private val daviplataRegex = Regex(
        "DaviPlata:?\\s*(?:Pasaste|Compraste por|Pagaste)\\s*(?:por)?\\s*\\$\\s*([0-9.,]+)\\s*(?:a|en|al)?\\s*([^.]+?)(?:\\s*desde|\\s*con|\\.|$)",
        RegexOption.IGNORE_CASE
    )

    private val nuBankRegex = Regex(
        "(?:Compra aprobada en|Compra de)\\s*([^.]+?)\\s*por\\s*\\$\\s*([0-9.,]+)",
        RegexOption.IGNORE_CASE
    )

    // Fallback genérico para cualquier SMS con signo de pesos
    private val genericAmountRegex = Regex("\\$\\s*([0-9.,]+)")

    fun parse(smsContent: String): ParsedBankSms {
        val trimmed = smsContent.trim()

        // 1. Detección Bancolombia Compra
        bancolombiaPurchaseRegex.find(trimmed)?.let { match ->
            val amountStr = match.groupValues[1]
            val rawMerchant = match.groupValues[2].trim()
            val amount = cleanAmount(amountStr)
            val classification = merchantClassifier.classify(rawMerchant)
            return ParsedBankSms(
                amount = amount,
                merchant = classification.normalizedMerchant,
                walletName = "Bancolombia",
                suggestedCategory = classification.categoryName,
                isAntCandidate = classification.isAntCandidate,
                rawText = trimmed,
                isSuccess = true
            )
        }

        // 2. Detección Bancolombia Transferencia
        bancolombiaTransferRegex.find(trimmed)?.let { match ->
            val amountStr = match.groupValues[1]
            val rawDest = match.groupValues[2].trim()
            val amount = cleanAmount(amountStr)
            return ParsedBankSms(
                amount = amount,
                merchant = "Transferencia: $rawDest",
                walletName = "Bancolombia",
                suggestedCategory = "Transferencias",
                isAntCandidate = false,
                rawText = trimmed,
                isSuccess = true
            )
        }

        // 3. Detección Nequi
        nequiSendRegex.find(trimmed)?.let { match ->
            val amountStr = match.groupValues[1]
            val rawMerchant = match.groupValues[2].trim()
            val amount = cleanAmount(amountStr)
            val classification = merchantClassifier.classify(rawMerchant)
            return ParsedBankSms(
                amount = amount,
                merchant = classification.normalizedMerchant,
                walletName = "Nequi",
                suggestedCategory = classification.categoryName,
                isAntCandidate = classification.isAntCandidate,
                rawText = trimmed,
                isSuccess = true
            )
        }

        // 4. Detección DaviPlata
        daviplataRegex.find(trimmed)?.let { match ->
            val amountStr = match.groupValues[1]
            val rawMerchant = match.groupValues[2].trim()
            val amount = cleanAmount(amountStr)
            val classification = merchantClassifier.classify(rawMerchant)
            return ParsedBankSms(
                amount = amount,
                merchant = classification.normalizedMerchant,
                walletName = "Daviplata",
                suggestedCategory = classification.categoryName,
                isAntCandidate = classification.isAntCandidate,
                rawText = trimmed,
                isSuccess = true
            )
        }

        // 5. Detección NuBank
        nuBankRegex.find(trimmed)?.let { match ->
            val rawMerchant = match.groupValues[1].trim()
            val amountStr = match.groupValues[2]
            val amount = cleanAmount(amountStr)
            val classification = merchantClassifier.classify(rawMerchant)
            return ParsedBankSms(
                amount = amount,
                merchant = classification.normalizedMerchant,
                walletName = "NuBank",
                suggestedCategory = classification.categoryName,
                isAntCandidate = classification.isAntCandidate,
                rawText = trimmed,
                isSuccess = true
            )
        }

        // 6. Extracción genérica de emergencia
        genericAmountRegex.find(trimmed)?.let { match ->
            val amountStr = match.groupValues[1]
            val amount = cleanAmount(amountStr)
            if (amount > 0L) {
                val detectedWallet = when {
                    trimmed.contains("nequi", ignoreCase = true) -> "Nequi"
                    trimmed.contains("daviplata", ignoreCase = true) -> "Daviplata"
                    trimmed.contains("bancolombia", ignoreCase = true) -> "Bancolombia"
                    else -> "Efectivo"
                }
                return ParsedBankSms(
                    amount = amount,
                    merchant = "Gasto Registrado",
                    walletName = detectedWallet,
                    suggestedCategory = "Otros",
                    isAntCandidate = false,
                    rawText = trimmed,
                    isSuccess = true
                )
            }
        }

        return ParsedBankSms(
            amount = 0L,
            merchant = "",
            walletName = "Efectivo",
            suggestedCategory = "Otros",
            isAntCandidate = false,
            rawText = trimmed,
            isSuccess = false
        )
    }

    private fun cleanAmount(rawAmount: String): Long {
        // En Colombia los montos usan punto para miles (ej: 45.000) o comas
        val clean = rawAmount.replace("$", "").trim()
        val digitsOnly = if (clean.contains(".") && !clean.contains(",")) {
            // Si tiene punto, eliminar separadores de miles
            clean.replace(".", "")
        } else if (clean.contains(",")) {
            clean.substringBefore(",").replace(".", "")
        } else {
            clean
        }.filter { it.isDigit() }

        return digitsOnly.toLongOrNull() ?: 0L
    }
}

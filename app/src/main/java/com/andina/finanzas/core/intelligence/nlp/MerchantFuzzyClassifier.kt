package com.andina.finanzas.core.intelligence.nlp

import java.text.Normalizer
import java.util.Locale
import kotlin.math.min

/**
 * Resultado de clasificación semántica de comercio.
 */
data class MerchantClassification(
    val normalizedMerchant: String,
    val categoryName: String,
    val isAntCandidate: Boolean,
    val confidence: Float
)

/**
 * Árbol Trie en memoria para búsqueda de prefijos ultrarrápida (< 1 ms) sin red.
 */
private class TrieNode {
    val children = mutableMapOf<Char, TrieNode>()
    var entry: MerchantEntry? = null
}

private data class MerchantEntry(
    val canonicalName: String,
    val categoryName: String,
    val isAntCandidate: Boolean
)

/**
 * Clasificador semántico difuso de comercios colombianos en dispositivo (ARQUITECTURA.md §10 Nivel 1).
 * Emplea Trie para coincidencia de prefijos y distancia Levenshtein para variaciones ortográficas / abreviaturas.
 */
class MerchantFuzzyClassifier {

    private val root = TrieNode()
    private val entries = mutableListOf<MerchantEntry>()

    init {
        seedKnownColombianMerchants()
    }

    private fun seedKnownColombianMerchants() {
        val database = listOf(
            // Mercado y Supermercados
            MerchantEntry("Tiendas D1", "Mercado", false),
            MerchantEntry("D1", "Mercado", false),
            MerchantEntry("Éxito", "Mercado", false),
            MerchantEntry("Carulla", "Mercado", false),
            MerchantEntry("Olímpica", "Mercado", false),
            MerchantEntry("Jumbo", "Mercado", false),
            MerchantEntry("Metro", "Mercado", false),
            MerchantEntry("Tiendas Ara", "Mercado", false),
            MerchantEntry("Ara", "Mercado", false),
            MerchantEntry("Ísimo", "Mercado", false),
            MerchantEntry("Surtimax", "Mercado", false),
            MerchantEntry("PriceSmart", "Mercado", false),

            // Café y Gastos Hormiga
            MerchantEntry("Tostao", "Café y Antojos", true),
            MerchantEntry("Juan Valdez", "Café y Antojos", true),
            MerchantEntry("Starbucks", "Café y Antojos", true),
            MerchantEntry("Dunkin Donuts", "Café y Antojos", true),
            MerchantEntry("Oxxo", "Café y Antojos", true),

            // Restaurantes y Comidas Rápidas
            MerchantEntry("Crepes & Waffles", "Restaurantes", false),
            MerchantEntry("El Corral", "Restaurantes", false),
            MerchantEntry("Frisby", "Restaurantes", false),
            MerchantEntry("KFC", "Restaurantes", false),
            MerchantEntry("McDonald's", "Restaurantes", false),
            MerchantEntry("Subway", "Restaurantes", false),
            MerchantEntry("Archies", "Restaurantes", false),
            MerchantEntry("Wok", "Restaurantes", false),
            MerchantEntry("Rappi", "Restaurantes", false),

            // Transporte y Combustible
            MerchantEntry("Uber", "Transporte", false),
            MerchantEntry("Didi", "Transporte", false),
            MerchantEntry("Cabify", "Transporte", false),
            MerchantEntry("Terpel", "Transporte", false),
            MerchantEntry("Primax", "Transporte", false),
            MerchantEntry("Texaco", "Transporte", false),
            MerchantEntry("Transmilenio", "Transporte", false),
            MerchantEntry("SITP", "Transporte", false),
            MerchantEntry("Metro de Medellín", "Transporte", false),

            // Servicios Públicos y Telefonía
            MerchantEntry("Claro", "Servicios", false),
            MerchantEntry("Tigo", "Servicios", false),
            MerchantEntry("Movistar", "Servicios", false),
            MerchantEntry("WOM", "Servicios", false),
            MerchantEntry("Enel Codensa", "Servicios", false),
            MerchantEntry("Vanti Gas Natural", "Servicios", false),
            MerchantEntry("Acueducto", "Servicios", false),
            MerchantEntry("EPM", "Servicios", false),

            // Salud y Farmacias
            MerchantEntry("Farmatodo", "Salud", false),
            MerchantEntry("Drogas La Rebaja", "Salud", false),
            MerchantEntry("Cruz Verde", "Salud", false),
            MerchantEntry("Pasteur", "Salud", false),

            // Entretenimiento y Suscripciones
            MerchantEntry("Netflix", "Entretenimiento", false),
            MerchantEntry("Spotify", "Entretenimiento", false),
            MerchantEntry("Cine Colombia", "Entretenimiento", false),
            MerchantEntry("Procinal", "Entretenimiento", false),
            MerchantEntry("Cinemark", "Entretenimiento", false)
        )

        val stopWords = setOf("tiendas", "restaurante", "supermercado", "almacen", "cafeteria")

        database.forEach { entry ->
            entries.add(entry)
            insertIntoTrie(normalize(entry.canonicalName), entry)
            // Variaciones abreviadas (excluyendo palabras genéricas)
            val words = normalize(entry.canonicalName).split(" ")
            words.forEach { w ->
                if (w.length >= 2 && !stopWords.contains(w)) {
                    insertIntoTrie(w, entry)
                }
            }
        }
        // Ordenar por longitud de nombre canónico descendente para priorizar nombres específicos
        entries.sortByDescending { it.canonicalName.length }
    }

    private fun insertIntoTrie(normalizedKey: String, entry: MerchantEntry) {
        var current = root
        for (char in normalizedKey) {
            current = current.children.getOrPut(char) { TrieNode() }
        }
        current.entry = entry
    }

    /**
     * Clasifica un texto en bruto de comercio (ej. "d1 cr 7", "tostao 93", "uber trip").
     */
    fun classify(rawMerchant: String): MerchantClassification {
        val clean = normalize(rawMerchant).trim()
        if (clean.isEmpty()) {
            return MerchantClassification(
                normalizedMerchant = "Gasto General",
                categoryName = "Otros",
                isAntCandidate = false,
                confidence = 0.3f
            )
        }

        // 1. Coincidencia por sub-frase completa (prioridad a nombres compuestos como "Tiendas D1")
        for (entry in entries) {
            val entryNorm = normalize(entry.canonicalName)
            if (clean.contains(entryNorm)) {
                return MerchantClassification(
                    normalizedMerchant = entry.canonicalName,
                    categoryName = entry.categoryName,
                    isAntCandidate = entry.isAntCandidate,
                    confidence = 0.99f
                )
            }
        }

        // 2. Coincidencia directa por Trie en tokens individuales
        val tokens = clean.split(" ", "-", "_", "/")
        for (token in tokens) {
            val trieMatch = searchPrefix(token)
            if (trieMatch != null) {
                return MerchantClassification(
                    normalizedMerchant = trieMatch.canonicalName,
                    categoryName = trieMatch.categoryName,
                    isAntCandidate = trieMatch.isAntCandidate,
                    confidence = 0.95f
                )
            }
        }

        // 2. Coincidencia difusa por distancia de Levenshtein (tolerancia tipográfica)
        var bestEntry: MerchantEntry? = null
        var minDistance = Int.MAX_VALUE
        val target = tokens.firstOrNull { it.length >= 3 } ?: clean

        for (entry in entries) {
            val entryNorm = normalize(entry.canonicalName)
            val dist = calculateLevenshteinDistance(target, entryNorm)
            if (dist < minDistance && dist <= 2) {
                minDistance = dist
                bestEntry = entry
            }
        }

        if (bestEntry != null) {
            val conf = 1.0f - (minDistance * 0.15f)
            return MerchantClassification(
                normalizedMerchant = bestEntry.canonicalName,
                categoryName = bestEntry.categoryName,
                isAntCandidate = bestEntry.isAntCandidate,
                confidence = conf.coerceIn(0.5f, 0.90f)
            )
        }

        // 3. Fallback genérico inteligente
        return MerchantClassification(
            normalizedMerchant = rawMerchant.trim().capitalizeWords(),
            categoryName = "Varios",
            isAntCandidate = false,
            confidence = 0.4f
        )
    }

    private fun searchPrefix(token: String): MerchantEntry? {
        if (token.length < 2) return null
        var current = root
        for (char in token) {
            val next = current.children[char] ?: return null
            current = next
        }
        return current.entry
    }

    private fun calculateLevenshteinDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j

        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = min(
                    min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        return dp[s1.length][s2.length]
    }

    private fun normalize(text: String): String {
        val normalized = Normalizer.normalize(text.lowercase(Locale.ROOT), Normalizer.Form.NFD)
        return normalized.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
    }

    private fun String.capitalizeWords(): String = split(" ")
        .joinToString(" ") { it.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase(Locale.ROOT) else char.toString() } }
}

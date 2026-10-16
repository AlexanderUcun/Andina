package com.andina.finanzas.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.andina.finanzas.ui.icons.CategoryIconCatalog
import com.andina.finanzas.ui.theme.FrailejonGold
import com.andina.finanzas.ui.theme.LegacyColorMapper

/**
 * Catálogo centralizado y unificado de iconos y colores de categorías para todo Andina Finanzas.
 */
object CategoryIconHelper {

    fun getIcon(iconName: String?): ImageVector {
        if (iconName.isNullOrBlank()) return Icons.Default.Category
        val normalized = iconName.lowercase().trim()

        // 1. Coincidencia directa con las claves del catálogo
        CategoryIconCatalog.options.find { it.key == normalized }?.let { return it.icon }

        // 2. Mapeo de sinónimos y términos en español a claves maestras del catálogo
        val resolvedKey = when (normalized) {
            "cafe", "snack", "snacks" -> "coffee"
            "fastfood", "food", "alimentacion", "almuerzo", "cena" -> "restaurant"
            "transport", "transporte", "bus", "vehiculo" -> "directions_bus"
            "delivery", "domicilios", "domicilio" -> "delivery_dining"
            "casa", "servicios del hogar", "servicios y hogar", "familia", "familia y hogar" -> "home"
            "entertainment", "ocio", "entretenimiento", "cine" -> "movie"
            "health", "salud", "salud y cuidado", "salud y bienestar", "bienestar", "spa", "cuidado personal", "belleza" -> "favorite"
            "hospital", "medicina", "farmacia" -> "local_hospital"
            "gym", "gimnasio", "deporte", "ejercicio" -> "fitness_center"
            "perro", "gato", "veterinaria" -> "pets"
            "education", "educacion", "estudio", "colegio", "universidad" -> "school"
            "viaje", "turismo", "vuelo", "vacaciones" -> "flight"
            "tech", "tecnologia", "suscripciones", "software" -> "computer"
            "regalo", "donacion" -> "card_giftcard"
            "fiesta", "celebracion" -> "celebration"
            "salary", "salario", "quincena" -> "payments"
            "freelance", "honorarios" -> "work"
            "investment", "inversiones", "rendimientos" -> "trending_up"
            "ropa", "mercado" -> "shopping_bag"
            "supermercado" -> "shopping_cart"
            "factura", "servicios publicos" -> "receipt"
            "combustible" -> "local_gas_station"
            "alcancia", "metas" -> "savings"
            else -> "category"
        }

        return CategoryIconCatalog.options.find { it.key == resolvedKey }?.icon ?: Icons.Default.Category
    }

    fun parseColor(hex: String, fallback: Color = FrailejonGold): Color {
        return runCatching {
            LegacyColorMapper.mapLegacyHex(hex)
        }.getOrDefault(fallback)
    }
}


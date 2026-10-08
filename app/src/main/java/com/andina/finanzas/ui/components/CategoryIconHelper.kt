package com.andina.finanzas.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Catálogo centralizado y unificado de iconos y colores de categorías para todo Andina Finanzas.
 */
object CategoryIconHelper {

    fun getIcon(iconName: String?): ImageVector {
        if (iconName.isNullOrBlank()) return Icons.Default.Category
        return when (iconName.lowercase().trim()) {
            "coffee", "cafe", "snack", "snacks" -> Icons.Default.LocalCafe
            "restaurant", "fastfood", "food", "alimentacion", "almuerzo", "cena" -> Icons.Default.Restaurant
            "directions_bus", "transport", "transporte", "bus", "gasolina" -> Icons.Default.DirectionsBus
            "delivery_dining", "delivery", "domicilios", "domicilio" -> Icons.Default.DeliveryDining
            "home", "casa", "servicios del hogar", "servicios y hogar", "servicios" -> Icons.Default.Home
            "movie", "entertainment", "ocio", "entretenimiento" -> Icons.Default.Movie
            "favorite", "health", "salud", "salud y cuidado", "bienestar" -> Icons.Default.Favorite
            "local_hospital", "hospital", "medicina" -> Icons.Default.LocalHospital
            "school", "education", "educacion", "estudio" -> Icons.Default.School
            "payments", "salary", "salario", "sueldo", "quincena" -> Icons.Default.Payments
            "work", "freelance", "honorarios", "trabajo" -> Icons.Default.Work
            "trending_up", "investment", "inversiones", "rendimientos" -> Icons.AutoMirrored.Filled.TrendingUp
            "shopping_bag", "compras", "mercado", "ropa" -> Icons.Default.ShoppingBag
            "receipt", "factura", "servicios publicos" -> Icons.Default.Receipt
            "add_circle", "otros ingresos" -> Icons.Default.AddCircle
            "more_horiz", "otros", "otros gastos" -> Icons.Default.MoreHoriz
            else -> Icons.Default.Category
        }
    }

    fun parseColor(hex: String, fallback: Color = Color(0xFF4A6B47)): Color {
        return runCatching {
            val clean = hex.removePrefix("#")
            Color(android.graphics.Color.parseColor("#$clean"))
        }.getOrDefault(fallback)
    }
}

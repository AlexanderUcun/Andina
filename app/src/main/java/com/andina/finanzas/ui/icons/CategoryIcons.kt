package com.andina.finanzas.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Representa una opción de icono seleccionable en el panel de personalización de categorías.
 */
data class CategoryIconOption(
    val key: String,
    val label: String,
    val icon: ImageVector
)

/**
 * Catálogo maestro de iconos para la personalización integral de categorías.
 */
object CategoryIconCatalog {
    val options = listOf(
        CategoryIconOption("restaurant", "Comida", Icons.Default.Restaurant),
        CategoryIconOption("coffee", "Café", Icons.Default.LocalCafe),
        CategoryIconOption("delivery_dining", "Domicilios", Icons.Default.DeliveryDining),
        CategoryIconOption("shopping_bag", "Compras", Icons.Default.ShoppingBag),
        CategoryIconOption("shopping_cart", "Mercado", Icons.Default.ShoppingCart),
        CategoryIconOption("directions_bus", "Transporte", Icons.Default.DirectionsBus),
        CategoryIconOption("local_gas_station", "Gasolina", Icons.Default.LocalGasStation),
        CategoryIconOption("home", "Hogar", Icons.Default.Home),
        CategoryIconOption("receipt", "Servicios", Icons.Default.Receipt),
        CategoryIconOption("movie", "Ocio", Icons.Default.Movie),
        CategoryIconOption("favorite", "Salud", Icons.Default.Favorite),
        CategoryIconOption("local_hospital", "Farmacia", Icons.Default.LocalHospital),
        CategoryIconOption("fitness_center", "Gimnasio", Icons.Default.FitnessCenter),
        CategoryIconOption("pets", "Mascotas", Icons.Default.Pets),
        CategoryIconOption("school", "Educación", Icons.Default.School),
        CategoryIconOption("flight", "Viajes", Icons.Default.Flight),
        CategoryIconOption("computer", "Tecnología", Icons.Default.Computer),
        CategoryIconOption("card_giftcard", "Regalos", Icons.Default.CardGiftcard),
        CategoryIconOption("celebration", "Eventos", Icons.Default.Celebration),
        CategoryIconOption("payments", "Sueldo", Icons.Default.Payments),
        CategoryIconOption("work", "Trabajo", Icons.Default.Work),
        CategoryIconOption("trending_up", "Inversión", Icons.AutoMirrored.Filled.TrendingUp),
        CategoryIconOption("savings", "Ahorro", Icons.Default.Savings),
        CategoryIconOption("category", "General", Icons.Default.Category)
    )
}

package com.andina.finanzas.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

/**
 * Rutas Type-Safe serializables para Jetpack Navigation Compose 2.8+.
 * Cumple con el estándar oficial de Kotlin Serialization sin strings codificados a mano.
 */
sealed interface AndinaDestination

/**
 * Pestaña principal: Dashboard general con patrimonio, balance del mes y accesos directos.
 */
@Serializable
data object HomeRoute : AndinaDestination

/**
 * Pestaña de Metas de Ahorro: Bolsillos individuales con crecimiento del frailejón.
 */
@Serializable
data object PocketsRoute : AndinaDestination

/**
 * Pestaña de Fuentes de Dinero: Billeteras, cuentas bancarias y efectivo.
 */
@Serializable
data object WalletsRoute : AndinaDestination

/**
 * Pestaña del Libro Mayor: Historial detallado de transacciones y gastos hormiga.
 */
@Serializable
data object TransactionsRoute : AndinaDestination

/**
 * Flujo de Registro Rápido: Modal / Pantalla protagonista con Muggy y teclado numérico propio.
 */
@Serializable
data class RegisterRoute(
    val initialType: String = "EXPENSE",
    val preselectedWalletId: Long? = null,
    val preselectedPocketId: Long? = null
) : AndinaDestination

/**
 * Pantalla de Análisis y Métricas: Gráficas de distribución y progreso financiero.
 */
@Serializable
data object StatsRoute : AndinaDestination

/**
 * Definición estructurada de las 4 pestañas de navegación inferior de Andina.
 *
 * Distribución simétrica aprobada:
 * [ Inicio ] · [ Bolsillos ] · 🟢 (+) Central · [ Billeteras ] · [ Movimientos ]
 */
enum class AndinaNavTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val destination: AndinaDestination
) {
    HOME(
        title = "Inicio",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        destination = HomeRoute
    ),
    POCKETS(
        title = "Bolsillos",
        selectedIcon = Icons.Filled.Savings,
        unselectedIcon = Icons.Outlined.Savings,
        destination = PocketsRoute
    ),
    WALLETS(
        title = "Billeteras",
        selectedIcon = Icons.Filled.AccountBalanceWallet,
        unselectedIcon = Icons.Outlined.AccountBalanceWallet,
        destination = WalletsRoute
    ),
    TRANSACTIONS(
        title = "Movimientos",
        selectedIcon = Icons.AutoMirrored.Filled.ReceiptLong,
        unselectedIcon = Icons.AutoMirrored.Outlined.ReceiptLong,
        destination = TransactionsRoute
    );

    companion object {
        val leftTabs = listOf(HOME, POCKETS)
        val rightTabs = listOf(WALLETS, TRANSACTIONS)

        fun fromDestination(destination: Any?): AndinaNavTab? {
            return when (destination) {
                is HomeRoute, HomeRoute::class -> HOME
                is PocketsRoute, PocketsRoute::class -> POCKETS
                is WalletsRoute, WalletsRoute::class -> WALLETS
                is TransactionsRoute, TransactionsRoute::class -> TRANSACTIONS
                else -> null
            }
        }
    }
}

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
 * Pantalla de Detalle y Edición de Movimiento Contable (Fase 10).
 */
@Serializable
data class TransactionDetailRoute(val transactionId: Long) : AndinaDestination

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
 * S19: El Consultorio Financiero de Muggy (Andina Intelligence Core On-Device).
 */
@Serializable
data object ConsultorioRoute : AndinaDestination

/**
 * Gestor de Presupuesto Mensual Adaptativo (Módulo 4).
 */
@Serializable
data object BudgetRoute : AndinaDestination

/**
 * Perfil y Configuración del Usuario (Módulo 1).
 */
@Serializable
data object UserProfileRoute : AndinaDestination

/**
 * Centro de Notificaciones y Alertas del Páramo (Módulo 5).
 */
@Serializable
data object NotificationsRoute : AndinaDestination

/**
 * Gestor de Gastos e Ingresos Fijos Programables (Módulo 8).
 */
@Serializable
data object RecurringRoute : AndinaDestination

/**
 * Hub de Personalización Total del Ecosistema (Módulo 9).
 */
@Serializable
data object CustomizationHubRoute : AndinaDestination

/**
 * Calendario Financiero Interactivo & Flujo de Caja (Módulo 10).
 */
@Serializable
data object CalendarRoute : AndinaDestination

/**
 * Exportación de Reportes PDF/CSV y Respaldos Locales (Módulo 6).
 */
@Serializable
data object ReportExportRoute : AndinaDestination

/**
 * Definición estructurada de las 4 pestañas maestras de navegación inferior de Andina.
 *
 * Distribución simétrica aprobada (Fase 2.1):
 * [ Inicio ] · [ Movimientos ] · 🟢 (+) Central · [ Patrimonio ] · [ Plan & IA ]
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
    TRANSACTIONS(
        title = "Movimientos",
        selectedIcon = Icons.AutoMirrored.Filled.ReceiptLong,
        unselectedIcon = Icons.AutoMirrored.Outlined.ReceiptLong,
        destination = TransactionsRoute
    ),
    WALLETS(
        title = "Patrimonio",
        selectedIcon = Icons.Filled.AccountBalanceWallet,
        unselectedIcon = Icons.Outlined.AccountBalanceWallet,
        destination = WalletsRoute
    ),
    CONSULTORIO(
        title = "Plan & IA",
        selectedIcon = Icons.Filled.Savings,
        unselectedIcon = Icons.Outlined.Savings,
        destination = ConsultorioRoute
    );

    companion object {
        val leftTabs = listOf(HOME, TRANSACTIONS)
        val rightTabs = listOf(WALLETS, CONSULTORIO)

        fun fromDestination(destination: Any?): AndinaNavTab? {
            return when (destination) {
                is HomeRoute, HomeRoute::class -> HOME
                is TransactionsRoute, TransactionsRoute::class -> TRANSACTIONS
                is CalendarRoute, CalendarRoute::class -> TRANSACTIONS // Redirección a Movimientos
                is WalletsRoute, WalletsRoute::class -> WALLETS
                is PocketsRoute, PocketsRoute::class -> WALLETS // Redirección a Patrimonio
                is ConsultorioRoute, ConsultorioRoute::class -> CONSULTORIO
                is StatsRoute, StatsRoute::class -> CONSULTORIO // Redirección a Plan & IA
                else -> null
            }
        }
    }
}

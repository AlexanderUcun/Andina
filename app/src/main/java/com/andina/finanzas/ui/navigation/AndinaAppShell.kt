package com.andina.finanzas.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.andina.finanzas.ui.components.AndinaBottomBar
import com.andina.finanzas.ui.components.AndinaButton
import com.andina.finanzas.ui.components.AndinaButtonVariant
import com.andina.finanzas.ui.components.FrostedGlassCard
import com.andina.finanzas.ui.home.HomeScreen
import com.andina.finanzas.ui.theme.AndeanPrimaryContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Gestor del estado raíz de navegación y experiencia global de Andina.
 */
@Stable
class AndinaAppState(
    val navController: NavHostController,
    val snackbarHostState: SnackbarHostState,
    val coroutineScope: CoroutineScope
) {
    /**
     * Determina la pestaña activa basada en la ruta serializada actual.
     */
    val currentTab: AndinaNavTab?
        @Composable
        get() {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val destination = backStackEntry?.destination ?: return AndinaNavTab.HOME
            return when {
                destination.hasRoute<HomeRoute>() -> AndinaNavTab.HOME
                destination.hasRoute<PocketsRoute>() -> AndinaNavTab.POCKETS
                destination.hasRoute<WalletsRoute>() -> AndinaNavTab.WALLETS
                destination.hasRoute<TransactionsRoute>() -> AndinaNavTab.TRANSACTIONS
                else -> null
            }
        }

    /**
     * La barra inferior solo se muestra en las pestañas principales del dashboard.
     */
    val showBottomBar: Boolean
        @Composable
        get() = currentTab != null

    /**
     * Navegación a pestañas con preservación de estado (`saveState`/`restoreState`)
     * y retorno natural al inicio al presionar Atrás.
     */
    fun navigateToTab(tab: AndinaNavTab) {
        navController.navigate(tab.destination) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    /**
     * Abre el modal/pantalla global de Registro Rápido con Muggy.
     */
    fun navigateToRegister(initialType: String = "EXPENSE") {
        navController.navigate(RegisterRoute(initialType = initialType))
    }

    /**
     * Navega a la pantalla de estadísticas financieras.
     */
    fun navigateToStats() {
        navController.navigate(StatsRoute)
    }

    /**
     * Navega al Consultorio Financiero de Muggy (S19).
     */
    fun navigateToConsultorio() {
        navController.navigate(ConsultorioRoute)
    }

    /**
     * Navega al Perfil y Configuración del Usuario (Módulo 1).
     */
    fun navigateToProfile() {
        navController.navigate(UserProfileRoute)
    }

    /**
     * Navega al Gestor de Presupuesto Mensual Adaptativo (Módulo 4).
     */
    fun navigateToBudget() {
        navController.navigate(BudgetRoute)
    }

    /**
     * Navega al Centro de Notificaciones y Alertas (Módulo 5).
     */
    fun navigateToNotifications() {
        navController.navigate(NotificationsRoute)
    }

    /**
     * Retrocede en la pila de navegación de forma segura.
     */
    fun navigateUp() {
        navController.navigateUp()
    }

    /**
     * Navega a la pantalla de detalle de un movimiento individual.
     */
    fun navigateToTransactionDetail(transactionId: Long) {
        navController.navigate(TransactionDetailRoute(transactionId))
    }

    /**
     * Muestra un mensaje flotante accesible en la UI.
     */
    fun showSnackbar(message: String) {
        coroutineScope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    /**
     * Muestra un mensaje flotante con botón de acción (ej. Deshacer).
     */
    fun showSnackbarWithAction(
        message: String,
        actionLabel: String = "Deshacer",
        onAction: () -> Unit
    ) {
        coroutineScope.launch {
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                onAction()
            }
        }
    }
}

/**
 * Recuerda la instancia de [AndinaAppState] sobreviviendo a recomposiciones.
 */
@Composable
fun rememberAndinaAppState(
    navController: NavHostController = rememberNavController(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): AndinaAppState {
    return remember(navController, snackbarHostState, coroutineScope) {
        AndinaAppState(
            navController = navController,
            snackbarHostState = snackbarHostState,
            coroutineScope = coroutineScope
        )
    }
}

/**
 * Shell raíz de la aplicación Andina Finanzas.
 *
 * Responsabilidades:
 * 1. Control de Scaffold global y `WindowInsets(0)` para arquitectura Edge-to-Edge pura.
 * 2. Visualización sincronizada de la barra inferior [AndinaBottomBar].
 * 3. Integración del host de navegación type-safe y gestión de mensajes con [SnackbarHost].
 * 4. Animaciones de entrada/salida de la barra de navegación para evitar parpadeos.
 */
@Composable
fun AndinaAppShell(
    modifier: Modifier = Modifier,
    appState: AndinaAppState = rememberAndinaAppState()
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(
                hostState = appState.snackbarHostState,
                modifier = Modifier.padding(bottom = if (appState.showBottomBar) 80.dp else 16.dp)
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = appState.showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                AndinaBottomBar(
                    currentTab = appState.currentTab ?: AndinaNavTab.HOME,
                    onTabSelected = appState::navigateToTab,
                    onRegisterClick = { appState.navigateToRegister() }
                )
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { paddingValues ->
        NavHost(
            navController = appState.navController,
            startDestination = HomeRoute,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Pestaña 1: Inicio (Dashboard Maestro)
            composable<HomeRoute> {
                HomeScreen(
                    onNavigateToWallet = { appState.navigateToTab(AndinaNavTab.WALLETS) },
                    onNavigateToPocket = { appState.navigateToTab(AndinaNavTab.POCKETS) },
                    onNavigateToTransaction = { id -> appState.navigateToTransactionDetail(id) },
                    onNavigateToWalletsTab = { appState.navigateToTab(AndinaNavTab.WALLETS) },
                    onNavigateToPocketsTab = { appState.navigateToTab(AndinaNavTab.POCKETS) },
                    onNavigateToTransactionsTab = { appState.navigateToTab(AndinaNavTab.TRANSACTIONS) },
                    onNavigateToRegister = { appState.navigateToRegister() },
                    onNavigateToSettings = { appState.navigateToProfile() },
                    onNavigateToBudgets = { appState.navigateToBudget() },
                    onNavigateToNotifications = { appState.navigateToNotifications() },
                    onNavigateToConsultorio = { appState.navigateToConsultorio() },
                    onShowSnackbar = { msg -> appState.showSnackbar(msg) }
                )
            }

            // Pestaña 2: Bolsillos de Ahorro
            composable<PocketsRoute> {
                com.andina.finanzas.ui.pockets.PocketsScreen(
                    onShowSnackbar = { msg -> appState.showSnackbar(msg) }
                )
            }

            // Pestaña 3: Billeteras
            composable<WalletsRoute> {
                ShellPlaceholderScreen(
                    title = "Billeteras & Cuentas",
                    subtitle = "Efectivo, Nequi, Daviplata y cuentas sin guardar números confidenciales",
                    icon = Icons.Default.AccountBalanceWallet,
                    accentColor = Color(0xFF845400),
                    actionText = "Añadir Billetera",
                    onAction = { appState.navigateToRegister() }
                )
            }

            // Pestaña 4: Movimientos (Libro Mayor & Auditoría Financiera)
            composable<TransactionsRoute> {
                com.andina.finanzas.ui.transactions.TransactionsScreen(
                    onNavigateToDetail = { id -> appState.navigateToTransactionDetail(id) },
                    onShowSnackbar = { msg -> appState.showSnackbar(msg) },
                    onShowSnackbarWithAction = { msg, action, onAction ->
                        appState.showSnackbarWithAction(msg, action, onAction)
                    }
                )
            }

            // Pantalla de Detalle y Edición de Movimiento Contable
            composable<TransactionDetailRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<TransactionDetailRoute>()
                com.andina.finanzas.ui.transactions.detail.TransactionDetailScreen(
                    transactionId = route.transactionId,
                    onNavigateBack = { appState.navigateUp() },
                    onShowSnackbar = { msg -> appState.showSnackbar(msg) }
                )
            }

            // Pantalla / Modal de Registro Rápido
            composable<RegisterRoute> {
                com.andina.finanzas.ui.register.RegisterRoute(
                    onDone = { appState.navigateUp() },
                    onShowSnackbar = { message -> appState.showSnackbar(message) }
                )
            }

            // Pantalla de Estadísticas
            composable<StatsRoute> {
                ShellPlaceholderScreen(
                    title = "Métricas & Salud Financiera",
                    subtitle = "Gráficas Vico y distribución por categorías del mes",
                    icon = Icons.Default.BarChart,
                    accentColor = Color(0xFF587550),
                    actionText = "Volver al Inicio",
                    onAction = { appState.navigateUp() },
                    showBackArrow = true,
                    onBack = { appState.navigateUp() }
                )
            }

            // Pantalla S19: El Consultorio Financiero de Muggy
            composable<ConsultorioRoute> {
                com.andina.finanzas.ui.consultorio.ConsultorioScreen(
                    onNavigateBack = { appState.navigateUp() },
                    onNavigateToRegister = { _, _, _, _, _ ->
                        appState.navigateToRegister()
                    },
                    onNavigateToPockets = { appState.navigateToTab(AndinaNavTab.POCKETS) }
                )
            }

            // Pantalla de Perfil de Usuario (Módulo 1)
            composable<UserProfileRoute> {
                com.andina.finanzas.ui.profile.UserProfileScreen(
                    onNavigateBack = { appState.navigateUp() },
                    onShowSnackbar = { msg -> appState.showSnackbar(msg) }
                )
            }

            // Pantalla de Presupuesto Mensual Adaptativo (Módulo 4)
            composable<BudgetRoute> {
                com.andina.finanzas.ui.budget.BudgetScreen(
                    onNavigateBack = { appState.navigateUp() },
                    onShowSnackbar = { msg -> appState.showSnackbar(msg) }
                )
            }

            // Pantalla de Centro de Notificaciones y Alertas (Módulo 5)
            composable<NotificationsRoute> {
                com.andina.finanzas.ui.notifications.NotificationsScreen(
                    onNavigateBack = { appState.navigateUp() },
                    onShowSnackbar = { msg -> appState.showSnackbar(msg) }
                )
            }
        }
    }
}

/**
 * Pantalla provisional estilizada para validar la navegación y el back-stack
 * antes de que cada fase inserte su implementación definitiva.
 */
@Composable
private fun ShellPlaceholderScreen(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    actionText: String,
    onAction: () -> Unit,
    showBackArrow: Boolean = false,
    onBack: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showBackArrow) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Text(
                        text = "Atrás",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Ícono insignia dentro de círculo estilizado
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            FrostedGlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = AndeanPrimaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Andina Arquitectura Limpia",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = AndeanPrimaryContainer
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Módulo enlazado al grafo de navegación Type-Safe con soporte de restauración de estado (saveState/restoreState).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            AndinaButton(
                text = actionText,
                onClick = onAction,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}


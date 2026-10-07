package com.andina.finanzas.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andina.finanzas.ui.components.ecosystem.AndeanDewPullRefresh
import com.andina.finanzas.ui.home.components.AndeanEditorialBanner
import com.andina.finanzas.ui.home.components.HeroBalanceCard
import com.andina.finanzas.ui.home.components.HomeBudgetContent
import com.andina.finanzas.ui.home.components.HomePocketsCarousel
import com.andina.finanzas.ui.home.components.HomeWalletsCarousel
import com.andina.finanzas.ui.home.components.PeriodMetricsGrid
import com.andina.finanzas.ui.home.components.RecentActivitySection
import com.andina.finanzas.ui.theme.AndeanPrimary

/**
 * Pantalla Principal de Inicio (Master Dashboard) de Andina Finanzas.
 *
 * Cumple con todas las especificaciones de diseño "Andean Mist & Moss":
 * - Gradiente orgánico vertical de fondo.
 * - Cabecera editorial con saludo horario y nivel de salud financiera.
 * - Tarjeta Hero de Patrimonio con odómetro numérico y ojo de privacidad.
 * - Métricas del periodo (Entró, Salió, Te queda).
 * - Barra animada de presupuesto mensual.
 * - Carrusel de billeteras activas y bolsillos de ahorro.
 * - Gota de rocío andino (AndeanDewPullRefresh) en pull-to-refresh.
 * - Últimos movimientos y estado vacío de Muggy.
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToWallet: (Long) -> Unit = {},
    onNavigateToPocket: (Long) -> Unit = {},
    onNavigateToTransaction: (Long) -> Unit = {},
    onNavigateToWalletsTab: () -> Unit = {},
    onNavigateToPocketsTab: () -> Unit = {},
    onNavigateToTransactionsTab: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToBudgets: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onShowSnackbar: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isDark = isSystemInDarkTheme()

    // Manejo de efectos secundarios
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is HomeEffect.NavigateToWallet -> onNavigateToWallet(effect.walletId)
                is HomeEffect.NavigateToPocket -> onNavigateToPocket(effect.pocketId)
                is HomeEffect.NavigateToTransactionDetail -> onNavigateToTransaction(effect.transactionId)
                is HomeEffect.NavigateToWalletsTab -> onNavigateToWalletsTab()
                is HomeEffect.NavigateToPocketsTab -> onNavigateToPocketsTab()
                is HomeEffect.NavigateToTransactionsTab -> onNavigateToTransactionsTab()
                is HomeEffect.NavigateToRegister -> onNavigateToRegister()
                is HomeEffect.NavigateToSettings -> onNavigateToSettings()
                is HomeEffect.NavigateToBudgets -> onNavigateToBudgets()
                is HomeEffect.NavigateToAntExpenses -> onShowSnackbar("Panel de gastos hormiga")
                is HomeEffect.NavigateToNotifications -> onNavigateToNotifications()
                is HomeEffect.ShowSnackbar -> onShowSnackbar(effect.message)
            }
        }
    }

    // Fondo con gradiente orgánico vertical andino
    val backgroundBrush = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF141713),
                Color(0xFF1B1E19)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFF5F3EF),
                Color(0xFFEBE6DC)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        AndeanDewPullRefresh(
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.onAction(HomeAction.RefreshData) }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 96.dp)
            ) {
                // Espacio de la barra de estado
                item {
                    Spacer(modifier = Modifier.statusBarsPadding())
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // 1. Barra Superior Editorial (Avatar + Saludo + Notificaciones)
                item {
                    HomeTopBar(
                        greeting = state.greeting,
                        userName = state.userName,
                        userLevel = state.userLevel,
                        onProfileClick = { viewModel.onAction(HomeAction.ProfileClick) },
                        onNotificationsClick = { viewModel.onAction(HomeAction.NotificationsClick) }
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                }

                // 2. Hero Card de Saldo Maestro y Privacidad
                item {
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        HeroBalanceCard(
                            totalNetWorth = state.totalNetWorth,
                            isPrivacyMode = state.isPrivacyMode,
                            paramoHealthLevel = state.paramoHealthLevel,
                            onTogglePrivacy = { viewModel.onAction(HomeAction.TogglePrivacyMode) },
                            onCardClick = { viewModel.onAction(HomeAction.SeeAllWalletsClick) }
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // 3. Grid de Periodo (Entró · Salió · Te queda)
                item {
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        PeriodMetricsGrid(
                            periodIncome = state.periodIncome,
                            periodExpense = state.periodExpense,
                            periodNetRemaining = state.periodNetRemaining,
                            isPrivacyMode = state.isPrivacyMode
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // 4. Barra de Presupuesto Mensual
                item {
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        HomeBudgetContent(
                            budgetTotal = state.budgetTotal,
                            budgetSpent = state.budgetSpent,
                            budgetProgress = state.budgetProgress,
                            isPrivacyMode = state.isPrivacyMode,
                            onClick = { viewModel.onAction(HomeAction.BudgetClick) }
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // 5. Banner de Sabiduría de Muggy (si no ha sido descartado)
                if (!state.isInsightDismissed && state.andinoInsight != null) {
                    item {
                        AndeanEditorialBanner(
                            insightText = state.andinoInsight!!,
                            onDismiss = { viewModel.onAction(HomeAction.DismissInsight) },
                            onClick = { onShowSnackbar("Consultorio financiero de Muggy") }
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                    }
                }

                // 6. Carrusel de Billeteras
                item {
                    HomeWalletsCarousel(
                        wallets = state.wallets,
                        onWalletClick = { id -> viewModel.onAction(HomeAction.WalletClick(id)) },
                        onSeeAllClick = { viewModel.onAction(HomeAction.SeeAllWalletsClick) }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // 7. Carrusel de Bolsillos de Ahorro
                item {
                    HomePocketsCarousel(
                        pockets = state.pockets,
                        onPocketClick = { id -> viewModel.onAction(HomeAction.PocketClick(id)) },
                        onSeeAllClick = { viewModel.onAction(HomeAction.SeeAllPocketsClick) }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // 8. Actividad Reciente (Últimos 5 movimientos o Empty State)
                item {
                    RecentActivitySection(
                        transactions = state.recentTransactions,
                        onTransactionClick = { id -> viewModel.onAction(HomeAction.TransactionClick(id)) },
                        onSeeAllClick = { viewModel.onAction(HomeAction.SeeAllTransactionsClick) },
                        onQuickRegisterClick = { viewModel.onAction(HomeAction.QuickRegisterClick) }
                    )
                }
            }
        }
    }
}

/**
 * Barra superior editorial con avatar de usuario y saludo dinámico.
 */
@Composable
private fun HomeTopBar(
    greeting: String,
    userName: String,
    userLevel: String,
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(onClick = onProfileClick)
        ) {
            // Avatar Circular con Inicial
            Surface(
                shape = CircleShape,
                color = AndeanPrimary,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = userName.firstOrNull()?.toString() ?: "V",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.size(12.dp))

            Column {
                Text(
                    text = "$greeting, $userName",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = userLevel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Botón de Campana de Notificaciones
        IconButton(
            onClick = onNotificationsClick,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Icon(
                imageVector = Icons.Default.NotificationsNone,
                contentDescription = "Notificaciones",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

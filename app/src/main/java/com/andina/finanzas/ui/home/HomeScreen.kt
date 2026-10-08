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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
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
    onNavigateToCalendar: () -> Unit = {},
    onNavigateToReportExport: () -> Unit = {},
    onNavigateToCustomization: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToConsultorio: () -> Unit = {},
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
                is HomeEffect.NavigateToCalendar -> onNavigateToCalendar()
                is HomeEffect.NavigateToReportExport -> onNavigateToReportExport()
                is HomeEffect.NavigateToCustomization -> onNavigateToCustomization()
                is HomeEffect.NavigateToConsultorioSms -> onNavigateToConsultorio()
                is HomeEffect.NavigateToAntExpenses -> onNavigateToConsultorio()
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
                contentPadding = PaddingValues(bottom = 110.dp)
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
                        avatarEmoji = state.avatarEmoji,
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

                // 2.1. Fila de Acciones Rápidas (Quick Actions)
                item {
                    HomeQuickActionsRow(
                        onSmsClick = { viewModel.onAction(HomeAction.SmsClick) },
                        onBudgetClick = { viewModel.onAction(HomeAction.BudgetClick) },
                        onCalendarClick = { viewModel.onAction(HomeAction.CalendarClick) },
                        onExportClick = { viewModel.onAction(HomeAction.ExportClick) }
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // 3. Grid de Periodo (Entró · Salió · Flujo Neto)
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
                if (state.showBudgetCard) {
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
                }

                // 5. Banner de Sabiduría de Muggy (si no ha sido descartado)
                if (!state.isInsightDismissed && state.andinoInsight != null) {
                    item {
                        AndeanEditorialBanner(
                            insightText = state.andinoInsight!!,
                            onDismiss = { viewModel.onAction(HomeAction.DismissInsight) },
                            onClick = onNavigateToConsultorio
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

                // 7. Carrusel de Bolsillos de Ahorro (controlado por personalización de módulos)
                if (state.showPocketsCard && state.pockets.isNotEmpty()) {
                    item {
                        HomePocketsCarousel(
                            pockets = state.pockets,
                            onPocketClick = { id -> viewModel.onAction(HomeAction.PocketClick(id)) },
                            onSeeAllClick = { viewModel.onAction(HomeAction.SeeAllPocketsClick) }
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }

                // 8. Actividad Reciente (Últimos 5 movimientos o Empty State)
                item {
                    RecentActivitySection(
                        transactions = state.recentTransactions,
                        onTransactionClick = { id -> viewModel.onAction(HomeAction.TransactionClick(id)) },
                        onSeeAllClick = { viewModel.onAction(HomeAction.SeeAllTransactionsClick) },
                        onQuickRegisterClick = { viewModel.onAction(HomeAction.QuickRegisterClick) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 9. Acceso directo a Personalización de Módulos y Tema
                item {
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Card(
                            onClick = { viewModel.onAction(HomeAction.CustomizationClick) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(AndeanPrimary.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Tune,
                                            contentDescription = null,
                                            tint = AndeanPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Personalizar Módulos y Tema",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Ajusta tarjetas visibles y paleta del páramo",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Fila de 4 Acciones Rápidas debajo del Saldo Maestro.
 */
@Composable
private fun HomeQuickActionsRow(
    onSmsClick: () -> Unit,
    onBudgetClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onExportClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuickActionButton(
            label = "Leer SMS",
            icon = Icons.Default.MarkEmailUnread,
            onClick = onSmsClick,
            modifier = Modifier.weight(1f)
        )
        QuickActionButton(
            label = "Presupuesto",
            icon = Icons.Default.PieChart,
            onClick = onBudgetClick,
            modifier = Modifier.weight(1f)
        )
        QuickActionButton(
            label = "Calendario",
            icon = Icons.Default.CalendarMonth,
            onClick = onCalendarClick,
            modifier = Modifier.weight(1f)
        )
        QuickActionButton(
            label = "Exportar",
            icon = Icons.Default.Description,
            onClick = onExportClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickActionButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = AndeanPrimary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
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
    avatarEmoji: String,
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
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = onProfileClick)
                .padding(vertical = 4.dp, horizontal = 4.dp)
        ) {
            // Avatar Circular con Emoji del Páramo y badge de ajuste
            Box {
                Surface(
                    shape = CircleShape,
                    color = AndeanPrimary.copy(alpha = 0.16f),
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = avatarEmoji.ifBlank { "🦅" },
                            fontSize = 22.sp
                        )
                    }
                }
                Surface(
                    shape = CircleShape,
                    color = AndeanPrimary,
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Ajustes",
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                    }
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


package com.andina.finanzas.ui.icons

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.andina.finanzas.ui.theme.LocalAndinaColors

/**
 * Catálogo semántico de iconos "Andean Gems" de Andina Finanzas.
 * Centraliza las claves para ratchetear los 183 iconos a 0 en pantallas.
 */
enum class AndinaIconKey {
    NAV_HOME,
    NAV_TRANSACTIONS,
    NAV_PATRIMONIO,
    NAV_PLAN_IA,
    ACTION_ADD,
    ACTION_EDIT,
    ACTION_DELETE,
    ACTION_FILTER,
    ACTION_SEARCH,
    ACTION_CLOSE,
    ACTION_BACK,
    FINANCE_INCOME,
    FINANCE_EXPENSE,
    FINANCE_TRANSFER,
    FINANCE_WALLET,
    FINANCE_POCKET,
    FINANCE_BUDGET,
    TOOL_CALENDAR,
    TOOL_REPORTS,
    TOOL_SECURITY,
    TOOL_PALETTE,
    TOOL_NOTIFICATIONS,
    CHEVRON_LEFT,
    CHEVRON_RIGHT
}

enum class AndinaIconState {
    DEFAULT,
    SELECTED,
    DISABLED,
    ALERT
}

/**
 * Renderizador unificado con soporte de microinteracciones en graphicsLayer.
 */
@Composable
fun AndinaIcon(
    icon: AndinaIconKey,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    state: AndinaIconState = AndinaIconState.DEFAULT
) {
    val colors = LocalAndinaColors.current

    val imageVector: ImageVector = when (icon) {
        AndinaIconKey.NAV_HOME -> if (state == AndinaIconState.SELECTED) Icons.Filled.Home else Icons.Outlined.Home
        AndinaIconKey.NAV_TRANSACTIONS -> if (state == AndinaIconState.SELECTED) Icons.AutoMirrored.Filled.ReceiptLong else Icons.AutoMirrored.Outlined.ReceiptLong
        AndinaIconKey.NAV_PATRIMONIO -> if (state == AndinaIconState.SELECTED) Icons.Filled.AccountBalanceWallet else Icons.Outlined.AccountBalanceWallet
        AndinaIconKey.NAV_PLAN_IA -> if (state == AndinaIconState.SELECTED) Icons.Filled.Savings else Icons.Outlined.Savings
        AndinaIconKey.ACTION_ADD -> Icons.Default.Add
        AndinaIconKey.ACTION_EDIT -> Icons.Default.Edit
        AndinaIconKey.ACTION_DELETE -> Icons.Default.Delete
        AndinaIconKey.ACTION_FILTER -> Icons.Default.FilterAlt
        AndinaIconKey.ACTION_SEARCH -> Icons.Default.Search
        AndinaIconKey.ACTION_CLOSE -> Icons.Default.Close
        AndinaIconKey.ACTION_BACK -> Icons.AutoMirrored.Filled.ArrowBack
        AndinaIconKey.FINANCE_INCOME -> Icons.Default.TrendingUp
        AndinaIconKey.FINANCE_EXPENSE -> Icons.Default.TrendingDown
        AndinaIconKey.FINANCE_TRANSFER -> Icons.Default.Payments
        AndinaIconKey.FINANCE_WALLET -> Icons.Default.AccountBalanceWallet
        AndinaIconKey.FINANCE_POCKET -> Icons.Default.Savings
        AndinaIconKey.FINANCE_BUDGET -> Icons.Default.BarChart
        AndinaIconKey.TOOL_CALENDAR -> Icons.Default.CalendarMonth
        AndinaIconKey.TOOL_REPORTS -> Icons.Default.Description
        AndinaIconKey.TOOL_SECURITY -> Icons.Default.Lock
        AndinaIconKey.TOOL_PALETTE -> Icons.Default.Palette
        AndinaIconKey.TOOL_NOTIFICATIONS -> Icons.Default.Notifications
        AndinaIconKey.CHEVRON_LEFT -> Icons.Default.ChevronLeft
        AndinaIconKey.CHEVRON_RIGHT -> Icons.Default.ChevronRight
    }

    val tint = when (state) {
        AndinaIconState.DEFAULT -> colors.textSecondary
        AndinaIconState.SELECTED -> colors.textLink
        AndinaIconState.DISABLED -> colors.textSecondary.copy(alpha = 0.38f)
        AndinaIconState.ALERT -> colors.statusDanger
    }

    val scale by animateFloatAsState(
        targetValue = if (state == AndinaIconState.SELECTED) 1.08f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "iconScale"
    )

    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier
            .size(size)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    )
}

/**
 * Badge de Icono con fondo mineral de platino y bisel (AndeanGlassIconBadge).
 */
@Composable
fun AndeanGlassIconBadge(
    icon: AndinaIconKey,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    badgeSize: Dp = 44.dp,
    iconSize: Dp = 22.dp,
    tint: Color? = null,
    backgroundColor: Color? = null
) {
    val colors = LocalAndinaColors.current
    val bg = backgroundColor ?: colors.heroTop.copy(alpha = 0.5f)

    Box(
        modifier = modifier
            .size(badgeSize)
            .clip(RoundedCornerShape(14.dp))
            .background(bg),
        contentAlignment = Alignment.Center
    ) {
        AndinaIcon(
            icon = icon,
            contentDescription = contentDescription,
            size = iconSize,
            state = if (tint != null) AndinaIconState.SELECTED else AndinaIconState.DEFAULT
        )
    }
}

package com.andina.finanzas.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.ui.components.FrostedGlassCard
import com.andina.finanzas.ui.theme.AndeanError
import com.andina.finanzas.ui.theme.AndeanErrorContainer
import com.andina.finanzas.ui.theme.AndeanPrimary
import com.andina.finanzas.ui.theme.AndeanPrimaryContainer
import com.andina.finanzas.ui.theme.CornerMedium

/**
 * Métrica individual de resumen de periodo financiero.
 */
@Composable
private fun MetricItem(
    title: String,
    amount: String,
    icon: ImageVector,
    iconColor: Color,
    iconBgColor: Color,
    modifier: Modifier = Modifier
) {
    // Escalamiento dinámico de fuente para evitar truncamientos en cifras millonarias
    val dynamicFontSize = when {
        amount.length > 15 -> 12.sp
        amount.length > 12 -> 13.sp
        amount.length > 9 -> 14.sp
        else -> 16.sp
    }

    FrostedGlassCard(
        modifier = modifier,
        shape = CornerMedium,
        elevation = 2.dp,
        contentPadding = 10.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(14.dp)
                )
            }

            Spacer(modifier = Modifier.size(6.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = amount,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = dynamicFontSize,
                fontWeight = FontWeight.Bold,
                fontFeatureSettings = "tnum"
            ),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
    }
}

/**
 * Grid de Métricas del Periodo Actual (Entró · Salió · Balance Neto).
 *
 * Muestra el flujo de caja del mes con respeto a la privacidad y códigos de color semánticos.
 */
@Composable
fun PeriodMetricsGrid(
    periodIncome: Money,
    periodExpense: Money,
    periodNetRemaining: Money,
    isPrivacyMode: Boolean,
    modifier: Modifier = Modifier
) {
    val incomeText = if (isPrivacyMode) "••••••" else periodIncome.formatCop()
    val expenseText = if (isPrivacyMode) "••••••" else periodExpense.formatCop()
    val netBalanceText = if (isPrivacyMode) "••••••" else periodNetRemaining.formatCop()

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Métrica 1: Entró (Ingresos)
        MetricItem(
            title = "Entró",
            amount = incomeText,
            icon = Icons.Default.ArrowDownward,
            iconColor = AndeanPrimary,
            iconBgColor = AndeanPrimaryContainer.copy(alpha = 0.6f),
            modifier = Modifier.weight(1f)
        )

        // Métrica 2: Salió (Gastos)
        MetricItem(
            title = "Salió",
            amount = expenseText,
            icon = Icons.Default.ArrowUpward,
            iconColor = AndeanError,
            iconBgColor = AndeanErrorContainer.copy(alpha = 0.6f),
            modifier = Modifier.weight(1f)
        )

        // Métrica 3: Flujo Neto del Mes
        MetricItem(
            title = "Flujo Neto",
            amount = netBalanceText,
            icon = Icons.Default.Wallet,
            iconColor = if (periodNetRemaining.pesos >= 0L) AndeanPrimary else AndeanError,
            iconBgColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.weight(1f)
        )
    }
}

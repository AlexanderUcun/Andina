package com.andina.finanzas.ui.home.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.ui.components.AndeanHyperCard
import com.andina.finanzas.ui.components.HyperCardVariant
import com.andina.finanzas.ui.theme.LocalAndinaColors

/**
 * Tarjeta de Control de Presupuesto Mensual (HomeBudgetContent).
 *
 * Muestra el porcentaje gastado del presupuesto con barra fluida animada,
 * en sintonía con la estética platino mineral y oro frailejón.
 */
@Composable
fun HomeBudgetContent(
    budgetTotal: Money,
    budgetSpent: Money,
    budgetProgress: Float,
    isPrivacyMode: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAndinaColors.current
    val animatedProgress by animateFloatAsState(
        targetValue = budgetProgress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "BudgetProgressAnimation"
    )

    val progressPercent = (budgetProgress * 100).toInt()

    // Color semántico según consumo
    val progressBrush = when {
        budgetProgress >= 1f -> Brush.horizontalGradient(listOf(colors.statusDanger, colors.statusDangerFill))
        budgetProgress >= 0.8f -> Brush.horizontalGradient(listOf(colors.statusWarning, colors.accentGold))
        else -> Brush.horizontalGradient(listOf(colors.accentGold, colors.statusSuccess))
    }

    val spentText = if (isPrivacyMode) "••••••" else budgetSpent.formatCop()
    val totalText = if (isPrivacyMode) "••••••" else budgetTotal.formatCop()

    AndeanHyperCard(
        modifier = modifier.fillMaxWidth(),
        variant = HyperCardVariant.CARD,
        onClick = onClick,
        contentPadding = 16.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(colors.accentGold.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PieChart,
                        contentDescription = null,
                        tint = colors.accentGold,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.size(10.dp))

                Column {
                    Text(
                        text = "Presupuesto Mensual",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "$spentText de $totalText",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$progressPercent%",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (budgetProgress >= 1f) colors.statusDanger else colors.textMoney
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = colors.textSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Barra de progreso fluida
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(colors.cardBorder)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(progressBrush)
            )
        }
    }
}

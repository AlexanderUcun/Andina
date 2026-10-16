package com.andina.finanzas.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.ui.components.AndeanHyperCard
import com.andina.finanzas.ui.components.HyperCardVariant
import com.andina.finanzas.ui.components.MoneyDisplayLevel
import com.andina.finanzas.ui.components.MoneyText
import com.andina.finanzas.ui.theme.LocalAndinaColors

/**
 * Tarjeta Hero de Patrimonio Consolidado y Privacidad Andina.
 *
 * Características:
 * 1. Chip interactivo combinado [$ 👁] en Oro Frailejón: signo pesos estilizado y discreto
 *    fusionado con el botón de ocultar/mostrar saldo.
 * 2. Odómetro inteligente y suave: únicamente rotan los dígitos que sufren cambios de saldo.
 * 3. Píldora de ecosistema andino ("Nivel Frailejón") balanceada en la cabecera.
 */
@Composable
fun HeroBalanceCard(
    totalNetWorth: Money,
    isPrivacyMode: Boolean,
    paramoHealthLevel: String,
    onTogglePrivacy: () -> Unit,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier,
    refreshTrigger: Any? = null
) {
    val haptic = LocalHapticFeedback.current
    val colors = LocalAndinaColors.current

    AndeanHyperCard(
        modifier = modifier.fillMaxWidth(),
        variant = HyperCardVariant.HERO,
        onClick = onCardClick,
        contentPadding = 22.dp
    ) {
        // 1. Fila Superior: Título editorial a la izquierda + Píldora de Ecosistema a la derecha
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PATRIMONIO CONSOLIDADO",
                style = MaterialTheme.typography.labelSmall,
                color = colors.textSecondary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.3.sp
            )

            // Píldora Platino / Oro de Salud Financiera
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = colors.cardBackground.copy(alpha = 0.8f),
                contentColor = colors.textPrimary,
                border = androidx.compose.foundation.BorderStroke(0.8.dp, colors.cardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = colors.statusSuccess,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = paramoHealthLevel,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 2. Fila Central: Saldo Maestro con Chip Dorado Integrado [$ 👁]
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            // Chip interactivo combinado: Signo pesos dorado + Icono de privacidad
            Surface(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onTogglePrivacy()
                },
                shape = RoundedCornerShape(12.dp),
                color = colors.accentGold.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(
                    0.8.dp,
                    colors.accentGold.copy(alpha = 0.35f)
                ),
                modifier = Modifier
                    .defaultMinSize(minHeight = 36.dp)
                    .semantics { role = Role.Button }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (totalNetWorth.pesos < 0) "-$" else "$",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.accentGold
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Icon(
                        imageVector = if (isPrivacyMode) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (isPrivacyMode) "Mostrar saldo" else "Ocultar saldo",
                        tint = colors.accentGold,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Saldo numérico principal
            if (isPrivacyMode) {
                Text(
                    text = "••••••••",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = colors.textMoneyDisplay
                )
            } else {
                MoneyText(
                    amount = totalNetWorth,
                    level = MoneyDisplayLevel.DISPLAY,
                    fontSize = 38.sp,
                    showSymbol = false, // Símbolo manejado en el chip [$ 👁]
                    showCode = true,
                    refreshTrigger = refreshTrigger
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Fila Inferior: Enlace sutil para ver desglose de billeteras
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total disponible en billeteras y reservas",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Billeteras",
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.textLink,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = colors.textLink,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

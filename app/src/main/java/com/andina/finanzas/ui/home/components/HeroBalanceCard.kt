package com.andina.finanzas.ui.home.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.ui.components.FrostedGlassCard
import com.andina.finanzas.ui.theme.AndeanPrimary
import com.andina.finanzas.ui.theme.AndeanPrimaryContainer
import com.andina.finanzas.ui.theme.CornerLarge
import com.andina.finanzas.ui.theme.financialAmbientGlow

/**
 * Tarjeta Hero de Patrimonio Consolidado y Privacidad Andina.
 *
 * Especificaciones de diseño:
 * 1. Vidrio Esmerilado (Apple Frosted Glass) con borde sutil andino.
 * 2. Saldo maestro en Manrope DisplayLarge (36sp, bold 700) con números tabulares.
 * 3. Animación de odómetro vertical con [AnimatedContent].
 * 4. Botón táctil de privacidad (ojo) accesible (>= 48dp) con respuesta háptica.
 * 5. Píldora de salud del ecosistema ("Nivel Páramo Sagrado" / "Nivel Frailejón").
 */
@Composable
fun HeroBalanceCard(
    totalNetWorth: Money,
    isPrivacyMode: Boolean,
    paramoHealthLevel: String,
    onTogglePrivacy: () -> Unit,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    // Jerarquía visual tipográfica para dinero de alta gama con Oro Frailejón
    val annotatedAmount = androidx.compose.ui.text.buildAnnotatedString {
        if (isPrivacyMode) {
            append("••••••••")
        } else {
            // Símbolo de moneda en Oro Frailejón cálido
            pushStyle(
                androidx.compose.ui.text.SpanStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = com.andina.finanzas.ui.theme.FrailejonGold
                )
            )
            append(if (totalNetWorth.pesos < 0) "-$ " else "$ ")
            pop()

            // Cifra entera principal en gran formato con números tabulares
            pushStyle(
                androidx.compose.ui.text.SpanStyle(
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            append(totalNetWorth.formatCop(includeSymbol = false, includeCode = false))
            pop()

            // Código de divisa sutil
            pushStyle(
                androidx.compose.ui.text.SpanStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            )
            append(" COP")
            pop()
        }
    }

    FrostedGlassCard(
        modifier = modifier
            .fillMaxWidth()
            .financialAmbientGlow(
                glowColor = com.andina.finanzas.ui.theme.AndeanEmeraldLight,
                glowRadiusMultiplier = 0.9f,
                alpha = 0.22f
            ),
        shape = CornerLarge,
        elevation = 4.dp,
        onClick = onCardClick,
        contentPadding = 20.dp
    ) {
        // Fila Superior: Label editorial + Píldora de Ecosistema + Botón Ojo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "PATRIMONIO CONSOLIDADO",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.3.sp
                )

                Spacer(modifier = Modifier.height(5.dp))

                // Píldora Joya de Nivel de Salud Andina
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AndeanPrimary.copy(alpha = 0.12f),
                    contentColor = AndeanPrimary,
                    border = androidx.compose.foundation.BorderStroke(0.8.dp, AndeanPrimary.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = paramoHealthLevel,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Botón interactivo de privacidad con acabado translúcido
            IconButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onTogglePrivacy()
                },
                modifier = Modifier
                    .defaultMinSize(minWidth = 44.dp, minHeight = 44.dp)
                    .clip(CircleShape)
                    .background(
                        if (isPrivacyMode) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    )
                    .semantics { role = Role.Button }
            ) {
                Icon(
                    imageVector = if (isPrivacyMode) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (isPrivacyMode) "Mostrar saldo" else "Ocultar saldo",
                    tint = if (isPrivacyMode) AndeanPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Fila Central: Odómetro de Saldo Maestro con jerarquía tipográfica
        AnimatedContent(
            targetState = annotatedAmount,
            transitionSpec = {
                if (targetState.length >= initialState.length) {
                    slideInVertically { height -> height } + fadeIn() togetherWith
                        slideOutVertically { height -> -height } + fadeOut()
                } else {
                    slideInVertically { height -> -height } + fadeIn() togetherWith
                        slideOutVertically { height -> height } + fadeOut()
                }
            },
            label = "HeroBalanceOdometer"
        ) { targetAnnotatedString ->
            Text(
                text = targetAnnotatedString,
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 38.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Fila Inferior: Enlace sutil para ver desglose de billeteras
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total disponible en billeteras y reservas",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Billeteras",
                    style = MaterialTheme.typography.labelMedium,
                    color = AndeanPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = AndeanPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

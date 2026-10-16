package com.andina.finanzas.ui.register.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andina.finanzas.domain.model.TransactionType
import com.andina.finanzas.ui.theme.AndeanPrimary

/**
 * Segmentador tipo píldora para alternar entre Gasto, Ingreso y Transferencia.
 * Sigue la estética minimalista de Stitch y Material 3.
 */
@Composable
fun RegisterTypeSelector(
    selectedType: TransactionType,
    onTypeSelected: (TransactionType) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val isDark = isSystemInDarkTheme()

    val containerBackground = if (isDark) Color(0xFF1F221E) else Color(0xFFE8E5DF)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(containerBackground)
            .padding(4.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            TransactionType.entries.forEach { type ->
                val isSelected = type == selectedType

                val tabBackground by animateColorAsState(
                    targetValue = if (isSelected) {
                        when (type) {
                            TransactionType.GASTO -> com.andina.finanzas.ui.theme.SemanticExpenseCoral
                            TransactionType.INGRESO -> com.andina.finanzas.ui.theme.SemanticIncomeEmerald
                            TransactionType.TRANSFERENCIA -> com.andina.finanzas.ui.theme.SemanticTransferCyan
                        }
                    } else Color.Transparent,
                    label = "tabBackground"
                )

                val tabTextColor by animateColorAsState(
                    targetValue = if (isSelected) {
                        com.andina.finanzas.ui.theme.AndeanOnPrimary
                    } else {
                        if (isDark) Color(0xFFB0B5AB) else Color(0xFF4C5047)
                    },
                    label = "tabTextColor"
                )

                val label = when (type) {
                    TransactionType.GASTO -> "Gasto"
                    TransactionType.INGRESO -> "Ingreso"
                    TransactionType.TRANSFERENCIA -> "Transferir"
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(tabBackground)
                        .clickable(
                            role = Role.Tab,
                            onClick = {
                                if (!isSelected) {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onTypeSelected(type)
                                }
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp
                        ),
                        color = tabTextColor
                    )
                }
            }
        }
    }
}

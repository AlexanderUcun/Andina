package com.andina.finanzas.ui.help

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.ui.components.AndeanHyperCard
import com.andina.finanzas.ui.theme.LocalAndinaColors
import com.andina.finanzas.ui.components.ecosystem.FrailejonView
import com.andina.finanzas.ui.theme.AndeanPrimary
import com.andina.finanzas.ui.theme.AndeanSlateInk
import com.andina.finanzas.ui.theme.FrailejonGold
import kotlin.math.ceil

/**
 * "La Vara del Páramo": Simulador visual e interactivo de proyección de ahorro.
 */
@Composable
fun PocketSimulatorCard(
    pocketName: String,
    targetAmount: Money,
    currentAmount: Money,
    modifier: Modifier = Modifier
) {
    var weeklySavingsSlider by remember { mutableFloatStateOf(50_000f) }

    val remainingMoney = (targetAmount.pesos - currentAmount.pesos).coerceAtLeast(0L)
    val weeklySavingsLong = weeklySavingsSlider.toLong().coerceAtLeast(10_000L)
    val weeksNeeded = if (remainingMoney <= 0L) 0 else ceil(remainingMoney.toDouble() / weeklySavingsLong).toInt()
    val monthsNeeded = ceil(weeksNeeded / 4.33).toInt()

    val progressFraction = if (targetAmount.pesos > 0L) {
        (currentAmount.pesos.toFloat() / targetAmount.pesos.toFloat()).coerceIn(0f, 1f)
    } else 1f

    AndeanHyperCard(
        shape = RoundedCornerShape(24.dp),
        contentPadding = 20.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            // Cabecera con icono limpio y título
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(AndeanPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.TrendingUp,
                            contentDescription = null,
                            tint = AndeanPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Simulador de Fondo",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Proyección para \"$pocketName\"",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Frailejón vivo proporcional al progreso
                FrailejonView(
                    progress = progressFraction,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Control deslizante interactivo de ahorro semanal
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Ahorro semanal propuesto:",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = Money(weeklySavingsLong).formatCop(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AndeanPrimary
                )
            }

            Slider(
                value = weeklySavingsSlider,
                onValueChange = { weeklySavingsSlider = it },
                valueRange = 10_000f..300_000f,
                steps = 28,
                colors = SliderDefaults.colors(
                    thumbColor = AndeanPrimary,
                    activeTrackColor = AndeanPrimary,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Proyección limpia sin duplicar avatar
            AnimatedContent(
                targetState = weeksNeeded,
                label = "WeeksNeededAnimation"
            ) { targetWeeks ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = AndeanPrimary.copy(alpha = 0.10f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (targetWeeks == 0) {
                            Text(
                                text = "🎉 ¡Meta Cumplida!",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = AndeanPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tu páramo está en plenitud 🌿 Todo el caudal reservado está listo para su propósito.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        } else {
                            Text(
                                text = "⏱️ ¡Alcanzas la meta en $targetWeeks semanas!",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = AndeanPrimary
                            )
                            Text(
                                text = "~$monthsNeeded meses al ritmo actual de ahorro",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (targetWeeks <= 6) {
                                    "Con este ritmo rápido y constante, tu meta florecerá muy pronto."
                                } else {
                                    "Paso a paso y con constancia montañera, las cumbres se conquistan con firmeza."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}


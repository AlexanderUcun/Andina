package com.andina.finanzas.ui.help

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.ui.components.ecosystem.FrailejonView
import com.andina.finanzas.ui.components.muggy.MuggyAvatar
import com.andina.finanzas.ui.components.muggy.MuggyPose
import kotlin.math.ceil

/**
 * "La Vara del Páramo": Simulador visual e interactivo de proyección de ahorro (AYUDAS_INTERACTIVAS.md §2.2).
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

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF6F4EF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Cabecera con Muggy y título
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    MuggyAvatar(
                        size = 44.dp,
                        pose = if (weeksNeeded <= 4) MuggyPose.CELEBRATING else MuggyPose.OBSERVING,
                        isInteractive = false
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "La Vara del Páramo",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C241E)
                        )
                        Text(
                            text = "Simulador dinámico para \"$pocketName\"",
                            fontSize = 12.sp,
                            color = Color(0xFF6B655D)
                        )
                    }
                }

                // Frailejón vivo proporcional al progreso
                FrailejonView(
                    progress = progressFraction,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Control deslizante interactivo de ahorro semanal
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Ahorro semanal propuesto:",
                    fontSize = 13.sp,
                    color = Color(0xFF4A453E)
                )
                Text(
                    text = Money(weeklySavingsLong).formatCop(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF435C3C)
                )
            }

            Slider(
                value = weeklySavingsSlider,
                onValueChange = { weeklySavingsSlider = it },
                valueRange = 10_000f..300_000f,
                steps = 28,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF435C3C),
                    activeTrackColor = Color(0xFF435C3C),
                    inactiveTrackColor = Color(0xFFD6CEBD)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tarjeta de revelación de proyección
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    AnimatedContent(
                        targetState = weeksNeeded,
                        label = "WeeksNeededAnimation"
                    ) { targetWeeks ->
                        if (targetWeeks == 0) {
                            Text(
                                text = "¡Meta cumplida! Tu páramo está en plenitud 🌿",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF435C3C)
                            )
                        } else {
                            Column {
                                Text(
                                    text = "¡Alcanzas la meta en $targetWeeks semanas! (~$monthsNeeded meses)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1D1B19)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (targetWeeks <= 6) {
                                        "Muggy dice: Con este ritmo rápido, tu meta está a la vuelta de la esquina."
                                    } else {
                                        "Muggy dice: Paso a paso y con constancia, las nieves se conquistan."
                                    },
                                    fontSize = 12.sp,
                                    color = Color(0xFF6B655D),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.andina.finanzas.ui.pockets

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.domain.model.Pocket
import com.andina.finanzas.ui.components.AndeanHyperCard
import com.andina.finanzas.ui.components.HyperCardVariant
import com.andina.finanzas.ui.components.ecosystem.FrailejonView
import com.andina.finanzas.ui.components.bouncyClick
import com.andina.finanzas.ui.theme.LocalAndinaColors
import com.andina.finanzas.ui.theme.specularGlassBorder
import com.andina.finanzas.ui.theme.AndeanMistCanvasBottom
import com.andina.finanzas.ui.theme.AndeanMistCanvasTop
import com.andina.finanzas.ui.theme.AndeanEmeraldDeep
import com.andina.finanzas.ui.theme.AndeanSlateInk
import com.andina.finanzas.ui.theme.AndinaAmber
import com.andina.finanzas.ui.theme.AndinaDeepForest
import com.andina.finanzas.ui.theme.AndinaMossGreen
import com.andina.finanzas.ui.theme.FrailejonGold
import kotlin.math.ceil

/**
 * Pantalla de Detalle de una Meta de Ahorro con Simulación "La Vara del Páramo".
 *
 * Incluye:
 * 1. Visualización botánica en gran formato del frailejón vivo.
 * 2. Desglose contable (Ahorrado, Meta, Faltante).
 * 3. "La Vara del Páramo": Simulador interactivo con Slider de ahorro semanal y cálculo de semanas restantes.
 * 4. Botones atómicos de aporte y retiro.
 */
@Composable
fun PocketDetailScreen(
    pocket: Pocket,
    onBackClick: () -> Unit,
    onContributeClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val animatedProgress by animateFloatAsState(
        targetValue = pocket.progress,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "PocketDetailProgress"
    )

    // Estado del simulador interactivo "La Vara del Páramo"
    var weeklySavingPesos by remember { mutableFloatStateOf(50_000f) }
    val remainingPesos = pocket.remaining.pesos.toFloat()
    val weeksRemaining = if (remainingPesos <= 0f) 0 else ceil(remainingPesos / weeklySavingPesos).toInt()
    val monthsRemaining = (weeksRemaining / 4.33f).toInt()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(AndeanMistCanvasTop, AndeanMistCanvasBottom)
                )
            )
            .statusBarsPadding()
            .verticalScroll(scrollState)
    ) {
        // Barra Superior
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .clip(CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = AndeanSlateInk
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = pocket.name,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = AndeanSlateInk
                )
            )
        }

        // Hero Botánico del Frailejón Vivo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            AndeanHyperCard(
                modifier = Modifier.fillMaxWidth(),
                variant = HyperCardVariant.HERO,
                contentPadding = 24.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Frailejón en gran formato
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .background(AndinaMossGreen.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        FrailejonView(
                            progress = animatedProgress,
                            size = 130.dp,
                            animateSway = true
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (pocket.isCompleted) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AndinaMossGreen
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "¡META 100% FLORECIDA!",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "${(pocket.progress * 100).toInt()}% alcanzado",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = AndinaMossGreen
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = pocket.currentBalance.formatCop(),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = FrailejonGold,
                            fontSize = 32.sp,
                            fontFeatureSettings = "tnum"
                        )
                    )

                    Text(
                        text = "de ${pocket.goal.formatCop()}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = AndeanSlateInk.copy(alpha = 0.7f),
                            fontFeatureSettings = "tnum"
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Barra de progreso
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = if (pocket.isCompleted) AndeanEmeraldDeep else FrailejonGold,
                        trackColor = AndeanSlateInk.copy(alpha = 0.1f),
                        strokeCap = StrokeCap.Round
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (pocket.isCompleted) "¡Tu frailejón ha florecido por completo!"
                        else "Te faltan ${pocket.remaining.formatCop()} para completar tu meta",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AndeanSlateInk.copy(alpha = 0.75f),
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }

        // Acciones rápidas de Aporte y Retiro
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onWithdrawClick,
                enabled = pocket.currentBalance.isPositive,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.85f),
                    contentColor = AndeanSlateInk,
                    disabledContainerColor = Color(0xFFF3F4F6),
                    disabledContentColor = Color(0xFF9CA3AF)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
            ) {
                Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Retirar", fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = onContributeClick,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AndeanEmeraldDeep)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Aportar", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // "La Vara del Páramo" — Simulador Visual de Bolsillos
        if (!pocket.isCompleted) {
            AndeanHyperCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                variant = HyperCardVariant.CARD,
                contentPadding = 20.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(AndinaAmber.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timeline,
                                contentDescription = null,
                                tint = AndinaAmber,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = "Simulador de Fondo",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AndinaDeepForest
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Ajusta cuánto puedes reservar cada semana y descubre cuándo florece tu meta:",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF6B7280))
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ahorro semanal:",
                            style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFF4B5563))
                        )
                        Text(
                            text = Money(weeklySavingPesos.toLong()).formatCop(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AndinaMossGreen
                            )
                        )
                    }

                    Slider(
                        value = weeklySavingPesos,
                        onValueChange = { weeklySavingPesos = it },
                        valueRange = 10_000f..500_000f,
                        steps = 48,
                        colors = SliderDefaults.colors(
                            thumbColor = AndinaMossGreen,
                            activeTrackColor = AndinaMossGreen,
                            inactiveTrackColor = Color(0xFFE5E7EB)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Tarjeta de Proyección Dinámica
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF9FAFB)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ElectricBolt,
                                contentDescription = null,
                                tint = AndinaAmber,
                                modifier = Modifier.size(22.dp)
                            )
                            Column {
                                Text(
                                    text = "¡Meta cumplida en aprox. $weeksRemaining semanas!",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AndinaDeepForest
                                    )
                                )
                                Text(
                                    text = if (monthsRemaining > 0) "Aproximadamente $monthsRemaining meses con este ritmo constante."
                                    else "¡En menos de un mes estarás celebrando con Muggy!",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF6B7280))
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

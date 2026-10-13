package com.andina.finanzas.ui.pockets.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.andina.finanzas.ui.components.FrostedGlassCard
import com.andina.finanzas.ui.components.ecosystem.FrailejonView
import com.andina.finanzas.ui.components.bouncyClick
import com.andina.finanzas.ui.theme.specularGlassBorder
import com.andina.finanzas.ui.theme.AndeanEmeraldDeep
import com.andina.finanzas.ui.theme.AndeanSlateInk
import com.andina.finanzas.ui.theme.FrailejonGold

/**
 * Tarjeta Hero superior de la pantalla de Bolsillos.
 * Muestra el patrimonio total resguardado en metas y el porcentaje consolidado de avance.
 */
@Composable
fun PocketsHeroCard(
    totalReserved: Money,
    totalGoals: Money,
    overallProgress: Float,
    onCreatePocketClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = overallProgress,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "HeroOverallProgress"
    )

    val progressPercent = (overallProgress * 100).toInt()

    FrostedGlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Savings,
                            contentDescription = null,
                            tint = FrailejonGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "TOTAL RESERVADO EN METAS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                color = AndeanSlateInk.copy(alpha = 0.65f)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = totalReserved.formatCop(),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = FrailejonGold,
                            fontSize = 30.sp,
                            fontFeatureSettings = "tnum"
                        )
                    )
                }

                // Frailejón maestro del páramo en halo de cuarzo
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(AndeanEmeraldDeep.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    FrailejonView(
                        progress = animatedProgress,
                        size = 56.dp,
                        animateSway = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Barra de progreso global
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = FrailejonGold,
                trackColor = AndeanSlateInk.copy(alpha = 0.1f),
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$progressPercent% del objetivo (${totalGoals.formatCop()})",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = AndeanSlateInk.copy(alpha = 0.75f),
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp,
                        fontFeatureSettings = "tnum"
                    ),
                    maxLines = 1,
                    modifier = Modifier.weight(1f, fill = false)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Botón para crear nueva meta
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .bouncyClick(onClick = onCreatePocketClick),
                    shape = RoundedCornerShape(12.dp),
                    color = AndeanEmeraldDeep.copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Nueva Meta",
                            tint = AndeanEmeraldDeep,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Nueva Meta",
                            maxLines = 1,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = AndeanEmeraldDeep
                            )
                        )
                    }
                }
            }
        }
    }
}

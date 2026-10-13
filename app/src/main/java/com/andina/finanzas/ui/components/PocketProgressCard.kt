package com.andina.finanzas.ui.components

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Savings
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.domain.model.Pocket
import com.andina.finanzas.ui.theme.AndeanPrimary
import com.andina.finanzas.ui.theme.AndeanPrimaryContainer
import com.andina.finanzas.ui.theme.AndeanSecondaryContainer
import com.andina.finanzas.ui.theme.AndinaTheme
import com.andina.finanzas.ui.theme.CornerMedium

/**
 * Tarjeta de Progreso de Bolsillo de Ahorro (PocketProgressCard).
 *
 * Muestra el avance de una meta financiera con:
 * - Nombre de la meta y porcentaje de consecución.
 * - Barra de progreso fluida animada con [FastOutSlowInEasing] (600 ms).
 * - Monto acumulado y monto objetivo formateados en COP.
 * - Indicador dinámico de saldo faltante o insignia de meta completada.
 *
 * @param pocket Modelo inmutable de dominio con los datos del bolsillo.
 * @param onClick Acción opcional para abrir el detalle o abonar a la meta.
 * @param modifier Modificador Compose externo.
 */
@Composable
fun PocketProgressCard(
    pocket: Pocket,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // Animación suave del progreso de 0 a targetValue
    val animatedProgress by animateFloatAsState(
        targetValue = pocket.progress,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "PocketProgressAnimation"
    )

    val progressPercent = (pocket.progress * 100).toInt()

    AndeanHyperCard(
        modifier = modifier
            .width(240.dp)
            .height(150.dp),
        variant = AndeanHyperCardVariant.CARD,
        contentPadding = 14.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Cabecera: Ícono + Nombre + Porcentaje / Insignia
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(AndeanPrimaryContainer.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Savings,
                            contentDescription = pocket.name,
                            tint = AndeanPrimaryContainer,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = pocket.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Porcentaje o Check de completitud
                if (pocket.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completado",
                        tint = AndeanPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "$progressPercent%",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            fontFeatureSettings = "tnum"
                        ),
                        color = AndeanPrimary
                    )
                }
            }

            // Cuerpo: Cifras de ahorro y Barra animada
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = pocket.currentBalance.formatCop(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            fontFeatureSettings = "tnum"
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "de ${pocket.goal.formatCop()}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            fontFeatureSettings = "tnum"
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Pista de progreso de 8dp con bordes redondeados
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0x1F1D1B19)) // 12% opacidad carbón
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        AndeanPrimaryContainer,
                                        Color(0xFF6F9964)
                                    )
                                )
                            )
                    )
                }
            }

            // Pie: Saldo restante o mensaje celebratorio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (pocket.isCompleted) {
                    Text(
                        text = "✨ ¡Meta alcanzada con éxito!",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = AndeanPrimary
                        )
                    )
                } else {
                    Text(
                        text = "Te faltan ${pocket.remaining.formatCop()}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            fontFeatureSettings = "tnum"
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(name = "PocketProgressCard Previews", showBackground = true)
@Composable
private fun PocketProgressCardPreview() {
    AndinaTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PocketProgressCard(
                pocket = Pocket(
                    id = 1,
                    name = "Fondo Emergencia",
                    goal = Money(2000000L),
                    currentBalance = Money(1500000L)
                )
            )
            PocketProgressCard(
                pocket = Pocket(
                    id = 2,
                    name = "Viaje Páramo",
                    goal = Money(500000L),
                    currentBalance = Money(500000L)
                )
            )
        }
    }
}

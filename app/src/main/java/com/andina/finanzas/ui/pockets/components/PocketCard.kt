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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Remove
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andina.finanzas.domain.model.Pocket
import com.andina.finanzas.ui.components.FrostedGlassCard
import com.andina.finanzas.ui.components.ecosystem.FrailejonView
import com.andina.finanzas.ui.components.bouncyClick
import com.andina.finanzas.ui.theme.AndeanEmeraldDeep
import com.andina.finanzas.ui.theme.AndeanSlateInk
import com.andina.finanzas.ui.theme.AndinaAmber
import com.andina.finanzas.ui.theme.AndinaDeepForest
import com.andina.finanzas.ui.theme.AndinaMossGreen
import com.andina.finanzas.ui.theme.FrailejonGold

import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Tarjeta interactiva de un Bolsillo de Ahorro con Frailejón Vivo y Menú de Gestión (Sub-fase 3.3).
 */
@Composable
fun PocketCard(
    pocket: Pocket,
    onClick: () -> Unit,
    onContributeClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    onEditClick: () -> Unit = {},
    onReminderClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val animatedProgress by animateFloatAsState(
        targetValue = pocket.progress,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "PocketCardProgress"
    )

    val progressPercent = (pocket.progress * 100).toInt()
    val pocketColor = try {
        Color(android.graphics.Color.parseColor(pocket.colorHex))
    } catch (_: Exception) {
        AndinaMossGreen
    }

    FrostedGlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Fila superior: Frailejón en miniatura + Info básica + Badge + Menú 3 puntos
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Flora Viva: Frailejón que refleja el progreso del ahorro
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(AndeanEmeraldDeep.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    FrailejonView(
                        progress = animatedProgress,
                        size = 62.dp,
                        animateSway = true
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = pocket.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AndeanSlateInk
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = if (pocket.isCompleted) "¡Meta completada!" else "Faltan ${pocket.remaining.formatCop()}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (pocket.isCompleted) AndeanEmeraldDeep else AndeanSlateInk.copy(alpha = 0.65f),
                            fontWeight = if (pocket.isCompleted) FontWeight.SemiBold else FontWeight.Normal
                        )
                    )
                }

                // Badge de Porcentaje o Éxito
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (pocket.isCompleted) AndinaMossGreen else pocketColor.copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (pocket.isCompleted) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Completado",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "100%",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        } else {
                            Text(
                                text = "$progressPercent%",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = pocketColor
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Menú de 3 puntos (⋮)
                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Opciones del bolsillo",
                            tint = Color(0xFF6B7280),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Editar Meta") },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                            },
                            onClick = {
                                menuExpanded = false
                                onEditClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Programar Recordatorio") },
                            leadingIcon = {
                                Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(18.dp))
                            },
                            onClick = {
                                menuExpanded = false
                                onReminderClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Eliminar Bolsillo", color = Color(0xFFBA1A1A)) },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFBA1A1A), modifier = Modifier.size(18.dp))
                            },
                            onClick = {
                                menuExpanded = false
                                onDeleteClick()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Barra de progreso animada
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (pocket.isCompleted) AndinaAmber else pocketColor,
                trackColor = Color(0xFFE5E7EB),
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Montos: Ahorrado vs Meta
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Ahorrado",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AndeanSlateInk.copy(alpha = 0.65f)
                        )
                    )
                    Text(
                        text = pocket.currentBalance.formatCop(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = FrailejonGold,
                            fontFeatureSettings = "tnum"
                        )
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Meta",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AndeanSlateInk.copy(alpha = 0.65f)
                        )
                    )
                    Text(
                        text = pocket.goal.formatCop(),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = AndeanSlateInk,
                            fontFeatureSettings = "tnum"
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Botones de acción rápida: Aportar y Retirar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Botón Retirar (solo si tiene fondos)
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(enabled = pocket.currentBalance.isPositive, onClick = onWithdrawClick),
                    shape = RoundedCornerShape(14.dp),
                    color = if (pocket.currentBalance.isPositive) Color(0xFFF3F4F6) else Color(0xFFF9FAFB)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Retirar",
                            tint = if (pocket.currentBalance.isPositive) Color(0xFF4B5563) else Color(0xFF9CA3AF),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Retirar",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (pocket.currentBalance.isPositive) Color(0xFF4B5563) else Color(0xFF9CA3AF)
                            )
                        )
                    }
                }

                // Botón Aportar
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(onClick = onContributeClick),
                    shape = RoundedCornerShape(14.dp),
                    color = AndinaMossGreen
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Aportar",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Aportar",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}

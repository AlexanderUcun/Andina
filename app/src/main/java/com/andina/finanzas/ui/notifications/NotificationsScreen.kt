package com.andina.finanzas.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andina.finanzas.ui.components.AndeanHyperCard
import com.andina.finanzas.ui.theme.LocalAndinaColors
import com.andina.finanzas.ui.components.bouncyClick
import com.andina.finanzas.ui.theme.AndeanAntExpenseAmber
import com.andina.finanzas.ui.theme.AndeanPrimary
import com.andina.finanzas.ui.theme.AndeanMistCanvasBottom
import com.andina.finanzas.ui.theme.AndeanMistCanvasTop
import com.andina.finanzas.ui.theme.AndeanSlateInk
import androidx.compose.ui.graphics.Brush

data class AndinaAlert(
    val id: String,
    val title: String,
    val message: String,
    val timeAgo: String,
    val icon: ImageVector,
    val color: Color
)

/**
 * Pantalla de Centro de Notificaciones y Alertas del Páramo (Módulo 5).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onNavigateBack: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    onNavigateToRecurring: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val alerts = listOf(
        AndinaAlert(
            id = "0",
            title = "🗓️ Confirmación de Quincena",
            message = "¿Recibiste tu salario programado hoy? Toca para confirmar o editar antes de abonarlo.",
            timeAgo = "Pendiente hoy",
            icon = Icons.Default.Savings,
            color = AndeanPrimary
        ),
        AndinaAlert(
            id = "1",
            title = "Aviso de Presupuesto",
            message = "Has alcanzado el 75% de tu presupuesto para Alimentación.",
            timeAgo = "Hace 2 horas",
            icon = Icons.Default.Warning,
            color = AndeanAntExpenseAmber
        ),
        AndinaAlert(
            id = "2",
            title = "Meta de Ahorro en Camino",
            message = "Tu bolsillo 'Viaje a Villa de Leyva' ha crecido un 15% este mes.",
            timeAgo = "Ayer",
            icon = Icons.Default.Savings,
            color = AndeanPrimary
        ),
        AndinaAlert(
            id = "3",
            title = "Consumo Hormiga Identificado",
            message = "Muggy detectó 4 compras menores a $10.000 COP esta semana.",
            timeAgo = "Hace 3 días",
            icon = Icons.Default.BugReport,
            color = Color(0xFF8E24AA)
        ),
        AndinaAlert(
            id = "4",
            title = "Libro Mayor Cuadrado",
            message = "Tus saldos en todas las billeteras coinciden con las transacciones registradas.",
            timeAgo = "Hace 5 días",
            icon = Icons.Default.CheckCircle,
            color = Color(0xFF43A047)
        )
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Centro de Alertas",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AndeanSlateInk
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = AndeanSlateInk
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(AndeanMistCanvasTop, AndeanMistCanvasBottom)
                    )
                )
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    AndeanHyperCard(
                        shape = RoundedCornerShape(18.dp),
                        onClick = onNavigateToRecurring,
                        contentPadding = 16.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Savings,
                                    contentDescription = null,
                                    tint = AndeanPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Movimientos Fijos & Recurrentes",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AndeanSlateInk
                                    )
                                    Text(
                                        text = "Administrar pagos con confirmación a 1 tap",
                                        fontSize = 12.sp,
                                        color = AndeanSlateInk.copy(alpha = 0.7f)
                                    )
                                }
                            }
                            Text(
                                text = "Ver",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = AndeanPrimary
                            )
                        }
                    }
                }

                items(alerts, key = { it.id }) { alert ->
                    AndeanHyperCard(
                        shape = RoundedCornerShape(18.dp),
                        contentPadding = 16.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(alert.color.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = alert.icon,
                                contentDescription = null,
                                tint = alert.color,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = alert.title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = AndeanSlateInk
                                )
                                Text(
                                    text = alert.timeAgo,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AndeanSlateInk.copy(alpha = 0.65f)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = alert.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = AndeanSlateInk.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }
        }
    }
}
}

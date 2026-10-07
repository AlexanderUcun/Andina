package com.andina.finanzas.ui.transactions.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andina.finanzas.domain.model.Transaction
import com.andina.finanzas.ui.components.TransactionItem
import com.andina.finanzas.ui.theme.AndeanAntExpenseAmber
import com.andina.finanzas.ui.theme.AndinaDeepForest
import com.andina.finanzas.ui.theme.AndeanError
import com.andina.finanzas.ui.theme.AndinaMossGreen

/**
 * Fila deslizable (Swipe-to-Dismiss) con estética andina y ráfaga de hojas de frailejón.
 *
 * Al deslizar hacia la izquierda (o derecha):
 * - Revela un fondo degradado con partículas sutiles de hojas de frailejón.
 * - Dispara la acción [onDelete] y permite restaurar desde el Snackbar "Deshacer".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableTransactionRow(
    transaction: Transaction,
    onClick: () -> Unit,
    onDelete: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart || dismissValue == SwipeToDismissBoxValue.StartToEnd) {
                onDelete(transaction)
                true
            } else {
                false
            }
        },
        positionalThreshold = { totalDistance -> totalDistance * 0.45f }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            AndeanFrailejonDismissBackground(dismissState)
        },
        content = {
            TransactionItem(
                transaction = transaction,
                onClick = onClick,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            )
        }
    )
}

/**
 * Fondo visual que simula viento del páramo con hojas doradas/verdes de frailejón al deslizar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AndeanFrailejonDismissBackground(
    dismissState: SwipeToDismissBoxState
) {
    val direction = dismissState.dismissDirection
    val isDismissing = dismissState.targetValue != SwipeToDismissBoxValue.Settled

    // Color de fondo dinámico
    val backgroundColor by animateColorAsState(
        targetValue = if (isDismissing) {
            AndeanError.copy(alpha = 0.92f)
        } else {
            AndinaDeepForest.copy(alpha = 0.15f)
        },
        animationSpec = tween(250),
        label = "DismissBackgroundAnim"
    )

    val contentAlpha by animateFloatAsState(
        targetValue = if (dismissState.progress > 0.15f) 1f else 0f,
        animationSpec = tween(150),
        label = "ContentAlphaAnim"
    )

    val alignment = when (direction) {
        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
        SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
        else -> Alignment.CenterEnd
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(horizontal = 24.dp),
        contentAlignment = alignment
    ) {
        // Decoración en Canvas: Ráfaga sutil de hojas de frailejón
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Dibujar 4 hojas orgánicas estilizadas flotando con la brisa
            val leafColor = if (isDismissing) Color.White.copy(alpha = 0.25f) else AndinaMossGreen.copy(alpha = 0.35f)
            val leafAmber = AndeanAntExpenseAmber.copy(alpha = 0.3f)

            // Hoja 1
            drawFrailejonLeaf(
                center = Offset(width * 0.3f, height * 0.4f),
                size = 18f,
                rotationDegrees = -25f,
                color = leafColor
            )
            // Hoja 2
            drawFrailejonLeaf(
                center = Offset(width * 0.65f, height * 0.65f),
                size = 14f,
                rotationDegrees = 35f,
                color = leafAmber
            )
            // Hoja 3
            drawFrailejonLeaf(
                center = Offset(width * 0.85f, height * 0.35f),
                size = 16f,
                rotationDegrees = -10f,
                color = leafColor
            )
        }

        // Ícono de papelera y rótulo
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (direction == SwipeToDismissBoxValue.StartToEnd) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Eliminar",
                    tint = Color.White.copy(alpha = contentAlpha),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Eliminar",
                    color = Color.White.copy(alpha = contentAlpha),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                )
            } else {
                Text(
                    text = "Eliminar",
                    color = Color.White.copy(alpha = contentAlpha),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Eliminar",
                    tint = Color.White.copy(alpha = contentAlpha),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/**
 * Traza geométrica de una hoja aterciopelada de frailejón sobre el Canvas.
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFrailejonLeaf(
    center: Offset,
    size: Float,
    rotationDegrees: Float,
    color: Color
) {
    val path = Path().apply {
        moveTo(center.x, center.y - size)
        cubicTo(
            center.x + size * 0.8f, center.y - size * 0.4f,
            center.x + size * 0.8f, center.y + size * 0.4f,
            center.x, center.y + size
        )
        cubicTo(
            center.x - size * 0.8f, center.y + size * 0.4f,
            center.x - size * 0.8f, center.y - size * 0.4f,
            center.x, center.y - size
        )
        close()
    }

    drawPath(path = path, color = color)
    // Nervadura central sutil
    drawLine(
        color = color.copy(alpha = color.alpha * 1.3f),
        start = Offset(center.x, center.y - size * 0.8f),
        end = Offset(center.x, center.y + size * 0.8f),
        strokeWidth = 1.5f
    )
}

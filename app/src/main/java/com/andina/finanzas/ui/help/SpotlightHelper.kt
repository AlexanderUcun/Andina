package com.andina.finanzas.ui.help

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andina.finanzas.ui.components.muggy.MuggyAvatar
import com.andina.finanzas.ui.components.muggy.MuggyPose

/**
 * Modelo de paso para el foco guiado interactivo (AYUDAS_INTERACTIVAS.md §2.1).
 */
data class SpotlightStep(
    val title: String,
    val description: String,
    val targetBounds: Rect,
    val stepIndex: Int = 1,
    val totalSteps: Int = 1
)

/**
 * Superposición de foco guiado (Spotlight Coach Marks) con velo oscuro al 70%,
 * recorte transparente y tarjeta de guía de Muggy.
 */
@Composable
fun SpotlightOverlay(
    step: SpotlightStep?,
    visible: Boolean,
    onNext: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible && step != null,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        if (step == null) return@AnimatedVisibility

        Box(modifier = modifier.fillMaxSize()) {
            // 1. Velo oscuro con recorte transparente en el elemento objetivo
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = 0.99f } // Habilita BlendMode.Clear en capa de hardware
            ) {
                // Capa oscura 70%
                drawRect(color = Color.Black.copy(alpha = 0.72f))

                // Recorte transparente sobre el objetivo con margen de 8dp
                val padding = 8.dp.toPx()
                val cutoutPath = Path().apply {
                    addRoundRect(
                        RoundRect(
                            rect = Rect(
                                left = step.targetBounds.left - padding,
                                top = step.targetBounds.top - padding,
                                right = step.targetBounds.right + padding,
                                bottom = step.targetBounds.bottom + padding
                            ),
                            cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
                        )
                    )
                }
                drawPath(path = cutoutPath, color = Color.Transparent, blendMode = BlendMode.Clear)
            }

            // 2. Tarjeta contextual de Muggy debajo o encima del recorte
            val isTargetInTopHalf = step.targetBounds.top < 1000f
            val cardYOffset = if (isTargetInTopHalf) {
                (step.targetBounds.bottom + 24f).toInt()
            } else {
                (step.targetBounds.top - 240f).toInt()
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(x = 0, y = cardYOffset.coerceIn(80, 1800)) }
                    .padding(horizontal = 24.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF7F2)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MuggyAvatar(
                                size = 48.dp,
                                pose = MuggyPose.OBSERVING,
                                isInteractive = false
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = step.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1D1B19)
                                )
                                Text(
                                    text = "Paso ${step.stepIndex} de ${step.totalSteps}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF845400),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = step.description,
                            fontSize = 14.sp,
                            color = Color(0xFF4A453E),
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = onDismiss,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF6B655D))
                            ) {
                                Text("Omitir", fontSize = 13.sp)
                            }

                            Button(
                                onClick = onNext,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF435C3C))
                            ) {
                                val buttonText = if (step.stepIndex >= step.totalSteps) "Entendido" else "Siguiente"
                                Text(buttonText, fontSize = 13.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

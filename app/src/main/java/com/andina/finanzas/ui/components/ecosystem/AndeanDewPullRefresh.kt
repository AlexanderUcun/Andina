package com.andina.finanzas.ui.components.ecosystem

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.andina.finanzas.ui.theme.AndeanPrimary
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Contenedor Pull-to-Refresh con Gota de Rocío Andino (AndeanDewPullRefresh).
 *
 * Especificaciones de diseño ecológico y cinemático:
 * 1. La gota de agua es un trazado paramétrico en Canvas (Bézier Spline) que se deforma elásticamente.
 * 2. Cuello adelgazado por tensión superficial al estirarse.
 * 3. Umbral de disparo táctil a 80dp con vibración háptica cuando se rompe la tensión.
 * 4. Al soltar y refrescar, genera ondas concéntricas de agua cristalina del páramo.
 */
@Composable
fun AndeanDewPullRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    refreshThreshold: Dp = 80.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    val thresholdPx = with(density) { refreshThreshold.toPx() }
    val maxDragPx = thresholdPx * 1.6f

    var pullOffset by remember { mutableFloatStateOf(0f) }
    var hasTriggeredHaptic by remember { mutableStateOf(false) }

    val animatedOffset = remember { Animatable(0f) }

    // Animación de ondas durante la recarga
    val rippleProgress = remember { Animatable(0f) }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            animatedOffset.animateTo(
                targetValue = thresholdPx,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            )
            rippleProgress.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1100, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            pullOffset = 0f
            hasTriggeredHaptic = false
            rippleProgress.snapTo(0f)
            animatedOffset.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 280, easing = LinearEasing)
            )
        }
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (isRefreshing) return Offset.Zero

                // Deslizamiento hacia arriba mientras hay offset acumulado
                if (available.y < 0 && pullOffset > 0f) {
                    val consumed = available.y
                    pullOffset = (pullOffset + consumed).coerceAtLeast(0f)
                    coroutineScope.launch { animatedOffset.snapTo(pullOffset) }
                    return Offset(0f, consumed)
                }
                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (isRefreshing) return Offset.Zero

                // Deslizamiento hacia abajo cuando la lista está en el tope
                if (available.y > 0) {
                    val resistance = 0.45f
                    val newOffset = (pullOffset + available.y * resistance).coerceAtMost(maxDragPx)
                    pullOffset = newOffset
                    coroutineScope.launch { animatedOffset.snapTo(newOffset) }

                    if (newOffset >= thresholdPx && !hasTriggeredHaptic) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        hasTriggeredHaptic = true
                    } else if (newOffset < thresholdPx && hasTriggeredHaptic) {
                        hasTriggeredHaptic = false
                    }
                    return Offset(0f, available.y)
                }
                return Offset.Zero
            }

            override suspend fun onPreFling(available: androidx.compose.ui.unit.Velocity): androidx.compose.ui.unit.Velocity {
                if (pullOffset >= thresholdPx && !isRefreshing) {
                    onRefresh()
                } else if (!isRefreshing) {
                    coroutineScope.launch {
                        animatedOffset.animateTo(0f, spring(stiffness = Spring.StiffnessMedium))
                        pullOffset = 0f
                    }
                }
                return super.onPreFling(available)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        val currentOffset = animatedOffset.value
        val isVisible = currentOffset > 1f || isRefreshing

        // Indicador de Gota de Rocío en el tope (colapsado y disuelto en reposo)
        if (isVisible) {
            val dropAlpha = if (isRefreshing) 1f else (currentOffset / thresholdPx).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(with(density) { currentOffset.toDp() }),
                contentAlignment = Alignment.BottomCenter
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(with(density) { currentOffset.toDp() })
                ) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    val centerX = canvasWidth / 2f

                    if (isRefreshing) {
                        // Modo Ondas Concéntricas de Agua Cristalina
                        val maxRadius = min(canvasWidth / 2f, 70.dp.toPx())
                        val r = rippleProgress.value * maxRadius
                        val alpha = (1f - rippleProgress.value).coerceIn(0f, 1f)

                        drawCircle(
                            color = Color(0xFF6B9080).copy(alpha = alpha * 0.7f),
                            radius = r,
                            center = Offset(centerX, canvasHeight / 2f),
                            style = Stroke(width = 2.dp.toPx())
                        )
                        drawCircle(
                            color = Color(0xFFA4C3B2).copy(alpha = alpha * 0.5f),
                            radius = (r * 0.65f),
                            center = Offset(centerX, canvasHeight / 2f),
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                        drawCircle(
                            color = AndeanPrimary.copy(alpha = 0.9f),
                            radius = 6.dp.toPx(),
                            center = Offset(centerX, canvasHeight / 2f)
                        )
                    } else {
                        // Modo Gota Elástica Bézier Spline
                        val stretchProgress = (currentOffset / thresholdPx).coerceIn(0f, 1.5f)
                        val bulbRadius = (10.dp.toPx() + stretchProgress * 6.dp.toPx())
                        val neckWidth = (22.dp.toPx() / (1f + stretchProgress * 0.8f)).coerceAtLeast(6.dp.toPx())
                        val dropBottomY = canvasHeight - 6.dp.toPx()
                        val bulbCenterY = dropBottomY - bulbRadius

                        val dropPath = Path().apply {
                            // Inicio en el techo (cuello superior)
                            moveTo(centerX - neckWidth, 0f)
                            // Curva Bézier cúbica hacia el bulbo de la gota
                            cubicTo(
                                centerX - neckWidth * 0.7f, bulbCenterY * 0.6f,
                                centerX - bulbRadius, bulbCenterY - bulbRadius * 0.5f,
                                centerX - bulbRadius, bulbCenterY
                            )
                            // Arco inferior de la gota
                            arcTo(
                                rect = androidx.compose.ui.geometry.Rect(
                                    centerX - bulbRadius,
                                    bulbCenterY - bulbRadius,
                                    centerX + bulbRadius,
                                    bulbCenterY + bulbRadius
                                ),
                                startAngleDegrees = 180f,
                                sweepAngleDegrees = -180f,
                                forceMoveTo = false
                            )
                            // Subida por el lado derecho hacia el techo
                            cubicTo(
                                centerX + bulbRadius, bulbCenterY - bulbRadius * 0.5f,
                                centerX + neckWidth * 0.7f, bulbCenterY * 0.6f,
                                centerX + neckWidth, 0f
                            )
                            close()
                        }

                        // Gradiente translúcido de agua andina con opacidad dinámica
                        val waterBrush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0x33A4C3B2).copy(alpha = 0.2f * dropAlpha),
                                Color(0x996B9080).copy(alpha = 0.6f * dropAlpha),
                                Color(0xDD3B6E58).copy(alpha = 0.9f * dropAlpha)
                            )
                        )

                        drawPath(path = dropPath, brush = waterBrush)

                        // Destello blanco en la gota (reflejo de sol del páramo)
                        drawCircle(
                            color = Color.White.copy(alpha = 0.85f * dropAlpha),
                            radius = 2.5.dp.toPx(),
                            center = Offset(centerX - bulbRadius * 0.35f, bulbCenterY - bulbRadius * 0.25f)
                        )
                    }
                }
            }
        }

        // Contenido principal desplazado hacia abajo durante el arrastre
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(x = 0, y = if (currentOffset > 1f) currentOffset.roundToInt() else 0) }
        ) {
            content()
        }
    }
}

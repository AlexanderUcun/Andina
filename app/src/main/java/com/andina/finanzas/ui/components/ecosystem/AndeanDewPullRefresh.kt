package com.andina.finanzas.ui.components.ecosystem

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.andina.finanzas.ui.theme.AndeanPrimary
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.min

/**
 * Contenedor Pull-to-Refresh con Gota de Rocío Andino (AndeanDewPullRefresh).
 *
 * Arquitectura de alto rendimiento (60 / 120 FPS):
 * 1. Sin relayouts en drag: El contenido se desplaza en GPU mediante [graphicsLayer].
 * 2. Cero asignación de corrutinas por micro-frame táctil.
 * 3. Física de resorte suave (*Spring Physics*) al soltar o refrescar.
 * 4. Trazado Bézier Spline paramétrico con tensión superficial elástica.
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
    var animationJob by remember { mutableStateOf<Job?>(null) }

    // Animación de ondas durante la recarga
    val rippleProgress = remember { Animatable(0f) }

    LaunchedEffect(isRefreshing) {
        animationJob?.cancel()
        if (isRefreshing) {
            animationJob = launch {
                animate(
                    initialValue = pullOffset,
                    targetValue = thresholdPx,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) { value, _ ->
                    pullOffset = value
                }
            }
            rippleProgress.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1100, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            hasTriggeredHaptic = false
            rippleProgress.snapTo(0f)
            animationJob = launch {
                animate(
                    initialValue = pullOffset,
                    targetValue = 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) { value, _ ->
                    pullOffset = value
                }
            }
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
                } else if (!isRefreshing && pullOffset > 0f) {
                    animationJob?.cancel()
                    animationJob = coroutineScope.launch {
                        animate(
                            initialValue = pullOffset,
                            targetValue = 0f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) { value, _ ->
                            pullOffset = value
                        }
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
        val currentOffset = pullOffset
        val isVisible = currentOffset > 1f || (isRefreshing && currentOffset > 0f)

        // Indicador de Gota de Rocío en el tope (renderizado en capa estática sin provocar relayouts)
        if (isVisible) {
            val dropAlpha = if (isRefreshing) 1f else (currentOffset / thresholdPx).coerceIn(0f, 1f)
            val canvasHeightDp = with(density) { maxDragPx.toDp() }

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(canvasHeightDp)
            ) {
                val canvasWidth = size.width
                val centerX = canvasWidth / 2f

                if (isRefreshing) {
                    // Modo Ondas Concéntricas de Agua Cristalina
                    val centerY = thresholdPx / 2f
                    val maxRadius = min(canvasWidth / 2f, 65.dp.toPx())
                    val r = rippleProgress.value * maxRadius
                    val alpha = (1f - rippleProgress.value).coerceIn(0f, 1f)

                    drawCircle(
                        color = Color(0xFF6B9080).copy(alpha = alpha * 0.7f),
                        radius = r,
                        center = Offset(centerX, centerY),
                        style = Stroke(width = 2.dp.toPx())
                    )
                    drawCircle(
                        color = Color(0xFFA4C3B2).copy(alpha = alpha * 0.5f),
                        radius = (r * 0.65f),
                        center = Offset(centerX, centerY),
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                    drawCircle(
                        color = AndeanPrimary.copy(alpha = 0.9f),
                        radius = 6.dp.toPx(),
                        center = Offset(centerX, centerY)
                    )
                } else {
                    // Modo Gota Elástica Bézier Spline
                    val effectiveHeight = currentOffset.coerceIn(0f, maxDragPx)
                    val stretchProgress = (effectiveHeight / thresholdPx).coerceIn(0f, 1.5f)
                    val bulbRadius = (9.dp.toPx() + stretchProgress * 6.dp.toPx())
                    val neckWidth = (20.dp.toPx() / (1f + stretchProgress * 0.85f)).coerceAtLeast(5.dp.toPx())
                    val dropBottomY = effectiveHeight - 4.dp.toPx()
                    val bulbCenterY = dropBottomY - bulbRadius

                    if (bulbCenterY > 0f) {
                        val dropPath = Path().apply {
                            moveTo(centerX - neckWidth, 0f)
                            cubicTo(
                                centerX - neckWidth * 0.7f, bulbCenterY * 0.55f,
                                centerX - bulbRadius, bulbCenterY - bulbRadius * 0.5f,
                                centerX - bulbRadius, bulbCenterY
                            )
                            arcTo(
                                rect = Rect(
                                    centerX - bulbRadius,
                                    bulbCenterY - bulbRadius,
                                    centerX + bulbRadius,
                                    bulbCenterY + bulbRadius
                                ),
                                startAngleDegrees = 180f,
                                sweepAngleDegrees = -180f,
                                forceMoveTo = false
                            )
                            cubicTo(
                                centerX + bulbRadius, bulbCenterY - bulbRadius * 0.5f,
                                centerX + neckWidth * 0.7f, bulbCenterY * 0.55f,
                                centerX + neckWidth, 0f
                            )
                            close()
                        }

                        val waterBrush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0x33A4C3B2).copy(alpha = 0.2f * dropAlpha),
                                Color(0x996B9080).copy(alpha = 0.6f * dropAlpha),
                                Color(0xDD3B6E58).copy(alpha = 0.9f * dropAlpha)
                            )
                        )

                        drawPath(path = dropPath, brush = waterBrush)

                        drawCircle(
                            color = Color.White.copy(alpha = 0.85f * dropAlpha),
                            radius = 2.5.dp.toPx(),
                            center = Offset(centerX - bulbRadius * 0.35f, bulbCenterY - bulbRadius * 0.25f)
                        )
                    }
                }
            }
        }

        // Contenido principal desplazado en GPU (cero recomposiciones de hijos durante el arrastre)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationY = currentOffset
                }
        ) {
            content()
        }
    }
}


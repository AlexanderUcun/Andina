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
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.graphics.StrokeCap
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

/**
 * Contenedor Pull-to-Refresh con Gota de Rocío Andino Cristalina (AndeanDewPullRefresh).
 *
 * Especificaciones de diseño premium:
 * 1. Gota de agua cristalina hiperrealista: Refracción de luz, destello solar y contorno de tensión superficial.
 * 2. Cápsula de rocío suspendida en recarga: Micro-vórtice de agua andina que nunca invade el texto.
 * 3. Renderizado GPU en fase de dibujo sin relayouts para 60/120 FPS fluidos.
 */
@Composable
fun AndeanDewPullRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    refreshThreshold: Dp = 72.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    val thresholdPx = with(density) { refreshThreshold.toPx() }
    val refreshHoldPx = with(density) { 56.dp.toPx() }
    val maxDragPx = thresholdPx * 1.5f

    var pullOffset by remember { mutableFloatStateOf(0f) }
    var hasTriggeredHaptic by remember { mutableStateOf(false) }
    var animationJob by remember { mutableStateOf<Job?>(null) }

    // Rotación continua del micro-vórtice cristalino durante la recarga
    val vortexRotation = remember { Animatable(0f) }
    // Pulsación suave de la gota central
    val pulseScale = remember { Animatable(1f) }

    LaunchedEffect(isRefreshing) {
        animationJob?.cancel()
        if (isRefreshing) {
            animationJob = launch {
                animate(
                    initialValue = pullOffset,
                    targetValue = refreshHoldPx,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) { value, _ ->
                    pullOffset = value
                }
            }
            launch {
                vortexRotation.animateTo(
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 850, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    )
                )
            }
            launch {
                pulseScale.animateTo(
                    targetValue = 1.15f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 425, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )
            }
        } else {
            hasTriggeredHaptic = false
            vortexRotation.snapTo(0f)
            pulseScale.snapTo(1f)
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

                // Solo arrastre manual directo del usuario
                if (source == NestedScrollSource.UserInput && available.y > 0) {
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

            override suspend fun onPostFling(
                consumed: androidx.compose.ui.unit.Velocity,
                available: androidx.compose.ui.unit.Velocity
            ): androidx.compose.ui.unit.Velocity {
                if (!isRefreshing && pullOffset > 0f) {
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
                return super.onPostFling(consumed, available)
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

        // Indicador de Gota de Rocío Cristalina en el tope
        if (isVisible) {
            val canvasHeightDp = with(density) { maxDragPx.toDp() }

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(canvasHeightDp)
            ) {
                val canvasWidth = size.width
                val centerX = canvasWidth / 2f

                if (isRefreshing) {
                    // MODO RECARGA: Micro-cápsula suspendida con Vórtice de Agua Andina
                    val badgeCenterY = refreshHoldPx / 2f
                    val badgeRadius = 19.dp.toPx()

                    // Fondo de cristal esmerilado de la cápsula
                    val capsuleBg = if (isDark) Color(0xF21C201A) else Color(0xF2FFFFFF)
                    val capsuleBorder = AndeanPrimary.copy(alpha = 0.22f)

                    // Sombra sutil de la cápsula
                    drawCircle(
                        color = Color.Black.copy(alpha = 0.08f),
                        radius = badgeRadius + 2.dp.toPx(),
                        center = Offset(centerX, badgeCenterY + 1.5.dp.toPx())
                    )
                    // Cuerpo de la cápsula
                    drawCircle(
                        color = capsuleBg,
                        radius = badgeRadius,
                        center = Offset(centerX, badgeCenterY)
                    )
                    // Borde fino de cristal
                    drawCircle(
                        color = capsuleBorder,
                        radius = badgeRadius,
                        center = Offset(centerX, badgeCenterY),
                        style = Stroke(width = 1.dp.toPx())
                    )

                    // Arco rotativo del vórtice de agua
                    val ringRadius = 12.dp.toPx()
                    val arcRect = Rect(
                        centerX - ringRadius,
                        badgeCenterY - ringRadius,
                        centerX + ringRadius,
                        badgeCenterY + ringRadius
                    )

                    // Arco fluido esmeralda
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(
                                AndeanPrimary.copy(alpha = 0.05f),
                                AndeanPrimary.copy(alpha = 0.4f),
                                AndeanPrimary
                            ),
                            center = Offset(centerX, badgeCenterY)
                        ),
                        startAngle = vortexRotation.value,
                        sweepAngle = 260f,
                        useCenter = false,
                        topLeft = Offset(arcRect.left, arcRect.top),
                        size = androidx.compose.ui.geometry.Size(arcRect.width, arcRect.height),
                        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Micro-gota cristalina central con pulsación
                    val dotRadius = 3.5.dp.toPx() * pulseScale.value
                    drawCircle(
                        color = AndeanPrimary,
                        radius = dotRadius,
                        center = Offset(centerX, badgeCenterY)
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.85f),
                        radius = 1.2.dp.toPx(),
                        center = Offset(centerX - 1.dp.toPx(), badgeCenterY - 1.dp.toPx())
                    )
                } else {
                    // MODO ARRASTRE: Gota Elástica Cristalina con Tensión Superficial y Refracción
                    val dropAlpha = (currentOffset / thresholdPx).coerceIn(0f, 1f)
                    val effectiveHeight = currentOffset.coerceIn(0f, maxDragPx)
                    val stretchProgress = (effectiveHeight / thresholdPx).coerceIn(0f, 1.5f)
                    val bulbRadius = (9.dp.toPx() + stretchProgress * 6.5.dp.toPx())
                    val neckWidth = (18.dp.toPx() / (1f + stretchProgress * 0.9f)).coerceAtLeast(4.5.dp.toPx())
                    val dropBottomY = effectiveHeight - 4.dp.toPx()
                    val bulbCenterY = dropBottomY - bulbRadius

                    if (bulbCenterY > 0f) {
                        val dropPath = Path().apply {
                            moveTo(centerX - neckWidth, 0f)
                            cubicTo(
                                centerX - neckWidth * 0.65f, bulbCenterY * 0.5f,
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
                                centerX + neckWidth * 0.65f, bulbCenterY * 0.5f,
                                centerX + neckWidth, 0f
                            )
                            close()
                        }

                        // 1. Relleno cristalino translúcido de agua de páramo
                        val crystalWaterBrush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0x3080B9AD).copy(alpha = 0.25f * dropAlpha),
                                Color(0x75528D78).copy(alpha = 0.65f * dropAlpha),
                                Color(0xCC386A20).copy(alpha = 0.92f * dropAlpha)
                            )
                        )
                        drawPath(path = dropPath, brush = crystalWaterBrush)

                        // 2. Contorno de refracción de luz (efecto cristal 3D)
                        val glassRefractionBrush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.75f * dropAlpha),
                                Color(0x88A4C3B2).copy(alpha = 0.45f * dropAlpha),
                                Color(0x33386A20).copy(alpha = 0.3f * dropAlpha)
                            )
                        )
                        drawPath(
                            path = dropPath,
                            brush = glassRefractionBrush,
                            style = Stroke(width = 1.2.dp.toPx())
                        )

                        // 3. Destello especular de luz solar en la superficie de la gota
                        drawCircle(
                            color = Color.White.copy(alpha = 0.35f * dropAlpha),
                            radius = 3.8.dp.toPx(),
                            center = Offset(centerX - bulbRadius * 0.35f, bulbCenterY - bulbRadius * 0.25f)
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = 0.95f * dropAlpha),
                            radius = 1.8.dp.toPx(),
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


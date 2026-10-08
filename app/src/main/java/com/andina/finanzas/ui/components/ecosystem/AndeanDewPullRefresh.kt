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
import androidx.compose.ui.geometry.Size
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Contenedor Pull-to-Refresh con Gota de Agua Glaciar Cristalina y Transición Cromática (AndeanDewPullRefresh).
 *
 * Especificaciones de diseño sensorial hiperrealista:
 * 1. Agua azul cristalina con refracción 3D, bisel cáustico y reflejos solares especulares.
 * 2. Transición cromática dinámica: Cambia fluidamente de Azul Cielo Glaciar a Azul Turquesa Esmeralda con la tensión.
 * 3. En recarga: Micro-cápsula de cristal suspendida con vórtice cromático dual entrelazado.
 * 4. Renderizado 100% acelerado por GPU (cero relayouts a 60/120 FPS).
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
    val maxDragPx = thresholdPx * 1.55f

    var pullOffset by remember { mutableFloatStateOf(0f) }
    var hasTriggeredHaptic by remember { mutableStateOf(false) }
    var animationJob by remember { mutableStateOf<Job?>(null) }

    // Rotación del vórtice cromático principal
    val vortexRotation = remember { Animatable(0f) }
    // Rotación armónica del contra-vórtice secundario
    val subVortexRotation = remember { Animatable(0f) }
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
                        animation = tween(durationMillis = 900, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    )
                )
            }
            launch {
                subVortexRotation.animateTo(
                    targetValue = -360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 1400, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    )
                )
            }
            launch {
                pulseScale.animateTo(
                    targetValue = 1.18f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 450, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )
            }
        } else {
            hasTriggeredHaptic = false
            vortexRotation.snapTo(0f)
            subVortexRotation.snapTo(0f)
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

        // Indicador de Gota de Rocío Glaciar Cristalina
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
                    // MODO RECARGA: Micro-cápsula suspendida con Vórtice Cromático Dual
                    val badgeCenterY = refreshHoldPx / 2f
                    val badgeRadius = 20.dp.toPx()

                    // Resplandor ambiental cian translúcido
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(Color(0x3038BDF8), Color(0x0038BDF8)),
                            center = Offset(centerX, badgeCenterY),
                            radius = badgeRadius + 8.dp.toPx()
                        ),
                        radius = badgeRadius + 8.dp.toPx(),
                        center = Offset(centerX, badgeCenterY)
                    )

                    // Fondo de cristal esmerilado
                    val capsuleBg = if (isDark) Color(0xF4161C22) else Color(0xF8FFFFFF)
                    val capsuleBorder = Color(0x4038BDF8)

                    // Sombra sutil de la cápsula
                    drawCircle(
                        color = Color.Black.copy(alpha = 0.09f),
                        radius = badgeRadius + 2.dp.toPx(),
                        center = Offset(centerX, badgeCenterY + 1.5.dp.toPx())
                    )
                    // Cuerpo de la cápsula
                    drawCircle(
                        color = capsuleBg,
                        radius = badgeRadius,
                        center = Offset(centerX, badgeCenterY)
                    )
                    // Borde de cristal zafiro
                    drawCircle(
                        color = capsuleBorder,
                        radius = badgeRadius,
                        center = Offset(centerX, badgeCenterY),
                        style = Stroke(width = 1.2.dp.toPx())
                    )

                    // 1. Filamento de Vórtice Principal (Azul Glaciar -> Turquesa Esmeralda)
                    val mainRingRadius = 13.dp.toPx()
                    val mainArcRect = Rect(
                        centerX - mainRingRadius,
                        badgeCenterY - mainRingRadius,
                        centerX + mainRingRadius,
                        badgeCenterY + mainRingRadius
                    )

                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(
                                Color(0x000284C7),
                                Color(0x600284C7),
                                Color(0xFF00B4D8),
                                Color(0xFF06D6A0),
                                Color(0xFF38BDF8)
                            ),
                            center = Offset(centerX, badgeCenterY)
                        ),
                        startAngle = vortexRotation.value,
                        sweepAngle = 270f,
                        useCenter = false,
                        topLeft = Offset(mainArcRect.left, mainArcRect.top),
                        size = Size(mainArcRect.width, mainArcRect.height),
                        style = Stroke(width = 2.6.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // 2. Contra-filamento Armónico Secundario (Cian Cristalino)
                    val subRingRadius = 8.5.dp.toPx()
                    val subArcRect = Rect(
                        centerX - subRingRadius,
                        badgeCenterY - subRingRadius,
                        centerX + subRingRadius,
                        badgeCenterY + subRingRadius
                    )

                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(
                                Color(0x0038BDF8),
                                Color(0x80BAE6FD),
                                Color(0xFF7DD3FC)
                            ),
                            center = Offset(centerX, badgeCenterY)
                        ),
                        startAngle = subVortexRotation.value,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(subArcRect.left, subArcRect.top),
                        size = Size(subArcRect.width, subArcRect.height),
                        style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // 3. Micro-gota central cristalina con pulso y destello blanco
                    val dotRadius = 3.6.dp.toPx() * pulseScale.value
                    drawCircle(
                        color = Color(0xFF0284C7),
                        radius = dotRadius,
                        center = Offset(centerX, badgeCenterY)
                    )
                    drawCircle(
                        color = Color(0xFF7DD3FC),
                        radius = dotRadius * 0.65f,
                        center = Offset(centerX - 0.5.dp.toPx(), badgeCenterY - 0.5.dp.toPx())
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 1.2.dp.toPx(),
                        center = Offset(centerX - 1.dp.toPx(), badgeCenterY - 1.dp.toPx())
                    )
                } else {
                    // MODO ARRASTRE: Gota Elástica Cristalina con Transición Cromática Dinámica
                    val dropAlpha = (currentOffset / thresholdPx).coerceIn(0f, 1f)
                    val effectiveHeight = currentOffset.coerceIn(0f, maxDragPx)
                    val stretchProgress = (effectiveHeight / thresholdPx).coerceIn(0f, 1.5f)
                    val bulbRadius = (9.dp.toPx() + stretchProgress * 6.8.dp.toPx())
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

                        // Transición cromática dinámica: De Azul Cielo Glaciar a Azul Turquesa Esmeralda
                        val topWaterColor = Color(0x357DD3FC).copy(alpha = 0.3f * dropAlpha)
                        val midWaterColor = if (stretchProgress > 0.85f) {
                            Color(0x8500B4D8).copy(alpha = 0.7f * dropAlpha)
                        } else {
                            Color(0x7538BDF8).copy(alpha = 0.65f * dropAlpha)
                        }
                        val bottomWaterColor = if (stretchProgress > 0.85f) {
                            Color(0xE006D6A0).copy(alpha = 0.95f * dropAlpha) // Transición a Turquesa Esmeralda
                        } else {
                            Color(0xDE0284C7).copy(alpha = 0.92f * dropAlpha) // Azul Glaciar Zafiro
                        }

                        // 1. Relleno cristalino de agua azul glaciar
                        val crystalBlueWaterBrush = Brush.verticalGradient(
                            colors = listOf(topWaterColor, midWaterColor, bottomWaterColor)
                        )
                        drawPath(path = dropPath, brush = crystalBlueWaterBrush)

                        // 2. Contorno de refracción de luz (efecto cristal 3D de alta definición)
                        val glassRefractionBrush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.85f * dropAlpha),
                                Color(0xAA7DD3FC).copy(alpha = 0.55f * dropAlpha),
                                Color(0x400284C7).copy(alpha = 0.35f * dropAlpha)
                            )
                        )
                        drawPath(
                            path = dropPath,
                            brush = glassRefractionBrush,
                            style = Stroke(width = 1.4.dp.toPx())
                        )

                        // 3. Destello cáustico interior (curvatura de luz en la base)
                        drawCircle(
                            color = Color(0x45BAE6FD).copy(alpha = 0.5f * dropAlpha),
                            radius = bulbRadius * 0.55f,
                            center = Offset(centerX, bulbCenterY + bulbRadius * 0.25f)
                        )

                        // 4. Destello especular de luz solar en el lateral de la gota
                        drawCircle(
                            color = Color.White.copy(alpha = 0.4f * dropAlpha),
                            radius = 4.2.dp.toPx(),
                            center = Offset(centerX - bulbRadius * 0.36f, bulbCenterY - bulbRadius * 0.28f)
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = 0.98f * dropAlpha),
                            radius = 2.0.dp.toPx(),
                            center = Offset(centerX - bulbRadius * 0.36f, bulbCenterY - bulbRadius * 0.28f)
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



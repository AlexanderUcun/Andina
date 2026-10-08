package com.andina.finanzas.ui.components.ecosystem

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.ui.graphics.drawscope.DrawScope
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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Contenedor Pull-to-Refresh con Gota de Rocío Líquida Orgánica y Morfismo Continuo (AndeanDewPullRefresh).
 *
 * Características clave:
 * 1. Morfismo continuo: Desprendimiento elástico sin saltos ni parpadeos de transición.
 * 2. Física líquida orgánica: Geometría dinámica no circular basada en armónicos sinusoidales de tensión superficial.
 * 3. Cristalino y cromático: Gradientes fluidos de Azul Zafiro, Cian Glaciar y Turquesa Andino con refracción 3D.
 * 4. Cierre etéreo: Disolución suave y natural al finalizar la recarga.
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

    // Factor de morfismo: 0f = estiramiento elástico con cuello, 1f = gota flotante orgánica libre
    val morphProgress = remember { Animatable(0f) }
    // Fase de ondulación líquida continua (armónicos de fluidos)
    val liquidPhase = remember { Animatable(0f) }
    // Rotación del filamento de agua y destellos cáusticos
    val swirlRotation = remember { Animatable(0f) }
    // Opacidad de salida suave para evitar parpadeos
    val exitAlpha = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        launch {
            liquidPhase.animateTo(
                targetValue = (2 * PI).toFloat(),
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1800, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        }
        launch {
            swirlRotation.animateTo(
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1200, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    LaunchedEffect(isRefreshing) {
        animationJob?.cancel()
        if (isRefreshing) {
            exitAlpha.snapTo(1f)
            animationJob = launch {
                launch {
                    morphProgress.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
                    )
                }
                launch {
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
            }
        } else {
            hasTriggeredHaptic = false
            animationJob = launch {
                // Desvanecimiento etéreo suave y retorno
                launch {
                    exitAlpha.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                    )
                }
                launch {
                    morphProgress.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                    )
                }
                launch {
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
                exitAlpha.snapTo(1f)
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
        val isVisible = (currentOffset > 0.5f || isRefreshing) && exitAlpha.value > 0.01f

        if (isVisible) {
            val canvasHeightDp = with(density) { maxDragPx.toDp() }

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(canvasHeightDp)
            ) {
                val canvasWidth = size.width
                val centerX = canvasWidth / 2f
                val morph = morphProgress.value
                val alphaMultiplier = exitAlpha.value

                val effectiveHeight = currentOffset.coerceIn(0f, maxDragPx)
                val dragRatio = (effectiveHeight / thresholdPx).coerceIn(0f, 1.5f)

                // Posición central del cuerpo líquido
                val restingCenterY = refreshHoldPx * 0.48f
                val dragCenterY = (effectiveHeight - 12.dp.toPx()).coerceAtLeast(16.dp.toPx())
                val liquidCenterY = lerp(dragCenterY, restingCenterY, morph)

                // Radio base dinámico de la masa de agua
                val baseRadius = with(density) {
                    lerp(10.dp.toPx() + dragRatio * 5.5.dp.toPx(), 18.5.dp.toPx(), morph)
                }

                val phase = liquidPhase.value
                val rot = swirlRotation.value

                // 1. Resplandor ambiental de agua andina (aura fluida translúcida)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x3538BDF8).copy(alpha = 0.35f * alphaMultiplier),
                            Color(0x0038BDF8)
                        ),
                        center = Offset(centerX, liquidCenterY),
                        radius = baseRadius * 1.85f
                    ),
                    radius = baseRadius * 1.85f,
                    center = Offset(centerX, liquidCenterY)
                )

                // 2. Construcción del Trazado Líquido Orgánico (Bézier con deformación armónica)
                val liquidPath = if (morph < 0.95f) {
                    // Estado Elástico con cuello que se adelgaza y desprende
                    val neckWidth = (16.dp.toPx() / (1f + dragRatio * 0.85f)) * (1f - morph * 0.95f)
                    val topY = lerp(0f, liquidCenterY - baseRadius, morph)

                    Path().apply {
                        moveTo(centerX - neckWidth, topY)
                        cubicTo(
                            centerX - neckWidth * 0.7f, topY + (liquidCenterY - topY) * 0.45f,
                            centerX - baseRadius * 1.05f, liquidCenterY - baseRadius * 0.55f,
                            centerX - baseRadius, liquidCenterY
                        )
                        // Curva inferior con oscilación de tensión
                        val bottomWobble = sin(phase * 2f) * 1.8f * (1f - morph)
                        cubicTo(
                            centerX - baseRadius * 0.95f, liquidCenterY + baseRadius + bottomWobble,
                            centerX + baseRadius * 0.95f, liquidCenterY + baseRadius + bottomWobble,
                            centerX + baseRadius, liquidCenterY
                        )
                        cubicTo(
                            centerX + baseRadius * 1.05f, liquidCenterY - baseRadius * 0.55f,
                            centerX + neckWidth * 0.7f, topY + (liquidCenterY - topY) * 0.45f,
                            centerX + neckWidth, topY
                        )
                        close()
                    }
                } else {
                    // Estado Gota Flotante Orgánica (Masa líquida viva no-circular con 8 nodos armónicos)
                    createOrganicBlobPath(
                        centerX = centerX,
                        centerY = liquidCenterY,
                        baseRadius = baseRadius,
                        phase = phase
                    )
                }

                // 3. Gradiente Cromático Líquido (Azul Zafiro -> Cian Glaciar -> Turquesa Esmeralda)
                val tensionHue = (dragRatio * 0.7f + morph * 0.5f).coerceIn(0f, 1f)
                val topColor = Color(0x997DD3FC).copy(alpha = 0.65f * alphaMultiplier)
                val midColor = if (tensionHue > 0.6f) {
                    Color(0xD000B4D8).copy(alpha = 0.88f * alphaMultiplier)
                } else {
                    Color(0xC038BDF8).copy(alpha = 0.82f * alphaMultiplier)
                }
                val bottomColor = if (tensionHue > 0.6f) {
                    Color(0xF006D6A0).copy(alpha = 0.95f * alphaMultiplier) // Turquesa Esmeralda
                } else {
                    Color(0xEA0284C7).copy(alpha = 0.92f * alphaMultiplier) // Azul Zafiro Glaciar
                }

                val waterGradient = Brush.verticalGradient(
                    colors = listOf(topColor, midColor, bottomColor),
                    startY = liquidCenterY - baseRadius,
                    endY = liquidCenterY + baseRadius
                )

                // Render del cuerpo de agua
                drawPath(path = liquidPath, brush = waterGradient)

                // 4. Borde de Refracción Cristalina 3D
                val glassStroke = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.92f * alphaMultiplier),
                        Color(0xB0BAE6FD).copy(alpha = 0.65f * alphaMultiplier),
                        Color(0x500284C7).copy(alpha = 0.40f * alphaMultiplier)
                    ),
                    startY = liquidCenterY - baseRadius,
                    endY = liquidCenterY + baseRadius
                )
                drawPath(
                    path = liquidPath,
                    brush = glassStroke,
                    style = Stroke(width = with(density) { 1.35.dp.toPx() })
                )

                // 5. Filamento de Corriente Líquida Interna (Espiral armónica en movimiento)
                if (morph > 0.15f) {
                    val streamAlpha = (morph * alphaMultiplier).coerceIn(0f, 1f)
                    val streamRadius = baseRadius * 0.62f
                    val arcRect = Rect(
                        centerX - streamRadius,
                        liquidCenterY - streamRadius,
                        centerX + streamRadius,
                        liquidCenterY + streamRadius
                    )

                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(
                                Color(0x00FFFFFF),
                                Color(0x60BAE6FD),
                                Color(0xE006D6A0),
                                Color(0xFF38BDF8),
                                Color(0x00FFFFFF)
                            ),
                            center = Offset(centerX, liquidCenterY)
                        ),
                        startAngle = rot,
                        sweepAngle = 240f,
                        useCenter = false,
                        topLeft = Offset(arcRect.left, arcRect.top),
                        size = Size(arcRect.width, arcRect.height),
                        style = Stroke(
                            width = with(density) { 2.2.dp.toPx() },
                            cap = StrokeCap.Round
                        ),
                        alpha = streamAlpha
                    )
                }

                // 6. Destellos Especulares Asimétricos y Cáusticos (Brillos solares sobre agua viva)
                val glintAngle1 = phase
                val glintX1 = centerX + cos(glintAngle1) * (baseRadius * 0.45f)
                val glintY1 = liquidCenterY + sin(glintAngle1) * (baseRadius * 0.38f) - baseRadius * 0.18f

                val glintAngle2 = phase + PI.toFloat() * 0.85f
                val glintX2 = centerX + cos(glintAngle2) * (baseRadius * 0.35f)
                val glintY2 = liquidCenterY + sin(glintAngle2) * (baseRadius * 0.32f)

                // Destello primario de luz viva
                drawCircle(
                    color = Color.White.copy(alpha = 0.95f * alphaMultiplier),
                    radius = with(density) { 2.4.dp.toPx() },
                    center = Offset(glintX1, glintY1)
                )
                drawCircle(
                    color = Color(0x80BAE6FD).copy(alpha = 0.6f * alphaMultiplier),
                    radius = with(density) { 4.5.dp.toPx() },
                    center = Offset(glintX1, glintY1)
                )

                // Destello cáustico secundario
                drawCircle(
                    color = Color.White.copy(alpha = 0.75f * alphaMultiplier),
                    radius = with(density) { 1.6.dp.toPx() },
                    center = Offset(glintX2, glintY2)
                )
            }
        }

        // Contenido principal desplazado fluidamente en GPU
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

/**
 * Genera un trazado orgánico no circular (Gota viva) mediante perturbaciones sinusoidales armónicas.
 */
private fun createOrganicBlobPath(
    centerX: Float,
    centerY: Float,
    baseRadius: Float,
    phase: Float
): Path {
    val pointsCount = 8
    val coords = ArrayList<Offset>(pointsCount)

    for (i in 0 until pointsCount) {
        val angle = (i * 2 * PI / pointsCount).toFloat()
        // Armónicos de vibración líquida
        val harmonic1 = sin(angle * 2f + phase) * 0.09f
        val harmonic2 = cos(angle * 3f - phase * 1.2f) * 0.06f
        val harmonic3 = sin(angle * 4f + phase * 0.7f) * 0.035f

        val radius = baseRadius * (1f + harmonic1 + harmonic2 + harmonic3)
        val x = centerX + cos(angle) * radius
        val y = centerY + sin(angle) * radius
        coords.add(Offset(x, y))
    }

    val path = Path()
    if (coords.isEmpty()) return path

    path.moveTo((coords[0].x + coords[pointsCount - 1].x) / 2f, (coords[0].y + coords[pointsCount - 1].y) / 2f)

    for (i in 0 until pointsCount) {
        val curr = coords[i]
        val next = coords[(i + 1) % pointsCount]
        val midX = (curr.x + next.x) / 2f
        val midY = (curr.y + next.y) / 2f
        path.quadraticTo(curr.x, curr.y, midX, midY)
    }
    path.close()
    return path
}

/**
 * Interpolación lineal simple.
 */
private fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return start + (stop - start) * fraction.coerceIn(0f, 1f)
}




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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
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
import androidx.compose.ui.zIndex
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Contenedor Pull-to-Refresh con Protocolo Inteligente de Doble Modo y Marea Andina Fluida (AndeanDewPullRefresh).
 *
 * Protocolo de Recarga:
 * 1. Soltado Rápido (< 350 ms): Carga ágil y ligera sin esperas.
 * 2. Retención Sostenida (>= 350 ms): Animación completa cinematográfica con doble vórtice, destellos y morfismo orgánico.
 * 3. Renderizado inmediato en primer plano desde el primer píxel de arrastre.
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
    val fullRefreshHoldPx = with(density) { 68.dp.toPx() }
    val quickRefreshHoldPx = with(density) { 42.dp.toPx() }
    val maxDragPx = thresholdPx * 1.55f

    var pullOffset by remember { mutableFloatStateOf(0f) }
    var thresholdReachedTime by remember { mutableLongStateOf(0L) }
    var isFullAnimationMode by remember { mutableStateOf(false) }
    var hasTriggeredInitialHaptic by remember { mutableStateOf(false) }
    var hasTriggeredFullHaptic by remember { mutableStateOf(false) }
    var animationJob by remember { mutableStateOf<Job?>(null) }

    // Factor de morfismo: 0f = marea elástica de arrastre, 1f = gota orgánica flotante libre
    val morphProgress = remember { Animatable(0f) }
    // Fase de ondulación líquida continua (armónicos de fluidos)
    val liquidPhase = remember { Animatable(0f) }
    // Rotación del filamento de agua y destellos cáusticos
    val swirlRotation = remember { Animatable(0f) }
    // Opacidad de salida suave
    val exitAlpha = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        launch {
            liquidPhase.animateTo(
                targetValue = (2 * PI).toFloat(),
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 2400, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        }
        launch {
            swirlRotation.animateTo(
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1800, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    LaunchedEffect(isRefreshing) {
        animationJob?.cancel()
        if (isRefreshing) {
            exitAlpha.snapTo(1f)
            val isFull = isFullAnimationMode
            val targetHold = if (isFull) fullRefreshHoldPx else quickRefreshHoldPx
            val morphTarget = if (isFull) 1f else 0.45f
            val morphDuration = if (isFull) 550 else 240

            animationJob = launch {
                launch {
                    morphProgress.animateTo(
                        targetValue = morphTarget,
                        animationSpec = tween(durationMillis = morphDuration, easing = FastOutSlowInEasing)
                    )
                }
                launch {
                    animate(
                        initialValue = pullOffset,
                        targetValue = targetHold,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = if (isFull) Spring.StiffnessLow else Spring.StiffnessMedium
                        )
                    ) { value, _ ->
                        pullOffset = value
                    }
                }
            }
        } else {
            val isFull = isFullAnimationMode
            val exitDuration = if (isFull) 420 else 260

            animationJob = launch {
                launch {
                    exitAlpha.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = exitDuration, easing = FastOutSlowInEasing)
                    )
                }
                launch {
                    morphProgress.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = exitDuration, easing = FastOutSlowInEasing)
                    )
                }
                launch {
                    animate(
                        initialValue = pullOffset,
                        targetValue = 0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = if (isFull) Spring.StiffnessLow else Spring.StiffnessMedium
                        )
                    ) { value, _ ->
                        pullOffset = value
                    }
                }
                exitAlpha.snapTo(1f)
                thresholdReachedTime = 0L
                isFullAnimationMode = false
                hasTriggeredInitialHaptic = false
                hasTriggeredFullHaptic = false
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

                if (available.y > 0) {
                    val resistance = 0.48f
                    val newOffset = (pullOffset + available.y * resistance).coerceAtMost(maxDragPx)
                    pullOffset = newOffset

                    if (newOffset >= thresholdPx) {
                        if (thresholdReachedTime == 0L) {
                            thresholdReachedTime = System.currentTimeMillis()
                        }
                        if (!hasTriggeredInitialHaptic) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            hasTriggeredInitialHaptic = true
                        }
                        // Detección de retención sostenida (> 350 ms)
                        val elapsed = System.currentTimeMillis() - thresholdReachedTime
                        if (elapsed >= 350L && !hasTriggeredFullHaptic) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            hasTriggeredFullHaptic = true
                            isFullAnimationMode = true
                        }
                    } else {
                        thresholdReachedTime = 0L
                        hasTriggeredInitialHaptic = false
                        hasTriggeredFullHaptic = false
                        isFullAnimationMode = false
                    }
                    return Offset(0f, available.y)
                }
                return Offset.Zero
            }

            override suspend fun onPreFling(available: androidx.compose.ui.unit.Velocity): androidx.compose.ui.unit.Velocity {
                if (pullOffset >= thresholdPx && !isRefreshing) {
                    val elapsed = if (thresholdReachedTime > 0L) System.currentTimeMillis() - thresholdReachedTime else 0L
                    isFullAnimationMode = elapsed >= 350L || hasTriggeredFullHaptic
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

    @OptIn(ExperimentalFoundationApi::class)
    CompositionLocalProvider(
        LocalOverscrollConfiguration provides null
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .nestedScroll(nestedScrollConnection)
        ) {
            val currentOffset = pullOffset
            val isVisible = (currentOffset > 0.5f || isRefreshing) && exitAlpha.value > 0.01f

            // 1. Contenido principal desplazado fluidamente en GPU
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationY = currentOffset
                    }
            ) {
                content()
            }

            // 2. Marea y Gota de Agua Andina en Primer Plano (Renderizado inmediato)
            if (isVisible) {
                val canvasHeightDp = with(density) { (maxDragPx + 20.dp.toPx()).toDp() }

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(canvasHeightDp)
                        .zIndex(10f)
                ) {
                    val canvasWidth = size.width
                    val centerX = canvasWidth / 2f
                    val morph = morphProgress.value
                    val alphaMultiplier = exitAlpha.value

                    val effectiveHeight = currentOffset.coerceIn(0f, maxDragPx)
                    val dragRatio = (effectiveHeight / thresholdPx).coerceIn(0f, 1.5f)
                    val dragAlpha = ((effectiveHeight / 8.dp.toPx()).coerceIn(0.15f, 1f) * alphaMultiplier)

                    // Posición vertical del centro líquido
                    val restingCenterY = fullRefreshHoldPx * 0.52f
                    val dragCenterY = (effectiveHeight * 0.58f).coerceAtLeast(8.dp.toPx())
                    val liquidCenterY = lerp(dragCenterY, restingCenterY, morph)

                    // Radio dinámico del cuerpo de agua
                    val currentBulbRadius = with(density) {
                        lerp((6.dp.toPx() + dragRatio * 8.5.dp.toPx()).coerceAtMost(20.dp.toPx()), 19.dp.toPx(), morph)
                    }

                    val phase = liquidPhase.value
                    val rot = swirlRotation.value

                    // 1. Resplandor ambiental de agua andina
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x4038BDF8).copy(alpha = 0.45f * dragAlpha),
                                Color(0x0038BDF8)
                            ),
                            center = Offset(centerX, liquidCenterY),
                            radius = currentBulbRadius * 2.4f
                        ),
                        radius = currentBulbRadius * 2.4f,
                        center = Offset(centerX, liquidCenterY)
                    )

                    // 2. Cuello Elástico de Tensión Superficial (Conecta al techo y se adelgaza/desprende con morph)
                    if (morph < 0.98f) {
                        val neckAlpha = ((1f - morph) * dragAlpha).coerceIn(0f, 1f)
                        val neckTopWidth = (24.dp.toPx() + dragRatio * 8.dp.toPx()) * (1f - morph)
                        val neckWaistWidth = (14.dp.toPx() / (1f + dragRatio * 0.9f)).coerceAtLeast(3.5.dp.toPx()) * (1f - morph)

                        val neckPath = Path().apply {
                            moveTo(centerX - neckTopWidth, 0f)
                            cubicTo(
                                centerX - neckWaistWidth, liquidCenterY * 0.45f,
                                centerX - currentBulbRadius * 0.85f, liquidCenterY - currentBulbRadius * 0.5f,
                                centerX - currentBulbRadius * 0.85f, liquidCenterY
                            )
                            lineTo(centerX + currentBulbRadius * 0.85f, liquidCenterY)
                            cubicTo(
                                centerX + currentBulbRadius * 0.85f, liquidCenterY - currentBulbRadius * 0.5f,
                                centerX + neckWaistWidth, liquidCenterY * 0.45f,
                                centerX + neckTopWidth, 0f
                            )
                            close()
                        }

                        val neckGradient = Brush.verticalGradient(
                            colors = listOf(
                                Color(0x807DD3FC).copy(alpha = 0.65f * neckAlpha),
                                Color(0xB000B4D8).copy(alpha = 0.80f * neckAlpha),
                                Color(0xD00284C7).copy(alpha = 0.90f * neckAlpha)
                            ),
                            startY = 0f,
                            endY = liquidCenterY
                        )
                        drawPath(path = neckPath, brush = neckGradient)

                        val neckStroke = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.85f * neckAlpha),
                                Color(0xA0BAE6FD).copy(alpha = 0.60f * neckAlpha),
                                Color(0x400284C7).copy(alpha = 0.35f * neckAlpha)
                            ),
                            startY = 0f,
                            endY = liquidCenterY
                        )
                        drawPath(
                            path = neckPath,
                            brush = neckStroke,
                            style = Stroke(width = with(density) { 1.3.dp.toPx() })
                        )
                    }

                    // 3. CUERPO PRINCIPAL: Gota Orgánica Tridimensional (Siempre presente y viva)
                    val dropBlobPath = createOrganicBlobPath(
                        centerX = centerX,
                        centerY = liquidCenterY,
                        baseRadius = currentBulbRadius,
                        phase = phase
                    )

                    // Gradiente Cromático Dinámico de Agua Glaciar (Azul Zafiro -> Cian -> Turquesa)
                    val tensionHue = (dragRatio * 0.75f + morph * 0.5f).coerceIn(0f, 1f)
                    val topColor = Color(0x997DD3FC).copy(alpha = 0.75f * dragAlpha)
                    val midColor = if (tensionHue > 0.55f) {
                        Color(0xE000B4D8).copy(alpha = 0.92f * dragAlpha)
                    } else {
                        Color(0xD038BDF8).copy(alpha = 0.88f * dragAlpha)
                    }
                    val bottomColor = if (tensionHue > 0.55f) {
                        Color(0xF806D6A0).copy(alpha = 0.98f * dragAlpha) // Turquesa Esmeralda
                    } else {
                        Color(0xF50284C7).copy(alpha = 0.96f * dragAlpha) // Azul Zafiro Glaciar
                    }

                    val waterGradient = Brush.verticalGradient(
                        colors = listOf(topColor, midColor, bottomColor),
                        startY = liquidCenterY - currentBulbRadius * 1.1f,
                        endY = liquidCenterY + currentBulbRadius * 1.1f
                    )
                    drawPath(path = dropBlobPath, brush = waterGradient)

                    // 4. Borde de Refracción Cristalina 3D
                    val glassStroke = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.98f * dragAlpha),
                            Color(0xD0BAE6FD).copy(alpha = 0.75f * dragAlpha),
                            Color(0x700284C7).copy(alpha = 0.45f * dragAlpha)
                        ),
                        startY = liquidCenterY - currentBulbRadius * 1.1f,
                        endY = liquidCenterY + currentBulbRadius * 1.1f
                    )
                    drawPath(
                        path = dropBlobPath,
                        brush = glassStroke,
                        style = Stroke(width = with(density) { 1.5.dp.toPx() })
                    )

                    // 5. Filamento de Corriente Interna en Rotación Continua
                    val streamAlpha = ((0.35f + morph * 0.65f) * dragAlpha).coerceIn(0f, 1f)
                    val streamRadius = currentBulbRadius * 0.60f
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
                                Color(0x70BAE6FD),
                                Color(0xF006D6A0),
                                Color(0xFF38BDF8),
                                Color(0x00FFFFFF)
                            ),
                            center = Offset(centerX, liquidCenterY)
                        ),
                        startAngle = rot,
                        sweepAngle = 250f,
                        useCenter = false,
                        topLeft = Offset(arcRect.left, arcRect.top),
                        size = Size(arcRect.width, arcRect.height),
                        style = Stroke(
                            width = with(density) { 2.4.dp.toPx() },
                            cap = StrokeCap.Round
                        ),
                        alpha = streamAlpha
                    )

                // 6. Destellos Especulares Asimétricos y Cáusticos
                val glintAngle1 = phase
                val glintX1 = centerX + cos(glintAngle1) * (currentBulbRadius * 0.45f)
                val glintY1 = liquidCenterY + sin(glintAngle1) * (currentBulbRadius * 0.38f) - currentBulbRadius * 0.18f

                val glintAngle2 = phase + PI.toFloat() * 0.85f
                val glintX2 = centerX + cos(glintAngle2) * (currentBulbRadius * 0.35f)
                val glintY2 = liquidCenterY + sin(glintAngle2) * (currentBulbRadius * 0.32f)

                // Destello primario de luz viva
                drawCircle(
                    color = Color.White.copy(alpha = 0.98f * dragAlpha),
                    radius = with(density) { 2.6.dp.toPx() },
                    center = Offset(glintX1, glintY1)
                )
                drawCircle(
                    color = Color(0x90BAE6FD).copy(alpha = 0.65f * dragAlpha),
                    radius = with(density) { 4.8.dp.toPx() },
                    center = Offset(glintX1, glintY1)
                )

                // Destello cáustico secundario
                drawCircle(
                    color = Color.White.copy(alpha = 0.80f * dragAlpha),
                    radius = with(density) { 1.8.dp.toPx() },
                    center = Offset(glintX2, glintY2)
                )
            }
        }
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




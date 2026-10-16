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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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
import com.andina.finanzas.ui.theme.AndeanSplashColors
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

/**
 * Contenedor Pull-to-Refresh con Marea y Ola de Agua Andina continua de izquierda a derecha (AndeanDewPullRefresh).
 *
 * Características:
 * 1. Detección inmediata en pre-scroll: responde y es visible desde el primer píxel de arrastre sin esperar a soltar.
 * 2. Geometría de ola líquida armónica completamente suave (cero puntas o estalactitas).
 * 3. Movimiento continuo de onda viajera de izquierda a derecha.
 * 4. Capas multicapa de agua: resplandor ambiental, onda de fondo y ola principal con cresta espumosa.
 * 5. Perlas de espuma marina y destellos que cabalgan las crestas de la ola.
 */
@Composable
fun AndeanDewPullRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    canScrollBackward: () -> Boolean = { false },
    refreshThreshold: Dp = 72.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    val thresholdPx = with(density) { refreshThreshold.toPx() }
    val fullRefreshHoldPx = with(density) { 68.dp.toPx() }
    val maxDragPx = thresholdPx * 1.65f

    var pullOffset by remember { mutableFloatStateOf(0f) }
    var hasTriggeredHaptic by remember { mutableStateOf(false) }
    var animationJob by remember { mutableStateOf<Job?>(null) }

    // Fase continua de la ola viajera (fluye suavemente de izquierda a derecha)
    val wavePhase = remember { Animatable(0f) }
    // Opacidad de salida suave
    val exitAlpha = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        wavePhase.animateTo(
            targetValue = (2 * PI).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2400, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    LaunchedEffect(isRefreshing) {
        animationJob?.cancel()
        if (isRefreshing) {
            exitAlpha.snapTo(1f)
            animationJob = launch {
                animate(
                    initialValue = pullOffset,
                    targetValue = fullRefreshHoldPx,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) { value, _ ->
                    pullOffset = value
                }
            }
        } else {
            animationJob = launch {
                launch {
                    exitAlpha.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing)
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
                hasTriggeredHaptic = false
            }
        }
    }

    val nestedScrollConnection = remember(canScrollBackward, isRefreshing) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (isRefreshing) return Offset.Zero

                // 1. Al arrastrar hacia abajo (available.y > 0) y el scroll está en el tope superior
                if (available.y > 0 && !canScrollBackward()) {
                    val resistance = 0.52f
                    val delta = available.y * resistance
                    pullOffset = (pullOffset + delta).coerceAtMost(maxDragPx)

                    if (pullOffset >= thresholdPx && !hasTriggeredHaptic) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        hasTriggeredHaptic = true
                    }
                    return Offset(0f, available.y)
                }

                // 2. Al arrastrar hacia arriba (available.y < 0) mientras la ola está desplegada
                if (available.y < 0 && pullOffset > 0f) {
                    val consumedY = available.y.coerceAtLeast(-pullOffset)
                    pullOffset = (pullOffset + consumedY).coerceAtLeast(0f)
                    if (pullOffset < thresholdPx) {
                        hasTriggeredHaptic = false
                    }
                    return Offset(0f, consumedY)
                }

                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (isRefreshing) return Offset.Zero

                // Respaldo secundario si la lista no consumió desplazamiento hacia abajo
                if (available.y > 0) {
                    val resistance = 0.52f
                    val delta = available.y * resistance
                    pullOffset = (pullOffset + delta).coerceAtMost(maxDragPx)
                    if (pullOffset >= thresholdPx && !hasTriggeredHaptic) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        hasTriggeredHaptic = true
                    }
                    return Offset(0f, available.y)
                }
                return Offset.Zero
            }

            override suspend fun onPreFling(available: androidx.compose.ui.unit.Velocity): androidx.compose.ui.unit.Velocity {
                if (pullOffset >= thresholdPx && !isRefreshing) {
                    onRefresh()
                    animationJob?.cancel()
                    animationJob = coroutineScope.launch {
                        animate(
                            initialValue = pullOffset,
                            targetValue = fullRefreshHoldPx,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        ) { value, _ ->
                            pullOffset = value
                        }
                    }
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

            // 1. Contenido principal desplazado hacia abajo en GPU
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationY = currentOffset
                    }
            ) {
                content()
            }

            // 2. Ola de Agua Andina Continua (Viaja de izquierda a derecha suavemente)
            if (isVisible) {
                val canvasHeightDp = with(density) { (maxDragPx * 1.6f).toDp() }

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(canvasHeightDp)
                        .zIndex(10f)
                ) {
                    val canvasWidth = size.width
                    val waveDepth = (currentOffset * 0.90f).coerceAtLeast(3f)
                    val alpha = exitAlpha.value * (currentOffset / with(density) { 4.dp.toPx() }).coerceIn(0.2f, 1f)
                    val phase = wavePhase.value

                    // A) Resplandor ambiental de rocío andino
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                AndeanSplashColors.AmbientGlow.copy(alpha = 0.50f * alpha),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = waveDepth * 1.5f
                        ),
                        size = Size(canvasWidth, waveDepth * 1.5f)
                    )

                    // B) Ola de fondo (segunda capa con desfase para volumen y profundidad 3D)
                    val backWavePath = createRollingWavePath(
                        width = canvasWidth,
                        depth = waveDepth * 0.85f,
                        phase = phase,
                        wavelengthRatio = 0.75f,
                        amplitudeRatio = 0.16f,
                        phaseOffset = 1.4f
                    )
                    drawPath(
                        path = backWavePath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                AndeanSplashColors.MidWater.copy(alpha = 0.38f * alpha),
                                AndeanSplashColors.LightWater.copy(alpha = 0.55f * alpha)
                            ),
                            startY = 0f,
                            endY = waveDepth
                        )
                    )

                    // C) Ola frontal principal (Ondulación armónica de izquierda a derecha)
                    val frontWavePath = createRollingWavePath(
                        width = canvasWidth,
                        depth = waveDepth,
                        phase = phase,
                        wavelengthRatio = 0.65f,
                        amplitudeRatio = 0.20f,
                        phaseOffset = 0f
                    )

                    val waterFillBrush = Brush.verticalGradient(
                        colors = listOf(
                            AndeanSplashColors.DeepWater.copy(alpha = 0.90f * alpha),
                            AndeanSplashColors.MidWater.copy(alpha = 0.94f * alpha),
                            AndeanSplashColors.LightWater.copy(alpha = 0.98f * alpha)
                        ),
                        startY = 0f,
                        endY = waveDepth * 1.25f
                    )
                    drawPath(path = frontWavePath, brush = waterFillBrush)

                    // D) Cresta de espuma blanca (Borde menisco líquido suave trazado solo sobre la cresta)
                    val crestStrokePath = createWaveCrestLine(
                        width = canvasWidth,
                        depth = waveDepth,
                        phase = phase,
                        wavelengthRatio = 0.65f,
                        amplitudeRatio = 0.20f,
                        phaseOffset = 0f
                    )
                    val foamBrush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.70f * alpha),
                            Color.White.copy(alpha = 0.98f * alpha),
                            AndeanSplashColors.CausticFoam.copy(alpha = 0.90f * alpha),
                            Color.White.copy(alpha = 0.70f * alpha)
                        ),
                        startX = 0f,
                        endX = canvasWidth
                    )
                    drawPath(
                        path = crestStrokePath,
                        brush = foamBrush,
                        style = Stroke(
                            width = with(density) { 3.2.dp.toPx() },
                            cap = StrokeCap.Round
                        )
                    )

                    // E) Perlas de espuma marina y burbujas que cabalgan la cresta de izquierda a derecha
                    val k1 = (2 * PI / (canvasWidth * 0.65f)).toFloat()
                    val bubbleCount = 6
                    for (b in 0 until bubbleCount) {
                        val normX = (b * 0.17f + (phase / (2 * PI).toFloat()) * 0.40f) % 1.0f
                        val bx = normX * canvasWidth
                        val by = waveDepth + sin(k1 * bx - phase) * (waveDepth * 0.20f) - with(density) { 2.dp.toPx() }
                        val bubbleRadius = with(density) { (2.0.dp.toPx() + (b % 3) * 0.6.dp.toPx()) }

                        drawCircle(
                            color = Color.White.copy(alpha = 0.88f * alpha),
                            radius = bubbleRadius,
                            center = Offset(bx, by)
                        )
                        drawCircle(
                            color = AndeanSplashColors.DropletGleam.copy(alpha = 0.95f * alpha),
                            radius = bubbleRadius * 0.45f,
                            center = Offset(bx - bubbleRadius * 0.25f, by - bubbleRadius * 0.25f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Traza el polígono cerrado del cuerpo de agua de una ola oceánica sinusoidal continua.
 * El término (- p) asegura que la onda viaje físicamente de IZQUIERDA a DERECHA.
 */
private fun createRollingWavePath(
    width: Float,
    depth: Float,
    phase: Float,
    wavelengthRatio: Float = 0.65f,
    amplitudeRatio: Float = 0.20f,
    phaseOffset: Float = 0f
): Path {
    val path = Path()
    if (depth <= 0.5f || width <= 0f) return path

    val steps = 50
    val dx = width / steps
    val k1 = (2 * PI / (width * wavelengthRatio)).toFloat()
    val k2 = (2 * PI / (width * wavelengthRatio * 0.50f)).toFloat()
    val p = phase + phaseOffset

    fun waveY(x: Float): Float {
        val primaryHarmonic = sin(k1 * x - p) * (depth * amplitudeRatio)
        val secondaryHarmonic = sin(k2 * x - p * 1.25f) * (depth * amplitudeRatio * 0.25f)
        return (depth + primaryHarmonic + secondaryHarmonic).coerceAtLeast(2f)
    }

    path.moveTo(0f, 0f)
    path.lineTo(0f, waveY(0f))

    for (i in 1..steps) {
        val x = i * dx
        path.lineTo(x, waveY(x))
    }

    path.lineTo(width, 0f)
    path.close()
    return path
}

/**
 * Traza únicamente la línea inferior de la cresta (para aplicar el trazo de espuma sin pintar bordes en la pantalla).
 */
private fun createWaveCrestLine(
    width: Float,
    depth: Float,
    phase: Float,
    wavelengthRatio: Float = 0.65f,
    amplitudeRatio: Float = 0.20f,
    phaseOffset: Float = 0f
): Path {
    val path = Path()
    if (depth <= 0.5f || width <= 0f) return path

    val steps = 50
    val dx = width / steps
    val k1 = (2 * PI / (width * wavelengthRatio)).toFloat()
    val k2 = (2 * PI / (width * wavelengthRatio * 0.50f)).toFloat()
    val p = phase + phaseOffset

    fun waveY(x: Float): Float {
        val primaryHarmonic = sin(k1 * x - p) * (depth * amplitudeRatio)
        val secondaryHarmonic = sin(k2 * x - p * 1.25f) * (depth * amplitudeRatio * 0.25f)
        return (depth + primaryHarmonic + secondaryHarmonic).coerceAtLeast(2f)
    }

    path.moveTo(0f, waveY(0f))
    for (i in 1..steps) {
        val x = i * dx
        path.lineTo(x, waveY(x))
    }
    return path
}

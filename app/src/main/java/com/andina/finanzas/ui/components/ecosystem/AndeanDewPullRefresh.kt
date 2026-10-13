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
import kotlin.math.cos
import kotlin.math.sin

/**
 * Contenedor Pull-to-Refresh con Splash de Agua Andina Fluido de lado a lado (AndeanDewPullRefresh).
 *
 * Características:
 * 1. Renderizado inmediato desde el primer píxel de arrastre (sobre-desplazamiento en tiempo real).
 * 2. Cascada orgánica de agua de ancho completo ("de lado a lado") con gotas colgantes y un chorro central protagonista.
 * 3. Ondulación líquida continua de fluidos durante el estado de recarga activa.
 * 4. Gotas perladas suspendidas y menisco de espuma cristalina reflectante.
 * 5. Retracción elástica suave al completar la recarga.
 */
@Composable
fun AndeanDewPullRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    refreshThreshold: Dp = 72.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    val thresholdPx = with(density) { refreshThreshold.toPx() }
    val fullRefreshHoldPx = with(density) { 68.dp.toPx() }
    val maxDragPx = thresholdPx * 1.70f

    var pullOffset by remember { mutableFloatStateOf(0f) }
    var hasTriggeredHaptic by remember { mutableStateOf(false) }
    var animationJob by remember { mutableStateOf<Job?>(null) }

    // Fase continua de ondulación líquida
    val liquidPhase = remember { Animatable(0f) }
    // Opacidad de salida suave
    val exitAlpha = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        liquidPhase.animateTo(
            targetValue = (2 * PI).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2200, easing = LinearEasing),
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
                        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
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

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (isRefreshing) return Offset.Zero

                // Al deslizar hacia arriba mientras hay sobre-desplazamiento activo, consumir primero
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
                    val resistance = 0.52f
                    val newOffset = (pullOffset + available.y * resistance).coerceAtMost(maxDragPx)
                    pullOffset = newOffset

                    if (newOffset >= thresholdPx) {
                        if (!hasTriggeredHaptic) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            hasTriggeredHaptic = true
                        }
                    } else {
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

            // 1. Contenido principal desplazado hacia abajo en GPU conforme baja el agua
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationY = currentOffset
                    }
            ) {
                content()
            }

            // 2. Splash de Agua Andina de lado a lado (Renderizado inmediato visible en primer plano)
            if (isVisible) {
                val canvasHeightDp = with(density) { (maxDragPx * 1.55f).toDp() }

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(canvasHeightDp)
                        .zIndex(10f)
                ) {
                    val canvasWidth = size.width
                    val splashDepth = (currentOffset * 0.90f).coerceAtLeast(4f)
                    val dragRatio = (currentOffset / thresholdPx).coerceIn(0f, 1.5f)
                    val alpha = exitAlpha.value * (currentOffset / with(density) { 6.dp.toPx() }).coerceIn(0.2f, 1f)
                    val phase = liquidPhase.value

                    // A) Resplandor de rocío ambiental sobre el fondo
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                AndeanSplashColors.AmbientGlow.copy(alpha = 0.55f * alpha),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = splashDepth * 1.6f
                        ),
                        size = Size(canvasWidth, splashDepth * 1.6f)
                    )

                    // B) Capa de agua secundaria de fondo (profundidad 3D traslúcida)
                    val backWavePath = createSecondaryBackWavePath(
                        width = canvasWidth,
                        depth = splashDepth,
                        phase = phase
                    )
                    drawPath(
                        path = backWavePath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                AndeanSplashColors.MidWater.copy(alpha = 0.35f * alpha),
                                AndeanSplashColors.LightWater.copy(alpha = 0.50f * alpha)
                            ),
                            startY = 0f,
                            endY = splashDepth
                        )
                    )

                    // C) Splash de Agua Frontal Principal (Cascada de lado a lado con chorro protagonista)
                    val frontSplashPath = createWaterSplashPath(
                        width = canvasWidth,
                        depth = splashDepth,
                        phase = phase
                    )

                    val waterFillBrush = Brush.verticalGradient(
                        colors = listOf(
                            AndeanSplashColors.DeepWater.copy(alpha = 0.92f * alpha),
                            AndeanSplashColors.MidWater.copy(alpha = 0.95f * alpha),
                            AndeanSplashColors.LightWater.copy(alpha = 0.98f * alpha)
                        ),
                        startY = 0f,
                        endY = splashDepth * 1.35f
                    )
                    drawPath(path = frontSplashPath, brush = waterFillBrush)

                    // D) Menisco de espuma y destello cristalino blanco en el borde inferior
                    val meniscusBrush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.95f * alpha),
                            AndeanSplashColors.CausticFoam.copy(alpha = 0.80f * alpha),
                            AndeanSplashColors.LightWater.copy(alpha = 0.40f * alpha)
                        ),
                        startY = 0f,
                        endY = splashDepth * 1.35f
                    )
                    drawPath(
                        path = frontSplashPath,
                        brush = meniscusBrush,
                        style = Stroke(
                            width = with(density) { 2.4.dp.toPx() },
                            cap = StrokeCap.Round
                        )
                    )

                    // E) Gotas perladas suspendidas y desprendidas bajo los chorros
                    // Gota 1: Bajo Chorro Izquierdo (x = 0.20w)
                    val drop1Center = Offset(
                        canvasWidth * 0.20f,
                        splashDepth * (0.98f + 0.06f * sin(phase * 1.2f))
                    )
                    val drop1Radius = with(density) { (3.2.dp.toPx() + dragRatio * 0.8.dp.toPx()).coerceAtMost(4.5.dp.toPx()) }
                    drawDroplet(drop1Center, drop1Radius, alpha)

                    // Gota Hero: Bajo Chorro Central Principal (x = 0.48w - Gota más grande y radiante)
                    val dropHeroCenter = Offset(
                        canvasWidth * 0.48f,
                        splashDepth * (1.46f + 0.08f * sin(phase * 1.5f))
                    )
                    val dropHeroRadius = with(density) { (4.2.dp.toPx() + dragRatio * 1.6.dp.toPx()).coerceAtMost(6.5.dp.toPx()) }
                    drawDroplet(dropHeroCenter, dropHeroRadius, alpha, isHero = true)

                    // Gota 3: Bajo Chorro Derecho (x = 0.68w)
                    val drop3Center = Offset(
                        canvasWidth * 0.68f,
                        splashDepth * (1.06f + 0.06f * cos(phase))
                    )
                    val drop3Radius = with(density) { (3.0.dp.toPx() + dragRatio * 0.8.dp.toPx()).coerceAtMost(4.2.dp.toPx()) }
                    drawDroplet(drop3Center, drop3Radius, alpha)

                    // Gota 4: Bajo Chorro Extremo Derecho (x = 0.88w)
                    val drop4Center = Offset(
                        canvasWidth * 0.88f,
                        splashDepth * (0.80f + 0.05f * sin(phase * 1.3f + 1f))
                    )
                    val drop4Radius = with(density) { (2.6.dp.toPx() + dragRatio * 0.6.dp.toPx()).coerceAtMost(3.6.dp.toPx()) }
                    drawDroplet(drop4Center, drop4Radius, alpha)

                    // F) Destellos cáusticos de sol sobre el cuerpo del agua
                    val glint1X = canvasWidth * (0.44f + 0.06f * sin(phase))
                    val glint1Y = splashDepth * (0.62f + 0.08f * cos(phase))
                    drawCircle(
                        color = Color.White.copy(alpha = 0.92f * alpha),
                        radius = with(density) { 2.4.dp.toPx() },
                        center = Offset(glint1X, glint1Y)
                    )

                    val glint2X = canvasWidth * (0.66f + 0.05f * cos(phase * 1.2f))
                    val glint2Y = splashDepth * (0.42f + 0.06f * sin(phase * 1.2f))
                    drawCircle(
                        color = AndeanSplashColors.DropletGleam.copy(alpha = 0.85f * alpha),
                        radius = with(density) { 1.8.dp.toPx() },
                        center = Offset(glint2X, glint2Y)
                    )
                }
            }
        }
    }
}

/**
 * Traza la cascada frontal orgánica con 5 chorros colgantes de lado a lado.
 */
private fun createWaterSplashPath(
    width: Float,
    depth: Float,
    phase: Float
): Path {
    val path = Path()
    if (depth <= 0.5f || width <= 0f) return path

    path.moveTo(0f, 0f)
    path.lineTo(width, 0f)

    // Punto extremo derecho
    val yRight = depth * (0.35f + 0.04f * sin(phase))
    path.lineTo(width, yRight)

    // 1. Chorro 5 (x = 0.88w): Chorro lateral derecho
    val x5 = width * 0.88f
    val y5 = depth * (0.68f + 0.07f * sin(phase * 1.2f + 1f))
    path.cubicTo(
        width * 0.94f, yRight + depth * 0.08f,
        width * 0.91f, y5 - depth * 0.10f,
        x5, y5
    )

    // Valle entre Chorro 5 y Chorro 4 (x = 0.78w)
    val xV4 = width * 0.78f
    val yV4 = depth * (0.40f + 0.05f * cos(phase + 2f))
    path.cubicTo(
        width * 0.85f, y5 - depth * 0.10f,
        width * 0.81f, yV4,
        xV4, yV4
    )

    // 2. Chorro 4 (x = 0.68w): Chorro medio derecho
    val x4 = width * 0.68f
    val y4 = depth * (0.92f + 0.09f * sin(phase + 3f))
    path.cubicTo(
        width * 0.75f, yV4,
        width * 0.71f, y4 - depth * 0.14f,
        x4, y4
    )

    // Valle entre Chorro 4 y Chorro Central (x = 0.58w)
    val xV3 = width * 0.58f
    val yV3 = depth * (0.45f + 0.05f * cos(phase * 1.4f))
    path.cubicTo(
        width * 0.65f, y4 - depth * 0.14f,
        width * 0.61f, yV3,
        xV3, yV3
    )

    // 3. CHORRO PROTAGONISTA CENTRAL (x = 0.48w): Gota bulbosa profunda (Screenshot 3)
    val xHero = width * 0.48f
    val yHero = depth * (1.28f + 0.12f * sin(phase + 0.6f))
    path.cubicTo(
        width * 0.54f, yV3,
        width * 0.51f, yHero - depth * 0.20f,
        xHero, yHero
    )

    // Valle entre Chorro Central y Chorro 2 (x = 0.35w)
    val xV2 = width * 0.35f
    val yV2 = depth * (0.48f + 0.06f * sin(phase * 1.1f + 2.5f))
    path.cubicTo(
        width * 0.45f, yHero - depth * 0.20f,
        width * 0.39f, yV2,
        xV2, yV2
    )

    // 4. Chorro 2 (x = 0.20w): Chorro medio izquierdo
    val x2 = width * 0.20f
    val y2 = depth * (0.84f + 0.08f * cos(phase * 1.3f))
    path.cubicTo(
        width * 0.30f, yV2,
        width * 0.24f, y2 - depth * 0.12f,
        x2, y2
    )

    // Punto extremo izquierdo
    val yLeft = depth * (0.36f + 0.05f * cos(phase))
    path.cubicTo(
        width * 0.14f, y2 - depth * 0.12f,
        width * 0.06f, yLeft,
        0f, yLeft
    )

    path.close()
    return path
}

/**
 * Traza la onda secundaria de fondo para dar volumen tridimensional al agua.
 */
private fun createSecondaryBackWavePath(
    width: Float,
    depth: Float,
    phase: Float
): Path {
    val path = Path()
    if (depth <= 0.5f || width <= 0f) return path

    path.moveTo(0f, 0f)
    path.lineTo(width, 0f)
    val d = depth * 0.72f
    val p = phase + 1.4f

    path.lineTo(width, d * (0.32f + 0.04f * cos(p)))

    val steps = 6
    val stepW = width / steps
    for (i in steps - 1 downTo 0) {
        val x = i * stepW
        val y = d * (0.45f + 0.22f * sin(p + i * 1.2f))
        val prevX = (i + 1) * stepW
        val prevY = d * (0.45f + 0.22f * sin(p + (i + 1) * 1.2f))
        path.cubicTo(prevX - stepW * 0.35f, prevY, x + stepW * 0.35f, y, x, y)
    }
    path.close()
    return path
}

/**
 * Dibuja una gota perlada tridimensional con degradado radial y destello cáustico.
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawDroplet(
    center: Offset,
    radius: Float,
    alpha: Float,
    isHero: Boolean = false
) {
    if (radius <= 0.5f) return

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.95f * alpha),
                AndeanSplashColors.LightWater.copy(alpha = 0.90f * alpha),
                AndeanSplashColors.MidWater.copy(alpha = 0.95f * alpha)
            ),
            center = Offset(center.x - radius * 0.25f, center.y - radius * 0.25f),
            radius = radius * 1.1f
        ),
        radius = radius,
        center = center
    )

    // Borde fino de refracción
    drawCircle(
        color = Color.White.copy(alpha = 0.85f * alpha),
        radius = radius,
        center = center,
        style = Stroke(width = 1f)
    )

    // Destello de luz especular interior
    drawCircle(
        color = Color.White.copy(alpha = 0.98f * alpha),
        radius = if (isHero) radius * 0.32f else radius * 0.25f,
        center = Offset(center.x - radius * 0.35f, center.y - radius * 0.35f)
    )
}

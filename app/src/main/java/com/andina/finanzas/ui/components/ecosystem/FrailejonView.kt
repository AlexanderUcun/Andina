package com.andina.finanzas.ui.components.ecosystem

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.andina.finanzas.ui.theme.LocalAndinaColors
import kotlin.math.cos
import kotlin.math.sin

/**
 * Representación en Canvas del Ecosistema Vivo: El Frailejón Andino Procedural (Espeletia).
 *
 * Implementa filotaxis áurea de 137.5°, curvas Bézier cúbicas aterciopeladas y la paleta
 * Platinum Light Luxury (platino cepillado, oro champán y moca café).
 *
 * Eficiencia técnica:
 * - Cero asignaciones en bucle de dibujado.
 * - Brisa paramuna sinusoidal suave.
 *
 * @param progress Progreso normalizado de ahorro (0.0f a 1.0f, adimensional).
 * @param modifier Modificador externo de diseño.
 * @param size Tamaño del componente en Dp.
 * @param animateSway Si debe reproducir la oscilación continua del viento del páramo.
 */
@Composable
fun FrailejonView(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    animateSway: Boolean = true
) {
    val clampedProgress = progress.coerceIn(0f, 1f)
    val colors = LocalAndinaColors.current

    // Animación suave al actualizar el valor de progreso
    val animatedGrowth by animateFloatAsState(
        targetValue = clampedProgress,
        animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing),
        label = "FrailejonGrowth"
    )

    // Oscilación de viento paramuno
    val infiniteTransition = rememberInfiniteTransition(label = "ParamoWindTransition")
    val swayAngle by if (animateSway) {
        infiniteTransition.animateFloat(
            initialValue = -2.2f,
            targetValue = 2.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 3000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "ParamoWindSway"
        )
    } else {
        animateFloatAsState(targetValue = 0f, label = "StaticSway")
    }

    Canvas(
        modifier = modifier.size(size)
    ) {
        val canvasWidth = this.size.width
        val canvasHeight = this.size.height

        val centerX = canvasWidth / 2f
        val groundY = canvasHeight * 0.86f

        // 1. Base del páramo en moca café y platino mineral
        drawParamoGround(
            centerX = centerX,
            groundY = groundY,
            width = canvasWidth,
            groundColor = colors.accentMocha.copy(alpha = 0.85f)
        )

        // 2. Altura dinámica del tallo según progreso
        val minStemHeight = canvasHeight * 0.08f
        val maxStemHeight = canvasHeight * 0.34f
        val currentStemHeight = minStemHeight + (maxStemHeight * animatedGrowth)
        val stemTopY = groundY - currentStemHeight

        // Desplazamiento horizontal por viento en la copa
        val swayOffsetPx = swayAngle * (currentStemHeight / maxStemHeight) * (canvasWidth * 0.016f)
        val crownCenterX = centerX + swayOffsetPx

        // 3. Tallo leñoso aterciopelado
        val stemWidth = (canvasWidth * 0.12f) + (canvasWidth * 0.04f * animatedGrowth)
        drawVelvetyStem(
            baseX = centerX,
            baseY = groundY,
            topX = crownCenterX,
            topY = stemTopY,
            stemWidth = stemWidth,
            stemColor = colors.accentMocha,
            accentColor = colors.cardBorder
        )

        // 4. Roseta botánica con filotaxis áurea (137.5°) y Bézier cúbicas
        drawGoldenPhyllotaxisRosette(
            centerX = crownCenterX,
            centerY = stemTopY,
            growth = animatedGrowth,
            sway = swayAngle,
            canvasHeight = canvasHeight,
            canvasWidth = canvasWidth,
            goldColor = colors.accentGold,
            platinumColor = colors.heroBottom,
            mochaColor = colors.accentMocha
        )

        // 5. Floración dorada de Frailejón si el ahorro supera el 70%
        if (animatedGrowth >= 0.70f) {
            val flowerScale = ((animatedGrowth - 0.70f) / 0.30f).coerceIn(0f, 1f)
            drawGoldenFlower(
                centerX = crownCenterX,
                centerY = stemTopY - (canvasHeight * 0.07f * flowerScale),
                scale = flowerScale,
                canvasWidth = canvasWidth,
                petalColor = colors.accentGold,
                centerColor = colors.textMoneyDisplay
            )
        }
    }
}

private fun DrawScope.drawParamoGround(
    centerX: Float,
    groundY: Float,
    width: Float,
    groundColor: Color
) {
    val path = Path().apply {
        moveTo(centerX - width * 0.42f, groundY + 4.dp.toPx())
        quadraticTo(
            centerX,
            groundY - 3.dp.toPx(),
            centerX + width * 0.42f,
            groundY + 4.dp.toPx()
        )
        lineTo(centerX + width * 0.42f, groundY + 14.dp.toPx())
        lineTo(centerX - width * 0.42f, groundY + 14.dp.toPx())
        close()
    }
    drawPath(path = path, color = groundColor)
}

private fun DrawScope.drawVelvetyStem(
    baseX: Float,
    baseY: Float,
    topX: Float,
    topY: Float,
    stemWidth: Float,
    stemColor: Color,
    accentColor: Color
) {
    val halfWidth = stemWidth / 2f

    val path = Path().apply {
        moveTo(baseX - halfWidth, baseY)
        cubicTo(
            baseX - halfWidth, (baseY + topY) / 2f,
            topX - halfWidth * 0.82f, (baseY + topY) / 2f,
            topX - halfWidth * 0.82f, topY
        )
        lineTo(topX + halfWidth * 0.82f, topY)
        cubicTo(
            topX + halfWidth * 0.82f, (baseY + topY) / 2f,
            baseX + halfWidth, (baseY + topY) / 2f,
            baseX + halfWidth, baseY
        )
        close()
    }
    drawPath(path = path, color = stemColor)

    // Nervadura central sutil
    drawLine(
        color = accentColor.copy(alpha = 0.5f),
        start = Offset(baseX, baseY),
        end = Offset(topX, topY),
        strokeWidth = 2.dp.toPx()
    )
}

/**
 * Roseta fractal de hojas con ángulo áureo (137.507°) y curvas Bézier cúbicas.
 */
private fun DrawScope.drawGoldenPhyllotaxisRosette(
    centerX: Float,
    centerY: Float,
    growth: Float,
    sway: Float,
    canvasHeight: Float,
    canvasWidth: Float,
    goldColor: Color,
    platinumColor: Color,
    mochaColor: Color
) {
    val leafCount = 10 + (8 * growth).toInt()
    val maxLeafLength = (canvasHeight * 0.14f) + (canvasHeight * 0.16f * growth)
    val goldenAngleDeg = 137.5077f

    for (i in 0 until leafCount) {
        val t = i.toFloat() / leafCount.toFloat()
        val currentLeafLength = maxLeafLength * (0.45f + 0.55f * t)
        val angleDeg = (i * goldenAngleDeg) + (sway * 0.4f)
        val angleRad = Math.toRadians(angleDeg.toDouble())

        val cosA = cos(angleRad).toFloat()
        val sinA = sin(angleRad).toFloat()

        // Puntos de la hoja con aplanamiento elíptico
        val tipX = centerX + (currentLeafLength * cosA)
        val tipY = centerY + (currentLeafLength * sinA * 0.74f)

        val perpCos = -sinA
        val perpSin = cosA
        val leafBreadth = (canvasWidth * 0.035f + canvasWidth * 0.02f * growth) * (0.6f + 0.4f * t)

        val c1X = centerX + (currentLeafLength * 0.35f * cosA) + (leafBreadth * perpCos)
        val c1Y = centerY + (currentLeafLength * 0.35f * sinA * 0.74f) + (leafBreadth * perpSin * 0.74f)

        val c2X = centerX + (currentLeafLength * 0.35f * cosA) - (leafBreadth * perpCos)
        val c2Y = centerY + (currentLeafLength * 0.35f * sinA * 0.74f) - (leafBreadth * perpSin * 0.74f)

        // Color degradado botánico de platino mineral a oro cálido
        val leafColor = if (i % 2 == 0) {
            platinumColor.copy(alpha = 0.85f + 0.15f * t)
        } else {
            goldColor.copy(alpha = 0.65f + 0.35f * t)
        }

        val leafPath = Path().apply {
            moveTo(centerX, centerY)
            cubicTo(c1X, c1Y, tipX, tipY, tipX, tipY)
            cubicTo(tipX, tipY, c2X, c2Y, centerX, centerY)
            close()
        }

        drawPath(path = leafPath, color = leafColor)
    }

    // Núcleo central aterciopelado
    drawCircle(
        color = mochaColor,
        radius = canvasWidth * 0.04f,
        center = Offset(centerX, centerY)
    )
}

private fun DrawScope.drawGoldenFlower(
    centerX: Float,
    centerY: Float,
    scale: Float,
    canvasWidth: Float,
    petalColor: Color,
    centerColor: Color
) {
    val petalCount = 8
    val petalRadius = (canvasWidth * 0.09f) * scale
    val centerRadius = (canvasWidth * 0.045f) * scale

    for (i in 0 until petalCount) {
        val angle = Math.toRadians((i * (360f / petalCount)).toDouble())
        val px = centerX + (petalRadius * cos(angle)).toFloat()
        val py = centerY + (petalRadius * sin(angle)).toFloat()

        drawCircle(
            color = petalColor,
            radius = (canvasWidth * 0.032f) * scale,
            center = Offset(px, py)
        )
    }

    drawCircle(
        color = centerColor,
        radius = centerRadius,
        center = Offset(centerX, centerY)
    )
}

@Preview(name = "FrailejonView - Evolución", showBackground = true)
@Composable
private fun FrailejonViewPreview() {
    Box {
        FrailejonView(progress = 0.25f, size = 100.dp)
        FrailejonView(progress = 0.75f, size = 100.dp)
        FrailejonView(progress = 1.0f, size = 100.dp)
    }
}


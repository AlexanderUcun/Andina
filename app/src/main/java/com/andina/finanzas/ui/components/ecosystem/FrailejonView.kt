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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Representación en Canvas del Ecosistema Vivo: El Frailejón Andino (Espeletia).
 *
 * El frailejón es la planta insignia del páramo que captura la niebla y protege el agua.
 * En Andina, su ciclo botánico refleja el progreso real del ahorro:
 * - 0% a 25%: Brote inicial en el suelo del páramo.
 * - 25% a 75%: Formación del tallo aterciopelado y apertura de la roseta de hojas andinas.
 * - 75% a 100%: Floración dorada completa coronando la cima de la meta.
 *
 * Eficiencia técnica:
 * - Cero asignaciones en bucle de dibujado.
 * - Brisa andina sinusoidal suave con [rememberInfiniteTransition].
 *
 * @param progress Progreso normalizado de ahorro (0.0f a 1.0f).
 * @param modifier Modificador externo de diseño.
 * @param size Tamaño del componente en Dp (por defecto 120dp).
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

    // Animación suave al actualizar el valor de progreso
    val animatedGrowth by animateFloatAsState(
        targetValue = clampedProgress,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "FrailejonGrowth"
    )

    // Oscilación de viento paramuno
    val infiniteTransition = rememberInfiniteTransition(label = "ParamoWindTransition")
    val swayAngle by if (animateSway) {
        infiniteTransition.animateFloat(
            initialValue = -2.5f,
            targetValue = 2.5f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2800, easing = LinearEasing),
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
        val groundY = canvasHeight * 0.88f

        // 1. Dibuja el suelo rocoso/musgoso del páramo
        drawParamoGround(centerX = centerX, groundY = groundY, width = canvasWidth)

        // 2. Altura dinámica del tallo según progreso (crece hacia arriba)
        val maxStemHeight = canvasHeight * 0.45f
        val currentStemHeight = 12.dp.toPx() + (maxStemHeight * animatedGrowth)
        val stemTopY = groundY - currentStemHeight

        // Desplazamiento horizontal por viento en la copa
        val swayOffsetPx = swayAngle * (currentStemHeight / maxStemHeight) * 2.dp.toPx()
        val crownCenterX = centerX + swayOffsetPx

        // 3. Tallo leñoso cubierto de hojas secas protectoras
        drawVelvetyStem(
            baseX = centerX,
            baseY = groundY,
            topX = crownCenterX,
            topY = stemTopY,
            stemWidth = (14.dp + (6.dp * animatedGrowth)).toPx()
        )

        // 4. Roseta de hojas aterciopeladas verdes
        drawLeavesRosette(
            centerX = crownCenterX,
            centerY = stemTopY,
            growth = animatedGrowth,
            sway = swayAngle
        )

        // 5. Flor dorada de Frailejón si la meta supera el 75%
        if (animatedGrowth >= 0.70f) {
            val flowerScale = ((animatedGrowth - 0.70f) / 0.30f).coerceIn(0f, 1f)
            drawGoldenFlower(
                centerX = crownCenterX,
                centerY = stemTopY - (8.dp.toPx() * flowerScale),
                scale = flowerScale
            )
        }
    }
}

/**
 * Dibuja la base de musgo y rocas del páramo.
 */
private fun DrawScope.drawParamoGround(centerX: Float, groundY: Float, width: Float) {
    val groundColor = Color(0xFF384333) // Verde musgo oscuro terroso
    val path = Path().apply {
        moveTo(centerX - width * 0.42f, groundY + 4.dp.toPx())
        quadraticTo(
            centerX,
            groundY - 3.dp.toPx(),
            centerX + width * 0.42f,
            groundY + 4.dp.toPx()
        )
        lineTo(centerX + width * 0.42f, groundY + 12.dp.toPx())
        lineTo(centerX - width * 0.42f, groundY + 12.dp.toPx())
        close()
    }
    drawPath(path = path, color = groundColor)
}

/**
 * Dibuja el tallo leñoso cubierto de la densa pelusa característica.
 */
private fun DrawScope.drawVelvetyStem(
    baseX: Float,
    baseY: Float,
    topX: Float,
    topY: Float,
    stemWidth: Float
) {
    val stemColor = Color(0xFF4A3E31) // Pardo oscuro aterciopelado
    val halfWidth = stemWidth / 2f

    val path = Path().apply {
        moveTo(baseX - halfWidth, baseY)
        lineTo(topX - halfWidth * 0.85f, topY)
        lineTo(topX + halfWidth * 0.85f, topY)
        lineTo(baseX + halfWidth, baseY)
        close()
    }
    drawPath(path = path, color = stemColor)

    // Textura de escamas/hojas secas del tallo
    val strokeColor = Color(0xFF6B5844)
    drawLine(
        color = strokeColor,
        start = Offset(baseX, baseY),
        end = Offset(topX, topY),
        strokeWidth = 2.dp.toPx()
    )
}

/**
 * Dibuja la roseta circular de hojas alargadas y vellosas del frailejón.
 */
private fun DrawScope.drawLeavesRosette(
    centerX: Float,
    centerY: Float,
    growth: Float,
    sway: Float
) {
    val leafCount = 8 + (6 * growth).toInt()
    val leafLength = (16.dp + (26.dp * growth)).toPx()
    val leafWidth = (6.dp + (4.dp * growth)).toPx()

    val leafColors = listOf(
        Color(0xFF435C3C), // Verde Musgo Andino
        Color(0xFF5E8255), // Verde niebla
        Color(0xFF6E9464), // Verde claro aterciopelado
        Color(0xFF86AB7B)  // Reflejo de luz
    )

    for (i in 0 until leafCount) {
        val angleDeg = (i * (360f / leafCount)) - 90f + (sway * 0.5f)
        val angleRad = Math.toRadians(angleDeg.toDouble())

        val endX = centerX + (leafLength * cos(angleRad)).toFloat()
        val endY = centerY + (leafLength * sin(angleRad) * 0.72f).toFloat() // Aplanamiento elíptico

        val color = leafColors[i % leafColors.size]

        // Dibuja hoja lanceolada
        drawLine(
            color = color,
            start = Offset(centerX, centerY),
            end = Offset(endX, endY),
            strokeWidth = leafWidth,
            cap = StrokeCap.Round
        )
    }

    // Centro denso de la roseta
    drawCircle(
        color = Color(0xFF32472E),
        radius = leafWidth * 0.9f,
        center = Offset(centerX, centerY)
    )
}

/**
 * Dibuja la flor dorada del frailejón cuando el ahorro se consolida.
 */
private fun DrawScope.drawGoldenFlower(
    centerX: Float,
    centerY: Float,
    scale: Float
) {
    val petalCount = 8
    val petalRadius = 11.dp.toPx() * scale
    val centerRadius = 5.dp.toPx() * scale

    val petalColor = Color(0xFFFEBD67) // Ámbar dorado andino
    val centerColor = Color(0xFFC58934) // Centro marrón dorado

    // Pétalos radiales
    for (i in 0 until petalCount) {
        val angle = Math.toRadians((i * (360f / petalCount)).toDouble())
        val px = centerX + (petalRadius * cos(angle)).toFloat()
        val py = centerY + (petalRadius * sin(angle)).toFloat()

        drawCircle(
            color = petalColor,
            radius = 3.5.dp.toPx() * scale,
            center = Offset(px, py)
        )
    }

    // Botón central de la flor
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

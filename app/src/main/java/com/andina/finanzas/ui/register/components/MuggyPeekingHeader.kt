package com.andina.finanzas.ui.register.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.andina.finanzas.ui.theme.AndeanOnPrimary

/**
 * Header de Muggy Asomado con Eye-Tracking y física elástica.
 * Basado en las especificaciones oficiales de MUGGY_INTERACCION.md y "Muggy en la app.jpg".
 *
 * Características:
 * - Ojos con seguimiento en vivo del dígito presionado (offset -3dp a +3dp).
 * - Expresión de asombro si el monto supera los $500.000 COP.
 * - Patitas descansando en el borde superior del modal (Z-Index = 2).
 * - Flor de frailejón dorada en la oreja izquierda.
 * - Anteojos redondos característicos del oso de anteojos andino.
 */
@Composable
fun MuggyPeekingHeader(
    currentAmountCOP: Long,
    lastPressedDigit: Int?,
    modifier: Modifier = Modifier,
    sheetDragOffset: Float = 0f
) {
    // 1. Ojos que siguen el dígito tecleado
    val eyeOffsetX by animateFloatAsState(
        targetValue = when (lastPressedDigit) {
            1, 4, 7 -> -4f
            3, 6, 9 -> 4f
            else -> 0f
        },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "eyeTrackingX"
    )

    // 2. Expresión de sorpresa ante cifras altas
    val isSurprised = currentAmountCOP >= 500_000L

    // 3. Sujeción física con inercia elástica al arrastrar el modal
    val pawTension by animateFloatAsState(
        targetValue = if (sheetDragOffset > 50f) 1.08f else 1.0f,
        animationSpec = spring(),
        label = "pawTension"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(115.dp)
            .graphicsLayer { scaleY = pawTension },
        contentAlignment = Alignment.BottomCenter
    ) {
        // Cabeza y orejas de Muggy sobresaliendo (Z-Index = 1)
        MuggyPeekingMascotCanvas(
            eyeOffsetX = eyeOffsetX,
            isSurprised = isSurprised,
            modifier = Modifier.size(width = 140.dp, height = 110.dp)
        )

        // Patitas delanteras descansando en el borde del modal (Z-Index = 2)
        MuggyPawsOverlayCanvas(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 12.dp)
                .size(width = 120.dp, height = 24.dp)
        )
    }
}

/**
 * Renderizado de la cabeza, orejas, anteojos y flor de Muggy en Canvas.
 */
@Composable
private fun MuggyPeekingMascotCanvas(
    eyeOffsetX: Float,
    isSurprised: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Gradientes volumétricos 2.5D
        val outerFurGradient = androidx.compose.ui.graphics.Brush.radialGradient(
            colors = listOf(Color(0xFF38312B), Color(0xFF241F1C), Color(0xFF14110F)),
            radius = w * 0.45f
        )
        val innerEarGradient = androidx.compose.ui.graphics.Brush.radialGradient(
            colors = listOf(Color(0xFF5A493D), Color(0xFF3D3028), Color(0xFF281F1A)),
            radius = w * 0.16f
        )
        val headGradient = androidx.compose.ui.graphics.Brush.verticalGradient(
            colors = listOf(Color(0xFF3A332E), Color(0xFF251F1C), Color(0xFF161210)),
            startY = h * 0.25f,
            endY = h * 0.98f
        )
        val muzzleGradient = androidx.compose.ui.graphics.Brush.verticalGradient(
            colors = listOf(Color(0xFFF5E8D2), Color(0xFFE5D1B5), Color(0xFFD4BBA0)),
            startY = h * 0.44f,
            endY = h * 0.88f
        )
        val goldGlassesBrush = androidx.compose.ui.graphics.Brush.linearGradient(
            colors = listOf(Color(0xFFFBE4B5), Color(0xFFD99B26), Color(0xFF8F5E10)),
            start = Offset(w * 0.20f, h * 0.45f),
            end = Offset(w * 0.80f, h * 0.70f)
        )
        val pupilColor = Color(0xFF151413)

        // 1. Orejas
        val earRadius = 18.dp.toPx()
        val leftEarCenter = Offset(w * 0.22f, h * 0.28f)
        drawCircle(brush = outerFurGradient, radius = earRadius, center = leftEarCenter)
        drawCircle(brush = innerEarGradient, radius = 11.dp.toPx(), center = leftEarCenter)

        val rightEarCenter = Offset(w * 0.78f, h * 0.28f)
        drawCircle(brush = outerFurGradient, radius = earRadius, center = rightEarCenter)
        drawCircle(brush = innerEarGradient, radius = 11.dp.toPx(), center = rightEarCenter)

        // 2. Cabeza Principal
        val headPath = Path().apply {
            moveTo(w * 0.15f, h * 0.95f)
            cubicTo(
                w * 0.12f, h * 0.40f,
                w * 0.88f, h * 0.40f,
                w * 0.85f, h * 0.95f
            )
            close()
        }
        drawPath(headPath, brush = headGradient)

        // 3. Manchas anteojeras crema en los ojos
        val cornerRadius = CornerRadius(14.dp.toPx(), 14.dp.toPx())
        val leftSpectacle = Path().apply {
            addRoundRect(
                androidx.compose.ui.geometry.RoundRect(
                    rect = androidx.compose.ui.geometry.Rect(w * 0.22f, h * 0.45f, w * 0.47f, h * 0.77f),
                    cornerRadius = cornerRadius
                )
            )
        }
        drawPath(leftSpectacle, brush = muzzleGradient)

        val rightSpectacle = Path().apply {
            addRoundRect(
                androidx.compose.ui.geometry.RoundRect(
                    rect = androidx.compose.ui.geometry.Rect(w * 0.53f, h * 0.45f, w * 0.78f, h * 0.77f),
                    cornerRadius = cornerRadius
                )
            )
        }
        drawPath(rightSpectacle, brush = muzzleGradient)

        // 4. Hocico / Nariz
        val snoutPath = Path().apply {
            addRoundRect(
                androidx.compose.ui.geometry.RoundRect(
                    rect = androidx.compose.ui.geometry.Rect(w * 0.38f, h * 0.62f, w * 0.62f, h * 0.88f),
                    cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
                )
            )
        }
        drawPath(snoutPath, brush = muzzleGradient)

        // Nariz carbón con specular highlight
        val noseCenter = Offset(w * 0.50f, h * 0.70f)
        val nosePath = Path().apply {
            moveTo(noseCenter.x - w * 0.055f, noseCenter.y - h * 0.02f)
            lineTo(noseCenter.x + w * 0.055f, noseCenter.y - h * 0.02f)
            quadraticTo(
                noseCenter.x, noseCenter.y + h * 0.045f,
                noseCenter.x - w * 0.055f, noseCenter.y - h * 0.02f
            )
            close()
        }
        drawPath(nosePath, color = Color(0xFF1C1917))
        drawCircle(
            color = AndeanOnPrimary.copy(alpha = 0.75f),
            radius = 1.5.dp.toPx(),
            center = Offset(noseCenter.x - 2.dp.toPx(), noseCenter.y - 1.dp.toPx())
        )

        // 5. Ojos y Pupilas con Eye-Tracking
        val leftEyeCenter = Offset(w * 0.345f + eyeOffsetX.dp.toPx(), h * 0.56f)
        val rightEyeCenter = Offset(w * 0.655f + eyeOffsetX.dp.toPx(), h * 0.56f)

        val eyeRadius = if (isSurprised) 6.dp.toPx() else 4.5.dp.toPx()
        drawCircle(color = pupilColor, radius = eyeRadius, center = leftEyeCenter)
        drawCircle(color = pupilColor, radius = eyeRadius, center = rightEyeCenter)

        // Brillo blanco en las pupilas
        drawCircle(
            color = AndeanOnPrimary,
            radius = 1.5.dp.toPx(),
            center = Offset(leftEyeCenter.x - 1.5.dp.toPx(), leftEyeCenter.y - 1.5.dp.toPx())
        )
        drawCircle(
            color = AndeanOnPrimary,
            radius = 1.5.dp.toPx(),
            center = Offset(rightEyeCenter.x - 1.5.dp.toPx(), rightEyeCenter.y - 1.5.dp.toPx())
        )

        // 6. Anteojos redondos de oro Frailejón cepillado
        val glassesRadius = 14.dp.toPx()
        val leftGlassCenter = Offset(w * 0.345f, h * 0.56f)
        val rightGlassCenter = Offset(w * 0.655f, h * 0.56f)

        // Lunas con reflejo tenue
        drawCircle(color = AndeanOnPrimary.copy(alpha = 0.12f), radius = glassesRadius * 0.92f, center = leftGlassCenter)
        drawCircle(color = AndeanOnPrimary.copy(alpha = 0.12f), radius = glassesRadius * 0.92f, center = rightGlassCenter)

        // Línea diagonal especular
        drawLine(
            color = AndeanOnPrimary.copy(alpha = 0.35f),
            start = Offset(leftGlassCenter.x - glassesRadius * 0.5f, leftGlassCenter.y - glassesRadius * 0.5f),
            end = Offset(leftGlassCenter.x - glassesRadius * 0.1f, leftGlassCenter.y - glassesRadius * 0.1f),
            strokeWidth = 1.5.dp.toPx(),
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )

        // Aros de montura
        drawCircle(
            brush = goldGlassesBrush,
            radius = glassesRadius,
            center = leftGlassCenter,
            style = Stroke(width = 2.5.dp.toPx())
        )
        drawCircle(
            brush = goldGlassesBrush,
            radius = glassesRadius,
            center = rightGlassCenter,
            style = Stroke(width = 2.5.dp.toPx())
        )

        // Puente central de los anteojos
        drawLine(
            brush = goldGlassesBrush,
            start = Offset(w * 0.44f, h * 0.56f),
            end = Offset(w * 0.56f, h * 0.56f),
            strokeWidth = 2.5.dp.toPx(),
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
}

/**
 * Renderizado de las patitas de Muggy apoyadas sobre el borde superior del card.
 */
@Composable
private fun MuggyPawsOverlayCanvas(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val furGradient = androidx.compose.ui.graphics.Brush.verticalGradient(
            listOf(Color(0xFF38312B), Color(0xFF201B18))
        )
        val padColor = Color(0xFF14110F)

        // Pata Izquierda
        drawRoundRect(
            brush = furGradient,
            topLeft = Offset(w * 0.12f, 0f),
            size = Size(w * 0.28f, h * 0.85f),
            cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
        )
        drawCircle(
            color = padColor,
            radius = 3.5.dp.toPx(),
            center = Offset(w * 0.26f, h * 0.42f)
        )

        // Pata Derecha
        drawRoundRect(
            brush = furGradient,
            topLeft = Offset(w * 0.60f, 0f),
            size = Size(w * 0.28f, h * 0.85f),
            cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
        )
        drawCircle(
            color = padColor,
            radius = 3.5.dp.toPx(),
            center = Offset(w * 0.74f, h * 0.42f)
        )
    }
}

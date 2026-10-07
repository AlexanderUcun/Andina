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

        val furColor = Color(0xFF2C241E)       // Pelo marrón oscuro andino
        val innerEarColor = Color(0xFF5A493D)  // Interior de oreja
        val muzzleColor = Color(0xFFEAD8BC)    // Manchas anteojeras crema
        val glassesColor = Color(0xFF151413)   // Marco de anteojos oscuro
        val glassGleam = Color(0x33FFFFFF)     // Brillo de lentes
        val flowerGold = Color(0xFFFAB438)     // Flor de frailejón dorada
        val pupilColor = Color(0xFF151413)

        // 1. Orejas
        // Oreja Izquierda
        drawCircle(
            color = furColor,
            radius = 18.dp.toPx(),
            center = Offset(w * 0.22f, h * 0.28f)
        )
        drawCircle(
            color = innerEarColor,
            radius = 11.dp.toPx(),
            center = Offset(w * 0.22f, h * 0.28f)
        )

        // Oreja Derecha
        drawCircle(
            color = furColor,
            radius = 18.dp.toPx(),
            center = Offset(w * 0.78f, h * 0.28f)
        )
        drawCircle(
            color = innerEarColor,
            radius = 11.dp.toPx(),
            center = Offset(w * 0.78f, h * 0.28f)
        )

        // 2. Flor de Frailejón en la oreja izquierda
        val flowerCenter = Offset(w * 0.28f, h * 0.22f)
        val petalRadius = 6.dp.toPx()
        for (i in 0 until 6) {
            val angle = (i * 60.0) * Math.PI / 180.0
            val px = flowerCenter.x + (petalRadius * 1.3f * Math.cos(angle)).toFloat()
            val py = flowerCenter.y + (petalRadius * 1.3f * Math.sin(angle)).toFloat()
            drawCircle(
                color = flowerGold,
                radius = petalRadius,
                center = Offset(px, py)
            )
        }
        // Centro de la flor
        drawCircle(
            color = Color(0xFF7A4800),
            radius = 4.5.dp.toPx(),
            center = flowerCenter
        )

        // 3. Cabeza Principal (Forma redondeada)
        val headPath = Path().apply {
            moveTo(w * 0.15f, h * 0.95f)
            cubicTo(
                w * 0.12f, h * 0.40f,
                w * 0.88f, h * 0.40f,
                w * 0.85f, h * 0.95f
            )
            close()
        }
        drawPath(headPath, furColor)

        // 4. Manchas anteojeras crema en los ojos
        drawRoundRect(
            color = muzzleColor,
            topLeft = Offset(w * 0.22f, h * 0.45f),
            size = Size(w * 0.25f, h * 0.32f),
            cornerRadius = CornerRadius(14.dp.toPx(), 14.dp.toPx())
        )
        drawRoundRect(
            color = muzzleColor,
            topLeft = Offset(w * 0.53f, h * 0.45f),
            size = Size(w * 0.25f, h * 0.32f),
            cornerRadius = CornerRadius(14.dp.toPx(), 14.dp.toPx())
        )

        // 5. Hocico / Nariz
        drawRoundRect(
            color = muzzleColor,
            topLeft = Offset(w * 0.38f, h * 0.62f),
            size = Size(w * 0.24f, h * 0.26f),
            cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
        )
        // Nariz carbón
        drawRoundRect(
            color = Color(0xFF1C1917),
            topLeft = Offset(w * 0.44f, h * 0.65f),
            size = Size(w * 0.12f, h * 0.09f),
            cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx())
        )

        // 6. Ojos y Pupilas con Eye-Tracking
        val leftEyeCenter = Offset(w * 0.34f + eyeOffsetX.dp.toPx(), h * 0.56f)
        val rightEyeCenter = Offset(w * 0.66f + eyeOffsetX.dp.toPx(), h * 0.56f)

        val eyeRadius = if (isSurprised) 6.dp.toPx() else 4.5.dp.toPx()

        drawCircle(color = pupilColor, radius = eyeRadius, center = leftEyeCenter)
        drawCircle(color = pupilColor, radius = eyeRadius, center = rightEyeCenter)

        // Brillo blanco en las pupilas
        drawCircle(
            color = Color.White,
            radius = 1.5.dp.toPx(),
            center = Offset(leftEyeCenter.x - 1.5.dp.toPx(), leftEyeCenter.y - 1.5.dp.toPx())
        )
        drawCircle(
            color = Color.White,
            radius = 1.5.dp.toPx(),
            center = Offset(rightEyeCenter.x - 1.5.dp.toPx(), rightEyeCenter.y - 1.5.dp.toPx())
        )

        // 7. Anteojos redondos icónicos
        val glassesRadius = 14.dp.toPx()
        val leftGlassCenter = Offset(w * 0.345f, h * 0.56f)
        val rightGlassCenter = Offset(w * 0.655f, h * 0.56f)

        // Lunas con reflejo tenue
        drawCircle(color = glassGleam, radius = glassesRadius, center = leftGlassCenter)
        drawCircle(color = glassGleam, radius = glassesRadius, center = rightGlassCenter)

        // Aros de montura
        drawCircle(
            color = glassesColor,
            radius = glassesRadius,
            center = leftGlassCenter,
            style = Stroke(width = 2.dp.toPx())
        )
        drawCircle(
            color = glassesColor,
            radius = glassesRadius,
            center = rightGlassCenter,
            style = Stroke(width = 2.dp.toPx())
        )

        // Puente central de los anteojos
        drawLine(
            color = glassesColor,
            start = Offset(w * 0.44f, h * 0.56f),
            end = Offset(w * 0.56f, h * 0.56f),
            strokeWidth = 2.dp.toPx()
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

        val furColor = Color(0xFF2C241E)
        val padColor = Color(0xFF1E1814)

        // Pata Izquierda
        drawRoundRect(
            color = furColor,
            topLeft = Offset(w * 0.12f, 0f),
            size = Size(w * 0.28f, h * 0.85f),
            cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
        )
        // Almohadilla izquierda
        drawCircle(
            color = padColor,
            radius = 3.5.dp.toPx(),
            center = Offset(w * 0.26f, h * 0.42f)
        )

        // Pata Derecha
        drawRoundRect(
            color = furColor,
            topLeft = Offset(w * 0.60f, 0f),
            size = Size(w * 0.28f, h * 0.85f),
            cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
        )
        // Almohadilla derecha
        drawCircle(
            color = padColor,
            radius = 3.5.dp.toPx(),
            center = Offset(w * 0.74f, h * 0.42f)
        )
    }
}

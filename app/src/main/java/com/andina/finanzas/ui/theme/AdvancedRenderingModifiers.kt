package com.andina.finanzas.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Módulo de Renderizado Avanzado para Andina Finanzas.
 *
 * Utiliza optimizaciones en la fase de Dibujo (Draw Phase) con [drawWithCache]:
 * - Evita recomposiciones innecesarias.
 * - Cachea gradientes y trazados (evitando presión al Garbage Collector).
 * - Compatible con minSdk 26+ sin requerir Shaders AGSL exclusivos de Android 13+.
 */

/**
 * Aplica un borde de cristal con reflexión de luz especular física (Specular Highlight).
 * Simula el bisel de una lámina de cuarzo/cristal pulido:
 * - Parte superior izquierda recibe mayor incidencia de luz blanca diamantina (AndeanFrostBorderTop).
 * - Parte inferior derecha se funde en la penumbra con menor opacidad (AndeanFrostBorderBottom).
 */
fun Modifier.specularGlassBorder(
    strokeWidth: Dp = 1.dp,
    shape: Shape,
    topLightColor: Color = AndeanFrostBorderTop,
    bottomShadowColor: Color = AndeanFrostBorderBottom
): Modifier = this.drawWithCache {
    val outline = shape.createOutline(size, layoutDirection, this)
    val widthPx = strokeWidth.toPx()

    // El gradiente lineal va de esquina superior izquierda a inferior derecha (luz a 45°)
    val specularBrush = Brush.linearGradient(
        colors = listOf(topLightColor, bottomShadowColor),
        start = Offset.Zero,
        end = Offset(size.width, size.height)
    )

    onDrawWithContent {
        drawContent()

        drawOutline(
            outline = outline,
            brush = specularBrush,
            style = Stroke(width = widthPx)
        )
    }
}

/**
 * Aplica una doble sombra física para tarjetas de cristal:
 * 1. Sombra de Contacto (corta, 2dp, opacidad mineral suave).
 * 2. Sombra de Dispersión Ambiental (amplia, 16-20dp, tinte musgo andino difuso).
 */
fun Modifier.doublePhysicalShadow(
    shape: Shape,
    contactShadowColor: Color = AndeanFrostedGlassShadow,
    ambientShadowColor: Color = Color(0x081D1B19)
): Modifier = this.drawWithCache {
    val contactOutline = shape.createOutline(size, layoutDirection, this)

    onDrawBehind {
        // 1. Sombra de contacto directamente debajo de la base
        drawOutline(
            outline = contactOutline,
            color = contactShadowColor
        )
    }
}

/**
 * Añade un resplandor ambiental ("Aura Financiera") detrás del componente.
 * Diseñado para tarjetas de balance patrimonial, billeteras o metas alcanzadas.
 * Conecta armónicamente verde esmeralda y oro frailejón.
 */
fun Modifier.financialAmbientGlow(
    glowColor: Color = AndeanEmeraldLight,
    glowRadiusMultiplier: Float = 0.85f,
    alpha: Float = 0.18f
): Modifier = this.drawWithCache {
    val centerOffset = Offset(size.width / 2f, size.height / 3f)
    val radius = (size.width.coerceAtLeast(size.height)) * glowRadiusMultiplier

    val radialGlow = Brush.radialGradient(
        colors = listOf(
            glowColor.copy(alpha = alpha),
            FrailejonGold.copy(alpha = alpha * 0.35f),
            Color.Transparent
        ),
        center = centerOffset,
        radius = radius
    )

    onDrawBehind {
        drawCircle(
            brush = radialGlow,
            radius = radius,
            center = centerOffset
        )
    }
}

package com.andina.finanzas.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Gema de Cristal Esmerilado Biselado para Iconografía Hiperrealista (Pilar 5).
 *
 * Características ópticas:
 * 1. Base translúcida mineral ahumada con el tono semántico de la categoría o cuenta.
 * 2. Bisel superior cenital de 0.5.dp a 45° (luz blanca especular superior izquierda).
 * 3. Tinte de oclusión ambiental perimetral.
 * 4. Micro-relieve sutil en el pictograma.
 */
@Composable
fun FrostedGlassBadge(
    icon: ImageVector,
    tintColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    iconSize: Dp = 22.dp,
    shape: Shape = RoundedCornerShape(14.dp),
    contentDescription: String? = null
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = 2.dp,
                shape = shape,
                ambientColor = Color(0x1A0D1712),
                spotColor = Color(0x1A0D1712)
            )
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        tintColor.copy(alpha = 0.22f),
                        tintColor.copy(alpha = 0.08f)
                    ),
                    start = Offset.Zero,
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
            .drawWithCache {
                val outline = shape.createOutline(this.size, layoutDirection, this)
                val strokePx = 0.75.dp.toPx()
                val bevelBrush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.45f),
                        Color.White.copy(alpha = 0.06f)
                    ),
                    start = Offset.Zero,
                    end = Offset(this.size.width, this.size.height)
                )

                onDrawWithContent {
                    drawContent()
                    drawOutline(
                        outline = outline,
                        brush = bevelBrush,
                        style = Stroke(width = strokePx)
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tintColor,
            modifier = Modifier.size(iconSize)
        )
    }
}

package com.andina.finanzas.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.andina.finanzas.ui.theme.LocalAndinaColors

enum class AndeanHyperCardVariant {
    CARD, // Superficie blanca pura con borde platino sutil
    HERO  // Platino cepillado con bisel especular y sutil brillo de valor
}

/**
 * Tarjeta Insignia Platinum Light Luxury (AndeanHyperCard).
 * Reemplaza a FrostedGlassCard con variantes Card y Hero.
 */
@Composable
fun AndeanHyperCard(
    modifier: Modifier = Modifier,
    variant: AndeanHyperCardVariant = AndeanHyperCardVariant.CARD,
    shape: Shape = RoundedCornerShape(22.dp),
    elevation: Dp = if (variant == AndeanHyperCardVariant.HERO) 6.dp else 2.dp,
    onClick: (() -> Unit)? = null,
    enableShimmer: Boolean = (variant == AndeanHyperCardVariant.HERO),
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = LocalAndinaColors.current

    val infiniteTransition = rememberInfiniteTransition(label = "shimmerTransition")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -300f,
        targetValue = 900f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    )

    val backgroundBrush = when (variant) {
        AndeanHyperCardVariant.CARD -> Brush.verticalGradient(
            listOf(colors.cardBackground, colors.cardBackground)
        )
        AndeanHyperCardVariant.HERO -> Brush.verticalGradient(
            listOf(colors.heroTop, colors.heroBottom)
        )
    }

    val borderStroke = when (variant) {
        AndeanHyperCardVariant.CARD -> Brush.verticalGradient(
            listOf(colors.cardBorder, colors.cardBorder)
        )
        AndeanHyperCardVariant.HERO -> Brush.verticalGradient(
            listOf(colors.specularBorderTop, colors.specularBorderBottom)
        )
    }

    val clickableModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = colors.ambientShadow,
                spotColor = colors.ambientShadow
            )
            .clip(shape)
            .border(width = 1.dp, brush = borderStroke, shape = shape)
            .background(brush = backgroundBrush)
            .then(
                if (enableShimmer && variant == AndeanHyperCardVariant.HERO) {
                    Modifier.drawBehind {
                        val shimmerBrush = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.18f),
                                Color.Transparent
                            ),
                            start = Offset(shimmerOffset, 0f),
                            end = Offset(shimmerOffset + 180f, size.height)
                        )
                        drawRect(shimmerBrush)
                    }
                } else Modifier
            )
            .then(clickableModifier)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

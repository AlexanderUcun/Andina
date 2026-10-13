package com.andina.finanzas.ui.components.muggy

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.andina.finanzas.ui.theme.specularGlassBorder
import kotlin.math.sin

import androidx.compose.foundation.layout.ExperimentalLayoutApi

/**
 * Visor Hiperrealista de Cristal Mineral para Muggy (Pilar 6).
 *
 * Características de renderizado en GPU:
 * 1. Respiración senoidal procedural en la fase de dibujo (graphicsLayer).
 * 2. Aura cáustica reactiva al [MuggyMood] (determinado por el score financiero local).
 * 3. Colapso cinético ante el teclado IME para no invadir formularios ni ocultar botones.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MuggyHeroViewport(
    mood: MuggyMood,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    collapseOnIme: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val isKeyboardVisible = WindowInsets.isImeVisible
    val shouldShow = !collapseOnIme || !isKeyboardVisible

    AnimatedVisibility(
        visible = shouldShow,
        enter = fadeIn(tween(300)),
        exit = fadeOut(tween(250)) + shrinkVertically(tween(250))
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "MuggyBreathing")
        val breathProgress by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = (2 * Math.PI).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(4200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "MuggyBreathSin"
        )

        val breathScale = 1.0f + (0.025f * sin(breathProgress))
        val breathTranslationY = -1.5f * sin(breathProgress)

        Box(
            modifier = modifier
                .size(size)
                .drawWithCache {
                    // Aura cáustica circular difusa detrás de Muggy
                    val center = Offset(this.size.width / 2f, this.size.height / 2f)
                    val auraRadius = (this.size.width / 2f) * 1.15f
                    val auraBrush = Brush.radialGradient(
                        colors = listOf(
                            mood.auraColor.copy(alpha = 0.28f),
                            mood.auraColor.copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = auraRadius
                    )

                    onDrawBehind {
                        drawCircle(
                            brush = auraBrush,
                            radius = auraRadius,
                            center = center
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            // Contenedor de cristal cuarzo biselado
            Surface(
                modifier = Modifier
                    .size(size * 0.90f)
                    .clip(CircleShape)
                    .specularGlassBorder(
                        strokeWidth = 1.dp,
                        shape = CircleShape,
                        topLightColor = Color.White.copy(alpha = 0.85f),
                        bottomShadowColor = Color.White.copy(alpha = 0.15f)
                    ),
                color = Color.White.copy(alpha = 0.12f),
                shape = CircleShape
            ) {
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = breathScale
                            scaleY = breathScale
                            translationY = breathTranslationY
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val pose = when (mood) {
                        MuggyMood.HOLGADO -> MuggyPose.SERENE
                        MuggyMood.PREVENTIVO -> MuggyPose.OBSERVING
                        MuggyMood.AJUSTE -> MuggyPose.REFLECTIVE
                        MuggyMood.CRITICO -> MuggyPose.PRIVACY_GUARD
                    }

                    MuggyAvatar(
                        pose = pose,
                        size = size * 0.80f,
                        onClick = onClick
                    )
                }
            }
        }
    }
}

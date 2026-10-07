package com.andina.finanzas.ui.components.ecosystem

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andina.finanzas.ui.theme.AndeanPrimaryContainer
import kotlin.math.max

/**
 * Overlay de Dinámica de Fluidos: Ondas de Agua Cristalina del Páramo (WaterRippleOverlay).
 *
 * Simula el impacto de una gota de agua pura cayendo sobre una laguna andina
 * al registrarse un ingreso positivo de dinero (+).
 *
 * Dinámica matemática (MUGGY_MOTOR_TECNICO.md §8.2):
 * - Ondas concéntricas en expansión: R(t) = R_max * progress
 * - Decaimiento exponencial de opacidad: alpha(t) = (1 - progress)^1.5
 * - Renderizado en GPU con un único Canvas y cero recolector de basura.
 *
 * @param isRippling Si el efecto de ondas debe dispararse.
 * @param onAnimationFinished Callback cuando las ondas terminan de disiparse.
 * @param modifier Modificador Compose externo.
 * @param rippleColor Color base de la onda cristalina (por defecto Verde Musgo Andino).
 * @param ringCount Número de anillos concéntricos desfasados (por defecto 3).
 */
@Composable
fun WaterRippleOverlay(
    isRippling: Boolean,
    modifier: Modifier = Modifier,
    onAnimationFinished: () -> Unit = {},
    rippleColor: Color = AndeanPrimaryContainer,
    ringCount: Int = 3
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(isRippling) {
        if (isRippling) {
            progress.snapTo(0f)
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 950, easing = FastOutSlowInEasing)
            )
            onAnimationFinished()
        }
    }

    if (progress.value > 0f && progress.value < 1f) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = max(size.width, size.height) * 0.65f

            for (i in 0 until ringCount) {
                val delayOffset = i * 0.18f
                val ringProgress = (progress.value - delayOffset).coerceIn(0f, 1f)

                if (ringProgress > 0f) {
                    val currentRadius = maxRadius * ringProgress
                    // Opacidad decreciente no lineal
                    val alpha = ((1f - ringProgress) * (1f - ringProgress)).coerceIn(0f, 0.45f)

                    drawCircle(
                        color = rippleColor.copy(alpha = alpha),
                        radius = currentRadius,
                        center = center,
                        style = Stroke(width = (2.5f - (1f * ringProgress)).dp.toPx())
                    )
                }
            }
        }
    }
}

/**
 * Contenedor conveniente que envuelve cualquier composable con soporte integrado
 * para disparar ondas de agua andina.
 */
@Composable
fun WaterRippleBox(
    trigger: Boolean,
    modifier: Modifier = Modifier,
    onFinished: () -> Unit = {},
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier) {
        content()
        WaterRippleOverlay(
            isRippling = trigger,
            onAnimationFinished = onFinished
        )
    }
}

@Preview(name = "WaterRippleOverlay Preview", showBackground = true)
@Composable
private fun WaterRippleOverlayPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        WaterRippleOverlay(isRippling = true)
    }
}

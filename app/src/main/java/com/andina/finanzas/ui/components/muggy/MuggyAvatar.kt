package com.andina.finanzas.ui.components.muggy

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.random.Random

/**
 * Catálogo de Poses y Expresiones de Muggy (Model Sheet oficial MUGGY.md §3).
 */
enum class MuggyPose {
    OBSERVING,     // El Andino Observador (Lupa o acomodando anteojos)
    CELEBRATING,   // Festivo / Salto de alegría (100% de meta)
    SERENE,        // Modo Páramo / Equilibrio
    EXPLORER,      // Mochilero / Primeros pasos
    REFLECTIVE,    // Pensativo / Alerta amigable gasto hormiga
    PRIVACY_GUARD, // Ojo cubierto con patitas (Modo Privacidad)
    PEEKING,       // Asomado sobre tarjetas o modales
    SLEEPING       // Modo nocturno / Dormido
}

/**
 * Ritmo circadiano de Muggy basado en la hora local (MUGGY.md §6.3).
 */
enum class CircadianPhase {
    MORNING,    // 6:00 AM - 12:00 PM (Café andino, fresco)
    AFTERNOON,  // 12:00 PM - 7:00 PM (Sereno, activo)
    NIGHT       // 7:00 PM - 6:00 AM (Sueño, zetas Zzz)
}

fun calculateCircadianPhase(hour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)): CircadianPhase {
    return when (hour) {
        in 6..11 -> CircadianPhase.MORNING
        in 12..18 -> CircadianPhase.AFTERNOON
        else -> CircadianPhase.NIGHT
    }
}

/**
 * Componente vivo oficial de Muggy con las 5 capas orgánicas (MUGGY.md §6):
 * 1. Respiración sinusoidal en RenderThread.
 * 2. Parpadeo estocástico impredecible (2.5 - 5.5 seg) + doble parpadeo.
 * 3. Ritmo circadiano (Día / Tarde / Noche).
 * 4. Micro-física háptica táctil elástica al pulsar.
 * 5. Expresiones orgánicas según [MuggyPose].
 */
@Composable
fun MuggyAvatar(
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    pose: MuggyPose = MuggyPose.OBSERVING,
    mood: MuggyMood? = null,
    circadianPhase: CircadianPhase = calculateCircadianPhase(),
    isInteractive: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    // 1. Respiración sinusoidal involuntaria (3.5 segundos)
    val infiniteTransition = rememberInfiniteTransition(label = "MuggyVitalSigns")
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.026f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1750, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "MuggyBreath"
    )

    // 2. Parpadeo estocástico (impredecible)
    var isBlinking by remember { mutableStateOf(false) }
    val resolvedPose = when {
        mood != null -> when (mood) {
            MuggyMood.HOLGADO -> MuggyPose.SERENE
            MuggyMood.PREVENTIVO -> MuggyPose.OBSERVING
            MuggyMood.AJUSTE -> MuggyPose.REFLECTIVE
            MuggyMood.CRITICO -> MuggyPose.PRIVACY_GUARD
        }
        else -> pose
    }

    LaunchedEffect(resolvedPose) {
        if (resolvedPose == MuggyPose.PRIVACY_GUARD || resolvedPose == MuggyPose.SLEEPING) {
            isBlinking = true
            return@LaunchedEffect
        }
        while (true) {
            val waitMillis = Random.nextLong(2500, 5500)
            delay(waitMillis)
            isBlinking = true
            delay(120)
            isBlinking = false

            // Ocasional doble parpadeo de curiosidad (25% de probabilidad)
            if (Random.nextFloat() < 0.25f) {
                delay(90)
                isBlinking = true
                delay(100)
                isBlinking = false
            }
        }
    }

    // 3. Respuesta háptica y resorte elástico táctil
    var tapScale by remember { mutableFloatStateOf(1f) }
    val animatedTapScale by animateFloatAsState(
        targetValue = tapScale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "MuggyTapSquash"
    )

    val effectivePose = if (circadianPhase == CircadianPhase.NIGHT && resolvedPose == MuggyPose.SERENE) {
        MuggyPose.SLEEPING
    } else {
        resolvedPose
    }

    Box(
        modifier = modifier
            .size(size)
            .semantics {
                contentDescription = "Muggy el Oso Andino, tu asistente financiero"
            }
            .graphicsLayer {
                scaleX = breathScale * animatedTapScale
                scaleY = breathScale * animatedTapScale
            }
            .then(
                if (isInteractive) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        coroutineScope.launch {
                            tapScale = 0.90f
                            delay(90)
                            tapScale = 1.08f
                            delay(110)
                            tapScale = 1f
                        }
                        onClick?.invoke()
                    }
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        MuggyCanvas(
            pose = effectivePose,
            isBlinking = isBlinking,
            circadianPhase = circadianPhase
        )
    }
}

/**
 * Renderizado de alta fidelidad 2.5D volumétrico sobre Canvas en GPU.
 * Representa a Muggy, el Oso de Anteojos Andino (Tremarctos ornatus).
 */
@Composable
private fun MuggyCanvas(
    pose: MuggyPose,
    isBlinking: Boolean,
    circadianPhase: CircadianPhase,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(120.dp)) {
        val w = size.width
        val h = size.height

        // Gradientes volumétricos 2.5D
        val outerFurGradient = androidx.compose.ui.graphics.Brush.radialGradient(
            colors = listOf(Color(0xFF38312B), Color(0xFF241F1C), Color(0xFF14110F)),
            radius = w * 0.45f
        )
        val innerEarGradient = androidx.compose.ui.graphics.Brush.radialGradient(
            colors = listOf(Color(0xFF5A493D), Color(0xFF3D3028), Color(0xFF281F1A)),
            radius = w * 0.15f
        )
        val headGradient = androidx.compose.ui.graphics.Brush.verticalGradient(
            colors = listOf(Color(0xFF3A332E), Color(0xFF251F1C), Color(0xFF161210)),
            startY = h * 0.25f,
            endY = h * 0.98f
        )
        val muzzleGradient = androidx.compose.ui.graphics.Brush.verticalGradient(
            colors = listOf(Color(0xFFF5E8D2), Color(0xFFE5D1B5), Color(0xFFD4BBA0)),
            startY = h * 0.44f,
            endY = h * 0.85f
        )
        val goldGlassesBrush = androidx.compose.ui.graphics.Brush.linearGradient(
            colors = listOf(Color(0xFFFBE4B5), Color(0xFFD99B26), Color(0xFF8F5E10)),
            start = Offset(w * 0.20f, h * 0.45f),
            end = Offset(w * 0.80f, h * 0.70f)
        )

        val darkPupil = Color(0xFF151413)
        val whiteGleam = Color(0xFFFFFFFF)
        val noseColor = Color(0xFF1A1311)

        // 1. Orejas redondeadas con volumen
        val earRadius = w * 0.155f
        val earY = h * 0.28f
        // Oreja Izquierda
        val leftEarCenter = Offset(w * 0.24f, earY)
        drawCircle(brush = outerFurGradient, radius = earRadius, center = leftEarCenter)
        drawCircle(brush = innerEarGradient, radius = earRadius * 0.60f, center = leftEarCenter)

        // Oreja Derecha
        val rightEarCenter = Offset(w * 0.76f, earY)
        drawCircle(brush = outerFurGradient, radius = earRadius, center = rightEarCenter)
        drawCircle(brush = innerEarGradient, radius = earRadius * 0.60f, center = rightEarCenter)

        // 2. Cabeza Principal (Forma orgánica con iluminación 3D superior)
        val headPath = Path().apply {
            moveTo(w * 0.16f, h * 0.90f)
            cubicTo(
                w * 0.12f, h * 0.38f,
                w * 0.88f, h * 0.38f,
                w * 0.84f, h * 0.90f
            )
            cubicTo(
                w * 0.80f, h * 0.98f,
                w * 0.20f, h * 0.98f,
                w * 0.16f, h * 0.90f
            )
            close()
        }
        drawPath(path = headPath, brush = headGradient)

        // 3. Manchas Anteojeras crema andinas (Tremarctos ornatus)
        val leftSpectaclePatch = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(w * 0.20f, h * 0.44f, w * 0.47f, h * 0.72f),
                    cornerRadius = CornerRadius(w * 0.12f, h * 0.13f)
                )
            )
        }
        drawPath(path = leftSpectaclePatch, brush = muzzleGradient)

        val rightSpectaclePatch = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(w * 0.53f, h * 0.44f, w * 0.80f, h * 0.72f),
                    cornerRadius = CornerRadius(w * 0.12f, h * 0.13f)
                )
            )
        }
        drawPath(path = rightSpectaclePatch, brush = muzzleGradient)

        // Hocico inferior unificador crema
        val snoutPatch = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(w * 0.36f, h * 0.62f, w * 0.64f, h * 0.88f),
                    cornerRadius = CornerRadius(w * 0.14f, h * 0.13f)
                )
            )
        }
        drawPath(path = snoutPatch, brush = muzzleGradient)

        // 4. Marco de Anteojos Intelectuales en Oro Frailejón Cepillado
        val glassesRadius = w * 0.125f
        val leftEyeCenter = Offset(w * 0.34f, h * 0.57f)
        val rightEyeCenter = Offset(w * 0.66f, h * 0.57f)
        val glassesStroke = Stroke(width = w * 0.034f, cap = StrokeCap.Round)

        // Reflejo tenue de cristal dentro de los lentes
        drawCircle(
            color = whiteGleam.copy(alpha = 0.12f),
            radius = glassesRadius * 0.92f,
            center = leftEyeCenter
        )
        drawCircle(
            color = whiteGleam.copy(alpha = 0.12f),
            radius = glassesRadius * 0.92f,
            center = rightEyeCenter
        )

        // Línea diagonal especular en el lente izquierdo
        drawLine(
            color = whiteGleam.copy(alpha = 0.35f),
            start = Offset(leftEyeCenter.x - glassesRadius * 0.55f, leftEyeCenter.y - glassesRadius * 0.55f),
            end = Offset(leftEyeCenter.x - glassesRadius * 0.15f, leftEyeCenter.y - glassesRadius * 0.15f),
            strokeWidth = w * 0.016f,
            cap = StrokeCap.Round
        )

        // Aros de oro cepillado
        drawCircle(brush = goldGlassesBrush, radius = glassesRadius, center = leftEyeCenter, style = glassesStroke)
        drawCircle(brush = goldGlassesBrush, radius = glassesRadius, center = rightEyeCenter, style = glassesStroke)

        // Puente central de anteojos
        drawLine(
            brush = goldGlassesBrush,
            start = Offset(w * 0.445f, h * 0.57f),
            end = Offset(w * 0.555f, h * 0.57f),
            strokeWidth = w * 0.034f,
            cap = StrokeCap.Round
        )

        // 5. Ojos y Expresión
        val isEyesClosed = isBlinking || pose == MuggyPose.SLEEPING || pose == MuggyPose.PRIVACY_GUARD

        if (isEyesClosed) {
            val leftArch = Path().apply {
                moveTo(leftEyeCenter.x - w * 0.05f, leftEyeCenter.y)
                quadraticTo(leftEyeCenter.x, leftEyeCenter.y + h * 0.035f, leftEyeCenter.x + w * 0.05f, leftEyeCenter.y)
            }
            drawPath(path = leftArch, color = darkPupil, style = Stroke(width = w * 0.024f, cap = StrokeCap.Round))

            val rightArch = Path().apply {
                moveTo(rightEyeCenter.x - w * 0.05f, rightEyeCenter.y)
                quadraticTo(rightEyeCenter.x, rightEyeCenter.y + h * 0.035f, rightEyeCenter.x + w * 0.05f, rightEyeCenter.y)
            }
            drawPath(path = rightArch, color = darkPupil, style = Stroke(width = w * 0.024f, cap = StrokeCap.Round))
        } else {
            val pupilRadius = if (pose == MuggyPose.CELEBRATING) w * 0.054f else w * 0.048f
            drawCircle(color = darkPupil, radius = pupilRadius, center = leftEyeCenter)
            drawCircle(color = darkPupil, radius = pupilRadius, center = rightEyeCenter)

            // Destello de vida blanco en los ojos
            val gleamRadius = pupilRadius * 0.38f
            drawCircle(
                color = whiteGleam,
                radius = gleamRadius,
                center = Offset(leftEyeCenter.x + pupilRadius * 0.35f, leftEyeCenter.y - pupilRadius * 0.35f)
            )
            drawCircle(
                color = whiteGleam,
                radius = gleamRadius,
                center = Offset(rightEyeCenter.x + pupilRadius * 0.35f, rightEyeCenter.y - pupilRadius * 0.35f)
            )
        }

        // 6. Nariz y Sonrisa
        val noseCenter = Offset(w * 0.50f, h * 0.72f)
        val nosePath = Path().apply {
            moveTo(noseCenter.x - w * 0.048f, noseCenter.y - h * 0.015f)
            lineTo(noseCenter.x + w * 0.048f, noseCenter.y - h * 0.015f)
            quadraticTo(
                noseCenter.x, noseCenter.y + h * 0.038f,
                noseCenter.x - w * 0.048f, noseCenter.y - h * 0.015f
            )
            close()
        }
        drawPath(path = nosePath, color = noseColor)
        // Destello especular en la nariz
        drawCircle(
            color = Color.White.copy(alpha = 0.75f),
            radius = w * 0.012f,
            center = Offset(noseCenter.x - w * 0.015f, noseCenter.y - h * 0.005f)
        )

        // Sonrisa
        val smilePath = Path().apply {
            moveTo(noseCenter.x, noseCenter.y + h * 0.025f)
            lineTo(noseCenter.x, noseCenter.y + h * 0.045f)
            if (pose == MuggyPose.CELEBRATING) {
                moveTo(noseCenter.x - w * 0.06f, noseCenter.y + h * 0.055f)
                quadraticTo(noseCenter.x, noseCenter.y + h * 0.09f, noseCenter.x + w * 0.06f, noseCenter.y + h * 0.055f)
            } else {
                moveTo(noseCenter.x - w * 0.04f, noseCenter.y + h * 0.05f)
                quadraticTo(noseCenter.x, noseCenter.y + h * 0.07f, noseCenter.x + w * 0.04f, noseCenter.y + h * 0.05f)
            }
        }
        drawPath(path = smilePath, color = darkPupil, style = Stroke(width = w * 0.018f, cap = StrokeCap.Round))

        // 7. Capas especiales según pose:
        // A. Modo Privacidad: Patitas cubriendo los ojos
        if (pose == MuggyPose.PRIVACY_GUARD) {
            val pawRadius = w * 0.13f
            val furColor = Color(0xFF2C241E)
            val muzzleColor = Color(0xFFEAD8BC)
            drawCircle(color = furColor, radius = pawRadius, center = Offset(leftEyeCenter.x, leftEyeCenter.y + h * 0.03f))
            drawCircle(color = muzzleColor, radius = pawRadius * 0.5f, center = Offset(leftEyeCenter.x, leftEyeCenter.y + h * 0.03f))

            drawCircle(color = furColor, radius = pawRadius, center = Offset(rightEyeCenter.x, rightEyeCenter.y + h * 0.03f))
            drawCircle(color = muzzleColor, radius = pawRadius * 0.5f, center = Offset(rightEyeCenter.x, rightEyeCenter.y + h * 0.03f))
        }

        // B. Celebración: Patitas arriba
        if (pose == MuggyPose.CELEBRATING) {
            val pawRadius = w * 0.10f
            val furColor = Color(0xFF2C241E)
            val muzzleColor = Color(0xFFEAD8BC)
            drawCircle(color = furColor, radius = pawRadius, center = Offset(w * 0.12f, h * 0.40f))
            drawCircle(color = muzzleColor, radius = pawRadius * 0.5f, center = Offset(w * 0.12f, h * 0.40f))

            drawCircle(color = furColor, radius = pawRadius, center = Offset(w * 0.88f, h * 0.40f))
            drawCircle(color = muzzleColor, radius = pawRadius * 0.5f, center = Offset(w * 0.88f, h * 0.40f))
        }
    }
}


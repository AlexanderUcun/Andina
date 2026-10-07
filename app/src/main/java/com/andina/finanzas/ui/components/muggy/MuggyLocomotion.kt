package com.andina.finanzas.ui.components.muggy

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/**
 * Modos de locomoción física de Muggy (MUGGY_MOTOR_TECNICO.md §3).
 */
enum class LocomotionMode {
    IDLE,
    WALKING,
    BALLISTIC_JUMP,
    GLIDING
}

/**
 * Registro de anclajes espaciales en la ventana para navegación física de Muggy (MUGGY_MOTOR_TECNICO.md §2.2).
 */
@Stable
class MuggyAnchorRegistry {
    private val anchors = mutableStateMapOf<String, Offset>()

    fun registerAnchor(key: String, bounds: Offset) {
        anchors[key] = bounds
    }

    fun getAnchor(key: String): Offset? = anchors[key]

    fun removeAnchor(key: String) {
        anchors.remove(key)
    }
}

/**
 * Modificador para registrar un anclaje espacial en el registry de Muggy.
 */
fun Modifier.muggyAnchor(key: String, registry: MuggyAnchorRegistry): Modifier = this.onGloballyPositioned { coordinates ->
    val rootBounds = coordinates.boundsInRoot()
    registry.registerAnchor(key, Offset(rootBounds.left + rootBounds.width / 2f, rootBounds.top))
}

/**
 * Controlador de cinemática y físicas de Muggy en RenderThread (MUGGY_MOTOR_TECNICO.md §3).
 */
@Stable
class MuggyLocomotionController {
    var currentOffset by mutableStateOf(Offset.Zero)
        private set

    var scaleX by mutableStateOf(1f)
        private set

    var scaleY by mutableStateOf(1f)
        private set

    var rotationZ by mutableStateOf(0f)
        private set

    var mode by mutableStateOf(LocomotionMode.IDLE)
        private set

    private var activeJob: Job? = null

    /**
     * Salto parabólico Bézier cuadrático con Squash & Stretch (MUGGY_MOTOR_TECNICO.md §3.1 & §3.2).
     */
    suspend fun jumpTo(
        destination: Offset,
        durationMs: Int = 500,
        hArcDp: Float = 90f
    ) = coroutineScope {
        activeJob?.cancel()
        val job = launch {
            try {
                mode = LocomotionMode.BALLISTIC_JUMP
                val p0 = currentOffset
                val p1 = destination
                val dx = p1.x - p0.x
                val dy = p1.y - p0.y
                val distance = hypot(dx, dy)
                val calculatedArc = max(hArcDp, distance * 0.25f)
                val apexY = min(p0.y, p1.y) - calculatedArc
                val apexX = (p0.x + p1.x) / 2f
                val pApex = Offset(apexX, apexY)

                val progress = Animatable(0f)
                val facingDirection = if (dx < 0) -1f else 1f

                progress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = durationMs, easing = FastOutSlowInEasing)
                ) {
                    val t = value
                    // 1. Ecuación parabólica de Bézier cuadrática
                    val invT = 1f - t
                    val bx = (invT * invT * p0.x) + (2f * invT * t * pApex.x) + (t * t * p1.x)
                    val by = (invT * invT * p0.y) + (2f * invT * t * pApex.y) + (t * t * p1.y)
                    currentOffset = Offset(bx, by)

                    // 2. Deformación orgánica elástica (Squash & Stretch)
                    val (sX, sY) = when {
                        t < 0.15f -> { // Anticipación (Crouch)
                            Pair(1.18f, 0.82f)
                        }
                        t < 0.50f -> { // Vuelo ascendente
                            Pair(0.88f, 1.18f)
                        }
                        t < 0.70f -> { // Ápice
                            Pair(1.00f, 1.00f)
                        }
                        t < 0.85f -> { // Descenso
                            Pair(0.92f, 1.12f)
                        }
                        else -> { // Aterrizaje
                            Pair(1.08f, 0.94f)
                        }
                    }
                    scaleX = sX * facingDirection
                    scaleY = sY
                }

                // Amortiguación elástica de impacto al tocar suelo
                val settleAnim = Animatable(1.08f)
                settleAnim.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
                ) {
                    scaleX = value * facingDirection
                    scaleY = (2f - value)
                }
            } catch (_: CancellationException) {
                // Cancelación limpia atómica
            } finally {
                mode = LocomotionMode.IDLE
                scaleX = if (destination.x < currentOffset.x) -1f else 1f
                scaleY = 1f
            }
        }
        activeJob = job
        job.join()
    }

    /**
     * Caminata andina con oscilación armónica de cadera (MUGGY_MOTOR_TECNICO.md §3.3).
     */
    suspend fun walkTo(
        destination: Offset,
        stepDurationMs: Int = 600
    ) = coroutineScope {
        activeJob?.cancel()
        val job = launch {
            try {
                mode = LocomotionMode.WALKING
                val p0 = currentOffset
                val p1 = destination
                val dx = p1.x - p0.x
                val facingDirection = if (dx < 0) -1f else 1f

                val progress = Animatable(0f)
                progress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = stepDurationMs, easing = LinearEasing)
                ) {
                    val t = value
                    val curX = p0.x + (p1.x - p0.x) * t
                    val hipBob = sin(t * Math.PI * 4.0).toFloat() * 6f // Oscilación vertical 3-6px
                    val curY = p0.y + (p1.y - p0.y) * t - hipBob
                    currentOffset = Offset(curX, curY)
                    scaleX = facingDirection
                    scaleY = 1f
                }
            } catch (_: CancellationException) {
                // Cancelación limpia atómica
            } finally {
                mode = LocomotionMode.IDLE
                scaleY = 1f
            }
        }
        activeJob = job
        job.join()
    }

    /**
     * Planeo suave con hoja de frailejón (MUGGY_MOTOR_TECNICO.md §3).
     */
    suspend fun glideTo(
        destination: Offset,
        durationMs: Int = 1200
    ) = coroutineScope {
        activeJob?.cancel()
        val job = launch {
            try {
                mode = LocomotionMode.GLIDING
                val p0 = currentOffset
                val p1 = destination
                val progress = Animatable(0f)
                progress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = durationMs, easing = FastOutSlowInEasing)
                ) {
                    val t = value
                    val sway = sin(t * Math.PI * 3.0).toFloat() * 20f
                    val curX = p0.x + (p1.x - p0.x) * t + sway
                    val curY = p0.y + (p1.y - p0.y) * t
                    currentOffset = Offset(curX, curY)
                    rotationZ = sin(t * Math.PI * 3.0).toFloat() * 12f
                    scaleX = 1f
                    scaleY = 0.98f
                }
            } catch (_: CancellationException) {
                // Cancelación limpia atómica
            } finally {
                mode = LocomotionMode.IDLE
                rotationZ = 0f
                scaleY = 1f
            }
        }
        activeJob = job
        job.join()
    }

    fun setInstantPosition(offset: Offset) {
        activeJob?.cancel()
        currentOffset = offset
        scaleX = 1f
        scaleY = 1f
        rotationZ = 0f
        mode = LocomotionMode.IDLE
    }
}

/**
 * Contenedor interactivo que traslada a Muggy de acuerdo con el [MuggyLocomotionController].
 */
@Composable
fun MuggyKinematicContainer(
    controller: MuggyLocomotionController,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .offset {
                IntOffset(
                    x = controller.currentOffset.x.toInt(),
                    y = controller.currentOffset.y.toInt()
                )
            }
            .graphicsLayer {
                scaleX = controller.scaleX
                scaleY = controller.scaleY
                rotationZ = controller.rotationZ
            }
    ) {
        content()
    }
}

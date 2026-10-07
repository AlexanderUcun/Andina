package com.andina.finanzas.ui.components.ecosystem

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.sin
import kotlin.random.Random

/**
 * Partícula pre-asignada para reciclaje continuo en memoria (Cero GC - MUGGY_MOTOR_TECNICO.md §8.1).
 */
private class ParamoParticle(
    var x0: Float = 0f,
    var y0: Float = 0f,
    var currentX: Float = 0f,
    var currentY: Float = 0f,
    var vx: Float = 0f,
    var vy: Float = 0f,
    var amplitude: Float = 15f,
    var omega: Float = 3f,
    var phi: Float = 0f,
    var gTerminal: Float = 40f,
    var size: Float = 16f,
    var rotation: Float = 0f,
    var rotationSpeed: Float = 45f,
    var color: Color = Color(0xFF435C3C),
    var alpha: Float = 0.9f,
    var isLeaf: Boolean = true,
    var isAlive: Boolean = false,
    var startTimeSec: Float = 0f
) {
    fun reset(
        width: Float,
        height: Float,
        currentTimeSec: Float,
        isBurst: Boolean,
        burstSource: Offset?
    ) {
        startTimeSec = currentTimeSec
        isAlive = true
        isLeaf = Random.nextFloat() > 0.35f

        val leafColors = listOf(
            Color(0xFF435C3C), // Musgo Andino
            Color(0xFF2E4029), // Bosque Profundo
            Color(0xFFFAB438), // Oro Páramo
            Color(0xFFE89A20), // Ámbar Frailejón
            Color(0xFF7A8D6E)  // Hoja aterciopelada
        )
        color = leafColors.random()

        if (isBurst && burstSource != null) {
            // Explosión radial hacia arriba desde el origen (celebración)
            x0 = burstSource.x + Random.nextFloat() * 40f - 20f
            y0 = burstSource.y + Random.nextFloat() * 40f - 20f
            currentX = x0
            currentY = y0
            val angle = (Random.nextFloat() * 140f + 200f) * (Math.PI.toFloat() / 180f) // hacia arriba
            val speed = Random.nextFloat() * 260f + 140f
            vx = (Math.cos(angle.toDouble()) * speed).toFloat()
            vy = (Math.sin(angle.toDouble()) * speed).toFloat()
            gTerminal = Random.nextFloat() * 80f + 70f
        } else {
            // Viento natural entrando por arriba a la izquierda
            x0 = Random.nextFloat() * width * 1.2f - width * 0.2f
            y0 = -Random.nextFloat() * 100f
            currentX = x0
            currentY = y0
            vx = Random.nextFloat() * 50f + 20f
            vy = Random.nextFloat() * 70f + 40f
            gTerminal = Random.nextFloat() * 30f + 20f
        }

        amplitude = Random.nextFloat() * 25f + 10f
        omega = Random.nextFloat() * 3f + 1.5f
        phi = Random.nextFloat() * 6.28f
        size = if (isLeaf) Random.nextFloat() * 12f + 10f else Random.nextFloat() * 5f + 3f
        rotation = Random.nextFloat() * 360f
        rotationSpeed = Random.nextFloat() * 90f - 45f
        alpha = Random.nextFloat() * 0.3f + 0.7f
    }

    fun update(currentTimeSec: Float, width: Float, height: Float): Boolean {
        if (!isAlive) return false
        val t = currentTimeSec - startTimeSec
        if (t < 0) return true

        // Ecuación de viento andino (MUGGY_MOTOR_TECNICO.md §8.1):
        // x(t) = x0 + vx*t + A*sin(omega*t + phi)
        // y(t) = y0 + vy*t + 0.5*g_terminal*t^2
        currentX = x0 + vx * t + amplitude * sin(omega * t + phi)
        currentY = y0 + vy * t + 0.5f * gTerminal * t * t
        rotation += rotationSpeed * 0.016f

        if (currentY > height + 50f || currentX > width + 100f || currentX < -100f) {
            isAlive = false
        }
        return isAlive
    }
}

/**
 * Canvas interactivo de confeti orgánico de hojas de frailejón y rocío del páramo.
 * Renderizado de 120 FPS con cero recolección de basura (Object Pooling).
 */
@Composable
fun LeafWindCanvas(
    modifier: Modifier = Modifier,
    isActive: Boolean = true,
    isConfettiBurst: Boolean = false,
    burstOrigin: Offset? = null,
    onBurstComplete: (() -> Unit)? = null
) {
    // Pool acotado de partículas (30 hojas + 25 polen = 55 partículas pre-asignadas)
    val particlePool = remember {
        Array(55) { ParamoParticle() }
    }

    var lastFrameTimeNanos by remember { mutableLongStateOf(0L) }
    var runningTimeSec by remember { mutableStateOf(0f) }
    var initialized by remember { mutableStateOf(false) }

    LaunchedEffect(isActive, isConfettiBurst) {
        if (!isActive) return@LaunchedEffect

        // Inicializar partículas si es ráfaga festiva
        if (isConfettiBurst) {
            particlePool.forEach { particle ->
                particle.reset(
                    width = 1080f,
                    height = 2400f,
                    currentTimeSec = runningTimeSec,
                    isBurst = true,
                    burstSource = burstOrigin ?: Offset(540f, 1200f)
                )
            }
        }

        while (isActive) {
            withFrameNanos { frameTimeNanos ->
                if (lastFrameTimeNanos == 0L) {
                    lastFrameTimeNanos = frameTimeNanos
                }
                val dt = (frameTimeNanos - lastFrameTimeNanos) / 1_000_000_000f
                lastFrameTimeNanos = frameTimeNanos
                runningTimeSec += dt.coerceIn(0f, 0.05f)

                if (isConfettiBurst) {
                    val anyAlive = particlePool.any { it.isAlive }
                    if (!anyAlive && initialized) {
                        onBurstComplete?.invoke()
                    }
                }
                initialized = true
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        particlePool.forEach { p ->
            if (!p.isAlive && isActive && !isConfettiBurst) {
                // En modo brisa continua, revivir con baja probabilidad para flujo orgánico
                if (Random.nextFloat() < 0.04f) {
                    p.reset(w, h, runningTimeSec, isBurst = false, burstSource = null)
                }
            }

            if (p.isAlive) {
                p.update(runningTimeSec, w, h)
                if (p.isLeaf) {
                    drawAndeanLeaf(
                        center = Offset(p.currentX, p.currentY),
                        size = p.size,
                        rotation = p.rotation,
                        color = p.color.copy(alpha = p.alpha)
                    )
                } else {
                    // Partícula de polen / gota dorada
                    drawCircle(
                        color = p.color.copy(alpha = p.alpha),
                        radius = p.size,
                        center = Offset(p.currentX, p.currentY)
                    )
                }
            }
        }
    }
}

/**
 * Trazado vectorial de hoja de frailejón aterciopelada con nervadura central sutil.
 */
private fun DrawScope.drawAndeanLeaf(
    center: Offset,
    size: Float,
    rotation: Float,
    color: Color
) {
    rotate(degrees = rotation, pivot = center) {
        val halfW = size * 0.45f
        val halfH = size

        val leafPath = Path().apply {
            moveTo(center.x, center.y - halfH)
            cubicTo(
                center.x + halfW, center.y - halfH * 0.4f,
                center.x + halfW, center.y + halfH * 0.4f,
                center.x, center.y + halfH
            )
            cubicTo(
                center.x - halfW, center.y + halfH * 0.4f,
                center.x - halfW, center.y - halfH * 0.4f,
                center.x, center.y - halfH
            )
            close()
        }
        drawPath(leafPath, color = color)

        // Nervadura central sutil
        drawLine(
            color = Color.White.copy(alpha = 0.25f),
            start = Offset(center.x, center.y - halfH * 0.7f),
            end = Offset(center.x, center.y + halfH * 0.7f),
            strokeWidth = 1.2f
        )
    }
}

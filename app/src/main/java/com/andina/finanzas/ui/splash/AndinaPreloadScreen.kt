package com.andina.finanzas.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andina.finanzas.ui.theme.AndeanMistGradient
import com.andina.finanzas.ui.theme.AndeanSlateInk
import com.andina.finanzas.ui.theme.FrailejonGold
import com.andina.finanzas.ui.theme.financialAmbientGlow
import com.andina.finanzas.ui.theme.specularGlassBorder
import kotlinx.coroutines.delay

/**
 * Pantalla de Precarga Cinética con Apertura Sensorial en Telón de Bruma (Pilar 8).
 *
 * Duración calibrada: 750 ms mientras se inicializan llaves de SQLCipher y Room.
 * Elimina parpadeos y transiciones bruscas.
 */
@Composable
fun AndinaPreloadScreen(
    onPreloadCompleted: () -> Unit
) {
    val scaleAnim = remember { Animatable(0.85f) }
    val alphaAnim = remember { Animatable(0f) }
    val curtainAlpha = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        // 1. Materialización del monograma en relieve de Oro Frailejón
        alphaAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(400, easing = LinearOutSlowInEasing)
        )
        scaleAnim.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(450, easing = FastOutSlowInEasing)
        )

        // 2. Tiempo mínimo de persistencia y confort perceptivo
        delay(250)

        // 3. Apertura en telón de bruma matutina revelando el dashboard
        curtainAlpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(300, easing = FastOutSlowInEasing)
        )

        onPreloadCompleted()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = curtainAlpha.value }
            .background(AndeanMistGradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(scaleAnim.value)
                .graphicsLayer { alpha = alphaAnim.value }
        ) {
            // Emblema circular de cuarzo con resplandor en Oro Frailejón
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .financialAmbientGlow(
                        glowColor = FrailejonGold,
                        glowRadiusMultiplier = 1.2f,
                        alpha = 0.35f
                    )
                    .clip(CircleShape)
                    .specularGlassBorder(
                        strokeWidth = 1.5.dp,
                        shape = CircleShape,
                        topLightColor = Color.White,
                        bottomShadowColor = Color.White.copy(alpha = 0.2f)
                    )
                    .background(Color.White.copy(alpha = 0.82f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Terrain,
                    contentDescription = "Andina Ecosistema",
                    tint = FrailejonGold,
                    modifier = Modifier.size(46.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "ANDINA",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 4.sp,
                    color = AndeanSlateInk
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "FINANZAS DEL PÁRAMO",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp,
                    color = AndeanSlateInk.copy(alpha = 0.65f)
                )
            )
        }
    }
}

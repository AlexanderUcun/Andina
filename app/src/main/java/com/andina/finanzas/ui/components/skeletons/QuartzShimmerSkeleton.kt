package com.andina.finanzas.ui.components.skeletons

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.andina.finanzas.ui.theme.CornerLarge
import com.andina.finanzas.ui.theme.CornerMedium
import com.andina.finanzas.ui.theme.specularGlassBorder

/**
 * Shimmer de Cristal de Cuarzo Translúcido (Pilar 8).
 *
 * Ejecuta un barrido diagonal cenital a 45° sobre siluetas de cuarzo,
 * preservando exactamente las dimensiones finales de los componentes (Zero Layout Shift).
 */
@Composable
fun Modifier.quartzShimmer(
    baseColor: Color = Color.White.copy(alpha = 0.55f),
    highlightColor: Color = Color.White.copy(alpha = 0.90f),
    durationMillis: Int = 1200
): Modifier {
    val transition = rememberInfiniteTransition(label = "QuartzShimmerTransition")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "QuartzShimmerTranslate"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(baseColor, highlightColor, baseColor),
        start = Offset(translateAnim - 400f, translateAnim - 400f),
        end = Offset(translateAnim, translateAnim)
    )

    return this.background(shimmerBrush)
}

/**
 * Skeleton para HeroBalanceCard con cristal de cuarzo y bisel cenital.
 */
@Composable
fun HeroBalanceCardSkeleton(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(170.dp)
            .clip(CornerLarge)
            .specularGlassBorder(1.dp, CornerLarge),
        color = Color.White.copy(alpha = 0.70f),
        shape = CornerLarge
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(160.dp)
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .quartzShimmer()
                )
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .quartzShimmer()
                )
            }

            Box(
                modifier = Modifier
                    .width(240.dp)
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .quartzShimmer()
            )

            Box(
                modifier = Modifier
                    .width(110.dp)
                    .height(22.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .quartzShimmer()
            )
        }
    }
}

/**
 * Skeleton para carrusel de billeteras (QuickWalletCard).
 */
@Composable
fun WalletRowSkeleton(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(3) {
            Surface(
                modifier = Modifier
                    .width(175.dp)
                    .height(110.dp)
                    .clip(CornerMedium)
                    .specularGlassBorder(1.dp, CornerMedium),
                color = Color.White.copy(alpha = 0.65f),
                shape = CornerMedium
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .quartzShimmer()
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(14.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .quartzShimmer()
                        )
                    }
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(20.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .quartzShimmer()
                    )
                }
            }
        }
    }
}

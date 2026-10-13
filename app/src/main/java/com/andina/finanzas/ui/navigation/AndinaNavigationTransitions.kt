package com.andina.finanzas.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith

/**
 * Transiciones de Bruma Andina y Eje Z Óptico (Pilar 7).
 *
 * Evita el deslizamiento plano 2D lateral:
 * - La pantalla saliente se disuelve suavemente hacia atrás (scale 0.98f, alpha 0f).
 * - La pantalla entrante emerge desde la bruma hacia adelante (scale 1.02f -> 1.0f, alpha 1f).
 */
object AndinaNavigationTransitions {

    private const val TRANSITION_DURATION = 260

    fun createMistTransition(): ContentTransform {
        val enterTransition = fadeIn(
            animationSpec = tween(TRANSITION_DURATION, easing = FastOutSlowInEasing)
        ) + scaleIn(
            initialScale = 1.02f,
            animationSpec = tween(TRANSITION_DURATION, easing = FastOutSlowInEasing)
        )

        val exitTransition = fadeOut(
            animationSpec = tween(TRANSITION_DURATION, easing = FastOutSlowInEasing)
        ) + scaleOut(
            targetScale = 0.98f,
            animationSpec = tween(TRANSITION_DURATION, easing = FastOutSlowInEasing)
        )

        return enterTransition.togetherWith(exitTransition)
    }
}

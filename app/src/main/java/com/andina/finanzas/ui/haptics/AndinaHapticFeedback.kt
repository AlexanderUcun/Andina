package com.andina.finanzas.ui.haptics

import android.content.Context
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView

/**
 * Gestor Centralizado de Vibración Háptica de Alta Definición (Pilar 3).
 *
 * Utiliza [VibrationEffect] nativo de Android (API 29+) para clicks mecánicos,
 * resistencias de sobregiro y deslizamiento de muescas con latencia cero.
 */
class AndinaHapticFeedback(
    private val context: Context,
    private val view: View?
) {
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    /**
     * Micro-click mecánico de odómetro o slider (8-12 ms).
     * Sensación de rotación de cilindro o dial de caja fuerte suiza.
     */
    fun performMechanicalTick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
        } else {
            view?.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
    }

    /**
     * Vibración sorda y amortiguada ante intentos de sobregiro o límite superado.
     * Simula el tope o freno mecánico con resistencia física.
     */
    fun performOverdraftResistance() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
        } else {
            view?.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        }
    }

    /**
     * Doble pulso armónico de confirmación al guardar o persistir datos contables.
     */
    fun performSuccessDoublePulse() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK))
        } else {
            view?.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
        }
    }

    /**
     * Chasquido táctil calibrado al soltar un botón elástico o alternar privacidad.
     */
    fun performClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        } else {
            view?.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }
    }
}

/**
 * Proveedor Composable de [AndinaHapticFeedback].
 */
@Composable
fun rememberAndinaHaptics(): AndinaHapticFeedback {
    val context = LocalContext.current
    val view = LocalView.current
    return remember(context, view) {
        AndinaHapticFeedback(context, view)
    }
}

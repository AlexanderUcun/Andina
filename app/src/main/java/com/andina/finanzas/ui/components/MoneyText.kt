package com.andina.finanzas.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.andina.finanzas.ui.theme.LocalAndinaColors
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

enum class MoneyDisplayLevel {
    BODY,    // Cifras estándar en textos y filas (usa textMoney bronce con contraste >= 4.5:1)
    DISPLAY  // Cifras grandes >= 24sp bold (usa textMoneyDisplay oro con contraste >= 3.0:1)
}

/**
 * Componente maestro de renderizado tipográfico monetario con soporte para
 * efecto odómetro suave, inteligente y selectivo (solo giran los números que cambian).
 */
@Composable
fun MoneyText(
    amount: com.andina.finanzas.domain.model.Money,
    modifier: Modifier = Modifier,
    level: MoneyDisplayLevel = MoneyDisplayLevel.BODY,
    fontSize: TextUnit = if (level == MoneyDisplayLevel.DISPLAY) 38.sp else 16.sp,
    fontWeight: FontWeight = if (level == MoneyDisplayLevel.DISPLAY) FontWeight.ExtraBold else FontWeight.Bold,
    prefix: String = if (amount.pesos < 0) "-$ " else "$ ",
    customColor: Color? = null,
    showSymbol: Boolean = true,
    showCode: Boolean = false,
    animateOdometer: Boolean = (level == MoneyDisplayLevel.DISPLAY),
    refreshTrigger: Any? = null
) {
    val displayPrefix = if (showSymbol) prefix else ""
    val colors = LocalAndinaColors.current

    val textColor = customColor ?: when {
        amount.pesos < 0 -> colors.statusDanger
        level == MoneyDisplayLevel.DISPLAY -> colors.textMoneyDisplay
        else -> colors.textMoney
    }

    val formattedNumber = amount.formatCop(includeSymbol = false, includeCode = false)
    val suffix = if (showCode) " COP" else ""
    val fullSpokenText = amount.formatCop(includeSymbol = true, includeCode = showCode)

    val textStyle = TextStyle(
        fontSize = fontSize,
        fontWeight = fontWeight,
        color = textColor,
        fontFeatureSettings = "tnum"
    )

    if (animateOdometer) {
        var previousNumber by rememberSaveable { mutableStateOf("") }
        val currentNumber = formattedNumber

        val oldNumberToCompare = if (previousNumber.isEmpty() || previousNumber.length != currentNumber.length) {
            // En el arranque inicial, iniciamos los dígitos desde 0
            currentNumber.map { if (it.isDigit()) '0' else it }.joinToString("")
        } else {
            previousNumber
        }

        LaunchedEffect(currentNumber) {
            previousNumber = currentNumber
        }

        Row(
            modifier = modifier.semantics {
                this.contentDescription = fullSpokenText
                this.liveRegion = LiveRegionMode.Polite
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (displayPrefix.isNotEmpty()) {
                Text(
                    text = displayPrefix,
                    style = textStyle
                )
            }

            var digitIndex = 0
            currentNumber.forEachIndexed { index, char ->
                if (char.isDigit()) {
                    val targetDigit = char.digitToInt()
                    val oldChar = oldNumberToCompare.getOrNull(index) ?: '0'
                    val startDigit = if (oldChar.isDigit()) oldChar.digitToInt() else 0

                    OdometerDigit(
                        startDigit = startDigit,
                        targetDigit = targetDigit,
                        digitIndex = digitIndex,
                        textStyle = textStyle
                    )
                    digitIndex++
                } else {
                    Text(
                        text = char.toString(),
                        style = textStyle
                    )
                }
            }

            if (suffix.isNotEmpty()) {
                Text(
                    text = suffix,
                    style = textStyle.copy(
                        fontSize = (fontSize.value * 0.45f).sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textSecondary
                    )
                )
            }
        }
    } else {
        Text(
            text = "$displayPrefix$formattedNumber$suffix",
            style = textStyle,
            modifier = modifier.semantics {
                this.contentDescription = fullSpokenText
            }
        )
    }
}

/**
 * Rueda individual de odómetro de precisión:
 * - Si startDigit == targetDigit, no gira (estable, sin parpadeo al cambiar de pestaña).
 * - Si startDigit != targetDigit, gira suavemente solo los pasos necesarios con desaceleración orgánica.
 */
@Composable
private fun OdometerDigit(
    startDigit: Int,
    targetDigit: Int,
    digitIndex: Int,
    textStyle: TextStyle,
    modifier: Modifier = Modifier
) {
    if (startDigit == targetDigit) {
        Text(
            text = targetDigit.toString(),
            style = textStyle,
            modifier = modifier
        )
        return
    }

    val steps = (targetDigit - startDigit + 10) % 10
    val totalSteps = if (steps == 0) 10 else steps
    val animatedProgress = remember(startDigit, targetDigit) { Animatable(0f) }

    LaunchedEffect(startDigit, targetDigit) {
        delay(digitIndex * 35L)
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 450,
                easing = FastOutSlowInEasing
            )
        )
    }

    val currentStep = animatedProgress.value * totalSteps

    Layout(
        content = {
            // Child 0: Elemento ghost para dimensionar exactamente el ancho y alto de 1 carácter
            Text(
                text = "0",
                style = textStyle
            )
            // Child 1: Tambor vertical con únicamente los pasos necesarios
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (step in 0..totalSteps) {
                    val stepChar = ((startDigit + step) % 10).toString()
                    Text(
                        text = stepChar,
                        style = textStyle
                    )
                }
            }
        },
        modifier = modifier
            .clipToBounds()
            .clearAndSetSemantics { }
    ) { measurables, constraints ->
        val ghostPlaceable = measurables[0].measure(constraints)
        val width = ghostPlaceable.width
        val height = ghostPlaceable.height

        val columnPlaceable = measurables[1].measure(
            constraints.copy(
                minWidth = width,
                maxWidth = width,
                minHeight = 0,
                maxHeight = Constraints.Infinity
            )
        )

        layout(width, height) {
            val offset = (-currentStep * height).roundToInt()
            columnPlaceable.placeRelative(0, offset)
        }
    }
}

/**
 * Componente unificado para renderizado tipográfico monetario con WCAG 2.1 garantizado.
 */
@Composable
fun MoneyText(
    amountPesos: Long,
    modifier: Modifier = Modifier,
    level: MoneyDisplayLevel = MoneyDisplayLevel.BODY,
    fontSize: TextUnit = if (level == MoneyDisplayLevel.DISPLAY) 28.sp else 16.sp,
    fontWeight: FontWeight = if (level == MoneyDisplayLevel.DISPLAY) FontWeight.ExtraBold else FontWeight.Bold,
    prefix: String = "$ ",
    customColor: Color? = null
) {
    val colors = LocalAndinaColors.current

    val textColor = customColor ?: when {
        amountPesos < 0 -> colors.statusDanger
        level == MoneyDisplayLevel.DISPLAY -> colors.textMoneyDisplay
        else -> colors.textMoney
    }

    val formatter = NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
        maximumFractionDigits = 0
    }
    val formattedNumber = formatter.format(amountPesos).replace("$", "").trim()

    Text(
        text = "$prefix$formattedNumber",
        style = TextStyle(
            fontSize = fontSize,
            fontWeight = fontWeight,
            color = textColor,
            fontFeatureSettings = "tnum"
        ),
        modifier = modifier
    )
}

package com.andina.finanzas.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.andina.finanzas.ui.theme.LocalAndinaColors
import java.text.NumberFormat
import java.util.Locale

enum class MoneyDisplayLevel {
    BODY,    // Cifras estándar en textos y filas (usa textMoney bronce con contraste >= 4.5:1)
    DISPLAY  // Cifras grandes >= 24sp bold (usa textMoneyDisplay oro con contraste >= 3.0:1)
}

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
    animateOdometer: Boolean = (level == MoneyDisplayLevel.DISPLAY)
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
            // Animar dígitos individualmente con transición vertical estilo odómetro mecánico
            formattedNumber.forEachIndexed { index, char ->
                if (char.isDigit()) {
                    AnimatedContent(
                        targetState = char,
                        transitionSpec = {
                            if (targetState > initialState) {
                                (slideInVertically { height -> height } + fadeIn())
                                    .togetherWith(slideOutVertically { height -> -height } + fadeOut())
                            } else {
                                (slideInVertically { height -> -height } + fadeIn())
                                    .togetherWith(slideOutVertically { height -> height } + fadeOut())
                            }
                        },
                        label = "OdometerDigit_$index"
                    ) { targetChar ->
                        Text(
                            text = targetChar.toString(),
                            style = textStyle
                        )
                    }
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
                    style = textStyle
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

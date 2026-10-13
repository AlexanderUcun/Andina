package com.andina.finanzas.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

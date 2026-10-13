package com.andina.finanzas.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.andina.finanzas.domain.model.WalletType

/**
 * Glifos vectoriales geométricos oficiales para billeteras y entidades colombianas.
 * Renderizados en Canvas de alto rendimiento sin requerir recursos PNG pesados.
 */
@Composable
fun WalletBrandGlyph(
    walletType: WalletType,
    tint: Color,
    modifier: Modifier = Modifier,
    size: Dp = 20.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        when (walletType) {
            WalletType.NEQUI -> {
                // Glifo Nequi: Rombo/N estilizada contemporánea
                val path = Path().apply {
                    moveTo(w * 0.22f, h * 0.80f)
                    lineTo(w * 0.22f, h * 0.20f)
                    lineTo(w * 0.78f, h * 0.80f)
                    lineTo(w * 0.78f, h * 0.20f)
                }
                drawPath(
                    path = path,
                    color = tint,
                    style = Stroke(width = w * 0.16f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }
            WalletType.DAVIPLATA -> {
                // Glifo Daviplata: Casita geométrica distintiva
                val path = Path().apply {
                    moveTo(w * 0.50f, h * 0.18f)
                    lineTo(w * 0.82f, h * 0.45f)
                    lineTo(w * 0.82f, h * 0.82f)
                    lineTo(w * 0.18f, h * 0.82f)
                    lineTo(w * 0.18f, h * 0.45f)
                    close()
                }
                drawPath(
                    path = path,
                    color = tint,
                    style = Stroke(width = w * 0.14f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                // Detalle central de chimenea / tejado
                drawLine(
                    color = tint,
                    start = Offset(w * 0.50f, h * 0.52f),
                    end = Offset(w * 0.50f, h * 0.82f),
                    strokeWidth = w * 0.12f,
                    cap = StrokeCap.Round
                )
            }
            WalletType.BANCOLOMBIA -> {
                // Glifo Bancolombia: Los tres trazos dinámicos
                val strokeW = w * 0.14f
                drawLine(
                    color = tint,
                    start = Offset(w * 0.22f, h * 0.30f),
                    end = Offset(w * 0.78f, h * 0.30f),
                    strokeWidth = strokeW,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = tint,
                    start = Offset(w * 0.32f, h * 0.50f),
                    end = Offset(w * 0.88f, h * 0.50f),
                    strokeWidth = strokeW,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = tint,
                    start = Offset(w * 0.15f, h * 0.70f),
                    end = Offset(w * 0.68f, h * 0.70f),
                    strokeWidth = strokeW,
                    cap = StrokeCap.Round
                )
            }
            WalletType.EFECTIVO -> {
                // Glifo Efectivo: Billete/filigrana con sello central
                val path = Path().apply {
                    moveTo(w * 0.15f, h * 0.25f)
                    lineTo(w * 0.85f, h * 0.25f)
                    lineTo(w * 0.85f, h * 0.75f)
                    lineTo(w * 0.15f, h * 0.75f)
                    close()
                }
                drawPath(
                    path = path,
                    color = tint,
                    style = Stroke(width = w * 0.12f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                // Círculo central de moneda
                drawCircle(
                    color = tint,
                    radius = w * 0.14f,
                    center = Offset(w * 0.50f, h * 0.50f)
                )
            }
            WalletType.TARJETA_CREDITO, WalletType.OTRA -> {
                // Símbolo de gema financiera universal
                val path = Path().apply {
                    moveTo(w * 0.50f, h * 0.15f)
                    lineTo(w * 0.85f, h * 0.50f)
                    lineTo(w * 0.50f, h * 0.85f)
                    lineTo(w * 0.15f, h * 0.50f)
                    close()
                }
                drawPath(
                    path = path,
                    color = tint,
                    style = Stroke(width = w * 0.14f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }
        }
    }
}

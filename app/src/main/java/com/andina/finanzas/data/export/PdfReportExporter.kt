package com.andina.finanzas.data.export

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.andina.finanzas.domain.model.FinancialSummary
import com.andina.finanzas.domain.model.Transaction
import com.andina.finanzas.domain.model.TransactionType
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Generador nativo de reportes contables en PDF mediante [PdfDocument].
 * Crea un documento A4 de alta fidelidad estética con la paleta de Andina Finanzas,
 * desglose patrimonial y tabla de movimientos sin dependencias externas pesadas.
 */
@Singleton
class PdfReportExporter @Inject constructor() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("es", "CO"))
    private val generationDateFormat = SimpleDateFormat("dd 'de' MMMM 'de' yyyy, HH:mm", Locale("es", "CO"))

    /**
     * Genera un reporte PDF y lo escribe en el [outputStream] provisto.
     */
    fun exportReport(
        summary: FinancialSummary,
        transactions: List<Transaction>,
        outputStream: OutputStream
    ) {
        val pdfDocument = PdfDocument()

        val pageWidth = 595 // A4 standard width (pt)
        val pageHeight = 842 // A4 standard height (pt)
        var pageNumber = 1

        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        val paint = Paint().apply { isAntiAlias = true }

        // --- Encabezado Andino ---
        // Franja verde musgo superior
        paint.color = Color.rgb(0x2C, 0x44, 0x27)
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), 70f, paint)

        // Título de la app
        paint.color = Color.WHITE
        paint.textSize = 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("ANDINA FINANZAS", 36f, 38f, paint)

        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Extracto Financiero y Contable Oficial", 36f, 54f, paint)

        // Fecha de emisión
        val nowStr = "Generado: " + generationDateFormat.format(Date())
        paint.textSize = 8f
        val dateWidth = paint.measureText(nowStr)
        canvas.drawText(nowStr, pageWidth - 36f - dateWidth, 54f, paint)

        var yPos = 95f

        // --- Resumen Ejecutivo de Cuentas ---
        paint.color = Color.rgb(0xF5, 0xF3, 0xEF) // Fondo arena suave
        canvas.drawRoundRect(36f, yPos, pageWidth - 36f, yPos + 68f, 8f, 8f, paint)

        paint.color = Color.rgb(0x74, 0x79, 0x6F)
        paint.textSize = 8f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        canvas.drawText("INGRESOS DEL PERIODO", 52f, yPos + 22f, paint)
        canvas.drawText("GASTOS DEL PERIODO", 210f, yPos + 22f, paint)
        canvas.drawText("BALANCE DISPONIBLE", 370f, yPos + 22f, paint)

        paint.textSize = 13f
        paint.color = Color.rgb(0x2E, 0x7D, 0x32) // Verde positivo
        canvas.drawText(summary.monthlyIncome.formatCop(), 52f, yPos + 44f, paint)

        paint.color = Color.rgb(0xC6, 0x28, 0x28) // Rojo gasto
        canvas.drawText(summary.monthlyExpense.formatCop(), 210f, yPos + 44f, paint)

        paint.color = Color.rgb(0x1D, 0x1B, 0x19) // Carbón
        canvas.drawText(summary.monthlyNetBalance.formatCop(), 370f, yPos + 44f, paint)

        yPos += 95f

        // --- Título de Tabla ---
        paint.color = Color.rgb(0x1D, 0x1B, 0x19)
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Detalle de Movimientos Registrados (${transactions.size})", 36f, yPos, paint)

        yPos += 14f

        // Cabecera de la tabla
        paint.color = Color.rgb(0xEA, 0xEE, 0xE8)
        canvas.drawRect(36f, yPos, pageWidth - 36f, yPos + 20f, paint)

        paint.color = Color.rgb(0x43, 0x48, 0x40)
        paint.textSize = 8f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        canvas.drawText("FECHA", 44f, yPos + 13f, paint)
        canvas.drawText("DESCRIPCIÓN / NOTA", 110f, yPos + 13f, paint)
        canvas.drawText("CATEGORÍA", 280f, yPos + 13f, paint)
        canvas.drawText("MÉTODO", 380f, yPos + 13f, paint)
        canvas.drawText("VALOR (COP)", 480f, yPos + 13f, paint)

        yPos += 20f

        // Filas de movimientos
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        val rowHeight = 20f

        for ((index, tx) in transactions.withIndex()) {
            // Verificamos si necesitamos una nueva página
            if (yPos + rowHeight > pageHeight - 40f) {
                // Pie de página con número de página
                drawFooter(canvas, pageNumber, paint, pageWidth, pageHeight)
                pdfDocument.finishPage(page)

                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas

                yPos = 40f
            }

            // Alternancia sutil de fondo
            if (index % 2 == 1) {
                paint.color = Color.rgb(0xFA, 0xFA, 0xF9)
                canvas.drawRect(36f, yPos, pageWidth - 36f, yPos + rowHeight, paint)
            }

            paint.textSize = 8f
            paint.color = Color.rgb(0x1D, 0x1B, 0x19)

            // Fecha
            val dateStr = dateFormat.format(Date(tx.occurredAt))
            canvas.drawText(dateStr, 44f, yPos + 13f, paint)

            // Descripción / Nota
            val desc = tx.note?.take(30) ?: (if (tx.isAntExpense) "🐜 Gasto hormiga" else tx.type.name)
            canvas.drawText(desc, 110f, yPos + 13f, paint)

            // Categoría
            val category = tx.categoryName?.take(18) ?: "-"
            canvas.drawText(category, 280f, yPos + 13f, paint)

            // Método
            val method = tx.walletName?.take(16) ?: "-"
            canvas.drawText(method, 380f, yPos + 13f, paint)

            // Valor con color
            paint.color = when (tx.type) {
                TransactionType.INGRESO -> Color.rgb(0x2E, 0x7D, 0x32)
                TransactionType.GASTO -> Color.rgb(0xC6, 0x28, 0x28)
                TransactionType.TRANSFERENCIA -> Color.rgb(0x84, 0x54, 0x00)
            }
            val amountStr = when (tx.type) {
                TransactionType.INGRESO -> "+${tx.amount.formatCop()}"
                TransactionType.GASTO -> "-${tx.amount.formatCop()}"
                TransactionType.TRANSFERENCIA -> tx.amount.formatCop()
            }
            canvas.drawText(amountStr, 480f, yPos + 13f, paint)

            yPos += rowHeight
        }

        // Pie de página de la última página
        drawFooter(canvas, pageNumber, paint, pageWidth, pageHeight)
        pdfDocument.finishPage(page)

        try {
            pdfDocument.writeTo(outputStream)
        } finally {
            pdfDocument.close()
        }
    }

    private fun drawFooter(
        canvas: Canvas,
        pageNumber: Int,
        paint: Paint,
        pageWidth: Int,
        pageHeight: Int
    ) {
        paint.color = Color.rgb(0x74, 0x79, 0x6F)
        paint.textSize = 7f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

        val footerText = "Andina Finanzas — Bóveda Local Segura — Página $pageNumber"
        canvas.drawText(footerText, 36f, pageHeight - 20f, paint)
    }
}

package com.andina.finanzas.data.export

import com.andina.finanzas.domain.model.Transaction
import com.andina.finanzas.domain.model.TransactionType
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Exportador contable a formato CSV estándar con compatibilidad total para Microsoft Excel en Windows/Mac.
 * Emplea codificación UTF-8 con BOM (\uFEFF) y delimitador de punto y coma (;) oficial en Latinoamérica.
 */
@Singleton
class CsvExporter @Inject constructor() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("es", "CO"))
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale("es", "CO"))

    /**
     * Convierte una lista de transacciones a una cadena CSV con BOM y cabeceras en español.
     */
    fun exportToCsvString(transactions: List<Transaction>): String {
        val builder = StringBuilder()
        // Prefijo UTF-8 BOM para que Excel reconozca tildes y caracteres especiales sin importar la configuración regional
        builder.append('\uFEFF')

        // Cabeceras oficiales
        builder.append(
            "ID;Fecha;Hora;Tipo;Billetera Origen;Billetera Destino;Categoría;Monto Pesos (COP);Gasto Hormiga;Nota / Descripción\n"
        )

        for (tx in transactions) {
            val date = Date(tx.occurredAt)
            val dateStr = dateFormat.format(date)
            val timeStr = timeFormat.format(date)

            val typeStr = when (tx.type) {
                TransactionType.GASTO -> "Gasto"
                TransactionType.INGRESO -> "Ingreso"
                TransactionType.TRANSFERENCIA -> "Transferencia"
            }

            val originWallet = escapeCsvField(tx.walletName.orEmpty())
            val targetWallet = escapeCsvField(tx.toWalletName.orEmpty())
            val category = escapeCsvField(tx.categoryName.orEmpty())
            val amountPesos = tx.amount.pesos.toString()
            val antExpenseStr = if (tx.isAntExpense) "SÍ" else "NO"
            val note = escapeCsvField(tx.note)

            builder.append(
                "${tx.id};$dateStr;$timeStr;$typeStr;$originWallet;$targetWallet;$category;$amountPesos;$antExpenseStr;$note\n"
            )
        }

        return builder.toString()
    }

    /**
     * Escribe la exportación CSV directamente a un [OutputStream] (útil para ContentResolver / DocumentFile).
     */
    fun exportToStream(transactions: List<Transaction>, outputStream: OutputStream) {
        OutputStreamWriter(outputStream, StandardCharsets.UTF_8).use { writer ->
            writer.write(exportToCsvString(transactions))
            writer.flush()
        }
    }

    private fun escapeCsvField(field: String): String {
        return if (field.contains(";") || field.contains("\"") || field.contains("\n")) {
            "\"" + field.replace("\"", "\"\"") + "\""
        } else {
            field
        }
    }
}

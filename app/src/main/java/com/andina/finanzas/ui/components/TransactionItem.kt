package com.andina.finanzas.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.domain.model.Transaction
import com.andina.finanzas.domain.model.TransactionType
import com.andina.finanzas.ui.theme.AndeanAntExpenseAmber
import com.andina.finanzas.ui.theme.AndeanError
import com.andina.finanzas.ui.theme.AndeanPrimary
import com.andina.finanzas.ui.theme.AndinaTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Fila de Movimiento Financiero Oficial (TransactionItem).
 *
 * Muestra el flujo de entrada o salida monetaria con:
 * - Ícono circular diferenciado (40dp) según tipo de movimiento (Gasto, Ingreso o Transferencia).
 * - Título del movimiento o nombre de categoría.
 * - Badge caramelo distintivo "Hormiga" para consumos no planificados.
 * - Subtítulo contextual con billetera y fecha legible.
 * - Cifra formateada con signo explícito y números tabulares anti-salto.
 *
 * @param transaction Modelo de dominio inmutable con los datos del movimiento.
 * @param onClick Acción opcional al pulsar la fila (para ver detalles o comprobante adjunto).
 * @param modifier Modificador Compose externo.
 */
@Composable
fun TransactionItem(
    transaction: Transaction,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isIncome = transaction.type == TransactionType.INGRESO
    val isTransfer = transaction.type == TransactionType.TRANSFERENCIA

    val colors = com.andina.finanzas.ui.theme.LocalAndinaColors.current

    // Color y vector del icono circular izquierdo con semántica contable estricta
    val (iconBackground, iconTint, iconVector) = when {
        isIncome -> {
            val iconVec = if (!transaction.categoryIcon.isNullOrBlank()) {
                CategoryIconHelper.getIcon(transaction.categoryIcon)
            } else {
                Icons.Default.ArrowDownward
            }
            Triple(
                Color(0x1A10B981), // Soft emerald badge
                com.andina.finanzas.ui.theme.SemanticIncomeEmerald,
                iconVec
            )
        }
        isTransfer -> Triple(
            Color(0x1A0EA5E9), // Soft cyan badge
            com.andina.finanzas.ui.theme.SemanticTransferCyan,
            Icons.AutoMirrored.Filled.CompareArrows
        )
        else -> {
            val baseColor = if (transaction.isAntExpense) colors.statusWarning else com.andina.finanzas.ui.theme.SemanticExpenseCoral
            val badgeBg = if (transaction.isAntExpense) colors.statusWarning.copy(alpha = 0.16f) else Color(0x1AEF4444)
            Triple(
                badgeBg,
                baseColor,
                CategoryIconHelper.getIcon(transaction.categoryIcon)
            )
        }
    }

    // Texto del signo monetario y color de la cifra según semántica contable
    val (amountPrefix, amountColor) = when {
        isIncome -> "+$ " to com.andina.finanzas.ui.theme.SemanticIncomeEmerald
        isTransfer -> "⇄ $ " to com.andina.finanzas.ui.theme.SemanticTransferCyan
        else -> "-$ " to com.andina.finanzas.ui.theme.SemanticExpenseCoral
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp)
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else Modifier
            ),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícono circular de 40dp
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = transaction.type.name,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Información central: Nombre + Badge Hormiga + Metadatos
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val hasCustomNote = transaction.note.isNotBlank()
                    val displayName = when {
                        hasCustomNote -> transaction.note
                        !transaction.categoryName.isNullOrBlank() -> transaction.categoryName
                        isTransfer -> "Transferencia"
                        isIncome -> "Ingreso"
                        else -> "Gasto"
                    }

                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    // Badge "Hormiga" caramelo si aplica
                    if (transaction.isAntExpense) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AndeanAntExpenseAmber.copy(alpha = 0.16f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Hormiga",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = AndeanAntExpenseAmber
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Subtítulo con categoría (solo si hay nota personalizada), fecha y billetera
                val hasCustomNote = transaction.note.isNotBlank()
                val dateString = formatDate(transaction.occurredAt)
                val categoryPart = if (hasCustomNote && !transaction.categoryName.isNullOrBlank()) transaction.categoryName else null
                val walletContext = buildString {
                    if (isTransfer) {
                        val from = transaction.walletName ?: "Origen"
                        val to = transaction.toWalletName ?: "Destino"
                        append("$from → $to · $dateString")
                    } else {
                        if (categoryPart != null) {
                            append("$categoryPart · ")
                        }
                        append(dateString)
                        transaction.walletName?.let { append(" · $it") }
                    }
                }

                Text(
                    text = walletContext,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Cifra monetaria alineada a la derecha mediante MoneyText (WCAG AA garantizado)
            MoneyText(
                amountPesos = transaction.amount.pesos,
                level = MoneyDisplayLevel.BODY,
                prefix = amountPrefix,
                customColor = amountColor
            )
        }
    }
}

/**
 * Formateador de fecha amigable para transacciones.
 */
private fun formatDate(timestamp: Long): String {
    val formatter = SimpleDateFormat("d MMM, h:mm a", Locale("es", "CO"))
    return formatter.format(Date(timestamp))
}

@Preview(name = "TransactionItem Previews", showBackground = true)
@Composable
private fun TransactionItemPreview() {
    AndinaTheme {
        Column {
            TransactionItem(
                transaction = Transaction(
                    id = 1,
                    type = TransactionType.GASTO,
                    amount = Money(6500L),
                    walletId = 1,
                    note = "Tinto campesino y buñuelo",
                    occurredAt = System.currentTimeMillis(),
                    isAntExpense = true,
                    walletName = "Efectivo",
                    categoryName = "Café",
                    categoryIcon = "coffee"
                )
            )
            TransactionItem(
                transaction = Transaction(
                    id = 2,
                    type = TransactionType.INGRESO,
                    amount = Money(1500000L),
                    walletId = 2,
                    note = "Quincena Nómina",
                    occurredAt = System.currentTimeMillis() - 86400000L,
                    walletName = "Bancolombia"
                )
            )
            TransactionItem(
                transaction = Transaction(
                    id = 3,
                    type = TransactionType.TRANSFERENCIA,
                    amount = Money(200000L),
                    walletId = 2,
                    toWalletId = 1,
                    note = "Paso a Nequi",
                    occurredAt = System.currentTimeMillis() - 172800000L,
                    walletName = "Bancolombia",
                    toWalletName = "Nequi"
                )
            )
        }
    }
}

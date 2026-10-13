package com.andina.finanzas.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.andina.finanzas.domain.model.Transaction
import com.andina.finanzas.ui.components.AndeanHyperCard
import com.andina.finanzas.ui.components.AndinaButton
import com.andina.finanzas.ui.components.HyperCardVariant
import com.andina.finanzas.ui.components.TransactionItem
import com.andina.finanzas.ui.theme.LocalAndinaColors

/**
 * Sección de Actividad Reciente de la pantalla de Inicio.
 *
 * Muestra hasta 5 movimientos recientes usando [TransactionItem] o un estado
 * vacío motivacional con botón de registro rápido.
 */
@Composable
fun RecentActivitySection(
    transactions: List<Transaction>,
    onTransactionClick: (Long) -> Unit,
    onSeeAllClick: () -> Unit,
    onQuickRegisterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAndinaColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Encabezado de Sección
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Actividad Reciente",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )

            if (transactions.isNotEmpty()) {
                TextButton(onClick = onSeeAllClick) {
                    Text(
                        text = "Ver todos",
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.textLink,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = colors.textLink,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (transactions.isEmpty()) {
            AndeanHyperCard(
                modifier = Modifier.fillMaxWidth(),
                variant = HyperCardVariant.CARD,
                contentPadding = 20.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                        contentDescription = null,
                        tint = colors.accentGold,
                        modifier = Modifier.size(36.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Sin movimientos registrados este mes",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Registra tu primer gasto o ingreso tocando el botón central",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    AndinaButton(
                        text = "Registrar Movimiento",
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        onClick = onQuickRegisterClick,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } else {
            AndeanHyperCard(
                modifier = Modifier.fillMaxWidth(),
                variant = HyperCardVariant.CARD,
                contentPadding = 8.dp
            ) {
                Column {
                    transactions.forEachIndexed { index, transaction ->
                        TransactionItem(
                            transaction = transaction,
                            onClick = { onTransactionClick(transaction.id) }
                        )
                        if (index < transactions.size - 1) {
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
    }
}

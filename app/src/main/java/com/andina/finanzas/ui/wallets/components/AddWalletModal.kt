package com.andina.finanzas.ui.wallets.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.andina.finanzas.domain.model.Wallet
import com.andina.finanzas.domain.model.WalletType
import com.andina.finanzas.ui.theme.AndeanPrimary
import java.text.NumberFormat
import java.util.Locale

/**
 * Modal para Crear o Editar una Billetera (Módulo 3 / Sub-fase 3.2).
 */
@Composable
fun AddWalletModal(
    editingWallet: Wallet?,
    isSaving: Boolean,
    onConfirm: (name: String, type: WalletType, initialBalancePesos: Long, colorHex: String) -> Unit,
    onDismiss: () -> Unit
) {
    var nameInput by remember { mutableStateOf(editingWallet?.name ?: "") }
    var selectedType by remember { mutableStateOf(editingWallet?.type ?: WalletType.NEQUI) }
    var initialBalanceInput by remember {
        mutableStateOf(editingWallet?.initialBalance?.pesos?.toString() ?: "")
    }
    var selectedColorHex by remember {
        mutableStateOf(editingWallet?.colorHex ?: "#700054")
    }

    val palette = listOf(
        "#700054", // Nequi Magenta
        "#B32D2D", // Daviplata Rojo
        "#C58934", // Bancolombia Ámbar
        "#435C3C", // Efectivo Musgo
        "#820AD1", // NuBank Morado
        "#1E88E5", // Banco Azul
        "#00897B", // Esmeralda
        "#5D4037"  // Café Cuero
    )

    val availableTypes: List<Pair<WalletType, String>> = listOf(
        WalletType.NEQUI to "Nequi",
        WalletType.DAVIPLATA to "Daviplata",
        WalletType.BANCOLOMBIA to "Bancolombia",
        WalletType.EFECTIVO to "Efectivo",
        WalletType.TARJETA_CREDITO to "Tarjeta",
        WalletType.OTRA to "Otra Cuenta"
    )

    val previewPesos = initialBalanceInput.toLongOrNull() ?: 0L

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (editingWallet == null) "Añadir Billetera" else "Editar Billetera",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // 1. Nombre
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Nombre de la cuenta") },
                    placeholder = { Text("Ej. Nequi Personal, Efectivo") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Selector de Tipo
                Text(
                    text = "Tipo de Cuenta:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(availableTypes) { typePair ->
                        val (type, label) = typePair
                        val isSelected = selectedType == type
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedType = type
                                // Auto-asignar color por defecto según la marca
                                when (type) {
                                    WalletType.NEQUI -> selectedColorHex = "#700054"
                                    WalletType.DAVIPLATA -> selectedColorHex = "#B32D2D"
                                    WalletType.BANCOLOMBIA -> selectedColorHex = "#C58934"
                                    WalletType.EFECTIVO -> selectedColorHex = "#435C3C"
                                    WalletType.TARJETA_CREDITO -> selectedColorHex = "#820AD1"
                                    WalletType.OTRA -> selectedColorHex = "#1E88E5"
                                }
                            },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AndeanPrimary.copy(alpha = 0.15f),
                                selectedLabelColor = AndeanPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 3. Saldo Inicial
                OutlinedTextField(
                    value = initialBalanceInput,
                    onValueChange = { initialBalanceInput = it.filter { ch -> ch.isDigit() }.take(8) },
                    label = { Text("Saldo inicial") },
                    prefix = { Text("$ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (previewPesos > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Valor: ${formatPesos(previewPesos)}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = AndeanPrimary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 4. Selector de Color
                Text(
                    text = "Color Identificador:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    palette.forEach { colorHex ->
                        val color = try {
                            Color(android.graphics.Color.parseColor(colorHex))
                        } catch (_: Exception) {
                            AndeanPrimary
                        }
                        val isSelected = selectedColorHex.equals(colorHex, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable { selectedColorHex = colorHex }
                                .then(
                                    if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                    else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Seleccionado",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (nameInput.isNotBlank()) {
                        onConfirm(
                            nameInput.trim(),
                            selectedType,
                            previewPesos,
                            selectedColorHex
                        )
                    }
                },
                enabled = nameInput.isNotBlank() && !isSaving
            ) {
                Text(
                    text = if (isSaving) "Guardando..." else "Guardar",
                    fontWeight = FontWeight.Bold,
                    color = AndeanPrimary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSaving) {
                Text("Cancelar")
            }
        }
    )
}

private fun formatPesos(amount: Long): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
    format.maximumFractionDigits = 0
    return format.format(amount)
}

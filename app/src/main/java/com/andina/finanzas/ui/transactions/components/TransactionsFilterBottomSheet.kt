package com.andina.finanzas.ui.transactions.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andina.finanzas.domain.model.Category
import com.andina.finanzas.domain.model.TransactionType
import com.andina.finanzas.domain.model.Wallet
import com.andina.finanzas.ui.components.AndinaButton
import com.andina.finanzas.ui.components.CategoryIconHelper
import com.andina.finanzas.ui.theme.AndeanAntExpenseAmber
import com.andina.finanzas.ui.theme.AndeanPrimary
import com.andina.finanzas.ui.transactions.DateRangeFilterPreset
import com.andina.finanzas.ui.transactions.TransactionsFilter

/**
 * Modal BottomSheet para filtros avanzados de auditoría financiera.
 * Permite filtrar por tipo de movimiento, billetera/método, categoría, compras hormiga y rango temporal.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsFilterBottomSheet(
    filter: TransactionsFilter,
    availableWallets: List<Wallet>,
    availableCategories: List<Category> = emptyList(),
    onTypeSelected: (TransactionType?) -> Unit,
    onWalletSelected: (Long?) -> Unit,
    onCategorySelected: (Long?) -> Unit = {},
    onToggleAntExpense: (Boolean) -> Unit,
    onDatePresetSelected: (DateRangeFilterPreset) -> Unit,
    onResetFilters: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val isAntEligible = filter.type == null || filter.type == TransactionType.GASTO

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
            )
        },
        modifier = modifier
    ) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState)
        ) {
            // Cabecera del BottomSheet
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AndeanPrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterAlt,
                            contentDescription = null,
                            tint = AndeanPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Filtros de Auditoría",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (filter.activeFilterCount > 0) {
                            Text(
                                text = "${filter.activeFilterCount} activo(s)",
                                style = MaterialTheme.typography.labelSmall,
                                color = AndeanPrimary
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!filter.isDefault) {
                        IconButton(onClick = onResetFilters) {
                            Icon(
                                imageVector = Icons.Default.RestartAlt,
                                contentDescription = "Restablecer filtros",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismissRequest) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 1. Tipo de Movimiento
            Text(
                text = "Tipo de Movimiento",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChipItem(
                    label = "Todos",
                    selected = filter.type == null,
                    onClick = { onTypeSelected(null) }
                )
                FilterChipItem(
                    label = "Gastos",
                    selected = filter.type == TransactionType.GASTO,
                    onClick = { onTypeSelected(TransactionType.GASTO) }
                )
                FilterChipItem(
                    label = "Ingresos",
                    selected = filter.type == TransactionType.INGRESO,
                    onClick = {
                        onTypeSelected(TransactionType.INGRESO)
                        if (filter.onlyAntExpense) onToggleAntExpense(false)
                    }
                )
                FilterChipItem(
                    label = "Transferencias",
                    selected = filter.type == TransactionType.TRANSFERENCIA,
                    onClick = {
                        onTypeSelected(TransactionType.TRANSFERENCIA)
                        if (filter.onlyAntExpense) onToggleAntExpense(false)
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Método de Pago / Billetera
            Text(
                text = "Método de Pago / Billetera",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChipItem(
                    label = "Todas las cuentas",
                    selected = filter.walletId == null,
                    onClick = { onWalletSelected(null) }
                )
                availableWallets.forEach { wallet ->
                    FilterChipItem(
                        label = wallet.name,
                        selected = filter.walletId == wallet.id,
                        onClick = { onWalletSelected(wallet.id) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2.1. Categoría
            if (availableCategories.isNotEmpty()) {
                Text(
                    text = "Categoría",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChipItem(
                        label = "Todas las categorías",
                        selected = filter.categoryId == null,
                        onClick = { onCategorySelected(null) }
                    )
                    availableCategories.forEach { category ->
                        FilterChipItem(
                            label = category.name,
                            selected = filter.categoryId == category.id,
                            onClick = { onCategorySelected(category.id) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // 3. Switch de Compras Hormiga (deshabilitado si no aplica)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (isAntEligible) 0.5f else 0.2f),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = isAntEligible) { onToggleAntExpense(!filter.onlyAntExpense) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isAntEligible) AndeanAntExpenseAmber.copy(alpha = 0.16f)
                                    else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.BugReport,
                                contentDescription = null,
                                tint = if (isAntEligible) AndeanAntExpenseAmber else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Solo compras hormiga",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = if (isAntEligible) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                            Text(
                                text = if (isAntEligible) "Consumos cotidianos e impulsivos" else "Solo aplica a gastos",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = if (isAntEligible) 1f else 0.4f)
                            )
                        }
                    }

                    Switch(
                        checked = filter.onlyAntExpense && isAntEligible,
                        onCheckedChange = { if (isAntEligible) onToggleAntExpense(it) },
                        enabled = isAntEligible,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = AndeanAntExpenseAmber,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Período / Rango de Fechas
            Text(
                text = "Rango de Fechas",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DateRangeFilterPreset.entries.forEach { preset ->
                    FilterChipItem(
                        label = preset.label,
                        selected = filter.datePreset == preset,
                        onClick = { onDatePresetSelected(preset) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Botón de aplicación
            val applyText = if (filter.activeFilterCount > 0) {
                "Aplicar Filtros (${filter.activeFilterCount})"
            } else {
                "Ver Todos los Movimientos"
            }

            AndinaButton(
                text = applyText,
                onClick = onDismissRequest,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FilterChipItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                )
            )
        },
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        } else null,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = AndeanPrimary.copy(alpha = 0.16f),
            selectedLabelColor = AndeanPrimary,
            selectedLeadingIconColor = AndeanPrimary,
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            selectedBorderColor = AndeanPrimary.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    )
}

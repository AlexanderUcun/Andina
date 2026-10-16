package com.andina.finanzas.ui.wallets.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andina.finanzas.domain.model.Debt
import com.andina.finanzas.domain.model.DebtType
import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.domain.model.Wallet
import com.andina.finanzas.ui.components.AndeanHyperCard
import com.andina.finanzas.ui.components.HyperCardVariant
import com.andina.finanzas.ui.theme.AndeanOnPrimary
import com.andina.finanzas.ui.theme.AndeanPrimary
import com.andina.finanzas.ui.theme.FrailejonGold
import com.andina.finanzas.ui.theme.LocalAndinaColors
import com.andina.finanzas.ui.theme.SemanticDebtPayableOrange
import com.andina.finanzas.ui.theme.SemanticDebtReceivableGold
import com.andina.finanzas.ui.wallets.DebtFilter
import com.andina.finanzas.ui.wallets.WalletsEvent
import com.andina.finanzas.ui.wallets.WalletsUiState
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Pestaña oficial de Deudas y Préstamos ("Me deben" & "Yo debo") - Fase 4.
 */
@Composable
fun DebtsTabContent(
    state: WalletsUiState,
    onEvent: (WalletsEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAndinaColors.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Tarjeta Resumen Dual de Deudas
        item {
            DebtsDualSummaryCard(
                receivableAmount = state.totalReceivablePesos,
                payableAmount = state.totalPayablePesos,
                netBalance = state.netDebtBalancePesos,
                onNewDebtClick = { onEvent(WalletsEvent.OnOpenCreateDebt) }
            )
        }

        // 2. Sub-chips de filtrado
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = state.debtFilter == DebtFilter.TODAS,
                    onClick = { onEvent(WalletsEvent.OnSelectDebtFilter(DebtFilter.TODAS)) },
                    label = { Text("Todas") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AndeanPrimary.copy(alpha = 0.15f),
                        selectedLabelColor = AndeanPrimary
                    )
                )
                FilterChip(
                    selected = state.debtFilter == DebtFilter.RECEIVABLE,
                    onClick = { onEvent(WalletsEvent.OnSelectDebtFilter(DebtFilter.RECEIVABLE)) },
                    label = { Text("Me deben") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SemanticDebtReceivableGold.copy(alpha = 0.18f),
                        selectedLabelColor = SemanticDebtReceivableGold
                    )
                )
                FilterChip(
                    selected = state.debtFilter == DebtFilter.PAYABLE,
                    onClick = { onEvent(WalletsEvent.OnSelectDebtFilter(DebtFilter.PAYABLE)) },
                    label = { Text("Yo debo") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SemanticDebtPayableOrange.copy(alpha = 0.18f),
                        selectedLabelColor = SemanticDebtPayableOrange
                    )
                )
                FilterChip(
                    selected = state.debtFilter == DebtFilter.SALDADAS,
                    onClick = { onEvent(WalletsEvent.OnSelectDebtFilter(DebtFilter.SALDADAS)) },
                    label = { Text("Saldadas") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = colors.accentGold.copy(alpha = 0.15f),
                        selectedLabelColor = colors.accentGold
                    )
                )
            }
        }

        // 3. Lista de Deudas y Préstamos
        if (state.filteredDebts.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = colors.cardBackground,
                    border = BorderStroke(1.dp, colors.cardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = AndeanPrimary.copy(alpha = 0.12f),
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Handshake,
                                    contentDescription = null,
                                    tint = AndeanPrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Sin registros en esta vista",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tus cuentas con terceros están completamente al día. ¡Páramo sereno!",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(state.filteredDebts, key = { it.id }) { debt ->
                val associatedWallet = state.wallets.find { it.id == debt.walletId }
                DebtItemCard(
                    debt = debt,
                    walletName = associatedWallet?.name ?: "Billetera",
                    onAbonarClick = { onEvent(WalletsEvent.OnOpenDebtPayment(debt)) },
                    onDeleteClick = { onEvent(WalletsEvent.OnRequestDeleteDebt(debt)) }
                )
            }
        }
    }

    // Modal Crear Deuda
    if (state.showCreateDebtDialog) {
        CreateDebtModal(
            wallets = state.wallets,
            onDismiss = { onEvent(WalletsEvent.OnDismissCreateDebt) },
            onConfirm = { personName, type, amount, walletId, affectWallet, dueDate, note ->
                onEvent(
                    WalletsEvent.OnCreateDebt(
                        personName = personName,
                        type = type,
                        totalAmountPesos = amount,
                        walletId = walletId,
                        affectWalletBalance = affectWallet,
                        dueDateMillis = dueDate,
                        note = note
                    )
                )
            }
        )
    }

    // Modal Registrar Abono
    state.debtForPayment?.let { debt ->
        RegisterAbonoModal(
            debt = debt,
            wallets = state.wallets,
            onDismiss = { onEvent(WalletsEvent.OnDismissDebtPayment) },
            onConfirm = { amount, walletId, note ->
                onEvent(
                    WalletsEvent.OnRegisterDebtPayment(
                        debtId = debt.id,
                        amountPesos = amount,
                        walletId = walletId,
                        note = note
                    )
                )
            }
        )
    }

    // Diálogo Eliminar Deuda
    state.debtToDelete?.let { debt ->
        AlertDialog(
            onDismissRequest = { onEvent(WalletsEvent.OnDismissDeleteDebtDialog) },
            title = {
                Text(
                    text = "Eliminar Registro",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = "¿Deseas eliminar el registro de deuda de ${debt.personName}? (Esta acción no afectará los movimientos pasados en tu libro mayor).",
                    style = MaterialTheme.typography.bodySmall
                )
            },
            confirmButton = {
                Button(
                    onClick = { onEvent(WalletsEvent.OnConfirmDeleteDebt(debt.id)) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA1A1A)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Eliminar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { onEvent(WalletsEvent.OnDismissDeleteDebtDialog) }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * Tarjeta Resumen Dual de Deudas: Me deben vs Yo debo y balance neto.
 */
@Composable
private fun DebtsDualSummaryCard(
    receivableAmount: Long,
    payableAmount: Long,
    netBalance: Long,
    onNewDebtClick: () -> Unit
) {
    val colors = LocalAndinaColors.current

    AndeanHyperCard(
        modifier = Modifier.fillMaxWidth(),
        variant = HyperCardVariant.HERO,
        contentPadding = 20.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Columna Izquierda: Me deben (Por cobrar)
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(SemanticDebtReceivableGold)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Me deben",
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.textSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "+$ ${formatCop(receivableAmount)}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        ),
                        color = SemanticDebtReceivableGold
                    )
                    Text(
                        text = "Por cobrar",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = colors.textSecondary
                    )
                }

                // Divisor Vertical
                Box(
                    modifier = Modifier
                        .height(46.dp)
                        .width(1.dp)
                        .background(colors.cardBorder)
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Columna Derecha: Yo debo (Por pagar)
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(SemanticDebtPayableOrange)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Yo debo",
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.textSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "-$ ${formatCop(payableAmount)}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        ),
                        color = SemanticDebtPayableOrange
                    )
                    Text(
                        text = "Por pagar",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(thickness = 0.5.dp, color = colors.cardBorder)
            Spacer(modifier = Modifier.height(12.dp))

            // Fila Inferior: Balance Neto y Botón Nueva Deuda
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Balance Neto: ",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textSecondary
                    )
                    Text(
                        text = if (netBalance >= 0) "+$ ${formatCop(netBalance)}" else "-$ ${formatCop(-netBalance)}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (netBalance >= 0) SemanticDebtReceivableGold else SemanticDebtPayableOrange
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AndeanPrimary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onNewDebtClick() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = AndeanOnPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Nueva",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AndeanOnPrimary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Tarjeta individual para una deuda o préstamo (DebtItemCard).
 */
@Composable
private fun DebtItemCard(
    debt: Debt,
    walletName: String,
    onAbonarClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val colors = LocalAndinaColors.current
    val isReceivable = debt.type == DebtType.RECEIVABLE
    val accentColor = if (isReceivable) SemanticDebtReceivableGold else SemanticDebtPayableOrange
    val badgeBg = if (isReceivable) Color(0x1AE5A93C) else Color(0x1AF97316)

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = colors.cardBackground,
        border = BorderStroke(1.dp, colors.cardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Cabecera: Persona, Badge y Menú/Eliminar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = badgeBg,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isReceivable) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = debt.personName,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = colors.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = badgeBg
                            ) {
                                Text(
                                    text = if (isReceivable) "Me debe" else "Le debo",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = accentColor,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            if (debt.isSettled) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = AndeanPrimary.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "✓ Saldada",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = AndeanPrimary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar registro",
                        tint = colors.textSecondary.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Nota (si existe)
            if (debt.note.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = debt.note,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Barra de Progreso
            LinearProgressIndicator(
                progress = { debt.progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (debt.isSettled) AndeanPrimary else accentColor,
                trackColor = colors.canvasBottom,
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Detalle de Montos y Botón Abonar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Pagado ${formatCop(debt.paidAmount.pesos)} de ${formatCop(debt.totalAmount.pesos)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textSecondary
                    )
                    if (!debt.isSettled) {
                        Text(
                            text = "Pendiente: $ ${formatCop(debt.remainingAmount.pesos)}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = accentColor
                        )
                    }
                }

                if (!debt.isSettled) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = accentColor.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onAbonarClick() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Abonar",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentColor
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Modal para Crear una nueva Deuda o Préstamo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateDebtModal(
    wallets: List<Wallet>,
    onDismiss: () -> Unit,
    onConfirm: (
        personName: String,
        type: DebtType,
        amount: Long,
        walletId: Long,
        affectWallet: Boolean,
        dueDate: Long?,
        note: String
    ) -> Unit
) {
    var selectedType by remember { mutableStateOf(DebtType.RECEIVABLE) }
    var personNameInput by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("") }
    var noteInput by remember { mutableStateOf("") }
    var affectWalletBalance by remember { mutableStateOf(true) }

    var selectedWalletId by remember {
        mutableStateOf(wallets.firstOrNull()?.id ?: 1L)
    }

    var walletMenuExpanded by remember { mutableStateOf(false) }
    val selectedWallet = wallets.find { it.id == selectedWalletId } ?: wallets.firstOrNull()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Nueva Deuda / Préstamo",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Selector Tipo: Presté dinero vs Pedí prestado
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(3.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(9.dp),
                        color = if (selectedType == DebtType.RECEIVABLE) SemanticDebtReceivableGold else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(9.dp))
                            .clickable { selectedType = DebtType.RECEIVABLE }
                    ) {
                        Text(
                            text = "🤝 Presté (Me deben)",
                            fontSize = 11.sp,
                            fontWeight = if (selectedType == DebtType.RECEIVABLE) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedType == DebtType.RECEIVABLE) AndeanOnPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(9.dp),
                        color = if (selectedType == DebtType.PAYABLE) SemanticDebtPayableOrange else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(9.dp))
                            .clickable { selectedType = DebtType.PAYABLE }
                    ) {
                        Text(
                            text = "✋ Pedí (Yo debo)",
                            fontSize = 11.sp,
                            fontWeight = if (selectedType == DebtType.PAYABLE) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedType == DebtType.PAYABLE) AndeanOnPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                // Nombre de la persona
                OutlinedTextField(
                    value = personNameInput,
                    onValueChange = { personNameInput = it },
                    label = { Text("Nombre de la persona") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Monto en COP
                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Monto total") },
                    prefix = { Text("$ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Selector de Billetera
                if (wallets.isNotEmpty()) {
                    ExposedDropdownMenuBox(
                        expanded = walletMenuExpanded,
                        onExpandedChange = { walletMenuExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedWallet?.name ?: "Selecciona cuenta",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Billetera asociada") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = walletMenuExpanded) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = walletMenuExpanded,
                            onDismissRequest = { walletMenuExpanded = false }
                        ) {
                            wallets.forEach { w ->
                                DropdownMenuItem(
                                    text = { Text("${w.name} (${formatCop(w.currentBalance.pesos)})") },
                                    onClick = {
                                        selectedWalletId = w.id
                                        walletMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Toggle para afectar saldo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Text(
                            text = "Afectar saldo de la billetera",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = if (selectedType == DebtType.RECEIVABLE) {
                                "Descontará el dinero prestado de tu cuenta"
                            } else {
                                "Ingresará el dinero recibido a tu cuenta"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = affectWalletBalance,
                        onCheckedChange = { affectWalletBalance = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = AndeanPrimary, checkedTrackColor = AndeanPrimary.copy(alpha = 0.5f))
                    )
                }

                // Nota opcional
                OutlinedTextField(
                    value = noteInput,
                    onValueChange = { noteInput = it },
                    label = { Text("Nota o motivo (opcional)") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountInput.toLongOrNull() ?: 0L
                    if (personNameInput.isNotBlank() && amount > 0) {
                        onConfirm(
                            personNameInput,
                            selectedType,
                            amount,
                            selectedWalletId,
                            affectWalletBalance,
                            null,
                            noteInput
                        )
                    }
                },
                enabled = personNameInput.isNotBlank() && (amountInput.toLongOrNull() ?: 0L) > 0,
                colors = ButtonDefaults.buttonColors(containerColor = AndeanPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Guardar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

/**
 * Modal para Registrar Abono a una Deuda o Préstamo existente.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegisterAbonoModal(
    debt: Debt,
    wallets: List<Wallet>,
    onDismiss: () -> Unit,
    onConfirm: (amount: Long, walletId: Long, note: String) -> Unit
) {
    val remaining = debt.remainingAmount.pesos
    var amountInput by remember { mutableStateOf(remaining.toString()) }
    var noteInput by remember { mutableStateOf("") }
    var selectedWalletId by remember { mutableStateOf(debt.walletId) }
    var walletMenuExpanded by remember { mutableStateOf(false) }

    val selectedWallet = wallets.find { it.id == selectedWalletId } ?: wallets.firstOrNull()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Registrar Abono · ${debt.personName}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Saldo pendiente actual: $ ${formatCop(remaining)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Monto del abono
                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Monto del abono") },
                    prefix = { Text("$ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Selector de Billetera
                if (wallets.isNotEmpty()) {
                    ExposedDropdownMenuBox(
                        expanded = walletMenuExpanded,
                        onExpandedChange = { walletMenuExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedWallet?.name ?: "Selecciona cuenta",
                            onValueChange = {},
                            readOnly = true,
                            label = {
                                Text(
                                    if (debt.type == DebtType.RECEIVABLE) "Cuenta que recibe el dinero"
                                    else "Cuenta que paga la deuda"
                                )
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = walletMenuExpanded) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = walletMenuExpanded,
                            onDismissRequest = { walletMenuExpanded = false }
                        ) {
                            wallets.forEach { w ->
                                DropdownMenuItem(
                                    text = { Text("${w.name} (${formatCop(w.currentBalance.pesos)})") },
                                    onClick = {
                                        selectedWalletId = w.id
                                        walletMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Nota del abono
                OutlinedTextField(
                    value = noteInput,
                    onValueChange = { noteInput = it },
                    label = { Text("Nota del abono (opcional)") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountInput.toLongOrNull() ?: 0L
                    if (amount > 0) {
                        onConfirm(amount, selectedWalletId, noteInput)
                    }
                },
                enabled = (amountInput.toLongOrNull() ?: 0L) > 0,
                colors = ButtonDefaults.buttonColors(containerColor = AndeanPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Registrar Abono", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

private fun formatCop(amount: Long): String {
    val format = NumberFormat.getNumberInstance(Locale("es", "CO"))
    format.maximumFractionDigits = 0
    return format.format(amount)
}

package com.andina.finanzas.ui.transactions.detail

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.graphics.Brush
import com.andina.finanzas.ui.components.AndeanHyperCard
import com.andina.finanzas.ui.components.HyperCardVariant
import com.andina.finanzas.ui.theme.LocalAndinaColors
import com.andina.finanzas.ui.components.bouncyClick
import com.andina.finanzas.ui.theme.AndeanMistCanvasBottom
import com.andina.finanzas.ui.theme.AndeanMistCanvasTop
import com.andina.finanzas.ui.theme.AndeanSlateInk
import com.andina.finanzas.ui.theme.FrailejonGold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.andina.finanzas.domain.model.Category
import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.domain.model.Transaction
import com.andina.finanzas.domain.model.TransactionType
import com.andina.finanzas.domain.model.Wallet
import com.andina.finanzas.ui.components.AndinaButton
import com.andina.finanzas.ui.theme.AndeanAntExpenseAmber
import com.andina.finanzas.ui.theme.AndeanError
import com.andina.finanzas.ui.theme.AndeanPrimary
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Pantalla de Detalle y Edición Integral del Movimiento Contable (Sub-fase 2.3).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    transactionId: Long,
    onNavigateBack: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TransactionDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(transactionId) {
        if (state.transaction == null || state.transaction?.id != transactionId) {
            viewModel.loadTransaction(transactionId)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is TransactionDetailEffect.NavigateBack -> onNavigateBack()
                is TransactionDetailEffect.ShowSnackbar -> onShowSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalle del Movimiento",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AndeanSlateInk
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = AndeanSlateInk
                        )
                    }
                },
                actions = {
                    if (state.transaction != null) {
                        IconButton(onClick = { viewModel.onEvent(TransactionDetailEvent.OnRequestDelete) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar movimiento",
                                tint = AndeanError
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(AndeanMistCanvasTop, AndeanMistCanvasBottom)
                    )
                )
        ) {
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AndeanPrimary)
                }
            } else {
                val tx = state.transaction
                if (tx != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                    ) {
                    // 1. Tarjeta Hero con Cifra y Estado Editable
                    TransactionHeroCard(
                        transaction = tx,
                        currentAmountPesos = state.editedAmountPesos,
                        onEditAmountClick = { viewModel.onEvent(TransactionDetailEvent.OnOpenAmountDialog) }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 2. Información y Edición de Metadatos del Movimiento
                    val selectedWallet = state.availableWallets.find { it.id == state.editedWalletId }
                    val selectedToWallet = state.editedToWalletId?.let { toId ->
                        state.availableWallets.find { it.id == toId }
                    }
                    val selectedCategory = state.editedCategoryId?.let { catId ->
                        state.availableCategories.find { it.id == catId }
                    }

                    TransactionMetaCard(
                        transaction = tx,
                        walletName = selectedWallet?.name ?: tx.walletName ?: "Billetera #${state.editedWalletId}",
                        toWalletName = selectedToWallet?.name ?: tx.toWalletName,
                        categoryName = selectedCategory?.name ?: tx.categoryName ?: "Sin categoría",
                        occurredAt = state.editedOccurredAt,
                        onWalletClick = { viewModel.onEvent(TransactionDetailEvent.OnOpenWalletDialog) },
                        onToWalletClick = { viewModel.onEvent(TransactionDetailEvent.OnOpenToWalletDialog) },
                        onCategoryClick = { viewModel.onEvent(TransactionDetailEvent.OnOpenCategoryDialog) },
                        onDateTimeClick = {
                            val cal = Calendar.getInstance().apply { timeInMillis = state.editedOccurredAt }
                            val datePicker = DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    val timePicker = TimePickerDialog(
                                        context,
                                        { _, hourOfDay, minute ->
                                            val newCal = Calendar.getInstance().apply {
                                                set(year, month, dayOfMonth, hourOfDay, minute, 0)
                                            }
                                            viewModel.onEvent(TransactionDetailEvent.OnSelectDate(newCal.timeInMillis))
                                        },
                                        cal.get(Calendar.HOUR_OF_DAY),
                                        cal.get(Calendar.MINUTE),
                                        false
                                    )
                                    timePicker.show()
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            )
                            datePicker.show()
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 3. Sección de Nota y Concepto
                    Text(
                        text = "Nota o Concepto",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.editedNote,
                        onValueChange = { viewModel.onEvent(TransactionDetailEvent.OnNoteChanged(it)) },
                        label = { Text("Nota o concepto") },
                        placeholder = { Text("Ej. Almuerzo con amigos, Transporte") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        singleLine = false,
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AndeanPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Switch Gasto Hormiga (solo para gastos)
                    if (tx.type == TransactionType.GASTO) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
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
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(AndeanAntExpenseAmber.copy(alpha = 0.16f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.BugReport,
                                            contentDescription = null,
                                            tint = AndeanAntExpenseAmber,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "¿Gasto hormiga?",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Impacta la métrica de consumo impulsivo",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Switch(
                                    checked = state.isAntExpense,
                                    onCheckedChange = { viewModel.onEvent(TransactionDetailEvent.OnToggleAntExpense(it)) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = AndeanAntExpenseAmber
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // Botón para guardar cambios
                    AnimatedVisibility(
                        visible = state.hasUnsavedChanges,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        AndinaButton(
                            text = if (state.isSaving) "Guardando..." else "Guardar Cambios",
                            onClick = { viewModel.onEvent(TransactionDetailEvent.OnSaveEdits) },
                            enabled = !state.isSaving,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))
                }
            }
        }
    }
}

    // Modal de edición de Monto
    if (state.showAmountEditDialog) {
        AmountEditDialog(
            currentInput = state.editedAmountInput,
            onInputChange = { viewModel.onEvent(TransactionDetailEvent.OnAmountInputChanged(it)) },
            onConfirm = { viewModel.onEvent(TransactionDetailEvent.OnConfirmAmountEdit) },
            onDismiss = { viewModel.onEvent(TransactionDetailEvent.OnDismissAmountDialog) }
        )
    }

    // Modal de selección de Billetera Origen
    if (state.showWalletPickerDialog) {
        WalletSelectionDialog(
            title = "Seleccionar Billetera Origen",
            wallets = state.availableWallets,
            selectedWalletId = state.editedWalletId,
            onSelect = { viewModel.onEvent(TransactionDetailEvent.OnSelectWallet(it)) },
            onDismiss = { viewModel.onEvent(TransactionDetailEvent.OnDismissWalletDialog) }
        )
    }

    // Modal de selección de Billetera Destino (Transferencias)
    if (state.showToWalletPickerDialog) {
        WalletSelectionDialog(
            title = "Seleccionar Billetera Destino",
            wallets = state.availableWallets.filter { it.id != state.editedWalletId },
            selectedWalletId = state.editedToWalletId ?: 0L,
            onSelect = { viewModel.onEvent(TransactionDetailEvent.OnSelectToWallet(it)) },
            onDismiss = { viewModel.onEvent(TransactionDetailEvent.OnDismissToWalletDialog) }
        )
    }

    // Modal de selección de Categoría
    if (state.showCategoryPickerDialog) {
        CategorySelectionDialog(
            categories = state.availableCategories,
            selectedCategoryId = state.editedCategoryId,
            onSelect = { viewModel.onEvent(TransactionDetailEvent.OnSelectCategory(it)) },
            onDismiss = { viewModel.onEvent(TransactionDetailEvent.OnDismissCategoryDialog) }
        )
    }

    // Modal de confirmación para eliminar
    if (state.showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(TransactionDetailEvent.OnDismissDeleteDialog) },
            title = {
                Text(
                    text = "Eliminar Movimiento",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = "¿Confirmas la eliminación de este registro contable? El saldo de tus cuentas se recalculará automáticamente de forma inmediata."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.onEvent(TransactionDetailEvent.OnConfirmDelete) }
                ) {
                    Text(text = "Eliminar", color = AndeanError, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.onEvent(TransactionDetailEvent.OnDismissDeleteDialog) }
                ) {
                    Text(text = "Cancelar")
                }
            }
        )
    }
}

@Composable
private fun TransactionHeroCard(
    transaction: Transaction,
    currentAmountPesos: Long,
    onEditAmountClick: () -> Unit
) {
    val isIncome = transaction.type == TransactionType.INGRESO
    val isTransfer = transaction.type == TransactionType.TRANSFERENCIA
    val sign = when {
        isIncome -> "+ "
        isTransfer -> ""
        else -> "- "
    }
    val amountColor = when {
        isIncome -> AndeanPrimary
        isTransfer -> Color(0xFF1E88E5)
        else -> MaterialTheme.colorScheme.onSurface
    }

    val colors = LocalAndinaColors.current

    AndeanHyperCard(
        modifier = Modifier.fillMaxWidth(),
        variant = HyperCardVariant.HERO,
        contentPadding = 24.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Badges
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val typeLabel = when (transaction.type) {
                    TransactionType.GASTO -> "Gasto"
                    TransactionType.INGRESO -> "Ingreso"
                    TransactionType.TRANSFERENCIA -> "Transferencia"
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(amountColor.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = typeLabel,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = amountColor
                    )
                }

                if (transaction.isAntExpense) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AndeanAntExpenseAmber.copy(alpha = 0.16f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Gasto Hormiga 🐜",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = AndeanAntExpenseAmber
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Cifra grande clicable para editar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onEditAmountClick() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "$sign${formatPesos(currentAmountPesos)}",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 32.sp,
                        fontFeatureSettings = "tnum"
                    ),
                    color = amountColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar monto",
                    tint = amountColor.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }

            if (transaction.note.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = transaction.note,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TransactionMetaCard(
    transaction: Transaction,
    walletName: String,
    toWalletName: String?,
    categoryName: String,
    occurredAt: Long,
    onWalletClick: () -> Unit,
    onToWalletClick: () -> Unit,
    onCategoryClick: () -> Unit,
    onDateTimeClick: () -> Unit
) {
    val colors = LocalAndinaColors.current

    AndeanHyperCard(
        modifier = Modifier.fillMaxWidth(),
        variant = HyperCardVariant.CARD,
        contentPadding = 16.dp
    ) {
        Column {
            Text(
                text = "Detalles del Asiento (Toca para editar)",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.textSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Billetera origen
            MetaRow(
                icon = Icons.Default.AccountBalanceWallet,
                label = "Billetera origen",
                value = walletName,
                isEditable = true,
                onClick = onWalletClick
            )

            // Billetera destino si es transferencia
            if (transaction.type == TransactionType.TRANSFERENCIA && toWalletName != null) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), thickness = 0.5.dp)
                MetaRow(
                    icon = Icons.AutoMirrored.Filled.CompareArrows,
                    label = "Billetera destino",
                    value = toWalletName,
                    isEditable = true,
                    onClick = onToWalletClick
                )
            }

            // Categoría (para gastos e ingresos)
            if (transaction.type != TransactionType.TRANSFERENCIA) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), thickness = 0.5.dp)
                MetaRow(
                    icon = Icons.Default.Category,
                    label = "Categoría",
                    value = categoryName,
                    isEditable = true,
                    onClick = onCategoryClick
                )
            }

            // Fecha y hora compacta
            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), thickness = 0.5.dp)
            val compactDateStr = formatCompactDate(occurredAt)
            MetaRow(
                icon = Icons.Default.CalendarMonth,
                label = "Fecha y hora",
                value = compactDateStr,
                isEditable = true,
                onClick = onDateTimeClick
            )

            // Folio (solo lectura)
            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), thickness = 0.5.dp)
            MetaRow(
                icon = Icons.Default.Numbers,
                label = "Folio del Libro Mayor",
                value = "#TX-${transaction.id.toString().padStart(6, '0')}",
                isEditable = false
            )
        }
    }
}

@Composable
private fun MetaRow(
    icon: ImageVector,
    label: String,
    value: String,
    isEditable: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .then(if (isEditable && onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(end = 12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Valor alineado a la derecha con weight(1f) para evitar cualquier solapamiento
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = if (isEditable) AndeanPrimary else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.End,
                maxLines = 1
            )
            if (isEditable) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Editar",
                    tint = AndeanPrimary.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun AmountEditDialog(
    currentInput: String,
    onInputChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val previewPesos = currentInput.toLongOrNull() ?: 0L

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Editar Monto",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Ingresa el valor real de la transacción (Máx. $99.999.999):",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = currentInput,
                    onValueChange = onInputChange,
                    label = { Text("Monto en COP") },
                    prefix = { Text("$ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Vista previa: ${formatPesos(previewPesos)}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = AndeanPrimary
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = previewPesos > 0
            ) {
                Text("Aceptar", fontWeight = FontWeight.Bold, color = AndeanPrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun WalletSelectionDialog(
    title: String,
    wallets: List<Wallet>,
    selectedWalletId: Long,
    onSelect: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 320.dp)
            ) {
                items(wallets, key = { it.id }) { wallet ->
                    val isSelected = wallet.id == selectedWalletId
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) AndeanPrimary.copy(alpha = 0.12f) else Color.Transparent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(wallet.id) }
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = wallet.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    color = if (isSelected) AndeanPrimary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Saldo: ${formatPesos(wallet.currentBalance.pesos)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Seleccionada",
                                    tint = AndeanPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun CategorySelectionDialog(
    categories: List<Category>,
    selectedCategoryId: Long?,
    onSelect: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Seleccionar Categoría",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 320.dp)
            ) {
                items(categories, key = { it.id }) { category ->
                    val isSelected = category.id == selectedCategoryId
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) AndeanPrimary.copy(alpha = 0.12f) else Color.Transparent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(category.id) }
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isSelected) AndeanPrimary else MaterialTheme.colorScheme.onSurface
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Seleccionada",
                                    tint = AndeanPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

private fun formatCompactDate(epochMillis: Long): String {
    val formatter = SimpleDateFormat("dd MMM yyyy · h:mm a", Locale("es", "CO"))
    return formatter.format(Date(epochMillis))
}

private fun formatPesos(amount: Long): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
    format.maximumFractionDigits = 0
    return format.format(amount)
}

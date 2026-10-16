package com.andina.finanzas.ui.register

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.andina.finanzas.ui.icons.AndinaIcon
import com.andina.finanzas.ui.icons.AndinaIconKey
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andina.finanzas.domain.model.CategoryKind
import com.andina.finanzas.domain.model.TransactionType
import com.andina.finanzas.ui.components.AndinaButton
import com.andina.finanzas.ui.components.AndinaButtonVariant
import com.andina.finanzas.ui.register.components.CategoryChipsSelector
import com.andina.finanzas.ui.register.components.PaymentWalletSelector
import com.andina.finanzas.ui.theme.AndeanAntExpenseAmber
import com.andina.finanzas.ui.theme.AndeanPrimary
import com.andina.finanzas.ui.theme.LocalAndinaColors
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Bottom Sheet Ergonómico de Registro Rápido (One-Handed Express Register Sheet).
 *
 * Características clave:
 * 1. Diseñado para manejo total con una sola mano (Thumb Zone): Todo se opera en la mitad inferior.
 * 2. Cero distracciones: Sin ilustraciones innecesarias; flujo puro de entrada financiera en 3 segundos.
 * 3. Tipografía limpia sin redundancia: Sin signo pesos ni etiqueta COP innecesaria.
 * 4. Paleta de lujo Andina consistente: Oro Frailejón, Verde Andino y Neutros Minerales.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterBottomSheet(
    onDismiss: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier,
    initialType: TransactionType = TransactionType.GASTO,
    onNavigateToCategoryManagement: () -> Unit = {},
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val colors = LocalAndinaColors.current

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    // Manejo de efectos secundarios del ViewModel
    LaunchedEffect(Unit) {
        if (initialType != TransactionType.GASTO) {
            viewModel.onAction(RegisterAction.TypeChanged(initialType))
        }
        viewModel.effects.collect { effect ->
            when (effect) {
                is RegisterEffect.Saved -> {
                    coroutineScope.launch { sheetState.hide() }
                    onDismiss()
                    onShowSnackbar(effect.message)
                }
                is RegisterEffect.Error -> {
                    onShowSnackbar(effect.message)
                }
                RegisterEffect.HapticTick -> {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            }
        }
    }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()

    // Automatiza la salida del teclado con foco directo al monto
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(200)
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var showSmsPasteDialog by remember { mutableStateOf(false) }
    var smsInputText by remember { mutableStateOf("") }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    var showNoteField by remember { mutableStateOf(state.note.isNotEmpty()) }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = state.occurredAt)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = colors.cardBackground,
        dragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(colors.cardBorder)
                )
            }
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 1. Barra Superior Compacta Ergonómica: Utilidades (izquierda) + Selector de Tipo (derecha, bajo el pulgar)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Utilidades rápidas: Cerrar, Fecha y SMS (A la izquierda)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Botón Cerrar
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = colors.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Chip Fecha
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = colors.canvasBottom,
                        border = androidx.compose.foundation.BorderStroke(0.6.dp, colors.cardBorder),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showDatePicker = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = colors.accentGold,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = formatShortDate(state.occurredAt),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                ),
                                color = colors.textPrimary
                            )
                        }
                    }

                    // Chip SMS
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = colors.accentGold.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(0.6.dp, colors.accentGold.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showSmsPasteDialog = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sms,
                                contentDescription = "SMS",
                                tint = colors.accentGold,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "SMS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = colors.accentGold
                                )
                            )
                        }
                    }
                }

                // Selector Segmentado de Tipo [ Gasto | Ingreso | Transferir ] (A la derecha para alcance inmediato del pulgar)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.canvasBottom)
                        .border(0.8.dp, colors.cardBorder, RoundedCornerShape(12.dp))
                        .padding(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val types = listOf(TransactionType.GASTO, TransactionType.INGRESO, TransactionType.TRANSFERENCIA)

                    types.forEach { typeOption ->
                        val isSelected = state.type == typeOption
                        val label = when (typeOption) {
                            TransactionType.GASTO -> "Gasto"
                            TransactionType.INGRESO -> "Ingreso"
                            TransactionType.TRANSFERENCIA -> "Transferir"
                        }
                        val activeColor = when (typeOption) {
                            TransactionType.GASTO -> colors.textPrimary
                            TransactionType.INGRESO -> colors.statusSuccess
                            TransactionType.TRANSFERENCIA -> colors.accentGold
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) colors.cardBackground else Color.Transparent,
                            shadowElevation = if (isSelected) 1.dp else 0.dp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    viewModel.onAction(RegisterAction.TypeChanged(typeOption))
                                }
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 12.sp
                                ),
                                color = if (isSelected) activeColor else colors.textSecondary,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // 2. Display de Monto Hero (Sin pesos ni COP estorbosos, número limpio tabular)
            val heroColor = when {
                state.amountError != null -> colors.statusDanger
                state.type == TransactionType.INGRESO -> colors.statusSuccess
                else -> colors.textMoneyDisplay
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BasicTextField(
                    value = state.amountDigits,
                    onValueChange = { viewModel.onAction(RegisterAction.AmountDigitsChanged(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    textStyle = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 38.sp,
                        textAlign = TextAlign.Center,
                        color = heroColor,
                        fontFeatureSettings = "tnum"
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    visualTransformation = CopDigitsVisualTransformation(),
                    singleLine = true,
                    cursorBrush = SolidColor(colors.accentGold),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (state.amountDigits.isEmpty()) {
                                Text(
                                    text = "0",
                                    style = MaterialTheme.typography.displayMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 38.sp,
                                        textAlign = TextAlign.Center,
                                        color = colors.textSecondary.copy(alpha = 0.28f),
                                        fontFeatureSettings = "tnum"
                                    )
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                // Subtexto en letras / error
                if (state.amountInWords.isNotEmpty()) {
                    Text(
                        text = state.amountInWords,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = colors.textSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                if (state.amountError != null) {
                    Text(
                        text = state.amountError!!,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = colors.statusDanger,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            // 3. Atajos Rápidos de Dinero (+10k, +20k, +50k, +100k, ✕)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                listOf(10_000L, 20_000L, 50_000L, 100_000L).forEach { quick ->
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.onAction(RegisterAction.AddQuickAmount(quick))
                            },
                        shape = RoundedCornerShape(8.dp),
                        color = colors.canvasBottom,
                        border = androidx.compose.foundation.BorderStroke(0.6.dp, colors.cardBorder)
                    ) {
                        Text(
                            text = "+${quick / 1000}k",
                            modifier = Modifier.padding(vertical = 5.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = colors.textPrimary
                            )
                        )
                    }
                }

                if (state.amountDigits.isNotEmpty()) {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.onAction(RegisterAction.ClearAmount)
                            },
                        shape = RoundedCornerShape(8.dp),
                        color = colors.statusDanger.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "✕",
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = colors.statusDanger
                            )
                        )
                    }
                }
            }

            // 4. Selector de Billetera (Método de Pago)
            PaymentWalletSelector(
                wallets = state.wallets,
                selectedWalletId = state.walletId,
                projectedRemainingBalance = state.projectedRemainingBalance,
                onWalletSelected = { viewModel.onAction(RegisterAction.WalletSelected(it)) },
                labelTitle = if (state.type == TransactionType.TRANSFERENCIA) "Desde cuenta" else "Método de pago"
            )

            // Si es Transferencia: Selector de cuenta destino
            if (state.type == TransactionType.TRANSFERENCIA) {
                val destinationWallets = state.wallets.filter { it.id != state.walletId }
                PaymentWalletSelector(
                    wallets = destinationWallets,
                    selectedWalletId = state.toWalletId ?: destinationWallets.firstOrNull()?.id,
                    projectedRemainingBalance = null,
                    onWalletSelected = { viewModel.onAction(RegisterAction.ToWalletSelected(it)) },
                    labelTitle = "Hacia cuenta de destino"
                )
            }

            // 5. Selector de Categorías (solo si no es transferencia)
            if (state.type != TransactionType.TRANSFERENCIA && state.categories.isNotEmpty()) {
                CategoryChipsSelector(
                    categories = state.categories,
                    selectedCategoryId = state.categoryId,
                    onCategorySelected = { viewModel.onAction(RegisterAction.CategorySelected(it)) },
                    onAddCategoryClick = { showAddCategoryDialog = true },
                    onOpenCategoryManager = { onNavigateToCategoryManagement() }
                )
            }

            // 6. Micro-utilidades táctiles (Gasto Hormiga + Nota opcional)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Chip Gasto Hormiga (visible en gastos)
                if (state.type == TransactionType.GASTO) {
                    val isAnt = state.isAntExpense
                    val antBg = if (isAnt) AndeanAntExpenseAmber.copy(alpha = 0.2f) else colors.canvasBottom
                    val antBorder = if (isAnt) AndeanAntExpenseAmber else colors.cardBorder
                    val antText = if (isAnt) AndeanAntExpenseAmber else colors.textSecondary

                    Row(
                        modifier = Modifier
                            .height(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(antBg)
                            .border(0.8.dp, antBorder, RoundedCornerShape(8.dp))
                            .clickable(
                                role = Role.Checkbox,
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    viewModel.onAction(RegisterAction.ToggleAntExpense)
                                }
                            )
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.BugReport,
                            contentDescription = null,
                            tint = antText,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isAnt) "Hormiga ✓" else "Hormiga",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isAnt) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.sp
                            ),
                            color = antText
                        )
                    }
                }

                // Chip Nota opcional
                val hasNote = state.note.isNotEmpty()
                Row(
                    modifier = Modifier
                        .height(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (hasNote || showNoteField) colors.accentGold.copy(alpha = 0.12f) else colors.canvasBottom)
                        .border(0.8.dp, if (hasNote || showNoteField) colors.accentGold else colors.cardBorder, RoundedCornerShape(8.dp))
                        .clickable {
                            showNoteField = !showNoteField
                        }
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = null,
                        tint = if (hasNote || showNoteField) colors.accentGold else colors.textSecondary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (hasNote) "Nota: ${state.note.take(8)}..." else "Añadir nota",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        ),
                        color = if (hasNote || showNoteField) colors.accentGold else colors.textSecondary
                    )
                }
            }

            // Campo de nota opcional desplegable
            AnimatedVisibility(
                visible = showNoteField,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                OutlinedTextField(
                    value = state.note,
                    onValueChange = { viewModel.onAction(RegisterAction.NoteChanged(it)) },
                    placeholder = { Text("Nota o detalle...", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.accentGold,
                        unfocusedBorderColor = colors.cardBorder
                    )
                )
            }

            // 7. Botón Principal Ergonómico para el Pulgar
            val isOverdraft = state.type == TransactionType.GASTO &&
                    (state.projectedRemainingBalance?.isNegative == true) &&
                    !state.amountMoney.isZero

            val ctaText = when {
                state.amountMoney.isZero -> "Ingresa un monto"
                isOverdraft -> "Confirmar Gasto (Sobregiro)"
                state.type == TransactionType.GASTO -> "Guardar Gasto (${state.amountMoney.formatCop()})"
                state.type == TransactionType.INGRESO -> "Guardar Ingreso (${state.amountMoney.formatCop()})"
                state.type == TransactionType.TRANSFERENCIA -> "Transferir (${state.amountMoney.formatCop()})"
                else -> "Guardar Movimiento"
            }

            AndinaButton(
                text = ctaText,
                onClick = { viewModel.onAction(RegisterAction.Save) },
                variant = if (isOverdraft) AndinaButtonVariant.AntExpense else AndinaButtonVariant.RegisterAction,
                isSaving = state.isSaving,
                enabled = state.canSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 6.dp)
            )
        }
    }

    // Modal de selección de Fecha
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                        viewModel.onAction(RegisterAction.DateChanged(millis))
                        showDatePicker = false
                    }
                ) {
                    Text("Seleccionar", color = AndeanPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Modal de Pegado de SMS Bancario
    if (showSmsPasteDialog) {
        AlertDialog(
            onDismissRequest = { showSmsPasteDialog = false },
            title = {
                Text(
                    text = "Pegar SMS Bancario",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Pega un SMS de Bancolombia, Nequi o DaviPlata:",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = smsInputText,
                        onValueChange = { smsInputText = it },
                        placeholder = { Text("Ej: Bancolombia le informa compra por $45.000...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (smsInputText.isNotBlank()) {
                            viewModel.onAction(RegisterAction.ParseSms(smsInputText))
                            showSmsPasteDialog = false
                            smsInputText = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AndeanPrimary)
                ) {
                    Text("Autocompletar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSmsPasteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Modal de creación rápida de categoría
    if (showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = {
                Text(
                    text = "Nueva Categoría",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Crea una nueva categoría para tus registros contables:",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newCategoryName,
                        onValueChange = { newCategoryName = it },
                        label = { Text("Nombre de la categoría") },
                        placeholder = { Text("Ej: Mascotas, Gimnasio") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                showAddCategoryDialog = false
                                onNavigateToCategoryManagement()
                            },
                        color = colors.accentGold.copy(alpha = 0.12f),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(colors.accentGold.copy(alpha = 0.5f))
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AndinaIcon(
                                    icon = AndinaIconKey.TOOL_PALETTE,
                                    contentDescription = null,
                                    size = 18.dp
                                )
                                Column {
                                    Text(
                                        text = "Personalización Completa",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = colors.textPrimary
                                        )
                                    )
                                    Text(
                                        text = "Elige más colores andinos e iconos",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            color = colors.textSecondary
                                        )
                                    )
                                }
                            }
                            AndinaIcon(
                                icon = AndinaIconKey.CHEVRON_RIGHT,
                                contentDescription = null,
                                size = 16.dp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCategoryName.isNotBlank()) {
                            viewModel.onAction(
                                RegisterAction.CreateCategory(
                                    name = newCategoryName.trim(),
                                    kind = if (state.type == TransactionType.INGRESO) CategoryKind.INGRESO else CategoryKind.GASTO,
                                    icon = "category",
                                    colorHex = "#C5A059"
                                )
                            )
                            showAddCategoryDialog = false
                            newCategoryName = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AndeanPrimary)
                ) {
                    Text("Crear")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Modal de confirmación a 2 pasos para cifras altas (> $10.000.000 COP)
    if (state.showHighAmountConfirmation) {
        AlertDialog(
            onDismissRequest = { viewModel.onAction(RegisterAction.DismissHighAmount) },
            shape = RoundedCornerShape(24.dp),
            containerColor = colors.cardBackground,
            title = {
                Text(
                    text = "Confirmar cifra inusual",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
            },
            text = {
                Text(
                    text = "Has ingresado un monto de ${state.amountMoney.formatCop()} (${state.amountInWords}). ¿Estás seguro de registrar este movimiento?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onAction(RegisterAction.ConfirmHighAmount) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AndeanPrimary)
                ) {
                    Text("Sí, registrar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onAction(RegisterAction.DismissHighAmount) }) {
                    Text("Revisar cifra", color = colors.textSecondary)
                }
            }
        )
    }

    // Modal de advertencia de sobregiro
    if (state.showNegativeBalanceConfirmation) {
        val wallet = state.selectedWallet
        val currentBal = wallet?.currentBalance ?: com.andina.finanzas.domain.model.Money.ZERO
        val remaining = currentBal - state.amountMoney
        AlertDialog(
            onDismissRequest = { viewModel.onAction(RegisterAction.DismissNegativeBalance) },
            shape = RoundedCornerShape(24.dp),
            containerColor = colors.cardBackground,
            title = {
                Text(
                    text = "⚠️ Saldo insuficiente en ${wallet?.name ?: "billetera"}",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = colors.statusDanger
                )
            },
            text = {
                Text(
                    text = "El saldo actual es de ${currentBal.formatCop()} y vas a registrar ${state.amountMoney.formatCop()}.\n\nEsta cuenta quedará con saldo negativo de ${remaining.formatCop()}.\n\n¿Deseas registrar este sobregiro?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onAction(RegisterAction.ConfirmNegativeBalance) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.statusDanger)
                ) {
                    Text("Registrar sobregiro", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onAction(RegisterAction.DismissNegativeBalance) }) {
                    Text("Cambiar billetera", color = colors.textSecondary)
                }
            }
        )
    }
}

private fun formatShortDate(timestamp: Long): String {
    val formatter = SimpleDateFormat("d MMM", Locale("es", "CO"))
    val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
    val itemDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date(timestamp))
    return if (today == itemDate) "Hoy" else formatter.format(Date(timestamp))
}

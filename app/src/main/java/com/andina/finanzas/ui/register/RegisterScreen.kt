package com.andina.finanzas.ui.register

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
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
import com.andina.finanzas.ui.register.components.MuggyPeekingHeader
import com.andina.finanzas.ui.register.components.PaymentWalletSelector
import com.andina.finanzas.ui.register.components.RegisterTypeSelector
import com.andina.finanzas.ui.theme.AndeanAntExpenseAmber
import com.andina.finanzas.ui.theme.AndeanDragHandle
import com.andina.finanzas.ui.theme.AndeanPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * VisualTransformation para formateo numérico con puntos separadores de miles.
 * Mantiene la correspondencia biunívoca exacta de posiciones de cursor sin interferir con prefijos/sufijos de UI.
 */
class CopDigitsVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        if (raw.isEmpty()) {
            return TransformedText(AnnotatedString(""), OffsetMapping.Identity)
        }
        val formatted = StringBuilder()
        val totalDigits = raw.length
        for (i in 0 until totalDigits) {
            formatted.append(raw[i])
            val digitsFromRight = totalDigits - 1 - i
            if (digitsFromRight > 0 && digitsFromRight % 3 == 0) {
                formatted.append('.')
            }
        }
        val transformed = AnnotatedString(formatted.toString())

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val clampedOffset = offset.coerceIn(0, totalDigits)
                if (clampedOffset == 0) return 0
                var dots = 0
                for (k in 0 until clampedOffset) {
                    val digitsFromRight = totalDigits - 1 - k
                    if (digitsFromRight > 0 && digitsFromRight % 3 == 0 && k < clampedOffset - 1) {
                        dots++
                    }
                }
                return (clampedOffset + dots).coerceIn(0, transformed.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 0) return 0
                if (offset >= transformed.length) return totalDigits
                val sub = transformed.text.substring(0, offset.coerceIn(0, transformed.length))
                val digitCount = sub.count { it.isDigit() }
                return digitCount.coerceIn(0, totalDigits)
            }
        }

        return TransformedText(transformed, offsetMapping)
    }
}

/**
 * Punto de entrada Route para Registro Rápido.
 * Conecta el [RegisterViewModel], recolecta estados y efectos de forma desacoplada.
 */
@Composable
fun RegisterRoute(
    onDone: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is RegisterEffect.Saved -> {
                    onDone()
                    onShowSnackbar(effect.message)
                }
                is RegisterEffect.Error -> {
                    onShowSnackbar(effect.message)
                }
                RegisterEffect.HapticTick -> {
                    // Feedback emitido directamente
                }
            }
        }
    }

    RegisterScreen(
        state = state,
        onAction = viewModel::onAction,
        onClose = onDone,
        modifier = modifier
    )
}

/**
 * Pantalla completa de Registro Rápido con Muggy asomado,
 * teclado nativo de Android, confirmación verbal y previsualización de saldo.
 */
@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    state: RegisterUiState,
    onAction: (RegisterAction) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val isDark = isSystemInDarkTheme()
    val scrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }

    val isImeVisible = WindowInsets.isImeVisible

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    var showNoteField by remember { mutableStateOf(state.note.isNotEmpty()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showSmsPasteDialog by remember { mutableStateOf(false) }
    var smsInputText by remember { mutableStateOf("") }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = state.occurredAt)

    val colors = com.andina.finanzas.ui.theme.LocalAndinaColors.current

    Surface(
        modifier = modifier.fillMaxSize(),
        color = colors.canvasTop
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Muggy asomado con colapso reactivo cuando el teclado virtual está desplegado
            AnimatedVisibility(
                visible = !isImeVisible,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                MuggyPeekingHeader(
                    currentAmountCOP = state.amountMoney.pesos,
                    lastPressedDigit = state.lastPressedDigit,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // 2. Card contenedor blanco con esquinas redondeadas
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(colors.cardBackground)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Manija de arrastre y barra de utilidades superior (Fecha + Pegar SMS + Cerrar)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Chip de Selector de Fecha
                        val formattedDate = formatShortDate(state.occurredAt)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = colors.canvasBottom,
                            border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { showDatePicker = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = colors.accentGold,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = formattedDate,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp
                                    ),
                                    color = colors.textPrimary
                                )
                            }
                        }

                        // Botón de Decodificar / Pegar SMS
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = colors.accentGold.copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, colors.accentGold.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { showSmsPasteDialog = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sms,
                                    contentDescription = "Pegar SMS",
                                    tint = colors.accentGold,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "📩 Pegar SMS",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = colors.accentGold
                                    )
                                )
                            }
                        }

                        // Botón cerrar (X)
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Contenido scrollable del formulario (NUNCA se ocultan billeteras ni categorías)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(scrollState)
                            .padding(horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 3. Segmentador Tipo (Gasto | Ingreso | Transferencia)
                        RegisterTypeSelector(
                            selectedType = state.type,
                            onTypeSelected = { onAction(RegisterAction.TypeChanged(it)) }
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // 4. Input Nativo de Monto con Teclado Numérico Android y VisualTransformation
                        val heroColor = when {
                            state.amountError != null -> colors.statusDanger
                            state.type == TransactionType.INGRESO -> colors.statusSuccess
                            else -> colors.textMoneyDisplay
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            OutlinedTextField(
                                value = state.amountDigits,
                                onValueChange = { onAction(RegisterAction.AmountDigitsChanged(it)) },
                                modifier = Modifier
                                    .weight(1f)
                                    .focusRequester(focusRequester),
                                textStyle = MaterialTheme.typography.displaySmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 28.sp,
                                    lineHeight = 34.sp,
                                    fontFeatureSettings = "tnum",
                                    textAlign = TextAlign.Center,
                                    color = heroColor
                                ),
                                prefix = {
                                    Text(
                                        text = "$ ",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 22.sp,
                                        lineHeight = 28.sp,
                                        color = heroColor
                                    )
                                },
                                placeholder = {
                                    Text(
                                        text = "0",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.displaySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 28.sp,
                                            lineHeight = 34.sp,
                                            color = colors.textSecondary.copy(alpha = 0.35f)
                                        )
                                    )
                                },
                                visualTransformation = CopDigitsVisualTransformation(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                shape = RoundedCornerShape(18.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = colors.accentGold,
                                    unfocusedBorderColor = colors.cardBorder
                                )
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            // Sufijo COP desacoplado como componente Text fijo estilizado
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = colors.canvasBottom,
                                border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder)
                            ) {
                                Text(
                                    text = "COP",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = colors.textSecondary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp)
                                )
                            }
                        }

                        // Confirmación en palabras (ej. "Cincuenta mil pesos")
                        if (state.amountInWords.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = state.amountInWords,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 13.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }

                        if (state.amountError != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = state.amountError,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Chips de atajos rápidos de dinero fijados inmediatamente debajo del monto
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(10_000L, 20_000L, 50_000L, 100_000L).forEach { quick ->
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            onAction(RegisterAction.AddQuickAmount(quick))
                                        },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isDark) Color(0xFF222520) else Color(0xFFF1EEE8)
                                ) {
                                    Text(
                                        text = "+$${quick / 1000}k",
                                        modifier = Modifier.padding(vertical = 6.dp),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }
                            }

                            // Botón limpiar monto
                            if (state.amountDigits.isNotEmpty()) {
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            onAction(RegisterAction.ClearAmount)
                                        },
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = "Limpiar",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // 5. Selector de Billetera Origen
                        PaymentWalletSelector(
                            wallets = state.wallets,
                            selectedWalletId = state.walletId,
                            projectedRemainingBalance = state.projectedRemainingBalance,
                            onWalletSelected = { onAction(RegisterAction.WalletSelected(it)) },
                            labelTitle = if (state.type == TransactionType.TRANSFERENCIA) "Desde cuenta" else "Método de pago"
                        )

                        // Si es Transferencia, selector de cuenta destino
                        if (state.type == TransactionType.TRANSFERENCIA) {
                            Spacer(modifier = Modifier.height(12.dp))
                            val destinationWallets = state.wallets.filter { it.id != state.walletId }
                            PaymentWalletSelector(
                                wallets = destinationWallets,
                                selectedWalletId = state.toWalletId ?: destinationWallets.firstOrNull()?.id,
                                projectedRemainingBalance = null,
                                onWalletSelected = { onAction(RegisterAction.ToWalletSelected(it)) },
                                labelTitle = "Hacia cuenta de destino"
                            )
                        }

                        // 6. Selector de Categoría (si no es transferencia)
                        if (state.type != TransactionType.TRANSFERENCIA && state.categories.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            CategoryChipsSelector(
                                categories = state.categories,
                                selectedCategoryId = state.categoryId,
                                onCategorySelected = { onAction(RegisterAction.CategorySelected(it)) },
                                onAddCategoryClick = { showAddCategoryDialog = true }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // 7. Botones auxiliares: Gasto Hormiga y Nota
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Botón Gasto Hormiga caramelo (visible en gastos)
                            if (state.type == TransactionType.GASTO) {
                                val isAnt = state.isAntExpense
                                val antBg = if (isAnt) AndeanAntExpenseAmber else AndeanAntExpenseAmber.copy(alpha = 0.12f)
                                val antText = if (isAnt) Color.White else AndeanAntExpenseAmber

                                Row(
                                    modifier = Modifier
                                        .height(38.dp)
                                        .clip(RoundedCornerShape(19.dp))
                                        .background(antBg)
                                        .border(
                                            width = 1.dp,
                                            color = AndeanAntExpenseAmber,
                                            shape = RoundedCornerShape(19.dp)
                                        )
                                        .clickable(
                                            role = Role.Checkbox,
                                            onClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                onAction(RegisterAction.ToggleAntExpense)
                                            }
                                        )
                                        .padding(horizontal = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BugReport,
                                        contentDescription = null,
                                        tint = antText,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isAnt) "Gasto Hormiga ✓" else "Gasto Hormiga",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 12.sp
                                        ),
                                        color = antText
                                    )
                                }
                            }

                            // Botón para expandir/ocultar nota (se oculta cuando el campo ya está abierto)
                            if (!showNoteField) {
                                Row(
                                    modifier = Modifier
                                        .height(38.dp)
                                        .clip(RoundedCornerShape(19.dp))
                                        .background(if (isDark) Color(0xFF222520) else Color(0xFFF1EEE8))
                                        .clickable { showNoteField = true }
                                        .padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EditNote,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (state.note.isNotEmpty()) "Nota: ${state.note.take(12)}..." else "Añadir nota",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontSize = 12.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Campo de texto de Nota expandible con placeholder contextual
                        AnimatedVisibility(
                            visible = showNoteField,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            val notePlaceholder = when (state.type) {
                                TransactionType.INGRESO -> "Ej. Pago de quincena, honorarios, venta..."
                                TransactionType.GASTO -> "Ej. Almuerzo con amigos, mercado, gasolina..."
                                TransactionType.TRANSFERENCIA -> "Ej. Ahorro para vacaciones, pago tarjeta..."
                            }

                            Column(modifier = Modifier.padding(top = 8.dp)) {
                                OutlinedTextField(
                                    value = state.note,
                                    onValueChange = { onAction(RegisterAction.NoteChanged(it)) },
                                    placeholder = { Text(notePlaceholder) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    singleLine = true,
                                    trailingIcon = {
                                        IconButton(onClick = {
                                            showNoteField = false
                                            if (state.note.isNotEmpty()) {
                                                onAction(RegisterAction.NoteChanged(""))
                                            }
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Ocultar nota",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AndeanPrimary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // 8. Botón Principal CTA Fijo en Barra Inferior [ Guardar Gasto ]
                    Surface(
                        color = colors.cardBackground,
                        tonalElevation = 4.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                        ) {
                            val isOverdraft = state.type == TransactionType.GASTO &&
                                    (state.projectedRemainingBalance?.isNegative == true) &&
                                    !state.amountMoney.isZero

                            val ctaText = when {
                                state.amountMoney.isZero -> "Ingresa un monto"
                                isOverdraft -> "Confirmar Gasto (Sobregiro detectado)"
                                state.type == TransactionType.GASTO -> "Guardar Gasto (${state.amountMoney.formatCop()})"
                                state.type == TransactionType.INGRESO -> "Guardar Ingreso (${state.amountMoney.formatCop()})"
                                state.type == TransactionType.TRANSFERENCIA -> "Transferir (${state.amountMoney.formatCop()})"
                                else -> "Guardar Movimiento"
                            }

                            AndinaButton(
                                text = ctaText,
                                onClick = { onAction(RegisterAction.Save) },
                                variant = if (isOverdraft) AndinaButtonVariant.AntExpense else AndinaButtonVariant.RegisterAction,
                                isSaving = state.isSaving,
                                enabled = state.canSave,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
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
                        onAction(RegisterAction.DateChanged(millis))
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
                        text = "Pega un SMS de Bancolombia, Nequi o DaviPlata. Se completará el monto, billetera, categoría y nota:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = smsInputText,
                        onValueChange = { smsInputText = it },
                        placeholder = { Text("Ej: Bancolombia le informa compra por $45.000 en TIENDAS D1...") },
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
                            onAction(RegisterAction.ParseSms(smsInputText))
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newCategoryName,
                        onValueChange = { newCategoryName = it },
                        label = { Text("Nombre de la categoría") },
                        placeholder = { Text("Ej: Mascotas, Gimnasio, Cursos") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCategoryName.isNotBlank()) {
                            onAction(
                                RegisterAction.CreateCategory(
                                    name = newCategoryName.trim(),
                                    kind = if (state.type == TransactionType.INGRESO) CategoryKind.INGRESO else CategoryKind.GASTO,
                                    icon = "category",
                                    colorHex = "#435C3C"
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
            onDismissRequest = { onAction(RegisterAction.DismissHighAmount) },
            shape = RoundedCornerShape(24.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    text = "Confirmar cifra inusual",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = "Has ingresado un monto de ${state.amountMoney.formatCop()} (${state.amountInWords}). ¿Estás seguro de registrar este movimiento?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = { onAction(RegisterAction.ConfirmHighAmount) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AndeanPrimary)
                ) {
                    Text("Sí, registrar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { onAction(RegisterAction.DismissHighAmount) }) {
                    Text("Revisar cifra", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }

    // Modal de advertencia de saldo insuficiente / sobregiro (Módulo 14)
    if (state.showNegativeBalanceConfirmation) {
        val wallet = state.selectedWallet
        val currentBal = wallet?.currentBalance ?: com.andina.finanzas.domain.model.Money.ZERO
        val remaining = currentBal - state.amountMoney
        AlertDialog(
            onDismissRequest = { onAction(RegisterAction.DismissNegativeBalance) },
            shape = RoundedCornerShape(24.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    text = "⚠️ Saldo insuficiente en ${wallet?.name ?: "billetera"}",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.error
                )
            },
            text = {
                Text(
                    text = "El saldo actual es de ${currentBal.formatCop()} y vas a registrar ${state.amountMoney.formatCop()}.\n\nEsta cuenta quedará con un saldo negativo de ${remaining.formatCop()}.\n\n¿Deseas registrar este sobregiro/deuda o seleccionar otra billetera?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = { onAction(RegisterAction.ConfirmNegativeBalance) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Registrar sobregiro", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { onAction(RegisterAction.DismissNegativeBalance) }) {
                    Text("Cambiar billetera", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }
}

private fun formatShortDate(timestamp: Long): String {
    val formatter = SimpleDateFormat("d MMM", Locale("es", "CO"))
    val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
    val itemDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date(timestamp))
    return if (today == itemDate) "📅 Hoy ▾" else "📅 ${formatter.format(Date(timestamp))} ▾"
}

package com.andina.finanzas.ui.register

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andina.finanzas.domain.model.TransactionType
import com.andina.finanzas.ui.components.AndinaButton
import com.andina.finanzas.ui.components.AndinaButtonVariant
import com.andina.finanzas.ui.register.components.CategoryChipsSelector
import com.andina.finanzas.ui.register.components.MuggyPeekingHeader
import com.andina.finanzas.ui.register.components.NumericKeypad
import com.andina.finanzas.ui.register.components.PaymentWalletSelector
import com.andina.finanzas.ui.register.components.RegisterTypeSelector
import com.andina.finanzas.ui.theme.AndeanAntExpenseAmber
import com.andina.finanzas.ui.theme.AndeanDragHandle
import com.andina.finanzas.ui.theme.AndeanPrimary

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
 * teclado táctil, confirmación verbal y previsualización de saldo.
 */
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

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    var showNoteField by remember { mutableStateOf(state.note.isNotEmpty()) }

    val cardBackground = if (isDark) Color(0xFF161914) else Color.White
    val topBackdrop = if (isDark) Color(0xFF0F110E) else Color(0xFFEFECE5)

    Surface(
        modifier = modifier.fillMaxSize(),
        color = topBackdrop
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Muggy asomado en la parte superior con Eye-Tracking
            MuggyPeekingHeader(
                currentAmountCOP = state.amountMoney.pesos,
                lastPressedDigit = state.lastPressedDigit,
                modifier = Modifier.padding(top = 8.dp)
            )

            // 2. Card contenedor blanco con esquinas redondeadas
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(cardBackground)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .navigationBarsPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Manija de arrastre y botón de cerrar
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Drag handle
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(AndeanDragHandle)
                        )

                        // Botón cerrar (X)
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // 3. Segmentador Tipo (Gasto | Ingreso | Transferencia)
                    RegisterTypeSelector(
                        selectedType = state.type,
                        onTypeSelected = { onAction(RegisterAction.TypeChanged(it)) }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4. Input Nativo de Monto con Teclado Numérico Android
                    val heroColor = when {
                        state.amountError != null -> MaterialTheme.colorScheme.error
                        state.type == TransactionType.INGRESO -> Color(0xFF2E6316)
                        else -> MaterialTheme.colorScheme.onSurface
                    }

                    OutlinedTextField(
                        value = state.amountDigits,
                        onValueChange = { onAction(RegisterAction.AmountDigitsChanged(it)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        textStyle = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 32.sp,
                            fontFeatureSettings = "tnum",
                            textAlign = TextAlign.Center,
                            color = heroColor
                        ),
                        placeholder = {
                            Text(
                                text = "$ 0 COP",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.displaySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 30.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                                )
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AndeanPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                    )

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

                    // Chips de atajos rápidos de dinero
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
                        PaymentWalletSelector(
                            wallets = state.wallets.filter { it.id != state.walletId },
                            selectedWalletId = state.toWalletId,
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
                            onCategorySelected = { onAction(RegisterAction.CategorySelected(it)) }
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

                        // Botón para expandir/ocultar nota
                        Row(
                            modifier = Modifier
                                .height(38.dp)
                                .clip(RoundedCornerShape(19.dp))
                                .background(if (isDark) Color(0xFF222520) else Color(0xFFF1EEE8))
                                .clickable { showNoteField = !showNoteField }
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

                    // Campo de texto de Nota expandible
                    AnimatedVisibility(
                        visible = showNoteField,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            OutlinedTextField(
                                value = state.note,
                                onValueChange = { onAction(RegisterAction.NoteChanged(it)) },
                                placeholder = { Text("Ej. Almuerzo con amigos, mercado...") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AndeanPrimary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 9. Botón Principal CTA [ Guardar Gasto ]
                    val ctaText = when (state.type) {
                        TransactionType.GASTO -> if (state.amountMoney.isZero) "Ingresa un monto" else "Guardar Gasto (${state.amountMoney.formatCop()})"
                        TransactionType.INGRESO -> if (state.amountMoney.isZero) "Ingresa un monto" else "Guardar Ingreso (${state.amountMoney.formatCop()})"
                        TransactionType.TRANSFERENCIA -> if (state.amountMoney.isZero) "Ingresa un monto" else "Transferir (${state.amountMoney.formatCop()})"
                    }

                    AndinaButton(
                        text = ctaText,
                        onClick = { onAction(RegisterAction.Save) },
                        variant = AndinaButtonVariant.RegisterAction,
                        isSaving = state.isSaving,
                        enabled = state.canSave,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

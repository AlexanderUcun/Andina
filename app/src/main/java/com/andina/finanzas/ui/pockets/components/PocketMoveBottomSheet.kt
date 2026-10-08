package com.andina.finanzas.ui.pockets.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.view.HapticFeedbackConstants
import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.domain.model.Pocket
import com.andina.finanzas.domain.model.Wallet
import com.andina.finanzas.ui.pockets.PocketMoveType
import com.andina.finanzas.ui.theme.AndinaAmber
import com.andina.finanzas.ui.theme.AndinaDeepForest
import com.andina.finanzas.ui.theme.AndinaErrorRed
import com.andina.finanzas.ui.theme.AndinaMossGreen

/**
 * Modal BottomSheet para realizar Aportes o Retiros Atómicos a un Bolsillo.
 *
 * Características:
 * - Selección intuitiva de billetera origen/destino con saldo en tiempo real.
 * - Validación matemática contable inmediata contra saldos disponibles.
 * - Atajos rápidos de montos (+20k, +50k, +100k, meta restante).
 * - Teclado numérico táctil accesible con respuesta háptica.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PocketMoveBottomSheet(
    isVisible: Boolean,
    pocket: Pocket?,
    wallets: List<Wallet>,
    selectedWalletId: Long?,
    moveType: PocketMoveType,
    amountDigits: String,
    isMoving: Boolean,
    errorMessage: String?,
    canExecute: Boolean,
    onWalletSelected: (Long) -> Unit,
    onDigitClicked: (Char) -> Unit = {},
    onDeleteDigit: () -> Unit = {},
    onClearDigits: () -> Unit = {},
    onAmountDigitsChanged: (String) -> Unit = {},
    onQuickAmount: (Long) -> Unit,
    onConfirmMove: () -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    if (!isVisible || pocket == null) return

    val view = LocalView.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val currentAmount = Money(amountDigits.toLongOrNull() ?: 0L)
    val selectedWallet = wallets.find { it.id == selectedWalletId } ?: wallets.firstOrNull()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título e indicador de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (moveType == PocketMoveType.CONTRIBUTION) AndinaMossGreen.copy(alpha = 0.15f)
                            else AndinaAmber.copy(alpha = 0.15f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (moveType == PocketMoveType.CONTRIBUTION) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                        contentDescription = null,
                        tint = if (moveType == PocketMoveType.CONTRIBUTION) AndinaMossGreen else AndinaAmber,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = if (moveType == PocketMoveType.CONTRIBUTION) "Aportar a ${pocket.name}" else "Retirar de ${pocket.name}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = AndinaDeepForest
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selector de Billetera (Origen para aporte, Destino para retiro)
            Text(
                text = if (moveType == PocketMoveType.CONTRIBUTION) "Desde la billetera:" else "Hacia la billetera:",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = Color(0xFF6B7280),
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(wallets, key = { it.id }) { wallet ->
                    val isSelected = wallet.id == selectedWallet?.id
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) AndinaMossGreen else Color(0xFFE5E7EB),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                onWalletSelected(wallet.id)
                            },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) AndinaMossGreen.copy(alpha = 0.08f) else Color.White
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = if (isSelected) AndinaMossGreen else Color(0xFF6B7280),
                                modifier = Modifier.size(18.dp)
                            )
                            Column {
                                Text(
                                    text = wallet.name,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) AndinaDeepForest else Color(0xFF374151)
                                    )
                                )
                                Text(
                                    text = wallet.currentBalance.formatCop(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) AndinaMossGreen else Color(0xFF6B7280),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Monto Input con Teclado Numérico Nativo Android
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFF9FAFB)
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "MONTO A MOVER",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF9CA3AF),
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = amountDigits,
                        onValueChange = onAmountDigitsChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        textStyle = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = AndinaDeepForest,
                            fontSize = 32.sp,
                            textAlign = TextAlign.Center
                        ),
                        placeholder = {
                            Text(
                                text = "$ 0",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 30.sp,
                                    color = Color(0xFF9CA3AF).copy(alpha = 0.5f)
                                )
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (moveType == PocketMoveType.CONTRIBUTION) AndinaMossGreen else AndinaAmber,
                            unfocusedBorderColor = Color(0xFFE5E7EB)
                        )
                    )

                    // Previsualización de impacto
                    if (currentAmount.isPositive && selectedWallet != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        val impactText = when (moveType) {
                            PocketMoveType.CONTRIBUTION -> {
                                val remainingInWallet = selectedWallet.currentBalance - currentAmount
                                "Quedarán ${remainingInWallet.formatCop()} en ${selectedWallet.name}"
                            }
                            PocketMoveType.WITHDRAWAL -> {
                                val remainingInPocket = pocket.currentBalance - currentAmount
                                "Quedarán ${remainingInPocket.formatCop()} en ${pocket.name}"
                            }
                        }
                        Text(
                            text = impactText,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF6B7280),
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }

            // Mensaje de Error si aplica
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AndinaErrorRed.copy(alpha = 0.1f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.WarningAmber,
                        contentDescription = null,
                        tint = AndinaErrorRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AndinaErrorRed,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Atajos rápidos de dinero
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(20_000L, 50_000L, 100_000L).forEach { quickAmount ->
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                onQuickAmount(quickAmount)
                            },
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF3F4F6)
                    ) {
                        Text(
                            text = "+$${quickAmount / 1000}k",
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF374151)
                            )
                        )
                    }
                }

                // Atajo especial: Meta restante (para aporte) o Todo el saldo (para retiro)
                val specialAmount = if (moveType == PocketMoveType.CONTRIBUTION) pocket.remaining.pesos else pocket.currentBalance.pesos
                if (specialAmount > 0L) {
                    Surface(
                        modifier = Modifier
                            .weight(1.2f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                onQuickAmount(specialAmount)
                            },
                        shape = RoundedCornerShape(12.dp),
                        color = AndinaAmber.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (moveType == PocketMoveType.CONTRIBUTION) "Faltante" else "Todo",
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AndinaAmber
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botón de confirmación CTA
            Button(
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                    onConfirmMove()
                },
                enabled = canExecute && !isMoving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (moveType == PocketMoveType.CONTRIBUTION) AndinaMossGreen else AndinaAmber,
                    disabledContainerColor = Color(0xFFE5E7EB)
                )
            ) {
                if (isMoving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (moveType == PocketMoveType.CONTRIBUTION) "Confirmar Aporte" else "Confirmar Retiro",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}

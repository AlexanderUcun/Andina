package com.andina.finanzas.ui.register.components

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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.domain.model.Wallet
import com.andina.finanzas.domain.model.WalletType
import com.andina.finanzas.ui.theme.WalletBrandColors

/**
 * Carrusel interactivo de billeteras y cuentas con identidades de marca colombianas
 * y previsualización de saldo proyectado en tiempo real.
 */
@Composable
fun PaymentWalletSelector(
    wallets: List<Wallet>,
    selectedWalletId: Long?,
    projectedRemainingBalance: Money?,
    onWalletSelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
    labelTitle: String = "Método de pago"
) {
    val haptic = LocalHapticFeedback.current
    val isDark = isSystemInDarkTheme()

    val selectedWallet = wallets.find { it.id == selectedWalletId }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Título de la sección
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = labelTitle,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Previsualización en vivo: "Te quedarán $45.000 en Nequi"
            AnimatedVisibility(
                visible = selectedWallet != null && projectedRemainingBalance != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                if (selectedWallet != null && projectedRemainingBalance != null) {
                    val remainingColor = if (projectedRemainingBalance.isNegative) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    }

                    Text(
                        text = "Te quedarán ${projectedRemainingBalance.formatCop()} en ${selectedWallet.name}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        ),
                        color = remainingColor
                    )
                }
            }
        }

        // Carrusel horizontal de chips de billeteras
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(wallets, key = { it.id }) { wallet ->
                val isSelected = wallet.id == selectedWalletId
                val brandColor = getWalletColor(wallet.type)
                val icon = getWalletIcon(wallet.type)

                val cardBg = if (isSelected) {
                    if (isDark) brandColor.copy(alpha = 0.28f) else brandColor.copy(alpha = 0.12f)
                } else {
                    if (isDark) Color(0xFF1E211D) else Color(0xFFF1EEE8)
                }

                val borderColor = if (isSelected) brandColor else Color.Transparent

                Row(
                    modifier = Modifier
                        .height(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(cardBg)
                        .border(
                            width = if (isSelected) 1.5.dp else 0.dp,
                            color = borderColor,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable(
                            role = Role.RadioButton,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onWalletSelected(wallet.id)
                            }
                        )
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Círculo insignia con el color de marca
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(brandColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = brandColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = wallet.name,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = wallet.currentBalance.formatCop(),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (isSelected) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Seleccionado",
                            tint = brandColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun getWalletColor(type: WalletType): Color {
    return when (type) {
        WalletType.NEQUI -> WalletBrandColors.Nequi
        WalletType.DAVIPLATA -> WalletBrandColors.Daviplata
        WalletType.BANCOLOMBIA -> WalletBrandColors.Bancolombia
        WalletType.EFECTIVO -> WalletBrandColors.Cash
        WalletType.TARJETA_CREDITO -> WalletBrandColors.NuBank
        WalletType.OTRA -> Color(0xFF587550)
    }
}

private fun getWalletIcon(type: WalletType): ImageVector {
    return when (type) {
        WalletType.NEQUI, WalletType.DAVIPLATA -> Icons.Default.Smartphone
        WalletType.BANCOLOMBIA -> Icons.Default.AccountBalance
        WalletType.EFECTIVO -> Icons.Default.Payments
        WalletType.TARJETA_CREDITO -> Icons.Default.CreditCard
        WalletType.OTRA -> Icons.Default.Payments
    }
}

package com.andina.finanzas.ui.register.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.domain.model.Wallet
import com.andina.finanzas.domain.model.WalletType
import com.andina.finanzas.ui.components.WalletBrandGlyph
import com.andina.finanzas.ui.theme.LocalAndinaColors
import com.andina.finanzas.ui.theme.WalletBrandColors

/**
 * Selector horizontal compacto de billeteras para registro rápido con una sola mano.
 * Paleta armonizada con Andina Tokens y advertencia sutil sin sobrecargar.
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
    val colors = LocalAndinaColors.current

    val selectedWallet = wallets.find { it.id == selectedWalletId }
    val showWarning = selectedWallet != null &&
            projectedRemainingBalance != null &&
            projectedRemainingBalance.isNegative &&
            projectedRemainingBalance != selectedWallet.currentBalance

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Título de la sección
        Text(
            text = labelTitle,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            ),
            color = colors.textSecondary,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        // Advertencia compacta en una sola línea sutil
        AnimatedVisibility(
            visible = showWarning,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            if (selectedWallet != null && projectedRemainingBalance != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = colors.statusWarning.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(0.6.dp, colors.statusWarning.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = null,
                            tint = colors.statusWarning,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Quedarían ${projectedRemainingBalance.formatCop()} en ${selectedWallet.name}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            ),
                            color = colors.statusWarning
                        )
                    }
                }
            }
        }

        // Carrusel horizontal de chips de billeteras
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(wallets, key = { it.id }) { wallet ->
                val isSelected = wallet.id == selectedWalletId
                val brandColor = getWalletColor(wallet.type)

                val cardBg = if (isSelected) colors.accentGold.copy(alpha = 0.12f) else colors.canvasBottom
                val borderColor = if (isSelected) colors.accentGold else colors.cardBorder

                Row(
                    modifier = Modifier
                        .height(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(cardBg)
                        .border(
                            width = if (isSelected) 1.5.dp else 0.8.dp,
                            color = borderColor,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable(
                            role = Role.RadioButton,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onWalletSelected(wallet.id)
                            }
                        )
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Círculo insignia con glifo de marca oficial
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(brandColor.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        WalletBrandGlyph(
                            walletType = wallet.type,
                            tint = brandColor,
                            size = 15.dp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = wallet.name,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp
                            ),
                            color = colors.textPrimary
                        )
                        Text(
                            text = wallet.currentBalance.formatCop(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp
                            ),
                            color = colors.textSecondary
                        )
                    }

                    if (isSelected) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Seleccionado",
                            tint = colors.accentGold,
                            modifier = Modifier.size(14.dp)
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

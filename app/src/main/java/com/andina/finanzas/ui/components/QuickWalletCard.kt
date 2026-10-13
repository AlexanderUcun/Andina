package com.andina.finanzas.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.domain.model.Wallet
import com.andina.finanzas.domain.model.WalletType
import com.andina.finanzas.ui.theme.AndinaTheme
import com.andina.finanzas.ui.theme.CornerMedium
import com.andina.finanzas.ui.theme.WalletBrandColors

/**
 * Tarjeta de Billetera Oficial Andina (QuickWalletCard).
 *
 * Diseñada para carruseles horizontales con ancho ergonómico de 180dp y alto de 115dp.
 * Muestra el distintivo cromático de la entidad colombiana (Nequi, Daviplata, Bancolombia, etc.),
 * el nombre del método y el saldo reactivo disponible con números tabulares.
 *
 * @param wallet Entidad de dominio con los datos del saldo y marca.
 * @param onClick Acción opcional al tocar la tarjeta (navegación a movimientos filtrados o ajuste).
 * @param modifier Modificador Compose externo.
 */
@Composable
fun QuickWalletCard(
    wallet: Wallet,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val brandBadgeColor = resolveBrandColor(wallet.type, wallet.colorHex)
    val brandBadgeInitial = resolveBrandInitial(wallet.type, wallet.name)

    AndeanHyperCard(
        modifier = modifier
            .width(180.dp)
            .height(115.dp),
        variant = AndeanHyperCardVariant.CARD,
        contentPadding = 12.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Cabecera: Distintivo de Marca + Nombre
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Insignia / Gema de Cristal con el Glifo Oficial de la entidad
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    brandBadgeColor.copy(alpha = 0.25f),
                                    brandBadgeColor.copy(alpha = 0.08f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    WalletBrandGlyph(
                        walletType = wallet.type,
                        tint = brandBadgeColor,
                        size = 18.dp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Nombre de la billetera
                Text(
                    text = wallet.name,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Cuerpo: Subtítulo "Saldo disponible" y Cifra monetaria
            Column {
                Text(
                    text = "Saldo disponible",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = wallet.currentBalance.formatCop(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFeatureSettings = "tnum"
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Resuelve el color de acento característico de la billetera.
 */
private fun resolveBrandColor(type: WalletType, colorHex: String): Color {
    return when (type) {
        WalletType.NEQUI -> WalletBrandColors.Nequi
        WalletType.DAVIPLATA -> WalletBrandColors.Daviplata
        WalletType.BANCOLOMBIA -> WalletBrandColors.Bancolombia
        WalletType.EFECTIVO -> WalletBrandColors.Cash
        WalletType.TARJETA_CREDITO -> Color(0xFF1E88E5)
        WalletType.OTRA -> runCatching {
            Color(android.graphics.Color.parseColor(colorHex))
        }.getOrDefault(WalletBrandColors.Cash)
    }
}

/**
 * Resuelve la letra distintiva de la marca.
 */
private fun resolveBrandInitial(type: WalletType, name: String): String {
    return when (type) {
        WalletType.NEQUI -> "N"
        WalletType.DAVIPLATA -> "D"
        WalletType.BANCOLOMBIA -> "B"
        WalletType.EFECTIVO -> "$"
        WalletType.TARJETA_CREDITO -> "TC"
        WalletType.OTRA -> name.take(1).uppercase()
    }
}

@Preview(name = "QuickWalletCard Preview", showBackground = true)
@Composable
private fun QuickWalletCardPreview() {
    AndinaTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickWalletCard(
                wallet = Wallet(
                    id = 1,
                    name = "Nequi",
                    type = WalletType.NEQUI,
                    currentBalance = Money(450000L),
                    colorHex = "#700054"
                )
            )
            QuickWalletCard(
                wallet = Wallet(
                    id = 2,
                    name = "Efectivo",
                    type = WalletType.EFECTIVO,
                    currentBalance = Money(85000L),
                    colorHex = "#435C3C"
                )
            )
        }
    }
}

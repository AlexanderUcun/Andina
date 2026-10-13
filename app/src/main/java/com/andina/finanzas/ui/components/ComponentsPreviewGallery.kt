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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.domain.model.Pocket
import com.andina.finanzas.domain.model.Transaction
import com.andina.finanzas.domain.model.TransactionType
import com.andina.finanzas.domain.model.Wallet
import com.andina.finanzas.domain.model.WalletType
import com.andina.finanzas.ui.components.ecosystem.FrailejonView
import com.andina.finanzas.ui.components.ecosystem.WaterRippleOverlay
import com.andina.finanzas.ui.components.AndeanHyperCard
import com.andina.finanzas.ui.components.HyperCardVariant
import com.andina.finanzas.ui.theme.LocalAndinaColors
import com.andina.finanzas.ui.theme.AndeanMistGradient
import com.andina.finanzas.ui.theme.AndinaTheme

/**
 * Galería Maestra de Previsualización Visual (ComponentsPreviewGallery).
 *
 * Consolida todos los bloques de construcción visuales del sistema "Andean Mist & Moss":
 * 1. Vidrio Esmerilado (FrostedGlassCard).
 * 2. Botones Hápticos Accesibles (AndinaButton) en todas sus variantes.
 * 3. Tarjetas de Billeteras Colombianas (QuickWalletCard).
 * 4. Fila de Movimientos (TransactionItem) con etiquetas de gasto hormiga.
 * 5. Tarjetas de Metas de Ahorro (PocketProgressCard).
 * 6. Ecosistema Vivo en Canvas (FrailejonView y WaterRippleOverlay).
 */
@Composable
fun ComponentsPreviewGallery() {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AndeanMistGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Título de la galería
            Column {
                Text(
                    text = "Catálogo de Componentes",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Sistema de Diseño 'Andean Mist & Moss'",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.3f))

            // 1. AndeanHyperCard
            SectionTitle(title = "1. Tarjeta Insignia (AndeanHyperCard)")
            AndeanHyperCard {
                Text(
                    text = "Patrimonio Líquido Total",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$ 14.850.000 COP",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontFeatureSettings = "tnum"
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // 2. AndinaButton Variantes
            SectionTitle(title = "2. Botones Hápticos y Accesibles (AndinaButton)")
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AndinaButton(
                    text = "Guardar Gasto (CTA Principal)",
                    variant = AndinaButtonVariant.RegisterAction,
                    onClick = {}
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AndinaButton(
                        text = "Gasto Hormiga",
                        variant = AndinaButtonVariant.AntExpense,
                        modifier = Modifier.weight(1f),
                        onClick = {}
                    )
                    AndinaButton(
                        text = "Guardando...",
                        isSaving = true,
                        modifier = Modifier.weight(1f),
                        onClick = {}
                    )
                }
            }

            // 3. QuickWalletCard
            SectionTitle(title = "3. Billeteras Colombianas (QuickWalletCard)")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    QuickWalletCard(
                        wallet = Wallet(
                            id = 1,
                            name = "Nequi",
                            type = WalletType.NEQUI,
                            currentBalance = Money(850000L),
                            colorHex = "#700054"
                        )
                    )
                }
                item {
                    QuickWalletCard(
                        wallet = Wallet(
                            id = 2,
                            name = "Daviplata",
                            type = WalletType.DAVIPLATA,
                            currentBalance = Money(320000L),
                            colorHex = "#B32D2D"
                        )
                    )
                }
                item {
                    QuickWalletCard(
                        wallet = Wallet(
                            id = 3,
                            name = "Efectivo",
                            type = WalletType.EFECTIVO,
                            currentBalance = Money(140000L),
                            colorHex = "#435C3C"
                        )
                    )
                }
            }

            // 4. PocketProgressCard
            SectionTitle(title = "4. Metas de Ahorro (PocketProgressCard)")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    PocketProgressCard(
                        pocket = Pocket(
                            id = 1,
                            name = "Fondo Emergencia",
                            goal = Money(3000000L),
                            currentBalance = Money(2250000L)
                        )
                    )
                }
                item {
                    PocketProgressCard(
                        pocket = Pocket(
                            id = 2,
                            name = "Viaje a Chingaza",
                            goal = Money(800000L),
                            currentBalance = Money(800000L)
                        )
                    )
                }
            }

            // 5. TransactionItem
            SectionTitle(title = "5. Movimientos Financieros (TransactionItem)")
            AndeanHyperCard(contentPadding = 4.dp) {
                TransactionItem(
                    transaction = Transaction(
                        id = 1,
                        type = TransactionType.GASTO,
                        amount = Money(8500L),
                        walletId = 1,
                        note = "Café con almojábana",
                        occurredAt = System.currentTimeMillis(),
                        isAntExpense = true,
                        walletName = "Nequi",
                        categoryName = "Snacks",
                        categoryIcon = "coffee"
                    )
                )
                HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.15f))
                TransactionItem(
                    transaction = Transaction(
                        id = 2,
                        type = TransactionType.INGRESO,
                        amount = Money(2400000L),
                        walletId = 2,
                        note = "Salario Mensual",
                        occurredAt = System.currentTimeMillis() - 86400000L,
                        walletName = "Bancolombia"
                    )
                )
            }

            // 6. Ecosistema Vivo en Canvas
            SectionTitle(title = "6. Ecosistema Vivo: Frailejón Andino")
            AndeanHyperCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FrailejonView(progress = 0.20f, size = 90.dp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "20% (Semilla)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FrailejonView(progress = 0.60f, size = 90.dp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "60% (Crecimiento)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FrailejonView(progress = 1.0f, size = 90.dp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "100% (Flor)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        ),
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Preview(name = "Andina Components Gallery - Light", showBackground = true)
@Composable
private fun ComponentsPreviewGalleryLightPreview() {
    AndinaTheme(darkTheme = false) {
        ComponentsPreviewGallery()
    }
}

@Preview(name = "Andina Components Gallery - Dark", showBackground = true)
@Composable
private fun ComponentsPreviewGalleryDarkPreview() {
    AndinaTheme(darkTheme = true) {
        ComponentsPreviewGallery()
    }
}

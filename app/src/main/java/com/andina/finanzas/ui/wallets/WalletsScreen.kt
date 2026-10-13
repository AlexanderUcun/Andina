package com.andina.finanzas.ui.wallets

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import com.andina.finanzas.ui.components.AndeanHyperCard
import com.andina.finanzas.ui.components.HyperCardVariant
import com.andina.finanzas.ui.theme.LocalAndinaColors
import com.andina.finanzas.ui.components.WalletBrandGlyph
import com.andina.finanzas.ui.components.bouncyClick
import com.andina.finanzas.ui.theme.specularGlassBorder
import com.andina.finanzas.ui.theme.AndeanMistCanvasBottom
import com.andina.finanzas.ui.theme.AndeanMistCanvasTop
import com.andina.finanzas.ui.theme.AndeanSlateInk
import com.andina.finanzas.ui.theme.FrailejonGold
import androidx.compose.ui.graphics.Brush
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.andina.finanzas.domain.model.Wallet
import com.andina.finanzas.domain.model.WalletType
import com.andina.finanzas.ui.components.AndinaButton
import com.andina.finanzas.ui.theme.AndeanPrimary
import com.andina.finanzas.ui.wallets.components.AddWalletModal
import java.text.NumberFormat
import java.util.Locale

import com.andina.finanzas.ui.pockets.PocketsAction
import com.andina.finanzas.ui.pockets.PocketsViewModel

/**
 * Pantalla Principal de Patrimonio (Billeteras y Bolsillos & Metas - Módulo 3 / Fase 2.1).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletsScreen(
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier,
    initialPatrimonioTab: PatrimonioTab = PatrimonioTab.BILLETERAS,
    viewModel: WalletsViewModel = hiltViewModel(),
    pocketsViewModel: PocketsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(initialPatrimonioTab) {
        if (state.selectedPatrimonioTab != initialPatrimonioTab) {
            viewModel.onEvent(WalletsEvent.OnSelectPatrimonioTab(initialPatrimonioTab))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is WalletsEffect.ShowSnackbar -> onShowSnackbar(effect.message)
            }
        }
    }

    var selectedWalletForDetail by remember { mutableStateOf<Wallet?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Patrimonio",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AndeanSlateInk
                            )
                        )

                        // Segmented bar [ Billeteras | Bolsillos ]
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Row(modifier = Modifier.padding(3.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(9.dp),
                                    color = if (state.selectedPatrimonioTab == PatrimonioTab.BILLETERAS) AndeanPrimary else Color.Transparent,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(9.dp))
                                        .clickable { viewModel.onEvent(WalletsEvent.OnSelectPatrimonioTab(PatrimonioTab.BILLETERAS)) }
                                ) {
                                    Text(
                                        text = "💳 Cuentas",
                                        fontSize = 11.sp,
                                        fontWeight = if (state.selectedPatrimonioTab == PatrimonioTab.BILLETERAS) FontWeight.Bold else FontWeight.Medium,
                                        color = if (state.selectedPatrimonioTab == PatrimonioTab.BILLETERAS) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(9.dp),
                                    color = if (state.selectedPatrimonioTab == PatrimonioTab.BOLSILLOS) AndeanPrimary else Color.Transparent,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(9.dp))
                                        .clickable { viewModel.onEvent(WalletsEvent.OnSelectPatrimonioTab(PatrimonioTab.BOLSILLOS)) }
                                ) {
                                    Text(
                                        text = "🌱 Metas",
                                        fontSize = 11.sp,
                                        fontWeight = if (state.selectedPatrimonioTab == PatrimonioTab.BOLSILLOS) FontWeight.Bold else FontWeight.Medium,
                                        color = if (state.selectedPatrimonioTab == PatrimonioTab.BOLSILLOS) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (state.selectedPatrimonioTab == PatrimonioTab.BILLETERAS) {
                                viewModel.onEvent(WalletsEvent.OnOpenAddWallet)
                            } else {
                                pocketsViewModel.processAction(PocketsAction.OpenCreatePocket)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = if (state.selectedPatrimonioTab == PatrimonioTab.BILLETERAS) "Añadir billetera" else "Crear meta de bolsillo",
                            tint = AndeanPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        val colors = LocalAndinaColors.current
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.canvasGradient)
        ) {
            if (state.selectedPatrimonioTab == PatrimonioTab.BOLSILLOS) {
                com.andina.finanzas.ui.pockets.PocketsScreen(
                    onShowSnackbar = onShowSnackbar,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    viewModel = pocketsViewModel
                )
            } else if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colors.accentGold)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // 1. Tarjeta Resumen de Saldo Consolidado
                    item {
                        AndeanHyperCard(
                            modifier = Modifier.fillMaxWidth(),
                            variant = HyperCardVariant.HERO,
                            contentPadding = 22.dp
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Saldo Total en Billeteras",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = colors.textSecondary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = formatPesos(state.totalBalance),
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 32.sp,
                                        fontFeatureSettings = "tnum"
                                    ),
                                    color = colors.textMoneyDisplay
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "${state.wallets.size} cuentas activas · ${state.archivedWallets.size} archivadas",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.textSecondary
                                )
                            }
                        }
                    }

                    // 2. Selector de Pestañas: Activas vs Archivadas
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            androidx.compose.material3.FilterChip(
                                selected = state.selectedTab == WalletsTab.ACTIVAS,
                                onClick = { viewModel.onEvent(WalletsEvent.OnSelectTab(WalletsTab.ACTIVAS)) },
                                label = { Text("Activas (${state.wallets.size})", fontWeight = FontWeight.SemiBold) },
                                colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = colors.accentGold.copy(alpha = 0.16f),
                                    selectedLabelColor = colors.accentGold
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            androidx.compose.material3.FilterChip(
                                selected = state.selectedTab == WalletsTab.ARCHIVADAS,
                                onClick = { viewModel.onEvent(WalletsEvent.OnSelectTab(WalletsTab.ARCHIVADAS)) },
                                label = { Text("Archivadas (${state.archivedWallets.size})", fontWeight = FontWeight.SemiBold) },
                                colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = colors.accentGold.copy(alpha = 0.16f),
                                    selectedLabelColor = colors.accentGold
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    // 3. Lista de Billeteras según la pestaña seleccionada
                    val currentList = if (state.selectedTab == WalletsTab.ACTIVAS) state.wallets else state.archivedWallets

                    if (currentList.isEmpty()) {
                        item {
                            AndeanHyperCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                variant = HyperCardVariant.CARD,
                                contentPadding = 28.dp
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = if (state.selectedTab == WalletsTab.ACTIVAS) Icons.Default.AccountBalanceWallet else Icons.Default.Inventory2,
                                        contentDescription = null,
                                        tint = colors.accentGold,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = if (state.selectedTab == WalletsTab.ACTIVAS) "No tienes cuentas activas" else "No hay cuentas archivadas",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = colors.textPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (state.selectedTab == WalletsTab.ACTIVAS) "Pulsa el botón + para registrar una nueva cuenta." else "Las cuentas que archives aparecerán aquí para restaurarlas cuando quieras.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = colors.textSecondary,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else {
                        items(currentList, key = { it.id }) { wallet ->
                            WalletItemCard(
                                wallet = wallet,
                                isArchived = state.selectedTab == WalletsTab.ARCHIVADAS,
                                onClick = { selectedWalletForDetail = wallet },
                                onEdit = { viewModel.onEvent(WalletsEvent.OnOpenEditWallet(wallet)) },
                                onArchive = { viewModel.onEvent(WalletsEvent.OnArchiveWallet(wallet.id)) },
                                onUnarchive = { viewModel.onEvent(WalletsEvent.OnUnarchiveWallet(wallet.id)) },
                                onDelete = { viewModel.onEvent(WalletsEvent.OnRequestDeleteWallet(wallet)) }
                            )
                        }
                    }

                    // Espaciador inferior para la barra de navegación
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    // Modal de Detalle de Billetera
    selectedWalletForDetail?.let { wallet ->
        androidx.compose.material3.ModalBottomSheet(
            onDismissRequest = { selectedWalletForDetail = null },
            sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = AndeanMistCanvasTop
        ) {
            val colors = LocalAndinaColors.current
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val detailColor = try {
                    Color(android.graphics.Color.parseColor(wallet.colorHex))
                } catch (_: Exception) {
                    colors.accentGold
                }

                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(detailColor.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    WalletBrandGlyph(
                        walletType = wallet.type,
                        tint = detailColor,
                        size = 46.dp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = wallet.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = AndeanSlateInk
                    )
                )

                Text(
                    text = when (wallet.type) {
                        WalletType.EFECTIVO -> "Efectivo en Mano"
                        WalletType.NEQUI -> "Billetera Digital Nequi"
                        WalletType.DAVIPLATA -> "Billetera Digital Daviplata"
                        WalletType.BANCOLOMBIA -> "Cuenta Bancaria Bancolombia"
                        WalletType.TARJETA_CREDITO -> "Tarjeta de Crédito"
                        WalletType.OTRA -> "Cuenta Local"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = AndeanSlateInk.copy(alpha = 0.65f)
                )

                Spacer(modifier = Modifier.height(14.dp))

                AndeanHyperCard(
                    modifier = Modifier.fillMaxWidth(),
                    variant = HyperCardVariant.CARD,
                    contentPadding = 16.dp
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Saldo Disponible",
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatPesos(wallet.currentBalance.pesos),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontFeatureSettings = "tnum"
                            ),
                            color = if (wallet.currentBalance.pesos < 0) colors.statusDanger else colors.textMoneyDisplay
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Acciones Rápidas del Detalle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    androidx.compose.material3.OutlinedButton(
                        onClick = {
                            selectedWalletForDetail = null
                            viewModel.onEvent(WalletsEvent.OnOpenEditWallet(wallet))
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Cuadrar Saldo", fontSize = 12.sp)
                    }

                    androidx.compose.material3.Button(
                        onClick = {
                            selectedWalletForDetail = null
                            viewModel.onEvent(WalletsEvent.OnOpenEditWallet(wallet))
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = AndeanPrimary)
                    ) {
                        Text("Editar Cuenta", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Modal de Creación / Edición
    if (state.showAddEditDialog) {
        AddWalletModal(
            editingWallet = state.editingWallet,
            isSaving = state.isSaving,
            onConfirm = { name, type, initialBalance, colorHex ->
                viewModel.onEvent(
                    WalletsEvent.OnSaveWallet(
                        name = name,
                        type = type,
                        initialBalancePesos = initialBalance,
                        colorHex = colorHex
                    )
                )
            },
            onDismiss = { viewModel.onEvent(WalletsEvent.OnDismissDialog) }
        )
    }

    // Diálogo de Confirmación para Eliminar Definitivamente
    state.walletToDelete?.let { walletToDelete ->
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(WalletsEvent.OnDismissDeleteDialog) },
            title = {
                Text(
                    text = "Eliminar Cuenta Definitivamente",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = "¿Confirmas que deseas eliminar la cuenta '${walletToDelete.name}'? Si tiene transacciones asociadas, considera archivarla en su lugar para preservar tu historial contable."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.onEvent(WalletsEvent.OnConfirmDeleteWallet(walletToDelete.id)) }
                ) {
                    Text("Eliminar Definitivamente", fontWeight = FontWeight.Bold, color = Color(0xFFBA1A1A))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.onEvent(WalletsEvent.OnDismissDeleteDialog) }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun WalletItemCard(
    wallet: Wallet,
    isArchived: Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onArchive: () -> Unit,
    onUnarchive: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val walletColor = try {
        Color(android.graphics.Color.parseColor(wallet.colorHex))
    } catch (_: Exception) {
        AndeanPrimary
    }

    val categorySubtitle = when (wallet.type) {
        WalletType.EFECTIVO -> "Efectivo en Mano"
        WalletType.NEQUI -> "Billetera Digital"
        WalletType.DAVIPLATA -> "Billetera Digital"
        WalletType.BANCOLOMBIA -> "Cuenta Bancaria"
        WalletType.TARJETA_CREDITO -> "Tarjeta de Crédito"
        WalletType.OTRA -> "Cuenta Local"
    }

    AndeanHyperCard(
        modifier = Modifier.fillMaxWidth(),
        variant = HyperCardVariant.CARD,
        contentPadding = 16.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                WalletBrandGlyph(
                    walletType = wallet.type,
                    tint = walletColor,
                    size = 46.dp
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = wallet.name,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = AndeanSlateInk
                    )
                    Text(
                        text = if (isArchived) "$categorySubtitle · Archivada" else categorySubtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isArchived) Color(0xFFBA1A1A).copy(alpha = 0.8f) else AndeanSlateInk.copy(alpha = 0.65f)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatPesos(wallet.currentBalance.pesos),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontFeatureSettings = "tnum"
                        ),
                        color = if (wallet.currentBalance.pesos < 0) Color(0xFFBA1A1A) else MaterialTheme.colorScheme.onSurface
                    )
                }

                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Opciones",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Editar Billetera") },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                            },
                            onClick = {
                                menuExpanded = false
                                onEdit()
                            }
                        )
                        if (!isArchived) {
                            DropdownMenuItem(
                                text = { Text("Archivar Cuenta") },
                                leadingIcon = {
                                    Icon(Icons.Default.Inventory2, contentDescription = null, modifier = Modifier.size(18.dp))
                                },
                                onClick = {
                                    menuExpanded = false
                                    onArchive()
                                }
                            )
                        } else {
                            DropdownMenuItem(
                                text = { Text("Restaurar a Activas") },
                                leadingIcon = {
                                    Icon(Icons.Default.Unarchive, contentDescription = null, modifier = Modifier.size(18.dp))
                                },
                                onClick = {
                                    menuExpanded = false
                                    onUnarchive()
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Eliminar Definitivamente", color = Color(0xFFBA1A1A)) },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFBA1A1A), modifier = Modifier.size(18.dp))
                            },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun formatPesos(amount: Long): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
    format.maximumFractionDigits = 0
    return format.format(amount)
}

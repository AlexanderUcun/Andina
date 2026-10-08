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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Savings
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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

/**
 * Pantalla Principal de Billeteras y Cuentas (Módulo 3 / Sub-fase 3.2).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletsScreen(
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WalletsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

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
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Billeteras & Cuentas",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(WalletsEvent.OnOpenAddWallet) }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Añadir billetera",
                            tint = AndeanPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Tarjeta Resumen de Saldo Consolidado
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Saldo Total en Billeteras",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = formatPesos(state.totalBalance),
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 32.sp,
                                    fontFeatureSettings = "tnum"
                                ),
                                color = AndeanPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${state.wallets.size} cuentas activas locales",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // 2. Lista de Billeteras
                items(state.wallets, key = { it.id }) { wallet ->
                    WalletItemCard(
                        wallet = wallet,
                        onClick = { selectedWalletForDetail = wallet },
                        onEdit = { viewModel.onEvent(WalletsEvent.OnOpenEditWallet(wallet)) },
                        onArchive = { viewModel.onEvent(WalletsEvent.OnArchiveWallet(wallet.id)) }
                    )
                }

                // Espaciador inferior para la barra de navegación
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // Modal de Detalle de Billetera
    selectedWalletForDetail?.let { wallet ->
        androidx.compose.material3.ModalBottomSheet(
            onDismissRequest = { selectedWalletForDetail = null },
            sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
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
                    AndeanPrimary
                }

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(detailColor.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (wallet.type) {
                            WalletType.EFECTIVO -> Icons.Default.Payments
                            WalletType.TARJETA_CREDITO -> Icons.Default.CreditCard
                            WalletType.BANCOLOMBIA -> Icons.Default.AccountBalance
                            WalletType.NEQUI -> Icons.Default.AccountBalanceWallet
                            WalletType.DAVIPLATA -> Icons.Default.Savings
                            WalletType.OTRA -> Icons.Default.AccountBalanceWallet
                        },
                        contentDescription = null,
                        tint = detailColor,
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = wallet.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Saldo Disponible",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatPesos(wallet.currentBalance.pesos),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontFeatureSettings = "tnum"
                            ),
                            color = if (wallet.currentBalance.pesos < 0) Color(0xFFBA1A1A) else AndeanPrimary
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
}

@Composable
private fun WalletItemCard(
    wallet: Wallet,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onArchive: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val walletColor = try {
        Color(android.graphics.Color.parseColor(wallet.colorHex))
    } catch (_: Exception) {
        AndeanPrimary
    }

    val icon = when (wallet.type) {
        WalletType.EFECTIVO -> Icons.Default.Payments
        WalletType.TARJETA_CREDITO -> Icons.Default.CreditCard
        WalletType.BANCOLOMBIA -> Icons.Default.AccountBalance
        WalletType.NEQUI -> Icons.Default.AccountBalanceWallet
        WalletType.DAVIPLATA -> Icons.Default.Savings
        WalletType.OTRA -> Icons.Default.AccountBalanceWallet
    }

    val categorySubtitle = when (wallet.type) {
        WalletType.EFECTIVO -> "Efectivo en Mano"
        WalletType.NEQUI -> "Billetera Digital"
        WalletType.DAVIPLATA -> "Billetera Digital"
        WalletType.BANCOLOMBIA -> "Cuenta Bancaria"
        WalletType.TARJETA_CREDITO -> "Tarjeta de Crédito"
        WalletType.OTRA -> "Cuenta Local"
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(walletColor.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = walletColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = wallet.name,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = categorySubtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                            onClick = {
                                menuExpanded = false
                                onEdit()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Archivar Cuenta") },
                            onClick = {
                                menuExpanded = false
                                onArchive()
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

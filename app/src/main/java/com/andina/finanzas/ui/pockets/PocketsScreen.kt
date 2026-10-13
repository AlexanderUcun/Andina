package com.andina.finanzas.ui.pockets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.view.HapticFeedbackConstants
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andina.finanzas.domain.model.Pocket
import com.andina.finanzas.ui.components.ecosystem.FrailejonView
import com.andina.finanzas.ui.pockets.components.PocketCard
import com.andina.finanzas.ui.pockets.components.PocketFormBottomSheet
import com.andina.finanzas.ui.pockets.components.PocketMoveBottomSheet
import com.andina.finanzas.ui.pockets.components.PocketsHeroCard
import com.andina.finanzas.ui.theme.AndinaAmber
import com.andina.finanzas.ui.theme.AndinaDeepForest
import com.andina.finanzas.ui.theme.AndinaMossGreen

/**
 * Pantalla principal del módulo de Bolsillos y Metas de Ahorro Programado.
 */
@Composable
fun PocketsScreen(
    onShowSnackbar: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: PocketsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val view = LocalView.current

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is PocketsEffect.ShowSnackbar -> onShowSnackbar(effect.message)
                is PocketsEffect.PlayCoinSound -> {
                    view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                }
                is PocketsEffect.ShowCelebrationConfetti -> {
                    view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                }
                is PocketsEffect.NavigateToDetail -> {
                    // Detalle interno manejado en pantalla
                }
            }
        }
    }

    // Si hay un bolsillo seleccionado, mostramos la pantalla de detalle
    val detailPocket = state.activePocketForDetail
    if (detailPocket != null) {
        PocketDetailScreen(
            pocket = detailPocket,
            onBackClick = { viewModel.processAction(PocketsAction.ClearSelectedPocket) },
            onContributeClick = { viewModel.processAction(PocketsAction.OpenContribute(detailPocket.id)) },
            onWithdrawClick = { viewModel.processAction(PocketsAction.OpenWithdraw(detailPocket.id)) }
        )
    } else {
        PocketsDashboardContent(
            state = state,
            onAction = { viewModel.processAction(it) },
            modifier = modifier
        )
    }

    // Modal de Aporte / Retiro Atómico
    PocketMoveBottomSheet(
        isVisible = state.isMoveSheetVisible,
        pocket = state.selectedPocket,
        wallets = state.wallets,
        selectedWalletId = state.moveWalletId,
        moveType = state.moveType,
        amountDigits = state.moveAmountDigits,
        isMoving = state.isMoving,
        errorMessage = state.moveError,
        canExecute = state.canExecuteMove,
        onWalletSelected = { viewModel.processAction(PocketsAction.SelectMoveWallet(it)) },
        onAmountDigitsChanged = { viewModel.processAction(PocketsAction.UpdateMoveAmountDigits(it)) },
        onQuickAmount = { viewModel.processAction(PocketsAction.SetMoveAmount(it)) },
        onConfirmMove = { viewModel.processAction(PocketsAction.ExecuteMove) },
        onDismiss = { viewModel.processAction(PocketsAction.DismissMoveSheet) }
    )

    // Modal de Creación de Bolsillo
    PocketFormBottomSheet(
        isVisible = state.isCreateSheetVisible,
        onDismiss = { viewModel.processAction(PocketsAction.DismissCreatePocket) },
        onCreatePocket = { name, goalAmount, icon, colorHex ->
            viewModel.processAction(
                PocketsAction.CreatePocket(
                    name = name,
                    goalAmount = goalAmount,
                    icon = icon,
                    colorHex = colorHex
                )
            )
        }
    )

    // Diálogo Festivo de Cumplimiento de Meta (100%)
    state.celebrationPocket?.let { completedPocket ->
        PocketCelebrationDialog(
            pocket = completedPocket,
            onDismiss = { viewModel.processAction(PocketsAction.DismissCelebration) }
        )
    }

    // Diálogo de Edición de Meta
    state.editingPocket?.let { pocketToEdit ->
        var editName by remember(pocketToEdit) { mutableStateOf(pocketToEdit.name) }
        var editGoal by remember(pocketToEdit) { mutableStateOf(pocketToEdit.goal.pesos.toString()) }

        AlertDialog(
            onDismissRequest = { viewModel.processAction(PocketsAction.DismissEditPocket) },
            title = {
                Text(
                    text = "Editar Meta del Bolsillo",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    androidx.compose.material3.OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Nombre de la meta") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    androidx.compose.material3.OutlinedTextField(
                        value = editGoal,
                        onValueChange = { editGoal = it.filter { ch -> ch.isDigit() }.take(8) },
                        label = { Text("Meta en COP") },
                        prefix = { Text("$ ") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        val parsedGoal = editGoal.toLongOrNull() ?: pocketToEdit.goal.pesos
                        if (editName.isNotBlank() && parsedGoal > 0) {
                            viewModel.processAction(
                                PocketsAction.UpdatePocket(
                                    pocketToEdit.copy(
                                        name = editName.trim(),
                                        goal = com.andina.finanzas.domain.model.Money(parsedGoal)
                                    )
                                )
                            )
                        }
                    }
                ) {
                    Text("Guardar", fontWeight = FontWeight.Bold, color = AndinaMossGreen)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = { viewModel.processAction(PocketsAction.DismissEditPocket) }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo de Confirmación para Eliminar Bolsillo
    state.pocketToDelete?.let { pocketToDelete ->
        AlertDialog(
            onDismissRequest = { viewModel.processAction(PocketsAction.DismissDeletePocket) },
            title = {
                Text(
                    text = "Eliminar Meta de Bolsillo",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = "¿Confirmas que deseas eliminar el bolsillo '${pocketToDelete.name}'? Tu saldo reservado de ${pocketToDelete.currentBalance.formatCop()} permanecerá a salvo en tus cuentas."
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = { viewModel.processAction(PocketsAction.ConfirmDeletePocket(pocketToDelete.id)) }
                ) {
                    Text("Eliminar Definitivamente", fontWeight = FontWeight.Bold, color = Color(0xFFBA1A1A))
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = { viewModel.processAction(PocketsAction.DismissDeletePocket) }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
internal fun PocketsDashboardContent(
    state: PocketsUiState,
    onAction: (PocketsAction) -> Unit,
    modifier: Modifier = Modifier,
    showHeader: Boolean = true
) {
    var selectedFilter by remember { mutableStateOf(PocketFilter.ALL) }

    val filteredPockets = remember(state.pockets, selectedFilter) {
        when (selectedFilter) {
            PocketFilter.ALL -> state.pockets
            PocketFilter.IN_PROGRESS -> state.pockets.filter { !it.isCompleted }
            PocketFilter.COMPLETED -> state.pockets.filter { it.isCompleted }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF5F3EF),
                        Color(0xFFEBE6DC)
                    )
                )
            ),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Cabecera Editorial
        if (showHeader) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Bolsillos & Metas",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = AndinaDeepForest
                            )
                        )
                        Text(
                            text = "Ahorros protegidos que crecen en tu páramo",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF6B7280)
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Savings,
                            contentDescription = null,
                            tint = AndinaMossGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // Hero Card de Ahorro Consolidado
        item {
            PocketsHeroCard(
                totalReserved = state.totalReserved,
                totalGoals = state.totalGoals,
                overallProgress = state.overallProgress,
                onCreatePocketClick = { onAction(PocketsAction.OpenCreatePocket) }
            )
        }

        // Píldoras de Filtro
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(PocketFilter.entries) { filter ->
                    val isSelected = filter == selectedFilter
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedFilter = filter },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) AndinaDeepForest else Color.White
                    ) {
                        Text(
                            text = when (filter) {
                                PocketFilter.ALL -> "Todas (${state.pockets.size})"
                                PocketFilter.IN_PROGRESS -> "En Crecimiento (${state.pockets.count { !it.isCompleted }})"
                                PocketFilter.COMPLETED -> "Completadas (${state.pockets.count { it.isCompleted }})"
                            },
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFF4B5563)
                            )
                        )
                    }
                }
            }
        }

        // Lista de Bolsillos o Estado Vacío
        if (filteredPockets.isEmpty()) {
            item {
                PocketsEmptyState(
                    onCreateClick = { onAction(PocketsAction.OpenCreatePocket) }
                )
            }
        } else {
            items(filteredPockets, key = { it.id }) { pocket ->
                PocketCard(
                    pocket = pocket,
                    onClick = { onAction(PocketsAction.SelectPocket(pocket.id)) },
                    onContributeClick = { onAction(PocketsAction.OpenContribute(pocket.id)) },
                    onWithdrawClick = { onAction(PocketsAction.OpenWithdraw(pocket.id)) },
                    onEditClick = { onAction(PocketsAction.OpenEditPocket(pocket)) },
                    onReminderClick = { onAction(PocketsAction.ScheduleReminder(pocket.name)) },
                    onDeleteClick = { onAction(PocketsAction.RequestDeletePocket(pocket)) }
                )
            }
        }

        // Espaciador inferior para navegación
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

enum class PocketFilter {
    ALL,
    IN_PROGRESS,
    COMPLETED
}

@Composable
private fun PocketsEmptyState(onCreateClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(AndinaMossGreen.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                FrailejonView(progress = 0.1f, size = 80.dp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tu páramo espera su primera meta",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = AndinaDeepForest
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Crea bolsillos para resguardar dinero para vacaciones, emergencias o compras grandes sin tocar tu saldo del día a día.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF6B7280),
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onCreateClick,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AndinaMossGreen)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Sembrar Mi Primer Bolsillo", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PocketCelebrationDialog(
    pocket: Pocket,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(28.dp),
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(AndinaAmber.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = AndinaAmber,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "¡META FLORECIDA AL 100%!",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = AndinaDeepForest,
                        textAlign = TextAlign.Center
                    )
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "¡Felicitaciones! Has completado con éxito tu meta '${pocket.name}' reuniendo ${pocket.goal.formatCop()}.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF4B5563),
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                FrailejonView(progress = 1.0f, size = 110.dp)

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tu frailejón ha desplegado su flor dorada en la cima del páramo 🌿✨",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = AndinaMossGreen,
                        textAlign = TextAlign.Center
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AndinaMossGreen)
            ) {
                Text("¡Celebrar con Muggy!", fontWeight = FontWeight.Bold)
            }
        }
    )
}

package com.andina.finanzas.ui.recurring

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.ui.components.muggy.MuggyPose
import com.andina.finanzas.ui.components.muggy.MuggySpeechBubble
import com.andina.finanzas.ui.theme.AndeanAntExpenseAmber
import com.andina.finanzas.ui.theme.AndeanPrimary

enum class RecurringType {
    INGRESO,
    GASTO
}

data class RecurringTransactionItem(
    val id: String,
    val title: String,
    val amount: Long,
    val type: RecurringType,
    val dayOfMonth: Int,
    val walletName: String,
    val isAutoPromptEnabled: Boolean = true,
    var isConfirmedThisCycle: Boolean = false
)

/**
 * Gestor de Gastos e Ingresos Fijos Programables con Autorización (Módulo 8).
 * La app NUNCA abona dinero a ciegas; requiere confirmación previa con opción a editar monto/fecha.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringTransactionsScreen(
    onNavigateBack: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var recurringList by remember {
        mutableStateOf(
            listOf(
                RecurringTransactionItem(
                    id = "1",
                    title = "Salario Quincenal",
                    amount = 2_500_000L,
                    type = RecurringType.INGRESO,
                    dayOfMonth = 15,
                    walletName = "Bancolombia",
                    isAutoPromptEnabled = true,
                    isConfirmedThisCycle = false
                ),
                RecurringTransactionItem(
                    id = "2",
                    title = "Arriendo Apartamento",
                    amount = 1_200_000L,
                    type = RecurringType.GASTO,
                    dayOfMonth = 5,
                    walletName = "Bancolombia",
                    isAutoPromptEnabled = true,
                    isConfirmedThisCycle = true
                ),
                RecurringTransactionItem(
                    id = "3",
                    title = "Servicios Públicos (Agua/Luz/Gas)",
                    amount = 180_000L,
                    type = RecurringType.GASTO,
                    dayOfMonth = 18,
                    walletName = "Nequi",
                    isAutoPromptEnabled = true,
                    isConfirmedThisCycle = false
                ),
                RecurringTransactionItem(
                    id = "4",
                    title = "Suscripción Spotify & Netflix",
                    amount = 65_000L,
                    type = RecurringType.GASTO,
                    dayOfMonth = 24,
                    walletName = "Tarjeta Débito",
                    isAutoPromptEnabled = true,
                    isConfirmedThisCycle = false
                )
            )
        )
    }

    var selectedItemForEdit by remember { mutableStateOf<RecurringTransactionItem?>(null) }
    var editAmountInput by remember { mutableStateOf("") }
    var editDayInput by remember { mutableStateOf("") }
    var showNewModal by remember { mutableStateOf(false) }

    // Nuevo item temporal
    var newTitle by remember { mutableStateOf("") }
    var newAmount by remember { mutableStateOf("") }
    var newDay by remember { mutableStateOf("15") }
    var newType by remember { mutableStateOf(RecurringType.GASTO) }
    var newWallet by remember { mutableStateOf("Bancolombia") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Movimientos Programables",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showNewModal = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Nuevo programable",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Mensaje de Muggy explicando la regla de oro: CERO abonos a ciegas
            item {
                MuggySpeechBubble(
                    title = "Autorización Inteligente",
                    greeting = "Regla de Oro Andina 🛡️",
                    text = "Tus ingresos y gastos fijos nunca se aplicarán solos sin tu visto bueno. Al llegar la fecha, te pediré confirmar el monto exacto antes de mover tu saldo.",
                    pose = MuggyPose.SERENE
                )
            }

            // 2. Alertas pendientes de confirmación para este ciclo
            val pendingItems = recurringList.filter { !it.isConfirmedThisCycle }
            if (pendingItems.isNotEmpty()) {
                item {
                    Text(
                        text = "Pendientes de Confirmación este Ciclo",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                items(pendingItems, key = { it.id }) { item ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f).padding(end = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (item.type == RecurringType.INGRESO) AndeanPrimary.copy(alpha = 0.15f)
                                                else AndeanAntExpenseAmber.copy(alpha = 0.15f)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (item.type == RecurringType.INGRESO) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                            contentDescription = null,
                                            tint = if (item.type == RecurringType.INGRESO) AndeanPrimary else AndeanAntExpenseAmber,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f, fill = false)) {
                                        Text(
                                            text = item.title,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Día ${item.dayOfMonth} · ${item.walletName}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Text(
                                    text = Money(item.amount).formatCop(),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    maxLines = 1,
                                    softWrap = false,
                                    color = if (item.type == RecurringType.INGRESO) AndeanPrimary else Color(0xFFB55800)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        selectedItemForEdit = item
                                        editAmountInput = item.amount.toString()
                                        editDayInput = item.dayOfMonth.toString()
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Editar", fontSize = 12.sp)
                                }

                                Button(
                                    onClick = {
                                        recurringList = recurringList.map {
                                            if (it.id == item.id) it.copy(isConfirmedThisCycle = true) else it
                                        }
                                        onShowSnackbar("¡${item.title} confirmado e ingresado al Libro Mayor!")
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AndeanPrimary),
                                    modifier = Modifier.weight(1.3f)
                                ) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Confirmar", fontSize = 12.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            // 3. Todos los movimientos programados activos
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Todos los Movimientos Fijos Programados",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(recurringList, key = { "all-${it.id}" }) { item ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f).padding(end = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (item.isConfirmedThisCycle) Icons.Default.CheckCircle else Icons.Default.Schedule,
                                contentDescription = null,
                                tint = if (item.isConfirmedThisCycle) AndeanPrimary else Color(0xFF8D8374),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f, fill = false)) {
                                Text(
                                    text = item.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Día ${item.dayOfMonth} de cada mes · ${item.walletName}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = Money(item.amount).formatCop(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            softWrap = false,
                            color = if (item.type == RecurringType.INGRESO) AndeanPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Modal para editar monto / fecha antes de confirmar
    if (selectedItemForEdit != null) {
        val item = selectedItemForEdit!!
        AlertDialog(
            onDismissRequest = { selectedItemForEdit = null },
            title = {
                Text(
                    text = "Editar ${item.title}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Ajusta la cifra recibida o la fecha programada si hubo deducciones o cambios:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = editAmountInput,
                        onValueChange = { editAmountInput = it.filter { ch -> ch.isDigit() }.take(8) },
                        label = { Text("Monto Exacto COP") },
                        prefix = { Text("$ ") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = editDayInput,
                        onValueChange = { editDayInput = it.filter { ch -> ch.isDigit() }.take(2) },
                        label = { Text("Día del Mes (1-31)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val parsedAmount = editAmountInput.toLongOrNull()
                        val parsedDay = editDayInput.toIntOrNull()?.coerceIn(1, 31)
                        if (parsedAmount != null && parsedAmount > 0 && parsedDay != null) {
                            recurringList = recurringList.map {
                                if (it.id == item.id) it.copy(amount = parsedAmount, dayOfMonth = parsedDay) else it
                            }
                            selectedItemForEdit = null
                            onShowSnackbar("Monto actualizado a ${Money(parsedAmount).formatCop()}")
                        }
                    }
                ) {
                    Text("Guardar Cambios", fontWeight = FontWeight.Bold, color = AndeanPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedItemForEdit = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Modal para crear nuevo recurrente
    if (showNewModal) {
        AlertDialog(
            onDismissRequest = { showNewModal = false },
            title = {
                Text(
                    text = "Nuevo Movimiento Programable",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FilterChip(
                            selected = newType == RecurringType.GASTO,
                            onClick = { newType = RecurringType.GASTO },
                            label = { Text("Gasto Fijo") }
                        )
                        FilterChip(
                            selected = newType == RecurringType.INGRESO,
                            onClick = { newType = RecurringType.INGRESO },
                            label = { Text("Ingreso Fijo") }
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Nombre (ej. Salario, Arriendo)") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newAmount,
                        onValueChange = { newAmount = it.filter { ch -> ch.isDigit() }.take(8) },
                        label = { Text("Monto Estimado COP") },
                        prefix = { Text("$ ") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newDay,
                        onValueChange = { newDay = it.filter { ch -> ch.isDigit() }.take(2) },
                        label = { Text("Día del mes (1-31)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val parsed = newAmount.toLongOrNull()
                        val parsedDay = newDay.toIntOrNull()?.coerceIn(1, 31) ?: 15
                        if (newTitle.isNotBlank() && parsed != null && parsed > 0) {
                            recurringList = recurringList + RecurringTransactionItem(
                                id = System.currentTimeMillis().toString(),
                                title = newTitle.trim(),
                                amount = parsed,
                                type = newType,
                                dayOfMonth = parsedDay,
                                walletName = newWallet
                            )
                            showNewModal = false
                            newTitle = ""
                            newAmount = ""
                            onShowSnackbar("¡${newTitle.trim()} programado con éxito!")
                        }
                    }
                ) {
                    Text("Crear", fontWeight = FontWeight.Bold, color = AndeanPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewModal = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

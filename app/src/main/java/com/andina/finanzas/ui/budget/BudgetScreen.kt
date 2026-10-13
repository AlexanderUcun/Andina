package com.andina.finanzas.ui.budget

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andina.finanzas.ui.components.CategoryIconHelper
import com.andina.finanzas.ui.components.muggy.MuggyPose
import com.andina.finanzas.ui.components.muggy.MuggySpeechBubble
import com.andina.finanzas.ui.theme.AndeanAntExpenseAmber
import com.andina.finanzas.ui.theme.AndeanError
import com.andina.finanzas.ui.theme.AndeanPrimary
import java.text.NumberFormat
import java.util.Locale

/**
 * Pantalla de Gestión de Presupuesto Mensual Adaptativo (Módulo 4).
 * Conectada 100% a la base de datos Room y al perfil persistido del usuario.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    onNavigateBack: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BudgetViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var showEditBudgetDialog by remember { mutableStateOf(false) }
    var tempBudgetInput by remember { mutableStateOf("") }

    var selectedCategoryForEdit by remember { mutableStateOf<CategoryBudgetItem?>(null) }
    var tempCategoryLimitInput by remember { mutableStateOf("") }

    val percentage = state.progress
    val animatedProgress by animateFloatAsState(targetValue = percentage, label = "budgetProgress")

    val progressColor = when {
        percentage >= 0.90f -> AndeanError
        percentage >= 0.75f -> AndeanAntExpenseAmber
        else -> AndeanPrimary
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Presupuesto Mensual",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // 1. Tarjeta Hero de Presupuesto
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Tope Mensual Asignado",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = formatPesos(state.monthlyBudgetLimit),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 26.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(
                            onClick = {
                                tempBudgetInput = state.monthlyBudgetLimit.toString()
                                showEditBudgetDialog = true
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(AndeanPrimary.copy(alpha = 0.12f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar tope",
                                tint = AndeanPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Barra de progreso
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = progressColor,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        strokeCap = StrokeCap.Round
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Gastado este mes",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = formatPesos(state.totalExecutedSpent),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Disponible",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = formatPesos(state.remainingBudget),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = progressColor
                            )
                        }
                    }
                }
            }

            // Indicador de Saldo sin asignar a categorías
            if (state.unallocatedBudget > 0L) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = AndeanPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Saldo sin asignar a categorías: ${formatPesos(state.unallocatedBudget)}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Diagnóstico Inteligente de Muggy
            val exceededCategories = state.categories.filter { it.limit > 0 && it.spent > it.limit }
            val mostExceeded = exceededCategories.maxByOrNull { it.spent - it.limit }

            if (mostExceeded != null) {
                val overAmount = mostExceeded.spent - mostExceeded.limit
                MuggySpeechBubble(
                    title = "¡Atención en el sendero!",
                    greeting = "Tope superado en ${mostExceeded.name}",
                    text = "Has superado el tope de ${mostExceeded.name} en ${formatPesos(overAmount)}. Modera este gasto en las próximas semanas para evitar desajustar tu caudal.",
                    pose = MuggyPose.REFLECTIVE
                )
            } else if (percentage >= 1.0f) {
                val overTotal = state.totalExecutedSpent - state.monthlyBudgetLimit
                MuggySpeechBubble(
                    title = "¡Alerta Crítica de Presupuesto!",
                    greeting = "Presupuesto Mensual Excedido al ${(percentage * 100).toInt()}%",
                    text = "Has sobrepasado el límite total del mes en ${formatPesos(overTotal)}. Se recomienda pausar gastos prescindibles para no comprometer tus fondos de reserva.",
                    pose = MuggyPose.REFLECTIVE
                )
            } else {
                MuggySpeechBubble(
                    title = when {
                        percentage >= 0.90f -> "¡Cuidado con el caudal!"
                        percentage >= 0.75f -> "Atención al consumo"
                        else -> "Presupuesto Saludable"
                    },
                    greeting = "Ritmo del mes: ${(percentage * 100).toInt()}% ejecutado",
                    text = when {
                        percentage >= 0.90f -> "Has consumido más del 90% de tu tope. Te sugiero posponer compras no esenciales para cerrar el mes en positivo."
                        percentage >= 0.75f -> "Vas en el ${(percentage * 100).toInt()}% del presupuesto asignado. Modera los gastos variables en las próximas semanas."
                        else -> "Tu caudal fluye de manera controlada sobre la base de datos real. Mantén este ritmo para asegurar tus metas de ahorro."
                    },
                    pose = when {
                        percentage >= 0.90f -> MuggyPose.REFLECTIVE
                        percentage >= 0.75f -> MuggyPose.OBSERVING
                        else -> MuggyPose.SERENE
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Desglose y Control por Categorías desde Room
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Topes por Categoría",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Toca para editar",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    state.categories.forEachIndexed { index, item ->
                        BudgetCategoryRow(
                            name = item.name,
                            icon = item.icon,
                            colorHex = item.colorHex,
                            spent = item.spent,
                            limit = item.limit,
                            onClick = {
                                selectedCategoryForEdit = item
                                tempCategoryLimitInput = if (item.limit > 0) item.limit.toString() else ""
                            }
                        )
                        if (index < state.categories.size - 1) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }

    // Modal para editar tope mensual global
    if (showEditBudgetDialog) {
        AlertDialog(
            onDismissRequest = { showEditBudgetDialog = false },
            title = {
                Text(
                    text = "Ajustar Tope Mensual Global",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Define el límite máximo de gasto total para el mes actual (máx $99.999.999 COP):",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = tempBudgetInput,
                        onValueChange = { tempBudgetInput = it.filter { ch -> ch.isDigit() }.take(8) },
                        label = { Text("Tope Global COP") },
                        prefix = { Text("$ ") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                androidx.compose.material3.Button(
                    onClick = {
                        val parsed = tempBudgetInput.toLongOrNull()
                        if (parsed != null && parsed > 0) {
                            viewModel.updateMonthlyBudget(parsed)
                            showEditBudgetDialog = false
                            onShowSnackbar("Presupuesto global actualizado a ${formatPesos(parsed)}")
                        }
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = AndeanPrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Guardar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditBudgetDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Modal para editar tope de categoría individual
    if (selectedCategoryForEdit != null) {
        val cat = selectedCategoryForEdit!!
        AlertDialog(
            onDismissRequest = { selectedCategoryForEdit = null },
            title = {
                Text(
                    text = "Tope para ${cat.name}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Actualmente has gastado ${formatPesos(cat.spent)}. Asigna un nuevo límite:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = tempCategoryLimitInput,
                        onValueChange = { tempCategoryLimitInput = it.filter { ch -> ch.isDigit() }.take(8) },
                        label = { Text("Límite en COP") },
                        placeholder = { Text("0 = Sin tope") },
                        prefix = { Text("$ ") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                androidx.compose.material3.Button(
                    onClick = {
                        val parsed = tempCategoryLimitInput.toLongOrNull() ?: 0L
                        viewModel.updateCategoryLimit(cat.categoryId, parsed)
                        selectedCategoryForEdit = null
                        if (parsed > 0) {
                            onShowSnackbar("Límite de ${cat.name} ajustado a ${formatPesos(parsed)}")
                        } else {
                            onShowSnackbar("Tope de ${cat.name} eliminado")
                        }
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = AndeanPrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Guardar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedCategoryForEdit = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun BudgetCategoryRow(
    name: String,
    icon: String,
    colorHex: String,
    spent: Long,
    limit: Long,
    onClick: () -> Unit
) {
    val isOverLimit = limit > 0 && spent > limit
    val progress = if (limit > 0) (spent.toFloat() / limit.toFloat()).coerceIn(0f, 1f) else 0f
    val categoryColor = if (isOverLimit) AndeanError else CategoryIconHelper.parseColor(colorHex)
    val categoryIconVector = CategoryIconHelper.getIcon(icon)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 6.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(categoryColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = categoryIconVector,
                contentDescription = null,
                tint = categoryColor,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (isOverLimit) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AndeanError.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Excedido",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = AndeanError,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                if (limit > 0) {
                    Text(
                        text = "${formatPesos(spent)} / ${formatPesos(limit)}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = if (isOverLimit) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (isOverLimit) AndeanError else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${formatPesos(spent)} · Sin tope",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AndeanPrimary.copy(alpha = 0.1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = "Asignar",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = AndeanPrimary
                                )
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Asignar tope",
                                    tint = AndeanPrimary,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }
                }
            }
            if (limit > 0) {
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = categoryColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = StrokeCap.Round
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Editar límite",
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(16.dp)
        )
    }
}

private fun formatPesos(amount: Long): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
    format.maximumFractionDigits = 0
    return format.format(amount)
}


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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.runtime.mutableLongStateOf
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
import com.andina.finanzas.ui.components.AndinaButton
import com.andina.finanzas.ui.theme.AndeanAntExpenseAmber
import com.andina.finanzas.ui.theme.AndeanError
import com.andina.finanzas.ui.theme.AndeanPrimary
import java.text.NumberFormat
import java.util.Locale

data class CategoryBudgetItem(
    val name: String,
    var spent: Long,
    var limit: Long,
    val color: Color
)

/**
 * Pantalla de Gestión de Presupuesto Mensual Adaptativo (Módulo 4).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    onNavigateBack: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var monthlyBudgetLimit by remember { mutableLongStateOf(2_500_000L) }
    var showEditBudgetDialog by remember { mutableStateOf(false) }
    var tempBudgetInput by remember { mutableStateOf("") }

    var categoryList by remember {
        mutableStateOf(
            listOf(
                CategoryBudgetItem("Alimentación & Mercado", 620_000L, 900_000L, Color(0xFFE57373)),
                CategoryBudgetItem("Transporte & Movilidad", 280_000L, 400_000L, Color(0xFF64B5F6)),
                CategoryBudgetItem("Servicios & Hogar", 350_000L, 500_000L, Color(0xFFFFB74D)),
                CategoryBudgetItem("Ocio & Entretenimiento", 170_000L, 300_000L, Color(0xFF81C784)),
                CategoryBudgetItem("Salud & Bienestar", 50_000L, 200_000L, Color(0xFFBA68C8))
            )
        )
    }

    var selectedCategoryForEdit by remember { mutableStateOf<CategoryBudgetItem?>(null) }
    var tempCategoryLimitInput by remember { mutableStateOf("") }

    val currentSpent = categoryList.sumOf { it.spent }

    val percentage = if (monthlyBudgetLimit > 0) {
        (currentSpent.toFloat() / monthlyBudgetLimit.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val remaining = (monthlyBudgetLimit - currentSpent).coerceAtLeast(0L)
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
                                text = formatPesos(monthlyBudgetLimit),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 26.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(
                            onClick = {
                                tempBudgetInput = monthlyBudgetLimit.toString()
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
                                text = formatPesos(currentSpent),
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
                                text = formatPesos(remaining),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = progressColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Diagnóstico Inteligente de Muggy
            com.andina.finanzas.ui.components.muggy.MuggySpeechBubble(
                title = when {
                    percentage >= 0.90f -> "¡Cuidado con el caudal!"
                    percentage >= 0.75f -> "Atención al consumo"
                    else -> "Presupuesto Saludable"
                },
                greeting = "Ritmo del mes: ${(percentage * 100).toInt()}% ejecutado",
                text = when {
                    percentage >= 0.90f -> "Has consumido más del 90% de tu tope. Te sugiero posponer compras no esenciales para cerrar el mes en positivo."
                    percentage >= 0.75f -> "Vas en el 75% del presupuesto asignado. Modera los gastos variables en las próximas semanas."
                    else -> "Tu caudal fluye de manera controlada. Mantén este ritmo para asegurar tus metas de ahorro en los frailejones."
                },
                pose = when {
                    percentage >= 0.90f -> com.andina.finanzas.ui.components.muggy.MuggyPose.REFLECTIVE
                    percentage >= 0.75f -> com.andina.finanzas.ui.components.muggy.MuggyPose.OBSERVING
                    else -> com.andina.finanzas.ui.components.muggy.MuggyPose.SERENE
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Desglose y Control por Categorías
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
                    categoryList.forEachIndexed { index, item ->
                        BudgetCategoryRow(
                            name = item.name,
                            spent = item.spent,
                            limit = item.limit,
                            color = item.color,
                            onClick = {
                                selectedCategoryForEdit = item
                                tempCategoryLimitInput = item.limit.toString()
                            }
                        )
                        if (index < categoryList.size - 1) {
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
                TextButton(
                    onClick = {
                        val parsed = tempBudgetInput.toLongOrNull()
                        if (parsed != null && parsed > 0) {
                            monthlyBudgetLimit = parsed
                            showEditBudgetDialog = false
                            onShowSnackbar("Presupuesto global actualizado a ${formatPesos(parsed)}")
                        }
                    }
                ) {
                    Text("Guardar", fontWeight = FontWeight.Bold, color = AndeanPrimary)
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
                        prefix = { Text("$ ") },
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
                        val parsed = tempCategoryLimitInput.toLongOrNull()
                        if (parsed != null && parsed > 0) {
                            categoryList = categoryList.map {
                                if (it.name == cat.name) it.copy(limit = parsed) else it
                            }
                            selectedCategoryForEdit = null
                            onShowSnackbar("Límite de ${cat.name} ajustado a ${formatPesos(parsed)}")
                        }
                    }
                ) {
                    Text("Guardar", fontWeight = FontWeight.Bold, color = AndeanPrimary)
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
    spent: Long,
    limit: Long,
    color: Color,
    onClick: () -> Unit
) {
    val progress = if (limit > 0) (spent.toFloat() / limit.toFloat()).coerceIn(0f, 1f) else 0f
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${formatPesos(spent)} / ${formatPesos(limit)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap = StrokeCap.Round
        )
    }
}

private fun formatPesos(amount: Long): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
    format.maximumFractionDigits = 0
    return format.format(amount)
}

package com.andina.finanzas.ui.calendar

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andina.finanzas.domain.model.Transaction
import com.andina.finanzas.domain.model.TransactionType
import com.andina.finanzas.ui.components.CategoryIconHelper
import com.andina.finanzas.ui.theme.AndeanAntExpenseAmber
import com.andina.finanzas.ui.theme.AndeanPrimary
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale

/**
 * Calendario Financiero Interactivo & Mapa de Flujo de Caja (Módulo 10).
 * Conectado en tiempo real a las transacciones de Room.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onNavigateBack: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val daysOfWeek = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
    val currentYearMonth = state.yearMonth
    val monthTitle = "${currentYearMonth.month.getDisplayName(TextStyle.FULL, Locale("es", "CO")).replaceFirstChar { it.uppercase() }} ${currentYearMonth.year}"
    val daysInMonth = currentYearMonth.lengthOfMonth()
    val firstDayOfWeek = currentYearMonth.atDay(1).dayOfWeek.value // 1 (Mon) to 7 (Sun)
    val leadingEmptyCells = firstDayOfWeek - 1

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Calendario Financiero",
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
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            // 1. Selector de Mes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.changeMonth(-1) }) {
                    Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Mes anterior")
                }
                Text(
                    text = monthTitle,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = { viewModel.changeMonth(1) }) {
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Mes siguiente")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Encabezados de días de la semana
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                daysOfWeek.forEach { dayName ->
                    Text(
                        text = dayName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(36.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 3. Grid de días del mes
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Celdas vacías al inicio del mes
                    items(leadingEmptyCells) {
                        Spacer(modifier = Modifier.size(38.dp))
                    }

                    // Días reales del mes
                    items(daysInMonth) { index ->
                        val dayNumber = index + 1
                        val isSelected = dayNumber == state.selectedDay
                        val hasExpense = state.daysWithExpense.contains(dayNumber)
                        val hasIncome = state.daysWithIncome.contains(dayNumber)

                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    when {
                                        isSelected -> AndeanPrimary
                                        else -> Color.Transparent
                                    }
                                )
                                .clickable { viewModel.selectDay(dayNumber) },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = dayNumber.toString(),
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )

                                // Indicadores de actividad (Puntos)
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    if (hasExpense) {
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) Color.White else AndeanAntExpenseAmber)
                                        )
                                    }
                                    if (hasIncome) {
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) Color.White else Color(0xFF2E6316))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Detalle de movimientos del día seleccionado
            val monthName = currentYearMonth.month.getDisplayName(TextStyle.FULL, Locale("es", "CO")).replaceFirstChar { it.uppercase() }
            Text(
                text = "Movimientos del ${state.selectedDay} de $monthName de ${currentYearMonth.year}",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (state.selectedDayTransactions.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Sin movimientos registrados",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Día libre de gastos e ingresos. ¡Páramo en tranquilidad!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.selectedDayTransactions, key = { it.id }) { tx ->
                        val category = tx.categoryId?.let { state.categoriesMap[it] }
                        val wallet = state.walletsMap[tx.walletId]

                        val iconName = category?.icon ?: when (tx.type) {
                            TransactionType.GASTO -> "restaurant"
                            TransactionType.INGRESO -> "payments"
                            TransactionType.TRANSFERENCIA -> "payments"
                        }
                        val colorHex = category?.colorHex ?: when (tx.type) {
                            TransactionType.GASTO -> "#D97706"
                            TransactionType.INGRESO -> "#2E7D32"
                            TransactionType.TRANSFERENCIA -> "#1565C0"
                        }

                        val categoryColor = CategoryIconHelper.parseColor(colorHex)
                        val categoryIconVector = CategoryIconHelper.getIcon(iconName)

                        val title = if (tx.note.isNotBlank()) tx.note else (category?.name ?: if (tx.type == TransactionType.INGRESO) "Ingreso" else "Gasto")
                        val timeFormat = SimpleDateFormat("h:mm a", Locale("es", "CO"))
                        val timeStr = timeFormat.format(Date(tx.occurredAt))
                        val categoryLabel = category?.name ?: if (tx.type == TransactionType.INGRESO) "Ingreso" else "Gasto"
                        val walletName = wallet?.name ?: "Billetera"

                        val subtitle = "$categoryLabel · $timeStr · $walletName"

                        val amountPrefix = when (tx.type) {
                            TransactionType.GASTO -> "- "
                            TransactionType.INGRESO -> "+ "
                            TransactionType.TRANSFERENCIA -> ""
                        }
                        val amountColor = when (tx.type) {
                            TransactionType.GASTO -> MaterialTheme.colorScheme.onSurface
                            TransactionType.INGRESO -> Color(0xFF2E6316)
                            TransactionType.TRANSFERENCIA -> Color(0xFF1565C0)
                        }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
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
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = subtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Text(
                                    text = "$amountPrefix${tx.amount.formatCop()}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = amountColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

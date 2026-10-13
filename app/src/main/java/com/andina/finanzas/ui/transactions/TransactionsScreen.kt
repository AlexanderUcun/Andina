package com.andina.finanzas.ui.transactions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.andina.finanzas.domain.model.Transaction
import com.andina.finanzas.domain.model.TransactionType
import com.andina.finanzas.ui.components.AndinaButton
import com.andina.finanzas.ui.theme.AndeanAntExpenseAmber
import com.andina.finanzas.ui.theme.AndinaMossGreen
import com.andina.finanzas.ui.theme.AndeanPrimary
import com.andina.finanzas.ui.transactions.components.SwipeableTransactionRow
import com.andina.finanzas.ui.transactions.components.TransactionsFilterBottomSheet
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Pantalla principal de Historial de Movimientos y Auditoría Financiera (Fase 10).
 * Integra Paging 3, agrupación cronológica (Hoy, Ayer, Mes actual), swipe con hojas andinas,
 * búsqueda reactiva y modal de filtros avanzados.
 */
@Composable
fun TransactionsScreen(
    onNavigateToDetail: (Long) -> Unit,
    onShowSnackbar: (String) -> Unit,
    onShowSnackbarWithAction: (message: String, actionLabel: String, onAction: () -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    initialViewMode: TransactionsViewMode = TransactionsViewMode.LISTA,
    onNavigateToCalendar: () -> Unit = {},
    viewModel: TransactionsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val filter by viewModel.filter.collectAsState()
    val pagingItems: LazyPagingItems<Transaction> = viewModel.transactionsPagingFlow.collectAsLazyPagingItems()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(initialViewMode) {
        if (state.viewMode != initialViewMode) {
            viewModel.onEvent(TransactionsEvent.OnViewModeSelected(initialViewMode))
        }
    }

    // Escucha de efectos secundarios (navegación y snackbar de deshacer)
    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is TransactionsEffect.ShowUndoSnackbar -> {
                    onShowSnackbarWithAction(effect.message, effect.actionLabel) {
                        viewModel.onEvent(TransactionsEvent.OnUndoDelete)
                    }
                }
                is TransactionsEffect.NavigateToDetail -> {
                    onNavigateToDetail(effect.transactionId)
                }
                is TransactionsEffect.ShowMessage -> {
                    onShowSnackbar(effect.message)
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TransactionsTopHeader(
                viewMode = state.viewMode,
                filter = filter,
                onQueryChanged = { viewModel.onEvent(TransactionsEvent.OnQueryChanged(it)) },
                onFilterClick = { viewModel.onEvent(TransactionsEvent.OnSetFilterSheetVisible(true)) },
                onViewModeSelected = { viewModel.onEvent(TransactionsEvent.OnViewModeSelected(it)) },
                onQuickTypeSelected = { type ->
                    val newType = if (filter.type == type) null else type
                    viewModel.onEvent(TransactionsEvent.OnTypeSelected(newType))
                },
                onQuickToggleAnt = {
                    viewModel.onEvent(TransactionsEvent.OnToggleAntExpense(!filter.onlyAntExpense))
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (state.viewMode == TransactionsViewMode.CALENDARIO) {
                com.andina.finanzas.ui.calendar.CalendarContent(
                    modifier = Modifier.fillMaxSize()
                )
            } else when {
                // Estado de carga inicial
                pagingItems.loadState.refresh is LoadState.Loading && pagingItems.itemCount == 0 -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                color = AndeanPrimary,
                                modifier = Modifier.size(40.dp),
                                strokeWidth = 3.dp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Consultando el libro mayor...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Estado vacío
                pagingItems.loadState.refresh is LoadState.NotLoading && pagingItems.itemCount == 0 -> {
                    EmptyTransactionsState(
                        filter = filter,
                        hasFilters = !filter.isDefault,
                        onResetFilters = { viewModel.onEvent(TransactionsEvent.OnResetFilters) }
                    )
                }

                // Lista paginada con agrupación por fechas
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 88.dp)
                    ) {
                        var previousDateGroup: String? = null

                        for (i in 0 until pagingItems.itemCount) {
                            val transaction = pagingItems.peek(i)
                            val currentDateGroup = transaction?.occurredAt?.let { getDateGroupHeader(it) }

                            if (currentDateGroup != null && currentDateGroup != previousDateGroup) {
                                previousDateGroup = currentDateGroup
                                item(key = "header_${currentDateGroup}_$i") {
                                    DateSectionHeader(title = currentDateGroup)
                                }
                            }

                            item(key = transaction?.id ?: i) {
                                val item = pagingItems[i]
                                if (item != null) {
                                    SwipeableTransactionRow(
                                        transaction = item,
                                        onClick = {
                                            viewModel.onEvent(TransactionsEvent.OnTransactionClick(item.id))
                                        },
                                        onDelete = { tx ->
                                            viewModel.onEvent(TransactionsEvent.OnDeleteTransaction(tx))
                                        }
                                    )
                                    HorizontalDivider(
                                        thickness = 0.5.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f),
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                            }
                        }

                        // Indicador de paginación al final de la lista
                        if (pagingItems.loadState.append is LoadState.Loading) {
                            item(key = "loading_append") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = AndeanPrimary,
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal de Filtros Avanzados
    if (state.isFilterSheetVisible) {
        TransactionsFilterBottomSheet(
            filter = filter,
            availableWallets = state.availableWallets,
            availableCategories = state.availableCategories,
            onTypeSelected = { viewModel.onEvent(TransactionsEvent.OnTypeSelected(it)) },
            onWalletSelected = { viewModel.onEvent(TransactionsEvent.OnWalletSelected(it)) },
            onCategorySelected = { viewModel.onEvent(TransactionsEvent.OnCategorySelected(it)) },
            onToggleAntExpense = { viewModel.onEvent(TransactionsEvent.OnToggleAntExpense(it)) },
            onDatePresetSelected = { viewModel.onEvent(TransactionsEvent.OnDatePresetSelected(it)) },
            onResetFilters = { viewModel.onEvent(TransactionsEvent.OnResetFilters) },
            onDismissRequest = { viewModel.onEvent(TransactionsEvent.OnSetFilterSheetVisible(false)) }
        )
    }
}

/**
 * Cabecera superior con selector de vista (Lista | Calendario), búsqueda y chips de filtro.
 */
@Composable
private fun TransactionsTopHeader(
    viewMode: TransactionsViewMode,
    filter: TransactionsFilter,
    onQueryChanged: (String) -> Unit,
    onFilterClick: () -> Unit,
    onViewModeSelected: (TransactionsViewMode) -> Unit,
    onQuickTypeSelected: (TransactionType) -> Unit,
    onQuickToggleAnt: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
            .padding(top = 8.dp, bottom = 4.dp)
    ) {
        // Título de sección y selector segmentado
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Movimientos",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Libro mayor y auditoría de caudal",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Selector Segmentado de Vista (Lista / Calendario)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Row(modifier = Modifier.padding(3.dp)) {
                        Surface(
                            shape = RoundedCornerShape(9.dp),
                            color = if (viewMode == TransactionsViewMode.LISTA) AndeanPrimary else Color.Transparent,
                            modifier = Modifier
                                .clip(RoundedCornerShape(9.dp))
                                .clickable { onViewModeSelected(TransactionsViewMode.LISTA) }
                        ) {
                            Text(
                                text = "📋 Lista",
                                fontSize = 11.sp,
                                fontWeight = if (viewMode == TransactionsViewMode.LISTA) FontWeight.Bold else FontWeight.Medium,
                                color = if (viewMode == TransactionsViewMode.LISTA) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(9.dp),
                            color = if (viewMode == TransactionsViewMode.CALENDARIO) AndeanPrimary else Color.Transparent,
                            modifier = Modifier
                                .clip(RoundedCornerShape(9.dp))
                                .clickable { onViewModeSelected(TransactionsViewMode.CALENDARIO) }
                        ) {
                            Text(
                                text = "📅 Calendario",
                                fontSize = 11.sp,
                                fontWeight = if (viewMode == TransactionsViewMode.CALENDARIO) FontWeight.Bold else FontWeight.Medium,
                                color = if (viewMode == TransactionsViewMode.CALENDARIO) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Botón de filtros con badge reactivo
                BadgedBox(
                    badge = {
                        if (filter.activeFilterCount > 0) {
                            Badge(
                                containerColor = AndeanPrimary,
                                contentColor = Color.White
                            ) {
                                Text(text = filter.activeFilterCount.toString())
                            }
                        }
                    }
                ) {
                    IconButton(
                        onClick = onFilterClick,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (filter.activeFilterCount > 0) AndeanPrimary.copy(alpha = 0.12f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterAlt,
                            contentDescription = "Filtros avanzados",
                            tint = if (filter.activeFilterCount > 0) AndeanPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Campo de búsqueda integrado
        OutlinedTextField(
            value = filter.query,
            onValueChange = onQueryChanged,
            placeholder = {
                Text(
                    text = "Buscar por nota, negocio o categoría...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                if (filter.query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChanged("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Limpiar búsqueda",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                focusedBorderColor = AndeanPrimary,
                unfocusedBorderColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(50.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Chips rápidos de selección inmediata
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = filter.type == null && !filter.onlyAntExpense,
                onClick = {
                    if (filter.type != null || filter.onlyAntExpense) {
                        onQuickTypeSelected(TransactionType.GASTO)
                    }
                },
                label = { Text("Todos") },
                shape = RoundedCornerShape(10.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AndeanPrimary.copy(alpha = 0.16f),
                    selectedLabelColor = AndeanPrimary
                )
            )

            FilterChip(
                selected = filter.type == TransactionType.GASTO && !filter.onlyAntExpense,
                onClick = { onQuickTypeSelected(TransactionType.GASTO) },
                label = { Text("Gastos") },
                shape = RoundedCornerShape(10.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AndeanPrimary.copy(alpha = 0.16f),
                    selectedLabelColor = AndeanPrimary
                )
            )

            FilterChip(
                selected = filter.type == TransactionType.INGRESO,
                onClick = { onQuickTypeSelected(TransactionType.INGRESO) },
                label = { Text("Ingresos") },
                shape = RoundedCornerShape(10.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AndeanPrimary.copy(alpha = 0.16f),
                    selectedLabelColor = AndeanPrimary
                )
            )

            FilterChip(
                selected = filter.onlyAntExpense,
                onClick = onQuickToggleAnt,
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.BugReport,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = if (filter.onlyAntExpense) AndeanAntExpenseAmber else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Hormiga")
                    }
                },
                shape = RoundedCornerShape(10.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AndeanAntExpenseAmber.copy(alpha = 0.16f),
                    selectedLabelColor = AndeanAntExpenseAmber
                )
            )

            FilterChip(
                selected = filter.type == TransactionType.TRANSFERENCIA,
                onClick = { onQuickTypeSelected(TransactionType.TRANSFERENCIA) },
                label = { Text("Transferencias") },
                shape = RoundedCornerShape(10.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AndeanPrimary.copy(alpha = 0.16f),
                    selectedLabelColor = AndeanPrimary
                )
            )
        }

        HorizontalDivider(
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        )
    }
}

/**
 * Cabecera de sección para agrupar movimientos por temporalidad (Hoy, Ayer, Mes actual).
 */
@Composable
private fun DateSectionHeader(title: String, dayNetBalance: String? = null) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    ),
                    color = AndinaMossGreen
                )
            }
            if (dayNetBalance != null) {
                Text(
                    text = dayNetBalance,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Estado vacío cuando no se encuentran movimientos, con InputChips interactivos para remover filtros.
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun EmptyTransactionsState(
    filter: TransactionsFilter,
    hasFilters: Boolean,
    onResetFilters: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(AndeanPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ReceiptLong,
                    contentDescription = null,
                    tint = AndeanPrimary,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (hasFilters) "Sin movimientos coincidentes" else "Aún no hay movimientos",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (hasFilters) {
                    "No se encontraron movimientos con los filtros seleccionados. Toca un filtro para quitarlo o restablece la búsqueda."
                } else {
                    "Registra tu primer gasto o ingreso usando el botón central (+) para iniciar tu libro mayor."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            if (hasFilters) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (filter.query.isNotBlank()) {
                        androidx.compose.material3.InputChip(
                            selected = true,
                            onClick = onResetFilters,
                            label = { Text("\"${filter.query}\"") },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Quitar filtro",
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        )
                    }
                    if (filter.type != null) {
                        androidx.compose.material3.InputChip(
                            selected = true,
                            onClick = onResetFilters,
                            label = { Text(filter.type.name) },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Quitar filtro",
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        )
                    }
                    if (filter.onlyAntExpense) {
                        androidx.compose.material3.InputChip(
                            selected = true,
                            onClick = onResetFilters,
                            label = { Text("🐜 Hormiga") },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Quitar filtro",
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                AndinaButton(
                    text = "Restablecer Filtros",
                    onClick = onResetFilters
                )
            }
        }
    }
}

/**
 * Calcula el título de agrupación cronológica según timestamp epoch.
 */
fun getDateGroupHeader(timestamp: Long, now: Long = System.currentTimeMillis()): String {
    val calItem = Calendar.getInstance().apply { timeInMillis = timestamp }
    val calNow = Calendar.getInstance().apply { timeInMillis = now }

    val isSameYear = calItem.get(Calendar.YEAR) == calNow.get(Calendar.YEAR)
    val isSameDay = isSameYear && calItem.get(Calendar.DAY_OF_YEAR) == calNow.get(Calendar.DAY_OF_YEAR)

    val calYesterday = Calendar.getInstance().apply {
        timeInMillis = now
        add(Calendar.DAY_OF_YEAR, -1)
    }
    val isYesterday = isSameYear && calItem.get(Calendar.DAY_OF_YEAR) == calYesterday.get(Calendar.DAY_OF_YEAR)

    return when {
        isSameDay -> "Hoy"
        isYesterday -> "Ayer"
        isSameYear && calItem.get(Calendar.MONTH) == calNow.get(Calendar.MONTH) -> "Mes actual"
        isSameYear -> {
            val monthFormat = SimpleDateFormat("MMMM", Locale("es", "CO"))
            monthFormat.format(Date(timestamp)).replaceFirstChar { it.uppercase() }
        }
        else -> {
            val fullFormat = SimpleDateFormat("MMMM yyyy", Locale("es", "CO"))
            fullFormat.format(Date(timestamp)).replaceFirstChar { it.uppercase() }
        }
    }
}

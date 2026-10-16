package com.andina.finanzas.ui.customization

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andina.finanzas.domain.model.Category
import com.andina.finanzas.domain.model.CategoryKind
import com.andina.finanzas.ui.components.CategoryIconHelper
import com.andina.finanzas.ui.icons.AndinaIcon
import com.andina.finanzas.ui.icons.AndinaIconKey
import com.andina.finanzas.ui.icons.CategoryIconCatalog
import com.andina.finanzas.ui.theme.AndeanPrimary
import com.andina.finanzas.ui.theme.AndinaCategoryPalette
import com.andina.finanzas.ui.theme.LegacyColorMapper
import com.andina.finanzas.ui.theme.LocalAndinaColors

/**
 * Estudio y Panel de Personalización Completa de Categorías.
 * Permite crear, modificar y afinar colores e iconos con la estética Andina de alta gama.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagementScreen(
    onNavigateBack: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CategoryManagementViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = LocalAndinaColors.current
    val haptic = LocalHapticFeedback.current

    var categoryToDelete by remember { mutableStateOf<Category?>(null) }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is CategoryManagementEffect.ShowMessage -> {
                    onShowSnackbar(effect.message)
                }
                CategoryManagementEffect.CategorySaved -> {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.canvasTop,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Estudio de Categorías",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                        )
                        Text(
                            text = "Personalización completa de colores e iconos",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = colors.textSecondary
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        AndinaIcon(
                            icon = AndinaIconKey.ACTION_BACK,
                            contentDescription = "Regresar",
                            size = 20.dp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.cardBackground
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Selector de Pestaña Principal (Gastos vs Ingresos)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.cardBackground)
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(14.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val tabs = listOf(
                        CategoryKind.GASTO to "Gastos (${state.categories.count { it.kind == CategoryKind.GASTO }})",
                        CategoryKind.INGRESO to "Ingresos (${state.categories.count { it.kind == CategoryKind.INGRESO }})"
                    )

                    tabs.forEach { (kind, label) ->
                        val isSelected = state.selectedTabKind == kind
                        val tabBg = if (isSelected) AndeanPrimary else colors.cardBackground
                        val tabTextColor = if (isSelected) colors.cardBackground else colors.textSecondary

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(tabBg)
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    viewModel.selectTabKind(kind)
                                    if (!state.isEditing) {
                                        viewModel.onKindChange(kind)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = tabTextColor
                                )
                            )
                        }
                    }
                }
            }

            // 2. Tarjeta Estudio: Editor / Creador Interactivo de Categoría
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (state.isEditing) colors.accentGold else colors.cardBorder
                        )
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Cabecera del Estudio
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                AndinaIcon(
                                    icon = AndinaIconKey.TOOL_PALETTE,
                                    contentDescription = null,
                                    size = 18.dp
                                )
                                Text(
                                    text = if (state.isEditing) "Editando Categoría" else "Crear Nueva Categoría",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (state.isEditing) colors.accentGold else colors.textPrimary
                                    )
                                )
                            }

                            if (state.isEditing) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = colors.cardBorder,
                                    modifier = Modifier.clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        viewModel.cancelEdit()
                                    }
                                ) {
                                    Text(
                                        text = "Cancelar",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = colors.textSecondary
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        // Vista Previa en Vivo (Live Preview Chip)
                        val currentColor = CategoryIconHelper.parseColor(state.selectedColorHex)
                        val currentIcon = CategoryIconHelper.getIcon(state.selectedIcon)
                        val previewLabel = if (state.nameInput.isBlank()) "Nombre de la categoría" else state.nameInput.trim()

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(colors.canvasBottom)
                                .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(currentColor.copy(alpha = 0.2f))
                                            .border(1.2.dp, currentColor, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = currentIcon,
                                            contentDescription = null,
                                            tint = currentColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = previewLabel,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = colors.textPrimary
                                            )
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = if (state.selectedKind == CategoryKind.GASTO) "Gasto" else "Ingreso",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 10.sp,
                                                    color = colors.textSecondary
                                                )
                                            )
                                            if (state.selectedKind == CategoryKind.GASTO && state.isAntCandidate) {
                                                Text(
                                                    text = "· 🐜 Hormiga",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontSize = 10.sp,
                                                        color = colors.accentGold,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = currentColor.copy(alpha = 0.15f),
                                    border = CardDefaults.outlinedCardBorder().copy(
                                        brush = androidx.compose.ui.graphics.SolidColor(currentColor)
                                    )
                                ) {
                                    Text(
                                        text = "Vista Previa",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = currentColor
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        // Campo de Texto para el Nombre
                        OutlinedTextField(
                            value = state.nameInput,
                            onValueChange = { viewModel.onNameChange(it) },
                            label = { Text("Nombre de la Categoría") },
                            placeholder = { Text("Ej: Mascotas, Gimnasio, Libros...") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Selector de Tipo (Gasto vs Ingreso)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Naturaleza:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textSecondary
                                )
                            )
                            FilterChip(
                                selected = state.selectedKind == CategoryKind.GASTO,
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    viewModel.onKindChange(CategoryKind.GASTO)
                                },
                                label = { Text("Gasto") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AndeanPrimary.copy(alpha = 0.16f),
                                    selectedLabelColor = AndeanPrimary
                                )
                            )
                            FilterChip(
                                selected = state.selectedKind == CategoryKind.INGRESO,
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    viewModel.onKindChange(CategoryKind.INGRESO)
                                },
                                label = { Text("Ingreso") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AndeanPrimary.copy(alpha = 0.16f),
                                    selectedLabelColor = AndeanPrimary
                                )
                            )
                        }

                        // Switch / Chip Gasto Hormiga (solo en Gastos)
                        if (state.selectedKind == CategoryKind.GASTO) {
                            FilterChip(
                                selected = state.isAntCandidate,
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    viewModel.onAntCandidateChange(!state.isAntCandidate)
                                },
                                shape = RoundedCornerShape(14.dp),
                                label = {
                                    Text("🐜 Sugerir automáticamente como Gasto Hormiga")
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = colors.accentGold.copy(alpha = 0.18f),
                                    selectedLabelColor = colors.textPrimary
                                )
                            )
                        }

                        HorizontalDivider(thickness = 0.6.dp, color = colors.cardBorder)

                        // 3. Selector de Paleta de Colores Andina
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            val activeColorName = AndinaCategoryPalette.options.find {
                                it.hex.equals(state.selectedColorHex, ignoreCase = true)
                            }?.name ?: "Personalizado"

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Paleta de Colores Andina",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary
                                    )
                                )
                                Text(
                                    text = activeColorName,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colors.accentGold
                                    )
                                )
                            }

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                            ) {
                                items(AndinaCategoryPalette.options) { option ->
                                    val isSelected = option.hex.equals(state.selectedColorHex, ignoreCase = true)

                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(option.color)
                                            .border(
                                                width = if (isSelected) 3.dp else 1.dp,
                                                color = if (isSelected) colors.textPrimary else colors.cardBorder,
                                                shape = CircleShape
                                            )
                                            .clickable {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                viewModel.onColorChange(option.hex)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(colors.cardBackground)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        HorizontalDivider(thickness = 0.6.dp, color = colors.cardBorder)

                        // 4. Selector de Iconos de Categoría (Catálogo de 24 Iconos)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Icono Representativo",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary
                                )
                            )

                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(minSize = 58.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(CategoryIconCatalog.options, key = { it.key }) { opt ->
                                    val isSelected = opt.key.equals(state.selectedIcon, ignoreCase = true)
                                    val iconBg = if (isSelected) currentColor.copy(alpha = 0.18f) else colors.canvasBottom
                                    val iconBorder = if (isSelected) currentColor else colors.cardBorder
                                    val iconTint = if (isSelected) currentColor else colors.textSecondary

                                    Column(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(iconBg)
                                            .border(
                                                width = if (isSelected) 1.5.dp else 0.8.dp,
                                                color = iconBorder,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clickable {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                viewModel.onIconChange(opt.key)
                                            }
                                            .padding(vertical = 8.dp, horizontal = 4.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = opt.icon,
                                            contentDescription = opt.label,
                                            tint = iconTint,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = opt.label,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) colors.textPrimary else colors.textSecondary
                                            ),
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }

                        // Botón de Acción Principal (Guardar / Actualizar)
                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.saveCategory()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AndeanPrimary),
                            enabled = !state.isSaving
                        ) {
                            Text(
                                text = if (state.isEditing) "Actualizar Categoría" else "Guardar Categoría",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }
                }
            }

            // 5. Encabezado de la Lista de Categorías Existentes
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Categorías Activas (${state.filteredCategories.size})",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                    )
                    Text(
                        text = "Toca para editar",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            color = colors.textSecondary
                        )
                    )
                }
            }

            // 6. Lista de Categorías de la sección actual
            items(state.filteredCategories, key = { it.id }) { category ->
                val categoryColor = CategoryIconHelper.parseColor(category.colorHex)
                val categoryIcon = CategoryIconHelper.getIcon(category.icon)
                val isBeingEdited = state.editingCategoryId == category.id

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (isBeingEdited) colors.accentGold else colors.cardBorder
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.startEdit(category)
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(categoryColor.copy(alpha = 0.16f))
                                    .border(1.dp, categoryColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = categoryIcon,
                                    contentDescription = null,
                                    tint = categoryColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = category.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = colors.textPrimary
                                    )
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = if (category.kind == CategoryKind.GASTO) "Gasto" else "Ingreso",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            color = colors.textSecondary
                                        )
                                    )
                                    if (category.isAntCandidate) {
                                        Text(
                                            text = "· 🐜 Hormiga",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.sp,
                                                color = colors.accentGold,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    viewModel.startEdit(category)
                                },
                                modifier = Modifier.size(34.dp)
                            ) {
                                AndinaIcon(
                                    icon = AndinaIconKey.ACTION_EDIT,
                                    contentDescription = "Editar",
                                    size = 16.dp
                                )
                            }

                            IconButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    categoryToDelete = category
                                },
                                modifier = Modifier.size(34.dp)
                            ) {
                                AndinaIcon(
                                    icon = AndinaIconKey.ACTION_DELETE,
                                    contentDescription = "Eliminar",
                                    size = 16.dp
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Modal de Confirmación para Eliminar Categoría
    if (categoryToDelete != null) {
        val cat = categoryToDelete!!
        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            title = {
                Text(
                    text = "¿Eliminar '${cat.name}'?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = "Esta acción eliminará la categoría. Las transacciones pasadas asociadas mantendrán su registro contable histórico.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCategory(cat.id)
                        categoryToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.statusDanger)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { categoryToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

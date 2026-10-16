package com.andina.finanzas.ui.customization

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andina.finanzas.domain.model.CategoryKind
import com.andina.finanzas.ui.components.CategoryIconHelper
import com.andina.finanzas.ui.components.muggy.MuggyPose
import com.andina.finanzas.ui.components.muggy.MuggySpeechBubble
import com.andina.finanzas.ui.theme.AndeanPrimary

/**
 * Hub de Personalización Total del Ecosistema Andina (Módulo 9).
 * Conectado a la base de datos Room y al UserProfileRepository.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizationHubScreen(
    onNavigateBack: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToCategoryManagement: () -> Unit = {},
    viewModel: CustomizationViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val profile = state.userProfile

    var selectedThemeIndex by remember { mutableIntStateOf(0) }
    val themeNames = listOf("Páramo Andino 🌿", "Niebla de Cumbre 🏔️", "Laguna Esmeralda 💧", "Tarde Cafetera ☕")

    var showCreateCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    var newCategoryColorHex by remember { mutableStateOf("#435C3C") }
    var categoryToDelete by remember { mutableStateOf<com.andina.finanzas.domain.model.Category?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Personalización",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                MuggySpeechBubble(
                    title = "Tu Ecosistema a tu Medida",
                    greeting = "Ajustes Visuales & Categorías",
                    text = "Personaliza los colores, íconos y qué secciones se muestran en tu pantalla de Inicio para una experiencia totalmente adaptada a tu rutina.",
                    pose = MuggyPose.EXPLORER
                )
            }

            // 1. Tarjetas Visibles en el Dashboard de Inicio (Conectadas a UserProfileRepository)
            item {
                Text(
                    text = "Módulos de la Pantalla de Inicio",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        HomeModuleToggleRow(
                            title = "Tarjeta de Presupuesto Mensual",
                            subtitle = "Muestra la barra de gasto y alertas en Inicio",
                            checked = profile.showBudgetCard,
                            onCheckedChange = {
                                viewModel.updateModuleVisibility(
                                    showBudget = it,
                                    showWallets = profile.showWalletsCard,
                                    showMuggy = profile.showMuggyInsight,
                                    showPockets = profile.showPocketsCard
                                )
                            }
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), thickness = 0.5.dp)
                        HomeModuleToggleRow(
                            title = "Carrusel de Billeteras",
                            subtitle = "Muestra tus cuentas y saldos en Inicio",
                            checked = profile.showWalletsCard,
                            onCheckedChange = {
                                viewModel.updateModuleVisibility(
                                    showBudget = profile.showBudgetCard,
                                    showWallets = it,
                                    showMuggy = profile.showMuggyInsight,
                                    showPockets = profile.showPocketsCard
                                )
                            }
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), thickness = 0.5.dp)
                        HomeModuleToggleRow(
                            title = "Consejo de Sabiduría de Muggy",
                            subtitle = "Banner con diagnósticos y tips de IA",
                            checked = profile.showMuggyInsight,
                            onCheckedChange = {
                                viewModel.updateModuleVisibility(
                                    showBudget = profile.showBudgetCard,
                                    showWallets = profile.showWalletsCard,
                                    showMuggy = it,
                                    showPockets = profile.showPocketsCard
                                )
                            }
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), thickness = 0.5.dp)
                        HomeModuleToggleRow(
                            title = "Bolsillos de Ahorro",
                            subtitle = "Frailejones vivos y metas en progreso",
                            checked = profile.showPocketsCard,
                            onCheckedChange = {
                                viewModel.updateModuleVisibility(
                                    showBudget = profile.showBudgetCard,
                                    showWallets = profile.showWalletsCard,
                                    showMuggy = profile.showMuggyInsight,
                                    showPockets = it
                                )
                            }
                        )
                    }
                }
            }

            // 2. Paleta de Tema del Páramo
            item {
                Text(
                    text = "Atmósfera Visual del Páramo",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            item {
                val themePalettes = listOf(
                    listOf(Color(0xFF435C3C), Color(0xFFC58934), Color(0xFFEBE6DC)),
                    listOf(Color(0xFF4A5568), Color(0xFF718096), Color(0xFFEDF2F7)),
                    listOf(Color(0xFF00897B), Color(0xFF26A69A), Color(0xFFE0F2F1)),
                    listOf(Color(0xFF5D4037), Color(0xFF8D6E63), Color(0xFFEFEBE9))
                )

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        themeNames.forEachIndexed { index, theme ->
                            val palette = themePalettes.getOrElse(index) { listOf(AndeanPrimary, Color.Gray, Color.LightGray) }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        selectedThemeIndex = index
                                        onShowSnackbar("Tema aplicado: $theme")
                                    }
                                    .padding(vertical = 10.dp, horizontal = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Paleta de 3 círculos con colores reales
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        palette.forEach { color ->
                                            Box(
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .clip(CircleShape)
                                                    .background(color)
                                            )
                                        }
                                    }

                                    Text(
                                        text = theme,
                                        fontSize = 14.sp,
                                        fontWeight = if (selectedThemeIndex == index) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selectedThemeIndex == index) AndeanPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                if (selectedThemeIndex == index) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = AndeanPrimary)
                                }
                            }
                        }
                    }
                }
            }

            // 3. Gestión de Categorías desde Room
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Gestión de Categorías",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = AndeanPrimary.copy(alpha = 0.12f),
                                modifier = Modifier.clickable { onNavigateToCategoryManagement() }
                            ) {
                                Text(
                                    text = "🎨 Estudio Completo",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AndeanPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = AndeanPrimary.copy(alpha = 0.12f),
                                modifier = Modifier.clickable { showCreateCategoryDialog = true }
                            ) {
                                Text(
                                    text = "+ Rápida",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AndeanPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Pestañas Gasto vs Ingreso
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FilterChip(
                            selected = state.selectedKind == CategoryKind.GASTO,
                            onClick = { viewModel.selectCategoryKind(CategoryKind.GASTO) },
                            label = { Text("Gastos") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AndeanPrimary.copy(alpha = 0.15f),
                                selectedLabelColor = AndeanPrimary
                            )
                        )
                        FilterChip(
                            selected = state.selectedKind == CategoryKind.INGRESO,
                            onClick = { viewModel.selectCategoryKind(CategoryKind.INGRESO) },
                            label = { Text("Ingresos") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AndeanPrimary.copy(alpha = 0.15f),
                                selectedLabelColor = AndeanPrimary
                            )
                        )
                    }
                }
            }

            items(state.filteredCategories, key = { it.id }) { cat ->
                val categoryColor = CategoryIconHelper.parseColor(cat.colorHex)
                val categoryIcon = CategoryIconHelper.getIcon(cat.icon)

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onShowSnackbar("Categoría: ${cat.name}") }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(categoryColor.copy(alpha = 0.18f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = categoryIcon,
                                    contentDescription = null,
                                    tint = categoryColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = cat.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (cat.kind == CategoryKind.GASTO) {
                                        if (cat.isAntCandidate) "Gasto · Candidato Hormiga" else "Gasto"
                                    } else "Ingreso",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { categoryToDelete = cat }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar Categoría",
                                    tint = Color(0xFFBA1A1A).copy(alpha = 0.8f),
                                    modifier = Modifier.size(18.dp)
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

    if (showCreateCategoryDialog) {
        val kindLabel = if (state.selectedKind == CategoryKind.INGRESO) "Ingreso" else "Gasto"
        AlertDialog(
            onDismissRequest = { showCreateCategoryDialog = false },
            title = {
                Text(
                    text = "Nueva Categoría de $kindLabel",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Escribe el nombre de la categoría para agregarla a tus movimientos:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newCategoryName,
                        onValueChange = { newCategoryName = it },
                        placeholder = { Text("Ej. Mascotas, Deportes, Libros") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCategoryName.isNotBlank()) {
                            viewModel.createCategory(
                                name = newCategoryName.trim(),
                                kind = state.selectedKind,
                                icon = "category",
                                colorHex = newCategoryColorHex
                            )
                            onShowSnackbar("Categoría '${newCategoryName.trim()}' creada exitosamente")
                            newCategoryName = ""
                            showCreateCategoryDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AndeanPrimary)
                ) {
                    Text("Crear Categoría")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateCategoryDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo de Confirmación para Eliminar Categoría
    categoryToDelete?.let { cat ->
        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            title = {
                Text(
                    text = "Eliminar Categoría",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = "¿Confirmas que deseas eliminar la categoría '${cat.name}'? Ya no aparecerá en las opciones de registro de nuevos movimientos."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCategory(cat.id)
                        onShowSnackbar("Categoría '${cat.name}' eliminada")
                        categoryToDelete = null
                    }
                ) {
                    Text("Eliminar", fontWeight = FontWeight.Bold, color = Color(0xFFBA1A1A))
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

@Composable
private fun HomeModuleToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
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
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AndeanPrimary
            )
        )
    }
}

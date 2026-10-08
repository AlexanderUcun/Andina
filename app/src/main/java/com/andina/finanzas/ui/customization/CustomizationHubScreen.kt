package com.andina.finanzas.ui.customization

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import com.andina.finanzas.ui.components.muggy.MuggyPose
import com.andina.finanzas.ui.components.muggy.MuggySpeechBubble
import com.andina.finanzas.ui.theme.AndeanPrimary

data class CustomCategory(
    val id: String,
    val name: String,
    val emoji: String,
    val color: Color,
    val isExpense: Boolean,
    var isVisible: Boolean = true
)

/**
 * Hub de Personalización Total del Ecosistema Andina (Módulo 9).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizationHubScreen(
    onNavigateBack: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showBudgetCardOnHome by remember { mutableStateOf(true) }
    var showWalletsCardOnHome by remember { mutableStateOf(true) }
    var showMuggyInsightOnHome by remember { mutableStateOf(true) }
    var showPocketsCardOnHome by remember { mutableStateOf(true) }

    var selectedThemeIndex by remember { mutableStateOf(0) }
    val themeNames = listOf("Páramo Andino 🌿", "Niebla de Cumbre 🏔️", "Laguna Esmeralda 💧", "Tarde Cafetera ☕")

    var categories by remember {
        mutableStateOf(
            listOf(
                CustomCategory("1", "Alimentación & Mercado", "🍔", Color(0xFFE57373), true),
                CustomCategory("2", "Transporte & Gasolina", "🚗", Color(0xFF64B5F6), true),
                CustomCategory("3", "Servicios del Hogar", "💡", Color(0xFFFFB74D), true),
                CustomCategory("4", "Ocio & Restaurantes", "🍿", Color(0xFF81C784), true),
                CustomCategory("5", "Salario & Honorarios", "💼", AndeanPrimary, false),
                CustomCategory("6", "Rendimientos & Ahorros", "📈", Color(0xFF4DB6AC), false)
            )
        )
    }

    var showAddCategoryModal by remember { mutableStateOf(false) }
    var newCatName by remember { mutableStateOf("") }
    var newCatEmoji by remember { mutableStateOf("🛍️") }
    var newCatIsExpense by remember { mutableStateOf(true) }
    var newCatColor by remember { mutableStateOf(Color(0xFFE57373)) }

    val paletteColors = listOf(
        Color(0xFFE57373), Color(0xFF64B5F6), Color(0xFFFFB74D),
        Color(0xFF81C784), Color(0xFFBA68C8), Color(0xFF4DB6AC),
        Color(0xFF587550), Color(0xFF8D6E63)
    )

    val emojiChoices = listOf("🛍️", "🍔", "☕", "🚕", "✈️", "🎮", "💊", "📚", "🏠", "⚡", "🎁", "💼")

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
                actions = {
                    IconButton(onClick = { showAddCategoryModal = true }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Añadir categoría", tint = AndeanPrimary)
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

            // 1. Tarjetas Visibles en el Dashboard de Inicio
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
                            checked = showBudgetCardOnHome,
                            onCheckedChange = { showBudgetCardOnHome = it }
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), thickness = 0.5.dp)
                        HomeModuleToggleRow(
                            title = "Carrusel de Billeteras",
                            subtitle = "Muestra tus cuentas y saldos en Inicio",
                            checked = showWalletsCardOnHome,
                            onCheckedChange = { showWalletsCardOnHome = it }
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), thickness = 0.5.dp)
                        HomeModuleToggleRow(
                            title = "Consejo de Sabiduría de Muggy",
                            subtitle = "Banner con diagnósticos y tips de IA",
                            checked = showMuggyInsightOnHome,
                            onCheckedChange = { showMuggyInsightOnHome = it }
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), thickness = 0.5.dp)
                        HomeModuleToggleRow(
                            title = "Bolsillos de Ahorro",
                            subtitle = "Frailejones vivos y metas en progreso",
                            checked = showPocketsCardOnHome,
                            onCheckedChange = { showPocketsCardOnHome = it }
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
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        themeNames.forEachIndexed { index, theme ->
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
                                Text(
                                    text = theme,
                                    fontSize = 14.sp,
                                    fontWeight = if (selectedThemeIndex == index) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedThemeIndex == index) AndeanPrimary else MaterialTheme.colorScheme.onSurface
                                )
                                if (selectedThemeIndex == index) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = AndeanPrimary)
                                }
                            }
                        }
                    }
                }
            }

            // 3. Gestión de Categorías
            item {
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
                    Text(
                        text = "+ Crear Nueva",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AndeanPrimary,
                        modifier = Modifier.clickable { showAddCategoryModal = true }
                    )
                }
            }

            items(categories, key = { it.id }) { cat ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(cat.color.copy(alpha = 0.18f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = cat.emoji, fontSize = 18.sp)
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
                                    text = if (cat.isExpense) "Gasto" else "Ingreso",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                categories = categories.map {
                                    if (it.id == cat.id) it.copy(isVisible = !it.isVisible) else it
                                }
                                onShowSnackbar(if (!cat.isVisible) "Categoría activada" else "Categoría ocultada")
                            }
                        ) {
                            Icon(
                                imageVector = if (cat.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Visibilidad",
                                tint = if (cat.isVisible) AndeanPrimary else Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Modal Crear Nueva Categoría
    if (showAddCategoryModal) {
        AlertDialog(
            onDismissRequest = { showAddCategoryModal = false },
            title = {
                Text(
                    text = "Nueva Categoría",
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
                            selected = newCatIsExpense,
                            onClick = { newCatIsExpense = true },
                            label = { Text("Gasto") }
                        )
                        FilterChip(
                            selected = !newCatIsExpense,
                            onClick = { newCatIsExpense = false },
                            label = { Text("Ingreso") }
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newCatName,
                        onValueChange = { newCatName = it },
                        label = { Text("Nombre de Categoría") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Selecciona Ícono:", style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        emojiChoices.take(6).forEach { emoji ->
                            Surface(
                                shape = CircleShape,
                                color = if (newCatEmoji == emoji) AndeanPrimary.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .clickable { newCatEmoji = emoji }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = emoji, fontSize = 18.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Selecciona Color:", style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        paletteColors.take(6).forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (newCatColor == color) 3.dp else 0.dp,
                                        color = if (newCatColor == color) Color.White else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { newCatColor = color }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newCatName.isNotBlank()) {
                            categories = categories + CustomCategory(
                                id = System.currentTimeMillis().toString(),
                                name = newCatName.trim(),
                                emoji = newCatEmoji,
                                color = newCatColor,
                                isExpense = newCatIsExpense
                            )
                            showAddCategoryModal = false
                            newCatName = ""
                            onShowSnackbar("¡Categoría ${newCatName.trim()} creada con éxito!")
                        }
                    }
                ) {
                    Text("Crear", fontWeight = FontWeight.Bold, color = AndeanPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryModal = false }) {
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

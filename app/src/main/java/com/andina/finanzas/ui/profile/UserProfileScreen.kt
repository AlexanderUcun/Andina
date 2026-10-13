package com.andina.finanzas.ui.profile

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Brush
import com.andina.finanzas.ui.components.FrostedGlassCard
import com.andina.finanzas.ui.components.bouncyClick
import com.andina.finanzas.ui.theme.AndeanMistCanvasBottom
import com.andina.finanzas.ui.theme.AndeanMistCanvasTop
import com.andina.finanzas.ui.theme.AndeanSlateInk
import com.andina.finanzas.ui.theme.AndeanPrimary
import com.andina.finanzas.ui.theme.AndinaMossGreen
import com.andina.finanzas.ui.theme.FrailejonGold
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andina.finanzas.ui.components.AndinaButton
import com.andina.finanzas.ui.theme.AndinaMossGreen
import com.andina.finanzas.ui.theme.AndeanPrimary

/**
 * Pantalla de Perfil de Usuario y Preferencias del Páramo (Módulo 1).
 * Conectada al UserProfileRepository reactivo y persistido.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    onNavigateBack: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    onNavigateToCustomization: () -> Unit = {},
    onNavigateToCalendar: () -> Unit = {},
    onNavigateToReportExport: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: UserProfileViewModel = hiltViewModel()
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    var showEditNameDialog by remember { mutableStateOf(false) }
    var tempNameInput by remember { mutableStateOf("") }
    var tempNicknameInput by remember { mutableStateOf("") }

    val avatarList = listOf(
        Pair("🐻", "Muggy"),
        Pair("🌿", "Frailejón"),
        Pair("🦅", "Cóndor"),
        Pair("🌸", "Colibrí"),
        Pair("💧", "Laguna"),
        Pair("☕", "Cafetal")
    )

    val nicknamePresets = listOf(
        "Guardián del Frailejón",
        "Frailejón Mayor",
        "Explorador Andino",
        "Caminante de Niebla"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mi Perfil Andino",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AndeanSlateInk
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = AndeanSlateInk
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(AndeanMistCanvasTop, AndeanMistCanvasBottom)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Tarjeta Avatar Hero con Identidad y Progreso del Páramo
                FrostedGlassCard(
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(AndeanPrimary.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userProfile.avatarEmoji.ifBlank { "🦅" },
                            fontSize = 42.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = userProfile.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AndinaMossGreen.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = userProfile.nickname,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = AndeanPrimary
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Barra de Progreso de Nivel (XP Financiero)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Progreso del Rango",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Nivel 3 · 680 / 1000 XP",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = AndeanPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        androidx.compose.material3.LinearProgressIndicator(
                            progress = { 0.68f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = AndeanPrimary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Registra tus gastos diarios y mantén tu presupuesto bajo control para avanzar al siguiente rango.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Selector horizontal de avatares
                    Text(
                        text = "Elige tu avatar del páramo",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        avatarList.forEach { (avatar, label) ->
                            val isSelected = avatar == userProfile.avatarEmoji
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) AndeanPrimary.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        viewModel.updateAvatar(avatar, label)
                                        onShowSnackbar("Avatar actualizado a $label")
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = avatar, fontSize = 22.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    AndinaButton(
                        text = "Editar Nombre y Apodo",
                        onClick = {
                            tempNameInput = userProfile.name
                            tempNicknameInput = userProfile.nickname
                            showEditNameDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 1.1. Cuadrícula Bento 2x2 de Herramientas Clave
            Text(
                text = "Accesos Rápidos del Páramo",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    BentoToolCard(
                        title = "Tema y Módulos",
                        subtitle = "Personalizar Inicio",
                        icon = Icons.Default.Palette,
                        onClick = onNavigateToCustomization,
                        modifier = Modifier.weight(1f)
                    )
                    BentoToolCard(
                        title = "Mis Categorías",
                        subtitle = "Gastos e Ingresos",
                        icon = Icons.Default.AutoAwesome,
                        onClick = onNavigateToCustomization,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    BentoToolCard(
                        title = "Reportes PDF/CSV",
                        subtitle = "Exportar Extractos",
                        icon = Icons.Default.Description,
                        onClick = onNavigateToReportExport,
                        modifier = Modifier.weight(1f)
                    )
                    BentoToolCard(
                        title = "Copia de Seguridad",
                        subtitle = "Respaldo Cifrado",
                        icon = Icons.Default.Lock,
                        onClick = onNavigateToReportExport,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Sección de Preferencias y Rendimiento
            Text(
                text = "Preferencias del Páramo",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            FrostedGlassCard(
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Formato Moneda con icono de pagos
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Payments,
                                contentDescription = null,
                                tint = AndeanPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Moneda del Sistema",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Pesos Colombianos (COP)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Text(
                            text = userProfile.currencyFormat,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = AndeanPrimary)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

                    // Switch Alto Rendimiento con icono de destellos AutoAwesome
                    PreferenceSwitchRow(
                        icon = Icons.Default.AutoAwesome,
                        title = "Modo Gráficos 4K",
                        subtitle = "Animaciones botánicas y efectos de páramo vivos",
                        checked = userProfile.isHighPerformanceMode,
                        onCheckedChange = {
                            viewModel.updatePreferences(
                                isBiometric = userProfile.isBiometricEnabled,
                                isPrivacyDefault = userProfile.isPrivacyByDefault,
                                isHighPerformance = it,
                                currencyFormat = userProfile.currencyFormat
                            )
                            onShowSnackbar(if (it) "Modo 4K Completo activado" else "Modo ahorro de batería activado")
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Sección de Seguridad y Privacidad
            Text(
                text = "Seguridad y Privacidad",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            FrostedGlassCard(
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Switch Biometría
                    PreferenceSwitchRow(
                        icon = Icons.Default.Lock,
                        title = "Bloqueo Biométrico",
                        subtitle = "Solicitar huella o rostro al abrir la app",
                        checked = userProfile.isBiometricEnabled,
                        onCheckedChange = {
                            viewModel.updatePreferences(
                                isBiometric = it,
                                isPrivacyDefault = userProfile.isPrivacyByDefault,
                                isHighPerformance = userProfile.isHighPerformanceMode,
                                currencyFormat = userProfile.currencyFormat
                            )
                            onShowSnackbar(if (it) "Seguridad biométrica activada" else "Seguridad biométrica desactivada")
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

                    // Switch Privacidad
                    PreferenceSwitchRow(
                        icon = Icons.Default.Shield,
                        title = "Modo Oculto por Defecto",
                        subtitle = "Ocultar saldos automáticamente al iniciar",
                        checked = userProfile.isPrivacyByDefault,
                        onCheckedChange = {
                            viewModel.updatePreferences(
                                isBiometric = userProfile.isBiometricEnabled,
                                isPrivacyDefault = it,
                                isHighPerformance = userProfile.isHighPerformanceMode,
                                currencyFormat = userProfile.currencyFormat
                            )
                            onShowSnackbar(if (it) "Modo incógnito activado por defecto" else "Saldos visibles al iniciar")
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Zona de Reinicio de Datos Demo
            var showResetConfirmationDialog by remember { mutableStateOf(false) }
            var showFinalConfirmDialog by remember { mutableStateOf(false) }

            androidx.compose.material3.OutlinedButton(
                onClick = { showResetConfirmationDialog = true },
                shape = RoundedCornerShape(14.dp),
                colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFBA1A1A)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBA1A1A).copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color(0xFFBA1A1A),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reiniciar Datos de Demostración", fontWeight = FontWeight.Bold)
            }

            if (showResetConfirmationDialog) {
                AlertDialog(
                    onDismissRequest = { showResetConfirmationDialog = false },
                    title = { Text("¿Reiniciar Datos de Demostración?", fontWeight = FontWeight.Bold) },
                    text = {
                        Text("Esta acción eliminará todos los movimientos de prueba y dejará las billeteras listas en $0 COP para que inicies tu contabilidad real. ¿Deseas continuar?")
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showResetConfirmationDialog = false
                                showFinalConfirmDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA1A1A))
                        ) {
                            Text("Continuar", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showResetConfirmationDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }

            if (showFinalConfirmDialog) {
                AlertDialog(
                    onDismissRequest = { showFinalConfirmDialog = false },
                    title = { Text("Confirmación Definitiva", fontWeight = FontWeight.Bold, color = Color(0xFFBA1A1A)) },
                    text = {
                        Text("Confirma por favor: ¿Deseas borrar definitivamente las transacciones de prueba?")
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showFinalConfirmDialog = false
                                viewModel.resetDemoData {
                                    onShowSnackbar("Datos de demostración reiniciados. ¡Bienvenido a tu contabilidad real!")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA1A1A))
                        ) {
                            Text("Borrar y Reiniciar", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showFinalConfirmDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

    // Diálogo para editar nombre y apodo
    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = {
                Text(
                    text = "Editar Identidad",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = tempNameInput,
                        onValueChange = { tempNameInput = it },
                        label = { Text("Nombre") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = tempNicknameInput,
                        onValueChange = { tempNicknameInput = it },
                        label = { Text("Apodo en el Páramo") },
                        placeholder = { Text("Ej. Guardián del Frailejón") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sugerencias de títulos:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        nicknamePresets.take(2).forEach { preset ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { tempNicknameInput = preset }
                            ) {
                                Text(
                                    text = preset,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempNameInput.isNotBlank()) {
                            val newName = tempNameInput.trim()
                            val newNickname = tempNicknameInput.trim().ifEmpty { "Guardián del Frailejón" }
                            viewModel.updateIdentity(newName, newNickname)
                            showEditNameDialog = false
                            onShowSnackbar("Perfil actualizado con éxito")
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AndeanPrimary)
                ) {
                    Text("Guardar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }
}

@Composable
private fun PreferenceSwitchRow(
    icon: ImageVector,
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
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AndeanPrimary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
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

@Composable
private fun BentoToolCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FrostedGlassCard(
        shape = RoundedCornerShape(16.dp),
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(AndeanPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AndeanPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

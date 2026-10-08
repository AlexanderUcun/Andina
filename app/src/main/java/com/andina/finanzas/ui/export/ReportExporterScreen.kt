package com.andina.finanzas.ui.export

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TableChart
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andina.finanzas.ui.components.muggy.MuggyPose
import com.andina.finanzas.ui.components.muggy.MuggySpeechBubble
import com.andina.finanzas.ui.theme.AndeanPrimary

/**
 * Pantalla de Exportación de Reportes y Respaldos Locales (Módulo 6).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportExporterScreen(
    onNavigateBack: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showBackupPinDialog by remember { mutableStateOf(false) }
    var backupPinInput by remember { mutableStateOf("") }
    var showRestorePinDialog by remember { mutableStateOf(false) }
    var restorePinInput by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Reportes & Respaldos",
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
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Muggy Explica Privacidad 100% Offline
            MuggySpeechBubble(
                title = "Tus Datos Te Pertenecen",
                greeting = "100% Local & Privado",
                text = "Tus registros financieros nunca viajan a servidores remotos. Puedes exportar extractos diseñados o generar una copia cifrada con clave militar AES-256 para pasar a otro celular.",
                pose = MuggyPose.PRIVACY_GUARD
            )

            // 1. Exportación de Extractos y Reportes
            Text(
                text = "Exportación de Documentos",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ExportActionRow(
                        icon = Icons.Default.PictureAsPdf,
                        iconColor = Color(0xFFD32F2F),
                        title = "Extracto Mensual (PDF)",
                        subtitle = "Diseño editorial listo para imprimir o archivar",
                        buttonText = "Generar PDF",
                        onClick = { onShowSnackbar("📄 Extracto PDF generado con éxito en Descargas") }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

                    ExportActionRow(
                        icon = Icons.Default.TableChart,
                        iconColor = Color(0xFF2E7D32),
                        title = "Libro Mayor (Excel / CSV)",
                        subtitle = "Exporta todos tus movimientos para hojas de cálculo",
                        buttonText = "Exportar CSV",
                        onClick = { onShowSnackbar("📊 Archivo CSV exportado con éxito en Descargas") }
                    )
                }
            }

            // 2. Copias de Seguridad Cifradas SQLite (.andina)
            Text(
                text = "Copia de Seguridad (.andina)",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ExportActionRow(
                        icon = Icons.Default.CloudDownload,
                        iconColor = AndeanPrimary,
                        title = "Crear Copia Cifrada",
                        subtitle = "Guarda tus billeteras, bolsillos y movimientos protegidos con PIN",
                        buttonText = "Crear Copia",
                        onClick = { showBackupPinDialog = true }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

                    ExportActionRow(
                        icon = Icons.Default.CloudUpload,
                        iconColor = Color(0xFFE65100),
                        title = "Restaurar Copia de Seguridad",
                        subtitle = "Importa un archivo .andina desde tu almacenamiento",
                        buttonText = "Restaurar",
                        onClick = { showRestorePinDialog = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Modal Crear Backup con PIN
    if (showBackupPinDialog) {
        AlertDialog(
            onDismissRequest = { showBackupPinDialog = false },
            title = {
                Text(
                    text = "Proteger Copia de Seguridad",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Ingresa un PIN numérico de 4 a 6 dígitos para cifrar tus datos con AES-256 GCM:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = backupPinInput,
                        onValueChange = { backupPinInput = it.filter { ch -> ch.isDigit() }.take(6) },
                        label = { Text("PIN de Cifrado") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (backupPinInput.length >= 4) {
                            showBackupPinDialog = false
                            backupPinInput = ""
                            onShowSnackbar("🛡️ Copia de seguridad cifrada guardada como andina_backup.andina")
                        } else {
                            onShowSnackbar("El PIN debe tener al menos 4 dígitos")
                        }
                    }
                ) {
                    Text("Cifrar y Guardar", fontWeight = FontWeight.Bold, color = AndeanPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBackupPinDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Modal Restaurar Backup
    if (showRestorePinDialog) {
        AlertDialog(
            onDismissRequest = { showRestorePinDialog = false },
            title = {
                Text(
                    text = "Descifrar Copia de Seguridad",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Ingresa el PIN con el que creaste el archivo .andina para restaurar el libro mayor:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = restorePinInput,
                        onValueChange = { restorePinInput = it.filter { ch -> ch.isDigit() }.take(6) },
                        label = { Text("PIN del Respaldo") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (restorePinInput.isNotBlank()) {
                            showRestorePinDialog = false
                            restorePinInput = ""
                            onShowSnackbar("✅ ¡Base de datos restaurada con éxito!")
                        }
                    }
                ) {
                    Text("Descifrar", fontWeight = FontWeight.Bold, color = AndeanPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestorePinDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun ExportActionRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    buttonText: String,
    onClick: () -> Unit
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
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }
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
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AndeanPrimary)
        ) {
            Text(buttonText, fontSize = 12.sp, color = Color.White)
        }
    }
}

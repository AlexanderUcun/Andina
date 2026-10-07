package com.andina.finanzas.ui.pockets.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.view.HapticFeedbackConstants
import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.ui.theme.AndinaDeepForest
import com.andina.finanzas.ui.theme.AndinaMossGreen

data class PocketIconOption(val key: String, val icon: ImageVector, val label: String)
data class PocketColorOption(val hex: String, val color: Color, val name: String)

/**
 * Modal BottomSheet para la creación de una nueva meta de ahorro (Bolsillo).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PocketFormBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onCreatePocket: (name: String, goalAmount: Long, icon: String, colorHex: String) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    if (!isVisible) return

    val view = LocalView.current
    var name by remember { mutableStateOf("") }
    var goalDigits by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("savings") }
    var selectedColorHex by remember { mutableStateOf("#435C3C") }

    val iconOptions = remember {
        listOf(
            PocketIconOption("savings", Icons.Default.Savings, "Ahorro"),
            PocketIconOption("flight", Icons.Default.Flight, "Viaje"),
            PocketIconOption("home", Icons.Default.Home, "Hogar"),
            PocketIconOption("directions_bike", Icons.AutoMirrored.Filled.DirectionsBike, "Transporte"),
            PocketIconOption("school", Icons.Default.School, "Educación"),
            PocketIconOption("favorite", Icons.Default.Favorite, "Salud"),
            PocketIconOption("sports_esports", Icons.Default.SportsEsports, "Hobbies")
        )
    }

    val colorOptions = remember {
        listOf(
            PocketColorOption("#435C3C", Color(0xFF435C3C), "Verde Musgo"),
            PocketColorOption("#C58934", Color(0xFFC58934), "Ámbar Páramo"),
            PocketColorOption("#2A7B9B", Color(0xFF2A7B9B), "Azul Laguna"),
            PocketColorOption("#8B5E3C", Color(0xFF8B5E3C), "Pardo Tallo"),
            PocketColorOption("#6B4C82", Color(0xFF6B4C82), "Morado Andino")
        )
    }

    val goalAmount = goalDigits.toLongOrNull() ?: 0L
    val canSubmit = name.isNotBlank() && goalAmount > 0L

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(AndinaMossGreen.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Savings,
                        contentDescription = null,
                        tint = AndinaMossGreen,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "Sembrar Nueva Meta",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = AndinaDeepForest
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Campo Nombre
            Text(
                text = "Nombre de la Meta",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4B5563)
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { if (it.length <= 40) name = it },
                placeholder = { Text("Ej. Fondo de Emergencia, Vacaciones...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AndinaMossGreen,
                    unfocusedBorderColor = Color(0xFFE5E7EB)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Meta en COP
            Text(
                text = "Meta en Dinero (COP)",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4B5563)
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = goalDigits,
                onValueChange = { input ->
                    val filtered = input.filter { it.isDigit() }.take(11)
                    goalDigits = filtered.trimStart('0')
                },
                placeholder = { Text("0") },
                prefix = { Text("$ ", fontWeight = FontWeight.Bold) },
                suffix = { Text(" COP", color = Color(0xFF9CA3AF)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                supportingText = {
                    if (goalAmount > 0L) {
                        Text(
                            text = Money(goalAmount).formatCop(),
                            fontWeight = FontWeight.SemiBold,
                            color = AndinaMossGreen
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AndinaMossGreen,
                    unfocusedBorderColor = Color(0xFFE5E7EB)
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Selector de Ícono
            Text(
                text = "Ícono Distintivo",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4B5563)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(iconOptions, key = { it.key }) { opt ->
                    val isSelected = opt.key == selectedIcon
                    Surface(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) AndinaMossGreen else Color(0xFFE5E7EB),
                                shape = CircleShape
                            )
                            .clickable {
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                selectedIcon = opt.key
                            },
                        shape = CircleShape,
                        color = if (isSelected) AndinaMossGreen.copy(alpha = 0.12f) else Color.White
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = opt.icon,
                                contentDescription = opt.label,
                                tint = if (isSelected) AndinaMossGreen else Color(0xFF6B7280),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selector de Color
            Text(
                text = "Color de la Meta",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4B5563)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                colorOptions.forEach { opt ->
                    val isSelected = opt.hex == selectedColorHex
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(opt.color)
                            .clickable {
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                selectedColorHex = opt.hex
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Seleccionado",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Crear Meta
            Button(
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                    onCreatePocket(name, goalAmount, selectedIcon, selectedColorHex)
                },
                enabled = canSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AndinaMossGreen,
                    disabledContainerColor = Color(0xFFE5E7EB)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sembrar Meta en el Páramo",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }
    }
}

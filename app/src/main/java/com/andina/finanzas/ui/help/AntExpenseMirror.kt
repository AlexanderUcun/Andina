package com.andina.finanzas.ui.help

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.ui.components.AndeanHyperCard
import com.andina.finanzas.ui.theme.LocalAndinaColors
import com.andina.finanzas.ui.components.muggy.MuggyAvatar
import com.andina.finanzas.ui.components.muggy.MuggyPose
import com.andina.finanzas.ui.theme.AndeanSlateInk
import com.andina.finanzas.ui.theme.FrailejonGold

data class AntSampleItem(
    val emoji: String,
    val name: String,
    val costCop: Long
)

/**
 * "El Espejo Anual": Simulador interactivo de gastos hormiga para concientización sin culpa (AYUDAS_INTERACTIVAS.md §2.3).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AntExpenseMirrorCard(
    modifier: Modifier = Modifier,
    onCreatePocketWithSavings: ((suggestedName: String, yearlyAmount: Long) -> Unit)? = null
) {
    val sampleItems = remember {
        listOf(
            AntSampleItem("☕", "Café diario", 6_000L),
            AntSampleItem("🥟", "Empanada", 4_000L),
            AntSampleItem("🚗", "Taxi corto", 12_000L),
            AntSampleItem("🍬", "Snacks / Dulces", 3_500L),
            AntSampleItem("🥤", "Bebida / Gaseosa", 4_500L)
        )
    }

    var selectedItem by remember { mutableStateOf(sampleItems[0]) }
    var timesPerWeek by remember { mutableFloatStateOf(5f) }

    val weeklyCost = selectedItem.costCop * timesPerWeek.toLong()
    val monthlyCost = (weeklyCost * 4.33).toLong()
    val yearlyCost = weeklyCost * 52L

    val equivalentDream = when {
        yearlyCost > 2_500_000L -> "¡Un viaje completo ida y vuelta a San Andrés o Cartagena!"
        yearlyCost > 1_500_000L -> "Un computador nuevo o renovar tu celular con holgura."
        yearlyCost > 800_000L -> "Tu fondo de emergencia para 3 meses de imprevistos."
        else -> "Un fin de semana inolvidable de descanso en Villa de Leyva."
    }

    AndeanHyperCard(
        shape = RoundedCornerShape(24.dp),
        contentPadding = 20.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            // Encabezado
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                MuggyAvatar(
                    size = 46.dp,
                    pose = MuggyPose.REFLECTIVE,
                    isInteractive = false
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "El Espejo Anual 🐜",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1B19)
                    )
                    Text(
                        text = "¿Cuánto suman tus micro-gastos cotidianos?",
                        fontSize = 12.sp,
                        color = Color(0xFF6B655D)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Chips interactivos de ejemplos de antojos
            Text(
                text = "Selecciona un antojo frecuente:",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF3B362F)
            )

            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                sampleItems.forEach { item ->
                    val isSelected = item == selectedItem
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedItem = item },
                        label = {
                            Text(
                                "${item.emoji} ${item.name} (${Money(item.costCop).formatCop()})",
                                fontSize = 12.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF435C3C),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFFECE7DE),
                            labelColor = Color(0xFF3B362F)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Frecuencia semanal
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Frecuencia semanal:",
                    fontSize = 13.sp,
                    color = Color(0xFF4A453E)
                )
                Text(
                    text = "${timesPerWeek.toInt()} veces por semana",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF845400)
                )
            }

            Slider(
                value = timesPerWeek,
                onValueChange = { timesPerWeek = it },
                valueRange = 1f..7f,
                steps = 5,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF845400),
                    activeTrackColor = Color(0xFF845400),
                    inactiveTrackColor = Color(0xFFE2DCD1)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Revelación en números grandes
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9EE)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Impacto proyectado en 1 año:",
                        fontSize = 12.sp,
                        color = Color(0xFF845400),
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    AnimatedContent(
                        targetState = yearlyCost,
                        label = "YearlyCostAnimation"
                    ) { cost ->
                        Column {
                            Text(
                                text = Money(cost).formatCop(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF845400)
                            )
                            Text(
                                text = "(${Money(monthlyCost).formatCop()} al mes)",
                                fontSize = 13.sp,
                                color = Color(0xFF6B655D)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "💡 Equivalencia: $equivalentDream",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2C241E),
                        lineHeight = 18.sp
                    )

                    if (onCreatePocketWithSavings != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                onCreatePocketWithSavings("Meta de Ahorro Antojos", yearlyCost)
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF435C3C)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("🌱 Crear Bolsillo con este ahorro", fontSize = 13.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

package com.andina.finanzas.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andina.finanzas.ui.navigation.AndinaNavTab
import com.andina.finanzas.ui.theme.AndeanPrimaryContainer
import com.andina.finanzas.ui.theme.AndinaTheme

/**
 * Barra de navegación inferior insignia de Andina Finanzas.
 *
 * Arquitectura y distribución:
 * - 4 pestañas simétricas: `[ Inicio ]` · `[ Bolsillos ]` · 🟢 `(+)` · `[ Billeteras ]` · `[ Movimientos ]`
 * - Botón central flotante (+) elevado -12.dp con halo de sombra verde andino y micro-animación de escala tipo resorte.
 * - Área táctil accesible mínima de 48dp en cada pestaña.
 * - SafeDrawing mediante WindowInsets.navigationBars.
 * - Alineación correcta de Column (`Alignment.CenterHorizontally`) previniendo fallos de layout.
 *
 * @param currentTab Pestaña actualmente seleccionada en el grafo de navegación.
 * @param onTabSelected Callback invocado al seleccionar una pestaña.
 * @param onRegisterClick Callback invocado al pulsar el botón central flotante (+).
 * @param modifier Modificador opcional.
 */
@Composable
fun AndinaBottomBar(
    currentTab: AndinaNavTab,
    onTabSelected: (AndinaNavTab) -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Superficie base de la barra
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Línea divisoria sutil superior
                HorizontalDivider(
                    thickness = 0.8.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Pestañas izquierdas: [ Inicio ] y [ Bolsillos ]
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AndinaNavTab.leftTabs.forEach { tab ->
                            BottomBarTabItem(
                                tab = tab,
                                isSelected = tab == currentTab,
                                onClick = {
                                    if (tab != currentTab) {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        onTabSelected(tab)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Espaciador central reservado para el botón flotante (+)
                    Spacer(modifier = Modifier.width(68.dp))

                    // Pestañas derechas: [ Billeteras ] y [ Movimientos ]
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AndinaNavTab.rightTabs.forEach { tab ->
                            BottomBarTabItem(
                                tab = tab,
                                isSelected = tab == currentTab,
                                onClick = {
                                    if (tab != currentTab) {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        onTabSelected(tab)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Botón Central Protagonista (+) Flotante
        CenterFloatingActionButton(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onRegisterClick()
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-16).dp)
        )
    }
}

/**
 * Ítem individual de navegación para la barra inferior.
 * Cumple con un área táctil mínima de 48dp y alineación horizontal centrada.
 */
@Composable
private fun BottomBarTabItem(
    tab: AndinaNavTab,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) activeColor else inactiveColor,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "BottomBarColor_${tab.name}"
    )

    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "BottomBarScale_${tab.name}"
    )

    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                onClick = onClick,
                role = Role.Tab,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, radius = 28.dp)
            )
            .semantics {
                contentDescription = tab.title
                role = Role.Tab
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier
                    .size(24.dp)
                    .scale(iconScale)
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = tab.title,
                color = contentColor,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    letterSpacing = 0.2.sp
                ),
                maxLines = 1
            )
        }
    }
}

/**
 * Botón (+) central flotante con elevación de -12dp a -16dp, halo de resplandor verde andino
 * y animación de pulsación háptica.
 */
@Composable
private fun CenterFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "FabPressScale"
    )

    // Resplandor cálido verde andino
    val glowBrush = Brush.radialGradient(
        colors = listOf(
            Color(0x66435C3C),
            Color(0x1A435C3C),
            Color.Transparent
        )
    )

    Box(
        modifier = modifier
            .size(76.dp),
        contentAlignment = Alignment.Center
    ) {
        // Halo difuso de resplandor
        Box(
            modifier = Modifier
                .size(74.dp)
                .clip(CircleShape)
                .background(glowBrush)
        )

        // Botón circular principal de 54dp
        Box(
            modifier = Modifier
                .size(54.dp)
                .scale(scale)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    spotColor = Color(0x66435C3C),
                    ambientColor = Color(0x33435C3C)
                )
                .clip(CircleShape)
                .background(AndeanPrimaryContainer)
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(bounded = true, color = Color.White),
                    role = Role.Button,
                    onClick = onClick
                )
                .semantics {
                    contentDescription = "Registrar nuevo gasto o ingreso rápido"
                    role = Role.Button
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Preview(name = "AndinaBottomBar - Inicio Seleccionado", showBackground = true)
@Composable
private fun AndinaBottomBarPreviewHome() {
    AndinaTheme {
        AndinaBottomBar(
            currentTab = AndinaNavTab.HOME,
            onTabSelected = {},
            onRegisterClick = {}
        )
    }
}

@Preview(name = "AndinaBottomBar - Modo Oscuro", showBackground = true)
@Composable
private fun AndinaBottomBarPreviewDark() {
    AndinaTheme(darkTheme = true) {
        AndinaBottomBar(
            currentTab = AndinaNavTab.POCKETS,
            onTabSelected = {},
            onRegisterClick = {}
        )
    }
}

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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import com.andina.finanzas.ui.theme.LocalAndinaColors
import com.andina.finanzas.ui.theme.specularGlassBorder

/**
 * Barra de navegación inferior insignia de Andina Finanzas.
 *
 * Arquitectura y distribución:
 * - 4 pestañas simétricas: `[ Inicio ]` · `[ Movimientos ]` · 🟡 `(+)` · `[ Patrimonio ]` · `[ Plan & IA ]`
 * - Botón central flotante (+) elevado con halo áureo y micro-animación de escala tipo resorte.
 * - Fondo integrado sin cortes con gradiente suave del canvas.
 * - Iconos con paleta viva Platinum Light Luxury (Oro, Zafiro, Esmeralda, Amatista).
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
    val colors = LocalAndinaColors.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        colors.canvasBottom.copy(alpha = 0.70f),
                        colors.canvasBottom
                    )
                )
            )
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 6.dp)
                .height(68.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(26.dp),
                    ambientColor = colors.ambientShadow,
                    spotColor = colors.ambientShadow
                )
                .clip(RoundedCornerShape(26.dp))
                .specularGlassBorder(
                    strokeWidth = 1.dp,
                    shape = RoundedCornerShape(26.dp),
                    topLightColor = colors.specularBorderTop,
                    bottomShadowColor = colors.cardBorder
                ),
            color = colors.cardBackground.copy(alpha = 0.96f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pestañas izquierdas: [ Inicio ] y [ Movimientos ]
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
                Spacer(modifier = Modifier.width(64.dp))

                // Pestañas derechas: [ Patrimonio ] y [ Plan & IA ]
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
 * Con micro-cápsula luminosa y colores vivos de la paleta de lujo.
 */
@Composable
private fun BottomBarTabItem(
    tab: AndinaNavTab,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAndinaColors.current

    // Colores vivos con identidad de lujo (Platinum Light Luxury)
    val tabActiveColor = when (tab) {
        AndinaNavTab.HOME -> colors.navHome                // Oro Solar Champagne
        AndinaNavTab.TRANSACTIONS -> colors.navTransactions // Azul Zafiro Glaciar
        AndinaNavTab.WALLETS -> colors.navWallets          // Esmeralda Andina Vibrante
        AndinaNavTab.CONSULTORIO -> colors.navConsultorio  // Amatista Cuarzo / Violeta IA
    }
    val inactiveColor = colors.navInactive

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) tabActiveColor else inactiveColor,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "BottomBarColor_${tab.name}"
    )

    val capsuleBgColor by animateColorAsState(
        targetValue = if (isSelected) tabActiveColor.copy(alpha = 0.14f) else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "BottomBarCapsule_${tab.name}"
    )

    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) 1.12f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "BottomBarScale_${tab.name}"
    )

    Box(
        modifier = modifier
            .height(58.dp)
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
            // Micro-cápsula con halo del color distintivo
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(capsuleBgColor)
                    .padding(horizontal = 10.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier
                        .size(24.dp)
                        .scale(iconScale)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = tab.title,
                color = contentColor,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.5.sp,
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                    letterSpacing = 0.15.sp
                ),
                maxLines = 1
            )
        }
    }
}

/**
 * Botón (+) central flotante con elevación, halo áureo y bisel especular.
 */
@Composable
private fun CenterFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAndinaColors.current
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

    val goldSolar = colors.navHome
    // Resplandor áureo cálido
    val glowBrush = Brush.radialGradient(
        colors = listOf(
            goldSolar.copy(alpha = 0.45f),
            goldSolar.copy(alpha = 0.12f),
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

        // Botón circular principal de 54dp con gradiente Oro Solar rico y bisel
        Box(
            modifier = Modifier
                .size(54.dp)
                .scale(scale)
                .shadow(
                    elevation = 10.dp,
                    shape = CircleShape,
                    spotColor = goldSolar.copy(alpha = 0.55f),
                    ambientColor = colors.ambientShadow
                )
                .clip(CircleShape)
                .background(colors.fabGoldGradient)
                .specularGlassBorder(
                    strokeWidth = 1.dp,
                    shape = CircleShape,
                    topLightColor = colors.specularBorderTop.copy(alpha = 0.7f),
                    bottomShadowColor = colors.cardBorder
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(bounded = true, color = colors.cardBackground),
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
                tint = colors.cardBackground,
                modifier = Modifier.size(28.dp)
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
            currentTab = AndinaNavTab.TRANSACTIONS,
            onTabSelected = {},
            onRegisterClick = {}
        )
    }
}

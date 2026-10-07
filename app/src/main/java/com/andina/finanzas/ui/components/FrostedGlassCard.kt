package com.andina.finanzas.ui.components

import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.andina.finanzas.ui.theme.AndeanDarkFrostedGlass
import com.andina.finanzas.ui.theme.AndeanFrostedGlassBorder
import com.andina.finanzas.ui.theme.AndeanFrostedGlassShadow
import com.andina.finanzas.ui.theme.AndeanFrostedGlassSurface
import com.andina.finanzas.ui.theme.AndinaTheme
import com.andina.finanzas.ui.theme.CornerLarge

/**
 * Tarjeta Insignia con Efecto de Vidrio Esmerilado (Apple Frosted Glass).
 *
 * Implementa las directrices de "Lujo Silencioso" y "Andean Mist & Moss":
 * - Fondo translúcido (76% opacidad en claro, tinte carbón/musgo en oscuro).
 * - Borde perimetral ultrafino de 1.dp con opacidad sutil (85%).
 * - Sombra difusa de elevación baja (2.dp) que simula flotación sobre el gradiente andino.
 * - Soporte nativo para desenfoque en Android 12+ (API 31+) con degradación limpia.
 *
 * @param modifier Modificador de layout.
 * @param shape Forma de esquinas (por defecto [CornerLarge] de 18.dp).
 * @param elevation Elevación de sombra sutil.
 * @param onClick Acción opcional de pulsación; si es nulo, la tarjeta actúa como contenedor pasivo.
 * @param content Contenido interno organizado verticalmente con [ColumnScope].
 */
@Composable
fun FrostedGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = CornerLarge,
    elevation: Dp = 2.dp,
    backgroundColor: Color? = null,
    borderColor: Color? = null,
    onClick: (() -> Unit)? = null,
    contentPadding: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val isDark = isSystemInDarkTheme()

    val resolvedBackground = backgroundColor ?: if (isDark) {
        Color(0x33222820) // Carbón con tinte musgo translúcido
    } else {
        AndeanFrostedGlassSurface
    }

    val resolvedBorderColor = borderColor ?: if (isDark) {
        Color(0x33FFFFFF) // Borde blanco sutil 20%
    } else {
        AndeanFrostedGlassBorder
    }

    // Contenedor principal con sombra y forma
    Surface(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = AndeanFrostedGlassShadow,
                spotColor = AndeanFrostedGlassShadow
            )
            .clip(shape)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        role = Role.Button,
                        onClick = onClick
                    )
                } else Modifier
            ),
        shape = shape,
        color = resolvedBackground,
        border = BorderStroke(width = 1.dp, color = resolvedBorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            content = content
        )
    }
}

@Preview(name = "Frosted Glass - Modo Claro", showBackground = true)
@Composable
private fun FrostedGlassCardLightPreview() {
    AndinaTheme(darkTheme = false) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFF5F3EF), Color(0xFFEBE6DC))
                    )
                )
                .padding(24.dp)
        ) {
            FrostedGlassCard {
                Text(
                    text = "Patrimonio Total",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$ 14.850.000 COP",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview(name = "Frosted Glass - Modo Oscuro", showBackground = true)
@Composable
private fun FrostedGlassCardDarkPreview() {
    AndinaTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .background(Color(0xFF121411))
                .padding(24.dp)
        ) {
            FrostedGlassCard {
                Text(
                    text = "Patrimonio Total",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$ 14.850.000 COP",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

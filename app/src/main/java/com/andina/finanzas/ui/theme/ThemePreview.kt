package com.andina.finanzas.ui.theme

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Galería de previsualización de componentes y tokens del sistema "Andean Mist & Moss".
 */
@Composable
fun ThemePreviewContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Andina — Sistema Visual",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "$ 14.850.000 COP",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Tipografía Manrope con números tabulares (tnum) y contraste WCAG AA.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "PALETA DE COLORES SEMÁNTICOS",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ColorChip(name = "Primario", color = MaterialTheme.colorScheme.primary, textColor = MaterialTheme.colorScheme.onPrimary)
            ColorChip(name = "Contenedor", color = MaterialTheme.colorScheme.primaryContainer, textColor = MaterialTheme.colorScheme.onPrimaryContainer)
            ColorChip(name = "Secundario", color = MaterialTheme.colorScheme.secondary, textColor = MaterialTheme.colorScheme.onSecondary)
            ColorChip(name = "Alerta", color = MaterialTheme.colorScheme.secondaryContainer, textColor = MaterialTheme.colorScheme.onSecondaryContainer)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "BILLETERAS COLOMBIANAS",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WalletBadge(name = "Nequi", brandColor = WalletBrandColors.Nequi, letter = "N")
            WalletBadge(name = "Daviplata", brandColor = WalletBrandColors.Daviplata, letter = "D")
            WalletBadge(name = "Bancolombia", brandColor = WalletBrandColors.Bancolombia, letter = "B")
            WalletBadge(name = "Efectivo", brandColor = WalletBrandColors.Cash, letter = "$")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "FORMAS Y ELEVACIÓN",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )

        Card(
            shape = CornerLarge,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Tarjeta Andina Frosted (18dp)",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Diseño minimalista con respiración visual generosa y lujo silencioso.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ColorChip(name: String, color: Color, textColor: Color) {
    Surface(
        color = color,
        shape = CornerMedium,
        modifier = Modifier.size(width = 76.dp, height = 48.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelSmall,
                color = textColor
            )
        }
    }
}

@Composable
private fun WalletBadge(name: String, brandColor: Color, letter: String) {
    Surface(
        color = brandColor,
        shape = CornerSmall,
        modifier = Modifier.size(width = 76.dp, height = 36.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "$letter $name",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White
            )
        }
    }
}

@Preview(name = "Modo Claro — Andean Mist & Moss", showBackground = true)
@Composable
fun ThemePreviewLight() {
    AndinaTheme(darkTheme = false) {
        ThemePreviewContent()
    }
}

@Preview(name = "Modo Oscuro — Páramo Nocturno", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ThemePreviewDark() {
    AndinaTheme(darkTheme = true) {
        ThemePreviewContent()
    }
}

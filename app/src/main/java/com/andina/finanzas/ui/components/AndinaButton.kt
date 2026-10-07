package com.andina.finanzas.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andina.finanzas.ui.theme.AndeanAntExpenseAmber
import com.andina.finanzas.ui.theme.AndeanOnPrimary
import com.andina.finanzas.ui.theme.AndeanPrimary
import com.andina.finanzas.ui.theme.AndeanPrimaryContainer
import com.andina.finanzas.ui.theme.AndeanRegisterActionGreen
import com.andina.finanzas.ui.theme.AndinaTheme
import com.andina.finanzas.ui.theme.CornerPill

/**
 * Variantes semánticas de botones oficiales en Andina.
 */
enum class AndinaButtonVariant {
    /** Botón de acción principal verde musgo */
    Primary,
    /** Botón caramelo/ámbar especial para marcación de gasto hormiga */
    AntExpense,
    /** Botón verde de registro [ Guardar Gasto ] */
    RegisterAction,
    /** Botón secundario/tonal con fondo neutro suave */
    Secondary,
    /** Botón con delineado fino sin relleno */
    Outlined
}

/**
 * Botón Háptico y Accesible Oficial de Andina.
 *
 * Blindajes técnicos incorporados (conforme a LECCIONES_Y_BLINDAJE.md):
 * 1. Área táctil accesible mínima de 48dp (WCAG AA).
 * 2. Protección anti-doble tap (ignora pulsaciones sucesivas con intervalo < 500 ms).
 * 3. Estado de carga reactivo (`isSaving`/`isLoading`): reemplaza texto con spinner suave y bloquea clics.
 * 4. Micro-pulso háptico inmediato en cada pulsación válida.
 *
 * @param text Etiqueta visible del botón.
 * @param onClick Callback invocado al presionar el botón.
 * @param modifier Modificador Compose externo.
 * @param enabled Si el botón se encuentra activo para interacción.
 * @param isSaving Si hay una operación asíncrona en curso (muestra spinner y desactiva pulsación).
 * @param variant Variante visual semántica del botón.
 * @param shape Forma de recorte y contorno (por defecto [CornerPill]).
 * @param minHeight Altura mínima táctil (por defecto 52.dp >= 48.dp accesibles).
 * @param leadingIcon Ícono opcional a la izquierda del texto.
 * @param trailingIcon Ícono opcional a la derecha del texto.
 */
@Composable
fun AndinaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isSaving: Boolean = false,
    variant: AndinaButtonVariant = AndinaButtonVariant.Primary,
    shape: Shape = CornerPill,
    minHeight: Dp = 52.dp,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    val haptic = LocalHapticFeedback.current
    var lastClickTimestamp by remember { mutableLongStateOf(0L) }
    val debounceIntervalMs = 500L

    val isInteractive = enabled && !isSaving

    // Resolución de colores según variante y estado
    val (backgroundColor, contentColor, borderStroke) = when (variant) {
        AndinaButtonVariant.Primary -> {
            if (enabled) {
                Triple(AndeanPrimaryContainer, Color.White, null)
            } else {
                Triple(Color(0xFFE2E4DE), Color(0xFF90948D), null)
            }
        }
        AndinaButtonVariant.RegisterAction -> {
            if (enabled) {
                Triple(AndeanRegisterActionGreen, Color.White, null)
            } else {
                Triple(Color(0xFFE2E4DE), Color(0xFF90948D), null)
            }
        }
        AndinaButtonVariant.AntExpense -> {
            if (enabled) {
                Triple(AndeanAntExpenseAmber, Color.White, null)
            } else {
                Triple(Color(0xFFE8E0D7), Color(0xFFA1978E), null)
            }
        }
        AndinaButtonVariant.Secondary -> {
            if (enabled) {
                Triple(Color(0xFFF0EBE5), MaterialTheme.colorScheme.onSurface, null)
            } else {
                Triple(Color(0xFFF7F5F2), Color(0xFFB5B0A9), null)
            }
        }
        AndinaButtonVariant.Outlined -> {
            if (enabled) {
                Triple(
                    Color.Transparent,
                    MaterialTheme.colorScheme.onSurface,
                    BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                )
            } else {
                Triple(
                    Color.Transparent,
                    Color(0xFFB5B0A9),
                    BorderStroke(1.dp, Color(0xFFE5E2DC))
                )
            }
        }
    }

    Surface(
        modifier = modifier
            .defaultMinSize(minHeight = minHeight)
            .clip(shape)
            .then(
                if (isInteractive) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = contentColor.copy(alpha = 0.24f)),
                        role = Role.Button,
                        onClick = {
                            val now = System.currentTimeMillis()
                            if (now - lastClickTimestamp >= debounceIntervalMs) {
                                lastClickTimestamp = now
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onClick()
                            }
                        }
                    )
                } else Modifier
            ),
        shape = shape,
        color = backgroundColor,
        border = borderStroke
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .defaultMinSize(minHeight = minHeight - 24.dp),
            contentAlignment = Alignment.Center
        ) {
            // Spinner de carga animado cuando isSaving es true
            AnimatedVisibility(
                visible = isSaving,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = contentColor,
                    strokeWidth = 2.5.dp
                )
            }

            // Contenido textual e íconos (oculto suavemente durante el guardado)
            AnimatedVisibility(
                visible = !isSaving,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (leadingIcon != null) {
                        leadingIcon()
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Text(
                        text = text,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = contentColor
                    )

                    if (trailingIcon != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        trailingIcon()
                    }
                }
            }
        }
    }
}

@Preview(name = "AndinaButton - Variantes", showBackground = true)
@Composable
private fun AndinaButtonPreview() {
    AndinaTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AndinaButton(
                    text = "Guardar",
                    onClick = {}
                )
                AndinaButton(
                    text = "Guardando",
                    onClick = {},
                    isSaving = true
                )
                AndinaButton(
                    text = "Gasto Hormiga",
                    variant = AndinaButtonVariant.AntExpense,
                    onClick = {}
                )
            }
        }
    }
}

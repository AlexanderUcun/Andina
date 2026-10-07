package com.andina.finanzas.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ============================================================================
// 🎨 PALETA OFICIAL "ANDEAN MIST & MOSS" (MATERIAL 3)
// ============================================================================

// Tonos Primarios (Verde Musgo Andino)
val AndeanPrimary = Color(0xFF2C4427)
val AndeanOnPrimary = Color(0xFFFFFFFF)
val AndeanPrimaryContainer = Color(0xFF435C3C)
val AndeanOnPrimaryContainer = Color(0xFFB6D3AB)

// Tonos Secundarios (Ámbar Anteojos - Muggy)
val AndeanSecondary = Color(0xFF845400)
val AndeanOnSecondary = Color(0xFFFFFFFF)
val AndeanSecondaryContainer = Color(0xFFFEBD67)
val AndeanOnSecondaryContainer = Color(0xFF764B00)

// Superficies Claras (Niebla Matutina)
val AndeanSurface = Color(0xFFFFF8F4)
val AndeanOnSurface = Color(0xFF1D1B19) // Carbón Mineral
val AndeanSurfaceContainer = Color(0xFFF3EDE9)
val AndeanSurfaceContainerHigh = Color(0xFFEDE7E3)
val AndeanOnSurfaceVariant = Color(0xFF434840) // Tinta Atenuada

// Bordes y Delineados
val AndeanOutline = Color(0xFF74796F)
val AndeanOutlineVariant = Color(0xFFC3C8BD)

// Estados de Error y Saldo Negativo
val AndeanError = Color(0xFFBA1A1A)
val AndeanOnError = Color(0xFFFFFFFF)
val AndeanErrorContainer = Color(0xFFFFDAD6)
val AndeanOnErrorContainer = Color(0xFF410002)

// ============================================================================
// 🌙 MODO OSCURO (PÁRAMO NOCTURNO OLED #121411)
// ============================================================================
val AndeanDarkSurface = Color(0xFF121411) // Negro carbón con tinte verde musgo profundo
val AndeanDarkSurfaceContainer = Color(0xFF1B1E19)
val AndeanDarkSurfaceContainerHigh = Color(0xFF242822)
val AndeanDarkSurfaceContainerLowest = Color(0xFF0C0E0B)
val AndeanDarkPrimary = Color(0xFF8BB582) // Verde musgo desaturado 15% para menor fatiga nocturna
val AndeanDarkPrimaryContainer = Color(0xFF3B5236)
val AndeanDarkSecondary = Color(0xFFE5B067) // Ámbar anteojos suave desaturado
val AndeanDarkSecondaryContainer = Color(0xFF6B4500)
val AndeanDarkGoldHighlight = Color(0xFFD4AF37) // Reflejos dorados de flores de frailejón nocturno
val AndeanDarkOnSurface = Color(0xFFE3E5DF) // Blanco mineral de alto contraste WCAG AA
val AndeanDarkOnSurfaceVariant = Color(0xFFA1A69B)
val AndeanDarkOutlineVariant = Color(0x1AFFFFFF) // 10% blanco sutil para bordes de tarjeta
val AndeanDarkFrostedGlass = Color(0x261F231D) // 15% blanco + tono musgo nocturno

// ============================================================================
// 🪟 EFECTO VIDRIO ESMERILADO (QUIET FROSTED GLASS)
// ============================================================================
val AndeanFrostedGlassSurface = Color(0xC2FFFFFF) // 76% opacidad
val AndeanFrostedGlassBorder = Color(0xD9FFFFFF)  // 85% opacidad
val AndeanFrostedGlassShadow = Color(0x0D1D1B19)  // 5% carbón

// ============================================================================
// 💳 IDENTIDAD DE BILLETERAS COLOMBIANAS
// ============================================================================
object WalletBrandColors {
    // Nequi
    val Nequi = Color(0xFF700054)
    val NequiContainer = Color(0x1F700054)

    // Daviplata
    val Daviplata = Color(0xFFB32D2D)
    val DaviplataContainer = Color(0x1FB32D2D)

    // Bancolombia
    val Bancolombia = Color(0xFFC58934)
    val BancolombiaContainer = Color(0x26C58934)

    // Efectivo
    val Cash = Color(0xFF435C3C)
    val CashContainer = Color(0x26435C3C)

    // NuBank (Billetera adicional colombiana)
    val NuBank = Color(0xFF820AD1)
    val NuBankContainer = Color(0x1F820AD1)
}

// Botones de acción específicos
val AndeanAntExpenseAmber = Color(0xFFBA7834) // Botón Gasto Hormiga caramelo
val AndeanRegisterActionGreen = Color(0xFF4A6B47) // Botón Guardar Gasto
val AndeanKeypadKeyBackground = Color(0xFFF3E8D3) // Teclas tono arena cálido
val AndeanDragHandle = Color(0xFFCCCCCC)

// Aliases semánticos andinos
val AndinaMossGreen = Color(0xFF435C3C)
val AndinaDeepForest = Color(0xFF2C4427)
val AndinaAmber = Color(0xFFC58934)
val AndinaErrorRed = Color(0xFFBA1A1A)

// ============================================================================
// 🌄 GRADIENTES ANDINOS DE PANTALLA
// ============================================================================
val MistGradientTop = Color(0xFFF5F3EF)
val MistGradientBottom = Color(0xFFEBE6DC)

val AndeanMistGradient = Brush.verticalGradient(
    colors = listOf(MistGradientTop, MistGradientBottom)
)

val AndeanDarkGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF161914), Color(0xFF10120F))
)

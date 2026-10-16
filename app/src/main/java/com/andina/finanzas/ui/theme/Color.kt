package com.andina.finanzas.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ============================================================================
// 🎨 PALETA OFICIAL "ANDEAN MIST & MOSS" (MATERIAL 3)
// ============================================================================

// Tonos Primarios (Verde Musgo Andino Profundo)
val AndeanPrimary = Color(0xFF1E4D3B)
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
val AndeanOnSurfaceVariant = Color(0xFF383E35) // Tinta Atenuada Alto Contraste (WCAG AA)

// Bordes y Delineados
val AndeanOutline = Color(0xFF74796F)
val AndeanOutlineVariant = Color(0xFFC3C8BD)

// Estados de Error y Saldo Negativo
val AndeanError = Color(0xFFBA1A1A)
val AndeanOnError = Color(0xFFFFFFFF)
val AndeanErrorContainer = Color(0xFFFFDAD6)
val AndeanOnErrorContainer = Color(0xFF410002)

// ============================================================================
// 🪙 TOKENS SEMÁNTICOS CONTABLES OBLIGATORIOS (ACCOUNTING SEMANTICS)
// ============================================================================
val SemanticIncomeEmerald = Color(0xFF10B981)      // Solo para Ingresos (+$) y crecimiento
val SemanticExpenseCoral = Color(0xFFEF4444)       // Obligatorio para Gastos (-$) y sobregiros
val SemanticTransferCyan = Color(0xFF0EA5E9)       // Obligatorio para Transferencias (⇄)
val SemanticDebtReceivableGold = Color(0xFFE5A93C) // Para "Me deben" (Préstamos por cobrar)
val SemanticDebtPayableOrange = Color(0xFFF97316)  // Para "Yo debo" (Deudas por pagar)

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
// 🏔️ PÁRAMO DIURNO LUMINOSO & CRISTAL DE CUARZO (LIGHT MINERAL GLASS)
// ============================================================================
val AndeanMistCanvasTop = Color(0xFFECF2EB)      // Pizarra caliza con rocío matutino
val AndeanMistCanvasBottom = Color(0xFFE4ECE3)   // Base niebla andina suave
val AndeanQuartzGlass = Color(0xD9FFFFFF)        // 85% blanco cuarzo translúcido
val AndeanFrostBorderTop = Color(0xF2FFFFFF)     // 95% blanco cenital escarchado
val AndeanFrostBorderBottom = Color(0x59CAD5C7)  // Bisel inferior fundido con la niebla

// Oro Frailejón Cálido y Esmeralda Andina (Acentos de Valor)
val FrailejonGold = Color(0xFFD99B26)            // Oro ámbar cálido para saldos y logros
val FrailejonGoldDeep = Color(0xFFC8871E)        // Oro ámbar oscuro para bordes y contraste
val AndeanEmeraldDeep = Color(0xFF15803D)        // Esmeralda pura para ingresos y confirmaciones
val AndeanEmeraldLight = Color(0xFF10B981)       // Esmeralda luminosa para auras cáusticas
val AndeanSlateInk = Color(0xFF1F2923)           // Tinta carbón vegetal mineral (WCAG AAA)
val AndeanVolcanicEmber = Color(0xFFDC2626)      // Rojo brasas para sobregiros y alertas

// ============================================================================
// 🪟 EFECTO VIDRIO ESMERILADO (QUIET FROSTED GLASS)
// ============================================================================
val AndeanFrostedGlassSurface = Color(0xD9FFFFFF) // 85% opacidad cuarzo
val AndeanFrostedGlassBorder = Color(0xF2FFFFFF)  // 95% opacidad bisel
val AndeanFrostedGlassShadow = Color(0x0A1D1B19)  // 4% sombra mineral de contacto

// ============================================================================
// 💳 IDENTIDAD DE BILLETERAS COLOMBIANAS (GEMAS DE CRISTAL)
// ============================================================================
object WalletBrandColors {
    // Nequi: Amatista profundo con reflejo neón
    val Nequi = Color(0xFF700054)
    val NequiAccent = Color(0xFFFF007A)
    val NequiContainer = Color(0x1F700054)

    // Daviplata: Rubí volcánico templado
    val Daviplata = Color(0xFFB32D2D)
    val DaviplataAccent = Color(0xFFE11D48)
    val DaviplataContainer = Color(0x1FB32D2D)

    // Bancolombia: Oro mostaza andino metalizado
    val Bancolombia = Color(0xFFC58934)
    val BancolombiaAccent = Color(0xFFF59E0B)
    val BancolombiaContainer = Color(0x26C58934)

    // Efectivo: Jade paramuno satinado
    val Cash = Color(0xFF2C4427)
    val CashAccent = Color(0xFF15803D)
    val CashContainer = Color(0x26435C3C)

    // NuBank (Billetera adicional colombiana)
    val NuBank = Color(0xFF820AD1)
    val NuBankAccent = Color(0xFFA855F7)
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
val MistGradientTop = AndeanMistCanvasTop
val MistGradientBottom = AndeanMistCanvasBottom

val AndeanMistGradient = Brush.verticalGradient(
    colors = listOf(AndeanMistCanvasTop, AndeanMistCanvasBottom)
)

val AndeanDarkGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF161914), Color(0xFF10120F))
)

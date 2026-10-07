# 🎨 Andina — Guía de Diseño Visual y Reglas de Interfaz (Design System)

> Documento oficial de diseño, estética, tokens visuales, comportamiento sensorial y reglas de interacción para **Andina Android**.  
> Basado fielmente en el sistema **Andean Mist & Moss** de Google Stitch y adaptado a las directrices de **Material Design 3 (M3)** y **Jetpack Compose**.

> [!TIP]
> **Documentos relacionados del proyecto:**
> * Plan de ejecución y Quality Gates: [**PLAN_DE_FASES.md**](./PLAN_DE_FASES.md)
> * Lógica, base de datos y pantallas: [**ARQUITECTURA.md**](./ARQUITECTURA.md)
> * Historia y personalidad de Muggy: [**MUGGY.md**](./MUGGY.md)
> * Implementación de animaciones de Muggy: [**MUGGY_INTERACCION.md**](./MUGGY_INTERACCION.md)
> * Motor técnico e ingeniería de Muggy: [**MUGGY_MOTOR_TECNICO.md**](./MUGGY_MOTOR_TECNICO.md)
> * Resumen general del proyecto: [**README.md**](./README.md)

---

## 1. Filosofía de Identidad: "Andean Mist & Moss"

La experiencia visual de Andina se inspira en el **ecosistema de páramo y bosque de niebla andino**:
* **Verde Musgo (*Andean Moss*):** Crecimiento, serenidad, estabilidad y resguardo financiero.
* **Ámbar Anteojos (*Spectacle Amber*):** Energía, alerta amable, calidez y el color característico de la máscara facial del Oso Andino (Muggy).
* **Niebla Andina (*Mist Surface*):** Luz matutina, frescura, claridad mental y fondos tipo papel de alta gama.
* **Carbón Mineral (*Charcoal Ink*):** Legibilidad absoluta, contraste noble y precisión tipográfica.
* **Vidrio Esmerilado (*Apple Frosted Glass*):** Capas de elevación translúcidas que dejan traslucir los gradientes orgánicos de fondo sin ensuciar el contenido.

---

## 2. Paleta de Colores y Tokens Semánticos

### 2.1 Colores Principales (Tema Claro)

| Token Semántico | Valor Hex | Alpha / Opacidad | Uso en UI |
|---|---|---|---|
| `Primary` | `#2C4427` | 100% | Botones primarios, branding Andina, estados activos destacados |
| `OnPrimary` | `#FFFFFF` | 100% | Texto e íconos sobre elementos primarios |
| `PrimaryContainer` | `#435C3C` | 100% | *Verde Musgo Andino*: Botón central (+), badges de balance positivo |
| `OnPrimaryContainer` | `#B6D3AB` | 100% | Acentos suaves sobre contenedores oscuros |
| `Secondary` | `#845400` | 100% | *Ámbar Anteojos*: Alertas preventivas, acentos de Andino Observador |
| `SecondaryContainer` | `#FEBD67` | 100% | Contenedores de alerta cálida y metas destacadas |
| `OnSecondaryContainer` | `#764B00` | 100% | Texto sobre fondos ámbar suaves |
| `Surface` | `#FFF8F4` | 100% | *Superficie Niebla*: Fondo base general de pantallas |
| `SurfaceContainer` | `#F3EDE9` | 100% | Fondo de chips, campos y barras no activas |
| `SurfaceContainerHigh` | `#EDE7E3` | 100% | Fondos secundarios y divisores suaves |
| `OnSurface` | `#1D1B19` | 100% | *Carbón Mineral*: Textos primarios y encabezados |
| `OnSurfaceVariant` | `#434840` | 100% | *Tinta Atenuada*: Subtítulos, labels y fechas |
| `Outline` | `#74796F` | 100% | Bordes de inputs inactivos |
| `OutlineVariant` | `#C3C8BD` | 100% | Bordes sutiles de tarjetas |
| `Error` | `#BA1A1A` | 100% | Gastos, presupuestos excedidos, saldos negativos |
| `ErrorContainer` | `#FFDAD6` | 100% | Fondo de badges de error / gasto |
| `OnError` | `#FFFFFF` | 100% | Texto sobre color de error |

### 2.2 Fondos Gradientes de Pantalla
Las pantallas principales (**Inicio**, **Bolsillos**, **Reportes**) no utilizan un fondo blanco plano, sino un gradiente orgánico vertical:
```kotlin
Brush.verticalGradient(
    colors = listOf(
        Color(0xFFF5F3EF), // Niebla clara superior
        Color(0xFFEBE6DC)  // Tono cálido inferior
    )
)
```

### 2.3 Identidad de Billeteras Colombianas

| Billetera | Color Marca | Contenedor Suave | Letra / Insignia |
|---|---|---|:---:|
| **Nequi** | `#700054` | `#1F700054` (12%) | **N** |
| **Daviplata** | `#B32D2D` | `#1FB32D2D` (12%) | **D** |
| **Bancolombia** | `#C58934` | `#26C58934` (15%) | **B** |
| **Efectivo** | `#435C3C` | `#26435C3C` (15%) | Ícono billete |

### 2.4 Tokens del Tema Oscuro (Modo Páramo Nocturno)

| Token Semántico | Valor Hex | Uso en UI |
|---|---|---|
| `DarkSurface` | `#121411` | Fondo general de noche |
| `DarkSurfaceContainer` | `#1B1E19` | Fondo de tarjetas y listas |
| `DarkPrimaryContainer` | `#587550` | Verde musgo iluminado para legibilidad nocturna |
| `DarkOnSurface` | `#E3E5DF` | Texto blanco cálido mineral |
| `DarkOnSurfaceVariant` | `#A1A69B` | Textos secundarios atenuados |
| `DarkFrostedGlass` | `#261F231D` (15% blanco + blur) | Efecto frosted sobre fondos oscuros |

---

## 3. Tipografía Oficial: Manrope

Andina utiliza exclusivamente la tipografía geométrica humanista **Manrope**, configurada con métricas precisas para números tabulares y legibilidad bancaria:

```
Manrope Weights:
• 700 (Bold)      → Títulos de balance, cantidades grandes, botones de acción
• 600 (SemiBold)  → Subtítulos, nombres de categorías, nombres de billeteras
• 500 (Medium)    → Labels de navegación, estados, tags
• 400 (Regular)   → Textos descriptivos, notas, leyendas
```

### 3.1 Escala Tipográfica (Typography Scale)

| Token M3 | Peso | Tamaño (sp) | Interlineado (LineHeight) | Espaciado (LetterSpacing) | Uso Exclusivo |
|---|---|---|---|---|---|
| `DisplayLarge` | Bold (700) | 36 sp | 44 sp | -0.03em | Saldo maestro consolidado en pantalla de Inicio |
| `HeadlineLarge` | Bold (700) | 28 sp | 34 sp | -0.02em | Títulos de pantallas principales (*Bolsillos & Metas*) |
| `HeadlineMedium`| SemiBold (600) | 22 sp | 28 sp | -0.015em| Saludo *"Hola, Valentina"*, montos en modales |
| `HeadlineSmall` | SemiBold (600) | 18 sp | 24 sp | -0.01em | Título de secciones (*Actividad Reciente*) |
| `TitleMedium`   | SemiBold (600) | 16 sp | 22 sp | -0.005em| Nombres de comercios, saldos en billeteras |
| `BodyLarge`     | Regular (400)  | 16 sp | 24 sp | 0.0em   | Descripciones largas, notas, chat de Andino |
| `BodyMedium`    | Regular (400)  | 14 sp | 20 sp | 0.0em   | Texto de movimientos, descripciones de bolsillos |
| `BodySmall`     | Regular (400)  | 12 sp | 16 sp | 0.0em   | Subtextos, método de pago, fechas relativas (*"Ayer"*) |
| `LabelLarge`    | SemiBold (600) | 14 sp | 18 sp | +0.01em | Textos de botones primarios (*"Guardar Movimiento"*) |
| `LabelMedium`   | SemiBold (600) | 12 sp | 16 sp | +0.03em | Tags de estado (*"Exitoso"*, *"Colombia"*) |
| `LabelSmall`    | Bold (700)     | 11 sp | 14 sp | +0.05em | Subtítulos en MAYÚSCULAS (*"SALDO CONSOLIDADO"*) |

> **Regla de Cifras:** Todo valor monetario debe usar la característica tipográfica `FontFeature.TabularNumbers` (`tnum`) para que las cifras no salten ni vibren al animarse los números.

---

## 4. Efecto Frosted Glass (Vidrio Esmerilado Apple)

El elemento visual insignia de Stitch es la tarjeta translúcida sobre el gradiente de páramo:

### 4.1 Especificación Matemática del Vidrio
* **Color de fondo:** `rgba(255, 255, 255, 0.76)` (76% opacidad).
* **Borde perimetral:** `1.dp solid rgba(255, 255, 255, 0.85)`.
* **Sombra suave exterior:** `elevation = 2.dp`, color `rgba(29, 27, 25, 0.05)` (5% carbón).
* **Desenfoque de fondo:** `blur = 24.dp` (soportado nativamente en Android 12+ / API 31+). En Android 10 y 11 se degrada limpiamente a fondo blanco con 85% de opacidad sin perder estética.
* **Radio de esquinas:** `18.dp` (tarjetas hero y contenedores principales).

---

## 5. Formas, Espaciados y Radios (Borders & Shapes)

### 5.1 Sistema de Radios de Esquina (*Border Radius*)
| Token | Valor dp | Uso |
|---|---|---|
| `CornerSmall` | `8.dp` | Avatares de banco, tags pequeños, mini-chips |
| `CornerMedium` | `12.dp` | Botones secundarios, insignias de estado, mini-cards |
| `CornerLarge` | `18.dp` | Tarjetas hero, `FrostedGlassCard`, contenedores de lista |
| `CornerExtraLarge`| `24.dp` | Hojas de diálogo modales (`ModalBottomSheet`) |
| `CornerPill` | `999.dp` (Full) | Botones primarios, cápsulas de filtro (*Hoy* / *Esta semana*), botón flotante (+) |

### 5.2 Grilla de Espaciado (*Spacing Grid 4dp/8dp*)
* `4.dp`: Separación micro entre ícono y texto.
* `8.dp`: Padding interno de chips, separación entre botones de filtro.
* `12.dp`: Padding entre filas de movimientos.
* `16.dp`: Padding interno estándar de tarjetas y campos de texto.
* `20.dp`: Márgenes laterales exteriores de toda pantalla en móvil.
* `24.dp`: Separación entre secciones mayores (Hero a Billeteras, Billeteras a Bolsillos).
* `72.dp`: Altura ergonómica de la barra inferior de navegación.

---

## 6. Mascota y Gamificación: Muggy el Oso Andino

Muggy (el Oso de Anteojos) es el embajador visual de Andina. Da calidez y elimina la frialdad de las apps bancarias tradicionales.

### 6.1 Poses y Estados de Ánimo de Muggy
1. **Pose Alerta / Sabio (Muggy con monóculo u ojos abiertos):** 
   * Aparece en la tarjeta del **Andino Observador** cuando hay un gasto hormiga detectado o una advertencia de presupuesto al 80%.
2. **Pose Celebración (Muggy saltando o con una hoja de frailejón):**
   * Aparece cuando el usuario completa el 100% de un Bolsillo, sube de Nivel Financiero o finaliza el Onboarding.
3. **Pose Zen / Dormido (Muggy descansando sobre musgo):**
   * Aparece en la pantalla de Inicio cuando la salud financiera está en perfecto equilibrio (*"Nivel Páramo"*).
4. **Pose Guía (Muggy con mochila):**
   * Aparece en los estados vacíos (*"Aún no tienes movimientos registrados este mes"*) invitando a usar el botón (+).

### 6.2 Niveles Financieros (Gamificación de la Flora y Fauna Andina)
* 🌰 **Nivel Semilla:** Tasa de ahorro < 10% (usuario iniciando hábitos).
* 🌿 **Nivel Bosque de Niebla:** Tasa de ahorro entre 10% y 25% (hábitos estables).
* 🐻 **Nivel Páramo:** Tasa de ahorro entre 25% y 40% (reserva financiera sólida).
* ❄️ **Nivel Nevado:** Tasa de ahorro > 40% (independencia y patrimonio blindado).

---

## 7. Componentes Visuales Clave y sus Reglas

### 7.1 Barra Inferior de Navegación (*AndinaBottomBar*) y Botón Central Protagonista (+)
* **Distribución:** 4 pestañas de navegación simétricas con botón (+) flotante en el centro:
  * `[ Inicio ]` · `[ Bolsillos ]` · **🟢 (+) Central** · `[ Billeteras ]` · `[ Movimientos ]`
* **Definición de "Billeteras":** Métodos de pago y bolsas de efectivo/cuentas (Nequi, Daviplata, Efectivo, Bancolombia). Simple anotación contable de origen de dinero; **sin guardar datos de tarjetas bancarias, CVV ni credenciales**.
* **Altura de la barra:** `72.dp` + padding del sistema `navigationBarsPadding()` (SafeDrawing).
* **Botón Central (+):**
  * **Tamaño:** `54.dp` de diámetro circular perfecto.
  * **Elevación:** Desplazado `-12.dp` hacia arriba, flotando sobre el borde superior de la barra.
  * **Color:** `AndeanPrimaryContainer` (`#435C3C`) con ícono blanco centrado de `30.dp`.
  * **Sombra:** Sombra cálida con resplandor verde (`box-shadow: 0 6px 18px rgba(91, 117, 83, 0.42)`).
  * **Microinteracción:** Al presionarse, se escala con resorte elástico (`scale(0.92)`), feedback háptico `Confirm` y despliega el modal interactivo con Muggy asomado ([`Muggy en la app.jpg`](file:///c:/Users/AlekeyG11/Documents/VsCode/Andina/Muggy%20en%20la%20app.jpg)).

### 7.2 Tarjeta de Billetera (*QuickWalletCard*)
* **Ancho fijo:** `180.dp`, **Alto:** `115.dp` en un carrusel de scroll horizontal (`LazyRow`).
* **Cabecera de tarjeta:** Letra distintiva con fondo del banco a la izquierda + Badge del nombre a la derecha.
* **Cuerpo:** Subtítulo *"Saldo disponible"* en gris suave + Saldo en Manrope SemiBold 16sp.
* **Efecto de presión:** Al tocarla, brilla suavemente el borde translúcido y navega a su historial.

### 7.3 Tarjeta de Progreso de Bolsillo (*PocketProgressCard*)
* **Ancho:** `240.dp`, **Alto:** `150.dp`.
* **Barra de Progreso:**
  * Pista inactiva: `rgba(29, 27, 25, 0.12)` de 6dp de espesor.
  * Pista activa: Gradiente Verde Musgo (`#435C3C`) o Ámbar (`#C58934`) según la categoría.
  * Al abrir la pantalla, la barra se llena desde 0% hasta el valor real con animación `FastOutSlowInEasing` de 600 ms.

### 7.4 Fila de Movimiento (*TransactionItem*)
* **Ícono lateral izquierdo:** Círculo de `40.dp`.
  * Verde suave con flecha abajo para ingresos (`+$`).
  * Rojo/Ámbar suave con ícono del comercio para gastos (`-$`).
* **Etiqueta "Hormiga":** Cápsula pequeña en color ámbar suave al lado del nombre de compras no planificadas.
* **Cifra derecha:** Manrope Bold alineada a la derecha con signo explícito (`+$450.000` o `-$18.500`).

### 7.5 Modal de Registro Rápido Protagonista (*QuickAddBottomSheet*) — Basado en `Muggy en la app.jpg`
Referencia canónica: [`Muggy en la app.jpg`](file:///c:/Users/AlekeyG11/Documents/VsCode/Andina/Muggy%20en%20la%20app.jpg) y diseño base [`Muggy.jpg`](file:///c:/Users/AlekeyG11/Documents/VsCode/Andina/Muggy.jpg).

* **Header con Muggy Asomado (Peeking Bear Mascot):**
  * Muggy sobresale en la parte superior del Card blanco con `Z-Index = 2`.
  * Sus dos patitas descansan directamente sobre el borde superior redondeado del BottomSheet (`RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)`).
  * Lleva sus icónicos anteojos redondos de montura oscura y la flor dorada de frailejón en su oreja izquierda.
  * Drag handle gris tenue (`#CCCCCC`, ancho `40.dp`, alto `4.dp`) centrado debajo de las patitas.
* **Segmentador Gasto / Ingreso:**
  * Píldora redondeada dividida: `[ Gasto | Ingreso ]`.
  * Estado activo (Gasto): Verde Musgo Andino (`#4A6B47`), texto blanco en semibold.
  * Estado inactivo: Fondo neutro suave (`#E5E6E1`), texto gris grafito.
* **Hero Display de Monto:**
  * `$ 14.500 COP` en Manrope Bold/ExtraBold (`38.sp`), color carbón andino (`#1C1E1B`).
* **Carrusel de Métodos de Pago (Billeteras y Cuentas):**
  * Chips redondeados horizontales dinámicos (`LazyRow` reactivo a Room):
    * Métodos por defecto: `💵 Efectivo`, `🟣 Nequi`, `🔴 Daviplata`, `🟡 Bancolombia`.
    * **Billeteras personalizadas ilimitadas:** Cualquier método creado por el usuario (ej. `🟣 NuBank`, `🟢 Lulo Bank`, `🪙 Caja Fuerte`, `📦 RappiPay`).
    * Botón chip rápido `[ + Nuevo ]` al final de la fila para crear un método nuevo al instante si lo necesita.
* **Grid de Teclado Numérico y Acciones (2 Columnas Asimétricas):**
  * **Columna Izquierda (Teclado 3x4):**
    * Teclas del `1` al `9`, `.`, `0` y `⌫` (tecla borrar).
    * Teclas cuadradas con esquinas redondeadas (`16.dp`) en tono arena/crema cálido (`#F3E8D3`), tipografía limpia carbón.
  * **Columna Derecha (Acciones Rápidas):**
    * `[ 📷 🧾 Adjuntar recibo ]`: Botón rectangular con borde discontinuo (*dashed border*), iconos y texto centrado.
    * `[ Gasto Hormiga ]`: Botón píldora en color caramelo/tierra andina (`#BA7834`), texto blanco.
    * `[ Guardar Gasto ]`: Botón principal CTA de gran tamaño en verde musgo (`#4A6B47`), tipografía blanca en negrita, esquinas redondeadas de `24.dp`.

---

## 8. Reglas Sensoriales: Microinteracciones y Háptica

Una app de clase mundial se siente viva al tacto. Todo toque debe dar una respuesta física y visual inmediata:

| Acción en UI | Respuesta Visual | Respuesta Háptica (Vibración) | Sonido (Opcional) |
|---|---|---|---|
| **Toque en botón primario (+) o Guardar** | Rebote suave escala 95% | `HapticFeedbackType.LongPress` / Click táctil nítido | Pop sutil |
| **Digitar número en teclado de dinero** | Cifra cambia con animación | Micro-click muy tenue (tipo teclado iOS/Pixel) | Mudo |
| **Completar meta de Bolsillo (100%)** | Confeti en pantalla con Muggy | Doble pulso festivo | Tintineo suave |
| **Borrar movimiento (deslizar)** | Fila colapsa y desaparece | Pulso seco de confirmación | Mudo |
| **Error (Monto en $0 o saldo insuficiente)** | Campo vibra horizontalmente (*Shake*) | Triple pulso corto de advertencia | Tono grave tenue |
| **Alternar visibilidad del ojo de saldo** | Cifras pasan a `••••••••` en fade | Click simple | Mudo |

---

## 9. Modo Oscuro: Reglas de Transición y Coherencia

* **Cero negros puros (#000000):** Se usa `#121411` (negro carbón con tinte verde musgo profundo) para evitar halos agresivos en pantallas OLED.
* **Menos saturación:** Los colores verde y ámbar reducen su saturación en un 15% para no cansar la vista en la noche.
* **Bordes más sutiles:** Los bordes de las tarjetas bajan a 10% de opacidad blanca (`#1AFFFFFF`) para mantener la separación de capas sin verse estridentes.
* **El ojo conserva privacidad:** El estado del ojo (saldos ocultos o visibles) se mantiene idéntico si el usuario cambia entre modo claro y oscuro.

---

## 10. Accesibilidad Universal (A11y)

1. **Tamaño táctil mínimo:** Ningún botón o elemento interactivo puede medir menos de `48.dp x 48.dp` de área táctil (`Modifier.minimumInteractiveComponentSize()`).
2. **Contraste de Color:** Todo texto sobre cualquier fondo cumple mínimo con el estándar **WCAG AA** (ratio de contraste ≥ 4.5:1).
3. **Escala de fuentes dinámicas:** Todo texto usa unidades relativas `sp`. La interfaz se adapta ordenadamente si el usuario amplía el texto al 150% o 200% en los ajustes de Android sin romper cajas ni ocultar cifras.
4. **Lectura para TalkBack:** Los saldos numéricos contienen `contentDescription` expandido (ej. `"Saldo disponible: catorce millones ochocientos cincuenta mil pesos colombianos"`).

---

## 11. Ecosistema Vivo del Páramo: Interacciones de Flora, Agua y Viento

En Andina, la interfaz no es un fondo inerte de software; es una **fábrica de agua andina viva**. Cada elemento natural interactúa bidireccionalmente con el usuario y con Muggy:

### 11.1 El Frailejón del Ahorro (Flora Interactiva en Bolsillos)
* **Crecimiento Orgánico con tus Metas:**
  * En cada meta de Bolsillo, junto a la barra vive una planta de frailejón andino:
    * **0% a 30%:** Brote tierno con pequeñas hojas aterciopeladas.
    * **30% a 70%:** Tronco robusto con hojas anchas que se mecen con una brisa sutil.
    * **100% de la meta:** **Abre su corona de flores doradas** y Muggy celebra a su lado con un salto.
* **Micro-interacción Táctil (Polen Dorado):**
  * Al tocar directamente el frailejón con el dedo, la planta se sacude con resorte elástico y desprende un halo diminuto de partículas de polen dorado brillante que caen suavemente sobre la tarjeta.

### 11.2 Hidrología Financiera: El Agua del Páramo y Gotas de Rocío
El dinero se conceptualiza como un **recurso líquido vital**:

* **Pull-to-Refresh con Gota de Rocío Andino:**
  * Al deslizar hacia abajo para actualizar, en lugar del spinner genérico de Android, una **gota cristalina de rocío andino** se estira elásticamente desde el borde superior de la pantalla.
  * Al soltar, la gota cae por gravedad, emite un sonido sutil de agua `*plop*` y se disuelve en una onda expansiva translúcida que renueva las cuentas.
* **Ingreso de Dinero (+): Ondas de Agua Cristalina (*Water Ripple Canvas*):**
  * Al registrar un ingreso, una gota cae sobre la tarjeta de la billetera receptora (Nequi, Daviplata, etc.) y genera **ondas concéntricas de agua cristalina** que llenan el saldo con un efecto visual líquido.
* **Tanque de Agua Líquida en Bolsillos:**
  * El fondo del progreso en bolsillos representa un nivel de agua pura de páramo con pequeñas olas que oscilan suavemente si el usuario inclina el teléfono (giroscopio leve).

### 11.3 Viento Andino y Ráfagas de Hojas
* **Inercia de Deslizamiento (Swipe de Movimientos):**
  * Al deslizar una fila de movimiento para ver opciones o borrar, una **ráfaga tenue de hojas de frailejón y niebla** acompaña la inercia del pulgar y se desvanece en el aire.
* **Celebración Orgánica (Sin Plástico):**
  * Al cumplir una meta, no cae confeti artificial de cotillón; cae una **lluvia de hojas andinas doradas y verdes** que planean y giran con física de viento andino.

### 11.4 La Niebla Viva (*Andean Mist Ambient*)
* **Capa Atmosférica:** Una bruma muy sutil y translúcida recorre el tercio superior de la pantalla a velocidad lenta (2 px/s en GPU sin impacto de CPU).
* **Páramo Nocturno:** En modo oscuro, la niebla desciende sobre las tarjetas y deja ver pequeños destellos dorados que flotan como luciérnagas andinas del páramo.

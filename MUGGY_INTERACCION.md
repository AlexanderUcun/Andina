# 🐻 Integración Interactiva de Muggy en Andina

> **Manual de Implementación Técnica, Máquina de Estados, Sistema de Animación y Flujos de Tareas de Muggy en Jetpack Compose.**  
> Este documento detalla cómo vive Muggy dentro del código, cómo reacciona a cada acción del usuario, qué animaciones reproduce y cómo interactúa constantemente sin ser invasivo.

> [!TIP]
> **Documentos relacionados del proyecto:**
> * Lógica, base de datos y pantallas: [**ARQUITECTURA.md**](./ARQUITECTURA.md)
> * Tokens, colores y componentes visuales: [**DISENO_VISUAL.md**](./DISENO_VISUAL.md)
> * Historia, personalidad y poses de Muggy: [**MUGGY.md**](./MUGGY.md)
> * Arquitectura de ingeniería y físicas: [**MUGGY_MOTOR_TECNICO.md**](./MUGGY_MOTOR_TECNICO.md)

---

## 1. Principio de Interacción: Presencia Orgánica, Cero Fricción

Muggy **no es un chatbot molesto tipo "Clippy" ni una ventana emergente que bloquea la pantalla**.  
Su presencia sigue tres reglas estrictas:
1. **Vive integrado en la interfaz:** Forma parte de tarjetas, cabeceras, botones de estado y fondos. No cubre botones de acción ni detiene al usuario.
2. **Reacciona a eventos reales:** Solo se mueve o habla cuando el usuario realiza una acción financiera o cuando el estado de la base de datos cambia.
3. **Compañerismo discreto:** Si el usuario está trabajando rápido (registrando 3 compras seguidas), Muggy responde con micro-animaciones mínimas (guiño, pulgar arriba de 200 ms) sin ralentizar el flujo.

---

## 2. Máquina de Estados de Muggy (`MuggyState`)

El comportamiento de Muggy se modela como un flujo de estado reactivo expuesto por el sistema:

```kotlin
sealed interface MuggyMood {
    object IdleSerene : MuggyMood       // Finanzas estables, respiración sutil
    object AlertObserving : MuggyMood   // Detectando gastos hormiga / presupuesto al 80%
    object Celebrating : MuggyMood      // Meta cumplida (100%), racha de ahorro, nuevo nivel
    object Guiding : MuggyMood          // Onboarding, listas vacías, primeras acciones
    object Thinking : MuggyMood         // IA procesando análisis o consejo
    object SleepingNight : MuggyMood    // Modo noche (después de las 10:00 PM)
    object Peekaboo : MuggyMood         // Ojo de privacidad activado (tapándose los ojos)
    object PeekingTopSheet : MuggyMood  // Asomado sobre el modal con las patitas apoyadas en el borde
}

data class MuggyInteractionState(
    val mood: MuggyMood = MuggyMood.IdleSerene,
    val speechBubbleText: String? = null,
    val isAnimating: Boolean = false,
    val showConfetti: Boolean = false
)
```

---

## 3. Muggy en el Flujo de Tareas Paso a Paso

### 3.1 Flujo: Primer Ingreso y Onboarding (Muggy Guía)
* **Paso 1 (Bienvenida):** Muggy aparece en tamaño mediano (120dp) sosteniendo una pequeña planta de frailejón.
  * *Animación:* Saludo con la patita derecha (`rotation` suave de -5° a 5° en bucle).
  * *Interacción:* Tocar a Muggy hace que suelte una risita visual (ondas de sonido gráficas) y diga: *"¡Bienvenido al páramo, caminante!"*.
* **Paso 2 (Nombre):** Muggy saca una libreta de cuero andina y toma nota mientras el usuario escribe su nombre.
* **Paso 3 (Billeteras y saldo inicial):** Muggy se sienta junto a cuatro cofres pequeños con los logos de Nequi, Daviplata, Bancolombia y Efectivo. Al ingresar montos, Muggy asiente complacido.
* **Paso 4 (Finalización):** Muggy salta de espaldas con su mochila de senderismo puesta y señala el botón *"Entrar a mi Páramo"*.

---

### 3.2 Flujo: Registro Rápido Protagonista (Muggy Asomado en Modal — Referencia Oficial `Muggy en la app.jpg`)
1. **Apertura del Modal (`QuickAddBottomSheet`):**
   * Muggy pasa a estado `MuggyMood.PeekingTopSheet`.
   * Se asoma en la parte superior del BottomSheet: su cuerpo y cabeza sobresalen por arriba (`140.dp x 120.dp`), y sus dos patitas delanteras descansan físicamente sobre el borde curvo del modal blanco (`Z-Index = 2`).
   * Lleva sus anteojos redondos y la flor dorada de frailejón en la oreja izquierda ([`Muggy.jpg`](file:///c:/Users/AlekeyG11/Documents/VsCode/Andina/Muggy.jpg)).
2. **Usuario digita cifras en el teclado numérico:**
   * Las pupilas de Muggy siguen los números tecleados con micro-desplazamientos de `2.dp`.
   * El monto se formatea dinámicamente en tiempo real (ej. `$ 14.500 COP`).
3. **Selección de método de pago (`Efectivo`, `Nequi`, `Daviplata`):**
   * Muggy inclina ligeramente la cabeza hacia el método seleccionado confirmando la fuente del dinero.
4. **Activación de "Gasto Hormiga":**
   * Al pulsar el botón píldora caramelo `[ Gasto Hormiga ]`, Muggy abre bien los ojos y ajusta sus gafas redondas con una patita.
5. **Guardar Gasto:**
   * Al presionar `[ Guardar Gasto ]`, Muggy levanta el pulgar complacido, da un parpadeo alegre y el modal se desliza hacia abajo.

---

### 3.3 Flujo: Detección y Advertencia de Presupuesto (Muggy Alerta Amable)
* **Disparador:** Un gasto hace que la categoría supere el 80% del presupuesto mensual.
* **Ubicación:** En la tarjeta superior de la pantalla de Inicio (`AndinoAdvisorCard`).
* **Visual:** Muggy aparece sentado rascándose la cabeza con timidez, señalando con el hocico la barra de progreso en color ámbar (`#845400`).
* **Bocadillo flotante (expandible al tap):**
  > *"Ojo, Valentina: casi alcanzamos el tope de Salidas este mes. Si nos cuidamos hoy, cerramos el mes con saldo a favor."*
* **Acción sugerida:** Botón incrustado en el bocadillo: *"Ajustar presupuesto"* o *"Entendido, Muggy"*. Al tocar *"Entendido"*, Muggy asiente y el bocadillo se repliega limpiamente.

---

### 3.4 Flujo: Aporte a Bolsillo y Cumplimiento de Meta (Muggy Celebrador)
* **Aporte normal (progreso parcial):**
  * Al ingresar dinero a un bolsillo (ej. *$50.000 COP a Viaje Chingaza*), Muggy aparece en miniatura junto a la barra de progreso y empuja una moneda dorada hacia la meta. La barra se llena y Muggy sonríe.
* **Cumplimiento de Meta (100% alcanzado):**
  * **Fase 1 (Explosión de Confeti):** `Canvas` en Compose dispara confeti de hojas andinas doradas y verdes.
  * **Fase 2 (Animación de Éxito):** Muggy aparece en el centro de la pantalla dando un salto con una corona de frailejón dorado.
  * **Fase 3 (Vibración háptica):** Doble pulso festivo en el hardware del teléfono.
  * **Fase 4 (Insignia):** Muggy le entrega al usuario una estampilla coleccionable del bolsillo completado.

---

### 3.5 Flujo: Modo Noche / Fin del Día (Muggy Descanso)
* **Disparador:** La hora local del dispositivo supera las 10:00 PM y la app está en modo oscuro.
* **Comportamiento:**
  * En la tarjeta de Inicio, Muggy aparece acostado sobre un lecho de musgo oscuro, con un gorro de lana andina y zetas flotantes (`z Z Z`) que se desvanecen con un `alpha` oscilante.
  * Su mensaje cambia a: *"Tus cuentas del día quedaron en paz. Es hora de descansar; mañana seguimos cosechando."*
  * Al tocarlo, no emite sonidos fuertes; solo abre un ojo lentamente, sonríe y vuelve a dormir.

---

### 3.6 Flujo: Modo Privacidad (Botón del Ojo de Saldo)
* **Disparador:** El usuario presiona el ícono del ojo para ocultar sus montos (`••••••••`).
* **Comportamiento:**
  * En la tarjeta del saldo maestro, un avatar de Muggy de 28dp se cubre los ojos con ambas patitas delanteras con una animación de resorte rápido (`snap` de 150 ms).
  * Al desocultar los saldos, Muggy separa las patas y asoma los ojos con curiosidad.

---

## 4. Arquitectura de Animación en Jetpack Compose

Para garantizar **60 a 120 FPS constantes sin pérdida de frames ni consumo de batería**, se utiliza una arquitectura híbrida de renderizado:

```
┌────────────────────────────────────────────────────────┐
│             SISTEMA DE RENDERIZADO MUGGY               │
├──────────────────────────┬─────────────────────────────┤
│   MICRO-ANIMACIONES UI   │    CELEBRACIONES Y FIESTA   │
│     (Compose Nativo)     │        (Lottie / Rive)      │
├──────────────────────────┼─────────────────────────────┤
│ • Parpadeo de ojos       │ • Salto de meta al 100%     │
│ • Respiración sutil      │ • Lluvia de confeti         │
│ • Guiño y sonrisa        │ • Cambio de Piso Térmico    │
│ • Taparse los ojos       │ • Pantalla de bienvenida    │
└──────────────────────────┴─────────────────────────────┘
```

### 4.1 Componente Composable Reutilizable: `MuggyAvatar`

```kotlin
@Composable
fun MuggyAvatar(
    mood: MuggyMood,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    onClick: (() -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "MuggyBreathing")
    
    // Respiración sutil (escala 1.0 a 1.03) cada 3 segundos
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath"
    )

    // Animación de rotación elástica al hacer tap
    var isTapped by remember { mutableStateOf(false) }
    val tapScale by animateFloatAsState(
        targetValue = if (isTapped) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        finishedListener = { isTapped = false },
        label = "tapSpring"
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(breathScale * tapScale)
            .clickable(enabled = onClick != null) {
                isTapped = true
                onClick?.invoke()
            },
        contentAlignment = Alignment.Center
    ) {
        when (mood) {
            MuggyMood.IdleSerene -> MuggyVectorDrawing(pose = MuggyPose.SERENE)
            MuggyMood.AlertObserving -> MuggyVectorDrawing(pose = MuggyPose.OBSERVING)
            MuggyMood.Celebrating -> MuggyLottieAnimation(animationRes = R.raw.muggy_celebrate)
            MuggyMood.Peekaboo -> MuggyVectorDrawing(pose = MuggyPose.PEEKABOO)
            MuggyMood.SleepingNight -> MuggyVectorDrawing(pose = MuggyPose.SLEEPING)
            MuggyMood.PeekingTopSheet -> MuggyPeekingDrawing(isSurprised = false)
            else -> MuggyVectorDrawing(pose = MuggyPose.DEFAULT)
        }
    }
}
```

### 4.2 Componente del Modal: `MuggyPeekingHeader` con Eye-Tracking y Drag Physics
Este componente renderiza a Muggy asomado sobre el modal de registro rápido ([`Muggy en la app.jpg`](file:///c:/Users/AlekeyG11/Documents/VsCode/Andina/Muggy%20en%20la%20app.jpg)):

```kotlin
@Composable
fun MuggyPeekingHeader(
    currentAmountCOP: Long,
    lastPressedDigit: Int?,
    sheetDragOffset: Float, // Desplazamiento al deslizar el modal
    modifier: Modifier = Modifier
) {
    // 1. Ojos que siguen el dígito tecleado (Offset sutil de -4dp a +4dp)
    val eyeOffsetX by animateFloatAsState(
        targetValue = when (lastPressedDigit) {
            1, 4, 7 -> -3f
            3, 6, 9 -> 3f
            else -> 0f
        },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "eyeTrackingX"
    )

    // 2. Expresión de sorpresa si el monto supera $500.000 COP
    val isSurprised = currentAmountCOP >= 500_000L

    // 3. Sujeción física con inercia elástica al arrastrar para cerrar
    val pawTension by animateFloatAsState(
        targetValue = if (sheetDragOffset > 50f) 1.08f else 1.0f,
        label = "dragGrip"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .graphicsLayer { scaleY = pawTension },
        contentAlignment = Alignment.BottomCenter
    ) {
        // Cuerpo y cabeza sobresaliendo (Z-Index = 1)
        MuggyPeekingMascot(
            eyeOffsetX = eyeOffsetX,
            isSurprised = isSurprised,
            modifier = Modifier.size(width = 140.dp, height = 120.dp)
        )
        // Patitas delanteras descansando en el borde superior del card blanco (Z-Index = 2)
        MuggyPawsOverlay(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 8.dp)
        )
    }
}
```

### 4.3 Control de Parpadeo Estocástico y Ritmo Circadiano
Para garantizar la sensación de vida autónoma sin gastar CPU en bucles infinitos:
* **Parpadeo Estocástico (`LaunchedEffect`):** Un bucle asíncrono espera un lapso aleatorio `delay(Random.nextLong(2500, 5500))` y contrae las pupilas durante `120 ms`. Si `Random.nextFloat() < 0.25f`, concatena un segundo parpadeo rápido inmediato.
* **Ritmo Circadiano (`LocalTime.now()`):** Al crearse el `MuggyInteractionState`, se evalúa la hora local:
  * `06:00 - 09:00` → Estado activo con taza de café.
  * `12:00 - 14:00` → Snack andino.
  * `22:00 - 05:00` → `MuggyMood.SleepingNight` (gorro de lana andina y cabeceo suave).

### 4.4 Sistema de Locomoción Física y Libertad de Movimiento (`MuggyLocomotionSystem`)
> **Regla Mandatoria:** Muggy **NUNCA** se desplaza como una imagen estática flotante, una diapositiva rígida ni una esfera deslizante. Posee **agencia corporal andina real**: camina con patitas, salta con parábola física (Squash & Stretch), trepa repisas y planea con hojas de frailejón.

```
┌────────────────────────────────────────────────────────────────────────┐
│               SISTEMA DE LOCOMOCIÓN FÍSICA DE MUGGY                   │
├────────────────────┬────────────────────┬──────────────────────────────┤
│ 1. CAMINATA ANDINA │ 2. SALTO PARABÓLICO│ 3. PLANEO CON FRAILEJÓN      │
│   (Walk Cycle)     │ (Squash & Stretch) │      (Glider Leaf)           │
├────────────────────┼────────────────────┼──────────────────────────────┤
│ Pasitos rítmicos   │ Anticipación baja  │ Descenso flotante pendular   │
│ Cadera oscilante   │ Trayectoria de arco│ Balanceo suave -12° a +12°   │
│ Inclinación visual │ Rebote de aterrizaje│ Para descensos de pantalla   │
└────────────────────┴────────────────────┴──────────────────────────────┘
```

#### 1. Principio: La Interfaz como "Terreno Andino" (UI as Physical Terrain)
Para Muggy, las tarjetas (`FrostedGlassCard`), los modales y las barras de navegación no son cajas de software: son **rocas, riscos, repisas y ramas del páramo**.
* **Al abrir el Modal de Registro ([`Muggy en la app.jpg`](file:///c:/Users/AlekeyG11/Documents/VsCode/Andina/Muggy%20en%20la%20app.jpg)):** Muggy no aparece por arte de magia; da un salto ágil desde la base, se agarra del borde superior curvo de la tarjeta con sus dos patitas y se asoma con curiosidad.
* **Al guiar en un Spotlight:** Camina físicamente por el borde de la tarjeta hacia el botón a explicar y señala con su patita.
* **Al completar una meta (100%):** Salta sobre la barra de progreso, la celebra dando brinquitos elásticos y aterriza triunfal.

#### 2. Mecánica de los 3 Modos de Movimiento Cinemático en Compose

```kotlin
sealed interface MuggyLocomotion {
    data class Resting(val surfaceBounds: Rect) : MuggyLocomotion
    data class Walking(val fromX: Float, val toX: Float, val isFacingRight: Boolean) : MuggyLocomotion
    data class Jumping(val origin: Offset, val destination: Offset, val arcHeight: Float = 80f) : MuggyLocomotion
    data class Gliding(val origin: Offset, val destination: Offset) : MuggyLocomotion
    data class PeekingEdge(val shelfY: Float) : MuggyLocomotion
}
```

* **A. Caminata con Ciclo de Pasos (`Walking`):**
  * **Oscilación de cadera:** `offsetY = sin(t * 12f) * 3.dp` (pequeño rebote rítmico de caminante).
  * **Patitas alternadas:** Las patitas delanteras y traseras rotan de `-8°` a `+8°` simulando pasos naturales de oso.
  * **Orientación:** Si se mueve a la derecha `scaleX = 1f`, si va a la izquierda `scaleX = -1f` (gira su cuerpo hacia donde va).
* **B. Salto Parabólico con Principios de Disney (`Jumping`):**
  * **1. Anticipación (Squash previo):** Antes de saltar, Muggy se agacha flexionando el cuerpo (`scaleY = 0.82f, scaleX = 1.18f`) durante 120 ms.
  * **2. Impulso y Vuelo (Stretch en Arco):** Se estira verticalmente (`scaleY = 1.20f, scaleX = 0.85f`) mientras su posición sigue una curva parabólica Bézier $(x(t), y(t) - 4 \cdot h \cdot t(1-t))$.
  * **3. Aterrizaje y Amortiguación (Impact & Settle):** Al tocar la superficie destino, se comprime suavemente con resorte elástico (`spring(dampingRatio = Spring.DampingRatioMediumBouncy)`), sacude sus orejitas y ajusta sus gafas redondas.
* **C. Planeo Andino con Hoja de Frailejón (`Gliding`):**
  * Cuando necesita descender una gran distancia (ej. desde la cabecera superior hacia una tarjeta inferior), saca una hoja ancha de frailejón andino como paracaídas orgánico.
  * Desciende con velocidad terminal suave mientras oscila como un péndulo balanceándose de `-12°` a `+12°` hasta posar sus patitas suavemente en la tarjeta.

---

## 5. Respuestas Táctiles y Easter Eggs (Descubrimientos Amigables)

Para premiar la curiosidad del usuario, Muggy cuenta con interacciones ocultas (*Easter Eggs*):

| Gesto sobre Muggy | Reacción de Muggy | Efecto en la App |
|---|---|---|
| **Tap simple en cualquier pantalla** | Guiño de ojo y salto breve con sonido sutil de madera/pop | Burbuja flotante con un tip financiero andino breve |
| **Mantener presionado 2 segundos** | Muggy se sorprende con las mejillas sonrojadas | Emite un dato curioso sobre la fauna colombiana |
| **Doble tap rápido** | Muggy saca una flor de frailejón y la ofrece a la pantalla | Aparece un efecto de corazones/hojas flotantes durante 1.5 s |
| **Agitar el teléfono (Shake)** | Muggy se marea con espirales en los ojos durante 1 segundo | Si el saldo estaba oculto, lo vuelve a ocultar automáticamente |

---

## 6. Rendimiento y Optimización para Dispositivos de Gama Baja

En Colombia existe una amplia gama de dispositivos Android (desde gamas de entrada con 2GB de RAM hasta gamas altas). Muggy está estrictamente optimizado para no ralentizar el teléfono:
1. **Pausa en Scroll (`LazyListState.isScrollInProgress`):**
   * Cuando el usuario está desplazándose a toda velocidad por la lista de movimientos, las animaciones vectoriales de Muggy se congelan temporalmente. Se reanudan inmediatamente al detenerse el scroll.
2. **Modo Ahorro de Energía Nativo:**
   * Al escuchar `PowerManager.isPowerSaveMode`, todas las transiciones infinitas (respiración, parpadeo) se apagan y Muggy se renderiza como imagen vectorial estática de cero consumo de GPU.
3. **Liberación de Memoria:**
   * Las animaciones pesadas de Lottie (celebración de meta) se destruyen de la memoria RAM tan pronto termina el evento festivo.

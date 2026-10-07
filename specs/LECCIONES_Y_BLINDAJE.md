# 🛡️ Lecciones Aprendidas y Blindaje Técnico de Andina

> **Análisis Forense de Fallos Previos, Patrones de Prevención y Checklist de Estabilidad.**  
> Este documento recopila los errores críticos que provocaron el colapso de la versión inicial y define las reglas de ingeniería obligatorias para blindar el código antes de escribir cualquier pantalla.

> [!TIP]
> **Documentos relacionados del proyecto:**
> * Plan de ejecución y Quality Gates: [**PLAN_DE_FASES.md**](./PLAN_DE_FASES.md)
> * Lógica, base de datos y pantallas: [**ARQUITECTURA.md**](./ARQUITECTURA.md)
> * Tokens, colores y componentes visuales: [**DISENO_VISUAL.md**](./DISENO_VISUAL.md)
> * Motor técnico e ingeniería de Muggy: [**MUGGY_MOTOR_TECNICO.md**](./MUGGY_MOTOR_TECNICO.md)
> * Resumen general del proyecto: [**README.md**](../README.md)

---

## 1. Resumen Ejecutivo: ¿Por qué colapsó la versión anterior?

La primera implementación intentó generar demasiadas capas y pantallas de golpe sobre cimientos frágiles.  
Esto provocó **3 problemas estructurales**:
1. **Desincronización de datos:** Los ingresos no sumaban a las billeteras porque no existía un libro mayor contable (*ledger*).
2. **Colapso de memoria de compilación:** Configuraciones incompatibles de Gradle provocaron volcados de memoria masivos de **4 GB en archivos `.hprof`**.
3. **Fricción e inmovilidad en la UI:** Los botones no respondían, el botón flotante (+) se desconectaba fuera de Inicio y la entrada de texto numérico fallaba en silencio al recibir puntos o comas.

A continuación se detalla la anatomía de cada fallo y su solución técnica definitiva.

---

## 2. Los 8 Errores Críticos y sus Soluciones de Blindaje

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    MATRIZ DE PREVENCIÓN Y BLINDAJE                          │
├──────────────────────────┬──────────────────────────────────────────────────┤
│ ERROR EXPERIMENTADO      │ BLINDAJE TÉCNICO APLICADO                        │
├──────────────────────────┼──────────────────────────────────────────────────┤
│ 1. 4 GB de dumps .hprof  │ Stack canónico probado + jvmargs -Xmx2048m       │
│ 2. Ingresos no sumaban   │ Libro Mayor en Room (@DatabaseView reactiva)     │
│ 3. Botones "sordos"      │ Máquina de estados UI + protección anti-doble tap│
│ 4. Fallos con "$50.000"  │ Dinero en Long + teclado numérico con máscara    │
│ 5. Botón (+) inerte      │ Formulario global en AndinaAppShell              │
│ 6. Lag en scroll         │ items(key) estricto + tipos @Immutable           │
│ 7. Bloqueos por IA       │ Motor local primero (offline-first); IA diferida │
│ 8. Sin botón atrás       │ Navigation Compose con back-stack type-safe      │
└──────────────────────────┴──────────────────────────────────────────────────┘
```

---

### Error 1: Volcados Masivos de Memoria en Gradle (4 GB de `.hprof`)
* **Causa Raíz:** Versiones inexistentes en `libs.versions.toml` y `gradle-wrapper.properties` provocaron bucles infinitos de reintentos en el *daemon* de Gradle hasta agotar la RAM (`OutOfMemoryError`).
* **Blindaje:**
  1. Utilizar exclusivamente versiones probadas:
     * **Gradle Wrapper:** `8.10.2`
     * **AGP (Android Gradle Plugin):** `8.7.2`
     * **Kotlin:** `2.0.21`
     * **KSP:** `2.0.21-1.0.25`
  2. En `gradle.properties`, fijar un límite sano de memoria:
     ```properties
     org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
     org.gradle.parallel=true
     org.gradle.caching=true
     ```

---

### Error 2: El Saldo no Cambiaba al Registrar Ingresos
* **Causa Raíz:** El saldo se leía de una tabla estática `wallets` que nunca era actualizada por `addTransaction()`.
* **Blindaje (Ledger Pattern):**
  * Ninguna pantalla ni caso de uso modifica saldos a mano.
  * El saldo de cada billetera se deriva automáticamente en Room sumando y restando movimientos mediante una `@DatabaseView`:
    $$\text{Saldo Billetera} = \text{Saldo Inicial} + \sum(\text{Ingresos}) - \sum(\text{Gastos}) - \sum(\text{Aportes})$$
  * **Efecto:** Cada vez que se inserta una transacción, Room detecta el cambio de tabla y emite instantáneamente el nuevo saldo por Flow a todas las pantallas abiertas.

---

### Error 3: Botones sin Respuesta y Dobles Registros
* **Causa Raíz:** Botones sin control de estado asíncrono. El usuario tocaba dos veces rápido y duplicaba gastos, o tocaba con un campo inválido y la app no daba ninguna señal visual.
* **Blindaje:**
  * Componente obligatorio `AndinaButton`:
    1. Si `isSaving == true`: muestra spinner interno y desactiva eventos de clic (`enabled = false`).
    2. Si el formulario no es válido: resalta el campo con error en color ámbar/rojo con texto de ayuda.
    3. Respuesta háptica inmediata (`HapticFeedbackType.LongPress` / Click táctil) en cada pulsación válida.

---

### Error 4: Fallo Silencioso al Digitar Cifras con Puntos (`$50.000`)
* **Causa Raíz:** El código usaba `toDoubleOrNull()`. Si el usuario ponía un punto o coma (`"50.000"`), Kotlin interpretaba cincuenta pesos o arrojaba `null`, abortando el guardado sin avisar. Además, `Double` acumulaba errores de coma flotante.
* **Blindaje:**
  * **Representación interna:** `Money` encapsulado en un entero `Long` (pesos COP sin decimales).
  * **Entrada de datos:** Teclado numérico donde el estado solo almacena dígitos puros (`"50000"`).
  * **Máscara visual:** Un `VisualTransformation` de Compose muestra visualmente `$ 50.000 COP` sin contaminar los datos numéricos.

---

### Error 5: El Botón Flotante (+) Dejaba de Funcionar fuera de Inicio
* **Causa Raíz:** El modal `QuickAddTransactionSheet` fue programado como hijo exclusivo de `HomeScreen.kt`. Al cambiar de pestaña, el componente dejaba de existir.
* **Blindaje:**
  * El formulario de Registro se aloja en el **App Shell (`AndinaAppShell`)**.
  * Es una ruta modal global accesible desde cualquier pestaña, notificación o atajo de escritorio mediante `navController.navigate(RegisterRoute())`.

---

### Error 6: Tirones y Pérdida de Fluidez en el Scroll (Lag)
* **Causa Raíz:** Colocar un bucle `forEach` dentro de un único bloque `item { }` de un `LazyColumn`. Esto desactivaba el reciclaje de vistas de Android y obligaba a Compose a instanciar todas las tarjetas simultáneamente.
* **Blindaje:**
  * Uso estricto de:
    ```kotlin
    items(
        items = transactionsList,
        key = { it.id },
        contentType = { it.type }
    ) { transaction ->
        TransactionItem(transaction = transaction)
    }
    ```
  * Modelos inmutables (`@Immutable`, `ImmutableList`) para evitar recomposiciones parasitarias.
  * Pausa automática de las animaciones de Muggy mientras el usuario se desplace rápidamente por una lista.

---

### Error 7: Consultas a Servidores Cloud que Ralentizaban la App
* **Causa Raíz:** Anteriormente el ViewModel intentaba solicitar consejos de IA a la nube en cada emisión de la base de datos local.
* **Blindaje:**
  * **100% On-Device:** El 100 % de los balances, alertas de presupuesto al 80%, cálculo de gastos hormiga y diagnósticos se ejecutan localmente en el teléfono sin tocar internet.
  * **Eliminación Total de APIs Cloud:** Se erradicó cualquier llamada externa; el asesor de Muggy se alimenta del *Andina Intelligence Core* (NLP local, series de tiempo EWMA y triaje financiero en < 8 ms).

---

### Error 8: Navegación Rígida sin Soporte para el Botón "Atrás"
* **Causa Raíz:** La navegación se basaba en un `when(tab)` con un simple `mutableStateOf`. El botón físico/gesto de "Atrás" de Android cerraba la aplicación.
* **Blindaje:**
  * Grafo oficial con **Navigation Compose Type-Safe**:
    * Soporte automático para el botón de retroceso de Android en cada nivel.
    * Diálogo interceptor "¿Descartar cambios?" si el usuario intenta salir de un formulario con datos escritos.
    * Preservación del estado de scroll entre pestañas con `saveState = true` y `restoreState = true`.

---

## 3. Protocolo de Construcción Segura (Fases de Implementación)

Para garantizar que estos errores jamás se repitan, la construcción de Andina se ejecutará en **4 fases progresivas**:

```
FASE 1: Cimientos Inquebrantables
├── Gradle limpio y verificado (sin bucles de dependencias)
├── Modelo Money (Long) y Room con Ledger View
└── Pruebas unitarias de que un ingreso SUMA de verdad
        │
        ▼
FASE 2: Navegación y Captura
├── App Shell con Navigation Compose type-safe
├── Teclado numérico propio y máscara COP
└── Formulario de registro global (+) funcional desde cualquier pestaña
        │
        ▼
FASE 3: Experiencia Visual y Muggy
├── Paleta Andean Mist & Moss y tarjetas Frosted Glass
├── Integración de Muggy y su máquina de estados reactiva
└── Bolsillos de ahorro, presupuestos y gastos hormiga
        │
        ▼
FASE 4: Inteligencia y Pulido
├── Ayudas interactivas y simuladores en tiempo real
├── Motor de Inteligencia On-Device (NLP local, Triaje, Diálogos NLG)
└── Cifrado SQLCipher con Android Keystore
```

---

## 4. Riesgos Operativos y Estrategia de Trabajo IA / IDE

Para garantizar que esta sesión de desarrollo sea quirúrgica y no repita sobrecargas, se establecen las siguientes reglas de trabajo:

### 4.1 Estrategia de Entrega por Hitos (Milestones Atómicos)
* **El riesgo:** Intentar generar más de 20 archivos en un solo turno satura la ventana de contexto de la IA, degrada la precisión sintáctica y provoca omisiones de código o errores de importación difíciles de rastrear.
* **La regla de trabajo:** La construcción se realiza en **bloques atómicos independientes**. No se pasa al siguiente bloque sin verificar que el anterior esté 100% libre de errores:
  1. **Milestone 1:** Configuración y sincronización limpia de Gradle (`libs.versions.toml`, `settings`, `build.gradle.kts`).
  2. **Milestone 2:** Sistema de diseño base (`Color`, `Type`, `Shape`, `Theme`).
  3. **Milestone 3:** Persistencia local base (Room estándar + entidades + DAOs + Libro Mayor).
  4. **Milestone 4:** Navegación y App Shell (`AndinaNavHost`, `AndinaBottomBar`).
  5. **Milestone 5:** Formulario de Registro con teclado numérico propio.
  6. **Milestone 6:** Pantallas completas (Inicio, Bolsillos, Movimientos).
  7. **Milestone 7:** Integración de Muggy, ayudas interactivas e Inteligencia On-Device.
  8. **Milestone 8:** Blindaje final con SQLCipher y Android Keystore.

### 4.2 Desacoplamiento de SQLCipher durante el Desarrollo
* **El riesgo:** Introducir SQLCipher en el minuto 1 mezcla errores de librerías nativas C++ (`.so`) con errores de lógica de negocio o de Gradle.
* **La regla de trabajo:**
  * Se implementa y valida primero la base de datos con **Room estándar**.
  * Una vez que las pantallas guarden, listen y sumen perfectamente, se conecta la factoría `SupportFactory` de **SQLCipher** como el paso de endurecimiento final de seguridad.

### 4.3 Inmutabilidad Obligatoria en Compose (Cero Jank)
* **El riesgo:** Pasar `List<T>` estándar a componentes Compose causa recomposiciones masivas involuntarias (*Jank* o lag).
* **La regla de trabajo:**
  * Uso estricto de `ImmutableList` de `kotlinx.collections.immutable` y anotación `@Immutable` en todos los modelos de UI.

### 4.4 Manejo Obligatorio de Edge-to-Edge y Window Insets (Android 14/15)
* **El riesgo:** En Android 14/15, la barra de gestos del sistema se dibuja encima de la barra inferior o del botón (+), y el teclado tapa el botón de "Guardar".
* **La regla de trabajo:**
  * El `Scaffold` raíz consume los insets con `WindowInsets.safeDrawing`.
  * Todo formulario con teclado numérico o inputs incluye `Modifier.imePadding()` para que el botón principal suba limpiamente sin ser tapado por el teclado.

### 4.5 Resiliencia ante Muerte de Proceso (*Process Death* y Rotación)
* **El riesgo:** Si el usuario pasa a Nequi a ver su saldo y Android suspende la app en segundo plano, al volver pierde el borrador del gasto que estaba llenando.
* **La regla de trabajo:**
  * Uso de `SavedStateHandle` en `RegisterViewModel` para que las cifras digitadas y la categoría seleccionada sobrevivan a la suspensión del sistema operativo.

### 4.6 Archivo `.gitignore` Blindado desde el Hito Cero
* **El riesgo:** La compilación de Gradle genera gigabytes de temporales (`.gradle`, `/build`, volcados `.hprof`, `local.properties`).
* **La regla de trabajo:**
  * Creación inmediata de un `.gitignore` estricto antes de lanzar la primera compilación.

### 4.7 Tipografía Manrope Embebida (*Bundled Fonts* en `res/font/`)
* **El riesgo:** Usar *Downloadable Fonts* de Google Play Services provoca que en modo avión o con datos lentos la app se vea con la fuente del sistema (Roboto) o parpadee feo al abrir (*FOUT*).
* **La regla de trabajo:**
  * Los archivos `.ttf` reales de Manrope (Bold, SemiBold, Regular) se incluyen directamente en `app/src/main/res/font/` (ocupan menos de 400 KB y aseguran fidelidad idéntica a Stitch al 100% desde el milisegundo 0).

### 4.8 Gráficos Vectoriales Puros en XML / Compose (Cero PNGs pesados)
* **El riesgo:** Cargar imágenes rasterizadas PNG para los logos de bancos o avatares de Muggy dispara el peso del APK y se pixela en pantallas de alta densidad (xxxhdpi).
* **La regla de trabajo:**
  * Todo ícono, logo de billetera e ilustración de Muggy se implementa mediante **Vector Drawables (`.xml`)** o primitivas de dibujo de Compose.

### 4.9 Puerta de Calidad con Pruebas Automatizadas (*Quality Gate* tras cada Fase)
* **El riesgo:** Acumular código no probado que falle silenciosamente y explote tarde al integrar varias pantallas.
* **La regla de trabajo:**
  * Cada una de las 12 fases concluye con sus **pruebas obligatorias específicas**:
    * Tests unitarios JUnit (`MoneyMathUnitTest`, `RoomLedgerUnitTest`) para lógica contable y base de datos.
    * Tests de navegación (`NavigationBackStackTest`) y validación de Previews para Compose.
  * Si un test falla, **se detiene el avance y se repara de inmediato** antes de escribir código de la siguiente fase.

---

## 5. Checklist de Validación antes de dar una Pantalla por "Terminada"

- [ ] **¿Compila limpiamente en Gradle sin advertencias?**
- [ ] **¿Pasan todas las pruebas automáticas de la fase en verde (`./gradlew testDebugUnitTest`)?**
- [ ] **¿El dinero se maneja como `Long` sin riesgo de redondeo?**
- [ ] **¿Los ingresos y gastos recalculan el balance automáticamente vía Ledger?**
- [ ] **¿Los botones tienen estado de carga y evitan el doble clic?**
- [ ] **¿El botón físico de "Atrás" se comporta de forma natural?**
- [ ] **¿La lista usa `key` y mantiene 60 fps estables en el scroll?**
- [ ] **¿La pantalla maneja `safeDrawing` e `imePadding` sin que el teclado tape botones?**
- [ ] **¿El formulario sobrevive a la suspensión de la app (`SavedStateHandle`)?**
- [ ] **¿La pantalla funciona al 100% en Modo Avión (sin internet)?**



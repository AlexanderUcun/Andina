# 🗺️ Plan Maestro de Fases Atómicas y Sub-Fases de Andina

> **Protocolo Quirúrgico de Desarrollo: 12 Fases Divididas en Sub-Fases Atómicas.**  
> Diseñado para eliminar al 100% el riesgo de pérdida de contexto, alucinaciones de código o sobrecarga de tokens en el IDE.  
> **Regla de oro:** No se avanza a la siguiente sub-fase sin que la actual compile limpiamente y esté verificada.

> [!TIP]
> **Documentos relacionados del proyecto:**
> * Arquitectura, Room y 26 pantallas: [**ARQUITECTURA.md**](./ARQUITECTURA.md)
> * Tokens, colores y componentes visuales: [**DISENO_VISUAL.md**](./DISENO_VISUAL.md)
> * Personalidad y lore de Muggy: [**MUGGY.md**](./MUGGY.md)
> * Animaciones y microinteracciones: [**MUGGY_INTERACCION.md**](./MUGGY_INTERACCION.md)
> * Motor técnico e ingeniería de Muggy: [**MUGGY_MOTOR_TECNICO.md**](./MUGGY_MOTOR_TECNICO.md)
> * Ayudas interactivas y simuladores: [**AYUDAS_INTERACTIVAS.md**](./AYUDAS_INTERACTIVAS.md)
> * Lecciones aprendidas y blindaje: [**LECCIONES_Y_BLINDAJE.md**](./LECCIONES_Y_BLINDAJE.md)
> * Índice maestro: [**README.md**](../README.md)

---

## 🚦 Resumen de la Ruta de las 12 Fases

```
[ FASE 01: Cimientos de Gradle ]  ──►  Sincronización perfecta, .gitignore, catálogo limpio
           │
[ FASE 02: Design System ]        ──►  Tokens de color, Manrope local, formas y tema M3
           │
[ FASE 03: Motor Contable Room ]  ──►  Entidades, DAOs, DatabaseView (Ledger) que SUMA
           │
[ FASE 04: Dominio y Casos ]      ──►  Money (Long), validaciones, detección de gastos hormiga
           │
[ FASE 05: Componentes Visuales ] ──►  FrostedGlassCard, WalletCard, PocketProgress, Ecosistema
           │
[ FASE 06: App Shell & Nav ]      ──►  Navigation Compose, barra inferior 4 pestañas con SafeDrawing
           │
[ FASE 07: Formulario Registro ]  ──►  Teclado numérico, máscara COP, Muggy asomado, imePadding
           │
[ FASE 08: Pantalla de Inicio ]   ──►  Hero Balance, ojo de privacidad, carruseles, rocío refresh
           │
[ FASE 09: Bolsillos y Metas ]    ──►  Progreso con frailejón vivo, aportes atómicos, retiros
           │
[ FASE 10: Movimientos & Filtros ]──►  Lista con Paging 3, swipe con hojas andinas, búsqueda
           │
[ FASE 11: Muggy & IA On-Device ] ──►  Locomoción, simuladores, NLP local, triaje y S19 Consultorio
           │
[ FASE 12: Seguridad y Pulido ]   ──►  Cifrado SQLCipher, Android Keystore, modo oscuro y tests
```

---

## 🔬 Desglose Detallado de Cada Fase en Sub-Fases Quirúrgicas

---

### 🔹 FASE 01: Cimientos del Entorno y Gradle Limpio
* **Objetivo:** Tener la estructura base de Android Studio que sincronice en segundos sin bucles ni saturación de RAM.
* **Sub-fases:**
  * **Sub-fase 1.1 (Higiene y Control de Memoria):** Crear `.gitignore` estricto (excluyendo `.hprof`, `/build`, `.gradle`) y `gradle.properties` (`jvmargs=-Xmx2048m`).
  * **Sub-fase 1.2 (Catálogo Centralizado de Dependencias):** Crear `gradle/libs.versions.toml` con versiones blindadas y probadas (AGP 8.7.2, Kotlin 2.0.21, KSP 2.0.21, Compose BOM, Room 2.6.1, Navigation).
  * **Sub-fase 1.3 (Configuración de Módulos y Manifiesto):** Crear `settings.gradle.kts`, `build.gradle.kts` raíz, `app/build.gradle.kts` y `app/src/main/AndroidManifest.xml`.
  * **Sub-fase 1.4 (Quality Gate):** Ejecutar `./gradlew tasks --dry-run` y verificar sincronización en limpio sin advertencias de desajuste.
* **Criterio de Aceptación:**
  * ✅ Gradle sincroniza en menos de 15 segundos con cero errores y consumo de memoria limitado a 2048 MB.

---

### 🔹 FASE 02: Sistema de Diseño Base "Andean Mist & Moss"
* **Objetivo:** Definir todos los colores, tipografía Manrope embebida, formas y tema visual para que ninguna pantalla use colores o estilos improvisados.
* **Sub-fases:**
  * **Sub-fase 2.1 (Tipografía Local Embebida):** Incluir fuentes en `app/src/main/res/font/` (`manrope_bold.ttf`, `manrope_semibold.ttf`, `manrope_regular.ttf`) y definir escala en `ui/theme/Type.kt` con números tabulares `tnum`.
  * **Sub-fase 2.2 (Tokens de Color Andino y Formas):** Crear `ui/theme/Color.kt` (paleta *Andean Mist & Moss*, identidades de billeteras) y `ui/theme/Shape.kt` (radios 8dp, 12dp, 18dp, 24dp y pills).
  * **Sub-fase 2.3 (Tema Material 3):** Crear `ui/theme/Theme.kt` (`AndinaTheme`) con tokens semánticos adaptables claro/oscuro.
  * **Sub-fase 2.4 (Quality Gate):** Crear `ui/theme/ThemePreviewTest.kt` y verificar ratios de contraste accesibles WCAG AA (≥ 4.5:1).
* **Criterio de Aceptación:**
  * ✅ Previsualización Compose (`@Preview`) renderizando Manrope y la paleta andina con fidelidad pixel-perfect.

---

### 🔹 FASE 03: Motor de Persistencia y Libro Mayor Contable (Room)
* **Objetivo:** Construir la base de datos local estándar con la vista contable (*Ledger*) que garantiza que todo ingreso y gasto sume o reste automáticamente.
* **Sub-fases:**
  * **Sub-fase 3.1 (Entidades Maestras):** Crear `WalletEntity.kt` (con billeteras colombianas por defecto) y `CategoryEntity.kt` (con iconos, colores y flag de gasto hormiga).
  * **Sub-fase 3.2 (Entidades Transaccionales):** Crear `TransactionEntity.kt` (con montos en `Long` entero, timestamp y tipo), `PocketEntity.kt` y `PocketMovementEntity.kt`.
  * **Sub-fase 3.3 (Libro Mayor - Ledger View):** Crear `@DatabaseView` `WalletBalanceView.kt` que calcula el saldo disponible sumando ingresos y restando gastos dinámicamente.
  * **Sub-fase 3.4 (DAOs Reactivos con Flow):** Crear `TransactionDao.kt`, `WalletDao.kt`, `PocketDao.kt` y `CategoryDao.kt`.
  * **Sub-fase 3.5 (Base de Datos y Seeder Inicial):** Crear `AppDatabase.kt` con seeder inicial de billeteras y categorías colombianas.
  * **Sub-fase 3.6 (Quality Gate Unitario):** Crear `data/local/RoomLedgerUnitTest.kt` (JUnit con Room en memoria) y verificar que un gasto descuente de `@DatabaseView` y una transferencia no duplique dinero.
* **Criterio de Aceptación:**
  * ✅ Compilación limpia de Room, KSP genera los adaptadores y los tests de balance contable pasan al 100%.

---

### 🔹 FASE 04: Capa de Dominio y Matemáticas Financieras
* **Objetivo:** Aislar la lógica de negocio y validación de dinero para que las pantallas no contengan cálculos manuales.
* **Sub-fases:**
  * **Sub-fase 4.1 (Value Object Monetario):** Crear `domain/model/Money.kt` (inline value class sobre `Long` con suma, resta y formateador en COP: `$ 14.850.000 COP`).
  * **Sub-fase 4.2 (Modelos de Dominio Inmutables):** Crear `Transaction.kt`, `Wallet.kt`, `Pocket.kt` y `FinancialSummary.kt`.
  * **Sub-fase 4.3 (Casos de Uso Transaccionales):** Crear `RegisterTransactionUseCase.kt` (validación de montos > 0 y detector de compras hormiga) y `ObserveBalancesUseCase.kt`.
  * **Sub-fase 4.4 (Caso de Uso de Ahorro Atómico):** Crear `ContributeToPocketUseCase.kt` (transacción atómica entre billetera y bolsillo con validación de saldo).
  * **Sub-fase 4.5 (Repositorio de Dominio):** Crear `FinanceRepository.kt` e implementar `data/repository/FinanceRepositoryImpl.kt`.
  * **Sub-fase 4.6 (Quality Gate Unitario):** Crear `domain/model/MoneyMathUnitTest.kt` y verificar precisión sin coma flotante y casos de uso con `./gradlew testDebugUnitTest`.
* **Criterio de Aceptación:**
  * ✅ Pruebas unitarias de dinero y casos de uso pasan 100% en verde.

---

### 🔹 FASE 05: Componentes UI Reutilizables de Stitch y Ecosistema
* **Objetivo:** Construir los bloques visuales de construcción certificados contra lag, con área táctil accesible y las bases del ecosistema vivo.
* **Sub-fases:**
  * **Sub-fase 5.1 (Vidrio Esmerilado):** Crear `ui/components/FrostedGlassCard.kt` (efecto frosted glass translúcido con borde sutil).
  * **Sub-fase 5.2 (Botones Hápticos Accesibles):** Crear `ui/components/AndinaButton.kt` (área táctil ≥ 48dp, estado de carga `isSaving`, micro-pulso háptico y anti-doble clic).
  * **Sub-fase 5.3 (Tarjetas de Billeteras y Transacciones):** Crear `ui/components/QuickWalletCard.kt` (identidades Nequi/Daviplata/Efectivo) y `TransactionItem.kt` (con badge de gasto hormiga).
  * **Sub-fase 5.4 (Barra de Progreso de Bolsillo):** Crear `ui/components/PocketProgressCard.kt` (animación de llenado suave con `FastOutSlowInEasing`).
  * **Sub-fase 5.5 (Ecosistema Vivo en Canvas):** Crear `ui/components/ecosystem/FrailejonView.kt` (planta viva que florece con tus metas) y `WaterRippleOverlay.kt` (ondas de agua cristalina para ingresos).
  * **Sub-fase 5.6 (Quality Gate Visual):** Crear `ui/components/ComponentsPreviewGallery.kt` con previews interactivos verificando estabilidad `@Immutable`.
* **Criterio de Aceptación:**
  * ✅ Previews interactivos de todos los componentes renderizan sin advertencias de recomposición en el IDE.

---

### 🔹 FASE 06: App Shell y Grafo de Navegación Type-Safe
* **Objetivo:** Implementar la estructura raíz de la app con Navigation Compose, respetando el botón físico "Atrás" y manejando Edge-to-Edge.
* **Sub-fases:**
  * **Sub-fase 6.1 (Rutas Type-Safe):** Crear `ui/navigation/AndinaDestinations.kt` con rutas serializables `@Serializable`: `HomeRoute`, `PocketsRoute`, `WalletsRoute`, `TransactionsRoute`, `RegisterRoute`, `StatsRoute`.
  * **Sub-fase 6.2 (Barra Inferior Andina Oficial):** Crear `ui/components/AndinaBottomBar.kt` con las 4 pestañas aprobadas (`[ Inicio ]` · `[ Bolsillos ]` · 🟢 `(+)` · `[ Billeteras ]` · `[ Movimientos ]`) y botón central flotante elevado -12dp.
  * **Sub-fase 6.3 (Scaffold Raíz con SafeDrawing):** Crear `ui/navigation/AndinaAppShell.kt` con manejo de insets del sistema, `SnackbarHost` y control global de apertura del modal de Muggy.
  * **Sub-fase 6.4 (Punto de Entrada MainActivity):** Configurar `MainActivity.kt` con `enableEdgeToEdge()` y llamada a `AndinaAppShell`.
  * **Sub-fase 6.5 (Quality Gate de Navegación):** Crear `ui/navigation/NavigationBackStackTest.kt` y comprobar alternancia de pestañas con preservación de scroll (`saveState`/`restoreState`) y retroceso natural.
* **Criterio de Aceptación:**
  * ✅ La app arranca en el emulador, muestra la barra inferior y permite alternar entre pestañas conservando el estado y respondiendo al botón "Atrás".

---

### 🔹 FASE 07: Formulario Global de Registro Rápido (+)
* **Objetivo:** Construir la pantalla/modal de registro rápido que funciona desde cualquier lugar, con teclado numérico propio y Muggy asomado ([`Muggy en la app.jpg`](file:///c:/Users/AlekeyG11/Documents/VsCode/Andina/Muggy%20en%20la%20app.jpg)).
* **Sub-fases:**
  * **Sub-fase 7.1 (Contrato MVI de Registro):** Crear `ui/register/RegisterContract.kt` (`RegisterUiState`, `RegisterAction`, `RegisterEffect`).
  * **Sub-fase 7.2 (ViewModel Resiliente):** Crear `ui/register/RegisterViewModel.kt` con `SavedStateHandle` (sobrevive a suspensión de la app en segundo plano).
  * **Sub-fase 7.3 (Teclado Numérico Táctil):** Crear `ui/register/components/NumericKeypad.kt` en tono arena cálido con atajos `+$10k`, `+$50k` y confirmación en palabras (*"Cincuenta mil pesos"*).
  * **Sub-fase 7.4 (Chips de Métodos de Pago y Hormiga):** Crear selectores Gasto/Ingreso, carrusel de métodos de pago (`Efectivo`, `Nequi`, `Daviplata`) con previsualización en vivo (*"Te quedarán $45.000 en Nequi"*), botón caramelo Hormiga y botón verde `[ Guardar Gasto ]`.
  * **Sub-fase 7.5 (Header de Muggy Asomado con Eye-Tracking):** Conectar `MuggyPeekingHeader.kt` en la parte superior con patitas sobre el card blanco ($Z\text{-Index} = 2$) y seguimiento ocular de dígitos tecleados.
  * **Sub-fase 7.6 (Quality Gate de Registro):** Crear `ui/register/RegisterViewModelTest.kt` validando digitación, guardado y actualización reactiva de balances.
* **Criterio de Aceptación:**
  * ✅ Tocar el botón (+) desde cualquier pestaña abre el registro con Muggy asomado, se digita `$50.000`, se previsualiza el saldo restante y se guarda con respuesta háptica y reactividad inmediata.

---

### 🔹 FASE 08: Pantalla de Inicio (Master Dashboard)
* **Objetivo:** Replicar con fidelidad pixel-perfect la pantalla `inicio_spectra_finanzas` de Stitch.
* **Sub-fases:**
  * **Sub-fase 8.1 (Contrato y ViewModel de Inicio):** Crear `ui/home/HomeContract.kt` y `HomeViewModel.kt`.
  * **Sub-fase 8.2 (Hero Card de Patrimonio y Privacidad):** Implementar tarjeta de saldo total con animación de odómetro (`AnimatedContent`) y alternancia de privacidad (`••••••••`).
  * **Sub-fase 8.3 (Grid de Periodo y Carruseles):** Implementar métricas de periodo (Entró, Salió, Te queda), barra de presupuesto y carruseles de billeteras y bolsillos activos.
  * **Sub-fase 8.4 (Pull-to-Refresh con Gota de Rocío):** Crear `ui/components/ecosystem/AndeanDewPullRefresh.kt` con gota elástica de rocío andino.
  * **Sub-fase 8.5 (Actividad Reciente y Banner Andino):** Lista de últimos 5 movimientos y banner editorial de conservación del páramo.
  * **Sub-fase 8.6 (Quality Gate de Inicio):** Crear `ui/home/HomeViewModelTest.kt` validando reactividad de flujos y modo privado.
* **Criterio de Aceptación:**
  * ✅ Pantalla de Inicio completa, reactiva a la base de datos y con scroll a 60 fps estables.

---

### 🔹 FASE 09: Módulo de Bolsillos y Metas de Ahorro Programado
* **Objetivo:** Replicar la pantalla `bolsillos_y_presupuestos` de Stitch, permitiendo crear metas y realizar aportes/retiros atómicos.
* **Sub-fases:**
  * **Sub-fase 9.1 (Contrato y ViewModel de Bolsillos):** Crear `ui/pockets/PocketsContract.kt` y `PocketsViewModel.kt`.
  * **Sub-fase 9.2 (Pantalla de Bolsillos con Flora Viva):** Crear `ui/pockets/PocketsScreen.kt` mostrando el total reservado y lista de metas con frailejón vivo que crece con el ahorro.
  * **Sub-fase 9.3 (Detalle de Meta y Proyección):** Crear `ui/pockets/PocketDetailScreen.kt` con anillo de progreso y semanas estimadas para completar la meta.
  * **Sub-fase 9.4 (Modal de Aporte/Retiro Atómico):** Crear `ui/pockets/PocketMoveBottomSheet.kt` con validación de saldo disponible y sonido de alcancía.
  * **Sub-fase 9.5 (Formulario de Creación de Bolsillo):** Crear `ui/pockets/PocketFormScreen.kt` con selección de nombre, icono, color y meta en COP.
  * **Sub-fase 9.6 (Quality Gate de Bolsillos):** Crear `ui/pockets/PocketMoveValidationTest.kt` validando bloqueo ante saldo insuficiente y disparo festivo al 100%.
* **Criterio de Aceptación:**
  * ✅ Crear un bolsillo "Fondo Emergencia", aportarle $200.000 desde Bancolombia y verificar que Bancolombia baje y el bolsillo suba instantáneamente.

---

### 🔹 FASE 10: Historial de Movimientos, Paging y Filtros
* **Objetivo:** Pantalla completa de auditoría financiera con alto rendimiento.
* **Sub-fases:**
  * **Sub-fase 10.1 (Contrato y ViewModel de Movimientos):** Crear `ui/transactions/TransactionsContract.kt` y `TransactionsViewModel.kt`.
  * **Sub-fase 10.2 (Lista Paginada con Paging 3):** Crear `ui/transactions/TransactionsScreen.kt` con agrupación por fechas (*Hoy*, *Ayer*, *Mes actual*).
  * **Sub-fase 10.3 (Swipe-to-Action con Hojas Andinas):** Deslizar para borrar con ráfaga sutil de hojas de frailejón y snackbar "Deshacer" de 5 segundos.
  * **Sub-fase 10.4 (Modal de Filtros Avanzados):** Filtros multi-criterio por método de pago (`Nequi`, `Daviplata`, `Efectivo`), solo compras hormiga y rango de fechas.
  * **Sub-fase 10.5 (Detalle y Edición de Movimiento):** Crear `ui/transactions/TransactionDetailScreen.kt`.
  * **Sub-fase 10.6 (Quality Gate de Transacciones):** Crear `ui/transactions/TransactionsFilterTest.kt` validando filtros y restauración al presionar "Deshacer".
* **Criterio de Aceptación:**
  * ✅ Scroll veloz sin lag con más de 100 movimientos de prueba y borrado con opción de deshacer funcional.

---

### 🔹 FASE 11: Muggy, Ecosistema Vivo e Inteligencia On-Device (Andina Intelligence Core)
* **Objetivo:** Dar vida a la mascota interactiva, simulación física del páramo y el motor de inteligencia financiera 100% offline (NLP local, series de tiempo, triaje financiero, diálogos NLG y pantalla S19 Consultorio).
* **Sub-fases:**
  * **Sub-fase 11.1 (Signos Vitales y Estados de Muggy):** Implementar `ui/components/muggy/MuggyAvatar.kt` con respiración sinusoidal, parpadeo estocástico y ritmo circadiano día/noche.
  * **Sub-fase 11.2 (Motor de Locomoción Física y Cinemática):** Implementar `MuggyLocomotion.kt` (caminata andina, saltos balísticos con *Squash & Stretch* y planeo con hoja de frailejón).
  * **Sub-fase 11.3 (Lluvia de Hojas Andinas y Gotas en Canvas):** Implementar `ui/components/ecosystem/LeafWindCanvas.kt` (confeti orgánico de hojas doradas/verdes y ondas de agua al cumplir metas).
  * **Sub-fase 11.4 (Súper Ayudas y Simuladores Interactivos):** Crear `ui/help/SpotlightHelper.kt` (foco brillante tutorial), `PocketSimulator.kt` ("La Vara del Páramo") y `AntExpenseMirror.kt` ("El Espejo Anual").
  * **Sub-fase 11.5 (Motor Semántico & NLP Colombiano On-Device):** Crear `core/intelligence/nlp/MerchantFuzzyClassifier.kt` (Trie + Levenshtein) y `ColombianBankSmsParser.kt` (decodificador offline de SMS de Bancolombia, Nequi y Daviplata).
  * **Sub-fase 11.6 (Motor Estadístico de Series de Tiempo):** Crear `core/intelligence/stats/EwmaAnomalyDetector.kt` (desviación de gasto diario), `QuincenaCycleAnalyzer.kt` (burn rate días 15/30) y cálculo de Días de Oxígeno (Runway).
  * **Sub-fase 11.7 (Sistema Experto de Triaje y Diálogos NLG):** Crear `core/intelligence/triage/FinancialHealthScorer.kt` (Score 0-100 ponderado) y `core/intelligence/nlg/MuggyDialogueEngine.kt` (generador modular de texto cálido con modismos colombianos y botones de acción en 1 tap).
  * **Sub-fase 11.8 (S19 Consultorio Financiero de Muggy):** Crear `ui/consultorio/ConsultorioContract.kt`, `ConsultorioViewModel.kt` y `ConsultorioScreen.kt` integrando los 4 motores locales y pegado rápido de SMS.
  * **Sub-fase 11.9 (Quality Gate de Inteligencia Local y Muggy):** Crear `core/intelligence/IntelligenceCoreTest.kt` validando que las inferencias tomen < 15 ms, sin conexiones de red, con 100% de precisión matemática en el triaje.
* **Criterio de Aceptación:**
  * ✅ Muggy camina y reacciona fluidamente a 60-120 FPS; el simulador proyecta gastos al instante; pegar un SMS de Bancolombia precarga el gasto sin internet; el Consultorio de Muggy diagnostica la salud financiera y emite consejos accionables en < 10 ms.

---

### 🔹 FASE 12: Blindaje de Seguridad, Modo Oscuro, Exportación y Publicación
* **Objetivo:** Cierre de grado bancario para producción, copias de seguridad portables y compilación release optimizada.
* **Sub-fases:**
  * **Sub-fase 12.1 (Android Keystore por Hardware):** Crear `data/security/KeystoreHelper.kt` para generar y custodiar claves criptográficas de 256 bits.
  * **Sub-fase 12.2 (Cifrado SQLCipher de la Base de Datos):** Conectar `net.zetetic:sqlcipher-android` (`SupportFactory`) a `AppDatabase.kt`.
  * **Sub-fase 12.3 (Bloqueo Biométrico):** Crear `ui/security/LockScreen.kt` con `BiometricPrompt` nativo (huella/rostro) y pin de respaldo.
  * **Sub-fase 12.4 (Paleta Modo Oscuro "Páramo Nocturno"):** Afinar paleta OLED `#121411` con niebla sutil y reflejos dorados en `Theme.kt`.
  * **Sub-fase 12.5 (Exportación Contable y Backup Portable):** Crear `data/export/CsvExporter.kt` (UTF-8 con BOM y `;`), `PdfReportExporter.kt` (`PdfDocument` nativo M3) y `BackupManager.kt` (archivo `.andina` serializado con `kotlinx.serialization` y cifrado AES-GCM opcional con PIN para migración entre celulares).
  * **Sub-fase 12.6 (Ofuscación y Reglas ProGuard/R8):** Configurar `proguard-rules.pro` para SQLCipher, Room y modelos serializables en compilación Release.
  * **Sub-fase 12.7 (Quality Gate Final de Seguridad, Backup y Release):** Crear `data/security/SqlCipherEncryptionTest.kt`, validar exportación/restauración íntegra de base de datos y ejecutar `./gradlew assembleRelease` sin advertencias.
* **Criterio de Aceptación:**
  * ✅ La base de datos está cifrada en el disco del teléfono, la app bloquea capturas si se desea, genera reportes CSV y PDF legibles, exporta/importa copias `.andina` correctamente y el APK Release se genera limpio.

---

## 📌 Protocolo de Ejecución por Sub-Fase

1. **Una sub-fase a la vez:** En cada paso se crea o modifica únicamente el grupo de archivos de esa sub-fase.
2. **Verificación inmediata:** Si una sub-fase tiene un error de sintaxis o tipo, se resuelve inmediatamente antes de pasar a la siguiente sub-fase.
3. **Cero pérdida de contexto:** La conversación se mantiene ligera y enfocada en micro-objetivos claros y alcanzables.
4. **Bitácora y Retroalimentación Continua:** Al terminar cada sub-fase, el agente documenta los archivos creados y el Quality Gate en [**RETROALIMENTACION.md**](./RETROALIMENTACION.md). El usuario cuenta con su sección editable para observaciones, ajustes o aprobación.
5. **Commits Atómicos en Git:** Cada sub-fase verificada se consolida con su commit descriptivo en Git para mantener un historial auditable.

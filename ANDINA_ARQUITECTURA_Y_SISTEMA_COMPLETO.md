# 🐻 ANDINA FINANZAS — ESPECIFICACIÓN TÉCNICA Y ARQUITECTURA COMPLETA
> **Documento Autónomo de Contexto y Transferencia para Modelos de IA e Ingenieros.**  
> *Este archivo contiene el 100% del contexto conceptual, arquitectónico, tecnológico, contable, patrones de diseño y catálogo de módulos del proyecto sin requerir fuentes externas.*

---

## 📌 1. Visión General & Filosofía del Producto

**Andina Finanzas** es una aplicación nativa para Android construida para el contexto económico y sociocultural de **Colombia**.

### Principios Fundamentales:
1. **Zero-Cloud & 100% Offline (Privacidad Absoluta):**  
   - Los datos financieros nunca salen del dispositivo. No hay backend remoto, telemetría invasiva ni dependencias de servicios externos en la nube.
   - El almacenamiento, las agregaciones contables y los motores de IA corren localmente.
2. **Identidad Cultural Colombiana & Estética Andina:**  
   - Moneda nativa en Pesos Colombianos (**COP**) con reglas de formato locales (sin decimales irrelevantes, puntuación estándar `$ 1.250.000 COP`).
   - Soporte para cuentas populares del país: **Nequi**, **Daviplata**, **Bancolombia**, **Efectivo** y **Tarjetas**.
   - Metáfora visual del **Páramo Andino**: atmósfera de niebla y musgo (*Andean Mist & Moss*), vegetación nativa y crecimiento de ahorros visualizado a través de **Frailejones vivos** dibujados en Canvas.
3. **Muggy (Asistente Empático On-Device):**  
   - El oso de anteojos andino que guía al usuario.  
   - En lugar de alertas punitivas o restrictivas, Muggy actúa como un acompañante pedagógico que analiza salud financiera, compras hormiga y autonomía de caja con empatía y humor sutil del altiplano.

---

## 🛠️ 2. Stack Tecnológico & Dependencias Clave

* **Plataforma:** Android Nativo (Compile SDK: 35, Target SDK: 35, Min SDK: 26).
* **Lenguaje:** Kotlin 2.0+ con Coroutines (`Flow`, `StateFlow`) y `kotlinx.serialization`.
* **Framework UI:** Jetpack Compose con **Material 3**.
  - Arquitectura **Edge-to-Edge** nativa (`WindowInsets.safeDrawing`, `imePadding()`).
  - Gráficos generativos y custom renderers mediante Compose `Canvas`.
* **Inyección de Dependencias (DI):** Dagger Hilt 2.51+ (`@HiltViewModel`, `@AndroidEntryPoint`, `@Module`, `@InstallIn`).
* **Persistencia Local:**  
  - **Android Room 2.6+** con vistas contables indexadas (`wallet_balances`).
  - **SQLCipher** (cifrado AES-256 en reposo sobre la base de datos SQLite).
* **Navegación:** Jetpack Navigation Compose 2.8+ con **Type-Safe Routes** (rutas basadas en `@Serializable data object` / `data class`).
* **Seguridad de Respaldos:**  
  - Formato binario `.andina` cifrado con **AES-256 GCM**.
  - Derivación de llaves criptográficas con **PBKDF2WithHmacSHA256** (10.000 iteraciones + salt aleatorio de 16 bytes).
  - Autenticación biométrica con `androidx.biometric.BiometricPrompt`.
* **Motor de IA On-Device:** Módulo Kotlin puro de baja latencia (< 8 ms), sin modelos externos ni OpenAI/Gemini remotos.
* **Pruebas:** JUnit 4, Kotlinx Coroutines Test, MockK y tests de consistencia aritmética en `testDebugUnitTest`.

---

## 🏛️ 3. Arquitectura del Software & Patrones

El proyecto implementa estrictamente **Clean Architecture** estructurada en 4 capas desacopladas, complementada con el patrón **MVI (Model-View-Intent)** y **UDF (Unidirectional Data Flow)**:

```
com.andina.finanzas/
├── core/                   # Utilitarios compartidos y Motores de IA
│   ├── intelligence/       # Muggy AI Core (NLG, NLP, Estadísticas, Triaje)
│   └── util/               # Extensiones, formateadores de fecha y moneda
├── data/                   # Implementación de persistencia y fuentes de datos
│   ├── export/             # BackupManager (Cifrado AES-256 GCM .andina, exportador CSV/PDF)
│   ├── local/              # Base de datos Room, DAOs, Entidades, SQLCipher y Seeds
│   │   ├── dao/            # TransactionDao, WalletDao, CategoryDao, PocketDao, etc.
│   │   ├── entity/         # Tablas SQLite (TransactionEntity, WalletEntity, etc.)
│   │   ├── view/           # Vistas SQL de saldo recalculado (WalletBalanceView)
│   │   └── seed/           # Semillas iniciales (categorías y billeteras colombianas)
│   ├── mapper/             # Mappers bidireccionales Entity <-> Domain
│   └── repository/         # Implementación de contratos de repositorio (FinanceRepositoryImpl)
├── domain/                 # Lógica de negocio pura (independiente del framework Android)
│   ├── model/              # Modelos inmutables (Money, Transaction, Pocket, Wallet, UserProfile)
│   ├── repository/         # Interfaces de repositorio (FinanceRepository)
│   └── usecase/            # Casos de uso de grano fino atómicos
└── ui/                     # Capa de presentación reactiva con Jetpack Compose
    ├── budget/             # Gestión y diagnóstico de presupuestos
    ├── calendar/           # Vista de calendario y mapa térmico de flujo de caja
    ├── components/         # Design System (FrostedGlassCard, MuggyAvatar, FrailejonView, etc.)
    ├── consultorio/        # Consultorio financiero de Muggy & Simulador
    ├── customization/      # Personalización de categorías y tarjetas
    ├── export/             # Pantalla de exportación y respaldo cifrado
    ├── help/               # Centro de ayuda y educación financiera
    ├── home/               # Dashboard principal, resumen de periodo y patrimonio
    ├── navigation/         # AndinaAppShell, BottomBar y grafo Type-Safe
    ├── notifications/      # Alertas, confirmaciones quincenales y gastos recurrentes
    ├── pockets/            # Bolsillos de ahorro con crecimiento botánico
    ├── profile/            # Perfil de usuario, biometría y preferencias
    ├── recurring/          # Gestión de gastos/ingresos fijos (1-Tap confirm)
    ├── register/           # Modal de registro rápido de movimientos
    ├── security/           # Bloqueo por PIN y biometría
    ├── theme/              # Andean Mist & Moss tokens, Tipografía y Modificadores de renderizado
    ├── transactions/       # Libro Mayor contable, auditoría y filtros
    └── wallets/            # Billeteras, cuentas de banco y transferencias
```

### Reglas Arquitectónicas Inquebrantables:
1. **Inmutabilidad y Cero Punto Flotante en Finanzas:**  
   - Se utiliza el Value Object `Money(val pesos: Long)` para todas las operaciones monetarias. Jamás se usan `Float` o `Double` para guardar o sumar dinero.
2. **Libro Mayor Atómico (Ledger System):**  
   - Los saldos de las billeteras **no** son un campo mutable suelto que se incrementa o decrementa arbitrariamente.  
   - Se calculan y sincronizan mediante transacciones atómicas registradas en la tabla `transactions` y son consolidadas por la vista Room `wallet_balances` (evita desincronizaciones e inconsistencias contables).
3. **Flujo MVI / UDF:**  
   - La UI emite `Intent` / eventos al `ViewModel`.
   - El `ViewModel` ejecuta Use Cases en el `CoroutineScope(Dispatchers.Default / IO)`.
   - La UI consume un único `StateFlow<UiState>` inmutable.

---

## 🐻 4. Motor de Inteligencia Local On-Device (Muggy AI Core)

Ubicado en `com.andina.finanzas.core.intelligence`, corre 100% en el procesador del teléfono:

1. **`SmsFinanceParser.kt` (NLP / Regex Colombiano):**  
   - Analizador sintáctico determinístico optimizado para SMS bancarios colombianos (Bancolombia, Nequi, Daviplata).
   - Extrae automáticamente: monto COP, comercio/destinatario, billetera origen y tipo (gasto, transferencia o ingreso).
2. **`QuincenaCycleAnalyzer.kt` (Estadísticas & Series EWMA):**  
   - Detecta el ciclo salarial del usuario (típicamente días 15 y 30 en Colombia).
   - Utiliza promedios móviles exponencialmente ponderados (EWMA) para detectar anomalías de consumo acelerado en los primeros días tras el pago de la quincena.
3. **`FinancialHealthScorer.kt` (Triaje Financiero):**  
   - Calcula el **Score de Salud Andina** (rango 0 a 100).
   - Clasifica el estado en 4 niveles paramunos:
     - 🌿 **Holgado (80 - 100):** Ahorro sólido, sin sobregiros.
     - 🌾 **Preventivo (60 - 79):** Equilibrio de ingresos y gastos pero bajo margen de imprevistos.
     - 🍂 **Ajuste (40 - 59):** Alertas por gastos hormiga o desviación de presupuesto > 80%.
     - ❄️ **Crítico (0 - 39):** Flujo de caja negativo y riesgo de insolvencia quincenal.
   - Calcula la métrica clave: **Días de Autonomía de Caja** (cuántos días puede vivir el usuario con su saldo actual si se detuvieran los ingresos).
4. **`DialogueGenerator.kt` (NLG - Natural Language Generation):**  
   - Genera respuestas y consejos en lenguaje natural adaptados al tono de Muggy: afectuoso, reflexivo, empático y libre de tecnicismos intimidantes.

---

## 🎨 5. Sistema de Diseño: Andean Mist & Moss (M3)

### Tokens y Filosofía Visual:
* **Paleta de Colores (`Color.kt`):**
  - `AndeanPrimary` (`#2D5A43` / Verde Musgo Profundo): Simboliza estabilidad, vegetación de páramo y crecimiento.
  - `AndeanSecondary` (`#A3B899` / Verde Salvia / Niebla): Acentos suaves y contenedores armónicos.
  - `AndeanTertiary` (`#D4A373` / Ámbar Frailejón): Tonos cálidos que recuerdan los vellos dorados del frailejón.
  - `AndeanBackgroundLight` (`#F7F9F6`) y `AndeanBackgroundDark` (`#111613`): Fondos orgánicos que reemplazan el blanco y negro plano.
  - `AndeanError` (`#C85A54` / Teja Andina): Advertencias visibles pero no agresivas.
* **Componentes de Alto Nivel:**
  - `FrostedGlassCard`: Tarjeta de cristal esmerilado con translucidez y borde sutil.
  - `financialAmbientGlow`: Modificador de brillo perimetral ambiental para tarjetas maestras de saldo.
  - `FrailejonView`: Renderizado en `Canvas` procedural que modela la altura, hojas y flor del frailejón en proporción al % alcanzado en los bolsillos de ahorro.
  - `HeroBalanceCard`: Tarjeta de patrimonio principal con odómetro vertical animado (`AnimatedContent`), jerarquía tipográfica para COP y toggle de privacidad háptico.
  - `AndeanDewPullRefresh`: Indicador de recarga personalizado inspirado en una gota de rocío matutina.

---

## 📋 6. Catálogo de Módulos Funcionales (20 Módulos)

1. **Perfil del Usuario (`UserProfileScreen.kt`):** Configuración de nombre, títulos honoríficos de páramo, avatares de Muggy, biometría y opciones visuales.
2. **Selector de Categorías (`CategoryChipsSelector.kt`):** Matriz ergonómica en dos filas (`LazyHorizontalGrid`) para selección rápida y accesible de categorías.
3. **Gestión de Billeteras (`WalletsScreen.kt`, `AddWalletModal.kt`):** Administración de efectivo, cuentas de ahorro y tarjetas con balance reactivo.
4. **Presupuesto Adaptativo (`BudgetScreen.kt`):** Límites globales y por categoría con avisos tempranos al 75% y 90%.
5. **Centro de Alertas & Notificaciones (`NotificationsScreen.kt`):** Avisos de presupuesto, detección de hormigas y confirmaciones de pago fijo.
6. **Exportación & Copias de Seguridad (`ReportExporterScreen.kt`, `BackupManager.kt`):** Reportes en PDF/CSV y copias portables cifradas `.andina` con PIN.
7. **Consultorio de Muggy (`ConsultorioScreen.kt`):** Diagnóstico conversacional, score de salud y cálculo de autonomía de caja.
8. **Recurrentes & Quincenas (`RecurringManagerScreen.kt`):** Confirmación a 1-tap de ingresos y pagos periódicos; nunca se aplican transacciones a ciegas.
9. **Hub de Personalización (`CustomizationHubScreen.kt`):** Gestión y visibilidad de tarjetas en el Home y categorías personalizadas.
10. **Calendario Financiero (`CalendarScreen.kt`):** Calendario con mapa térmico de gastos e inspección día por día.
11. **Insets & Safe Drawing (`AndinaAppShell.kt`):** Respeto estricto a las barras del sistema (Edge-to-Edge nativo sin solapamiento de teclado o barra de navegación).
12. **Bolsillos de Ahorro (`PocketCard.kt`, `PocketDetailScreen.kt`):** Metas de ahorro personificadas en frailejones vivos con inyección y retiro de caudal.
13. **Tope Monetario Seguro (`RegisterViewModel.kt`):** Validación estricta hasta $99.999.999 COP y doble confirmación preventiva para montos superiores a $10.000.000 COP.
14. **Control de Sobregiros (`RegisterScreen.kt`):** Detección previa antes de procesar un gasto que exceda el saldo de la billetera.
15. **Gota de Rocío (`AndeanDewPullRefresh.kt`):** Mecanismo Pull-to-Refresh estilizado con Material 3.
16. **Navegación Type-Safe (`AndinaDestinations.kt`):** Grafo con rutas y argumentos fuertemente tipados sin cadenas mágicas.
17. **Frailejón Vivo en Canvas (`FrailejonView.kt`):** Dibujo vectorial procedural con márgenes de seguridad para no recortar la floración.
18. **Teclado Numérico Nativo (`RegisterScreen.kt`):** Uso de teclado del sistema optimizado con `imePadding()` para máxima velocidad de captura.
19. **Edición Integral de Movimientos (`TransactionDetailScreen.kt`):** Modificación retroactiva de fecha, hora, billetera, categoría y nota en el Libro Mayor.
20. **Simulador de Fondo (`PocketSimulator.kt`):** Proyector de semanas y cuotas de ahorro con feedback interactivo de Muggy.

---

## 🔒 7. Esquema de Datos y Persistencia (Room & SQLCipher)

### Entidades Principales:
- `TransactionEntity`: `id`, `amountPesos` (Long), `type` (EXPENSE, INCOME, TRANSFER), `categoryId`, `walletId`, `targetWalletId`, `timestamp`, `note`, `isRecurring`.
- `WalletEntity`: `id`, `name`, `type` (CASH, BANK, DIGITAL_WALLET, CREDIT_CARD), `colorHex`, `iconRes`, `initialBalancePesos`.
- `CategoryEntity`: `id`, `name`, `type` (INCOME, EXPENSE), `iconName`, `colorHex`, `isCustom`, `orderIndex`.
- `PocketEntity`: `id`, `name`, `targetAmountPesos`, `currentAmountPesos`, `targetDate`, `walletId`, `frailejonStage`.
- `RecurringTransactionEntity`: `id`, `amountPesos`, `walletId`, `categoryId`, `frequency` (MONTHLY, BIWEEKLY), `dayOfExecution`, `lastProcessedDate`.

### Vistas SQL:
- `WalletBalanceView` (`wallet_balances`):  
  Une el saldo inicial de cada billetera con la suma de ingresos, egresos y transferencias (origen vs destino), garantizando consistencia matemática inmediata.

---

## 🚀 8. Comandos de Compilación & Verificación

```powershell
# 1. Configurar JDK 17 (utilizando el entorno embebido de Android Studio)
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

# 2. Ejecutar la suite completa de pruebas unitarias
./gradlew testDebugUnitTest --no-daemon

# 3. Compilar APK de depuración
./gradlew assembleDebug --no-daemon
```

---

## 💡 9. Guía para Modelos de IA & Revisores de Código

Cuando te soliciten trabajar, extender o auditar este repositorio, debes cumplir estas pautas:
1. **Nunca violes la política Zero-Cloud:** No propongas bibliotecas como Firebase, Supabase remoto o llamadas a endpoints REST cloud a menos que sea explícitamente pedido.
2. **Usa `Money` y no tipos flotantes:** Toda matemática monetaria debe expresarse en `Money` / `Long` en pesos colombianos.
3. **Mantén el ecosistema Jetpack Compose puro:** Usa Material 3, `WindowInsets.safeDrawing`, estados inmutables y rutas serializables en `AndinaDestinations`.
4. **Respeta la personalidad de Muggy:** Los mensajes y diálogos deben conservar la empatía, el respeto y la identidad botánica andina.

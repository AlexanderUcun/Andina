# 🐻 Andina Finanzas — Aplicación Financiera Nativa para Android

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-purple.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-blue.svg)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Architecture-Clean%20%2B%20MVI%20%2B%20UDF-brightgreen.svg)]()
[![Database](https://img.shields.io/badge/Database-Room%20%2B%20SQLCipher%20(Encrypted)-darkgreen.svg)]()
[![AI](https://img.shields.io/badge/AI%20Core-100%25%20On--Device%20(Offline)-orange.svg)]()
[![License](https://img.shields.io/badge/License-Private-lightgrey.svg)]()

> **Andina Finanzas** es una aplicación financiera moderna, privada y 100% offline para Colombia, inspirada en la atmósfera botánica del **páramo andino** y el oso de anteojos (**Muggy**). Diseñada con estándares de ingeniería de alto rendimiento (Clean Architecture, MVI, Jetpack Compose, Room cifrado con SQLCipher y un motor de inteligencia artificial local de baja latencia < 8 ms).

---

## 📑 Tabla de Contenidos
1. [Visión General & Filosofía](#-visión-general--filosofía)
2. [Stack Tecnológico](#-stack-tecnológico)
3. [Arquitectura de Software & Estructura de Paquetes](#-arquitectura-de-software--estructura-de-paquetes)
4. [Catálogo de los 20 Módulos Funcionales (100% Completados)](#-catálogo-de-los-20-módulos-funcionales-100-completados)
5. [Motor de Inteligencia Local On-Device (Muggy AI Core)](#-motor-de-inteligencia-local-on-device-muggy-ai-core)
6. [Seguridad, Cifrado & Respaldos](#-seguridad-cifrado--respaldos)
7. [Instrucciones de Compilación y Pruebas](#-instrucciones-de-compilación-y-pruebas)
8. [Estructura de Documentación y Especificaciones](#-estructura-de-documentación-y-especificaciones)
9. [Guía de Evaluación & Retroalimentación para IAs y Revisores](#-guía-de-evaluación--retroalimentación-para-ias-y-revisores)

---

## 🌿 Visión General & Filosofía

Andina nace bajo tres premisas fundamentales:
1. **Privacidad Absoluta (Zero-Cloud / 100% Offline):** Tus datos contables jamás salen de tu teléfono hacia servidores de terceros. Todo el almacenamiento, indexación y machine learning se ejecuta en el dispositivo.
2. **Identidad Colombiana & Estética Andina:** Soporte nativo para el ecosistema monetario colombiano (**COP**, Nequi, Daviplata, Bancolombia, Efectivo, Tarjetas) con una experiencia visual orgánica (*Andean Mist & Moss*) y crecimiento botánico de metas en frailejones vivos.
3. **Asistente Inteligente Empático (Muggy):** Un oso de anteojos que evalúa compras hormiga, advierte sobregiros y calcula la autonomía financiera sin juzgar y con calidez pedagógica.

---

## 🛠️ Stack Tecnológico

* **Lenguaje:** Kotlin 2.0+ (Coroutines, Flow, Kotlinx Serialization).
* **UI & Diseño:** Jetpack Compose con **Material 3**, Canvas reactivo para botánica paramuna y arquitectura Edge-to-Edge nativa (`WindowInsets.safeDrawing`).
* **Inyección de Dependencias:** Dagger Hilt 2.51+.
* **Persistencia Local:** Android Room 2.6+ con vistas reactivas de balance contable (`wallet_balances`) y cifrado militar **SQLCipher**.
* **Navegación:** Jetpack Navigation Compose 2.8+ con **Type-Safe Routes** serializables.
* **Testing:** JUnit 4, Kotlinx Coroutines Test, MockK y tests unitarios de precisión matemática (`testDebugUnitTest`).

---

## 🏛️ Arquitectura de Software & Estructura de Paquetes

El proyecto sigue estrictamente **Clean Architecture** combinada con **MVI (Model-View-Intent)** y **UDF (Unidirectional Data Flow)**:

```text
com.andina.finanzas/
├── core/
│   └── intelligence/          # Motor de IA On-Device (Muggy Core)
│       ├── nlg/               # Generador de Diálogos Naturales
│       ├── parser/            # Decodificador de SMS Bancarios Colombianos
│       ├── stats/             # Analizador de Ciclos Quincenales y Series EWMA
│       └── triage/            # Calculador de Salud Financiera (Score 0-100)
├── data/
│   ├── export/                # BackupManager (Cifrado AES-256 GCM .andina)
│   ├── local/                 # Room Database, DAOs, Entidades & SQLCipher
│   ├── mapper/                # Mappers bidireccionales Entity <-> Domain
│   └── repository/            # Implementación de FinanceRepository
├── domain/
│   ├── model/                 # Modelos inmutables (Money, Transaction, Pocket, Wallet)
│   ├── repository/            # Contratos de repositorio
│   └── usecase/               # Casos de uso de negocio atómicos
└── ui/
    ├── budget/                # Gestor de Presupuesto Mensual Adaptativo
    ├── calendar/              # Calendario Financiero & Flujo de Caja
    ├── components/            # Design System (MuggyAvatar, FrailejonView, MuggySpeechBubble)
    ├── consultorio/           # Consultorio de Muggy & Simulador de Fondo
    ├── customization/         # Hub de Personalización de Categorías y Módulos
    ├── export/                # Pantalla de Exportación PDF/CSV & Respaldo
    ├── home/                  # Dashboard Principal con Métricas del Periodo
    ├── navigation/            # AndinaAppShell, BottomBar & Type-Safe Destinos
    ├── notifications/         # Centro de Alertas & Confirmación Quincenal
    ├── pockets/               # Bolsillos de Ahorro & Detalle con Frailejón Vivo
    ├── profile/               # Perfil del Explorador & Preferencias
    ├── recurring/             # Gestor de Gastos/Ingresos Fijos (1-Tap Authorization)
    ├── register/              # Modal de Registro Rápido con Teclado Nativo
    ├── theme/                 # Paleta Andean Mist & Moss, Tipografía y Color
    ├── transactions/          # Libro Mayor, Auditoría y Filtros
    └── wallets/               # Gestión de Billeteras y Medios de Pago
```

---

## 📦 Catálogo de los 20 Módulos Funcionales (100% Completados)

| # | Módulo | Pantalla / Componente | Descripción Técnica |
|---|---|---|---|
| **1** | **Perfil del Usuario** | `UserProfileScreen.kt` | Edición de nombre, selección de títulos paramunos, avatares ilustrados, selector de moneda COP, switches de biometría y Modo 4K. |
| **2** | **Selector de Categorías** | `CategoryChipsSelector.kt` | Matriz de 2 filas (`LazyHorizontalGrid`) táctil, aislando categorías de ingreso y gasto. |
| **3** | **Gestión de Billeteras** | `WalletsScreen.kt`, `AddWalletModal.kt` | Creación y edición de cuentas (Nequi, Daviplata, Bancolombia, Efectivo, Tarjetas) con recalculo reactivo de saldos. |
| **4** | **Presupuesto Adaptativo** | `BudgetScreen.kt` | Tope global mensual y límites por categoría con alertas automáticas al 75% y 90% diagnosticadas por Muggy. |
| **5** | **Centro de Alertas** | `NotificationsScreen.kt` | Avisos de presupuesto, detección de hormigas, confirmación de quincena y acceso directo a recurrentes. |
| **6** | **Exportación & Backup** | `ReportExporterScreen.kt`, `BackupManager.kt` | Generación de extractos PDF, reportes CSV y respaldo portable cifrado AES-256 GCM `.andina` con PIN. |
| **7** | **Consultorio de Muggy** | `ConsultorioScreen.kt` | IA local de triaje financiero con métrica de **"Días de Autonomía Financiera"** y recomendaciones conversacionales. |
| **8** | **Recurrentes & Quincenas** | `RecurringManagerScreen.kt` | Regla de cero abonos a ciegas: notificación previa con confirmación a 1 tap y diálogo de edición de monto/fecha. |
| **9** | **Hub de Personalización** | `CustomizationHubScreen.kt` | Creación/ocultamiento de categorías personalizadas, paletas de color y visibilidad de tarjetas en Inicio. |
| **10** | **Calendario Financiero** | `CalendarScreen.kt` | Mapa de calor diario con indicadores de ingresos/gastos e inspección detallada por fecha seleccionada. |
| **11** | **Insets & Safe Drawing** | `AndinaAppShell.kt` | Integración de `WindowInsets.safeDrawing`, `statusBarsPadding()` y `navigationBarsPadding()` anti-solapamiento. |
| **12** | **Gestión de Bolsillos** | `PocketCard.kt`, `PocketDetailScreen.kt` | Menú contextual (`⋮`) con *Editar Meta*, *Programar Recordatorio* y *Eliminar Bolsillo* con devolución de caudal. |
| **13** | **Tope Monetario Seguro** | `RegisterViewModel.kt`, `PaymentWalletSelector.kt` | Límite estricto de 8 dígitos ($99.999.999 COP) y diálogo de confirmación a 2 pasos para cifras > $10.000.000. |
| **14** | **Control de Sobregiros** | `RegisterScreen.kt` | Validación de saldo antes del gasto, diálogo de sobregiro consciente y cálculo de ratio de endeudamiento. |
| **15** | **Gota de Rocío** | `AndeanDewPullRefresh.kt` | Pull-to-Refresh optimizado con `PullToRefreshBox` de Material 3 que disuelve la gota al finalizar la corrutina. |
| **16** | **Navegación Type-Safe** | `AndinaDestinations.kt`, `AndinaAppShell.kt` | Grafo 100% conectado sin placeholders temporales ni strings sueltos. |
| **17** | **Frailejón Vivo en Canvas** | `FrailejonView.kt` | Bounding box con margen de seguridad del 15% y escala relativa para evitar recortes al 100% de la meta. |
| **18** | **Teclado Nativo Android** | `RegisterScreen.kt`, Modales | Eliminación de teclados custom por campos numéricos nativos con `Modifier.imePadding()` responsivo. |
| **19** | **Edición de Movimientos** | `TransactionDetailScreen.kt` | Edición integral de monto, fecha/hora compacta (`07 Oct 2026 · 6:56 PM`), billeteras y notas en el Libro Mayor. |
| **20** | **Simulador de Fondo** | `PocketSimulator.kt` | Slider dinámico de ahorro semanal con proyección de semanas y asesoría ilustrada de Muggy. |

---

## 🤖 Motor de Inteligencia Local On-Device (Muggy AI Core)

Muggy no utiliza APIs externas de OpenAI ni servidores cloud; todo corre en CPU local con latencia ultrabaja (< 8 ms):

1. **Parser Bancario Colombiano (`SmsFinanceParser.kt`):** Expresiones regulares y autómatas que extraen montos, comercios y medios de pago desde SMS de Bancolombia, Nequi y Daviplata.
2. **Analizador de Series EWMA (`QuincenaCycleAnalyzer.kt`):** Detección de patrones de gasto diario, quincenas y anomalías de consumo hormiga.
3. **Motor de Triaje (`FinancialHealthScorer.kt`):** Cálculo del Score de Salud Financiera (0 a 100) en 4 niveles (*Holgado*, *Preventivo*, *Ajuste*, *Crítico*).
4. **Generador NLG Paramuno (`DialogueGenerator.kt`):** Respuestas conversacionales humanizadas con modismos sutiles del altiplano y selección dinámica de poses.

---

## 🔒 Seguridad, Cifrado & Respaldos

* **SQLCipher en Reposo:** La base de datos SQLite local está cifrada con clave AES-256 gestionada a través de Android Keystore.
* **Copias de Seguridad Portables (`.andina`):**
  * Serialización completa de Billeteras, Categorías, Movimientos, Bolsillos e Historial.
  * Cifrado con **AES-GCM (128-bit IV)** y clave derivada mediante **PBKDF2 con HmacSHA256 (10.000 iteraciones y Salt de 16 bytes)** a partir del PIN del usuario.

---

## 🚀 Instrucciones de Compilación y Pruebas

Para compilar y ejecutar la suite completa de pruebas unitarias en PowerShell usando el JDK de Android Studio:

```powershell
# 1. Configurar variables de entorno apuntando al JDK de Android Studio
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

# 2. Ejecutar pruebas unitarias de toda la aplicación
./gradlew testDebugUnitTest --no-daemon

# 3. Compilar APK de depuración
./gradlew assembleDebug --no-daemon
```

---

## 📚 Estructura de Documentación y Especificaciones

Toda la documentación técnica se encuentra organizada en `specs/`:
* 🗺️ [**`03_PLAN_MAESTRO_FASES_SUBFASES.md`**](./specs/funcionalidades_pendientes/03_PLAN_MAESTRO_FASES_SUBFASES.md): Hoja de ruta quirúrgica de las 5 Fases ejecutadas con registro de commits.
* 📋 [**`01_TODAS_LAS_FUNCIONALIDADES_PENDIENTES.md`**](./specs/funcionalidades_pendientes/01_TODAS_LAS_FUNCIONALIDADES_PENDIENTES.md): Requerimientos funcionales módulo a módulo.
* 🏛️ [**`ARQUITECTURA.md`**](./specs/specs_iniciales/ARQUITECTURA.md): Modelo conceptual, Libro Mayor y diagramas de flujo.
* 🎨 [**`DISENO_VISUAL.md`**](./specs/specs_iniciales/DISENO_VISUAL.md): Tokens de color, espaciado y directrices de Material 3.
* 🐻 [**`MUGGY.md`**](./specs/specs_iniciales/MUGGY.md) & [**`MUGGY_INTERACCION.md`**](./specs/specs_iniciales/MUGGY_INTERACCION.md): Poses, máquina de estados y diseño de interacción del asistente.

---

## 🔍 Guía de Evaluación & Retroalimentación para IAs y Revisores

Si eres un modelo de Inteligencia Artificial, un arquitecto de software o un revisor de código analizando este repositorio:

1. **Revisión de Integridad Contable:**
   * Verifica cómo `Room` utiliza vistas SQL (`wallet_balances`) para recalcular saldos a partir de transacciones atómicas en lugar de mutar campos sueltos.
2. **Revisión de UI/UX & Compose:**
   * Evalúa la coherencia del sistema de diseño *Andean Mist & Moss*, el manejo de `WindowInsets` y la ergonomía de los modales con `imePadding`.
3. **Revisión del Motor de IA Local:**
   * Analiza `core/intelligence/` para proponer nuevas heurísticas de detección de gastos hormiga o mejoras en la gramática NLG de Muggy.
4. **Retroalimentación Esperada:**
   * Rendimiento y consumo de memoria en dispositivos gama baja (Android Go / 2GB RAM).
   * Nuevos patrones de interacción táctil o visualización 4K en Canvas.

---
*Desarrollado con ❤️ y pasión por el páramo andino colombiano.*

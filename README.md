# 🐻 Andina - Aplicación Financiera Nativa para Android

Aplicación financiera moderna para Colombia desarrollada en **Kotlin**, **Jetpack Compose**, **Room Cifrado con SQLCipher**, y potenciada por el **Motor de Inteligencia Local de Muggy (100% offline, privado y en el dispositivo)**.

Diseñada bajo el sistema de diseño **Andean Mist & Moss** de Google Stitch, con soporte para billeteras colombianas (**Nequi, Daviplata, Bancolombia, Efectivo**), bolsillos de ahorro programado y el asistente inteligente **Muggy / Andino Observador** (inspirado en el Oso de Anteojos).

---

## 📚 Índice Maestro de Documentación del Proyecto

El proyecto cuenta con un sistema de 4 documentos maestros complementarios y perfectamente interconectados:

| Documento | Enfoque | Contenido Clave |
|---|---|---|
| 🗺️ [**PLAN_DE_FASES.md**](./PLAN_DE_FASES.md) | **Estrategia de Ejecución** | Ruta quirúrgica de 12 fases atómicas independientes con criterios de aceptación para evitar pérdida de contexto o alucinaciones. |
| 🏛️ [**ARQUITECTURA.md**](./ARQUITECTURA.md) | **Lógica, Datos y Navegación** | Clean Architecture, Room + SQLCipher, libro mayor (*ledger*), casos de uso, las 26 pantallas con fichas detalladas y matriz de reactividad. |
| 🎨 [**DISENO_VISUAL.md**](./DISENO_VISUAL.md) | **Sistema de Diseño e Interfaz** | Paleta *Andean Mist & Moss*, tokens semánticos, Manrope, frosted glass de Apple, grilla de espaciado y reglas hápticas. |
| 🐻 [**MUGGY.md**](./MUGGY.md) | **Biblia del Personaje** | Historia, personalidad, anatomía visual, catálogo de poses, redacción/tono de voz y gamificación de los pisos térmicos andinos. |
| ⚡ [**MUGGY_INTERACCION.md**](./MUGGY_INTERACCION.md) | **Comportamiento y Microinteracciones** | Máquina de estados en Kotlin, animaciones en Compose a 60 fps, flujos de tareas paso a paso, easter eggs y optimización para gama baja. |
| 💡 [**AYUDAS_INTERACTIVAS.md**](./AYUDAS_INTERACTIVAS.md) | **Sistemas de Ayuda y Aprendizaje** | Foco guiado (*Spotlights*), simuladores de ahorro en vivo, espejo anual de gastos hormiga, modo práctica/sandbox y auto-resolución de errores. |
| 🚀 [**MUGGY_MOTOR_TECNICO.md**](./MUGGY_MOTOR_TECNICO.md) | **Motor Técnico y Código de Muggy** | Rutas de paquetes y código Kotlin de producción: máquina de estados, físicas Bézier, seguimiento ocular y SoundPool. |
| 🛡️ [**LECCIONES_Y_BLINDAJE.md**](./LECCIONES_Y_BLINDAJE.md) | **Prevención y Estabilidad** | Análisis de los fallos previos (4 GB de dumps, ingresos que no sumaban, lag), soluciones de blindaje y checklist de estabilidad. |

---

## 🏛️ Resumen de Arquitectura Técnica

El proyecto sigue los principios de **Clean Architecture** y directrices oficiales de Android Architecture:

* **Presentation Layer:** Jetpack Compose con UDF (Unidirectional Data Flow) y contratos MVI (State, Action, Effect) en `collectAsStateWithLifecycle`.
* **Domain Layer:** Casos de uso atómicos (`RegisterTransactionUseCase`, `ContributeToPocketUseCase`, etc.) y modelo monetario inmutable en pesos colombianos (`Long`).
* **Data Layer:** Room Database con vistas reactivas de libro mayor (`wallet_balances`), eliminando cualquier desincronización de saldos.
* **Seguridad:** Cifrado de base de datos con **SQLCipher** y claves protegidas mediante Android Keystore.
* **Andina Intelligence Core:** Motor de Inteligencia On-Device en 5 niveles (NLP local Levenshtein, decodificador de SMS bancarios colombianos, series de tiempo EWMA para detección de anomalías, triaje financiero con Score 0-100 y generador NLG paramuno de diálogos cálidos; 100% offline, cero costos, latencia < 8 ms).

---

## 🗺️ Mapa de Pantallas de la Aplicación

La aplicación se estructura en 4 pestañas principales y un botón central de acción rápida con Muggy:

1. **Inicio (`HomeRoute`):** Saldo total consolidado, balances de periodo, carrusel de billeteras, bolsillos activos, consejo de Muggy y reportes analíticos.
2. **Bolsillos (`PocketsRoute`):** Metas de ahorro programado con aportes, retiros y celebraciones al 100%.
3. **🟢 (+) Registrar con Muggy (`RegisterRoute`):** Modal interactivo global de captura ultra-rápida (Gasto/Ingreso) con teclado numérico integrado y Muggy asomado ([`Muggy en la app.jpg`](file:///c:/Users/AlekeyG11/Documents/VsCode/Andina/Muggy%20en%20la%20app.jpg)).
4. **Billeteras (`WalletsRoute`):** Bolsas de saldo y métodos de pago (`Efectivo`, `Nequi`, `Daviplata`, `Bancolombia`), con creación ilimitada y cero datos sensibles.
5. **Movimientos (`TransactionsRoute`):** Historial paginado con filtros avanzados por método, búsqueda y acciones por deslizamiento.

---

## 🧪 Protocolo de Pruebas de Calidad (*Quality Gate*)
Cada una de las 12 fases de [`PLAN_DE_FASES.md`](./PLAN_DE_FASES.md) incluye obligatoriamente sus pruebas automáticas:
* **Fases 1–4 (Cimientos y Datos):** Tests unitarios JUnit (`MoneyMathUnitTest`, `RoomLedgerUnitTest`) que validan la matemática y el libro contable antes de tocar la interfaz.
* **Fases 5–10 (Interfaz y Navegación):** Previews de Compose, pruebas de navegación (`NavigationBackStackTest`) y validación en emulador.
* **Fases 11–12 (Muggy y Blindaje):** Tests de máquina de estados, apertura de base de datos cifrada (SQLCipher) y `./gradlew assembleRelease`.
> **Regla:** Ninguna fase se aprueba si no compila limpiamente y pasa sus pruebas al 100%.

Para consultar los flujos paso a paso y la ficha técnica de cada pantalla, consulta [**ARQUITECTURA.md**](./ARQUITECTURA.md).
Para la especificación estética y componentes, consulta [**DISENO_VISUAL.md**](./DISENO_VISUAL.md).
Para las reacciones del asistente y animaciones, consulta [**MUGGY.md**](./MUGGY.md) y [**MUGGY_INTERACCION.md**](./MUGGY_INTERACCION.md).

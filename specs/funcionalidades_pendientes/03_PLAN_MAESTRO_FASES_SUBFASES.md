# 🗺️ PLAN MAESTRO ANTI-ALUCINACIÓN: FASES Y SUB-FASES QUIRÚRGICAS

**Propósito Único:** Servir como la única fuente de verdad y hoja de ruta estricta e inmutable para corregir los 20 Módulos de la App Andina.  
**Regla de Oro Anti-Alucinación:** El agente DEBE ejecutar y verificar una sub-fase a la vez. PROHIBIDO improvisar funcionalidades no descritas o saltar de fase sin validación compilada y runtime.

---

## 📌 REGISTRO DE ESTADO Y RETROALIMENTACIÓN EN TIEMPO REAL
> **Estado Actual:** `TODAS LAS 5 FASES CULMINADAS AL 100%`  
> **Último Commit Local:** `feat(ecosystem): hub de personalizacion, calendario de flujo de caja y exportacion cifrada`  
> **Última Sub-fase Validada:** Sub-fase 5.3  

```text
[x] FASE 1: ARQUITECTURA DE INSETS, NAVEGACIÓN BASE Y TECLADO NATIVO
    [x] Sub-fase 1.1: Solución Definitiva de WindowInsets y Status Bar Padding (Módulo 11)
    [x] Sub-fase 1.2: Remoción de Teclado Custom e Integración del Teclado Nativo (Módulos 18 y 2)
    [x] Sub-fase 1.3: Reparación del Componente Gota de Rocío / Pull-To-Refresh (Módulo 15)
[x] FASE 2: INTEGRIDAD CONTABLE, CONTROL DE SALDOS Y EDICIÓN COMPLETA
    [x] Sub-fase 2.1: Tope Monetario Máximo ($99.999.999 COP) y Alerta Inteligente (Módulo 13)
    [x] Sub-fase 2.2: Control de Saldos Negativos y Capacidad de Deuda (Módulo 14)
    [x] Sub-fase 2.3: Edición Integral de Movimientos (Módulo 19)
[x] FASE 3: ENRUTAMIENTO REAL, FORMULARIOS Y CATEGORÍAS EN GRID
    [x] Sub-fase 3.1: Eliminación Total de Placeholders (Módulo 16)
    [x] Sub-fase 3.2: Formulario Funcional "Añadir / Editar Billetera" (Módulo 3)
    [x] Sub-fase 3.3: Rediseño de Categorías en Grid y Menú de 3 Puntos (Módulos 2 y 12)
[x] FASE 4: EXPERIENCIA ILUSTRADA, CANVAS FIT Y CONSULTORIO DE MUGGY
    [x] Sub-fase 4.1: Bounding Box y Aspect Ratio en Frailejones (Módulo 17)
    [x] Sub-fase 4.2: Rediseño de "La Vara" y Globos de Diálogo de Muggy (Módulos 20 y 7)
    [x] Sub-fase 4.3: Perfil y Configuración del Usuario (Módulo 1)
[x] FASE 5: ECOSISTEMA AVANZADO (PRESUPUESTOS, RECURRENTES Y REPORTES)
    [x] Sub-fase 5.1: Gestor de Presupuesto Mensual Adaptativo (Módulo 4)
    [x] Sub-fase 5.2: Gastos e Ingresos Fijos Programables (Módulo 8)
    [x] Sub-fase 5.3: Notificaciones, Hub, Calendario y Exportación (Módulos 5, 9, 10 y 6)
```

---

## 🐙 PROTOCOLO ESTRICTO DE GIT Y GITHUB
1. **Commits Locales Atómicos por Sub-fase:** Al completar y validar cada sub-fase (ej. Sub-fase 1.1), se realizará un commit local descriptivo siguiendo Convencional Commits (ej. `feat(insets): aplicar statusBarsPadding y WindowInsets.safeDrawing`).
2. **Push a GitHub Únicamente con Autorización Explícita:** Queda **ESTRICTAMENTE PROHIBIDO** hacer `git push` a GitHub de manera autónoma.
3. **Petición de Push al Finalizar Cada Fase Completa:** Al culminar todas las sub-fases de una Fase completa (ej. culminar la FASE 1), el agente solicitará la autorización explícita del usuario para subir los cambios a GitHub.

---

## 🏗️ FASE 1: ARQUITECTURA DE INSETS, NAVEGACIÓN BASE Y TECLADO NATIVO
> **Meta de la Fase:** Eliminar todos los empalmes con la barra del sistema (batería/hora), quitar teclados custom y garantizar que los modales floten suavemente sin desbordes.

### 📍 Sub-fase 1.1: Solución Definitiva de WindowInsets y Status Bar Padding (Módulo 11)
- **Archivos Objetivo:** `AndinaAppShell.kt`, `HomeScreen.kt`, `WalletsScreen.kt`, `PocketsScreen.kt`, `TransactionsScreen.kt`, `ConsultorioScreen.kt`, `TransactionDetailScreen.kt`.
- **Acciones Técnicas:**
  1. Reemplazar `contentWindowInsets = WindowInsets(0)` por `WindowInsets.safeDrawing` en `Scaffold`.
  2. Aplicar `Modifier.statusBarsPadding()` en los contenedores superiores de cada pantalla.
  3. Aplicar `Modifier.navigationBarsPadding()` en la barra inferior de navegación (`AndinaBottomBar`).
- **Criterio de Cierre Sub-fase 1.1:** El header de ninguna pantalla colisiona ni se encima con el reloj o íconos de la barra de estado de Android.

### 📍 Sub-fase 1.2: Remoción de Teclado Custom e Integración del Teclado Nativo Android (Módulos 18 y 2)
- **Archivos Objetivo:** `TransactionModal.kt`, `PocketContributionModal.kt`, `DecodificadorSmsScreen.kt`.
- **Acciones Técnicas:**
  1. Eliminar el componente `NumericKeyboardGrid` / botones custom (`1, 2, 3... C, 0, ⌫`).
  2. Sustituir por `OutlinedTextField` o `BasicTextField` con `keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)`.
  3. Vincular `FocusRequester` para que al desplegar el modal el teclado del celular se abra automáticamente en modo numérico.
  4. Aplicar `Modifier.imePadding()` en el contenedor modal para deslizar la vista hacia arriba al escribir notas o cifras.
- **Criterio de Cierre Sub-fase 1.2:** El usuario interactúa 100% con el teclado numérico de su celular sin distorsiones en el formulario.

### 📍 Sub-fase 1.3: Reparación del Componente Gota de Rocío / Pull-To-Refresh (Módulo 15)
- **Archivos Objetivo:** `AndeanDewPullRefresh.kt`, `HomeScreen.kt`.
- **Acciones Técnicas:**
  1. Migrar a `PullToRefreshBox` de Material 3 Compose.
  2. Configurar el callback de actualización para disolver la gota inmediatamente al terminar la corrutina de refresh.
- **Criterio de Cierre Sub-fase 1.3:** Al deslizar hacia abajo en Inicio, la gota baja suavemente y se retira sin quedarse pegada en la parte superior.

---

## ✅ FASE 2: INTEGRIDAD CONTABLE, CONTROL DE SALDOS Y EDICIÓN COMPLETA (100% COMPLETADA)
> **Meta de la Fase:** Proteger la coherencia financiera del usuario restringiendo disparates contables y permitiendo la edición total de transacciones.

### 📍 Sub-fase 2.1: Tope Monetario Máximo ($99.999.999 COP) y Alerta Inteligente (Módulo 13) [COMPLETADA]
- **Archivos Objetivo:** `RegisterViewModel.kt`, `RegisterContract.kt`, `RegisterScreen.kt`, `PaymentWalletSelector.kt`.
- **Acciones Técnicas:**
  1. Restringir la longitud del campo de monto a 8 dígitos máximos ($99.999.999 COP).
  2. Implementar alerta modal a 2 pasos si la cifra ingresada supera los $10.000.000 COP (*"¿Estás seguro de registrar esta cifra?"*).
  3. Reestructurar la alerta de saldo en una caja independiente para no pisar la etiqueta de *"Método de pago"*.
- **Criterio de Cierre Sub-fase 2.1:** Imposible ingresar montos de 500 mil millones; los textos de alerta se ven ordenados.

### 📍 Sub-fase 2.2: Control de Saldos Negativos, Sobregiros y Capacidad de Deuda (Módulo 14) [COMPLETADA]
- **Archivos Objetivo:** `RegisterViewModel.kt`, `RegisterContract.kt`, `RegisterScreen.kt`.
- **Acciones Técnicas:**
  1. Validar el saldo disponible de la billetera seleccionada antes de confirmar un gasto.
  2. Si el saldo es menor al gasto en billetera débito/efectivo, lanzar diálogo: *"Saldo insuficiente en [Billetera]. ¿Deseas registrar como Crédito/Deuda o cambiar de billetera?"*.
  3. Calcular el ratio de endeudamiento recomendado (máx 30%-40% de ingresos registrados) y notificar si se excede.
- **Criterio de Cierre Sub-fase 2.2:** No se generan saldos negativos accidentales sin la autorización explícita del usuario.

### 📍 Sub-fase 2.3: Edición Integral de Movimientos y Ajuste de Layout en Detalle (Módulo 19) [COMPLETADA]
- **Archivos Objetivo:** `TransactionDetailScreen.kt`, `TransactionDetailViewModel.kt`, `TransactionDetailContract.kt`.
- **Acciones Técnicas:**
  1. Habilitar edición clicable en: Monto, Billetera Origen, Billetera Destino, Categoría, Fecha/Hora y Nota.
  2. Al guardar, invocar actualización en SQLite (`repository.updateTransaction`) que re-calcula las vistas de balance reactivas automáticamente.
  3. Formatear la fecha a versión compacta (`07 Oct 2026 · 6:56 PM`) y asignar `weight(1f)` para evitar empalmes con el título *"Fecha y hora"*.
- **Criterio de Cierre Sub-fase 2.3:** Se pueden editar todos los campos de una transacción y los saldos de las billeteras se recalculan exactamente.

---

## ✅ FASE 3: ENRUTAMIENTO REAL, FORMULARIOS Y CATEGORÍAS EN GRID (100% COMPLETADA)
> **Meta de la Fase:** Eliminar el 100% de los mensajes "En Desarrollo" y conectar formularios reales de creación y edición.

### 📍 Sub-fase 3.1: Eliminación Total de Placeholders y Grafo de Navegación Directo (Módulo 16) [COMPLETADA]
- **Archivos Objetivo:** `AndinaAppShell.kt`, `AndinaDestinations.kt`, `UserProfileScreen.kt`, `BudgetScreen.kt`, `NotificationsScreen.kt`.
- **Acciones Técnicas:**
  1. Eliminar Snacks/Toasts de *"Gestión en desarrollo"* o *"Fase 10"*.
  2. Conectar el clic en Presupuesto -> `BudgetScreen` (`BudgetRoute`).
  3. Conectar el clic en Patrimonio -> `WalletsScreen` / `WalletsRoute`.
  4. Conectar el clic en Avatar -> `UserProfileScreen` (`UserProfileRoute`).
  5. Conectar el clic en Notificaciones -> `NotificationsScreen` (`NotificationsRoute`).
- **Criterio de Cierre Sub-fase 3.1:** Cero mensajes temporales al tocar contenedores en Inicio.

### 📍 Sub-fase 3.2: Formulario Funcional "Añadir / Editar Billetera" (Módulo 3) [COMPLETADA]
- **Archivos Objetivo:** `WalletsScreen.kt`, `AddWalletModal.kt`, `WalletsViewModel.kt`, `WalletsContract.kt`, `FinanceRepository.kt`.
- **Acciones Técnicas:**
  1. Crear la pantalla funcional `WalletsScreen` con balance total consolidado, lista reactiva de billeteras y tarjeta de cuenta.
  2. Crear el modal `AddWalletModal` con campos: Nombre, Tipo (Nequi, Daviplata, Bancolombia, Efectivo, Tarjeta, Otra), Saldo Inicial (con teclado numérico y 8 dígitos máx) y Selector de Color.
  3. Guardar, actualizar y archivar billeteras en la base de datos local recalculando saldos en tiempo real.
- **Criterio de Cierre Sub-fase 3.2:** El botón *"Añadir Billetera"* abre el formulario y la nueva billetera aparece inmediatamente en la lista.

### 📍 Sub-fase 3.3: Rediseño de Categorías en Grid y Menú de 3 Puntos en Bolsillos (Módulos 2 y 12) [COMPLETADA]
- **Archivos Objetivo:** `CategoryChipsSelector.kt`, `PocketsScreen.kt`, `PocketCard.kt`, `PocketsViewModel.kt`, `PocketsContract.kt`.
- **Acciones Técnicas:**
  1. Reemplazar la fila horizontal infinita de categorías por un `LazyHorizontalGrid` de 2 filas con navegación táctil fluida.
  2. Implementar el menú `DropdownMenu` (`⋮`) en cada tarjeta de bolsillo: `Editar Meta`, `Programar Recordatorio` y `Eliminar Bolsillo` (con diálogo interactivo de confirmación).
- **Criterio de Cierre Sub-fase 3.3:** Categorías organizadas en matriz limpia y bolsillos totalmente administrables.

---

## ✅ FASE 4: EXPERIENCIA ILUSTRADA, CANVAS FIT Y CONSULTORIO DE MUGGY (100% COMPLETADA)
> **Meta de la Fase:** Corregir los escalados vectoriales de Canvas y humanizar la interacción de la IA.

### 📍 Sub-fase 4.1: Bounding Box y Aspect Ratio en Frailejones/Árboles (Módulo 17) [COMPLETADA]
- **Archivos Objetivo:** `FrailejonView.kt`, `PocketsScreen.kt`.
- **Acciones Técnicas:**
  1. Restringir la escala máxima de crecimiento en el Canvas con `ContentScale.Fit`.
  2. Configurar un margen de seguridad interno (*padding*) del 15% respecto a los bordes del contenedor.
- **Criterio de Cierre Sub-fase 4.1:** Al alcanzar el 100% de la meta, las hojas superiores no se recortan contra la tarjeta.

### 📍 Sub-fase 4.2: Rediseño de "La Vara" y Burbuja de Diálogo de Muggy (Módulo 20 y Módulo 7) [COMPLETADA]
- **Archivos Objetivo:** `ConsultorioScreen.kt`, `MuggySpeechBubble.kt`.
- **Acciones Técnicas:**
  1. Renombrar la pestaña *"La Vara"* a **"Simulador de Fondo"**.
  2. Cambiar la métrica *"Días de Oxígeno"* a **"Días de Autonomía Financiera"**.
  3. Transformar el texto `"Muggy dice: ..."` en un componente `MuggySpeechBubble` con el mini-avatar ilustrado a un costado.
- **Criterio de Cierre Sub-fase 4.2:** Interacción inmersiva y natural en el consultorio.

### 📍 Sub-fase 4.3: Perfil y Configuración del Usuario (Módulo 1) [COMPLETADA]
- **Archivos Objetivo:** `UserProfileScreen.kt`, `UserPreferencesRepository.kt`.
- **Acciones Técnicas:**
  1. Crear la pantalla de perfil para editar nombre, apodo en el páramo y seleccionar avatar ilustrado.
- **Criterio de Cierre Sub-fase 4.3:** Nombre y avatar editables desde la esquina superior izquierda.

---

## ✅ FASE 5: ECOSISTEMA AVANZADO (PRESUPUESTOS, RECURRENTES Y REPORTES) (100% COMPLETADA)
> **Meta de la Fase:** Implementar las herramientas avanzadas del ecosistema Andina.

### 📍 Sub-fase 5.1: Gestor de Presupuesto Mensual Adaptativo (Módulo 4) [COMPLETADA]
- **Archivos:** `BudgetScreen.kt`, `BudgetViewModel.kt`. (Definición de tope global y por categoría con alertas al 75% y 90%).

### 📍 Sub-fase 5.2: Gastos e Ingresos Fijos Programables con Autorización (Módulo 8) [COMPLETADA]
- **Archivos:** `RecurringManagerScreen.kt`, `NotificationsScreen.kt`, `AndinaAppShell.kt`. (Confirmación a 1 tap, edición previa de monto/fecha y regla de cero abonos a ciegas).

### 📍 Sub-fase 5.3: Centro de Notificaciones, Hub de Personalización, Calendario y Exportación (Módulos 5, 9, 10 y 6) [COMPLETADA]
- **Archivos:** `NotificationsScreen.kt`, `CustomizationHubScreen.kt`, `CalendarScreen.kt`, `ReportExporterScreen.kt`, `BackupManager.kt`, `UserProfileScreen.kt`, `AndinaAppShell.kt`. (Generación de PDF/CSV, Backup cifrado AES-256 SQLite con PIN, Calendario interactivo y Personalización de categorías y tarjetas de Inicio).

---

*Documento creado en `specs/funcionalidades_pendientes/03_PLAN_MAESTRO_FASES_SUBFASES.md`.*

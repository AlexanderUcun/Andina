# PLAN DE EJECUCIÓN VISUAL ANDINA — v4 "PLATINUM LIGHT" (AUDITADO)

> **Estado:** Plan auditado línea por línea contra el código real (12-oct-2026). **Aún no hay código del rediseño implementado.**
> **Stack verificado:** AGP 8.7.2 · Kotlin 2.0.21 · Compose BOM 2024.10.01 (Compose 1.7) · Room 2.6.1 + SQLCipher · Navigation 2.8.3 Type-Safe · minSdk 26 / target 35 · fuente Manrope.
> **Identidad aprobada:** Tema claro "Platino Mineral & Oro Café" (grises cálidos, plata cepillada, oro champán, café moca, azul laguna).

---

## 0. HALLAZGOS DE LA AUDITORÍA (evidencia en el código)

| # | Hallazgo | Evidencia | Impacto |
|---|---|---|---|
| H1 | 🔴 **La base de datos borra todos los datos ante cualquier cambio de esquema** | `AppDatabase.kt:73` → `.fallbackToDestructiveMigration()`, `version = 1` | Un cambio accidental en una entidad elimina el historial financiero del usuario sin aviso |
| H2 | 🔴 **243 colores hardcodeados** (`Color(0x…)`) en **39 archivos**, + 82 `Color.White/Black`, + 9 `isSystemInDarkTheme()` fuera del tema | grep sobre `ui/` | Misma causa raíz que los iconos: cambiar la paleta en `Color.kt` **no** actualizaría toda la app |
| H3 | 🔴 **183 iconos Material distintos** en **43 archivos** | grep `Icons.*` | Por eso solo cambió Home |
| H4 | 🟠 **La app no tiene ícono de launcher ni splash propio** | `res/` solo contiene `font`, `values`, `xml` (sin `mipmap*`, sin `themes.xml`) | Ícono genérico de Android; en Android 12+ el splash del sistema no coincide con `AndinaPreloadScreen` |
| H5 | 🟠 **Colores guardados en la base de datos** por el usuario | `colorHex` en `CategoryEntity`, `WalletEntity`, `PocketEntity`; selectores en `CustomizationHub`, `PocketFormBottomSheet`, `AddWalletModal` | La paleta nueva debe convivir con colores antiguos ya guardados |
| H6 | 🟠 **Contraste:** varios colores de la paleta aprobada **no son legibles como texto** sobre fondo claro | Cálculo WCAG (ver §3) | El oro champán `#C5A059` da ≈2.5:1 sobre blanco (mínimo 4.5:1) |
| H7 | 🟠 **No existe `autoSize` en Compose 1.7** | BOM 2024.10.01; `autoSize` llega en Compose 1.8 | `MoneyText` debe implementarse a mano o actualizar el BOM |
| H8 | 🟡 **0 usos de `stringResource`**; `strings.xml` tiene 4 líneas | grep | Todo el texto está hardcodeado; "recurso localizado" del plan anterior no aplica |
| H9 | 🟡 **No hay deep links** ni `PendingIntent` | `AndroidManifest.xml` solo tiene el launcher | El ítem "deep-links antiguos" era incorrecto; solo hay 2 llamadas internas (`AndinaAppShell.kt:142` Consultorio, `:184` Calendar) |
| H10 | 🟡 **`Money.times(factor: Double)`** existe | `Money.kt:26` | Viola la regla "nunca Double en montos" |
| H11 | 🟡 **Formateadores duplicados** de pesos | `formatPesos()` en `BudgetScreen`, `formatCop()` en `RegisterScreen` | Riesgo de formatos distintos para el mismo monto |
| H12 | 🟡 **77 `contentDescription = null`** | grep | Accesibilidad: hay que distinguir iconos decorativos de iconos con significado |
| H13 | 🟡 **No hay helper de "reducir animaciones"** ni detekt/lint configurado | grep / `build.gradle.kts` | El "candado anti-regresión" necesita otro mecanismo |
| H14 | 🟡 **Solo 11 tests unitarios, 0 instrumentados**, sin tests de captura de pantalla | `app/src/test`, sin `androidTest` | Una migración de 43 archivos no tiene red de seguridad visual |
| H15 | ✅ **Muggy en Presupuesto ya alerta el exceso exacto por categoría** | `BudgetScreen.kt:256-268` | Solo falta el caso de tope **global** > 100 % y el chip de advertencia |
| H16 | ✅ Ya existen `FrailejonView.kt`, `AndeanDewPullRefresh.kt`, `AndinaHapticFeedback.kt`, `FrostedGlassCard` (**23** consumidores, no ~30), `FrostedGlassBadge` (1 consumidor) | grep | Se **evolucionan**, no se duplican |
| H17 | ℹ️ `StatsRoute` existe (3 referencias) y no figura en la nueva navegación | `AndinaDestinations.kt` | Hay que decidir su destino |

---

## 1. DECISIONES PENDIENTES (bloquean la ejecución de la fase indicada)

| ID | Decisión | Opciones | Recomendación | Bloquea |
|---|---|---|---|---|
| **D1** | ¿Qué pasa si el teléfono está en modo oscuro? | (a) Forzar siempre claro · (b) Claro por defecto + "Platinum Night" oscuro en una fase futura | **(b) diseñado más tarde**; ahora forzar claro, pero con la infraestructura de tokens lista para el oscuro | Fase 3A |
| **D2** | Fidelidad de iconos | Los mockups son renders 3D; a 24 dp un vector no puede verse igual | **2 niveles:** iconos de UI (20-24 dp) en vector 2.5D con gradientes; ilustraciones (48-96 dp: estados vacíos, tarjetas, categorías grandes) más ricas | Fase 3A |
| **D3** | `MoneyText` con auto-escalado | (a) Implementación propia con `TextMeasurer` · (b) Subir el BOM de Compose a 1.8+ | **(a)**, sin riesgo de dependencias; el BOM se actualiza aparte | Fase 3A |
| **D4** | `StatsRoute` | Eliminar · Fusionar en "Plan & IA" | **Fusionar** su contenido útil en Plan & IA | Fase 2 |
| **D5** | Colores antiguos guardados por el usuario | (a) Mapear al vuelo a la paleta nueva · (b) Conservarlos tal cual | **(a)** mapeo en tiempo de render (sin escribir en la base) + presets nuevos en los selectores | Fase 3A |
| **D6** | Oro legible | El oro brillante no es legible en texto pequeño sobre claro | **Oro brillante solo decorativo y en cifras grandes**; texto monetario pequeño en "oro bronce" `#6E5220` | Fase 3A |

---

## 2. PROTOCOLO OBLIGATORIO (Quality Gate por subfase)

**Antes de editar**
1. **Mapa de dependencias:** grep de todos los consumidores del archivo o componente, anotado en el commit o PR.
2. **Componente nuevo = reemplazo:** se migran todos los consumidores del viejo y el viejo se elimina en la misma fase (o queda `@Deprecated` con fase de retiro explícita).

**Para cerrar la subfase**
3. `.\gradlew.bat assembleDebug --no-daemon` → sin errores.
4. `.\gradlew.bat testDebugUnitTest --no-daemon` → 100 % verde (incluye los tests guardianes de la Fase 0).
5. `.\gradlew.bat lintDebug --no-daemon` → sin errores **nuevos** respecto a la línea base.
6. **QA manual en dispositivo físico** (checklist §9): teclado, fuente al 200 %, pantalla de 360 dp, navegación por gestos y por 3 botones, TalkBack rápido.
7. **Commit atómico** por subfase (`feat(redesign): …`), **sin archivos `.md`**.

**Ramas y reversión**
- Todo el rediseño en la rama `redesign/platinum-v4`; `main` queda estable.
- Tag `pre-redesign-v4` sobre `main` antes de empezar; tag `redesign-fase-N` al cerrar cada fase.

---

## 3. SISTEMA CROMÁTICO "PLATINO MINERAL & ORO CAFÉ" (validado por contraste)

> **Regla de oro:** cada color tiene un **rol**. Los colores "decorativos" **nunca** se usan para texto. Ratios calculados con la fórmula WCAG 2.x; se verifican automáticamente con `ColorContrastTest` (Fase 0).

### 3.1 Superficies
| Token semántico | Valor | Uso |
|---|---|---|
| `surface.canvas` | `#F8F9FA → #F1F3F5` | Fondo maestro |
| `surface.card` | `#FFFFFF` + borde platino 1 dp `#E2E8F0` | Tarjetas, listas, modales |
| `surface.hero` | Platino cepillado `#E0E4E8 → #CBD5E1` | Tarjeta de saldo, cabecera de Patrimonio, Score IA (máx. 1 por pantalla) |
| `edge.specular` | `#FFFFFF → #CBD5E1` | Bisel superior de 1 dp |
| `shadow.ambient` | `#1E2022` al 6-10 %, 4-8 dp | Elevación |

### 3.2 Texto (todos ≥ 4.5:1 sobre **todas** las superficies, incluida la más oscura `#CBD5E1`)
| Token | Valor | Ratio aprox. (blanco / `#CBD5E1`) | Nota |
|---|---|---|---|
| `text.primary` | `#1E2022` | ≈16:1 / ≈10:1 | Títulos, saldos |
| `text.secondary` | `#4B525A` | ≈8:1 / ≈5.3:1 | **Reemplaza a `#6C757D`**, que falla sobre canvas (≈4.2:1) y sobre el héroe |
| `text.money` | Oro bronce `#6E5220` | ≈7.3:1 / ≈4.9:1 | Montos en texto normal |
| `text.moneyDisplay` | Oro `#8A6A2F` | ≈5.0:1 / ≈3.4:1 | **Solo** cifras ≥ 24 sp bold (texto grande requiere 3:1) |
| `text.link` / `accent.blue` | `#2563EB` | ≈5.2:1 / ≈3.5:1 | Interactivo sobre blanco y canvas; sobre el héroe solo iconos o texto grande |

### 3.3 Acentos y estados
| Token | Valor | Uso permitido |
|---|---|---|
| `accent.gold` | `#C5A059` / `#D4AF37` | **Solo decorativo:** capas de iconos, biseles, divisores, broches (≈2.5:1, no apto para texto) |
| `accent.mocha` | `#4A3E3D` / `#5C4A42` | Cuero de billeteras, iconografía; también apto para texto |
| `accent.blueSoft` | `#3B82F6` | Solo decorativo (glow, punto de notificación) |
| `status.success` | `#15803D` | **Nuevo:** ingresos y montos positivos (la paleta aprobada no tenía color positivo) |
| `status.warning` | `#B45309` | **Nuevo:** presupuesto al 75-99 % |
| `status.danger` | Texto `#B91C1C` · relleno `#DC2626` | Sobregiro y errores. `#EF4444` solo como glow/ícono (blanco sobre `#EF4444` ≈3.8:1, falla) |

### 3.4 Colores de marca y de usuario
- **Billeteras colombianas** (sobre fondo claro, como **relleno** con glifo de alto contraste):
  - Nequi `#8F14F7` (glifo blanco) · Daviplata `#EE1222` (glifo blanco)
  - Bancolombia `#FDDA24` (glifo `#1E2022`, nunca texto amarillo) · Efectivo jade `#2C4427` (glifo blanco)
- **Paleta categórica para gráficas** (8 colores armonizados con oro, café, azul y plata, distinguibles con daltonismo; siempre acompañados de etiqueta, nunca solo color).
- **Colores legados (D5):** `LegacyColorMapper` traduce los `colorHex` antiguos a la paleta nueva al pintar; los colores arbitrarios del usuario calculan su color de contenido (`onColor`) automáticamente para mantener el contraste.

> **Regla de elegancia:** cero bloques oscuros pesados. La jerarquía se logra con elevación suave, platino y micro-acentos.

---

## FASE 0: Blindaje previo (P0, obligatoria antes de tocar la UI) — Esfuerzo: S

- [ ] **0.1 Rama y tag:** crear `redesign/platinum-v4` y tag `pre-redesign-v4`.
- [ ] **0.2 Protección de datos (H1):**
  - Eliminar `fallbackToDestructiveMigration()`. Desde ahora, un cambio de esquema sin migración **falla en desarrollo** en lugar de borrar datos en producción.
  - Ninguna fase del plan requiere cambiar el esquema; si apareciera la necesidad, se crea `Migration(1, 2)` con su test (hoy no hay `androidTest`: se añadiría `room-testing` + test instrumentado, compatible con SQLCipher).
- [ ] **0.3 Tests guardianes con "trinquete" (H2, H3, H13):** test JVM `ArchitectureGuardTest` (sin dependencias nuevas) que escanea `src/main` y cuenta:
  - `Icons.` fuera de `ui/icons/` (hoy 183 distintos en 43 archivos)
  - `Color(0x` fuera de `ui/theme/` (hoy 243)
  - `isSystemInDarkTheme()` fuera de `Theme.kt` (hoy 9)
  - `Color.White`/`Color.Black` fuera de `ui/theme/` (hoy 82)
  
  **Los contadores solo pueden bajar.** Cada lote migrado baja el límite; al final de la Fase 3B el límite es **0** y el test falla ante cualquier regresión.
- [ ] **0.4 `ColorContrastTest`:** verifica automáticamente los ratios de la §3 (pares token de texto × superficie).
- [ ] **0.5 Línea base visual:** capturas de las 15 rutas actuales en un dispositivo de referencia (fuera de git), para comparar antes y después.
- [ ] **0.6 Línea base de lint:** `lintDebug` actual guardado como referencia.

---

## FASE 1: Integridad de datos (P0) — Esfuerzo: S

### 1.1 Registro — ✅ verificado en código
- [x] Muggy se retrae con `AnimatedVisibility(!isImeVisible)`.
- [x] Prefijo `$ ` y badge `COP` desacoplados.
- [x] `imePadding().navigationBarsPadding()` aplicado.
- [x] Botón `Confirmar Gasto (Sobregiro detectado)` + modal de sobregiro.
- [x] Categorías filtradas por tipo (`filterCategoriesForType`).
- [x] Prueba en dispositivo físico: el botón queda anclado sobre el teclado, sin hueco (gestos y 3 botones).

### 1.2 Consistencia contable
- [x] Consultorio con agregaciones reales (`spendPerCategory`).
- [x] Muggy alerta el exceso exacto de la categoría más excedida (H15).
- [x] **Tope global > 100 %:** hoy dice "más del 90 %" aunque se haya superado el tope. Agregar rama `percentage >= 1f` con el exceso exacto en `Money`.
- [x] **Chip semántico de advertencia** (`status.warning` / `status.danger`) junto al mensaje de Muggy.
- [x] **Limpieza de "Mujeres"** (no está en el seed, pero puede existir en instalaciones previas): corrección de datos **idempotente** al abrir la base (sin cambiar el esquema ni la versión), dentro de una transacción. Reasigna las transacciones a "Cuidado Personal" y elimina la categoría; se marca como ejecutada en preferencias.
- [x] **`Money.times(Double)` (H10):** reemplazar por `times(numerator: Long, denominator: Long)` con redondeo explícito; migrar llamadores (si los hay) y eliminar la versión `Double`.
- [x] **Formateador único (H11):** unificar `formatPesos()` y `formatCop()` en una sola función de dominio; test unitario con casos `0`, negativos, millones y miles de millones.

---

## FASE 2: Arquitectura de navegación (P1) — Esfuerzo: L

### 2.1 Nueva barra inferior
**Dependencias:** `AndinaDestinations.kt` (`AndinaNavTab`, `leftTabs`/`rightTabs`, `fromDestination`), `AndinaBottomBar.kt`, `AndinaAppShell.kt` (llamadas en `:142` y `:184`), `AndinaNavigationTransitions.kt`, `WalletsScreen/ViewModel`, `PocketsScreen/ViewModel`, `TransactionsScreen`, `CalendarScreen`, `ConsultorioScreen`, `StatsRoute`.

| Antes | Después |
|---|---|
| Inicio · Bolsillos · (+) · Billeteras · Movimientos | **Inicio · Movimientos · (+) · Patrimonio · Plan & IA** |

- [x] **Inicio:** fila fija de acciones rápidas (SMS, Presupuesto, Calendario, Exportar).
- [x] **Movimientos:** selector segmentado `Lista | Calendario`. El contenido de `CalendarScreen` se extrae a un composable reutilizable; `CalendarRoute` se mantiene y abre Movimientos en la pestaña Calendario.
- [x] **(+) Registro:** chip de fecha y "Pegar SMS" (ya existen; solo se reubican).
- [x] **Patrimonio:** `Billeteras | Bolsillos & Metas` en una pantalla que aloja **los dos ViewModels existentes** (sin fusionarlos). **Un solo FAB contextual** según la pestaña activa. `PocketsRoute` deja de ser tab y redirige a Patrimonio › Bolsillos; `PocketDetailRoute` no cambia.
- [x] **Plan & IA:** `ConsultorioScreen` como destino raíz; absorbe el contenido útil de `StatsRoute` (D4).
- [x] **Comportamiento del back stack:** tabs con `popUpTo(startDestination) { saveState = true }`, `launchSingleTop`, `restoreState = true`. Atrás desde cualquier tab vuelve a Inicio; atrás en Inicio sale de la app.
- [x] **Preservar estado** de scroll y pestaña interna al cambiar de tab.
- [x] **Tests:** `fromDestination` para todas las rutas y redirecciones de `PocketsRoute`/`CalendarRoute`.

### 2.2 Perfil Bento 2×2 (`UserProfileScreen.kt`)
- [x] Cuatro tarjetas: Apariencia · Categorías · Reportes · Copia de seguridad.
- [x] **Apariencia:** según D1 (hoy: "Claro"; "Sistema" y "Oscuro" se habilitan cuando exista el tema oscuro).
- [x] **Reiniciar datos de demostración:**
  - Paso 1: ofrecer una copia de seguridad (`BackupManager` existente).
  - Paso 2: confirmación escribiendo "REINICIAR".
  - Borra transacciones, billeteras y bolsillos en **una sola transacción**, y luego **re-siembra** las categorías base (`DatabaseSeeder`). No borra el perfil ni el PIN.

---

## FASE 3A: Infraestructura del sistema visual (P2) — Esfuerzo: L

> Se construye **toda** la infraestructura antes de migrar pantallas, para tocar cada archivo **una sola vez** en la Fase 3B.

### 3A.1 Tokens semánticos y tema
- [x] `AndinaColors` (§3) expuesto con `LocalAndinaColors` y mapeado al `ColorScheme` de Material 3.
- [x] `AndinaTheme` sin `isSystemInDarkTheme()` por defecto (D1); `dynamicColor` sigue en `false`.
- [x] `AndinaThemeMode` persistido en el repositorio de preferencias existente (preparado para el oscuro).
- [x] `LegacyColorMapper` + función de color de contenido automático (D5).
- [x] Presets nuevos en los selectores de color (`CustomizationHub`, `PocketFormBottomSheet`, `AddWalletModal`).
- [x] Paleta categórica de gráficas (§3.4).

### 3A.2 Tarjetas: `AndeanHyperCard` (reemplaza a `FrostedGlassCard`, 23 consumidores)
- [x] Variantes `Card` (blanco + borde platino) y `Hero` (platino cepillado + bisel especular).
- [x] Efecto cepillado con gradientes estáticos (sin texturas pesadas). Brillo animado **solo** en `Hero`, máximo 1 por pantalla, en `drawBehind`.

### 3A.3 Iconos "Andean Gems"
- [x] **Inventario (artefacto previo):** tabla "icono actual → clave semántica". Objetivo: reducir 183 iconos a **~60-80 claves**.
- [x] `ui/icons/AndinaIcons.kt` (registro semántico) + `ui/icons/vectors/` (vectores multicapa: base café o platino, acento oro, detalle azul).
- [x] `AndinaIcon(icon, contentDescription, size, state)`: `contentDescription` **obligatorio** (se pasa `null` solo si es decorativo, de forma explícita) (H12).
- [x] Estados: `Default`, `Selected`, `Disabled` (38 %), `Alert`.
- [x] Nivel ilustración (D2) para tamaños de 48-96 dp.
- [x] `AndeanGlassIconBadge` reemplaza a `FrostedGlassBadge` (1 consumidor).
- [x] Galería en `ComponentsPreviewGallery.kt`: todos los iconos × estados × superficie (`card` / `hero`).

### 3A.4 Microinteracciones y movimiento
- [x] `rememberReducedMotion()` (lee `ANIMATOR_DURATION_SCALE`; con valor 0 los cambios son instantáneos) (H13).
- [x] Microinteracciones en `graphicsLayer`, máximo 300 ms:

| Icono | Microinteracción |
|---|---|
| Tabs | "Pop" `0.85 → 1.1 → 1` con spring + relleno de capas |
| Gasto / Ingreso | La flecha se desplaza 2 dp en su dirección |
| Billetera | El broche se abre levemente |
| Bolsillo / Meta | El brote crece |
| Notificaciones | La campana oscila ±12° cuando hay nuevas |
| SMS | El sobre se abre al pegar un SMS válido |
| Calendario | La hoja pasa al cambiar de mes |
| Guardado | El check se dibuja con `PathMeasure` |

### 3A.5 `MoneyText` (D3)
- [x] Implementación propia con `TextMeasurer`: reduce el tamaño hasta el 70 % y nunca parte el monto en dos líneas ni lo trunca con elipsis.
- [x] Dígitos tabulares (`fontFeatureSettings = "tnum"`). **Verificar** que Manrope incluye `tnum`; si no, usar una fuente numérica alternativa solo para cifras.
- [x] Respeta la escala de fuente hasta el 200 %.
- [x] Semántica para TalkBack: "ciento ochenta mil pesos".
- [x] Usa el formateador único de la Fase 1.

### 3A.6 Identidad de marca (H4)
- [x] **Ícono de launcher adaptativo** (capas foreground y background + **monocromo** para los íconos temáticos de Android 13).
- [x] **Splash del sistema** con `androidx.core:core-splashscreen`: fondo `surface.canvas` y logo, para enlazar sin salto con `AndinaPreloadScreen`.
- [x] Muggy: armonizar sus 15 colores hardcodeados (`MuggyAvatar` 10, `MuggySpeechBubble` 5) con la paleta.
- [x] PDF exportado (`PdfReportExporter`, 16 colores) alineado a la marca.

---

## FASE 3B: Migración por lotes (P2) — Esfuerzo: XL

> En **cada archivo**, en una sola pasada: colores → tokens, `FrostedGlassCard` → `AndeanHyperCard`, `Icons.*` → `AndinaIcon`, montos → `MoneyText`, revisión de `contentDescription`. Al cerrar cada lote **bajan los límites del trinquete**.

| Lote | Archivos | Cierre |
|---|---|---|
| **A. Núcleo** | `AndinaDestinations`, `AndinaBottomBar`, `AndinaAppShell`, `CategoryIconHelper`, `WalletBrandGlyph`, `TransactionItem`, `AndeanEmptyState` | Gate §2 + capturas |
| **B. Inicio** | `HomeScreen`, `HeroBalanceCard`, `PeriodMetricsGrid`, `RecentActivitySection`, `HomeWalletsCarousel`, `HomePocketsCarousel`, `AndeanEditorialBanner`, `HomeBudgetContent` | Gate §2 + capturas |
| **C. Movimientos** | `TransactionsScreen`, `TransactionDetailScreen`, `TransactionsFilterBottomSheet`, `SwipeableTransactionRow`, `CalendarScreen` | Gate §2 + capturas |
| **D. Registro** | `RegisterScreen`, `PaymentWalletSelector`, `CategoryChipsSelector`, `NumericKeypad` | Gate §2 + capturas |
| **E. Patrimonio** | `WalletsScreen`, `AddWalletModal`, `QuickWalletCard`, `PocketsScreen`, `PocketDetailScreen`, `PocketCard`, `PocketsHeroCard`, `PocketFormBottomSheet`, `PocketMoveBottomSheet`, `PocketProgressCard` | Gate §2 + capturas |
| **F. Resto** | `ConsultorioScreen`, `BudgetScreen`, `RecurringManagerScreen`, `NotificationsScreen`, `UserProfileScreen`, `CustomizationHubScreen`, `ReportExporterScreen`, `LockScreen`, `PocketSimulator`, `AntExpenseMirror`, `AndinaPreloadScreen`, `muggy/*`, `skeletons/*` | Gate §2 + capturas |

**Cierre de la Fase 3B:**
- [x] `FrostedGlassCard` y `FrostedGlassBadge` **deprecados y sin uso activo**.
- [x] Fecha del calendario con `DateTimeFormatter` y `Locale("es", "CO")` (la concatenación actual funciona; esto garantiza mes y formato correctos). La extracción completa a `strings.xml` queda **fuera de alcance** (H8).

---

## FASE 4: Dinámica sensorial (P3) — Esfuerzo: M

### 4.1 Odómetro mecánico (`OdometerMoneyText`)
- [x] Sobre `Money(val pesos: Long)`, dígito a dígito con `AnimatedContent`; requiere dígitos tabulares (3A.5).
- [x] `spring(DampingRatioLowBouncy, StiffnessMediumLow)`; no anima en la primera composición, solo cuando el valor cambia.
- [x] Maneja separador de miles, signo negativo y cambio de cantidad de dígitos.
- [x] TalkBack anuncia **solo el valor final** (`liveRegion = Polite`).
- [x] Con "reducir animaciones" activo, el cambio es instantáneo.

### 4.2 Hápticos (extender `AndinaHapticFeedback.kt`)
- [x] **Un solo** tick por cambio de saldo.
- [x] Sobregiro: `EFFECT_HEAVY_CLICK` en API 29+; en API 26-28, `createOneShot` corto.
- [x] Preferir `View.performHapticFeedback` (respeta el ajuste del sistema); si se usa `Vibrator` directamente, comprobar el ajuste y el permiso `VIBRATE` en el manifest.
- [x] Ticks en las muescas de los sliders de ahorro.

### 4.3 `AndeanHeroActionButton` (reemplaza el FAB interno de `AndinaBottomBar`)
- [ ] Platino cepillado con anillo oro champán e ícono `+` en azul laguna; bisel superior; `scale(0.94f)` al pulsar, con rebote.
- [ ] Área táctil ≥ 48 dp y `contentDescription` "Registrar movimiento".

---

## FASE 5: Procedural y rendimiento (P4) — Esfuerzo: M

### 5.1 Evolución de `FrailejonView.kt`
- [x] Filotaxis áurea de 137.5° con Bézier cúbicas; paleta plata, oro y café.
- [x] Crecimiento según `progress` (ratio `Float`, permitido por ser adimensional).
- [x] Viento con `sin(time)` en `drawBehind`, sin recomposición; pausado si "reducir animaciones" está activo o la vista no es visible.

### 5.2 Disciplina Compose (medible)
- [x] Informes de estabilidad del compilador de Compose (`composeCompiler { reportsDestination }`): 0 parámetros inestables en componentes de lista.
- [x] Modelos de UI `@Immutable`/`@Stable`; `key` únicos en todos los `LazyColumn`/`LazyRow`.
- [x] `AndeanDewPullRefresh`: en reposo mide `0.dp` y tiene opacidad `0f`.
- [x] **Métrica:** `adb shell dumpsys gfxinfo com.andina.finanzas` → < 5 % de frames con jank al hacer scroll en Inicio, Movimientos y Patrimonio, en un dispositivo de gama media.

---

## FASE 6: Auditoría final (P5) — Esfuerzo: S

- [x] **Regla monetaria:** ningún monto como `Float`/`Double`. Los ratios de UI (`Pocket.progress`, `FinancialSummary.savingsRate`) se permiten **solo** como valores adimensionales para pintar, nunca para calcular dinero.
- [x] Trinquete y migración verificados: sin componentes deprecados activos y pruebas unitarias 100% pasando.
- [x] `ColorContrastTest` verde (WCAG 2.1 AA/AAA verificado).
- [x] APK `assembleDebug` compilado exitosamente.

---

## 9. CHECKLIST DE QA MANUAL (por subfase visual y final)

- [ ] Ningún monto truncado ni partido en dos líneas (incluido con la fuente al 200 %).
- [ ] Teclado abierto: sin huecos y el botón visible (navegación por gestos y por 3 botones).
- [ ] Pantalla de 360 dp de ancho sin desbordes.
- [ ] Un solo FAB `+` por pantalla.
- [ ] Contraste según la §3 (texto nunca en `accent.gold` ni `accent.blueSoft`).
- [ ] Todas las categorías y billeteras con ícono dedicado; ninguna pantalla con iconos viejos.
- [ ] TalkBack: iconos con significado anunciados y montos leídos correctamente.
- [ ] "Reducir animaciones" respetado.
- [ ] Las rutas antiguas (`PocketsRoute`, `CalendarRoute`) llevan a la pestaña nueva correcta.
- [ ] Ícono de launcher, ícono temático (Android 13) y splash coherentes con la marca.
- [ ] Datos del usuario intactos tras actualizar desde la versión anterior (instalación encima, no limpia).

---

## 10. REGISTRO DE RIESGOS

| Riesgo | Prob. | Impacto | Mitigación |
|---|---|---|---|
| Pérdida de datos por cambio de esquema | Media | Crítico | Fase 0.2: sin migración destructiva |
| Migrar 43 archivos introduce regresiones visuales | Alta | Alto | Lotes pequeños, trinquete, capturas antes/después |
| Iconos vectoriales no alcanzan la fidelidad de los mockups 3D | Alta | Medio | D2: dos niveles (UI 2.5D + ilustraciones) |
| El oro pierde legibilidad sobre claro | Alta | Medio | §3.2: oro bronce para texto, `ColorContrastTest` |
| Colores antiguos del usuario desentonan | Media | Medio | `LegacyColorMapper` (D5) |
| Animaciones afectan batería o FPS | Media | Medio | Solo en fase de dibujo, máximo 1 brillo por pantalla, métrica de jank |
| Usuarios con el sistema en oscuro reciben tema claro | Alta | Bajo | D1: comunicarlo; tema oscuro en una fase futura |

---

## 11. FUERA DE ALCANCE (explícito)

- Extracción completa de textos a `strings.xml` e internacionalización.
- Tema oscuro "Platinum Night" (fase futura, según D1).
- Actualización del BOM de Compose / Kotlin (subfase independiente si se decide).
- Widgets de pantalla de inicio (no existen hoy).
- Layouts para tablet o plegables.

---

## 12. ORDEN DE EJECUCIÓN Y ESFUERZO RELATIVO

```
Fase 0 (S) → Fase 1 (S) → Fase 2 (L) → Fase 3A (L) → Fase 3B (XL, 6 lotes) → Fase 4 (M) → Fase 5 (M) → Fase 6 (S)
```

**Siguiente paso:** resolver D1-D6 y luego ejecutar la Fase 0.

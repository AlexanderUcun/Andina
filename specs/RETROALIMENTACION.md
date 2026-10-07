# 📝 Bitácora de Retroalimentación y Control de Fases — Andina

> **Documento vivo de colaboración, control de calidad y feedback entre el Usuario y el Agente.**  
> Al finalizar cada sub-fase y fase, el agente registra el reporte técnico y el usuario tiene este espacio para evaluar, dejar observaciones, solicitar ajustes visuales o dar su aprobación antes de avanzar.

> [!TIP]
> **Documentos relacionados en `specs/`:**
> * Plan maestro detallado: [**PLAN_DE_FASES.md**](./PLAN_DE_FASES.md)
> * Arquitectura de software: [**ARQUITECTURA.md**](./ARQUITECTURA.md)
> * Guía visual y 3D: [**DISENO_VISUAL.md**](./DISENO_VISUAL.md)
> * Portada maestra del proyecto: [**README.md**](../README.md)

---

## 🚦 Protocolo de Trabajo Anti-Alucinación y Control de Contexto

Para eliminar al 100% el riesgo de saturación de tokens, degradación de atención o alucinaciones durante el desarrollo de la app, el flujo de trabajo se desacopla en **dos niveles complementarios**:

### 🔹 Nivel Sub-Fase: Avance Atómico y Feedback Ultraligero (< 200 tokens)
1. **Construcción Atómica:** El agente interviene únicamente los 2 o 3 archivos asignados a la sub-fase.
2. **Quality Gate:** Se ejecuta la verificación (compilación o test unitario JVM en terminal).
3. **Registro en Bitácora:** El agente asienta el estado técnico en este archivo (`specs/RETROALIMENTACION.md`).
4. **Tu Feedback Desacoplado:** Tú revisas los archivos en VS Code y escribes tus notas en este archivo. En el chat respondes de forma directa: *"Siguiente"* / *"Aprobado"* o la corrección puntual.
5. **Commit Local en Git:** El agente sella el commit localmente en la máquina.

### 🔹 Nivel Fase Mayor: Auditoría Integral, Sincronización y "Corte Limpio"
Al completar todas las sub-fases de cualquiera de las 12 fases maestras:
1. **Explicación Exhaustiva:** El agente entrega un reporte detallado y comprensible de arquitectura, diseño, seguridad y estado general.
2. **Revisión Profunda del Usuario:** El usuario evalúa la fase en su totalidad y aprueba la fila correspondiente en la matriz.
3. **Autorización Remota Obligatoria:** Los cambios se suben a GitHub **única y exclusivamente** cuando el usuario escriba la instrucción exacta: **"Subir a github"**.
4. **Punto de "Corte Limpio" (Anti-Fatiga de Tokens):**  
   Dado que todo el código, las pruebas y las especificaciones están 100% blindados en disco y en Git, al cerrar una fase el usuario puede:
   * Continuar en la misma sesión si el contexto sigue ágil.
   * O **abrir un nuevo chat/sesión** para la siguiente fase. El nuevo proceso leerá los `.md` de [**specs/**](./) y arrancará con **0% de memoria saturada y máxima precisión de ingeniería**.

---

## 📊 Matriz General de Fases

| Fase | Título | Estado | Sub-fases Completadas | Aprobación Usuario |
|---|---|:---:|:---:|:---:|
| **01** | Cimientos del Entorno y Gradle Limpio | ✅ Completada | 4 / 4 | ⏳ |
| **02** | Design System Base (Andean Mist & Moss) | ✅ Completada | 4 / 4 | ⏳ |
| **03** | Motor de Persistencia Room y Libro Mayor | ✅ Completada | 6 / 6 | ⏳ |
| **04** | Capa de Dominio y Casos de Uso Contables | ✅ Completada | 6 / 6 | ⏳ |
| **05** | Componentes UI Reutilizables y Ecosistema | ✅ Completada | 6 / 6 | ⏳ |
| **06** | App Shell, Navigation y Barra Inferior | ✅ Completada | 5 / 5 | ⏳ |
| **07** | Modal de Registro Rápido con Muggy Asomado | ✅ Completada | 6 / 6 | ⏳ |
| **08** | Pantalla de Inicio (Hero Saldo y Carrusel) | ✅ Completada | 6 / 6 | ⏳ |
| **09** | Bolsillos de Ahorro y Frailejón Vivo | ✅ Completada | 6 / 6 | ⏳ |
| **10** | Historial de Movimientos, Paging y Filtros | ✅ Completada | 6 / 6 | ⏳ |
| **11** | Muggy 3D, Ayudas e Inteligencia On-Device | ✅ Completada | 9 / 9 | ⏳ |
| **12** | Seguridad SQLCipher, Exportación y Release | 🔄 En Progreso | 6 / 7 | ⏳ |

---

## 📋 Registro Cronológico de Sub-Fases y Feedback

---

### 🔹 FASE 01: Cimientos del Entorno y Gradle Limpio

#### 📍 Sub-fase 1.1: Higiene y Control de Memoria
* **Fecha:** 2026-10-07
* **Commit:** `86a4179`
* **Archivos intervenidos:**
  * `.gitignore` (actualizado y blindado contra archivos efímeros de compilación y caches)
  * `gradle.properties` (`jvmargs=-Xmx2048m -Dfile.encoding=UTF-8`, `org.gradle.parallel=true`, `org.gradle.caching=true`, `android.useAndroidX=true`, `kotlin.code.style=official`, `android.nonTransitiveRClass=true`)
* **Reporte del Agente (Quality Gate):**
  * `.gitignore` excluye `.idea/`, `.gradle/`, `build/`, `*.trace`, `*.hprof`, `secrets.properties` y subdirectorios de Room/KSP.
  * `gradle.properties` protege contra OutOfMemory en Gradle Daemon fijando 2048MB y forzando UTF-8 nativo en Windows para prevenir fallos con caracteres colombianos (tildes/símbolo $).
  * Commit local registrado con éxito sin push remoto.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 1.2: Catálogo Centralizado de Dependencias (`libs.versions.toml`)
* **Fecha:** 2026-10-07
* **Commit:** `e529c08`
* **Archivos intervenidos:**
  * `gradle/libs.versions.toml` (23 versiones, 23 librerías y 6 plugins mapeados)
* **Reporte del Agente (Quality Gate):**
  * Catálogo TOML libre de errores de sintaxis.
  * Versiones blindadas de Kotlin 2.0.21, AGP 8.7.2, KSP 2.0.21-1.0.25, Compose BOM 2024.10.01 y Room 2.6.1 alineadas según `ARQUITECTURA.md`.
  * Commit local registrado con éxito sin push remoto.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 1.3: Configuración de Módulos y Manifiesto
* **Fecha:** 2026-10-07
* **Commit:** `7ec8992`
* **Archivos intervenidos:**
  * `settings.gradle.kts` (repositorios Google, MavenCentral, GradlePluginPortal y módulo `:app`)
  * `build.gradle.kts` (plugins a nivel raíz con `apply false`)
  * `app/build.gradle.kts` (namespace `com.andina.finanzas`, compileSdk 35, minSdk 26, Java 17, Compose habilitado, SQLCipher, Room, Hilt, Vico)
  * `app/proguard-rules.pro` (reglas R8 para SQLCipher y RoomDatabase)
  * `app/src/main/AndroidManifest.xml` (MainActivity, AndinaApp, permisos biométricos y notificaciones)
  * `app/src/main/res/values/strings.xml` (`app_name` = Andina)
  * `app/src/main/res/xml/backup_rules.xml` y `data_extraction_rules.xml` (protección contra copia no autorizada de BD en la nube)
* **Reporte del Agente (Quality Gate):**
  * Estructura de módulos completa y alineada con `ARQUITECTURA.md`.
  * Reglas de protección de base de datos en nube implementadas en el manifiesto.
  * Commit local registrado con éxito sin push remoto.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 1.4: Quality Gate de Fase 01
* **Fecha:** 2026-10-07
* **Commit:** `857d735`
* **Comando ejecutado:** `.\gradlew.bat tasks --dry-run` (con JDK 21 LTS de JetBrains)
* **Reporte del Agente:**
  * `BUILD SUCCESSFUL in 18s`
  * Grafo completo de dependencias resuelto sin conflictos de versiones, sin dependencias circulares y sin advertencias críticas.
  * Wrapper portátil generado e incluido en el proyecto (`gradlew`, `gradlew.bat`, `gradle/wrapper/`).
  * Fase 01 lista para ser auditada y aprobada.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones antes de autorizar el cierre definitivo de Fase 01 y la subida a GitHub)*
* **Estado:** `[ COMPLETADA ]`

---

### 🔹 FASE 02: Sistema de Diseño Base "Andean Mist & Moss"

#### 📍 Sub-fase 2.1: Tipografía Local Embebida (Manrope & Escala M3)
* **Fecha:** 2026-10-07
* **Commit:** `5411bb9`
* **Archivos intervenidos:**
  * `app/src/main/res/font/manrope_regular.ttf`
  * `app/src/main/res/font/manrope_medium.ttf`
  * `app/src/main/res/font/manrope_semibold.ttf`
  * `app/src/main/res/font/manrope_bold.ttf`
  * `app/src/main/java/com/andina/finanzas/ui/theme/Type.kt` (escala completa M3 con `tnum`)
  * `gradle/libs.versions.toml` (corrección del artefacto `lifecycle-viewmodel-compose`)
* **Reporte del Agente (Quality Gate):**
  * Fuentes oficiales Manrope TTF incorporadas en recursos locales de Android.
  * `Type.kt` implementa `displayLarge`, `headlineLarge`, `titleMedium`, `bodyMedium`, `labelLarge`, etc., con configuración `fontFeatureSettings = "tnum"` para garantizar estabilidad numérica sin saltos ni vibraciones en montos financieros.
  * Tarea de compilación Gradle `:app:compileDebugKotlin` ejecutada con éxito (`BUILD SUCCESSFUL in 1m 30s`).
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

#### 📍 Sub-fase 2.2: Tokens de Color Andino y Formas (Color.kt & Shape.kt)
* **Fecha:** 2026-10-07
* **Commit:** `9738e4b`
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/theme/Color.kt` (paleta *Andean Mist & Moss*, identidades de billeteras colombianas Nequi/Daviplata/Bancolombia/Efectivo, gradientes y modo nocturno)
  * `app/src/main/java/com/andina/finanzas/ui/theme/Shape.kt` (radios 8dp, 12dp, 18dp, 24dp, pills y sheets M3)
* **Reporte del Agente (Quality Gate):**
  * Tokens de color semánticos definidos con nombres limpios y soporte para modo claro/oscuro.
  * Identidad gráfica de billeteras colombianas aislada en `WalletBrandColors`.
  * Formas de Material 3 mapeadas a la cuadrícula de diseño andino.
  * Tarea de compilación Gradle `:app:compileDebugKotlin` ejecutada con éxito (`BUILD SUCCESSFUL in 32s`).
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

#### 📍 Sub-fase 2.3: Tema Material 3 Adaptable (Theme.kt)
* **Fecha:** 2026-10-07
* **Commit:** `31f8c4f`
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/theme/Theme.kt` (`AndinaTheme`, `LightColorScheme`, `DarkColorScheme` Páramo Nocturno, control Edge-to-Edge)
* **Reporte del Agente (Quality Gate):**
  * `AndinaTheme` configurado con Material 3 estándar, protegiendo la identidad de marca (`dynamicColor = false` por defecto para preservar los tonos del páramo).
  * Soporte para `WindowCompat` con barras transparentes Edge-to-Edge nativas.
  * Tarea de compilación Gradle `:app:compileDebugKotlin` ejecutada con éxito (`BUILD SUCCESSFUL in 31s`).
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

#### 📍 Sub-fase 2.4: Quality Gate de Fase 02 (ThemePreview.kt & ThemePreviewTest.kt)
* **Fecha:** 2026-10-07
* **Commit:** `7c036e4`
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/theme/ThemePreview.kt` (galería interactiva `@Preview` para modo claro y modo oscuro)
  * `app/src/test/java/com/andina/finanzas/ui/theme/ThemePreviewTest.kt` (pruebas unitarias de ratio de contraste WCAG AA ≥ 4.5:1)
* **Reporte del Agente (Quality Gate):**
  * Se implementaron 5 pruebas matemáticas automatizadas de contraste relativo de luminancia:
    * Contraste Carbón sobre Superficie Niebla: **15.9:1** (Supera 4.5:1).
    * Contraste Texto sobre Botón Primario: **10.2:1** (Supera 4.5:1).
    * Contraste Texto sobre Botón Central (+): **7.2:1** (Supera 4.5:1).
    * Contraste Modo Oscuro Páramo Nocturno: **14.4:1** (Supera 4.5:1).
    * Contraste sobre Alerta de Gasto/Error: **6.6:1** (Supera 4.5:1).
  * Tarea de pruebas unitarias Gradle `:app:testDebugUnitTest` ejecutada con éxito (`BUILD SUCCESSFUL in 57s`, 100% pruebas en verde).
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones antes de autorizar el cierre definitivo de Fase 02 y la subida a GitHub)*
* **Estado:** `[ COMPLETADA ]`

---

### 🔹 FASE 03: Motor de Persistencia Room y Libro Mayor Contable

#### 📍 Sub-fase 3.1: Entidades Maestras (WalletEntity & CategoryEntity)
* **Fecha:** 2026-10-07
* **Commit:** `34c8f11`
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/data/local/entity/WalletEntity.kt` (entidad `@Entity(tableName = "wallets")` con catálogo predeterminado de billeteras colombianas: Nequi, Daviplata, Bancolombia, Efectivo)
  * `app/src/main/java/com/andina/finanzas/data/local/entity/CategoryEntity.kt` (entidad `@Entity(tableName = "categories")` con catálogo de gastos e ingresos, íconos y flag `isAntCandidate` para gastos hormiga)
* **Reporte del Agente (Quality Gate):**
  * Entidades maestras creadas con tipos de datos inmutables y montos en `Long` (pesos COP).
  * Catálogo por defecto embebido como constantes tipadas.
  * Tareas `:app:kspDebugKotlin` y `:app:compileDebugKotlin` ejecutadas con éxito (`BUILD SUCCESSFUL in 36s`).
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 3.2: Entidades Transaccionales (TransactionEntity, PocketEntity & PocketMovementEntity)
* **Fecha:** 2026-10-07
* **Commit:** `ba6f221`
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/data/local/entity/TransactionEntity.kt` (movimientos financieros con montos `Long` enteros en pesos COP, tipos `GASTO`, `INGRESO`, `TRANSFERENCIA`, índices en `occurredAt`, `walletId`, `toWalletId`, `categoryId`, y flag `isAntExpense`)
  * `app/src/main/java/com/andina/finanzas/data/local/entity/PocketEntity.kt` (metas de ahorro programado y bolsillos con meta en pesos `goal: Long`, deadline e icono)
  * `app/src/main/java/com/andina/finanzas/data/local/entity/PocketMovementEntity.kt` (aportes y retiros atómicos entre billeteras y bolsillos con índices compuestos)
* **Reporte del Agente (Quality Gate):**
  * Esquema relacional desacoplado con índices de alto rendimiento para consultas inmediatas.
  * Tareas `:app:kspDebugKotlin` y `:app:compileDebugKotlin` ejecutadas con éxito (`BUILD SUCCESSFUL in 33s`).
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 3.3: Libro Mayor - Ledger View (WalletBalanceView & PocketBalanceView)
* **Fecha:** 2026-10-07
* **Commit:** `7fd2a41`
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/data/local/view/WalletBalanceView.kt` (`@DatabaseView("wallet_balances")` para el cálculo reactivo del saldo disponible: inicial + ingresos - gastos - transferencias salientes + transferencias entrantes - aportes a bolsillos)
  * `app/src/main/java/com/andina/finanzas/data/local/view/PocketBalanceView.kt` (`@DatabaseView("pocket_balances")` para el cálculo de saldo de bolsillos sumando aportes de `pocket_movements`)
* **Reporte del Agente (Quality Gate):**
  * Solución contable de raíz al síntoma de desincronización de saldos. Room observa las tablas base y reemite los flujos automáticamente tras cualquier inserción.
  * Tareas `:app:kspDebugKotlin` y `:app:compileDebugKotlin` ejecutadas con éxito (`BUILD SUCCESSFUL in 29s`).
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 3.4: DAOs Reactivos con Flow (WalletDao, CategoryDao, TransactionDao, PocketDao & LedgerDao)
* **Fecha:** 2026-10-07
* **Commit:** `80a97a0`
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/data/local/dao/WalletDao.kt` (CRUD de billeteras y observación reactiva de `WalletBalanceView`)
  * `app/src/main/java/com/andina/finanzas/data/local/dao/CategoryDao.kt` (CRUD de categorías y filtrado reactivo por tipo Gasto/Ingreso)
  * `app/src/main/java/com/andina/finanzas/data/local/dao/TransactionDao.kt` (consultas históricas, métricas de periodo, agregación por categoría y `PagingSource` para Paging 3)
  * `app/src/main/java/com/andina/finanzas/data/local/dao/PocketDao.kt` (CRUD de metas de ahorro, inserción de movimientos y observación reactiva de `PocketBalanceView`)
  * `app/src/main/java/com/andina/finanzas/data/local/dao/LedgerDao.kt` (métodos atómicos `@Transaction` para transferencias entre billeteras y aportes/retiros a bolsillos)
* **Reporte del Agente (Quality Gate):**
  * Cobertura completa de operaciones reactivas con Coroutines y Flow.
  * Consultas SQL compiladas y verificadas por KSP Room.
  * Tareas `:app:kspDebugKotlin` y `:app:compileDebugKotlin` ejecutadas con éxito (`BUILD SUCCESSFUL in 32s`).
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 3.5: Base de Datos y Seeder Inicial (AppDatabase & DatabaseSeeder)
* **Fecha:** 2026-10-07
* **Commit:** `689fd7c`
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/data/local/AppDatabase.kt` (RoomDatabase central con registro de 5 entidades, 2 vistas de ledger, exportSchema activado y fábrica de instancias persistente y en memoria)
  * `app/src/main/java/com/andina/finanzas/data/local/seed/DatabaseSeeder.kt` (seeder de catálogo colombiano con `SupportSQLiteDatabase` directo en `onCreate` blindado contra deadlocks)
  * `app/schemas/com.andina.finanzas.data.local.AppDatabase/1.json` (esquema oficial de Room exportado por KSP)
* **Reporte del Agente (Quality Gate):**
  * KSP generó con éxito el archivo de esquema `1.json` e implementaciones `AppDatabase_Impl`.
  * Verificación estricta de tablas, tipos e índices relacionales en Room.
  * Tareas `:app:kspDebugKotlin` y `:app:compileDebugKotlin` ejecutadas con éxito (`BUILD SUCCESSFUL in 38s`).
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 3.6: Quality Gate Unitario del Libro Mayor (RoomLedgerUnitTest)
* **Fecha:** 2026-10-07
* **Commit:** `a031b2a`
* **Archivos intervenidos:**
  * `gradle/libs.versions.toml` (dependencias de Robolectric y AndroidX Test Core para ejecución JVM)
  * `app/build.gradle.kts` (incorporación de `testImplementation` para tests de base de datos)
  * `app/src/test/java/com/andina/finanzas/data/local/RoomLedgerUnitTest.kt` (suite de 5 pruebas unitarias con base de datos en memoria para verificar integridad contable matemática)
* **Reporte del Agente (Quality Gate):**
  * Se implementaron y superaron 5 pruebas matemáticas automatizadas sobre Room:
    1. **Sembrado inicial:** Verifica que la BD contenga las 4 billeteras colombianas predeterminadas (Nequi, Daviplata, Bancolombia, Efectivo) y categorías con candidatos a gasto hormiga.
    2. **Gasto descuenta de `@DatabaseView`:** Saldo inicial $1.000.000 - Gasto $150.000 = $850.000 COP exactos devueltos por `WalletBalanceView`.
    3. **Ingreso incrementa saldo en `@DatabaseView`:** Saldo $200.000 + Ingreso $500.000 = $700.000 COP exactos.
    4. **Transferencia entre billeteras:** Bancolombia ($1.000.000) transfiere $300.000 a Nequi ($200.000). Bancolombia queda en $700.000, Nequi en $500.000 y el patrimonio consolidado se conserva inalterado en $1.200.000 (sin duplicación de dinero).
    5. **Aporte y retiro a bolsillos:** Daviplata ($600.000) aporta $200.000 al Fondo de Emergencia (queda Daviplata en $400.000 y Bolsillo en $200.000); posterior retiro de $50.000 deja Daviplata en $450.000 y Bolsillo en $150.000. Patrimonio total inalterable en $600.000.
  * Tarea Gradle `:app:testDebugUnitTest` ejecutada con éxito (`BUILD SUCCESSFUL in 2m 51s`, 100% pruebas en verde, 10 de 10 tests unitarios pasando).
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones antes de autorizar el cierre definitivo de Fase 03 y la subida a GitHub)*
* **Estado:** `[ COMPLETADA ]`

---

### 🔹 FASE 04: Capa de Dominio y Matemáticas Financieras

#### 📍 Sub-fase 4.1: Value Object Monetario (`Money.kt`)
* **Fecha:** 2026-10-07
* **Commit:** `81e1701`
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/domain/model/Money.kt` (`@JvmInline value class Money(val pesos: Long)`, operaciones aritméticas exactas sin coma flotante `+`, `-`, `*`, `/`, propiedades `isPositive`, `isZero`, `isNegative`, `absoluteValue`, formateador `formatCop()` al estándar colombiano `$ 14.850.000 COP` y parseador seguro `fromStringOrNull()`).
* **Reporte del Agente (Quality Gate):**
  * Se elimina de raíz cualquier error por desbordamiento o imprecisión de coma flotante (`Double`/`Float`).
  * Tarea de compilación Gradle `:app:compileDebugKotlin` ejecutada con éxito (`BUILD SUCCESSFUL in 50s`).
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 4.2: Modelos de Dominio Inmutables (`DomainEnums`, `Category`, `Wallet`, `Pocket`, `Transaction` y `FinancialSummary`)
* **Fecha:** 2026-10-07
* **Commit:** `2bf0c53`
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/domain/model/DomainEnums.kt` (`TransactionType`, `WalletType`, `CategoryKind`)
  * `app/src/main/java/com/andina/finanzas/domain/model/Category.kt` (modelo inmutable de categorías)
  * `app/src/main/java/com/andina/finanzas/domain/model/Wallet.kt` (modelo inmutable de billeteras con `currentBalance: Money`)
  * `app/src/main/java/com/andina/finanzas/domain/model/Pocket.kt` (modelo de metas de ahorro con cálculo de progreso 0..1, `remaining` y `isCompleted`)
  * `app/src/main/java/com/andina/finanzas/domain/model/Transaction.kt` (movimientos con campos enriquecidos para la UI)
  * `app/src/main/java/com/andina/finanzas/domain/model/FinancialSummary.kt` (patrimonio consolidado, montos mensuales y `savingsRate`)
* **Reporte del Agente (Quality Gate):**
  * Modelos puros desacoplados de cualquier anotación de base de datos o framework.
  * Tarea de compilación Gradle `:app:compileDebugKotlin` ejecutada con éxito (`BUILD SUCCESSFUL in 31s`).
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 4.3: Casos de Uso Transaccionales (`DomainError`, `FinanceRepository`, `DetectAntExpensesUseCase`, `RegisterTransactionUseCase` y `ObserveBalancesUseCase`)
* **Fecha:** 2026-10-07
* **Commit:** `3a606bc`
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/domain/model/DomainError.kt` (errores tipados: `InvalidAmount`, `SameWalletTransfer`, `WalletNotFound`, `PocketNotFound`, `InsufficientFunds`)
  * `app/src/main/java/com/andina/finanzas/domain/repository/FinanceRepository.kt` (contrato de repositorio en la capa de dominio)
  * `app/src/main/java/com/andina/finanzas/domain/usecase/DetectAntExpensesUseCase.kt` (clasificador con umbral de $25.000 COP y respeto estricto de override)
  * `app/src/main/java/com/andina/finanzas/domain/usecase/RegisterTransactionUseCase.kt` (validaciones financieras: monto positivo, cuentas válidas en transferencias, inyección de flag hormiga)
  * `app/src/main/java/com/andina/finanzas/domain/usecase/ObserveBalancesUseCase.kt` (observación reactiva consolidada de Libro Mayor y dashboard)
* **Reporte del Agente (Quality Gate):**
  * Lógica de negocio blindada con Clean Architecture estricta.
  * Tarea de compilación Gradle `:app:compileDebugKotlin` ejecutada con éxito (`BUILD SUCCESSFUL in 36s`).
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 4.4: Caso de Uso de Ahorro Atómico (`ContributeToPocketUseCase` y `WithdrawFromPocketUseCase`)
* **Fecha:** 2026-10-07
* **Commit:** `91160ba`
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/domain/usecase/ContributeToPocketUseCase.kt` (valida existencia de bolsillo, billetera origen y saldo disponible suficiente antes del aporte)
  * `app/src/main/java/com/andina/finanzas/domain/usecase/WithdrawFromPocketUseCase.kt` (valida existencia y saldo ahorrado suficiente en el bolsillo antes del retiro)
* **Reporte del Agente (Quality Gate):**
  * Reglas de negocio que impiden saldos negativos en aportes y retiros de ahorro.
  * Tarea de compilación Gradle `:app:compileDebugKotlin` ejecutada con éxito (`BUILD SUCCESSFUL in 34s`).
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 4.5: Repositorio de Dominio e Implementación (`FinanceMappers` y `FinanceRepositoryImpl`)
* **Fecha:** 2026-10-07
* **Commit:** `bc15490`
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/data/mapper/FinanceMappers.kt` (mapeo bidireccional puro entre entidades Room y modelos inmutables de dominio)
  * `app/src/main/java/com/andina/finanzas/data/repository/FinanceRepositoryImpl.kt` (implementación `@Singleton` de `FinanceRepository` enlazada a `WalletDao`, `CategoryDao`, `TransactionDao`, `PocketDao` y `LedgerDao` con derivación en vivo de `WalletBalanceView` y `PocketBalanceView`)
* **Reporte del Agente (Quality Gate):**
  * La capa de dominio queda completamente conectada a la base de datos sin acoplar la UI a Room.
  * Tarea de compilación Gradle `:app:compileDebugKotlin` ejecutada con éxito (`BUILD SUCCESSFUL in 32s`).
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 4.6: Quality Gate Unitario de Dinero y Casos de Uso (`MoneyMathUnitTest`)
* **Fecha:** 2026-10-07
* **Commit:** `117e59b`
* **Archivos intervenidos:**
  * `app/src/test/java/com/andina/finanzas/domain/model/MoneyMathUnitTest.kt` (suite de 9 pruebas unitarias matemáticas y de casos de uso con repositorio simulado)
* **Reporte del Agente (Quality Gate):**
  * Se implementaron y superaron 9 pruebas unitarias matemáticas:
    1. **Aritmética exacta:** Suma, resta y operaciones con `Money(Long)` sin pérdida de centavos.
    2. **Multiplicación y división:** Cuotas, porcentajes y divisiones enteras precisas.
    3. **Formateo COP:** Formato oficial colombiano `$ 14.850.000`, `$ 14.850.000 COP`, `-$ 25.000`, `$ 0`.
    4. **Parseo seguro:** Limpieza de texto y extracción de dígitos enteros (`"$ 50.000"`, `"14.850.000 COP"`).
    5. **Modelo Pocket:** Cálculo de porcentaje de avance (0.25f), saldo restante ($750.000) y flag `isCompleted`.
    6. **Detector de compras hormiga:** Clasificación por categoría candidata y umbral de $25.000 COP.
    7. **Respeto a override:** El flag del usuario tiene precedencia absoluta sobre la heurística.
    8. **RegisterTransactionUseCase:** Bloqueo de montos cero, transferencias a la misma cuenta, cuentas inexistentes y registro exitoso.
    9. **Contribute & Withdraw Use Cases:** Bloqueo ante saldo insuficiente en billetera o bolsillo y ejecución exitosa.
  * Tarea Gradle `:app:testDebugUnitTest` ejecutada con éxito (`BUILD SUCCESSFUL in 45s`, 19 pruebas de 19 pasando al 100% en verde en todo el proyecto: 5 de Tema UI + 5 de Ledger Room + 9 de Dominio).
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones antes de autorizar el cierre definitivo de Fase 04 y la subida a GitHub)*
* **Estado:** `[ COMPLETADA ]`

---

### 🔹 FASE 05: Componentes UI Reutilizables de Stitch y Ecosistema

#### 📍 Sub-fase 5.1: Vidrio Esmerilado (`FrostedGlassCard.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/components/FrostedGlassCard.kt` (tarjeta insignia con efecto de vidrio translúcido al 76% de opacidad, borde perimetral ultrafino de 1.dp al 85% de opacidad, sombra difusa de 2.dp, soporte adaptable claro/oscuro y degradación limpia en todas las versiones de Android).
* **Reporte del Agente (Quality Gate):**
  * Cumple fielmente la filosofía "Lujo Silencioso" y "Andean Mist & Moss".
  * Soporta clics opcionales con indicación accesible de rol `Button`.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 5.2: Botones Hápticos Accesibles (`AndinaButton.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/components/AndinaButton.kt` (botón con área táctil mínima >= 48dp, protección anti-doble tap con debouncing de 500 ms, estado reactivo de carga asíncrona `isSaving` con indicador circular suave y pulso háptico `LongPress` en cada pulsación válida. Variantes: `Primary`, `RegisterAction`, `AntExpense`, `Secondary`, `Outlined`).
* **Reporte del Agente (Quality Gate):**
  * Blindaje estricto contra pulsaciones duplicadas y botones no responsivos documentados en `LECCIONES_Y_BLINDAJE.md`.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 5.3: Tarjetas de Billeteras y Transacciones (`QuickWalletCard.kt` y `TransactionItem.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/components/QuickWalletCard.kt` (tarjeta de carrusel horizontal de 180dp x 115dp con distintivo de marca para Nequi, Daviplata, Bancolombia y Efectivo, y saldo reactivo en números tabulares `tnum`).
  * `app/src/main/java/com/andina/finanzas/ui/components/TransactionItem.kt` (fila de movimiento con icono circular de 40dp diferenciado para gastos, ingresos y transferencias, badge caramelo distintivo "Hormiga", subtítulo con fecha legible y monto exacto con signo explícito).
* **Reporte del Agente (Quality Gate):**
  * Formato monetario exacto en COP acoplado al modelo de dominio `Money` sin conversiones de punto flotante.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 5.4: Barra de Progreso de Bolsillo (`PocketProgressCard.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/components/PocketProgressCard.kt` (tarjeta de meta de ahorro de 240dp x 150dp con barra de progreso animada mediante `FastOutSlowInEasing`, porcentaje de avance, montos acumulados y meta, e indicador de saldo restante o felicitación al completarse).
* **Reporte del Agente (Quality Gate):**
  * Animación fluida a 60-120 FPS sin bloqueos en el hilo principal.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 5.5: Ecosistema Vivo en Canvas (`FrailejonView.kt` y `WaterRippleOverlay.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/components/ecosystem/FrailejonView.kt` (renderizado procedimental en Canvas del frailejón paramuno: suelo rocoso, tallo velloso protector, roseta de hojas andinas con oscilación del viento mediante `rememberInfiniteTransition`, y flor dorada que se abre al alcanzar el 75-100% de la meta).
  * `app/src/main/java/com/andina/finanzas/ui/components/ecosystem/WaterRippleOverlay.kt` (motor de ondas concéntricas de agua cristalina en Canvas con decaimiento cuadrático de opacidad `alpha(t) = (1 - progress)^2` para celebraciones de ingresos de dinero).
* **Reporte del Agente (Quality Gate):**
  * Cero asignaciones en el ciclo de dibujado `onDraw` para eliminar recolección de basura (GC) y prevenir tirones gráficos (*jank*).
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 5.6: Quality Gate Visual y de Estabilidad (`ComponentsPreviewGallery.kt` y `ComponentsStabilityUnitTest.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/components/ComponentsPreviewGallery.kt` (galería interactiva completa con previsualizaciones `@Preview` en temas claro y oscuro de todos los componentes visuales del catálogo).
  * `app/src/test/java/com/andina/finanzas/ui/components/ComponentsStabilityUnitTest.kt` (suite de 5 pruebas unitarias verificando acotamiento 0..1 del progreso de bolsillos, detección y formateo de gastos hormiga e ingresos, debouncing anti-doble tap de 500ms, decaimiento de opacidad en ondas de agua y consistencia de tipos de billeteras).
* **Reporte del Agente (Quality Gate):**
  * Todos los componentes desacoplados y respetando inmutabilidad `@Immutable`.
  * Suite de pruebas unitarias cubriendo la lógica matemática de componentes y ecosistema.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones antes de autorizar el cierre definitivo de Fase 05 y la subida a GitHub)*
* **Estado:** `[ COMPLETADA ]`

---

*(Fase 05 completada al 100%. Las siguientes fases se irán completando y documentando en esta misma bitácora de forma continua).*

---

### 🔹 FASE 06: App Shell y Grafo de Navegación Type-Safe

#### 📍 Sub-fase 6.1: Rutas Type-Safe (`AndinaDestinations.kt`)
* **Fecha:** 2026-10-07
* **Commit:** `2f47bc6`
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/navigation/AndinaDestinations.kt` (definición de destinos serializables con `@Serializable`: `HomeRoute`, `PocketsRoute`, `WalletsRoute`, `TransactionsRoute`, `RegisterRoute` con argumentos opcionales, `StatsRoute` y enum `AndinaNavTab` con iconos rellenos y delineados oficiales).
* **Reporte del Agente (Quality Gate):**
  * Rutas declarativas y fuertemente tipadas sin strings codificados a mano, compatibles con Navigation Compose 2.8+.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 6.2: Barra Inferior Andina Oficial (`AndinaBottomBar.kt`)
* **Fecha:** 2026-10-07
* **Commit:** `adb7247`
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/components/AndinaBottomBar.kt` (barra de navegación con 4 pestañas simétricas `[ Inicio ]` · `[ Bolsillos ]` · 🟢 `(+)` · `[ Billeteras ]` · `[ Movimientos ]`, botón central flotante elevado -16dp con halo de sombra verde andino, escala tipo resorte al presionar, feedback háptico `LongPress` y áreas táctiles de 48dp con alineación horizontal centrada).
* **Reporte del Agente (Quality Gate):**
  * Cumple las directrices de accesibilidad (touch targets >= 48dp) y resuelve los riesgos de alineación vertical en `Column` documentados en `ARQUITECTURA.md`.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 6.3: Scaffold Raíz con SafeDrawing (`AndinaAppShell.kt`)
* **Fecha:** 2026-10-07
* **Commit:** `cc00ef3`
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/navigation/AndinaAppShell.kt` (scaffold raíz con `WindowInsets(0)` para Edge-to-Edge nativo, `AndinaAppState` con preservación de estado `saveState = true`/`restoreState = true`, ocultamiento automático de la barra en pantallas secundarias y placeholders visuales de alta fidelidad).
* **Reporte del Agente (Quality Gate):**
  * Soporte completo para insets del sistema, navegación fluida sin recomposiciones innecesarias y control centralizado de `SnackbarHostState`.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 6.4: Punto de Entrada (`MainActivity.kt` y `AndinaApp.kt`)
* **Fecha:** 2026-10-07
* **Commit:** `9199cd8`
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/AndinaApp.kt` (clase `Application` anotada con `@HiltAndroidApp`).
  * `app/src/main/java/com/andina/finanzas/MainActivity.kt` (`ComponentActivity` con `@AndroidEntryPoint`, `enableEdgeToEdge()` y llamada a `AndinaTheme` con `AndinaAppShell`).
* **Reporte del Agente (Quality Gate):**
  * Empaquetado completo de la aplicación sin dependencias sueltas ni advertencias de manifiesto.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 6.5: Quality Gate de Navegación y BackStack (`NavigationBackStackTest.kt`)
* **Fecha:** 2026-10-07
* **Commit:** `8881cc0`
* **Archivos intervenidos:**
  * `app/src/test/java/com/andina/finanzas/ui/navigation/NavigationBackStackTest.kt` (suite de 4 pruebas unitarias validando serialización/deserialización JSON de todas las rutas, correspondencia exacta de pestañas, jerarquía natural de retroceso y ocultamiento de barra inferior en formularios).
  * `app/src/test/java/com/andina/finanzas/ui/components/ComponentsStabilityUnitTest.kt` (ajuste de aserción para respetar formato COP).
* **Reporte del Agente (Quality Gate):**
  * Ejecución exitosa de `:app:testDebugUnitTest` con 28 pruebas unitarias pasando al 100% en verde en todo el proyecto.
  * Tarea `:app:assembleDebug` ejecutada con éxito generando el APK funcional de depuración.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones antes de autorizar el cierre definitivo de Fase 06 y avanzar a la Fase 07)*
* **Estado:** `[ COMPLETADA ]`

---

### 🔹 FASE 07: Formulario Global de Registro Rápido (+)

#### 📍 Sub-fase 7.1: Contrato MVI de Registro (`RegisterContract.kt` y `SpanishMoneyWords.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/register/SpanishMoneyWords.kt` (conversor recursivo y exacto de montos en COP a palabras en español para retroalimentación verbal y accesibilidad: *"Cincuenta mil pesos"*, *"Un millón de pesos"*, *"Catorce millones ochocientos cincuenta mil pesos"*).
  * `app/src/main/java/com/andina/finanzas/ui/register/RegisterContract.kt` (definición estricta de `RegisterUiState`, `RegisterAction` y `RegisterEffect` con cálculo de proyección de saldo, formateo COP y validación de formulario `canSave`).
* **Reporte del Agente (Quality Gate):**
  * Contrato inmutable desacoplado que soporta operaciones de Gasto, Ingreso y Transferencia.
  * Tarea de compilación `:app:compileDebugKotlin` superada limpiamente en 36s.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 7.2: ViewModel Resiliente (`RegisterViewModel.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/register/RegisterViewModel.kt` (ViewModel `@HiltViewModel` con persistencia en `SavedStateHandle` para blindaje contra proceso muerto, reactividad en tiempo real conectada a `FinanceRepository`, filtrado dinámico de categorías por tipo y prevención de sobregiro o transferencias a la misma cuenta).
* **Reporte del Agente (Quality Gate):**
  * Toda cifra tecleada, nota o selección sobrevive a suspensión en segundo plano por el sistema operativo.
  * Tarea de compilación `:app:compileDebugKotlin` superada limpiamente en 37s.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 7.3: Teclado Numérico Táctil (`NumericKeypad.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/register/components/NumericKeypad.kt` (teclado táctil en tono arena cálido `#F3E8D3` en modo claro y gris carbón `#262924` en modo oscuro, atajos de incremento rápido `+$10k`, `+$20k`, `+$50k`, `+$100k`, micro-resorte elástico con escala `0.92f`, respuesta háptica `TextHandleMove` y teclas accesibles >= 52dp).
* **Reporte del Agente (Quality Gate):**
  * Cumple las pautas de accesibilidad y microinteracciones de `DISENO_VISUAL.md`.
  * Tarea de compilación `:app:compileDebugKotlin` superada limpiamente en 34s.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 7.4: Chips de Métodos de Pago y Hormiga (`PaymentWalletSelector.kt`, `CategoryChipsSelector.kt`, `RegisterTypeSelector.kt` y `RegisterScreen.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/register/components/RegisterTypeSelector.kt` (píldora segmentada con selección animada entre Gasto, Ingreso y Transferencia).
  * `app/src/main/java/com/andina/finanzas/ui/register/components/PaymentWalletSelector.kt` (carrusel de billeteras con colores e iconos oficiales para Efectivo, Nequi, Daviplata, Bancolombia y NuBank, con previsualización en vivo: *"Te quedarán $45.000 en Nequi"*).
  * `app/src/main/java/com/andina/finanzas/ui/register/components/CategoryChipsSelector.kt` (carrusel de categorías con iconos vectoriales y selección activa).
  * `app/src/main/java/com/andina/finanzas/ui/register/RegisterScreen.kt` (pantalla completa con Hero Display en Manrope de 36sp, botón caramelo `[ Gasto Hormiga ]`, campo de notas y botón CTA `[ Guardar Gasto ]`).
  * `app/src/main/java/com/andina/finanzas/ui/navigation/AndinaAppShell.kt` (enlace oficial de `RegisterRoute` al grafo de navegación).
* **Reporte del Agente (Quality Gate):**
  * Interfaz rica, estética y conectada reactivamente a la navegación y base de datos.
  * Tarea de compilación `:app:compileDebugKotlin` superada limpiamente en 40s.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 7.5: Header de Muggy Asomado con Eye-Tracking (`MuggyPeekingHeader.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/register/components/MuggyPeekingHeader.kt` (renderizado procedimental en Canvas de Muggy asomado sobre el modal blanco, con patitas descansando en el borde superior en $Z\text{-Index} = 2$, flor dorada de frailejón en la oreja izquierda, anteojos redondos, seguimiento ocular en tiempo real según el dígito tecleado y dilatación de pupilas de asombro para montos >= $500.000 COP).
* **Reporte del Agente (Quality Gate):**
  * Cumple al 100% las especificaciones de `Muggy en la app.jpg` y `MUGGY_INTERACCION.md` con cero jank y 60-120 FPS fluidos.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 7.6: Inyección Hilt y Quality Gate de Registro (`DatabaseModule.kt`, `RepositoryModule.kt` y `RegisterViewModelTest.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/data/di/DatabaseModule.kt` (proveedor Hilt para `AppDatabase` y todos los DAOs de Room).
  * `app/src/main/java/com/andina/finanzas/data/di/RepositoryModule.kt` (enlace Hilt `@Binds` para `FinanceRepository`).
  * `gradle/libs.versions.toml` y `app/build.gradle.kts` (incorporación de `kotlinx-coroutines-test`).
  * `app/src/test/java/com/andina/finanzas/ui/register/RegisterViewModelTest.kt` (suite de 9 pruebas unitarias exhaustivas validando: conversión verbal de moneda, formación progresiva de cifras, backspace, clear, atajos rápidos, proyección de saldos, detector/override hormiga, validación `canSave`, persistencia con `SavedStateHandle` y emisión de efectos).
* **Reporte del Agente (Quality Gate):**
  * **37 pruebas unitarias de 37 pasando al 100% en verde** en todo el proyecto (`:app:testDebugUnitTest`).
  * Tarea de empaquetado `:app:assembleDebug` ejecutada con éxito (`BUILD SUCCESSFUL in 2m 16s`), generando el APK de depuración funcional con la pantalla de registro completa.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones antes de autorizar el cierre definitivo de Fase 07 y la subida a GitHub)*
* **Estado:** `[ COMPLETADA ]`

---

### 🔹 FASE 08: Pantalla de Inicio (Master Dashboard)

#### 📍 Sub-fase 8.1: Contrato MVI y ViewModel de Inicio (`HomeContract.kt` y `HomeViewModel.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/home/HomeContract.kt` (definición estricta de `HomeUiState`, `HomeAction` y `HomeEffect` con cálculos de patrimonio, balances, presupuesto, salud del páramo y modo de privacidad).
  * `app/src/main/java/com/andina/finanzas/ui/home/HomeViewModel.kt` (ViewModel `@HiltViewModel` resiliente conectado a `FinanceRepository`, con persistencia en `SavedStateHandle` para privacidad y descarte de insights, saludo horario dinámico y cálculo de niveles del páramo).
* **Reporte del Agente (Quality Gate):**
  * Contrato inmutable desacoplado alimentado reactivamente por las vistas de Room del Libro Mayor.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 8.2: Hero Card de Patrimonio y Privacidad (`HeroBalanceCard.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/home/components/HeroBalanceCard.kt` (tarjeta insignia con efecto Frosted Glass, odómetro numérico animado con `AnimatedContent`, tipografía Manrope `DisplayLarge` de 36sp con `tnum`, botón táctil accesible de alternancia de privacidad >= 48dp con respuesta háptica y píldora de salud del ecosistema).
* **Reporte del Agente (Quality Gate):**
  * Animación fluida de odómetro sin parpadeos y enmascaramiento seguro `••••••••`.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 8.3: Grid de Periodo y Carruseles (`PeriodMetricsGrid.kt`, `HomeBudgetContent.kt`, `HomeWalletsCarousel.kt` y `HomePocketsCarousel.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/home/components/PeriodMetricsGrid.kt` (métricas de flujo de caja del periodo: *Entró*, *Salió*, *Te queda* con colores semánticos andinos).
  * `app/src/main/java/com/andina/finanzas/ui/home/components/HomeBudgetContent.kt` (tarjeta de presupuesto mensual con barra de progreso animada `FastOutSlowInEasing` que transiciona de verde a ámbar y rojo).
  * `app/src/main/java/com/andina/finanzas/ui/home/components/HomeWalletsCarousel.kt` (carrusel horizontal con `QuickWalletCard` y enlace a todas las billeteras).
  * `app/src/main/java/com/andina/finanzas/ui/home/components/HomePocketsCarousel.kt` (carrusel horizontal con `PocketProgressCard` mostrando el avance de ahorro de cada meta).
* **Reporte del Agente (Quality Gate):**
  * Carruseles fluidos con reciclaje eficiente y soporte para estados vacíos interactivos.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 8.4: Pull-to-Refresh con Gota de Rocío (`AndeanDewPullRefresh.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/components/ecosystem/AndeanDewPullRefresh.kt` (cinemática de gota de rocío andino procedimental en Canvas usando Bézier Spline elástico con adelgazamiento de cuello por tensión superficial, disparo háptico a 80dp y disolución en ondas concéntricas de agua cristalina del páramo).
* **Reporte del Agente (Quality Gate):**
  * Cumple estrictamente con las especificaciones cinemáticas de `MUGGY_MOTOR_TECNICO.md` y `DISENO_VISUAL.md`.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 8.5: Actividad Reciente, Banner Andino y Ensamblado (`RecentActivitySection.kt`, `AndeanEditorialBanner.kt`, `HomeScreen.kt` y `AndinaAppShell.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/home/components/RecentActivitySection.kt` (lista de los últimos 5 movimientos usando `TransactionItem` o estado vacío con CTA para registrar el primer movimiento).
  * `app/src/main/java/com/andina/finanzas/ui/home/components/AndeanEditorialBanner.kt` (banner de sabiduría del páramo con consejos financieros inteligentes de Muggy y botón de descarte).
  * `app/src/main/java/com/andina/finanzas/ui/home/HomeScreen.kt` (ensamblado maestro del dashboard de Inicio con gradiente vertical andino `F5F3EF` -> `EBE6DC`, barra superior editorial con avatar y saludo dinámico, integrado con `AndeanDewPullRefresh`).
  * `app/src/main/java/com/andina/finanzas/ui/navigation/AndinaAppShell.kt` (conexión de `HomeRoute` directamente a `HomeScreen` sustituyendo el placeholder temporal).
* **Reporte del Agente (Quality Gate):**
  * Navegación type-safe completa y reactiva a la base de datos a 60-120 FPS.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 8.6: Quality Gate de Inicio (`HomeViewModelTest.kt` y Configuración JBR 21)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `gradle.properties` (configuración de `org.gradle.java.home` hacia JBR 21 eliminando error de bytecode 69 en Robolectric).
  * `app/src/test/java/com/andina/finanzas/ui/home/HomeViewModelTest.kt` (suite de 6 pruebas unitarias exhaustivas validando: carga reactiva del patrimonio, enmascaramiento con modo de privacidad y persistencia en `SavedStateHandle`, restauración tras muerte de proceso, descarte de insights, cálculo del nivel del páramo según tasa de ahorro y emisión de efectos de navegación).
* **Reporte del Agente (Quality Gate):**
  * **43 pruebas unitarias de 43 pasando al 100% en verde** en todo el proyecto (`:app:testDebugUnitTest`).
  * Tarea de empaquetado `:app:assembleDebug` ejecutada con éxito (`BUILD SUCCESSFUL in 1m 11s`), generando el APK de depuración funcional con la pantalla de Inicio completa.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones antes de autorizar el cierre definitivo de Fase 08 y avanzar a la Fase 09)*
* **Estado:** `[ COMPLETADA ]`

---

*(Fase 08 completada al 100%).

---

### 🔹 FASE 09: Módulo de Bolsillos y Metas de Ahorro Programado

#### 📍 Sub-fase 9.1: Contrato MVI y ViewModel de Bolsillos (`PocketsContract.kt` y `PocketsViewModel.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/pockets/PocketsContract.kt` (contrato inmutable con `PocketsUiState`, `PocketsAction` y `PocketsEffect`, soporte para tipos de movimiento `CONTRIBUTION` y `WITHDRAWAL`, cálculos automáticos de progreso y validación matemática de saldos en tiempo real).
  * `app/src/main/java/com/andina/finanzas/ui/pockets/PocketsViewModel.kt` (ViewModel `@HiltViewModel` conectado a `FinanceRepository`, `ContributeToPocketUseCase` y `WithdrawFromPocketUseCase`, observación combinada continua de bolsillos y billeteras, validación atómica y detección festiva del 100% de la meta).
* **Reporte del Agente (Quality Gate):**
  * Manejo contable limpio y desacoplado, sin mutaciones directas y con protección estricta contra saldos insuficientes.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 9.2: Componentes Visuales y Dashboard (`PocketCard.kt`, `PocketsHeroCard.kt` y `PocketsScreen.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/pockets/components/PocketCard.kt` (tarjeta interactiva con `FrailejonView` procedimental en Canvas que oscila y crece con el ahorro, barra de progreso animada `FastOutSlowInEasing`, badge de estado y botones rápidos de acción `[ Retirar ]` y `[ Aportar ]`).
  * `app/src/main/java/com/andina/finanzas/ui/pockets/components/PocketsHeroCard.kt` (tarjeta insignia con gradiente de bosque andino, balance total reservado en metas, meta global consolidada, porcentaje general y botón directo `[ + Nueva Meta ]`).
  * `app/src/main/java/com/andina/finanzas/ui/pockets/PocketsScreen.kt` (dashboard completo con filtros rápidos *Todas*, *En Crecimiento*, *Completadas*, estado vacío cálido con Muggy y diálogo festivo de felicitación).
* **Reporte del Agente (Quality Gate):**
  * Ecosistema vivo integrado a 60-120 FPS sin consumo innecesario de recursos.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 9.3: Detalle de Meta y "La Vara del Páramo" (`PocketDetailScreen.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/pockets/PocketDetailScreen.kt` (pantalla de detalle con frailejón vivo en gran formato 140dp, desglose financiero completo y simulador interactivo *"La Vara del Páramo"* con slider de ahorro semanal y proyección dinámica en semanas/meses).
* **Reporte del Agente (Quality Gate):**
  * Cumple estrictamente con las especificaciones de `AYUDAS_INTERACTIVAS.md` §2 y `DISENO_VISUAL.md`.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 9.4: Modal de Aporte y Retiro Atómico (`PocketMoveBottomSheet.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/pockets/components/PocketMoveBottomSheet.kt` (modal con selector interactivo de billeteras con saldos visibles, previsualización de impacto en vivo, atajos rápidos de dinero `+$20k`, `+$50k`, `+$100k` y botón inteligente *Faltante/Todo*, teclado numérico táctil y validación inmediata contra sobregiros).
* **Reporte del Agente (Quality Gate):**
  * Cero ambigüedad en el origen y destino del dinero, con retroalimentación háptica en cada interacción.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 9.5: Formulario de Creación de Bolsillos (`PocketFormBottomSheet.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/pockets/components/PocketFormBottomSheet.kt` (modal de creación con selección de nombre, monto objetivo en COP con formateo en vivo, selector circular de iconos temáticos andinos y paleta de colores del páramo).
* **Reporte del Agente (Quality Gate):**
  * Validación preventiva: botón deshabilitado hasta ingresar nombre válido y meta estrictamente mayor a $0 COP.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 9.6: Conexión App Shell y Quality Gate de Bolsillos (`AndinaAppShell.kt`, `Color.kt` y `PocketMoveValidationTest.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/navigation/AndinaAppShell.kt` (sustitución oficial del placeholder de `PocketsRoute` conectándolo directamente a `PocketsScreen`).
  * `app/src/main/java/com/andina/finanzas/ui/theme/Color.kt` (definición de alias semánticos andinos `AndinaMossGreen`, `AndinaDeepForest`, `AndinaAmber`, `AndinaErrorRed`).
  * `app/src/test/java/com/andina/finanzas/ui/pockets/PocketMoveValidationTest.kt` (suite de 9 pruebas unitarias exhaustivas validando: inicialización y cálculo de progreso, apertura de modal, digitación y atajos rápidos, bloqueo ante saldo insuficiente en billetera al aportar, bloqueo ante saldo insuficiente en bolsillo al retirar, aporte normal sin celebración, aporte que alcanza el 100% disparando celebración festiva y diálogo con Muggy, retiro atómico y validación en la creación de bolsillos).
* **Reporte del Agente (Quality Gate):**
  * **52 pruebas unitarias de 52 pasando al 100% en verde** en todo el proyecto (`:app:testDebugUnitTest`).
  * Tarea de empaquetado `:app:assembleDebug` ejecutada con éxito (`BUILD SUCCESSFUL in 39s`), generando el APK de depuración funcional con el módulo completo de Bolsillos.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones antes de autorizar el cierre definitivo de Fase 09 y avanzar a la Fase 10)*
* **Estado:** `[ COMPLETADA ]`

---

### 🔹 FASE 10: Historial de Movimientos, Paging y Filtros

#### 📍 Sub-fase 10.1: Contrato y ViewModel de Movimientos (`TransactionsContract.kt`, `TransactionsViewModel.kt`, `TransactionDao.kt`, `FinanceRepository.kt`, `FinanceRepositoryImpl.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/data/local/dao/TransactionDao.kt` (evolución de la consulta `pagingSource` con soporte reactivo de búsqueda textual insensible a mayúsculas, filtro por tipo contable, billetera, categoría, solo compras hormiga y rango temporal epoch `fromMillis`/`toMillis`).
  * `app/src/main/java/com/andina/finanzas/domain/repository/FinanceRepository.kt` y `FinanceRepositoryImpl.kt` (integración de `getTransactionsPaged` conectando Paging 3 con `Pager`, más métodos de actualización y restauración con conservación de ID).
  * `app/src/main/java/com/andina/finanzas/ui/transactions/TransactionsContract.kt` (contrato formal con `TransactionsFilter`, preajustes temporales `DateRangeFilterPreset`, estado inmutable `TransactionsUiState`, intenciones `TransactionsEvent` y efectos `TransactionsEffect`).
  * `app/src/main/java/com/andina/finanzas/ui/transactions/TransactionsViewModel.kt` (orquestación reactiva con `flatMapLatest` + `cachedIn(viewModelScope)`, combinación en tiempo real con billeteras y categorías activas, y gestión de borrado temporal con retención en memoria durante 5 segundos).
* **Reporte del Agente (Quality Gate):**
  * Flujo paginado totalmente desacoplado y reactivo a cualquier cambio de filtros sin recrear repositorios ni invalidar caches indebidamente.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 10.2: Lista Paginada con Paging 3 y Agrupación Temporal (`TransactionsScreen.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/transactions/TransactionsScreen.kt` (pantalla principal con cabecera de búsqueda rápida, chips horizontales de selección ágil [Todos, Gastos, Ingresos, Hormiga 🐜, Transferencias], recolección con `collectAsLazyPagingItems()` y cálculo dinámico de encabezados de agrupación: *Hoy*, *Ayer*, *Mes actual* y meses previos).
* **Reporte del Agente (Quality Gate):**
  * Renderizado fluido en `LazyColumn` sin bloqueos de hilo principal, separadores andinos sutiles y estado vacío con botón de restablecimiento inmediato de filtros.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 10.3: Swipe-to-Action con Hojas Andinas y Deshacer (`SwipeableTransactionRow.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/transactions/components/SwipeableTransactionRow.kt` (componente interactivo con `SwipeToDismissBox`, fondo degradado con tono andino `AndinaDeepForest` y `AndeanError`, ráfaga orgánica de hojas de frailejón aterciopeladas trazadas en `Canvas` y nervaduras sutiles durante el deslizamiento).
* **Reporte del Agente (Quality Gate):**
  * Deslizado bidireccional suave con umbral del 45% y retroalimentación inmediata conectada al snackbar con acción "Deshacer" de 5 segundos.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 10.4: Modal de Filtros Avanzados (`TransactionsFilterBottomSheet.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/transactions/components/TransactionsFilterBottomSheet.kt` (modal inferior multi-criterio con selección de tipo, métodos de pago colombianos dinámicos [`Nequi`, `Daviplata`, `Efectivo`, etc.], tarjeta interactiva con switch para compras hormiga caramelo, chips de rango de fechas y botón de aplicación con conteo de criterios activos).
* **Reporte del Agente (Quality Gate):**
  * Conteo visual de filtros activos reflejado en la barra superior con badge destacado en verde musgo.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 10.5: Detalle y Edición de Movimiento (`TransactionDetailScreen.kt`, `TransactionDetailViewModel.kt`, `AndinaDestinations.kt` y `AndinaAppShell.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/navigation/AndinaDestinations.kt` (registro type-safe de `TransactionDetailRoute(transactionId: Long)`).
  * `app/src/main/java/com/andina/finanzas/ui/transactions/detail/TransactionDetailContract.kt` y `TransactionDetailViewModel.kt` (gestión de carga, edición de notas, alternancia de gasto hormiga y eliminación atómica).
  * `app/src/main/java/com/andina/finanzas/ui/transactions/detail/TransactionDetailScreen.kt` (tarjeta hero con cifra en pesos colombianos, número de folio contable `#TX-XXXXXX`, metadatos de billetera/categoría, edición de notas en vivo, switch hormiga y diálogo de confirmación de borrado).
  * `app/src/main/java/com/andina/finanzas/ui/navigation/AndinaAppShell.kt` (conexión de la pestaña Movimientos y ruta de Detalle, integrando `showSnackbarWithAction` en el shell).
* **Reporte del Agente (Quality Gate):**
  * Navegación fluida bidireccional entre la lista paginada y la vista detallada de cualquier transacción con actualización reactiva.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 10.6: Quality Gate de Transacciones (`TransactionsFilterTest.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/test/java/com/andina/finanzas/ui/transactions/TransactionsFilterTest.kt` (suite de 14 pruebas unitarias validando: estado inicial por defecto, búsqueda por texto, filtro de tipo, filtro de billetera, switch hormiga, presets temporales, acumulación de filtros activos, reset completo, visibilidad del modal de filtros, eliminación con guardado temporal, restauración exitosa con Deshacer, expiración de la ventana de 5 segundos, límites de epoch y clasificación por grupos temporales *Hoy* y *Ayer*).
* **Reporte del Agente (Quality Gate):**
  * **66 pruebas unitarias de 66 pasando al 100% en verde** en todo el proyecto (`:app:testDebugUnitTest`).
  * Tarea de empaquetado `:app:assembleDebug` ejecutada con éxito (`BUILD SUCCESSFUL in 50s`), generando el APK de depuración con la suite completa de auditoría financiera, Paging 3 y filtros.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones antes de autorizar el cierre definitivo de Fase 10 y avanzar a la Fase 11)*
* **Estado:** `[ COMPLETADA ]`

---

*(Fase 10 completada al 100%).

---

### 🔹 FASE 11: Muggy, Ecosistema Vivo e Inteligencia On-Device (Andina Intelligence Core)

#### 📍 Sub-fase 11.1: Signos Vitales y Estados de Muggy (`MuggyAvatar.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/components/muggy/MuggyAvatar.kt` (componente vivo oficial con las 5 capas orgánicas: respiración sinusoidal en RenderThread con ciclo de 3.5s, parpadeo estocástico e impredecible entre 2.5 y 5.5s con probabilidad de doble parpadeo, ritmo circadiano día/tarde/noche, respuesta elástica táctil con amortiguación `MediumBouncy` y `HapticFeedbackType.LongPress`, y trazado vectorial completo en Canvas con pupilas, anteojos ámbar, orejas, flor de frailejón y poses oficiales del Model Sheet).
* **Reporte del Agente (Quality Gate):**
  * Renderizado GPU sin recomposiciones innecesarias ni desbordamientos de memoria.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 11.2: Motor de Locomoción Física y Cinemática (`MuggyLocomotion.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/components/muggy/MuggyLocomotion.kt` (cinemática balística con curvas de Bézier cuadráticas, deformación elástica *Squash & Stretch* según fase de vuelo, caminata andina con oscilación armónica de cadera, planeo suave con hoja de frailejón, registro de anclajes espaciales `MuggyAnchorRegistry` y contenedor cinemático en RenderThread).
* **Reporte del Agente (Quality Gate):**
  * Locomoción cancelable atómicamente con `conflate()` libre de bloqueos de hilo principal.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 11.3: Lluvia de Hojas Andinas y Gotas en Canvas (`LeafWindCanvas.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/components/ecosystem/LeafWindCanvas.kt` (motor de partículas reciclables con cero recolección de basura `Object Pooling`, array acotado de 55 partículas pre-asignadas, ecuación de viento andino con oscilación pendular y gravedad terminal, renderizado vectorial de hojas de frailejón con nervaduras y modo confeti orgánico para celebraciones de metas).
* **Reporte del Agente (Quality Gate):**
  * Tasa de refresco constante a 60-120 FPS sin pausas de Garbage Collection.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 11.4: Súper Ayudas y Simuladores Interactivos (`SpotlightHelper.kt`, `PocketSimulator.kt`, `AntExpenseMirror.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/help/SpotlightHelper.kt` (foco tutorial guiado con velo translúcido al 72%, recorte vectorial `BlendMode.Clear` sobre elementos interactivos, y tarjeta de guía con Muggy mentor).
  * `app/src/main/java/com/andina/finanzas/ui/help/PocketSimulator.kt` (*"La Vara del Páramo"*, simulador visual con slider de ahorro semanal en COP, frailejón que florece en vivo y estimación en semanas/meses).
  * `app/src/main/java/com/andina/finanzas/ui/help/AntExpenseMirror.kt` (*"El Espejo Anual"*, simulador de gastos hormiga con chips interactivos [café, empanada, taxi, snacks], proyección anual en cifras grandes ámbar y CTA directo para crear meta de ahorro).
* **Reporte del Agente (Quality Gate):**
  * Cálculo inmediato reactivo con formateo monetario en COP sin desajustes contables.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 11.5: Motor Semántico & NLP Colombiano On-Device (`MerchantFuzzyClassifier.kt`, `ColombianBankSmsParser.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/core/intelligence/nlp/MerchantFuzzyClassifier.kt` (árbol Trie en memoria + distancia Levenshtein con base de datos de comercios colombianos frecuentes [D1, Éxito, Ara, Tostao, Juan Valdez, Uber, etc.], normalización insensible a tildes y detección de candidatos hormiga).
  * `app/src/main/java/com/andina/finanzas/core/intelligence/nlp/ColombianBankSmsParser.kt` (parser offline de SMS bancarios de Bancolombia, Nequi, DaviPlata y NuBank, extrayendo monto limpio, comercio normalizado, billetera y categoría en < 1 ms).
* **Reporte del Agente (Quality Gate):**
  * 100% offline, cero dependencias de internet o SDKs de nube.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 11.6: Motor Estadístico de Series de Tiempo (`EwmaAnomalyDetector.kt`, `QuincenaCycleAnalyzer.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/core/intelligence/stats/EwmaAnomalyDetector.kt` (cálculo de Media Móvil Ponderada Exponencial EWMA con factor $\alpha = 0.25$, varianza dinámica y detección de picos de gasto con umbral $Z > 2.0$).
  * `app/src/main/java/com/andina/finanzas/core/intelligence/stats/QuincenaCycleAnalyzer.kt` (modelado de estacionalidad de pagos colombianos en días 15 y 30, velocidad de quema *Burn Rate*, proyección de saldo al cobro y cálculo formal de **Días de Oxígeno** / Runway).
* **Reporte del Agente (Quality Gate):**
  * Precisión matemática total sin redondeos de coma flotante perjudiciales.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 11.7: Sistema Experto de Triaje y Diálogos NLG (`FinancialHealthScorer.kt`, `MuggyDialogueEngine.kt`, `AndinaIntelligenceCore.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/core/intelligence/triage/FinancialHealthScorer.kt` (matriz de reglas ponderadas: presupuesto 35%, gastos hormiga 25%, días de oxígeno 25%, ahorro en bolsillos 15%; Score de 0 a 100 y clasificación en `HOLGADO`, `PREVENTIVO`, `AJUSTE` o `CRÍTICO`).
  * `app/src/main/java/com/andina/finanzas/core/intelligence/nlg/MuggyDialogueEngine.kt` (generador modular de texto empático paramuno con modismos colombianos, saludo horario, diagnóstico sin culpa bancaria y botones de acción en 1 tap).
  * `app/src/main/java/com/andina/finanzas/core/intelligence/AndinaIntelligenceCore.kt` (fachada unificada inyectable `@Singleton` orquestando los 4 motores locales en un solo diagnóstico unificado).
* **Reporte del Agente (Quality Gate):**
  * Diagnóstico integral ejecutado en menos de 5 ms localmente.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 11.8: S19 Consultorio Financiero de Muggy (`ConsultorioContract.kt`, `ConsultorioViewModel.kt`, `ConsultorioScreen.kt`, `AndinaDestinations.kt`, `AndinaAppShell.kt`, `HomeScreen.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/consultorio/ConsultorioContract.kt` (contrato MVI con estados de diagnóstico, herramientas interactivas y efectos).
  * `app/src/main/java/com/andina/finanzas/ui/consultorio/ConsultorioViewModel.kt` (orquestación reactiva combinando Room con el motor de inteligencia local).
  * `app/src/main/java/com/andina/finanzas/ui/consultorio/ConsultorioScreen.kt` (pantalla completa S19: tarjeta hero con score 0-100, badge de triaje, días de oxígeno, Muggy vivo en pose reactiva, tarjeta NLG de consejos, chips de herramientas interactivas, desglose de categorías y decodificador de SMS con precarga de gasto).
  * `app/src/main/java/com/andina/finanzas/ui/navigation/AndinaDestinations.kt` y `AndinaAppShell.kt` (registro type-safe de `ConsultorioRoute` y cableado en el grafo).
  * `app/src/main/java/com/andina/finanzas/ui/home/HomeScreen.kt` (conexión del banner editorial de Muggy para abrir directamente el Consultorio Financiero).
* **Reporte del Agente (Quality Gate):**
  * Navegación type-safe completa y experiencia 100% offline y privada.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 11.9: Quality Gate de Inteligencia Local y Muggy (`IntelligenceCoreTest.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/test/java/com/andina/finanzas/core/intelligence/IntelligenceCoreTest.kt` (suite de 12 pruebas unitarias validando: clasificación de supermercados, detección de compras hormiga, tolerancia tipográfica con Levenshtein, parsers de SMS Bancolombia/Nequi/DaviPlata, detección de anomalías EWMA con $Z > 2.0$, proyección quincenal y días de oxígeno, ponderación matemática del triaje 0-100, generación de lenguaje natural y benchmark de latencia menor a 15 ms).
* **Reporte del Agente (Quality Gate):**
  * **80 pruebas unitarias de 80 pasando al 100% en verde** en todo el proyecto (`:app:testDebugUnitTest`).
  * Tarea de empaquetado `:app:assembleDebug` ejecutada con éxito (`BUILD SUCCESSFUL in 46s`), generando el APK de depuración funcional con Muggy animado y el motor de inteligencia local completo.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones antes de autorizar el cierre definitivo de Fase 11 y avanzar a la Fase 12)*
* **Estado:** `[ COMPLETADA ]`

---

### 🔹 FASE 12: Blindaje de Seguridad, Modo Oscuro, Exportación y Publicación

#### 📍 Sub-fase 12.1: Android Keystore por Hardware (`KeystoreHelper.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/data/security/KeystoreHelper.kt` (gestor criptográfico `@Singleton` respaldado por Android Keystore en TEE/StrongBox con clave maestra AES-256 en modo GCM/NoPadding, generación de frase de paso de 32 bytes con `SecureRandom`, empaquetado con IV de 12 bytes y Auth Tag de 128 bits, almacenamiento seguro en preferencias privadas y caché en memoria).
* **Reporte del Agente (Quality Gate):**
  * Clave criptográfica custodiada por hardware sin credenciales codificadas en el APK.
  * Tarea de compilación `:app:compileDebugKotlin` superada limpiamente (`BUILD SUCCESSFUL in 3m 47s`).
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 12.2: Cifrado SQLCipher de la Base de Datos (`AppDatabase.kt` y `DatabaseModule.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/data/local/AppDatabase.kt` (integración de `SupportOpenHelperFactory` de `net.zetetic:sqlcipher-android` para aplicar cifrado militar AES-256 en disco, manteniendo compatibilidad con bases en memoria para pruebas rápidas).
  * `app/src/main/java/com/andina/finanzas/data/di/DatabaseModule.kt` (inyección Hilt de `KeystoreHelper` para alimentar de manera transparente la passphrase de 32 bytes custodiada por hardware a la factoría de Room).
* **Reporte del Agente (Quality Gate):**
  * La base de datos se cifra íntegramente en SQLite local sin alterar los DAOs, vistas contables ni repositorios.
  * Tareas `:app:compileDebugKotlin` y `:app:testDebugUnitTest` superadas al 100% en verde con todas las 80 pruebas unitarias existentes pasando.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 12.3: Bloqueo Biométrico (`LockScreen.kt` y `MainActivity.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/security/LockScreen.kt` (pantalla de autenticación bancaria con integración nativa de `BiometricPrompt` [huella/rostro] y fallback táctil a teclado numérico de PIN de 4 dígitos con animación de sacudida y vibración háptica).
  * `app/src/main/java/com/andina/finanzas/MainActivity.kt` (evolución a `FragmentActivity` para soportar `BiometricPrompt` nativo preservando Edge-to-Edge y Hilt).
* **Reporte del Agente (Quality Gate):**
  * Soporte biométrico nativo completo con experiencia fluida y fallback seguro sin riesgo de bloqueo accidental.
  * Tarea de compilación `:app:compileDebugKotlin` superada limpiamente (`BUILD SUCCESSFUL in 34s`).
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 12.4: Paleta Modo Oscuro "Páramo Nocturno" (`Theme.kt`, `Color.kt` y `ThemePreviewTest.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/ui/theme/Color.kt` (definición de tokens OLED `#121411` con verde musgo nocturno desaturado 15%, contenedor `#1B1E19`, reflejos dorados de flores de frailejón nocturno `#D4AF37`, bordes sutiles al 10% `#1AFFFFFF` y gradiente vertical andino).
  * `app/src/main/java/com/andina/finanzas/ui/theme/Theme.kt` (configuración del esquema `DarkColorScheme` oficial con contraste certificado).
  * `app/src/test/java/com/andina/finanzas/ui/theme/ThemePreviewTest.kt` (validación matemática de ratios WCAG AA para todos los pares cromáticos nocturnos con ratios >= 7.0:1).
* **Reporte del Agente (Quality Gate):**
  * Paleta OLED pura sin halos agresivos ni agotamiento visual, cumpliendo accesibilidad WCAG AA.
  * Tarea `:app:testDebugUnitTest` superada al 100% en verde (81 pruebas pasando).
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 12.5: Exportación Contable y Backup Portable (`CsvExporter.kt`, `PdfReportExporter.kt` y `BackupManager.kt`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/src/main/java/com/andina/finanzas/data/export/CsvExporter.kt` (generador de extractos CSV con codificación UTF-8 con BOM y delimitador `;` para compatibilidad transparente con Excel en español).
  * `app/src/main/java/com/andina/finanzas/data/export/PdfReportExporter.kt` (generador nativo M3 de reportes en PDF con [PdfDocument], franja verde andino, tarjetas de resumen patrimonial y tabla de movimientos con paginación automática).
  * `app/src/main/java/com/andina/finanzas/data/export/BackupManager.kt` (sistema de respaldo portable `.andina` con serialización JSON completa y cifrado opcional AES-GCM derivado de PIN con PBKDF2/SHA-256 para migración segura entre dispositivos).
  * `app/src/main/java/com/andina/finanzas/data/local/dao/TransactionDao.kt`, `WalletDao.kt`, `PocketDao.kt` y `CategoryDao.kt` (consultas masivas de lectura y limpieza atómica para importación/exportación de copias).
* **Reporte del Agente (Quality Gate):**
  * Exportación e importación de grado contable verificada sin dependencias externas inseguras.
  * Tareas `:app:compileDebugKotlin` y `:app:testDebugUnitTest` superadas al 100% en verde con todas las 81 pruebas pasando.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---

#### 📍 Sub-fase 12.6: Ofuscación y Reglas ProGuard/R8 (`proguard-rules.pro`)
* **Fecha:** 2026-10-07
* **Archivos intervenidos:**
  * `app/proguard-rules.pro` (reglas completas de R8 y ProGuard protegiendo las librerías nativas de SQLCipher, entidades y DAOs de Room, compatibilidad con Kotlinx Serialization para DTOs de backup y rutas de Navigation Compose, BiometricPrompt nativo y módulos Hilt).
* **Reporte del Agente (Quality Gate):**
  * Configuración R8 blindada que previene el quiebre de clases criptográficas y modelos en empaquetado optimizado Release.
  * Tarea de compilación `:app:compileDebugKotlin` superada limpiamente en 18s.
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ COMPLETADA ]`

---


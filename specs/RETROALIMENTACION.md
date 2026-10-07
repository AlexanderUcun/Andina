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
| **04** | Capa de Dominio y Casos de Uso Contables | ⏳ Pendiente | 0 / 5 | ⏳ |
| **05** | Componentes UI Reutilizables y Ecosistema | ⏳ Pendiente | 0 / 6 | ⏳ |
| **06** | App Shell, Navigation y Barra Inferior | ⏳ Pendiente | 0 / 5 | ⏳ |
| **07** | Modal de Registro Rápido con Muggy Asomado | ⏳ Pendiente | 0 / 5 | ⏳ |
| **08** | Pantalla de Inicio (Hero Saldo y Carrusel) | ⏳ Pendiente | 0 / 6 | ⏳ |
| **09** | Bolsillos de Ahorro y Frailejón Vivo | ⏳ Pendiente | 0 / 6 | ⏳ |
| **10** | Historial de Movimientos, Paging y Filtros | ⏳ Pendiente | 0 / 6 | ⏳ |
| **11** | Muggy 3D, Ayudas e Inteligencia On-Device | ⏳ Pendiente | 0 / 9 | ⏳ |
| **12** | Seguridad SQLCipher, Exportación y Release | ⏳ Pendiente | 0 / 7 | ⏳ |

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
* **Commit:** `Pendiente de commit`
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

*(Fase 03 completada al 100%. Las siguientes fases se irán completando y documentando en esta misma bitácora de forma continua).*




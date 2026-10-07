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
| **01** | Cimientos del Entorno y Gradle Limpio | 🟡 En curso | 2 / 4 | ⏳ |
| **02** | Design System Base (Andean Mist & Moss) | ⏳ Pendiente | 0 / 5 | ⏳ |
| **03** | Motor de Persistencia Room y Libro Mayor | ⏳ Pendiente | 0 / 6 | ⏳ |
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
* **Fecha:** *[Pendiente]*
* **Commit:** *[Pendiente]*
* **Archivos intervenidos:**
  * `settings.gradle.kts`
  * `build.gradle.kts` (raíz)
  * `app/build.gradle.kts`
  * `app/src/main/AndroidManifest.xml`
* **Reporte del Agente (Quality Gate):**
  * *[Pendiente de ejecución]*
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ PENDIENTE ]`

---

#### 📍 Sub-fase 1.4: Quality Gate de Fase 01
* **Fecha:** *[Pendiente]*
* **Commit:** *[Pendiente]*
* **Comando ejecutado:** `./gradlew tasks --dry-run`
* **Reporte del Agente:**
  * *[Pendiente de ejecución]*
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones antes de cerrar la Fase 01)*
* **Estado:** `[ PENDIENTE ]`

---

*(Las siguientes fases se irán completando y documentando en esta misma bitácora de forma continua).*

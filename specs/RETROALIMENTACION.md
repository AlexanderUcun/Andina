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

## 🚦 Protocolo de Retroalimentación y Control en 4 Pasos

```
   ┌───────────────────────┐
   │ 1. REPORTE Y DETALLE  │  --> Explicación detallada de la sub-fase, archivos y Quality Gate.
   └───────────┬───────────┘
               ▼
   ┌───────────────────────┐
   │ 2. FEEDBACK USUARIO   │  --> Escribes tus notas, ajustes estéticos o correcciones aquí.
   └───────────┬───────────┘
               ▼
   ┌───────────────────────┐
   │ 3. CIERRE Y COMMIT    │  --> El agente incorpora tu feedback y sella el commit local en Git.
   └───────────┬───────────┘
               ▼
   ┌───────────────────────┐
   │ 4. "Subir a github"   │  --> SOLO con autorización explícita del usuario se hace git push.
   └───────────────────────┘
```

> [!IMPORTANT]
> **Regla Estricta de Sincronización Remota:** El agente **NUNCA ejecutará `git push` automáticamente**.  
> Al finalizar cada sub-fase y cada fase mayor, el agente entregará una **explicación técnica detallada y comprensible**. Los cambios solo se enviarán a GitHub cuando el usuario lo autorice explícitamente escribiendo: **"Subir a github"**.


---

## 📊 Matriz General de Fases

| Fase | Título | Estado | Sub-fases Completadas | Aprobación Usuario |
|---|---|:---:|:---:|:---:|
| **01** | Cimientos del Entorno y Gradle Limpio | ⏳ Pendiente | 0 / 4 | ⏳ |
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
* **Fecha:** *[Pendiente]*
* **Commit:** *[Pendiente]*
* **Archivos intervenidos:**
  * `.gitignore` (actualizado y blindado)
  * `gradle.properties` (`jvmargs=-Xmx2048m -Dfile.encoding=UTF-8`)
* **Reporte del Agente (Quality Gate):**
  * *[Pendiente de ejecución]*
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ PENDIENTE ]`

---

#### 📍 Sub-fase 1.2: Catálogo Centralizado de Dependencias (`libs.versions.toml`)
* **Fecha:** *[Pendiente]*
* **Commit:** *[Pendiente]*
* **Archivos intervenidos:**
  * `gradle/libs.versions.toml`
* **Reporte del Agente (Quality Gate):**
  * *[Pendiente de ejecución]*
* **📝 Espacio de Retroalimentación del Usuario:**
  > *(Escribe aquí tus observaciones, dudas o confirmación para esta sub-fase)*
* **Estado:** `[ PENDIENTE ]`

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

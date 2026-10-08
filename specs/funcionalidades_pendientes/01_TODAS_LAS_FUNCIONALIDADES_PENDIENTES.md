# 🛠️ ESPECIFICACIONES FUNCIONALES PENDIENTES — APP ANDINA

**Versión:** 1.3  
**Fecha de Actualización:** 07 de Octubre, 2026  
**Propósito:** Definir de manera metódica todas las funcionalidades, pantallas y reglas de negocio pendientes que deben estar construidas y validadas al 100% antes de iniciar la fase de Rediseño Visual 4K.

---

## 📌 ÍNDICE DE MÓDULOS PENDIENTES

```text
specs/funcionalidades_pendientes/
└── 01_TODAS_LAS_FUNCIONALIDADES_PENDIENTES.md
```

---

### 👤 MÓDULO 1: Perfil y Configuración del Usuario ("Perfil del Explorador")
* **Estado Actual:** El header de Inicio solo muestra un avatar estático con la inicial "V" y el texto rígido "Buenas noches, Valentina". No existe pantalla de ajustes.
* **Requerimientos Funcionales:**
  1. **Pantalla de Perfil dedicando acceso desde el Avatar superior izquierdo:**
     - Edición del Nombre del Usuario (ej: Valentina, Alexander).
     - Selección de Apodo/Título en el Páramo (ej: "Frailejón Mayor", "Guardiana del Páramo").
  2. **Personalización del Avatar:**
     - Opción de subir foto personal desde la Galería.
     - Opción de elegir Avatares Andinos Ilustrados (Muggy, Frailejón, Colibrí, Cóndor).
  3. **Preferencias Generales:**
     - Formato de moneda por defecto (`$ COP`).
     - Configuración de horario de saludos.
     - Switch de modo de rendimiento (Modo 4K Completo con Shaders vs. Modo Ahorro de Batería).

---

### 🏷️ MÓDULO 2: Rediseño de Selección de Categorías & Independencia del Campo de Nota
* **Fallas Visuales y de UX Detectadas en Capturas:**
  - La selección de categorías actual es una **fila horizontal que se puede volver infinita e incómoda** de deslizar en pantallas pequeñas.
  - No existe una pantalla/modal dedicada para ver, seleccionar o personalizar categorías.
  - El botón `"Añadir nota"` despliega una caja de texto gigante dentro del mismo modal que **colisiona con el teclado numérico custom**, haciendo que al abrir el teclado del sistema se empuje o desordene todo el formulario.
* **Requerimientos Funcionales:**
  1. **Selección de Categorías en Cuadrícula / Modal Dedicado:**
     - Reemplazar la fila horizontal infinita por un grid organizado de 2 filas o un selector modal visual donde se vean todas las categorías disponibles claras y accesibles.
     - Aislamiento estricto: Pestaña `GASTO` solo muestra categorías de egreso; Pestaña `INGRESO` solo muestra categorías de ingreso.
  2. **Independencia del Campo de Nota:**
     - Separar la edición de la nota del flujo principal del teclado numérico.
     - La nota se podrá ingresar mediante un chip modal independiente o una caja emergente limpia, evitando que el teclado suave del sistema empuje o desordene el teclado numérico de dinero de la app.

---

### 💳 MÓDULO 3: Gestión Completa de Billeteras y Medios de Pago
* **Requerimientos Funcionales:**
  1. Pantalla de administración para Agregar, Editar, Ocultar o Reordenar Billeteras (Nequi, Daviplata, Bancolombia, Efectivo, Tarjetas).
  2. Configuración de Saldos Iniciales y límites de sobregiro por billetera.

---

### 📊 MÓDULO 4: Gestor de Presupuesto Mensual Adaptativo
* **Requerimientos Funcionales:**
  1. Definición del Límite de Gasto Global Mensual (ej: `$2.500.000 COP`).
  2. Asignación de topes de presupuesto por categoría (ej: máximo `$400.000 COP` en Domicilios).
  3. Alertas preventivas automáticas cuando el presupuesto alcance el 75% y 90% de consumo.

---

### 🔔 MÓDULO 5: Centro de Notificaciones y Alertas (Campana Superior)
* **Requerimientos Funcionales:**
  1. Conectar el ícono de la campana en la barra superior a un panel flotante / pantalla de notificaciones.
  2. Historial de avisos: Detección de Gastos Hormiga por Muggy, Alertas de Presupuesto y Recordatorios de Ahorro para Bolsillos.

---

### 📦 MÓDULO 6: Exportación de Reportes y Respaldos Locales
* **Requerimientos Funcionales:**
  1. Exportación de movimientos en formato **PDF** (reporte mensual diseñado) y **CSV/Excel**.
  2. Respaldo de seguridad local (Exportar/Importar base de datos SQLite cifrada para no perder datos al cambiar de teléfono).

---

### 🤖 MÓDULO 7: Consultorio Financiero de Muggy & Simplificación de Métricas (IA On-Device)
* **Requerimientos Funcionales & UX de Acceso:**
  1. **Rediseño de Métrica Confusa ("Días de Oxígeno" -> "Días de Autonomía"):**
     - Cambiar la terminología extraña "85 Días de Oxígeno 🫁" por **"85 Días de Autonomía Financiera 🛡️"** o **"Reserva para 85 Días"**.
     - Incluir tooltip / micro-explicación: *"Indica cuántos días puedes vivir con tu saldo disponible actual según tu promedio de gasto diario"*.
  2. **Acceso Inmediato a 1 Tap:**
     - Hacer que la tarjeta de Muggy en la Home ("Muggy · Consejo Andino") sea un botón interactivo que abra directamente el Consultorio de IA.
  3. **Pantalla de Consultorio e Interacción:**
     - Pantalla de diálogo / chat interactivo con Muggy para pedir diagnósticos financieros en tiempo real.
     - Evaluación de compras impulsivas y recomendaciones de ahorro basadas en el historial del usuario.

---

### 🔄 MÓDULO 8: Gestor de Gastos e Ingresos Fijos Programables (Recurrentes con Autorización)
* **Requerimientos Funcionales:**
  1. **Programación y Registro de Ingresos Recurrentes (Sueldo/Rentas):**
     - Al llegar la fecha programada (ej. día 15 o 30), la app **NUNCA abonará el dinero a ciegas**.
     - Generará una notificación flotante de confirmación previa: *"¿Recibiste tu salario de $2.500.000 COP hoy en Bancolombia?"*.
     - Incluir botón directo **"Editar Monto/Fecha"** (por si la quincena vino con deducciones, horas extras o cambió el valor) antes de confirmarlo.
  2. **Programación Automática de Pagos Fijos:**
     - Registrar gastos recurrentes (Arriendo, Servicios Públicos, Suscripciones).
     - Aviso de vencimiento con opción de confirmación previa a 1 tap.

---

### 🎨 MÓDULO 9: Hub / Pestaña de Personalización Total ("Personaliza tu Ecosistema")
* **Requerimientos Funcionales:**
  1. **Gestión Avanzada de Categorías:**
     - Crear, editar, ocultar y reordenar categorías personalizadas de gasto o ingreso.
     - Selector de paleta de colores (Hex) y selector de iconos vectoriales para cada categoría.
  2. **Personalización del Páramo y Apariencia:**
     - Configurar qué tarjetas se muestran en la pantalla de Inicio (ocultar/mostrar Presupuesto, Billeteras o Muggy).
     - Selección de temas de color del ecosistema andino y estilos visuales.

---

### 📅 MÓDULO 10: Calendario Financiero Interactivo & Mapa de Flujo de Caja
* **Requerimientos Funcionales:**
  1. **Vista Calendario de Gastos y Pagos:**
     - Un calendario visual mensual/semanal donde cada día muestra indicadores de color (ingresos, gastos, vencimientos).
  2. **Navegación e Inspección por Día:**
     - Al tocar cualquier día, se despliega el desglose de transacciones y pagos pendientes.

---

### 📱 MÓDULO 11: Solución Definitiva de WindowInsets & Safe Drawing (Solapamiento StatusBar)
* **Falla Crítica de UI Identificada en Captura:**
  - El título de las pantallas (`Medios & Billeteras`, `Movimientos`) y los botones superiores se mezclan y traslapan directamente con la hora (`12:09`), la señal 5G, las notificaciones y la batería (`70%`) del sistema Android.
* **Solución Técnica Definitiva:**
  1. **Configuración de Insets en `AndinaAppShell.kt`:**
     - Reemplazar `contentWindowInsets = WindowInsets(0)` en el `Scaffold` por la propagación nativa de `WindowInsets.safeDrawing` para que los `paddingValues` absorban automáticamente la altura exacta de la barra de estado y de navegación.
  2. **Aplicación Estricta de `statusBarsPadding()`:**
     - Garantizar que los contenedores superiores de `HomeScreen`, `WalletsScreen`, `PocketsScreen`, `TransactionsScreen`, `ConsultorioScreen` y `TransactionDetailScreen` apliquen `Modifier.statusBarsPadding()`.
  3. **Ajuste para Teclado Virtual (`imePadding`):**
     - Aplicar `Modifier.imePadding()` en la pantalla del Decodificador SMS y formularios para que la vista flote por encima del teclado sin tapar botones.

---

### 🌱 MÓDULO 12: Gestión Completa de Bolsillos (Menú de 3 Puntos, Interior y Recordatorios)
* **Requerimientos Funcionales & UX:**
  1. **Especificación del Menú Contextual de 3 Puntos (`⋮`):** `✏️ Editar Meta`, `🔔 Programar Recordatorio`, `🗑️ Eliminar Bolsillo` (con devolución de saldo a billetera elegida).
  2. **Mejoras en Detalle Interior:** Mostrar Nombre del Bolsillo en TopBar e Historial de Abonos/Retiros.

---

### 🚨 MÓDULO 13: Control Estricto de Montos Máximos (Ingresos/Gastos) & Prevención de Overlap
* **Fallas de Negocio y UI Identificadas:**
  - La app permite ingresar **Ingresos o Gastos desproporcionados e ilógicos** de hasta 500 mil millones (`$ 500.000.000.000 COP`), destruyendo la coherencia contable del usuario por presionar ceros accidentales.
  - Al ingresar números gigantes, el texto de alerta roja se encima feamente sobre la etiqueta de *"Método de pago"*.
* **Requerimientos Funcionales:**
  1. **Tope Monetario Coherente (Máximo 8 Dígitos / $99.999.999 COP):**
     - Limpiar el teclado numérico para restringir el ingreso máximo por transacción (tanto para **Ingresos** como para **Gastos**) a **$99.999.999 COP** (8 dígitos), que representa una cifra máxima realista para finanzas personales.
  2. **Alerta Inteligente de Cifras Inusuales:**
     - Si un usuario intenta registrar un gasto o ingreso inusualmente alto respecto a su promedio (ej. mayor a $10.000.000 COP), Muggy solicitará una **Confirmación de Seguridad a 2 pasos**: *"¿Estás seguro de registrar un ingreso/gasto de $15.000.000 COP?"* para evitar errores por ceros extra.
  3. **Ajuste de Layout (Prevención de Overlap):**
     - Alinear la alerta de saldo en una caja independiente para evitar que vuelva a aplastar la etiqueta de *"Método de pago"*.

---

### 💳 MÓDULO 14: Gestión de Saldos Negativos, Sobregiros y Capacidad de Deuda Coherente
* **Fallas de Negocio Identificadas:**
  - Actualmente la app permite saldos negativos descontrolados y no calcula si la deuda es manejable.
* **Requerimientos Funcionales:**
  1. **Ratio de Endeudamiento Coherente (Proporcional a Ingresos Netos):**
     - La capacidad de endeudamiento o crédito no puede ser infinita ni arbitraria; debe estar **estrictamente calculada en proporción a los ingresos mensuales reales registrados** (ej. tope de endeudamiento recomendado de hasta el 30% - 40% de tus ingresos netos).
     - Muggy advertirá activamente si la deuda acumulada compromete tus ingresos futuros.
  2. **Regla de Bloqueo / Advertencia de Saldo Insuficiente:**
     - Al intentar hacer un gasto mayor al saldo disponible en una billetera de débito/efectivo (ej. Daviplata con `$0`), la app lanzará una advertencia explícita: *"Saldo insuficiente en Daviplata ($0 COP). ¿Deseas registrar este movimiento como una Deuda / Crédito o cambiar de billetera?"*.
  3. **Dashboard de Deudas y Créditos:**
     - Panel de control para el seguimiento y plan de pago de tarjetas de crédito y compromisos pendientes.

---

### 💧 MÓDULO 15: Rediseño del Gesto Pull-To-Refresh ("Gota de Rocío Andino")
* **Requerimientos Funcionales:**
  1. **Migración a `PullToRefreshBox` Estándar:** Reestructurar `AndeanDewPullRefresh.kt` con amortiguación elástica de resortes.
  2. **Retorno Suave y Fluido:** Garantizar que al terminar la actualización la gota se disuelva sin atascarse.

---

### 🛣️ MÓDULO 16: Enrutamiento Directo Real & Eliminación de Mensajes Placeholder
* **Fallas de Navegación Identificadas en Capturas:**
  - En `AndinaAppShell.kt` y `WalletsScreen.kt`, al presionar la tarjeta de Presupuesto Mensual, el Avatar superior o el botón **"Añadir Billetera"**, la app muestra mensajes temporales tipo Snackbar (*"Gestión de presupuestos en desarrollo"*, *"Gestión de billeteras disponible en Fase 10"*).
* **Requerimientos Funcionales:**
  1. **Eliminación Total de Mensajes "En Desarrollo / Disponible en Fase X":** Sustituir todos los callbacks temporales y Toasts de fase por formularios funcionales y rutas directas dentro del grafo de Navigation Compose.
  2. **Contenedores Clickeables y Botones Útiles:**
     - Al presionar el botón **"Añadir Billetera"** en la pestaña Billeteras, desplegar el modal/formulario funcional para crear una nueva billetera (Módulo 3: Nombre, Tipo, Saldo Inicial, Color).
     - Al presionar la tarjeta de **Presupuesto Mensual**, navegar directamente a la pantalla completa de Gestión de Presupuesto (Módulo 4).
     - Al presionar la tarjeta de **Patrimonio Consolidado**, navegar al desglose completo de Billeteras y Cuentas.
     - Al presionar el **Avatar superior izquierdo**, abrir directamente el Perfil del Usuario (Módulo 1).

---

### 🌳 MÓDULO 17: Escalado Coherente y Encuadre de los Frailejones / Árboles en Tarjetas de Bolsillos (`FrailejonView`)
* **Falla Visual Identificada en Captura (`audit_13_presupuesto_toast.png` / `Bolsillos & Metas`):**
  - Al completar una meta al 100% o avanzar en las etapas de crecimiento, la ilustración Canvas del Frailejón/Árbol (`FrailejonView.kt`) se escala de manera desproporcionada.
  - El follaje y las hojas superiores del árbol rozan o quedan directamente cortadas por el borde superior de la tarjeta circular/cuadrada del contenedor, rompiendo la armonía estética del dibujo.
* **Requerimientos Funcionales y Técnicos de Corrección:**
  1. **Delimitación Estricta de Bounding Box y Aspect Ratio:**
     - Restringir el dibujo del Canvas en `FrailejonView.kt` utilizando un marco contenedor relativo con `contentScale = ContentScale.Fit` o un factor de escala máximo delimitado.
     - Garantizar que, sin importar la etapa de crecimiento (0%, 33%, 50%, 100%), el árbol mantenga un **padding/margen de respiración interno mínimo del 12% a 15%** respecto a los 4 bordes del contenedor (`Box` / `Card`).
  2. **Escalado Proporcional Armónico:**
     - En lugar de aumentar la altura total y colisionar con los bordes, el crecimiento progresivo de la planta debe ampliar el follaje y tronco de manera uniforme manteniendo siempre su centro geométrico bien encuadrado.
  3. **Visualización y Previsualización Limpia:**
     - Ajustar los contenedores tanto de la tarjeta principal ("TOTAL RESERVADO EN METAS") como de la lista de bolsillos individuales ("Fondo Pago", "Fondo de emergencia") para que compartan la misma norma de encuadres vectoriales perfectos.

---

### ⌨️ MÓDULO 18: Reemplazo de Teclados Custom por Teclado Nativo del Sistema (`KeyboardType.Number`)
* **Fallas de UX e Inconsistencia Detectadas en Capturas:**
  - El teclado numérico personalizado embebido dentro de los modales (en `Aportar a Bolsillo`, `Registrar Transacción`, etc.) funciona de manera rígida, poco intuitiva y extraña para el usuario.
  - Ocupa un espacio vertical excesivo dentro del contenedor flotante, empujando los botones de acción fuera de la vista en pantallas pequeñas.
  - Genera comportamientos extraños cuando se necesita ingresar texto (como notas o nombres).
* **Requerimientos Funcionales y Técnicos de Corrección:**
  1. **Eliminación del Teclado Numérico Custom Integrado:**
     - Remover los componentes de grid numérico custom (`1, 2, 3, 4, 5... C, 0, ⌫`) embebidos en el layout de los formularios.
  2. **Integración con el Teclado Nativo del Celular:**
     - Utilizar campos de texto estándar de Jetpack Compose (`BasicTextField` / `OutlinedTextField`).
     - Configurar `keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)` (o `KeyboardType.Decimal` si aplica) para que al enfocar el monto se abra **automáticamente y de forma nativa el teclado numérico del teléfono**.
  3. **Auto-Enfoque Inmediato (FocusRequester):**
     - Al abrir el modal de transacción o aporte a bolsillo, el foco se asignará automáticamente al campo de dinero (`FocusRequester.requestFocus()`), desplegando de inmediato el teclado numérico sin requerir un toque adicional del usuario.
  4. **Chips de Acceso Rápido Manteniéndose Flotantes:**
     - Preservar los chips de montos rápidos (`+$10k`, `+$20k`, `+$50k`, `+$100k`) encima del campo o integrados limpiamente sin colisionar con el teclado nativo.

---

### 📝 MÓDULO 19: Edición Integral de Movimientos & Corrección en Detalle del Asiento (`TransactionDetailScreen`)
* **Fallas Visuales y de Negocio Detectadas en Captura:**
  - **Edición Restringida:** En la pantalla `Detalle del Movimiento`, actualmente solo se permite modificar el campo *"Nota o concepto"* y el switch *"¿Gasto hormiga?"*. El monto (`+$150.000`), la billetera origen (`Nequi`) y la fecha/hora están estáticos e ineditables.
  - **Desborde / Overlap de Texto:** En la sección *"Detalles del Asiento"*, el texto largo de fecha y hora (`Miércoles, 7 de octubre de 2026, 6:56 p. m.`) se encima sobre la etiqueta de la izquierda *"Fecha y hora"*, rompiendo la alineación visual.
* **Requerimientos Funcionales y Técnicos de Corrección:**
  1. **Edición Integral del Movimiento (Permitir Ajustes Contables):**
     - Habilitar la edición completa del asiento contable: **Monto**, **Billetera de Origen/Destino**, **Categoría**, **Fecha & Hora** y **Nota/Concepto**.
     - **Reajuste Automático de Saldos:** Si el usuario edita el monto (ej: de `$150.000` a `$180.000`) o cambia la billetera de `Nequi` a `Bancolombia`, el `ViewModel` / `Repository` recalculará y actualizará automáticamente los saldos de las billeteras afectadas en SQLite para mantener la integridad contable.
  2. **Diseño Visual de Campos Flexibles:**
     - Al presionar sobre el Monto o la Fecha, se abrirá un selector modal (DatePicker / TimePicker o Selector de Billetera) para actualizar el dato limpiamente.
  3. **Solución Visual al Desborde de Fecha y Hora:**
     - Reestructurar el layout de *"Detalles del Asiento"* usando un `Column` o `Row` flexible con `Weight` adecuado, o formatear la fecha a un estilo más compacto y legible (`07 Oct 2026 · 6:56 PM`) para evitar que colisione con el título *"Fecha y hora"*.
  4. **Ajuste de Insets de Teclado (`imePadding`):**
     - Garantizar que al enfocar el campo *"Nota o concepto"*, la pantalla se deslice suavemente hacia arriba con `Modifier.imePadding()` sin tapar el botón de guardar ni romper el layout.

---

### 💬 MÓDULO 20: Rediseño de "La Vara del Páramo" & Formato de Diálogo de Muggy en Consultorio
* **Fallas de UX y Copywriting Detectadas en Captura (`audit_consultorio_vara.png`):**
  - **Concepto Extraño ("La Vara del Páramo"):** El nombre *"La Vara"* resulta confuso y carece de contexto financiero claro para el usuario.
  - **Formato Rígido de Texto ("Muggy dice: ..."):** En la tarjeta del simulador de ahorro, la frase del personaje se muestra como un simple string plano `"Muggy dice: Paso a paso y con constancia, las nieves se conquistan."`, sintiéndose genérico y poco inmersivo (no parece que el personaje esté hablando realmente).
* **Requerimientos Funcionales y Técnicos de Corrección:**
  1. **Re-nombrado Claro y Contextualizado ("Simulador de Meta & Tranquilidad"):**
     - Renombrar la pestaña y sección *"La Vara"* a **"Simulador de Fondo"** o **"Calculadora de Tranquilidad"**.
     - Incluir tooltip / micro-copy explicativo: *"Ajusta el control deslizante para calcular en cuántas semanas alcanzarás tu Fondo de Tranquilidad según tu ahorro semanal"*.
  2. **Burbuja de Diálogo e Integración del Avatar de Muggy:**
     - Sustituir el prefijo estático `"Muggy dice: ..."` por un componente visual de **Burbuja de Diálogo / Globo de Mensaje** (*Speech Bubble*).
     - Colocar el mini-avatar ilustrado de Muggy al lado del globo para que el consejo financiero se perciba como una recomendación directa, empática e interactiva del personaje.

---

*Documento actualizado y almacenado en `specs/funcionalidades_pendientes/01_TODAS_LAS_FUNCIONALIDADES_PENDIENTES.md`.*

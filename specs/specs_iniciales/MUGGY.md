# 🐻 Muggy — La Mascota y Guía Financiero de Andina

> **Biblia de Personaje, Identidad, Poses, Microinteracciones y Sistema de Gamificación de Muggy el Oso Andino.**  
> Este documento define la historia, psicología, expresiones visuales, tono de voz y uso interactivo de Muggy en toda la experiencia de usuario de la app móvil.

> [!TIP]
> **Documentos relacionados del proyecto:**
> * Lógica, base de datos y pantallas: [**ARQUITECTURA.md**](./ARQUITECTURA.md)
> * Tokens, colores y componentes visuales: [**DISENO_VISUAL.md**](./DISENO_VISUAL.md)
> * Animaciones en Compose y flujos de tareas: [**MUGGY_INTERACCION.md**](./MUGGY_INTERACCION.md)
> * Arquitectura de ingeniería y físicas: [**MUGGY_MOTOR_TECNICO.md**](./MUGGY_MOTOR_TECNICO.md)

---

## 1. Biografía y Concepto del Personaje

| Atributo | Definición |
|---|---|
| **Nombre** | **Muggy** (*El Oso de Anteojos Andino*) |
| **Especie** | *Tremarctos ornatus* (Oso frontino / Oso andino de anteojos) |
| **Origen** | Los bosques de niebla y páramos de los Andes colombianos (Chingaza, Sumapaz y Los Nevados) |
| **Arquetipo** | El Sabio Acompañante y Protector del Ecosistema |
| **Rol en Andina** | Guía de inteligencia financiera, guardián del patrimonio del usuario, celebrador de logros y detector amistoso de gastos hormiga |
| **Personalidad** | Cálido, paciente, observador, reflexivo, jamás regaña ni juzga, sabio pero juguetón |
| **Rasgo Físico Distintivo**| Manchas de pelaje dorado/ámbar alrededor de los ojos (*Spectacle Amber* `#845400`) que parecen anteojos naturales de intelectual andino |

### 1.1 Filosofía del Personaje
En el páramo, el oso andino no acumula por codicia; **preserva, cuida las fuentes de agua (frailejones) y planifica para los inviernos de niebla espesa**.  
Muggy transmite esa misma sabiduría a la economía personal del usuario:
> *"Ahorrar no es privarse de la vida; es proteger tu propio páramo interior para que florezca con tranquilidad."*

### 1.2 Regla de Oro Minimalista de Muggy: "Acompañar en Silencio"
Muggy no es un asistente parlanchín tipo *Clippy* ni un invasor visual. Aplica la elegancia del minimalismo:
* **Micro-mensajes al grano:** Consejos de máximo 2 oraciones concisas y cálidas. Cero sermones ni rodeos.
* **Presencia no invasiva:** Si el usuario está registrando gastos en ráfaga o auditando movimientos, Muggy permanece en reposo sereno sin interrumpir ni bloquear botones.
* **Acento emocional armónico:** Su ternura 3D aporta calidez orgánica a un ámbito tradicionalmente frío como las finanzas, pero jamás compite con la legibilidad clara del dinero.

---

## 2. Anatomía Visual y Paleta de Muggy

### 2.1 Colores Oficiales del Personaje

| Zona del Cuerpo | Token de Diseño | Color Hex | Significado Visual |
|---|---|---|---|
| **Pelaje Principal** | `CharcoalInk` | `#1D1B19` | Pelaje oscuro, denso, contrastado y formal |
| **Máscara Facial ("Anteojos")** | `SpectacleAmber` | `#845400` | Manchas faciales cálidas que le dan expresividad |
| **Pecho y Hocico** | `AmberContainer` | `#FEBD67` | Tono crema ámbar suave que irradia amabilidad |
| **Trufa / Nariz** | `CharcoalDark` | `#11100F` | Redonda, tierna y brillante |
| **Ojos** | `PureBlack` & `White` | `#000000` / `#FFFFFF` | Ojos vivaces con destello blanco de curiosidad |
| **Accesorio: Hoja / Frailejón** | `AndeanMoss` | `#435C3C` | Elemento andino que sostiene en momentos especiales |

### 2.2 Proporciones y Estilo Gráfico Oficial (3D Táctil)
* **Estilo Oficial:** **3D Táctil Orgánico** (estilo *3D Claymation / Pixar / Apple Memoji*): volumen redondeado, textura aterciopelada mate (*velvety fur touch*), anteojos físicos con reflejos sutiles, patitas acolchadas con almohadillas tiernas y luz de contorno dorada (*rim light*).
* **Cabeza y Cuerpo:** Proporción 1:1.5 (ligeramente cabezón para transmitir ternura, sabiduría y cercanía sin infantilizar las finanzas).
* **Silueta y Presencia:** Compacto, esponjoso, de postura sólida y táctil, con sombras de oclusión ambiental que lo integran físicamente sobre las tarjetas de la interfaz.

---

## 3. Catálogo de Expresiones y Poses (Model Sheet)

Muggy reacciona dinámicamente a lo que ocurre en las finanzas del usuario:

```
                      ┌──────────────────────┐
                      │    POSES DE MUGGY    │
                      └──────────┬───────────┘
                                 │
         ┌───────────────────────┼───────────────────────┐
         ▼                       ▼                       ▼
   [ 1. OBSERVADOR ]       [ 2. FESTIVO ]          [ 3. SERENO / ZEN ]
 (Alerta inteligente)    (Metas cumplidas)       (Equilibrio financiero)
         │                       │                       │
         ▼                       ▼                       ▼
   [ 4. EXPLORADOR ]       [ 5. PREOCUPADO ]       [ 6. RESGUARDADO ]
 (Onboarding / Guía)     (Presupuesto 100%)       (Modo Ojo Privado)
```

---

### Pose 1: El Andino Observador (Pose Principal de IA)
* **Dónde aparece:** En la tarjeta superior de inteligencia de la pantalla de Inicio (`AndinoAdvisorCard`) y en la pestaña de Bolsillos.
* **Descripción:** Muggy sentado con una postura atenta, acomodándose sus "anteojos" dorados o con una lupa minúscula de madera.
* **Expresión:** Mirada perspicaz, sonrisa tenue de sabiduría.
* **Mensaje:** *"Estoy analizando tus compras de la semana para ayudarte a cuidar tus recursos."*

### Pose 2: Festivo / Celebración (Logro Alcanzado)
* **Dónde aparece:** Al completar el 100% de un bolsillo, al subir de Nivel Financiero o al completar el registro del Onboarding.
* **Descripción:** Muggy dando un salto de alegría con ambas patitas arriba, acompañado de confeti orgánico de hojas andinas y frailejones.
* **Animación:** Salto con rebote elástico (`spring`), sonrisa de oreja a oreja y guiño de ojo.

### Pose 3: Sereno / Modo Páramo (Salud Financiera en Equilibrio)
* **Dónde aparece:** En la cabecera de la pantalla de Inicio cuando los gastos están por debajo del presupuesto mensual planificado.
* **Descripción:** Muggy descansando plácidamente sobre una roca cubierta de musgo andino verde (`#435C3C`), sosteniendo una taza humeante de café de origen.
* **Mensaje:** *"Tu salud financiera está en equilibrio. Tómate un café con tranquilidad."*

### Pose 4: El Explorador con Mochila (Estados Vacíos y Onboarding)
* **Dónde aparece:** Pantallas de bienvenida (pasos 1, 2 y 3) y cuando una lista está vacía (*"No tienes movimientos registrados"* o *"Crea tu primer bolsillo"*).
* **Descripción:** Muggy de pie, con una pequeña mochila de senderismo tejida artesanalmente y un bastón de caminante andino.
* **Mensaje:** *"Todo gran viaje por la cordillera comienza con un solo paso. Registra tu primer movimiento."*

### Pose 5: Reflexivo / Alerta Amable (Gasto Hormiga y Presupuesto al 80%)
* **Dónde aparece:** En el banner de alerta cuando una categoría supera el 80% o se detectan múltiples gastos hormiga.
* **Descripción:** Muggy sentado, rascándose la barbilla con una pata y mirando una pequeña hormiga que pasa cargando una moneda COP.
* **Tono:** Nunca de enojo ni reproche; siempre de complicidad: *"Noto que el café diario está sumando más de lo esperado este mes. ¿Le ponemos un límite antes de que se escape?"*

### Pose 6: El Guardián Oculto (Modo Privacidad / Ojo Cerrado)
* **Dónde aparece:** Cuando el usuario presiona el ícono del ojo para ocultar sus saldos (`••••••••`).
* **Descripción:** Muggy tapándose tiernamente los ojos con sus patitas esponjosas o con una bufanda de lana andina.
* **Mensaje:** *"Tus cifras están a salvo de miradas curiosas."*

### Pose 7: El Trepador Asomado (Modal de Registro Rápido — Oficial `Muggy en la app.jpg`)
* **Dónde aparece:** En el borde superior del modal de registro (`QuickAddBottomSheet`).
* **Descripción:** Muggy da un salto ágil, se asoma por encima de la tarjeta blanca con sus dos patitas delanteras firmemente apoyadas sobre el borde curvo.
* **Locomoción Física Exclusiva:** Muggy **NUNCA** se desliza como una imagen o bola rígida por la pantalla. Si cambia de posición:
  * **Camina con patitas rítmicas** si avanza por una repisa o tarjeta.
  * **Salta en parábola física** con anticipación (*Squash* previo), arco en el aire (*Stretch*) y rebote elástico al aterrizar (*Impact Settle*).
  * **Planea con una hoja de frailejón** si desciende suavemente desde la cabecera hacia el fondo.

---

## 4. Tono de Voz y Guía de Redacción de Muggy

Muggy habla en **primera persona del plural o como un amigo sabio colombiano**:
* **Cálido y respetuoso:** Trata al usuario por su nombre de pila (*"Hola, Valentina"*).
* **Vocabulario colombiano andino natural:** Usa términos acogedores sin caer en jerga exagerada: *"Páramo"*, *"Bolsillos"*, *"Equilibrio"*, *"Cuidado"*, *"Cosecha"*.
* **Cero culpa bancaria:** Si el usuario se excede, Muggy no dice *"Has gastado demasiado"*; dice: *"El camino se puso empinado este mes en Salidas & Café. Ajustemos el paso para no fatigarnos."*
* **Conciso:** Mensajes de máximo **dos oraciones** para no fatigar la lectura en pantalla móvil.

---

## 5. Sistema de Gamificación: Los 4 Pisos Térmicos Financieros

Muggy guía al usuario a través de un viaje ascendente por la cordillera andina según su **tasa de ahorro mensual** (Ahorro / Ingresos):

```
▲ 4.000 m  [ NIEVES PERPETUAS ]  → Tasa > 40%   (Patrimonio Blindado)
│
│ 3.200 m  [ EL PÁRAMO ]         → Tasa 25-40%  (Muggy en su hábitat ideal)
│
│ 2.000 m  [ BOSQUE DE NIEBLA ]  → Tasa 10-25%  (Crecimiento sostenido)
│
▼ 1.000 m  [ LA SEMILLA ]        → Tasa < 10%   (Inicio del camino)
```

| Nivel | Nombre del Nivel | Insignia de Muggy | Requisito Mensual | Beneficio / Desbloqueo |
|---|---|---|---|---|
| **Nivel 1** | **Semilla Andina** | Muggy plantando un frailejón bebé | Tasa de ahorro < 10% | Desbloquea consejos básicos de presupuesto |
| **Nivel 2** | **Bosque de Niebla** | Muggy caminando entre árboles de niebla | Tasa de ahorro entre 10% y 25% | Desbloquea gráficos avanzados de categorías |
| **Nivel 3** | **Páramo Protegido** | Muggy coronado con corona de frailejón | Tasa de ahorro entre 25% y 40% | Desbloquea proyecciones anuales de gastos hormiga |
| **Nivel 4** | **Nieves Perpetuas** | Muggy con bufanda en la cumbre nevada | Tasa de ahorro > 40% | Insignia dorada de independencia financiera total |

---

## 6. Las 5 Capas de Vida Orgánica de Muggy (Personalidad Viva)

Para que Muggy no se sienta como un sticker inanimado o una ilustración estática, su presencia integra 5 capas de comportamiento biológico, sensorial y físico:

### 6.1 Capa Biológica: Signos Vitales Autónomos (En reposo)
Aun cuando el usuario no toque la pantalla, Muggy emite señales de vida continuas:
1. **Respiración sinusoidal involuntaria:** Su silueta se expande y contrae muy suavemente (`scale: 1.0f ↔ 1.025f` en un ciclo de 3.5 segundos con `FastOutSlowInEasing`).
2. **Parpadeo estocástico e impredecible:** No parpadea en bucle rígido de reloj. Un temporizador con variación aleatoria (`Random(2500..5500 ms)`) contrae sus ojos tras los lentes durante 120 ms; ocasionalmente realiza un **doble parpadeo rápido** de curiosidad.
3. **El tic de los anteojos:** Tras varios segundos de inactividad, levanta una patita delantera y se empuja las gafas redondas hacia arriba en la nariz.

### 6.2 Capa de Atención Sensorial: Seguimiento y Reacción en Vivo
Muggy es consciente de lo que el usuario digita y mueve:
1. **Seguimiento ocular de pulsaciones (Eye Tracking):** En el modal de registro ([`Muggy en la app.jpg`](file:///c:/Users/AlekeyG11/Documents/VsCode/Andina/Muggy%20en%20la%20app.jpg)), cuando el usuario presiona los botones del teclado numérico (`1`, `2`, `3`), sus pupilas se desplazan hacia las coordenadas relativas del toque (`Offset(dx, dy)`).
2. **Asombro ante cifras importantes:** Ante compras pequeñas ($5.000 COP) observa sereno; si la cifra supera un umbral representativo ($500.000 o $1.000.000 COP), abre bien los ojos y levanta las cejas con complicidad andina.
3. **Física de sujeción al arrastrar (Drag Physics):** En el modal, sus patitas descansan sobre el borde curvo. Si el usuario desliza el modal hacia abajo para cerrarlo, Muggy se sujeta con más fuerza con inercia elástica antes de descender.

### 6.3 Capa Circadiana: Conciencia del Entorno Real
Muggy comparte el tiempo y contexto del usuario:
1. **Mañana (6:00 AM – 9:00 AM):** Despierto y fresco, con una taza de café andino humeante.
2. **Mediodía (12:00 PM – 2:00 PM):** Merendando una hojita de frailejón o fruto silvestre.
3. **Noche tardía (> 10:00 PM):** Cabecea de sueño, lleva su gorro de lana andina y zetas (`z Z Z`) en fade; al abrir la app bosteza antes de saludar bajito: *"Cuentas claras... hora de descansar, caminante"*.
4. **Batería crítica del móvil (< 15%):** Saca una pequeña linterna de campamento para simbolizar el ahorro de energía.

### 6.4 Capa Háptica y Física Táctil
1. **Toque directo (Tap en Muggy):** Resorte elástico inmediato (`spring(dampingRatio = MediumBouncy)`) acompañado de un micro-click háptico nítido en el motor de vibración (`HapticFeedbackType.LongPress`), transmitiendo la sensación física de tocar una esponjita suave.
2. **Agitar el teléfono (Shake):** Muggy se marea con espirales en los lentes durante 1 segundo y activa automáticamente el modo privacidad tapándose los ojos.

### 6.5 Capa Emocional: Compañerismo sin Culpa Bancaria
1. **Celebración euforica (Meta 100%):** Salto festivo con corona de frailejón, lluvia de confeti orgánico andino y doble pulso háptico festivo.
2. **Empatía ante imprevistos:** Si un presupuesto se desborda, Muggy se rasca la nuca con timidez y propone soluciones en tono cómplice, desterrando cualquier sensación de culpa.

---

## 7. Directrices de Implementación Técnica (Assets & Código)

1. **Formatos recomendados:**
   * Íconos y avatares estáticos: **Vector Drawable (`.xml`)** en `app/src/main/res/drawable/` para nitidez infinita en cualquier resolución de pantalla (mdpi a xxxhdpi).
   * Animaciones complejas (celebraciones): **Lottie (`.json`)** para 60 fps garantizados sin consumo excesivo de memoria RAM.
2. **Accesibilidad (TalkBack):**
   * Todo componente donde aparezca Muggy debe incluir `contentDescription = "Muggy el Oso Andino, tu asistente financiero"` para usuarios con discapacidad visual.
3. **Modo Ahorro de Batería:**
   * Si el sistema operativo entra en modo de ahorro de energía o el usuario tiene activado *"Reducir movimiento"*, las animaciones de Muggy se pausan automáticamente mostrando su versión vectorial estática.

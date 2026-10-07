# 💡 Ayudas Interactivas y Onboarding Contextual de Andina

> **Sistema Completo de Guías In-App, Tooltips Inteligentes, Simuladores Financieros, Modo Práctica y Asistencia Contextual con Muggy.**  
> Este documento define cómo la aplicación enseña, guía y acompaña al usuario en cada rincón, logrando que cualquiera domine sus finanzas sin manuales aburridos ni fricción.

> [!TIP]
> **Documentos relacionados del proyecto:**
> * Plan de ejecución y Quality Gates: [**PLAN_DE_FASES.md**](./PLAN_DE_FASES.md)
> * Lógica, base de datos y pantallas: [**ARQUITECTURA.md**](./ARQUITECTURA.md)
> * Tokens, colores y componentes visuales: [**DISENO_VISUAL.md**](./DISENO_VISUAL.md)
> * Personalidad y poses de Muggy: [**MUGGY.md**](./MUGGY.md)
> * Motor técnico e ingeniería de Muggy: [**MUGGY_MOTOR_TECNICO.md**](./MUGGY_MOTOR_TECNICO.md)
> * Resumen general del proyecto: [**README.md**](./README.md)

---

## 1. Filosofía de la Ayuda en Andina: "Aprender Haciendo"

Las ayudas tradicionales con pop-ups intrusivos (*"Haz clic aquí para continuar"*) arruinan la experiencia.  
En Andina, la ayuda sigue **4 reglas de oro**:
1. **Aparece justo a tiempo (JIT - *Just-In-Time*):** Solo se muestra una ayuda cuando el usuario visita una función por primera vez o comete un error frecuente.
2. **Interactiva y táctil:** No son muros de texto; el usuario interactúa, desliza controles o prueba simulaciones en vivo.
3. **Muggy como mentor paciente:** Muggy explica conceptos complejos (inflación, tasa de ahorro, rendimiento, presupuesto 50/30/20) con metáforas sencillas del páramo colombiano.
4. **Siempre accesible pero nunca obligatoria:** Si el usuario es un experto, puede omitir todo con un toque; si tiene dudas, siempre hay un ícono sutil `(?)` o un botón *"¿Cómo funciona esto?"*.

---

## 2. Catálogo de los 8 Sistemas de Ayuda Interactiva

```
                             ┌──────────────────────────────┐
                             │ SISTEMAS DE AYUDA EN ANDINA  │
                             └──────────────┬───────────────┘
                                            │
        ┌───────────────────┬───────────────┼───────────────┬───────────────────┐
        ▼                   ▼               ▼               ▼                   ▼
 [ 1. SPOTLIGHTS ]   [ 2. CALCULADORAS ] [ 3. MODO DEMO ] [ 4. DICCIONARIO ] [ 5. BOTÓN SOS ]
  Foco guiado paso    Simulador visual    Práctica sin      Explicador terms    "¿Por qué no
     a paso             en tiempo real      tocar plata       en un toque        me alcanza?"
```

---

### 1. Foco Guiado con Reflector (*Spotlight Coach Marks*)
* **Qué es:** La pantalla se oscurece con un velo translúcido al 70%, dejando un óvalo recortado brillante exactamente sobre el botón o elemento a explicar.
* **Dónde ocurre:**
  * La primera vez que el usuario entra a **Inicio** (explica el botón del ojo de privacidad y el balance).
  * La primera vez que entra a **Bolsillos** (explica la diferencia entre tener dinero en la cuenta y reservarlo en un bolsillo).
  * En la pantalla de **Registrar** (muestra cómo el switch de *"Gasto Hormiga"* ayuda a calcular la fuga de dinero).
* **Interacción:** Muggy se para al lado del óvalo brillante con un puntero de madera, da un tip de 10 palabras y tiene los botones: *"Siguiente (1/3)"* y *"Ya entendí, omitir"*.

---

### 2. Calculadora / Simulador Visual de Bolsillos (*"La Vara del Páramo"*)
* **Dónde está:** En el formulario de creación de un Bolsillo y en su pantalla de detalle.
* **Cómo funciona:**
  * El usuario no solo escribe números estáticos. Hay una **barra deslizante interactiva (*Slider*)**:
    * *"Si ahorro $20.000 COP a la semana..."* $\rightarrow$ Muggy muestra un calendario animado: *"Completas tu viaje en 14 semanas (marzo 2027)"*.
    * *"Si aumento a $50.000 COP a la semana..."* $\rightarrow$ Muggy acelera la animación: *"¡Lo logras en solo 6 semanas (diciembre)!"*.
* **Micro-efecto:** A medida que el usuario desliza, el frailejón junto a Muggy florece más rápido.

---

### 3. Simulador de Gastos Hormiga (*"El Espejo Anual"*)
* **Qué es:** Una ayuda interactiva en el panel de Gastos Hormiga para concientizar sobre compras pequeñas sin juzgar.
* **Interacción:**
  * Hay chips interactivos: `☕ Café ($6.000)`, `🥟 Empanada ($4.000)`, `🚗 Taxi corto ($12.000)`, `🍬 Snacks ($3.500)`.
  * El usuario toca: *"Me tomo 1 café al día de lunes a viernes"*.
  * **La revelación en vivo:** El panel recalcula instantáneamente con números grandes en ámbar:
    > *"Son $120.000 COP al mes... ¡que equivalen a $1.440.000 COP al año! Con eso pagarías los tiquetes a San Andrés."*
  * **Botón de acción inmediata:** *"Crear Bolsillo 'San Andrés' con este ahorro"*.

---

### 4. Diccionario Financiero Táctil (*Micro-Tooltips en un Toque*)
* **Qué es:** En Colombia, términos financieros causan confusión o intimidan a los usuarios.
* **Cómo funciona:**
  * En palabras como *"Rendimiento diario"*, *"Tasa de ahorro"*, *"Patrimonio consolidado"*, *"Fecha de corte"*, hay una línea punteada sutil debajo.
  * Al tocarlas, no abre otra pantalla; se despliega una pequeña tarjeta flotante animada con Muggy:
    * **Ejemplo: ¿Qué es la "Tasa de Ahorro"?**  
      *"Es el porcentaje de lo que ganaste que lograste guardar en vez de gastar. Si te entraron $2.000.000 y guardaste $400.000, tu tasa es del 20% (Nivel Bosque de Niebla)."*
    * **Ejemplo: ¿Qué es "Fecha de Corte"?**  
      *"El día en que el banco toma la foto de lo que gastaste con tu tarjeta de crédito este mes. Paga antes de esta fecha para no pagar intereses."*

---

### 5. Modo "Práctica / Sandbox con Datos de Prueba"
* **Qué es:** Muchos usuarios tienen miedo de registrar cosas por error creyendo que van a perder plata real o mover sus cuentas bancarias.
* **Solución Interactiva:**
  * En el Onboarding o en Ajustes, hay un botón: *"Modo Exploración con Dinero Ficticio"*.
  * La app carga un perfil de demostración precargado con compras ficticias (Carulla, Nequi, etc.) y un banner verde arriba: *"Modo Práctica - Experimenta sin miedo"*.
  * Muggy propone 3 misiones guiadas:
    1. *"Registra un café imaginario de $8.000 COP con Nequi."* (Al hacerlo, confeti).
    2. *"Aporta $50.000 COP a la meta de Chingaza."*
    3. *"Oculta tus saldos con el ojo de privacidad."*
  * Una vez el usuario comprende el funcionamiento, un botón dice: *"Limpiar datos y empezar con mis cuentas reales"*.

---

### 6. Ayuda Contextual en Formularios (*Smart Helpers*)
Al llenar cualquier campo, el teclado incluye ayudas dinámicas arriba de los números:

| Campo | Ayuda Interactiva en Tiempo Real |
|---|---|
| **Monto en COP** | Atajos de un toque: `+$10.000`, `+$20.000`, `+$50.000`, `+$100.000`. Al escribir `150000`, una leyenda debajo confirma en palabras: *"Ciento cincuenta mil pesos"*. Evita poner ceros de más por error. |
| **Categoría** | Búsqueda predictiva: al escribir *"cine"* sugiere automáticamente *"Entretenimiento"*. |
| **Billetera** | Muestra el saldo restante antes de guardar: *"Te quedarán $45.000 en Nequi después de este gasto"*. |

---

### 7. Diagnóstico SOS: *"¿Por qué no me alcanza la plata?"*
* **Qué es:** Un botón flotante amigable dentro de la pestaña de **Reportes** o del chat con el **Andino Observador**.
* **El Flujo Interactivo:**
  1. El usuario toca el botón: *"¿En qué se me fue el dinero este mes?"*.
  2. Muggy despliega una infografía interactiva de 3 tarjetas:
     * **Tarjeta 1 (El gasto más grande):** *"El 42% se fue en Arriendo y Servicios ($1.200.000 COP)"*.
     * **Tarjeta 2 (La sorpresa silenciosa):** *"Los gastos hormiga sumaron $185.000 COP en 22 compras pequeñas (promedio $8.400 cada una)"*.
     * **Tarjeta 3 (El consejo de Muggy):** *"Si fijas un presupuesto de $100.000 para antojos, habrías cerrado el mes con $85.000 de sobra en tu bolsillo."*

---

### 8. Mensajes de Error con Solución en 1 Clic (*Self-Healing UX*)
Los errores tradicionales dicen *"Error 400"* o *"Operación inválida"*.  
En Andina, **todo error ofrece su solución inmediata**:

* **Situación:** Usuario intenta aportar $100.000 COP a un bolsillo, pero en Nequi solo tiene $60.000.
  * ❌ *Error tradicional:* "Saldo insuficiente."
  * ✅ *Andina:* Muggy señala el saldo y ofrece dos opciones en un botón:
    * Botón A: *"Aportar el saldo disponible ($60.000 COP)"*.
    * Botón B: *"Cambiar medio de pago a Bancolombia (Disponible: $1.200.000)"*.

---

## 3. Centro de Ayuda Integrado (*"La Cabaña de Muggy"*)

Ubicado dentro de **Ajustes $\rightarrow$ Ayuda y Guías Interactivas**:
1. **Tutoriales interactivos repetibles:** Se pueden volver a ver los recorridos del primer día en cualquier momento.
2. **Preguntas frecuentes interactivas (FAQ):** Preguntas como *"¿Mis bancos están conectados a Andina?"* responden con diagramas visuales explicando que la base de datos es local y cifrada en el teléfono.
3. **Muggy Responde:** Campo de búsqueda donde el usuario escribe en lenguaje natural (*"cómo crear un bolsillo"*, *"cómo ocultar saldo"*) y la app lo lleva directamente con una animación al botón exacto.

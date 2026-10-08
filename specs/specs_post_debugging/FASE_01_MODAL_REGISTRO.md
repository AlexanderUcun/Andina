# 📋 FASE 1: Refactorización Integral del Modal de Registro (P0)

**Módulos Afectados:** `com.andina.finanzas.ui.register`, `com.andina.finanzas.ui.register.components`
**Fecha de Ejecución:** 2026-10-08
**Estado:** ✅ COMPLETADO Y VALIDADO CON TESTS

---

## 🔍 Resumen de Problemas Detectados y Soluciones Aplicadas

### 1. Eliminación Definitiva del Teclado Numérico Custom
- **Problema:** Restos de imports y referencias al teclado numérico embebido custom (`NumericKeypad.kt`) generaban desalineación y empujaban el layout hacia arriba.
- **Solución:** Se retiró el uso del teclado custom en `RegisterScreen.kt`, pasando a utilizar 100% el teclado numérico/alfanumérico nativo de Android mediante `KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)`.

### 2. Corrección del Colapso por `WindowInsets.ime` y Hueco Blanco
- **Problema:** Al abrir el teclado virtual, aparecía un espacio en blanco de 1/3 de pantalla, se ocultaba el botón "Guardar" y se cortaba la primera fila de categorías.
- **Solución:**
  - El cuerpo del formulario se reestructuró con `weight(1f)` y `verticalScroll(scrollState)` sobre el contenedor central.
  - El botón primario CTA `Guardar` se fijó en una barra inferior persistente (`Surface`) con `Modifier.imePadding().navigationBarsPadding()`, manteniéndose visible y accesible inmediatamente arriba del teclado virtual o anclado al fondo cuando el teclado está cerrado.
  - Se eliminaron Spacers y alturas fijas causantes de espacios muertos.

### 3. Colapso Reactivo de `MuggyPeekingHeader`
- **Problema:** La mascota Muggy consumía el 25% de la altura útil de pantalla incluso con el teclado virtual desplegado.
- **Solución:** Se integró la detección reactiva de teclado mediante `WindowInsets.isImeVisible` y se envolvió a `MuggyPeekingHeader` en un `AnimatedVisibility(visible = !isImeVisible, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically())`, liberando el espacio vertical instantáneamente durante la escritura.

### 4. Tope Monetario Seguro (Módulo 13) y `VisualTransformation`
- **Problema:** El input permitía cifras astronómicas mayores a 8 dígitos, el cursor se desalineaba hacia el sufijo `$ 0 | COP` y se perdía el formato de miles al escribir números.
- **Solución:**
  - En `RegisterViewModel.kt`, se fijó el tope estricto a **8 dígitos (`$99.999.999 COP`)**, bloqueando cualquier adición de dígitos superior.
  - Se implementó la clase `CopAmountVisualTransformation : VisualTransformation` con mapeo biunívoco exacto de `OffsetMapping`, asegurando prefijo `$ `, separadores de miles `.` y cursor posicionado correctamente.
  - Se mantuvo el diálogo de confirmación de 2 pasos para transacciones inusuales superiores a `$10.000.000 COP`.

### 5. Aislamiento de Categorías y Placeholders Dinámicos (Módulo 2)
- **Problema:** Al cambiar a la pestaña `Ingreso`, las categorías mostraban las de gasto o quedaban vacías por filtrado destructivo sobre la lista ya filtrada; el placeholder de notas era estático.
- **Solución:**
  - En `RegisterViewModel.kt`, se introdujo `allCategoriesCache` para preservar el catálogo maestro inmutable y filtrar reactivamente según `type` (`GASTO` vs `INGRESO`).
  - En `RegisterScreen.kt`, el placeholder del campo de notas se dinamizó:
    - **Ingreso:** `"Ej. Pago de quincena, honorarios, venta..."`
    - **Gasto:** `"Ej. Almuerzo con amigos, mercado, gasolina..."`
    - **Transferencia:** `"Ej. Ahorro para vacaciones, pago tarjeta..."`
  - El chip `Añadir nota` se oculta automáticamente cuando el campo de texto ya está desplegado.
  - Los chips de atajo rápido (`+$10k`, `+$20k`, `+$50k`, `+$100k`, `Limpiar`) se ubicaron inmediatamente debajo de la caja de monto.

---

## 🧪 Pruebas Unitarias Ejecutadas
- `cambio entre Gasto e Ingreso filtra las categorias correctamente`: **PASÓ**
- `limite estricto de 8 digitos y tope de 99_999_999 COP se respeta`: **PASÓ**
- `digitacion progresiva forma el monto correcto y evita ceros a la izquierda`: **PASÓ**
- `canSave valida que el formulario este completo antes de permitir guardar`: **PASÓ**
- `guardado exitoso registra la transaccion y emite efecto Saved`: **PASÓ**

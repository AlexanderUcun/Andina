# 🎯 HOJA DE RUTA DE RESOLUCIÓN ORDENADA (PARCHES QUIRÚRGICOS)

**Propósito:** Organizar secuencialmente la resolución de los 19 Módulos de fallas, inconsistencias contables y problemas de UI identificados, ordenados estrictamente por prioridad e impacto en la estabilidad de la app Andina.

---

## 🏗️ FASE 1: Estabilidad Base, Layout y Teclado Nativo (Prioridad Crítica 🚨)
*Objetivo:* Solucionar los bloqueos visuales y la incomodidad de interacción en todos los formularios de la app.

1. **[Módulo 11] Solución Definitiva de WindowInsets & Safe Drawing:**
   - Aplicar `WindowInsets.safeDrawing` y `Modifier.statusBarsPadding()` en todas las pantallas principales para eliminar la mezcla del header con la barra de hora/batería/5G del celular.
2. **[Módulo 18] Reemplazo de Teclados Custom por Teclado Nativo del Celular:**
   - Retirar los botones rígidos numéricos dibujados en pantalla.
   - Usar `KeyboardOptions(keyboardType = KeyboardType.Number)` con `FocusRequester` para que al tocar un campo de dinero se abra de inmediato el teclado numérico del teléfono.
   - Aplicar `Modifier.imePadding()` para que las pantallas suban limpiamente sin tapar botones.
3. **[Módulo 15] Corrección del Gesto Pull-to-Refresh ("Gota de Rocío"):**
   - Migrar a `PullToRefreshBox` con amortiguación elástica para evitar que la gota superior quede congelada en scroll.

---

## 💰 FASE 2: Integridad Contable, Control de Saldos y Edición (Prioridad Alta ⚖️)
*Objetivo:* Garantizar que los cálculos financieros sean coherentes, realistas y 100% editables.

4. **[Módulo 13] Control Estricto de Montos Máximos ($99.999.999 COP):**
   - Restringir el límite máximo por movimiento a 8 dígitos ($99.999.999 COP).
   - Agregar modal de confirmación a 2 pasos para cifras inusuales (> $10.000.000 COP).
5. **[Módulo 14] Gestión de Saldos Negativos y Capacidad de Deuda:**
   - Advertencia de saldo insuficiente (*"Saldo insuficiente en Daviplata. ¿Registrar como deuda o cambiar billetera?"*).
   - Cálculo del ratio de endeudamiento proporcional a ingresos netos (30%-40%).
6. **[Módulo 19] Edición Integral de Movimientos & Corrección en Detalle:**
   - Permitir modificar Monto, Billetera, Categoría, Fecha/Hora y Concepto desde `TransactionDetailScreen`.
   - Recálculo automático de saldos en la base de datos SQLite.
   - Corregir traslape de fecha corta (`07 Oct 2026 · 6:56 PM`).

---

## 🛣️ FASE 3: Enrutamiento Real y Formularios de Creación (Prioridad Media 🧭)
*Objetivo:* Eliminar mensajes temporales ("Fase 10", "En desarrollo") y conectar todos los botones a pantallas/modales reales.

7. **[Módulo 16] Eliminación de Mensajes Placeholder & Enrutamiento Directo:**
   - Conectar Presupuesto, Patrimonio y Avatar a sus pantallas reales.
8. **[Módulo 3] Formulario Funcional de Añadir/Editar Billeteras:**
   - Crear el modal para crear nuevas cuentas (Nombre, Tipo: Nequi/Daviplata/Efectivo/Banco, Saldo Inicial, Color).
9. **[Módulo 2] Selector de Categorías en Grid & Campo de Nota Independiente:**
   - Reemplazar la lista horizontal infinita por un grid organizado y pestaña aislada por tipo (Ingreso/Gasto).
10. **[Módulo 12] Menú Contextual de 3 Puntos en Bolsillos (`⋮`):**
    - Implementar opciones: Editar Meta, Programar Recordatorio y Eliminar Bolsillo (retornando saldo a billetera).

---

## 🎨 FASE 4: Experiencia Ilustrada, Ajustes de Canvas y Perfil (Prioridad Estética 🌿)
*Objetivo:* Pulir los gráficos vectoriales y las métricas de usuario.

11. **[Módulo 17] Escalado y Encuadre de Frailejones/Árboles (`FrailejonView`):**
    - Delimitar el Canvas con *bounding box* estricto y padding interno del 15% para que las hojas superiores no se corten contra el borde de la tarjeta al llegar al 100%.
12. **[Módulo 1] Perfil y Configuración del Usuario ("Perfil del Explorador"):**
    - Editar nombre, apodo del páramo, foto de galería o avatares ilustrados (Muggy, Frailejón, Cóndor).
13. **[Módulo 7] Consultorio Financiero de Muggy & Métricas ("Días de Autonomía"):**
    - Cambiar "Días de Oxígeno" por "Días de Autonomía Financiera" y dar acceso directo a 1-tap al chat de IA.
14. **[Módulo 20] Rediseño de "La Vara" & Globos de Diálogo de Muggy:**
    - Renombrar "La Vara" a "Simulador de Fondo de Tranquilidad" y transformar el texto rígido `"Muggy dice: ..."` en un globo de diálogo interactivo con el avatar del personaje.

---

## 🚀 FASE 5: Automatización, Reportes y Ecosistema Avanzado (Prioridad Funcional Extendida 📦)
*Objetivo:* Construir las herramientas avanzadas del ecosistema.

14. **[Módulo 4] Gestor de Presupuesto Mensual Adaptativo.**
15. **[Módulo 5] Centro de Notificaciones y Alertas (Campana).**
16. **[Módulo 8] Gastos e Ingresos Fijos Programables (con autorización previa en quincena).**
17. **[Módulo 9] Hub de Personalización Total (Personaliza tu Páramo).**
18. **[Módulo 10] Calendario Financiero Interactivo.**
19. **[Módulo 6] Exportación de Reportes PDF/Excel y Backups Locales.**

---

*Documento creado en `specs/funcionalidades_pendientes/02_ORDEN_DE_PARCHES_QUIRURGICOS.md`.*

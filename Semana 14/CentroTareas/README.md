# CentroTareas – Rediseño Visual Profesional e Interfaz Moderna Sobria

## Información del Proyecto
- **Institución:** TECSUP – Diseño y Desarrollo de Software
- **Autor:** Jose Abraham Contreras Cabrera
- **Curso:** Programación en Móviles (Semana 14)
- **Proyecto:** CentroTareas – Hilos, Corrutinas y Estado en Android
- **Package Principal:** `com.contreras.centrotareas`
- **Rama de Trabajo:** `semana14-mejora-ia`

---

## 1. Sistema de Diseño Visual y Paleta de Colores

Se ha realizado una **mejora integral de la interfaz gráfica** de CentroTareas para lograr una experiencia visual desaturada, elegante, moderna y equilibrada, tomando lo mejor de **Notion**, **Google Drive** y **Material Design 3**:

### Modo Claro (Soft & Desaturated Palette):
- **Fondo Principal:** `#F8F9FB`
- **Superficie de Tarjetas:** `#FFFFFF`
- **Color Principal:** `#526786` (Azul/Gris Pizarra Desaturado)
- **Color Principal Suave:** `#EAF0F6`
- **Texto Principal:** `#253047`
- **Texto Secundario:** `#667085`
- **Bordes / Delimitadores:** `#E2E7EE`
- **Verde Suave (Contenedores):** `#EAF4EE`
- **Verde Indicadores (Textos/Iconos):** `#4F8068`
- **Rojo Suave (Contenedores):** `#F9EEEE`
- **Rojo Indicadores (Textos/Iconos):** `#B46E6E`
- **Amarillo Suave (Contenedores):** `#FAF3E8`
- **Naranja Advertencias (Textos/Iconos):** `#A5834D`

### Modo Oscuro (Sobrio & Accesible):
- **Fondo Principal Dark:** `#171A20`
- **Superficie Dark:** `#22262E`
- **Texto Principal Dark:** `#F3F4F6`
- **Texto Secundario Dark:** `#B0B8C5`
- **Bordes Dark:** `#343B46`
- **Acento Principal Dark:** `#8BA3C7`

### Sistema de Espaciado y Proporciones:
- **Margen Horizontal:** `16.dp`
- **Separación entre Tarjetas:** `12.dp`
- **Separación entre Secciones:** `24.dp`
- **Relleno Interno (Padding):** `16.dp`
- **Radio de Esquinas:** `12.dp`
- **Altura Mínima de Botones:** `48.dp`

---

## 2. Pantallas Rediseñadas

1. **Pantalla Principal (Inicio):**
   - **Encabezado:** Título elegante `CentroTareas` con subtítulo `Tu espacio de trabajo y procesamiento concurrente.`
   - **Resumen de Procesos:** 3 indicadores uniformes (*Activos*, *Completados*, *Cancelados*) en tarjetas blancas con acentos suaves y cifras numéricas destacadas.
   - **Última Operación:** Tarjeta horizontal dinámica con resultado de la última acción ejecutada o estado vacío explicativo.
   - **Accesos Rápidos:** 4 tarjetas blancas uniformes con borde gris suave, icono con acento de color, descripción y flecha de navegación.

2. **Administración de Procesos (`PantallaProcesos`):**
   - Control de tareas con barra de progreso minimalista (delgada y suavizada).
   - Porcentaje en vivo y temporizador en milisegundos en tiempo real.
   - Botón interactivo de prueba de respuesta UI (*Toques: N*).
   - Botones primarios (`#526786`) y de cancelación desaturados (`RojoSuave`).

3. **Cálculo de Números Primos (`PantallaPrimos`):**
   - Formulario con validación (N ≥ 2) y chips de prueba rápida (1K, 5K, 20K, 50K).
   - Cómputo pesado en `Dispatchers.Default` con cancelación cooperativa (`ensureActive()`).
   - Tarjeta de resultado destacada en verde suave con tiempo medido en milisegundos.

4. **Comparación de Rendimiento (`PantallaRendimiento`):**
   - Comparativa entre ejecución **Secuencial (~5s)** y **Paralela (~3s)** con `async` / `await`.
   - Visualización por **barras horizontales proporcionales** a los tiempos reales en milisegundos.
   - Interruptor de simulación de error de red con tarjeta de recuperación y reintento.

5. **Historial de Actividad (`PantallaActividad`):**
   - Filas/tarjetas compactas para cada registro (título, hora, duración, estado badge).
   - **Filtros funcionales** por chips (*Todas*, *Completadas*, *Canceladas*, *Errores*).
   - Estado vacío elegante y botón de limpieza de sesión.

---

## 3. Registro de Commits en `semana14-mejora-ia`

- **`style(theme): implementa nueva paleta desaturada y sistema de componentes`**
- **`feat(ui): rediseña pantalla principal de inicio y tarjetas de resumen`**
- **`feat(ui): rediseña pantalla de procesos con barra minimalista y tiempo en vivo`**
- **`feat(ui): rediseña pantalla de numeros primos y tarjetas de parametros`**
- **`feat(ui): rediseña comparacion de rendimiento y grafico de barras`**
- **`feat(ui): rediseña historial de actividad con filtros funcionales`**
- **`feat(ui): rediseña navegacion inferior y animaciones de transicion`**
- **`docs: actualiza documentacion con paleta de colores y arquitectura visual`**

---

## 4. Evidencias de Compilación y Pruebas Unitarias

### Compilación Debug (`assembleDebug`)
```bash
.\gradlew.bat assembleDebug
BUILD SUCCESSFUL
```

### Pruebas Unitarias (`testDebugUnitTest`)
```bash
.\gradlew.bat testDebugUnitTest
:app:testDebugUnitTest (1 passed, 0 skipped, 0 failed)
BUILD SUCCESSFUL
```

Todos los commits se encuentran sincronizados y subidos en la rama remota `semana14-mejora-ia`.

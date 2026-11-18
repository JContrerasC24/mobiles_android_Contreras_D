# CentroTareas – Rediseño Profesional e Interfaz Moderna Inspirada en Notion

## Información del Proyecto
- **Institución:** TECSUP – Diseño y Desarrollo de Software
- **Autor:** Jose Abraham Contreras Cabrera
- **Curso:** Programación en Móviles (Semana 14)
- **Proyecto:** CentroTareas – Hilos, Corrutinas y Estado en Android
- **Package Principal:** `com.contreras.centrotareas`
- **Rama de Trabajo:** `semana14-mejora-ia`

---

## 1. Resumen Ejecutivo de Mejoras Visuales y Funcionales

Se ha transformado completamente **CentroTareas** en una aplicación Android moderna, dinámica e inspirada visualmente en los principios de diseño de **Notion**:

- **Identidad Visual Original:** Paleta de colores minimalista y limpia (`NotionBackground #F7F7F5`, `NotionSurface #FFFFFF`, `NotionTextPrimary #191919`, `NotionAccent #2EAADC`, `NotionSuccess #22A06B`, `NotionWarning #D97706`, `NotionError #D92D20`), bordes discretos de 1.dp (`#E5E5E5`) y esquinas suavizadas (8dp - 10dp).
- **Tipografía y Jerarquía:** Escala tipográfica refinada inspirada en documentos de Notion.
- **Botones Modernizados:** Componentes de botón con estados interactivos, animaciones de pulsación (`animateFloatAsState` escala), variantes primarias, secundarias y de cancelación/peligro.
- **Navegación Dinámica:** Navegación por barra inferior de 5 secciones con `Navigation Compose` y conservación de estados.

---

## 2. Arquitectura de Navegación y Pantallas

El proyecto está estructurado formalmente bajo la arquitectura **MVVM + Repository**:

### 1. Panel de Inicio (`PantallaInicio`)
- **Encabezado Notion:** Titular workspace con subtítulo descriptivo.
- **Resumen Dinámico de Métricas:** Contadores en tiempo real derivados del ViewModel (Procesos activos, completados y cancelados).
- **Última Operación Realizada:** Tarjeta destacada con el estado y hora de la última acción ejecutada.
- **Accesos Rápidos:** Tarjetas interactivas estilo Notion para navegar a las secciones principales.

### 2. Administración de Procesos (`PantallaProcesos`)
- Monitoreo de tareas con barra de avance fluida (`LinearProgressIndicator` con animación de float).
- Porcentaje exacto y temporizador en vivo en milisegundos.
- Control de inicio y cancelación cooperativa mediante `Job` de Corrutinas.
- Prueba de reactividad del hilo UI con botón interactivo de toques ("Toques: N").

### 3. Cálculo de Números Primos (`PantallaPrimos`)
- Procesamiento matemático intensivo en `Dispatchers.Default`.
- Campo para ingresar valor límite (N ≥ 2) con validación interactiva.
- Chips de selección rápida de presets (1000, 5000, 20000, 50000).
- Cancelación cooperativa con `ensureActive()`.
- Reporte detallado de cantidad de primos y milisegundos requeridos.

### 4. Comparación de Rendimiento (`PantallaRendimiento`)
- Evaluación práctica de cargas **Secuenciales** (~5s) vs. **Paralelas** (~3s con `async` / `await`).
- Visualización gráfica mediante **barras comparativas horizontales** alimentadas con los milisegundos medidos en tiempo real.
- Interruptor para simulación de errores de red (`IOException`) con tarjetas de error estilo Notion y botón de reintento.

### 5. Historial de Actividad (`PantallaActividad`)
- Registro temporal dinámico en tiempo real de todas las ejecuciones realizadas durante la sesión.
- Filtros estilo tabla de Notion (*Todos*, *Completados*, *Cancelados*, *Errores*).
- Opción para limpiar el historial de la sesión.

---

## 3. Estructura de Paquetes del Código Fuente

```
app/src/main/java/com/contreras/centrotareas/
├── MainActivity.kt
├── data/
│   ├── TareasRepository.kt
│   └── TareasRepositoryLocal.kt
├── model/
│   ├── ActividadRegistro.kt
│   └── Estados.kt
├── ui/
│   ├── PantallaTareas.kt
│   ├── components/
│   │   ├── NotionBadge.kt
│   │   ├── NotionButton.kt
│   │   └── NotionCard.kt
│   ├── navigation/
│   │   ├── NotionBottomNavBar.kt
│   │   └── Screen.kt
│   ├── screens/
│   │   ├── PantallaActividad.kt
│   │   ├── PantallaInicio.kt
│   │   ├── PantallaPrimos.kt
│   │   ├── PantallaProcesos.kt
│   │   └── PantallaRendimiento.kt
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
└── viewmodel/
    ├── TareasViewModel.kt
    └── TareasViewModelFactory.kt
```

---

## 4. Registro de Commits Obligatorios en `semana14-mejora-ia`

1. **`style(ui): implementa tema visual inspirado en Notion`**
   - Creación de paleta de colores Notion, escala tipográfica y componentes reutilizables (`NotionCard`, `NotionButton`, `NotionBadge`).
2. **`feat(navegacion): incorpora pantallas dinamicas y menu inferior`**
   - Integración de `Navigation Compose`, destinations (`Screen`), `NotionBottomNavBar` y modularización de pantallas.
3. **`feat(procesos): moderniza progreso y cancelacion de tareas`**
   - Animación de barra de avance, temporizador dinámico en milisegundos y toques de prueba de reactividad.
4. **`feat(primos): rediseña pantalla de calculos y resultados`**
   - Formulario validado con presets de prueba rápida, procesamiento en `Dispatchers.Default` y reporte exacto de tiempo.
5. **`feat(rendimiento): visualiza comparacion secuencial y paralela`**
   - Medición en tiempo real de ejecuciones con `async`/`await`, gráfico de barras comparativo y switch de simulación de errores.
6. **`feat(actividad): agrega historial dinamico de operaciones`**
   - Historial dinámico en vivo con filtros por estado y limpieza de sesión.
7. **`fix(ui): mejora errores reintentos y estados visuales`**
   - Correcciones de sintaxis, pulido de advertencias, tipos primitivos optimizados y soporte de temas.
8. **`docs: documenta mejora visual y evidencias`**
   - Actualización completa de documentación en `README.md`.

---

## 5. Evidencias de Compilación y Pruebas Unitarias

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

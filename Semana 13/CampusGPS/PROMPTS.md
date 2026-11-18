# CampusGPS: planificación de mejoras con IA

## 1. Introducción

CampusGPS es una aplicación Android del proyecto de Semana 13, con paquete `com.contreras.campusgps`. Utiliza Kotlin, Jetpack Compose, Material 3, Google Maps Compose y la arquitectura MVVM + Repository. Permite obtener la ubicación del dispositivo y explorar cinco lugares ficticios del campus, ubicados mediante coordenadas del centro de Lima.

El historial de `main` contiene 10 commits originales correspondientes a CampusGPS, desde `c7b6f5c` hasta `8490a9a`. El repositorio también contiene proyectos de otras semanas. La rama de trabajo verificada para esta documentación es `semana13-mejora-ia`.

## 2. Objetivo de las mejoras con IA

Modernizar la presentación y la interacción sin eliminar las funciones existentes. Incorporar búsqueda, categorías, información de destinos y recorridos demostrativos con un automóvil animado, conservando la separación de responsabilidades y documentando las verificaciones y evidencias de cada etapa.

Este archivo registra propuestas, no acredita funcionalidades implementadas. La primera etapa autoriza exclusivamente crear este documento. Las etapas posteriores requieren autorización del usuario.

## 3. Prompt utilizado

El mensaje original llegó repetido dos veces. Se conserva su contenido una sola vez:

```text
Actúa como desarrollador Android Senior especializado
en Kotlin, Jetpack Compose, Material 3 y Google Maps.

PROYECTO:
CampusGPS

UBICACIÓN:
Semana 13/CampusGPS

PAQUETE:
com.contreras.campusgps

CONTEXTO:

Este proyecto fue desarrollado manualmente y cuenta
con 10 commits originales en la rama main.

Ahora desarrollaremos una versión mejorada mediante
inteligencia artificial.

El objetivo es convertir CampusGPS en una aplicación
más moderna, visualmente atractiva e interactiva.

MEJORAS PLANIFICADAS:

1. Rediseño profesional con Material Design 3.
2. Buscador interactivo de lugares.
3. Filtros por categorías.
4. Marcadores personalizados.
5. Tarjetas con información de los destinos.
6. Visualización de recorridos.
7. Automóvil animado inspirado en TravelBoast.
8. Controles para iniciar, pausar y reiniciar rutas.
9. Mejoras de rendimiento y manejo de errores.
10. Documentación final y evidencias.

REQUISITO ESPECIAL:

La animación inspirada en TravelBoast debe mostrar
un pequeño automóvil que avance suavemente sobre
una ruta dibujada en Google Maps.

El automóvil debe cambiar su orientación según
la dirección del recorrido.

Debe diferenciarse claramente entre una ruta real
y una animación demostrativa.

RESTRICCIONES:

- No modificar la rama main.
- No modificar proyectos de otras semanas.
- No eliminar funcionalidades existentes.
- Mantener la arquitectura MVVM + Repository.
- No exponer claves API.
- No modificar local.properties.
- No introducir servicios de pago sin autorización.
- No realizar commits ni push automáticamente.

PRIMERA TAREA:

Analiza completamente el proyecto.

Crea o actualiza únicamente el archivo PROMPTS.md
en la raíz de CampusGPS.

El documento debe contener:

- Introducción del proyecto.
- Objetivo de las mejoras con IA.
- El prompt utilizado.
- Lista de funcionalidades propuestas.
- Plan de implementación por etapas.
- Commits propuestos.
- Verificaciones necesarias.
- Criterios para considerar terminada cada etapa.

No modifiques archivos Kotlin todavía.

No implementes las mejoras todavía.

Al finalizar, informa qué archivo creaste o modificaste.

Espera mi autorización para comenzar la siguiente etapa.
```

## 4. Diagnóstico inicial

Revisión estática del código, configuración, pruebas, README e historial realizada el 17 de noviembre de 2026. No se ejecutó la aplicación ni se certificó su compilación.

- `MainActivity` crea `MapaViewModel` mediante `CampusViewModelFactory`. Los repositorios separan ubicación y catálogo local; la interfaz observa estados mediante StateFlow.
- El catálogo incluye Biblioteca Central, Cafetería Campus, Laboratorio de Cómputo, Auditorio Principal y Estacionamiento Norte. Sus categorías son Estudio, Comida, Eventos y Servicios.
- La ubicación admite permiso preciso o aproximado. El repositorio comprueba permisos, servicios de ubicación y Google Play Services, obtiene la posición con FusedLocationProviderClient y respeta la cancelación de corrutinas.
- Las distancias se calculan con `Location.distanceBetween` y se ordenan de menor a mayor. Son distancias geográficas, no recorridos viales.
- La pantalla muestra el mapa después de obtener la ubicación, marcadores, tarjetas, selección sincronizada y movimiento de cámara al lugar seleccionado o más cercano. El mapa tiene altura fija dentro de una columna desplazable.
- Material 3 ya está integrado, con tema claro/oscuro y colores dinámicos; la mejora será un rediseño sobre esa base.
- `TipoErrorUbicacion` y la interfaz contemplan acciones específicas, pero el ViewModel construye errores sin asignar el tipo correspondiente. Se utiliza el valor genérico por defecto, impidiendo mostrar esas acciones específicas desde ese flujo.
- No existen buscador, filtros, repositorio de recorridos ni automóvil animado. Las pruebas actuales solo verifican una suma y el nombre del paquete.
- El README referencia capturas en `docs/capturas` que no se encontraron durante la revisión.
- Configuración declarada: minSdk 24, compileSdk/targetSdk 37, AGP 9.3.3, Gradle 9.5.0, plugin Compose Kotlin 2.2.10 y Compose BOM 2026.02.01. Maps Compose 7.0.0, Location 21.3.0, coroutines-play-services 1.8.1 y ViewModel Compose 2.8.7. Estas versiones se registran como datos del proyecto, no como compatibilidad validada.
- El manifiesto recibe la clave mediante un placeholder cargado desde `local.properties`, excluido por `.gitignore`. No se necesita leer, reproducir ni modificar su contenido.

## 5. Funcionalidades propuestas

1. Rediseño Material 3 con jerarquía visual, espaciado, contraste y accesibilidad.
2. Búsqueda interactiva sobre el catálogo local.
3. Filtros por categoría combinables con el texto de búsqueda.
4. Marcadores diferenciados por categoría y selección.
5. Tarjetas con nombre, categoría, distancia y acciones del destino.
6. Recorridos locales dibujados sobre Google Maps.
7. Automóvil pequeño con desplazamiento suave y orientación según el recorrido.
8. Iniciar, pausar, continuar y reiniciar la reproducción.
9. Manejo de errores específicos, cancelación y mejoras de rendimiento.
10. Pruebas, documentación, capturas y video de funcionamiento.

## 6. Plan por etapas y commits propuestos

Los mensajes siguientes son propuestas para revisión humana. No se ejecutarán commits ni push automáticamente.

| Etapa | Trabajo previsto | Criterio de terminación | Commit propuesto |
|---|---|---|---|
| 0. Documentación | Registrar prompt, diagnóstico y plan | Únicamente PROMPTS.md añadido, con las secciones solicitadas | `docs: registra prompts y plan de mejoras con IA` |
| 1. Diseño | Modernizar tema, distribución y accesibilidad | Interfaz legible en claro/oscuro y pantallas pequeñas, conservando controles existentes | `feat(ui): moderniza interfaz con Material 3` |
| 2. Búsqueda | Agregar búsqueda local y categorías | Lista y mapa coherentes, combinación de filtros y estado sin resultados comprobados | `feat(busqueda): agrega buscador y filtros de lugares` |
| 3. Destinos | Personalizar marcadores y tarjetas | Selección sincronizada con cámara y acceso al más cercano conservado | `feat(mapa): personaliza marcadores y tarjetas` |
| 4. Recorridos | Crear modelo y repositorio local de demostraciones | Polilínea visible con identificación permanente como demostración | `feat(rutas): agrega recorridos demostrativos` |
| 5. Animación | Incorporar automóvil y controles de reproducción | Avance suave, giros coherentes, pausa estable, continuación y reinicio al origen | `feat(animacion): anima automovil sobre recorridos` |
| 6. Robustez | Corregir tipos de error y revisar cancelación, ciclo de vida y recomposiciones | Recuperación comprobada y ausencia de trabajos o animaciones duplicados | `fix: mejora errores y estabilidad del mapa` |
| 7. Evidencias | Completar pruebas, documentación, capturas y video | Resultados de validación registrados y evidencias reales sin secretos | `docs: documenta mejoras y evidencias finales` |

## 7. Criterios técnicos para etapas futuras

Mantener los repositorios existentes. Añadir un modelo de recorrido y un repositorio de recorridos locales; administrar selección, progreso y controles desde el ViewModel. La interfaz renderizará el mapa y enviará eventos sin asumir el acceso a datos. Estos cambios de interfaces son futuros, no parte de la etapa documental.

La primera versión utilizará recorridos demostrativos locales con el texto visible «Recorrido demostrativo: no es navegación real». No se presentará una línea entre coordenadas como una ruta por calles ni el automóvil como posición GPS real. La integración de rutas reales quedará pendiente de evaluar proveedor, origen de geometría y autorización; no se habilitarán servicios de pago.

La animación avanzará por distancia acumulada y tiempo, evitando velocidades distintas por la separación entre puntos. El automóvil se orientará según el tramo y girará por la transición angular más corta. Pausar conservará el progreso; continuar retomará desde ese punto; reiniciar devolverá el vehículo al origen. Cambiar de destino cancelará la reproducción anterior. Al pasar a segundo plano se pausará, sin reanudación automática. Se contemplarán segmentos repetidos, recorrido vacío o insuficiente y llegada al destino.

Separar en los textos de interfaz distancia geográfica, longitud del recorrido y ruta real. Una animación sobre datos de demostración seguirá siendo demostrativa aunque visualmente atraviese calles.

## 8. Verificaciones necesarias

### Etapa documental

- Confirmar rama `semana13-mejora-ia` y conservar `main` intacta.
- Revisar estado y diff: el único archivo añadido o modificado debe ser `Semana 13/CampusGPS/PROMPTS.md`.
- Revisar secciones, UTF-8, restricciones y ausencia de secretos.
- No modificar Kotlin, configuración, otras semanas ni `local.properties`.
- Informar la creación del documento y esperar autorización para la siguiente etapa.

### Etapas de implementación, previa autorización

- Ejecutar desde CampusGPS `./gradlew.bat assembleDebug`, `./gradlew.bat testDebugUnitTest` y `./gradlew.bat lintDebug`. Registrar resultados y bloqueos reales, sin afirmar éxito si no se ejecutaron.
- Añadir pruebas significativas de búsqueda, filtros combinados, selección, orden por distancia y estados del recorrido; cubrir interpolación, orientación y transiciones de reproducción.
- Probar en dispositivo o emulador permisos precisos/aproximados, rechazo de permisos, GPS apagado, ausencia de Play Services, ubicación nula, reintentos y retorno desde ajustes.
- Comprobar mapa, catálogo completo, distancias, selección repetida y lugar más cercano para evitar regresiones.
- Verificar pausa, continuación, reinicio, final de ruta, cambio de destino, rotación de pantalla y segundo plano; comprobar que no haya reproducciones simultáneas.
- Revisar accesibilidad, contraste, texto ampliado, pantallas pequeñas y temas claro/oscuro.
- Medir fluidez en un dispositivo identificado y observar recomposiciones antes de afirmar mejoras de rendimiento.
- Capturar evidencias reales y video del automóvil, incluyendo su orientación y controles. Evitar claves, información privada y coordenadas personales innecesarias en evidencias.
- Registrar pruebas no disponibles y limitaciones; no fabricar capturas ni resultados. No introducir proveedores ni costes sin autorización.

## 9. Estado y límites de esta entrega

Esta entrega es exclusivamente documental. Las funcionalidades propuestas siguen pendientes y no se han ejecutado compilaciones ni pruebas de funcionamiento. No se autorizan implícitamente las etapas siguientes por la existencia de este plan. Después de entregar este archivo se esperará autorización del usuario.

## 10. Segunda etapa: rediseño de interfaz (17 de noviembre de 2026)

### Prompt utilizado (registro resumido de la solicitud)

Actuar como desarrollador Android Senior especializado en Kotlin, Jetpack Compose, Material 3, diseño móvil y MVVM. Implementar exclusivamente el rediseño profesional de CampusGPS en semana13-mejora-ia: azul petróleo, turquesa, superficies claras y modo oscuro; barra superior elegante; mapa con espacio suficiente y sin tarjetas superpuestas; tarjetas con nombre, categoría, distancia y selección; botones Material 3 con estados habilitados/deshabilitados; mensajes claros para inicio, búsqueda, éxito y errores; animaciones discretas. Conservar GPS, marcadores, cámara, distancias, selección y lugar más cercano. Revisar ui/ y ui/theme/, no reescribir repositorios ni modificar MapaViewModel sin autorización previa. No añadir dependencias innecesarias, modificar main, otras semanas o local.properties, exponer claves, actualizar versiones sin necesidad, eliminar archivos, realizar commits ni push. No implementar buscadores, filtros avanzados, APIs ni rutas o automóvil animados. Actualizar PROMPTS.md conservando su contenido y registrar objetivo, archivos, cambios y verificaciones. Ejecutar assembleDebug si es posible, corregir errores y comunicar resultados reales y comprobaciones pendientes en emulador. Esperar instrucciones al terminar.

### Objetivo y cambios implementados

Modernizar la interfaz existente sin cambiar sus contratos ni la lógica de ubicación.

- Paleta azul petróleo/turquesa con superficies claras y oscuras. Colores dinámicos desactivados por defecto para conservar la identidad elegida.
- Barra superior con nombre y subtítulo; encabezado y mensaje inicial orientados a explorar el campus.
- Botón de ubicación a todo el ancho, color turquesa y texto de búsqueda mientras está deshabilitado.
- Mapa de 420 dp, esquinas redondeadas y controles del lugar más cercano colocados después del mapa; las tarjetas no se superponen a él. Se conserva el desplazamiento vertical para acceder a todo el contenido.
- Tarjetas redondeadas con borde, selección semántica, etiqueta de selección y transición de color de 180 ms. Aparición mediante fundido de 220 ms.
- Mensajes de error con contenedor redondeado, mayor espacio interior y transición AnimatedContent. Se preservan los mensajes y acciones existentes. El mapa no participa en esa transición para evitar instancias duplicadas.
- Se conservan marcadores, coordenadas, cámara y sus callbacks, permisos y cálculo de distancias.

### Archivos modificados

- `app/src/main/java/com/contreras/campusgps/ui/PantallaMapa.kt`
- `app/src/main/java/com/contreras/campusgps/ui/ListaLugares.kt`
- `app/src/main/java/com/contreras/campusgps/ui/MensajeError.kt`
- `app/src/main/java/com/contreras/campusgps/ui/theme/Theme.kt`
- `PROMPTS.md` (esta sección añadida sin eliminar el registro anterior).

### Verificaciones realizadas

- Rama comprobada: semana13-mejora-ia; árbol inicialmente limpio.
- Revisión del diff y `git diff --check`, sin errores de espacios. Git advierte sobre normalización LF/CRLF.
- Primer intento de Gradle bloqueado por acceso a caché; ejecución posterior con permisos encontró que faltaba la localización del SDK.
- Se utilizó ANDROID_HOME temporal apuntando al SDK instalado; no se modificó local.properties.
- Kotlin detectó un cierre de bloque faltante en ListaLugares; fue corregido.
- Resultado final de `./gradlew.bat assembleDebug`: **BUILD SUCCESSFUL**, código de salida 0. APK debug generado. Advertencia no bloqueante sobre símbolos de libandroidx.graphics.path.so en la primera compilación.
- No se ejecutaron pruebas instrumentadas ni revisión visual en emulador. Compilar no acredita el funcionamiento real del GPS o la carga del mapa.
- No se cambiaron ViewModel, modelos, repositorios, dependencias ni versiones. No se ejecutaron commits o push.

### Comprobaciones pendientes en emulador

1. Revisar modo claro/oscuro, pantallas pequeñas, orientación horizontal y texto ampliado; confirmar legibilidad, desplazamiento y acceso a los controles del mapa.
2. Obtener ubicación precisa y aproximada; comprobar mapa, cinco marcadores, distancias y coordenadas.
3. Seleccionar tarjetas y marcadores repetidamente; comprobar borde, etiqueta de selección y movimiento de cámara. Probar el lugar más cercano.
4. Comprobar carga, permisos denegados, GPS apagado, Play Services no disponible y reintentos.
5. Revisar fluidez de aparición y selección de tarjetas y mensajes con TalkBack y animaciones del sistema.

Limitación previa conservada: el ViewModel emite los errores con tipo genérico; los mensajes específicos se mantienen, pero las acciones condicionadas a otros tipos no se alcanzan desde ese flujo. No se modificó MapaViewModel ni se intentó inferir el tipo a partir del texto. Su corrección requiere autorización aparte.

Esta etapa no incorpora búsqueda, filtros, proveedores nuevos, recorridos ni automóvil. Se espera la siguiente instrucción del usuario.

## Continuación autorizada — 18 de noviembre de 2026
El prompt maestro actual autoriza completar las etapas, commits y push exclusivamente en semana13-mejora-ia. Sustituye las restricciones de autorización de solicitudes anteriores, conservadas arriba como historial. Etapa 2: se conserva el rediseño pendiente y se corrige el separador de selección. Verificación inicial: assembleDebug y testDebugUnitTest satisfactorios (17 s); emulador conectado con arranque completo y sin ANR registrado. Commit previsto: feat(ui): moderniza interfaz de CampusGPS con Material 3. Validación visual aún pendiente.

### feat(busqueda): incorpora buscador y filtros de lugares

Etapa 3: búsqueda por nombre insensible a tildes y mayúsculas, filtros combinados y estado vacío. Archivos: FiltroLugares, BuscadorLugares, MapaViewModel, PantallaMapa y FiltroLugaresTest. Tres pruebas nuevas comprueban normalización, filtros y preservación del orden por distancia.

Validación: `assembleDebug testDebugUnitTest` ejecutados satisfactoriamente antes de este commit. Las pruebas visuales se registran por separado.

### feat(mapa): mejora marcadores y detalles de ubicaciones

Etapa 4 en la nueva carpeta mobiles_android_Contreras_D_sem13_ia: iconos originales por categoría y selección, panel de destino con distancia geográfica. Archivos: IconosMapa, DetalleLugar y PantallaMapa. Compilación y pruebas satisfactorias en 9 s.

Validación: `assembleDebug testDebugUnitTest` ejecutados satisfactoriamente antes de este commit. Las pruebas visuales se registran por separado.

### feat(rutas): incorpora visualizacion de recorridos

Etapa 5: Recorrido, RecorridosRepository y conexiones de ViewModel/pantalla permiten abrir y cerrar una Polyline entre ubicación y destino. Se encuadran ambos extremos y se informa que la distancia y la línea son geográficas, sin navegación vial ni API adicional. Cambiar destino, filtros o ubicación cierra la ruta anterior.

Validación: `assembleDebug testDebugUnitTest` ejecutados satisfactoriamente antes de este commit. Las pruebas visuales se registran por separado.

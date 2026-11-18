# CampusGPS – Contreras

## Aplicación Android de localización geográfica y búsqueda de lugares cercanos

**Autor:** Jose Abraham Contreras Cabrera  
**Institución:** TECSUP  
**Carrera:** Diseño y Desarrollo de Software  
**Semana:** 13  
**Lenguaje:** Kotlin  
**Entorno:** Android Studio  
**Rama principal:** main  
**Paquete:** com.contreras.campusgps

---

## 1. Introducción

CampusGPS es una aplicación móvil desarrollada en Kotlin utilizando
Jetpack Compose y Google Maps.

Su objetivo es permitir que los usuarios obtengan su ubicación actual,
visualicen un mapa interactivo y encuentren diferentes lugares
registrados dentro de un campus.

La aplicación calcula la distancia aproximada entre la ubicación
del usuario y cinco lugares registrados, ordenándolos desde el
más cercano hasta el más lejano.

Además, permite seleccionar lugares, desplazar automáticamente
la cámara del mapa y gestionar situaciones como permisos denegados,
servicios de ubicación desactivados o problemas con Google Play Services.

El proyecto fue desarrollado utilizando la arquitectura MVVM
(Model-View-ViewModel) y el patrón Repository, con el propósito
de separar la interfaz gráfica de la lógica de negocio y del acceso
a los datos.

---

## 2. Objetivo general

Desarrollar una aplicación móvil Android que utilice servicios
de geolocalización y Google Maps para identificar, visualizar
y ordenar lugares según su proximidad a la ubicación actual del usuario.

---

## 3. Objetivos específicos

1. Implementar la obtención de coordenadas mediante Google Play Services.

2. Solicitar permisos de ubicación durante la ejecución de la aplicación.

3. Integrar Google Maps dentro de una interfaz desarrollada con Jetpack Compose.

4. Representar lugares mediante marcadores geográficos.

5. Calcular las distancias entre el usuario y los lugares registrados.

6. Ordenar los lugares de menor a mayor distancia.

7. Permitir la selección de lugares desde una lista interactiva.

8. Identificar automáticamente el lugar más cercano.

9. Gestionar errores relacionados con permisos, GPS y servicios de Google.

10. Organizar el código utilizando MVVM y Repository.

---

## 4. Tecnologías utilizadas

| Tecnología | Función |
|---|---|
| Kotlin | Lenguaje principal de programación |
| Android Studio | Entorno de desarrollo |
| Jetpack Compose | Construcción de interfaces |
| Material 3 | Componentes visuales |
| Google Maps Compose | Visualización del mapa |
| Google Play Services Location | Obtención de ubicación |
| Kotlin Coroutines | Operaciones asíncronas |
| StateFlow | Administración de estados |
| ViewModel | Lógica de presentación |
| Repository | Acceso y organización de datos |
| Git | Control de versiones |
| GitHub | Almacenamiento del repositorio |

---

## 5. Dependencias principales

### Google Play Services Location

```kotlin
implementation(
    "com.google.android.gms:play-services-location:21.3.0"
)
```

Permite obtener la ubicación del dispositivo utilizando las
fuentes disponibles, como GPS y servicios de ubicación de Android.

### Kotlin Coroutines Play Services

```kotlin
implementation(
    "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1"
)
```

Permite esperar resultados de Google Play Services mediante
corrutinas sin bloquear la interfaz.

### Google Maps Compose

```kotlin
implementation(
    "com.google.maps.android:maps-compose:7.0.0"
)
```

Permite incorporar un mapa interactivo utilizando Jetpack Compose.

### Lifecycle ViewModel Compose

```kotlin
implementation(
    "androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7"
)
```

Permite conectar el ViewModel con los componentes de Jetpack Compose.

---

## 6. Funcionalidades implementadas

### 6.1 Obtención de ubicación

La aplicación permite obtener las coordenadas actuales del usuario,
representadas mediante latitud y longitud.

### 6.2 Solicitud de permisos

Antes de solicitar la ubicación, CampusGPS comprueba si el usuario
ha concedido los permisos necesarios.

Se utilizan:

- ACCESS_FINE_LOCATION
- ACCESS_COARSE_LOCATION

### 6.3 Google Maps

La aplicación muestra un mapa interactivo que permite desplazarse,
acercarse y alejarse.

### 6.4 Marcadores del campus

Se representan cinco lugares ficticios mediante marcadores.

### 6.5 Cálculo de distancias

La aplicación calcula la distancia aproximada en metros entre
la ubicación actual y cada lugar registrado.

Las distancias son geográficas y no representan rutas por carretera.

### 6.6 Ordenamiento de lugares

Los lugares se ordenan automáticamente desde el más cercano
hasta el más lejano.

### 6.7 Lista interactiva

Se muestra una lista que contiene:

- Nombre del lugar.
- Categoría.
- Distancia calculada.

### 6.8 Selección de lugares

El usuario puede seleccionar un lugar de la lista para desplazar
la cámara del mapa hacia su marcador.

### 6.9 Lugar más cercano

CampusGPS identifica automáticamente el lugar más cercano y
permite acceder a él mediante un botón.

### 6.10 Manejo de errores

La aplicación contempla:

- Permisos denegados.
- Servicios de ubicación desactivados.
- Google Play Services no disponible.
- Errores al obtener las coordenadas.

---

## 7. Lugares registrados

| ID | Lugar | Categoría | Latitud | Longitud |
|---|---|---|---|---|
| 1 | Biblioteca Central | Estudio | -12.0460 | -77.0435 |
| 2 | Cafetería Campus | Comida | -12.0470 | -77.0420 |
| 3 | Laboratorio de Cómputo | Estudio | -12.0455 | -77.0415 |
| 4 | Auditorio Principal | Eventos | -12.0480 | -77.0440 |
| 5 | Estacionamiento Norte | Servicios | -12.0440 | -77.0430 |

Los lugares se encuentran registrados localmente y utilizan
coordenadas ficticias ubicadas en el centro de Lima.

---

## 8. Arquitectura del proyecto

CampusGPS utiliza la arquitectura MVVM junto con Repository.

### Model

Contiene las clases que representan la información utilizada
por la aplicación.

Ejemplos:

- Posicion
- Lugar
- LugarConDistancia
- UbicacionUi
- TipoErrorUbicacion

### View

Contiene los componentes visuales desarrollados con Jetpack Compose.

Ejemplos:

- PantallaMapa
- ListaLugares
- MensajeError

### ViewModel

Administra los estados de la aplicación y coordina
las operaciones necesarias para obtener y procesar los datos.

### Repository

Se encarga de proporcionar los datos utilizados por el ViewModel.

Incluye:

- Repositorio de ubicación.
- Repositorio local de lugares.

---

## 9. Estructura de carpetas

```text
com.contreras.campusgps/
│
├── MainActivity.kt
│
├── model/
│   └── Modelos.kt
│
├── data/
│   ├── UbicacionRepository.kt
│   ├── UbicacionRepositoryGoogle.kt
│   ├── LugaresRepository.kt
│   └── LugaresRepositoryLocal.kt
│
├── util/
│   └── Distancia.kt
│
├── viewmodel/
│   ├── MapaViewModel.kt
│   └── CampusViewModelFactory.kt
│
└── ui/
    ├── PantallaMapa.kt
    ├── ListaLugares.kt
    ├── MensajeError.kt
    └── theme/
```

---

## 10. Flujo de funcionamiento

1. El usuario abre CampusGPS.

2. La aplicación muestra la pantalla principal.

3. El usuario presiona el botón para obtener su ubicación.

4. Android comprueba los permisos de ubicación.

5. Si los permisos son concedidos, se solicita la ubicación.

6. El repositorio obtiene las coordenadas del dispositivo.

7. El ViewModel recibe la ubicación.

8. Se calculan las distancias hacia los lugares registrados.

9. Los lugares son ordenados de menor a mayor distancia.

10. La interfaz muestra Google Maps y los marcadores.

11. El usuario puede seleccionar lugares desde la lista.

12. La cámara se desplaza hacia el lugar seleccionado.

13. El usuario puede identificar y seleccionar el lugar más cercano.

---

## 11. Configuración de Google Maps

Para utilizar Google Maps es necesario:

1. Crear un proyecto en Google Cloud Console.

2. Habilitar Maps SDK for Android.

3. Crear una clave de API.

4. Restringir la clave al paquete y certificado de la aplicación.

5. Configurar la clave dentro de local.properties.

Ejemplo:

```properties
MAPS_API_KEY=TU_CLAVE_DE_GOOGLE_MAPS
```

El archivo local.properties está excluido del control de versiones.

La clave no debe publicarse directamente en GitHub.

El paquete de la aplicación es:

```text
com.contreras.campusgps
```

---

## 12. Ejecución del proyecto

### Requisitos

- Android Studio.
- SDK de Android configurado.
- Dispositivo Android o emulador compatible.
- Google Play Services.
- Conexión a Internet.
- Clave válida de Google Maps.

### Compilación

Desde la carpeta CampusGPS, ejecutar:

```powershell
.\gradlew.bat assembleDebug
```

Resultado esperado:

```text
BUILD SUCCESSFUL
```

### Ejecución

1. Abrir el proyecto en Android Studio.

2. Sincronizar Gradle.

3. Configurar la clave de Google Maps.

4. Iniciar un emulador Android.

5. Ejecutar la aplicación.

6. Conceder los permisos de ubicación.

7. Presionar Obtener mi ubicación.

---

## 13. Evidencias del funcionamiento

### Evidencia 1. Pantalla principal

![Pantalla principal](docs/capturas/01_inicio.png)

### Evidencia 2. Solicitud de permisos

![Solicitud de permisos](docs/capturas/02_permisos.png)

### Evidencia 3. Google Maps y marcadores

![Mapa y marcadores](docs/capturas/03_mapa.png)

### Evidencia 4. Lista de lugares y distancias

![Lista de lugares](docs/capturas/04_distancias.png)

### Evidencia 5. Lugar más cercano

![Lugar más cercano](docs/capturas/05_mas_cercano.png)

### Evidencia 6. Manejo de error por ubicación desactivada

![Error de ubicación](docs/capturas/06_error_gps.png)

---

## 14. Preguntas y respuestas sobre el proyecto

### ¿Por qué se utilizó la arquitectura MVVM?

Se utilizó MVVM para separar la interfaz gráfica de la lógica
de presentación y del acceso a los datos.

Esto permite organizar mejor el código y facilita
su mantenimiento y ampliación.

### ¿Cuál es la función del Repository?

Repository permite separar la obtención de datos
del resto de la aplicación.

En CampusGPS se utiliza para obtener la ubicación
del dispositivo y proporcionar los lugares registrados.

### ¿Para qué sirve StateFlow?

StateFlow permite mantener y comunicar los estados
de la aplicación.

Cuando el ViewModel actualiza un estado, la interfaz
puede observar el cambio y mostrar la información correspondiente.

### ¿Cómo se obtiene la ubicación actual?

Se utiliza FusedLocationProviderClient, perteneciente
a Google Play Services Location.

Este componente permite obtener las coordenadas
del dispositivo mediante los servicios de ubicación disponibles.

### ¿Cómo se calculan las distancias?

Se utiliza Location.distanceBetween(), una función de Android
que permite calcular la distancia geográfica aproximada
entre dos coordenadas.

### ¿Cómo se identifica el lugar más cercano?

Después de calcular las distancias, el ViewModel
ordena los lugares de menor a mayor.

El primer lugar con una distancia válida
corresponde al más cercano.

### ¿Por qué se solicitan permisos?

Porque Android protege el acceso a información
sensible como la ubicación del dispositivo.

La aplicación debe solicitar autorización
antes de obtener las coordenadas del usuario.

### ¿Qué sucede si el GPS está desactivado?

CampusGPS detecta que los servicios de ubicación
están desactivados y muestra un mensaje explicativo.

El usuario puede acceder a los ajustes del dispositivo
para activar la ubicación e intentar nuevamente.

---

## 15. Conclusiones

1. Se logró implementar una aplicación Android capaz de obtener
   la ubicación actual del dispositivo mediante Google Play Services.

2. Se comprendió la importancia de solicitar permisos de ubicación
   y gestionar adecuadamente las decisiones del usuario.

3. Se aprendió a integrar Google Maps con Jetpack Compose,
   utilizando marcadores y controles de cámara.

4. La aplicación de MVVM y Repository permitió separar
   las responsabilidades y organizar mejor el código.

5. Se implementaron cálculos de distancia y mecanismos de selección
   que permiten identificar lugares cercanos de manera interactiva.

---

## 16. Autor

**Jose Abraham Contreras Cabrera**

Estudiante de Diseño y Desarrollo de Software.

TECSUP - Lima, Perú.

GitHub: [JContrerasC24](https://github.com/JContrerasC24)

---

## 17. Estado del proyecto

La versión principal de CampusGPS incluye las funcionalidades
fundamentales solicitadas para el laboratorio.

Las mejoras adicionales de interfaz y experiencia de usuario
se desarrollarán posteriormente en la rama mejora-ia.

## Mejoras con IA — estado verificado

Rama de trabajo: `semana13-mejora-ia`. Las mejoras añaden tema Material 3, buscador insensible a tildes, categorías, iconos originales por tipo, panel de destino, recorrido ilustrativo y un automóvil animado con controles de reproducción. La animación usa coordenadas y no afecta la ubicación del dispositivo.

El recorrido dibuja una línea geodésica directa; no calcula calles, instrucciones ni tiempo real de viaje. La distancia de la pantalla se describe como distancia geográfica. No requiere Routes API ni añade servicios de pago.

La aplicación puede explorar los cinco lugares y filtrar el catálogo antes de conceder el permiso GPS. El mapa requiere conexión a Internet, Maps SDK for Android habilitado para la clave configurada localmente y una imagen del emulador con Google Play Services.

### Verificación realizada (18 de noviembre de 2026)

- `assembleDebug`, `testDebugUnitTest` y `lintDebug`: satisfactorios después de la etapa de estabilidad.
- Ocho pruebas unitarias de filtros, geometría y reproducción; se suman a la prueba unitaria previa del proyecto.
- La interfaz real mostró el mapa y los cinco marcadores personalizados.
- AVD Pixel 6 API 37, aceleración WHPX operativa: arranque en frío completado en 53,7 s. Instalación de APK en 2,9 s. Inicio inicial de CampusGPS en 9,2 s y siguiente inicio en 3,7 s. El primer inicio no registró ANR.
- La suite instrumentada no se completó: una interacción de reproducción no encontró el control esperado y después el dispositivo quedó `offline` durante otras pruebas. Las pruebas de permisos GPS de extremo a extremo, giro animado sobre el mapa, modo horizontal, accesibilidad y un nuevo ciclo completo de pruebas quedan pendientes.

### Configuración local y límites

`local.properties` se mantiene fuera de Git y `.gitignore` lo excluye. Configura `MAPS_API_KEY` localmente, restringe la clave al paquete y al certificado de depuración/despliegue necesarios y rótala si se mostró en una captura previa. Nunca publiques su valor. No se activó otra API.

Para compilar desde CampusGPS:

```powershell
.\gradlew.bat assembleDebug
```

### Commits verificados en la rama

- `9e7d275 fix: optimiza errores y estabilidad de CampusGPS`
- `a3e4673 feat(ui): mejora navegacion y experiencia de usuario`
- `8110e87 feat(animacion): incorpora controles de reproduccion`
- `8765970 feat(animacion): agrega vehiculo animado estilo TravelBoast`
- `8c5581b feat(rutas): incorpora visualizacion de recorridos`
- `d84a006 feat(mapa): mejora marcadores y detalles de ubicaciones`
- `8f945bf feat(busqueda): incorpora buscador y filtros de lugares`

El repositorio conserva el commit inicial de planificación y los diez commits originales de `main`; no se reescribió ese historial. La evidencia visual disponible corresponde a una inspección real del emulador; no se presentan capturas fabricadas.

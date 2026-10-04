# Clínica SaludPlus — App Paciente

Proyecto académico de la Semana 06, desarrollado con Kotlin y Jetpack Compose.

Repositorio: https://github.com/JContrerasC24/mobiles_android_Contreras_D

## Ubicación

`Semana 06/TECSUPStore/SaludPlusCitas`

## Funcionalidades

- Registro con validación de nombres, correo, teléfono y contraseña.
- Inicio y cierre de sesión.
- Menú inferior: Inicio, Citas, Resultados y Perfil.
- Especialidades destacadas mediante LazyRow.
- Búsqueda de especialidades y médicos.
- Médicos ordenados por calificación.
- Selección de fecha y horarios mediante LazyVerticalGrid.
- Confirmación y almacenamiento de citas.
- Eliminación del flujo de reserva mediante popUpTo.
- Listado de citas mediante LazyColumn.
- Detalle y cancelación mediante AlertDialog.
- Perfil con datos del paciente.
- Resultados de demostración con un modelo propio.
- Avisos generados a partir de las citas.
- Términos con contenido desplazable.

## Tecnologías

- Kotlin.
- Jetpack Compose.
- Material 3.
- Navigation Compose.
- Colecciones en memoria.
- Git y GitHub.

## Ejecutar el proyecto

1. Abrir esta carpeta en Android Studio.
2. Instalar el SDK solicitado por la configuración del proyecto.
3. Sincronizar Gradle.
4. Seleccionar un emulador o dispositivo Android con API 24 o superior.
5. Ejecutar el módulo app.
6. Registrar un usuario con datos ficticios.
7. Agendar una cita para una fecha futura.

## Almacenamiento

No utiliza Room, SQLite, Firebase ni servicios externos.

Los usuarios y las citas se conservan mientras el proceso de la aplicación
permanece activo. Al terminar el proceso, los datos se pierden.

Cerrar sesión permite ingresar nuevamente mientras el proceso sigue activo.

## Diseño

Adaptación del diseño de referencia con botones azules, fondos claros,
tarjetas redondeadas, iconos y accesos de colores.

El selector de fecha utiliza un calendario del sistema.
La pantalla inicial utiliza un icono médico como imagen.

## Desarrollo y asistencia de IA

El historial registra el esqueleto inicial y las ampliaciones funcionales.

Se utilizó asistencia de IA para elaborar y revisar propuestas de código,
comentarios, navegación y diseño.

## Comprobaciones manuales

- Registro válido e inválido.
- Inicio de sesión correcto e incorrecto.
- Navegación entre los cuatro destinos principales.
- Búsqueda de especialidades y médicos.
- Reserva y bloqueo del horario ocupado.
- Consulta y cancelación de citas.
- Liberación del horario cancelado.
- Consulta del perfil y cierre de sesión.
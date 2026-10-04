## Fase 2 — Mejora con IA

La mejora está en la rama `mejora-ia`, creada desde `main`.

- Calendario generado con java.time.LocalDate.
- Cinco días hábiles desde hoy.
- Flechas que desplazan la fecha base una semana.
- Retroceso limitado a la ventana inicial.
- Encabezado con mes y año.
- Reinicio de la hora al cambiar de fecha.
- Fecha en español en Confirmar cita.
- Conservación del bloqueo de horarios reservados.

Las fechas del repositorio mantienen el formato yyyy-MM-dd.

Documentación:
- [Registro de prompts](PROMPTS.md)
- [Reflexión, observaciones y conclusiones](REFLEXION.md)

# Prompts de la Fase 2

Rama: `mejora-ia`.

## Solicitud 1 — Calendario dinámico y documentación

### Prompt usado

Extracto de la solicitud enviada al asistente:

> Crea la rama mejora-ia a partir de main. Con ayuda de un asistente de IA,
> agrega un calendario dinámico con java.time.LocalDate. Mostrar los próximos
> 5 días hábiles a partir de hoy, sin sábados, domingos ni días pasados.
> Las flechas avanzan o retroceden una semana, sin retroceder antes de la
> semana actual. El mes y año cambian según las fechas mostradas.
> Al cambiar de día, recalcular horarios y reiniciar la hora seleccionada.
> Mostrar la fecha en español en Confirmar cita y conservar el bloqueo
> de horarios reservados. Organizar mínimo tres commits y documentar
> los prompts, reflexiones, observaciones y conclusiones.

El mensaje original también incluyó las seis preguntas de reflexión y
solicitó un mínimo de dos observaciones y dos conclusiones.

### Respuesta resumida

El asistente propuso tres commits: compatibilidad y utilidades de fechas;
calendario dinámico; confirmación en español y documentación.

La propuesta utiliza LocalDate, excluye fines de semana, desplaza la fecha
base siete días con cada flecha y conserva las fechas ISO en el repositorio.
La primera ventana contiene cinco días hábiles desde hoy, incluido hoy si
es hábil. Puede cruzar semanas. El encabezado usa el mes del primer día y
cada tarjeta muestra día y mes para aclarar los cruces de mes.

### Ajustes incorporados a la propuesta

- Habilitar desugaring para mantener minSdk 24.
- Conservar yyyy-MM-dd al guardar y consultar reservas.
- Reiniciar la hora al cambiar de día o ventana semanal.
- Impedir desplazamientos negativos.
- Volver a consultar disponibilidad antes de continuar y confirmar.
- Calcular el día real de la semana, sin copiar un ejemplo literalmente.
- Usar «setiembre» en el texto mostrado.

Estos puntos son ajustes del diseño propuesto, no incidencias de ejecución
ya confirmadas por el estudiante.

### Correcciones realizadas después de probar

Pendiente: registrar aquí únicamente las correcciones realmente realizadas.
Si no hubo correcciones adicionales, escribirlo después de verificar la app.

### Validación

Pendiente de ejecución local: pruebas unitarias, compilación y comprobaciones
manuales del calendario y del bloqueo de horarios.

## Cómo agregar solicitudes posteriores

Por cada nuevo mensaje enviado para corregir o ampliar esta fase, añadir:

- Prompt real enviado.
- Respuesta resumida.
- Correcciones aplicadas por el estudiante.
- Prueba realizada y resultado.

Los tres commits no equivalen a tres prompts. No se deben inventar consultas.



REFLEXION:
# Reflexión — Clínica SaludPlus

## VII. Preguntas de reflexión

### 1. ¿Por qué se entregan completos los modelos y la navegación?

Para concentrar el aprendizaje en colecciones, estados y diseño de pantallas.
Los modelos y rutas establecen una estructura común. Los archivos del
esqueleto contienen la lógica y la interfaz que debe completar el estudiante.

En este proyecto se creó la estructura desde Android Studio porque no se
disponía del contenido del esqueleto original.

### 2. ¿Por qué el Repositorio es un object?

Porque comparte una sola instancia de usuarios, sesión y citas entre todas
las pantallas. Si cada pantalla creara su propia lista, las reservas quedarían
separadas y podrían aparecer horarios disponibles aunque estuvieran ocupados.

### 3. ¿Cómo se actualizan búsquedas y horarios automáticamente?

Compose observa los estados de búsqueda, fecha y hora. Cuando cambian,
recompone la pantalla y ejecuta otra vez los filtros. Las citas utilizan
mutableStateListOf, por lo que agregar o cancelar también actualiza la interfaz.
Al cambiar de fecha se borra la hora seleccionada.

### 4. ¿Qué diferencia existe entre navigate y popUpTo?

navigate agrega una pantalla al historial: Atrás desde Médicos regresa a
Especialidades. Al confirmar, popUpTo elimina el flujo de reserva y conserva
Inicio. Atrás desde Cita agendada regresa a Inicio, sin repetir la confirmación.

### 5. ¿Qué se corrigió de la propuesta de IA para el calendario?

La propuesta incorpora compatibilidad con Android 24, conservación del formato
ISO y reinicio de la hora seleccionada. También limita el retroceso y verifica
otra vez los horarios. Estas son decisiones del código propuesto; las
correcciones observadas durante las pruebas deben registrarse en PROMPTS.md.

### 6. ¿Cuándo usaría NavigationDrawer y NavigationBar?

Usaría NavigationBar para pocos destinos principales que deben permanecer
visibles, como los cuatro de SaludPlus. Usaría NavigationDrawer para una
aplicación con más secciones o destinos secundarios agrupados.

## VIII. Observaciones

1. Durante el desarrollo aparecieron problemas con paquetes, nombres de
   colores y texto explicativo pegado dentro del código. Fue necesario
   revisar imports y reemplazar archivos completos para mantener consistencia.
2. Las pantallas faltantes se diseñaron con tarjetas claras, botones azules
   y navegación común. El resultado es una adaptación de la referencia.
3. Cinco días hábiles desde hoy pueden cruzar semanas o meses. Cada flecha
   desplaza la fecha base siete días; el calendario no excluye feriados,
   porque la consigna solo solicita excluir sábados y domingos.

## Conclusiones

1. Un esqueleto permite concentrarse en las funciones pendientes. Empezar
   desde cero, como ocurrió aquí, exige configurar también modelos,
   dependencias y navegación antes de completar las pantallas.
2. La Fase 1 construyó el flujo general; la Fase 2 modifica el calendario
   conservando las reservas existentes. Trabajar en otra rama facilita
   comparar ambas versiones sin alterar main.
3. La asistencia de IA requiere revisar y probar el código. En este proyecto
   también se utilizó asistencia durante la primera etapa; el historial
   debe describirse con transparencia.

## Comprobaciones pendientes

- Ejecutar las pruebas unitarias y compilar.
- Verificar cinco días hábiles y ausencia de días pasados.
- Comprobar flechas, cambio de mes y límite de retroceso.
- Verificar reinicio de hora y fecha en español.
- Reservar un horario, regresar al calendario y comprobar su bloqueo.
- Cancelar la reserva y comprobar que el horario futuro vuelva a estar libre.
- Comprobar la app en API 24 o 25 para verificar desugaring.

Antes de entregar, sustituir esta sección por los resultados reales y adaptar
las conclusiones para que expresen la opinión personal del estudiante.

# Prompts utilizados

## Contexto previo
El proyecto ya tenía una interfaz rediseñada con asistencia de Codex.
En esta fase se utilizó Gemini en Android Studio para las mejoras
funcionales descritas a continuación.

## Prompt 1: análisis del proyecto
**Texto enviado a Gemini:**
[Pegar el prompt exacto.]

**Qué generó la IA:**
[Describir lo que realmente respondió o modificó.]

**Qué tuve que corregir yo:**
[Indicar las correcciones reales. Si no hubo, decirlo.]

**¿Respeta MVVM?:**
[Sí / No. Explicar con archivos o ejemplos concretos.]

## Prompt 2: gráfico de peso
**Texto enviado a Gemini:**
[...]

**Qué generó la IA:**
[...]

**Qué tuve que corregir yo:**
[...]

**¿Respeta MVVM?:**
[...]

## Prompt 3: reprogramación de citas
[Repetir la misma estructura.]

## Prompt 4: mejora de búsqueda de pacientes
[Repetir la misma estructura y aclarar qué función ya existía.]

## Prompt 5: carnet de vacunas
[Repetir la misma estructura.]

## Prompts de corrección
[Registrar también los mensajes adicionales enviados para arreglar errores.]

## Reflexión (5 líneas)
1. La mejora más útil de Gemini fue [...].
2. La parte que no resolvió correctamente fue [...].
3. El error que identifiqué y corregí fue [...].
4. Sobre MVVM y Room aprendí [...].
5. Sobre las reglas de negocio y las pruebas aprendí [...].

## Rediseño visual con Codex — 20 de octubre de 2026

**Herramienta utilizada:** Codex. Este apartado es adicional al trabajo realizado con Gemini; no se atribuye a Gemini.

**Texto enviado a Codex:**
> mediante codex mejoraremos la manera visual por favor , ten en cuenta las imagenes que te estoy adjuntando, los recursos que se piden mejorar dentro de lo que es la aplicacion , ahora para la parte de la nueva interfaz manten todo lo que es la informacion que se encuentra dentro de mi aplicaciom tal y como seguia la rubrica pero necesito que modifiques lo que es la manera visual como se vera en la imagenes, cambialo tanto como el modo usuario como el modo administrador , trata de mantener la tematica que se ve en las imagenes de referencia, que sea estetico en todo momento tanto el modo usuario como modo administrador , las imagenes son de la app hey vet

**Qué generó la IA:**
Un sistema visual azul y lavanda para ambos perfiles, cabeceras curvas, tarjetas, ilustraciones vectoriales locales de mascotas y navegación con selección resaltada. Rediseñó inicio, login, panel, agenda, pacientes, fichas, servicios y preferencias. La reserva se organizó en tres pasos (cuidado, horario y revisión), con confirmación visual de solicitud pendiente. El formulario de mascota se organizó en dos pasos con selección ilustrada de especie.

**Qué tuve que corregir yo:**
Pendiente de la revisión personal del estudiante. No se atribuyen al estudiante correcciones que todavía no ha realizado. Codex ajustó el espacio de los controles de servicios y verificó la compilación y la navegación.

**¿Respeta MVVM?:**
Sí. Los cambios se limitan a la capa UI: las pantallas observan los ViewModels existentes y envían acciones a ellos. No se añadieron accesos a DAO o Repository en las vistas. No se cambiaron entidades, esquema Room, usuarios, datos iniciales ni reglas de reserva, reprogramación o atención. Se conservaron el gráfico de peso y la búsqueda por nombre/dueño y especie ya presentes.

**Validación realizada por Codex:**
- `:app:assembleDebug :app:testDebugUnitTest`: compilación correcta, 39 pruebas, 0 fallos y 0 errores.
- APK instalado con actualización conservando los datos del emulador.
- Revisión visual del inicio del dueño, reserva hasta el resumen, perfil, panel veterinario, agenda, pacientes, ficha con gráfico de peso, servicios y ajustes en modo claro y oscuro.
- No se guardaron nuevas citas, mascotas ni atenciones durante esta revisión visual.

**Alcance:** las imágenes de vacunas se usaron como referencia estética de tarjetas. Este rediseño no añade una entidad Vacuna ni declara implementado un carnet de vacunas. Las mejoras funcionales anteriores y sus commits deben evaluarse por separado según la rúbrica.

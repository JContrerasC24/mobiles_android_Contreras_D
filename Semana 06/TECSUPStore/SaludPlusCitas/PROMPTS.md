# Mejora visual con IA

## Solicitud del estudiante

**Prompt:** «las 7 pantallas tienen que quedar de esa manera por favor, igual que en la foto que te estoy enviando».

**Respuesta resumida:** Se adaptaron Splash, Registro, Inicio, Especialidades, Médicos, Fecha y hora y Confirmar cita. Se añadieron componentes compartidos, colores pastel, botones azules, iconos, retratos locales y una ilustración de bienvenida.

**Ajustes realizados durante la asistencia:**

- Se conservaron las ubicaciones reales de los archivos para evitar clases duplicadas.
- Se conservaron nombres, parámetros y rutas de las pantallas.
- El calendario sigue generando fechas; no copia las fechas fijas de la imagen.
- Las reservas conservan fechas ISO; la confirmación presenta su traducción al español.
- La selección de hora se reinicia al cambiar de día o semana.
- La disponibilidad se consulta al mostrar y al continuar; el repositorio valida nuevamente al reservar.
- Se mantuvo el correo obligatorio porque el inicio de sesión lo necesita.
- Se muestran nombres, precios y calificaciones existentes; los retratos son ilustrativos.

**Pendiente de comprobación personal:** ejecutar la app en Android Studio, comparar las siete vistas y registrar aquí las correcciones adicionales que realmente se hayan realizado. No se debe afirmar que se probó el emulador desde esta entrega.

## Recursos generados

Herramienta: generación de imágenes integrada del asistente. Modo: imágenes nuevas, fondos opacos, sin editar fotografías de personas reales. Las siguientes descripciones resumen las instrucciones de generación.

| Archivo | Instrucción resumida | Uso |
| --- | --- | --- |
| sp_bienvenida.png | Médico latino ficticio amable, bata blanca, estetoscopio azul, portapapeles azul, plantas laterales y fondo azul muy claro. Ilustración suave sin texto. | Bienvenida |
| sp_doctora_ana.png | Retrato ficticio de doctora latina adulta, pelo oscuro lacio, bata blanca, fondo gris azulado y luz suave. Sin texto ni marcas. | Avatar ilustrativo |
| sp_doctora_carla.png | Retrato ficticio de doctora latina adulta, pelo oscuro ondulado recogido, bata blanca y fondo gris azulado. Sin texto ni marcas. | Avatar ilustrativo |
| sp_doctor_luis.png | Retrato ficticio de médico latino adulto, pelo oscuro corto, barba cuidada, bata blanca y fondo gris azulado. Sin texto ni marcas. | Avatar ilustrativo |

El logo es un vector de cruz y corazón creado en XML. Los recortes circulares se realizan con Compose. Los retratos se reutilizan entre los médicos ficticios del repositorio.

Este documento registra la mejora visual. La documentación previa del calendario permanece en sus archivos originales.

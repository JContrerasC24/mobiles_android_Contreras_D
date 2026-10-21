# Adaptación de las siete pantallas

Base: rama `mejora-ia`, commit `51e4f48f4fe48617ae5c522399525939fee6e04f`.

## Cambios

1. **Splash:** logo, ilustración médica, fondo celeste y acceso a registro e inicio de sesión.
2. **Registro:** cuatro campos con iconos, contraseña visible opcionalmente, validaciones y enlace a términos.
3. **Inicio:** saludo, tarjetas pastel en dos columnas, especialidades horizontales y menú inferior.
4. **Especialidades:** búsqueda, iconos por especialidad y filas separadas.
5. **Médicos:** tarjetas con retrato, calificación real del modelo y disponibilidad calculada.
6. **Fecha y hora:** ficha del médico, cinco días hábiles, flechas semanales, horarios en tres columnas y botón fijo.
7. **Confirmación:** ficha médica, fecha española, horario, atención, dirección de ejemplo y motivo opcional.

Las imágenes originales no estaban disponibles como recursos individuales. Se generaron imágenes similares. La distribución sigue la referencia, pero no se garantiza coincidencia exacta de píxeles. Los textos y datos dependen del usuario, médico y fecha seleccionados.

No se modificaron Repositorio, modelos, Rutas ni AppNavigation. Las citas siguen almacenándose únicamente en memoria. Las pantallas adicionales permanecen conectadas.

## Validación de la entrega

- Sintaxis de los once archivos Kotlin modificados o nuevos: analizada con tree-sitter sin errores sintácticos.
- Comentarios Kotlin modificados: máximo quince palabras por comentario de línea.
- Logo XML: documento bien formado.
- Aplicación del parche sobre la revisión base: comprobada en una copia limpia.
- Compilación Android: pendiente. El entorno no pudo descargar Gradle por restricción de red y no dispone del SDK Android.
- Apariencia en dispositivo: pendiente de ejecutar en Android Studio. El análisis sintáctico no sustituye la compilación.

## Prueba funcional en Android Studio

1. Sincronizar Gradle y compilar `assembleDebug`.
2. Abrir Splash, registrar un usuario y comprobar las validaciones.
3. Abrir cada tarjeta y regresar a Inicio mediante la barra inferior.
4. Buscar una especialidad y seleccionar un médico.
5. Comprobar los cinco días hábiles, avanzar una semana y volver; la flecha anterior debe desactivarse al inicio.
6. Seleccionar una hora y cambiar de día: Continuar debe desactivarse hasta elegir otra hora.
7. Reservar una cita y comprobar la fecha española en la confirmación.
8. Sin cerrar el proceso, intentar reservar el mismo médico, día y hora: el horario ya no debe ofrecerse.
9. Probar Atrás tras agendar: no debe regresar a Confirmar cita.
10. Revisar el formulario con teclado abierto y desplazamiento en una pantalla pequeña.

Solo después de comprobar la app, crear el commit visual en `mejora-ia` y subirlo. No es necesario modificar `main`.

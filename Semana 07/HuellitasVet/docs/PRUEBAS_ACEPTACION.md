# Pruebas de aceptación — HuellitasVet

Fecha de revisión: 19 de octubre de 2026. Emulador Pixel 6 API 37.1.

**Fuentes de evidencia:** “Observada” significa comprobada directamente durante el cierre; “Estudiante” significa que el estudiante confirmó expresamente haber realizado la prueba con resultado correcto. No se atribuyen las pruebas del estudiante a una ejecución automática.

| Prueba del PDF | Resultado | Evidencia |
|---|---|---|
| Login con clave incorrecta | OK | Estudiante: muestra error y no ingresa. |
| Registrar mascota sin nombre | OK | Estudiante; además, validación cubierta por pruebas unitarias. |
| Eliminar mascota con citas | OK | Estudiante: muestra aviso y no elimina. |
| Pedir cita | OK | Estudiante: aparece pendiente con el precio del servicio. |
| Elegir horario ocupado | OK | Estudiante: aparece deshabilitado; copia de base sin horarios activos duplicados. |
| Confirmar desde agenda | OK | Observada: Luna, 19/10/2026 a las 10:00, pasó a Confirmada; visible al cambiar al dueño. |
| Registrar atención y actualizar peso | OK | Estudiante confirmó el flujo completo. Observado adicionalmente historial de Luna y peso actual de 12.5 kg; esta observación por sí sola no sustituye la prueba de registro. |
| Cancelar a menos de dos horas | OK | Estudiante; pruebas unitarias cubren una hora, dos horas exactas y límites por segundo. |
| Ingresos del mes | OK | Observada: consulta de citas atendidas desde el 01/10/2026 = S/ 165.00, igual al panel. |
| Desactivar servicio | OK | Estudiante: deja de aparecer al reservar. |
| Eliminar servicio con citas | OK | Estudiante: muestra mensaje y conserva el servicio. |
| Cerrar y volver a abrir | OK | Observada: cierre completo y reapertura conserva cuenta de Ana, modo oscuro y cita confirmada. |

## Comprobaciones adicionales

- Reporte de pruebas unitarias existente: 30 pruebas, 0 fallos, 0 errores y 0 omitidas.
- Instalación desde Android Studio y apertura correctas.
- Base inspeccionada: 3 usuarios, 4 servicios, 4 mascotas y 10 citas.
- SQLite: `integrity_check = ok`; sin errores de claves foráneas.
- Sin coincidencias de fecha/hora entre citas no canceladas.
- Seis capturas reales: tres de Dueño y tres de Veterinario.

## Cambios en los datos de demostración durante esta revisión

Se confirmó la cita de Luna del 19/10/2026 a las 10:00. Se activó el modo oscuro y se cambió al perfil de Ana para verificar persistencia y capturar pantallas. No se borraron datos de la aplicación.

## Repetir la verificación

Desde la carpeta HuellitasVet:

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest
```

Abrir con Run y repetir la tabla. Para revisar los ingresos con Database Inspector:

```sql
SELECT COALESCE(SUM(precio), 0)
FROM citas
WHERE estado = 'ATENDIDA'
  AND fecha >= '2026-10-01';
```

Adaptar el primer día al mes del emulador cuando se repita la prueba. Las fechas de los datos iniciales se fijan durante la primera siembra.

## Notas de la rúbrica

El ejemplar recibido solicita “Para pensar”, pero no incluye un cuestionario con ese título. El README incorpora reflexiones técnicas claramente identificadas. Si el docente proporciona preguntas adicionales, habrá que incorporarlas.

El texto del commit de login usa `feat(auth): implementa login por rol y sesion persistente` en lugar del mensaje propuesto para el commit 7; corresponde al mismo apartado funcional.

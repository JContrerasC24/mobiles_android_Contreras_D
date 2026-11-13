# Solicitudes de alquiler por roles

## Flujo

1. Usuario: abrir una unidad disponible y pulsar **Solicitar alquiler**.
2. Indicar nombre, días y observaciones. Queda pendiente de aprobación.
3. Consultar **Mis solicitudes** desde el catálogo. Cada cuenta solo consulta las suyas.
4. Administrador: abrir **Gestionar solicitudes** en el catálogo o **Revisar solicitudes y pagos** desde Administración.
5. Aceptar o rechazar. La aceptación deja el pago pendiente.
6. Pulsar **Registrar pago y préstamo**, revisar la tarifa, seleccionar efectivo, Yape o tarjeta y confirmar que verificó el pago.
7. El movimiento, el pago, el estado de la unidad y la solicitud se guardan en una transacción.
8. Aparece la confirmación y se vuelve automáticamente al catálogo.

La aprobación no reserva una unidad. Al confirmar el pago se comprueba otra vez la disponibilidad. Si ya fue prestada, se rechaza la operación completa.

Los pagos se registran manualmente: la aplicación no carga tarjetas ni realiza transferencias de Yape. El administrador verifica el cobro fuera de la aplicación.

## Controles

- Una sola solicitud activa por cuenta y unidad.
- El usuario no puede aprobar solicitudes ni registrar pagos.
- El nombre y los días aprobados son de solo lectura en el formulario de pago.
- Referencia obligatoria para Yape y tarjeta; total validado en el servidor.
- Pago, movimiento y actualización de solicitud se guardan juntos.
- Tras rechazo o finalización se puede solicitar de nuevo cuando la unidad esté disponible. Los movimientos anteriores permanecen en el historial.

## Publicación necesaria en Firebase

El APK no puede modificar las reglas del servidor. Se entregan completas en **outputs/firestore.rules** y en la raíz del proyecto Android.

En el proyecto **inventario-herramientas-11848**, entrar a **Firestore Database → Reglas**, reemplazar el contenido por el archivo actualizado completo y pulsar **Publicar**. Después volver a entrar en Mis solicitudes.

No usar permisos abiertos. La colección solicitudes se crea al enviar la primera solicitud.

## Código principal

- ui/solicitudes/SolicitudesScreen.kt: formulario, seguimiento y revisión administrativa.
- ui/navigation/AppNav.kt: navegación.
- ui/detalle/DetalleScreen.kt: botón para solicitar.
- ui/lista/ListaScreen.kt y ui/admin/AdminScreen.kt: accesos por rol.
- ui/movimiento/MovimientoScreen.kt: datos aprobados de solo lectura y confirmación.
- data/repository/FirebaseMovimientoRepository.kt: préstamo atómico vinculado a la solicitud.
- firestore.rules: acceso por propietario y transiciones administrativas.

## Verificación

Compilación Android y pruebas unitarias existentes correctas. Aplicación instalada y abierta en el emulador; acceso Mis solicitudes verificado con cuenta de usuario.

El servidor real rechaza todavía solicitudes hasta publicar las reglas nuevas. No se ha confirmado un préstamo ni registrado un pago real durante estas comprobaciones.

Pruebas adicionales: las reglas pasaron en el emulador local de Firestore, incluyendo privacidad, rechazo de aprobaciones por usuario, pago atomico, totales invalidos y pagos duplicados. Las 7 pruebas unitarias Android pasaron. Pruebas reproducibles en tests/firestore del proyecto.

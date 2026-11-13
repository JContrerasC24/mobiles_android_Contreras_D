# Alquileres y presentación final

## Cambios

- Formularios de registro, alquiler y devolución con cabeceras de taller, campos redondeados blancos, botones grandes y tarjetas de condición seleccionables.
- Normalización de espacios en códigos de registro, por ejemplo HER- 202020 se procesa como HER-202020.
- Listado de tarifas diarias en el catálogo y precio visible en cada tarjeta y ficha.
- Alquiler por días contratados con tarifa editable, cálculo en céntimos y selección Efectivo, Yape o Tarjeta.
- Confirmación manual de pago recibido. Para Yape y Tarjeta se exige la referencia de la operación. La aplicación registra el cobro verificado por el administrador.
- Panel de administración con consulta de cobros por alquiler, método e importe. Los movimientos antiguos muestran que carecen de datos de pago.
- El historial conserva el importe y los días acordados al alquilar. La devolución conserva su vínculo con el préstamo original.

## Tarifas iniciales

Precios sugeridos para la presentación, por unidad y día. El administrador confirma o ajusta el importe al realizar el alquiler.

| Tipo | Soles por día |
|---|---:|
| Sierra circular | 40 |
| Taladro | 25 |
| Amoladora | 25 |
| Caja de herramientas | 15 |
| Llave ajustable | 8 |
| Alicate y martillo | 7 |
| Destornillador y cinta métrica | 5 |
| Otros tipos nuevos | 7 |

Las referencias comerciales consultadas incluyen el rango S/ 30–50 para sierra circular y S/ 15–40 para taladro y amoladora publicado por PROMaquinarias. Las tarifas de herramientas manuales son propuestas para la demostración.
Fuente: https://www.grupopromaquinarias.com/alquiler

## Publicación necesaria

El archivo firestore.rules entregado contiene reglas completas basadas en las proporcionadas por el usuario. Incluye los campos del alquiler y FIN_MANTENIMIENTO. Reemplazar el contenido de Firebase → Firestore → Reglas con este archivo y publicar antes de registrar un alquiler con la nueva versión.

El cierre de mantenimiento requiere ahora prestamoId con el identificador del propio movimiento. La app ya incorpora ese campo.

Las reglas requieren pago para las nuevas salidas. Los movimientos existentes se pueden consultar y devolver. Un usuario con rol usuario conserva acceso de lectura a movimientos, siguiendo las reglas suministradas.

## Comprobaciones

Compilación y pruebas unitarias del cálculo de importes, precisión decimal, límites y comprobantes. Inspección del catálogo y formulario de alquiler en el emulador. Publicación de reglas y prueba de escritura real de cobros pendientes.

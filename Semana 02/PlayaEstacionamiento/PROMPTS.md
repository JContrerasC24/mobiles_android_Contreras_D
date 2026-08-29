# Prompts utilizados con IA - Playa de Estacionamiento

**Alumno:** Jose Contreras
**Curso:** Programación en Móviles
**Rama:** semana02-estacionamiento
**Herramienta usada:** Claude (Anthropic)

Este documento registra los prompts (indicaciones) que se usaron para
generar el código de este ejercicio, en el orden en que se fueron dando
las instrucciones progresivas del profesor.

---

## Commit 1: Ingreso de datos

**Contexto entregado a la IA:**
> "Ayúdame a crear un programa en Kotlin de una playa de estacionamiento
> con un tarifario básico: moto S/2, auto S/4, camioneta S/10 por hora.
> Se debe buscar el propietario de cada vehículo por: placa, tipo, horas
> y cliente."

**Prompt de ajuste (versión interactiva):**
> "Necesito que hagas ahora uno en donde yo mismo inserte datos: la placa,
> tipo, horas y cómo se llama el cliente. Se mantiene el mismo tarifario
> de cada vehículo."

**Resultado:** función `leerVehiculo()` con `readLine()` para capturar
cada dato por consola, con validación de tipo de vehículo (solo acepta
moto/auto/camioneta) y de horas (número positivo).

---

## Commit 2: Operaciones con los cálculos

**Reglas de negocio entregadas (transcritas de la pizarra de clase):**
- Si permanece hasta 2 horas, paga la tarifa normal.
- Si permanece más de 2 horas hasta 4 horas, esas horas adicionales
  tienen un recargo del 20%.
- Si permanece más de 5 horas, esas horas tienen un recargo del 50%.
- Si el cliente es frecuente, obtiene un 10% de descuento sobre el total.
- Ningún vehículo puede registrar menos de una hora.
- Registro máximo de 30 vehículos por día.

**Prompt inicial:**
> "Para poder terminar el proyecto lo dividiremos en dos partes más, la
> segunda parte tendrá que ver con las operaciones con los cálculos...
> [se listaron las reglas de recargo y descuento de arriba]. También
> debes plantear un registro máximo de 30 vehículos."

**Prompt de corrección (aclaración del recargo escalonado):**
> "El recargo se debe aplicar a todas las horas cuando el registro supera
> las 5 horas, o sea cada hora el 50% más de su tarifario habitual."
>
> Luego se aclaró: "Si tiene dos horas se le cobra normal 4 soles por
> hora, si se pasa a 3 y 4 horas a esos 4 soles se le aumenta el 20% y
> si se pasa de las 5 horas para adelante se le cobra el 50% más a los
> 4 soles."

**Resultado:** función `calcularTarifa()` con recargo por tramos de hora
(1-2 horas normal, 3-4 horas +20%, 5+ horas +50%), y función
`esClienteFrecuente()`.

**Prompt de mejora (realismo):**
> "¿Cómo se sabe cuándo se aplica el 10% de descuento al vehículo que
> viene más a menudo?" — seguido de la instrucción de basar la detección
> en datos reales del sistema en vez de una lista fija, ya que "todos mis
> laboratorios se basarán en casos realistas".

**Resultado final:** `contarFrecuenciaPorPlaca()` calcula dinámicamente
qué placas se repiten dentro del mismo registro del día (usando
`groupingBy` y `eachCount`), sin depender de una lista predefinida.

---

## Commit 3: Mostrar resultados

**Prompt:**
> "Ahora se realizará el tercer commit en donde se deben mostrar los
> resultados: que se muestre un resumen del día, cuánto es lo que se
> recaudó en total, y cuál fue el vehículo con mayor pago de acuerdo a
> la placa del vehículo."

**Resultado:** función `mostrarResumenDelDia()` que calcula el total de
vehículos atendidos (desglosado por tipo), la recaudación total con
`sumOf`, y el vehículo con el pago más alto usando `maxByOrNull`.

---

## Resumen de la estructura del código

- `Vehiculo`: data class con placa, tipo, horas y cliente.
- `tarifas`: mapa con el precio base por tipo de vehículo.
- `calcularTarifa()`: aplica los recargos escalonados por hora y el
  descuento por cliente frecuente.
- `contarFrecuenciaPorPlaca()` / `esClienteFrecuente()`: detectan
  dinámicamente si una placa se repite en el registro del día.
- `leerVehiculo()` / `leerCantidadVehiculos()`: capturan datos por
  consola con validaciones.
- `mostrarVehiculo()` / `mostrarResumenDelDia()`: presentan los
  resultados con formato alineado.
- `main()`: orquesta el flujo completo (registro → detalle → búsqueda
  opcional → resumen del día).
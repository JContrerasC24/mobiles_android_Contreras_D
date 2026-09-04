package com.contreras.estacionamiento

data class Vehiculo(
    val placa: String,
    val tipo: String,
    val horas: Int,
    val cliente: String
)

val tarifas = mapOf(
    "moto" to 2.0,
    "vehiculo" to 4.0,
    "camioneta" to 10.0,
    "trailer" to 20.0
)

const val MAX_VEHICULOS = 30
const val PORCENTAJE_IGV = 0.18

fun contarFrecuenciaPorPlaca(vehiculos: List<Vehiculo>): Map<String, Int> {
    return vehiculos.groupingBy { it.placa.uppercase() }.eachCount()
}

fun esClienteFrecuente(placa: String, frecuencia: Map<String, Int>): Boolean {
    return (frecuencia[placa.uppercase()] ?: 0) > 1
}

/**
 * Calcula el monto por horas, aplicando el porcentaje de la tarifa
 * que corresponde a cada tramo:
 * - Horas 1 y 2: no se cobra nada (0%).
 * - Horas 3 a 5: se cobra el 20% de la tarifa por cada una de esas horas.
 * - Horas 6 a 10: se cobra el 40% de la tarifa por cada una de esas horas.
 * - Hora 11 en adelante: se cobra el 50% de la tarifa por cada una.
 */
fun calcularMontoPorHoras(vehiculo: Vehiculo): Double {
    val tarifaBase = tarifas[vehiculo.tipo.lowercase()] ?: 0.0
    val horas = vehiculo.horas

    val horasGratis = minOf(horas, 2)
    val horasAl20 = if (horas > 2) minOf(horas - 2, 3) else 0
    val horasAl40 = if (horas > 5) minOf(horas - 5, 5) else 0
    val horasAl50 = if (horas > 10) horas - 10 else 0

    return (horasGratis * tarifaBase * 0.0) +
            (horasAl20 * tarifaBase * 0.20) +
            (horasAl40 * tarifaBase * 0.40) +
            (horasAl50 * tarifaBase * 0.50)
}

/**
 * Calcula el SUBTOTAL (antes de IGV), aplicando en orden:
 * 1. El costo por horas segun el tramo.
 * 2. Si ese monto supera S/ 500, se aplica un 20% de descuento.
 * 3. Si el cliente es frecuente (placa repetida en el dia), se aplica
 *    un 10% de descuento adicional sobre el resultado anterior.
 */
fun calcularSubtotal(vehiculo: Vehiculo, frecuencia: Map<String, Int>): Double {
    var monto = calcularMontoPorHoras(vehiculo)

    if (monto > 500.0) {
        monto *= 0.80
    }

    if (esClienteFrecuente(vehiculo.placa, frecuencia)) {
        monto *= 0.90
    }

    return monto
}

/**
 * Calcula el IGV (18%) a partir del subtotal ya con los descuentos aplicados.
 */
fun calcularIGV(subtotal: Double): Double {
    return subtotal * PORCENTAJE_IGV
}

/**
 * Monto final que paga el cliente: subtotal + IGV.
 */
fun calcularTarifa(vehiculo: Vehiculo, frecuencia: Map<String, Int>): Double {
    val subtotal = calcularSubtotal(vehiculo, frecuencia)
    val igv = calcularIGV(subtotal)
    return subtotal + igv
}

fun buscarPorPlaca(vehiculos: List<Vehiculo>, placa: String): Vehiculo? {
    return vehiculos.find { it.placa.equals(placa, ignoreCase = true) }
}

fun mostrarVehiculo(vehiculo: Vehiculo, frecuencia: Map<String, Int>) {
    val subtotal = calcularSubtotal(vehiculo, frecuencia)
    val igv = calcularIGV(subtotal)
    val total = subtotal + igv
    val frecuente = if (esClienteFrecuente(vehiculo.placa, frecuencia)) " (Cliente frecuente)" else ""
    println(
        String.format(
            "Placa: %-8s | Tipo: %-11s | Horas: %2d | Cliente: %-15s | Subtotal: S/ %.2f | IGV: S/ %.2f | Total: S/ %.2f%s",
            vehiculo.placa, vehiculo.tipo, vehiculo.horas, vehiculo.cliente, subtotal, igv, total, frecuente
        )
    )
}

fun mostrarResumenDelDia(vehiculos: List<Vehiculo>, frecuencia: Map<String, Int>) {
    println("\n=========================================")
    println("           RESUMEN DEL DIA")
    println("=========================================")

    if (vehiculos.isEmpty()) {
        println("No se registraron vehiculos el dia de hoy.")
        return
    }

    val totalVehiculosRegistrados = vehiculos.size
    val totalMotos = vehiculos.count { it.tipo == "moto" }
    val totalVehiculo = vehiculos.count { it.tipo == "vehiculo" }
    val totalCamionetas = vehiculos.count { it.tipo == "camioneta" }
    val totalTrailers = vehiculos.count { it.tipo == "trailer" }

    println("Vehiculos atendidos: $totalVehiculosRegistrados")
    println("  - Motos: $totalMotos")
    println("  - Vehiculos: $totalVehiculo")
    println("  - Camionetas: $totalCamionetas")
    println("  - Trailers: $totalTrailers")

    val subtotalTotal = vehiculos.sumOf { calcularSubtotal(it, frecuencia) }
    val igvTotal = vehiculos.sumOf { calcularIGV(calcularSubtotal(it, frecuencia)) }
    val recaudacionTotal = subtotalTotal + igvTotal

    println(String.format("\nSubtotal general: S/ %.2f", subtotalTotal))
    println(String.format("IGV general (18%%): S/ %.2f", igvTotal))
    println(String.format("Recaudacion total (con IGV): S/ %.2f", recaudacionTotal))

    val vehiculoMayorPago = vehiculos.maxByOrNull { calcularTarifa(it, frecuencia) }
    if (vehiculoMayorPago != null) {
        val montoMayor = calcularTarifa(vehiculoMayorPago, frecuencia)
        println(
            String.format(
                "Vehiculo con mayor pago -> Placa: %s | Cliente: %s | Monto: S/ %.2f",
                vehiculoMayorPago.placa, vehiculoMayorPago.cliente, montoMayor
            )
        )
    }

    println("=========================================")
}

fun leerVehiculo(): Vehiculo {
    print("Placa: ")
    val placa = readLine()?.trim() ?: ""

    var tipo: String
    do {
        print("Tipo de vehiculo (moto / vehiculo / camioneta / trailer): ")
        tipo = readLine()?.trim()?.lowercase() ?: ""
        if (!tarifas.containsKey(tipo)) {
            println("Tipo invalido. Solo se acepta: moto, vehiculo, camioneta o trailer.")
        }
    } while (!tarifas.containsKey(tipo))

    var horas: Int
    do {
        print("Horas estacionado (minimo 1): ")
        horas = readLine()?.trim()?.toIntOrNull() ?: -1
        if (horas < 1) {
            println("Ningun vehiculo puede registrar menos de una hora.")
        }
    } while (horas < 1)

    print("Nombre del cliente: ")
    val cliente = readLine()?.trim() ?: ""

    return Vehiculo(placa, tipo, horas, cliente)
}

fun leerCantidadVehiculos(): Int {
    var cantidad: Int
    do {
        print("¿Cuantos vehiculos desea registrar? (maximo $MAX_VEHICULOS): ")
        cantidad = readLine()?.trim()?.toIntOrNull() ?: -1
        if (cantidad < 0) {
            println("Ingrese un numero valido.")
        } else if (cantidad > MAX_VEHICULOS) {
            println("No se pueden registrar mas de $MAX_VEHICULOS vehiculos.")
        }
    } while (cantidad < 0 || cantidad > MAX_VEHICULOS)
    return cantidad
}

fun main() {
    println("=========================================")
    println("   PLAYA DE ESTACIONAMIENTO - TARIFARIO")
    println("=========================================")
    println("Moto: S/ 2.00/hora | Vehiculo: S/ 4.00/hora | Camioneta: S/ 10.00/hora | Trailer: S/ 20.00/hora")
    println("Horas 1-2: gratis | Horas 3-5: 20% | Horas 6-10: 40% | Hora 11+: 50%")
    println("Si el subtotal supera S/ 500: -20% | Cliente frecuente (placa repetida): -10% | IGV: 18%")
    println()

    val cantidad = leerCantidadVehiculos()
    val vehiculos = mutableListOf<Vehiculo>()

    for (i in 1..cantidad) {
        println("\n--- Registrando vehiculo $i de $cantidad ---")
        vehiculos.add(leerVehiculo())
    }

    val frecuencia = contarFrecuenciaPorPlaca(vehiculos)

    println("\n--- Detalle de vehiculos registrados ---")
    if (vehiculos.isEmpty()) {
        println("No se registro ningun vehiculo.")
    } else {
        vehiculos.forEach { mostrarVehiculo(it, frecuencia) }
    }

    println()
    print("¿Desea buscar un vehiculo por placa? (s/n): ")
    val respuesta = readLine()?.trim()?.lowercase()

    if (respuesta == "s") {
        print("Ingrese la placa a buscar: ")
        val placaBuscada = readLine()?.trim() ?: ""

        val encontrado = buscarPorPlaca(vehiculos, placaBuscada)
        if (encontrado != null) {
            println("\nVehiculo encontrado:")
            mostrarVehiculo(encontrado, frecuencia)
        } else {
            println("\nNo se encontro ningun vehiculo con la placa '$placaBuscada'.")
        }
    }

    mostrarResumenDelDia(vehiculos, frecuencia)

    println("\nFin del programa.")
}
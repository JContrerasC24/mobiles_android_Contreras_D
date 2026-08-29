package com.contreras.estacionamiento

data class Vehiculo(
    val placa: String,
    val tipo: String,
    val horas: Int,
    val cliente: String
)

val tarifas = mapOf(
    "moto" to 2.0,
    "auto" to 4.0,
    "camioneta" to 10.0
)

/**
 * Placas registradas como clientes frecuentes. En un sistema real esto
 * vendria de una base de datos; aqui lo simulamos con una lista fija.
 */
val clientesFrecuentes = setOf("ABC-123", "XYZ-789")

const val MAX_VEHICULOS = 30

fun esClienteFrecuente(placa: String): Boolean {
    return clientesFrecuentes.contains(placa.uppercase())
}

/**
 * Calcula el monto a pagar aplicando recargos POR TRAMO DE HORA:
 * - Horas 1 y 2: tarifa normal (100%).
 * - Horas 3 y 4: tarifa + 20% de recargo (solo esas horas).
 * - Hora 5 en adelante: tarifa + 50% de recargo (solo esas horas).
 * Ejemplo con auto (S/4/hora) y 6 horas:
 *   2h normales (4x2=8) + 2h al 20% (4*1.2x2=9.6) + 2h al 50% (4*1.5x2=12) = 29.6
 * Al final, si el cliente es frecuente (segun su placa), se aplica un
 * descuento del 10% sobre el monto total.
 */
fun calcularTarifa(vehiculo: Vehiculo): Double {
    val tarifaBase = tarifas[vehiculo.tipo.lowercase()] ?: 0.0
    val horas = vehiculo.horas

    val horasNormales = minOf(horas, 2)
    val horasConRecargo20 = if (horas > 2) minOf(horas - 2, 2) else 0
    val horasConRecargo50 = if (horas > 4) horas - 4 else 0

    var monto = (horasNormales * tarifaBase) +
            (horasConRecargo20 * tarifaBase * 1.20) +
            (horasConRecargo50 * tarifaBase * 1.50)

    if (esClienteFrecuente(vehiculo.placa)) {
        monto *= 0.90
    }

    return monto
}

fun buscarPorPlaca(vehiculos: List<Vehiculo>, placa: String): Vehiculo? {
    return vehiculos.find { it.placa.equals(placa, ignoreCase = true) }
}

fun mostrarVehiculo(vehiculo: Vehiculo) {
    val total = calcularTarifa(vehiculo)
    val frecuente = if (esClienteFrecuente(vehiculo.placa)) " (Cliente frecuente)" else ""
    println(
        String.format(
            "Placa: %-8s | Tipo: %-11s | Horas: %2d | Cliente: %-15s | Total: S/ %.2f%s",
            vehiculo.placa, vehiculo.tipo, vehiculo.horas, vehiculo.cliente, total, frecuente
        )
    )
}

fun leerVehiculo(): Vehiculo {
    print("Placa: ")
    val placa = readLine()?.trim() ?: ""

    var tipo: String
    do {
        print("Tipo de vehiculo (moto / auto / camioneta): ")
        tipo = readLine()?.trim()?.lowercase() ?: ""
        if (!tarifas.containsKey(tipo)) {
            println("Tipo invalido. Solo se acepta: moto, auto o camioneta.")
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
    println("Moto: S/ 2.00/hora | Auto: S/ 4.00/hora | Camioneta: S/ 10.00/hora")
    println("Horas 1-2: normal | Horas 3-4: +20% | Hora 5+: +50% | Cliente frecuente: -10%")
    println()

    val cantidad = leerCantidadVehiculos()
    val vehiculos = mutableListOf<Vehiculo>()

    for (i in 1..cantidad) {
        println("\n--- Registrando vehiculo $i de $cantidad ---")
        vehiculos.add(leerVehiculo())
    }

    println("\n--- Resumen de vehiculos registrados ---")
    if (vehiculos.isEmpty()) {
        println("No se registro ningun vehiculo.")
    } else {
        vehiculos.forEach { mostrarVehiculo(it) }
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
            mostrarVehiculo(encontrado)
        } else {
            println("\nNo se encontro ningun vehiculo con la placa '$placaBuscada'.")
        }
    }

    println("\nFin del programa.")
}
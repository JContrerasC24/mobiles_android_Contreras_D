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

fun calcularTarifa(vehiculo: Vehiculo): Double {
    val tarifaHora = tarifas[vehiculo.tipo.lowercase()] ?: 0.0
    return tarifaHora * vehiculo.horas
}

fun buscarPorPlaca(vehiculos: List<Vehiculo>, placa: String): Vehiculo? {
    return vehiculos.find { it.placa.equals(placa, ignoreCase = true) }
}

fun mostrarVehiculo(vehiculo: Vehiculo) {
    val total = calcularTarifa(vehiculo)
    println(
        String.format(
            "Placa: %-8s | Tipo: %-11s | Horas: %2d | Cliente: %-15s | Total: S/ %.2f",
            vehiculo.placa, vehiculo.tipo, vehiculo.horas, vehiculo.cliente, total
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
        print("Horas estacionado: ")
        horas = readLine()?.trim()?.toIntOrNull() ?: -1
        if (horas <= 0) {
            println("Ingrese un numero de horas valido (mayor a 0).")
        }
    } while (horas <= 0)

    print("Nombre del cliente: ")
    val cliente = readLine()?.trim() ?: ""

    return Vehiculo(placa, tipo, horas, cliente)
}

fun main() {
    println("=========================================")
    println("   PLAYA DE ESTACIONAMIENTO - TARIFARIO")
    println("=========================================")
    println("Moto: S/ 2.00/hora | Auto: S/ 4.00/hora | Camioneta: S/ 10.00/hora")
    println()

    print("¿Cuantos vehiculos desea registrar? ")
    val cantidad = readLine()?.trim()?.toIntOrNull() ?: 0

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
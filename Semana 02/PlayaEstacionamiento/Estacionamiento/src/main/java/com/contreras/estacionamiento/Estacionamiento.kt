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

const val MAX_VEHICULOS = 30

/**
 * Cuenta cuantas veces aparece cada placa en la lista de vehiculos
 * registrados. Esto permite detectar clientes frecuentes de forma
 * dinamica, en base a los datos reales del dia, sin listas fijas.
 */
fun contarFrecuenciaPorPlaca(vehiculos: List<Vehiculo>): Map<String, Int> {
    return vehiculos.groupingBy { it.placa.uppercase() }.eachCount()
}

/**
 * Un vehiculo es "cliente frecuente" si su placa aparece MAS DE UNA VEZ
 * entre los vehiculos registrados en el dia (es decir, regreso a la
 * playa de estacionamiento mas de una vez).
 */
fun esClienteFrecuente(placa: String, frecuencia: Map<String, Int>): Boolean {
    return (frecuencia[placa.uppercase()] ?: 0) > 1
}

/**
 * Calcula el monto a pagar aplicando recargos POR TRAMO DE HORA:
 * - Horas 1 y 2: tarifa normal.
 * - Horas 3 y 4: tarifa + 20% de recargo.
 * - Hora 5 en adelante: tarifa + 50% de recargo.
 * Si la placa del vehiculo aparece mas de una vez en el registro del
 * dia (cliente frecuente), se aplica un 10% de descuento sobre el total.
 */
fun calcularTarifa(vehiculo: Vehiculo, frecuencia: Map<String, Int>): Double {
    val tarifaBase = tarifas[vehiculo.tipo.lowercase()] ?: 0.0
    val horas = vehiculo.horas

    val horasNormales = minOf(horas, 2)
    val horasConRecargo20 = if (horas > 2) minOf(horas - 2, 2) else 0
    val horasConRecargo50 = if (horas > 4) horas - 4 else 0

    var monto = (horasNormales * tarifaBase) +
            (horasConRecargo20 * tarifaBase * 1.20) +
            (horasConRecargo50 * tarifaBase * 1.50)

    if (esClienteFrecuente(vehiculo.placa, frecuencia)) {
        monto *= 0.90
    }

    return monto
}

fun buscarPorPlaca(vehiculos: List<Vehiculo>, placa: String): Vehiculo? {
    return vehiculos.find { it.placa.equals(placa, ignoreCase = true) }
}

fun mostrarVehiculo(vehiculo: Vehiculo, frecuencia: Map<String, Int>) {
    val total = calcularTarifa(vehiculo, frecuencia)
    val frecuente = if (esClienteFrecuente(vehiculo.placa, frecuencia)) " (Cliente frecuente)" else ""
    println(
        String.format(
            "Placa: %-8s | Tipo: %-11s | Horas: %2d | Cliente: %-15s | Total: S/ %.2f%s",
            vehiculo.placa, vehiculo.tipo, vehiculo.horas, vehiculo.cliente, total, frecuente
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

    val totalVehiculos = vehiculos.size
    val totalMotos = vehiculos.count { it.tipo == "moto" }
    val totalAutos = vehiculos.count { it.tipo == "auto" }
    val totalCamionetas = vehiculos.count { it.tipo == "camioneta" }

    println("Vehiculos atendidos: $totalVehiculos")
    println("  - Motos: $totalMotos")
    println("  - Autos: $totalAutos")
    println("  - Camionetas: $totalCamionetas")

    val recaudacionTotal = vehiculos.sumOf { calcularTarifa(it, frecuencia) }
    println(String.format("\nRecaudacion total: S/ %.2f", recaudacionTotal))

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
    println("Horas 1-2: normal | Horas 3-4: +20% | Hora 5+: +50% | Cliente frecuente (placa repetida): -10%")
    println()

    val cantidad = leerCantidadVehiculos()
    val vehiculos = mutableListOf<Vehiculo>()

    for (i in 1..cantidad) {
        println("\n--- Registrando vehiculo $i de $cantidad ---")
        vehiculos.add(leerVehiculo())
    }

    // Se calcula la frecuencia UNA VEZ, con todos los vehiculos ya registrados,
    // para saber que placas se repiten en el dia.
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
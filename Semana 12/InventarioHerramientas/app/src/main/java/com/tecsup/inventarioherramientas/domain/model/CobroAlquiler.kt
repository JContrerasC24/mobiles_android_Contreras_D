package com.tecsup.inventarioherramientas.domain.model

import java.math.BigDecimal
import java.util.Locale

data class CobroAlquiler(val tarifaCentimos: Long, val dias: Int, val metodo: String, val referencia: String) {
    val totalCentimos: Long get() = tarifaCentimos * dias
    fun validar() {
        require(tarifaCentimos in 100..100000 && dias in 1..365) { "Revisa la tarifa y los días de alquiler." }
        require(metodo in listOf("EFECTIVO", "YAPE", "TARJETA")) { "Selecciona el método de pago." }
        require(referencia.length <= 80 && (metodo == "EFECTIVO" || referencia.isNotBlank())) {
            "Ingresa el número de operación o comprobante."
        }
    }
}
fun soles(centimos: Long) = String.format(Locale.forLanguageTag("es-PE"), "S/ %.2f", centimos / 100.0)
fun leerCentimos(texto: String): Long? = try {
    BigDecimal(texto.trim().replace(',', '.')).movePointRight(2).longValueExact()
} catch (_: Exception) { null }
fun tarifaSugerida(nombre: String): Long {
    val n = nombre.lowercase()
    return when {
        "sierra" in n -> 4000
        "taladro" in n || "amoladora" in n -> 2500
        "caja" in n -> 1500
        "llave" in n -> 800
        "destornillador" in n || "cinta" in n -> 500
        else -> 700
    }
}

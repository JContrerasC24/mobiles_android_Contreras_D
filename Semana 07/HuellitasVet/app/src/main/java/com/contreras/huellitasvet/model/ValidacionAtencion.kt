package com.contreras.huellitasvet.model

fun validarAtencion(diagnostico: String, peso: Double): Map<String, String> = buildMap {
    if (diagnostico.trim().length < 5) put("diagnostico", "Escribe al menos 5 caracteres")
    if (!peso.isFinite() || peso <= 0) put("peso", "Ingresa un peso mayor que 0")
}

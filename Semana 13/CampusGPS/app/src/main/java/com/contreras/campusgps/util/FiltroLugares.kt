package com.contreras.campusgps.util

import com.contreras.campusgps.model.LugarConDistancia
import java.text.Normalizer
import java.util.Locale

object FiltroLugares {
    private fun normalizar(valor: String) = Normalizer.normalize(valor.trim(), Normalizer.Form.NFD)
        .replace("\\p{M}+".toRegex(), "").lowercase(Locale.ROOT)

    // Conserva el orden por distancia del catálogo recibido.
    fun aplicar(lugares: List<LugarConDistancia>, consulta: String, categoria: String?): List<LugarConDistancia> {
        val texto = normalizar(consulta)
        return lugares.filter {
            (categoria == null || it.lugar.categoria == categoria) &&
                normalizar(it.lugar.nombre).contains(texto)
        }
    }
}

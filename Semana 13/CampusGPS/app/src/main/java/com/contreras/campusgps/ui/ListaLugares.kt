package com.contreras.campusgps.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.contreras.campusgps.model.LugarConDistancia

import java.util.Locale
import kotlin.math.roundToInt


/**
 * Lista interactiva de lugares del campus.
 *
 * Muestra:
 * - Nombre del lugar.
 * - Categoría.
 * - Distancia respecto al usuario.
 *
 * Permite seleccionar un lugar para navegar
 * hacia su posición dentro de Google Maps.
 */
@Composable
fun ListaLugares(

    lugares: List<LugarConDistancia>,

    seleccionadoId: Int?,

    onSeleccionar: (Int) -> Unit

) {

    Column(

        modifier = Modifier.fillMaxWidth()

    ) {

        // =================================================
        // TÍTULO
        // =================================================

        Text(

            text = "Lugares del campus",

            style = MaterialTheme.typography.titleLarge
        )


        // =================================================
        // LISTA DE LUGARES
        // =================================================

        /*
         * Los lugares ya llegan ordenados por distancia
         * desde MapaViewModel.
         */
        lugares.forEach { elemento ->

            val lugar = elemento.lugar

            // Comprueba si este lugar está seleccionado.
            val estaSeleccionado =
                seleccionadoId == lugar.id


            // =============================================
            // TARJETA DEL LUGAR
            // =============================================

            Card(

                onClick = {

                    /*
                     * Notifica el identificador del lugar
                     * seleccionado a PantallaMapa.
                     */
                    onSeleccionar(lugar.id)
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),

                colors = CardDefaults.cardColors(

                    /*
                     * Utiliza un color diferente
                     * cuando el lugar está seleccionado.
                     */
                    containerColor =

                        if (estaSeleccionado) {

                            MaterialTheme.colorScheme.primaryContainer

                        } else {

                            MaterialTheme.colorScheme.surfaceVariant
                        }
                )

            ) {

                Row(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),

                    horizontalArrangement =
                        Arrangement.SpaceBetween,

                    verticalAlignment =
                        Alignment.CenterVertically

                ) {

                    // =====================================
                    // NOMBRE Y CATEGORÍA
                    // =====================================

                    Column(

                        modifier = Modifier.weight(1f)

                    ) {

                        Text(

                            text = lugar.nombre,

                            style =
                                MaterialTheme.typography.titleMedium
                        )

                        Text(

                            text = lugar.categoria,

                            style =
                                MaterialTheme.typography.bodySmall
                        )
                    }


                    Spacer(
                        modifier = Modifier.width(12.dp)
                    )


                    // =====================================
                    // DISTANCIA DEL LUGAR
                    // =====================================

                    Text(

                        text = formatearDistancia(
                            elemento.metros
                        ),

                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}


// =====================================================
// FORMATEAR DISTANCIAS
// =====================================================

/**
 * Convierte una distancia numérica en texto.
 *
 * Ejemplos:
 *
 * 150.5 metros -> 151 m
 * 1500 metros  -> 1.50 km
 */
private fun formatearDistancia(
    metros: Float?
): String {

    // Todavía no se realizó el cálculo.
    if (metros == null) {

        return "Sin calcular"
    }

    return if (metros < 1000f) {

        // Distancias menores a un kilómetro.
        "${metros.roundToInt()} m"

    } else {

        // Distancias iguales o mayores a un kilómetro.
        String.format(
            Locale.getDefault(),
            "%.2f km",
            metros / 1000f
        )
    }
}
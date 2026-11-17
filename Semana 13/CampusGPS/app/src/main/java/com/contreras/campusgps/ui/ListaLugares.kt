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
 * Permite:
 * - Mostrar lugares ordenados por distancia.
 * - Seleccionar lugares.
 * - Identificar visualmente el lugar más cercano.
 */
@Composable
fun ListaLugares(

    lugares: List<LugarConDistancia>,

    seleccionadoId: Int?,

    // Identificador del lugar más cercano.
    masCercanoId: Int?,

    onSeleccionar: (Int) -> Unit

) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(

            text = "Lugares del campus",

            style = MaterialTheme.typography.titleLarge
        )


        // =================================================
        // RECORRER LOS LUGARES
        // =================================================

        lugares.forEach { elemento ->

            val lugar = elemento.lugar

            // Comprueba si el usuario seleccionó este lugar.
            val estaSeleccionado =
                seleccionadoId == lugar.id

            // Comprueba si es el lugar más cercano.
            val esMasCercano =
                masCercanoId == lugar.id


            // =================================================
            // COLOR DE LA TARJETA
            // =================================================

            /*
             * Utilizamos diferentes colores según
             * el estado del lugar.
             *
             * 1. Seleccionado: color principal.
             * 2. Más cercano: color destacado.
             * 3. Otros lugares: color normal.
             */
            val colorTarjeta = when {

                estaSeleccionado -> {
                    MaterialTheme.colorScheme.primaryContainer
                }

                esMasCercano -> {
                    MaterialTheme.colorScheme.tertiaryContainer
                }

                else -> {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            }


            // =================================================
            // TARJETA INTERACTIVA
            // =================================================

            Card(

                onClick = {

                    onSeleccionar(lugar.id)
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),

                colors = CardDefaults.cardColors(

                    containerColor = colorTarjeta
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
                    // INFORMACIÓN DEL LUGAR
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


                        // =================================
                        // ETIQUETA MÁS CERCANO
                        // =================================

                        if (esMasCercano) {

                            Text(

                                text = "Más cercano",

                                style =
                                    MaterialTheme.typography.labelMedium,

                                color =
                                    if (estaSeleccionado) {

                                        MaterialTheme.colorScheme
                                            .onPrimaryContainer

                                    } else {

                                        MaterialTheme.colorScheme
                                            .onTertiaryContainer
                                    }
                            )
                        }
                    }


                    Spacer(
                        modifier = Modifier.width(12.dp)
                    )


                    // =====================================
                    // DISTANCIA
                    // =====================================

                    Text(

                        text = formatearDistancia(
                            elemento.metros
                        ),

                        style =
                            MaterialTheme.typography.bodyMedium
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
 * Muestra las distancias en metros o kilómetros.
 */
private fun formatearDistancia(
    metros: Float?
): String {

    if (metros == null) {

        return "Sin calcular"
    }

    return if (metros < 1000f) {

        "${metros.roundToInt()} m"

    } else {

        String.format(
            Locale.getDefault(),
            "%.2f km",
            metros / 1000f
        )
    }
}
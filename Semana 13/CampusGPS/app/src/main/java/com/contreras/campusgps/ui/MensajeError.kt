package com.contreras.campusgps.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.Settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height

import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import com.contreras.campusgps.model.TipoErrorUbicacion


/**
 * Componente encargado de mostrar los errores
 * relacionados con la ubicación.
 *
 * Permite:
 * - Informar al usuario.
 * - Reintentar la operación.
 * - Abrir los ajustes correspondientes.
 */
@Composable
fun MensajeError(

    mensaje: String,

    tipo: TipoErrorUbicacion,

    onReintentar: () -> Unit

) {

    val contexto = LocalContext.current

    Card(

        modifier = Modifier.fillMaxWidth(),

        colors = CardDefaults.cardColors(

            containerColor =
                MaterialTheme.colorScheme.errorContainer
        )

    ) {

        Column(

            modifier = Modifier.padding(16.dp)

        ) {

            // =================================================
            // TÍTULO
            // =================================================

            Text(

                text = "Problema de ubicación",

                style = MaterialTheme.typography.titleMedium,

                color = MaterialTheme.colorScheme.onErrorContainer
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )


            // =================================================
            // MENSAJE
            // =================================================

            Text(

                text = mensaje,

                color = MaterialTheme.colorScheme.onErrorContainer
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )


            // =================================================
            // BOTÓN REINTENTAR
            // =================================================

            Button(

                onClick = onReintentar

            ) {

                Text("Reintentar")
            }


            // =================================================
            // ACCIONES SEGÚN EL ERROR
            // =================================================

            when (tipo) {

                // ---------------------------------------------
                // GPS DESACTIVADO
                // ---------------------------------------------

                TipoErrorUbicacion.GPS_APAGADO -> {

                    OutlinedButton(

                        onClick = {

                            /*
                             * Abre la configuración
                             * de ubicación de Android.
                             */
                            val intent = Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS
                            )

                            try {

                                contexto.startActivity(intent)

                            } catch (_: ActivityNotFoundException) {

                                contexto.startActivity(
                                    Intent(Settings.ACTION_SETTINGS)
                                )
                            }
                        }

                    ) {

                        Text("Activar ubicación")
                    }
                }


                // ---------------------------------------------
                // PERMISO DENEGADO
                // ---------------------------------------------

                TipoErrorUbicacion.PERMISO_DENEGADO -> {

                    OutlinedButton(

                        onClick = {

                            /*
                             * Abre los ajustes específicos
                             * de CampusGPS para que el usuario
                             * pueda conceder permisos.
                             */
                            val intent = Intent(

                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,

                                Uri.parse(
                                    "package:${contexto.packageName}"
                                )
                            )

                            contexto.startActivity(intent)
                        }

                    ) {

                        Text("Configurar permisos")
                    }
                }


                // ---------------------------------------------
                // GOOGLE PLAY SERVICES
                // ---------------------------------------------

                TipoErrorUbicacion.PLAY_SERVICES -> {

                    Text(

                        text = "Comprueba que tu dispositivo o emulador tenga Google Play Services disponible.",

                        style = MaterialTheme.typography.bodySmall
                    )
                }


                // ---------------------------------------------
                // ERROR GENÉRICO
                // ---------------------------------------------

                TipoErrorUbicacion.GENERICO -> {

                    Text(

                        text = "Puedes volver a intentar obtener la ubicación.",

                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
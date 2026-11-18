package com.contreras.centrotareas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import com.contreras.centrotareas.model.CargaUi
import com.contreras.centrotareas.model.PrimosUi
import com.contreras.centrotareas.viewmodel.TareasViewModel

/**
 * Pantalla principal de CentroTareas.
 *
 * Muestra tres tarjetas:
 *
 * 1. Tarea con progreso.
 * 2. Contar números primos.
 * 3. Dos cargas de datos.
 *
 * Observa los estados proporcionados por el ViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaTareas(
    vm: TareasViewModel
) {

    // ==========================================
    // ESTADOS DEL VIEWMODEL
    // ==========================================

    val mensaje by vm.mensaje.collectAsState()

    val primos by vm.primos.collectAsState()

    val carga by vm.carga.collectAsState()


    // ==========================================
    // ESTADOS LOCALES DE LA INTERFAZ
    // ==========================================

    /**
     * Contador utilizado para comprobar
     * si la pantalla continúa respondiendo.
     */
    var toques by remember {
        mutableStateOf(0)
    }

    /**
     * Número utilizado para el cálculo
     * de números primos.
     */
    var numeroPrimos by remember {
        mutableStateOf("1000")
    }

    /**
     * Control para simular errores.
     */
    var simularError by remember {
        mutableStateOf(false)
    }


    // ==========================================
    // ESTRUCTURA PRINCIPAL
    // ==========================================

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text("CentroTareas")
                },

                colors = TopAppBarDefaults.topAppBarColors(

                    containerColor = Color(0xFF1565C0),

                    titleContentColor = Color.White
                )
            )
        }

    ) { paddingValues ->

        LazyColumn(

            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),

            contentPadding = PaddingValues(16.dp),

            verticalArrangement = Arrangement.spacedBy(16.dp)

        ) {


            // =====================================
            // TARJETA 1: TAREA CON PROGRESO
            // =====================================

            item {

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(

                        modifier = Modifier.padding(16.dp),

                        verticalArrangement = Arrangement.spacedBy(12.dp)

                    ) {

                        Text(

                            text = "Tarea con progreso",

                            style = MaterialTheme.typography.titleLarge,

                            color = Color(0xFF1565C0)
                        )

                        Text(
                            text = "Comprueba cómo responde la aplicación mientras se ejecuta una tarea larga."
                        )


                        // =====================================
                        // BOTÓN DE PRUEBA DE VIDA
                        // =====================================

                        Button(

                            onClick = {

                                toques++
                            }

                        ) {

                            Text(
                                text = "Tócame: $toques"
                            )
                        }


                        // =====================================
                        // PASO 1: TAREA LARGA INCORRECTA
                        // =====================================

                        Button(
                            onClick = {
                                vm.tareaLarga()
                            }
                        ) {
                            Text("Tarea larga")
                        }


                        // =====================================
                        // MENSAJE DEL VIEWMODEL
                        // =====================================

                        Text(
                            text = "Estado: $mensaje"
                        )


                        // TODO Paso 3:
                        // Agregar LinearProgressIndicator.

                        // TODO Paso 4:
                        // Agregar botón Cancelar.
                    }
                }
            }


            // =====================================
            // TARJETA 2: CONTAR NÚMEROS PRIMOS
            // =====================================

            item {

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(

                        modifier = Modifier.padding(16.dp),

                        verticalArrangement = Arrangement.spacedBy(12.dp)

                    ) {

                        Text(

                            text = "Contar números primos",

                            style = MaterialTheme.typography.titleLarge,

                            color = Color(0xFF1565C0)
                        )


                        // Número máximo para el cálculo.
                        OutlinedTextField(

                            value = numeroPrimos,

                            onValueChange = {

                                numeroPrimos = it
                            },

                            label = {

                                Text("Número máximo")
                            },

                            modifier = Modifier.fillMaxWidth(),

                            singleLine = true
                        )


                        // =====================================
                        // BOTONES DE CÁLCULO
                        // =====================================

                        Row(

                            horizontalArrangement = Arrangement.spacedBy(8.dp)

                        ) {

                            Button(

                                onClick = {

                                    // TODO Paso 5
                                },

                                enabled = false

                            ) {

                                Text("Contar")
                            }


                            OutlinedButton(

                                onClick = {

                                    // TODO Paso 5
                                },

                                enabled = false

                            ) {

                                Text("Cancelar")
                            }
                        }


                        // =====================================
                        // ESTADOS DEL CÁLCULO
                        // =====================================

                        when (val estado = primos) {

                            PrimosUi.Inicial -> {

                                Text(
                                    "Todavía no se ha iniciado el cálculo."
                                )
                            }


                            PrimosUi.Calculando -> {

                                Text(
                                    "Calculando números primos..."
                                )
                            }


                            is PrimosUi.Listo -> {

                                Text(
                                    text = "Cantidad de primos: ${estado.cantidad}"
                                )

                                Text(
                                    text = "Tiempo: ${estado.milisegundos} ms"
                                )
                            }


                            PrimosUi.Cancelado -> {

                                Text(
                                    "Cálculo cancelado."
                                )
                            }
                        }
                    }
                }
            }


            // =====================================
            // TARJETA 3: DOS CARGAS
            // =====================================

            item {

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(

                        modifier = Modifier.padding(16.dp),

                        verticalArrangement = Arrangement.spacedBy(12.dp)

                    ) {

                        Text(

                            text = "Dos cargas",

                            style = MaterialTheme.typography.titleLarge,

                            color = Color(0xFF1565C0)
                        )

                        Text(
                            text = "Compara la ejecución secuencial y paralela de dos cargas simuladas."
                        )


                        // =====================================
                        // BOTONES DE CARGA
                        // =====================================

                        Row(

                            horizontalArrangement = Arrangement.spacedBy(8.dp)

                        ) {

                            OutlinedButton(

                                onClick = {

                                    // TODO Paso 6
                                },

                                enabled = false

                            ) {

                                Text("Una por una")
                            }


                            Button(

                                onClick = {

                                    // TODO Paso 6
                                },

                                enabled = false

                            ) {

                                Text("A la vez")
                            }
                        }


                        // =====================================
                        // INTERRUPTOR DE ERROR SIMULADO
                        // =====================================

                        Row(

                            horizontalArrangement = Arrangement.spacedBy(12.dp)

                        ) {

                            Text("Simular error")

                            Switch(

                                checked = simularError,

                                onCheckedChange = {

                                    simularError = it
                                },

                                enabled = false
                            )
                        }


                        // =====================================
                        // ESTADOS DE LAS CARGAS
                        // =====================================

                        when (val estado = carga) {


                            CargaUi.Inicial -> {

                                Text(
                                    "No se han cargado datos."
                                )
                            }


                            CargaUi.Cargando -> {

                                Text(
                                    "Cargando información..."
                                )
                            }


                            is CargaUi.Exito -> {

                                Text(
                                    "Modo: ${estado.modo}"
                                )

                                Text(
                                    "Perfil: ${estado.perfil}"
                                )

                                Text(
                                    "Pedidos: ${estado.pedidos}"
                                )

                                Text(
                                    "Tiempo: ${estado.milisegundos} ms"
                                )
                            }


                            is CargaUi.Error -> {

                                Text(

                                    text = estado.mensaje,

                                    color = MaterialTheme.colorScheme.error
                                )


                                OutlinedButton(

                                    onClick = {

                                        // TODO Paso 7
                                    },

                                    enabled = false

                                ) {

                                    Text("Reintentar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
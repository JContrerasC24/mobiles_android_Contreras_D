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
 * Muestra los estados proporcionados por el ViewModel.
 * Los ejercicios se implementarán progresivamente.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaTareas(
    vm: TareasViewModel
) {

    // Observamos los estados del ViewModel.
    val mensaje by vm.mensaje.collectAsState()
    val primos by vm.primos.collectAsState()
    val carga by vm.carga.collectAsState()

    // Estado local del contador de toques.
    var toques by remember {
        mutableStateOf(0)
    }

    // Número que utilizaremos para contar primos.
    var numeroPrimos by remember {
        mutableStateOf("1000")
    }

    // Interruptor de error simulado.
    var simularError by remember {
        mutableStateOf(false)
    }

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
                            text = "Comprueba que la interfaz responde mientras se ejecutan tareas."
                        )

                        Button(
                            onClick = {
                                toques++
                            }
                        ) {
                            Text("Tócame: $toques")
                        }

                        // Se habilitará en el Paso 1.
                        Button(
                            onClick = {
                                // TODO Paso 1
                            },
                            enabled = false
                        ) {
                            Text("Tarea larga")
                        }

                        Text(
                            text = "Estado: $mensaje"
                        )

                        // TODO Paso 3:
                        // Incorporar LinearProgressIndicator.

                        // TODO Paso 4:
                        // Incorporar botón Cancelar.
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

                        when (val estado = primos) {

                            PrimosUi.Inicial -> {
                                Text("Todavía no se ha iniciado el cálculo.")
                            }

                            PrimosUi.Calculando -> {
                                Text("Calculando números primos...")
                            }

                            is PrimosUi.Listo -> {

                                Text(
                                    "Cantidad de primos: ${estado.cantidad}"
                                )

                                Text(
                                    "Tiempo: ${estado.milisegundos} ms"
                                )
                            }

                            PrimosUi.Cancelado -> {
                                Text("Cálculo cancelado.")
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
                            text = "Compara cargas secuenciales y paralelas."
                        )

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

                        when (val estado = carga) {

                            CargaUi.Inicial -> {
                                Text("No se han cargado datos.")
                            }

                            CargaUi.Cargando -> {
                                Text("Cargando información...")
                            }

                            is CargaUi.Exito -> {

                                Text("Modo: ${estado.modo}")

                                Text("Perfil: ${estado.perfil}")

                                Text("Pedidos: ${estado.pedidos}")

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
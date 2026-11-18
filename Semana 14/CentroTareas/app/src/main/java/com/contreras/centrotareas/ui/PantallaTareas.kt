package com.contreras.centrotareas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

import com.contreras.centrotareas.model.CargaUi
import com.contreras.centrotareas.model.PrimosUi
import com.contreras.centrotareas.viewmodel.TareasViewModel

import kotlin.math.roundToInt

/**
 * Pantalla principal de CentroTareas.
 *
 * Implementa la interfaz mediante Jetpack Compose
 * y observa los estados del ViewModel.
 *
 * Laboratorio 14: Hilos y corrutinas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaTareas(
    vm: TareasViewModel
) {

    // ==========================================
    // ESTADOS DEL VIEWMODEL
    // ==========================================

    // Mensaje de la tarea con progreso.
    val mensaje by vm.mensaje.collectAsState()

    // Porcentaje de progreso.
    val progreso by vm.progreso.collectAsState()

    // Indica si la tarea está ejecutándose.
    val corriendo by vm.corriendo.collectAsState()

    // Estado del cálculo de números primos.
    val primos by vm.primos.collectAsState()

    // Estado de las cargas simuladas.
    val carga by vm.carga.collectAsState()


    // ==========================================
    // ESTADOS LOCALES
    // ==========================================

    // Contador utilizado para comprobar que la UI responde.
    var toques by remember {
        mutableStateOf(0)
    }

    // Número máximo para calcular primos.
    var numeroPrimos by remember {
        mutableStateOf("1000")
    }

    // Interruptor para simular errores.
    var simularError by remember {
        mutableStateOf(false)
    }

    // Comprueba si se están calculando números primos.
    val estaCalculando = primos is PrimosUi.Calculando

    // Valida el número ingresado.
    val numeroValido = numeroPrimos.toIntOrNull()?.let {
        it >= 2
    } == true


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
                            text = "Ejecuta una tarea sin bloquear la interfaz."
                        )


                        // =====================================
                        // CONTADOR DE TOQUES
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
                        // BOTONES INICIAR Y CANCELAR
                        // =====================================

                        Row(

                            horizontalArrangement = Arrangement.spacedBy(8.dp)

                        ) {

                            Button(

                                onClick = {
                                    vm.tareaLarga()
                                },

                                enabled = !corriendo

                            ) {

                                Text("Iniciar")
                            }


                            OutlinedButton(

                                onClick = {
                                    vm.cancelar()
                                },

                                enabled = corriendo

                            ) {

                                Text("Cancelar")
                            }
                        }


                        // =====================================
                        // BARRA DE PROGRESO
                        // =====================================

                        LinearProgressIndicator(

                            progress = {
                                progreso
                            },

                            modifier = Modifier.fillMaxWidth()
                        )


                        // =====================================
                        // PORCENTAJE
                        // =====================================

                        Text(

                            text = "Progreso: ${(progreso * 100).roundToInt()} %"
                        )


                        // =====================================
                        // MENSAJE
                        // =====================================

                        Text(
                            text = "Estado: $mensaje"
                        )
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

                        Text(
                            text = "Calcula números primos utilizando Dispatchers.Default."
                        )


                        // =====================================
                        // CAMPO NUMÉRICO
                        // =====================================

                        OutlinedTextField(

                            value = numeroPrimos,

                            onValueChange = { nuevoTexto ->

                                // Permite ingresar solo números.
                                numeroPrimos = nuevoTexto.filter {
                                    it.isDigit()
                                }
                            },

                            label = {
                                Text("Número máximo")
                            },

                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),

                            modifier = Modifier.fillMaxWidth(),

                            singleLine = true
                        )


                        // =====================================
                        // BOTONES CONTAR Y CANCELAR
                        // =====================================

                        Row(

                            horizontalArrangement = Arrangement.spacedBy(8.dp)

                        ) {

                            Button(

                                onClick = {

                                    numeroPrimos.toIntOrNull()?.let { numero ->

                                        vm.contar(numero)
                                    }
                                },

                                enabled = !estaCalculando && numeroValido

                            ) {

                                Text("Contar")
                            }


                            OutlinedButton(

                                onClick = {
                                    vm.cancelarPrimos()
                                },

                                enabled = estaCalculando

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
                                    "Presiona Contar para iniciar el cálculo."
                                )
                            }


                            PrimosUi.Calculando -> {

                                CircularProgressIndicator()

                                Text(
                                    "Calculando números primos..."
                                )
                            }


                            is PrimosUi.Listo -> {

                                // Resultado en un recuadro verde.
                                Card(

                                    colors = CardDefaults.cardColors(

                                        containerColor = Color(0xFFE8F5E9)
                                    ),

                                    modifier = Modifier.fillMaxWidth()

                                ) {

                                    Column(

                                        modifier = Modifier.padding(16.dp),

                                        verticalArrangement = Arrangement.spacedBy(8.dp)

                                    ) {

                                        Text(

                                            text = "Cálculo completado",

                                            style = MaterialTheme.typography.titleMedium
                                        )

                                        Text(
                                            text = "Cantidad de primos: ${estado.cantidad}"
                                        )

                                        Text(
                                            text = "Tiempo: ${estado.milisegundos} ms"
                                        )
                                    }
                                }
                            }


                            PrimosUi.Cancelado -> {

                                Text(

                                    text = "Cálculo cancelado",

                                    color = MaterialTheme.colorScheme.error
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
                            text = "Compara la ejecución secuencial y paralela de dos operaciones."
                        )


                        // =====================================
                        // BOTONES DE CARGA
                        // =====================================

                        Row(

                            horizontalArrangement = Arrangement.spacedBy(8.dp)

                        ) {

                            OutlinedButton(

                                onClick = {

                                    // TODO Paso 6:
                                    // Ejecutar cargarUnaPorUna().
                                },

                                enabled = false

                            ) {

                                Text("Una por una")
                            }


                            Button(

                                onClick = {

                                    // TODO Paso 6:
                                    // Ejecutar cargarALaVez().
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

                                CircularProgressIndicator()

                                Text(
                                    "Cargando información..."
                                )
                            }


                            is CargaUi.Exito -> {

                                Card(

                                    colors = CardDefaults.cardColors(

                                        containerColor = Color(0xFFE8F5E9)
                                    ),

                                    modifier = Modifier.fillMaxWidth()

                                ) {

                                    Column(

                                        modifier = Modifier.padding(16.dp),

                                        verticalArrangement = Arrangement.spacedBy(8.dp)

                                    ) {

                                        Text(
                                            text = "Modo: ${estado.modo}"
                                        )

                                        Text(
                                            text = "Perfil: ${estado.perfil}"
                                        )

                                        Text(
                                            text = "Pedidos: ${estado.pedidos}"
                                        )

                                        Text(
                                            text = "Tiempo: ${estado.milisegundos} ms"
                                        )
                                    }
                                }
                            }


                            is CargaUi.Error -> {

                                Text(

                                    text = estado.mensaje,

                                    color = MaterialTheme.colorScheme.error
                                )


                                OutlinedButton(

                                    onClick = {

                                        // TODO Paso 7:
                                        // Implementar reintento.
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
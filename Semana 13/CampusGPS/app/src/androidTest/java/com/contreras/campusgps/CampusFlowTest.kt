package com.contreras.campusgps

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.contreras.campusgps.data.*
import com.contreras.campusgps.model.*
import com.contreras.campusgps.ui.PantallaMapa
import com.contreras.campusgps.ui.theme.CampusGPSTheme
import com.contreras.campusgps.viewmodel.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class CampusFlowTest {
    @get:Rule val compose = createComposeRule()
    private lateinit var vm: MapaViewModel
    private fun preparar(error: Throwable? = null) {
        vm = MapaViewModel(object : UbicacionRepository {
            override suspend fun obtenerPosicion() = error?.let { Result.failure<Posicion>(it) }
                ?: Result.success(Posicion(-12.0465, -77.0441))
        }, LugaresRepositoryLocal())
        compose.setContent { CampusGPSTheme { PantallaMapa(vm) } }
    }

    @Test fun buscarFiltrarYLimpiarSinGps() {
        preparar()
        compose.onNodeWithText("Buscar por nombre").performScrollTo().performTextInput("cafeteria")
        compose.onNodeWithText("Cafetería Campus").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Estudio", useUnmergedTree = true).performScrollTo().performClick()
        compose.onNodeWithText("No hay resultados. Cambia el nombre o la categoría.").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Todas").performScrollTo().performClick()
        compose.onNodeWithText("Limpiar").performScrollTo().performClick()
        compose.onNodeWithText("5 resultados · catálogo de demostración").assertExists()
    }

    @Test fun seleccionarYControlarRecorrido() {
        preparar()
        compose.runOnIdle { vm.buscar() }
        compose.waitUntil(5000) { vm.estado.value is UbicacionUi.Lista }
        compose.onNodeWithText("Biblioteca Central").performScrollTo().performClick()
        compose.onNodeWithText("Ver recorrido").performScrollTo().performClick()
        compose.runOnIdle {
            vm.iniciarRecorrido()
            vm.pausarRecorrido()
            assertEquals(EstadoReproduccion.PAUSADO, vm.reproduccion.value.estado)
            vm.iniciarRecorrido()
            vm.detenerRecorrido()
            assertEquals(EstadoReproduccion.DETENIDO, vm.reproduccion.value.estado)
            vm.reiniciarRecorrido()
            assertEquals(EstadoReproduccion.REPRODUCIENDO, vm.reproduccion.value.estado)
            vm.cerrarRecorrido()
        }
        compose.runOnIdle { assertNull(vm.recorrido.value); assertEquals(Reproduccion(), vm.reproduccion.value) }
    }

    @Test fun gpsApagadoMuestraAccionEspecifica() {
        preparar(GpsDesactivadoException())
        compose.runOnIdle { vm.buscar() }
        compose.onNodeWithText("Activar ubicación").performScrollTo().assertIsDisplayed()
        compose.runOnIdle { assertEquals(TipoErrorUbicacion.GPS_APAGADO, (vm.estado.value as UbicacionUi.Error).tipo) }
    }

    @Test fun permisoDenegadoMuestraAjustes() {
        preparar()
        compose.runOnIdle { vm.permisoDenegado() }
        compose.onNodeWithText("Configurar permisos").performScrollTo().assertIsDisplayed()
    }

    @Test fun serviciosGoogleNoDisponibles() {
        preparar(PlayServicesNoDisponiblesException())
        compose.runOnIdle { vm.buscar() }
        compose.waitUntil(5000) { vm.estado.value is UbicacionUi.Error }
        compose.runOnIdle { assertEquals(TipoErrorUbicacion.PLAY_SERVICES, (vm.estado.value as UbicacionUi.Error).tipo) }
    }
}

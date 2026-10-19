package com.contreras.huellitasvet.viewmodel
import com.contreras.huellitasvet.data.local.entity.Servicio
import org.junit.Assert.*
import org.junit.Test

class ServiciosValidacionTest {
    private val valido = Servicio(nombre="Consulta",descripcion="",duracionMin=30,precio=50.0,icono="Pets")
    @Test fun nombreObligatorioYPrecioPositivoFinito() {
        assertTrue("nombre" in validarServicio(valido.copy(nombre="   ")))
        for(precio in listOf(0.0,-1.0,Double.NaN,Double.POSITIVE_INFINITY)) {
            assertTrue("precio" in validarServicio(valido.copy(precio=precio)))
        }
        assertTrue(validarServicio(valido.copy(precio=0.1)).isEmpty())
    }
    @Test fun soloAceptaLasCuatroDuraciones() {
        for(duracion in listOf(15,30,45,60)) {
            assertTrue(validarServicio(valido.copy(duracionMin=duracion)).isEmpty())
        }
        for(duracion in listOf(0,20,90)) {
            assertTrue("duracion" in validarServicio(valido.copy(duracionMin=duracion)))
        }
    }
    @Test fun permiteGuardarDesactivadoPeroRequiereIcono() {
        assertTrue(validarServicio(valido.copy(activo=false)).isEmpty())
        assertTrue("icono" in validarServicio(valido.copy(icono="")))
    }
}

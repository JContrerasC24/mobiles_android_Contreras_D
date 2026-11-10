package com.tecsup.inventarioherramientas

import com.tecsup.inventarioherramientas.data.auth.AuthRepository
import com.tecsup.inventarioherramientas.domain.model.*
import com.tecsup.inventarioherramientas.ui.UiState
import com.tecsup.inventarioherramientas.ui.login.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    private val sesion = Sesion("123", "taller@example.org", Rol.ADMIN)
    private val dispatcher = StandardTestDispatcher()
    private var guardada: Sesion? = null
    private var correoRecibido: String? = null
    private val repo = object : AuthRepository {
        override suspend fun sesionActual() = guardada
        override suspend fun iniciarSesion(correo: String, clave: String): Result<Sesion> {
            correoRecibido = correo
            return Result.success(sesion)
        }
        override fun cerrarSesion() { guardada = null }
    }

    @Before fun preparar() = Dispatchers.setMain(dispatcher)
    @After fun terminar() = Dispatchers.resetMain()

    @Test fun recuperaPerfilGuardado() {
        guardada = sesion
        val vm = LoginViewModel(repo)
        dispatcher.scheduler.runCurrent()
        assertEquals(UiState.Exito(sesion), vm.estado.value)
    }

    @Test fun rechazaClaveVaciaAntesDeAutenticar() {
        val vm = LoginViewModel(repo)
        dispatcher.scheduler.runCurrent()
        vm.iniciarSesion(sesion.correo, " ")
        dispatcher.scheduler.runCurrent()
        assertTrue(vm.estado.value is UiState.Error)
        assertNull(correoRecibido)
    }

    @Test fun autenticaConCorreoSinEspacios() {
        val vm = LoginViewModel(repo)
        dispatcher.scheduler.runCurrent()
        vm.iniciarSesion("  ${sesion.correo} ", "ejemplo")
        dispatcher.scheduler.runCurrent()
        assertEquals(sesion.correo, correoRecibido)
        assertEquals(UiState.Exito(sesion), vm.estado.value)
    }
}

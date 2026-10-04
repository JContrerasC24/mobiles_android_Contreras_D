package com.contreras.saludplus.citas.data.repository

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.contreras.saludplus.citas.data.model.Cita
import com.contreras.saludplus.citas.data.model.Especialidad
import com.contreras.saludplus.citas.data.model.Medico
import com.contreras.saludplus.citas.data.model.Usuario
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

// Centraliza los datos de la aplicación en memoria.
object Repositorio {

    // Almacena los usuarios registrados durante la ejecución.
    private val usuarios = mutableStateListOf<Usuario>()

    // Almacena las reservas y permite actualizar las pantallas.
    private val citas = mutableStateListOf<Cita>()

    // Mantiene la sesión del paciente actual.
    var usuarioActual by mutableStateOf<Usuario?>(null)
        private set

    // Define las especialidades disponibles de la clínica.
    private val especialidades = listOf(
        Especialidad(
            id = 1,
            nombre = "Medicina General",
            descripcion = "Atención integral y prevención"
        ),
        Especialidad(
            id = 2,
            nombre = "Pediatría",
            descripcion = "Atención de niños y adolescentes"
        ),
        Especialidad(
            id = 3,
            nombre = "Ginecología",
            descripcion = "Salud y atención de la mujer"
        ),
        Especialidad(
            id = 4,
            nombre = "Cardiología",
            descripcion = "Evaluación del corazón"
        ),
        Especialidad(
            id = 5,
            nombre = "Dermatología",
            descripcion = "Cuidado de la piel"
        ),
        Especialidad(
            id = 6,
            nombre = "Traumatología",
            descripcion = "Atención de huesos y articulaciones"
        ),
        Especialidad(
            id = 7,
            nombre = "Oftalmología",
            descripcion = "Evaluación de la visión"
        )
    )

    // Relaciona cada médico con su especialidad.
    private val medicos = listOf(
        Medico(
            id = 1,
            especialidadId = 1,
            nombre = "Dra. Ana Torres",
            descripcion = "Medicina General",
            calificacion = 4.9,
            precioConsulta = 80.0
        ),
        Medico(
            id = 2,
            especialidadId = 1,
            nombre = "Dr. Luis Ramírez",
            descripcion = "Medicina General",
            calificacion = 4.7,
            precioConsulta = 75.0
        ),
        Medico(
            id = 3,
            especialidadId = 2,
            nombre = "Dra. Carla Rojas",
            descripcion = "Pediatría",
            calificacion = 4.8,
            precioConsulta = 90.0
        ),
        Medico(
            id = 4,
            especialidadId = 3,
            nombre = "Dra. Mariana Soto",
            descripcion = "Ginecología",
            calificacion = 4.9,
            precioConsulta = 100.0
        ),
        Medico(
            id = 5,
            especialidadId = 4,
            nombre = "Dr. Pedro Flores",
            descripcion = "Cardiología",
            calificacion = 4.8,
            precioConsulta = 120.0
        ),
        Medico(
            id = 6,
            especialidadId = 5,
            nombre = "Dra. Lucía Vega",
            descripcion = "Dermatología",
            calificacion = 4.7,
            precioConsulta = 95.0
        ),
        Medico(
            id = 7,
            especialidadId = 6,
            nombre = "Dr. Jorge Díaz",
            descripcion = "Traumatología",
            calificacion = 4.6,
            precioConsulta = 100.0
        ),
        Medico(
            id = 8,
            especialidadId = 7,
            nombre = "Dra. Rosa Medina",
            descripcion = "Oftalmología",
            calificacion = 4.8,
            precioConsulta = 90.0
        )
    )

    // Define los horarios habituales de atención.
    private val horariosBase = listOf(
        "08:00",
        "08:30",
        "09:00",
        "09:30",
        "10:00",
        "10:30",
        "11:00",
        "11:30",
        "14:00",
        "14:30",
        "15:00",
        "15:30"
    )

    // Conserva identificadores únicos aunque se cancelen citas.
    private var siguienteCitaId = 1

    // Valida los datos, registra al usuario e inicia su sesión.
    fun registrarUsuario(
        nombres: String,
        correo: String,
        telefono: String,
        contrasena: String
    ): Boolean {
        val nombresLimpios = nombres.trim()
        val correoLimpio = correo.trim()
        val telefonoLimpio = telefono.trim()

        // Rechaza nombres vacíos.
        if (nombresLimpios.isBlank()) {
            return false
        }

        // Comprueba el formato del correo.
        if (!Patterns.EMAIL_ADDRESS.matcher(correoLimpio).matches()) {
            return false
        }

        // Solicita un teléfono de nueve dígitos.
        if (
            telefonoLimpio.length != 9 ||
            !telefonoLimpio.all { it.isDigit() }
        ) {
            return false
        }

        // Solicita una contraseña de al menos seis caracteres.
        if (contrasena.isBlank() || contrasena.length < 6) {
            return false
        }

        // Evita registrar correos duplicados.
        val correoRegistrado = usuarios.any {
            it.correo.equals(correoLimpio, ignoreCase = true)
        }

        if (correoRegistrado) {
            return false
        }

        // Genera un identificador para el nuevo usuario.
        val nuevoId = (usuarios.maxOfOrNull { it.id } ?: 0) + 1

        val nuevoUsuario = Usuario(
            id = nuevoId,
            nombres = nombresLimpios,
            correo = correoLimpio,
            telefono = telefonoLimpio,
            contrasena = contrasena
        )

        usuarios.add(nuevoUsuario)
        usuarioActual = nuevoUsuario

        return true
    }

    // Busca un usuario cuyas credenciales coincidan.
    fun iniciarSesion(
        correo: String,
        contrasena: String
    ): Boolean {
        val usuarioEncontrado = usuarios.find {
            it.correo.equals(correo.trim(), ignoreCase = true) &&
                    it.contrasena == contrasena
        } ?: return false

        usuarioActual = usuarioEncontrado

        return true
    }

    // Elimina la sesión sin borrar los usuarios registrados.
    fun cerrarSesion() {
        usuarioActual = null
    }

    // Filtra especialidades por nombre o descripción.
    fun buscarEspecialidades(
        consulta: String
    ): List<Especialidad> {
        val texto = consulta.trim()

        return especialidades.filter {
            it.nombre.contains(texto, ignoreCase = true) ||
                    it.descripcion.contains(texto, ignoreCase = true)
        }
    }

    // Devuelve las primeras especialidades para mostrarlas en Inicio.
    fun especialidadesDestacadas(
        cantidad: Int = 3
    ): List<Especialidad> {
        return especialidades.take(cantidad.coerceAtLeast(0))
    }

    // Busca una especialidad mediante su identificador.
    fun obtenerEspecialidad(
        especialidadId: Int
    ): Especialidad? {
        return especialidades.find {
            it.id == especialidadId
        }
    }

    // Busca un médico mediante su identificador.
    fun obtenerMedico(
        medicoId: Int
    ): Medico? {
        return medicos.find {
            it.id == medicoId
        }
    }

    // Filtra médicos por especialidad y ordena sus calificaciones.
    fun medicosPorEspecialidad(
        especialidadId: Int
    ): List<Medico> {
        return medicos
            .filter {
                it.especialidadId == especialidadId
            }
            .sortedByDescending {
                it.calificacion
            }
    }

    // Busca médicos dentro de la especialidad seleccionada.
    fun buscarMedicos(
        especialidadId: Int,
        consulta: String
    ): List<Medico> {
        val texto = consulta.trim()

        return medicosPorEspecialidad(especialidadId).filter {
            it.nombre.contains(texto, ignoreCase = true) ||
                    it.descripcion.contains(texto, ignoreCase = true)
        }
    }

    // Devuelve horarios futuros que todavía no están reservados.
    fun horariosDisponibles(
        medicoId: Int,
        fecha: String
    ): List<String> {

        // Comprueba que el médico exista.
        if (obtenerMedico(medicoId) == null) {
            return emptyList()
        }

        // Exige fechas con formato año, mes y día.
        if (!Regex("\\d{4}-\\d{2}-\\d{2}").matches(fecha)) {
            return emptyList()
        }

        val formatoFecha = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        ).apply {
            isLenient = false
        }

        // Rechaza fechas inexistentes o formatos incorrectos.
        try {
            val fechaInterpretada = formatoFecha.parse(fecha)
                ?: return emptyList()

            if (formatoFecha.format(fechaInterpretada) != fecha) {
                return emptyList()
            }
        } catch (_: ParseException) {
            return emptyList()
        }

        // Identifica las horas reservadas para ese médico y fecha.
        val horasOcupadas = citas
            .filter {
                it.medicoId == medicoId && it.fecha == fecha
            }
            .map {
                it.hora
            }

        val formatoHorario = SimpleDateFormat(
            "yyyy-MM-dd HH:mm",
            Locale.US
        ).apply {
            isLenient = false
        }

        val ahora = System.currentTimeMillis()

        // Excluye horarios ocupados y horas que ya pasaron.
        return horariosBase.filter { hora ->
            val momento = formatoHorario.parse("$fecha $hora")

            hora !in horasOcupadas &&
                    momento != null &&
                    momento.time > ahora
        }
    }

    // Valida la reserva y guarda la cita en memoria.
    fun agendarCita(
        medicoId: Int,
        fecha: String,
        hora: String,
        motivo: String = ""
    ): Cita? {
        val usuario = usuarioActual ?: return null

        // Comprueba que el médico exista.
        if (obtenerMedico(medicoId) == null) {
            return null
        }

        // Evita reservar horarios ocupados o pasados.
        if (hora !in horariosDisponibles(medicoId, fecha)) {
            return null
        }

        // Comprueba que ninguna cita ocupe ese horario.
        val horarioOcupado = citas.any {
            it.medicoId == medicoId &&
                    it.fecha == fecha &&
                    it.hora == hora
        }

        if (horarioOcupado) {
            return null
        }

        val nuevaCita = Cita(
            id = siguienteCitaId,
            usuarioId = usuario.id,
            medicoId = medicoId,
            fecha = fecha,
            hora = hora,
            motivo = motivo.trim()
        )

        citas.add(nuevaCita)
        siguienteCitaId += 1

        return nuevaCita
    }

    // Filtra las citas del paciente y ordena por fecha y hora.
    fun citasDelUsuario(): List<Cita> {
        val usuario = usuarioActual ?: return emptyList()

        return citas
            .filter {
                it.usuarioId == usuario.id
            }
            .sortedWith(
                compareBy<Cita> { it.fecha }
                    .thenBy { it.hora }
            )
    }

    // Busca una cita perteneciente al usuario actual.
    fun obtenerCita(
        citaId: Int
    ): Cita? {
        val usuario = usuarioActual ?: return null

        return citas.find {
            it.id == citaId && it.usuarioId == usuario.id
        }
    }

    // Elimina únicamente una cita perteneciente al paciente actual.
    fun cancelarCita(
        citaId: Int
    ): Boolean {
        val usuario = usuarioActual ?: return false

        return citas.removeAll {
            it.id == citaId && it.usuarioId == usuario.id
        }
    }
}
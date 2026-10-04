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

// Centraliza los datos compartidos durante la ejecución.
object Repositorio {

    // Almacena pacientes y citas únicamente en memoria.
    private val usuarios = mutableStateListOf<Usuario>()
    private val citas = mutableStateListOf<Cita>()

    // Permite observar cambios en la sesión desde Compose.
    var usuarioActual by mutableStateOf<Usuario?>(null)
        private set

    // Contiene las especialidades ficticias de la clínica.
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

    // Relaciona cada médico con su especialidad mediante especialidadId.
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

    // Define los horarios iniciales antes de descontar reservas.
    private val horariosBase = listOf(
        "08:00", "08:30", "09:00", "09:30",
        "10:00", "10:30", "11:00", "11:30",
        "14:00", "14:30", "15:00", "15:30"
    )

    // Valida los datos y registra pacientes sin correos duplicados.
    fun registrarUsuario(
        nombres: String,
        correo: String,
        telefono: String,
        contrasena: String
    ): Boolean {
        // Limpia espacios sin modificar la contraseña.
        val nombreLimpio = nombres.trim()
        val correoLimpio = correo.trim()
        val telefonoLimpio = telefono.trim()

        // Rechaza nombres vacíos y correos inválidos.
        if (nombreLimpio.isBlank()) return false

        if (!Patterns.EMAIL_ADDRESS.matcher(correoLimpio).matches()) {
            return false
        }

        // Requiere nueve dígitos y una contraseña mínima.
        if (telefonoLimpio.length != 9) return false
        if (!telefonoLimpio.all { it.isDigit() }) return false
        if (contrasena.length < 6 || contrasena.isBlank()) return false

        // Busca correos duplicados ignorando mayúsculas y minúsculas.
        val correoExiste = usuarios.any {
            it.correo.equals(correoLimpio, ignoreCase = true)
        }

        if (correoExiste) return false

        // Calcula un identificador disponible para el paciente.
        val nuevoId = (usuarios.maxOfOrNull { it.id } ?: 0) + 1

        val nuevoUsuario = Usuario(
            id = nuevoId,
            nombres = nombreLimpio,
            correo = correoLimpio,
            telefono = telefonoLimpio,
            contrasena = contrasena
        )

        // Guarda al paciente e inicia su sesión.
        usuarios.add(nuevoUsuario)
        usuarioActual = nuevoUsuario

        return true
    }

    // Busca credenciales y establece la sesión cuando coinciden.
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

    // Finaliza la sesión sin eliminar usuarios ni citas.
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

    // Obtiene una cantidad limitada de especialidades destacadas.
    fun especialidadesDestacadas(
        cantidad: Int = 3
    ): List<Especialidad> {
        return especialidades.take(cantidad.coerceAtLeast(0))
    }

    // Busca la especialidad correspondiente al identificador recibido.
    fun obtenerEspecialidad(
        especialidadId: Int
    ): Especialidad? {
        return especialidades.find {
            it.id == especialidadId
        }
    }

    // Busca el médico correspondiente al identificador recibido.
    fun obtenerMedico(
        medicoId: Int
    ): Medico? {
        return medicos.find {
            it.id == medicoId
        }
    }

    // Filtra médicos por especialidad y ordena sus valoraciones.
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

    // TODO: Excluir horarios ocupados para el médico y fecha seleccionados.
    fun horariosDisponibles(
        medicoId: Int,
        fecha: String
    ): List<String> {
        return emptyList()
    }

    // TODO: Validar sesión y disponibilidad antes de guardar la cita.
    fun agendarCita(
        medicoId: Int,
        fecha: String,
        hora: String,
        motivo: String = ""
    ): Cita? {
        return null
    }

    // TODO: Filtrar citas del usuario actual y ordenar cronológicamente.
    fun citasDelUsuario(): List<Cita> {
        return emptyList()
    }

    // TODO: Buscar una cita perteneciente al usuario actual.
    fun obtenerCita(
        citaId: Int
    ): Cita? {
        return null
    }

    // TODO: Eliminar únicamente una cita perteneciente al usuario actual.
    fun cancelarCita(
        citaId: Int
    ): Boolean {
        return false
    }
}
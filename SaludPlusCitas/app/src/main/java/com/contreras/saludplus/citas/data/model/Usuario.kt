package com.contreras.saludplus.citas.data.model

// Representa al paciente registrado en la aplicación.
data class Usuario(
    // Identificador único del paciente.
    val id: Int,

    // Nombres del paciente para el perfil y saludo.
    val nombres: String,

    // Correo utilizado para iniciar sesión.
    val correo: String,

    // Número telefónico de contacto.
    val telefono: String,

    // Contraseña ficticia para practicar autenticación en memoria.
    val contrasena: String
)


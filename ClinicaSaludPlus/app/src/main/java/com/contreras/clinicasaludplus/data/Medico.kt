package com.contreras.clinicasaludplus.data

/**
 * Modelo de un medico disponible en la clinica.
 * Se usa tanto en la lista de Inicio como en el Perfil del medico
 * (los datos viajan por navegacion segun el id).
 */
data class Medico(
    val id: Int,
    val nombre: String,
    val especialidad: String,
    val calificacion: Float,
    val numeroResenas: Int,
    val aniosExperiencia: Int,
    val descripcion: String
)

// ---- Datos de ejemplo (mock, sin backend) ----

val especialidades = listOf("Cardiologia", "Pediatria", "Dermatologia")

val listaMedicos = listOf(
    Medico(
        id = 1,
        nombre = "Dra. Ana Torres",
        especialidad = "Cardiologia",
        calificacion = 4.9f,
        numeroResenas = 128,
        aniosExperiencia = 12,
        descripcion = "Especialista en arritmias e hipertension, formacion en la Clinica Mayo."
    ),
    Medico(
        id = 2,
        nombre = "Dr. Luis Vega",
        especialidad = "Pediatria",
        calificacion = 4.7f,
        numeroResenas = 96,
        aniosExperiencia = 8,
        descripcion = "Enfocado en el desarrollo infantil y control de ninio sano."
    ),
    Medico(
        id = 3,
        nombre = "Dra. Rosa Diaz",
        especialidad = "Dermatologia",
        calificacion = 4.8f,
        numeroResenas = 74,
        aniosExperiencia = 10,
        descripcion = "Especialista en dermatologia clinica y esteticas."
    )
)
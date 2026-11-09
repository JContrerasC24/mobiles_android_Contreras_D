package com.contreras.myapplication.model

import com.contreras.myapplication.ui.theme.JOSE_AVATAR_URL

// Modelo de datos para representar a un estudiante
data class Student(
    val id: Int,
    val name: String,
    val career: String,
    val avatarUrl: String,
    val studentCode: String,
    val email: String,
    val faculty: String,
    val bio: String
)

// Repositorio singleton con la lista de estudiantes
object StudentRepository {
    // Lista de 5 alumnos con avatares reales de randomuser.me
    val students = listOf(
        Student(
            id = 1,
            name = "Jose Contreras",
            career = "Diseño y Desarrollo de Software",
            avatarUrl = JOSE_AVATAR_URL,
            studentCode = "2024-0001",
            email = "jose.contreras.ca@tecsup.edu.pe",
            faculty = "Ingeniería y Tecnología",
            bio = "Estudiante destacado con interés en desarrollo Android."
        ),
        Student(
            id = 2,
            name = "Lucía Fernández",
            career = "Ingeniería Civil",
            avatarUrl = "https://randomuser.me/api/portraits/women/61.jpg",
            studentCode = "2024-0002",
            email = "lucia.fernandez@tecsup.edu.pe",
            faculty = "Ingeniería y Tecnología",
            bio = "Interesada en proyectos de infraestructura sostenible."
        ),
        Student(
            id = 3,
            name = "Miguel Torres",
            career = "Ciencias de la Comunicación",
            avatarUrl = "https://randomuser.me/api/portraits/men/77.jpg",
            studentCode = "2024-0003",
            email = "miguel.torres@tecsup.edu.pe",
            faculty = "Comunicaciones",
            bio = "Enfocado en producción audiovisual y periodismo digital."
        ),
        Student(
            id = 4,
            name = "Valeria Rojas",
            career = "Psicología",
            avatarUrl = "https://randomuser.me/api/portraits/women/24.jpg",
            studentCode = "2024-0004",
            email = "valeria.rojas@tecsup.edu.pe",
            faculty = "Ciencias Sociales",
            bio = "Apasionada por la psicología organizacional."
        ),
        Student(
            id = 5,
            name = "Diego Castillo",
            career = "Contabilidad",
            avatarUrl = "https://randomuser.me/api/portraits/men/19.jpg",
            studentCode = "2024-0005",
            email = "diego.castillo@tecsup.edu.pe",
            faculty = "Ciencias Empresariales",
            bio = "Interesado en auditoría financiera y análisis contable."
        )
    )
}

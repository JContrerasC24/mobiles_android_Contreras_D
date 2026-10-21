package com.tecsup.tecsupstore

data class Producto(val id: Int, val nombre: String, val precio: Double)

val productosDemo = listOf(
    Producto(1, "Audifonos", 89.00),
    Producto(2, "Smartwatch", 199.00),
    Producto(3, "Funda celular", 25.00)
)
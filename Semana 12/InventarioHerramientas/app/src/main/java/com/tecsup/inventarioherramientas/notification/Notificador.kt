package com.tecsup.inventarioherramientas.notification

interface Notificador {

    fun mostrar(
        titulo: String,
        texto: String
    )
}
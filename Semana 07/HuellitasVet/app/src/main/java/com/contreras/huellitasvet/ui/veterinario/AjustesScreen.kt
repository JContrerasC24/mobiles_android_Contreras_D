package com.contreras.huellitasvet.ui.veterinario
import androidx.compose.runtime.Composable
import com.contreras.huellitasvet.ui.dueno.PreferenciasContenido
import com.contreras.huellitasvet.viewmodel.PreferenciasViewModel

@Composable
fun AjustesScreen(vm:PreferenciasViewModel,onVolver:()->Unit,onCambiar:()->Unit,onCerrar:()->Unit,ocupado:Boolean,errorSesion:String?) {
    PreferenciasContenido(vm,true,onVolver,onCambiar,onCerrar,ocupado,errorSesion)
}

package com.example.master_martini.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.master_martini.data.local.entity.Usuario
import com.example.master_martini.data.repository.MasterMartiniRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed interface EstadoSesion {
    data object Cargando : EstadoSesion
    data class Listo(val usuario: Usuario?) : EstadoSesion
}

class SesionViewModel(repositorio: MasterMartiniRepository) : ViewModel() {

    val estado: StateFlow<EstadoSesion> = repositorio.usuarioActual
        .map<Usuario?, EstadoSesion> { EstadoSesion.Listo(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EstadoSesion.Cargando)
}
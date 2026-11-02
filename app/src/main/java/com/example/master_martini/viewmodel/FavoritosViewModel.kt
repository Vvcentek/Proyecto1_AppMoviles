package com.example.master_martini.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.master_martini.data.local.entity.Contenido
import com.example.master_martini.data.repository.MasterMartiniRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FavoritosUiState(
    val cargando: Boolean = true,
    val usuarioId: Long? = null,
    val favoritos: List<Contenido> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritosViewModel(private val repositorio: MasterMartiniRepository) : ViewModel() {

    val estado: StateFlow<FavoritosUiState> = repositorio.usuarioActual
        .flatMapLatest { usuario ->
            if (usuario == null) flowOf(FavoritosUiState(cargando = false))
            else repositorio.favoritosDe(usuario.id).map { lista ->
                FavoritosUiState(cargando = false, usuarioId = usuario.id, favoritos = lista)
            }
        }
        .onStart { delay(CARGA_MINIMA_MS) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FavoritosUiState())

    fun quitar(contenidoId: Long) {
        val usuarioId = estado.value.usuarioId ?: return
        viewModelScope.launch {
            repositorio.alternarFavorito(usuarioId, contenidoId, esFavoritoAhora = true)
        }
    }
}
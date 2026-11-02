package com.example.master_martini.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.master_martini.data.local.entity.Contenido
import com.example.master_martini.data.repository.MasterMartiniRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DetalleUiState(
    val cargando: Boolean = true,
    val usuarioId: Long? = null,
    val contenido: Contenido? = null,
    val esFavorito: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
class DetalleViewModel(
    savedStateHandle: SavedStateHandle,
    private val repositorio: MasterMartiniRepository
) : ViewModel() {

    companion object {
        // Nombre del argumento que llega desde la navegación: detalle/{contenidoId}
        const val ARG_ID = "contenidoId"
    }

    private val contenidoId: Long = checkNotNull(savedStateHandle.get<Long>(ARG_ID))

    val estado: StateFlow<DetalleUiState> = repositorio.usuarioActual
        .flatMapLatest { usuario ->
            combine(
                repositorio.contenido(contenidoId),
                repositorio.idsFavoritosDe(usuario?.id ?: SIN_USUARIO)
            ) { contenido, favoritos ->
                DetalleUiState(
                    cargando = false,
                    usuarioId = usuario?.id,
                    contenido = contenido,
                    esFavorito = contenidoId in favoritos
                )
            }
        }
        .onStart { delay(CARGA_MINIMA_MS) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DetalleUiState())

    fun alternarFavorito() {
        val actual = estado.value
        val usuarioId = actual.usuarioId ?: return
        viewModelScope.launch {
            repositorio.alternarFavorito(usuarioId, contenidoId, actual.esFavorito)
        }
    }
}
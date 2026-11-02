package com.example.master_martini.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.master_martini.data.local.entity.Contenido
import com.example.master_martini.data.local.model.Categoria
import com.example.master_martini.data.repository.MasterMartiniRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CatalogoUiState(
    val cargando: Boolean = true,
    val usuarioId: Long? = null,
    val nombre: String = "",
    val categoriaSeleccionada: Categoria? = null,
    val recomendados: List<Contenido> = emptyList(),
    val contenidos: List<Contenido> = emptyList(),
    val idsFavoritos: Set<Long> = emptySet()
)

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogoViewModel(private val repositorio: MasterMartiniRepository) : ViewModel() {

    private val categoria = MutableStateFlow<Categoria?>(null)

    val estado: StateFlow<CatalogoUiState> = repositorio.usuarioActual
        .flatMapLatest { usuario ->
            val usuarioId = usuario?.id ?: SIN_USUARIO
            combine(
                repositorio.contenidos(),
                repositorio.interesesDe(usuarioId),
                repositorio.idsFavoritosDe(usuarioId),
                categoria
            ) { todos, intereses, favoritos, categoriaElegida ->
                CatalogoUiState(
                    cargando = false,
                    usuarioId = usuario?.id,
                    nombre = usuario?.nombre?.substringBefore(" ").orEmpty(),
                    categoriaSeleccionada = categoriaElegida,
                    // Recomendados: especialidades marcadas en la encuesta, primero el formato preferido.
                    recomendados = todos
                        .filter { it.categoria in intereses }
                        .sortedByDescending { it.tipo == usuario?.formatoPreferido }
                        .take(4),
                    contenidos = if (categoriaElegida == null) todos
                    else todos.filter { it.categoria == categoriaElegida },
                    idsFavoritos = favoritos
                )
            }
        }
        // Mantiene visible la animación de carga un instante al entrar a la pantalla.
        .onStart { delay(CARGA_MINIMA_MS) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CatalogoUiState())

    fun seleccionarCategoria(nueva: Categoria?) {
        categoria.value = if (categoria.value == nueva) null else nueva
    }

    fun alternarFavorito(contenidoId: Long) {
        val actual = estado.value
        val usuarioId = actual.usuarioId ?: return
        viewModelScope.launch {
            repositorio.alternarFavorito(usuarioId, contenidoId, contenidoId in actual.idsFavoritos)
        }
    }
}
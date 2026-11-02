package com.example.master_martini.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.master_martini.data.local.AlmacenFotos
import com.example.master_martini.data.local.entity.Usuario
import com.example.master_martini.data.local.model.Categoria
import com.example.master_martini.data.repository.MasterMartiniRepository
import kotlinx.coroutines.Dispatchers
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
import kotlinx.coroutines.withContext

data class PerfilUiState(
    val cargando: Boolean = true,
    val usuario: Usuario? = null,
    val intereses: Set<Categoria> = emptySet(),
    val totalFavoritos: Int = 0,
    val guardandoFoto: Boolean = false,
    val mensaje: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class PerfilViewModel(
    private val repositorio: MasterMartiniRepository,
    private val almacenFotos: AlmacenFotos
) : ViewModel() {

    private val guardandoFoto = MutableStateFlow(false)
    private val mensaje = MutableStateFlow<String?>(null)

    val estado: StateFlow<PerfilUiState> = repositorio.usuarioActual
        .flatMapLatest { usuario ->
            if (usuario == null) {
                combine(guardandoFoto, mensaje) { guardando, texto ->
                    PerfilUiState(cargando = false, guardandoFoto = guardando, mensaje = texto)
                }
            } else {
                combine(
                    repositorio.interesesDe(usuario.id),
                    repositorio.idsFavoritosDe(usuario.id),
                    guardandoFoto,
                    mensaje
                ) { intereses, favoritos, guardando, texto ->
                    PerfilUiState(
                        cargando = false,
                        usuario = usuario,
                        intereses = intereses,
                        totalFavoritos = favoritos.size,
                        guardandoFoto = guardando,
                        mensaje = texto
                    )
                }
            }
        }
        .onStart { delay(CARGA_MINIMA_MS) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PerfilUiState())

    fun onFotoTomada(bitmap: Bitmap?) {
        if (bitmap == null) {
            mensaje.value = "No se tomó ninguna foto"
            return
        }
        guardarFoto { almacenFotos.guardarBitmap(bitmap) }
    }

    fun onFotoElegida(uri: Uri?) {
        if (uri == null) return
        guardarFoto { almacenFotos.copiarDesdeGaleria(uri) }
    }

    fun onPermisoCamaraDenegado() {
        mensaje.value = "Sin permiso de cámara no podemos tomar la foto. Puedes elegir una de la galería."
    }

    fun onPermisoGaleriaDenegado() {
        mensaje.value = "Sin permiso no podemos abrir tus fotos. Puedes activarlo en Ajustes > Apps > Master Martini."
    }

    fun mensajeMostrado() {
        mensaje.value = null
    }

    private fun guardarFoto(guardar: () -> String?) {
        val usuario = estado.value.usuario ?: return
        viewModelScope.launch {
            guardandoFoto.value = true
            // Copiar archivos es trabajo pesado: se hace fuera del hilo de la interfaz.
            val ruta = withContext(Dispatchers.IO) { runCatching { guardar() }.getOrNull() }
            if (ruta != null) {
                withContext(Dispatchers.IO) { almacenFotos.borrar(usuario.fotoUri) }
                repositorio.actualizarUsuario(usuario.copy(fotoUri = ruta))
                mensaje.value = "Foto de perfil actualizada"
            } else {
                mensaje.value = "No se pudo guardar la foto"
            }
            guardandoFoto.value = false
        }
    }
}
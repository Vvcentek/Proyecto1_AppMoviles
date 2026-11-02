package com.example.master_martini.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.master_martini.ui.components.BarraSuperior
import com.example.master_martini.ui.components.EstadoVacio
import com.example.master_martini.ui.components.PantallaCarga
import com.example.master_martini.ui.components.TarjetaContenido
import com.example.master_martini.viewmodel.AppViewModelProvider
import com.example.master_martini.viewmodel.FavoritosViewModel
import com.example.master_martini.ui.components.rememberCargaDeEntrada

@Composable
fun FavoritosScreen(
    onAbrirContenido: (Long) -> Unit,
    onExplorar: () -> Unit,
    viewModel: FavoritosViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val cargaEntrada = rememberCargaDeEntrada()

    Scaffold(topBar = { BarraSuperior("Mis favoritos") }) { padding ->
        Crossfade(targetState = estado.cargando || cargaEntrada, label = "cargaFavoritos", modifier = Modifier.padding(padding)) { cargando ->
            when {
                cargando -> PantallaCarga("Cargando tus favoritos…")
                estado.favoritos.isEmpty() -> EstadoVacio(
                    icono = Icons.Filled.FavoriteBorder,
                    titulo = "Aún no tienes favoritos",
                    mensaje = "Toca el corazón en un contenido para guardarlo aquí.",
                    textoBoton = "Explorar contenidos",
                    onBoton = onExplorar
                )
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(estado.favoritos, key = { it.id }) { contenido ->
                        TarjetaContenido(
                            contenido = contenido,
                            esFavorito = true,
                            onClick = { onAbrirContenido(contenido.id) },
                            onFavorito = { viewModel.quitar(contenido.id) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }
}
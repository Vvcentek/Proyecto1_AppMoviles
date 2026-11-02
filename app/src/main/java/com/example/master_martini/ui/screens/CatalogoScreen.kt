package com.example.master_martini.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.master_martini.data.local.model.Categoria
import com.example.master_martini.ui.components.BarraSuperior
import com.example.master_martini.ui.components.EstadoVacio
import com.example.master_martini.ui.components.PantallaCarga
import com.example.master_martini.ui.components.TarjetaContenido
import com.example.master_martini.viewmodel.AppViewModelProvider
import com.example.master_martini.viewmodel.CatalogoViewModel
import com.example.master_martini.ui.components.rememberCargaDeEntrada

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    onAbrirContenido: (Long) -> Unit,
    viewModel: CatalogoViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val cargaEntrada = rememberCargaDeEntrada()

    Scaffold(topBar = { BarraSuperior("Contenidos") }) { padding ->
        // Fundido entre la animación de carga y el contenido.
        Crossfade(targetState = estado.cargando || cargaEntrada, label = "cargaCatalogo", modifier = Modifier.padding(padding)) { cargando ->
            if (cargando) {
                PantallaCarga("Preparando tus contenidos…")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Column {
                            Text(
                                if (estado.nombre.isNotEmpty()) "Hola, ${estado.nombre}" else "Bienvenido",
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Text("¿Qué quieres aprender hoy?", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            item {
                                FilterChip(
                                    selected = estado.categoriaSeleccionada == null,
                                    onClick = { viewModel.seleccionarCategoria(null) },
                                    label = { Text("Todas") }
                                )
                            }
                            items(Categoria.entries) { categoria ->
                                val seleccionada = estado.categoriaSeleccionada == categoria
                                FilterChip(
                                    selected = seleccionada,
                                    onClick = { viewModel.seleccionarCategoria(categoria) },
                                    label = { Text(categoria.etiqueta) },
                                    leadingIcon = if (seleccionada) {
                                        { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                                    } else null
                                )
                            }
                        }
                    }
                    if (estado.categoriaSeleccionada == null && estado.recomendados.isNotEmpty()) {
                        item { TituloLista("Recomendado para ti", Icons.Filled.AutoAwesome) }
                        items(estado.recomendados, key = { "recomendado-${it.id}" }) { contenido ->
                            TarjetaContenido(
                                contenido = contenido,
                                esFavorito = contenido.id in estado.idsFavoritos,
                                onClick = { onAbrirContenido(contenido.id) },
                                onFavorito = { viewModel.alternarFavorito(contenido.id) },
                                modifier = Modifier.animateItem()
                            )
                        }
                        item { TituloLista("Todos los contenidos", Icons.Filled.GridView) }
                    }
                    if (estado.contenidos.isEmpty()) {
                        item {
                            EstadoVacio(
                                icono = Icons.Filled.SearchOff,
                                titulo = "Sin contenidos",
                                mensaje = "Todavía no hay contenidos en esta especialidad."
                            )
                        }
                    }
                    items(estado.contenidos, key = { it.id }) { contenido ->
                        TarjetaContenido(
                            contenido = contenido,
                            esFavorito = contenido.id in estado.idsFavoritos,
                            onClick = { onAbrirContenido(contenido.id) },
                            onFavorito = { viewModel.alternarFavorito(contenido.id) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TituloLista(texto: String, icono: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
        Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(8.dp))
        Text(texto, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
    }
    Spacer(Modifier.height(4.dp))
}
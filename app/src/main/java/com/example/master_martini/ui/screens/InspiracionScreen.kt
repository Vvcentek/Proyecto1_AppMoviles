package com.example.master_martini.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.master_martini.ui.components.BarraSuperior
import com.example.master_martini.ui.components.EstadoVacio
import com.example.master_martini.ui.components.PantallaCarga
import com.example.master_martini.viewmodel.AppViewModelProvider
import com.example.master_martini.viewmodel.InspiracionViewModel

// Ideas de postres traídas desde la API externa TheMealDB.
@Composable
fun InspiracionScreen(
    viewModel: InspiracionViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    Scaffold(topBar = { BarraSuperior("Ideas de postres") }) { padding ->
        val modificador = Modifier.padding(padding)
        when {
            estado.cargando -> PantallaCarga("Buscando ideas…", modificador)
            estado.error != null -> EstadoVacio(
                icono = Icons.Filled.CloudOff,
                titulo = "Sin conexión",
                mensaje = estado.error ?: "",
                textoBoton = "Reintentar",
                onBoton = viewModel::cargar,
                modifier = modificador
            )
            else -> LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = modificador.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        "Inspiración internacional para tu vitrina. Fuente: TheMealDB.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                items(estado.recetas, key = { it.idMeal }) { receta ->
                    Card {
                        Column {
                            AsyncImage(
                                model = receta.strMealThumb,
                                contentDescription = receta.strMeal,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxWidth().aspectRatio(1f)
                            )
                            Text(
                                receta.strMeal,
                                style = MaterialTheme.typography.titleSmall,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

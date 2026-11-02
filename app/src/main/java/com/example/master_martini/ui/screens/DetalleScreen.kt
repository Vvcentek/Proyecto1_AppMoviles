package com.example.master_martini.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.master_martini.data.local.model.TipoContenido
import com.example.master_martini.ui.components.BarraSuperior
import com.example.master_martini.ui.components.BotonPrincipal
import com.example.master_martini.ui.components.EstadoVacio
import com.example.master_martini.ui.components.EtiquetaDato
import com.example.master_martini.ui.components.PantallaCarga
import com.example.master_martini.ui.components.iconoDe
import com.example.master_martini.viewmodel.AppViewModelProvider
import com.example.master_martini.viewmodel.DetalleViewModel

@Composable
fun DetalleScreen(
    onVolver: () -> Unit,
    viewModel: DetalleViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    Scaffold(topBar = { BarraSuperior(estado.contenido?.tipo?.etiqueta ?: "Detalle", onVolver) }) { padding ->
        Crossfade(targetState = estado.cargando, label = "cargaDetalle", modifier = Modifier.padding(padding)) { cargando ->
            val contenido = estado.contenido
            when {
                cargando -> PantallaCarga("Cargando contenido…")
                contenido == null -> EstadoVacio(
                    icono = Icons.Filled.SearchOff,
                    titulo = "Contenido no encontrado",
                    mensaje = "Vuelve al catálogo e intenta con otro."
                )
                else -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            iconoDe(contenido.tipo),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(80.dp)
                        )
                    }
                    Text(contenido.titulo, style = MaterialTheme.typography.headlineMedium)
                    Text(contenido.descripcion, style = MaterialTheme.typography.bodyLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        EtiquetaDato(Icons.Filled.Schedule, "${contenido.duracionMinutos} min")
                        EtiquetaDato(Icons.Filled.BarChart, contenido.nivel.etiqueta)
                    }
                    EtiquetaDato(Icons.Filled.Person, contenido.chef)
                    Text(
                        if (contenido.tipo == TipoContenido.CURSO) "Módulos" else "Pasos",
                        style = MaterialTheme.typography.titleLarge
                    )
                    contenido.pasos.split("\n").forEachIndexed { indice, paso ->
                        Row(verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "${indice + 1}",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(
                                paso,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(top = 4.dp)
                            )
                        }
                    }
                    BotonPrincipal(
                        texto = if (estado.esFavorito) "Quitar de favoritos" else "Guardar en favoritos",
                        onClick = viewModel::alternarFavorito,
                        icono = if (estado.esFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder
                    )
                }
            }
        }
    }
}
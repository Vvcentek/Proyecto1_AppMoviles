package com.example.master_martini.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.PlayCircle
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.master_martini.data.local.model.TipoContenido
import com.example.master_martini.ui.components.BarraSuperior
import com.example.master_martini.ui.components.BotonPrincipal
import com.example.master_martini.ui.components.EstadoVacio
import com.example.master_martini.ui.components.EtiquetaDato
import com.example.master_martini.ui.components.PantallaCarga
import com.example.master_martini.ui.components.imagenDe
import com.example.master_martini.viewmodel.AppViewModelProvider
import com.example.master_martini.viewmodel.DetalleViewModel
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color

@Composable
fun DetalleScreen(
    onVolver: () -> Unit,
    viewModel: DetalleViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val contexto = LocalContext.current

    // Abre YouTube con una búsqueda del tema (los videos reales los entregaría Master Martini).
    fun abrirVideo(titulo: String) {
        val uri = Uri.parse("https://www.youtube.com/results?search_query=" + Uri.encode(titulo))
        try {
            contexto.startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (e: ActivityNotFoundException) {
            // Sin navegador ni YouTube instalados: no se hace nada.
        }
    }

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
                    // Foto de la especialidad; si es video, se ve como miniatura y al tocarla se abre.
                    val esVideo = contenido.tipo == TipoContenido.VIDEO
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .then(if (esVideo) Modifier.clickable { abrirVideo(contenido.titulo) } else Modifier)
                    ) {
                        Image(
                            painter = painterResource(imagenDe(contenido.categoria)),
                            contentDescription = contenido.categoria.etiqueta,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )
                        if (esVideo) {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(Color.Black.copy(alpha = 0.45f))
                            )
                            Icon(
                                Icons.Filled.PlayCircle,
                                contentDescription = "Reproducir video",
                                tint = Color.White,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(80.dp)
                            )
                            // Etiqueta con el tipo y la duración, como en las plataformas de video.
                            Text(
                                "VIDEO · ${contenido.duracionMinutos} min",
                                color = Color.White,
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(12.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black.copy(alpha = 0.6f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Text(contenido.titulo, style = MaterialTheme.typography.headlineMedium)
                    Text(contenido.descripcion, style = MaterialTheme.typography.bodyLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        EtiquetaDato(Icons.Filled.Schedule, "${contenido.duracionMinutos} min")
                        EtiquetaDato(Icons.Filled.BarChart, contenido.nivel.etiqueta)
                    }
                    EtiquetaDato(Icons.Filled.Person, contenido.chef)
                    if (contenido.tipo == TipoContenido.VIDEO) {
                        BotonPrincipal(
                            texto = "Ver video",
                            onClick = { abrirVideo(contenido.titulo) },
                            icono = Icons.Filled.PlayCircle
                        )
                    }
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
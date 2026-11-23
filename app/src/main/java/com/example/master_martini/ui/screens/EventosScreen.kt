package com.example.master_martini.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.master_martini.data.local.model.Categoria
import com.example.master_martini.data.remote.EventoDto
import com.example.master_martini.ui.components.BarraSuperior
import com.example.master_martini.ui.components.EstadoVacio
import com.example.master_martini.ui.components.PantallaCarga
import com.example.master_martini.viewmodel.AppViewModelProvider
import com.example.master_martini.viewmodel.EventosViewModel

@Composable
fun EventosScreen(
    onNuevo: () -> Unit,
    onEditar: (Long) -> Unit,
    viewModel: EventosViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    var porEliminar by remember { mutableStateOf<EventoDto?>(null) }

    // Se recarga cada vez que se entra (también al volver del formulario).
    LaunchedEffect(Unit) { viewModel.cargar() }

    LaunchedEffect(estado.mensaje) {
        estado.mensaje?.let {
            snackbar.showSnackbar(it)
            viewModel.mensajeMostrado()
        }
    }

    Scaffold(
        topBar = { BarraSuperior("Eventos y talleres") },
        snackbarHost = { SnackbarHost(snackbar) },
        floatingActionButton = {
            if (estado.error == null) {
                ExtendedFloatingActionButton(
                    onClick = onNuevo,
                    icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                    text = { Text("Nuevo evento") }
                )
            }
        }
    ) { padding ->
        val modificador = Modifier.padding(padding)
        when {
            estado.cargando -> PantallaCarga("Cargando eventos…", modificador)
            estado.error != null -> EstadoVacio(
                icono = Icons.Filled.CloudOff,
                titulo = "Sin conexión con el servidor",
                mensaje = estado.error ?: "",
                textoBoton = "Reintentar",
                onBoton = viewModel::cargar,
                modifier = modificador
            )
            estado.eventos.isEmpty() -> EstadoVacio(
                icono = Icons.Filled.EventBusy,
                titulo = "No hay eventos",
                mensaje = "Crea el primer evento o taller con el botón \"Nuevo evento\".",
                modifier = modificador
            )
            else -> LazyColumn(
                modifier = modificador.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "${estado.eventos.size} eventos programados",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = viewModel::cargar) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Actualizar")
                        }
                    }
                }
                items(estado.eventos, key = { it.id ?: 0L }) { evento ->
                    TarjetaEvento(
                        evento = evento,
                        onEditar = { evento.id?.let(onEditar) },
                        onEliminar = { porEliminar = evento },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
    }

    porEliminar?.let { evento ->
        AlertDialog(
            onDismissRequest = { porEliminar = null },
            title = { Text("¿Eliminar evento?") },
            text = { Text("Se eliminará \"${evento.titulo}\" del servidor. Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    evento.id?.let(viewModel::eliminar)
                    porEliminar = null
                }) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { porEliminar = null }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun TarjetaEvento(
    evento: EventoDto,
    onEditar: () -> Unit,
    onEliminar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(
                Categoria.entries.firstOrNull { it.name == evento.categoria }?.etiqueta ?: evento.categoria,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(evento.titulo, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(4.dp))
            Text(evento.descripcion, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            Dato(Icons.Filled.CalendarMonth, evento.fecha)
            Dato(Icons.Filled.Place, evento.lugar)
            Dato(Icons.Filled.Groups, "${evento.cupos} cupos")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onEditar) {
                    Icon(Icons.Filled.Edit, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Editar")
                }
                TextButton(onClick = onEliminar) {
                    Icon(Icons.Filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.width(4.dp))
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun Dato(icono: ImageVector, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
        Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(8.dp))
        Text(texto, style = MaterialTheme.typography.bodyMedium)
    }
}

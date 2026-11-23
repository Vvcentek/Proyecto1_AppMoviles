package com.example.master_martini.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.master_martini.data.local.model.Categoria
import com.example.master_martini.ui.components.BarraSuperior
import com.example.master_martini.ui.components.BotonPrincipal
import com.example.master_martini.ui.components.CampoTexto
import com.example.master_martini.ui.components.MensajeError
import com.example.master_martini.ui.components.PantallaCarga
import com.example.master_martini.ui.components.SelectorOpciones
import com.example.master_martini.viewmodel.AppViewModelProvider
import com.example.master_martini.viewmodel.CampoEvento
import com.example.master_martini.viewmodel.EventoFormViewModel

@Composable
fun EventoFormScreen(
    onVolver: () -> Unit,
    viewModel: EventoFormViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    // Al guardar con éxito se vuelve a la lista, que se recarga sola.
    LaunchedEffect(estado.guardado) { if (estado.guardado) onVolver() }

    Scaffold(
        topBar = { BarraSuperior(if (estado.esEdicion) "Editar evento" else "Nuevo evento", onVolver = onVolver) }
    ) { padding ->
        if (estado.cargando) {
            PantallaCarga("Cargando evento…", Modifier.padding(padding))
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CampoTexto(
                valor = estado.titulo, onValorChange = viewModel::onTituloChange,
                etiqueta = "Título", icono = Icons.Filled.Title,
                error = estado.errorDe(CampoEvento.TITULO),
                capitalizacion = KeyboardCapitalization.Sentences
            )
            CampoTexto(
                valor = estado.descripcion, onValorChange = viewModel::onDescripcionChange,
                etiqueta = "Descripción", icono = Icons.Filled.Description,
                error = estado.errorDe(CampoEvento.DESCRIPCION),
                capitalizacion = KeyboardCapitalization.Sentences
            )
            CampoTexto(
                valor = estado.fecha, onValorChange = viewModel::onFechaChange,
                etiqueta = "Fecha", icono = Icons.Filled.CalendarMonth,
                error = estado.errorDe(CampoEvento.FECHA),
                ayuda = "Formato AAAA-MM-DD, por ejemplo: 2026-12-15",
                tipoTeclado = KeyboardType.Number
            )
            CampoTexto(
                valor = estado.lugar, onValorChange = viewModel::onLugarChange,
                etiqueta = "Lugar", icono = Icons.Filled.Place,
                error = estado.errorDe(CampoEvento.LUGAR),
                capitalizacion = KeyboardCapitalization.Sentences
            )
            CampoTexto(
                valor = estado.cupos, onValorChange = viewModel::onCuposChange,
                etiqueta = "Cupos", icono = Icons.Filled.Groups,
                error = estado.errorDe(CampoEvento.CUPOS),
                ayuda = "Entre 1 y 500",
                tipoTeclado = KeyboardType.Number,
                accionTeclado = ImeAction.Done
            )
            SelectorOpciones(
                titulo = "Especialidad",
                icono = Icons.Filled.Category,
                opciones = Categoria.entries.toList(),
                seleccionada = estado.categoria,
                etiqueta = { it.etiqueta },
                onSeleccion = viewModel::onCategoriaChange,
                error = estado.errorDe(CampoEvento.CATEGORIA)
            )
            MensajeError(estado.errorServidor)
            BotonPrincipal(
                texto = if (estado.esEdicion) "Guardar cambios" else "Crear evento",
                onClick = viewModel::guardar,
                cargando = estado.guardando,
                icono = Icons.Filled.Save
            )
        }
    }
}

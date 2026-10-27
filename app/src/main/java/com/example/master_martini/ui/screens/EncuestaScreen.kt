package com.example.master_martini.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.master_martini.data.local.model.Categoria
import com.example.master_martini.data.local.model.NivelDificultad
import com.example.master_martini.data.local.model.TipoContenido
import com.example.master_martini.ui.components.BarraSuperior
import com.example.master_martini.ui.components.BotonPrincipal
import com.example.master_martini.ui.components.SelectorMultiple
import com.example.master_martini.ui.components.SelectorOpciones
import com.example.master_martini.viewmodel.AppViewModelProvider
import com.example.master_martini.viewmodel.CampoEncuesta
import com.example.master_martini.viewmodel.EncuestaViewModel

@Composable
fun EncuestaScreen(
    onTerminado: () -> Unit,
    onVolver: (() -> Unit)? = null,
    viewModel: EncuestaViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    LaunchedEffect(estado.guardado) {
        if (estado.guardado) onTerminado()
    }

    Scaffold(topBar = { BarraSuperior("Mis intereses", onVolver) }) { padding ->
        if (estado.cargando) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    if (estado.nombreUsuario.isNotEmpty()) "¡Bienvenido, ${estado.nombreUsuario}!"
                    else "Encuesta de intereses",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    "Cuéntanos qué te interesa para recomendarte los contenidos más útiles.",
                    style = MaterialTheme.typography.bodyLarge
                )
                SelectorMultiple(
                    titulo = "¿Qué especialidades te interesan?",
                    icono = Icons.Filled.Favorite,
                    opciones = Categoria.entries,
                    seleccionadas = estado.intereses,
                    etiqueta = { it.etiqueta },
                    onAlternar = viewModel::onAlternarInteres,
                    error = estado.errorDe(CampoEncuesta.INTERESES)
                )
                SelectorOpciones(
                    titulo = "¿Qué formato prefieres?",
                    icono = Icons.Filled.OndemandVideo,
                    opciones = TipoContenido.entries,
                    seleccionada = estado.formatoPreferido,
                    etiqueta = { it.etiqueta },
                    onSeleccion = viewModel::onFormatoChange,
                    error = estado.errorDe(CampoEncuesta.FORMATO)
                )
                SelectorOpciones(
                    titulo = "¿Cuál es tu nivel de experiencia?",
                    icono = Icons.Filled.School,
                    opciones = NivelDificultad.entries,
                    seleccionada = estado.nivel,
                    etiqueta = { it.etiqueta },
                    onSeleccion = viewModel::onNivelChange,
                    error = estado.errorDe(CampoEncuesta.NIVEL)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .toggleable(
                            value = estado.aceptaNotificaciones,
                            onValueChange = viewModel::onNotificacionesChange,
                            role = Role.Switch
                        )
                        .padding(vertical = 8.dp)
                ) {
                    Icon(Icons.Filled.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Quiero recibir avisos de nuevos contenidos y eventos",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(checked = estado.aceptaNotificaciones, onCheckedChange = null)
                }
                BotonPrincipal(
                    texto = "Guardar mis intereses",
                    onClick = viewModel::guardar,
                    cargando = estado.guardando
                )
            }
        }
    }
}
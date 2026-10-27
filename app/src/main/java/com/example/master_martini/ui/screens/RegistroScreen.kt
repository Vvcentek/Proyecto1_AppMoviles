package com.example.master_martini.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.example.master_martini.data.local.model.TamanoNegocio
import com.example.master_martini.data.local.model.TipoNegocio
import com.example.master_martini.ui.components.BarraSuperior
import com.example.master_martini.ui.components.BotonPrincipal
import com.example.master_martini.ui.components.CampoTexto
import com.example.master_martini.ui.components.SelectorOpciones
import com.example.master_martini.viewmodel.AppViewModelProvider
import com.example.master_martini.viewmodel.CampoRegistro
import com.example.master_martini.viewmodel.RegistroViewModel

@Composable
fun RegistroScreen(
    onRegistrado: () -> Unit,
    onVolver: () -> Unit,
    viewModel: RegistroViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    // Cuando el ViewModel confirma el registro, se pasa a la encuesta.
    LaunchedEffect(estado.usuarioRegistradoId) {
        if (estado.usuarioRegistradoId != null) onRegistrado()
    }

    Scaffold(topBar = { BarraSuperior("Crear mi perfil", onVolver) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Cuéntanos de ti y de tu negocio", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Con estos datos te recomendamos contenidos. Todos los campos son obligatorios.",
                style = MaterialTheme.typography.bodyLarge
            )
            CampoTexto(
                valor = estado.nombre,
                onValorChange = viewModel::onNombreChange,
                etiqueta = "Nombre completo",
                icono = Icons.Filled.Person,
                error = estado.errorDe(CampoRegistro.NOMBRE),
                capitalizacion = KeyboardCapitalization.Words
            )
            CampoTexto(
                valor = estado.correo,
                onValorChange = viewModel::onCorreoChange,
                etiqueta = "Correo electrónico",
                icono = Icons.Filled.Email,
                error = estado.errorDe(CampoRegistro.CORREO),
                tipoTeclado = KeyboardType.Email
            )
            CampoTexto(
                valor = estado.telefono,
                onValorChange = viewModel::onTelefonoChange,
                etiqueta = "Teléfono",
                icono = Icons.Filled.Phone,
                error = estado.errorDe(CampoRegistro.TELEFONO),
                ayuda = "Ejemplo: 9 1234 5678",
                tipoTeclado = KeyboardType.Phone
            )
            CampoTexto(
                valor = estado.direccion,
                onValorChange = viewModel::onDireccionChange,
                etiqueta = "Dirección del negocio",
                icono = Icons.Filled.LocationOn,
                error = estado.errorDe(CampoRegistro.DIRECCION),
                capitalizacion = KeyboardCapitalization.Sentences
            )
            CampoTexto(
                valor = estado.rut,
                onValorChange = viewModel::onRutChange,
                etiqueta = "RUT de la empresa",
                icono = Icons.Filled.Badge,
                error = estado.errorDe(CampoRegistro.RUT),
                ayuda = "Ejemplo: 76.123.456-0",
                capitalizacion = KeyboardCapitalization.Characters,
                accionTeclado = ImeAction.Done
            )
            SelectorOpciones(
                titulo = "Tipo de negocio",
                icono = Icons.Filled.Storefront,
                opciones = TipoNegocio.entries,
                seleccionada = estado.tipoNegocio,
                etiqueta = { it.etiqueta },
                onSeleccion = viewModel::onTipoNegocioChange,
                error = estado.errorDe(CampoRegistro.TIPO_NEGOCIO)
            )
            SelectorOpciones(
                titulo = "Tamaño del negocio",
                icono = Icons.Filled.Groups,
                opciones = TamanoNegocio.entries,
                seleccionada = estado.tamanoNegocio,
                etiqueta = { it.etiqueta },
                onSeleccion = viewModel::onTamanoNegocioChange,
                error = estado.errorDe(CampoRegistro.TAMANO_NEGOCIO)
            )
            BotonPrincipal(
                texto = "Continuar",
                onClick = viewModel::registrar,
                cargando = estado.guardando
            )
        }
    }
}
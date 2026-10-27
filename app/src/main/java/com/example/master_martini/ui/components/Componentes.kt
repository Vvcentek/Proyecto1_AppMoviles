package com.example.master_martini.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

// Mensaje de error que aparece y desaparece con animación (retroalimentación visual).
@Composable
fun MensajeError(error: String?, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = error != null,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        ) {
            Icon(
                Icons.Filled.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = error ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun CampoTexto(
    valor: String,
    onValorChange: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    error: String?,
    modifier: Modifier = Modifier,
    ayuda: String? = null,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    capitalizacion: KeyboardCapitalization = KeyboardCapitalization.None,
    accionTeclado: ImeAction = ImeAction.Next
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = valor,
            onValueChange = onValorChange,
            label = { Text(etiqueta) },
            leadingIcon = { Icon(icono, contentDescription = null) },
            trailingIcon = {
                when {
                    error != null -> Icon(
                        Icons.Filled.Error,
                        contentDescription = "Campo con error",
                        tint = MaterialTheme.colorScheme.error
                    )
                    valor.isNotBlank() -> Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = "Campo correcto",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            },
            supportingText = if (ayuda != null && error == null) {
                { Text(ayuda) }
            } else null,
            isError = error != null,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge,
            keyboardOptions = KeyboardOptions(
                capitalization = capitalizacion,
                keyboardType = tipoTeclado,
                imeAction = accionTeclado
            ),
            modifier = Modifier.fillMaxWidth()
        )
        MensajeError(error)
    }
}

@Composable
private fun TituloSeccion(titulo: String, icono: ImageVector, conError: Boolean) {
    // El título cambia de color suavemente cuando la sección tiene un error.
    val color by animateColorAsState(
        targetValue = if (conError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        label = "colorTituloSeccion"
    )
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 4.dp)) {
        Icon(icono, contentDescription = null, tint = color)
        Spacer(Modifier.width(8.dp))
        Text(titulo, style = MaterialTheme.typography.titleMedium, color = color)
    }
}

// Selección única con filas grandes (más fáciles de tocar que un menú desplegable).
@Composable
fun <T> SelectorOpciones(
    titulo: String,
    icono: ImageVector,
    opciones: List<T>,
    seleccionada: T?,
    etiqueta: (T) -> String,
    onSeleccion: (T) -> Unit,
    error: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        TituloSeccion(titulo, icono, error != null)
        Column(Modifier.selectableGroup()) {
            opciones.forEach { opcion ->
                val marcada = opcion == seleccionada
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp)
                        .selectable(selected = marcada, onClick = { onSeleccion(opcion) }, role = Role.RadioButton)
                        .padding(horizontal = 8.dp)
                ) {
                    RadioButton(selected = marcada, onClick = null)
                    Spacer(Modifier.width(12.dp))
                    Text(etiqueta(opcion), style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
        MensajeError(error)
    }
}

// Selección múltiple con casillas.
@Composable
fun <T> SelectorMultiple(
    titulo: String,
    icono: ImageVector,
    opciones: List<T>,
    seleccionadas: Set<T>,
    etiqueta: (T) -> String,
    onAlternar: (T) -> Unit,
    error: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        TituloSeccion(titulo, icono, error != null)
        opciones.forEach { opcion ->
            val marcada = opcion in seleccionadas
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
                    .toggleable(value = marcada, onValueChange = { onAlternar(opcion) }, role = Role.Checkbox)
                    .padding(horizontal = 8.dp)
            ) {
                Checkbox(checked = marcada, onCheckedChange = null)
                Spacer(Modifier.width(12.dp))
                Text(etiqueta(opcion), style = MaterialTheme.typography.bodyLarge)
            }
        }
        MensajeError(error)
    }
}

@Composable
fun BotonPrincipal(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cargando: Boolean = false,
    icono: ImageVector? = null
) {
    Button(
        onClick = onClick,
        enabled = !cargando,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
    ) {
        if (cargando) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            if (icono != null) {
                Icon(icono, contentDescription = null)
                Spacer(Modifier.width(8.dp))
            }
            Text(texto, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(
    titulo: String,
    onVolver: (() -> Unit)? = null,
    acciones: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = { Text(titulo, style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            if (onVolver != null) {
                IconButton(onClick = onVolver) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
            }
        },
        actions = acciones,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}
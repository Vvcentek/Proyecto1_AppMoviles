package com.example.master_martini.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.master_martini.data.local.decodificarMiniatura
import com.example.master_martini.ui.components.BarraSuperior
import com.example.master_martini.ui.components.BotonPrincipal
import com.example.master_martini.ui.components.EstadoVacio
import com.example.master_martini.ui.components.PantallaCarga
import com.example.master_martini.viewmodel.AppViewModelProvider
import com.example.master_martini.viewmodel.PerfilViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.master_martini.ui.components.rememberCargaDeEntrada
import android.os.Build

@Composable
fun PerfilScreen(
    onEditarIntereses: () -> Unit,
    viewModel: PerfilViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val cargaEntrada = rememberCargaDeEntrada()
    val contexto = LocalContext.current
    val avisos = remember { SnackbarHostState() }

    // Cámara: devuelve una foto pequeña (suficiente para el perfil).
    val tomarFoto = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        viewModel.onFotoTomada(bitmap)
    }
    // Permiso de cámara pedido en el momento de usarla.
    val pedirPermisoCamara = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) tomarFoto.launch(null) else viewModel.onPermisoCamaraDenegado()
    }
    // Galería: selector de fotos del sistema (no necesita permisos).
    val elegirFoto = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        viewModel.onFotoElegida(uri)
    }

    // Permiso para leer fotos: cambia según la versión de Android.
    val permisosGaleria: Array<String> = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
        )
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        else -> arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    fun abrirSelectorFotos() {
        elegirFoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    val pedirPermisoGaleria = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultados ->
        // Sirve tanto "acceso total" como "solo fotos seleccionadas".
        if (resultados.values.any { it }) abrirSelectorFotos() else viewModel.onPermisoGaleriaDenegado()
    }

    fun abrirGaleria() {
        val tienePermiso = permisosGaleria.any {
            ContextCompat.checkSelfPermission(contexto, it) == PackageManager.PERMISSION_GRANTED
        }
        if (tienePermiso) abrirSelectorFotos() else pedirPermisoGaleria.launch(permisosGaleria)
    }

    fun abrirCamara() {
        val tienePermiso = ContextCompat.checkSelfPermission(contexto, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        if (tienePermiso) tomarFoto.launch(null) else pedirPermisoCamara.launch(Manifest.permission.CAMERA)
    }

    LaunchedEffect(estado.mensaje) {
        val texto = estado.mensaje
        if (texto != null) {
            avisos.showSnackbar(texto)
            viewModel.mensajeMostrado()
        }
    }

    Scaffold(
        topBar = { BarraSuperior("Mi perfil") },
        snackbarHost = { SnackbarHost(avisos) }
    ) { padding ->
        Crossfade(targetState = estado.cargando || cargaEntrada, label = "cargaPerfil", modifier = Modifier.padding(padding)) { cargando ->
            val usuario = estado.usuario
            when {
                cargando -> PantallaCarga("Cargando tu perfil…")
                usuario == null -> EstadoVacio(
                    icono = Icons.Filled.PersonOff,
                    titulo = "No hay perfil",
                    mensaje = "Crea tu perfil desde la pantalla de bienvenida."
                )
                else -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FotoPerfil(ruta = usuario.fotoUri, guardando = estado.guardandoFoto)
                    Text(usuario.nombre, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { abrirCamara() },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 56.dp)
                        ) {
                            Icon(Icons.Filled.PhotoCamera, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Tomar foto")
                        }
                        OutlinedButton(
                            onClick = { abrirGaleria() },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 56.dp)
                        ) {
                            Icon(Icons.Filled.PhotoLibrary, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Galería")
                        }
                    }
                    TarjetaDatos(
                        "Mi negocio",
                        listOf(
                            Icons.Filled.Storefront to usuario.tipoNegocio.etiqueta,
                            Icons.Filled.Groups to usuario.tamanoNegocio.etiqueta,
                            Icons.Filled.Badge to usuario.rutEmpresa,
                            Icons.Filled.LocationOn to usuario.direccion
                        )
                    )
                    TarjetaDatos(
                        "Contacto",
                        listOf(
                            Icons.Filled.Email to usuario.correo,
                            Icons.Filled.Phone to usuario.telefono
                        )
                    )
                    TarjetaDatos(
                        "Mis intereses",
                        listOf(
                            Icons.Filled.Favorite to estado.intereses.joinToString { it.etiqueta }.ifEmpty { "Sin intereses" },
                            Icons.Filled.OndemandVideo to (usuario.formatoPreferido?.etiqueta ?: "Sin preferencia"),
                            Icons.Filled.School to (usuario.nivelExperiencia?.etiqueta ?: "Sin nivel"),
                            Icons.Filled.Bookmark to "${estado.totalFavoritos} contenidos guardados"
                        )
                    )
                    BotonPrincipal(texto = "Editar mis intereses", onClick = onEditarIntereses, icono = Icons.Filled.Edit)
                }
            }
        }
    }
}

@Composable
private fun FotoPerfil(ruta: String?, guardando: Boolean) {
    // La foto se lee del almacenamiento fuera del hilo de la interfaz.
    val foto by produceState<Bitmap?>(initialValue = null, ruta) {
        value = ruta?.let { withContext(Dispatchers.IO) { decodificarMiniatura(it) } }
    }
    Box(
        modifier = Modifier
            .size(140.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        val imagen = foto
        if (imagen != null) {
            Image(
                bitmap = imagen.asImageBitmap(),
                contentDescription = "Foto de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                Icons.Filled.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(80.dp)
            )
        }
        if (guardando) CircularProgressIndicator()
    }
}

@Composable
private fun TarjetaDatos(titulo: String, filas: List<Pair<ImageVector, String>>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            filas.forEach { (icono, texto) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(12.dp))
                    Text(texto, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}
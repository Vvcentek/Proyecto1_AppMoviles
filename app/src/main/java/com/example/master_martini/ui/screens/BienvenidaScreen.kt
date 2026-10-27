package com.example.master_martini.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.master_martini.ui.components.BotonPrincipal
import com.example.master_martini.viewmodel.AppViewModelProvider
import com.example.master_martini.viewmodel.EstadoSesion
import com.example.master_martini.viewmodel.SesionViewModel

@Composable
fun BienvenidaScreen(
    onCrearPerfil: () -> Unit,
    onContinuar: () -> Unit,
    viewModel: SesionViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    // Animación de entrada: el contenido aparece deslizándose al abrir la app.
    val visible = remember { MutableTransitionState(false).apply { targetState = true } }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visibleState = visible,
                enter = fadeIn(tween(700)) + slideInVertically(tween(700)) { it / 3 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.BakeryDining,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(96.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Master Martini",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Recetas, videos y cursos para hacer crecer tu negocio",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(40.dp))
                    when (val s = estado) {
                        EstadoSesion.Cargando -> CircularProgressIndicator()
                        is EstadoSesion.Listo -> {
                            val usuario = s.usuario
                            if (usuario == null) {
                                BotonPrincipal(
                                    texto = "Crear mi perfil",
                                    onClick = onCrearPerfil,
                                    icono = Icons.Filled.PersonAdd
                                )
                            } else {
                                Text(
                                    "Hola de nuevo, ${usuario.nombre.substringBefore(" ")}",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Spacer(Modifier.height(16.dp))
                                BotonPrincipal(
                                    texto = "Ver contenidos",
                                    onClick = onContinuar,
                                    icono = Icons.AutoMirrored.Filled.ArrowForward
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
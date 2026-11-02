package com.example.master_martini.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.master_martini.ui.screens.BienvenidaScreen
import com.example.master_martini.ui.screens.CatalogoScreen
import com.example.master_martini.ui.screens.DetalleScreen
import com.example.master_martini.ui.screens.EncuestaScreen
import com.example.master_martini.ui.screens.FavoritosScreen
import com.example.master_martini.ui.screens.PerfilScreen
import com.example.master_martini.ui.screens.RegistroScreen
import com.example.master_martini.viewmodel.DetalleViewModel

object Rutas {
    const val BIENVENIDA = "bienvenida"
    const val REGISTRO = "registro"
    const val ENCUESTA = "encuesta"
    const val EDITAR_INTERESES = "editar_intereses"
    const val CATALOGO = "catalogo"
    const val FAVORITOS = "favoritos"
    const val PERFIL = "perfil"
    const val DETALLE = "detalle/{contenidoId}"
    fun detalle(id: Long) = "detalle/$id"
}

private data class Seccion(
    val ruta: String,
    val etiqueta: String,
    val icono: ImageVector,
    val iconoSeleccionado: ImageVector
)

// Secciones del menú inferior.
private val secciones = listOf(
    Seccion(Rutas.CATALOGO, "Contenidos", Icons.Outlined.Home, Icons.Filled.Home),
    Seccion(Rutas.FAVORITOS, "Favoritos", Icons.Outlined.FavoriteBorder, Icons.Filled.Favorite),
    Seccion(Rutas.PERFIL, "Perfil", Icons.Outlined.Person, Icons.Filled.Person)
)

private const val DURACION = 350

@Composable
fun MasterMartiniNavHost(navController: NavHostController = rememberNavController()) {
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route
    val mostrarMenu = rutaActual in secciones.map { it.ruta }

    val irASeccion: (String) -> Unit = { ruta ->
        navController.navigate(ruta) {
            popUpTo(Rutas.CATALOGO) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            // El menú aparece y desaparece deslizándose.
            AnimatedVisibility(
                visible = mostrarMenu,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                MenuInferior(rutaActual = rutaActual, onNavegar = irASeccion)
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Rutas.BIENVENIDA,
            modifier = Modifier.padding(padding),
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(DURACION)) +
                        fadeIn(tween(DURACION))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(DURACION)) +
                        fadeOut(tween(DURACION))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(DURACION)) +
                        fadeIn(tween(DURACION))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(DURACION)) +
                        fadeOut(tween(DURACION))
            }
        ) {
            composable(Rutas.BIENVENIDA) {
                BienvenidaScreen(
                    onCrearPerfil = { navController.navigate(Rutas.REGISTRO) },
                    onContinuar = {
                        navController.navigate(Rutas.CATALOGO) {
                            popUpTo(Rutas.BIENVENIDA) { inclusive = true }
                        }
                    }
                )
            }
            composable(Rutas.REGISTRO) {
                RegistroScreen(
                    onRegistrado = {
                        navController.navigate(Rutas.ENCUESTA) {
                            popUpTo(Rutas.BIENVENIDA) { inclusive = true }
                        }
                    },
                    onVolver = { navController.popBackStack() }
                )
            }
            composable(Rutas.ENCUESTA) {
                EncuestaScreen(
                    onTerminado = {
                        navController.navigate(Rutas.CATALOGO) {
                            popUpTo(Rutas.ENCUESTA) { inclusive = true }
                        }
                    }
                )
            }
            // Las tres secciones del menú cambian con un fundido suave.
            composable(Rutas.CATALOGO, enterTransition = { fadeIn(tween(DURACION)) }, exitTransition = { fadeOut(tween(DURACION)) }) {
                CatalogoScreen(onAbrirContenido = { id -> navController.navigate(Rutas.detalle(id)) })
            }
            composable(Rutas.FAVORITOS, enterTransition = { fadeIn(tween(DURACION)) }, exitTransition = { fadeOut(tween(DURACION)) }) {
                FavoritosScreen(
                    onAbrirContenido = { id -> navController.navigate(Rutas.detalle(id)) },
                    onExplorar = { irASeccion(Rutas.CATALOGO) }
                )
            }
            composable(Rutas.PERFIL, enterTransition = { fadeIn(tween(DURACION)) }, exitTransition = { fadeOut(tween(DURACION)) }) {
                PerfilScreen(onEditarIntereses = { navController.navigate(Rutas.EDITAR_INTERESES) })
            }
            composable(
                route = Rutas.DETALLE,
                arguments = listOf(navArgument(DetalleViewModel.ARG_ID) { type = NavType.LongType })
            ) {
                DetalleScreen(onVolver = { navController.popBackStack() })
            }
            composable(Rutas.EDITAR_INTERESES) {
                EncuestaScreen(
                    onTerminado = { navController.popBackStack() },
                    onVolver = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun MenuInferior(rutaActual: String?, onNavegar: (String) -> Unit) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
        secciones.forEach { seccion ->
            val seleccionada = rutaActual == seccion.ruta
            NavigationBarItem(
                selected = seleccionada,
                onClick = { if (!seleccionada) onNavegar(seccion.ruta) },
                icon = {
                    Icon(
                        if (seleccionada) seccion.iconoSeleccionado else seccion.icono,
                        contentDescription = null
                    )
                },
                label = { Text(seccion.etiqueta, style = MaterialTheme.typography.labelLarge) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}
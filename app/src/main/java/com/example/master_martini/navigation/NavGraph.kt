package com.example.master_martini.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.master_martini.ui.components.BarraSuperior
import com.example.master_martini.ui.screens.BienvenidaScreen
import com.example.master_martini.ui.screens.EncuestaScreen
import com.example.master_martini.ui.screens.RegistroScreen

object Rutas {
    const val BIENVENIDA = "bienvenida"
    const val REGISTRO = "registro"
    const val ENCUESTA = "encuesta"
    const val CATALOGO = "catalogo"
}

private const val DURACION = 350

@Composable
fun MasterMartiniNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Rutas.BIENVENIDA,
        // Transiciones animadas entre pantallas (IE 2.2.2).
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
        composable(Rutas.CATALOGO) { CatalogoProvisorio() }
    }
}

// Pantalla temporal: se reemplaza por el catálogo real en el siguiente paso.
@Composable
private fun CatalogoProvisorio() {
    Scaffold(topBar = { BarraSuperior("Contenidos") }) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("Catálogo: se implementa en el siguiente paso", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
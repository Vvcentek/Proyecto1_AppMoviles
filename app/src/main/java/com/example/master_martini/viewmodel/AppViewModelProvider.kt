package com.example.master_martini.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.master_martini.MasterMartiniApp
import com.example.master_martini.data.local.AlmacenFotos

// Tiempo mínimo que se muestra la animación de carga al entrar a una pantalla.
const val CARGA_MINIMA_MS = 700L

// Id usado en las consultas cuando todavía no hay perfil registrado.
const val SIN_USUARIO = -1L

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer { SesionViewModel(app().repositorio) }
        initializer { RegistroViewModel(app().repositorio) }
        initializer { EncuestaViewModel(app().repositorio) }
        initializer { CatalogoViewModel(app().repositorio) }
        initializer { DetalleViewModel(createSavedStateHandle(), app().repositorio) }
        initializer { FavoritosViewModel(app().repositorio) }
        initializer { PerfilViewModel(app().repositorio, AlmacenFotos(app())) }
    }
}

private fun CreationExtras.app(): MasterMartiniApp =
    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MasterMartiniApp
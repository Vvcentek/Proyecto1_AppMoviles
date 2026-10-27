package com.example.master_martini.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.master_martini.MasterMartiniApp

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer { SesionViewModel(app().repositorio) }
        initializer { RegistroViewModel(app().repositorio) }
        initializer { EncuestaViewModel(app().repositorio) }
    }
}

private fun CreationExtras.app(): MasterMartiniApp =
    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MasterMartiniApp
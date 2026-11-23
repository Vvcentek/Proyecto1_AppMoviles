package com.example.master_martini.data.repository

import com.example.master_martini.data.remote.RecetaExterna
import com.example.master_martini.data.remote.RecetasApi

class RecetasExternasRepository(private val api: RecetasApi) {

    suspend fun postres(): Result<List<RecetaExterna>> =
        runCatching { api.porCategoria("Dessert").meals.orEmpty() }
}

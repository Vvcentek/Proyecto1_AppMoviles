package com.example.master_martini.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

// API externa pública TheMealDB (gratuita, sin clave): recetas de postres.
interface RecetasApi {

    @GET("api/json/v1/1/filter.php")
    suspend fun porCategoria(@Query("c") categoria: String = "Dessert"): RespuestaRecetas
}

data class RespuestaRecetas(val meals: List<RecetaExterna>?)

data class RecetaExterna(
    val idMeal: String,
    val strMeal: String,
    val strMealThumb: String
)

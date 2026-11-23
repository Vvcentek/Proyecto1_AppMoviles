package com.example.master_martini.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Crea los clientes Retrofit. Se usa 127.0.0.1 porque con el celular conectado por USB
// el comando "adb reverse tcp:8080 tcp:8080" redirige ese puerto al computador.
// (En el emulador también funciona con adb reverse; sin él sería 10.0.2.2.)
object ClienteHttp {

    const val URL_MICROSERVICIO = "http://127.0.0.1:8080/"
    const val URL_THEMEALDB = "https://www.themealdb.com/"

    val eventosApi: EventosApi by lazy { crear(URL_MICROSERVICIO).create(EventosApi::class.java) }

    val recetasApi: RecetasApi by lazy { crear(URL_THEMEALDB).create(RecetasApi::class.java) }

    private fun crear(url: String): Retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

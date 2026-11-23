package com.example.master_martini

import android.app.Application
import com.example.master_martini.data.local.database.MasterMartiniDatabase
import com.example.master_martini.data.remote.ClienteHttp
import com.example.master_martini.data.repository.EventosRepository
import com.example.master_martini.data.repository.MasterMartiniRepository
import com.example.master_martini.data.repository.RecetasExternasRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MasterMartiniApp : Application() {

    // Se crea la primera vez que alguien lo pide y se reutiliza en toda la app.
    val repositorio: MasterMartiniRepository by lazy {
        val db = MasterMartiniDatabase.obtener(this)
        MasterMartiniRepository(db.usuarioDao(), db.interesDao(), db.contenidoDao(), db.favoritoDao())
    }

    // Repositorios remotos: microservicio propio y API externa.
    val eventosRepositorio: EventosRepository by lazy { EventosRepository(ClienteHttp.eventosApi) }
    val recetasRepositorio: RecetasExternasRepository by lazy { RecetasExternasRepository(ClienteHttp.recetasApi) }

    private val alcance = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        alcance.launch { repositorio.poblarSiEstaVacia() }
    }
}
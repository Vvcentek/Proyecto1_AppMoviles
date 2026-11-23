package com.example.master_martini.data.repository

import com.example.master_martini.data.remote.EventoDto
import com.example.master_martini.data.remote.EventosApi

// Intermediario entre los ViewModels y el microservicio.
// Devuelve Result: éxito con el dato, o fallo con la excepción (sin red, error 4xx/5xx...).
class EventosRepository(private val api: EventosApi) {

    suspend fun listar(): Result<List<EventoDto>> = runCatching { api.listar() }

    suspend fun obtener(id: Long): Result<EventoDto> = runCatching { api.obtener(id) }

    suspend fun guardar(evento: EventoDto): Result<EventoDto> = runCatching {
        val id = evento.id
        if (id == null) api.crear(evento) else api.actualizar(id, evento)
    }

    suspend fun eliminar(id: Long): Result<Unit> = runCatching {
        val respuesta = api.eliminar(id)
        if (!respuesta.isSuccessful) error("El servidor respondió ${respuesta.code()}")
    }
}

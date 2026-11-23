package com.example.master_martini.data.remote

// Lo que viaja en JSON entre la app y el microservicio Spring Boot.
// La fecha va como texto "yyyy-MM-dd" y la categoría con el nombre del enum Categoria.
data class EventoDto(
    val id: Long? = null,
    val titulo: String,
    val descripcion: String,
    val fecha: String,
    val lugar: String,
    val categoria: String,
    val cupos: Int
)

package com.example.master_martini.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.master_martini.data.local.model.Categoria
import com.example.master_martini.data.local.model.NivelDificultad
import com.example.master_martini.data.local.model.TipoContenido

@Entity(tableName = "contenidos")
data class Contenido(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titulo: String,
    val descripcion: String,
    val categoria: Categoria,
    val tipo: TipoContenido,
    val nivel: NivelDificultad,
    val duracionMinutos: Int,
    val chef: String,
    // Pasos separados por salto de línea; la pantalla de detalle los muestra como lista.
    val pasos: String
)
package com.example.master_martini.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "favoritos",
    primaryKeys = ["usuarioId", "contenidoId"],
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Contenido::class,
            parentColumns = ["id"],
            childColumns = ["contenidoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["contenidoId"])]
)
data class Favorito(
    val usuarioId: Long,
    val contenidoId: Long,
    val fechaGuardado: Long = System.currentTimeMillis()
)
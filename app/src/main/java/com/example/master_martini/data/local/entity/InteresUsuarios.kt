package com.example.master_martini.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.example.master_martini.data.local.model.Categoria

@Entity(
    tableName = "intereses_usuario",
    primaryKeys = ["usuarioId", "categoria"],
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class InteresUsuario(
    val usuarioId: Long,
    val categoria: Categoria
)
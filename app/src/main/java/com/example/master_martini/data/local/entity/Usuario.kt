package com.example.master_martini.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.master_martini.data.local.model.NivelDificultad
import com.example.master_martini.data.local.model.TamanoNegocio
import com.example.master_martini.data.local.model.TipoContenido
import com.example.master_martini.data.local.model.TipoNegocio

// Perfil del usuario y de su negocio. Sin contraseña: el caso prohíbe
// usar credenciales, y la app guarda un solo perfil local.
@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["correo"], unique = true)]
)
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val correo: String,
    val telefono: String,
    val direccion: String,
    val rutEmpresa: String,
    val tipoNegocio: TipoNegocio,
    val tamanoNegocio: TamanoNegocio,
    // Respuestas de la encuesta de intereses (se completan después del registro).
    val formatoPreferido: TipoContenido? = null,
    val nivelExperiencia: NivelDificultad? = null,
    val aceptaNotificaciones: Boolean = false,
    // Ruta de la foto tomada con la cámara (recurso nativo).
    val fotoUri: String? = null,
    val fechaRegistro: Long = System.currentTimeMillis()
)
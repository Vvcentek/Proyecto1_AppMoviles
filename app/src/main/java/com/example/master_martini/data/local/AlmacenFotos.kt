package com.example.master_martini.data.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File

// Guarda la foto de perfil en el almacenamiento interno de la app (privado: otras apps no lo ven).
class AlmacenFotos(private val context: Context) {

    private fun archivoNuevo(): File {
        val carpeta = File(context.filesDir, "fotos").apply { mkdirs() }
        return File(carpeta, "perfil_${System.currentTimeMillis()}.jpg")
    }

    fun guardarBitmap(bitmap: Bitmap): String {
        val archivo = archivoNuevo()
        archivo.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it) }
        return archivo.absolutePath
    }

    fun copiarDesdeGaleria(uri: Uri): String? {
        val entrada = context.contentResolver.openInputStream(uri) ?: return null
        val archivo = archivoNuevo()
        entrada.use { input -> archivo.outputStream().use { input.copyTo(it) } }
        return archivo.absolutePath
    }

    fun borrar(ruta: String?) {
        if (ruta != null) File(ruta).delete()
    }
}

// Carga la foto en tamaño reducido para no gastar memoria con fotos grandes de la galería.
fun decodificarMiniatura(ruta: String, ladoMaximo: Int = 600): Bitmap? {
    val opciones = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeFile(ruta, opciones)
    if (opciones.outWidth <= 0 || opciones.outHeight <= 0) return null
    var muestra = 1
    while (opciones.outWidth / (muestra * 2) >= ladoMaximo && opciones.outHeight / (muestra * 2) >= ladoMaximo) {
        muestra *= 2
    }
    return BitmapFactory.decodeFile(ruta, BitmapFactory.Options().apply { inSampleSize = muestra })
}
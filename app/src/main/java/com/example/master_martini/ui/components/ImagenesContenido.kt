package com.example.master_martini.ui.components

import androidx.annotation.DrawableRes
import com.example.master_martini.R
import com.example.master_martini.data.local.model.Categoria

// Imagen representativa de cada especialidad (guardadas en res/drawable-nodpi).
@DrawableRes
fun imagenDe(categoria: Categoria): Int = when (categoria) {
    Categoria.CHOCOLATERIA -> R.drawable.img_chocolateria
    Categoria.PASTELERIA_VITRINA -> R.drawable.img_tartaletas_frutal
    Categoria.HELADERIA -> R.drawable.img_heladeria
    Categoria.BOLLERIA -> R.drawable.img_bolleria
    Categoria.MASAS_HOJA -> R.drawable.img_medialuna_manteca
    Categoria.GALLETAS -> R.drawable.img_galletas
    Categoria.EMPANADAS -> R.drawable.img_empanadas
    Categoria.HORNEO -> R.drawable.img_pan_amasado
}
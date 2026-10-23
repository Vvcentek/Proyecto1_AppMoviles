package com.example.master_martini.data.local.seed

import com.example.master_martini.data.local.entity.Contenido
import com.example.master_martini.data.local.model.Categoria
import com.example.master_martini.data.local.model.NivelDificultad
import com.example.master_martini.data.local.model.TipoContenido

// Datos ficticios para el MVP académico: chefs y recetas inventados.
object DatosSemilla {

    val contenidos = listOf(
        Contenido(
            titulo = "Bombones rellenos de frambuesa",
            descripcion = "Bombones de chocolate semiamargo con relleno cremoso de frambuesa.",
            categoria = Categoria.CHOCOLATERIA, tipo = TipoContenido.RECETA,
            nivel = NivelDificultad.INTERMEDIO, duracionMinutos = 90, chef = "Chef Ana Rojas",
            pasos = "Temperar el chocolate a 31 °C\nCubrir los moldes y escurrir el exceso\nRellenar con la ganache de frambuesa\nSellar con chocolate y refrigerar 20 minutos"
        ),
        Contenido(
            titulo = "Templado de chocolate paso a paso",
            descripcion = "Video técnico sobre el templado por siembra para lograr brillo y quiebre.",
            categoria = Categoria.CHOCOLATERIA, tipo = TipoContenido.VIDEO,
            nivel = NivelDificultad.BASICO, duracionMinutos = 15, chef = "Chef Ana Rojas",
            pasos = "Fundir el chocolate a 45 °C\nAgregar un tercio de chocolate picado\nMezclar hasta bajar a 31 °C\nProbar en una espátula antes de usar"
        ),
        Contenido(
            titulo = "Tartaletas de frutas para vitrina",
            descripcion = "Tartaletas con crema pastelera y fruta fresca que duran bien en exhibición.",
            categoria = Categoria.PASTELERIA_VITRINA, tipo = TipoContenido.RECETA,
            nivel = NivelDificultad.INTERMEDIO, duracionMinutos = 75, chef = "Chef Pedro Lagos",
            pasos = "Hornear las bases de masa sablée\nRellenar con crema pastelera fría\nDecorar con fruta fresca\nAbrillantar con gelatina neutra"
        ),
        Contenido(
            titulo = "Curso de pastelería de vitrina",
            descripcion = "Curso de especialidad: montaje, conservación y presentación de tortas individuales.",
            categoria = Categoria.PASTELERIA_VITRINA, tipo = TipoContenido.CURSO,
            nivel = NivelDificultad.AVANZADO, duracionMinutos = 240, chef = "Chef Pedro Lagos",
            pasos = "Módulo 1: bizcochos y bases\nMódulo 2: mousses y cremas\nMódulo 3: glaseados espejo\nMódulo 4: montaje y exhibición"
        ),
        Contenido(
            titulo = "Helado artesanal de manjar",
            descripcion = "Base de helado equilibrada con manjar, sin máquina industrial.",
            categoria = Categoria.HELADERIA, tipo = TipoContenido.RECETA,
            nivel = NivelDificultad.BASICO, duracionMinutos = 40, chef = "Chef Carla Muñoz",
            pasos = "Calentar leche y crema\nIncorporar el manjar y mezclar\nEnfriar la base en refrigerador\nCongelar batiendo cada 30 minutos"
        ),
        Contenido(
            titulo = "Medialunas de manteca",
            descripcion = "Bollería clásica con masa fermentada y laminada.",
            categoria = Categoria.BOLLERIA, tipo = TipoContenido.RECETA,
            nivel = NivelDificultad.INTERMEDIO, duracionMinutos = 180, chef = "Chef Luis Pérez",
            pasos = "Preparar la masa y dejar reposar\nIncorporar la manteca y dar tres vueltas\nCortar triángulos y enrollar\nFermentar y hornear a 190 °C"
        ),
        Contenido(
            titulo = "Laminado de masa de hoja",
            descripcion = "Video técnico sobre vueltas simples y dobles para masa de hoja.",
            categoria = Categoria.MASAS_HOJA, tipo = TipoContenido.VIDEO,
            nivel = NivelDificultad.AVANZADO, duracionMinutos = 20, chef = "Chef Luis Pérez",
            pasos = "Preparar el empaste y la margarina de hojaldre\nEncerrar la grasa en la masa\nDar vueltas simples y dobles\nRefrigerar entre vueltas"
        ),
        Contenido(
            titulo = "Galletas de mantequilla decoradas",
            descripcion = "Galletas firmes, ideales para decorar con glasé real.",
            categoria = Categoria.GALLETAS, tipo = TipoContenido.RECETA,
            nivel = NivelDificultad.BASICO, duracionMinutos = 60, chef = "Chef Carla Muñoz",
            pasos = "Cremar mantequilla con azúcar\nAgregar huevo y harina\nUslerear y cortar las formas\nHornear a 170 °C y decorar en frío"
        ),
        Contenido(
            titulo = "Empanadas de pino al horno",
            descripcion = "Masa y pino tradicional para producción en cantidad.",
            categoria = Categoria.EMPANADAS, tipo = TipoContenido.RECETA,
            nivel = NivelDificultad.INTERMEDIO, duracionMinutos = 120, chef = "Chef Pedro Lagos",
            pasos = "Preparar el pino el día anterior\nAmasar con manteca y salmuera tibia\nRellenar y cerrar con repulgue\nPintar con huevo y hornear a 200 °C"
        ),
        Contenido(
            titulo = "Pan amasado de producción",
            descripcion = "Receta escalable para panaderías de barrio.",
            categoria = Categoria.HORNEO, tipo = TipoContenido.RECETA,
            nivel = NivelDificultad.BASICO, duracionMinutos = 150, chef = "Chef Luis Pérez",
            pasos = "Mezclar harina, sal y levadura\nAgregar manteca y agua tibia\nAmasar y dejar leudar\nFormar, pinchar y hornear a 220 °C"
        ),
        Contenido(
            titulo = "Curso de productos de horneo",
            descripcion = "Fermentación, temperaturas y uso de mejoradores en panificación.",
            categoria = Categoria.HORNEO, tipo = TipoContenido.CURSO,
            nivel = NivelDificultad.INTERMEDIO, duracionMinutos = 200, chef = "Chef Ana Rojas",
            pasos = "Módulo 1: harinas y fermentos\nMódulo 2: amasado y reposo\nMódulo 3: control de horno\nMódulo 4: costos de producción"
        ),
        Contenido(
            titulo = "Coberturas para heladería",
            descripcion = "Video técnico de salsas y coberturas que no se cristalizan en frío.",
            categoria = Categoria.HELADERIA, tipo = TipoContenido.VIDEO,
            nivel = NivelDificultad.BASICO, duracionMinutos = 12, chef = "Chef Carla Muñoz",
            pasos = "Elegir la cobertura según la temperatura de uso\nFundir a baño maría\nAjustar la fluidez con aceite neutro\nAplicar sobre el helado bien frío"
        )
    )
}
package com.example.master_martini.data.local.model

// Especialidades mencionadas en el caso Master Martini.
enum class Categoria(val etiqueta: String) {
    CHOCOLATERIA("Chocolatería"),
    PASTELERIA_VITRINA("Pastelería de vitrina"),
    HELADERIA("Heladería"),
    BOLLERIA("Bollería"),
    MASAS_HOJA("Masas de hoja"),
    GALLETAS("Galletas"),
    EMPANADAS("Empanadas"),
    HORNEO("Productos de horneo")
}

// Recetas simples, videos técnicos y cursos de especialidad.
enum class TipoContenido(val etiqueta: String) {
    RECETA("Receta"),
    VIDEO("Video técnico"),
    CURSO("Curso")
}

enum class NivelDificultad(val etiqueta: String) {
    BASICO("Básico"),
    INTERMEDIO("Intermedio"),
    AVANZADO("Avanzado")
}

// Rubros que el caso nombra como público objetivo.
enum class TipoNegocio(val etiqueta: String) {
    PANADERIA("Panadería"),
    PASTELERIA("Pastelería"),
    CHOCOLATERIA("Chocolatería"),
    CASINO("Casino"),
    RESTAURANTE("Restaurante"),
    OTRO("Otro negocio gastronómico")
}

enum class TamanoNegocio(val etiqueta: String) {
    EMPRENDIMIENTO("Emprendimiento (1 a 2 personas)"),
    PEQUENO("Pequeño (3 a 10 personas)"),
    MEDIANO("Mediano (11 a 50 personas)"),
    GRANDE("Grande (más de 50 personas)")
}
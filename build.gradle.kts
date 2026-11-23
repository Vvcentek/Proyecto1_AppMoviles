// Archivo de compilación de nivel superior donde puedes añadir opciones de configuración comunes a todos los subproyectos/módulos.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
}
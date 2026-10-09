package com.example.master_martini.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Un solo esquema claro y de alto contraste: menos estados que probar y
// colores de marca consistentes en todos los dispositivos.
private val EsquemaClaro = lightColorScheme(
    primary = Burdeo,
    onPrimary = Color.White,
    primaryContainer = BurdeoClaro,
    onPrimaryContainer = BurdeoMuyOscuro,
    secondary = Oliva,
    onSecondary = Color.White,
    secondaryContainer = OlivaClaro,
    onSecondaryContainer = OlivaMuyOscuro,
    tertiary = Dorado,
    onTertiary = Color.White,
    tertiaryContainer = DoradoClaro,
    background = Crema,
    onBackground = TextoOscuro,
    surface = Crema,
    onSurface = TextoOscuro,
    surfaceVariant = CremaOscuro,
    onSurfaceVariant = TextoSecundario,
    outline = Borde,
    error = ErrorRojo,
    onError = Color.White,
    errorContainer = ErrorRojoClaro,
    onErrorContainer = ErrorRojoOscuro
)

@Composable
fun Master_MartiniTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaClaro,
        typography = Typography,
        content = content
    )
}
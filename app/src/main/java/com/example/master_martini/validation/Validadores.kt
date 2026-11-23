package com.example.master_martini.validation

// Reglas de validación en Kotlin puro: no dependen de Android ni de Compose,
// por eso se prueban con tests unitarios y se reutilizan en cualquier pantalla.
object Validadores {

    private val REGEX_CORREO = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun nombre(valor: String): String? {
        val texto = valor.trim()
        return when {
            texto.isEmpty() -> "Ingresa tu nombre"
            texto.length < 3 -> "Debe tener al menos 3 letras"
            !texto.all { it.isLetter() || it == ' ' } -> "Usa solo letras y espacios"
            else -> null
        }
    }

    fun correo(valor: String): String? {
        val texto = valor.trim()
        return when {
            texto.isEmpty() -> "Ingresa tu correo"
            !REGEX_CORREO.matches(texto) -> "Revisa el formato, por ejemplo: nombre@correo.cl"
            else -> null
        }
    }

    // Teléfono chileno de 9 dígitos (celular con 9 o fijo con 2). Acepta +56, espacios y guiones.
    fun telefono(valor: String): String? {
        val digitos = soloDigitosTelefono(valor)
        return when {
            valor.isBlank() -> "Ingresa tu teléfono"
            digitos.length != 9 -> "Debe tener 9 dígitos, por ejemplo: 9 1234 5678"
            digitos.first() != '9' && digitos.first() != '2' -> "Debe comenzar con 9 (celular) o 2 (fijo)"
            else -> null
        }
    }

    fun soloDigitosTelefono(valor: String): String {
        val digitos = valor.filter { it.isDigit() }
        return if (digitos.length == 11 && digitos.startsWith("56")) digitos.drop(2) else digitos
    }

    fun direccion(valor: String): String? {
        val texto = valor.trim()
        return when {
            texto.isEmpty() -> "Ingresa la dirección del negocio"
            texto.length < 5 -> "La dirección es muy corta"
            texto.none { it.isDigit() } -> "Incluye la numeración, por ejemplo: Av. Matta 123"
            else -> null
        }
    }

    // RUT chileno: se comprueba el dígito verificador con el algoritmo módulo 11.
    fun rut(valor: String): String? {
        val limpio = limpiarRut(valor)
        if (limpio.isEmpty()) return "Ingresa el RUT de la empresa"
        if (limpio.length !in 8..9) return "Revisa el largo, por ejemplo: 76.123.456-0"
        val cuerpo = limpio.dropLast(1)
        val dv = limpio.last()
        if (!cuerpo.all { it.isDigit() }) return "Solo números y el dígito verificador al final"
        return if (calcularDv(cuerpo) == dv) null else "El dígito verificador no corresponde"
    }

    fun calcularDv(cuerpo: String): Char {
        var suma = 0
        var factor = 2
        for (c in cuerpo.reversed()) {
            suma += c.digitToInt() * factor
            factor = if (factor == 7) 2 else factor + 1
        }
        return when (val resultado = 11 - (suma % 11)) {
            11 -> '0'
            10 -> 'K'
            else -> '0' + resultado
        }
    }

    // El RUT se guarda siempre con el mismo formato: 76123456-0
    fun formatearRut(valor: String): String {
        val limpio = limpiarRut(valor)
        return "${limpio.dropLast(1)}-${limpio.last()}"
    }

    private fun limpiarRut(valor: String) = valor.filter { it.isLetterOrDigit() }.uppercase()

    fun <T> seleccion(valor: T?, mensaje: String): String? = if (valor == null) mensaje else null

    fun intereses(seleccionadas: Set<*>): String? =
        if (seleccionadas.isEmpty()) "Elige al menos una especialidad" else null

    // ---------- Eventos (CRUD con el microservicio) ----------

    fun tituloEvento(valor: String): String? {
        val texto = valor.trim()
        return when {
            texto.isEmpty() -> "Ingresa el título del evento"
            texto.length < 5 -> "Debe tener al menos 5 caracteres"
            texto.length > 80 -> "Máximo 80 caracteres"
            else -> null
        }
    }

    fun descripcionEvento(valor: String): String? {
        val texto = valor.trim()
        return when {
            texto.isEmpty() -> "Ingresa una descripción"
            texto.length < 10 -> "Debe tener al menos 10 caracteres"
            texto.length > 300 -> "Máximo 300 caracteres"
            else -> null
        }
    }

    fun lugarEvento(valor: String): String? {
        val texto = valor.trim()
        return when {
            texto.isEmpty() -> "Ingresa el lugar"
            texto.length < 5 -> "El lugar es muy corto"
            texto.length > 120 -> "Máximo 120 caracteres"
            else -> null
        }
    }

    fun cuposEvento(valor: String): String? {
        val numero = valor.trim().toIntOrNull()
        return when {
            valor.isBlank() -> "Ingresa los cupos"
            numero == null -> "Usa solo números"
            numero < 1 -> "Debe haber al menos 1 cupo"
            numero > 500 -> "Máximo 500 cupos"
            else -> null
        }
    }

    private val REGEX_FECHA = Regex("^(\\d{4})-(\\d{2})-(\\d{2})$")

    // Fecha en formato AAAA-MM-DD, que exista en el calendario y no esté en el pasado.
    // "hoy" se puede pasar desde los tests para que no dependan del día en que se ejecutan.
    fun fechaEvento(valor: String, hoy: String = hoyIso()): String? {
        val texto = valor.trim()
        if (texto.isEmpty()) return "Ingresa la fecha"
        val partes = REGEX_FECHA.matchEntire(texto) ?: return "Usa el formato AAAA-MM-DD, por ejemplo: 2026-12-15"
        val (anio, mes, dia) = partes.destructured.toList().map { it.toInt() }
        if (mes !in 1..12) return "El mes debe estar entre 01 y 12"
        if (dia !in 1..diasDelMes(anio, mes)) return "Ese día no existe en el mes indicado"
        // Con el formato AAAA-MM-DD, comparar textos equivale a comparar fechas.
        if (texto < hoy) return "La fecha no puede estar en el pasado"
        return null
    }

    fun diasDelMes(anio: Int, mes: Int): Int = when (mes) {
        2 -> if ((anio % 4 == 0 && anio % 100 != 0) || anio % 400 == 0) 29 else 28
        4, 6, 9, 11 -> 30
        else -> 31
    }

    fun hoyIso(): String =
        java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
}

package com.example.master_martini

import com.example.master_martini.validation.Validadores
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ValidadoresTest {

    @Test
    fun rutValidoConPuntosYGuion() {
        assertNull(Validadores.rut("76.123.456-0"))
    }

    @Test
    fun rutValidoConDigitoK() {
        assertNull(Validadores.rut("40000000-k"))
    }

    @Test
    fun rutConDigitoIncorrecto() {
        assertNotNull(Validadores.rut("76.123.456-5"))
    }

    @Test
    fun calculaDigitoVerificador() {
        assertEquals('5', Validadores.calcularDv("12345678"))
    }

    @Test
    fun correoValidoEInvalido() {
        assertNull(Validadores.correo("cliente@correo.cl"))
        assertNotNull(Validadores.correo("cliente@correo"))
    }

    @Test
    fun telefonoConPrefijoChileno() {
        assertNull(Validadores.telefono("+56 9 1234 5678"))
        assertNotNull(Validadores.telefono("12345"))
    }

    @Test
    fun interesesVacios() {
        assertNotNull(Validadores.intereses(emptySet<String>()))
    }
}
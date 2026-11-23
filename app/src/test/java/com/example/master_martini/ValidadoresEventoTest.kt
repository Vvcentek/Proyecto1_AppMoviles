package com.example.master_martini

import com.example.master_martini.validation.Validadores
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ValidadoresEventoTest {

    private val hoy = "2026-11-23"

    @Test fun `titulo valido`() = assertNull(Validadores.tituloEvento("Taller de chocolate"))
    @Test fun `titulo vacio corto o largo`() {
        assertNotNull(Validadores.tituloEvento("  "))
        assertNotNull(Validadores.tituloEvento("abc"))
        assertNotNull(Validadores.tituloEvento("a".repeat(81)))
    }

    @Test fun `descripcion`() {
        assertNull(Validadores.descripcionEvento("Una descripción suficiente"))
        assertNotNull(Validadores.descripcionEvento(""))
        assertNotNull(Validadores.descripcionEvento("corta"))
        assertNotNull(Validadores.descripcionEvento("a".repeat(301)))
    }

    @Test fun `lugar`() {
        assertNull(Validadores.lugarEvento("Sala Santiago"))
        assertNotNull(Validadores.lugarEvento(""))
        assertNotNull(Validadores.lugarEvento("abc"))
        assertNotNull(Validadores.lugarEvento("a".repeat(121)))
    }

    @Test fun `cupos`() {
        assertNull(Validadores.cuposEvento("50"))
        assertNotNull(Validadores.cuposEvento(""))
        assertNotNull(Validadores.cuposEvento("x"))
        assertNotNull(Validadores.cuposEvento("0"))
        assertNotNull(Validadores.cuposEvento("501"))
    }

    @Test fun `fecha valida hoy o futura`() {
        assertNull(Validadores.fechaEvento("2026-11-23", hoy))
        assertNull(Validadores.fechaEvento("2027-01-31", hoy))
    }

    @Test fun `fecha con formato o dia invalido`() {
        assertNotNull(Validadores.fechaEvento("", hoy))
        assertNotNull(Validadores.fechaEvento("23/11/2026", hoy))
        assertNotNull(Validadores.fechaEvento("2026-13-01", hoy))
        assertNotNull(Validadores.fechaEvento("2026-11-31", hoy))
        assertNotNull(Validadores.fechaEvento("2027-02-29", hoy))
    }

    @Test fun `fecha en el pasado`() = assertNotNull(Validadores.fechaEvento("2026-11-22", hoy))

    @Test fun `bisiestos`() {
        assertEquals(29, Validadores.diasDelMes(2028, 2))
        assertEquals(28, Validadores.diasDelMes(2100, 2))
        assertEquals(29, Validadores.diasDelMes(2000, 2))
        assertEquals(30, Validadores.diasDelMes(2026, 4))
        assertEquals(31, Validadores.diasDelMes(2026, 12))
    }

    @Test fun `hoy tiene formato iso`() =
        assertNotNull(Regex("\\d{4}-\\d{2}-\\d{2}").matchEntire(Validadores.hoyIso()))
}

package cl.duoc.mastermartini;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.duoc.mastermartini.repository.EventoRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

// Prueba el CRUD completo contra una base H2 en memoria.
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EventoControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private EventoRepository repositorio;

    private final String fecha = LocalDate.now().plusDays(10).toString();

    private String json(String titulo) {
        return """
                {"titulo":"%s","descripcion":"Descripción de prueba del taller",
                 "fecha":"%s","lugar":"Sala de pruebas, Santiago","categoria":"GALLETAS","cupos":10}
                """.formatted(titulo, fecha);
    }

    @BeforeEach
    void limpiar() {
        repositorio.deleteAll();
    }

    @Test
    void creaYListaEventos() throws Exception {
        mvc.perform(post("/api/eventos").contentType(MediaType.APPLICATION_JSON).content(json("Taller de galletas")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        mvc.perform(get("/api/eventos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Taller de galletas"));
    }

    @Test
    void actualizaYEliminaEvento() throws Exception {
        String respuesta = mvc.perform(post("/api/eventos").contentType(MediaType.APPLICATION_JSON).content(json("Taller original")))
                .andReturn().getResponse().getContentAsString();
        String id = respuesta.replaceAll(".*\"id\":(\\d+).*", "$1");

        mvc.perform(put("/api/eventos/" + id).contentType(MediaType.APPLICATION_JSON).content(json("Taller editado")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Taller editado"));

        mvc.perform(delete("/api/eventos/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/eventos/" + id)).andExpect(status().isNotFound());
    }

    @Test
    void rechazaDatosInvalidos() throws Exception {
        String invalido = """
                {"titulo":"","descripcion":"corta","fecha":"2000-01-01","lugar":"","categoria":"OTRA","cupos":0}
                """;
        mvc.perform(post("/api/eventos").contentType(MediaType.APPLICATION_JSON).content(invalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.titulo").exists())
                .andExpect(jsonPath("$.fecha").exists())
                .andExpect(jsonPath("$.cupos").exists());
    }

    @Test
    void eventoInexistenteDevuelve404() throws Exception {
        mvc.perform(get("/api/eventos/9999")).andExpect(status().isNotFound());
    }
}

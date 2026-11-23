package cl.duoc.mastermartini.controller;

import cl.duoc.mastermartini.model.Evento;
import cl.duoc.mastermartini.service.EventoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// Endpoints REST del CRUD de eventos: /api/eventos
@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final EventoService servicio;

    public EventoController(EventoService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<Evento> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id}")
    public Evento obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Evento crear(@Valid @RequestBody Evento evento) {
        return servicio.crear(evento);
    }

    @PutMapping("/{id}")
    public Evento actualizar(@PathVariable Long id, @Valid @RequestBody Evento evento) {
        return servicio.actualizar(id, evento);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
    }
}

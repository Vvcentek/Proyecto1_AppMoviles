package cl.duoc.mastermartini.service;

import cl.duoc.mastermartini.model.Evento;
import cl.duoc.mastermartini.repository.EventoRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

// Lógica de negocio del CRUD: el controlador solo recibe y responde.
@Service
public class EventoService {

    private final EventoRepository repositorio;

    public EventoService(EventoRepository repositorio) {
        this.repositorio = repositorio;
    }

    public List<Evento> listar() {
        return repositorio.findAllByOrderByFechaAsc();
    }

    public Evento obtener(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado"));
    }

    public Evento crear(Evento evento) {
        evento.setId(null);
        return repositorio.save(evento);
    }

    public Evento actualizar(Long id, Evento datos) {
        Evento evento = obtener(id);
        evento.setTitulo(datos.getTitulo());
        evento.setDescripcion(datos.getDescripcion());
        evento.setFecha(datos.getFecha());
        evento.setLugar(datos.getLugar());
        evento.setCategoria(datos.getCategoria());
        evento.setCupos(datos.getCupos());
        return repositorio.save(evento);
    }

    public void eliminar(Long id) {
        repositorio.delete(obtener(id));
    }
}

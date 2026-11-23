package cl.duoc.mastermartini.repository;

import cl.duoc.mastermartini.model.Evento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// Spring Data genera el SQL del CRUD (findAll, findById, save, delete).
public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findAllByOrderByFechaAsc();
}

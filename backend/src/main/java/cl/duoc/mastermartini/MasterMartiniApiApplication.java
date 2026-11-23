package cl.duoc.mastermartini;

import cl.duoc.mastermartini.model.Evento;
import cl.duoc.mastermartini.repository.EventoRepository;
import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class MasterMartiniApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MasterMartiniApiApplication.class, args);
    }

    // Carga eventos ficticios la primera vez, para que la app no parta vacía.
    @Bean
    @Profile("!test")
    CommandLineRunner datosIniciales(EventoRepository repositorio) {
        return args -> {
            if (repositorio.count() == 0) {
                LocalDate hoy = LocalDate.now();
                repositorio.save(new Evento(null, "Taller de templado de chocolate",
                        "Práctica guiada de templado y moldeado de bombones.",
                        hoy.plusDays(14), "Sala de capacitación Master Martini, Santiago", "CHOCOLATERIA", 20));
                repositorio.save(new Evento(null, "Masas de hoja para principiantes",
                        "Laminado, vueltas y horneado de masa de hoja.",
                        hoy.plusDays(21), "Sala de capacitación Master Martini, Santiago", "MASAS_HOJA", 15));
                repositorio.save(new Evento(null, "Encuentro de heladeros",
                        "Charla sobre coberturas y bases para heladería artesanal.",
                        hoy.plusDays(30), "Centro de eventos ficticio, Providencia", "HELADERIA", 40));
            }
        };
    }
}

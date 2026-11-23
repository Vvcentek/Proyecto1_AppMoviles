package cl.duoc.mastermartini.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

// Tabla "evento": cada fila es un evento o taller de Master Martini.
@Entity
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 5, max = 80, message = "El título debe tener entre 5 y 80 caracteres")
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, max = 300, message = "La descripción debe tener entre 10 y 300 caracteres")
    private String descripcion;

    @NotNull(message = "La fecha es obligatoria")
    @FutureOrPresent(message = "La fecha no puede estar en el pasado")
    private LocalDate fecha;

    @NotBlank(message = "El lugar es obligatorio")
    @Size(min = 5, max = 120, message = "El lugar debe tener entre 5 y 120 caracteres")
    private String lugar;

    // Debe coincidir con una de las especialidades de la app.
    @NotBlank(message = "La categoría es obligatoria")
    @Pattern(regexp = "CHOCOLATERIA|PASTELERIA_VITRINA|HELADERIA|BOLLERIA|MASAS_HOJA|GALLETAS|EMPANADAS|HORNEO",
            message = "La categoría no es válida")
    private String categoria;

    @NotNull(message = "Los cupos son obligatorios")
    @Min(value = 1, message = "Debe haber al menos 1 cupo")
    @Max(value = 500, message = "No puede haber más de 500 cupos")
    private Integer cupos;

    public Evento() {
    }

    public Evento(Long id, String titulo, String descripcion, LocalDate fecha, String lugar,
                  String categoria, Integer cupos) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.lugar = lugar;
        this.categoria = categoria;
        this.cupos = cupos;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getLugar() { return lugar; }
    public void setLugar(String lugar) { this.lugar = lugar; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public Integer getCupos() { return cupos; }
    public void setCupos(Integer cupos) { this.cupos = cupos; }
}

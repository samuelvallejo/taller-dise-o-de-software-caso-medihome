import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AtencionMedica {
    private final ServicioDomiciliario servicio;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFinalizacion;
    private String observaciones = "Sin observaciones registradas";
    private String recomendaciones = "Sin recomendaciones registradas";
    private final List<MedicionSignos> mediciones = new ArrayList<>();

    // Se utiliza desde ServicioDomiciliario.iniciarAtencion().
    AtencionMedica(ServicioDomiciliario servicio, LocalDateTime fechaHoraInicio) {
        this.servicio = Objects.requireNonNull(servicio, "servicio");
        setFechaHoraInicio(fechaHoraInicio);
    }

    public ServicioDomiciliario getServicio() { return servicio; }
    public LocalDateTime getFechaHoraInicio() { return fechaHoraInicio; }

    public void setFechaHoraInicio(LocalDateTime inicio) {
        Objects.requireNonNull(inicio, "inicio");
        if (servicio.getEstado() == EstadoServicio.FINALIZADO) {
            throw new IllegalStateException("La atención ya finalizó.");
        }
        if (inicio.isBefore(servicio.getFechaHora())) {
            throw new IllegalArgumentException("El inicio es anterior a la programación.");
        }
        for (MedicionSignos medicion : mediciones) {
            if (medicion.getFechaHora().isBefore(inicio)) {
                throw new IllegalArgumentException("El inicio dejaría una medición fuera de la atención.");
            }
        }
        fechaHoraInicio = inicio;
    }

    public LocalDateTime getFechaHoraFinalizacion() { return fechaHoraFinalizacion; }

    /** Establecer la finalización también finaliza el servicio asociado. */
    public void setFechaHoraFinalizacion(LocalDateTime fin) {
        finalizar(fin);
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = Validacion.texto(observaciones, "observaciones");
    }

    public String getRecomendaciones() {
        return recomendaciones;
    }

    public void setRecomendaciones(String recomendaciones) {
        this.recomendaciones = Validacion.texto(recomendaciones, "recomendaciones");
    }

    public List<MedicionSignos> getMediciones() {
        return Collections.unmodifiableList(mediciones);
    }

    /** Composición: cada medición nace dentro de una única atención. */
    public MedicionSignos registrarMedicion(LocalDateTime fechaHora, double temperatura,
            int frecuenciaCardiaca, int presionSistolica, int presionDiastolica,
            double saturacionOxigeno) {
        if (servicio.getEstado() != EstadoServicio.EN_ATENCION) {
            throw new IllegalStateException("Las mediciones se registran durante la atención.");
        }
        MedicionSignos medicion = new MedicionSignos(this, fechaHora, temperatura,
                frecuenciaCardiaca, presionSistolica, presionDiastolica, saturacionOxigeno);
        mediciones.add(medicion);
        return medicion;
    }

    public void finalizar(LocalDateTime fin) {
        if (servicio.getEstado() != EstadoServicio.EN_ATENCION) {
            throw new IllegalStateException("La atención debe estar en curso para finalizarla.");
        }
        Objects.requireNonNull(fin, "fin");
        if (fin.isBefore(fechaHoraInicio)) {
            throw new IllegalArgumentException("La finalización no puede ser anterior al inicio.");
        }
        for (MedicionSignos medicion : mediciones) {
            if (medicion.getFechaHora().isAfter(fin)) {
                throw new IllegalArgumentException("Hay mediciones posteriores a la finalización.");
            }
        }
        fechaHoraFinalizacion = fin;
        servicio.setEstado(EstadoServicio.FINALIZADO);
    }
}

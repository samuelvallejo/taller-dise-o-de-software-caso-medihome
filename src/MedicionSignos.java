import java.time.LocalDateTime;
import java.util.Objects;

public class MedicionSignos {
    private final AtencionMedica atencion;
    private LocalDateTime fechaHora;
    private double temperatura;
    private int frecuenciaCardiaca;
    private int presionSistolica;
    private int presionDiastolica;
    private double saturacionOxigeno;

    // Se utiliza desde AtencionMedica.registrarMedicion().
    MedicionSignos(AtencionMedica atencion, LocalDateTime fechaHora, double temperatura,
            int frecuenciaCardiaca, int presionSistolica, int presionDiastolica,
            double saturacionOxigeno) {
        this.atencion = Objects.requireNonNull(atencion, "atencion");
        setFechaHora(fechaHora);
        setTemperatura(temperatura);
        setFrecuenciaCardiaca(frecuenciaCardiaca);
        setPresionSistolica(presionSistolica);
        setPresionDiastolica(presionDiastolica);
        setSaturacionOxigeno(saturacionOxigeno);
    }

    public AtencionMedica getAtencion() { return atencion; }
    public LocalDateTime getFechaHora() { return fechaHora; }

    public void setFechaHora(LocalDateTime fechaHora) {
        Objects.requireNonNull(fechaHora, "fechaHora");
        if (fechaHora.isBefore(atencion.getFechaHoraInicio())
                || (atencion.getFechaHoraFinalizacion() != null
                && fechaHora.isAfter(atencion.getFechaHoraFinalizacion()))) {
            throw new IllegalArgumentException("La medición debe pertenecer al intervalo de la atención.");
        }
        this.fechaHora = fechaHora;
    }

    public double getTemperatura() { return temperatura; }
    public void setTemperatura(double temperatura) {
        this.temperatura = Validacion.positivo(temperatura, "temperatura");
    }

    public int getFrecuenciaCardiaca() { return frecuenciaCardiaca; }
    public void setFrecuenciaCardiaca(int frecuenciaCardiaca) {
        Validacion.positivo(frecuenciaCardiaca, "frecuenciaCardiaca");
        this.frecuenciaCardiaca = frecuenciaCardiaca;
    }

    public int getPresionSistolica() { return presionSistolica; }
    public void setPresionSistolica(int presionSistolica) {
        Validacion.positivo(presionSistolica, "presionSistolica");
        this.presionSistolica = presionSistolica;
    }

    public int getPresionDiastolica() { return presionDiastolica; }
    public void setPresionDiastolica(int presionDiastolica) {
        Validacion.positivo(presionDiastolica, "presionDiastolica");
        this.presionDiastolica = presionDiastolica;
    }

    public double getSaturacionOxigeno() { return saturacionOxigeno; }
    public void setSaturacionOxigeno(double saturacionOxigeno) {
        Validacion.positivo(saturacionOxigeno, "saturacionOxigeno");
        if (saturacionOxigeno > 100) {
            throw new IllegalArgumentException("La saturación no puede superar el 100 %.");
        }
        this.saturacionOxigeno = saturacionOxigeno;
    }
}

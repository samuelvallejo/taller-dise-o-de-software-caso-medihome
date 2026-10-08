import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

public class ServicioDomiciliario {
    private String codigoUnico;
    private LocalDateTime fechaHora;
    private String direccionAtencion;
    private String motivo;
    private EstadoServicio estado = EstadoServicio.SOLICITADO;
    private final Paciente paciente;
    private ProfesionalSalud profesional;
    private AtencionMedica atencion;
    private Empresa empresa;

    public ServicioDomiciliario(String codigoUnico, LocalDateTime fechaHora,
                                String direccionAtencion, String motivo, Paciente paciente) {
        this.paciente = Objects.requireNonNull(paciente, "paciente");
        setCodigoUnico(codigoUnico);
        setFechaHora(fechaHora);
        setDireccionAtencion(direccionAtencion);
        setMotivo(motivo);
        paciente.agregarServicio(this);
    }

    public String getCodigoUnico() { return codigoUnico; }

    public void setCodigoUnico(String codigoUnico) {
        String codigo = Validacion.texto(codigoUnico, "codigoUnico");
        for (ServicioDomiciliario servicio : paciente.getServicios()) {
            if (servicio != this && codigo.equals(servicio.getCodigoUnico())) {
                throw new IllegalArgumentException("El código del servicio ya existe.");
            }
        }
        if (empresa != null) {
            empresa.validarCodigoServicio(codigo, this);
        }
        this.codigoUnico = codigo;
    }

    public LocalDateTime getFechaHora() { return fechaHora; }

    public void setFechaHora(LocalDateTime fechaHora) {
        exigirEditable();
        Objects.requireNonNull(fechaHora, "fechaHora");
        if (profesional != null && !profesional.estaDisponible(fechaHora, this)) {
            throw new IllegalArgumentException("El profesional ya tiene un servicio en esa fecha y hora.");
        }
        this.fechaHora = fechaHora;
    }

    public String getDireccionAtencion() { return direccionAtencion; }
    public void setDireccionAtencion(String direccionAtencion) {
        exigirEditable();
        this.direccionAtencion = Validacion.texto(direccionAtencion, "direccionAtencion");
    }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) {
        exigirEditable();
        this.motivo = Validacion.texto(motivo, "motivo");
    }

    public EstadoServicio getEstado() { return estado; }

    /** El setter valida la transición; no permite saltarse la atención médica. */
    public void setEstado(EstadoServicio nuevoEstado) {
        Objects.requireNonNull(nuevoEstado, "estado");
        if (estado == nuevoEstado) { return; }
        boolean valido = (estado == EstadoServicio.SOLICITADO
                && nuevoEstado == EstadoServicio.PROGRAMADO && profesional != null)
                || (estado == EstadoServicio.PROGRAMADO
                && nuevoEstado == EstadoServicio.EN_ATENCION && atencion != null)
                || (estado == EstadoServicio.EN_ATENCION
                && nuevoEstado == EstadoServicio.FINALIZADO && atencion != null
                && atencion.getFechaHoraFinalizacion() != null)
                || ((estado == EstadoServicio.SOLICITADO || estado == EstadoServicio.PROGRAMADO)
                && nuevoEstado == EstadoServicio.CANCELADO);
        if (!valido) {
            throw new IllegalStateException("No se puede pasar de " + estado + " a " + nuevoEstado + ".");
        }
        estado = nuevoEstado;
        paciente.notificar("Servicio " + codigoUnico + ": " + estado + ".");
        if (profesional != null) {
            profesional.notificar("Servicio " + codigoUnico + ": " + estado + ".");
        }
    }

    public Paciente getPaciente() { return paciente; }
    public ProfesionalSalud getProfesional() { return profesional; }
    public AtencionMedica getAtencion() { return atencion; }
    public Empresa getEmpresa() { return empresa; }
    void vincularEmpresa(Empresa empresa) { this.empresa = empresa; }

    public void programar(ProfesionalSalud profesional, LocalDateTime fechaHora) {
        if (estado != EstadoServicio.SOLICITADO) {
            throw new IllegalStateException("Solo se programa un servicio solicitado.");
        }
        Objects.requireNonNull(profesional, "profesional");
        Objects.requireNonNull(fechaHora, "fechaHora");
        if (empresa != null && !empresa.getProfesionales().contains(profesional)) {
            throw new IllegalArgumentException("El profesional no está registrado en la empresa.");
        }
        if (!profesional.estaDisponible(fechaHora, this)) {
            throw new IllegalArgumentException("El profesional ya tiene un servicio en esa fecha y hora.");
        }
        this.profesional = profesional;
        this.fechaHora = fechaHora;
        profesional.agregarServicio(this);
        setEstado(EstadoServicio.PROGRAMADO);
    }

    /** Composición: la atención se crea desde su servicio y no se puede trasladar. */
    public AtencionMedica iniciarAtencion(LocalDateTime inicio) {
        if (estado != EstadoServicio.PROGRAMADO) {
            throw new IllegalStateException("El servicio debe estar programado para iniciar la atención.");
        }
        Objects.requireNonNull(inicio, "inicio");
        if (inicio.isBefore(fechaHora)) {
            throw new IllegalArgumentException("El inicio no puede ser anterior a la fecha programada.");
        }
        atencion = new AtencionMedica(this, inicio);
        setEstado(EstadoServicio.EN_ATENCION);
        return atencion;
    }

    public void cancelar() { setEstado(EstadoServicio.CANCELADO); }

    private void exigirEditable() {
        if (estado != EstadoServicio.SOLICITADO && estado != EstadoServicio.PROGRAMADO) {
            throw new IllegalStateException("El servicio ya no permite modificar sus datos de solicitud.");
        }
    }

    public String generarReporte() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        StringBuilder reporte = new StringBuilder();
        reporte.append("\n========== REPORTE DE ATENCIÓN MEDIHOME ==========\n");
        if (empresa != null) {
            reporte.append("Empresa: ").append(empresa.getNombre()).append("\n");
        }
        reporte.append("Paciente: ").append(paciente.getNombre())
                .append(" | Identificación: ").append(paciente.getIdentificacion()).append("\n")
                .append("Servicio: ").append(codigoUnico).append("\n")
                .append("Programado: ").append(fechaHora.format(formato)).append("\n")
                .append("Dirección: ").append(direccionAtencion).append("\n")
                .append("Motivo: ").append(motivo).append("\n")
                .append("Estado: ").append(estado).append("\n");
        if (profesional != null) {
            reporte.append("Profesional: ").append(profesional.getNombre())
                    .append(" | Especialidad: ").append(profesional.getEspecialidad())
                    .append(" | Registro: ").append(profesional.getNumeroRegistroProfesional()).append("\n");
        }
        if (atencion == null) {
            return reporte.append("Todavía no se ha registrado una atención médica.\n").toString();
        }
        reporte.append("Inicio: ").append(atencion.getFechaHoraInicio().format(formato)).append("\n")
                .append("Finalización: ").append(atencion.getFechaHoraFinalizacion() == null
                        ? "En curso" : atencion.getFechaHoraFinalizacion().format(formato)).append("\n")
                .append("Observaciones clínicas: ").append(atencion.getObservaciones()).append("\n")
                .append("Recomendaciones: ").append(atencion.getRecomendaciones()).append("\n")
                .append("Mediciones de signos vitales: ").append(atencion.getMediciones().size()).append("\n");
        for (MedicionSignos medicion : atencion.getMediciones()) {
            reporte.append(String.format(Locale.ROOT,
                    "  %s | Temperatura: %.1f °C | FC: %d lpm | PA: %d/%d mmHg | SpO2: %.1f %%\n",
                    medicion.getFechaHora().format(formato), medicion.getTemperatura(),
                    medicion.getFrecuenciaCardiaca(), medicion.getPresionSistolica(),
                    medicion.getPresionDiastolica(), medicion.getSaturacionOxigeno()));
        }
        return reporte.append("================================================\n").toString();
    }
}

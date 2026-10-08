import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfesionalSalud extends Usuario implements INotificable {
    private String numeroRegistroProfesional;
    private String especialidad;
    private EquipoMedico equipo;
    private final List<ServicioDomiciliario> servicios = new ArrayList<>();

    public ProfesionalSalud(String identificacion, String nombre, String correo,
                            String numeroRegistroProfesional, String especialidad) {
        super(identificacion, nombre, correo);
        setNumeroRegistroProfesional(numeroRegistroProfesional);
        setEspecialidad(especialidad);
    }

    public String getNumeroRegistroProfesional() {
        return numeroRegistroProfesional;
    }

    public void setNumeroRegistroProfesional(String numeroRegistroProfesional) {
        this.numeroRegistroProfesional = Validacion.texto(numeroRegistroProfesional, "numeroRegistroProfesional");
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = Validacion.texto(especialidad, "especialidad");
    }

    public EquipoMedico getEquipo() {
        return equipo;
    }

    /** Cambiar de equipo conserva el profesional y actualiza ambos equipos. */
    public void setEquipo(EquipoMedico nuevoEquipo) {
        if (equipo == nuevoEquipo) {
            return;
        }
        if (equipo != null) {
            equipo.desvincular(this);
        }
        equipo = nuevoEquipo;
        if (equipo != null) {
            equipo.vincular(this);
        }
    }

    public List<ServicioDomiciliario> getServicios() {
        return Collections.unmodifiableList(servicios);
    }

    public boolean estaDisponible(LocalDateTime fechaHora, ServicioDomiciliario actual) {
        for (ServicioDomiciliario servicio : servicios) {
            if (servicio != actual && servicio.getEstado() != EstadoServicio.CANCELADO
                    && fechaHora.equals(servicio.getFechaHora())) {
                return false;
            }
        }
        return true;
    }

    void agregarServicio(ServicioDomiciliario servicio) {
        if (!servicios.contains(servicio)) {
            servicios.add(servicio);
        }
    }

    @Override
    public void notificar(String mensaje) {
        System.out.println("[Notificación al profesional " + getNombre() + "] "
                + Validacion.texto(mensaje, "mensaje"));
    }
}

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Empresa {
    private String identificacion;
    private String nombre;
    private String correo;
    private String telefono;
    private String direccionPrincipal;
    private final List<Paciente> pacientes = new ArrayList<>();
    private final List<ProfesionalSalud> profesionales = new ArrayList<>();
    private final List<EquipoMedico> equipos = new ArrayList<>();
    private final List<ServicioDomiciliario> servicios = new ArrayList<>();

    public Empresa(String identificacion, String nombre, String correo,
                   String telefono, String direccionPrincipal) {
        setIdentificacion(identificacion);
        setNombre(nombre);
        setCorreo(correo);
        setTelefono(telefono);
        setDireccionPrincipal(direccionPrincipal);
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = Validacion.texto(identificacion, "identificacion");
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = Validacion.texto(nombre, "nombre");
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = Validacion.texto(correo, "correo");
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = Validacion.texto(telefono, "telefono");
    }

    public String getDireccionPrincipal() {
        return direccionPrincipal;
    }

    public void setDireccionPrincipal(String direccionPrincipal) {
        this.direccionPrincipal = Validacion.texto(direccionPrincipal, "direccionPrincipal");
    }

    public List<Paciente> getPacientes() { return Collections.unmodifiableList(pacientes); }
    public List<ProfesionalSalud> getProfesionales() { return Collections.unmodifiableList(profesionales); }
    public List<EquipoMedico> getEquipos() { return Collections.unmodifiableList(equipos); }
    public List<ServicioDomiciliario> getServicios() { return Collections.unmodifiableList(servicios); }

    public void registrarPaciente(Paciente paciente) {
        Objects.requireNonNull(paciente, "paciente");
        for (Paciente registrado : pacientes) {
            if (registrado.getIdentificacion().equals(paciente.getIdentificacion())) {
                throw new IllegalArgumentException("El paciente ya está registrado.");
            }
        }
        pacientes.add(paciente);
    }

    public void registrarProfesional(ProfesionalSalud profesional) {
        Objects.requireNonNull(profesional, "profesional");
        for (ProfesionalSalud registrado : profesionales) {
            if (registrado.getIdentificacion().equals(profesional.getIdentificacion())) {
                throw new IllegalArgumentException("El profesional ya está registrado.");
            }
        }
        profesionales.add(profesional);
    }

    public void agregarEquipo(EquipoMedico equipo) {
        Objects.requireNonNull(equipo, "equipo");
        for (EquipoMedico registrado : equipos) {
            if (registrado.getCodigo().equals(equipo.getCodigo())) {
                throw new IllegalArgumentException("El código de equipo ya existe.");
            }
        }
        equipos.add(equipo);
    }

    public void registrarServicio(ServicioDomiciliario servicio) {
        Objects.requireNonNull(servicio, "servicio");
        if (!pacientes.contains(servicio.getPaciente())) {
            throw new IllegalArgumentException("Primero registre al paciente en la empresa.");
        }
        if (servicio.getEmpresa() != null) {
            throw new IllegalArgumentException("El servicio ya está registrado en una empresa.");
        }
        validarCodigoServicio(servicio.getCodigoUnico(), servicio);
        servicios.add(servicio);
        servicio.vincularEmpresa(this);
    }

    void validarCodigoServicio(String codigo, ServicioDomiciliario actual) {
        for (ServicioDomiciliario servicio : servicios) {
            if (servicio != actual && servicio.getCodigoUnico().equals(codigo)) {
                throw new IllegalArgumentException("El código del servicio ya existe.");
            }
        }
    }
}

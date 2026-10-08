import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class EquipoMedico {
    private String codigo;
    private String nombre;
    private String zonaCobertura;
    private final List<ProfesionalSalud> profesionales = new ArrayList<>();

    public EquipoMedico(String codigo, String nombre, String zonaCobertura) {
        setCodigo(codigo);
        setNombre(nombre);
        setZonaCobertura(zonaCobertura);
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = Validacion.texto(codigo, "codigo");
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = Validacion.texto(nombre, "nombre");
    }

    public String getZonaCobertura() {
        return zonaCobertura;
    }

    public void setZonaCobertura(String zonaCobertura) {
        this.zonaCobertura = Validacion.texto(zonaCobertura, "zonaCobertura");
    }

    public List<ProfesionalSalud> getProfesionales() {
        return Collections.unmodifiableList(profesionales);
    }

    public void agregarProfesional(ProfesionalSalud profesional) {
        Objects.requireNonNull(profesional, "profesional").setEquipo(this);
    }

    public void quitarProfesional(ProfesionalSalud profesional) {
        Objects.requireNonNull(profesional, "profesional");
        if (profesional.getEquipo() == this) {
            profesional.setEquipo(null);
        }
    }

    void vincular(ProfesionalSalud profesional) {
        if (!profesionales.contains(profesional)) {
            profesionales.add(profesional);
        }
    }

    void desvincular(ProfesionalSalud profesional) {
        profesionales.remove(profesional);
    }
}

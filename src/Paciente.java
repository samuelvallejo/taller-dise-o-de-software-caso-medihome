import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Paciente extends Usuario implements INotificable {
    private String telefono;
    private String direccionPrincipal;
    private final List<ServicioDomiciliario> servicios = new ArrayList<>();

    public Paciente(String identificacion, String nombre, String correo,
                    String telefono, String direccionPrincipal) {
        super(identificacion, nombre, correo);
        setTelefono(telefono);
        setDireccionPrincipal(direccionPrincipal);
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

    public List<ServicioDomiciliario> getServicios() {
        return Collections.unmodifiableList(servicios);
    }

    // El servicio mantiene esta asociación al construirse.
    void agregarServicio(ServicioDomiciliario servicio) {
        if (!servicios.contains(servicio)) {
            servicios.add(servicio);
        }
    }

    @Override
    public void notificar(String mensaje) {
        System.out.println("[Notificación al paciente " + getNombre() + "] "
                + Validacion.texto(mensaje, "mensaje"));
    }
}

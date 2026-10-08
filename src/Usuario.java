/** Datos comunes de pacientes y profesionales. */
public abstract class Usuario {
    private String identificacion;
    private String nombre;
    private String correo;

    public Usuario(String identificacion, String nombre, String correo) {
        setIdentificacion(identificacion);
        setNombre(nombre);
        setCorreo(correo);
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
}

public enum EstadoServicio {
    SOLICITADO("Solicitado"),
    PROGRAMADO("Programado"),
    EN_ATENCION("En atención"),
    FINALIZADO("Finalizado"),
    CANCELADO("Cancelado");

    private final String descripcion;

    EstadoServicio(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}

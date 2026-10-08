/** Validaciones sencillas compartidas por las clases del modelo. */
final class Validacion {
    private Validacion() { }

    static String texto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " no puede estar vacío.");
        }
        return valor.trim();
    }

    static double positivo(double valor, String campo) {
        if (!Double.isFinite(valor) || valor <= 0) {
            throw new IllegalArgumentException(campo + " debe ser positivo y finito.");
        }
        return valor;
    }
}

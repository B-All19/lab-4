import java.util.Locale;
/** Validaciones comunes sin efectos secundarios. */
public final class Validacion {
    private Validacion() { }
    public static void exigir(boolean condicion, String mensaje) {
        if (!condicion) throw new IllegalArgumentException(mensaje);
    }
    public static String texto(String valor, String campo) {
        exigir(valor != null && !valor.trim().isEmpty(), campo + " no puede estar vacío.");
        return valor.trim().replaceAll("\\s+", " ");
    }
    public static String clave(String valor) {
        return texto(valor, "Identificador").toUpperCase(Locale.ROOT);
    }
}

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
public final class Baloncesto extends DisciplinaEncuentro {
    public Baloncesto() { super("Baloncesto", 4, 6, new BigDecimal("500.00")); }
    @Override public Map<String, Aporte> evaluar(List<Participante> ps, DatosResultado datos) {
        int[] m = marcadores(ps, datos);
        Validacion.exigir(datos.getPenales().isEmpty() && m[0] != m[1], "Baloncesto no admite penales ni empate.");
        return aportes(ps, m[0], m[1], m[0] > m[1] ? 2 : 1, m[1] > m[0] ? 2 : 1, m[0] > m[1] ? 0 : 1);
    }
}

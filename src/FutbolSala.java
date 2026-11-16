import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
/** Regla base de goles, empates y diferencia. */
public class FutbolSala extends DisciplinaEncuentro {
    public FutbolSala() { this("Fútbol sala", new BigDecimal("600.00")); }
    protected FutbolSala(String nombre, BigDecimal cuota) { super(nombre, 4, 8, cuota); }
    @Override public Map<String, Aporte> evaluar(List<Participante> ps, DatosResultado datos) {
        int[] m = marcadores(ps, datos);
        Validacion.exigir(datos.getPenales().isEmpty(), "Esta disciplina no admite penales.");
        if (m[0] == m[1]) return aportes(ps, m[0], m[1], 1, 1, -1);
        return aportes(ps, m[0], m[1], m[0] > m[1] ? 3 : 0, m[1] > m[0] ? 3 : 0, m[0] > m[1] ? 0 : 1);
    }
}

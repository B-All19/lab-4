import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
/** Especialización que cambia cuota y resolución del empate. */
public final class FutbolSalaPenales extends FutbolSala {
    public FutbolSalaPenales() { super("Fútbol sala con penales", new BigDecimal("750.00")); }
    @Override public Map<String, Aporte> evaluar(List<Participante> ps, DatosResultado datos) {
        int[] m = marcadores(ps, datos);
        if (m[0] != m[1]) return super.evaluar(ps, datos);
        Set<String> codigos = Set.of(ps.get(0).getCodigo(), ps.get(1).getCodigo());
        Validacion.exigir(datos.getPenales().keySet().equals(codigos), "El empate requiere los dos penales.");
        int a = datos.getPenales().get(ps.get(0).getCodigo());
        int b = datos.getPenales().get(ps.get(1).getCodigo());
        Validacion.exigir(a >= 0 && b >= 0 && a != b, "Los penales deben ser no negativos y diferentes.");
        return aportes(ps, m[0], m[1], a > b ? 2 : 1, b > a ? 2 : 1, a > b ? 0 : 1);
    }
    @Override public DatosResultado leerResultado(Entrada entrada, List<Participante> ps) {
        DatosResultado base = super.leerResultado(entrada, ps);
        Map<String, Integer> penales = new LinkedHashMap<>();
        if (base.getValores().get(ps.get(0).getCodigo()).compareTo(base.getValores().get(ps.get(1).getCodigo())) == 0) {
            for (Participante p : ps) penales.put(p.getCodigo(), entrada.entero("Penales de " + p.getNombre() + ": "));
        }
        return new DatosResultado(base.getValores(), penales, Set.of());
    }
}

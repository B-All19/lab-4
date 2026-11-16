import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
/** Copia inmutable de marcadores o tiempos, penales y descalificaciones. */
public final class DatosResultado {
    private final Map<String, BigDecimal> valores;
    private final Map<String, Integer> penales;
    private final Set<String> descalificados;
    public DatosResultado(Map<String, BigDecimal> valores, Map<String, Integer> penales, Set<String> descalificados) {
        this.valores = Map.copyOf(valores);
        this.penales = Map.copyOf(penales);
        this.descalificados = Set.copyOf(descalificados);
    }
    public Map<String, BigDecimal> getValores() { return valores; }
    public Map<String, Integer> getPenales() { return penales; }
    public Set<String> getDescalificados() { return descalificados; }
}

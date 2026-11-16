import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
public final class Voleibol extends DisciplinaEncuentro {
    public Voleibol() { super("Voleibol", 4, 6, new BigDecimal("450.00")); }
    @Override public Map<String, Aporte> evaluar(List<Participante> ps, DatosResultado datos) {
        int[] m = marcadores(ps, datos);
        int mayor = Math.max(m[0], m[1]);
        int menor = Math.min(m[0], m[1]);
        Validacion.exigir(datos.getPenales().isEmpty() && mayor == 3 && menor <= 2, "Voleibol requiere 3 a 0, 3 a 1 o 3 a 2.");
        int gana = menor == 2 ? 2 : 3;
        int pierde = menor == 2 ? 1 : 0;
        return aportes(ps, m[0], m[1], m[0] > m[1] ? gana : pierde, m[1] > m[0] ? gana : pierde, m[0] > m[1] ? 0 : 1);
    }
}

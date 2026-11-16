import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
public final class Relevos extends Disciplina {
    private static final int[] PUNTOS = {10, 8, 6, 5, 4, 3, 2, 1};
    public Relevos() { super("Relevos", 3, 8, new BigDecimal("300.00")); }
    @Override public List<Evento> crearEventos(String torneo, List<Participante> ps) {
        return List.of(new Carrera(torneo + "-C1", this, ps));
    }
    @Override public Map<String, Aporte> evaluar(List<Participante> ps, DatosResultado datos) {
        Set<String> esperados = ps.stream().map(Participante::getCodigo).collect(Collectors.toSet());
        Set<String> recibidos = new HashSet<>(datos.getValores().keySet());
        Validacion.exigir(datos.getPenales().isEmpty(), "Relevos no admite penales.");
        Validacion.exigir(datos.getDescalificados().stream().noneMatch(recibidos::contains), "Un descalificado no puede tener tiempo.");
        recibidos.addAll(datos.getDescalificados());
        Validacion.exigir(recibidos.equals(esperados), "Registre tiempo o descalificación de cada inscrito.");
        Set<BigDecimal> tiempos = new TreeSet<>();
        for (BigDecimal tiempo : datos.getValores().values()) {
            Validacion.exigir(tiempo.signum() > 0 && tiempos.add(tiempo), "Los tiempos deben ser positivos y distintos.");
        }
        List<String> orden = new ArrayList<>(datos.getValores().keySet());
        orden.sort((a, b) -> datos.getValores().get(a).compareTo(datos.getValores().get(b)));
        Map<String, Aporte> resultado = new LinkedHashMap<>();
        for (int i = 0; i < orden.size(); i++) resultado.put(orden.get(i), new Aporte(PUNTOS[i], 0, i == 0 ? 1 : 0));
        for (String codigo : datos.getDescalificados()) resultado.put(codigo, new Aporte(0, 0, 0));
        return Map.copyOf(resultado);
    }
    @Override public DatosResultado leerResultado(Entrada entrada, List<Participante> ps) {
        Map<String, BigDecimal> tiempos = new LinkedHashMap<>();
        Set<String> descalificados = new HashSet<>();
        for (Participante p : ps) {
            String texto = entrada.texto("Segundos de " + p.getNombre() + " o D para descalificado: ");
            if (texto.equalsIgnoreCase("D")) descalificados.add(p.getCodigo());
            else {
                try { tiempos.put(p.getCodigo(), new BigDecimal(texto)); }
                catch (NumberFormatException ex) { throw new IllegalArgumentException("Tiempo inválido. Use punto decimal."); }
            }
        }
        return new DatosResultado(tiempos, Map.of(), descalificados);
    }
}

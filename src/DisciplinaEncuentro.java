import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
/** Generación de parejas y validaciones comunes de los deportes por encuentros. */
public abstract class DisciplinaEncuentro extends Disciplina {
    protected DisciplinaEncuentro(String nombre, int minimo, int maximo, BigDecimal cuota) {
        super(nombre, minimo, maximo, cuota);
    }
    @Override public List<Evento> crearEventos(String torneo, List<Participante> participantes) {
        List<Evento> eventos = new ArrayList<>();
        for (int i = 0; i < participantes.size(); i++) {
            for (int j = i + 1; j < participantes.size(); j++) {
                eventos.add(new Encuentro(torneo + "-E" + (eventos.size() + 1), this, participantes.get(i), participantes.get(j)));
            }
        }
        return List.copyOf(eventos);
    }
    protected int[] marcadores(List<Participante> participantes, DatosResultado datos) {
        Validacion.exigir(participantes.size() == 2, "Un encuentro requiere dos participantes.");
        Set<String> codigos = Set.of(participantes.get(0).getCodigo(), participantes.get(1).getCodigo());
        Validacion.exigir(datos.getValores().keySet().equals(codigos) && datos.getDescalificados().isEmpty(), "Debe registrar exactamente los dos marcadores.");
        int[] valores = new int[2];
        for (int i = 0; i < 2; i++) {
            try { valores[i] = datos.getValores().get(participantes.get(i).getCodigo()).intValueExact(); }
            catch (ArithmeticException ex) { throw new IllegalArgumentException("El marcador debe ser un entero dentro del rango de int."); }
            Validacion.exigir(valores[i] >= 0, "Los marcadores no pueden ser negativos.");
        }
        return valores;
    }
    protected Map<String, Aporte> aportes(List<Participante> ps, int a, int b, int puntosA, int puntosB, int ganador) {
        return Map.of(ps.get(0).getCodigo(), new Aporte(puntosA, (long) a - b, ganador == 0 ? 1 : 0),
                      ps.get(1).getCodigo(), new Aporte(puntosB, (long) b - a, ganador == 1 ? 1 : 0));
    }
    @Override public DatosResultado leerResultado(Entrada entrada, List<Participante> participantes) {
        Map<String, BigDecimal> valores = new LinkedHashMap<>();
        for (Participante p : participantes) valores.put(p.getCodigo(), BigDecimal.valueOf(entrada.entero("Marcador de " + p.getNombre() + ": ")));
        return new DatosResultado(valores, Map.of(), Set.of());
    }
}

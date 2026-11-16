import java.util.List;
import java.util.Map;
/** Implementa Evento mediante una ficha y una estrategia deportiva. */
public final class Carrera implements Evento {
    private final FichaEvento ficha;
    private final Disciplina disciplina;
    public Carrera(String codigo, Disciplina disciplina, List<Participante> participantes) {
        this.ficha = new FichaEvento(codigo, participantes);
        this.disciplina = disciplina;
    }
    @Override public FichaEvento getFicha() { return ficha; }
    @Override public Map<String, Aporte> evaluar(DatosResultado datos) { return disciplina.evaluar(ficha.getParticipantes(), datos); }
    @Override public DatosResultado leerResultado(Entrada entrada) { return disciplina.leerResultado(entrada, ficha.getParticipantes()); }
}

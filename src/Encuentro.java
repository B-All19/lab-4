import java.util.List;
import java.util.Map;
/** Implementa Evento mediante una ficha y una estrategia deportiva. */
public final class Encuentro implements Evento {
    private final FichaEvento ficha;
    private final DisciplinaEncuentro disciplina;
    public Encuentro(String codigo, DisciplinaEncuentro disciplina, Participante local, Participante visitante) {
        this.ficha = new FichaEvento(codigo, List.of(local, visitante));
        this.disciplina = disciplina;
    }
    @Override public FichaEvento getFicha() { return ficha; }
    @Override public Map<String, Aporte> evaluar(DatosResultado datos) { return disciplina.evaluar(ficha.getParticipantes(), datos); }
    @Override public DatosResultado leerResultado(Entrada entrada) { return disciplina.leerResultado(entrada, ficha.getParticipantes()); }
}

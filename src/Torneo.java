import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
/** Agregado deportivo. El estado final y el premio se derivan de los eventos. */
public final class Torneo {
    private final String codigo;
    private final String nombre;
    private final Disciplina disciplina;
    private final boolean demostracion;
    private final List<Participante> participantes = new ArrayList<>();
    private List<Evento> eventos = List.of();
    private boolean cerrado;
    public Torneo(String codigo, String nombre, Disciplina disciplina, boolean demostracion) {
        this.codigo = Validacion.clave(codigo);
        this.nombre = Validacion.texto(nombre, "Nombre del torneo");
        this.disciplina = disciplina;
        this.demostracion = demostracion;
    }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public Disciplina getDisciplina() { return disciplina; }
    public boolean isDemostracion() { return demostracion; }
    public List<Participante> getParticipantes() { return List.copyOf(participantes); }
    public List<Evento> getEventos() { return eventos; }
    public EstadoTorneo getEstado() {
        if (!cerrado) return EstadoTorneo.INSCRIPCION;
        return eventos.stream().allMatch(e -> e.getFicha().getEstado() == EstadoEvento.FINALIZADO) ? EstadoTorneo.FINALIZADO : EstadoTorneo.EN_JUEGO;
    }
    void agregar(Participante participante) {
        Validacion.exigir(!cerrado, "Solo se inscribe durante inscripción.");
        disciplina.validarInscripcion(getParticipantes(), participante);
        participantes.add(participante);
    }
    void cerrar() {
        Validacion.exigir(!cerrado, "Las inscripciones ya están cerradas.");
        Validacion.exigir(participantes.size() >= disciplina.getMinimo(), "No se alcanzó el mínimo de participantes.");
        List<Evento> nuevos = List.copyOf(disciplina.crearEventos(codigo, getParticipantes()));
        Validacion.exigir(!nuevos.isEmpty(), "La disciplina debe generar eventos.");
        eventos = nuevos;
        cerrado = true;
    }
    public List<FilaTabla> tabla() {
        List<FilaTabla> filas = new ArrayList<>();
        for (Participante p : participantes) {
            int puntos = 0, victorias = 0;
            long diferencia = 0;
            for (Evento e : eventos) {
                Aporte a = e.getFicha().getAportes().get(p.getCodigo());
                if (a != null) { puntos += a.getPuntos(); diferencia += a.getDiferencia(); victorias += a.getVictorias(); }
            }
            filas.add(new FilaTabla(p, puntos, diferencia, victorias));
        }
        filas.sort(null);
        return List.copyOf(filas);
    }
    public Participante campeon() {
        Validacion.exigir(getEstado() == EstadoTorneo.FINALIZADO, "Todavía no hay campeón.");
        return tabla().get(0).getParticipante();
    }
    public BigDecimal premio() {
        return getEstado() == EstadoTorneo.FINALIZADO ? disciplina.cotizar(participantes.size()).multiply(new BigDecimal("0.50")).setScale(2) : new BigDecimal("0.00");
    }
}

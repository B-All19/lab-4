import java.time.LocalDate;
import java.util.List;
import java.util.Map;
/** Estado común por composición. Solo el servicio confirma cambios. */
public final class FichaEvento {
    private final String codigo;
    private final List<Participante> participantes;
    private LocalDate fecha;
    private int hora;
    private String sede;
    private Map<String, Aporte> aportes;
    private DatosResultado resultado;
    public FichaEvento(String codigo, List<Participante> participantes) {
        this.codigo = Validacion.clave(codigo);
        this.participantes = List.copyOf(participantes);
        Validacion.exigir(!participantes.isEmpty(), "El evento requiere participantes.");
    }
    public String getCodigo() { return codigo; }
    public List<Participante> getParticipantes() { return participantes; }
    public LocalDate getFecha() { return fecha; }
    public int getHora() { return hora; }
    public String getSede() { return sede; }
    public DatosResultado getResultado() { return resultado; }
    public Map<String, Aporte> getAportes() { return aportes == null ? Map.of() : aportes; }
    public EstadoEvento getEstado() {
        return aportes != null ? EstadoEvento.FINALIZADO : fecha == null ? EstadoEvento.SIN_PROGRAMAR : EstadoEvento.PROGRAMADO;
    }
    void programar(LocalDate fecha, int hora, String sede) {
        this.fecha = fecha;
        this.hora = hora;
        this.sede = sede;
    }
    void confirmar(DatosResultado datos, Map<String, Aporte> aportes) {
        Map<String, Aporte> copia = Map.copyOf(aportes);
        this.resultado = datos;
        this.aportes = copia;
    }
}

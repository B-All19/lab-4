import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
/** Punto de extensión para inscripción, eventos y reglas deportivas. */
public abstract class Disciplina {
    private final String nombre;
    private final int minimo;
    private final int maximo;
    private final BigDecimal cuota;
    protected Disciplina(String nombre, int minimo, int maximo, BigDecimal cuota) {
        Validacion.exigir(minimo > 0 && maximo >= minimo && cuota.signum() > 0, "Configuración inválida.");
        this.nombre = Validacion.texto(nombre, "Disciplina");
        this.minimo = minimo;
        this.maximo = maximo;
        this.cuota = cuota.setScale(2);
    }
    public String getNombre() { return nombre; }
    public int getMinimo() { return minimo; }
    public int getMaximo() { return maximo; }
    public BigDecimal getCuota() { return cuota; }
    public BigDecimal cotizar(int cantidad) {
        Validacion.exigir(cantidad > 0, "La cantidad debe ser positiva.");
        return cuota.multiply(BigDecimal.valueOf(cantidad));
    }
    public void validarInscripcion(List<Participante> inscritos, Participante nuevo) {
        Validacion.exigir(inscritos.size() < maximo, "El torneo alcanzó su cupo máximo.");
        Validacion.exigir(inscritos.stream().noneMatch(p -> p.getCodigo().equals(nuevo.getCodigo())), "Participante ya inscrito.");
        Validacion.exigir(inscritos.stream().filter(p -> p.getEmpresa().equals(nuevo.getEmpresa())).count() < 2, "La empresa ya tiene dos participantes.");
    }
    public abstract List<Evento> crearEventos(String torneo, List<Participante> participantes);
    public abstract Map<String, Aporte> evaluar(List<Participante> participantes, DatosResultado datos);
    public abstract DatosResultado leerResultado(Entrada entrada, List<Participante> participantes);
}

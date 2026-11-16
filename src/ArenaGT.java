import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
/** Servicio de aplicación. Valida toda operación antes de confirmar cambios. */
public final class ArenaGT {
    private final Map<String, Participante> participantes = new LinkedHashMap<>();
    private final Map<String, Torneo> torneos = new LinkedHashMap<>();
    private final Map<String, Disciplina> disciplinas = new LinkedHashMap<>();
    private final Map<String, BigDecimal> ingresos = new LinkedHashMap<>();
    private final Set<String> premiosIniciales = new HashSet<>();
    public ArenaGT() {
        for (Disciplina d : List.of(new FutbolSala(), new FutbolSalaPenales(), new Baloncesto(), new Voleibol(), new Relevos())) {
            disciplinas.put(Validacion.clave(d.getNombre()), d);
            ingresos.put(d.getNombre(), new BigDecimal("0.00"));
        }
    }
    public List<Disciplina> getDisciplinas() { return List.copyOf(disciplinas.values()); }
    public List<Participante> getParticipantes() { return List.copyOf(participantes.values()); }
    public List<Torneo> getTorneos() { return List.copyOf(torneos.values()); }
    public Participante participante(String codigo) {
        Participante p = participantes.get(Validacion.clave(codigo));
        Validacion.exigir(p != null, "Participante inexistente.");
        return p;
    }
    public Torneo torneo(String codigo) {
        Torneo t = torneos.get(Validacion.clave(codigo));
        Validacion.exigir(t != null, "Torneo inexistente.");
        return t;
    }
    public void registrarParticipante(String codigo, String nombre, String empresa) {
        Participante p = new Participante(codigo, nombre, empresa);
        Validacion.exigir(!participantes.containsKey(p.getCodigo()), "Código de participante repetido.");
        participantes.put(p.getCodigo(), p);
    }
    public Torneo crearTorneo(String codigo, String nombre, Disciplina disciplina) {
        return crear(codigo, nombre, disciplina, false);
    }
    private Torneo crear(String codigo, String nombre, Disciplina disciplina, boolean demo) {
        Validacion.exigir(disciplinas.containsValue(disciplina), "Seleccione una disciplina del catálogo.");
        Torneo t = new Torneo(codigo, nombre, disciplina, demo);
        Validacion.exigir(!torneos.containsKey(t.getCodigo()), "Código de torneo repetido.");
        torneos.put(t.getCodigo(), t);
        return t;
    }
    Torneo cargarTorneo(String codigo, String nombre, Disciplina disciplina, List<String> codigos) {
        Validacion.exigir(!torneos.containsKey(Validacion.clave(codigo)), "Código de torneo repetido.");
        Torneo t = new Torneo(codigo, nombre, disciplina, true);
        for (String c : codigos) t.agregar(participante(c));
        torneos.put(t.getCodigo(), t);
        return t;
    }
    public void inscribir(String torneo, String participante) {
        Torneo t = torneo(torneo);
        Participante p = participante(participante);
        BigDecimal nuevoIngreso = ingresos.get(t.getDisciplina().getNombre()).add(t.getDisciplina().getCuota());
        t.agregar(p);
        ingresos.put(t.getDisciplina().getNombre(), nuevoIngreso);
    }
    public void cerrarInscripciones(String codigo) { torneo(codigo).cerrar(); }
    public List<Evento> eventos() {
        return torneos.values().stream().flatMap(t -> t.getEventos().stream()).collect(Collectors.toUnmodifiableList());
    }
    public Evento evento(String codigo) {
        String clave = Validacion.clave(codigo);
        return eventos().stream().filter(e -> e.getFicha().getCodigo().equals(clave)).findFirst().orElseThrow(() -> new IllegalArgumentException("Evento inexistente."));
    }
    public void programar(String codigo, LocalDate fecha, int hora, String sede) {
        Evento objetivo = evento(codigo);
        FichaEvento ficha = objetivo.getFicha();
        String sedeValida = Validacion.clave(sede);
        Validacion.exigir(fecha != null && hora >= 0 && hora <= 23, "Fecha y hora inválidas. La hora va de 0 a 23.");
        Validacion.exigir(ficha.getEstado() != EstadoEvento.FINALIZADO, "Un evento finalizado no se reprograma.");
        Set<String> codigos = ficha.getParticipantes().stream().map(Participante::getCodigo).collect(Collectors.toSet());
        for (Evento e : eventos()) {
            FichaEvento otra = e.getFicha();
            if (e == objetivo || !fecha.equals(otra.getFecha()) || hora != otra.getHora()) continue;
            Validacion.exigir(!sedeValida.equals(otra.getSede()), "Conflicto de sede en esa fecha y hora.");
            Validacion.exigir(otra.getParticipantes().stream().noneMatch(p -> codigos.contains(p.getCodigo())), "Conflicto de participante en esa fecha y hora.");
        }
        ficha.programar(fecha, hora, sedeValida);
    }
    public void registrarResultado(String codigo, DatosResultado datos) {
        Evento e = evento(codigo);
        Validacion.exigir(e.getFicha().getEstado() == EstadoEvento.PROGRAMADO, "El evento debe estar programado y no tener resultado.");
        Map<String, Aporte> calculado = e.evaluar(datos);
        Set<String> esperados = e.getFicha().getParticipantes().stream().map(Participante::getCodigo).collect(Collectors.toSet());
        Validacion.exigir(calculado.keySet().equals(esperados), "La disciplina no calculó todos los aportes.");
        e.getFicha().confirmar(datos, calculado);
    }
    public Calendario calendarioGeneral() { return new Calendario(eventos()); }
    public Calendario calendarioTorneo(String codigo) { return new Calendario(torneo(codigo).getEventos()); }
    public Calendario calendarioParticipante(String codigo) {
        Participante p = participante(codigo);
        return new Calendario(eventos().stream().filter(e -> e.getFicha().getParticipantes().contains(p)).collect(Collectors.toList()));
    }
    public Map<String, BigDecimal> ingresosPorDisciplina() { return Map.copyOf(ingresos); }
    public BigDecimal ingresosTotales() { return ingresos.values().stream().reduce(new BigDecimal("0.00"), BigDecimal::add); }
    void cerrarCargaInicial() {
        for (Torneo t : torneos.values()) if (t.isDemostracion() && t.getEstado() == EstadoTorneo.FINALIZADO) premiosIniciales.add(t.getCodigo());
    }
    public boolean esPremioInicial(String codigo) { return premiosIniciales.contains(Validacion.clave(codigo)); }
    public BigDecimal premiosEntregados() {
        return torneos.values().stream().filter(t -> !esPremioInicial(t.getCodigo())).map(Torneo::premio).reduce(new BigDecimal("0.00"), BigDecimal::add);
    }
    public List<FilaEmpresa> rankingEmpresas() {
        Map<String, Long> puntos = new LinkedHashMap<>();
        for (Participante p : participantes.values()) puntos.putIfAbsent(p.getEmpresa(), 0L);
        for (Torneo t : torneos.values()) for (FilaTabla f : t.tabla()) puntos.merge(f.getParticipante().getEmpresa(), (long) f.getPuntos(), Long::sum);
        List<FilaEmpresa> filas = new ArrayList<>();
        puntos.forEach((empresa, total) -> filas.add(new FilaEmpresa(empresa, total)));
        filas.sort(null);
        return List.copyOf(filas);
    }
}

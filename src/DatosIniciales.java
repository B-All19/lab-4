import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
/** Escenario precargado sin movimientos financieros. */
public final class DatosIniciales {
    private DatosIniciales() { }
    public static ArenaGT crear() {
        ArenaGT app = new ArenaGT();
        String[] nombres = {"Alfa", "Beta", "Cobre", "Delta", "Eco", "Faro", "Gama", "Horizonte"};
        String[] empresas = {"Andes", "Andes", "Bosque", "Bosque", "Cima", "Cima", "Duna", "Duna"};
        for (int i = 0; i < 8; i++) app.registrarParticipante("P" + (i + 1), nombres[i], empresas[i]);
        int numero = 0;
        for (Disciplina d : app.getDisciplinas()) {
            numero++;
            int cantidad = numero == 1 ? 6 : numero == 5 ? 8 : 4;
            List<String> codigos = app.getParticipantes().subList(0, cantidad).stream().map(Participante::getCodigo).toList();
            Torneo t = app.cargarTorneo("T" + numero, "Copa " + d.getNombre(), d, codigos);
            app.cerrarInscripciones(t.getCodigo());
            int dia = 0;
            for (Evento e : t.getEventos()) app.programar(e.getFicha().getCodigo(), LocalDate.of(2026, 11, 1).plusDays(dia++), 8 + numero, "Arena " + numero);
        }
        // Alfa y Delta empatan con 3 puntos y diferencia cero, pero Alfa tiene una victoria.
        // Eco y Beta empatan con 1 punto, pero Eco tiene diferencia cero y Beta menos uno.
        partido(app, "T1", "P1", "P2", 1, 0, null, null);
        partido(app, "T1", "P1", "P3", 0, 1, null, null);
        partido(app, "T1", "P2", "P4", 0, 0, null, null);
        partido(app, "T1", "P3", "P4", 0, 0, null, null);
        partido(app, "T1", "P4", "P5", 0, 0, null, null);
        partido(app, "T2", "P1", "P2", 2, 2, 4, 3);
        partido(app, "T2", "P3", "P4", 3, 1, null, null);
        partido(app, "T3", "P1", "P2", 80, 70, null, null);
        partido(app, "T4", "P1", "P2", 3, 0, null, null);
        partido(app, "T4", "P1", "P3", 3, 1, null, null);
        partido(app, "T4", "P1", "P4", 3, 2, null, null);
        Map<String, BigDecimal> tiempos = new LinkedHashMap<>();
        for (int i = 1; i <= 7; i++) tiempos.put("P" + i, new BigDecimal("50.1").add(BigDecimal.valueOf(i - 1)));
        app.registrarResultado("T5-C1", new DatosResultado(tiempos, Map.of(), Set.of("P8")));
        // Segunda carrera para mostrar también el punto del octavo lugar.
        app.cargarTorneo("T6", "Relevos sin descalificaciones", app.getDisciplinas().get(4), app.getParticipantes().stream().map(Participante::getCodigo).toList());
        app.cerrarInscripciones("T6");
        app.programar("T6-C1", LocalDate.of(2026, 11, 20), 14, "Pista central");
        tiempos.put("P8", new BigDecimal("59.0"));
        app.registrarResultado("T6-C1", new DatosResultado(tiempos, Map.of(), Set.of()));
        app.cerrarCargaInicial();
        return app;
    }
    private static void partido(ArenaGT app, String torneo, String a, String b, int x, int y, Integer pa, Integer pb) {
        Evento e = app.torneo(torneo).getEventos().stream().filter(v -> v.getFicha().getParticipantes().get(0).getCodigo().equals(a) && v.getFicha().getParticipantes().get(1).getCodigo().equals(b)).findFirst().orElseThrow();
        Map<String, Integer> penales = pa == null ? Map.of() : Map.of(a, pa, b, pb);
        app.registrarResultado(e.getFicha().getCodigo(), new DatosResultado(Map.of(a, BigDecimal.valueOf(x), b, BigDecimal.valueOf(y)), penales, Set.of()));
    }
}

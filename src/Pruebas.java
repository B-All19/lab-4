import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/** Pruebas reproducibles sin bibliotecas externas. Lanza AssertionError al fallar. */
public final class Pruebas {
    private static int verificadas;
    private Pruebas() { }
    public static void main(String[] args) {
        reglas();
        finanzas();
        rechazos();
        calendarios();
        ordenes();
        consola();
        System.out.println("TOTAL\t" + verificadas + " pruebas\tTodas aprobadas\tTodas aprobadas");
    }
    private static void comprobar(String caso, String entrada, Object esperado, Object obtenido) {
        if (!String.valueOf(esperado).equals(String.valueOf(obtenido))) throw new AssertionError(caso + " esperado " + esperado + " obtenido " + obtenido);
        verificadas++;
        System.out.println(caso + "\t" + entrada + "\t" + esperado + "\t" + obtenido);
    }
    private static DatosResultado marcador(String a, String b, int x, int y) {
        return new DatosResultado(Map.of(a, BigDecimal.valueOf(x), b, BigDecimal.valueOf(y)), Map.of(), Set.of());
    }
    private static String resumen(Map<String, Aporte> aportes) {
        List<String> lineas = new ArrayList<>();
        aportes.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(e -> lineas.add(e.getKey() + " " + e.getValue().getPuntos() + " " + e.getValue().getDiferencia() + " " + e.getValue().getVictorias()));
        return String.join(", ", lineas);
    }
    private static ArenaGT base(int cantidad) {
        ArenaGT a = new ArenaGT();
        for (int i = 1; i <= cantidad; i++) a.registrarParticipante("P" + i, "Nombre " + i, "Empresa " + ((i - 1) / 2));
        return a;
    }
    private static String foto(ArenaGT a) {
        StringBuilder b = new StringBuilder();
        b.append(a.getParticipantes()).append(a.ingresosPorDisciplina()).append(a.premiosEntregados()).append(a.rankingEmpresas());
        for (Torneo t : a.getTorneos()) {
            b.append(t.getCodigo()).append(t.getEstado()).append(t.getParticipantes()).append(t.tabla());
            for (Evento e : t.getEventos()) {
                FichaEvento f = e.getFicha();
                b.append(f.getCodigo()).append(f.getFecha()).append(f.getHora()).append(f.getSede()).append(f.getEstado()).append(resumen(f.getAportes()));
                if (f.getResultado() != null) b.append(f.getResultado().getValores()).append(f.getResultado().getPenales()).append(f.getResultado().getDescalificados());
            }
        }
        return b.toString();
    }
    private static void rechazar(String caso, String entrada, ArenaGT a, Runnable operacion) {
        String antes = foto(a);
        boolean rechazo = false;
        try { operacion.run(); } catch (IllegalArgumentException ex) { rechazo = true; }
        comprobar(caso, entrada, "Rechazo y estado intacto", rechazo && antes.equals(foto(a)) ? "Rechazo y estado intacto" : "Fallo");
    }
    private static void reglas() {
        List<Participante> ps = List.of(new Participante("A", "Alfa", "Uno"), new Participante("B", "Beta", "Dos"));
        comprobar("Fútbol victoria", "4 a 1", "A 3 3 1, B 0 -3 0", resumen(new FutbolSala().evaluar(ps, marcador("A", "B", 4, 1))));
        comprobar("Fútbol empate", "0 a 0", "A 1 0 0, B 1 0 0", resumen(new FutbolSala().evaluar(ps, marcador("A", "B", 0, 0))));
        comprobar("Penales reglamentario", "1 a 3", "A 0 -2 0, B 3 2 1", resumen(new FutbolSalaPenales().evaluar(ps, marcador("A", "B", 1, 3))));
        DatosResultado dp = new DatosResultado(Map.of("A", new BigDecimal("2"), "B", new BigDecimal("2")), Map.of("A", 5, "B", 4), Set.of());
        comprobar("Definición por penales", "Goles 2 a 2 y penales 5 a 4", "A 2 0 1, B 1 0 0", resumen(new FutbolSalaPenales().evaluar(ps, dp)));
        comprobar("Baloncesto", "70 a 80", "A 1 -10 0, B 2 10 1", resumen(new Baloncesto().evaluar(ps, marcador("A", "B", 70, 80))));
        for (int i = 0; i < 3; i++) {
            String esperado = i == 0 ? "A 3 3 1, B 0 -3 0" : i == 1 ? "A 3 2 1, B 0 -2 0" : "A 2 1 1, B 1 -1 0";
            comprobar("Voleibol " + i, "3 a " + i, esperado, resumen(new Voleibol().evaluar(ps, marcador("A", "B", 3, i))));
        }
        comprobar("Voleibol visitante", "2 a 3", "A 1 -1 0, B 2 1 1", resumen(new Voleibol().evaluar(ps, marcador("A", "B", 2, 3))));
        List<Participante> ocho = base(8).getParticipantes();
        Map<String, BigDecimal> tiempos = new LinkedHashMap<>();
        for (int i = 1; i <= 8; i++) tiempos.put("P" + i, BigDecimal.valueOf(49 + i));
        comprobar("Relevos ocho lugares", "Tiempos de 50 a 57 segundos", "P1 10 0 1, P2 8 0 0, P3 6 0 0, P4 5 0 0, P5 4 0 0, P6 3 0 0, P7 2 0 0, P8 1 0 0", resumen(new Relevos().evaluar(ocho, new DatosResultado(tiempos, Map.of(), Set.of()))));
        tiempos.remove("P2");
        comprobar("Relevos descalificación", "P2 descalificado", "P1 10 0 1, P2 0 0 0, P3 8 0 0, P4 6 0 0, P5 5 0 0, P6 4 0 0, P7 3 0 0, P8 2 0 0", resumen(new Relevos().evaluar(ocho, new DatosResultado(tiempos, Map.of(), Set.of("P2")))));
    }
    private static void finanzas() {
        String[] cuotas = {"600.00", "750.00", "500.00", "450.00", "300.00"};
        for (int indice = 0; indice < 5; indice++) {
            ArenaGT a = base(4);
            Disciplina d = a.getDisciplinas().get(indice);
            comprobar("Cuota " + d.getNombre(), "Un participante", cuotas[indice], d.getCuota());
            String antes = foto(a);
            BigDecimal cotizacion = d.cotizar(4);
            comprobar("Cotización " + d.getNombre(), "Cuatro participantes", new BigDecimal(cuotas[indice]).multiply(new BigDecimal("4")), cotizacion);
            comprobar("Cotización sin cambios " + d.getNombre(), "Consultar cotización", true, antes.equals(foto(a)));
            Torneo t = a.crearTorneo("T", "Copa", d);
            for (int i = 1; i <= 4; i++) a.inscribir("T", "P" + i);
            comprobar("Ingresos " + d.getNombre(), "Cuatro inscripciones", cotizacion, a.ingresosTotales());
            comprobar("Ingreso por disciplina " + d.getNombre(), "Consultar ingresos", cotizacion, a.ingresosPorDisciplina().get(d.getNombre()));
            comprobar("Estado inscripción " + d.getNombre(), "Antes del cierre", EstadoTorneo.INSCRIPCION, t.getEstado());
            a.cerrarInscripciones("T");
            comprobar("Eventos " + d.getNombre(), "Cierre con cuatro inscritos", indice == 4 ? 1 : 6, t.getEventos().size());
            comprobar("Estado en juego " + d.getNombre(), "Cerrar inscripciones", EstadoTorneo.EN_JUEGO, t.getEstado());
            int dia = 0;
            for (Evento e : t.getEventos()) {
                a.programar(e.getFicha().getCodigo(), LocalDate.of(2026, 12, 1).plusDays(dia++), 10, "Sede");
                List<Participante> ps = e.getFicha().getParticipantes();
                DatosResultado datos;
                if (indice == 4) {
                    Map<String, BigDecimal> ts = new LinkedHashMap<>();
                    for (int i = 1; i <= 4; i++) ts.put("P" + i, BigDecimal.valueOf(50 + i));
                    datos = new DatosResultado(ts, Map.of(), Set.of());
                } else datos = marcador(ps.get(0).getCodigo(), ps.get(1).getCodigo(), 3, 1);
                a.registrarResultado(e.getFicha().getCodigo(), datos);
            }
            comprobar("Finalización " + d.getNombre(), "Último resultado", EstadoTorneo.FINALIZADO, t.getEstado());
            comprobar("Campeón " + d.getNombre(), "Todos los eventos completos", "P1", t.campeon().getCodigo());
            comprobar("Premio " + d.getNombre(), "Mitad de cuatro cuotas", cotizacion.multiply(new BigDecimal("0.5")).setScale(2), a.premiosEntregados());
            comprobar("Premio no duplicado " + d.getNombre(), "Consultar otra vez", t.premio(), a.premiosEntregados());
        }
        ArenaGT demo = DatosIniciales.crear();
        comprobar("Inicio sin ingresos", "Cargar escenario inicial", "0.00", demo.ingresosTotales());
        comprobar("Inicio sin premios", "Carrera inicial finalizada", "0.00", demo.premiosEntregados());
        comprobar("Torneos iniciales en juego", "Consultar estados", 4L, demo.getTorneos().stream().filter(t -> t.getEstado() == EstadoTorneo.EN_JUEGO).count());
        for (Evento e : demo.torneo("T3").getEventos()) {
            if (e.getFicha().getEstado() == EstadoEvento.FINALIZADO) continue;
            List<Participante> ps = e.getFicha().getParticipantes();
            demo.registrarResultado(e.getFicha().getCodigo(), marcador(ps.get(0).getCodigo(), ps.get(1).getCodigo(), 80, 70));
        }
        comprobar("Premio después de precarga", "Completar baloncesto inicial durante uso normal", "1000.00", demo.premiosEntregados());
    }
    private static void rechazos() {
        ArenaGT a = base(10);
        Disciplina d = a.getDisciplinas().get(2);
        a.crearTorneo("T", "Copa", d);
        rechazar("Código vacío", "Registrar código vacío", a, () -> a.registrarParticipante(" ", "Nombre", "Empresa"));
        rechazar("Participante repetido", "Registrar p1", a, () -> a.registrarParticipante("p1", "Otro", "Otra"));
        rechazar("Torneo repetido", "Crear t", a, () -> a.crearTorneo("t", "Otra", d));
        rechazar("Cantidad cero", "Cotizar cero", a, () -> d.cotizar(0));
        rechazar("Cantidad negativa", "Cotizar menos uno", a, () -> d.cotizar(-1));
        rechazar("Cierre sin mínimo", "Cerrar torneo vacío", a, () -> a.cerrarInscripciones("T"));
        a.inscribir("T", "P1");
        a.inscribir("T", "P2");
        rechazar("Inscripción duplicada", "P1 otra vez", a, () -> a.inscribir("T", "P1"));
        a.registrarParticipante("EXTRA", "Extra", " empresa   0 ");
        rechazar("Límite por empresa", "Tercer equipo de Empresa 0", a, () -> a.inscribir("T", "EXTRA"));
        for (int i = 3; i <= 6; i++) a.inscribir("T", "P" + i);
        rechazar("Cupo máximo", "Séptimo equipo en baloncesto", a, () -> a.inscribir("T", "P7"));
        a.cerrarInscripciones("T");
        rechazar("Fuera de inscripción", "Inscribir P7 tras cierre", a, () -> a.inscribir("T", "P7"));
        rechazar("Segundo cierre", "Cerrar nuevamente", a, () -> a.cerrarInscripciones("T"));
        rechazar("Resultado sin fecha", "Resultado T-E1", a, () -> a.registrarResultado("T-E1", marcador("P1", "P2", 3, 1)));
        rechazar("Hora inválida", "Hora 24", a, () -> a.programar("T-E1", LocalDate.now(), 24, "Arena"));
        rechazar("Sede vacía", "Sede vacía", a, () -> a.programar("T-E1", LocalDate.now(), 10, " "));
        a.programar("T-E1", LocalDate.of(2026, 12, 1), 10, "Arena");
        rechazar("Empate baloncesto", "4 a 4", a, () -> a.registrarResultado("T-E1", marcador("P1", "P2", 4, 4)));
        rechazar("Marcador negativo", "Menos uno a tres", a, () -> a.registrarResultado("T-E1", marcador("P1", "P2", -1, 3)));
        rechazar("Marcador fraccionario", "1.5 a 3", a, () -> a.registrarResultado("T-E1", new DatosResultado(Map.of("P1", new BigDecimal("1.5"), "P2", new BigDecimal("3")), Map.of(), Set.of())));
        a.registrarResultado("T-E1", marcador("P1", "P2", 3, 1));
        rechazar("Resultado repetido", "Repetir T-E1", a, () -> a.registrarResultado("T-E1", marcador("P1", "P2", 3, 1)));
        rechazar("Reprogramar finalizado", "Mover T-E1", a, () -> a.programar("T-E1", LocalDate.now(), 12, "Otra"));
        ArenaGT demo = DatosIniciales.crear();
        rechazar("Voleibol inválido", "2 a 1", demo, () -> demo.registrarResultado("T4-E4", marcador("P2", "P3", 2, 1)));
        rechazar("Penales ausentes", "1 a 1 sin penales", demo, () -> demo.registrarResultado("T2-E2", marcador("P1", "P3", 1, 1)));
        DatosResultado iguales = new DatosResultado(Map.of("P1", BigDecimal.ONE, "P3", BigDecimal.ONE), Map.of("P1", 4, "P3", 4), Set.of());
        rechazar("Penales iguales", "1 a 1 y penales 4 a 4", demo, () -> demo.registrarResultado("T2-E2", iguales));
        DatosResultado sobrantes = new DatosResultado(Map.of("P1", BigDecimal.ONE, "P3", BigDecimal.ZERO), Map.of("P1", 4, "P3", 3), Set.of());
        rechazar("Penales innecesarios", "1 a 0 con penales", demo, () -> demo.registrarResultado("T2-E2", sobrantes));
        a.crearTorneo("R", "Carrera", a.getDisciplinas().get(4));
        for (int i = 1; i <= 3; i++) a.inscribir("R", "P" + i);
        a.cerrarInscripciones("R");
        a.programar("R-C1", LocalDate.of(2026, 12, 2), 10, "Pista");
        rechazar("Tiempos iguales", "50.1 y 50.10 con P3 descalificado", a, () -> a.registrarResultado("R-C1", new DatosResultado(Map.of("P1", new BigDecimal("50.1"), "P2", new BigDecimal("50.10")), Map.of(), Set.of("P3"))));
        rechazar("Tiempo cero", "P1 cero y otros descalificados", a, () -> a.registrarResultado("R-C1", new DatosResultado(Map.of("P1", BigDecimal.ZERO), Map.of(), Set.of("P2", "P3"))));
        rechazar("Resultado incompleto", "Solo tiempo P1", a, () -> a.registrarResultado("R-C1", new DatosResultado(Map.of("P1", BigDecimal.ONE), Map.of(), Set.of())));
    }
    private static void calendarios() {
        ArenaGT a = base(8);
        a.cargarTorneo("F", "Fútbol", a.getDisciplinas().get(0), List.of("P1", "P2", "P3", "P4"));
        a.cargarTorneo("R", "Relevos", a.getDisciplinas().get(4), List.of("P5", "P6", "P7"));
        a.cargarTorneo("B", "Baloncesto", a.getDisciplinas().get(2), List.of("P1", "P2", "P3", "P4"));
        for (String c : List.of("F", "R", "B")) a.cerrarInscripciones(c);
        LocalDate fecha = LocalDate.of(2026, 12, 10);
        a.programar("F-E1", fecha, 9, "Zeta");
        rechazar("Conflicto sede entre tipos", "Carrera misma hora y sede", a, () -> a.programar("R-C1", fecha, 9, " zeta "));
        rechazar("Conflicto participante entre torneos", "P1 en dos encuentros", a, () -> a.programar("B-E1", fecha, 9, "Beta"));
        a.programar("R-C1", fecha, 9, "Alfa");
        a.programar("B-E1", fecha.minusDays(1), 23, "Beta");
        a.programar("F-E2", fecha, 8, "Zeta");
        comprobar("Orden cronológico mixto", "Fechas horas y sedes distintas", "B-E1 F-E2 R-C1 F-E1", codigos(a.calendarioGeneral()));
        comprobar("Calendario torneo", "Torneo F", "F-E2 F-E1", codigos(a.calendarioTorneo("F")));
        comprobar("Calendario participante", "Participante P1", "B-E1 F-E2 F-E1", codigos(a.calendarioParticipante("P1")));
        rechazar("Reprogramación conflictiva", "Mover B-E1 a horario de F-E1", a, () -> a.programar("B-E1", fecha, 9, "Beta"));
        a.programar("F-E1", fecha.plusDays(1), 0, "Nueva");
        comprobar("Reprogramación válida", "Mover F-E1 al día siguiente", fecha.plusDays(1), a.evento("F-E1").getFicha().getFecha());
        comprobar("Evento programado", "Evento F-E1 reprogramado", EstadoEvento.PROGRAMADO, a.evento("F-E1").getFicha().getEstado());
    }
    private static String codigos(Calendario calendario) {
        List<String> codigos = new ArrayList<>();
        for (Evento e : calendario) codigos.add(e.getFicha().getCodigo());
        return String.join(" ", codigos);
    }
    private static void ordenes() {
        Participante a = new Participante("A", "Alfa", "Uno");
        Participante b = new Participante("B", "Beta", "Dos");
        comprobar("Orden por puntos", "Alfa 7 Beta 6", true, new FilaTabla(a, 7, 0, 0).compareTo(new FilaTabla(b, 6, 9, 3)) < 0);
        comprobar("Desempate diferencia", "Ambos 7 puntos diferencias 3 y 1", true, new FilaTabla(a, 7, 3, 1).compareTo(new FilaTabla(b, 7, 1, 2)) < 0);
        comprobar("Desempate victorias", "Igualdad puntos y diferencia victorias 2 y 1", true, new FilaTabla(a, 7, 3, 2).compareTo(new FilaTabla(b, 7, 3, 1)) < 0);
        comprobar("Desempate nombre", "Mismos valores Alfa y Beta", true, new FilaTabla(a, 7, 3, 2).compareTo(new FilaTabla(b, 7, 3, 2)) < 0);
        comprobar("Ranking por puntos", "Uno 10 Dos 8", true, new FilaEmpresa("Uno", 10).compareTo(new FilaEmpresa("Dos", 8)) < 0);
        comprobar("Ranking por nombre", "Andes y Bosque con 10 puntos", true, new FilaEmpresa("Andes", 10).compareTo(new FilaEmpresa("Bosque", 10)) < 0);
        ArenaGT demo = DatosIniciales.crear();
        comprobar("Tabla inicial", "T1 con desempates", "P3 P1 P4 P5 P2 P6", String.join(" ", demo.torneo("T1").tabla().stream().map(f -> f.getParticipante().getCodigo()).toList()));
        comprobar("Ranking acumulado", "Todos los torneos iniciales", "[ANDES | puntos 54, BOSQUE | puntos 33, CIMA | puntos 15, DUNA | puntos 5]", demo.rankingEmpresas());
    }
    private static void consola() {
        ArenaGT a = DatosIniciales.crear();
        String antes = foto(a);
        PrintStream original = System.out;
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(salida));
            new Main(a, new Entrada(new Scanner("abc\\n99\\n6\\nT1-E3\\nfecha mala\\n3\\n1\\ntexto\\n0\\n"))).ejecutar();
        } finally { System.setOut(original); }
        comprobar("Consola tolera errores", "Texto opción 99 fecha y cantidad inválidas", true, salida.toString().contains("Operación rechazada") && foto(a).equals(antes));
    }
}

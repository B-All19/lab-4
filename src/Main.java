import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
/** Menú de consola y presentación, sin reglas deportivas por tipo. */
public final class Main {
    private final ArenaGT app;
    private final Entrada entrada;
    public Main(ArenaGT app, Entrada entrada) { this.app = app; this.entrada = entrada; }
    public static void main(String[] args) {
        new Main(DatosIniciales.crear(), new Entrada(new Scanner(System.in))).ejecutar();
    }
    public void ejecutar() {
        System.out.println("ArenaGT. Datos de demostración sin ingresos ni premios pagados.");
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n1 Registrar participante   2 Crear torneo   3 Cotizar   4 Inscribir");
            System.out.println("5 Cerrar inscripciones   6 Programar o reprogramar   7 Registrar resultado");
            System.out.println("8 Consultar torneos y eventos   9 Participantes   10 Calendarios");
            System.out.println("11 Tabla y campeón   12 Finanzas   13 Ranking de empresas   0 Salir");
            try {
                int opcion = entrada.entero("Opción: ");
                switch (opcion) {
                    case 0 -> continuar = false;
                    case 1 -> {
                        app.registrarParticipante(entrada.texto("Código: "), entrada.texto("Nombre: "), entrada.texto("Empresa: "));
                        System.out.println("Participante registrado.");
                    }
                    case 2 -> {
                        String codigo = entrada.texto("Código de torneo: ");
                        String nombre = entrada.texto("Nombre de torneo: ");
                        app.crearTorneo(codigo, nombre, seleccionarDisciplina());
                        System.out.println("Torneo creado en inscripción.");
                    }
                    case 3 -> {
                        Disciplina d = seleccionarDisciplina();
                        System.out.println("Cotización Q" + dinero(d.cotizar(entrada.entero("Cantidad de participantes: "))));
                    }
                    case 4 -> {
                        String torneo = entrada.texto("Código de torneo: ");
                        app.inscribir(torneo, entrada.texto("Código de participante: "));
                        System.out.println("Inscripción registrada. Cobro Q" + dinero(app.torneo(torneo).getDisciplina().getCuota()));
                    }
                    case 5 -> {
                        app.cerrarInscripciones(entrada.texto("Código de torneo: "));
                        System.out.println("Inscripciones cerradas. Eventos generados sin programar.");
                    }
                    case 6 -> programar();
                    case 7 -> {
                        Evento e = app.evento(entrada.texto("Código de evento: "));
                        app.registrarResultado(e.getFicha().getCodigo(), e.leerResultado(entrada));
                        System.out.println("Resultado registrado. Consulte tabla, campeón y premios.");
                    }
                    case 8 -> mostrarTorneos();
                    case 9 -> app.getParticipantes().forEach(System.out::println);
                    case 10 -> mostrarCalendario();
                    case 11 -> mostrarTabla();
                    case 12 -> mostrarFinanzas();
                    case 13 -> app.rankingEmpresas().forEach(System.out::println);
                    default -> System.out.println("Opción fuera de rango.");
                }
            } catch (IllegalArgumentException ex) {
                System.out.println("Operación rechazada. " + ex.getMessage());
            } catch (NoSuchElementException ex) {
                System.out.println("Fin de entrada. Programa terminado.");
                continuar = false;
            }
        }
    }
    private Disciplina seleccionarDisciplina() {
        List<Disciplina> ds = app.getDisciplinas();
        for (int i = 0; i < ds.size(); i++) System.out.println((i + 1) + " " + ds.get(i).getNombre() + " Q" + dinero(ds.get(i).getCuota()));
        int n = entrada.entero("Disciplina: ");
        Validacion.exigir(n >= 1 && n <= ds.size(), "Disciplina fuera de rango.");
        return ds.get(n - 1);
    }
    private void programar() {
        String evento = entrada.texto("Código de evento: ");
        LocalDate fecha = entrada.fecha("Fecha AAAA-MM-DD: ");
        int hora = entrada.entero("Hora entera de 0 a 23: ");
        app.programar(evento, fecha, hora, entrada.texto("Sede: "));
        System.out.println("Evento programado.");
    }
    private void mostrarTorneos() {
        for (Torneo t : app.getTorneos()) {
            System.out.println(t.getCodigo() + " " + t.getNombre() + " | " + t.getEstado() + " | inscritos " + t.getParticipantes().size());
            for (Evento e : app.calendarioTorneo(t.getCodigo())) mostrarEvento(e);
            for (Evento e : t.getEventos()) if (e.getFicha().getEstado() == EstadoEvento.SIN_PROGRAMAR) mostrarEvento(e);
        }
    }
    private void mostrarCalendario() {
        int tipo = entrada.entero("1 General, 2 Torneo, 3 Participante: ");
        Calendario calendario = switch (tipo) {
            case 1 -> app.calendarioGeneral();
            case 2 -> app.calendarioTorneo(entrada.texto("Código de torneo: "));
            case 3 -> app.calendarioParticipante(entrada.texto("Código de participante: "));
            default -> throw new IllegalArgumentException("Tipo de calendario inválido.");
        };
        for (Evento e : calendario) mostrarEvento(e);
        if (calendario.size() == 0) System.out.println("No hay eventos programados.");
    }
    private void mostrarEvento(Evento evento) {
        FichaEvento f = evento.getFicha();
        String fecha = f.getFecha() == null ? "sin fecha" : f.getFecha() + " " + String.format("%02d:00", f.getHora()) + " " + f.getSede();
        System.out.println(f.getCodigo() + " | " + fecha + " | " + f.getEstado());
        System.out.println("  " + f.getParticipantes());
        if (f.getResultado() != null) {
            System.out.println("  Marcadores o tiempos " + f.getResultado().getValores() + " | penales " + f.getResultado().getPenales() + " | descalificados " + f.getResultado().getDescalificados());
        }
    }
    private void mostrarTabla() {
        Torneo t = app.torneo(entrada.texto("Código de torneo: "));
        t.tabla().forEach(System.out::println);
        if (t.getEstado() == EstadoTorneo.FINALIZADO) {
            System.out.println("Campeón " + t.campeon());
            System.out.println((app.esPremioInicial(t.getCodigo()) ? "Premio teórico de demostración Q" : "Premio entregado Q") + dinero(t.premio()));
        }
    }
    private void mostrarFinanzas() {
        for (Disciplina d : app.getDisciplinas()) System.out.println(d.getNombre() + " | ingresos Q" + dinero(app.ingresosPorDisciplina().get(d.getNombre())));
        System.out.println("Ingresos totales Q" + dinero(app.ingresosTotales()));
        for (Torneo t : app.getTorneos()) {
            if (!app.esPremioInicial(t.getCodigo()) && t.getEstado() == EstadoTorneo.FINALIZADO) System.out.println(t.getCodigo() + " | campeón " + t.campeon().getNombre() + " | premio Q" + dinero(t.premio()));
        }
        System.out.println("Premios entregados Q" + dinero(app.premiosEntregados()));
    }
    private static String dinero(BigDecimal valor) { return valor.setScale(2).toPlainString(); }
}

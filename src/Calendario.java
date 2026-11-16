import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
/** Conserva referencias de eventos y renueva el orden en cada recorrido. */
public final class Calendario implements Iterable<Evento> {
    private final List<Evento> eventos;
    public Calendario(Collection<Evento> origen) { eventos = List.copyOf(origen); }
    @Override public Iterator<Evento> iterator() {
        List<Evento> programados = new ArrayList<>();
        for (Evento e : eventos) if (e.getFicha().getEstado() != EstadoEvento.SIN_PROGRAMAR) programados.add(e);
        programados.sort(null);
        return List.copyOf(programados).iterator();
    }
    public int size() { return (int) eventos.stream().filter(e -> e.getFicha().getEstado() != EstadoEvento.SIN_PROGRAMAR).count(); }
}

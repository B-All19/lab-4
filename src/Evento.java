import java.util.Map;
import java.util.Comparator;
/** Contrato compartido sin imponer una clase padre a encuentros y carreras. */
public interface Evento extends Comparable<Evento> {
    FichaEvento getFicha();
    Map<String, Aporte> evaluar(DatosResultado datos);
    DatosResultado leerResultado(Entrada entrada);
    @Override default int compareTo(Evento otro) {
        FichaEvento a = getFicha();
        FichaEvento b = otro.getFicha();
        int c = Comparator.nullsLast(java.time.LocalDate::compareTo).compare(a.getFecha(), b.getFecha());
        if (c == 0) c = Integer.compare(a.getHora(), b.getHora());
        if (c == 0) c = Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER).compare(a.getSede(), b.getSede());
        if (c == 0) c = a.getCodigo().compareTo(b.getCodigo());
        return c;
    }
}

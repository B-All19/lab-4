/** Fila calculada sin almacenar puntajes en el participante global. */
public final class FilaTabla implements Comparable<FilaTabla> {
    private final Participante participante;
    private final int puntos;
    private final long diferencia;
    private final int victorias;
    public FilaTabla(Participante participante, int puntos, long diferencia, int victorias) {
        this.participante = participante;
        this.puntos = puntos;
        this.diferencia = diferencia;
        this.victorias = victorias;
    }
    public Participante getParticipante() { return participante; }
    public int getPuntos() { return puntos; }
    public long getDiferencia() { return diferencia; }
    public int getVictorias() { return victorias; }
    @Override public int compareTo(FilaTabla otra) {
        int c = Integer.compare(otra.puntos, puntos);
        if (c == 0) c = Long.compare(otra.diferencia, diferencia);
        if (c == 0) c = Integer.compare(otra.victorias, victorias);
        if (c == 0) c = participante.getNombre().compareToIgnoreCase(otra.participante.getNombre());
        if (c == 0) c = participante.getCodigo().compareTo(otra.participante.getCodigo());
        return c;
    }
    @Override public String toString() {
        return participante.getCodigo() + " " + participante.getNombre() + " | puntos " + puntos + " | diferencia " + diferencia + " | victorias " + victorias;
    }
}

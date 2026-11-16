public final class FilaEmpresa implements Comparable<FilaEmpresa> {
    private final String empresa;
    private final long puntos;
    public FilaEmpresa(String empresa, long puntos) { this.empresa = empresa; this.puntos = puntos; }
    public String getEmpresa() { return empresa; }
    public long getPuntos() { return puntos; }
    @Override public int compareTo(FilaEmpresa otra) {
        int c = Long.compare(otra.puntos, puntos);
        return c == 0 ? empresa.compareToIgnoreCase(otra.empresa) : c;
    }
    @Override public String toString() { return empresa + " | puntos " + puntos; }
}

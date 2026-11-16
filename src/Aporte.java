/** Resultado deportivo inmutable para una participación en un evento. */
public final class Aporte {
    private final int puntos;
    private final long diferencia;
    private final int victorias;
    public Aporte(int puntos, long diferencia, int victorias) {
        Validacion.exigir(puntos >= 0 && (victorias == 0 || victorias == 1), "Aporte inválido.");
        this.puntos = puntos;
        this.diferencia = diferencia;
        this.victorias = victorias;
    }
    public int getPuntos() { return puntos; }
    public long getDiferencia() { return diferencia; }
    public int getVictorias() { return victorias; }
}

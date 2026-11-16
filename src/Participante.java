/** Identidad global e inmutable de un equipo o cuarteto. */
public final class Participante {
    private final String codigo;
    private final String nombre;
    private final String empresa;
    public Participante(String codigo, String nombre, String empresa) {
        this.codigo = Validacion.clave(codigo);
        this.nombre = Validacion.texto(nombre, "Nombre");
        this.empresa = Validacion.clave(empresa);
    }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getEmpresa() { return empresa; }
    @Override public String toString() { return codigo + " " + nombre + " / " + empresa; }
}

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;
import java.util.Scanner;
/** Lectura por líneas. Un error de formato se devuelve al menú. */
public final class Entrada {
    private final Scanner scanner;
    public Entrada(Scanner scanner) { this.scanner = scanner; }
    public String texto(String mensaje) {
        System.out.print(mensaje);
        if (!scanner.hasNextLine()) throw new NoSuchElementException("Fin de entrada.");
        return scanner.nextLine().trim();
    }
    public int entero(String mensaje) {
        try { return Integer.parseInt(texto(mensaje)); }
        catch (NumberFormatException ex) { throw new IllegalArgumentException("Ingrese un número entero dentro del rango permitido."); }
    }
    public LocalDate fecha(String mensaje) {
        try { return LocalDate.parse(texto(mensaje)); }
        catch (DateTimeParseException ex) { throw new IllegalArgumentException("Fecha inválida. Use AAAA-MM-DD."); }
    }
}

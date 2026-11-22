package modelo;

// Representa el día mínimo y máximo de llegada, no una fecha de calendario.
public class RangoFechas {
    private int fechaMinima;
    private int fechaMaxima;

    public RangoFechas(int minima, int maxima) {
        fechaMinima = minima;
        fechaMaxima = maxima;
    }
    public int getFechaMinima() { return fechaMinima; }
    public int getFechaMaxima() { return fechaMaxima; }
    @Override
    public String toString() {
        return "día " + fechaMinima + " a día " + fechaMaxima;
    }
}

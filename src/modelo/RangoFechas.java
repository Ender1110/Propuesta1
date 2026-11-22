package modelo;

import java.time.LocalDate;

/** Intervalo inclusivo de llegada, con fechas reales. */
public class RangoFechas {
    private final LocalDate fechaMinima, fechaMaxima;
    public RangoFechas(LocalDate minima, LocalDate maxima) {
        if (minima == null || maxima == null || maxima.isBefore(minima))
            throw new IllegalArgumentException("Rango de fechas inválido.");
        fechaMinima = minima; fechaMaxima = maxima;
    }
    public LocalDate getFechaMinima() { return fechaMinima; }
    public LocalDate getFechaMaxima() { return fechaMaxima; }
    @Override public String toString() { return fechaMinima + " a " + fechaMaxima; }
}

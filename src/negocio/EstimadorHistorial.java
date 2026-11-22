package negocio;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import modelo.Proveedor;
import modelo.RangoFechas;

/** Usa mínimo y máximo observados; antes de tres entregas aplica 2–7 días. */
public class EstimadorHistorial implements EstimadorEntrega {
    private static final int MINIMO_ENTREGAS = 3;
    private final Proveedor proveedor;
    public EstimadorHistorial(Proveedor proveedor) { this.proveedor = Objects.requireNonNull(proveedor); }
    @Override public RangoFechas estimarLlegada(LocalDate fechaPedido) {
        List<Integer> historial = proveedor.getHistorial();
        int minimo = 2, maximo = 7;
        if (historial.size() >= MINIMO_ENTREGAS) {
            minimo = Collections.min(historial);
            maximo = Collections.max(historial);
        }
        return new RangoFechas(fechaPedido.plusDays(minimo), fechaPedido.plusDays(maximo));
    }
}

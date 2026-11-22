package negocio;

import java.time.LocalDate;
import modelo.RangoFechas;

/** Estimación de un proveedor con plazo acordado. */
public class EstimadorFijo implements EstimadorEntrega {
    private final int diasFijos;
    public EstimadorFijo(int diasFijos) {
        if (diasFijos < 0) throw new IllegalArgumentException("El plazo no puede ser negativo.");
        this.diasFijos = diasFijos;
    }
    @Override public RangoFechas estimarLlegada(LocalDate fechaPedido) {
        LocalDate llegada = fechaPedido.plusDays(diasFijos);
        return new RangoFechas(llegada, llegada);
    }
}

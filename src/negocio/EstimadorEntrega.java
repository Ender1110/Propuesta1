package negocio;

import java.time.LocalDate;
import modelo.RangoFechas;

/** Contrato Strategy: todas las reglas devuelven un rango de fechas. */
public interface EstimadorEntrega {
    RangoFechas estimarLlegada(LocalDate fechaPedido);
}

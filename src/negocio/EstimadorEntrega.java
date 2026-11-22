package negocio;

import modelo.RangoFechas;

public interface EstimadorEntrega {
    RangoFechas estimarLlegada(int fechaPedido);
}

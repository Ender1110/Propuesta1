package negocio;

import modelo.RangoFechas;

public class EstimadorFijo implements EstimadorEntrega {
    private int diasFijos;
    public EstimadorFijo(int diasFijos) {
        this.diasFijos = diasFijos;
    }
    @Override
    public RangoFechas estimarLlegada(int fechaPedido) {
        if (fechaPedido < 0 || diasFijos < 0 || diasFijos > Integer.MAX_VALUE - fechaPedido) {
            return null;
        }
        int llegada = fechaPedido + diasFijos;
        return new RangoFechas(llegada, llegada);
    }
}

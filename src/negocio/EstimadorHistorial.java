package negocio;

import modelo.Proveedor;
import modelo.RangoFechas;

public class EstimadorHistorial implements EstimadorEntrega {
    private Proveedor proveedor;
    public EstimadorHistorial(Proveedor proveedor) {
        this.proveedor = proveedor;
    }
    @Override
    public RangoFechas estimarLlegada(int fechaPedido) {
        if (proveedor == null) { return null; }
        int[] historial = proveedor.getHistorial();
        int minimo = 2;
        int maximo = 7;
        if (historial.length >= 3) {
            minimo = historial[0];
            maximo = historial[0];
            for (int i = 1; i < historial.length; i++) {
                int dias = historial[i];
                if (dias < minimo) { minimo = dias; }
                if (dias > maximo) { maximo = dias; }
            }
        }
        if (fechaPedido < 0 || maximo > Integer.MAX_VALUE - fechaPedido) {
            return null;
        }
        return new RangoFechas(fechaPedido + minimo, fechaPedido + maximo);
    }
}

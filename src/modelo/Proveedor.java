package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Conserva los tiempos reales de entrega en días. */
public class Proveedor {
    private final String nombre;
    private final List<Integer> historialEntregas = new ArrayList<>();
    public Proveedor(String nombre) { this.nombre = Producto.texto(nombre, "proveedor"); }
    public String getNombre() { return nombre; }
    // Solo el modelo de Pedido registra nuevas recepciones.
    void registrarEntrega(int dias) {
        if (dias < 0) throw new IllegalArgumentException("Días de entrega inválidos.");
        historialEntregas.add(dias);
    }
    public List<Integer> getHistorial() {
        return Collections.unmodifiableList(new ArrayList<>(historialEntregas));
    }
    public double promedioDias() {
        return historialEntregas.stream().mapToInt(Integer::intValue).average().orElse(0);
    }
}

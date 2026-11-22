package modelo;

/** Producto sin fecha de caducidad en el alcance de este modelo. */
public class ProductoNoPerecedero extends Producto {
    public ProductoNoPerecedero(String codigo, String nombre, String categoria, String unidad,
            int stock, int minimo, int maximo, double costo, double ventas) {
        super(codigo, nombre, categoria, unidad, stock, minimo, maximo, costo, ventas);
    }
    @Override public boolean estaVigente(int dia) { return true; }
}

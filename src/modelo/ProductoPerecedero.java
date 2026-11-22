package modelo;

// Los días se cuentan desde el día 0 del caso de estudio.
public class ProductoPerecedero extends Producto {
    private int fechaCaducidad;

    public ProductoPerecedero(String codigo, String nombre, String categoria, String unidad,
            int stock, int minimo, int maximo, double costo, double ventas,
            int caducidad, int diaActual) {
        super(codigo, nombre, categoria, unidad, stock, minimo, maximo, costo, ventas);
        fechaCaducidad = caducidad;
    }
    @Override
    public String validar(int diaActual) {
        String error = super.validar(diaActual);
        if (!error.isEmpty()) { return error; }
        if (fechaCaducidad <= diaActual) {
            return "La fecha de caducidad debe ser futura.";
        }
        return "";
    }
    public int getFechaCaducidad() { return fechaCaducidad; }
    @Override
    public boolean estaVigente(int dia) {
        return dia < fechaCaducidad;
    }
}

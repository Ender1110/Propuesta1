package modelo;

import java.time.LocalDate;

/** Producto con una fecha de caducidad registrada. */
public class ProductoPerecedero extends Producto {
    private final LocalDate fechaCaducidad;

    public ProductoPerecedero(String codigo, String nombre, String categoria, String unidad,
            int stock, int minimo, int maximo, double costo, double ventas, LocalDate caducidad) {
        super(codigo, nombre, categoria, unidad, stock, minimo, maximo, costo, ventas);
        if (caducidad == null || !caducidad.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("La fecha de caducidad debe ser futura.");
        fechaCaducidad = caducidad;
    }
    public LocalDate getFechaCaducidad() { return fechaCaducidad; }
    @Override public boolean estaVigente(LocalDate fecha) {
        return fecha.isBefore(fechaCaducidad);
    }
}

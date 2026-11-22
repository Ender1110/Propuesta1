package modelo;

import java.time.LocalDate;

/** Datos y operaciones comunes a todos los productos. */
public abstract class Producto {
    private final String codigo, nombre, categoria, unidad;
    private int stock;
    private final int stockMinimo, stockMaximo;
    private final double costoUnitario, ventasDiarias;

    protected Producto(String codigo, String nombre, String categoria, String unidad,
                       int stock, int stockMinimo, int stockMaximo,
                       double costoUnitario, double ventasDiarias) {
        this.codigo = texto(codigo, "código");
        this.nombre = texto(nombre, "nombre");
        this.categoria = texto(categoria, "categoría");
        this.unidad = texto(unidad, "unidad");
        if (stock < 0 || stockMinimo < 0 || stockMaximo < stockMinimo || stockMaximo <= 0)
            throw new IllegalArgumentException("Stock o límites inválidos.");
        if (!Double.isFinite(costoUnitario) || costoUnitario <= 0)
            throw new IllegalArgumentException("El precio debe ser mayor que cero.");
        if (!Double.isFinite(ventasDiarias) || ventasDiarias < 0)
            throw new IllegalArgumentException("Ventas diarias inválidas.");
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.stockMaximo = stockMaximo;
        this.costoUnitario = costoUnitario;
        this.ventasDiarias = ventasDiarias;
    }
    static String texto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty())
            throw new IllegalArgumentException("El campo " + campo + " es obligatorio.");
        return valor.trim();
    }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getCategoria() { return categoria; }
    public String getUnidad() { return unidad; }
    public int getStock() { return stock; }
    public int getStockMinimo() { return stockMinimo; }
    public int getStockMaximo() { return stockMaximo; }
    public double getCostoUnitario() { return costoUnitario; }
    public double getVentasDiarias() { return ventasDiarias; }
    public void aumentarStock(int cantidad) {
        if (cantidad <= 0 || (long) stock + cantidad > Integer.MAX_VALUE)
            throw new IllegalArgumentException("Cantidad de entrada inválida.");
        stock += cantidad;
    }
    public void reducirStock(int cantidad) {
        if (cantidad <= 0 || cantidad > stock)
            throw new IllegalArgumentException("Cantidad inválida o stock insuficiente.");
        stock -= cantidad;
    }
    public boolean necesitaReposicion() { return stock <= stockMinimo; }
    public double diasCobertura() {
        return ventasDiarias == 0 ? Double.POSITIVE_INFINITY : stock / ventasDiarias;
    }
    public boolean estaVigente() { return estaVigente(LocalDate.now()); }
    public abstract boolean estaVigente(LocalDate fecha);
}

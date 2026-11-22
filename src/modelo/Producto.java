package modelo;

/** Datos y operaciones comunes a todos los productos. */
public abstract class Producto {
    private final String codigo, nombre, categoria, unidad;
    private int stock;
    private final int stockMinimo, stockMaximo;
    private final double costoUnitario, ventasDiarias;

    protected Producto(String codigo, String nombre, String categoria, String unidad,
                       int stock, int stockMinimo, int stockMaximo,
                       double costoUnitario, double ventasDiarias) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.categoria = categoria;
        this.unidad = unidad;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.stockMaximo = stockMaximo;
        this.costoUnitario = costoUnitario;
        this.ventasDiarias = ventasDiarias;
    }
    public String validar(int diaActual) {
        if (codigo == null || codigo.trim().isEmpty() || nombre == null || nombre.trim().isEmpty()
                || categoria == null || categoria.trim().isEmpty() || unidad == null || unidad.trim().isEmpty()) {
            return "Campos obligatorios incompletos.";
        }
        if (stock < 0 || stockMinimo < 0 || stockMaximo < stockMinimo || stockMaximo <= 0) {
            return "Stock o límites inválidos.";
        }
        if (!(costoUnitario > 0) || costoUnitario > 1000000000) {
            return "El precio debe ser mayor que cero.";
        }
        if (!(ventasDiarias >= 0) || ventasDiarias > 1000000000) {
            return "Ventas diarias inválidas.";
        }
        return "";
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
    public String aumentarStock(int cantidad) {
        if (cantidad <= 0 || (long) stock + cantidad > Integer.MAX_VALUE)
            return "Cantidad de entrada inválida.";
        stock += cantidad;
        return "";
    }
    public String reducirStock(int cantidad) {
        if (cantidad <= 0 || cantidad > stock)
            return "Cantidad inválida o stock insuficiente.";
        stock -= cantidad;
        return "";
    }
    public boolean necesitaReposicion() { return stock <= stockMinimo; }
    public double diasCobertura() {
        if (ventasDiarias == 0) {
            return -1; // -1 indica ausencia de consumo.
        }
        return stock / ventasDiarias;
    }
    public abstract boolean estaVigente(int dia);
}

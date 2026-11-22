package modelo;

import negocio.EstimadorEntrega;

public class Pedido {
    private String codigo;
    private Producto producto;
    private Proveedor proveedor;
    private int cantidad;
    private int fechaPedido;
    private int fechaRealLlegada = -1; // -1 indica que todavía no llega.
    private String estado = EstadoPedido.SOLICITADO;
    private EstimadorEntrega estimador;
    private RangoFechas rango;

    public Pedido(String codigo, Producto producto, Proveedor proveedor, int cantidad,
                  int fechaPedido, EstimadorEntrega estimador, int diaActual) {
        this.codigo = codigo;
        this.producto = producto;
        this.proveedor = proveedor;
        this.cantidad = cantidad;
        this.fechaPedido = fechaPedido;
        cambiarEstimador(estimador);
    }
    public String validar(int diaActual) {
        if (codigo == null || codigo.trim().isEmpty()) { return "Código obligatorio."; }
        if (producto == null || proveedor == null) { return "Producto o proveedor inválido."; }
        if (cantidad <= 0) { return "La cantidad debe ser mayor que cero."; }
        if (fechaPedido < 0 || fechaPedido > diaActual) { return "Fecha de pedido inválida."; }
        if (rango == null) { return "Plazo o día inválido."; }
        return "";
    }
    public String getCodigo() { return codigo; }
    public Producto getProducto() { return producto; }
    public Proveedor getProveedor() { return proveedor; }
    public int getCantidad() { return cantidad; }
    public int getFechaPedido() { return fechaPedido; }
    public int getFechaRealLlegada() { return fechaRealLlegada; }
    public double getCostoTotal() { return cantidad * producto.getCostoUnitario(); }
    public RangoFechas estimarLlegada() { return rango; }
    public String cambiarEstimador(EstimadorEntrega nuevo) {
        if (estado.equals(EstadoPedido.RECIBIDO)) {
            return "Pedido ya recibido.";
        }
        if (nuevo == null) {
            return "Estimador inválido.";
        }
        RangoFechas nuevaLlegada = nuevo.estimarLlegada(fechaPedido);
        if (nuevaLlegada == null) { return "Plazo o día inválido."; }
        estimador = nuevo;
        rango = nuevaLlegada;
        return "";
    }
    public String getEstado() { return estado; }
    public String estadoEn(int dia) {
        if (!estado.equals(EstadoPedido.RECIBIDO) && dia > rango.getFechaMaxima()) {
            return EstadoPedido.RETRASADO;
        }
        return estado;
    }
    public String marcarEnTransito() {
        if (estado.equals(EstadoPedido.RECIBIDO)) {
            return "Pedido ya recibido.";
        }
        estado = EstadoPedido.EN_TRANSITO;
        return "";
    }
    public String registrarRecepcion(int fecha, int diaActual) {
        if (estado.equals(EstadoPedido.RECIBIDO)) {
            return "Pedido ya recibido.";
        }
        if (fecha < fechaPedido || fecha > diaActual) {
            return "Fecha de recepción inválida.";
        }
        if (proveedor.historialLleno()) {
            return "Historial del proveedor lleno.";
        }
        int dias = fecha - fechaPedido;
        String error = producto.aumentarStock(cantidad);
        if (!error.isEmpty()) { return error; }
        proveedor.registrarEntrega(dias);
        fechaRealLlegada = fecha;
        estado = EstadoPedido.RECIBIDO;
        return "";
    }
}

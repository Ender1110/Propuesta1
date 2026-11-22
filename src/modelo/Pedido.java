package modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import negocio.EstimadorEntrega;

/** Delega la estimación y registra cada recepción una sola vez. */
public class Pedido {
    private final String codigo;
    private final Producto producto;
    private final Proveedor proveedor;
    private final int cantidad;
    private final LocalDate fechaPedido;
    private LocalDate fechaRealLlegada;
    private EstadoPedido estado = EstadoPedido.SOLICITADO;
    private EstimadorEntrega estimador;
    private RangoFechas rango;

    public Pedido(String codigo, Producto producto, Proveedor proveedor, int cantidad,
                  LocalDate fechaPedido, EstimadorEntrega estimador) {
        this.codigo = Producto.texto(codigo, "código del pedido");
        this.producto = Objects.requireNonNull(producto);
        this.proveedor = Objects.requireNonNull(proveedor);
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        if (fechaPedido == null || fechaPedido.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Fecha de pedido inválida.");
        this.cantidad = cantidad;
        this.fechaPedido = fechaPedido;
        cambiarEstimador(estimador);
    }
    public String getCodigo() { return codigo; }
    public Producto getProducto() { return producto; }
    public Proveedor getProveedor() { return proveedor; }
    public int getCantidad() { return cantidad; }
    public LocalDate getFechaPedido() { return fechaPedido; }
    public LocalDate getFechaRealLlegada() { return fechaRealLlegada; }
    public double getCostoTotal() { return cantidad * producto.getCostoUnitario(); }
    public RangoFechas estimarLlegada() { return rango; }
    public void cambiarEstimador(EstimadorEntrega nuevo) {
        if (estado == EstadoPedido.RECIBIDO) throw new IllegalArgumentException("Pedido ya recibido.");
        Objects.requireNonNull(nuevo);
        RangoFechas nuevaLlegada = nuevo.estimarLlegada(fechaPedido);
        estimador = nuevo;
        rango = nuevaLlegada;
    }
    public EstadoPedido getEstado() { return estadoEn(LocalDate.now()); }
    public EstadoPedido estadoEn(LocalDate fecha) {
        if (estado != EstadoPedido.RECIBIDO && fecha.isAfter(rango.getFechaMaxima()))
            return EstadoPedido.RETRASADO;
        return estado;
    }
    public void marcarEnTransito() {
        if (estado == EstadoPedido.RECIBIDO) throw new IllegalArgumentException("Pedido ya recibido.");
        estado = EstadoPedido.EN_TRANSITO;
    }
    public void registrarRecepcion(LocalDate fecha) {
        if (estado == EstadoPedido.RECIBIDO) throw new IllegalArgumentException("Pedido ya recibido.");
        if (fecha == null || fecha.isBefore(fechaPedido) || fecha.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Fecha de recepción inválida.");
        int dias = Math.toIntExact(ChronoUnit.DAYS.between(fechaPedido, fecha));
        producto.aumentarStock(cantidad);
        proveedor.registrarEntrega(dias);
        fechaRealLlegada = fecha;
        estado = EstadoPedido.RECIBIDO;
    }
}

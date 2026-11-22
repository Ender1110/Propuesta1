package negocio;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import modelo.*;

/** Administra registros en memoria, ventas, pedidos y alertas. */
public class GestorInventario {
    private final List<Producto> productos = new ArrayList<>();
    private final List<Proveedor> proveedores = new ArrayList<>();
    private final List<Pedido> pedidos = new ArrayList<>();
    public void registrarProducto(Producto p) {
        if (productos.stream().anyMatch(x -> x.getCodigo().equalsIgnoreCase(p.getCodigo())))
            throw new IllegalArgumentException("Código de producto duplicado.");
        productos.add(p);
    }
    public void registrarProveedor(Proveedor p) {
        if (proveedores.stream().anyMatch(x -> x.getNombre().equalsIgnoreCase(p.getNombre())))
            throw new IllegalArgumentException("Proveedor duplicado.");
        proveedores.add(p);
    }
    public Producto buscarProducto(String codigo) {
        return productos.stream().filter(p -> p.getCodigo().equalsIgnoreCase(codigo)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado."));
    }
    public Proveedor buscarProveedor(String nombre) {
        return proveedores.stream().filter(p -> p.getNombre().equalsIgnoreCase(nombre)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado."));
    }
    public Pedido buscarPedido(String codigo) {
        return pedidos.stream().filter(p -> p.getCodigo().equalsIgnoreCase(codigo)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado."));
    }
    public void registrarPedido(Pedido p) {
        if (!productos.contains(p.getProducto()) || !proveedores.contains(p.getProveedor()))
            throw new IllegalArgumentException("Registre primero el producto y el proveedor.");
        if (pedidos.stream().anyMatch(x -> x.getCodigo().equalsIgnoreCase(p.getCodigo())))
            throw new IllegalArgumentException("Código de pedido duplicado.");
        pedidos.add(p);
    }
    public void registrarVenta(String codigo, int cantidad) {
        Producto p = buscarProducto(codigo);
        if (!p.estaVigente()) throw new IllegalArgumentException("Producto caducado.");
        p.reducirStock(cantidad);
    }
    public void registrarRecepcion(String codigo, LocalDate fecha) { buscarPedido(codigo).registrarRecepcion(fecha); }
    public List<Producto> getProductos() { return Collections.unmodifiableList(productos); }
    public List<Proveedor> getProveedores() { return Collections.unmodifiableList(proveedores); }
    public List<Pedido> getPedidos() { return Collections.unmodifiableList(pedidos); }
    public long stockProyectado(Producto p) {
        long total = p.getStock();
        for (Pedido pedido : pedidos)
            if (pedido.getProducto() == p && pedido.getEstado() != EstadoPedido.RECIBIDO)
                total += pedido.getCantidad();
        return total;
    }
    public boolean riesgoQuiebre(Pedido pedido, LocalDate hoy) {
        if (pedido.getEstado() == EstadoPedido.RECIBIDO) return false;
        long dias = Math.max(0, ChronoUnit.DAYS.between(hoy, pedido.estimarLlegada().getFechaMaxima()));
        return pedido.getProducto().getStock() == 0 || pedido.getProducto().diasCobertura() < dias;
    }
    public List<String> alertas(LocalDate hoy) {
        List<String> resultado = new ArrayList<>();
        for (Producto p : productos) {
            String nombre = p.getCodigo() + " " + p.getNombre();
            if (p.necesitaReposicion()) resultado.add(nombre + ": stock mínimo alcanzado.");
            if (stockProyectado(p) > p.getStockMaximo()) resultado.add(nombre + ": riesgo de sobrestock (" + stockProyectado(p) + " > " + p.getStockMaximo() + ").");
            if (p.getStock() > 0 && !p.estaVigente(hoy)) resultado.add(nombre + ": producto caducado.");
            else if (p instanceof ProductoPerecedero && p.getStock() > 0 && !p.estaVigente(hoy.plusDays(7)))
                resultado.add(nombre + ": caducidad en 7 días o menos.");
            for (Pedido pedido : pedidos) {
                if (pedido.getProducto() != p || pedido.getEstado() == EstadoPedido.RECIBIDO) continue;
                if (riesgoQuiebre(pedido, hoy)) resultado.add(nombre + ": riesgo de quiebre antes de llegada de " + pedido.getCodigo() + ".");
                if (pedido.estadoEn(hoy) == EstadoPedido.RETRASADO) resultado.add(pedido.getCodigo() + ": pedido retrasado.");
            }
        }
        return resultado;
    }
    public List<Producto> productosEnRiesgo() {
        List<Producto> riesgo = new ArrayList<>();
        for (Producto p : productos) {
            if (p.necesitaReposicion() || !p.estaVigente() || stockProyectado(p) > p.getStockMaximo() ||
                pedidos.stream().anyMatch(q -> q.getProducto() == p && riesgoQuiebre(q, LocalDate.now()))) riesgo.add(p);
        }
        return riesgo;
    }
}

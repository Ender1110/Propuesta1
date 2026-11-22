package negocio;

import modelo.*;

public class GestorInventario {
    private Producto[] productos = new Producto[1000];
    private Proveedor[] proveedores = new Proveedor[100];
    private Pedido[] pedidos = new Pedido[1000];
    private int cantidadProductos = 0;
    private int cantidadProveedores = 0;
    private int cantidadPedidos = 0;
    private int diaActual = 0;

    public int getDiaActual() { return diaActual; }
    public String cambiarDia(int dia) {
        if (dia <= diaActual || dia > 1000000) {
            return "Ingrese un día posterior al actual (máximo 1000000).";
        }
        diaActual = dia;
        return "";
    }
    public String registrarProducto(Producto p) {
        String error = p.validar(diaActual);
        if (!error.isEmpty()) { return error; }
        for (int i = 0; i < cantidadProductos; i++) {
            Producto x = productos[i];
            if (x.getCodigo().equalsIgnoreCase(p.getCodigo())) {
                return "Código de producto duplicado.";
            }
        }
        if (cantidadProductos == productos.length) {
            return "Capacidad máxima de productos alcanzada.";
        }
        productos[cantidadProductos] = p;
        cantidadProductos++;
        return "";
    }
    public String registrarProveedor(Proveedor p) {
        if (p.getNombre() == null || p.getNombre().trim().isEmpty()) { return "El campo nombre del proveedor es obligatorio."; }
        for (int i = 0; i < cantidadProveedores; i++) {
            Proveedor x = proveedores[i];
            if (x.getNombre().equalsIgnoreCase(p.getNombre())) {
                return "Proveedor duplicado.";
            }
        }
        if (cantidadProveedores == proveedores.length) {
            return "Capacidad máxima de proveedores alcanzada.";
        }
        proveedores[cantidadProveedores] = p;
        cantidadProveedores++;
        return "";
    }
    public Producto buscarProducto(String codigo) {
        for (int i = 0; i < cantidadProductos; i++) {
            Producto p = productos[i];
            if (p.getCodigo().equalsIgnoreCase(codigo)) { return p; }
        }
        return null;
    }
    public Proveedor buscarProveedor(String nombre) {
        for (int i = 0; i < cantidadProveedores; i++) {
            Proveedor p = proveedores[i];
            if (p.getNombre().equalsIgnoreCase(nombre)) { return p; }
        }
        return null;
    }
    public Pedido buscarPedido(String codigo) {
        for (int j = 0; j < cantidadPedidos; j++) {
            Pedido p = pedidos[j];
            if (p.getCodigo().equalsIgnoreCase(codigo)) { return p; }
        }
        return null;
    }
    public String registrarPedido(Pedido p) {
        String error = p.validar(diaActual);
        if (!error.isEmpty()) { return error; }
        if (buscarProducto(p.getProducto().getCodigo()) != p.getProducto() || buscarProveedor(p.getProveedor().getNombre()) != p.getProveedor()) {
            return "Registre primero el producto y el proveedor.";
        }
        for (int j = 0; j < cantidadPedidos; j++) {
            Pedido x = pedidos[j];
            if (x.getCodigo().equalsIgnoreCase(p.getCodigo())) {
                return "Código de pedido duplicado.";
            }
        }
        if (cantidadPedidos == pedidos.length) {
            return "Capacidad máxima de pedidos alcanzada.";
        }
        pedidos[cantidadPedidos] = p;
        cantidadPedidos++;
        return "";
    }
    public String registrarVenta(String codigo, int cantidad) {
        Producto p = buscarProducto(codigo);
        if (p == null) { return "Producto no encontrado."; }
        if (!p.estaVigente(diaActual)) {
            return "Producto caducado.";
        }
        return p.reducirStock(cantidad);
    }
    public String registrarRecepcion(String codigo, int fecha) {
        Pedido p = buscarPedido(codigo);
        if (p == null) { return "Pedido no encontrado."; }
        return p.registrarRecepcion(fecha, diaActual);
    }
    public Producto[] getProductos() {
        Producto[] copia = new Producto[cantidadProductos];
        for (int i = 0; i < cantidadProductos; i++) {
            copia[i] = productos[i];
        }
        return copia;
    }
    public Proveedor[] getProveedores() {
        Proveedor[] copia = new Proveedor[cantidadProveedores];
        for (int i = 0; i < cantidadProveedores; i++) {
            copia[i] = proveedores[i];
        }
        return copia;
    }
    public Pedido[] getPedidos() {
        Pedido[] copia = new Pedido[cantidadPedidos];
        for (int i = 0; i < cantidadPedidos; i++) {
            copia[i] = pedidos[i];
        }
        return copia;
    }
    public long stockProyectado(Producto p) {
        long total = p.getStock();
        for (int j = 0; j < cantidadPedidos; j++) {
            Pedido pedido = pedidos[j];
            if (pedido.getProducto() == p && !pedido.getEstado().equals(EstadoPedido.RECIBIDO)) {
                total = total + pedido.getCantidad();
            }
        }
        return total;
    }
    public boolean riesgoQuiebre(Pedido pedido, int dia) {
        if (pedido.getEstado().equals(EstadoPedido.RECIBIDO)) { return false; }
        int dias = pedido.estimarLlegada().getFechaMaxima() - dia;
        if (dias < 0) { dias = 0; }
        return pedido.getProducto().getStock() == 0 || (pedido.getProducto().getVentasDiarias() > 0 && pedido.getProducto().diasCobertura() < dias);
    }
    public String[] alertas(int dia) {
        String[] resultado = new String[3 * cantidadProductos + 2 * cantidadPedidos];
        int cantidad = 0;
        for (int i = 0; i < cantidadProductos; i++) {
            Producto p = productos[i];
            String nombre = p.getCodigo() + " " + p.getNombre();
            if (p.necesitaReposicion()) { resultado[cantidad] = nombre + ": stock mínimo alcanzado."; cantidad++; }
            if (stockProyectado(p) > p.getStockMaximo()) {
                resultado[cantidad] = nombre + ": riesgo de sobrestock (" + stockProyectado(p) + " > " + p.getStockMaximo() + ")."; cantidad++;
            }
            if (p.getStock() > 0) {
                if (!p.estaVigente(dia)) {
                    resultado[cantidad] = nombre + ": producto caducado."; cantidad++;
                } else if (!p.estaVigente(dia + 7)) {
                    resultado[cantidad] = nombre + ": caducidad en 7 días o menos."; cantidad++;
                }
            }
            for (int j = 0; j < cantidadPedidos; j++) {
            Pedido pedido = pedidos[j];
                if (pedido.getProducto() == p && !pedido.getEstado().equals(EstadoPedido.RECIBIDO)) {
                    if (riesgoQuiebre(pedido, dia)) {
                        resultado[cantidad] = nombre + ": riesgo de quiebre antes de llegada de " + pedido.getCodigo() + "."; cantidad++;
                    }
                    if (pedido.estadoEn(dia).equals(EstadoPedido.RETRASADO)) {
                        resultado[cantidad] = pedido.getCodigo() + ": pedido retrasado."; cantidad++;
                    }
                }
            }
        }
        String[] copia = new String[cantidad];
        for (int i = 0; i < cantidad; i++) { copia[i] = resultado[i]; }
        return copia;
    }
    public Producto[] productosEnRiesgo() {
        Producto[] resultado = new Producto[cantidadProductos];
        int cantidad = 0;
        for (int i = 0; i < cantidadProductos; i++) {
            Producto p = productos[i];
            boolean riesgo = p.necesitaReposicion() || !p.estaVigente(diaActual)
                || stockProyectado(p) > p.getStockMaximo();
            for (int j = 0; j < cantidadPedidos; j++) {
            Pedido pedido = pedidos[j];
                if (pedido.getProducto() == p && riesgoQuiebre(pedido, diaActual)) {
                    riesgo = true;
                }
            }
            if (riesgo) { resultado[cantidad] = p; cantidad++; }
        }
        Producto[] copia = new Producto[cantidad];
        for (int i = 0; i < cantidad; i++) { copia[i] = resultado[i]; }
        return copia;
    }
}

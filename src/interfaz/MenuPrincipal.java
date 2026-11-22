package interfaz;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Scanner;
import modelo.*;
import negocio.*;

/** Interfaz de consola: no requiere bibliotecas externas. */
public class MenuPrincipal {
    private final Scanner entrada = new Scanner(System.in);
    private final GestorInventario gestor = new GestorInventario();

    public static void main(String[] args) { new MenuPrincipal().mostrarMenu(); }

    public void mostrarMenu() {
        System.out.println("=== COMERCIAL DON MARIO | PROPUESTA 1 ===");
        while (true) {
            System.out.println("\n1. Registrar producto    2. Registrar proveedor\n" +
                    "3. Crear pedido          4. Consultar inventario y pedidos\n" +
                    "5. Recibir pedido        6. Registrar venta\n" +
                    "7. Consultar alertas     8. Cambiar estimador\n" +
                    "9. Marcar en tránsito    0. Salir");
            if (!entrada.hasNextLine()) return;
            try {
                int opcion = entero("Opción");
                switch (opcion) {
                    case 1: registrarProducto(); break;
                    case 2: registrarProveedor(); break;
                    case 3: crearPedido(); break;
                    case 4: consultar(); break;
                    case 5: recibir(); break;
                    case 6: vender(); break;
                    case 7: alertas(); break;
                    case 8: cambiarEstimador(); break;
                    case 9: gestor.buscarPedido(texto("Código del pedido")).marcarEnTransito();
                        System.out.println("Estado actualizado."); break;
                    case 0: System.out.println("Fin del programa."); return;
                    default: System.out.println("Opción no válida.");
                }
            } catch (IllegalArgumentException | DateTimeParseException e) {
                System.out.println("ERROR: " + e.getMessage());
            } catch (java.util.NoSuchElementException e) { return; }
        }
    }
    private String leer(String campo) {
        System.out.print(campo + ": ");
        return entrada.nextLine().trim();
    }
    private String texto(String campo) {
        String s = leer(campo);
        if (s.isEmpty()) throw new IllegalArgumentException("El campo " + campo.toLowerCase(Locale.ROOT) + " es obligatorio.");
        return s;
    }
    private int entero(String campo) {
        try { return Integer.parseInt(texto(campo)); }
        catch (NumberFormatException e) { throw new IllegalArgumentException(campo + " debe ser un número entero."); }
    }
    private double decimal(String campo) {
        try { return Double.parseDouble(texto(campo).replace(',', '.')); }
        catch (NumberFormatException e) { throw new IllegalArgumentException(campo + " debe ser numérico."); }
    }
    private LocalDate fecha(String campo) {
        try { return LocalDate.parse(texto(campo + " (AAAA-MM-DD)")); }
        catch (DateTimeParseException e) { throw new IllegalArgumentException("Fecha inválida. Use AAAA-MM-DD."); }
    }
    private void registrarProducto() {
        String codigo = texto("Código"), nombre = texto("Nombre");
        int stock = entero("Cantidad"), minimo = entero("Stock mínimo"), maximo = entero("Stock máximo");
        double costo = decimal("Precio unitario USD"), ventas = decimal("Ventas diarias promedio");
        String categoria = texto("Categoría"), unidad = texto("Unidad");
        int tipo = entero("Tipo (1 perecedero, 2 no perecedero)");
        Producto p;
        if (tipo == 1) p = new ProductoPerecedero(codigo, nombre, categoria, unidad,
                stock, minimo, maximo, costo, ventas, fecha("Caducidad"));
        else if (tipo == 2) p = new ProductoNoPerecedero(codigo, nombre, categoria, unidad,
                stock, minimo, maximo, costo, ventas);
        else throw new IllegalArgumentException("Tipo de producto inválido.");
        gestor.registrarProducto(p);
        System.out.println("Producto registrado: " + p.getNombre());
    }
    private void registrarProveedor() {
        Proveedor p = new Proveedor(texto("Nombre del proveedor"));
        gestor.registrarProveedor(p);
        System.out.println("Proveedor registrado: " + p.getNombre());
    }
    private EstimadorEntrega elegirEstimador(Proveedor proveedor) {
        int tipo = entero("Estimador (1 fijo, 2 historial)");
        if (tipo == 1) return new EstimadorFijo(entero("Días acordados"));
        if (tipo == 2) return new EstimadorHistorial(proveedor);
        throw new IllegalArgumentException("Estimador inválido.");
    }
    private void crearPedido() {
        String codigo = texto("Código del pedido");
        Producto p = gestor.buscarProducto(texto("Código del producto"));
        Proveedor proveedor = gestor.buscarProveedor(texto("Nombre del proveedor"));
        int cantidad = entero("Cantidad");
        LocalDate f = fecha("Fecha del pedido");
        Pedido pedido = new Pedido(codigo, p, proveedor, cantidad, f, elegirEstimador(proveedor));
        gestor.registrarPedido(pedido);
        System.out.println("Pedido registrado: " + codigo + " | Llegada: " + pedido.estimarLlegada());
        System.out.printf(Locale.US, "Costo total: USD %.2f%n", pedido.getCostoTotal());
        if (gestor.stockProyectado(p) > p.getStockMaximo()) System.out.println("ALERTA: riesgo de sobrestock.");
        if (gestor.riesgoQuiebre(pedido, LocalDate.now())) System.out.println("ALERTA: riesgo de quiebre.");
    }
    private void consultar() {
        System.out.println("--- INVENTARIO ---");
        for (Producto p : gestor.getProductos()) {
            System.out.printf(Locale.US, "%s | %s | stock=%d | mínimo=%d | máximo=%d | costo=USD %.2f | cobertura=%.2f días | vigente=%s%n",
                    p.getCodigo(), p.getNombre(), p.getStock(), p.getStockMinimo(), p.getStockMaximo(),
                    p.getCostoUnitario(), p.diasCobertura(), p.estaVigente() ? "sí" : "no");
        }
        System.out.println("--- PEDIDOS ---");
        for (Pedido p : gestor.getPedidos()) System.out.println(p.getCodigo() + " | " + p.getProducto().getNombre()
                + " | " + p.getProveedor().getNombre() + " | cantidad=" + p.getCantidad()
                + " | " + p.getEstado() + " | estimado=" + p.estimarLlegada()
                + (p.getFechaRealLlegada() == null ? "" : " | recibido=" + p.getFechaRealLlegada()));
        System.out.println("--- PROVEEDORES ---");
        for (Proveedor p : gestor.getProveedores()) System.out.println(p.getNombre() + " | historial (días): " + p.getHistorial());
    }
    private void recibir() {
        String codigo = texto("Código del pedido");
        String dato = leer("Fecha real AAAA-MM-DD (Enter = hoy)");
        LocalDate f;
        try { f = dato.isEmpty() ? LocalDate.now() : LocalDate.parse(dato); }
        catch (DateTimeParseException e) { throw new IllegalArgumentException("Fecha inválida. Use AAAA-MM-DD."); }
        gestor.registrarRecepcion(codigo, f);
        Pedido p = gestor.buscarPedido(codigo);
        System.out.println("Recepción registrada. Stock actual: " + p.getProducto().getStock());
        System.out.println("Historial (días): " + p.getProveedor().getHistorial());
    }
    private void vender() {
        String codigo = texto("Código del producto");
        gestor.registrarVenta(codigo, entero("Cantidad a vender"));
        System.out.println("Venta registrada. Stock actual: " + gestor.buscarProducto(codigo).getStock());
    }
    private void alertas() {
        java.util.List<String> a = gestor.alertas(LocalDate.now());
        if (a.isEmpty()) System.out.println("Sin alertas.");
        else for (String s : a) System.out.println("ALERTA: " + s);
    }
    private void cambiarEstimador() {
        Pedido p = gestor.buscarPedido(texto("Código del pedido"));
        p.cambiarEstimador(elegirEstimador(p.getProveedor()));
        System.out.println("Estimador actualizado. Llegada: " + p.estimarLlegada());
    }
}

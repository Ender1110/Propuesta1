package interfaz;

import modelo.*;
import negocio.*;
import java.util.Scanner;

/** Interfaz de consola: no requiere bibliotecas externas. */
public class MenuPrincipal {
    // Lee los datos que el usuario escribe en la consola del IDE.
    private Scanner teclado = new Scanner(System.in);
    private String errorEntrada = "";
    private boolean finEntrada = false;
    private final GestorInventario gestor = new GestorInventario();

    public static void main(String[] args) {
        MenuPrincipal menu = new MenuPrincipal();
        menu.mostrarMenu();
    }

    public void mostrarMenu() {
        System.out.println("=== COMERCIAL DON MARIO | PROPUESTA 1 ===");
        while (true) {
            System.out.println("\nDía actual: " + gestor.getDiaActual());
            System.out.println("1. Registrar producto    2. Registrar proveedor\n" +
                    "3. Crear pedido          4. Consultar inventario y pedidos\n" +
                    "5. Recibir pedido        6. Registrar venta\n" +
                    "7. Consultar alertas     8. Cambiar estimador\n" +
                    "9. Marcar en tránsito    0. Salir\n" +
                    "10. Avanzar día");
            errorEntrada = "";
            int opcion = entero("Opción");
            if (finEntrada) { return; }
            if (errorEntrada.isEmpty()) {
                if (opcion == 1) { registrarProducto(); }
                else if (opcion == 2) { registrarProveedor(); }
                else if (opcion == 3) { crearPedido(); }
                else if (opcion == 4) { consultar(); }
                else if (opcion == 5) { recibir(); }
                else if (opcion == 6) { vender(); }
                else if (opcion == 7) { alertas(); }
                else if (opcion == 8) { cambiarEstimador(); }
                else if (opcion == 9) { marcarTransito(); }
                else if (opcion == 10) { avanzarDia(); }
                else if (opcion == 0) { System.out.println("Fin del programa."); return; }
                else { System.out.println("Opción no válida."); }
            }
            if (finEntrada) { return; }
            if (!errorEntrada.isEmpty()) { System.out.println("ERROR: " + errorEntrada); }
        }
    }
    private boolean correcto(String error) {
        if (!error.isEmpty()) { errorEntrada = error; return false; }
        return true;
    }
    private String leer(String campo) {
        if (!errorEntrada.isEmpty() || finEntrada) { return ""; }
        System.out.print(campo + ": ");
        if (!teclado.hasNextLine()) {
            finEntrada = true;
            return "";
        }
        return teclado.nextLine().trim();
    }
    private String texto(String campo) {
        String dato = leer(campo);
        if (errorEntrada.isEmpty() && !finEntrada && dato.isEmpty()) {
            errorEntrada = "El campo " + campo.toLowerCase() + " es obligatorio.";
        }
        return dato;
    }
    // Conversión básica: se recorren los caracteres del texto, sin excepciones.
    private double numero(String campo, boolean permiteDecimal) {
        String dato = texto(campo);
        if (!errorEntrada.isEmpty() || finEntrada) { return 0; }
        double valor = 0;
        double divisor = 1;
        int inicio = 0;
        int signo = 1;
        boolean punto = false;
        int digitos = 0;
        if (dato.charAt(0) == '-') { signo = -1; inicio = 1; }
        for (int i = inicio; i < dato.length(); i++) {
            char c = dato.charAt(i);
            if (c >= '0' && c <= '9') {
                digitos++;
                if (punto) { divisor = divisor * 10; valor = valor + (c - '0') / divisor; }
                else { valor = valor * 10 + c - '0'; }
            } else if (permiteDecimal && !punto && (c == '.' || c == ',')) {
                punto = true;
            } else { errorEntrada = campo + " debe ser numérico."; return 0; }
            if (valor > 1000000000) { errorEntrada = campo + " fuera de rango."; return 0; }
        }
        if (digitos == 0) { errorEntrada = campo + " debe ser numérico."; return 0; }
        return signo * valor;
    }
    private int entero(String campo) { return (int) numero(campo, false); }
    private double decimal(String campo) { return numero(campo, true); }
    private boolean entradaValida() { return errorEntrada.isEmpty() && !finEntrada; }
    private void marcarTransito() {
        String codigo = texto("Código del pedido");
        if (!entradaValida()) { return; }
        Pedido p = gestor.buscarPedido(codigo);
        if (p == null) { errorEntrada = "Pedido no encontrado."; return; }
        if (correcto(p.marcarEnTransito())) { System.out.println("Estado actualizado."); }
    }
    private void avanzarDia() {
        int dia = entero("Nuevo día actual");
        if (entradaValida() && correcto(gestor.cambiarDia(dia))) {
            System.out.println("Día actualizado: " + gestor.getDiaActual());
        }
    }
    private void registrarProducto() {
        String codigo = texto("Código"), nombre = texto("Nombre");
        int stock = entero("Cantidad"), minimo = entero("Stock mínimo"), maximo = entero("Stock máximo");
        double costo = decimal("Precio unitario USD"), ventas = decimal("Ventas diarias promedio");
        String categoria = texto("Categoría"), unidad = texto("Unidad");
        int tipo = entero("Tipo (1 perecedero, 2 no perecedero)");
        if (!entradaValida()) { return; }
        Producto p;
        if (tipo == 1) p = new ProductoPerecedero(codigo, nombre, categoria, unidad,
                stock, minimo, maximo, costo, ventas, entero("Día de caducidad"), gestor.getDiaActual());
        else if (tipo == 2) p = new ProductoNoPerecedero(codigo, nombre, categoria, unidad,
                stock, minimo, maximo, costo, ventas);
        else { errorEntrada = "Tipo de producto inválido."; return; }
        if (!entradaValida() || !correcto(gestor.registrarProducto(p))) { return; }
        System.out.println("Producto registrado: " + p.getNombre());
    }
    private void registrarProveedor() {
        Proveedor p = new Proveedor(texto("Nombre del proveedor"));
        if (!entradaValida() || !correcto(gestor.registrarProveedor(p))) { return; }
        System.out.println("Proveedor registrado: " + p.getNombre());
    }
    private EstimadorEntrega elegirEstimador(Proveedor proveedor) {
        int tipo = entero("Estimador (1 fijo, 2 historial)");
        if (tipo == 1) return new EstimadorFijo(entero("Días acordados"));
        if (tipo == 2) return new EstimadorHistorial(proveedor);
        if (entradaValida()) { errorEntrada = "Estimador inválido."; }
        return null;
    }
    private void crearPedido() {
        String codigo = texto("Código del pedido");
        Producto p = gestor.buscarProducto(texto("Código del producto"));
        if (!entradaValida()) { return; }
        if (p == null) { errorEntrada = "Producto no encontrado."; return; }
        Proveedor proveedor = gestor.buscarProveedor(texto("Nombre del proveedor"));
        if (!entradaValida()) { return; }
        if (proveedor == null) { errorEntrada = "Proveedor no encontrado."; return; }
        int cantidad = entero("Cantidad");
        int f = entero("Día del pedido");
        Pedido pedido = new Pedido(codigo, p, proveedor, cantidad, f, elegirEstimador(proveedor), gestor.getDiaActual());
        if (!entradaValida() || !correcto(gestor.registrarPedido(pedido))) { return; }
        System.out.println("Pedido registrado: " + codigo + " | Llegada: " + pedido.estimarLlegada());
        System.out.println("Costo total: USD " + pedido.getCostoTotal());
        if (gestor.stockProyectado(p) > p.getStockMaximo()) System.out.println("ALERTA: riesgo de sobrestock.");
        if (gestor.riesgoQuiebre(pedido, gestor.getDiaActual())) System.out.println("ALERTA: riesgo de quiebre.");
    }
    private void consultar() {
        System.out.println("--- INVENTARIO ---");
        Producto[] listaProductos = gestor.getProductos();
        for (int i = 0; i < listaProductos.length; i++) {
            Producto p = listaProductos[i];
            String vigente = "no";
            if (p.estaVigente(gestor.getDiaActual())) { vigente = "sí"; }
            String cobertura = "sin consumo";
            if (p.getVentasDiarias() > 0) { cobertura = "" + p.diasCobertura() + " días"; }
            System.out.println(p.getCodigo() + " | " + p.getNombre() + " | stock=" + p.getStock()
                    + " | mínimo=" + p.getStockMinimo() + " | máximo=" + p.getStockMaximo()
                    + " | costo=USD " + p.getCostoUnitario() + " | cobertura=" + cobertura
                    + " | vigente=" + vigente);
        }
        System.out.println("--- PEDIDOS ---");
        Pedido[] listaPedidos = gestor.getPedidos();
        for (int i = 0; i < listaPedidos.length; i++) {
            Pedido p = listaPedidos[i];
            String recibido = "";
            if (p.getFechaRealLlegada() >= 0) {
                recibido = " | recibido=día " + p.getFechaRealLlegada();
            }
            System.out.println(p.getCodigo() + " | " + p.getProducto().getNombre()
                    + " | " + p.getProveedor().getNombre() + " | cantidad=" + p.getCantidad()
                    + " | " + p.estadoEn(gestor.getDiaActual()) + " | estimado=" + p.estimarLlegada() + recibido);
        }
        System.out.println("--- PROVEEDORES ---");
        Proveedor[] listaProveedores = gestor.getProveedores();
        for (int i = 0; i < listaProveedores.length; i++) {
            Proveedor p = listaProveedores[i];
            System.out.println(p.getNombre() + " | historial (días): " + p.historialTexto());
        }
    }
    private void recibir() {
        String codigo = texto("Código del pedido");
        int dia = entero("Día real de recepción");
        if (!entradaValida() || !correcto(gestor.registrarRecepcion(codigo, dia))) { return; }
        Pedido p = gestor.buscarPedido(codigo);
        System.out.println("Recepción registrada. Stock actual: " + p.getProducto().getStock());
        System.out.println("Historial (días): " + p.getProveedor().historialTexto());
    }
    private void vender() {
        String codigo = texto("Código del producto");
        int cantidad = entero("Cantidad a vender");
        if (!entradaValida() || !correcto(gestor.registrarVenta(codigo, cantidad))) { return; }
        System.out.println("Venta registrada. Stock actual: " + gestor.buscarProducto(codigo).getStock());
    }
    private void alertas() {
        String[] a = gestor.alertas(gestor.getDiaActual());
        if (a.length == 0) System.out.println("Sin alertas.");
        else for (int i = 0; i < a.length; i++) System.out.println("ALERTA: " + a[i]);
    }
    private void cambiarEstimador() {
        Pedido p = gestor.buscarPedido(texto("Código del pedido"));
        if (!entradaValida()) { return; }
        if (p == null) { errorEntrada = "Pedido no encontrado."; return; }
        EstimadorEntrega estimador = elegirEstimador(p.getProveedor());
        if (!entradaValida() || !correcto(p.cambiarEstimador(estimador))) { return; }
        System.out.println("Estimador actualizado. Llegada: " + p.estimarLlegada());
    }
}

package modelo;

public class Proveedor {
    private String nombre;
    private int[] historialEntregas = new int[1000];
    private int cantidadEntregas = 0;

    public Proveedor(String nombre) {
        this.nombre = nombre;
    }
    public String getNombre() { return nombre; }
    public int getCantidadEntregas() { return cantidadEntregas; }
    public boolean historialLleno() {
        return cantidadEntregas == historialEntregas.length;
    }
    boolean registrarEntrega(int dias) {
        if (dias < 0 || historialLleno()) {
            return false;
        }
        historialEntregas[cantidadEntregas] = dias;
        cantidadEntregas++;
        return true;
    }
    public int[] getHistorial() {
        int[] copia = new int[cantidadEntregas];
        for (int i = 0; i < cantidadEntregas; i++) {
            copia[i] = historialEntregas[i];
        }
        return copia;
    }
    public String historialTexto() {
        String texto = "[";
        for (int i = 0; i < cantidadEntregas; i++) {
            if (i > 0) { texto = texto + ", "; }
            texto = texto + historialEntregas[i];
        }
        return texto + "]";
    }
    public double promedioDias() {
        if (cantidadEntregas == 0) { return 0; }
        double suma = 0;
        for (int i = 0; i < cantidadEntregas; i++) {
            suma = suma + historialEntregas[i];
        }
        return suma / cantidadEntregas;
    }
}

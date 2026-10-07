package LogicaJuego;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class HistorialTransacciones {
    private class Nodo {
        private Transaccion transaccion;
        private Nodo siguiente;
        private Nodo anterior;
    
        public Nodo(Transaccion transaccion) {
            this.transaccion = transaccion;
            this.siguiente = null;
            this.anterior = null;
        }
    }
    private Nodo primero;
    private Nodo ultimo;
    private int tamaño;

    public void agregar(Transaccion transaccion) {

        if (transaccion == null) {
            throw new IllegalArgumentException("La transacción no puede ser nula");
        }
        Nodo nuevoNodo = new Nodo(transaccion);

        if (primero == null) {
            primero = nuevoNodo;
            ultimo = nuevoNodo;
        } else {
            ultimo.siguiente = nuevoNodo;
            nuevoNodo.anterior = ultimo;
            ultimo = nuevoNodo;
        }
        tamaño++;
    }
    // Los métodos "Obtener" devuelven el resultado como texto (una transacción
    // por línea) para que Server lo pueda enviar a la GUI.
    // Los métodos "imprimir"/"buscar" muestran ese mismo texto en consola.
    public String ObtenerHistorial() {
        StringBuilder Texto = new StringBuilder();
        Nodo Actual = primero;
        while (Actual != null) {
            Texto.append(Actual.transaccion).append("\n");
            Actual = Actual.siguiente;
        }
        return Texto.toString();
    }
    public String ObtenerHistorialInverso() {
        StringBuilder Texto = new StringBuilder();
        Nodo Actual = ultimo;
        while (Actual != null) {
            Texto.append(Actual.transaccion).append("\n");
            Actual = Actual.anterior;
        }
        return Texto.toString();
    }
    public String ObtenerPorTipo(TipoTransaccion Tipo) {
        StringBuilder Texto = new StringBuilder();
        Nodo Actual = primero;
        while (Actual != null) {
            if (Actual.transaccion.getTipo().equals(Tipo)) {
                Texto.append(Actual.transaccion).append("\n");
            }
            Actual = Actual.siguiente;
        }
        return Texto.toString();
    }
    public String ObtenerPorJugador(String Jugador) {
        StringBuilder Texto = new StringBuilder();
        Nodo Actual = primero;
        while (Actual != null) {
            if (Actual.transaccion.getJugadorOrigen().equals(Jugador) || Actual.transaccion.getJugadorDestino().equals(Jugador)) {
                Texto.append(Actual.transaccion).append("\n");
            }
            Actual = Actual.siguiente;
        }
        return Texto.toString();
    }
    public void imprimirHistorial() {
        System.out.print(ObtenerHistorial());
    }
    public void imprimirHistorialInverso() {
        System.out.print(ObtenerHistorialInverso());
    }
    public void buscarPorTipo(TipoTransaccion tipo) {
        System.out.print(ObtenerPorTipo(tipo));
    }
    public void buscarPorJugador(String jugador) {
        System.out.print(ObtenerPorJugador(jugador));
    }
    public int getTamaño() {
        return tamaño;
    }
    public boolean exportarTXT(String nombreArchivo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(nombreArchivo))) {
            Nodo actual = primero;
            while (actual != null) {
                writer.println(actual.transaccion);
                actual = actual.siguiente;
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error al exportar el historial a archivo TXT." + e.getMessage());
            return false;
        }
    }
}



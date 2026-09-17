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
    public void imprimirHistorial() {
        Nodo actual = primero;
        while (actual != null) {
            System.out.println(actual.transaccion);
            actual = actual.siguiente;
        }
    }
    public void imprimirHistorialInverso() {
        Nodo actual = ultimo;
        while (actual != null) {
            System.out.println(actual.transaccion);
            actual = actual.anterior;
        }
    }
    public void buscarPorTipo(String tipo) {
        Nodo actual = primero;
        while (actual != null) {
            if (actual.transaccion.getTipo().equals(tipo)) {
                System.out.println(actual.transaccion);
            }
            actual = actual.siguiente;
        }
    }
    public void buscarPorJugador(String jugador) {
        Nodo actual = primero;
        while (actual != null) {
            if (actual.transaccion.getJugadorOrigen().equals(jugador) || actual.transaccion.getJugadorDestino().equals(jugador)) {
                System.out.println(actual.transaccion);
            }
            actual = actual.siguiente;
        }
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



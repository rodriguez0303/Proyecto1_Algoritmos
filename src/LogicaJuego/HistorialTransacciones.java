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

    //*****************************************************
    //*****************************************************

    // Método que permite obtener todas las transacciones guardadas en el historial.
        // Devuelve las transacciones como un String para que puedan ser utilizadas por otras clases. Por ejemplo, Server.
    public String obtenerHistorial() {

        // String que almacenará todas las transacciones encontradas.
        String historial = "";

        // Se crea una referencia temporal llamada "actual" que inicia apuntando al primer nodo del historial.
            // Se utiliza "actual" para recorrer los nodos sin modificar la referencia "primero".
        Nodo actual = primero;


        // Se recorren los nodos mientras exista una transacción por consultar.
            // Cuando "actual" sea null significa que se llegó al final del historial.
        while (actual != null) {


            // Se toma la transacción guardada en el nodo actual y se agrega al String que contiene el historial.
            historial = historial + actual.transaccion;


            // Se verifica si todavía existe otra transacción después de la actual.
            if (actual.siguiente != null) {

                // Se agrega un separador entre cada transacción .
                historial = historial + ";";
            }

            // Se avanza al siguiente nodo del historial. Desde la transacción más antigua hasta la más reciente.
            actual = actual.siguiente;
        }

        // Se devuelve el String que contiene todas las transacciones que fueron encontradas durante el recorrido del historial.
        return historial;
    }

        //*****************************************************
        //*****************************************************

    public void imprimirHistorialInverso() {
        Nodo actual = ultimo;
        while (actual != null) {
            System.out.println(actual.transaccion);
            actual = actual.anterior;
        }
    }
    public void buscarPorTipo(TipoTransaccion tipo) {
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



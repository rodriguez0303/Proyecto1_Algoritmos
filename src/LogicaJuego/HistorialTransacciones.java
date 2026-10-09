package LogicaJuego;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

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
    // Guarda el historial en un TXT fácil de leer, una transacción por línea:
    // T1   Turno 5    12:59:03   J002 pagó ₡450 al Banco (Compra de la propiedad BICITEC)
    // Se usa UTF-8 para que el símbolo ₡ y las tildes se guarden bien.
    public boolean exportarTXT(String nombreArchivo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(nombreArchivo, StandardCharsets.UTF_8))) {
            writer.println("HISTORIAL DE TRANSACCIONES");
            writer.println("==========================");
            writer.println();

            Nodo actual = primero;
            while (actual != null) {
                writer.println(FormatearLinea(actual.transaccion));
                actual = actual.siguiente;
            }

            writer.println();
            writer.println("Total de transacciones: " + tamaño);
            return true;
        } catch (IOException e) {
            System.err.println("Error al exportar el historial a archivo TXT." + e.getMessage());
            return false;
        }
    }

    // Arma la línea del TXT: identificador, turno, hora y una frase con quién pagó a quién.
    private String FormatearLinea(Transaccion Transaccion) {
        String Origen = Transaccion.getJugadorOrigen();
        String Destino = Transaccion.getJugadorDestino();
        String Monto = "₡" + (long) Transaccion.getMonto();

        String Frase;
        if (Origen.equals("BANCO")) {
            Frase = "El Banco pagó " + Monto + " a " + Destino;
        } else if (Destino.equals("BANCO")) {
            Frase = Origen + " pagó " + Monto + " al Banco";
        } else {
            Frase = Origen + " pagó " + Monto + " a " + Destino;
        }

        if (Transaccion.getDescripcion() != null && !Transaccion.getDescripcion().isBlank()) {
            Frase = Frase + " (" + Transaccion.getDescripcion() + ")";
        }

        String Hora = Transaccion.getFechaHora().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        return String.format("%-5s Turno %-4d %s   %s", Transaccion.getIdentificador(), Transaccion.getNumeroTurno(), Hora, Frase);
    }
}



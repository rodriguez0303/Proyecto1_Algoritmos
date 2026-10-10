package LogicaJuego;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * Historial implementado mediante una lista doblemente enlazada. Permite consultar, filtrar y exportar movimientos en ambos órdenes.
 */
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

    /**
     * Agrega una transacción al final de la lista doblemente enlazada y actualiza su tamaño.
     */
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
    /**
     * Construye la lista de registros desde el más antiguo hasta el más reciente.
     */
    public String ObtenerHistorial() {
        StringBuilder Texto = new StringBuilder();
        Nodo Actual = primero;
        while (Actual != null) {
            Texto.append(Actual.transaccion).append("\n");
            Actual = Actual.siguiente;
        }
        return Texto.toString();
    }
    /**
     * Construye los registros desde el último hacia el primero usando los enlaces anteriores.
     */
    public String ObtenerHistorialInverso() {
        StringBuilder Texto = new StringBuilder();
        Nodo Actual = ultimo;
        while (Actual != null) {
            Texto.append(Actual.transaccion).append("\n");
            Actual = Actual.anterior;
        }
        return Texto.toString();
    }
    /**
     * Selecciona las transacciones que corresponden a un tipo específico.
     */
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
    /**
     * Selecciona las transacciones en las que participa el identificador indicado, como origen o destino.
     */
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
    /**
     * Muestra por consola los movimientos en orden cronológico.
     */
    public void imprimirHistorial() {
        System.out.print(ObtenerHistorial());
    }

    //*****************************************************
    //*****************************************************

    // Método que permite obtener todas las transacciones guardadas en el historial.
        // Devuelve las transacciones como un String para que puedan ser utilizadas por otras clases. Por ejemplo, Server.
    /**
     * Devuelve los movimientos separados por punto y coma para el protocolo cliente-servidor.
     */
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

    /**
     * Muestra en consola los registros desde el más reciente hasta el más antiguo.
     */
    public void imprimirHistorialInverso() {
        System.out.print(ObtenerHistorialInverso());
    }
    /**
     * Imprime por consola las transacciones que coinciden con un tipo.
     */
    public void buscarPorTipo(TipoTransaccion tipo) {
        System.out.print(ObtenerPorTipo(tipo));
    }
    /**
     * Imprime por consola los movimientos vinculados a un jugador.
     */
    public void buscarPorJugador(String jugador) {
        System.out.print(ObtenerPorJugador(jugador));
    }
    /**
     * Devuelve el número total de transacciones registradas.
     */
    public int getTamaño() {
        return tamaño;
    }
    // Guarda el historial en un TXT (punto 13): una fila por transacción con
    // número de transacción, turno, tipo, origen, destino, monto y descripción.
    // Se usa UTF-8 para que el símbolo ₡ y las tildes se guarden bien.
    /**
     * Exporta el historial a un archivo de texto UTF-8 con encabezados y columnas.
     */
    public boolean exportarTXT(String nombreArchivo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(nombreArchivo, StandardCharsets.UTF_8))) {
            writer.println("HISTORIAL DE TRANSACCIONES");
            writer.println("Total de transacciones: " + tamaño);
            writer.println();
            writer.println(EncabezadoTabla());

            Nodo actual = primero;
            while (actual != null) {
                Transaccion t = actual.transaccion;
                writer.println(FilaTabla(t.getIdentificador(), t.getNumeroTurno(), t.getTipo().getNombre(),
                        t.getJugadorOrigen(), t.getJugadorDestino(), t.getMonto(), t.getDescripcion()));
                actual = actual.siguiente;
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error al exportar el historial a archivo TXT." + e.getMessage());
            return false;
        }
    }

    // Consulta combinada (la usa la pantalla final de la partida).
    // Recorre la lista doble desde la más antigua (primero -> siguiente) o desde la más reciente
    // (ultimo -> anterior) y se queda con las transacciones del Jugador y del Tipo pedidos.
    // Jugador == null o Tipo == null significa "todos".
    // Devuelve las transacciones separadas por ";", igual que obtenerHistorial().
    /**
     * Combina filtros por jugador, tipo y orden de lectura para la consulta de la pantalla final.
     */
    public String ObtenerConsulta(String Jugador, TipoTransaccion Tipo, boolean DesdeReciente) {
        StringBuilder Texto = new StringBuilder();
        Nodo Actual = DesdeReciente ? ultimo : primero;

        while (Actual != null) {
            Transaccion t = Actual.transaccion;

            boolean CoincideJugador = Jugador == null
                    || t.getJugadorOrigen().equals(Jugador)
                    || t.getJugadorDestino().equals(Jugador);
            boolean CoincideTipo = Tipo == null || t.getTipo() == Tipo;

            if (CoincideJugador && CoincideTipo) {
                if (Texto.length() > 0) {
                    Texto.append(";");
                }
                Texto.append(t);
            }

            Actual = DesdeReciente ? Actual.anterior : Actual.siguiente;
        }
        return Texto.toString();
    }

    // Encabezado y filas de la tabla de transacciones.
    // Los usan el TXT y la pantalla final, para que ambos se vean igual.
    /**
     * Construye el encabezado de columnas compartido por la interfaz y el archivo TXT.
     */
    public static String EncabezadoTabla() {
        return FilaTabla("N°", "Turno", "Tipo", "Origen", "Destino", "Monto", "Descripción") + "\n"
                + FilaTabla("----", "-----", "--------------------", "-------", "-------", "--------", "-----------");
    }

    /**
     * Da formato a los valores de una transacción para mostrarlos alineados en una fila.
     */
    public static String FilaTabla(String Numero, int Turno, String Tipo, String Origen,
                                   String Destino, double Monto, String Descripcion) {
        return FilaTabla(Numero, String.valueOf(Turno), Tipo, Origen, Destino, "₡" + (long) Monto, Descripcion);
    }

    /**
     * Da formato a los valores de una transacción para mostrarlos alineados en una fila.
     */
    private static String FilaTabla(String Numero, String Turno, String Tipo, String Origen,
                                    String Destino, String Monto, String Descripcion) {
        return String.format("%-5s %-6s %-21s %-8s %-8s %-9s %s",
                Numero, Turno, Tipo, Origen, Destino, Monto, Descripcion == null ? "" : Descripcion);
    }
}



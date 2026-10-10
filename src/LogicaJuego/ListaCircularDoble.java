package LogicaJuego;

/**
 * Clase ListaCircularDoble: Estructura de datos lineal propia (nodos
 * enlazados en círculo, en ambos sentidos), implementada desde cero según
 * lo exige el enunciado del Proyecto 1 (no se usa java.util.LinkedList ni
 * equivalentes).
 *
 * Se usa en Tablero como ListaCircularDoble<NodoCasilla>: administra la
 * colección completa de casillas del tablero (cuántas hay, cuál es la
 * primera), mientras que el recorrido casilla por casilla (avanzar,
 * retroceder) lo hacen directamente los propios NodoCasilla usando sus
 * atributos anterior/siguiente (ver NodoCasilla.java). Es decir: esta
 * clase es el "contenedor", NodoCasilla es quien realmente conecta las
 * casillas entre sí.
 *
 * Nota sobre dependencias externas:
 * - Es una clase genérica (T), no depende de ningún método específico de
 *   NodoCasilla; solo lo guarda como dato.
 *
 * Diferencia con ColaCircular (ver ColaCircular.java / Juego.java):
 * - ListaCircularDoble es de DOBLE sentido (Anterior y Siguiente) y se usa
 *   para el TABLERO: "¿en qué casilla estoy, cómo avanzo o retrocedo?".
 * - ColaCircular es de UN SOLO sentido (solo Siguiente) y se usa para el
 *   ORDEN DE TURNOS: "¿a quién le toca jugar?".
 * Se necesitan las dos por separado porque el tablero sí debe poder
 * retroceder (cartas de evento, punto 10 del enunciado), mientras que los
 * turnos solo avanzan.
 */

public class ListaCircularDoble<T> {

    // Nodo interno de la lista: Guarda un dato y se enlaza en ambos sentidos
    private class Nodo {
        T Dato;
        Nodo Anterior;
        Nodo Siguiente;

        Nodo(T Dato) {
            this.Dato = Dato;
        }
    }

    private Nodo Primero;   // Primer nodo agregado (referencia de entrada al círculo)
    private Nodo Ultimo;    // Último nodo agregado (para insertar en O(1))
    private int Tamaño;     // Cantidad de elementos en la lista

    // Agrega un elemento al final de la lista, manteniendo el círculo
    // cerrado en ambos sentidos (Anterior y Siguiente)
    /**
     * Inserta un nuevo elemento preservando los enlaces anterior y siguiente del círculo.
     */
    public void Agregar(T Elemento) {
        Nodo Nuevo = new Nodo(Elemento);
        if (Vacio()) {
            Nuevo.Anterior = Nuevo;
            Nuevo.Siguiente = Nuevo;
            Primero = Nuevo;
        } else {
            Nuevo.Siguiente = Primero;
            Nuevo.Anterior = Ultimo;
            Ultimo.Siguiente = Nuevo;
            Primero.Anterior = Nuevo;
        }
        Ultimo = Nuevo;
        Tamaño++;
    }

    // Devuelve el primer elemento agregado, sin moverse
    /**
     * Devuelve el primer dato almacenado sin alterar la lista.
     */
    public T ObtenerPrimero() {
        if (Vacio()) {
            return null;
        }
        return Primero.Dato;
    }

    // Cantidad actual de elementos en la lista
    /**
     * Devuelve el número total de elementos almacenados.
     */
    public int Tamaño() {
        return Tamaño;
    }

    // Indica si la lista no tiene elementos
    /**
     * Indica si la lista circular se encuentra vacía.
     */
    public boolean Vacio() {
        return Tamaño == 0;
    }
}
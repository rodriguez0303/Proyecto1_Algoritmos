package LogicaJuego;

/**
 * Clase ColaCircular: Estructura de datos lineal propia (nodos enlazados
 * en forma de círculo), implementada desde cero según lo exige el
 * enunciado del Proyecto 1 (no se usa java.util.Queue/List ni
 * equivalentes).
 *
 * Se usa en Juego como ColaCircular<Jugador>, para administrar el orden
 * de turno de los jugadores: "Avanzar()" mueve el puntero interno al
 * siguiente jugador y lo devuelve; "Vacio()" y "Tamaño()" permiten
 * controlar los límites al recorrerla (por ejemplo, para saltar
 * jugadores eliminados sin dar una vuelta infinita).
 *
 * Nota sobre dependencias externas:
 * - Es una clase genérica (T), no depende de ningún método específico
 *   de Jugador; solo lo guarda como dato.
 *
 * Diferencia con ListaCircularDoble (ver ListaCircularDoble.java / Tablero.java):
 * - ColaCircular es de UN SOLO sentido (cada nodo solo conoce a su
 *   Siguiente) y se usa para el ORDEN DE TURNOS: "¿a quién le toca jugar?".
 * - ListaCircularDoble es de DOBLE sentido (Anterior y Siguiente) y se usa
 *   para el TABLERO: "¿en qué casilla estoy, cómo avanzo o retrocedo?".
 * No son intercambiables: el orden de turnos nunca retrocede, pero el
 * tablero sí lo necesita (cartas de evento que hacen retroceder posiciones,
 * punto 10 del enunciado).
 */

public class ColaCircular<T> {
    // Nodo interno de la cola: Guarda un dato y apunta al siguiente nodo
    private class Nodo {
        T Dato;
        Nodo Siguiente;

        Nodo(T Dato) {
            this.Dato = Dato;
        }
    }

    private Nodo Actual;      // Nodo que representa el turno/posición actual
    private Nodo Ultimo;      // Referencia al último nodo agregado
    private int Tamaño;       // Cantidad de elementos en la cola

    // Agrega un elemento al final de la cola, manteniendo el círculo cerrado
    // si la cola está vacía, el nuevo nodo se apunta a sí mismo y queda como "Actual".
    public void Agregar(T Elemento) {
        Nodo Nuevo = new Nodo(Elemento);
        if (Vacio()) {
            Nuevo.Siguiente = Nuevo;
            Actual = Nuevo;
        } else {
            Nuevo.Siguiente = Ultimo.Siguiente;
            Ultimo.Siguiente = Nuevo;
        }
        Ultimo = Nuevo;
        Tamaño++;
    }

    // Devuelve el elemento en la posición actual, sin mover el puntero
    public T ObtenerActual() {
        if (Vacio()) {
            return null;
        }
        return Actual.Dato;
    }

    // Mueve el puntero "Actual" al siguiente nodo del círculo y devuelve su dato
    public T Avanzar() {
        if (Vacio()) {
            return null;
        }
        Actual = Actual.Siguiente;
        return Actual.Dato;
    }

    // Elimina el nodo actual de la cola y deja el puntero "Actual" apuntando al siguiente nodo
    public boolean EliminarActual() {
        if (Vacio()) {
            return false;
        }
        if (Tamaño == 1) {
            Actual = null;
            Ultimo = null;
            Tamaño = 0;
            return true;
        }
        Nodo Anterior = ObtenerAnterior(Actual);
        Anterior.Siguiente = Actual.Siguiente;
        if (Actual == Ultimo) {
            Ultimo = Anterior;
        }
        Actual = Actual.Siguiente;
        Tamaño--;
        return true;
    }

    // Recorre el círculo hasta encontrar el nodo previo al que se recibe
    // (es necesario para desconectar un nodo, pues no es una lista doble:
    // cada nodo solo conoce a su "Siguiente", no tiene un puntero hacia atrás)
    private Nodo ObtenerAnterior(Nodo Nodo) {
        // Temp es solo un puntero de trabajo (temporal): se usa para ir
        // avanzando nodo por nodo alrededor del círculo hasta encontrar
        // aquel cuyo "Siguiente" apunte justo al nodo que buscamos. No
        // representa nada del juego, se descarta al terminar el método.
        Nodo Temp = Nodo.Siguiente;
        while (Temp.Siguiente != Nodo) {
            Temp = Temp.Siguiente;
        }
        return Temp;
    }

    // Cantidad actual de elementos en la cola
    public int Tamaño() {
        return Tamaño;
    }

    // Indica si la cola no tiene elementos
    public boolean Vacio() {
        return Tamaño == 0;
    }
}
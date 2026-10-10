package LogicaJuego;

/**
 * Clase NodoCasilla: representa un nodo del tablero, con referencias a la
 * casilla anterior y siguiente (lista circular doblemente enlazada).
 * A diferencia de ColaCircular (donde el nodo es una clase
 * interna oculta), aquí NodoCasilla es una clase pública y propia, porque
 * así la define el diagrama de clases del proyecto.
 *
 * Nota sobre dependencias externas:
 * - Casilla es, por ahora, un stub mínimo (Par 2, aún no desarrollado a
 *   fondo por quien lo tenga asignado); NodoCasilla solo la referencia,
 *   no depende de su lógica interna.
 */

public class NodoCasilla {

    // Aca para Casilla tomar en cuenta esto, eliminar mensaje despúes:
    // - Casilla debe conservar el método público getNombre(): String
    // - Casilla debe conservar el método público Ejecutar(Jugador, Juego): void,
    //   NO "final", para que Propiedad/CasillaEvento/CasillaEspecial lo sobreescriban
    private Casilla Casilla;
    private NodoCasilla Anterior;
    private NodoCasilla Siguiente;

    /**
     * Crea un nodo que guarda una casilla del tablero.
     */
    public NodoCasilla(Casilla Casilla) {
        this.Casilla = Casilla;
    }

    /**
     * Devuelve la casilla almacenada en el nodo.
     */
    public Casilla getCasilla() {
        return Casilla;
    }

    /**
     * Consulta el nodo anterior para recorridos hacia atrás.
     */
    public NodoCasilla getAnterior() {
        return Anterior;
    }

    /**
     * Consulta el nodo siguiente para movimientos hacia adelante.
     */
    public NodoCasilla getSiguiente() {
        return Siguiente;
    }

    /**
     * Asigna la referencia al nodo anterior.
     */
    public void setAnterior(NodoCasilla N) {
        this.Anterior = N;
    }

    /**
     * Asigna la referencia al siguiente nodo.
     */
    public void setSiguiente(NodoCasilla N) {
        this.Siguiente = N;
    }
}
package LogicaJuego;

/**
 * Clase Tablero: Representa el tablero del juego como una lista circular
 * doblemente enlazada de NodoCasilla. El movimiento se hace recorriendo
 * los nodos del círculo.
 *
 * Nota sobre dependencias externas:
 * - Casilla es, por ahora, un stub mínimo (Par 2, pendiente de que quien
 *   lo tenga asignado la desarrolle con sus subtipos reales: Propiedad,
 *   CasillaEvento, CasillaEspecial, usando extends). Tablero solo la usa
 *   para armar cada NodoCasilla, no depende de su lógica interna.
 */

public class Tablero {

    private ListaCircularDoble<NodoCasilla> Lista;

    // Construye el tablero: Crea las casillas (por ahora genéricas, ver
    // Casilla.java) y las enlaza en un círculo doble usando NodoCasilla.
    // La cantidad de casillas se toma de Constantes.NUMERO_CASILLAS.
    public Tablero() {
        this.Lista = new ListaCircularDoble<>();

        NodoCasilla PrimerNodo = null;
        NodoCasilla NodoAnterior = null;

        for (int i = 0; i < Constantes.NUMERO_CASILLAS; i++) {
            // * placeholder: Casilla real la hace el Par 2. Cuando existan
            // Propiedad/CasillaEvento/CasillaEspecial, esta línea es la que
            // hay que cambiar para crear el tipo correcto según la posición
            // "i" (por ejemplo, algunas Propiedad, otras CasillaEvento,
            // etc.), en vez de siempre "new Casilla(...)". El resto de
            // Tablero (NodoCasilla, ObtenerSiguiente, ObtenerNodoActual)
            // no necesita cambiar, siempre que el resultado siga siendo
            // un objeto Casilla (o subtipo)
            Casilla Casilla = new Casilla("Casilla " + i);
            NodoCasilla NodoActual = new NodoCasilla(Casilla);

            Lista.Agregar(NodoActual);

            if (PrimerNodo == null) {
                PrimerNodo = NodoActual;
            } else {
                NodoAnterior.setSiguiente(NodoActual);
                NodoActual.setAnterior(NodoAnterior);
            }
            NodoAnterior = NodoActual;
        }

        // Cierra el círculo: Conecta el último nodo con el primero en ambos sentidos
        NodoAnterior.setSiguiente(PrimerNodo);
        PrimerNodo.setAnterior(NodoAnterior);
    }

    // Devuelve el NodoCasilla en el que está parado un Jugador, según su
    // posicionActual (recorre desde el primer nodo hasta llegar al índice).
    // Es el método "puente" entre Jugador (que solo guarda un int) y el
    // Tablero real (que navega por NodoCasilla), lo necesita Juego para
    // poder encadenar esto con ObtenerSiguiente() y moverse.
    public NodoCasilla ObtenerNodoActual(Jugador Jugador) {
        NodoCasilla Nodo = Lista.ObtenerPrimero();
        int Pasos = Jugador.getPosicionActual() % Constantes.NUMERO_CASILLAS;

        for (int i = 0; i < Pasos; i++) {
            Nodo = Nodo.getSiguiente();
        }
        return Nodo;
    }

    // Devuelve la Casilla en la que está parado un Jugador.
    // Reutiliza ObtenerNodoActual() para no repetir el recorrido.
    public Casilla ObtenerCasillaActual(Jugador Jugador) {
        return ObtenerNodoActual(Jugador).getCasilla();
    }

    // Avanza "pasos" nodos a partir de "nodo", recorriendo el círculo (nodo por nodo)
    public NodoCasilla ObtenerSiguiente(NodoCasilla Nodo, int Pasos) {
        NodoCasilla Actual = Nodo;
        for (int i = 0; i < Pasos; i++) {
            Actual = Actual.getSiguiente();
        }
        return Actual;
    }

    public int getNumeroCasillas() {
        return Lista.Tamaño();
    }

    // Recorre el círculo una vuelta completa desde el primer nodo e imprime el nombre de cada casilla, en orden
    public void MostrarTablero() {
        NodoCasilla Primero = Lista.ObtenerPrimero();
        NodoCasilla Actual = Primero;
        int Contador = 0;

        while (Contador < Lista.Tamaño()) {
            System.out.println(Actual.getCasilla().getNombre());
            Actual = Actual.getSiguiente();
            Contador++;
        }
    }
}
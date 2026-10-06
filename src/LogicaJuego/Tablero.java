package LogicaJuego;

/**
 * Clase Tablero: Representa el tablero del juego como una lista circular
 * doblemente enlazada de NodoCasilla. El movimiento se hace recorriendo
 * los nodos del círculo, hacia adelante (getSiguiente) o hacia atrás
 * (getAnterior).
 *
 * Detalles a tener en cuenta:
 * Se coloco "*", indica que depende de una clase que
 * todavía NO está incorporada de forma definitiva al proyecto.
 *
 * Nota sobre dependencias externas:
 * - Casilla ya existe como clase base con ejecutar(Jugador, Juego).
 * - CasillaEvento ya existe y se coloca en las posiciones fijas de
 *   Constantes.POSICIONES_EVENTO (3, 9, 15 y 21, contando desde Salida = 0).
 * - Propiedad y CasillaEspecial todavía no se colocan en el tablero;
 *   mientras tanto, esas posiciones usan una Casilla genérica.
 */

public class Tablero {

    private ListaCircularDoble<NodoCasilla> Lista;   // Lista circular doble propia con los nodos del tablero

    // Construye el tablero: Crea las casillas según su posición y las
    // enlaza en un círculo doble usando NodoCasilla.
    // La cantidad de casillas se toma de Constantes.NUMERO_CASILLAS.
    public Tablero() {
        this.Lista = new ListaCircularDoble<>();

        NodoCasilla PrimerNodo = null;
        NodoCasilla NodoAnterior = null;

        for (int i = 0; i < Constantes.NUMERO_CASILLAS; i++) {
            NodoCasilla NodoActual = new NodoCasilla(CrearCasilla(i));

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

    // Decide qué tipo de casilla va en la posición "i" al armar el tablero.
    // Se llama una sola vez por casilla: el tipo de cada posición no cambia
    // durante la partida. Para mover los eventos, solo se cambia
    // Constantes.POSICIONES_EVENTO.
    private Casilla CrearCasilla(int i) {
        if (EsPosicionEvento(i)) {
            return new CasillaEvento("Evento");
        }
        // * pendiente: cuando existan Propiedad y CasillaEspecial, crearlas aquí
        // según la posición, con los mismos nombres y precios de la interfaz
        // (esquinas: 0 Salida, 6 Edificio D3, 12 Especial, 18 Ir al D3)
        return new Casilla("Casilla " + i);
    }

    // Indica si la posición "i" es una de las casillas de evento fijas
    private boolean EsPosicionEvento(int i) {
        for (int Posicion : Constantes.POSICIONES_EVENTO) {
            if (Posicion == i) {
                return true;
            }
        }
        return false;
    }

    // Devuelve el NodoCasilla que está en una posición del tablero.
    // Acepta posiciones negativas o mayores al tamaño y las ajusta al círculo.
    public NodoCasilla ObtenerNodo(int Posicion) {
        int NumCasillas = getNumeroCasillas();
        int Pasos = ((Posicion % NumCasillas) + NumCasillas) % NumCasillas;

        NodoCasilla Nodo = Lista.ObtenerPrimero();
        for (int i = 0; i < Pasos; i++) {
            Nodo = Nodo.getSiguiente();
        }
        return Nodo;
    }

    // Devuelve el NodoCasilla en el que está parado un Jugador, según su
    // posicionActual. Es el método "puente" entre Jugador (que solo guarda
    // un int) y el Tablero real (que navega por NodoCasilla).
    public NodoCasilla ObtenerNodoActual(Jugador Jugador) {
        return ObtenerNodo(Jugador.getPosicionActual());
    }

    // Devuelve la Casilla en la que está parado un Jugador.
    // Reutiliza ObtenerNodoActual() para no repetir el recorrido.
    public Casilla ObtenerCasillaActual(Jugador Jugador) {
        return ObtenerNodoActual(Jugador).getCasilla();
    }

    // Avanza "Pasos" nodos a partir de "Nodo", recorriendo el círculo hacia adelante
    public NodoCasilla ObtenerSiguiente(NodoCasilla Nodo, int Pasos) {
        NodoCasilla Actual = Nodo;
        for (int i = 0; i < Pasos; i++) {
            Actual = Actual.getSiguiente();
        }
        return Actual;
    }

    // Retrocede "Pasos" nodos a partir de "Nodo", recorriendo el círculo hacia atrás.
    // Usa la referencia a la casilla anterior: es lo que justifica que el
    // tablero sea una lista DOBLEMENTE enlazada.
    public NodoCasilla ObtenerAnterior(NodoCasilla Nodo, int Pasos) {
        NodoCasilla Actual = Nodo;
        for (int i = 0; i < Pasos; i++) {
            Actual = Actual.getAnterior();
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
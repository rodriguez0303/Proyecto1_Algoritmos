package LogicaJuego;

/**
 * Clase Tablero: Representa el tablero del juego como una lista circular
 * doblemente enlazada de NodoCasilla. El movimiento se hace recorriendo
 * los nodos del círculo, hacia adelante (getSiguiente) o hacia atrás
 * (getAnterior).
 *
 * Detalles a tener en cuenta:
 * Se coloco "*", indica que depende de una clase que
 * todavía no está incorporada de forma definitiva al proyecto.
 *
 * Nota sobre dependencias externas:
 * - Casilla ya existe como clase base con ejecutar(Jugador, Juego).
 * - CasillaEvento ya existe y se coloca en las posiciones fijas de
 *   Constantes.POSICIONES_EVENTO (3, 9, 15 y 21, contando desde Salida = 0).
 * - CasillaEspecial va en las esquinas (0 Salida, 6 Edificio D3,
 *   12 Especial, 18 Ir al D3) y Propiedad en el resto de posiciones,
 *   con los mismos nombres y precios que la GUI.
 */

public class Tablero {

    private ListaCircularDoble<NodoCasilla> Lista;   // Lista circular doble propia con los nodos del tablero
    private MazoEventos Mazo;                        // Mazo único que comparten las 4 casillas de evento

    // Nombres de las casillas, en el mismo orden que la GUI (VentanaJuego.nombresCasillas)
    private static final String[] NombresCasillas = {
        "Salida",                       // 0
        "Comedor Institucional",        // 1
        "Soda Forestal",                // 2
        "Evento",                       // 3
        "Biblioteca Figueres Ferrer",   // 4
        "Learning Commons",             // 5
        "Edificio D3",                  // 6
        "ASETEC",                       // 7
        "Puesto Antonio",               // 8
        "Evento",                       // 9
        "Bosque de Bambúes",            // 10
        "Lago",                         // 11
        "Especial",                     // 12
        "GymTEC",                       // 13
        "Soda Deportiva",               // 14
        "Evento",                       // 15
        "Escuela de Computadores",      // 16
        "Escuela de Electrónica",       // 17
        "Ir al D3",                     // 18
        "Cancha de fútbol",             // 19
        "Cancha de béisbol",            // 20
        "Evento",                       // 21
        "BICITEC",                      // 22
        "UberTEC"                       // 23
    };

    // Precio de compra de cada casilla, igual que la GUI (VentanaJuego.preciosPropiedadesSimulados).
    // Las casillas que no son propiedad llevan 0.
    private static final double[] PreciosCasillas = {
        0,   100, 100,   0,   150, 150,
        0,   200, 200,   0,   250, 250,
        0,   300, 300,   0,   350, 350,
        0,   400, 400,   0,   450, 450
    };

    // Construye el tablero: Crea las casillas según su posición y las
    // enlaza en un círculo doble usando NodoCasilla.
    // La cantidad de casillas se toma de Constantes.NUMERO_CASILLAS.
    /**
     * Construye las casillas del campus y las conecta en la estructura circular.
     */
    public Tablero() {
        this.Lista = new ListaCircularDoble<>();
        this.Mazo = new MazoEventos();   // Se crea antes de las casillas porque las de evento lo necesitan

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

    // Decide qué tipo de casilla va en la posición "i" al armar el tablero:
    // - CasillaEvento: el centro de cada lado; sacan cartas del mazo de eventos.
    // - CasillaEspecial: las esquinas (Salida, Edificio D3, Especial, Ir al D3).
    // - Propiedad: todas las demás.
    // Se llama una sola vez por casilla: el tipo de cada posición no cambia
    // durante la partida. Para mover los eventos, solo se cambia
    // Constantes.POSICIONES_EVENTO.
    /**
     * Construye el tipo de casilla que corresponde a una posición concreta.
     */
    private Casilla CrearCasilla(int i) {
        if (EsPosicionEvento(i)) {
            return new CasillaEvento(NombresCasillas[i], Mazo);
        }
        switch (i) {
            case Constantes.POSICION_SALIDA:
                return new CasillaEspecial(NombresCasillas[i], TipoCasillaEspecial.SALIDA);
            case Constantes.POSICION_D3:
                return new CasillaEspecial(NombresCasillas[i], TipoCasillaEspecial.VISITA_D3);
            case Constantes.POSICION_ESPECIAL:
                return new CasillaEspecial(NombresCasillas[i], TipoCasillaEspecial.ESPECIAL);
            case Constantes.POSICION_IR_D3:
                return new CasillaEspecial(NombresCasillas[i], TipoCasillaEspecial.IR_AL_D3);
        }
        // El resto de posiciones son propiedades; el identificador usa la posición (P01, P02...)
        String Identificador = String.format("P%02d", i);
        return new Propiedad(Identificador, NombresCasillas[i], PreciosCasillas[i], Constantes.ALQUILER_PROPIEDAD);
    }

    // Indica si la posición "i" es una de las casillas de evento fijas
    /**
     * Determina si la posición representa una casilla de evento.
     */
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
    /**
     * Recupera el nodo que corresponde a un índice del tablero.
     */
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
    /**
     * Busca el nodo asociado a la posición actual de un jugador.
     */
    public NodoCasilla ObtenerNodoActual(Jugador Jugador) {
        return ObtenerNodo(Jugador.getPosicionActual());
    }

    // Devuelve la Casilla en la que está parado un Jugador.
    // Reutiliza ObtenerNodoActual() para no repetir el recorrido.
    /**
     * Obtiene el objeto Casilla situado bajo la ficha del jugador.
     */
    public Casilla ObtenerCasillaActual(Jugador Jugador) {
        return ObtenerNodoActual(Jugador).getCasilla();
    }

    // Avanza "Pasos" nodos a partir de "Nodo", recorriendo el círculo hacia adelante
    /**
     * Avanza una cantidad de enlaces siguientes desde el nodo dado.
     */
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
    /**
     * Retrocede una cantidad de enlaces anteriores desde el nodo dado.
     */
    public NodoCasilla ObtenerAnterior(NodoCasilla Nodo, int Pasos) {
        NodoCasilla Actual = Nodo;
        for (int i = 0; i < Pasos; i++) {
            Actual = Actual.getAnterior();
        }
        return Actual;
    }

    /**
     * Devuelve la cantidad de posiciones que integran el tablero.
     */
    public int getNumeroCasillas() {
        return Lista.Tamaño();
    }

    // Recorre el círculo una vuelta completa desde el primer nodo e imprime el nombre de cada casilla, en orden
    /**
     * Imprime el recorrido y las casillas del tablero para su comprobación.
     */
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
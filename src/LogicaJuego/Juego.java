package LogicaJuego;

/**
 * Clase Juego: Administra el estado de una partida (Turnos, tablero, dados,
 * transacciones, etc.) según el diagrama UML del Proyecto 1.
 *
 * D * Detalles a tener en cuenta:
 *  * Se coloco "*" junto a un atributo o método indica que depende de algo que
 *  * todavía no está terminado en otra clase (Banco, Propiedad, Jugador).
 *  * Mientras tanto se deja comentado o con un comportamiento mínimo; cuando
 *  * eso pase, solo hay que completar esas líneas sin tocar el resto de Juego.
 *
 * Nota sobre dependencias externas:
 * - Jugador es una clase aparte que ya está implementada
 * - Server (el "Servidor" del diagrama, paquete red) también es una
 *   clase aparte ya implementada; es quien le hace las solicitudes a Juego.
 * - "Banco" ya funciona (pagar, recibir, transferir), pero falta definir
 *    su saldo inicial y su relación final con Server.
 */

public class Juego {

    private ColaCircular<Jugador> Jugadores;   // Cola circular propia; "Jugador"
    private Jugador TurnoActual;               // Referencia al Jugador con el turno actual
    private int NumTurno;                      // Número de turno actual de la partida
    private boolean Curso;                     // Indica si la partida está activa
    private int MaxTurnos;                     // Límite de turnos configurable
    private boolean DadosLanzados;             // Indica si ya se lanzaron los dados en este turno (evita lanzar dos veces)
    private Tablero Tablero;                   // Estructura del tablero (24 casillas enlazadas en circulo doble)
    private Banco Banco;                       // / * pendiente: saldo inicial y relación final con Server
    private Dado Dado1;                        // Dado ya implementada; el módulo RFID real (punto 14) podría integrarse más adelante
    private Dado Dado2;                        // Dado ya implementada; el módulo RFID real (punto 14) podría integrarse más adelante
    private HistorialTransacciones Historial;  // * pendiente: implementación final del historial
    private ColaCircular<CartaEvento> Mazo;    // Mazo de cartas de evento: al usar una carta, pasa al final
    private boolean CartaEnCurso;              // Evita que una carta que mueve al jugador dispare otra carta en cadena

    // Constructor: Crea la partida y sus dependencias. (varias aún temporales, ver "*")
    public Juego(int MaxTurnos) {
        this.Jugadores = new ColaCircular<>();
        this.NumTurno = 0;
        this.Curso = false;
        this.MaxTurnos = MaxTurnos;
        this.DadosLanzados = false;
        this.Tablero = new Tablero();
        this.Banco = new Banco();                        // * pendiente: el Banco arranca con saldo 0, así que no puede pagar
        // cartas de "recibir dinero". Cuando Banco tenga un constructor con
        // saldo inicial, usar algo como: new Banco(Constantes.SALDO_INICIAL_BANCO)
        this.Dado1 = new Dado(1);
        this.Dado2 = new Dado(2);
        this.Historial = new HistorialTransacciones();
        this.Mazo = new ColaCircular<>();
        this.CartaEnCurso = false;
        CrearMazo();
    }

    // Llena el mazo con las cartas de evento, al menos una por cada TipoEvento (punto 10)
    private void CrearMazo() {
        Mazo.Agregar(new CartaEvento("C01", "Ganaste una beca: recibe 200", TipoEvento.RECIBIR_DINERO, 200));
        Mazo.Agregar(new CartaEvento("C02", "Pago de matrícula: paga 150", TipoEvento.PAGAR_DINERO, 150));
        Mazo.Agregar(new CartaEvento("C03", "Encontraste un atajo: avanza 3 casillas", TipoEvento.AVANZAR, 3));
        Mazo.Agregar(new CartaEvento("C04", "Olvidaste algo: retrocede 2 casillas", TipoEvento.RETROCEDER, 2));
        Mazo.Agregar(new CartaEvento("C05", "Te quedaste dormido: pierdes un turno", TipoEvento.PERDER_TURNO, 0));
        Mazo.Agregar(new CartaEvento("C06", "Regresa a la casilla de inicio", TipoEvento.IR_A_CASILLA, 0));
        Mazo.Agregar(new CartaEvento("C07", "Premio en un concurso: recibe 100", TipoEvento.RECIBIR_DINERO, 100));
        Mazo.Agregar(new CartaEvento("C08", "Multa de tránsito: paga 50", TipoEvento.PAGAR_DINERO, 50));
    }

    // Saca la carta que está al frente del mazo y avanza la cola circular.
    // Al avanzar, la carta recién usada queda justo antes del nuevo frente,
    // es decir, al final de la cola, lista para reutilizarse.
    public CartaEvento SacarCarta() {
        if (Mazo.Vacio()) {
            return null;
        }
        CartaEvento Carta = Mazo.ObtenerActual();
        Mazo.Avanzar();
        return Carta;
    }

    // Agrega un "Jugador" a la cola de turnos, solo si la partida aún no ha iniciado
    public void AgregarJugador(Jugador Jugador) {
        if (Curso) {
            throw new IllegalStateException("No se pueden agregar más jugadores con la partida en curso");
        }
        Jugadores.Agregar(Jugador);
    }

    // Marca la partida como iniciada y define quién tiene el primer turno
    public void IniciarPartida() {
        if (Jugadores.Vacio()) {
            throw new IllegalStateException("No hay jugadores para iniciar la partida");
        }
        Curso = true;
        NumTurno = 1;
        TurnoActual = Jugadores.ObtenerActual();
    }

    // Server (obtenerJugadorActual), no cambiar
    // Obtiene al jugador actual
    public Jugador obtenerJugadorActual() {
        return TurnoActual;
    }

    // Avanza al siguiente jugador activo y controla si se llegó al límite de turnos
    public void SiguienteTurno() {
        if (!Curso) {
            return;
        }

        Jugador Sig = Jugadores.Avanzar();
        int Intentos = Jugadores.Tamaño();

        // Salta a los jugadores AFK o eliminados, hasta encontrar uno activo.
        // * pendiente: cuando Jugador tenga debePerderTurno() y consumirTurnoPerdido(),
        // también saltar a quien perdió un turno por una carta:
        // while (Sig != null && Intentos > 0 && (!Sig.esActivo() || Sig.debePerderTurno())) {
        //     if (Sig.esActivo()) Sig.consumirTurnoPerdido();
        while (Sig != null && !Sig.esActivo() && Intentos > 0) {
            Sig = Jugadores.Avanzar();
            Intentos--;
        }

        TurnoActual = Sig;
        NumTurno++;

        if (MaxTurnos > 0 && NumTurno > MaxTurnos) {
            finalizarPartida();
        }
    }

    // Server (finalizarTurno), no cambiar.
    // Resetea el control de dados del turno y pasa el turno al siguiente jugador
    public void finalizarTurno() {
        DadosLanzados = false;
        SiguienteTurno();
    }

    // Server (lanzarDados), no cambiar.
    // Lanza los dos dados y devuelve la suma (Dado ya implementada; podría
    // conectarse más adelante al módulo RFID real del punto 14 sin cambiar esta firma)
    public int lanzarDados() {
        int Valor1 = Dado1.lanzar();
        int Valor2 = Dado2.lanzar();
        DadosLanzados = true;
        return Valor1 + Valor2;
    }

    // Server (getDadosLanzadosEsteTurno), no cambiar.
    // Server lo usa para impedir lanzar los dados dos veces en el mismo turno
    public boolean getDadosLanzadosEsteTurno() {
        return DadosLanzados;
    }

    // Mueve a un Jugador "Pos" casillas hacia adelante, recorriendo los nodos
    // reales del Tablero. Actualiza la posición (int) de Jugador, muestra a
    // qué casilla llegó y ejecuta el comportamiento de esa casilla.
    public void MoverJugador(Jugador Jugador, int Pos) {
        NodoCasilla NodoActual = Tablero.ObtenerNodoActual(Jugador);
        NodoCasilla NodoDestino = Tablero.ObtenerSiguiente(NodoActual, Pos);

        // * pendiente: si NuevaPos < posición anterior, el jugador pasó por
        // Inicio y hay que pagarle el premio (PREMIO_POR_INICIO, punto 11)
        Jugador.setPosicionActual(AjustarPosicion(Jugador.getPosicionActual() + Pos));

        System.out.println(Jugador.getNombre() + " Se mueve a: " + NodoDestino.getCasilla().getNombre());
        EjecutarCasilla(Jugador, NodoDestino);
    }

    // Mueve a un Jugador "Pasos" casillas hacia atrás usando la referencia
    // a la casilla anterior de cada nodo (lo usa la carta RETROCEDER).
    public void RetrocederJugador(Jugador Jugador, int Pasos) {
        NodoCasilla NodoActual = Tablero.ObtenerNodoActual(Jugador);
        NodoCasilla NodoDestino = Tablero.ObtenerAnterior(NodoActual, Pasos);

        Jugador.setPosicionActual(AjustarPosicion(Jugador.getPosicionActual() - Pasos));

        System.out.println(Jugador.getNombre() + " Retrocede a: " + NodoDestino.getCasilla().getNombre());
        EjecutarCasilla(Jugador, NodoDestino);
    }

    // Lleva a un Jugador directo a una casilla determinada (lo usa la carta IR_A_CASILLA)
    public void MoverJugadorA(Jugador Jugador, int Posicion) {
        int NuevaPos = AjustarPosicion(Posicion);
        NodoCasilla NodoDestino = Tablero.ObtenerNodo(NuevaPos);

        Jugador.setPosicionActual(NuevaPos);

        System.out.println(Jugador.getNombre() + " Va directo a: " + NodoDestino.getCasilla().getNombre());
        EjecutarCasilla(Jugador, NodoDestino);
    }

    // Deja cualquier posición dentro del rango 0 .. NumeroCasillas-1.
    // Se suma NumeroCasillas antes del segundo módulo porque en Java
    // -2 % 24 da -2 (y no 22), lo que dejaría al jugador en una posición negativa.
    private int AjustarPosicion(int Posicion) {
        int NumCasillas = Tablero.getNumeroCasillas();
        return ((Posicion % NumCasillas) + NumCasillas) % NumCasillas;
    }

    // Ejecuta el comportamiento de la casilla donde cayó el Jugador,
    // cada tipo de Casilla sobrescribe ejecutar()).
    // Si el movimiento viene de una carta y cae en otra CasillaEvento, no se
    // saca otra carta, para evitar una cadena infinita de cartas.
    private void EjecutarCasilla(Jugador Jugador, NodoCasilla Nodo) {
        Casilla Casilla = Nodo.getCasilla();

        if (Casilla instanceof CasillaEvento) {
            if (CartaEnCurso) {
                System.out.println("Ya se aplicó una carta en este movimiento, no se saca otra");
                return;
            }
            CartaEnCurso = true;
            try {
                Casilla.ejecutar(Jugador, this);
            } finally {
                CartaEnCurso = false;   // Se libera aunque la carta falle
            }
        } else {
            Casilla.ejecutar(Jugador, this);
        }
    }

    // Guarda una transacción en el historial
    public void RegistrarTransaccion(Transaccion Transaccion) {
        Historial.agregar(Transaccion);
    }

    // Finaliza la partida
    // * pendiente: al llegar al límite de turnos falta calcular el ganador por
    // mayor patrimonio (saldo + valor de propiedades, punto 18)
    public void finalizarPartida() {
        Curso = false;
    }

    // Server (isEnCurso), no cambiar
    public boolean isEnCurso() {
        return Curso;
    }

    // Server (buscarJugadorPorIdentificador), no cambiar.
    // Server la usa para asociar la conexión de un cliente con su Jugador,
    // según el identificador que envía en la solicitud CONECTAR;ID.
    // No es un stub: ya funciona completo, usando solo ColaCircular (lista).
    // Recorre el círculo exactamente Tamaño() veces, así que al terminar
    // el puntero interno de ColaCircular vuelve a la misma posición de
    // antes de buscar, sin afectar de quién es el turno actual.
    public Jugador buscarJugadorPorIdentificador(String identificador) {
        if (Jugadores.Vacio()) {
            return null;
        }
        Jugador encontrado = null;
        int totalJugadores = Jugadores.Tamaño();
        for (int i = 0; i < totalJugadores; i++) {
            Jugador candidato = Jugadores.Avanzar();
            if (candidato != null && candidato.getIdentificador().equals(identificador)) {
                encontrado = candidato;
            }
        }
        return encontrado;
    }

    // Server (obtenerPropiedadActual), no cambiar.
    // * pendiente: depende de que Propiedad se coloque en el Tablero
    public Propiedad obtenerPropiedadActual(Jugador jugador) {
        return null; // TODO: implementar cuando Tablero tenga casillas Propiedad
    }

    // Server (comprarPropiedad), no cambiar.
    // * pendiente: depende de Propiedad y del saldo inicial del Banco
    public void comprarPropiedad(Jugador jugador, Propiedad propiedad) {
        // TODO: implementar cuando Propiedad y Banco estén listos
    }

    public int getNumTurno() {
        return NumTurno;
    }

    public int getMaxTurnos() {
        return MaxTurnos;
    }

    // Dado ya implementada, no es dependencia pendiente
    public Dado getDado1() {
        return Dado1;
    }

    public Dado getDado2() {
        return Dado2;
    }

    public HistorialTransacciones getHistorial() {
        return Historial;
    }

    public Banco getBanco() {
        return Banco;
    }

    public Tablero getTablero() {
        return Tablero;
    }
}
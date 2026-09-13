package LogicaJuego;

/**
 * Clase Juego: Administra el estado de una partida (Turnos, tablero, dados,
 * transacciones, etc.) según el diagrama UML del Proyecto 1.
 *
 * Detalles a tener en cuenta:
 * Se coloco "*" junto a un atributo o método indica que depende de una clase que
 * todavía NO está incorporada de forma definitiva al proyecto (Banco,
 * Propiedad, HistorialTransacciones, Transaccion). Se trabaja con
 * versiones mínimas/temporales de esas clases mientras se completan;
 * cuando eso pase, solo hay que reemplazarlas (sin tocar la lógica de
 * Juego), siempre que mantengan los mismos nombres de método usados aquí.
 *
 * Nota sobre dependencias externas:
 * - Jugador es una clase aparte que ya está implementada
 * - Server (el "Servidor" del diagrama, paquete red) también es una
 *   clase aparte ya implementada; es quien le hace las solicitudes a Juego.
 * - "Banco" representa, por ahora, un rol interno temporal mientras se
 *   define su relación final con Server.
 */

public class Juego {

    private ColaCircular<Jugador> Jugadores;   // Cola circular propia; "Jugador"
    private Jugador TurnoActual;               // Referencia al Jugador con el turno actual
    private int NumTurno;                      // Número de turno actual de la partida
    private boolean Curso;                     // Indica si la partida está activa
    private int MaxTurnos;                     // Límite de turnos configurable
    private boolean DadosLanzados;             // Indica si ya se lanzaron los dados en este turno (evita lanzar dos veces)
    private Tablero Tablero;                   // Estructura del tablero (24 casillas enlazadas en circulo doble)
    private Banco Banco;                       // * pendiente: definir relación final con Server
    private Dado Dado1;                        // Dado ya implementada; el módulo RFID real (punto 14) podría integrarse más adelante
    private Dado Dado2;                        // Dado ya implementada; el módulo RFID real (punto 14) podría integrarse más adelante
    private HistorialTransacciones Historial;  // * pendiente: implementación final del historial

    // Constructor: Crea la partida y sus dependencias (varias aún temporales, ver "*")
    public Juego(int MaxTurnos) {
        this.Jugadores = new ColaCircular<>();
        this.NumTurno = 0;
        this.Curso = false;
        this.MaxTurnos = MaxTurnos;
        this.DadosLanzados = false;
        this.Tablero = new Tablero();
        this.Banco = new Banco();                        // *
        this.Dado1 = new Dado(1);
        this.Dado2 = new Dado(2);
        this.Historial = new HistorialTransacciones();   // *
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

        // Salta a los jugadores AFK o eliminados, hasta encontrar uno activo
        while (Sig != null && !Sig.esActivo() && Intentos > 0) {
            Sig = Jugadores.Avanzar();
            Intentos--;
        }

        TurnoActual = Sig;
        NumTurno++;

        if (MaxTurnos > 0 && NumTurno > MaxTurnos) {
            FinalizarPartida();
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
        int Valor1 = Dado1.Lanzar();
        int Valor2 = Dado2.Lanzar();
        DadosLanzados = true;
        return Valor1 + Valor2;
    }

    // Server (getDadosLanzadosEsteTurno), no cambiar.
    // Server lo usa para impedir lanzar los dados dos veces en el mismo turno
    public boolean getDadosLanzadosEsteTurno() {
        return DadosLanzados;
    }

    // Mueve a un Jugador recorriendo los nodos reales del Tablero en vez
    // de solo calcular la posición con una fórmula.
    // Actualiza la posición (int) de Jugador según a dónde llega, para no
    // romper la forma en que Jugador (clase aparte) guarda su posición,
    // y muestra en pantalla a qué casilla llegó.
    public void MoverJugador(Jugador Jugador, int Pos) {
        NodoCasilla NodoActual = Tablero.ObtenerNodoActual(Jugador);
        NodoCasilla NodoDestino = Tablero.ObtenerSiguiente(NodoActual, Pos);

        int NuevaPos = (Jugador.getPosicionActual() + Pos) % Tablero.getNumeroCasillas();
        Jugador.setPosicionActual(NuevaPos);

        // Por ahora se imprime en consola; cuando Casilla tenga Ejecutar() real (Par 2),
        // aquí también se llamaría NodoDestino.getCasilla().Ejecutar(Jugador, this).
        System.out.println(Jugador.getNombre() + " Se mueve a: " + NodoDestino.getCasilla().getNombre());
    }

    // * Guarda una transacción en el historial (depende de Transaccion e HistorialTransacciones, aún pendientes)
    public void RegistrarTransaccion(Transaccion Transaccion) {
        Historial.Agregar(Transaccion);
    }

    // Finaliza la partida
    // Al llegar al límite de turnos falta calcular el ganador por mayor patrimonio
    public void FinalizarPartida() {
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
    // * pendiente: depende de Tablero y Propiedad (aún no implementadas de verdad)
    public Propiedad obtenerPropiedadActual(Jugador jugador) {
        return null; // TODO: implementar cuando Tablero/Propiedad estén listas
    }

    // Server (comprarPropiedad), no cambiar.
    // * pendiente: depende de Propiedad y del manejo de saldo/Banco (aún no implementados de verdad)
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

    // * getter de dependencia aún temporal (HistorialTransacciones)
    public HistorialTransacciones getHistorial() {
        return Historial;
    }
}
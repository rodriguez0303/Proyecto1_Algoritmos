package src;

/**
 * Clase Juego: administra el estado de una partida (turnos, tablero, dados
 * transacciones, etc.) según el diagrama UML del Proyecto 1.
 *
 * Detalles a tener en cuenta:
 * "*" junto a un atributo o método indica que depende de una clase que
 * todavía NO está incorporada de forma definitiva al proyecto (Tablero,
 * Banco, Dado, HistorialTransacciones, Transaccion). Se trabaja con
 * versiones mínimas/temporales de esas clases mientras se reune lo necesario
 * para incorporarlo; cuando eso pase, solo hay que reemplazarlas
 * (sin tocar la lógica de Juego), siempre que mantengan los mismos
 * nombres de método usados aquí.
 *
 * Nota sobre dependencias externas:
 * - Jugador es una clase aparte que ya está implementada (no lleva "*").
 *   Aquí solo se usa/referencia; no se modifica ni se duplica su lógica.
 * - "Banco" representa, por ahora, el rol de la clase Servidor
 *   ("Servidor (Banco/Organizador)" en el diagrama), que aún no se
 *   ha incorporado.
 */

public class Juego {

    private ColaCircular<Jugador> Jugadores;   // Cola circular propia; "Jugador2 ya está lista
    private Jugador TurnoActual;               // Referencia al "Jugador" con el turno actual
    private int NumTurno;                      // Número de turno actual de la partida
    private boolean Curso;                     // Indica si la partida está activa
    private int MaxTurnos;                     // Límite de turnos configurable
    private Tablero Tablero;                   // * Pendiente: Implementación final del tablero
    private Banco Banco;                       // * Pendiente: Se reemplaza por la clase "Servidor" real
    private Dado Dado1;                        // * Pendiente: Confirmar versión final de Dado
    private Dado Dado2;                        // * Pendiente: confirmar versión final de Dado
    private HistorialTransacciones Historial;  // * Pendiente: Implementación final del historial

    // Constructor: crea la partida y sus dependencias (varias aún temporales, ver "*")
    public Juego(int MaxTurnos) {
        this.Jugadores = new ColaCircular<>();
        this.NumTurno = 0;
        this.Curso = false;
        this.MaxTurnos = MaxTurnos;
        this.Tablero = new Tablero();                    // *
        this.Banco = new Banco();                        // *
        this.Dado1 = new Dado();                         // *
        this.Dado2 = new Dado();                         // *
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

    // Devuelve el Jugador con el turno actual
    public Jugador getTurnoActual() {
        return TurnoActual;
    }

    // Avanza al siguiente jugador activo y controla si se llegó al límite de turnos
    public void SigTurno() {
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

    // * Lanza los dos dados y devuelve la suma (depende de Dado, aún pendiente)
    public int LanzarDados() {
        int Valor1 = Dado1.Lanzar();
        int Valor2 = Dado2.Lanzar();
        return Valor1 + Valor2;
    }

    // * Mueve a un Jugador según los pasos dados (depende de Tablero, aún pendiente)
    public void MoverJugador(Jugador Jugador, int Pos) {
        int NuevaPos = Tablero.CalcNuevaPos(Jugador.getPosicionActual(), Pos);
        Jugador.setPosicionActual(NuevaPos);
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

    // Indica si la partida sigue activa
    public boolean PartidaCurso() {
        return Curso;
    }

    public int getNumTurno() {
        return NumTurno;
    }

    public int getMaxTurnos() {
        return MaxTurnos;
    }

    // * Getters de dependencias aún temporales
    public Dado getDado1() {
        return Dado1;
    }

    public Dado getDado2() {
        return Dado2;
    }

    public HistorialTransacciones getHistorial() {
        return Historial;
    }
}
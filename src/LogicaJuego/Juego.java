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
    private int NumTurno;                      // Número de turno actual (se usa en las transacciones)
    private int NumRonda;                      // Número de ronda actual (una ronda = todos los jugadores participaron una vez)
    private int PosicionCola;                  // Posición del jugador actual dentro de la cola (0 .. Tamaño-1), para detectar fin de ronda
    private boolean Curso;                     // Indica si la partida está activa
    private int MaxRondas;                     // Límite de rondas (§18); 0 = sin límite (termina cuando queda un jugador activo)
    private Jugador Ganador;                   // Ganador de la partida (null mientras siga en curso o si hubo empate)
    private Jugador[] Empatados;               // Jugadores empatados en el mayor patrimonio (null si no hubo empate)
    private int ContadorTransacciones;         // Contador único para los IDs de transacción (T1, T2, T3...)
    private boolean DadosLanzados;             // Indica si ya se lanzaron los dados en este turno (evita lanzar dos veces)
    private Tablero Tablero;                   // Estructura del tablero (24 casillas enlazadas en circulo doble)
    private Banco Banco;                       // * pendiente: relación final con Server
    private Dado Dado1;                        // Dado ya implementada; el módulo RFID real (punto 14) podría integrarse más adelante
    private Dado Dado2;                        // Dado ya implementada; el módulo RFID real (punto 14) podría integrarse más adelante
    private HistorialTransacciones Historial;  // * pendiente: implementación final del historial
    private boolean CartaEnCurso;              // Evita que una carta que mueve al jugador dispare otra carta en cadena

    // Constructor: Crea la partida y sus dependencias.
    // La partida termina de dos formas (la que pase primero):
    // - Queda un solo jugador activo (siempre aplica).
    // - Se completan MaxRondas rondas (solo si MaxRondas > 0; con 0 las rondas son indefinidas).
    public Juego(int MaxRondas) {
        this.Jugadores = new ColaCircular<>();
        this.NumTurno = 0;
        this.NumRonda = 0;
        this.PosicionCola = 0;
        this.Curso = false;
        this.MaxRondas = MaxRondas;
        this.Ganador = null;
        this.Empatados = null;
        this.ContadorTransacciones = 0;
        this.DadosLanzados = false;
        this.Tablero = new Tablero();
        this.Banco = new Banco(Constantes.SALDO_INICIAL_BANCO);
        this.Dado1 = new Dado(1);
        this.Dado2 = new Dado(2);
        this.Historial = new HistorialTransacciones();
        this.CartaEnCurso = false;
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
        NumRonda = 1;
        PosicionCola = 0;
        TurnoActual = Jugadores.ObtenerActual();
    }

    // Server (obtenerJugadorActual), no cambiar
    // Obtiene al jugador actual
    public Jugador obtenerJugadorActual() {
        return TurnoActual;
    }

    // Avanza al siguiente jugador activo y controla si la partida terminó
    // (queda un solo jugador activo, o se completó la última ronda).
    public void SiguienteTurno() {
        if (!Curso) {
            return;
        }

        if (ContarJugadoresActivos() <= 1) {
            finalizarPartida();
            return;
        }

        Jugador Sig = AvanzarCola();
        if (!Curso) {
            return;   // Se completó la última ronda
        }
        // El ciclo se acota a dos vueltas: si todos los activos tenían un turno
        // perdido, en la segunda vuelta ya lo consumieron y alguno juega.
        int Intentos = Jugadores.Tamaño() * 2;

        // Salta a los jugadores AFK o eliminados, y a los que deben perder un turno
        // (carta PERDER_TURNO o "Ir al D3"). El turno perdido cuenta como su
        // participación en la ronda.
        while (Sig != null && Intentos > 0 && (!Sig.esActivo() || Sig.DebePerderTurno())) {
            if (Sig.esActivo()) {
                Sig.ConsumirTurnoPerdido();
                System.out.println(Sig.getNombre() + " pierde este turno");
            }
            Sig = AvanzarCola();
            if (!Curso) {
                return;   // Se completó la última ronda
            }
            Intentos--;
        }

        TurnoActual = Sig;
        NumTurno++;
    }

    // Avanza la cola de turnos una posición. Cuando la cola da la vuelta
    // completa (vuelve a la posición 0) todos los jugadores ya tiraron los dados
    // y jugaron su turno: termina la ronda y empieza la siguiente.
    // Si ya se completó la última ronda permitida, se finaliza la partida.
    private Jugador AvanzarCola() {
        Jugador Sig = Jugadores.Avanzar();
        PosicionCola = (PosicionCola + 1) % Jugadores.Tamaño();

        if (PosicionCola == 0) {
            NumRonda++;
            if (MaxRondas > 0 && NumRonda > MaxRondas) {
                NumRonda = MaxRondas;   // Se deja en la última ronda jugada
                finalizarPartida();
            }
        }
        return Sig;
    }

    // Cuenta cuántos jugadores siguen activos. Recorre la cola exactamente
    // Tamaño() veces, así que el puntero interno vuelve a donde estaba.
    private int ContarJugadoresActivos() {
        int Activos = 0;
        int Total = Jugadores.Tamaño();
        for (int i = 0; i < Total; i++) {
            Jugador J = Jugadores.Avanzar();
            if (J != null && J.esActivo()) {
                Activos++;
            }
        }
        return Activos;
    }

    // Revisa si queda un único jugador activo y, si es así, finaliza la partida (punto 18).
    // Se llama justo después de cada eliminación, sin esperar al siguiente SiguienteTurno().
    // Devuelve true si la partida ya terminó (por esta revisión o desde antes).
    public boolean RevisarFinPartida() {
        if (Curso && ContarJugadoresActivos() <= 1) {
            finalizarPartida();
        }
        return !Curso;
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

    public int registrarDadosFisicos(int valorDado1, int valorDado2) {
        Dado1.establecerValor(valorDado1);
        Dado2.establecerValor(valorDado2);

        DadosLanzados = true;

        return valorDado1 + valorDado2;
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

        // Si la suma llega o pasa del número de casillas, el jugador pasó (o cayó) en Salida
        boolean PasoPorSalida = Jugador.getPosicionActual() + Pos >= Tablero.getNumeroCasillas();
        Jugador.setPosicionActual(AjustarPosicion(Jugador.getPosicionActual() + Pos));

        System.out.println(Jugador.getNombre() + " Se mueve a: " + NodoDestino.getCasilla().getNombre());
        if (PasoPorSalida) {
            PagarPremioSalida(Jugador);
        }
        EjecutarCasilla(Jugador, NodoDestino);
    }

    // El Banco le paga al Jugador el premio por pasar por Salida (punto 11)
    // y se registra una transacción PREMIO_POR_INICIO.
    private void PagarPremioSalida(Jugador Jugador) {
        if (Banco.pagar(Jugador, Constantes.PREMIO_SALIDA)) {
            RegistrarTransaccion(new Transaccion(
                    GenerarIdTransaccion(),
                    NumTurno,
                    TipoTransaccion.PREMIO_POR_INICIO,
                    "BANCO",
                    Jugador.getIdentificador(),
                    Constantes.PREMIO_SALIDA,
                    "Premio por pasar por Salida"));
            System.out.println(Jugador.getNombre() + " recibe " + Constantes.PREMIO_SALIDA + " por pasar por Salida");
        }
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

    // Encierra a un Jugador en el D3 (lo usan la casilla "Ir al D3" y la carta IR_AL_D3).
    // Va directo sin pasar por Salida (no cobra premio) y pierde su próximo turno.
    // Si tiene guardada una carta "Salida libre del D3", la usa y no le pasa nada.
    public void EnviarAlD3(Jugador Jugador) {
        if (Jugador.UsarCartaSalidaD3()) {
            System.out.println(Jugador.getNombre() + " usa su carta \"Salida libre del D3\" y se libra del encierro");
            return;
        }
        Jugador.setPosicionActual(Constantes.POSICION_D3);
        Jugador.PerderTurno();
        System.out.println(Jugador.getNombre() + " va directo al D3 y pierde su próximo turno");
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

    // Genera el ID de la siguiente transacción: T1, T2, T3...
    // Es el único lugar donde se crean IDs (Propiedad, CartaEvento y Juego lo usan),
    // así nunca se repiten aunque una propiedad se libere y se vuelva a comprar.
    public String GenerarIdTransaccion() {
        ContadorTransacciones++;
        return "T" + ContadorTransacciones;
    }

    // Finaliza la partida y define al ganador (punto 18):
    // - Si queda un solo jugador activo, ese es el ganador (modo normal).
    // - Si no (se completaron las rondas), gana el jugador activo con mayor patrimonio.
    // - Si dos o más jugadores comparten el mayor patrimonio, la partida termina
    //   en empate: no hay Ganador y los empatados quedan en GetEmpatados().
    //   Así el resultado no depende de a quién le tocaba el turno al terminar.
    public void finalizarPartida() {
        if (!Curso) {
            return;
        }
        Curso = false;

        Ganador = null;
        Empatados = null;
        int Total = Jugadores.Tamaño();

        // Primera vuelta: mayor patrimonio entre los activos y cuántos lo tienen
        double MayorPatrimonio = 0;
        int CantidadConMayor = 0;
        for (int i = 0; i < Total; i++) {
            Jugador J = Jugadores.Avanzar();
            if (J == null || !J.esActivo()) {
                continue;
            }
            double Patrimonio = J.CalcularPatrimonio();
            if (CantidadConMayor == 0 || Patrimonio > MayorPatrimonio) {
                MayorPatrimonio = Patrimonio;
                CantidadConMayor = 1;
            } else if (Patrimonio == MayorPatrimonio) {
                CantidadConMayor++;
            }
        }

        // Segunda vuelta: se guardan los que tienen ese patrimonio
        Jugador[] ConMayor = new Jugador[CantidadConMayor];
        int Indice = 0;
        for (int i = 0; i < Total; i++) {
            Jugador J = Jugadores.Avanzar();
            if (J != null && J.esActivo() && J.CalcularPatrimonio() == MayorPatrimonio) {
                ConMayor[Indice++] = J;
            }
        }

        if (CantidadConMayor == 1) {
            Ganador = ConMayor[0];
            System.out.println("Partida finalizada. Ganador: " + Ganador.getNombre()
                    + " con un patrimonio de " + MayorPatrimonio);
        } else if (CantidadConMayor > 1) {
            Empatados = ConMayor;
            String Nombres = Empatados[0].getNombre();
            for (int i = 1; i < Empatados.length; i++) {
                Nombres += ", " + Empatados[i].getNombre();
            }
            System.out.println("Partida finalizada en empate entre " + Nombres
                    + " con un patrimonio de " + MayorPatrimonio);
        } else {
            System.out.println("Partida finalizada sin ganador");
        }
    }

    // Ganador de la partida; null mientras siga en curso o si terminó en empate
    public Jugador GetGanador() {
        return Ganador;
    }

    // Indica si la partida terminó en empate por patrimonio
    public boolean HayEmpate() {
        return Empatados != null;
    }

    // Jugadores empatados en el mayor patrimonio; null si no hubo empate
    public Jugador[] GetEmpatados() {
        return Empatados;
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
    // Devuelve la Propiedad donde está parado el jugador, o null si esa casilla
    // no es una propiedad (Evento, Salida, D3, etc.)
    public Propiedad obtenerPropiedadActual(Jugador jugador) {
        if (jugador == null) {
            return null;
        }
        Casilla Casilla = Tablero.ObtenerCasillaActual(jugador);
        if (Casilla instanceof Propiedad) {
            return (Propiedad) Casilla;
        }
        return null;
    }

    // Server (comprarPropiedad), no cambiar.
    // Valida que la compra tenga sentido y delega en Propiedad.comprar(),
    // que cobra con el Banco y registra la transacción COMPRA_PROPIEDAD.
    public void comprarPropiedad(Jugador jugador, Propiedad propiedad) {
        if (!Curso || jugador == null || propiedad == null) {
            System.out.println("No se puede comprar: partida no activa o datos inválidos");
            return;
        }
        if (jugador != TurnoActual) {
            System.out.println("No se puede comprar: no es el turno de " + jugador.getNombre());
            return;
        }
        if (propiedad != obtenerPropiedadActual(jugador)) {
            System.out.println("No se puede comprar: " + jugador.getNombre() + " no está en " + propiedad.getNombre());
            return;
        }

        if (propiedad.comprar(jugador, this)) {
            System.out.println(jugador.getNombre() + " compró " + propiedad.getNombre() + " por " + propiedad.getPrecioCompra());
        } else {
            System.out.println(jugador.getNombre() + " no pudo comprar " + propiedad.getNombre() + " (ya tiene dueño o no le alcanza el saldo)");
        }
    }

    public int getNumTurno() {
        return NumTurno;
    }

    public int GetNumRonda() {
        return NumRonda;
    }

    public int GetMaxRondas() {
        return MaxRondas;
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
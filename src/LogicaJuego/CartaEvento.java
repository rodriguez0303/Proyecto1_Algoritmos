package LogicaJuego;

/**
 * Clase CartaEvento: Representa una carta que se saca al caer en una
 * CasillaEvento y aplica su efecto sobre el jugador.
 *
 * Las cartas viven en un mazo (ColaCircular<CartaEvento> dentro de Juego):
 * al usarse una carta, el mazo avanza y la carta usada queda al final para
 * poder reutilizarse más adelante.
 *
 * Los IDs de las transacciones se piden a Juego.GenerarIdTransaccion(),
 * para que sean únicos en toda la partida.
 */

public class CartaEvento {

    private String Id;              // Identificador de la carta
    private String Descripcion;     // Texto que se le muestra a los jugadores
    private TipoEvento Tipo;        // Qué hace la carta (ver TipoEvento)
    private int Valor;              // Monto, cantidad de casillas o número de casilla, según el tipo

    // Constructor: Crea la carta con todos sus datos.
    public CartaEvento(String Id, String Descripcion, TipoEvento Tipo, int Valor) {
        this.Id = Id;
        this.Descripcion = Descripcion;
        this.Tipo = Tipo;
        this.Valor = Valor;
    }

    // Aplica el efecto de la carta sobre el Jugador que la sacó.
    // Lo llama CasillaEvento.ejecutar() después de sacar la carta del mazo.
    public void Aplicar(Jugador Jugador, Juego Juego) {
        System.out.println(Jugador.getNombre() + " saca la carta " + Id + ": " + Descripcion);

        switch (Tipo) {
            case RECIBIR_DINERO:
                AplicarGanancia(Jugador, Juego);
                break;

            case PAGAR_DINERO:
                AplicarPerdida(Jugador, Juego);
                break;

            case AVANZAR:
                Juego.MoverJugador(Jugador, Valor);
                break;

            case RETROCEDER:
                Juego.RetrocederJugador(Jugador, Valor);
                break;

            case PERDER_TURNO:
                // Juego.SiguienteTurno() salta al jugador la próxima vez que le toque
                Jugador.PerderTurno();
                System.out.println(Jugador.getNombre() + " pierde su próximo turno");
                break;

            case IR_A_CASILLA:
                Juego.MoverJugadorA(Jugador, Valor);
                break;

        }
    }

    // El banco le paga al Jugador y se registra una transacción GANANCIA_EVENTO.
    // Si el banco no tiene saldo suficiente, no se registra nada.
    private void AplicarGanancia(Jugador Jugador, Juego Juego) {
        if (Juego.getBanco().pagar(Jugador, Valor)) {
            Juego.RegistrarTransaccion(new Transaccion(
                    Juego.GenerarIdTransaccion(),
                    Juego.getNumTurno(),
                    TipoTransaccion.GANANCIA_EVENTO,
                    "BANCO",                                  // Origen: el banco
                    Jugador.getIdentificador(),               // Destino: el jugador
                    Valor,
                    "Carta " + Id + ": " + Descripcion));
        }
    }

    // El Jugador le paga al banco y se registra una transacción PERDIDA_EVENTO.
    // Si no le alcanza el saldo, queda eliminado.
    // Banco.recibir() ya valida el saldo, por eso no se repite la validación aquí.
    private void AplicarPerdida(Jugador Jugador, Juego Juego) {
        if (Juego.getBanco().recibir(Jugador, Valor)) {
            Juego.RegistrarTransaccion(new Transaccion(
                    Juego.GenerarIdTransaccion(),
                    Juego.getNumTurno(),
                    TipoTransaccion.PERDIDA_EVENTO,
                    Jugador.getIdentificador(),               // Origen: el jugador
                    "BANCO",                                  // Destino: el banco
                    Valor,
                    "Carta " + Id + ": " + Descripcion));
        } else {
            System.out.println(Jugador.getNombre() + " no puede pagar " + Valor + " y queda eliminado");
            Jugador.eliminar();
        }
    }

    public String getId() {
        return Id;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public TipoEvento getTipo() {
        return Tipo;
    }

    public int getValor() {
        return Valor;
    }

    // Resumen de la carta en una línea (útil para pruebas y para mostrarla en consola)
    @Override
    public String toString() {
        return Id + " | " + Tipo + " | " + Valor + " | " + Descripcion;
    }
}
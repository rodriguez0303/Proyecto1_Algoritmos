package LogicaJuego;

/**
 * Clase CartaEvento: Representa una carta que se saca al caer en una
 * CasillaEvento y aplica su efecto sobre el jugador.
 *
 * Las cartas viven en un mazo (ColaCircular<CartaEvento> dentro de Juego):
 * al usarse una carta, el mazo avanza y la carta usada queda al final para
 * poder reutilizarse más adelante.
 *
 * Detalles a tener en cuenta:
 * Se coloco "*", indica que depende de un ajuste
 * que todavía no está hecho en otra clase (Juego, Tablero, Jugador o
 * Transaccion). Esas líneas quedan comentadas para que el proyecto compile;
 * cuando el ajuste esté listo, solo hay que descomentarlas.
 *
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
        System.out.println(Jugador.getNombre() + " Saca la carta " + Id + ": " + Descripcion);

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
                // * pendiente: crear Juego.RetrocederJugador() y Tablero.ObtenerAnterior()
                // Juego.RetrocederJugador(Jugador, Valor);
                System.out.println("Pendiente: retroceder " + Valor + " casillas");
                break;

            case PERDER_TURNO:
                // * pendiente: agregar perderTurno() en Jugador y el salto en Juego.SiguienteTurno()
                // Jugador.perderTurno();
                System.out.println("Pendiente: perder un turno");
                break;

            case IR_A_CASILLA:
                // * pendiente: crear Juego.MoverJugadorA()
                // Juego.MoverJugadorA(Jugador, Valor);
                System.out.println("Pendiente: ir a la casilla " + Valor);
                break;
        }
    }

    // El banco le paga al Jugador y se registra una transacción GANANCIA_EVENTO.
    // Si el banco no tiene saldo suficiente, no se registra nada.
    private void AplicarGanancia(Jugador Jugador, Juego Juego) {
        if (Juego.getBanco().pagar(Jugador, Valor)) {
            Juego.RegistrarTransaccion(new Transaccion(
                    SiguienteId(Juego),
                    Juego.getNumTurno(),
                    TipoTransaccion.GANANCIA_EVENTO.name(),   // * cuando Transaccion use el enum, quitar .name()
                    "BANCO",                                  // Origen: el banco
                    Jugador.getIdentificador(),               // Destino: el jugador
                    Valor,
                    "Carta " + Id + ": " + Descripcion));
        }
    }

    // El Jugador le paga al banco y se registra una transacción PERDIDA_EVENTO.
    // Si no le alcanza el saldo, queda eliminado (puntos 17 y 18 del enunciado).
    // Banco.recibir() ya valida el saldo, por eso no se repite la validación aquí.
    private void AplicarPerdida(Jugador Jugador, Juego Juego) {
        if (Juego.getBanco().recibir(Jugador, Valor)) {
            Juego.RegistrarTransaccion(new Transaccion(
                    SiguienteId(Juego),
                    Juego.getNumTurno(),
                    TipoTransaccion.PERDIDA_EVENTO.name(),    // * cuando Transaccion use el enum, quitar .name()
                    Jugador.getIdentificador(),               // Origen: el jugador
                    "BANCO",                                  // Destino: el banco
                    Valor,
                    "Carta " + Id + ": " + Descripcion));
        } else {
            System.out.println(Jugador.getNombre() + " no puede pagar " + Valor + " y queda eliminado");
            Jugador.eliminar();
        }
    }

    // Genera el número correlativo de la siguiente transacción: T1, T2, T3...
    // (el punto 13 pide "número de transacción" en el reporte)
    private String SiguienteId(Juego Juego) {
        return "T" + (Juego.getHistorial().getTamaño() + 1);
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
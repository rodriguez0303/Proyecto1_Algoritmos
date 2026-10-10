package LogicaJuego;

/**
 * TipoTransaccion: Tipos mínimos de transacción
 *
 * Nota sobre quién usa cada tipo:
 * - COMPRA_PROPIEDAD, PAGO_ALQUILER -> Propiedad
 * - GANANCIA_EVENTO, PERDIDA_EVENTO -> CartaEvento
 * - PAGO_BANCO, PAGO_ENTRE_JUGADORES, PREMIO_POR_INICIO -> CasillaEspecial / Juego
 */

public enum TipoTransaccion {
    COMPRA_PROPIEDAD,       // Un jugador le compra una propiedad al banco
    PAGO_ALQUILER,          // Un jugador le paga alquiler al dueño de la propiedad
    PAGO_BANCO,             // Un jugador le paga al banco (impuestos, multas, etc.)
                            // * Por ahora ninguna parte del juego lo registra (reservado para el futuro).
                            //   Las cartas de pago (matrícula, Uber, multa) se registran como PERDIDA_EVENTO.
    PAGO_ENTRE_JUGADORES,   // Transferencia directa entre dos jugadores
                            // * Por ahora ninguna parte del juego lo registra (reservado para el futuro).
                            //   El pago de alquiler entre jugadores se registra como PAGO_ALQUILER.
    GANANCIA_EVENTO,        // Una carta de evento hace que el banco le pague al jugador
    PERDIDA_EVENTO,         // Una carta de evento hace que el jugador le pague al banco
    PREMIO_POR_INICIO;      // El banco le paga al jugador por pasar por la casilla de inicio

    // Nombre fácil de leer para el TXT y la pantalla final: COMPRA_PROPIEDAD -> "Compra propiedad".
    /**
     * Devuelve el nombre legible asociado al tipo de transacción.
     */
    public String getNombre() {
        String Texto = name().replace('_', ' ').toLowerCase();
        return Character.toUpperCase(Texto.charAt(0)) + Texto.substring(1);
    }
}
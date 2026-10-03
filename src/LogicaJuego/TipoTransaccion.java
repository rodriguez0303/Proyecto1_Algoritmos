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
    PAGO_ENTRE_JUGADORES,   // Transferencia directa entre dos jugadores
    GANANCIA_EVENTO,        // Una carta de evento hace que el banco le pague al jugador
    PERDIDA_EVENTO,         // Una carta de evento hace que el jugador le pague al banco
    PREMIO_POR_INICIO       // El banco le paga al jugador por pasar por la casilla de inicio
}
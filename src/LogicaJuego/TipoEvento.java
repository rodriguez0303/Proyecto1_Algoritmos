package LogicaJuego;

/**
 * TipoEvento: Tipos de carta de evento
 *
 * Detalles a tener en cuenta:
 * Cada CartaEvento tiene un "Valor" (int) cuyo significado depende del tipo:
 * - RECIBIR_DINERO / PAGAR_DINERO -> Monto de dinero
 * - AVANZAR / RETROCEDER          -> Cantidad de casillas
 * - IR_A_CASILLA                  -> Número de la casilla destino
 * - PERDER_TURNO                  -> No se usa (puede ir en 0)
 * - IR_AL_D3 / SALIDA_LIBRE_D3     -> No se usa (puede ir en 0)
 */

public enum TipoEvento {
    RECIBIR_DINERO,   // El banco le paga al jugador
    PAGAR_DINERO,     // El jugador le paga al banco
    AVANZAR,          // El jugador avanza N casillas
    RETROCEDER,       // El jugador retrocede N casillas
    PERDER_TURNO,     // El jugador se salta su próximo turno
    IR_A_CASILLA,     // El jugador va directo a una casilla determinada (sin perder turno)
    IR_AL_D3,         // Encierran al jugador en el D3: va directo y pierde su próximo turno
    SALIDA_LIBRE_D3   // El jugador guarda la carta y la usa si lo mandan al D3
}
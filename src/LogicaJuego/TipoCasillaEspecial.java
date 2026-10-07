package LogicaJuego;

/**
 * TipoCasillaEspecial: Tipos de las esquinas del tablero
 *
 * Detalles a tener en cuenta:
 * Cada tipo va en una posición fija (ver Constantes):
 * - SALIDA     -> 0
 * - VISITA_D3  -> 6
 * - ESPECIAL   -> 12
 * - IR_AL_D3   -> 18
 */

public enum TipoCasillaEspecial {
    SALIDA,       // Inicio del tablero; el premio lo paga Juego al pasar o caer aquí
    VISITA_D3,    // Edificio D3: solo de visita, no pasa nada
    ESPECIAL,     // Casilla especial: por ahora no tiene efecto (igual que la GUI)
    IR_AL_D3      // El jugador va directo al D3 y pierde su próximo turno
}

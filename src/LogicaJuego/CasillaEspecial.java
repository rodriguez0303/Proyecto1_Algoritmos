package LogicaJuego;

/**
 * Clase CasillaEspecial: Representa las esquinas del tablero (Salida,
 * Edificio D3, Especial e Ir al D3).
 *
 * Hereda de Casilla y sobrescribe ejecutar(), igual que Propiedad y
 * CasillaEvento: Juego no necesita saber qué tipo de casilla es.
 */

public class CasillaEspecial extends Casilla {

    private TipoCasillaEspecial Tipo;   // Qué hace la casilla (ver TipoCasillaEspecial)

    // Constructor: Crea la casilla con su nombre y su tipo.
    /**
     * Inicializa una casilla especial con su nombre y su categoría.
     */
    public CasillaEspecial(String Nombre, TipoCasillaEspecial Tipo) {
        super(Nombre);
        this.Tipo = Tipo;
    }

    // Se llama cuando un Jugador cae en esta casilla.
    @Override
    /**
     * Aplica la acción de la casilla especial al jugador que cayó en ella.
     */
    public void ejecutar(Jugador Jugador, Juego Juego) {
        switch (Tipo) {
            case SALIDA:
                // El premio ya lo pagó Juego.MoverJugador() al pasar por aquí
                System.out.println(Jugador.getNombre() + " está en la Salida");
                break;

            case VISITA_D3:
                System.out.println(Jugador.getNombre() + " está de visita en el D3");
                break;

            case ESPECIAL:
                System.out.println(Jugador.getNombre() + " cayó en una casilla especial");
                break;

            case IR_AL_D3:
                // Va directo al D3 y pierde su próximo turno, salvo que tenga la carta "Salida libre del D3"
                Juego.EnviarAlD3(Jugador);
                break;
        }
    }

    /**
     * Devuelve la categoría que identifica la casilla especial.
     */
    public TipoCasillaEspecial GetTipo() {
        return Tipo;
    }
}

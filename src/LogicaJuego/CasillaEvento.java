package LogicaJuego;

/**
 * Clase CasillaEvento: Casilla del tablero que, cuando un jugador cae en
 * ella, saca una carta del mazo del Juego y aplica su efecto.
 *
 * Hereda de Casilla y sobrescribe ejecutar(), Juego no
 * necesita saber qué tipo de casilla es, solo llama a ejecutar().
 *
 * Nota sobre el diagrama:
 * En vez de tener una sola carta fija por casilla, el atributo Carta guarda
 * la ultima carta sacada aquí. El mazo real está en Juego, así se cumple que
 * "la carta usada pasa al final para reutilizarse" (punto 10) del pdf.
 */

public class CasillaEvento extends Casilla {

    private CartaEvento Carta;   // Última carta sacada en esta casilla (null si nadie ha caído aún)

    // Constructor: Crea la casilla con su nombre.
    public CasillaEvento(String Nombre) {
        super(Nombre);
    }

    // Casilla (ejecutar), no cambiar.
    // Se llama cuando un Jugador cae en esta casilla: saca una carta y la aplica.
    // @Override hace que el compilador avise si el nombre no coincide con el de Casilla.
    @Override
    public void ejecutar(Jugador Jugador, Juego Juego) {
        Carta = Juego.SacarCarta();

        if (Carta != null) {
            Carta.Aplicar(Jugador, Juego);
        } else {
            System.out.println(Jugador.getNombre() + " cae en " + getNombre() + " (mazo pendiente)");
        }
    }

    // Devuelve la última carta sacada aquí.
    // Server la puede usar para avisar a los clientes qué carta salió.
    public CartaEvento ObtenerCarta() {
        return Carta;
    }
}

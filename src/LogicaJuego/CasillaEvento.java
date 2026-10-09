package LogicaJuego;

/**
 * Clase CasillaEvento: Casillas del centro de cada lado del tablero
 * (posiciones 3, 9, 15 y 21). Cuando un jugador cae en una de ellas,
 * saca una carta del mazo de eventos y aplica su efecto.
 *
 * Hereda de Casilla y sobrescribe ejecutar(), Juego no
 * necesita saber qué tipo de casilla es, solo llama a ejecutar().
 *
 * El mazo lo crea Tablero una sola vez y se lo entrega a las 4 casillas
 * de evento: todas sacan del mismo mazo, así se cumple que
 * "la carta usada pasa al final para reutilizarse" (punto 10) del pdf.
 *
 * Nota sobre el diagrama:
 * En vez de tener una sola carta fija por casilla, el atributo Carta guarda
 * la ultima carta sacada aquí.
 */

public class CasillaEvento extends Casilla {

    private MazoEventos Mazo;    // Mazo compartido por todas las casillas de evento (lo crea Tablero)
    private CartaEvento Carta;   // Última carta sacada en esta casilla (null si nadie ha caído aún)

    // Constructor: Crea la casilla con su nombre y el mazo del que va a sacar cartas.
    public CasillaEvento(String Nombre, MazoEventos Mazo) {
        super(Nombre);
        this.Mazo = Mazo;
    }

    // Casilla (ejecutar), no cambiar.
    // Se llama cuando un Jugador cae en esta casilla: saca una carta y la aplica.
    // @Override hace que el compilador avise si el nombre no coincide con el de Casilla.
    @Override
    public void ejecutar(Jugador Jugador, Juego Juego) {
        Carta = Mazo.SacarCarta();

        if (Carta != null) {
            Carta.Aplicar(Jugador, Juego);
        } else {
            System.out.println(Jugador.getNombre() + " cae en " + getNombre() + " (mazo vacío)");
        }
    }

    // Devuelve la última carta sacada aquí.
    // Server la puede usar para avisar a los clientes qué carta salió.
    public CartaEvento ObtenerCarta() {
        return Carta;
    }
}

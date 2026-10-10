// Se indica que la clase Casilla pertenece al paquete LogicaJuego.
    // Este paquete agrupa las clases que forman parte de la lógica del juego.
        // Esto permite que Casilla pueda trabajar con otras clases del mismo paquete,
package LogicaJuego;
//*******************************************************************************
//*******************************************************************************

/**
 * Clase base de las posiciones del tablero. Cada subtipo puede redefinir el efecto que ocurre al caer en la casilla.
 */
public class Casilla {
    // Atributo que guarda el nombre de la casilla.
    private String nombre;

    //*******************************************************************************
    //*******************************************************************************

    // Constructor de la clase CASILLA.
        // Recibe el nombre que tendrá la casilla.
            // El nombre recibido se guarda en el atributo "nombre" del objeto.
    /**
     * Inicializa una casilla con el nombre que mostrará el tablero.
     */
    public Casilla(String nombre) {
        this.nombre = nombre;
    }
    //*******************************************************************************
    //*******************************************************************************

    // Método que permite obtener el nombre de la casilla.
        // No recibe ningún parámetro.
            // Retorna el nombre que se encuentra guardado en el atributo "nombre".
    /**
     * Obtiene el nombre visible de la casilla.
     */
    public String getNombre() {
        return nombre;
    }

    //*******************************************************************************
    //*******************************************************************************

    // Método que representa la acción que ocurre cuando un jugador cae en esta casilla.
        // Recibe al Jugador que cayó en la propiedad
        // "Juego juego", recibe la información del juego
            // Los jugadores de la partida.
            //El jugador que tiene el turno actual.
            //El número de turno.
            //El historial de transacciones, etc.
    /**
     * Define el comportamiento base al llegar a una casilla; las subclases lo especializan.
     */
    public void ejecutar(Jugador jugador, Juego juego) {

    }
}


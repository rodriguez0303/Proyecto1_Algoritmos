// Se indica que la clase Casilla pertenece al paquete LogicaJuego.
    // Este paquete agrupa las clases que forman parte de la lógica del juego.
        // Esto permite que Casilla pueda trabajar con otras clases del mismo paquete,
package LogicaJuego;
//*******************************************************************************
//*******************************************************************************

public class Casilla {
    // Atributo que guarda el nombre de la casilla.
    private String nombre;

    //*******************************************************************************
    //*******************************************************************************

    // Constructor de la clase CASILLA.
        // Recibe el nombre que tendrá la casilla.
            // El nombre recibido se guarda en el atributo "nombre" del objeto.
    public Casilla(String nombre) {
        this.nombre = nombre;
    }
    //*******************************************************************************
    //*******************************************************************************

    // Método que permite obtener el nombre de la casilla.
        // No recibe ningún parámetro.
            // Retorna el nombre que se encuentra guardado en el atributo "nombre".
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
    public void ejecutar(Jugador jugador, Juego juego) {

    }
}


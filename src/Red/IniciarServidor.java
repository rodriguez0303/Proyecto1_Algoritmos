
package Red;

import LogicaJuego.Juego;
import LogicaJuego.Jugador;
import java.util.Scanner;

public class IniciarServidor {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        int cantidadJugadores = 0;

        // Seleccionar la cantidad de jugadores.
        while (cantidadJugadores != 3 && cantidadJugadores != 4) {

            System.out.print("Cantidad de jugadores (3 o 4): ");

            if (scanner.hasNextInt()) {
                cantidadJugadores = scanner.nextInt();
            } else {
                scanner.next();
            }
        }

        // Crear la partida con un máximo de 5 rondas.
        Juego juego = new Juego(5);

        // Registrar solamente los jugadores seleccionados.
        for (int i = 1; i <= cantidadJugadores; i++) {

            String id = String.format("J%03d", i);

            Jugador jugador = new Jugador(id, "Jugador" + i, 1500
            );

            juego.AgregarJugador(jugador);
        }

        juego.IniciarPartida();

        // Escuchar conexiones en las interfaces de red del equipo.
        Server servidor = new Server("0.0.0.0", 5000, juego, cantidadJugadores);

        servidor.iniciar();
    }
}


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

        int maxRondas = -1;

        // Seleccionar la cantidad de rondas (§18). Una ronda termina cuando todos los jugadores jugaron su turno.
            // Con 0 las rondas son indefinidas y la partida termina cuando queda un solo jugador activo.
        while (maxRondas < 0) {

            System.out.print("Cantidad de rondas (0 = indefinidas): ");

            if (scanner.hasNextInt()) {
                maxRondas = scanner.nextInt();
            } else {
                scanner.next();
            }
        }

        // Crear la partida con la cantidad de rondas seleccionada.
        Juego juego = new Juego(maxRondas);

        // Registrar solamente los jugadores seleccionados.
        for (int i = 1; i <= cantidadJugadores; i++) {

            String id = String.format("J%03d", i);

            Jugador jugador = new Jugador(id, "Jugador" + i, 1500
            );

            juego.AgregarJugador(jugador);
        }

        juego.IniciarPartida();

        // Escuchar conexiones en las interfaces de red del equipo.
        Server servidor = new Server("0.0.0.0", 5000, juego);

        servidor.iniciar();
    }
}

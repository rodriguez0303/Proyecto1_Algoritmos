package Red;

import LogicaJuego.Juego;
import LogicaJuego.Jugador;

public class IniciarServidor {
    public static void main(String[] args) {
        Juego juego = new Juego(5);
        for (int i = 1; i <= 4; i++) {
            String id = String.format("J%03d", i);

            Jugador jugador = new Jugador(id, "Jugador" + i, 1500);

            juego.AgregarJugador(jugador);
        }

        juego.IniciarPartida();

        Server servidor = new Server("172.18.141.107", 5000, juego);

        servidor.iniciar();
    }
}

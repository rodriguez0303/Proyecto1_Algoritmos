
package Red;

import Hardware.ControlDadosHardware;
import LogicaJuego.Juego;
import LogicaJuego.Jugador;

import java.io.IOException;
import java.util.Scanner;

public class IniciarServidor {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        int cantidadJugadores = 0;

        // Elegir si se usará el hardware físico.
        boolean usarHardware = args.length > 0
                && args[0].equalsIgnoreCase("hardware");

        // Seleccionar cantidad de jugadores.
        while (cantidadJugadores != 3 && cantidadJugadores != 4) {

            System.out.print("Cantidad de jugadores (3 o 4): ");

            if (scanner.hasNextInt()) {
                cantidadJugadores = scanner.nextInt();
            } else {
                scanner.next();
            }
        }

        // Crear la partida.
        Juego juego = new Juego(5);

        for (int i = 1; i <= cantidadJugadores; i++) {

            String id = String.format("J%03d", i);

            Jugador jugador = new Jugador(
                    id,
                    "Jugador" + i,
                    1500
            );

            juego.AgregarJugador(jugador);
        }

        // El recurso se cierra si el servidor termina.
        try (ControlDadosHardware hardware =
                usarHardware
                        ? new ControlDadosHardware("COM3")
                        : null) {

            if (usarHardware) {

                System.out.println(
                        "Iniciando hardware físico en COM3..."
                );

                if (!hardware.conectar()) {

                    System.out.println(
                            "No se pudo conectar con la Pico."
                    );

                    return;
                }

                System.out.println(
                        "Hardware conectado correctamente."
                );

                // Registrar las tarjetas RFID.
                for (int i = 1; i <= cantidadJugadores; i++) {

                    String id = String.format("J%03d", i);

                    System.out.println();
                    System.out.println("Registrando " + id);

                    String uid = hardware.registrarJugador(
                            id,
                            mensaje -> System.out.println(
                                    "PICO -> " + mensaje
                            )
                    );

                    System.out.println(
                            id + " registrado. UID: " + uid
                    );
                }

                System.out.println();
                System.out.println(
                        "Todas las tarjetas RFID registradas."
                );
            }

            // Crear el servidor con o sin hardware.
            Server servidor = new Server(
                    "0.0.0.0",
                    5000,
                    juego,
                    cantidadJugadores,
                    hardware
            );

            servidor.iniciar();

        } catch (IOException e) {

            System.out.println(
                    "Error al inicializar el hardware: "
                    + e.getMessage()
            );

            e.printStackTrace();
        }
    }
}

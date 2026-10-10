
package Red;

import LogicaJuego.Cliente;
import java.util.Scanner;

/**
 * Cliente de prueba por consola para enviar comandos al servidor TCP.
 * Es una herramienta auxiliar; la interfaz habitual usa VentanaInicio
 * y la clase Cliente del paquete LogicaJuego.
 */
public class PruebaCliente {

    /**
     * Conecta un jugador de prueba, imprime mensajes del servidor y permite
     * enviar solicitudes escritas en consola hasta introducir SALIR.
     *
     * @param args identificador opcional del jugador, por ejemplo J002
     */
    public static void main(String[] args) {

        String idJugador = args.length > 0
                ? args[0]
                : "J001";

        Cliente cliente = new Cliente(
                idJugador,
                "Jugador " + idJugador,
                "127.0.0.1",
                5000
        );

        if (!cliente.conectar()) {
            System.out.println("No se pudo conectar al servidor.");
            return;
        }

        // Identificar al jugador.
        cliente.enviarSolicitud("CONECTAR;" + idJugador);

        String respuesta = cliente.recibirRespuesta();
        System.out.println("Respuesta: " + respuesta);

        if (!"Conexión válida".equals(respuesta)) {
            cliente.desconectar();
            return;
        }

        // Escuchar continuamente los mensajes del servidor.
        Thread hiloReceptor = new Thread(() -> {

            while (true) {

                String mensaje = cliente.recibirRespuesta();

                if (mensaje == null) {
                    break;
                }

                if (mensaje.startsWith("ESTADO;")) {
                    cliente.mostrarEstado(mensaje);
                } else {
                    System.out.println("\nServidor: " + mensaje);
                }
            }
        });

        hiloReceptor.setDaemon(true);
        hiloReceptor.start();

        // Solicitar el estado inicial.
        cliente.enviarSolicitud("CONSULTAR_ESTADO");

        System.out.println("\nComandos disponibles:");
        System.out.println("TIRAR_DADOS");
        System.out.println("COMPRAR_PROPIEDAD");
        System.out.println("NO_COMPRAR");
        System.out.println("TERMINAR_TURNO");
        System.out.println("CONSULTAR_ESTADO");
        System.out.println("CONSULTAR_TRANSACCIONES");
        System.out.println("SALIR");

        // Leer las acciones escritas por el jugador.
        Scanner teclado = new Scanner(System.in);

        while (true) {

            System.out.print("\nComando: ");

            String comando = teclado.nextLine()
                    .trim()
                    .toUpperCase();

            if (comando.equals("SALIR")) {
                break;
            }

            if (!comando.isEmpty()) {
                cliente.enviarSolicitud(comando);
            }
        }

        cliente.desconectar();
        teclado.close();

        System.out.println("Cliente desconectado.");
    }
}

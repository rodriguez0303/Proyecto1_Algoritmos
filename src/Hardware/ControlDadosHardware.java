package Hardware;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortTimeoutException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.function.Consumer;

public class ControlDadosHardware implements AutoCloseable {

    private final String nombrePuerto;

    private SerialPort puerto;
    private BufferedReader entrada;
    private PrintWriter salida;

    // Solo puede existir una operación física a la vez sobre la Pico.
    private final Object bloqueoHardware = new Object();

    public ControlDadosHardware(String nombrePuerto) {
        this.nombrePuerto = nombrePuerto;
    }

    public boolean conectar() {

        if (estaConectado()) {
            return true;
        }

        puerto = SerialPort.getCommPort(nombrePuerto);

        puerto.setBaudRate(115200);
        puerto.setNumDataBits(8);
        puerto.setNumStopBits(SerialPort.ONE_STOP_BIT);
        puerto.setParity(SerialPort.NO_PARITY);

        puerto.setComPortTimeouts(
                SerialPort.TIMEOUT_READ_SEMI_BLOCKING,
                1000,
                0
        );

        if (!puerto.openPort()) {
            return false;
        }

        entrada = new BufferedReader(
                new InputStreamReader(
                        puerto.getInputStream()
                )
        );

        salida = new PrintWriter(
                puerto.getOutputStream(),
                true
        );

        try {

            // Le damos un momento a la Pico después de abrir COM3
            Thread.sleep(500);

            System.out.println(
                    "Verificando comunicación con la Pico..."
            );

            salida.println("PING");

            while (true) {

                String mensaje = leerMensaje();

                if (mensaje == null) {
                    continue;
                }

                System.out.println(
                        "PICO -> " + mensaje
                );

                // Puede aparecer HARDWARE_LISTO antes de PONG.
                if (mensaje.equals("PONG")) {

                    System.out.println(
                            "Hardware listo."
                    );

                    return true;
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "No se pudo verificar el hardware: "
                    + e.getMessage()
            );

            puerto.closePort();

            return false;
        }
    }

    public boolean estaConectado() {
        return puerto != null && puerto.isOpen();
    }

    public String registrarJugador(
            String identificador,
            Consumer<String> receptorEstado
    ) throws IOException {

        validarConexion();

        synchronized (bloqueoHardware) {

            enviar(
                    "REGISTRAR;" + identificador
            );

            String uidRegistrado = null;

            while (true) {

                String mensaje = leerMensaje();

                if (mensaje == null) {
                    continue;
                }

                notificar(
                        receptorEstado,
                        mensaje
                );

                // ---------------------------------
                // RFID leído correctamente
                // ---------------------------------

                String prefijoRfid =
                        "RFID;" + identificador + ";";

                if (mensaje.startsWith(prefijoRfid)) {

                    String[] partes =
                            mensaje.split(";");

                    if (partes.length != 3) {

                        throw new IOException(
                                "Respuesta RFID inválida: "
                                + mensaje
                        );
                    }

                    uidRegistrado =
                            partes[2];

                    continue;
                }

                // ---------------------------------
                // La tarjeta ya fue retirada
                // ---------------------------------

                if (mensaje.equals(
                        "RFID_RETIRADO;"
                        + identificador
                )) {

                    if (uidRegistrado == null) {

                        throw new IOException(
                                "Se retiró la tarjeta sin "
                                + "haber recibido primero su UID"
                        );
                    }

                    return uidRegistrado;
                }

                // ---------------------------------
                // Error
                // ---------------------------------

                if (mensaje.startsWith("ERROR;")) {

                    throw new IOException(
                            "Error del hardware: "
                            + mensaje
                    );
                }
            }
        }
    }

    public ResultadoDados tirarDados(
            String identificador,
            Consumer<String> receptorEstado
    ) throws IOException {

        validarConexion();

        synchronized (bloqueoHardware) {

            enviar("TIRAR;" + identificador);

            while (true) {

                String mensaje = leerMensaje();

                if (mensaje == null) {
                    continue;
                }

                notificar(receptorEstado, mensaje);

                String prefijoCorrecto =
                        "DADOS;" + identificador + ";";

                if (mensaje.startsWith(prefijoCorrecto)) {

                    String[] partes = mensaje.split(";");

                    if (partes.length != 5) {
                        throw new IOException(
                                "Respuesta de dados inválida: " + mensaje
                        );
                    }

                    try {

                        int dado1 =
                                Integer.parseInt(partes[2]);

                        int dado2 =
                                Integer.parseInt(partes[3]);

                        int sumaRecibida =
                                Integer.parseInt(partes[4]);

                        ResultadoDados resultado =
                                new ResultadoDados(
                                        identificador,
                                        dado1,
                                        dado2
                                );

                        if (resultado.getSuma() != sumaRecibida) {
                            throw new IOException(
                                    "La suma recibida no coincide: "
                                    + mensaje
                            );
                        }

                        return resultado;

                    } catch (NumberFormatException e) {

                        throw new IOException(
                                "Valores de dados inválidos: "
                                + mensaje,
                                e
                        );
                    }
                }

                if (mensaje.startsWith("ERROR;")) {
                    throw new IOException(
                            "Error del hardware: " + mensaje
                    );
                }
            }
        }
    }

    // Pide al jugador que paga pasar su tarjeta RFID por el lector.
    // Bloquea hasta que la Pico confirma con PAGO_OK;<identificador>.
    public void ValidarPago(
            String Identificador,
            Consumer<String> ReceptorEstado
    ) throws IOException {

        validarConexion();

        synchronized (bloqueoHardware) {

            enviar("VALIDAR_PAGO;" + Identificador);

            while (true) {

                String Mensaje = leerMensaje();

                if (Mensaje == null) {
                    continue;
                }

                notificar(ReceptorEstado, Mensaje);

                if (Mensaje.equals("PAGO_OK;" + Identificador)) {
                    return;
                }

                if (Mensaje.startsWith("ERROR;")) {
                    throw new IOException(
                            "Error del hardware: " + Mensaje
                    );
                }
            }
        }
    }

    private void enviar(String mensaje) throws IOException {

        if (salida == null) {
            throw new IOException(
                    "No existe salida hacia el hardware"
            );
        }

        salida.println(mensaje);

        if (salida.checkError()) {
            throw new IOException(
                    "No se pudo enviar al hardware: "
                    + mensaje
            );
        }
    }

    private String leerMensaje() throws IOException {

        try {

            String mensaje = entrada.readLine();

            if (mensaje == null) {
                return null;
            }

            mensaje = mensaje.trim();

            if (mensaje.isEmpty()) {
                return null;
            }

            return mensaje;

        } catch (SerialPortTimeoutException e) {

            // No es un error real: el usuario puede tardar en acercar
            // la tarjeta RFID o en presionar el botón físico.
            return null;
        }
    }

    private void notificar(
            Consumer<String> receptorEstado,
            String mensaje
    ) {

        if (receptorEstado != null) {
            receptorEstado.accept(mensaje);
        }
    }

    private void validarConexion() throws IOException {

        if (!estaConectado()) {
            throw new IOException(
                    "El hardware no está conectado a "
                    + nombrePuerto
            );
        }
    }

    @Override
    public void close() throws IOException {

        if (entrada != null) {
            entrada.close();
            entrada = null;
        }

        if (salida != null) {
            salida.close();
            salida = null;
        }

        if (puerto != null && puerto.isOpen()) {
            puerto.closePort();
        }
    }
}

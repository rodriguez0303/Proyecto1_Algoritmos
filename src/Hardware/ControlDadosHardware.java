package Hardware;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortTimeoutException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * Adaptador entre el servidor de Monopoly TEC y la Raspberry Pi Pico.
 * Utiliza comunicacion serial mediante jSerialComm para registrar las
 * tarjetas RFID, solicitar tiradas de dados y validar pagos fisicos.
 * El bloqueoHardware impide ejecutar operaciones simultaneas sobre la Pico.
 * Las respuestas del hardware se reenvian mediante Consumer<String>.
 */
public class ControlDadosHardware implements AutoCloseable {

    private final String nombrePuerto;

    private SerialPort puerto;
    private BufferedReader entrada;
    private PrintWriter salida;

    // Solo puede existir una operación física a la vez sobre la Pico.
    private final Object bloqueoHardware = new Object();

    /**
     * Define el puerto serial que se usara para comunicarse con la Pico.
     * La conexion no se abre hasta ejecutar conectar().
     *
     * @param nombrePuerto nombre del puerto, por ejemplo COM3
     */
    public ControlDadosHardware(String nombrePuerto) {
        this.nombrePuerto = nombrePuerto;
    }

    /**
     * Abre el puerto serial a 115200 baudios y comprueba la comunicacion.
     * Envia PING y espera la respuesta PONG de la Pico antes de continuar.
     *
     * @return true si el puerto y la comunicacion se habilitaron
     */
    public boolean conectar() {

        if (estaConectado()) {
            return true;
        }

        puerto = SerialPort.getCommPort(nombrePuerto);

        puerto.setBaudRate(115200);
        puerto.setNumDataBits(8);
        puerto.setNumStopBits(SerialPort.ONE_STOP_BIT);
        puerto.setParity(SerialPort.NO_PARITY);

        // Se espera como máximo 200 ms por lectura, para revisar seguido
        // si la interfaz pidió lanzar los dados (botón digital).
        puerto.setComPortTimeouts(
                SerialPort.TIMEOUT_READ_SEMI_BLOCKING,
                200,
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

    /**
     * Consulta si el puerto serial esta abierto.
     *
     * @return true cuando existe una conexion serial abierta
     */
    public boolean estaConectado() {
        return puerto != null && puerto.isOpen();
    }

    /**
     * Solicita a la Pico registrar la tarjeta RFID de un jugador.
     * La operacion mantiene ocupado el hardware hasta obtener la respuesta
     * del registro; los estados intermedios se entregan al receptor.
     *
     * @param identificador ID del jugador, por ejemplo J001
     * @param receptorEstado recibe los mensajes informativos de la Pico
     * @return UID de la tarjeta registrada
     * @throws IOException si falla la comunicacion serial
     */
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

    /**
     * Realiza un lanzamiento utilizando la validacion RFID y el boton fisico.
     * Delega al metodo sobrecargado sin proveedor de lanzamiento digital.
     *
     * @param identificador ID del jugador que lanza
     * @param receptorEstado recibe los estados de la operacion
     * @return resultado de los dos dados
     * @throws IOException si falla la comunicacion serial
     */
    public ResultadoDados tirarDados(
            String identificador,
            Consumer<String> receptorEstado
    ) throws IOException {

        return tirarDados(identificador, receptorEstado, null);
    }

    // Igual que tirarDados(), pero además del botón físico acepta el botón de la interfaz:
    // mientras la Pico espera el botón, se pregunta a "LanzamientoDigital" si el jugador
    // pulsó el botón en la interfaz; si es así, se le envía BOTON;<identificador> a la Pico.
    /**
     * Solicita una tirada de dados tras validar la tarjeta RFID.
     * Admite tanto el boton fisico como la solicitud desde la interfaz;
     * cuando LanzamientoDigital lo indica, informa a la Pico del boton digital.
     * El bloqueoHardware evita cruces entre los mensajes de distintos turnos.
     *
     * @param identificador ID del jugador que tiene el turno
     * @param receptorEstado recibe mensajes de progreso del hardware
     * @param LanzamientoDigital consulta opcional al boton de la interfaz
     * @return valores de ambos dados asociados al jugador
     * @throws IOException si falla la comunicacion serial
     */
    public ResultadoDados tirarDados(
            String identificador,
            Consumer<String> receptorEstado,
            BooleanSupplier LanzamientoDigital
    ) throws IOException {

        validarConexion();

        synchronized (bloqueoHardware) {

            enviar("TIRAR;" + identificador);

            // true cuando la Pico ya validó la tarjeta y está esperando el botón.
            boolean EsperandoBoton = false;

            // true cuando ya se le envió el botón digital (para no enviarlo dos veces).
            boolean BotonDigitalEnviado = false;

            while (true) {

                if (EsperandoBoton
                        && !BotonDigitalEnviado
                        && LanzamientoDigital != null
                        && LanzamientoDigital.getAsBoolean()) {

                    enviar("BOTON;" + identificador);

                    BotonDigitalEnviado = true;
                }

                String mensaje = leerMensaje();

                if (mensaje == null) {
                    continue;
                }

                notificar(receptorEstado, mensaje);

                if (mensaje.equals("ESPERANDO_BOTON;" + identificador)) {
                    EsperandoBoton = true;
                }

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
    // Si "Cancelacion" devuelve true mientras se espera la tarjeta (el jugador pulsó Cancelar
    // en la interfaz), se le envía CANCELAR;<identificador> a la Pico.
    // Devuelve true si el pago se validó y false si se canceló.
    /**
     * Comprueba con la Pico la tarjeta RFID del jugador que debe pagar.
     * La cancelacion digital se permite cuando se proporciona el callback
     * y la tarjeta aun no ha sido validada; el servidor decide si esa
     * cancelacion corresponde a una compra opcional.
     *
     * @param Identificador ID del jugador que valida el pago
     * @param ReceptorEstado recibe el avance de la validacion RFID
     * @param Cancelacion callback opcional para cancelar antes de validar
     * @return true si la Pico autorizo el pago; false si se cancelo
     * @throws IOException si ocurre un error de comunicacion
     */
    public boolean ValidarPago(
            String Identificador,
            Consumer<String> ReceptorEstado,
            BooleanSupplier Cancelacion
    ) throws IOException {

        validarConexion();

        synchronized (bloqueoHardware) {

            enviar("VALIDAR_PAGO;" + Identificador);

            // true cuando la Pico ya leyó la tarjeta correcta: desde ahí ya no se puede cancelar.
            boolean TarjetaLeida = false;

            // true cuando ya se envió CANCELAR (para no enviarlo dos veces).
            boolean CancelacionEnviada = false;

            while (true) {

                if (!TarjetaLeida
                        && !CancelacionEnviada
                        && Cancelacion != null
                        && Cancelacion.getAsBoolean()) {

                    enviar("CANCELAR;" + Identificador);

                    CancelacionEnviada = true;
                }

                String Mensaje = leerMensaje();

                if (Mensaje == null) {
                    continue;
                }

                notificar(ReceptorEstado, Mensaje);

                if (Mensaje.equals("RFID_OK;" + Identificador)) {
                    TarjetaLeida = true;
                }

                if (Mensaje.equals("PAGO_OK;" + Identificador)) {
                    return true;
                }

                if (Mensaje.equals("PAGO_CANCELADO;" + Identificador)) {
                    return false;
                }

                if (Mensaje.startsWith("ERROR;")) {
                    throw new IOException(
                            "Error del hardware: " + Mensaje
                    );
                }
            }
        }
    }

    /**
     * Envia una linea del protocolo al puerto serial.
     *
     * @param mensaje comando que se enviara a la Pico
     * @throws IOException si no existe salida o falla el envio
     */
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

    /**
     * Lee y limpia el siguiente mensaje serial.
     * Los tiempos de espera normales no se consideran errores de hardware.
     *
     * @return mensaje recibido o null si no hay uno disponible
     * @throws IOException si la lectura presenta un fallo real
     */
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

    /**
     * Notifica un estado al callback, cuando existe un receptor.
     *
     * @param receptorEstado consumidor que recibe los estados
     * @param mensaje texto enviado por la Pico
     */
    private void notificar(
            Consumer<String> receptorEstado,
            String mensaje
    ) {

        if (receptorEstado != null) {
            receptorEstado.accept(mensaje);
        }
    }

    /**
     * Impide operar el hardware cuando el puerto esta desconectado.
     *
     * @throws IOException si el puerto no esta abierto
     */
    private void validarConexion() throws IOException {

        if (!estaConectado()) {
            throw new IOException(
                    "El hardware no está conectado a "
                    + nombrePuerto
            );
        }
    }

    /**
     * Libera los flujos y cierra el puerto serial.
     * Permite usar ControlDadosHardware con try-with-resources.
     *
     * @throws IOException si falla el cierre de un flujo
     */
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

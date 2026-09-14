// Se usa para manejar errores que pueden ocurrir al iniciar el servidor o durante la comunicación por red.
import java.io.IOException;

// ServerSocket es una clase que ya viene implementada en Java.
    // Permite crear el servidor, abrir un puerto y esperar conexiones de los jugadores.
import java.net.ServerSocket;

// Socket es una clase que ya viene implementada en Java.
    // Permite  mantener la conexión entre el servidor y un jugador
import java.net.Socket;

// BufferedReader es una clase que Java ya tiene implementada.
    // Permite leer los mensajes que el jugador envía al servidor.
import java.io.BufferedReader;

// InputStreamReader es una clase que Java ya tiene implementada.
    // Permite preparar la información que llega por la conexión. Para que BufferedReader pueda leerla como texto.
import java.io.InputStreamReader;

// PrintWriter es una clase que Java ya tiene implementada.
    // Permite enviar mensajes de texto desde el servidor hacia el jugador por medio de su conexión Socket.
import java.io.PrintWriter;

//*****************************************************
//*****************************************************

public class Server {

    // Dirección IP de la computadora que funciona como servidor
    private String ip;

    // Puerto que utilizará el servidor
    private int puerto;

    // Juego que será administrado por el servidor
    private Juego juego;

    // ServerSocket es una clase que Java ya tiene implementada.
    // Guardará el servidor en el que se abrió en un puerto y estará a la espera de las conexiones de los jugadores al servidor.
    private ServerSocket serverSocket;


    // Socket es una clase que Java ya tiene implementada.
        // Se crea un arreglo de tamaño 4 que es el tamaño máximo de jugadores en el juego.
        // Cada espacio guardará la conexión de un jugador después de que el servidor lo acepte.
    private Socket[] clientesSocket = new Socket[4];

    // Arreglo que guardará el lector de cada jugador conectado.
        // Cada BufferedReader permitirá recibir los mensajes enviados por un jugador.
            // La posición del BufferedReader coincide con la posición de su Socket.
    private BufferedReader[] entradas = new BufferedReader[4];

    // Arreglo que guardará el escritor de cada jugador conectado.
    // Cada PrintWriter permitirá enviar mensajes desde el servidor hacia un jugador.
    // La posición del PrintWriter coincide con la posición de su Socket.
    private PrintWriter[] salidas = new PrintWriter[4];

    // Permite llevar el control de cuántos jugadores han sido aceptados por el servidor.
        // Cada vez que se acepta un nuevo jugador, este valor aumentará en 1.
            // También permite saber en qué posición de los arreglos se debe guardar el siguiente jugador.
    private int cantidadJugadores = 0;


    // Arreglo que guardará los 4 jugadores que participan en el juego.
    // Cada posición guarda un objeto Jugador con su información, como saldo, posición y propiedades.
    // La posición del jugador coincide con la posición de su conexión en clientesSocket.
    private Jugador[] jugadoresConectados = new Jugador[4];


    //*****************************************************
    //*****************************************************

    // Constructor de la clase Servidor.
        // Recibe la IP, el puerto y el objeto Juego que administrará el servidor. //("192.168.1.10", 5000, juegoMonopoly)
    public Server(String ip, int puerto, Juego juego) {

        this.ip = ip;

        this.puerto = puerto;

        this.juego = juego;
    }

    //*****************************************************
    //*****************************************************

    // Método que permite iniciar el servidor
    public void iniciar() {
        try {

            //// Se crea el servidor usando el puerto indicado
                //Se utiliza el puerto guardado en la variable "puerto".
                        // y queda a la espera de conexiones de los jugadores.
            serverSocket = new ServerSocket(puerto);

            // Se muestra la dirección IP que utilizará el servidor
            System.out.println("IP del servidor: " + ip);

            // Puerto que utilizará el servidor
            System.out.println("Puerto del servidor: " + puerto);

            // Mensaje que indica que el servidor ha sido iniciado
            System.out.println("Servidor iniciado");

            // Mientras no se hayan conectado los 4 jugadores,
                // el servidor continuará esperando nuevas conexiones.
                    // aceptarCliente() acepta un jugador y aumenta cantidadJugadores en 1.
                        // Cuando cantidadJugadores llegue a 4, el ciclo termina.
            while (cantidadJugadores < 4) {

                aceptarCliente();
            }
        }

            catch (IOException e) {

                // Se ejecuta si ocurrió un problema al iniciar el servidor
                System.out.println("Error al iniciar el servidor");
            }
    }

    //*****************************************************
    //*****************************************************
    // Método que permite esperar y aceptar la conexión de un jugador.
    public void aceptarCliente() {

        try {

            // Se valida que existan máximo 4 jugadores.
            if (cantidadJugadores < 4) {

                // serverSocket → Es la puerta del servidor que está esperando a que los jugadores se conecten.
                    // accept() espera hasta que un jugador intente conectarse.
                        // La conexión que se crea se guarda en el arreglo clientesSocket.
                            // cantidadJugadores indica en cuál posición del arreglo se debe guardar.
                clientesSocket[cantidadJugadores] = serverSocket.accept();

                // Se crea el BufferedReader que utilizará este jugador durante toda su conexión.
                    // getInputStream() obtiene la información que llega desde el jugador por medio de su Socket.
                        // El BufferedReader se guarda en la misma posición que la conexión del jugador.
                entradas[cantidadJugadores] = new BufferedReader(

                        // Se utiliza el Socket que acaba de ser aceptado.
                            // cantidadJugadores indica cuál Socket del arreglo clientesSocket se debe utilizar.
                                // getInputStream() obtiene los datos que ese jugador envía al servidor.
                        new InputStreamReader(
                                clientesSocket[cantidadJugadores].getInputStream()
                        )
                );

                // Se crea el PrintWriter que utilizará este jugador durante toda su conexión.
                    // getOutputStream() permite enviar información desde el servidor hacia el jugador.
                        // El PrintWriter se guarda en la misma posición que la conexión del jugador.
                salidas[cantidadJugadores] = new PrintWriter(

                        // Se utiliza el Socket que acaba de ser aceptado.
                            // cantidadJugadores indica cuál Socket del arreglo clientesSocket se debe utilizar.
                                // true permite enviar inmediatamente cada mensaje cuando se utiliza println().
                        clientesSocket[cantidadJugadores].getOutputStream(),
                        true
                );

                // Se guarda la posición del jugador que acaba de conectarse.
                    // Esta posición permitirá identificar posteriormente cuál de los 4 jugadores está siendo atendido.
                    // Un jugador que no está en su turno puede hacer solicitudes de consultar sus saldos y propiedades.
                int posicionJugador = cantidadJugadores;


                // Mensaje que indica cuál jugador logró conectarse.
                System.out.println(
                        "Jugador " + (cantidadJugadores + 1) + " conectado"
                );


                // Se aumenta en 1 la cantidad de jugadores conectados.
                cantidadJugadores++;


                // Se crea un Thread (hilo) para atender al jugador que acaba de conectarse.
                Thread hiloJugador = new Thread(() -> {

                    // Mientras el jugador continúe conectado, el servidor sigue escuchando sus solicitudes.
                    while (clientesSocket[posicionJugador] != null && !clientesSocket[posicionJugador].isClosed()) {

                        procesarSolicitud(posicionJugador);
                    }

                });


                // Se inicia el Thread del jugador.
                hiloJugador.start();

            }

            // Si ya existen 4 jugadores conectados, no se acepta otro jugador.
            else {

                System.out.println("Ya están conectados los 4 jugadores");
            }

        } catch (IOException e) {

            // Se ejecuta si ocurre un problema al aceptar la conexión.
            System.out.println("Error al aceptar la conexión del jugador");
        }
    }
    //*****************************************************
    //*****************************************************

    // Método que guarda un jugador dentro del arreglo de jugadores conectados.
    public void asignarJugador(int posicion, Jugador jugador) {

        // La posición coincide con la posición de su conexión dentro del arreglo clientesSocket.
        jugadoresConectados[posicion] = jugador;
    }

    //*****************************************************
    //*****************************************************

    // Método que permite recibir una solicitud enviada por el jugador.
    public void procesarSolicitud(int posicion) {

        try {

            // Se utiliza el BufferedReader que ya fue creado cuando el jugador se conectó.
                // "posicion" indica cuál BufferedReader del arreglo entradas pertenece a ese jugador.
                    // De esta forma se utiliza siempre el mismo lector mientras el jugador continúe conectado.
            String solicitud = entradas[posicion].readLine();

            // Se lee la línea de texto enviada por el jugador.
            // El mensaje recibido corresponde a una de las solicitudes definidas para el servidor.
            // La solicitud recibida se guarda en la variable "solicitud".
            /*
            CONECTAR;IDENTIFICADOR
            TIRAR_DADOS
            COMPRAR_PROPIEDAD
            NO_COMPRAR
            TERMINAR_TURNO
            CONSULTAR_ESTADO
            CONSULTAR_TRANSACCIONES
             */


            // Se verifica si readLine() devolvió null.
            // Esto ocurre cuando el jugador cerró su conexión y ya no puede enviar más solicitudes.
            // Si esto sucede, se cierra el Socket y se termina esta ejecución del método.
            if (solicitud == null) {

                // Se cierra la conexión Socket del jugador.
                clientesSocket[posicion].close();

                // La conexión ya terminó, por lo tanto no es necesario continuar ejecutando este método.
                return;
            }

            // -------------------------------------------------
            // CONECTAR, IDENTIFICADOR
            // -------------------------------------------------

            // Se verifica si la solicitud enviada por el cliente comienza con CONECTAR.
                // La solicitud también debe contener el identificador del jugador.
                    // Por ejemplo: CONECTAR;ID001
            if (solicitud.startsWith("CONECTAR;")) {

                // Se divide la solicitud utilizando el punto y coma.
                    // datosConexion[0] guardará "CONECTAR".
                        // datosConexion[1] guardará el identificador enviado por el jugador.
                String[] datosConexion = solicitud.split(";");


                // Se verifica que la solicitud tenga exactamente las dos partes necesarias.
                    // La primera parte corresponde a CONECTAR y la segunda al identificador.
                        // Si no existen las dos partes, la solicitud de conexión no puede continuar.
                if (datosConexion.length != 2) {

                    enviarRespuesta(posicion, "Solicitud de conexión no válida");

                    return;
                }


                // Se obtiene el identificador enviado por el jugador.
                    // datosConexion[1] corresponde a la segunda parte de la solicitud.
                        // El identificador se guarda en la variable "identificador".
                String identificador = datosConexion[1];


                // Se busca dentro de Juego el jugador que tiene ese identificador.
                    // El jugador encontrado se guarda en la variable "jugadorEncontrado".
                Jugador jugadorEncontrado = juego.buscarJugadorPorIdentificador(identificador);


                // Se verifica si se encontró un jugador con ese identificador.
                // Si jugadorEncontrado contiene null, significa que el identificador no pertenece a un jugador.
                if (jugadorEncontrado == null) {

                    enviarRespuesta(posicion, "Jugador no encontrado");

                    return;
                }


                // Se guarda el jugador encontrado dentro del arreglo jugadoresConectados.
                    // "posicion" corresponde a la misma posición donde está guardado su Socket.
                        // De esta forma el Socket y el objeto Jugador quedan asociados mediante la misma posición.
                asignarJugador(posicion, jugadorEncontrado);


                // Se informa al cliente que la conexión fue aceptada correctamente.
                    // "posicion" identifica cuál conexión Socket se debe utilizar.
                        // El mensaje se envía únicamente al jugador que realizó esta solicitud.
                enviarRespuesta(posicion, "Conexión válida");


                // La solicitud CONECTAR ya fue atendida completamente.
                    // El jugador ya quedó asociado con su conexión Socket.
                return;
            }


            // Se busca el jugador dentro del arreglo jugadoresConectados.
                // "posicion" indica en cuál espacio del arreglo se debe buscar.
                    // El jugador encontrado se guarda en la variable "jugador".
            Jugador jugador = jugadoresConectados[posicion];


            // Se verifica si existe un jugador guardado en esa posición.
                // Si jugador contiene null, significa que no existe un objeto Jugador asociado con esa conexión.
                // En ese caso no se puede continuar procesando la solicitud.
            if (jugador == null) {

                // Se envía un mensaje al cliente que realizó la solicitud.
                    // "posicion" permite identificar cuál conexión Socket se debe utilizar.
                        // El mensaje indica que todavía no existe un jugador asignado a esa conexión.
                enviarRespuesta(posicion, "No existe un jugador asignado a esta conexión");


                // La solicitud no puede continuar sino existe un jugador asociado.
                    // Por lo tanto, se termina esta ejecución de procesarSolicitud().
                return;
            }


            // Se obtiene la propiedad en la que se encuentra actualmente el jugador.
                // El objeto "jugador" se envía al método obtenerPropiedadActual() de Juego.
                    // La propiedad encontrada se guarda en la variable "propiedad".
            Propiedad propiedad = juego.obtenerPropiedadActual(jugador);


            // Se valida si la solicitud realizada por el jugador puede ejecutarse.
                // Se envía la solicitud, el jugador que la realizó y la propiedad donde se encuentra.
                    // El resultado true o false se guarda en la variable "accionValida".
            boolean accionValida = validarAccion(solicitud, jugador, propiedad);



            // Se verifica el resultado que devolvió el método validarAccion().
                // Si accionValida retorna true, significa que la solicitud puede ejecutarse.
                    // Luego se identifica cuál acción solicitó el jugador.
            if (accionValida) {


                // -------------------------------------------------
                // TIRAR_DADOS
                // -------------------------------------------------

                // Se verifica si la solicitud enviada por el jugador es TIRAR_DADOS.
                    // Si la solicitud coincide, se llama al método lanzarDados() de juego.
                        // El método lanzarDados() se encarga de realizar el lanzamiento de los dados.
                if (solicitud.equals("TIRAR_DADOS")) {

                    juego.lanzarDados();

                    // Se informa a todos los jugadores conectados que el estado del juego cambió.
                    actualizarClientes();
                }


                // -------------------------------------------------
                // COMPRAR_PROPIEDAD
                // -------------------------------------------------

                // Se verifica si la solicitud enviada por el jugador es COMPRAR_PROPIEDAD.
                    // Si la solicitud coincide, se llama al método comprarPropiedad() de juego.
                        // Se envía el jugador que realiza la compra y la propiedad donde se encuentra.
                else if (solicitud.equals("COMPRAR_PROPIEDAD")) {

                    juego.comprarPropiedad(
                                            jugador,
                                            propiedad
                                    );
                    // Se informa a todos los jugadores conectados que el estado del juego cambió.
                    actualizarClientes();
                }


                // -------------------------------------------------
                // NO_COMPRAR
                // -------------------------------------------------

                // Se verifica si la solicitud enviada por el jugador es NO_COMPRAR.
                    // Si la solicitud coincide, el jugador decidió no comprar la propiedad.
                        // No se modifica la propiedad ni el saldo del jugador.
                else if (solicitud.equals("NO_COMPRAR")) {

                    // Se envía una respuesta únicamente al jugador que realizó la solicitud.
                        // "posicion" identifica cuál conexión Socket se debe utilizar.
                            // El mensaje confirma que el jugador decidió no comprar.
                    enviarRespuesta(posicion, "El jugador decidió no comprar la propiedad"
                    );
                }


                // -------------------------------------------------
                // TERMINAR_TURNO
                // -------------------------------------------------

                // Se verifica si la solicitud enviada por el jugador es TERMINAR_TURNO.
                    // Si la solicitud coincide, se llama al método finalizarTurno() de juego.
                        // Juego se encarga de finalizar el turno del jugador actual.
                else if (solicitud.equals("TERMINAR_TURNO")) {

                    juego.finalizarTurno();

                    // Se informa a todos los jugadores conectados que el estado del juego cambió.
                    actualizarClientes();
                }


                // -------------------------------------------------
                // CONSULTAR_ESTADO
                // -------------------------------------------------

                // Se verifica si la solicitud enviada por el jugador es CONSULTAR_ESTADO.
                    // Esta solicitud permite consultar la información actual del jugador.
                        // No es necesario que el jugador se encuentre en su turno para realizar esta consulta.
                else if (solicitud.equals("CONSULTAR_ESTADO")) {

                    // Se envía una respuesta al jugador que realizó la consulta.
                        // "posicion" identifica cuál conexión Socket pertenece al jugador.
                            // enviarRespuesta() utiliza esa conexión para enviar el mensaje.
                    enviarRespuesta(
                            posicion,
                            "Consulta de estado válida"
                    );
                }


                // -------------------------------------------------
                // CONSULTAR_TRANSACCIONES
                // -------------------------------------------------

                // Se verifica si la solicitud enviada por el jugador es CONSULTAR_TRANSACCIONES.
                    // Esta solicitud permite consultar la información del historial de transacciones.
                     // No es necesario que el jugador se encuentre en su turno para realizar esta consulta.
                else if (solicitud.equals("CONSULTAR_TRANSACCIONES")) {

                    // Se envía una respuesta al jugador que realizó la consulta.
                        // "posicion" identifica cuál conexión Socket pertenece al jugador.
                        // enviarRespuesta() utiliza esa conexión para enviar el mensaje.
                    enviarRespuesta(posicion, "Consulta de transacciones válida");
                }
            }


            // Si validarAccion() devolvió false,
                // significa que la acción solicitada no puede realizarse.
            else {

                // Se informa al jugador que la solicitud que realizó no puede ejecutarse.
                    // "posicion" identifica cuál jugador realizó la solicitud.
                    // enviarRespuesta() envía el mensaje únicamente a ese jugador mediante su conexión Socket.
                enviarRespuesta(posicion, "La acción no es válida");
            }

        }

        catch (IOException e) {

            // Se ejecuta si ocurre un problema al recibir información del jugador.
            // El servidor muestra un mensaje indicando que ocurrió un error.
            System.out.println(
                    "Error al recibir la solicitud del cliente"
            );
        }
    }
    //*****************************************************
    // *****************************************************

    // Método que permite enviar una respuesta a un jugador específico.
    public void enviarRespuesta(int posicion, String mensaje) {

        // Se verifica que la posición del jugador sea válida.
            // Las posiciones permitidas van desde 0 hasta 3 (corresponde a los 4 jugadores posibles).
        if (posicion >= 0 && posicion < 4) {

            // Se verifica que exista una conexión guardada en esa posición.
                // También se verifica que exista un PrintWriter asociado a ese jugador (Si ambos existen, el servidor puede enviar el mensaje.)
            if (clientesSocket[posicion] != null && salidas[posicion] != null) {

                // Se utiliza el PrintWriter que ya fue creado cuando el jugador se conectó.
                    // "posicion" identifica cuál PrintWriter pertenece a ese jugador.
                        // println() envía el mensaje hacia el cliente correspondiente.
                salidas[posicion].println(mensaje);
            }

            else {

                // Se muestra un mensaje si no existe una conexión disponible en esa posición.
                    // Esto significa que todavía no hay un jugador conectado correctamente.
                System.out.println(
                        "No existe un jugador conectado en esa posición"
                );
            }
        }

        else {

            // Se muestra un mensaje si la posición recibida está fuera del rango permitido.
                // El servidor solamente admite las posiciones 0, 1, 2 y 3.
            System.out.println(
                    "Posición de jugador no válida"
            );
        }
    }

    //*****************************************************
    // *****************************************************

    // Método que valida si una solicitud enviada por un jugador se puede realizar o no

    public boolean validarAccion(String solicitud, Jugador jugador, Propiedad propiedad) {

        // -------------------------------------------------
        // SOLICITUD VACÍA
        // -------------------------------------------------

        // Si no se recibió ninguna solicitud, no se puede ejecutar ninguna acción.
        if (solicitud == null) {

            return false;
        }

        // -------------------------------------------------
        // JUEGO EN CURSO
        // -------------------------------------------------

        // Después de conectarse, las demás solicitudes solamente pueden realizarse mientras la partida esté en curso.
        else if (juego.isEnCurso() == false) {

            return false;
        }

        // -------------------------------------------------
        // TIRAR_DADOS
        // -------------------------------------------------

        else if (solicitud.equals("TIRAR_DADOS")) {

            // Verifica si el jugador NO tiene el turno actual.
            if (jugador != juego.obtenerJugadorActual()) {

                return false;
            }

            // Si tiene el turno, verifica si ya lanzó los dados (no puede lanzar los dados múltiples veces).
            else if (juego.getDadosLanzadosEsteTurno()) {

                return false;
            }

            // Si esta en su turno y no ha lanzado los dados retorna True
            else {

                return true;
            }
        }


        // -------------------------------------------------
        // COMPRAR_PROPIEDAD
        // -------------------------------------------------

        else if (solicitud.equals("COMPRAR_PROPIEDAD")) {

            // Se valida si el jugador NO tiene el turno actual.
            if (jugador != juego.obtenerJugadorActual()) {

                return false;
            }

            // Se verifica que exista una propiedad para comprar.
            else if (propiedad == null) {

                return false;
            }

            // Verifica si la propiedad NO está disponible.
            else if (!propiedad.isDisponible()) {

                return false;
            }

            // Verifica si el saldo del jugador es menor que el precio de la propiedad.
            else if (jugador.getSaldo() < propiedad.getPrecioCompra()) {

                return false;
            }

            // Si cumple todas las condiciones anteriores, el jugador puede comprar la propiedad.
            else {

                return true;
            }
        }


        // -------------------------------------------------
        // NO_COMPRAR (a pesar de tener el dinero y estar en su turno)
        // -------------------------------------------------

        // -------------------------------------------------
        // NO_COMPRAR
        // -------------------------------------------------

        else if (solicitud.equals("NO_COMPRAR")) {

            // Se verifica si el jugador NO tiene el turno actual.
            // El jugador solamente puede decidir no comprar durante su propio turno.
            if (jugador != juego.obtenerJugadorActual()) {

                return false;
            }

            // Se verifica que exista una propiedad en la posición actual del jugador.
            // Si propiedad contiene null, significa que el jugador no está sobre una propiedad.
            // En ese caso no existe ninguna propiedad que pueda decidir no comprar.
            else if (propiedad == null) {

                return false;
            }

            // Se verifica que la propiedad todavía esté disponible.
            // Si la propiedad ya pertenece a un jugador, no se puede decidir no comprarla.
            // Por lo tanto, la solicitud NO_COMPRAR no sería válida.
            else if (!propiedad.isDisponible()) {

                return false;
            }

            // Si el jugador tiene el turno y la propiedad está disponible,
            // puede decidir no comprarla.
            else {

                return true;
            }
        }


        // -------------------------------------------------
        // TERMINAR_TURNO
        // -------------------------------------------------

        else if (solicitud.equals("TERMINAR_TURNO")) {

            // Verifica si el jugador NO tiene el turno actual.
            if (jugador != juego.obtenerJugadorActual()) {

                return false;
            }

            // Si tiene el turno, puede terminarlo.
            else {

                return true;
            }
        }


        // -------------------------------------------------
        // CONSULTAR_ESTADO
        // -------------------------------------------------

        // Permite consultar la información actual del jugador (No es necesario que el jugador este en su turno)
        else if (solicitud.equals("CONSULTAR_ESTADO")) {
            return true;
        }

        // -------------------------------------------------
        // CONSULTAR_TRANSACCIONES
        // -------------------------------------------------

        // Permite consultar las transacciones  actuales del jugador (No es necesario que el jugador este en su turno)
        else if (solicitud.equals("CONSULTAR_TRANSACCIONES")) {
            return true;
        }

        // -------------------------------------------------
        // SOLICITUD NO RECONOCIDA
        // -------------------------------------------------

        // Si por algún error el servidor recibe una solicitud diferente a las acciones definidas anteriormente:
        // CONECTAR, IDENTIFICADOR
        // TIRAR_DADOS
        // COMPRAR_PROPIEDAD
        // NO_COMPRAR
        // TERMINAR_TURNO
        // CONSULTAR_ESTADO
        // CONSULTAR_TRANSACCIONES
            // la solicitud no se ejecuta y se devuelve false.

        else {
            return false;
        }
    }

    //*****************************************************
    // *****************************************************

    // Método que permite informar a todos los jugadores conectados que el estado del juego fue actualizado.
    public void actualizarClientes() {

        // Se recorren las posiciones correspondientes a los 4 posibles jugadores.
            // Cada posición corresponde a un jugador y a su conexión con el servidor.
        for (int posicion = 0; posicion < 4; posicion++) {

            // Se verifica que exista un Socket guardado en esta posición.
                // También se verifica que la conexión del jugador continúe abierta.
                // Si ambas condiciones se cumplen, el servidor puede enviarle la actualización.
            if (clientesSocket[posicion] != null && !clientesSocket[posicion].isClosed()) {

                // Se utiliza enviarRespuesta() para enviar el mensaje al jugador.
                // "posicion" permite identificar a cuál de los jugadores se debe enviar.
                // El mensaje le indica al cliente que el estado del juego fue actualizado.
                enviarRespuesta(posicion, "ACTUALIZAR_ESTADO"
                );
            }
        }
    }

    //*****************************************************
    // *****************************************************
}

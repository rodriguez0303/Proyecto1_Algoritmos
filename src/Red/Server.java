package Red;

import LogicaJuego.Juego;
import LogicaJuego.Jugador;
import LogicaJuego.Propiedad;

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

            // Se crea el servidor usando el puerto indicado.
                // Se utiliza el puerto guardado en la variable "puerto".
                    // El servidor queda preparado para esperar conexiones de los jugadores.
            serverSocket = new ServerSocket(puerto);


            // Se muestra la dirección IP que utilizará el servidor.
            System.out.println("IP del servidor: " + ip);


            // Se muestra el puerto que utilizará el servidor.
            System.out.println("Puerto del servidor: " + puerto);


            // Se muestra un mensaje indicando que el servidor fue iniciado correctamente.
            System.out.println("Servidor iniciado");


            // El servidor permanece esperando nuevas conexiones mientras continúe ejecutándose.
                // aceptarCliente() se encarga de revisar si existe una posición disponible.
                // De esta forma, si un jugador se desconecta, su espacio puede volver a utilizarse.
            while (true) {

                aceptarCliente();
            }

        }

        catch (IOException e) {

            // Se ejecuta si ocurre un problema al iniciar el servidor.
                // Por ejemplo, si el puerto ya está siendo utilizado por otro programa.
            System.out.println("Error al iniciar el servidor");
        }
    }

    //*****************************************************
    //*****************************************************

    // Método que busca una posición disponible para guardar una nueva conexión.
    public int buscarPosicionDisponible() {

        //Si un jugador se desconecta debe quedar el espacio disponible nuevamente para volverse a conectar
            // Se recorren las 4 posiciones posibles de los arreglos.
                // Una posición está disponible cuando contiene null.
                    // Esto significa que no existe una conexión guardada en esa posición.
        for (int i = 0; i < clientesSocket.length; i++) {

            // Se verifica si la posición está libre.
                // Una posición contendrá el valor de null cuando no existe una conexión guardada en ella.
                // Si la posición está libre, esta puede utilizarse para conectar a otro jugador.
            if (clientesSocket[i] == null) {

                return i;
            }
        }

        // Si las 4 posiciones tienen conexiones activas,
        // no existe espacio disponible para otro jugador.
        return -1;
    }
    //*****************************************************
    //*****************************************************
    //*****************************************************
//*****************************************************

    // Método que permite aceptar la conexión de un jugador.
    public void aceptarCliente() {

        try {

            // El servidor espera hasta que un cliente intente conectarse.
                // accept() detiene momentáneamente esta parte del programa hasta recibir una conexión.
                    // La nueva conexión se guarda temporalmente en la variable "nuevoSocket".
            Socket nuevoSocket = serverSocket.accept();


            // Se busca una posición disponible dentro del arreglo de conexiones.
                // buscarPosicionDisponible() revisa las 4 posiciones posibles.
                // Si encuentra una posición libre devuelve 0, 1, 2 o 3; si no encuentra ninguna devuelve -1.
            int posicionDisponible = buscarPosicionDisponible();

            // Se guarda la conexión Socket que pertenece a este cliente.
                // "nuevoSocket" contiene la conexión que acaba de aceptar el servidor.
                    // Esta conexión se guarda en "socketJugador" para utilizarla dentro del Thread.
            Socket socketJugador = nuevoSocket;


            // Se verifica si existe una posición disponible.
                // Una posición diferente de -1 significa que todavía hay espacio para aceptar al cliente.
                    // En ese caso la nueva conexión puede guardarse dentro de los arreglos del servidor.
            if (posicionDisponible != -1) {


                // Se guarda el Socket del cliente en la posición disponible.
                    // "posicionDisponible" indica cuál espacio del arreglo debe utilizarse.
                         // La conexión queda asociada a esa posición mientras el cliente continúe conectado.
                clientesSocket[posicionDisponible] = nuevoSocket;


                // Se crea el lector que permitirá recibir mensajes enviados por este cliente.
                    // getInputStream() obtiene la información que llega desde el cliente.
                     // El BufferedReader se guarda en la misma posición que su Socket.
                entradas[posicionDisponible] = new BufferedReader(
                        new InputStreamReader(
                                clientesSocket[posicionDisponible].getInputStream()
                        )
                );


                // Se crea el escritor que permitirá enviar mensajes hacia este cliente.
                    // getOutputStream() obtiene el canal de salida hacia el cliente.
                        // true permite enviar inmediatamente cada mensaje escrito con println().
                salidas[posicionDisponible] = new PrintWriter(
                        clientesSocket[posicionDisponible].getOutputStream(),
                        true
                );


                // Se guarda la posición que utilizará este cliente.
                    // Esta variable será utilizada dentro del Thread.
                        // Cada cliente tendrá su propia posición dentro de los arreglos.
                int posicionJugador = posicionDisponible;


                // Se informa en el servidor cuál espacio de conexión fue utilizado.
                    // posicionJugador comienza en 0, por eso se suma 1 solamente para mostrarlo de forma más comprensible.
                        // Este número representa la posición de conexión, no necesariamente el ID del jugador.
                System.out.println(
                        "Cliente " + (posicionJugador + 1) + " conectado"
                );


                // Se crea un Thread para atender las solicitudes de los jugadores.
                    // Cada jugador tendrá su propio Thread.
                        // Esto permitirá atender varias conexiones al mismo tiempo.
                Thread hiloCliente = new Thread(() -> {

                    // El Thread continúa solamente mientras la posición siga perteneciendo al mismo Socket.
                        // Si el jugador se desconecta y la posición es reutilizada,
                            // el Thread anterior ya no podrá procesar las solicitudes del nuevo cliente.
                    while (clientesSocket[posicionJugador] == socketJugador && !socketJugador.isClosed()) {

                        procesarSolicitud(posicionJugador);
                    }

                    // Se eliminan los datos que estaban asociados a la conexión de este jugador.
                        // Los cuatro arreglos utilizan la misma posición para guardar los datos de una conexión.
                            // Al colocar null, esta posición queda libre para que pueda conectarse otro jugador.
                    if (clientesSocket[posicionJugador] == socketJugador) {

                        entradas[posicionJugador] = null;
                        salidas[posicionJugador] = null;
                        jugadoresConectados[posicionJugador] = null;
                        clientesSocket[posicionJugador] = null;
                    }

                });


                // Se inicia el Thread del jugador.
                    // Desde este momento el Thread comienza a ejecutar su código.
                        // El servidor principal puede continuar aceptando otras conexiones.
                hiloCliente.start();
            }


            // Si posicionDisponible contiene -1, significa que las 4 conexiones están ocupadas.
                // El servidor no puede aceptar un quinto jugador simultáneamente.
                    // La nueva conexión se rechaza y se cierra.
            else {

                // Se crea un escritor temporal para poder informar al cliente antes de cerrar su conexión.
                    // Este PrintWriter solamente se utiliza para enviar el mensaje de rechazo.
                        // No se guarda dentro del arreglo salidas porque no existe una posición disponible.
                PrintWriter salidaTemporal = new PrintWriter(
                        nuevoSocket.getOutputStream(),
                        true
                );


                // Se informa al cliente que no existe espacio disponible.
                salidaTemporal.println(
                        "No se pueden conectar más de 4 jugadores"
                );


                // Se cierra la conexión que intentó entrar cuando ya había 4 clientes conectados.
                    // De esta forma nunca existen más de 4 conexiones activas dentro del servidor.
                nuevoSocket.close();
            }

        }

        catch (IOException e) {

            // Se ejecuta si ocurre un problema al aceptar o preparar la conexión del cliente.
                // Puede ocurrir al crear el Socket, BufferedReader o PrintWriter.
            System.out.println(
                    "Error al aceptar la conexión del cliente"
            );
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

                // La conexión ya terminó.
                return;
            }
            // "synchronized" es una palabra reservada de Java que permite controlar el acceso de varios Threads a un mismo objeto.
                // En este caso se utiliza el objeto "juego", porque es compartido por los Threads de los diferentes jugadores.
                    // Esto permite que solamente un Thread a la vez entre a este bloque y trabaje con el estado del juego.
            synchronized (juego) {

                // -------------------------------------------------
                // CONECTAR, IDENTIFICADOR
                // -------------------------------------------------

                // Se verifica si la solicitud enviada por el cliente comienza con CONECTAR.
                    // La solicitud también debe contener el identificador del jugador.
                        // Por ejemplo: CONECTAR;ID001
                if (solicitud.startsWith("CONECTAR;")) {

                    // Se verifica si esta conexión ya tiene un jugador asignado.
                        // "posicion" corresponde a la posición donde se encuentra guardado el Socket de este cliente.
                            // Si ya existe un Jugador en esa posición, significa que este cliente ya se identificó anteriormente.
                                // Evita que un cliente que ya tiene un jugador asignado pueda cambiarse a otro jugador.
                    if (jugadoresConectados[posicion] != null) {

                        // Se informa al cliente que no puede volver a asignarse otro jugador.
                        enviarRespuesta(posicion, "Esta conexión ya tiene un jugador asignado"
                        );

                        // Se termina esta solicitud sin modificar el jugador que ya estaba asociado con el Socket.
                        return;
                    }

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

                    // Se recorren las posiciones del arreglo jugadoresConectados.
                        // Cada posición puede contener un jugador que ya fue asociado a una conexión.
                            // El ciclo permite verificar si el jugador encontrado ya está conectado.
                                // El mismo jugador no debe aparecer dos veces conectados
                    for (int i = 0; i < jugadoresConectados.length; i++) {

                        // Se compara el jugador guardado en la posición actual con jugadorEncontrado.
                            // Si ambos objetos son el mismo, significa que ese jugador ya tiene una conexión activa.
                                // En ese caso no se debe permitir una segunda conexión con el mismo identificador.
                        if (jugadoresConectados[i] == jugadorEncontrado
                                && clientesSocket[i] != null
                                && !clientesSocket[i].isClosed()) {

                            // Se informa al jugador que intentó conectarse que ese id ya está siendo utilizado.
                            enviarRespuesta(posicion, "El jugador " + identificador + " ya está conectado"
                            );

                            return;
                        }
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
                        enviarRespuesta(posicion, "Consulta de estado válida"
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

            }

            catch (IOException e) {
                // Se ejecuta si ocurre un problema al recibir información del jugador.
                // Esto puede suceder si el jugador cierra su conexión mientras el servidor esperaba una solicitud.
                System.out.println(
                        "Error al recibir la solicitud del cliente" + (posicion + 1)
                );

                try {

                    // Se verifica que exista una conexión Socket para este jugador.
                        // "posicion" indica cuál conexión del arreglo pertenece al jugador.
                    if (clientesSocket[posicion] != null) {

                        // Se cierra la conexión Socket del jugador.
                            // "posicion" indica cuál conexión pertenece al cliente que tuvo el error.
                                // Después de cerrarla, esa conexión ya no puede seguir enviando solicitudes.
                        clientesSocket[posicion].close();
                    }

                } catch (IOException errorCierre) {

                    // Se ejecuta solamente si ocurre un problema al intentar cerrar la conexión.
                    System.out.println(
                            "Error al cerrar la conexión del cliente" + (posicion + 1)
                    );
                }
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

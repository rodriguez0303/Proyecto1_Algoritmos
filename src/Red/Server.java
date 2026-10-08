package Red;

import LogicaJuego.Juego;
import LogicaJuego.Jugador;
import LogicaJuego.Propiedad;

// Permite acceder a la lista donde se encuentran guardadas las propiedades adquiridas por un jugador.
import LogicaJuego.ListaSimplePropiedad;

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

// Permite acceder al Tablero real de la partida
    // para recorrer todas las casillas que lo forman.
import LogicaJuego.Tablero;

// Permite acceder a cada NodoCasilla almacenado
    // dentro de la lista circular doble del Tablero.
import LogicaJuego.NodoCasilla;

// Permite obtener la Casilla almacenada
    // dentro de cada NodoCasilla del Tablero.
import LogicaJuego.Casilla;

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

    // Guarda la Propiedad cuya compra se encuentra pendiente.
        // La propiedad solamente se guardará cuando el Jugador llegue a una Propiedad disponible durante su turno.
    private Propiedad propiedadPendienteCompra;


    // Guarda el Jugador al que pertenece la decisión de compra pendiente.
        // Permite impedir que otro Jugador intente comprar una Propiedad que no le corresponde.
    private Jugador jugadorPendienteCompra;


    // Guarda el número de turno en el que se generó la decisión de compra.
        // getNumTurno() es un método de la clase Juego.
            // Permite verificar que la compra o no compra se realice durante el mismo turno.
    private int turnoPendienteCompra;


    // Indica si ya se envió FIN;GANADOR;ID a los jugadores, para no avisar el fin de la partida dos veces.
    private boolean FinAvisado;


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
                    // Se sincroniza con "juego" porque los otros Threads pueden estar usando estos arreglos y el turno.
                    synchronized (juego) {

                        // Se guarda el Jugador que estaba asociado a esta conexión antes de liberar la posición.
                        Jugador JugadorDesconectado = jugadoresConectados[posicionJugador];

                        if (clientesSocket[posicionJugador] == socketJugador) {

                            entradas[posicionJugador] = null;
                            salidas[posicionJugador] = null;
                            jugadoresConectados[posicionJugador] = null;
                            clientesSocket[posicionJugador] = null;
                        }

                        // Si un Jugador se desconecta con la partida en curso, pierde automáticamente:
                            // queda eliminado (sus Propiedades se liberan) y ya no puede volver a conectarse.
                        if (JugadorDesconectado != null
                                && juego.isEnCurso()
                                && JugadorDesconectado.esActivo()) {

                            // Se guarda si tenía el turno antes de eliminarlo.
                            boolean EraSuTurno = JugadorDesconectado == juego.obtenerJugadorActual();

                            JugadorDesconectado.eliminar();
                            System.out.println(JugadorDesconectado.getNombre()
                                    + " se desconectó y queda eliminado de la partida");

                            // Con esta eliminación puede quedar un único jugador activo.
                            RevisarFinPartida();

                            if (juego.isEnCurso()) {

                                // Si se desconectó durante su turno, nadie más podría enviar TERMINAR_TURNO
                                    // por él y la partida quedaría trabada; por eso el Server pasa el turno.
                                if (EraSuTurno) {

                                    PasarTurnoAutomaticamente();
                                }

                                // Si no era su turno, solo se informa a los demás que ese Jugador quedó eliminado.
                                else {

                                    actualizarClientes();
                                }
                            }
                        }
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

    // Método que revisa si la partida terminó y, en ese caso, avisa a todos los jugadores conectados.
        // Si queda un único jugador activo, Juego finaliza la partida en ese mismo momento (punto 18).
            // También detecta el fin por límite de rondas, que Juego marca al pasar el turno.
                // El mensaje es FIN;GANADOR;ID, o FIN;GANADOR;SIN_GANADOR si no quedó ningún jugador activo.
                    // Debe llamarse dentro de un bloque synchronized (juego).
    private void RevisarFinPartida() {

        // Si la partida sigue en curso, o el fin ya fue avisado, no hay nada que enviar.
        if (!juego.RevisarFinPartida() || FinAvisado) {

            return;
        }

        FinAvisado = true;

        Jugador Ganador = juego.GetGanador();
        String MensajeFin = "FIN;GANADOR;"
                + (Ganador != null ? Ganador.getIdentificador() : "SIN_GANADOR");

        for (int i = 0; i < clientesSocket.length; i++) {

            if (clientesSocket[i] != null && !clientesSocket[i].isClosed()) {

                enviarRespuesta(i, MensajeFin);
            }
        }
    }

    //*****************************************************
    //*****************************************************

    // Método que pasa el turno sin esperar TERMINAR_TURNO del jugador actual.
        // Se usa cuando el Jugador del turno quedó eliminado o se desconectó, porque ya no puede terminarlo él mismo.
            // Debe llamarse dentro de un bloque synchronized (juego).
    private void PasarTurnoAutomaticamente() {

        // La compra pendiente pertenecía al turno que se está cerrando, así que se descarta.
        propiedadPendienteCompra = null;
        jugadorPendienteCompra = null;
        turnoPendienteCompra = 0;

        // finalizarTurno() reinicia el control de dados y avanza al siguiente jugador activo.
        juego.finalizarTurno();

        // Al pasar el turno la partida pudo terminar (un solo jugador activo o límite de rondas).
        RevisarFinPartida();

        // Se informa a todos los jugadores conectados que el turno cambió.
        actualizarClientes();
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

                    // Un Jugador eliminado (por bancarrota o por haberse desconectado) ya perdió la partida.
                        // Por eso no se le permite volver a conectarse ni incorporarse de nuevo.
                    if (!jugadorEncontrado.esActivo()) {

                        enviarRespuesta(posicion, "El jugador " + identificador
                                + " fue eliminado y no puede volver a incorporarse a la partida");

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
                    actualizarOtrosClientes(posicion);

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
                    if (solicitud.equals("TIRAR_DADOS")) {

                        // lanzarDados() es un método de la clase Juego.
                        // Este método lanza Dado1 y Dado2.
                        // Devuelve la suma de los valores obtenidos por ambos dados.
                        // La suma se guarda en la variable "pasos".
                        int pasos = juego.lanzarDados();


                        // getDado1() es un método de la clase Juego.
                        // Devuelve el objeto Dado que representa al dado número 1.
                        // getValor() es un método de la clase Dado.
                        // Devuelve el último valor obtenido por ese dado sin volver a lanzarlo.
                        // El resultado se guarda en la variable "valorDado1".
                        int valorDado1 = juego.getDado1().getValor();


                        // getDado2() es un método de la clase Juego.
                        // Devuelve el objeto Dado que representa al dado número 2.
                        // getValor() es un método de la clase Dado.
                        // Devuelve el último valor obtenido por ese dado sin volver a lanzarlo.
                        // El resultado se guarda en la variable "valorDado2".
                        int valorDado2 = juego.getDado2().getValor();


                        // Se mueve al jugador utilizando la suma obtenida al lanzar los dos dados.
                        // "jugador" corresponde al jugador asociado con esta conexión.
                        // "pasos" contiene la suma de valorDado1 + valorDado2.
                        juego.MoverJugador(jugador, pasos);

                        // Se obtiene la Propiedad donde quedó ubicado el Jugador
                        // después de realizar el movimiento en el tablero.
                        // obtenerPropiedadActual() es un método de la clase Juego.
                        Propiedad propiedadDespuesMovimiento = juego.obtenerPropiedadActual(jugador);


                        // Se verifica si el Jugador quedó ubicado sobre una Propiedad
                            // y si esa Propiedad se encuentra disponible para comprar.
                        if (propiedadDespuesMovimiento != null
                                && propiedadDespuesMovimiento.isDisponible()) {

                            // Se guarda la Propiedad que el Jugador puede decidir comprar o no comprar.
                            propiedadPendienteCompra = propiedadDespuesMovimiento;

                            // Se guarda el Jugador al que pertenece la compra pendiente.
                            jugadorPendienteCompra = jugador;

                            // getNumTurno() es un método de la clase Juego.
                                // Se guarda el número del turno actual para comprobar
                                 // que la compra o no compra se realice durante ese mismo turno.
                            turnoPendienteCompra = juego.getNumTurno();
                        }


                        // Si el Jugador no quedó sobre una Propiedad disponible,
                            // no existe ninguna compra pendiente después de su movimiento.
                        else {

                            propiedadPendienteCompra = null;
                            jugadorPendienteCompra = null;
                            turnoPendienteCompra = 0;
                        }


                        // Se prepara el mensaje que contiene el resultado de los dos dados.
                            // "DADOS" permite identificar el tipo de respuesta.
                             // Después se envía el valor del dado 1, el valor del dado 2 y el total.
                                // Por ejemplo, si los dados obtienen 3 y 5: y su sumatoria es 8
                        // DADOS;3;5;8
                        String resultadoDados = "DADOS;"
                                + valorDado1 + ";"
                                + valorDado2 + ";"
                                + pasos;

                        // enviarRespuesta() es un método de la clase Server.
                        // "posicion" identifica la conexión del jugador que lanzó los dados.
                        // "resultadoDados" contiene dado 1, dado 2 y el total obtenido.
                        for (int i = 0; i < jugadoresConectados.length; i++) {
                            if (jugadoresConectados[i] != null && clientesSocket[i] != null && !clientesSocket[i].isClosed()) {

                                enviarRespuesta(i, resultadoDados);
                            }
                        }

                        // Si el Jugador quedó eliminado durante su propio movimiento (por alquiler o por una carta),
                            // primero se revisa si con eso queda un único jugador activo (fin de partida).
                        if (!jugador.esActivo()) {

                            RevisarFinPartida();

                            // Si la partida sigue, el Jugador eliminado ya no puede enviar TERMINAR_TURNO,
                                // así que el Server pasa el turno automáticamente para que no quede trabada.
                            if (juego.isEnCurso() && jugador == juego.obtenerJugadorActual()) {

                                PasarTurnoAutomaticamente();
                            }
                        }
                    }

                    // -------------------------------------------------
                    // COMPRAR_PROPIEDAD
                    // -------------------------------------------------

                    // Se verifica si la solicitud enviada por el jugador es COMPRAR_PROPIEDAD.
                        // Se llama al método comprarPropiedad() de juego.
                            // Se envía el jugador que realiza la compra y la propiedad donde se encuentra.
                    else if (solicitud.equals("COMPRAR_PROPIEDAD")) {

                        juego.comprarPropiedad(jugador, propiedad);

                        // Si la Propiedad ya fue comprada por el Jugador, se elimina la información que indicaba
                            // que existía una Propiedad disponible para comprar.
                        propiedadPendienteCompra = null;

                        // Se elimina el Jugador que tenía la decisión de comprar o no comprar.
                        jugadorPendienteCompra = null;

                        // Se reinicia el número de turno que estaba asociado con esa decisión.
                        turnoPendienteCompra = 0;

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

                        // Si el Jugador decidió no comprar la Propiedad disponible.
                            // Por lo tanto, se elimina la información que indicaba que existía una Propiedad disponible para comprar.
                        propiedadPendienteCompra = null;

                        // Se elimina el Jugador que tenía la decisión de comprar o no comprar.
                        jugadorPendienteCompra = null;

                        // Se reinicia el número de turno que estaba asociado con esa decisión.
                        turnoPendienteCompra = 0;

                        // Se envía una respuesta únicamente al jugador que realizó la solicitud.
                            // "posicion" identifica cuál conexión Socket se debe utilizar.
                                // El mensaje confirma que el jugador decidió no comprar.
                        enviarRespuesta(
                                posicion, "El jugador decidió no comprar la propiedad");
                    }

                    // -------------------------------------------------
                    // TERMINAR_TURNO
                    // -------------------------------------------------

                    else if (solicitud.equals("TERMINAR_TURNO")) {

                        // Se eliminan los datos de la Propiedad disponible para comprar
                        propiedadPendienteCompra = null;

                        // Se elimina el Jugador que tenía la decisión de comprar o no comprar.
                        jugadorPendienteCompra = null;

                        // Se reinicia el número de turno que estaba asociado con esa decisión.
                        turnoPendienteCompra = 0;

                        // finalizarTurno() es un método de la clase Juego.
                            // Se ejecuta después de que validarAccion() permitió terminar el turno.
                                // Permite finalizar el turno del jugador actual y continuar con el siguiente jugador.
                        juego.finalizarTurno();

                        // Al pasar el turno la partida pudo terminar (un solo jugador activo o límite de rondas).
                        RevisarFinPartida();

                        // actualizarClientes() es un método de la clase Server.
                        // Informa a todos los jugadores conectados que el estado del juego fue actualizado.
                        actualizarClientes();
                    }

                    // -------------------------------------------------
                    // CONSULTAR_ESTADO
                    // -------------------------------------------------

                    // Se verifica si la solicitud enviada por el jugador es CONSULTAR_ESTADO.
                    // Permite consultar la información actual del jugador.
                    // No es necesario que el jugador se encuentre en su turno para realizar esta consulta.
                    else if (solicitud.equals("CONSULTAR_ESTADO")) {

                            // obtenerJugadorActual() es un método de la clase Juego.
                                // Devuelve el objeto Jugador que tiene actualmente el turno de la partida.
                                    // El Jugador obtenido se guarda en la variable "jugadorActual".
                            Jugador jugadorActual = juego.obtenerJugadorActual();


                            // getIdentificador() es un método de la clase Jugador.
                                // Permite obtener el identificador del Jugador que tiene actualmente el turno.
                                    // Por ejemplo: J001, J002, J003 o J004.
                            String identificadorJugadorActual =
                                    jugadorActual.getIdentificador();

                            // GetNumRonda() es un método de la clase Juego.
                                // Devuelve el número de la ronda actual de la partida.
                                    // El número obtenido se guarda en la variable "rondaActual".
                        int rondaActual = juego.GetNumRonda();

                        // Se crea un String para guardar la información de todos los jugadores de la partida.
                            // La información se irá agregando posteriormente al recorrer el arreglo jugadoresConectados.
                        String estadoJugadores = "";

                        // Se recorren las posiciones del arreglo jugadoresConectados.
                            // Cada posición puede contener uno de los jugadores que participa en la partida.
                        for (int i = 0; i < jugadoresConectados.length; i++) {

                            // Se obtiene el Jugador guardado en la posición actual del arreglo.
                                // El Jugador obtenido se guarda temporalmente en "jugadorEstado".
                            Jugador jugadorEstado = jugadoresConectados[i];

                            // Se verifica que exista un Jugador guardado en la posición actual.
                                // Si jugadorEstado es diferente de null, se puede obtener su información.
                            if (jugadorEstado != null) {

                                // getIdentificador() es un método de la clase Jugador.
                                    // Permite obtener el identificador del Jugador que se está recorriendo actualmente.
                                        // El identificador obtenido se guarda en la variable "identificadorEstado".
                                String identificadorEstado = jugadorEstado.getIdentificador();

                                // getPosicionActual() es un método de la clase Jugador.
                                    // Permite obtener la posición actual en el tablero del Jugador que se está recorriendo.
                                        // La posición obtenida se guarda en la variable "posicionEstado".
                                int posicionEstado = jugadorEstado.getPosicionActual();

                                // esActivo() es un método de la clase Jugador.
                                    // Permite conocer si el Jugador que se está recorriendo continúa activo dentro de la partida.
                                        // El resultado true o false se guarda en la variable "activoEstado".
                                boolean activoEstado = jugadorEstado.esActivo();

                            // Se verifica si estadoJugadores ya contiene la información de otro Jugador.
                                // Si no está vacío, se agrega "|" para separar al Jugador anterior del Jugador actual.
                            if (!estadoJugadores.equals("")) {

                                estadoJugadores = estadoJugadores + "|";
                            }

                            // Se agrega al String estadoJugadores la información del Jugador que se está recorriendo.
                                // Se guarda su identificador, su posición actual en el tablero y si continúa activo en la partida.
                                    // El símbolo "," permite separar los datos correspondientes al mismo Jugador.
                            estadoJugadores = estadoJugadores
                                    + identificadorEstado + ","
                                    + posicionEstado + ","
                                    + activoEstado;
                            }

                        }

                                    // Posteriormente se agregará cada Propiedad junto con el Jugador que sea su propietario.
                                String estadoPropiedades = "";

                                // getTablero() es un método de la clase Juego.
                                    // Devuelve el objeto Tablero que pertenece a la partida actual.
                                        // El Tablero obtenido se guarda en la variable "tablero".
                                Tablero tablero = juego.getTablero();

                        // getNumeroCasillas() es un método de la clase Tablero.
                            // Devuelve la cantidad total de casillas que existen en el tablero.
                                // La cantidad obtenida se guarda en la variable "cantidadCasillas".
                        int cantidadCasillas = tablero.getNumeroCasillas();


                        // Se recorren todas las posiciones que existen dentro del Tablero.
                        for (int i = 0; i < cantidadCasillas; i++) {

                            // ObtenerNodo(i) es un método de la clase Tablero.
                                // Permite obtener el NodoCasilla que se encuentra en la posición indicada.
                                    // El nodo obtenido se guarda temporalmente en "nodoCasilla".
                            NodoCasilla nodoCasilla = tablero.ObtenerNodo(i);


                            // getCasilla() es un método de la clase NodoCasilla.
                                // Devuelve la Casilla que se encuentra almacenada dentro del nodo.
                                    // La casilla obtenida se guarda en la variable "casilla".
                            Casilla casilla = nodoCasilla.getCasilla();


                            // Se verifica si la Casilla que se está recorriendo corresponde a una Propiedad.
                                // El Tablero también contiene otros tipos de casillas, por lo que solamente
                                    // se necesita obtener la información de las que sean del tipo Propiedad.
                            if (casilla instanceof Propiedad) {

                                // Se convierte la Casilla a Propiedad.
                                    // Esto permite utilizar los métodos propios de la clase Propiedad.
                                        // La Propiedad obtenida se guarda en "propiedadTablero".
                                Propiedad propiedadTablero = (Propiedad) casilla;


                                // getIdentificador() es un método de la clase Propiedad.
                                    // Devuelve el identificador de la Propiedad que se está recorriendo.
                                        // El identificador se guarda en "identificadorPropiedadTablero".
                                String identificadorPropiedadTablero =
                                        propiedadTablero.getIdentificador();


                                // getPropietario() es un método de la clase Propiedad.
                                    // Devuelve el Jugador que actualmente es propietario de la Propiedad.
                                        // Si nadie ha comprado la Propiedad, devuelve null.
                                Jugador propietarioPropiedad =
                                        propiedadTablero.getPropietario();


                                // Se crea un String para guardar quién es el propietario de esta Propiedad.
                                String identificadorPropietario;


                                // Se verifica si la Propiedad todavía no pertenece a ningún Jugador.
                                    // Si getPropietario() devolvió null, se guarda "SIN_PROPIETARIO".
                                if (propietarioPropiedad == null) {

                                    identificadorPropietario = "SIN_PROPIETARIO";
                                }

                                // Si existe un propietario, se obtiene el identificador del Jugador.
                                else {

                                    // getIdentificador() es un método de la clase Jugador.
                                    // Devuelve el identificador del Jugador propietario de la Propiedad.
                                    identificadorPropietario =
                                            propietarioPropiedad.getIdentificador();
                                }


                                // Se verifica si estadoPropiedades ya contiene información de otra Propiedad.
                                    // Si ya contiene información, se agrega "|" para separar
                                        // la Propiedad anterior de la Propiedad actual.
                                if (!estadoPropiedades.equals("")) {

                                    estadoPropiedades = estadoPropiedades + "|";
                                }


                                // Se agrega al String estadoPropiedades la información de la Propiedad actual.
                                    // Primero se guarda el identificador de la Propiedad.
                                        // Después se guarda el identificador de su propietario.
                                estadoPropiedades = estadoPropiedades
                                        + identificadorPropiedadTablero + ","
                                        + identificadorPropietario;
                            }
                        }


                        // Se obtiene la lista de propiedades que pertenece al jugador.
                            // "jugador" es un objeto de la clase Jugador.
                                // getPropiedadesAdquiridas() es un método de la clase Jugador que devuelve la ListaSimplePropiedad de ese jugador.
                                 // La lista obtenida se guarda en la variable "propiedades".
                        ListaSimplePropiedad propiedades = jugador.getPropiedadesAdquiridas();


                        // Se obtiene la cantidad de propiedades que actualmente tiene el jugador.
                            // "propiedades" es un objeto de la clase ListaSimplePropiedad.
                                 // Tamaño() es un método de la clase ListaSimplePropiedad que devuelve la cantidad de propiedades guardadas en la lista.
                                    // La cantidad obtenida se guarda en la variable "cantidadPropiedades".
                        int cantidadPropiedades = propiedades.Tamaño();

                        // Sirve para guardar los identificadores de las propiedades que tiene el jugador.
                        String propiedadesJugador = "";

                        //  Se guarda un texto para indicar que el jugador todavía no tiene propiedades adquiridas.
                        if (cantidadPropiedades == 0) {

                            propiedadesJugador = "SIN_PROPIEDADES";
                        }


                        // Si el jugador tiene una o más propiedades, se recorre el listado para obtener sus identificadores.
                        else {

                            for (int i = 0; i < cantidadPropiedades; i++) {

                                // "propiedades" es un objeto de la clase ListaSimplePropiedad.
                                // Obtener(i) es un método de la clase ListaSimplePropiedad que devuelve la Propiedad guardada en la posición indicada.
                                // La propiedad obtenida se guarda en la variable "propiedadJugador".
                                Propiedad propiedadJugador = propiedades.Obtener(i);


                                // Se obtiene el identificador de la propiedad.
                                // "propiedadJugador" es un objeto de la clase Propiedad.
                                // getIdentificador() es un método de la clase Propiedad que devuelve el identificador de esa propiedad.
                                // El identificador obtenido se guarda en la variable "identificadorPropiedad".
                                String identificadorPropiedad = propiedadJugador.getIdentificador();


                                // Se agrega el identificador de la propiedad al String "propiedadesJugador".
                                propiedadesJugador = propiedadesJugador + identificadorPropiedad;

                                // Si quedan más propiedades, se agrega una coma para separar sus identificadores.
                                if (i < cantidadPropiedades - 1) {

                                    propiedadesJugador = propiedadesJugador + ",";
                                }
                            }
                        }


                        // Se obtiene el identificador del jugador.
                            // "jugador" es un objeto de la clase Jugador.
                                // getIdentificador() es un método de la clase Jugador que devuelve el identificador guardado en ese jugador.
                                     // El identificador obtenido se guarda en la variable "identificadorJugador".
                        String identificadorJugador = jugador.getIdentificador();


                        // Se obtiene el nombre del jugador.
                            // "jugador" es un objeto de la clase Jugador.
                                // getNombre() es un método de la clase Jugador que devuelve el nombre guardado en ese jugador.
                                    // El nombre obtenido se guarda en la variable "nombreJugador".
                        String nombreJugador = jugador.getNombre();


                        // Se obtiene el saldo actual del jugador.
                            // "jugador" es un objeto de la clase Jugador.
                                // getSaldo() es un método de la clase Jugador que devuelve el saldo actual de ese jugador.
                                    // El saldo obtenido se guarda en la variable "saldoJugador".
                        double saldoJugador = jugador.getSaldo();


                        // Se obtiene la posición actual del jugador en el tablero.
                            // "jugador" es un objeto de la clase Jugador.
                                // getPosicionActual() es un método de la clase Jugador que devuelve la posición actual de ese jugador en el tablero.
                                    // La posición obtenida se guarda en la variable "posicionActualJugador".
                        int posicionActualJugador = jugador.getPosicionActual();


                        // Se inicia el mensaje que posteriormente será enviado hacia la clase el Cliente.
                            // "ESTADO" permite identificar que la respuesta contiene la información del estado actual del jugador.
                        String estado = "ESTADO";

                        // El símbolo ";" permite separar este dato del siguiente dato.
                        estado = estado + ";" + identificadorJugador;

                        // Se agrega el nombre del jugador al mensaje.
                        estado = estado + ";" + nombreJugador;

                        // Se agrega el saldo actual del jugador al mensaje.
                        estado = estado + ";" + saldoJugador;

                        // Se agrega la posición actual del jugador al mensaje.
                        estado = estado + ";" + posicionActualJugador;


                        // Se agregan las propiedades adquiridas por el jugador al mensaje.
                            // Si el jugador no tiene propiedades, este dato contendrá "SIN_PROPIEDADES".
                        estado = estado + ";" + propiedadesJugador;

                        // Se agrega el identificador del Jugador que tiene actualmente el turno.
                            // "identificadorJugadorActual" contiene el identificador obtenido
                                // anteriormente mediante obtenerJugadorActual().
                        estado = estado + ";" + identificadorJugadorActual;


                        // Se agrega el número de la ronda actual de la partida.
                            // "rondaActual" contiene el número obtenido mediante GetNumRonda().
                        estado = estado + ";" + rondaActual;


                        // Se agrega la información de todos los Jugadores.
                            // "estadoJugadores" contiene el identificador, la posición y el estado
                                // activo de cada Jugador, separados mediante el símbolo "|".
                        estado = estado + ";" + estadoJugadores;


                        // Se agrega la información de las Propiedades del Tablero.
                            // "estadoPropiedades" contiene el identificador de cada Propiedad y el identificador del Jugador que sea su propietario.
                        // Si una Propiedad todavía no tiene propietario, se le asigna "SIN_PROPIETARIO".
                        estado = estado + ";" + estadoPropiedades;

                        // Se envía el estado únicamente al jugador que realizó la consulta.
                            // enviarRespuesta() es un método de la clase Server.
                                // "posicion" permite identificar cuál conexión Socket pertenece al jugador.
                                    // "estado" contiene toda la información que se preparó anteriormente.
                        enviarRespuesta(posicion, estado);
                    }


                    // -------------------------------------------------
                    // CONSULTAR_TRANSACCIONES
                    // -------------------------------------------------

                    // Se verifica si la solicitud enviada por el jugador es CONSULTAR_TRANSACCIONES.
                    // Esta solicitud permite consultar la información del historial de transacciones.
                    // No es necesario que el jugador se encuentre en su turno para realizar esta consulta.
                    else if (solicitud.equals("CONSULTAR_TRANSACCIONES")) {

                        // "juego" es un objeto de la clase Juego que utiliza Server para acceder a la información de la partida.
                        // getHistorial() es un método de la clase Juego que devuelve el objeto de la clase HistorialTransacciones.
                        // obtenerHistorial() es un método de la clase HistorialTransacciones que recorre las transacciones
                        //  almacenadas y las devuelve como un String.
                        String historial = juego.getHistorial().obtenerHistorial();

                        // Se verifica si hay un historia de transacciones
                        // transacciones registradas en HistorialTransacciones.
                        if (historial.equals("")) {

                            // enviarRespuesta() es un método de la clase Server que permite enviar un mensaje al Cliente conectado.
                            // "posicion" indica cuál Cliente debe recibir la respuesta.
                            // Si no existen transacciones, se envía un mensaje indicando que el historial se encuentra vacío.
                            enviarRespuesta(posicion, "HISTORIAL;SIN_TRANSACCIONES");
                        }

                        else {

                            // enviarRespuesta() es un método de la clase Server que permite enviar un mensaje al Cliente conectado.
                            // "posicion" indica cuál Cliente debe recibir la respuesta.
                            // "HISTORIAL;" identifica que la respuesta contiene información del historial de transacciones.
                            // "historial" contiene las transacciones obtenidas desde la clase HistorialTransacciones.
                            enviarRespuesta(posicion, "HISTORIAL;" + historial);
                        }
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

            // Se verifica si el Jugador tiene una Propiedad disponible para comprar.
                // Si propiedadPendienteCompra contiene null, el Jugador no cayó en una Propiedad disponible durante su turno.
            else if (propiedadPendienteCompra == null) {

                return false;
            }


            // Se verifica que la Propiedad disponible para comprar corresponda al mismo Jugador que intenta realizar la compra.
                // Evita que otro Jugador pueda comprar una Propiedad que no le corresponde durante su turno.
            else if (jugadorPendienteCompra != jugador) {

                return false;
            }


            // Se verifica que la Propiedad disponible para comprar corresponda al turno actual.
                // getNumTurno() es un método de la clase Juego.
                    // Si los números de turno son diferentes, la Propiedad disponible para comprar corresponde a un turno anterior.
            else if (turnoPendienteCompra != juego.getNumTurno()) {

                return false;
            }

            // Se verifica que exista una propiedad para comprar.
            else if (propiedad == null) {

                return false;
            }

            // Se verifica que la Propiedad donde se encuentra el Jugador sea la misma Propiedad que quedó disponible para comprar
            else if (propiedadPendienteCompra != propiedad) {

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

            // Se verifica si el Jugador tiene una Propiedad disponible para comprar.
                // Si propiedadPendienteCompra contiene null, el Jugador no cayó en una Propiedad disponible durante su turno.
            else if (propiedadPendienteCompra == null) {

                return false;
            }

            // Se verifica que la Propiedad disponible para comprar corresponda al mismo Jugador que intenta decidir no comprarla.
                // Si jugadorPendienteCompra es diferente de jugador, la decisión de no comprar no se permite.
            else if (jugadorPendienteCompra != jugador) {

                return false;
            }

            // Se verifica que la Propiedad disponible para comprar corresponda al turno actual.
                // getNumTurno() es un método de la clase Juego.
                    // Si los números de turno son diferentes, la Propiedad disponible para comprar corresponde a un turno anterior.
            else if (turnoPendienteCompra != juego.getNumTurno()) {

                return false;
            }

            // Se verifica que exista una propiedad en la posición actual del jugador.
                // Si propiedad contiene null, significa que el jugador no está sobre una propiedad.
                    // En ese caso no existe ninguna propiedad que pueda decidir no comprar.
            else if (propiedad == null) {

                return false;
            }

            // Se verifica que la Propiedad donde se encuentra el Jugador sea la misma Propiedad que quedó disponible para comprar.
            else if (propiedadPendienteCompra != propiedad) {

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

            // Se verifica si el jugador que intenta terminar el turno es diferente al jugador que tiene el turno actual.
                // obtenerJugadorActual() es un método de la clase Juego.
                    // Devuelve el objeto Jugador que tiene el turno en ese momento.
                        // Si ambos jugadores son diferentes, no se permite terminar el turno.
            if (jugador != juego.obtenerJugadorActual()) {

                return false;
            }

            // Se verifica si el jugador todavía NO ha lanzado los dados durante su turno.
                // getDadosLanzadosEsteTurno() es un método de la clase Juego.
                    // Devuelve true cuando los dados ya fueron lanzados durante el turno actual.
                        // Devuelve false cuando todavía no se han lanzado.
                            // El símbolo ! cambia true por false y false por true.
                                // Esta condición se cumple cuando el jugador todavía no ha lanzado.
            else if (!juego.getDadosLanzadosEsteTurno()) {

                return false;
            }

            // Si el jugador tiene el turno actual y ya lanzó los dados, se permite terminar el turno.
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

    // Método de la clase Server que permite informar a los demás jugadores conectados que el estado del juego fue actualizado.
    // Recibe "posicionExcluir", que corresponde a la posición de la conexión del Jugador que acaba de recibir una respuesta directa del Server.
    // Ese Jugador no recibirá el mensaje ACTUALIZAR_ESTADO mediante este método.
    public void actualizarOtrosClientes(int posicionExcluir) {

        // Se recorren las 4 posiciones disponibles en los arreglos de conexiones del Server.
            // Cada posición puede corresponder a la conexión Socket de uno de los 4 jugadores.
        for (int posicion = 0; posicion < 4; posicion++) {

            // Se compara la posición que se está recorriendo con "posicionExcluir".
            // Si ambas posiciones son diferentes, significa que esta conexión pertenece a uno de los otros jugadores que sí debe recibir la actualización.
            if (posicion != posicionExcluir) {

                // clientesSocket es un arreglo de la clase Server.
                    // clientesSocket[posicion] contiene la conexión Socket
                        // Primero se verifica que clientesSocket[posicion] sea diferente de null.
                            // isClosed() es un método de la clase Socket de Java.
                                // Devuelve true cuando la conexión ya fue cerrada.
                                    // El símbolo ! cambia el resultado, por lo que esta condición
                                        // se cumple únicamente cuando la conexión continúa abierta.
                if (clientesSocket[posicion] != null
                        && !clientesSocket[posicion].isClosed()) {

                    // enviarRespuesta() es un método de la clase Server.
                        // "posicion" identifica cuál conexión debe recibir el mensaje.
                            // "ACTUALIZAR_ESTADO" informa al otro Cliente que ocurrió un cambio en el estado de la partida.
                    enviarRespuesta(
                            posicion, "ACTUALIZAR_ESTADO");
                }
            }
        }
    }

}

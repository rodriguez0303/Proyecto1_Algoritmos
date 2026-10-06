package LogicaJuego;

// Socket es una clase que Java ya tiene implementada.
    // Permite establecer y mantener la conexión del cliente con el servidor.
import java.net.Socket;

// BufferedReader es una clase que Java ya tiene implementada.
    // Permite leer como texto las respuestas enviadas por el servidor.
import java.io.BufferedReader;

// PrintWriter es una clase que Java ya tiene implementada.
    // Permite enviar mensajes de texto desde el cliente hacia el servidor.
import java.io.PrintWriter;

// InputStreamReader es una clase que Java ya tiene implementada.
    // Permite preparar la información recibida por la conexión para que BufferedReader pueda leerla como texto.
import java.io.InputStreamReader;

// IOException es una clase que Java ya tiene implementada.
    // Permite manejar errores que pueden ocurrir durante la conexiónno la comunicación entre el cliente y el servidor.
import java.io.IOException;

//*****************************************************
//*****************************************************

// Esta clase permitirá conectarse con el servidor, enviar solicitudes y recibir las respuestas enviadas por el servidor.
public class Cliente {

    // Guarda el identificador único del cliente.
    private String id;

    // Guarda el nombre del cliente.
    private String nombre;

    // Guarda la dirección IP del servidor al que se conectará el cliente.
    private String ip;

    // Guarda el número de puerto utilizado para establecer la conexión
    // con el servidor.
    private int puerto;

    // Socket es una clase que Java ya tiene implementada.
        // Permitirá mantener la conexión entre este cliente y el servidor.
            // La conexión se utilizará para enviar solicitudes y recibir respuestas.
    private Socket socket;


    // BufferedReader es una clase que Java ya tiene implementada.
        // Permitirá leer las respuestas que el servidor envía hacia este cliente.
                // Se utilizará mientras la conexión Socket continúe activa.
    private BufferedReader entrada;

    // PrintWriter es una clase que Java ya tiene implementada.
        // Permitirá enviar mensajes de texto desde este cliente hacia el servidor.
            // Se utilizará para enviar las solicitudes que posteriormente serán procesadas por Server.
    private PrintWriter salida;

    //*****************************************************
    //*****************************************************

    // Se recibe los datos necesarios para identificar al cliente y establecer posteriormente la conexión con el servidor.
    public Cliente(String id, String nombre, String ip, int puerto) {

        // Guarda el identificador recibido en el atributo id del Cliente.
        this.id = id;

        // Guarda el nombre recibido en el atributo nombre del Cliente.
        this.nombre = nombre;

        // Guarda la dirección IP recibida en el atributo ip del Cliente.
        this.ip = ip;

        // Guarda el puerto recibido en el atributo puerto del Cliente.
        this.puerto = puerto;
    }

    //*****************************************************
    //*****************************************************

    // Método que permite establecer la conexión entre este Cliente y el servidor.
    public void conectar() {

        try {

            // Se crea un nuevo objeto de tipo Socket utilizando la clase Socket de Java.
                // "ip" contiene la dirección IP del servidor al que el Cliente desea conectarse.
                    // "puerto" contiene el número de puerto donde el Server está esperando conexiones.
                        // Si la conexión se establece correctamente, se guarda en la variable "socket".
            socket = new Socket(ip, puerto);

             // InputStreamReader es una clase que Java ya tiene implementada. Su función es actuar como un puente entre la información
                // que llega por el Socket y el texto que se quiere leer
                    // "lector" permitirá preparar esa información para que posteriormente pueda ser leída como texto.
                        // getInputStream: obtiene la información que el servidor envía ("Conexión válida")
            InputStreamReader lector = new InputStreamReader(socket.getInputStream());


            // Se crea el BufferedReader que permitirá recibir las respuestas enviadas por el servidor.
                // Se utiliza "lector" porque contiene la información que llega desde el servidor preparada para ser leída como texto.
            entrada = new BufferedReader(lector);

            // Se crea el PrintWriter que permitirá enviar mensajes desde el Cliente hacia el servidor.
                // socket.getOutputStream() obtiene el canal por donde saldrá la información mediante la conexión guardada en "socket".
                    // El PrintWriter permite enviar esa información como texto hacia el servidor.
                        // "true" permite que cada mensaje enviado con println() se envíe inmediatamente.
            salida = new PrintWriter(socket.getOutputStream(), true);

        }

        catch (IOException e) {

            // Se ejecuta si ocurre un problema al intentar crear la conexión con el servidor.
                // Por ejemplo, si no se puede establecer la conexión utilizando la IP y el puerto indicados.
            System.out.println(
                    "Error al conectar con el servidor"
            );
        }
    }
    //*****************************************************
    //*****************************************************

    // Método que permite enviar una solicitud desde el Cliente hacia el servidor.
    public void enviarSolicitud(String solicitud) {

        // Las solicitudes deben enviarse utilizando el formato que espera el Server:
//          // CONECTAR;identificador
//          // TIRAR_DADOS
//          // COMPRAR_PROPIEDAD
//          // NO_COMPRAR
//          // TERMINAR_TURNO
//          // CONSULTAR_ESTADO
//          // CONSULTAR_TRANSACCIONES


        // "salida" contiene el PrintWriter que fue preparado dentro del método conectar().
            // PrintWriter utiliza el canal de salida del Socket para enviar información hacia el servidor.
                // println(solicitud) permite enviar como texto la solicitud recibida por este método.
        salida.println(solicitud);
    }

    //*****************************************************
    //*****************************************************

    // Método que permite recibir una respuesta enviada por el servidor.
    public String recibirRespuesta() {

        try {

            // Las respuestas que actualmente puede enviar el Server hacia el Cliente son:

                // Respuestas relacionadas con la conexión:
                    // "Conexión válida"
                    // "Esta conexión ya tiene un jugador asignado"
                    // "Solicitud de conexión no válida"
                    // "Jugador no encontrado"
                    // "El jugador [identificador] ya está conectado"
                    // "No existe un jugador asignado a esta conexión"
                    // "No se pueden conectar más de 4 jugadores"

                // Respuestas relacionadas con las solicitudes del jugador:
                    // "El jugador decidió no comprar la propiedad"
                // Esstado del jugador:
                    // "ESTADO;identificador;nombre;saldo;posición;propiedades". // Ejemplo: "ESTADO;J001;Jugador 1;900.0;7;P01,P04"
                    // "Consulta de transacciones válida"
                    // "La acción no es válida"

                // Respuesta enviada cuando cambia el estado del juego:
                     // "ACTUALIZAR_ESTADO"

            // "entrada" contiene el BufferedReader que fue preparado dentro del método conectar().
                // BufferedReader utiliza el canal de entrada del Socket para recibir información desde el servidor.
                    // readLine() permite leer como texto una respuesta enviada por el Server.
            String respuesta = entrada.readLine();

            // Se retorna la respuesta recibida para que pueda ser utilizada posteriormente por el Cliente.
            return respuesta;

        }

        catch (IOException e) {

            // Se ejecuta si ocurre un problema al intentar recibir la respuesta enviada por el servidor.
            System.out.println("Error al recibir la respuesta del servidor");

            // Se retorna null porque no fue posible recibir una respuesta del servidor.
            return null;
        }
    }

    //*****************************************************
    //*****************************************************

    // Método que permite mostrar la información del estado del jugador recibido desde el servidor.
    public void mostrarEstado(String respuesta) {

        // Se verifica que se haya recibido una respuesta del servidor.
            // También se verifica que la respuesta comience con la palabra "ESTADO;".
        if (respuesta != null && respuesta.startsWith("ESTADO;")) {

            // split(";") es un método de la clase String.
                // Permite separar la respuesta cada vez que encuentra el símbolo ";".
                    // Cada dato separado se guarda en una posición diferente del arreglo "datos".
            String[] datos = respuesta.split(";");


            // Se verifica que el arreglo "datos" contenga los 6 datos esperados.
                // length permite conocer la cantidad de posiciones que contiene el arreglo.
                     // Si length es igual a 6, significa que la respuesta contiene todos los datos necesarios.
            if (datos.length == 6) {

                // La información queda almacenada de la siguiente manera:
                // datos[0] = "ESTADO"
                // datos[1] = identificador del jugador
                // datos[2] = nombre del jugador
                // datos[3] = saldo del jugador
                // datos[4] = posición actual del jugador
                // datos[5] = propiedades adquiridas del jugador


                System.out.println("******** ESTADO DEL JUGADOR ********");

                // Se muestra el identificador recibido desde el Server.
                System.out.println("Identificador: " + datos[1]);

                // Se muestra el nombre del jugador recibido desde el Server.
                System.out.println("Nombre: " + datos[2]);

                // Se muestra el saldo actual del jugador recibido desde el Server.
                System.out.println("Saldo: " + datos[3]);

                // Se muestra la posición actual del jugador recibido desde el Server.
                System.out.println("Posición actual: " + datos[4]);

                // Se muestran las propiedades del jugador adquiridas.
                System.out.println("Propiedades adquiridas: " + datos[5]);
            }


            // Si el arreglo no contiene los 6 datos esperados, significa que la respuesta está incompleta.
            else {

                // Se muestra un mensaje indicando que no fue posible mostrar correctamente el estado.
                System.out.println("La información del estado está incompleta");
            }
        }
    }

    //*****************************************************
    //*****************************************************

    // Método que permite cerrar la conexión Socket del Cliente con el servidor.
        // Se implementa para que un Cliente pueda finalizar su conexión cuando ya no necesita comunicarse con el Server.
            // Al liberar la posición que estaba utilizando, permite que esa posición pueda ser utilizada posteriormente por otro Cliente..
    public void desconectar() {

        try {

            // Se verifica que exista una conexión Socket.
            if (socket != null) {

                // Se verifica que la conexión todavía se encuentre abierta.
                if (socket.isClosed() == false) {

                    // Se cierra la conexión entre este Cliente y el servidor.
                    socket.close();
                }
            }

        }

        catch (IOException e) {

            // Se ejecuta si ocurre un problema al intentar cerrar la conexión Socket.
            System.out.println(
                    "Error al desconectar del servidor"
            );
        }
    }

    //*****************************************************
    //*****************************************************



}

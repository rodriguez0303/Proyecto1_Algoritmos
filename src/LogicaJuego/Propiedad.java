// Se indica que la clase PROPIEDAD pertenece al paquete LogicaJuego.
    // Este paquete agrupa las clases que forman parte de la lógica del juego.
        // Esto permite que PROPIEDAD pueda trabajar con otras clases del mismo paquete,
            // como CASILLA, Jugador, Juego, Banco y Transaccion.
package LogicaJuego;
//*******************************************************************************
//*******************************************************************************

// PROPIEDAD (extends) hereda los elementos de la clase CASILLA.
    // Esto permite que una PROPIEDAD tenga el comportamiento general de una CASILLA y además sus propias características.
public class Propiedad extends Casilla {

    // Atributo que guarda el identificador único de cada propiedad.
    private String identificador;

    // Atributo que guarda el precio necesario para comprar la propiedad.
    private double precioCompra;

    // Atributo que guarda el monto de alquiler de la propiedad.
    private double alquiler;

    // Atributo que guarda al jugador que es dueño de la propiedad.
        // Si la propiedad todavía no tiene dueño, este atributo podrá tener el valor null.
    private Jugador propietario;

    //*******************************************************************************
    //*******************************************************************************

    // Constructor de la clase PROPIEDAD. Recibe los datos necesarios para crear una nueva propiedad.
        // Estos valores se utilizan para guardar el nombre, identificador, precio de compra y alquiler de la propiedad.
    public Propiedad(String identificador, String nombre, double precioCompra, double alquiler) {

        // Se utiliza "super" para llamar al CONSTRUCTOR de la clase padre CASILLA.
            // PROPIEDAD hereda de CASILLA, por eso primero se debe enviar el nombre al constructor de CASILLA.
                // De esta manera CASILLA guarda el nombre y no necesitamos volver a crear el atributo "nombre" dentro de PROPIEDAD.
        super(nombre);

        // "identificador" es el valor que recibió el constructor.
            // "this.identificador" representa el atributo que pertenece al objeto PROPIEDAD.
        this.identificador = identificador;

        // Se guarda el precio de compra recibido en el atributo precioCompra.
            // "precioCompra" contiene el precio que recibió el constructor.
        this.precioCompra = precioCompra;

        // Se guarda el alquiler recibido en el atributo alquiler.
            // "alquiler" contiene el monto que recibió el constructor.
        this.alquiler = alquiler;

        // Al iniciar el juego las propiedades no tienen un propietario por eson se les asinga "null"
        this.propietario = null;
    }

    //*******************************************************************************
    //*******************************************************************************

    // Método que permite obtener el identificador de la propiedad.
    // Retorna el valor almacenado en el atributo "identificador".
        // Ejemplo del identificador de  una propiedad:
            // identificador = "P11",
            // nombre = "Cartago",
            // precioCompra = 200.0
            // alquiler = 50.0.
    public String getIdentificador() {
        return identificador;
    }

    //*******************************************************************************
    //*******************************************************************************

    // Método que permite obtener el precio de compra de la propiedad.
        // No recibe ningún parámetro porque el precio ya está guardado dentro del objeto PROPIEDAD.
    public double getPrecioCompra() {
        return precioCompra;
    }

    //*******************************************************************************
    //*******************************************************************************

    // Método que permite obtener el monto del alquiler de la propiedad.
        // No recibe ningún parámetro porque el alquiler ya está guardado dentro del objeto PROPIEDAD.
    public double getAlquiler() {
        return alquiler;
    }

    //*******************************************************************************
    //*******************************************************************************

    // Método que permite obtener al jugador que es propietario de la propiedad.
        // "Jugador" es una clase y se utiliza aquí como el tipo de dato que puede guardar la variable "propietario".
            // Ejemplo: si propietario contiene al Jugador ID001, retorna ese Jugador; si nadie la ha comprado todavía, retorna null.
    public Jugador getPropietario() {
        return propietario;
    }

    //*******************************************************************************
    //*******************************************************************************

    // Método que permite saber si la propiedad todavía está disponible para ser comprada.
        // La variable "propietario" guarda al Jugador que compró la propiedad.
            // Si "propietario" contiene null, significa que ningún Jugador es dueño de la propiedad y retorna true.
    public boolean isDisponible() {
        return propietario == null;
    }

    //*******************************************************************************
    //*******************************************************************************

    // Método que permite realizar la compra de una PROPIEDAD.
        // "jugador" contiene al Jugador que quiere comprar la PROPIEDAD.
            // "juego" contiene la información de la partida y permite utilizar el Banco.
    public boolean comprar(Jugador jugador, Juego juego) {

        // Se verifica si la PROPIEDAD todavía está disponible para ser comprada.
        if (isDisponible() == true) {

            // Se obtiene el Banco que funciona como intermediario para realizar los pagos del Juego.
                // El Banco se utilizará para cobrar el dinero al Jugador cuando compra la PROPIEDAD.
            Banco banco = juego.getBanco();

            // Se guarda en una variable el precio que debe pagar el Jugador por la PROPIEDAD.
                // "precioCompra" contiene el precio establecido cuando se creó esta PROPIEDAD.
            double montoCompra = precioCompra;

            // Se verifica si el Jugador tiene suficiente dinero para comprar la PROPIEDAD.
                // "getSaldo()" permite consultar cuánto dinero tiene actualmente el Jugador.
                    // El saldo debe ser igual o mayor al precio de compra para poder continuar.
            if (jugador.getSaldo() >= montoCompra) {

                // El Banco realiza el cobro del dinero que debe pagar el Jugador.
                    // "recibir()" toma el dinero del Jugador y lo pasa al Banco.
                        // El resultado se guarda en "compraPagada" para saber si el cobro se pudo realizar.
                boolean compraPagada = banco.recibir(jugador, montoCompra);

                // Se verifica si el Banco pudo realizar correctamente el cobro.
                if (compraPagada == true) {

                    // Se guarda al Jugador como propietario de esta PROPIEDAD.
                        // "propietario" deja de contener null y ahora contiene al Jugador que realizó la compra.
                    propietario = jugador;

                    // Se agrega esta PROPIEDAD a las propiedades que pertenecen al Jugador.
                     // "this" representa la PROPIEDAD actual que el Jugador acaba de comprar.
                    jugador.agregarPropiedad(this);


                    //***********************************************************************
                    // CREACIÓN DE LA TRANSACCION DE COMPRA
                    //***********************************************************************

                    // Se pide al Juego el identificador de la nueva Transaccion.
                        // Juego es el único que genera IDs (T1, T2, T3...), así no se repiten
                            // aunque la PROPIEDAD se libere y se vuelva a comprar.
                    String idTransaccion = juego.GenerarIdTransaccion();


                    // Se obtiene el número del turno en el que se realizó la compra.
                     // El número de turno pertenece al Juego porque el Juego controla los turnos de la partida.
                    int numeroTurno = juego.getNumTurno();

                    // Se guarda el tipo de Transaccion que se está realizando.
                        // Se utiliza "COMPRA_PROPIEDAD" porque el Jugador está comprando esta PROPIEDAD.
                            // Este valor será enviado a la clase Transaccion para identificar la operación realizada.
                    String tipoTransaccion = "COMPRA_PROPIEDAD";

                    // "jugadorOrigen" es la variable que guardará la identificación del Jugador del cual sale el dinero de la Transaccion.
                        // "jugador" es el Jugador que fue recibido como parámetro en el método comprar().
                            // "getIdentificador()" obtiene la identificación que está guardada dentro de ese Jugador..
                    String jugadorOrigen = jugador.getIdentificador();

                    // Se guarda el destino del dinero que corresponde al pago de la compra.
                        // "jugadorDestino" es la variable que guardará quién recibe el dinero de la Transaccion.
                            // En la compra el dinero no lo recibe otro Jugador, sino el Banco.
                    String jugadorDestino = "BANCO";

                    // Se guarda el monto de dinero que corresponde al pago realizado por la compra de la PROPIEDAD.
                        // "montoTransaccion" es la variable que guardará la cantidad de dinero que se registrará en la Transaccion.
                            // "montoCompra" contiene el precio de la PROPIEDAD que anteriormente fue cobrado al Jugador por medio del Banco.
                    double montoTransaccion = montoCompra;

                    //Se guarda una descripción de la transacción realizada.
                    // "getNombre()" llama al método getNombre() de CASILLA para obtener el nombre de esta PROPIEDAD.
                    String descripcion = "Compra de la propiedad " + getNombre();

                    // Se crea un objeto de la clase Transaccion con la información de la compra.
                        // "transaccionCompra" representa el registro de la operación que acaba de realizarse.
                    Transaccion transaccionCompra = new Transaccion(
                                                                        idTransaccion,
                                                                        numeroTurno,
                                                                        tipoTransaccion,
                                                                        jugadorOrigen,
                                                                        jugadorDestino,
                                                                        montoTransaccion,
                                                                        descripcion
                    );

                    // Se registra la Transaccion dentro del Juego.
                        // De esta forma la compra queda almacenada en el HistorialTransacciones.
                    juego.RegistrarTransaccion(transaccionCompra);

                    // Se retorna true para indicar que la compra de la PROPIEDAD se realizó correctamente.
                    return true;
                }
            }
        }

        // Se retorna false cuando la compra de la PROPIEDAD no pudo realizarse (ya tiene dueño o no hay saldo suficiente).
        return false;
    }

    //*******************************************************************************
    //*******************************************************************************

    // Se sobrescribe el método "ejecutar" que PROPIEDAD heredó de la clase CASILLA.
     // "@Override" indica que este método ya existe en la clase padre CASILLA y PROPIEDAD tendrá su propia versión.
        // "jugador" recibe al Jugador que cayó en esta PROPIEDAD.
            // "juego" recibe el objeto Juego correspondiente a la partida.
    @Override
    public void ejecutar(Jugador jugador, Juego juego) {

        // Se verifica si la propiedad ya tiene un propietario.
            // "isDisponible() == false" significa que "propietario" ya contiene un Jugador.
        if (isDisponible() == false) {

            // Si son jugadores diferentes, corresponde realizar el pago del alquiler.
            if (propietario != jugador) {

                // Se llama al método que se encargará de realizar el pago del alquiler.
                    // "jugador" contiene al Jugador que debe pagar.
                        // "juego" se envía porque dentro de Juego se encuentra el Banco de la partida.
                pagarAlquiler(jugador, juego);
            }
        }
    }

    //*******************************************************************************
    //*******************************************************************************

    // Método que se utiliza cuando un Jugador debe pagar el alquiler de una PROPIEDAD.
        // "jugador" contiene al Jugador que debe pagar el alquiler.
            // "juego" contiene la información de la partida, incluyendo el objeto Banco que utiliza el juego.
    public void pagarAlquiler(Jugador jugador, Juego juego) {

        // "Banco" es la clase y "banco" es la variable donde guardamos ese objeto.
        // Se necesita este objeto porque el método "transferir()" pertenece a la clase Banco.
        Banco banco = juego.getBanco();

        // Se guarda en una variable el Jugador que debe pagar el alquiler.
            // "jugador" contiene al Jugador que cayó en esta PROPIEDAD.
                // "jugadorQuePaga" define cuál es el jugador que tiene que pagar
        Jugador jugadorQuePaga = jugador;

        // Se guarda en otra variable el Jugador que debe recibir el alquiler.
            // "propietario" contiene al Jugador que compró la PROPIEDAD.
                // "jugadorQueRecibe" es la persona que recibe el dinero por el alquiler.
        Jugador jugadorQueRecibe = propietario;

        // Se guarda en una variable el monto que se debe pagar por el alquiler.
            // "alquiler" contiene el monto establecido para esta PROPIEDAD.
                // Por ejemplo: si alquiler = 310.0, entonces montoAlquiler tendrá el valor de 310.0.
        double montoAlquiler = alquiler;

        // Se verifica si el Jugador que debe pagar tiene suficiente dinero para cubrir el alquiler.
            // "jugadorQuePaga" contiene al Jugador que cayó en la PROPIEDAD de otro Jugador.
                // "getSaldo()" llama al método de la clase Jugador que permite obtener el dinero disponible del Jugador.
         if (jugadorQuePaga.getSaldo() >= montoAlquiler) {

            // Se utiliza el método "transferir()" que pertenece a la clase Banco.
             // "transferir()" pasa el dinero del Jugador que paga al Jugador propietario que recibe el alquiler.
                // El resultado se guarda en "pagoRealizado" para saber si la transferencia se realizó correctamente.
            boolean pagoRealizado = banco.transferir(jugadorQuePaga, jugadorQueRecibe, montoAlquiler);

        // Si el pago realizado contiene true, significa que el Banco pudo realizar el pago del alquiler.
            // El Banco ya redujo el saldo del Jugador que pagó.
                // También aumentó el saldo del Jugador propietario.
            if (pagoRealizado == true) {

                // "numeroTurno" es la variable que guardará el número del turno en que ocurrió la Transaccion.
                    // "juego" contiene la información de la partida.
                        // "getNumTurno()" es el método de la clase Juego que permite obtener el número del turno actual.
                int numeroTurno = juego.getNumTurno();

                // Se pide al Juego el identificador de la nueva Transaccion de alquiler.
                    // "idTransaccion" es la variable que permitirá identificar esta Transaccion.
                        // Juego es el único que genera IDs (T1, T2, T3...), así no se repiten.
                String idTransaccion = juego.GenerarIdTransaccion();

                // "tipoTransaccion" es la variable que guardará el tipo de operación realizada.
                 // Se utiliza "PAGO_ALQUILER" porque el Jugador está pagando el alquiler de una PROPIEDAD.
                     // Este valor será enviado posteriormente al constructor de la clase Transaccion.
                String tipoTransaccion = "PAGO_ALQUILER";

                // "jugadorOrigen" es la variable que guardará la identificación del Jugador que paga el alquiler.
                    // "jugadorQuePaga" contiene al Jugador que cayó en la PROPIEDAD de otro Jugador.
                        // "getIdentificador()" obtiene la identificación que está guardada dentro de ese Jugador.
                String jugadorOrigen = jugadorQuePaga.getIdentificador();

                // Se guarda la identificación del Jugador que recibe el dinero de la Transaccion.
                    // "jugadorDestino" es la variable que guardará la identificación del Jugador que recibe el alquiler.
                        // "jugadorQueRecibe" contiene al Jugador que es propietario de la PROPIEDAD.
                            // "getIdentificador()" obtiene la identificación que está guardada dentro de ese Jugador.
                String jugadorDestino = jugadorQueRecibe.getIdentificador();

                // Se guarda el monto de dinero que corresponde al pago realizado por el alquiler de la PROPIEDAD.
                    // "montoTransaccion" es la variable que guardará la cantidad de dinero que se registrará en la Transaccion.
                        // "montoAlquiler" contiene el monto del alquiler de esta PROPIEDAD que anteriormente fue pagado por medio del Banco.
                double montoTransaccion = montoAlquiler;

                // "descripcion" es la variable que guardará el texto que describe la operación realizada.
                    // "getNombre()" llama al método getNombre() de CASILLA para obtener el nombre de esta PROPIEDAD.
                String descripcion = "Pago de alquiler de la propiedad " + getNombre();

                // Se crea un objeto de la clase Transaccion con la información del pago del alquiler.
                 // "Transaccion" es la clase que permite guardar la información de una operación realizada durante el Juego.
                    // "transaccionAlquiler" es la variable que guardará la nueva Transaccion correspondiente al pago del alquiler.
                        // "new Transaccion()" crea el nuevo objeto utilizando los datos que anteriormente se guardaron en las variables.
                Transaccion transaccionAlquiler = new Transaccion(
                                                                    idTransaccion,
                                                                    numeroTurno,
                                                                    tipoTransaccion,
                                                                    jugadorOrigen,
                                                                    jugadorDestino,
                                                                    montoTransaccion,
                                                                    descripcion
                );

                // "juego" contiene la información de la partida que se está ejecutando.
                // "RegistrarTransaccion()" es el método de la clase Juego encargado de guardar una Transaccion.
                // "transaccionAlquiler" contiene toda la información del pago de alquiler
                juego.RegistrarTransaccion(transaccionAlquiler);

            }
         }
        // El pago del alquiler no se pudo realizar.
        else {
            // El Banco no pudo realizar la transferencia, porque el jugador no tiene suficiente dinero.
                // y Como el alquiler es un pago obligatorio, el Jugador debe quedar eliminado.
            // Se utiliza el método "eliminar()" que pertenece a la clase Jugador.
                // El método "eliminar()" cambia el estado del Jugador para indicar que ya no continúa activo en la partida.
            jugadorQuePaga.eliminar();
         }

    }

    public void liberarPropiedad() {
        propietario = null;
    }

    //*******************************************************************************
    //*******************************************************************************
}

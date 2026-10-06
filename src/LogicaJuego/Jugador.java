package LogicaJuego;
public class Jugador {
    private String identificador;
    private String nombre;
    private double saldo;
    private int posicionActual;
    private boolean activo;

    //Se guarda la lista de Propiedades que ha adquirido el Jugador durante la partida.
    private ListaSimplePropiedad propiedadesAdquiridas;

    public Jugador(String identificador, String nombre, double saldo) {
        this.identificador = identificador;
        this.nombre = nombre;
        this.saldo = saldo;
        this.posicionActual = 0; // Inicializa la posición en 0
        this.activo = true; // Inicializa el jugador como activo

        // Se crea una nueva lista de Propiedades para el Jugador (ListaSimplePropiedad).
            // "ListaSimplePropiedad" es la clase que representa la estructura lineal
        this.propiedadesAdquiridas = new ListaSimplePropiedad();
    }

    //*******************************************************************************
    //*******************************************************************************

    // Método que permite agregar una Propiedad a la lista de Propiedades adquiridas por este Jugador.
        // "Propiedad" es el tipo de dato que recibe el método y corresponde a la clase Propiedad.
            // "propiedad" corresponde a la Propiedad que el Jugador acaba de adquirir.
    public void agregarPropiedad(Propiedad propiedad) {

        // "propiedadesAdquiridas" es la ListaSimplePropiedad que pertenece a este Jugador.
            // Se utiliza el método Agregar() de ListaSimplePropiedad.
                // Se envía "propiedad" como parámetro para que sea guardada en un nuevo Nodo de la lista.
        propiedadesAdquiridas.Agregar(propiedad);
    }

    //*******************************************************************************
    //*******************************************************************************

    // Permite obtener la lista de Propiedades que ya ha adquirido este Jugador.
        // "ListaSimplePropiedad" es el tipo de dato que devuelve este método.
    public ListaSimplePropiedad getPropiedadesAdquiridas() {

        // "propiedadesAdquiridas" contiene las Propiedades que han sido agregadas al Jugador.
        return propiedadesAdquiridas;
    }
    //*******************************************************************************
    //*******************************************************************************

    public String getIdentificador() {
        return identificador;
    }
    public String getNombre() {
        return nombre;
    }
    public double getSaldo() {
        return saldo;
    }
    public int getPosicionActual() {
        return posicionActual;
    }
    public boolean esActivo() {
        return activo;
    }
    public void modificarSaldo(double monto) {
        saldo += monto;
    }
    public void setPosicionActual(int nuevaPosicion) {
        this.posicionActual = nuevaPosicion;
    }

    // Método que permite eliminar al Jugador de la partida.
        // Antes de dejarlo inactivo, se liberan todas las Propiedades que había adquirido para que puedan volver a ser compradas.
    public void eliminar() {

        // "propiedadesAdquiridas" pertenece a la clase Jugador y contiene las Propiedades que este Jugador compró.
            // Tamaño() pertenece a ListaSimplePropiedad y permite conocer cuántas Propiedades debemos recorrer.
        for (int i = 0; i < propiedadesAdquiridas.Tamaño(); i++) {

            // Obtener() pertenece a la clase ListaSimplePropiedad.
                // Permite obtener la Propiedad almacenada en la posición indicada por "i".
            Propiedad propiedad = propiedadesAdquiridas.Obtener(i);

            // liberarPropiedad() pertenece a la clase Propiedad.
                // Cambia el propietario de esa Propiedad a null.
                    // De esta forma la Propiedad vuelve a quedar disponible para que otro Jugador pueda comprarla.
            propiedad.liberarPropiedad();
        }

        // Vaciar() pertenece a la clase ListaSimplePropiedad.
            // se eliminan las referencias almacenadas en la lista de Propiedades del Jugador.
        propiedadesAdquiridas.Vaciar();

        // "activo" pertenece a la clase Jugador.
            // false indica que el Jugador ya no continúa participando en la partida.
        this.activo = false;
    }
}
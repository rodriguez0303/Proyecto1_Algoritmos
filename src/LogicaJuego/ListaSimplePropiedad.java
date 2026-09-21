package LogicaJuego;

/**
 * Estructura lineal creada para almacenar las propiedades adquiridas por un Jugador
 *
 * Cada Nodo guarda:
 * - Una Propiedad.
 * - Y una referencia al siguiente Nodo.
 *
 * Ejemplo:Propiedad1 -> Propiedad2 -> Propiedad3 -> null
 */
public class ListaSimplePropiedad {

    // Cada Nodo guarda una Propiedad.
    private class Nodo {

        // Se crea una variable llamada Dato de tipo Propiedad (clase Propiedad).
            // Esta variable permitirá guardar una Propiedad dentro del Nodo.
        Propiedad Dato;

        // Se crea una variable llamada Siguiente de tipo Nodo (clase Nodo).
            // Esta variable permitirá guardar la referencia al siguiente Nodo de la lista.
                // Si no existe otro Nodo después, Siguiente tendrá el valor null.
        Nodo Siguiente;

        // El constructor recibe la Propiedad que se quiere almacenar dentro del Nodo.
        Nodo(Propiedad Dato) {

            // "this.Dato" corresponde al dato de este Nodo.
                // "Dato" corresponde a la Propiedad recibida como parámetro.
            this.Dato = Dato;

            // Cuando se crea un nuevo Nodo todavía no está conectado con otro Nodo.
                // Por eso su variable Siguiente inicia en null.
            this.Siguiente = null;
        }
    }

    //*******************************************************************************
    //*******************************************************************************

    // Se guarda una referencia al primer Nodo de la lista..
    private Nodo Primero;

    // Se guarda una referencia al último Nodo de la lista.
    private Nodo Ultimo;

    // Se guarda la cantidad de Propiedades que actualmente existen dentro de la lista.
    private int Tamaño;

    // Cuando se crea una nueva lista, el Jugador todavía no tiene ninguna Propiedad almacenada.
    public ListaSimplePropiedad() {

        // Todavía no existe un primer Nodo.
        this.Primero = null;

        // Todavía no existe un último Nodo.
        this.Ultimo = null;

        // La lista comienza con 0 Propiedades.
        this.Tamaño = 0;
    }

    //*******************************************************************************
    //*******************************************************************************

    // Se agrega una nueva Propiedad al final de la lista.
        // "Propiedad" es el tipo de dato que recibe el método (clase Propieda).
            // "propiedad" es la Propiedad que se desea guardar.
    public void Agregar(Propiedad propiedad) {

        // Se crea un nuevo Nodo para guardar la Propiedad recibida.
            // "Nuevo" guardará el Nodo que se acaba de crear.
                // Dentro de ese Nodo quedará almacenada "propiedad".
        Nodo Nuevo = new Nodo(propiedad);

        // Se verifica si la lista se encuentra vacía (jugador sin propiedades).
        if (Vacio() == true) {

            // Como todavía no existe ningún Nodo, el nuevo Nodo pasa a ser el primero de la lista.
            Primero = Nuevo;

            // Como solamente existe este Nodo, también pasa a ser el último de la lista.
            Ultimo = Nuevo;
        }
        else {
            // Cómo ya existen nodos previos (propiedades)
                // el último nodo actual comienza a apuntar hacia el nuevo Nodo.
            Ultimo.Siguiente = Nuevo;

            // y el nuevo Nodo pasa a convertirse en el último Nodo de la lista.
            Ultimo = Nuevo;
        }

        // se aumenta en 1 la cantidad de elementos de la lista tras agregar el nodo.
        Tamaño++;
    }

    //*******************************************************************************
    //*******************************************************************************

    // Permite obtener una Propiedad según su posición dentro de la lista.
    public Propiedad Obtener(int Posicion) {

        // Se valida que la posición de búsqueda en la lista sea válido
            // No menor a la posición 0 ni mayor a la cantidad de propiedades que tiene el juego
        if (Posicion < 0 || Posicion >= Tamaño) {

            // no existe una Propiedad que podamos devolver.
            return null;
        }

        // Se comienza el recorrido de los Nodos desde el primer Nodo de la lista.
            // Al nodo "Actual" se le asigna el valor de primero
        Nodo Actual = Primero;

        //Este contador permitirá saber cuándo se llega a la posición solicitada.
        int Contador = 0;

        // Se avanza mientras  se llega a la posición solicitada.
        while (Contador < Posicion) {

            // Se avanza hacia el siguiente Nodo.
            Actual = Actual.Siguiente;

            Contador++;
        }

        // Cuando termina el recorrido,"Actual" corresponde al Nodo de la posición solicitada.
            // Se devuelve la Propiedad almacenada en ese Nodo.
        return Actual.Dato;
    }

    //*******************************************************************************
    //*******************************************************************************

    // Función que permitirá eliminaa una Propiedad específica de la lista.
        // La Propiedad será eliminada solamente de la lista de Propiedades del Jugador.
    public boolean Eliminar(Propiedad propiedad) {

        // Si la lista está vacía no existen nodos por eliminar es porque no jugadores con propiedades.
        if (Vacio() == true) {

            return false;
        }

        // Si solo existe un Nodo se verifica si la Propiedad que se quiere eliminar se encuentra ahi .
        if (Primero.Dato == propiedad) {

            // Como solamente existe un Nodo, "Primero.Siguiente" tiene el valor null.
                // Esto ocurre porque no existe ningún otro Nodo después de "Primero".
                    // Por lo tanto, a "Primero" se le asigna null y la lista queda sin un primer Nodo.
            Primero = Primero.Siguiente;

            // Al eliminar una Propiedad,se tiene que disminuir el tamaño de la lista en 1.
            Tamaño--;

            // Se verifica si después de eliminar la lista quedó completamente vacía.
            if (Tamaño == 0) {

                // Si ya no existe ningún Nodo, tampoco puede existir un último Nodo.
                Ultimo = null;
            }

            // La Propiedad fue encontrada y eliminada correctamente.
            return true;
        }

        // Ya se revisó el primer Nodo y la propiedad no está ahí por eso
            // Se guarda el primer Nodo en la variable "Anterior".
        Nodo Anterior = Primero;

        // Se comienza a revisar desde el segundo Nodo.
            // El primer Nodo ya fue revisado anteriormente.
        Nodo Actual = Primero.Siguiente;

        // Se continúa recorriendo los Nodos mientras exista un valor diferente de Null
        while (Actual != null) {

            // Se verifica si el Nodo actual contiene la Propiedad que se necesita eliminar.
            if (Actual.Dato == propiedad) {

                //Cuando se va a eliminar un nodo que esta en el centro
                    // El Nodo anterior deja de apuntar al Nodo que se eliminará
                        // y ahora apunta directamente al Nodo que estaba después.
                            // [P01] ─────→ [P05] ─────→ [P08] ─────→ null
                Anterior.Siguiente = Actual.Siguiente;

                // Se verifica si el Nodo eliminado era el último Nodo de la lista.
                if (Actual == Ultimo) {

                    // Si se elimina el último Nodo, el Nodo anterior pasa a ser el nuevo último.
                    Ultimo = Anterior;
                }

                // Como se eliminó una Propiedad, se debe disminuirel tamaño de la lista en 1.
                Tamaño--;

                // La Propiedad fue encontrada y eliminada.
                return true;
            }

            // El Nodo actual pasa a convertirse en el Nodo anterior.
            Anterior = Actual;

            // Se avanza hacia el siguiente Nodo de la lista.
            Actual = Actual.Siguiente;
        }

        // Si se recorrió toda la lista y no se encontró la Propiedad, significa que no estaba almacenada.
        return false;
    }

    //*******************************************************************************
    //*******************************************************************************

    //Método que devuelve la primera Propiedad almacenada en la lista.
    public Propiedad ObtenerPrimero() {

        // Se verifica si la lista está vacía.
        if (Vacio() == true) {

            // Si está vacía,no existe una primera Propiedad.
            return null;
        }

        // Se devuelve la Propiedad almacenada dentro del primer Nodo.
        return Primero.Dato;
    }

    //*******************************************************************************
    //*******************************************************************************

    // Método que devuelve la cantidad de Propiedades
    public int Tamaño() {

        return Tamaño;
    }


    //*******************************************************************************
    //*******************************************************************************

    // Método que valida si la lista se encuentra vacía.
    public boolean Vacio() {

        return Tamaño == 0;
    }

    //*******************************************************************************
    //*******************************************************************************

    // Se elimina todas las referencias a Propiedades almacenadas dentro de esta lista.
        // Este método NO cambia el propietario de las Propiedades.
            // Solamente deja vacía la lista del Jugador.
    public void Vaciar() {

        // Se elimina la referencia al primer Nodo.
        Primero = null;

        // Se elimina la referencia al último Nodo.
        Ultimo = null;

        // La cantidad de Propiedades vuelve a ser 0.
        Tamaño = 0;
    }
}

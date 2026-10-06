package LogicaJuego;

// Esta clase permite representar las CasillasEspeciales que se encuentran en el Tablero.
    // "extends Casilla" indica que CasillaEspecial hereda de la clase Casilla.
public class CasillaEspecial extends Casilla {

    // Tipo que tendrá la CasillaEspecial.
        // Permitirá diferenciar entre SALIDA, EDIFICIO_D3, ESPECIAL e IR_D3.
    private String tipo;


    //*****************************************************
    //*****************************************************

    // Se recibe "nombre", que será utilizado por la clase padre Casilla.
        // También recibe "tipo", que pertenece directamente a CasillaEspecial.
    public CasillaEspecial(String nombre, String tipo) {

        // super() permite llamar al constructor de la clase padre Casilla.
            // Es decir casilla especial reutiliza el atributo nombre que ya maneja Casilla.
        super(nombre);

        // Este valor será utilizado posteriormente por el método ejecutar().
        this.tipo = tipo;
    }


    //*****************************************************
    //*****************************************************

    // Método propio de CasillaEspecial que permite consultar el tipo de la casilla.
    // Retorna un String porque el atributo "tipo" fue declarado como String.
    public String getTipo() {

        // Retorna el valor almacenado en el atributo tipo de esta CasillaEspecial.
        return tipo;
    }


    //*****************************************************
    //*****************************************************

    // @Override indica que CasillaEspecial está sobrescribiendo el método ejecutar () de la clase padre
        // "jugador" es un objeto de la clase Jugador y representa al Jugador que llegó a esta casilla.
            // "juego" es un objeto de la clase Juego y permite realizar acciones sobre la partida, como mover al Jugador a otra posición.

    @Override
    public void ejecutar(Jugador jugador, Juego juego) {

        // Si el "tipo" de casilla obtenido corresponde a "Salida"
        if (tipo.equals("SALIDA")) {

            // getNombre() pertenece a la clase Jugador.
                // Se utiliza para obtener el nombre del Jugador que llegó a esta casilla.
            System.out.println(
                    jugador.getNombre() + " llegó a Salida"
            );
        }

        // Si el "tipo" de casilla obtenido corresponde a "EDIFICIO_D3"
        else if (tipo.equals("EDIFICIO_D3")) {

            // getNombre() es un método de la clase Jugador.
                // Permite mostrar cuál Jugador llegó al Edificio D3.
            System.out.println(
                    jugador.getNombre() + " llegó al Edificio D3"
            );
        }


        // Si el "tipo" de casilla obtenido corresponde a "ESPECIAL"
            // Esta casilla no modifica el saldo ni la posición del Jugador.
        else if (tipo.equals("ESPECIAL")) {

            // Se utiliza getNombre() de la clase Jugador para indicar cuál Jugador llegó a la casilla.
            System.out.println(
                    jugador.getNombre() + " llegó a la casilla Especial"
            );
        }

        // Se valida si el tipo de CasillaEspecial corresponde a IR_D3.
            // Esta casilla debe enviar al Jugador directamente hasta el Edificio D3 ubicado en la posición 6.
        else if (tipo.equals("IR_D3")) {

            // "juego" es un objeto de la clase Juego recibido como parámetro.
                // MoverJugadorA() es un método que pertenece a la clase Juego.
                    // El método recibe el objeto "jugador" de la clase Jugador y la posición 6 como destino.
                        // Juego.MoverJugadorA() se encarga de mover al Jugador hasta la posición 6, que corresponde al Edificio D3.
            juego.MoverJugadorA(jugador, 6);

            // getNombre() pertenece a la clase Jugador.
                // Después del movimiento se utiliza para mostrar cuál Jugador fue enviado al Edificio D3.
            System.out.println(
                    jugador.getNombre() + " fue enviado al Edificio D3"
            );
        }
        // Se muestra un mensaje indicando que CasillaEspecial no reconoce el tipo recibido.
            // En este caso no se modifica la posición ni el saldo del Jugador.
        else {
            System.out.println("Tipo de CasillaEspecial no válido");
        }
    }

}
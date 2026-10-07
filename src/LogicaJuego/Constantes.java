package LogicaJuego;

/**
 * Clase Constantes: Agrupa los valores fijos que usan varias clases del
 * proyecto, para no repetir ni tener el mismo valor
 * definido por separado en cada archivo.
 *
 * Todos los campos son "public static final": se acceden directamente
 * como Constantes.NOMBRE, sin necesidad de crear un objeto Constantes
 * (por eso el constructor es privado, para impedir "new Constantes()").
 */

public final class Constantes {

    private Constantes() {
        // Clase de solo constantes; no debe instanciarse
    }

    // Cantidad mínima de casillas del tablero
    public static final int NUMERO_CASILLAS = 24;

    // Posiciones fijas de las casillas de evento, una en el centro de cada lado.
    public static final int[] POSICIONES_EVENTO = {3, 9, 15, 21};

    // Saldo con el que arranca el Banco, para que pueda pagar cartas de evento
    public static final double SALDO_INICIAL_BANCO = 100000;

    // Monto que el Banco le paga a un jugador cada vez que pasa o cae en Salida (igual que la GUI)
    public static final double PREMIO_SALIDA = 200;

    // Alquiler que se cobra al caer en una propiedad ajena (igual que la GUI)
    public static final double ALQUILER_PROPIEDAD = 100;

    // Posiciones fijas de las esquinas del tablero (casillas especiales)
    public static final int POSICION_SALIDA = 0;
    public static final int POSICION_D3 = 6;
    public static final int POSICION_ESPECIAL = 12;
    public static final int POSICION_IR_D3 = 18;
}
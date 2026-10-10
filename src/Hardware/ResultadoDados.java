package Hardware;

/**
 * Resultado de un lanzamiento de dos dados asociado a un jugador.
 * Guarda valores inmutables y valida que ambos dados esten entre 1 y 6.
 */
public class ResultadoDados {

    private final String jugador;
    private final int dado1;
    private final int dado2;

    /**
     * Crea y valida el resultado comunicado por el hardware.
     *
     * @param jugador identificador del jugador que lanzo
     * @param dado1 valor del primer dado (1 a 6)
     * @param dado2 valor del segundo dado (1 a 6)
     * @throws IllegalArgumentException si el jugador o los dados no son validos
     */
    public ResultadoDados(String jugador, int dado1, int dado2) {

        if (jugador == null || jugador.isBlank()) {
            throw new IllegalArgumentException("El jugador no puede estar vacío");
        }

        if (dado1 < 1 || dado1 > 6 || dado2 < 1 || dado2 > 6) {
            throw new IllegalArgumentException("Los dados deben tener valores entre 1 y 6");
        }

        this.jugador = jugador;
        this.dado1 = dado1;
        this.dado2 = dado2;
    }

    /**
     * @return identificador del jugador del lanzamiento
     */
    public String getJugador() {
        return jugador;
    }

    /**
     * @return valor del primer dado
     */
    public int getDado1() {
        return dado1;
    }

    /**
     * @return valor del segundo dado
     */
    public int getDado2() {
        return dado2;
    }

    /**
     * Calcula el desplazamiento correspondiente a la tirada.
     *
     * @return suma de los dos dados
     */
    public int getSuma() {
        return dado1 + dado2;
    }

    /**
     * @return true cuando ambos dados tienen el mismo valor
     */
    public boolean esDoble() {
        return dado1 == dado2;
    }

    /**
     * Ofrece una representacion de diagnostico del resultado.
     *
     * @return texto con jugador, valores y suma de los dados
     */
    @Override
    public String toString() {
        return "ResultadoDados{" +
                "jugador='" + jugador + '\'' +
                ", dado1=" + dado1 +
                ", dado2=" + dado2 +
                ", suma=" + getSuma() +
                '}';
    }
}

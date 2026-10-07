package Hardware;

public class ResultadoDados {

    private final String jugador;
    private final int dado1;
    private final int dado2;

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

    public String getJugador() {
        return jugador;
    }

    public int getDado1() {
        return dado1;
    }

    public int getDado2() {
        return dado2;
    }

    public int getSuma() {
        return dado1 + dado2;
    }

    public boolean esDoble() {
        return dado1 == dado2;
    }

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

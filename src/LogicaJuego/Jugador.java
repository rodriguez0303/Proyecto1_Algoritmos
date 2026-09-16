package LogicaJuego;
public class Jugador {
    private String identificador;
    private String nombre;
    private double saldo;
    private int posicionActual;
    private boolean activo;

    public Jugador(String identificador, String nombre, double saldo) {
        this.identificador = identificador;
        this.nombre = nombre;
        this.saldo = saldo;
        this.posicionActual = 0; // Inicializa la posición en 0
        this.activo = true; // Inicializa el jugador como activo
    }
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
    public void eliminar() {
        this.activo = false;
    }
}
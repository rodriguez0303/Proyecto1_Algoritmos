package LogicaJuego;
public class Transaccion {

    private String identificador;
    private int numeroTurno;
    private String tipo;
    private String jugadorOrigen;
    private String jugadorDestino;
    private double monto;
    private String descripcion;

    public Transaccion(String identificador, int numeroTurno, String tipo,
                       String jugadorOrigen, String jugadorDestino,
                       double monto, String descripcion) {
        this.identificador = identificador;
        this.numeroTurno = numeroTurno;
        this.tipo = tipo;
        this.jugadorOrigen = jugadorOrigen;
        this.jugadorDestino = jugadorDestino;
        this.monto = monto;
        this.descripcion = descripcion;
    }
    public String getIdentificador() {
        return identificador;
    }
    public int getNumeroTurno() {
        return numeroTurno;
    }
    public String getTipo() {
        return tipo;
    }
    public String getJugadorOrigen() {
        return jugadorOrigen;
    }
    public String getJugadorDestino() {
        return jugadorDestino;
    }
    public double getMonto() {
        return monto;
    }
    public String getDescripcion() {
        return descripcion;
    }
    @Override
    public String toString() {
        return identificador + "|Turno: " + numeroTurno 
        + "|Tipo: " + tipo 
        + "|Origen: " + jugadorOrigen     
        + "|Destino: " + jugadorDestino 
        + "|Monto: " + monto 
        + "|Descripcion: " + descripcion;
    }
}

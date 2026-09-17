package LogicaJuego;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaccion {

    private String identificador;
    private int numeroTurno;
    private String tipo;
    private String jugadorOrigen;
    private String jugadorDestino;
    private double monto;
    private String descripcion;
    private LocalDateTime fechaHora;

    public Transaccion(String identificador, int numeroTurno, String tipo,
                       String jugadorOrigen, String jugadorDestino,
                       double monto, String descripcion){
        this.identificador = identificador;
        this.numeroTurno = numeroTurno;
        this.tipo = tipo;
        this.jugadorOrigen = jugadorOrigen;
        this.jugadorDestino = jugadorDestino;
        this.monto = monto;
        this.descripcion = descripcion;
        this.fechaHora = LocalDateTime.now();
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
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
    @Override
    public String toString() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return identificador + "|Turno: " + numeroTurno 
        + "|Tipo: " + tipo 
        + "|Origen: " + jugadorOrigen     
        + "|Destino: " + jugadorDestino 
        + "|Monto: " + monto 
        + "|Descripcion: " + descripcion
        + "|FechaHora: " + fechaHora.format(formato);
    }
}

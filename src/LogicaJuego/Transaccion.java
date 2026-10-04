package LogicaJuego;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase Transaccion: Registro de una operación económica (punto 11).
 *
 * Cambios respecto a la versión anterior:
 * - "tipo" pasa de String a TipoTransaccion (enum), para que un error de
 *   escritura en el tipo lo detecte el compilador.
 * - Se agrega getResumen(), que es el nombre del método en el diagrama UML;
 *   toString() lo reutiliza para no repetir el formato.
 * - Origen y destino se mantienen como String (identificador del jugador o
 *   "BANCO"), porque el banco también puede ser origen o destino.
 */
public class Transaccion {

    private String identificador;
    private int numeroTurno;
    private TipoTransaccion tipo;
    private String jugadorOrigen;
    private String jugadorDestino;
    private double monto;
    private String descripcion;
    private LocalDateTime fechaHora;

    public Transaccion(String identificador, int numeroTurno, TipoTransaccion tipo,
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

    // * pendiente: constructor temporal para que Propiedad (que todavía pasa
    // "COMPRA_PROPIEDAD" y "PAGO_ALQUILER" como texto) siga compilando.
    // Convierte el texto al enum; si el texto no coincide con ningún valor
    // de TipoTransaccion, lanza IllegalArgumentException.
    // Borrarlo cuando Propiedad use TipoTransaccion directamente.
    public Transaccion(String identificador, int numeroTurno, String tipo,
                       String jugadorOrigen, String jugadorDestino,
                       double monto, String descripcion){
        this(identificador, numeroTurno, TipoTransaccion.valueOf(tipo),
                jugadorOrigen, jugadorDestino, monto, descripcion);
    }

    public String getIdentificador() {
        return identificador;
    }
    public int getNumeroTurno() {
        return numeroTurno;
    }
    public TipoTransaccion getTipo() {
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

    // Resumen de la transacción en una línea (lo usa el TXT del punto 13)
    public String getResumen() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return identificador + "|Turno: " + numeroTurno
                + "|Tipo: " + tipo
                + "|Origen: " + jugadorOrigen
                + "|Destino: " + jugadorDestino
                + "|Monto: " + monto
                + "|Descripcion: " + descripcion
                + "|FechaHora: " + fechaHora.format(formato);
    }

    @Override
    public String toString() {
        return getResumen();
    }
}
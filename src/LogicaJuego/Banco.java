package LogicaJuego;

/**
 * Representa el banco de la partida.
 * Se encarga de administrar pagos y transferencias
 * entre el banco y los jugadores.
 */
public class Banco {

    private double saldo;

    /**
     * Constructor por defecto.
     * El saldo inicial se establece temporalmente en 0.
     */
    public Banco() {
        this.saldo = 0;
    }

    /**
     * Constructor que permite definir un saldo inicial.
     *
     * @param saldoInicial saldo inicial del banco
     */
    public Banco(double saldoInicial) {
        this.saldo = saldoInicial;
    }

    /**
     * Devuelve los fondos que conserva actualmente el banco.
     */
    public double getSaldo() {
        return saldo;
    }

    /**
     * El banco paga dinero a un jugador.
     *
     * @param jugador jugador que recibe el dinero
     * @param monto cantidad a pagar
     * @return true si el pago se realizó correctamente
     */
    public boolean pagar(Jugador jugador, double monto) {

        if (jugador == null || monto <= 0 || saldo < monto) {
            return false;
        }

        saldo -= monto;
        jugador.modificarSaldo(monto);

        return true;
    }

    /**
     * El banco recibe dinero de un jugador.
     *
     * @param jugador jugador que realiza el pago
     * @param monto cantidad recibida
     */
    public boolean recibir(Jugador jugador, double monto) {

        if (jugador == null || monto <= 0 || jugador.getSaldo() < monto) {
            return false;
        }

        jugador.modificarSaldo(-monto);
        saldo += monto;
        return true;
    }

    /**
     * Intenta cobrar un pago obligatorio al jugador; si no dispone del saldo necesario, lo elimina de la partida.
     */
    public boolean recibirPagoObligatorio(Jugador jugador, double monto) {
        if (jugador == null || monto <= 0) {
            return false;
        }
        if (jugador.getSaldo() < monto) {
            jugador.eliminar();
            return false;
        } 
        return recibir(jugador, monto);
    }

    /**
     * Procesa un pago obligatorio entre jugadores y elimina al deudor cuando no puede cubrirlo.
     */
    public boolean transferirPagoObligatorio(Jugador origen, Jugador destino, double monto) {
        if (origen == destino || origen == null || destino == null || monto <= 0) {
            return false;
        }
        if (origen.getSaldo() < monto) {
            origen.eliminar();
            return false;
        }
        return transferir(origen, destino, monto);
    }

    /**
     * Transfiere dinero de un jugador a otro.
     *
     * @param origen jugador que paga
     * @param destino jugador que recibe
     * @param monto cantidad transferida
     * @return true si la transferencia se realizó correctamente
     */
    public boolean transferir(Jugador origen, Jugador destino, double monto) {

        if (origen == null || destino == null || monto <= 0 || origen.getSaldo() < monto || origen == destino) {
            return false;
        }
        origen.modificarSaldo(-monto);
        destino.modificarSaldo(monto);

        return true;
    }
}
package LogicaJuego;

public class PruebaHistorial {
    
    public static void main(String[] args) {
        HistorialTransacciones historial = new HistorialTransacciones();

        Transaccion transaccion1 = new Transaccion("T001", 1, TipoTransaccion.PAGO_ALQUILER, "Jugador1", "Jugador2", 100.0, "Pago de alquiler");
        Transaccion transaccion2 = new Transaccion("T002", 2, TipoTransaccion.PAGO_ENTRE_JUGADORES, "Jugador2", "Jugador3", 50.0, "Transferencia de dinero");
        Transaccion transaccion3 = new Transaccion("T003", 3, TipoTransaccion.COMPRA_PROPIEDAD, "Jugador1", "BANCO", 200.0, "Compra de propiedad");
        historial.agregar(transaccion1);
        historial.agregar(transaccion2);
        historial.agregar(transaccion3);

        boolean exportado = historial.exportarTXT("historial_transacciones.txt");
        System.out.println("Historial exportado: " + exportado);

        System.out.println("Historial completo:");
        historial.imprimirHistorial();
        System.out.println("Historial por tipo 'PAGO_ALQUILER':");
        historial.buscarPorTipo(TipoTransaccion.PAGO_ALQUILER);
        System.out.println("Historial por jugador 'Jugador1':");
        historial.buscarPorJugador("Jugador1");
        System.out.println("Historial inverso:");
        historial.imprimirHistorialInverso();

    }
}

package src;
public class PruebaJugador {
    public static void main(String[] args) {
        Jugador jugador1 = new Jugador("J001",
        "Mario",
        1500.0);
        System.out.println("JUGADOR INICIAL");
        System.out.println("ID: " + jugador1.getIdentificador());
        System.out.println("Nombre: " + jugador1.getNombre());
        System.out.println("Saldo: " + jugador1.getSaldo());
        System.out.println("Posición Actual: " + jugador1.getPosicionActual());
        System.out.println("Activo: " + jugador1.esActivo());

        jugador1.modificarSaldo(-200.0);
        jugador1.setPosicionActual(5);

        System.out.println("DESPUÉS DE MODIFICACIONES");
        System.out.println("Saldo: " + jugador1.getSaldo());
        System.out.println("Posición Actual: " + jugador1.getPosicionActual());

        jugador1.eliminar();
        System.out.println("DESPUÉS DE ELIMINAR");
        System.out.println("Activo: " + jugador1.esActivo());
    }
}

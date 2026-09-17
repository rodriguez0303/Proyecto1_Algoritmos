package LogicaJuego;

public class PruebaDado {

    public static void main(String[] args) {

        Dado dado1 = new Dado(1);
        Dado dado2 = new Dado(2);

        int valor1 = dado1.lanzar();
        int valor2 = dado2.lanzar();

        System.out.println("Dado 1: " + valor1);
        System.out.println("Dado 2: " + valor2);
        System.out.println("Total: " + (valor1 + valor2));
    }
}
package LogicaJuego;

/**
 * Prueba manual del lanzamiento y consulta de los dados.
 */
public class PruebaDado {

    /**
     * Ejecuta pruebas manuales de lanzamiento y almacenamiento de resultados.
     */
    public static void main(String[] args) {

        Dado dado1 = new Dado(1);
        Dado dado2 = new Dado(2);

        int valor1 = dado1.lanzar();
        int valor2 = dado2.lanzar();

        System.out.println("Dado 1: " + valor1);
        System.out.println("Dado 2: " + valor2);
        System.out.println("Total: " + (valor1 + valor2));

        boolean rangoCorrecto = true;

        for (int i = 0; i < 100; i++) {

            int resultado = dado1.lanzar();

            if (resultado < 1 || resultado > 6) {
                System.out.println("Error: el valor del dado está fuera del rango esperado: " + resultado);
                rangoCorrecto = false;
            }
        }
        if (rangoCorrecto) {
            System.out.println("Todos los valores del dado están dentro del rango esperado.");
        } else {
            System.out.println("Algunos valores del dado están fuera del rango esperado.");
        }

            System.out.println("ID dado 1: " + dado1.getId());
            System.out.println("ID dado 2: " + dado2.getId());
    }
}
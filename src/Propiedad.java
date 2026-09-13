package src;

/**
 * * NOTA: Clase mínima/temporal. Se crea únicamente para que el tipo
 * "Propiedad" exista y Juego (y Server) puedan compilar juntos, ya que
 * Server.java llama a propiedad.isDisponible() y propiedad.getPrecioCompra().
 *
 * La versión real (punto 7 del enunciado: identificador, nombre, precio
 * de compra, alquiler, propietario, y qué pasa cuando un jugador cae en
 * ella) se debe implementar
 */

public class Propiedad {

    private boolean disponible = true;
    private double precioCompra;

    public boolean isDisponible() {
        return disponible;
    }

    public double getPrecioCompra() {
        return precioCompra;
    }
}
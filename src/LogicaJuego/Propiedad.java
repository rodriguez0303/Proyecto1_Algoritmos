/**
 * NOTA: Clase mínima/temporal. Se crea únicamente para que el tipo
 * "Propiedad" exista y Juego (y Server) puedan compilar juntos, ya que
 * Server.java llama a propiedad.isDisponible() y propiedad.getPrecioCompra().
 * *
 * La versión real (punto 7 del enunciado: identificador, nombre, precio
 * de compra, alquiler, propietario, y qué pasa cuando un jugador cae en
 * ella) la debe implementar.
 **/
package LogicaJuego;

public class Propiedad {

    private String identificador;
    private String nombre;
    private double precioCompra;
    private double alquiler;
    private Jugador propietario;

    public Propiedad(String identificador, String nombre,
                     double precioCompra, double alquiler) {
        this.identificador = identificador;
        this.nombre = nombre;
        this.precioCompra = precioCompra;
        this.alquiler = alquiler;
        this.propietario = null;
    }

    public String getIdentificador() {
        return identificador;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecioCompra() {
        return precioCompra;
    }

    public double getAlquiler() {
        return alquiler;
    }

    public Jugador getPropietario() {
        return propietario;
    }

    public void setPropietario(Jugador propietario) {
        this.propietario = propietario;
    }

    public boolean isDisponible() {
        return propietario == null;
    }
}
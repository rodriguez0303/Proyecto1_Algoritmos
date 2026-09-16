package LogicaJuego;

public class Casilla {

    private String nombre;

    public Casilla(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}

/**
NOTA: Versión preliminar/temporal. Existe solo para que NodoCasilla y
Tablero compilen. La versión real (con Propiedad, CasillaEvento y
CasillaEspecial heredando de ella) le corresponde al Par 2.
**/
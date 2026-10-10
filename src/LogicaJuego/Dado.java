package LogicaJuego;

import java.util.Random;

/**
 * Clase Dado: representa un dado de 6 caras, usado por Juego para
 * simular el lanzamiento en cada turno.
 *
 * Nota: esta sigue siendo una versión basada en java.util.Random; el
 * lanzamiento con hardware real (módulo RFID + display de 7 segmentos,
 * punto 14 del enunciado) queda pendiente para más adelante, sin tocar
 * esta clase por ahora.
 */

public class Dado {

    private int id;                  // Identifica cuál de los dados es (1 o 2)
    private int valor;               // Último valor obtenido al lanzar
    private final Random generador;  // Generador de números aleatorios, se crea una sola vez

    // Constructor: Recibe el id del dado y prepara el generador aleatorio
    /**
     * Inicializa el dado con el identificador recibido.
     */
    public Dado(int id) {
        this.id = id;
        this.generador = new Random();
    }

    /**
     * Consulta la identificación del dado.
     */
    public int getId() {
        return id;
    }

    // Simula el lanzamiento del dado: Genera un número entero al azar
    // entre 1 y 6, lo guarda en valor y lo devuelve
    /**
     * Genera y conserva un resultado aleatorio del dado.
     */
    public int lanzar() {
        valor = generador.nextInt(6) + 1;
        return valor;
    }

    // Devuelve el último valor lanzado (sin volver a lanzar)
    /**
     * Obtiene el resultado almacenado del último lanzamiento.
     */
    public int getValor() {
        return valor;
    }

    /**
     * Registra el resultado recibido de un lanzamiento físico.
     */
    public void establecerValor(int nuevoValor) {
        if (nuevoValor < 1 || nuevoValor > 6) {
            throw new IllegalArgumentException("El dado debe tener un valor entre 1 y 6.");
        }
        this.valor = nuevoValor;
    }
}
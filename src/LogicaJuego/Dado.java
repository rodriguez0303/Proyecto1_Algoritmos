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

    private int Id;                  // Identifica cuál de los dados es (1 o 2)
    private int Valor;               // Último valor obtenido al lanzar
    private final Random Generador;  // Generador de números aleatorios, se crea una sola vez

    // Constructor: Recibe el id del dado y prepara el generador aleatorio
    public Dado(int Id) {
        this.Id = Id;
        this.Generador = new Random();
    }

    public int getId() {
        return Id;
    }

    // Simula el lanzamiento del dado: Genera un número entero al azar
    // entre 1 y 6, lo guarda en Valor y lo devuelve
    public int Lanzar() {
        Valor = Generador.nextInt(6) + 1;
        return Valor;
    }

    // Devuelve el último valor lanzado (sin volver a lanzar)
    public int getValor() {
        return Valor;
    }
}
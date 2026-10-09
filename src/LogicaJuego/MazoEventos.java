package LogicaJuego;

import java.util.Random;

/**
 * Clase MazoEventos: Guarda las cartas de evento de la partida y decide
 * cuál sale cada vez que un jugador cae en una CasillaEvento (punto 10).
 *
 * Cómo funciona:
 * 1. Al crearse, arma todas las cartas en CrearCartas().
 * 2. Las mezcla al azar y las guarda en una ColaCircular, así nadie sabe
 *    qué carta viene: puede darte dinero, quitártelo, moverte por el
 *    campus, encerrarte en el D3 o darte una carta para salvarte de él.
 * 3. SacarCarta() devuelve la carta del frente y avanza la cola: la carta
 *    usada queda al final, lista para reutilizarse cuando el mazo dé la vuelta.
 *
 * El efecto de cada carta no está aquí: lo aplica CartaEvento.Aplicar().
 * Para agregar una carta nueva basta con añadir una línea en CrearCartas().
 */

public class MazoEventos {

    private ColaCircular<CartaEvento> Cartas;   // Cartas del mazo; el frente es la próxima que sale
    private CartaEvento[] CartasCreadas;         // Arreglo temporal para mezclar antes de pasarlas a la cola
    private int CantidadCreadas;                 // Cuántas cartas se han creado (también genera los IDs C01, C02...)

    // Cantidad máxima de cartas que puede tener el mazo
    private static final int MAX_CARTAS = 30;

    // Posiciones del tablero a las que mandan algunas cartas (ver Tablero.NombresCasillas)
    private static final int POSICION_COMEDOR = 1;
    private static final int POSICION_LAGO = 11;
    private static final int POSICION_GYMTEC = 13;

    // Constructor: Crea las cartas, las mezcla y arma el mazo.
    public MazoEventos() {
        this.Cartas = new ColaCircular<>();
        this.CartasCreadas = new CartaEvento[MAX_CARTAS];
        this.CantidadCreadas = 0;

        CrearCartas();
        MezclarYArmarCola();
    }

    // Todas las cartas del mazo. El orden aquí no importa, porque después se mezclan.
    private void CrearCartas() {
        AgregarCarta("Ganaste una beca: recibe 200", TipoEvento.RECIBIR_DINERO, 200);
        AgregarCarta("Pediste un Uber para llegar a clases: paga 80", TipoEvento.PAGAR_DINERO, 80);
        AgregarCarta("Encontraste un atajo: avanza 3 casillas", TipoEvento.AVANZAR, 3);
        AgregarCarta("Te encerraron en el D3: vas directo y pierdes un turno", TipoEvento.IR_AL_D3, 0);
        AgregarCarta("Vendiste tus apuntes: recibe 100", TipoEvento.RECIBIR_DINERO, 100);
        AgregarCarta("Te dieron ganas de ver el Lago: vas para allá", TipoEvento.IR_A_CASILLA, POSICION_LAGO);
        AgregarCarta("Pago de matrícula: paga 150", TipoEvento.PAGAR_DINERO, 150);
        AgregarCarta("Salida libre del D3: guárdala por si te encierran", TipoEvento.SALIDA_LIBRE_D3, 0);
        AgregarCarta("Olvidaste el carné: retrocede 2 casillas", TipoEvento.RETROCEDER, 2);
        AgregarCarta("Vas a visitar a un amigo al D3 (solo de visita, no pierdes turno)", TipoEvento.IR_A_CASILLA, Constantes.POSICION_D3);
        AgregarCarta("Ganaste el hackathon: recibe 150", TipoEvento.RECIBIR_DINERO, 150);
        AgregarCarta("Te quedaste dormido: pierdes un turno", TipoEvento.PERDER_TURNO, 0);
        AgregarCarta("Hora de almorzar: vas al Comedor Institucional", TipoEvento.IR_A_CASILLA, POSICION_COMEDOR);
        AgregarCarta("Multa de parqueo: paga 50", TipoEvento.PAGAR_DINERO, 50);
        AgregarCarta("Te devolviste por la sombrilla: retrocede 3 casillas", TipoEvento.RETROCEDER, 3);
        AgregarCarta("Te inscribiste en el gimnasio: vas al GymTEC", TipoEvento.IR_A_CASILLA, POSICION_GYMTEC);
        AgregarCarta("Te prestaron una bici de BICITEC: avanza 2 casillas", TipoEvento.AVANZAR, 2);
        AgregarCarta("Regresa a la Salida", TipoEvento.IR_A_CASILLA, Constantes.POSICION_SALIDA);
    }

    // Crea una carta con el siguiente ID disponible (C01, C02...) y la guarda para mezclarla.
    private void AgregarCarta(String Descripcion, TipoEvento Tipo, int Valor) {
        if (CantidadCreadas >= MAX_CARTAS) {
            throw new IllegalStateException("El mazo no admite más de " + MAX_CARTAS + " cartas");
        }
        CantidadCreadas++;
        String Id = String.format("C%02d", CantidadCreadas);
        CartasCreadas[CantidadCreadas - 1] = new CartaEvento(Id, Descripcion, Tipo, Valor);
    }

    // Mezcla las cartas creadas al azar (Fisher-Yates) y las pasa a la cola circular.
    private void MezclarYArmarCola() {
        Random Aleatorio = new Random();
        for (int i = CantidadCreadas - 1; i > 0; i--) {
            int j = Aleatorio.nextInt(i + 1);
            CartaEvento Temp = CartasCreadas[i];
            CartasCreadas[i] = CartasCreadas[j];
            CartasCreadas[j] = Temp;
        }

        for (int i = 0; i < CantidadCreadas; i++) {
            Cartas.Agregar(CartasCreadas[i]);
        }
        CartasCreadas = null;   // Ya no se necesita: desde aquí el mazo es solo la cola
    }

    // Saca la carta que está al frente del mazo y avanza la cola circular.
    // Al avanzar, la carta recién usada queda justo antes del nuevo frente,
    // es decir, al final de la cola, lista para reutilizarse.
    public CartaEvento SacarCarta() {
        if (Cartas.Vacio()) {
            return null;
        }
        CartaEvento Carta = Cartas.ObtenerActual();
        Cartas.Avanzar();
        return Carta;
    }

    // Cantidad de cartas del mazo
    public int Tamaño() {
        return Cartas.Tamaño();
    }
}

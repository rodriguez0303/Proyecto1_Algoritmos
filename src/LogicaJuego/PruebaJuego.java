//package LogicaJuego;
//
///**
// * Clase PruebaJuego: prueba manual de Juego con lo que ya tenemos.
// * Tablero + NodoCasilla (Par 1) y Dado ya están completos, así que se
// * prueban a fondo. Casilla/Propiedad (Par 2) y Banco (Par 3) siguen
// * temporales, así que solo se confirma que compilan y no rompen nada,
// * sin esperar un comportamiento real de esas partes todavía.
// */
//public class PruebaJuego {
//    public static void main(String[] args) {
//        Juego Juego = new Juego(10);
//
//        Jugador Jugador1 = new Jugador("J001", "Ñemaman", 1500.0);
//        Jugador Jugador2 = new Jugador("J002", "Ozuna Nicaraguense", 1500.0);
//        Jugador Jugador3 = new Jugador("J003", "CuadradoBigTico", 1500.0);
//        Jugador Jugador4 = new Jugador("J004", "Carnciero de la bahia", 1500.0);
//
//        Juego.AgregarJugador(Jugador1);
//        Juego.AgregarJugador(Jugador2);
//        Juego.AgregarJugador(Jugador3);
//        Juego.AgregarJugador(Jugador4);
//
//        System.out.println("INICIANDO PARTIDA");
//        Juego.IniciarPartida();
//        System.out.println("Turno actual: " + Juego.obtenerJugadorActual().getNombre());
//        System.out.println("Número de turno: " + Juego.getNumTurno());
//
//        System.out.println();
//        System.out.println("TURNO DE " + Juego.obtenerJugadorActual().getNombre());
//        int Resultado = Juego.lanzarDados();
//        System.out.println("Dado 1 (id " + Juego.getDado1().getId() + "): " + Juego.getDado1().getValor()
//                + " | Dado 2 (id " + Juego.getDado2().getId() + "): " + Juego.getDado2().getValor()
//                + " (total: " + Resultado + ")");
//        System.out.println("¿Ya lanzó dados este turno? " + Juego.getDadosLanzadosEsteTurno());
//
//        Juego.MoverJugador(Jugador1, Resultado);
//        System.out.println("Nueva posición de " + Jugador1.getNombre() + ": " + Jugador1.getPosicionActual());
//
//        System.out.println();
//        System.out.println("BUSCAR JUGADOR POR IDENTIFICADOR");
//        Jugador Encontrado = Juego.buscarJugadorPorIdentificador("J002");
//        System.out.println("J002 corresponde a: " + (Encontrado != null ? Encontrado.getNombre() : "no encontrado"));
//
//        System.out.println();
//        System.out.println("PROBANDO LOS STUBS (Propiedad/comprarPropiedad, Par 2)");
//        Propiedad PropiedadActual = Juego.obtenerPropiedadActual(Jugador1);
//        System.out.println("obtenerPropiedadActual() por ahora devuelve: " + PropiedadActual);
//        Juego.comprarPropiedad(Jugador1, PropiedadActual); // no hace nada todavía, solo confirma que no falla
//
//        System.out.println();
//        System.out.println("REGISTRANDO UNA TRANSACCIÓN DE EJEMPLO");
//        Transaccion TransaccionEjemplo = new Transaccion("T001", Juego.getNumTurno(), "PAGO_ALQUILER",
//                Jugador1.getIdentificador(), Jugador2.getIdentificador(), 150.0, "Alquiler casilla de ejemplo");
//        Juego.RegistrarTransaccion(TransaccionEjemplo);
//
//        System.out.println();
//        System.out.println("FINALIZANDO TURNO");
//        Juego.finalizarTurno();
//        System.out.println("Nuevo turno actual: " + Juego.obtenerJugadorActual().getNombre());
//        System.out.println("¿Ya lanzó dados este turno? (debe ser false) " + Juego.getDadosLanzadosEsteTurno());
//        System.out.println("Número de turno: " + Juego.getNumTurno());
//
//        System.out.println();
//        System.out.println("ELIMINANDO AL SIGUIENTE JUGADOR EN LA FILA");
//        Jugador3.eliminar();
//        Juego.finalizarTurno();
//        System.out.println("Turno actual (debe saltar a " + Jugador3.getNombre() + " y caer en "
//                + Jugador4.getNombre() + "): " + Juego.obtenerJugadorActual().getNombre());
//
//        System.out.println();
//        System.out.println("MOSTRANDO EL TABLERO COMPLETO");
//        // Tablero es privado dentro de Juego (no tiene getter), así que para
//        // ver MostrarTablero() se crea un Tablero aparte solo para la prueba;
//        // estructuralmente es igual al que usa Juego (24 casillas genéricas).
//        Tablero TableroPrueba = new Tablero();
//        TableroPrueba.MostrarTablero();
//
//        System.out.println();
//        System.out.println("FINALIZANDO PARTIDA");
//        Juego.FinalizarPartida();
//        System.out.println("¿Partida en curso? " + Juego.isEnCurso());
//    }
//}
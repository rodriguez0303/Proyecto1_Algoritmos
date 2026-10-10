package Hardware;

/**
 * Prueba manual de la integracion fisica con la Raspberry Pi Pico.
 * Registra tarjetas RFID y ejecuta una ronda de dados por jugador.
 * No requiere abrir el tablero ni el servidor TCP.
 */
public class PruebaHardware {

    private static final int MAX_JUGADORES = 4;

    /**
     * Conecta COM3, registra las tarjetas indicadas y comprueba una ronda
     * de lanzamientos con el boton fisico.
     *
     * @param args cantidad opcional de jugadores; por defecto son tres
     */
    public static void main(String[] args) {

        // Nosotros somos 3, pero queda preparado para máximo 4.
        int cantidadJugadores = 3;

        if (args.length > 0) {
            cantidadJugadores = Integer.parseInt(args[0]);
        }

        if (cantidadJugadores < 1 || cantidadJugadores > MAX_JUGADORES) {
            System.out.println(
                    "La cantidad de jugadores debe estar entre 1 y 4."
            );
            return;
        }

        try (
                ControlDadosHardware hardware =
                        new ControlDadosHardware("COM3")
        ) {

            // -------------------------------------------------
            // CONEXIÓN
            // -------------------------------------------------

            if (!hardware.conectar()) {

                System.out.println(
                        "No se pudo conectar con el hardware en COM3."
                );

                return;
            }

            System.out.println(
                    "Hardware conectado correctamente."
            );

            System.out.println();
            System.out.println(
                    "================================"
            );

            System.out.println(
                    " REGISTRO DE JUGADORES"
            );

            System.out.println(
                    "================================"
            );

            // -------------------------------------------------
            // REGISTRAR RFID
            // -------------------------------------------------

            for (int i = 1; i <= cantidadJugadores; i++) {

                String jugador =
                        String.format("J%03d", i);

                System.out.println();
                System.out.println(
                        "Registrando " + jugador
                );

                String uid = hardware.registrarJugador(
                        jugador,

                        mensaje -> {

                            System.out.println(
                                    "PICO -> " + mensaje
                            );

                            if (mensaje.equals(
                                    "ESPERANDO_RFID;" + jugador
                            )) {

                                System.out.println(
                                        ">>> Acerque la tarjeta RFID de "
                                        + jugador
                                );
                            }
                        }
                );

                System.out.println(
                        jugador
                        + " registrado correctamente."
                );

                System.out.println(
                        "UID: " + uid
                );
            }

            // -------------------------------------------------
            // PRUEBA DE UNA RONDA
            // -------------------------------------------------

            System.out.println();
            System.out.println(
                    "================================"
            );

            System.out.println(
                    " PRUEBA DE UNA RONDA"
            );

            System.out.println(
                    "================================"
            );

            for (int i = 1; i <= cantidadJugadores; i++) {

                String jugador =
                        String.format("J%03d", i);

                System.out.println();
                System.out.println(
                        "Turno de " + jugador
                );

                ResultadoDados resultado =
                        hardware.tirarDados(
                                jugador,

                                mensaje -> {

                                    System.out.println(
                                            "PICO -> "
                                            + mensaje
                                    );

                                    if (mensaje.equals(
                                            "ESPERANDO_RFID;"
                                            + jugador
                                    )) {

                                        System.out.println(
                                                ">>> Acerque la tarjeta de "
                                                + jugador
                                        );
                                    }

                                    else if (mensaje.equals(
                                            "RFID_OK;"
                                            + jugador
                                    )) {

                                        System.out.println(
                                                "Tarjeta validada."
                                        );
                                    }

                                    else if (mensaje.equals(
                                            "ESPERANDO_BOTON;"
                                            + jugador
                                    )) {

                                        System.out.println(
                                                ">>> Presione el botón físico."
                                        );
                                    }

                                    else if (
                                            mensaje.startsWith(
                                                    "RFID_INCORRECTO;"
                                            )
                                    ) {

                                        System.out.println(
                                                "Tarjeta incorrecta."
                                        );
                                    }
                                }
                        );

                System.out.println();
                System.out.println(
                        "Resultado de " + jugador
                );

                System.out.println(
                        "Dado 1: "
                        + resultado.getDado1()
                );

                System.out.println(
                        "Dado 2: "
                        + resultado.getDado2()
                );

                System.out.println(
                        "Suma: "
                        + resultado.getSuma()
                );

                if (resultado.esDoble()) {

                    System.out.println(
                            "¡Salieron dobles!"
                    );
                }
            }

            System.out.println();
            System.out.println(
                    "================================"
            );

            System.out.println(
                    " RONDA COMPLETADA"
            );

            System.out.println(
                    "================================"
            );

        } catch (Exception e) {

            System.out.println();
            System.out.println(
                    "Error durante la prueba:"
            );

            e.printStackTrace();
        }
    }
}
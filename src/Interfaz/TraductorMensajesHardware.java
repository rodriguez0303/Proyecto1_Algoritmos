
package Interfaz;

public final class TraductorMensajesHardware {

    private TraductorMensajesHardware() {
        // Clase de utilidad: no necesita instancias.
    }

    public static String traducir(String mensajeHardware) {

        String[] partes = mensajeHardware.split(";");

        String tipo = partes[0];
        String jugador = partes.length > 1 ? partes[1] : "";

        if (tipo.equals("ESPERANDO_RFID")) {
            return "Acerque la tarjeta RFID de " + jugador + " al lector.";
        }
        else if (tipo.equals("RFID_OK")) {
            return "Tarjeta de " + jugador + " validada.";
        }
        else if (tipo.equals("RFID_INCORRECTO")) {
            return "Esa tarjeta no es de " + jugador + ". Intente de nuevo.";
        }
        else if (tipo.equals("RETIRAR_RFID")) {
            return "Retire la tarjeta del lector.";
        }
        else if (tipo.equals("ESPERANDO_BOTON")) {
            return "Presione el botón físico o \"Lanzar dados\" en la ventana.";
        }
        else if (tipo.equals("BOTON_DIGITAL")) {
            return "Dados lanzados desde la ventana.";
        }
        else if (tipo.equals("PAGO_OK")) {
            return "Pago autorizado.";
        }
        else if (tipo.equals("ERROR")) {
            return "Error del hardware: "
                    + mensajeHardware
                            .substring("ERROR;".length())
                            .replace(";", " ");
        }

        // Los avisos internos no necesitan mostrarse.
        return null;
    }
}

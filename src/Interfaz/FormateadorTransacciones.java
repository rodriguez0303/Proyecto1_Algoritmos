
package Interfaz;

/**
 * Convierte los registros de transacciones recibidos del servidor en mensajes legibles.
 * Es una clase de utilidad y no mantiene estado ni modifica la partida.
 */
public final class FormateadorTransacciones {

    private FormateadorTransacciones() {
        // Clase de utilidad: no necesita instancias.
    }

    /**
     * Interpreta los campos de una transaccion separados por barras verticales.
     * @param Registro registro emitido por el servidor.
     * @return mensaje legible; si faltan campos obligatorios, conserva el contenido recibido.
     */
    public static String formatear(String Registro) {

        String Origen = "";
        String Destino = "";
        String Monto = "";
        String Descripcion = "";

        for (String Campo : Registro.split("\\|")) {

            int Separador = Campo.indexOf(": ");

            if (Separador < 0) {
                continue;
            }

            String Clave = Campo.substring(0, Separador).trim();
            String Valor = Campo.substring(Separador + 2).trim();

            if (Clave.equals("Origen")) {
                Origen = Valor;
            }
            else if (Clave.equals("Destino")) {
                Destino = Valor;
            }
            else if (Clave.equals("Monto")) {
                Monto = Valor;
            }
            else if (Clave.equals("Descripcion")) {
                Descripcion = Valor;
            }
        }

        // Mantener el formato original si faltan datos.
        if (Origen.isEmpty() || Destino.isEmpty() || Monto.isEmpty()) {
            return Registro.replace("|", " - ");
        }

        try {
            Monto = String.valueOf((long) Double.parseDouble(Monto));
        } catch (NumberFormatException e) {
            // Mantener el monto original.
        }

        String Linea;

        if (Origen.equals("BANCO")) {
            Linea = "El Banco pagó ₡" + Monto + " a " + Destino;
        }
        else if (Destino.equals("BANCO")) {
            Linea = Origen + " pagó ₡" + Monto + " al Banco";
        }
        else {
            Linea = Origen + " pagó ₡" + Monto + " a " + Destino;
        }

        if (!Descripcion.isEmpty()) {
            Linea += " (" + Descripcion + ")";
        }

        return Linea + ".";
    }
}

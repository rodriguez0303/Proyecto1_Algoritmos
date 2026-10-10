
package Interfaz;

import LogicaJuego.HistorialTransacciones;
import LogicaJuego.TipoTransaccion;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Muestra el resultado final de la partida y permite consultar transacciones.
 * Las acciones de reinicio y cierre se reciben como callbacks desde VentanaJuego.
 */
public class VentanaFinPartida extends JFrame {

    // Filtros de la consulta de transacciones (punto 2: buscar por jugador, por tipo,
    // y recorrer desde la más antigua o desde la más reciente).
    /**
     * Filtros utilizados para consultar transacciones por jugador, tipo y orden.
     */
    private JComboBox<String> CmbJugador;
    private JComboBox<String> CmbTipo;
    private JComboBox<String> CmbOrden;
    private JButton BtnBuscar;
    private JPanel PanelConsulta;

    // Envía la consulta al servidor (la asigna VentanaJuego con SetAccionConsultar).
    private Consumer<String> AccionConsultar;

    private JTextPane lblGanador;
    private JLabel lblPatrimonio;
    private JTextArea areaTransacciones;

    private JButton btnConsultarTransacciones;
    private JButton btnVolverAJugar;
    private JButton btnFinalizar;

    private JScrollPane scrollTransacciones;

    // Constructor de la pantalla final.
    /**
     * Construye la pantalla de resultados y configura las acciones permitidas.
     * @param nombreGanador texto del resultado, incluidos los casos de empate.
     * @param patrimonio resumen del patrimonio final.
     * @param transacciones historial disponible al abrir la pantalla.
     * @param esAnfitrion indica si esta ventana puede administrar la partida.
     * @param accionVolverAJugar operaciÃ³n solicitada al pulsar Volver a jugar.
     * @param accionFinalizar operaciÃ³n solicitada al pulsar Finalizar.
     */
    public VentanaFinPartida(
            String nombreGanador,
            String patrimonio,
            String transacciones,
            boolean esAnfitrion,
            Runnable accionVolverAJugar,
            Runnable accionFinalizar) {

        // -----------------------------------------
        // CONFIGURACIÓN DE LA VENTANA
        // -----------------------------------------

        setTitle("Monopoly TEC - Fin de partida");
        setSize(980, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel panelPrincipal = new JPanel(
                new BorderLayout(15, 15)
        );

        panelPrincipal.setBackground(
                new Color(225, 215, 185)
        );

        panelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        );

        setContentPane(panelPrincipal);

        // -----------------------------------------
        // MOSTRAR GANADOR
        // -----------------------------------------

        JPanel panelGanador = new JPanel();
        panelGanador.setLayout(
                new BoxLayout(panelGanador, BoxLayout.Y_AXIS)
        );

        panelGanador.setOpaque(false);

        JLabel lblTitulo = new JLabel(
                "¡PARTIDA FINALIZADA!"
        );

        lblTitulo.setFont(
                new Font("SansSerif", Font.BOLD, 28)
        );

        lblTitulo.setForeground(
                new Color(35, 90, 65)
        );

        lblTitulo.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        String tituloResultado = nombreGanador.startsWith("Empate entre") || nombreGanador.equals("Sin ganador") ? "Resultado: " + nombreGanador : "Ganador: " + nombreGanador;

        lblGanador = new JTextPane();
        lblGanador.setText(tituloResultado);

        lblGanador.setFont(new Font("SansSerif", Font.BOLD, 23));
        lblGanador.setEditable(false);
        lblGanador.setFocusable(false);
        lblGanador.setOpaque(false);
        lblGanador.setBorder(null);

        // Centrar el texto.
        javax.swing.text.SimpleAttributeSet estilo = new javax.swing.text.SimpleAttributeSet();

        javax.swing.text.StyleConstants.setAlignment(estilo, javax.swing.text.StyleConstants.ALIGN_CENTER);

        lblGanador.getStyledDocument().setParagraphAttributes(0, lblGanador.getDocument().getLength(), estilo, false);

        // Permitir varias líneas.
        lblGanador.setPreferredSize(new Dimension(860, 100));
        lblGanador.setMaximumSize(new Dimension(860, 100));
        lblGanador.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblPatrimonio = new JLabel(
                "Patrimonio final: " + patrimonio
        );

        lblPatrimonio.setFont(
                new Font("SansSerif", Font.PLAIN, 17)
        );

        lblPatrimonio.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        panelGanador.add(lblTitulo);
        panelGanador.add(Box.createVerticalStrut(20));
        panelGanador.add(lblGanador);
        panelGanador.add(Box.createVerticalStrut(10));
        panelGanador.add(lblPatrimonio);

        panelPrincipal.add(
                panelGanador,
                BorderLayout.NORTH
        );

        // -----------------------------------------
        // HISTORIAL DE TRANSACCIONES
        // -----------------------------------------

        JPanel panelHistorial = new JPanel(
                new BorderLayout(10, 10)
        );

        panelHistorial.setOpaque(false);

        btnConsultarTransacciones = new JButton(
                "Consultar transacciones"
        );

        areaTransacciones = new JTextArea();

        areaTransacciones.setEditable(false);

        // Sin ajuste de línea: las transacciones se muestran como tabla (una fila por transacción).
        areaTransacciones.setLineWrap(false);

        areaTransacciones.setFont(
                new Font("Monospaced", Font.PLAIN, 13)
        );

        areaTransacciones.setText(FormatearTabla(transacciones));

        scrollTransacciones = new JScrollPane(
                areaTransacciones
        );

        // -----------------------------------------
        // FILTROS DE LA CONSULTA
        // -----------------------------------------

        CmbJugador = new JComboBox<>(new String[] {"Todos", "J001", "J002", "J003", "J004"});

        // "Todos" más un nombre legible por cada TipoTransaccion (en el mismo orden del enum).
        TipoTransaccion[] Tipos = TipoTransaccion.values();
        String[] NombresTipos = new String[Tipos.length + 1];
        NombresTipos[0] = "Todos";
        for (int i = 0; i < Tipos.length; i++) {
            NombresTipos[i + 1] = Tipos[i].getNombre();
        }
        CmbTipo = new JComboBox<>(NombresTipos);

        CmbOrden = new JComboBox<>(new String[] {"Más antigua primero", "Más reciente primero"});

        BtnBuscar = new JButton("Buscar");

        BtnBuscar.addActionListener(e -> {

            if (AccionConsultar == null) {
                return;
            }

            String Jugador = CmbJugador.getSelectedIndex() == 0
                    ? "TODOS"
                    : (String) CmbJugador.getSelectedItem();

            String Tipo = CmbTipo.getSelectedIndex() == 0
                    ? "TODOS"
                    : Tipos[CmbTipo.getSelectedIndex() - 1].name();

            String Orden = CmbOrden.getSelectedIndex() == 0 ? "ANTIGUA" : "RECIENTE";

            areaTransacciones.setText("Buscando...");

            AccionConsultar.accept("CONSULTAR_HISTORIAL;" + Jugador + ";" + Tipo + ";" + Orden);
        });

        JPanel PanelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        PanelFiltros.setOpaque(false);
        PanelFiltros.add(new JLabel("Jugador:"));
        PanelFiltros.add(CmbJugador);
        PanelFiltros.add(new JLabel("Tipo:"));
        PanelFiltros.add(CmbTipo);
        PanelFiltros.add(new JLabel("Orden:"));
        PanelFiltros.add(CmbOrden);
        PanelFiltros.add(BtnBuscar);

        PanelConsulta = new JPanel(new BorderLayout(5, 5));
        PanelConsulta.setOpaque(false);
        PanelConsulta.add(PanelFiltros, BorderLayout.NORTH);
        PanelConsulta.add(scrollTransacciones, BorderLayout.CENTER);

        PanelConsulta.setVisible(false);

        btnConsultarTransacciones.addActionListener(e -> {

            boolean mostrar =
                    !PanelConsulta.isVisible();

            PanelConsulta.setVisible(mostrar);

            btnConsultarTransacciones.setText(
                    mostrar
                    ? "Ocultar transacciones"
                    : "Consultar transacciones"
            );

            panelHistorial.revalidate();
            panelHistorial.repaint();
        });

        JPanel panelBotonConsulta = new JPanel(new FlowLayout(FlowLayout.CENTER,0 ,0));

        panelBotonConsulta.setOpaque(false);

        btnConsultarTransacciones.setPreferredSize(new Dimension(220, 38));

        panelBotonConsulta.add(btnConsultarTransacciones);

        panelHistorial.add(panelBotonConsulta, BorderLayout.NORTH);

        panelHistorial.add(
                PanelConsulta,
                BorderLayout.CENTER
        );

        panelPrincipal.add(
                panelHistorial,
                BorderLayout.CENTER
        );

        // -----------------------------------------
        // BOTONES FINALES
        // -----------------------------------------

        JPanel panelBotones = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 15, 10)
        );

        panelBotones.setOpaque(false);

        btnVolverAJugar = new JButton(
                "Volver a jugar"
        );

        btnFinalizar = new JButton(
                "Finalizar"
        );

        btnVolverAJugar.setPreferredSize(
                new Dimension(180, 40)
        );

        btnFinalizar.setPreferredSize(
                new Dimension(180, 40)
        );

        btnVolverAJugar.setBackground(
                new Color(35, 90, 65)
        );

        btnVolverAJugar.setForeground(Color.WHITE);

        btnFinalizar.setBackground(
                new Color(160, 55, 55)
        );

        btnFinalizar.setForeground(Color.WHITE);

        // Solo el anfitrión puede administrar
        // el reinicio y cierre del servidor.
        // Las acciones se habilitarán cuando
        // el servidor las tenga implementadas.

        btnVolverAJugar.setEnabled(
                esAnfitrion && accionVolverAJugar != null
        );

        btnFinalizar.setEnabled(
                esAnfitrion && accionFinalizar != null
        );

        btnVolverAJugar.addActionListener(e -> {

            int respuesta = JOptionPane.showConfirmDialog(
                    this,
                    "¿Desea iniciar una nueva partida?",
                    "Volver a jugar",
                    JOptionPane.YES_NO_OPTION
            );

            if (respuesta == JOptionPane.YES_OPTION
                    && accionVolverAJugar != null) {

                accionVolverAJugar.run();
            }
        });

        btnFinalizar.addActionListener(e -> {

            int respuesta = JOptionPane.showConfirmDialog(
                    this,
                    "¿Desea finalizar y cerrar el servidor?",
                    "Finalizar",
                    JOptionPane.YES_NO_OPTION
            );

            if (respuesta == JOptionPane.YES_OPTION
                    && accionFinalizar != null) {

                accionFinalizar.run();
            }
        });

        panelBotones.add(btnVolverAJugar);
        panelBotones.add(btnFinalizar);

        panelPrincipal.add(
                panelBotones,
                BorderLayout.SOUTH
        );
    }

    // Permite actualizar el historial cuando
    // lleguen nuevas transacciones del servidor.
    /**
     * Actualiza el historial completo mostrado en la pantalla final.
     * @param transacciones registros actuales enviados por el servidor.
     */
    public void actualizarTransacciones(String transacciones) {

        SwingUtilities.invokeLater(() -> {

            areaTransacciones.setText(FormatearTabla(transacciones));
        });
    }

    /**
     * Actualiza el resumen de patrimonio mostrado al finalizar la partida.
     * @param patrimonio texto con los valores finales.
     */
    public void actualizarPatrimonio(String patrimonio) {
        SwingUtilities.invokeLater(() -> {
                lblPatrimonio.setText("Patrimonio final: " + patrimonio);
        });
    }

    // Deja en el filtro solo a los jugadores de la partida (por ejemplo J001 y J002 en una de 2).
    // Si no llega ninguno, se mantienen los cuatro.
    /**
     * Limita el filtro de jugadores a los participantes reales de la partida.
     * @param Jugadores identificadores de los jugadores conectados.
     */
    public void SetJugadores(String[] Jugadores) {

        if (Jugadores == null || Jugadores.length == 0) {
            return;
        }

        CmbJugador.removeAllItems();
        CmbJugador.addItem("Todos");

        for (String Jugador : Jugadores) {
            CmbJugador.addItem(Jugador);
        }
    }

    // VentanaJuego indica cómo enviar la consulta al servidor.
    /**
     * Registra la operaciÃ³n que envÃ­a las consultas de transacciones al servidor.
     * @param Accion funciÃ³n receptora del mensaje de consulta.
     */
    public void SetAccionConsultar(Consumer<String> Accion) {
        this.AccionConsultar = Accion;
    }

    // Muestra el resultado de una consulta (respuesta CONSULTA; del servidor).
    /**
     * Muestra en la tabla la respuesta a una consulta filtrada de transacciones.
     * @param Registros registros devueltos por el servidor.
     */
    public void MostrarConsulta(String Registros) {

        SwingUtilities.invokeLater(() -> {

            areaTransacciones.setText(FormatearTabla(Registros));

            areaTransacciones.setCaretPosition(0);
        });
    }

    // Convierte las transacciones del servidor en una tabla con las mismas columnas del TXT:
    // número, turno, tipo, origen, destino, monto y descripción.
    // Cada transacción llega como: T1|Turno: 2|Tipo: COMPRA_PROPIEDAD|Origen: J002|Destino: BANCO|Monto: 250.0|Descripcion: ...
    // Las transacciones vienen separadas por salto de línea o por ";".
    /**
     * Transforma los registros del protocolo en una tabla de texto alineada.
     * @param Registros transacciones separadas por punto y coma o saltos de lÃ­nea.
     * @return representaciÃ³n tabular con el nÃºmero de resultados.
     */
    private String FormatearTabla(String Registros) {

        if (Registros == null || Registros.isBlank() || Registros.equals("SIN_TRANSACCIONES")) {
            return "No hay transacciones que coincidan.";
        }

        StringBuilder Tabla = new StringBuilder(HistorialTransacciones.EncabezadoTabla());

        int Cantidad = 0;

        for (String Registro : Registros.split("[;\\n]")) {

            if (Registro.isBlank()) {
                continue;
            }

            String[] Campos = Registro.split("\\|");

            String Numero = Campos[0].trim();
            int Turno = 0;
            String Tipo = "";
            String Origen = "";
            String Destino = "";
            double Monto = 0;
            String Descripcion = "";

            for (int i = 1; i < Campos.length; i++) {

                int Separador = Campos[i].indexOf(": ");

                if (Separador < 0) {
                    continue;
                }

                String Clave = Campos[i].substring(0, Separador).trim();
                String Valor = Campos[i].substring(Separador + 2).trim();

                try {
                    if (Clave.equals("Turno")) {
                        Turno = Integer.parseInt(Valor);
                    }
                    else if (Clave.equals("Tipo")) {
                        Tipo = TipoTransaccion.valueOf(Valor).getNombre();
                    }
                    else if (Clave.equals("Origen")) {
                        Origen = Valor;
                    }
                    else if (Clave.equals("Destino")) {
                        Destino = Valor;
                    }
                    else if (Clave.equals("Monto")) {
                        Monto = Double.parseDouble(Valor);
                    }
                    else if (Clave.equals("Descripcion")) {
                        Descripcion = Valor;
                    }
                } catch (IllegalArgumentException e) {
                    // Si un dato no tiene el formato esperado se deja tal como llegó.
                    if (Clave.equals("Tipo")) {
                        Tipo = Valor;
                    }
                }
            }

            Tabla.append("\n").append(
                    HistorialTransacciones.FilaTabla(Numero, Turno, Tipo, Origen, Destino, Monto, Descripcion)
            );

            Cantidad++;
        }

        Tabla.append("\n\nTransacciones encontradas: ").append(Cantidad);

        return Tabla.toString();
    }

    // -----------------------------------------
    // PRUEBA INDEPENDIENTE DE LA INTERFAZ
    // -----------------------------------------

    /**
     * Permite probar esta pantalla de manera independiente al servidor.
     * @param args argumentos de consola; no se utilizan.
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            String transaccionesPrueba =
                    "T1|Turno: 2|Tipo: COMPRA_PROPIEDAD|Origen: J001|Destino: BANCO|Monto: 250.0|Descripcion: Compra de la propiedad Biblioteca\n"
                    + "T2|Turno: 3|Tipo: PAGO_ALQUILER|Origen: J002|Destino: J001|Monto: 100.0|Descripcion: Pago de alquiler de la propiedad Biblioteca\n"
                    + "T3|Turno: 4|Tipo: PREMIO_POR_INICIO|Origen: BANCO|Destino: J003|Monto: 200.0|Descripcion: Premio por pasar por Salida\n";

            VentanaFinPartida ventana =
                    new VentanaFinPartida(
                            "José Miguel",
                            "₡2850",
                            transaccionesPrueba,
                            true,
                            null,
                            null
                    );

            ventana.setVisible(true);
        });
    }
}

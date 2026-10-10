
package Interfaz;

import LogicaJuego.Cliente;

import javax.swing.*;
import java.awt.*;

/**
 * Presenta el estado de una sala antes del inicio de una partida en lÃ­nea.
 * Muestra a los participantes y permite al anfitriÃ³n solicitar el inicio.
 */
public class VentanaSalaEspera extends JFrame {

    /**
     * ConexiÃ³n compartida con el servidor y cÃ³digo del jugador de esta ventana.
     */
    private final Cliente cliente;
    private final String idJugador;

    private JLabel lblContador;
    private JTextArea txtJugadores;
    private JButton btnIniciar;

    /**
     * Prepara la sala de espera para un jugador ya conectado al servidor.
     * @param cliente conexiÃ³n activa que se seguirÃ¡ usando al abrir el tablero.
     * @param idJugador identificador del jugador, por ejemplo J001.
     */
    public VentanaSalaEspera(Cliente cliente, String idJugador) {

        this.cliente = cliente;
        this.idJugador = idJugador;

        setTitle("Monopoly TEC - Sala de espera");
        setSize(500, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Título de la ventana.
        JLabel lblTitulo = new JLabel(
                "MONOPOLY TEC - SALA DE ESPERA",
                SwingConstants.CENTER
        );

        lblTitulo.setFont(
                new Font("SansSerif", Font.BOLD, 22)
        );

        lblTitulo.setOpaque(true);
        lblTitulo.setBackground(new Color(35, 90, 65));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBorder(
                BorderFactory.createEmptyBorder(20, 10, 20, 10)
        );

        add(lblTitulo, BorderLayout.NORTH);

        // Panel central.
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(
                new BoxLayout(panelCentral, BoxLayout.Y_AXIS)
        );

        panelCentral.setBorder(
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        );

        JLabel lblIdentificador = new JLabel(
                "Conectado como: " + idJugador
        );

        lblIdentificador.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        panelCentral.add(lblIdentificador);
        panelCentral.add(Box.createVerticalStrut(15));

        lblContador = new JLabel(
                "Esperando jugadores..."
        );

        lblContador.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        panelCentral.add(lblContador);
        panelCentral.add(Box.createVerticalStrut(15));

        txtJugadores = new JTextArea(8, 25);
        txtJugadores.setEditable(false);
        txtJugadores.setFont(
                new Font("Monospaced", Font.PLAIN, 14)
        );

        panelCentral.add(
                new JScrollPane(txtJugadores)
        );

        add(panelCentral, BorderLayout.CENTER);

        // Botón para iniciar la partida.
        btnIniciar = new JButton("Iniciar partida");
        btnIniciar.setEnabled(false);

        btnIniciar.addActionListener(e -> {
            cliente.enviarSolicitud("INICIAR_PARTIDA");
            btnIniciar.setEnabled(false);
        });

        JPanel panelInferior = new JPanel();
        panelInferior.setBorder(
                BorderFactory.createEmptyBorder(10, 20, 20, 20)
        );

        panelInferior.add(btnIniciar);
        add(panelInferior, BorderLayout.SOUTH);
    }

    // Actualiza los jugadores que están conectados.
    /**
     * Actualiza el contador y la lista de participantes de la sala.
     * El botÃ³n de inicio solo se habilita cuando la sala estÃ¡ completa y
     * esta ventana corresponde al anfitriÃ³n.
     * @param cantidadEsperada cantidad de jugadores prevista para la partida.
     * @param jugadoresConectados registros de jugadores anunciados por el servidor.
     */
    public void actualizarJugadores(
            int cantidadEsperada,
            String[] jugadoresConectados) {

        SwingUtilities.invokeLater(() -> {

            lblContador.setText(
                    "Jugadores: " +
                    jugadoresConectados.length +
                    " / " + cantidadEsperada
            );

            StringBuilder texto = new StringBuilder();

            
            for (String registro : jugadoresConectados) {

                String[] datosJugador = registro.split(",", 2);

                if (datosJugador.length == 2) {

                    String id = datosJugador[0];
                    String nombre = datosJugador[1];

                    texto.append(nombre)
                        .append(" (")
                        .append(id)
                        .append(")")
                        .append(" - Conectado\n");

                } else {
                    texto.append(registro)
                        .append(" - Conectado\n");
                }
            }

            txtJugadores.setText(texto.toString());

            boolean salaCompleta =
                    jugadoresConectados.length == cantidadEsperada;

            boolean esAnfitrion =
                    idJugador.equals("J001");

            btnIniciar.setEnabled(
                    salaCompleta && esAnfitrion
            );
        });
    }
    /**
     * Escucha mensajes de sala en un hilo separado.
     * Actualiza la interfaz mediante SwingUtilities y espera la notificaciÃ³n
     * PARTIDA_INICIADA antes de abrir el tablero.
     */
    public void iniciarEscuchaServidor() {
        Thread hiloReceptor = new Thread(() -> {

            while (true) {

                String mensaje = cliente.recibirRespuesta();

                // Detectar pérdida de conexión.
                if (mensaje == null) {

                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(
                                this,
                                "Se perdió la conexión con el servidor."
                        );
                        dispose();
                    });

                    break;
                }

                // Recibir la lista de jugadores conectados.
                if (mensaje.startsWith("SALA;")) {

                    String[] datos = mensaje.split(";", -1);

                    if (datos.length != 3) {
                        continue;
                    }

                    try {

                        int cantidadEsperada =
                                Integer.parseInt(datos[1]);

                        String[] jugadoresConectados;

                        if (datos[2].isEmpty()) {
                            jugadoresConectados = new String[0];
                        } else {
                            jugadoresConectados = datos[2].split("\\|");
                        }

                        actualizarJugadores(
                                cantidadEsperada,
                                jugadoresConectados
                        );

                    } catch (NumberFormatException ex) {
                        System.out.println(
                                "Cantidad de jugadores inválida."
                        );
                    }
                }

                // El servidor autorizó iniciar la partida.
                else if (mensaje.equals("PARTIDA_INICIADA")) {

                    abrirTablero();

                    // El tablero se encargará de recibir
                    // los siguientes mensajes del servidor.
                    break;
                }

                // Ignorar notificaciones del tablero
                // mientras estamos esperando el inicio.
                else if (mensaje.equals("ACTUALIZAR_ESTADO")) {
                    continue;
                }

                // Mostrar respuestas adicionales del servidor.
                else {

                    SwingUtilities.invokeLater(() -> {

                        JOptionPane.showMessageDialog(
                                this,
                                mensaje
                        );
                    });
                }
            }

        });

        hiloReceptor.setDaemon(true);
        hiloReceptor.start();
    }
    // Se llamará cuando el servidor autorice iniciar.
    /**
     * Cierra la sala de espera y abre la ventana de juego en modo en lÃ­nea.
     * Conserva la misma conexiÃ³n cliente-servidor para el tablero.
     */
    public void abrirTablero() {

        SwingUtilities.invokeLater(() -> {

            VentanaJuego ventana = new VentanaJuego();

            ventana.activarModoEnLinea(cliente);
            ventana.setVisible(true);

            dispose();
        });
    }
}

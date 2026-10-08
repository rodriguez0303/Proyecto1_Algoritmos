
package Interfaz;

import LogicaJuego.Cliente;

import javax.swing.*;
import java.awt.*;

public class VentanaSalaEspera extends JFrame {

    private final Cliente cliente;
    private final String idJugador;

    private JLabel lblContador;
    private JTextArea txtJugadores;
    private JButton btnIniciar;

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

            for (String id : jugadoresConectados) {
                texto.append(id)
                     .append(" - Conectado\n");
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

    // Se llamará cuando el servidor autorice iniciar.
    public void abrirTablero() {

        SwingUtilities.invokeLater(() -> {

            VentanaJuego ventana = new VentanaJuego();

            ventana.activarModoEnLinea(cliente);
            ventana.setVisible(true);

            dispose();
        });
    }
}

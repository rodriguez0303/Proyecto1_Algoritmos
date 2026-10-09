
package Interfaz;

import javax.swing.*;
import java.awt.*;

public class VentanaFinPartida extends JFrame {

    private JLabel lblGanador;
    private JLabel lblPatrimonio;
    private JTextArea areaTransacciones;

    private JButton btnConsultarTransacciones;
    private JButton btnVolverAJugar;
    private JButton btnFinalizar;

    private JScrollPane scrollTransacciones;

    // Constructor de la pantalla final.
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
        setSize(700, 550);
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

        lblGanador = new JLabel(
                "Ganador: " + nombreGanador
        );

        lblGanador.setFont(
                new Font("SansSerif", Font.BOLD, 23)
        );

        lblGanador.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

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
        areaTransacciones.setLineWrap(true);
        areaTransacciones.setWrapStyleWord(true);

        areaTransacciones.setFont(
                new Font("Monospaced", Font.PLAIN, 13)
        );

        areaTransacciones.setText(
                transacciones == null || transacciones.isBlank()
                        ? "No hay transacciones registradas."
                        : transacciones
        );

        scrollTransacciones = new JScrollPane(
                areaTransacciones
        );

        scrollTransacciones.setVisible(false);

        btnConsultarTransacciones.addActionListener(e -> {

            boolean mostrar =
                    !scrollTransacciones.isVisible();

            scrollTransacciones.setVisible(mostrar);

            btnConsultarTransacciones.setText(
                    mostrar
                    ? "Ocultar transacciones"
                    : "Consultar transacciones"
            );

            panelHistorial.revalidate();
            panelHistorial.repaint();
        });

        panelHistorial.add(
                btnConsultarTransacciones,
                BorderLayout.NORTH
        );

        panelHistorial.add(
                scrollTransacciones,
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
    public void actualizarTransacciones(String transacciones) {

        SwingUtilities.invokeLater(() -> {

            areaTransacciones.setText(
                    transacciones == null || transacciones.isBlank()
                            ? "No hay transacciones registradas."
                            : transacciones
            );
        });
    }

    // -----------------------------------------
    // PRUEBA INDEPENDIENTE DE LA INTERFAZ
    // -----------------------------------------

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            String transaccionesPrueba =
                    "T1 - José Miguel compró Biblioteca\n"
                    + "T2 - Luis pagó ₡100 de alquiler\n"
                    + "T3 - José recibió ₡200 por Salida\n";

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

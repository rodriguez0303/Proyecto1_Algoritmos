package Interfaz;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;


public class VentanaInicio extends JFrame {

    private JRadioButton rbPorRondas;
    private JRadioButton rbNormal;

    private JSpinner spRondas;

    public VentanaInicio() {

        // -------------------------------------------------
        // CONFIGURACIÓN DE LA VENTANA
        // -------------------------------------------------

        setTitle("Monopoly TEC");

        setSize(450, 350);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        setResizable(false);

        setLayout(new BorderLayout());

        getContentPane().setBackground(new Color(225, 215, 185));

        // -------------------------------------------------
        // TÍTULO
        // -------------------------------------------------

        JLabel lblTitulo = new JLabel("MONOPOLY TEC", JLabel.CENTER);

        lblTitulo.setFont(new Font("Serif", Font.BOLD, 36));

        lblTitulo.setForeground(Color.WHITE);

        lblTitulo.setOpaque(true);

        lblTitulo.setBackground(new Color(35, 90, 65));

        lblTitulo.setBorder(BorderFactory.createEmptyBorder(18, 10, 18, 10));

        add(lblTitulo, BorderLayout.NORTH);


        // -------------------------------------------------
        // OPCIONES DE PARTIDA
        // -------------------------------------------------

        JPanel panelOpciones = new JPanel();

        panelOpciones.setLayout(new BoxLayout(panelOpciones, BoxLayout.Y_AXIS));

        panelOpciones.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));

        panelOpciones.setBackground(new Color(225, 215, 185));

        JLabel lblModo = new JLabel("Seleccione el modo de partida:");

        lblModo.setAlignmentX(Component.LEFT_ALIGNMENT);

        rbPorRondas = new JRadioButton("Partida por rondas", true);

        rbNormal = new JRadioButton("Partida normal - Hasta que haya un ganador");

        rbPorRondas.setOpaque(false);
        rbNormal.setOpaque(false);

        ButtonGroup grupoModo = new ButtonGroup();

        grupoModo.add(rbPorRondas);
        grupoModo.add(rbNormal);

        rbPorRondas.setFocusPainted(false);
        rbNormal.setFocusPainted(false);

        // -------------------------------------------------
        // CANTIDAD DE RONDAS
        // -------------------------------------------------

        JPanel panelRondas = new JPanel();

        panelRondas.setLayout(new BoxLayout(panelRondas,BoxLayout.X_AXIS));

        panelRondas.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblRondas = new JLabel("Número de rondas: ");

        spRondas =new JSpinner(new SpinnerNumberModel(5, 1, 100, 1));

        spRondas.setMaximumSize(new Dimension(70, 30));

        panelRondas.add(lblRondas);
        panelRondas.add(spRondas);
        panelRondas.setOpaque(false);

        // -------------------------------------------------
        // BOTÓN INICIAR
        // -------------------------------------------------

        JButton btnIniciar = new JButton("Iniciar partida");

        btnIniciar.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnIniciar.setMaximumSize(new Dimension(180, 35));

        btnIniciar.setBackground(new Color(35, 90, 65));
        
        btnIniciar.setForeground(Color.WHITE);

        btnIniciar.setFont(new Font("SansSerif", Font.BOLD, 13));

        btnIniciar.setFocusPainted(false);

        // -------------------------------------------------
        // ACTIVAR / DESACTIVAR RONDAS
        // -------------------------------------------------

        rbPorRondas.addActionListener(e -> {
            spRondas.setEnabled(true);
        });

        rbNormal.addActionListener(e -> {
            spRondas.setEnabled(false);
        });

        // -------------------------------------------------
        // INICIAR PARTIDA
        // -------------------------------------------------

        btnIniciar.addActionListener(e -> {

            boolean partidaPorRondas =
                rbPorRondas.isSelected();

            int maxRondas = 0;

            if (partidaPorRondas) {

                maxRondas =
                    (int) spRondas.getValue();

            }

            VentanaJuego ventanaJuego =
                new VentanaJuego(
                    partidaPorRondas,
                    maxRondas
                );

            ventanaJuego.setVisible(true);

            dispose();
        });


        // -------------------------------------------------
        // AGREGAR COMPONENTES
        // -------------------------------------------------

        panelOpciones.add(lblModo);

        panelOpciones.add(
            Box.createVerticalStrut(15)
        );

        panelOpciones.add(rbPorRondas);

        panelOpciones.add(
            Box.createVerticalStrut(5)
        );

        panelOpciones.add(panelRondas);

        panelOpciones.add(
            Box.createVerticalStrut(15)
        );

        panelOpciones.add(rbNormal);

        panelOpciones.add(
            Box.createVerticalStrut(30)
        );

        panelOpciones.add(btnIniciar);


        add(
            panelOpciones,
            BorderLayout.CENTER
        );
    }


    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            VentanaInicio ventana =
                new VentanaInicio();

            ventana.setVisible(true);

        });
    }
}
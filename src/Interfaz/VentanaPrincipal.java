package Interfaz;

// Importaciones necesarias para la interfaz gráfica
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.BorderFactory;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;


// Clase principal de la interfaz gráfica del juego
public class VentanaPrincipal extends JFrame {

    // Constructor de la ventana principal
    public VentanaPrincipal() {

        // -------------------------------------------------
        // CONFIGURACIÓN DE LA VENTANA
        // -------------------------------------------------

        setTitle("Monopoly TEC");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());


        // -------------------------------------------------
        // CREACIÓN DE PANELES
        // -------------------------------------------------

        JPanel panelTablero = new JPanel();
        JPanel panelJugador = new JPanel();
        JPanel panelDados = new JPanel();


        // -------------------------------------------------
        // CONFIGURACIÓN DEL PANEL DEL TABLERO
        // -------------------------------------------------

        panelTablero.setBorder(BorderFactory.createTitledBorder("Tablero"));
        panelTablero.setLayout(new GridLayout(7, 7, 2, 2));

        int numeroCasilla = 0;

        for(int fila = 0; fila < 7; fila++) {
            for (int columna = 0; columna < 7; columna++) {
                if (fila == 0 || fila == 6 || columna == 0 || columna == 6) {
                    JPanel casilla = new JPanel();

                    casilla.setBorder(BorderFactory.createEtchedBorder());

                    JLabel textoCasilla = new JLabel("Casilla " + numeroCasilla);

                    casilla.add(textoCasilla);

                    panelTablero.add(casilla);

                    numeroCasilla++;
                } else {
                    JPanel espacioVacio = new JPanel();
                    panelTablero.add(espacioVacio);

                }
            }
        }

        // -------------------------------------------------
        // CONFIGURACIÓN DEL PANEL DEL JUGADOR
        // -------------------------------------------------

        panelJugador.setLayout(
            new BoxLayout(panelJugador, BoxLayout.Y_AXIS)
        );

        panelJugador.setPreferredSize(
            new Dimension(300, 700)
        );

        panelJugador.setBorder(
            BorderFactory.createTitledBorder("Jugador")
        );


        // -------------------------------------------------
        // CONFIGURACIÓN DEL PANEL DE DADOS
        // -------------------------------------------------

        panelDados.setLayout(
            new GridLayout(1, 2, 10, 0)
        );

        panelDados.setBorder(
            BorderFactory.createTitledBorder("Dados")
        );

        panelDados.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 70)
        );


        // -------------------------------------------------
        // INFORMACIÓN DEL JUGADOR
        // -------------------------------------------------

        JLabel lblNombre = new JLabel("Jugador: José");
        JLabel lblSaldo = new JLabel("Saldo: ₡1500");
        JLabel lblPosicion = new JLabel("Posición: 0");
        JLabel lblTurno = new JLabel("Turno actual: José");


        // -------------------------------------------------
        // INFORMACIÓN DE LOS DADOS
        // -------------------------------------------------

        JLabel lblDado1 = new JLabel("Dado 1: -");
        JLabel lblDado2 = new JLabel("Dado 2: -");

        panelDados.add(lblDado1);
        panelDados.add(lblDado2);


        // -------------------------------------------------
        // BOTONES
        // -------------------------------------------------

        JButton btnTirarDados = new JButton("Tirar dados");
        JButton btnComprar = new JButton("Comprar propiedad");
        JButton btnTerminarTurno = new JButton("Terminar turno");


        // -------------------------------------------------
        // ESTADO DEL JUEGO
        // -------------------------------------------------

        JLabel lblEstado = new JLabel("Esperando acción...");


        // -------------------------------------------------
        // ORGANIZACIÓN DEL PANEL DEL JUGADOR
        // -------------------------------------------------

        panelJugador.add(lblNombre);
        panelJugador.add(lblSaldo);
        panelJugador.add(lblPosicion);
        panelJugador.add(lblTurno);

        panelJugador.add(Box.createVerticalStrut(15));

        panelJugador.add(panelDados);

        panelJugador.add(Box.createVerticalStrut(15));

        panelJugador.add(btnTirarDados);
        panelJugador.add(btnComprar);
        panelJugador.add(btnTerminarTurno);

        panelJugador.add(Box.createVerticalStrut(20));

        panelJugador.add(lblEstado);


        // -------------------------------------------------
        // EVENTOS DE LOS BOTONES
        // -------------------------------------------------

        btnTirarDados.addActionListener(e -> {

            // Valores simulados temporalmente.
            // Más adelante vendrán del servidor.
            lblDado1.setText("Dado 1: 3");
            lblDado2.setText("Dado 2: 5");

            lblEstado.setText("Resultado de los dados recibido.");
        });


        btnComprar.addActionListener(e -> {

            lblEstado.setText(
                "Esperando acción de compra..."
            );
        });


        btnTerminarTurno.addActionListener(e -> {

            lblEstado.setText(
                "Solicitud para terminar turno..."
            );
        });


        // -------------------------------------------------
        // AGREGAR PANELES A LA VENTANA
        // -------------------------------------------------

        add(panelTablero, BorderLayout.CENTER);
        add(panelJugador, BorderLayout.EAST);
    }


    // Método principal
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            VentanaPrincipal ventana =
                new VentanaPrincipal();

            ventana.setVisible(true);
        });
    }
}
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
import javax.swing.JTextArea;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import java.util.Random;
import java.util.Arrays;


// Clase principal de la interfaz gráfica del juego
public class VentanaPrincipal extends JFrame {

    private JPanel [] casillasVisuales = new JPanel[24];
    private JPanel[] panelesFichas = new JPanel[24];

    private JLabel[] etiquetasPropietarios = new JLabel[24];
    private JLabel[] fichasJugadores = new JLabel[4];

    private Random generador = new Random();

    private int[] posicionesJugadoresSimulados = {0, 0, 0, 0};
    private int [] posicionesVisuales = {-1, -1, -1, -1};
    private int jugadorActualSimulado = 0;
    private int[] propietariosSimulados = new int[24];

    private boolean dadosLanzadosSimulados = false;

    // Constructor de la ventana principal
    public VentanaPrincipal() {

        Arrays.fill(propietariosSimulados, -1);

        // -------------------------------------------------
        // CONFIGURACIÓN DE LA VENTANA
        // -------------------------------------------------

        setTitle("Monopoly TEC");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
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

        for(int fila = 0; fila < 7; fila++) {
            for (int columna = 0; columna < 7; columna++) {
                if (fila == 0 || fila == 6 || columna == 0 || columna == 6) {

                    int numeroCasilla;
                    if (fila == 0) {
                        numeroCasilla = columna;
                    }
                    else if (columna == 6) {
                        numeroCasilla = 6 + fila;
                    }
                    else if (fila == 6) {
                        numeroCasilla = 18 - columna;
                    }
                    else {
                        numeroCasilla = 24 - fila;
                    }

                    JPanel casilla = new JPanel();

                    casilla.setLayout(new BorderLayout());

                    JPanel panelFichas = new JPanel();

                    casillasVisuales[numeroCasilla] = casilla;
                    panelesFichas[numeroCasilla] = panelFichas;
                    
                    casilla.setBorder(BorderFactory.createEtchedBorder());

                    JLabel textoCasilla = new JLabel("Casilla " + numeroCasilla);
                    JLabel lblPropietario = new JLabel("");

                    etiquetasPropietarios[numeroCasilla] = lblPropietario;

                    casilla.add(textoCasilla, BorderLayout.NORTH);
                    casilla.add(panelFichas, BorderLayout.CENTER);
                    casilla.add(lblPropietario, BorderLayout.SOUTH);

                    panelTablero.add(casilla);

                } else {
                    JPanel espacioVacio = new JPanel();
                    panelTablero.add(espacioVacio);

                }
            }
        }

        fichasJugadores[0] = new JLabel("J1");
        fichasJugadores[1] = new JLabel("J2");
        fichasJugadores[2] = new JLabel("J3");
        fichasJugadores[3] = new JLabel("J4");

        marcarPosicionJugador(0, 0);
        marcarPosicionJugador(1,0);
        marcarPosicionJugador(2, 0);
        marcarPosicionJugador(3,0);

        // -------------------------------------------------
        // CONFIGURACIÓN DEL PANEL DEL JUGADOR
        // -------------------------------------------------

        panelJugador.setLayout(
            new BoxLayout(panelJugador, BoxLayout.Y_AXIS)
        );

        panelJugador.setPreferredSize(
            new Dimension(300, 0)
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
        btnComprar.setEnabled(false);

        JButton btnTerminarTurno = new JButton("Terminar turno");


        // -------------------------------------------------
        // ESTADO DEL JUEGO
        // -------------------------------------------------

        JTextArea lblEstado = new JTextArea("Esperando acción...");

        lblEstado.setEditable(false);
        lblEstado.setLineWrap(true);
        lblEstado.setWrapStyleWord(true);
        lblEstado.setOpaque(false);
        lblEstado.setFocusable(false);

        lblEstado.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

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

            if (dadosLanzadosSimulados) {
                lblEstado.setText("J" + (jugadorActualSimulado + 1)
                                    + " ya lanzó los dados en este turno.");
                return;
            }

            // Valores simulados temporalmente.
            int dado1 = generador.nextInt(6) + 1;
            int dado2 = generador.nextInt(6) + 1;
            int total = dado1 + dado2;

            lblDado1.setText("Dado 1: " + dado1);
            lblDado2.setText("Dado 2: " + dado2);

            posicionesJugadoresSimulados[jugadorActualSimulado] = (posicionesJugadoresSimulados[jugadorActualSimulado] + total) % 24;

            int nuevaPosicion = posicionesJugadoresSimulados[jugadorActualSimulado];

            String mensajeCasilla;

            int propietario = propietariosSimulados[nuevaPosicion];

            if (!esPropiedadSimulada(nuevaPosicion)) {
                btnComprar.setEnabled(false);

                mensajeCasilla = "Cayó en una casilla especial.";
            }
            else if (propietario == -1) {
                btnComprar.setEnabled(true);

                mensajeCasilla = "La propiedad esta disponible.";
            }
            else if (propietario == jugadorActualSimulado) {
                btnComprar.setEnabled(false);

                mensajeCasilla = "Cayó en su propia propiedad.";
            }
            else { 
                btnComprar.setEnabled(false);

                mensajeCasilla = "La propiedad pertenece a J" + (propietario + 1) + ".";
            }

            lblPosicion.setText("Posición: " + nuevaPosicion);

            marcarPosicionJugador(jugadorActualSimulado, nuevaPosicion);

            dadosLanzadosSimulados = true;

            btnTirarDados.setEnabled(false);

            lblEstado.setText("J" + (jugadorActualSimulado + 1) + " avanzó " + total + " posiciones. " + mensajeCasilla);
        });


        btnComprar.addActionListener(e -> {

            int posicionActual = posicionesJugadoresSimulados[jugadorActualSimulado];

            if (!esPropiedadSimulada(posicionActual)) {
                lblEstado.setText("Esta casilla no se puede comprar.");
                return;
            }
            if (propietariosSimulados[posicionActual] != -1) {
                lblEstado.setText("Esta propiedad ya tiene propietario.");
                return;
            }
            propietariosSimulados[posicionActual] = jugadorActualSimulado;

            etiquetasPropietarios[posicionActual].setText("Dueño: J" + (jugadorActualSimulado + 1));

            lblEstado.setText("J" + (jugadorActualSimulado +1) + " compró la Casilla " + posicionActual + ".");

            btnComprar.setEnabled(false);
        });


        btnTerminarTurno.addActionListener(e -> {
            jugadorActualSimulado = (jugadorActualSimulado + 1) % 4;
            dadosLanzadosSimulados = false;
            btnTirarDados.setEnabled(true);
            btnComprar.setEnabled(false);

            lblTurno.setText("Turno actual: J" + (jugadorActualSimulado + 1));

            lblPosicion.setText("Posición: " + posicionesJugadoresSimulados[jugadorActualSimulado]);

            lblDado1.setText("Dado 1: -");
            lblDado2.setText("Dado 2: -");

            lblEstado.setText("Turno de J"+ (jugadorActualSimulado + 1));
        });


        // -------------------------------------------------
        // AGREGAR PANELES A LA VENTANA
        // -------------------------------------------------

        add(panelTablero, BorderLayout.CENTER);
        add(panelJugador, BorderLayout.EAST);
    }

    private boolean esPropiedadSimulada(int posicion) {
        return posicion != 0 &&
        posicion != 3 &&
        posicion != 6 &&
        posicion != 9 &&
        posicion != 12 &&
        posicion != 15 &&
        posicion != 18 &&
        posicion != 21;
    }

    private void marcarPosicionJugador(int jugador, int posicion) {
        if (jugador < 0 || jugador >= fichasJugadores.length) {
        return;
        }
        if (posicion < 0 || posicion >= panelesFichas.length){
            return;
        }
        JLabel ficha = fichasJugadores[jugador];

        int posicionAnterior = posicionesVisuales[jugador];

        if (posicionAnterior != -1) {
            JPanel panelAnterior = panelesFichas[posicionAnterior];
            panelAnterior.remove(ficha);

            panelAnterior.revalidate();
            panelAnterior.repaint();
        }
        JPanel panelNuevo = panelesFichas[posicion];
        panelNuevo.add(ficha);

        panelNuevo.revalidate();
        panelNuevo.repaint();

        posicionesVisuales[jugador] = posicion;
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
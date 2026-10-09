package Interfaz;

import LogicaJuego.Cliente;

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
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.Timer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Component;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import java.util.Random;
import java.util.Arrays;


// Clase principal de la interfaz gráfica del juego
public class VentanaJuego extends JFrame {

    private JPanel [] casillasVisuales = new JPanel[24];
    private JPanel[] panelesFichas = new JPanel[24];
    private JPanel[] tarjetasJugadores = new JPanel[4];
    private JPanel panelContenidoCentro;

    private JLabel[] lblSaldoJugadores = new JLabel[4];
    private JLabel[] lblEstadoJugadores = new JLabel[4];
    private JLabel[] etiquetasPropietarios = new JLabel[24];
    private JLabel[] etiquetasPrecios = new JLabel[24];
    private JLabel[] fichasJugadores = new JLabel[4];
    private JLabel lblNombre;
    private JLabel lblSaldo;
    private JLabel lblPatrimonio;
    private JLabel lblPosicion;
    private JLabel lblTurno;
    private JLabel lblNumeroRonda;
    private JLabel lblDado1;
    private JLabel lblDado2;

    private JButton btnTirarDados;
    private JButton btnComprar;
    private JButton btnTerminarTurno;

    private Cliente cliente;

    private JTextArea lblPropiedades;
    private JTextArea areaHistorial;
    private JTextArea lblEstado;

    private Random generador = new Random();

    private String[] nombresCasillas = {
    "Salida",        // 0
    "Comedor Institucional",   // 1
    "Soda Forestal",   // 2
    "Evento",        // 3
    "Biblioteca Figueres Ferrer",   // 4
    "Learning Commons",   // 5
    "Edificio D3",      // 6
    "ASETEC",   // 7
    "Puesto Antonio",   // 8
    "Evento",        // 9
    "Bosque de Bambúes",  // 10
    "Lago",  // 11
    "Paseo en el TEC",      // 12
    "GymTEC",  // 13
    "Soda Deportiva",  // 14
    "Evento",        // 15
    "Escuela de Computadores",  // 16
    "Escuela de Electrónica",  // 17
    "Ir al D3",      // 18
    "Cancha de fútbol",  // 19
    "Cancha de béisbol",  // 20
    "Evento",        // 21
    "BICITEC",  // 22
    "UberTEC"   // 23
    };
    private String[] cartasEventoSimuladas = {
        "Recibe ₡100 por beca del TEC.",
        "Paga ₡100 por romper algo de laboratorio.",
        "Avanza 3 posiciones.",
        "Vas directamente al Edificio D3.",
        "Vas directamente a la Salida."
    };

    private final java.util.Set<String> transaccionesMostradasEnLinea = new java.util.HashSet<>();

    private int maxRondasSimulado;
    private int numeroRondaSimulada = 1;
    private int indiceCartaEventosSimulada = 0;
    private int[] posicionesJugadoresSimulados = {0, 0, 0, 0};
    private int [] posicionesVisuales = {-1, -1, -1, -1};

    // Animación de las fichas: avanzan casilla por casilla en lugar de saltar.
    private static final int MS_POR_CASILLA = 250;      // Tiempo que la ficha se queda en cada casilla
    private static final int MS_ANTES_DE_SALTO = 700;   // Pausa antes de que una carta o "Ir al D3" la mueva
    private Timer[] animacionesFichas = new Timer[4];   // Animación en curso de cada ficha (null si está quieta)
    private int[] destinosFichas = {-1, -1, -1, -1};    // Casilla final donde debe terminar cada ficha
    private int pasosDadosPendientes = 0;               // Total de los últimos dados en línea, falta animarlo
    private int jugadorDadosPendiente = -1;             // Jugador que lanzó esos dados
    private int indiceTurnoEnLinea = -1;                // Jugador con el turno según el último ESTADO
    private int jugadorActualSimulado = 0;
    private int[] propietariosSimulados = new int[24];

    private double[] saldosJugadoresSimulados = {1500, 1500, 1500, 1500};
    private double[] preciosPropiedadesSimulados = {
        0,      // 0 Salida

        100,    // 1 Comedor Institucional
        100,    // 2 Soda Forestal

        0,      // 3 Evento

        150,    // 4 Biblioteca Figueres Ferrer
        150,    // 5 Learning Commons

        0,      // 6 D3

        200,    // 7 ASETEC
        200,    // 8 Puesto Antonio

        0,      // 9 Evento

        250,    // 10 Bosque de Bambúes
        250,    // 11 Lago

        0,      // 12 Especial

        300,    // 13 GymTEC
        300,    // 14 Soda Deportiva

        0,      // 15 Evento

        350,    // 16 Escuela de Computadores
        350,    // 17 Escuela de Electrónica

        0,      // 18 Ir al D3

        400,    // 19 Cancha de fútbol
        400,    // 20 Cancha de béisbol

        0,      // 21 Evento

        450,    // 22 BICITEC
        450     // 23 UberTEC
    };

    private static final double ALQUILER_SIMULADO = 100;
    private static final double PREMIO_SALIDA_SIMULADO = 200;

    private boolean modoEnLinea = false;
    private boolean partidaPorRondas;
    private boolean[] pierdeTurnoSimulado = {false, false, false, false};
    private boolean dadosLanzadosSimulados = false;
    private boolean[] jugadoresActivosSimulados = { true, true, true, true};

    // Constructor de la ventana principal
    public VentanaJuego() {
        this(true, 5);
    }

    public VentanaJuego(boolean partidaPorRondas, int maxRondasSimulado) {

        this.partidaPorRondas = partidaPorRondas;
        this.maxRondasSimulado = maxRondasSimulado;

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
        panelTablero.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        Dimension tamanoCasilla = new Dimension(145, 105);
        Dimension tamanoPanelFichas = new Dimension(110, 45);
        Dimension tamanoPropietario = new Dimension(110, 22);

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

                    casilla.setPreferredSize(tamanoCasilla);
                    casilla.setMinimumSize(tamanoCasilla);

                    JPanel panelFichas = new JPanel();
                    panelFichas.setOpaque(false);

                    panelFichas.setPreferredSize(tamanoPanelFichas);
                    panelFichas.setMinimumSize(tamanoPanelFichas);

                    casilla.setBackground(obtenerColorCasillaSimulada(numeroCasilla));

                    casillasVisuales[numeroCasilla] = casilla;
                    panelesFichas[numeroCasilla] = panelFichas;
                    
                    casilla.setBorder(BorderFactory.createEtchedBorder());

                    JLabel textoCasilla = new JLabel(nombresCasillas[numeroCasilla]);
                    JPanel panelSuperior = new JPanel(new BorderLayout());
                    panelSuperior.setOpaque(false);
                    textoCasilla.setHorizontalAlignment((JLabel.CENTER));

                    if (esPropiedadSimulada(numeroCasilla)) {
                        JPanel franjaColor = new JPanel();

                        franjaColor.setPreferredSize(new Dimension(0, 14));

                        franjaColor.setBackground(obtenerColorGrupoPropiedadSimulada(numeroCasilla));

                        panelSuperior.add(franjaColor, BorderLayout.NORTH);

                        JLabel lblPrecio = new JLabel("₡" + (int) preciosPropiedadesSimulados[numeroCasilla], JLabel.CENTER);

                        lblPrecio.setFont(lblPrecio.getFont().deriveFont(Font.PLAIN, 11f));

                        etiquetasPrecios[numeroCasilla] = lblPrecio;

                        panelSuperior.add(lblPrecio, BorderLayout.SOUTH);
                    }

                    panelSuperior.add(textoCasilla, BorderLayout.CENTER);

                    JLabel lblPropietario = new JLabel(" ");

                    lblPropietario.setPreferredSize(tamanoPropietario);
                    lblPropietario.setMinimumSize(tamanoPropietario);

                    etiquetasPropietarios[numeroCasilla] = lblPropietario;

                    casilla.add(panelSuperior, BorderLayout.NORTH);
                    casilla.add(panelFichas, BorderLayout.CENTER);
                    casilla.add(lblPropietario, BorderLayout.SOUTH);

                    gbc.gridx = columna;
                    gbc.gridy = fila;
                    gbc.gridwidth = 1;
                    gbc.gridheight = 1;

                    panelTablero.add(casilla, gbc);
                }
            }
        }

        // --------------------------------------------------
        // PANEL CENTRAL
        // --------------------------------------------------

        JPanel panelCentro = new JPanel();

        panelCentro.setBorder(BorderFactory.createEtchedBorder());

        gbc.gridx = 1;
        gbc.gridy = 1;

        gbc.gridwidth = 5;
        gbc.gridheight = 5;

        gbc.weightx = 5.0;
        gbc.weighty = 5.0;

        panelTablero.add(panelCentro, gbc);

        panelCentro.setLayout(new BorderLayout());

        panelCentro.setBackground(new Color(225, 215, 185));

        ImageIcon iconoMapa = new ImageIcon(getClass().getResource("/Interfaz/recursos/aerea_tec.jpg"));

        Image imagenMapa = iconoMapa.getImage();

        panelContenidoCentro = new JPanel(new BorderLayout()) {
            @Override 
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(imagenMapa, 0, 0, getWidth(), getHeight(), this);

            }
        };

        JLabel lblTituloCentro = new JLabel("MONOPOLY TEC", JLabel.CENTER);

        lblTituloCentro.setFont(new Font("Serif", Font.BOLD, 48));

        lblTituloCentro.setForeground((Color.WHITE));

        lblTituloCentro.setOpaque(true);

        lblTituloCentro.setBackground(new Color(35, 90, 65));

        lblTituloCentro.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        panelContenidoCentro.add(lblTituloCentro, BorderLayout.NORTH);

        panelCentro.add(panelContenidoCentro, BorderLayout.CENTER);

        fichasJugadores[0] = new JLabel("J1");
        fichasJugadores[1] = new JLabel("J2");
        fichasJugadores[2] = new JLabel("J3");
        fichasJugadores[3] = new JLabel("J4");
        
        fichasJugadores[0].setBackground(new Color(210, 60, 60));
        fichasJugadores[1].setBackground(new Color(60, 100, 210));
        fichasJugadores[2].setBackground(new Color(60, 160, 90));
        fichasJugadores[3].setBackground(new Color(230, 160, 50));

        for (JLabel ficha : fichasJugadores) {
            ficha.setOpaque(true);
            ficha.setForeground(Color.WHITE);

            ficha.setBorder(BorderFactory.createEmptyBorder(4, 7, 4, 7));
            ficha.setFont(ficha.getFont().deriveFont(Font.BOLD));
        }

        marcarPosicionJugador(0, 0);
        marcarPosicionJugador(1,0);
        marcarPosicionJugador(2, 0);
        marcarPosicionJugador(3,0);

        resaltarCasillaJugadorActualSimulado();

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
        // RESUMEN DE LOS JUGADORES
        // -----------------------------------------------

        JPanel panelResumenJugadores = new JPanel(new GridLayout(2, 2, 5, 5));

        panelResumenJugadores.setBorder(BorderFactory.createTitledBorder("Jugadores"));

        panelResumenJugadores.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        panelResumenJugadores.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (int i = 0; i < 4; i++) {
            panelResumenJugadores.add(crearTarjetaJugador(i));
        }
        actualizarTarjetasJugadoresSimulados();

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

        lblNombre = new JLabel("Jugador actual: -");
        lblSaldo = new JLabel("Saldo: ₡1500");
        lblPatrimonio = new JLabel("Patrimonio: ₡1500");
        lblPosicion = new JLabel("Posición: 0 - Salida");
        lblTurno = new JLabel("Turno actual: J1");
        lblNumeroRonda = new JLabel();
        actualizarEtiquetaRonda();

        JPanel panelInfoJugador = new JPanel();

        panelInfoJugador.setLayout(new BoxLayout(panelInfoJugador, BoxLayout.Y_AXIS));

        panelInfoJugador.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Estado actual"),
                                    BorderFactory.createEmptyBorder(5, 8, 8, 8)));

        panelInfoJugador.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        panelInfoJugador.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblPropiedades = new JTextArea("Propiedades: ninguna");

        lblPropiedades.setEditable(false);
        lblPropiedades.setLineWrap(true);
        lblPropiedades.setWrapStyleWord(true);
        lblPropiedades.setOpaque(false);
        lblPropiedades.setFocusable(false);
        lblPropiedades.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        lblPropiedades.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblPropiedades.setFont(lblPropiedades.getFont().deriveFont(Font.BOLD));

        lblPatrimonio.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblPatrimonio.setFont(new Font("SansSerif", Font.BOLD, 13));

        lblNumeroRonda.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblNumeroRonda.setFont(new Font("SansSerif", Font.BOLD, 13));

        lblNombre.setFont(new Font("SansSerif", Font.BOLD, 16));

        lblSaldo.setFont(new Font("SansSerif", Font.BOLD, 13));

        lblTurno.setOpaque(true);
        lblTurno.setForeground(Color.WHITE);
        lblTurno.setBackground(fichasJugadores[jugadorActualSimulado].getBackground());
        lblTurno.setBorder(BorderFactory.createEmptyBorder(4, 7, 4, 7));

        panelInfoJugador.add(lblNombre);

        panelInfoJugador.add(Box.createVerticalStrut(4));

        panelInfoJugador.add(lblSaldo);
        panelInfoJugador.add(lblPatrimonio);
        panelInfoJugador.add(lblPosicion);

        panelInfoJugador.add(Box.createVerticalStrut(5));

        panelInfoJugador.add(lblTurno);

        panelInfoJugador.add(Box.createVerticalStrut(3));

        panelInfoJugador.add(lblNumeroRonda);

        panelInfoJugador.add(Box.createVerticalStrut(5));

        panelInfoJugador.add(lblPropiedades);

        // -------------------------------------------------
        // INFORMACIÓN DE LOS DADOS
        // -------------------------------------------------

        JPanel panelDado1 = new JPanel(new BorderLayout());
        JPanel panelDado2 = new JPanel(new BorderLayout());

        panelDado1.setBackground(Color.WHITE);
        panelDado2.setBackground(Color.WHITE);

        panelDado1.setBorder(BorderFactory.createLineBorder(new Color(160, 160, 160), 1));
        panelDado2.setBorder(BorderFactory.createLineBorder(new Color(160, 160, 160), 1));

        JLabel tituloDado1 = new JLabel("Dado 1", JLabel.CENTER);
        JLabel tituloDado2 = new JLabel("Dado 2", JLabel.CENTER);

        tituloDado1.setFont(new Font("SansSerif", Font.BOLD, 12));
        tituloDado2.setFont(new Font("SansSerif", Font.BOLD, 12));

        lblDado1 = new JLabel("-", JLabel.CENTER);
        lblDado2 = new JLabel("-", JLabel.CENTER);

        lblDado1.setFont(new Font("SansSerif", Font.BOLD, 42));
        lblDado2.setFont(new Font("SansSerif", Font.BOLD, 42));

        lblDado1.setForeground(new Color(45, 45, 45));
        lblDado2.setForeground(new Color(45, 45, 45));

        panelDado1.add(tituloDado1, BorderLayout.NORTH);
        panelDado1.add(lblDado1, BorderLayout.CENTER);

        panelDado2.add(tituloDado2, BorderLayout.NORTH);
        panelDado2.add(lblDado2, BorderLayout.CENTER);

        panelDados.add(panelDado1);
        panelDados.add(panelDado2);

        // -------------------------------------------------
        // BOTONES
        // -------------------------------------------------

        btnTirarDados = new JButton("Tirar dados");
        btnComprar = new JButton("Comprar propiedad");
        btnComprar.setEnabled(false);

        btnTerminarTurno = new JButton("Terminar turno");
        btnTerminarTurno.setEnabled(false);

        Dimension tamanoBoton = new Dimension(260, 38);

        btnTirarDados.setPreferredSize(tamanoBoton);
        btnTirarDados.setMaximumSize(tamanoBoton);

        btnComprar.setPreferredSize(tamanoBoton);
        btnComprar.setMaximumSize(tamanoBoton);

        btnTerminarTurno.setPreferredSize(tamanoBoton);
        btnTerminarTurno.setMaximumSize(tamanoBoton);

        Font fuenteBotones = new Font("SansSerif", Font.BOLD, 13);

        btnTirarDados.setFont(fuenteBotones);
        btnComprar.setFont(fuenteBotones);
        btnTerminarTurno.setFont(fuenteBotones);

        btnTirarDados.setBackground(new Color(45, 110, 75));
        btnTirarDados.setForeground(Color.WHITE);

        btnComprar.setBackground(new Color(225, 185, 75));
        btnComprar.setForeground(Color.WHITE);

        btnTerminarTurno.setBackground(new Color(90, 95, 100));
        btnTerminarTurno.setForeground(Color.WHITE);

        btnTirarDados.setFocusPainted(false);
        btnComprar.setFocusPainted(false);
        btnTerminarTurno.setFocusPainted(false);

        // -------------------------------------------------
        // ESTADO DEL JUEGO
        // -------------------------------------------------

        lblEstado = new JTextArea("Esperando acción...");

        lblEstado.setEditable(false);
        lblEstado.setLineWrap(true);
        lblEstado.setWrapStyleWord(true);
        lblEstado.setFocusable(false);
        lblEstado.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        lblEstado.setFont(new Font("SansSerif" , Font.BOLD, 13));
        lblEstado.setBackground(new Color(245, 245, 245));
        lblEstado.setOpaque(true);
        lblEstado.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)), BorderFactory.createEmptyBorder(8, 8, 8,8)));


        // -------------------------------------------------
        // HISTORIAL VISIBLE DE ACCIONES
        // -------------------------------------------------

        areaHistorial = new JTextArea();
        areaHistorial.setFont(new Font("SansSerif", Font.PLAIN, 12));

        areaHistorial.setEditable(false);
        areaHistorial.setLineWrap(true);
        areaHistorial.setWrapStyleWord(true);
        areaHistorial.setFocusable(false);

        JScrollPane scrollHistorial = new JScrollPane(areaHistorial);

        scrollHistorial.setPreferredSize(new Dimension(280, 170));

        scrollHistorial.setMaximumSize(new Dimension(Integer.MAX_VALUE, 170));

        scrollHistorial.setAlignmentX(Component.LEFT_ALIGNMENT);

        scrollHistorial.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(160, 160, 160)), "Historial de acciones"));

        // -------------------------------------------------
        // ALINEACION DE COMPONENTES
        // -------------------------------------------------
        
        lblNombre.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblSaldo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblPosicion.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblTurno.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelDados.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnTirarDados.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnComprar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnTerminarTurno.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblEstado.setAlignmentX(Component.LEFT_ALIGNMENT);

        // -------------------------------------------------
        // ORGANIZACIÓN DEL PANEL DEL JUGADOR
        // -------------------------------------------------

        panelJugador.add(panelInfoJugador);

        panelJugador.add(Box.createVerticalStrut(15));

        panelJugador.add(panelResumenJugadores);

        panelJugador.add(Box.createVerticalStrut(15));

        panelJugador.add(panelDados);

        panelJugador.add(Box.createVerticalStrut(15));

        panelJugador.add(btnTirarDados);

        panelJugador.add(Box.createVerticalStrut(15));

        panelJugador.add(btnComprar);

        panelJugador.add(Box.createVerticalStrut(15));

        panelJugador.add(btnTerminarTurno);

        panelJugador.add(Box.createVerticalStrut(20));

        panelJugador.add(lblEstado);

        panelJugador.add(Box.createVerticalStrut(15));

        panelJugador.add(scrollHistorial);

        // -------------------------------------------------
        // EVENTOS DE LOS BOTONES
        // -------------------------------------------------

        btnTirarDados.addActionListener(e -> {

            if (modoEnLinea) {
                cliente.enviarSolicitud("TIRAR_DADOS");
                return;
            }

            if (dadosLanzadosSimulados) {
                lblEstado.setText("J" + (jugadorActualSimulado + 1)
                                    + " ya lanzó los dados en este turno.");
                return;
            }

            // Valores simulados temporalmente.
            int dado1 = generador.nextInt(6) + 1;
            int dado2 = generador.nextInt(6) + 1;
            int total = dado1 + dado2;
            agregarHistorialSimulado("J" + (jugadorActualSimulado + 1) + " lanzó " + dado1 + " y " + dado2 + " (total: " + total + ").");

            lblDado1.setText(obtenerCaraDado(dado1));
            lblDado2.setText(obtenerCaraDado(dado2));

            int posicionAnterior = posicionesJugadoresSimulados[jugadorActualSimulado];

            int nuevaPosicion = (posicionAnterior + total) % 24;

            posicionesJugadoresSimulados[jugadorActualSimulado] = nuevaPosicion;

            boolean pasoPorsalida = posicionAnterior + total >= 24;

            String mensajeSalida = "";

            if (pasoPorsalida) {
                saldosJugadoresSimulados[jugadorActualSimulado] += PREMIO_SALIDA_SIMULADO;

                lblSaldo.setText("Saldo: ₡" + saldosJugadoresSimulados[jugadorActualSimulado]);

                lblPatrimonio.setText("Patrimonio: ₡" + calcularPatrimonioSim(jugadorActualSimulado));

                mensajeSalida = " Recibió ₡" + PREMIO_SALIDA_SIMULADO + " por pasar por Salida.";
            }

            String mensajeCasilla;

            int propietario = propietariosSimulados[nuevaPosicion];

            if (esCasillaEventoSimulada(nuevaPosicion)) {

                btnComprar.setEnabled(false);

                String carta = obtenerCartaEventosSimulada();

                if (carta.equals("Recibe ₡100 por beca del TEC.")) {
                    saldosJugadoresSimulados[jugadorActualSimulado] += 100;

                    lblSaldo.setText("Saldo: ₡" + saldosJugadoresSimulados[jugadorActualSimulado]);

                    lblPatrimonio.setText("Patrimonio: ₡" + calcularPatrimonioSim(jugadorActualSimulado));
            
                }

                else if (carta.equals("Paga ₡100 por romper algo de laboratorio.")) {

                    if (saldosJugadoresSimulados[jugadorActualSimulado] >= 100) {
                        saldosJugadoresSimulados[jugadorActualSimulado] -= 100;

                        lblSaldo.setText("Saldo: ₡" + saldosJugadoresSimulados[jugadorActualSimulado]);

                        lblPatrimonio.setText("Patrimonio: ₡" + calcularPatrimonioSim(jugadorActualSimulado));
                    }
                    else {
                        jugadoresActivosSimulados[jugadorActualSimulado] = false;

                        liberarPropiedadesJugadorSimulado(jugadorActualSimulado);

                        carta = "No pudo pagar ₡100 por lo que rompió y fue eliminado.";
                    }
                }

                else if (carta.equals("Avanza 3 posiciones.")) {
                    nuevaPosicion = (nuevaPosicion + 3) % 24;

                    posicionesJugadoresSimulados[jugadorActualSimulado] = nuevaPosicion;

                    carta = "Avanza 3 posiciones. Ahora está en " + nombresCasillas[nuevaPosicion] + ".";
                }

                else if (carta.equals("Vas directamente al Edificio D3.")) {
                    nuevaPosicion = 6;

                    posicionesJugadoresSimulados[jugadorActualSimulado] = nuevaPosicion;
                    pierdeTurnoSimulado[jugadorActualSimulado] = true;

                    carta = "Está condenado a un turno en el D3.";
                }

                else if (carta.equals("Vas directamente a la Salida.")) {
                    nuevaPosicion = 0;

                    posicionesJugadoresSimulados[jugadorActualSimulado] = nuevaPosicion;

                    carta = "Fue directamente a la Salida.";
                }

                mensajeCasilla = "Carta de evento: " + carta;

                agregarHistorialSimulado("J" + (jugadorActualSimulado + 1) + " recibió una carta de Evento: " + carta);

                JOptionPane.showMessageDialog(this, carta, "Carta de Evento", JOptionPane.INFORMATION_MESSAGE);
            }

            else if (nuevaPosicion == 18) {
                btnComprar.setEnabled(false);

                nuevaPosicion = 6;

                posicionesJugadoresSimulados[jugadorActualSimulado] = nuevaPosicion;

                pierdeTurnoSimulado[jugadorActualSimulado] = true;

                mensajeCasilla = "Debe ir directamente al Edificio D3 y está condenado a perder un turno.";
            }

            else if (nuevaPosicion == 6) {
                btnComprar.setEnabled(false);

                mensajeCasilla = "Está de visita en el D3.";
            }

            else if (!esPropiedadSimulada(nuevaPosicion)) {
                btnComprar.setEnabled(false);

                mensajeCasilla = "Cayó en una casilla especial.";
            }

            else if (propietario == -1) {
                btnComprar.setEnabled(true);

                mensajeCasilla = "La propiedad está disponible.";
            }

            else if (propietario == jugadorActualSimulado) {
                btnComprar.setEnabled(false);

                mensajeCasilla = "Cayó en su propia propiedad.";
            }

            else { 
                btnComprar.setEnabled(false);

                if (saldosJugadoresSimulados[jugadorActualSimulado] >= ALQUILER_SIMULADO) {
                    saldosJugadoresSimulados[jugadorActualSimulado] -= ALQUILER_SIMULADO;
                    saldosJugadoresSimulados[propietario] += ALQUILER_SIMULADO;

                    agregarHistorialSimulado("J" + (jugadorActualSimulado + 1) + " pagó ₡" + (int) ALQUILER_SIMULADO 
                                            + " de alquiler a J" + (propietario + 1) + " por " + nombresCasillas[nuevaPosicion] + ".");

                    lblSaldo.setText("Saldo: ₡" + saldosJugadoresSimulados[jugadorActualSimulado]);

                    lblPatrimonio.setText("Patrimonio: ₡" + calcularPatrimonioSim(jugadorActualSimulado));

                    mensajeCasilla = "Pagó ₡" + ALQUILER_SIMULADO + " de alquiler a J" + (propietario + 1) + ".";
                }
                else {
                    jugadoresActivosSimulados[jugadorActualSimulado] = false;

                    liberarPropiedadesJugadorSimulado(jugadorActualSimulado);

                    agregarHistorialSimulado("J" + (jugadorActualSimulado + 1) + " no pudo pagar el alquiler y fue eliminado");

                    mensajeCasilla = "J" + (jugadorActualSimulado + 1) + " no pudo pagar alquiler y fue eliminado.";
                }
            }

            lblPosicion.setText("Posición: " + nuevaPosicion + " - " + nombresCasillas[nuevaPosicion]);

            if (jugadoresActivosSimulados[jugadorActualSimulado]) {

                animarFicha(jugadorActualSimulado, total, nuevaPosicion);

            } else {

                retirarFichaJugadorSimulado(jugadorActualSimulado);
            }

            agregarHistorialSimulado("J" + (jugadorActualSimulado + 1) + " llegó a " + nombresCasillas[nuevaPosicion] + ".");

            resaltarCasillaJugadorActualSimulado();

            dadosLanzadosSimulados = true;
            btnTirarDados.setEnabled(false);
            btnTerminarTurno.setEnabled(true);

            actualizarTarjetasJugadoresSimulados();
            lblEstado.setText("J" + (jugadorActualSimulado + 1) + " avanzó " + total + " posiciones. " + mensajeSalida + " " + mensajeCasilla);
        });

        btnComprar.addActionListener(e -> {

            if (modoEnLinea) {
                btnComprar.setEnabled(false);
                cliente.enviarSolicitud("COMPRAR_PROPIEDAD");
                return;
            }

            int posicionActual = posicionesJugadoresSimulados[jugadorActualSimulado];

            double precioActual = preciosPropiedadesSimulados[posicionActual];

            if (!esPropiedadSimulada(posicionActual)) {
                lblEstado.setText("Esta casilla no se puede comprar.");
                return;
            }
            if (propietariosSimulados[posicionActual] != -1) {
                lblEstado.setText("Esta propiedad ya tiene propietario.");
                return;
            }
            if (saldosJugadoresSimulados[jugadorActualSimulado] < precioActual) {
                lblEstado.setText("J" + (jugadorActualSimulado + 1) + " no tiene saldo suficiente.");
                return;
            }

            saldosJugadoresSimulados[jugadorActualSimulado] -= precioActual;

            lblSaldo.setText("Saldo: ₡" + saldosJugadoresSimulados[jugadorActualSimulado]);

            propietariosSimulados[posicionActual] = jugadorActualSimulado;

            etiquetasPrecios[posicionActual].setVisible(false);

            agregarHistorialSimulado(
                "J" + (jugadorActualSimulado + 1) + " compró " + nombresCasillas[posicionActual] + " por ₡" + (int) precioActual + ".");

            lblPropiedades.setText(obtenerPropiedadesJugadorSim(jugadorActualSimulado));

            etiquetasPropietarios[posicionActual].setText("Dueño: J" + (jugadorActualSimulado + 1));
            etiquetasPropietarios[posicionActual].setOpaque(true);
        
            etiquetasPropietarios[posicionActual].setBackground(fichasJugadores[jugadorActualSimulado].getBackground());
            etiquetasPropietarios[posicionActual].setForeground(Color.WHITE);

            etiquetasPropietarios[posicionActual].setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));

            lblEstado.setText("J" + (jugadorActualSimulado +1) + " compró " + nombresCasillas[posicionActual] + " por ₡" + precioActual + ".");

            lblPatrimonio.setText("Patrimonio: ₡" + calcularPatrimonioSim(jugadorActualSimulado));

            actualizarTarjetasJugadoresSimulados();
            btnComprar.setEnabled(false);
        });


        btnTerminarTurno.addActionListener(e -> {
            if (modoEnLinea) {
                cliente.enviarSolicitud("TERMINAR_TURNO");
                btnTerminarTurno.setEnabled(false);
                return;
            }

            int intentos = 0;
            String mensajeSalto = "";
            int jugadorAnterior = jugadorActualSimulado;

            while(intentos < 4) {
                jugadorActualSimulado = (jugadorActualSimulado + 1) % 4;

                intentos++;

                if (!jugadoresActivosSimulados[jugadorActualSimulado]) {
                    continue;
                }
                if (pierdeTurnoSimulado[jugadorActualSimulado]) {

                    pierdeTurnoSimulado[jugadorActualSimulado] = false;

                    mensajeSalto = "J" + (jugadorActualSimulado + 1) + " perdió su turno por estar en el D3.";

                    agregarHistorialSimulado("J" + (jugadorActualSimulado + 1) + " perdió su turno por estar en el D3.");
                    continue;
                }
                break;
            }
            boolean nuevaRonda = false;

            if (jugadorActualSimulado < jugadorAnterior) {
                numeroRondaSimulada++;

                nuevaRonda = true;
            }

            if (contarJugadoresActivos() == 1) {
                int ganador = -1;

                for (int i = 0; i < jugadoresActivosSimulados.length; i++) {
                    if (jugadoresActivosSimulados[i]) {
                        ganador = i;
                        break;
                    }
                }
                mostrarFinDePartidaSimulado("Solo queda un jugador activo.", ganador);

                return;
            }

            if (partidaPorRondas && numeroRondaSimulada > maxRondasSimulado) {
                int ganador = obtenerGanadorPorPatrimonioSimulado();

                lblNumeroRonda.setText("Ronda: " + maxRondasSimulado + "/" + maxRondasSimulado);

                mostrarFinDePartidaSimulado("Se completaron las " + maxRondasSimulado + " rondas de la partida.", ganador);

                return;
            }

            if (nuevaRonda) {
                agregarHistorialSimulado("Comienza la ronda " + numeroRondaSimulada + ".");
            }
            dadosLanzadosSimulados = false;

            btnTerminarTurno.setEnabled(false);
            btnTirarDados.setEnabled(true);
            btnComprar.setEnabled(false);

            actualizarPanelJugadorSimulado();
            resaltarCasillaJugadorActualSimulado();
            agregarHistorialSimulado("Turno de J" + (jugadorActualSimulado + 1) + ".");
            
            lblDado1.setText("-");
            lblDado2.setText("-");

            lblEstado.setText(mensajeSalto + "\nTurno de J" + (jugadorActualSimulado + 1));
        });


        // -------------------------------------------------
        // AGREGAR PANELES A LA VENTANA
        // -------------------------------------------------

        add(panelTablero, BorderLayout.CENTER);
        add(panelJugador, BorderLayout.EAST);
        agregarHistorialSimulado("Partida iniciada. Turno de J1");
    }

    public void activarModoEnLinea(Cliente clienteConectado) {
        if (clienteConectado == null) {
            throw new IllegalArgumentException("El cliente no puede ser null.");
        }

        if (modoEnLinea) {
            return;
        }
        this.cliente = clienteConectado;
        this.modoEnLinea = true;

        btnTirarDados.setEnabled(false);
        btnComprar.setEnabled(false);
        btnTerminarTurno.setEnabled(false);

        for (int i = 0; i < fichasJugadores.length; i++) {
            retirarFichaJugadorSimulado(i);
        }

        lblEstado.setText("Modo en línea: sincronizando con el servidor...");

        iniciarEscuchaServidor();
    }

    private Color obtenerColorGrupoPropiedadSimulada(int posicion) {
        if (posicion == 1 || posicion == 2) {
            return new Color(150, 95, 60);
        }
        if (posicion == 4 || posicion == 5) {
            return new Color(120, 190, 220);
        }
        if (posicion == 7 || posicion == 8) {
            return new Color(220, 130, 180);
        }
        if (posicion == 10 || posicion == 11) {
            return new Color(230, 160, 70);
        }
        if (posicion == 13 || posicion == 14) {
            return new Color(210, 80, 80);
        }
        if (posicion == 16 || posicion == 17) {
            return new Color(230, 210, 80);
        }
        if (posicion == 19 || posicion == 20) {
            return new Color(90, 170, 100);
        }
        if (posicion == 22 || posicion ==23) {
            return new Color(70, 100, 170);
        }
        return new Color(200, 200, 200);
    }

    private Color obtenerColorCasillaSimulada(int posicion) {

        if (posicion == 0) {
            return new Color(190, 225, 190);   // Salida
        }

        if (esCasillaEventoSimulada(posicion)) {
            return new Color(220, 205, 235);   // Evento
        }

        if (posicion == 6) {
            return new Color(205, 220, 235);   // Edificio D3
        }

        if (posicion == 18) {
            return new Color(235, 195, 195);   // Ir al D3
        }

        if (posicion == 12) {
            return new Color(235, 225, 185);   // Especial
        }

        return new Color(240, 235, 220);       // Propiedades
    }

    private void actualizarPanelJugadorSimulado() {

        lblNombre.setText("Jugador actual: J" + (jugadorActualSimulado + 1));

        lblSaldo.setText("Saldo: ₡" + saldosJugadoresSimulados[jugadorActualSimulado]);

        lblPatrimonio.setText("Patrimonio: ₡" + calcularPatrimonioSim(jugadorActualSimulado));

        int posicionActual = posicionesJugadoresSimulados[jugadorActualSimulado];
        lblPosicion.setText("Posición: " + posicionActual + " - " + nombresCasillas[posicionActual]);

        lblTurno.setText("Turno actual: J" + (jugadorActualSimulado + 1));

        lblTurno.setBackground(fichasJugadores[jugadorActualSimulado].getBackground());

        actualizarEtiquetaRonda();

        lblPropiedades.setText(obtenerPropiedadesJugadorSim(jugadorActualSimulado));

        actualizarTarjetasJugadoresSimulados();
    }

    private void mostrarFinDePartidaSimulado(String motivo, int ganador) {
        double patrimonioGanador = calcularPatrimonioSim(ganador);

        String mensaje = motivo + "\n\nGanador: J"+ (ganador + 1) + "\nPatrimonio: ₡" + (int) patrimonioGanador;

        lblEstado.setText("Partida finalizada. Ganador: J"+ (ganador + 1));

        agregarHistorialSimulado("Partida finalizada. Ganador: J"+ (ganador + 1) + " con un patrimonio de ₡" + patrimonioGanador + ".");

        btnTirarDados.setEnabled(false);
        btnComprar.setEnabled(false);
        btnTerminarTurno.setEnabled(false);

        JOptionPane.showMessageDialog(this, mensaje, "Fin de la partida", JOptionPane.INFORMATION_MESSAGE);
    }

    private int obtenerGanadorPorPatrimonioSimulado () {
        int ganador = -1;
        double mayorPatrimonio = -1;

        for (int i =0; i < jugadoresActivosSimulados.length; i++) {

            if (jugadoresActivosSimulados[i]) {

                double patrimonio = calcularPatrimonioSim(i);

                if (patrimonio > mayorPatrimonio) {
                    mayorPatrimonio = patrimonio;
                    ganador = i;
                }
            }
        }
        return ganador;
    }

    private String obtenerCartaEventosSimulada() {
        String carta = cartasEventoSimuladas[indiceCartaEventosSimulada];

        indiceCartaEventosSimulada = (indiceCartaEventosSimulada + 1) % cartasEventoSimuladas.length;

        return carta;
    }

    private boolean esCasillaEventoSimulada(int posicion) {
        return posicion == 3 ||
        posicion == 9 ||
        posicion == 15 ||
        posicion == 21;
    }

    private double calcularPatrimonioSim(int jugador) {
        double patrimonio = saldosJugadoresSimulados[jugador];

        for (int i = 0; i < propietariosSimulados.length; i++) {
            if (propietariosSimulados[i] == jugador) {
                patrimonio += preciosPropiedadesSimulados[i];
            }
        }
        return patrimonio;
    }

    private void liberarPropiedadesJugadorSimulado(int jugador) {
        for (int i = 0; i < propietariosSimulados.length; i++) {
            if (propietariosSimulados[i] == jugador) {
                propietariosSimulados[i] = -1;

                etiquetasPropietarios[i].setText(" ");
                etiquetasPropietarios[i].setOpaque(false);
                etiquetasPropietarios[i].setBorder(BorderFactory.createEmptyBorder());

                etiquetasPrecios[i].setVisible(true);
            }
        }
    }

    private String obtenerPropiedadesJugadorSim(int jugador) {
        StringBuilder texto = new StringBuilder("Propiedades: ");
        boolean tienePropiedades = false;

        for (int i = 0; i < propietariosSimulados.length; i++) {
            if (propietariosSimulados[i] == jugador) {
                if (tienePropiedades) {
                    texto.append(". ");
                }
                texto.append(nombresCasillas[i]);
                tienePropiedades = true;
            }
        }
        if (!tienePropiedades) {
            texto.append("ninguna");
        }
        return texto.toString();
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

    private int contarJugadoresActivos() {
        int cantidad = 0;

        for (boolean activo : jugadoresActivosSimulados) {
            if (activo){
                cantidad++;
            }
        }
        return cantidad;
    }

    private JPanel crearTarjetaJugador(int jugador) {
        JPanel tarjeta = new JPanel();

        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));

        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                        fichasJugadores[jugador].getBackground(), 2), 
                        BorderFactory.createEmptyBorder(5, 7, 5,7)));
        JLabel lblJugador = new JLabel("J" + (jugador + 1));

        lblJugador.setFont(lblJugador.getFont().deriveFont(Font.BOLD));

        lblSaldoJugadores[jugador] = new JLabel("Saldo: ₡1500");

        lblEstadoJugadores[jugador] = new JLabel("Activo");

        tarjeta.add(lblJugador);
        tarjeta.add(lblSaldoJugadores[jugador]);
        tarjeta.add(lblEstadoJugadores[jugador]);

        tarjetasJugadores[jugador] = tarjeta;

        return tarjeta;
    }

    // Convierte una transacción del servidor en una línea fácil de leer.
    // Llega como: T1|Turno: 5|Tipo: COMPRA_PROPIEDAD|Origen: J002|Destino: BANCO|Monto: 450.0|Descripcion: ...|FechaHora: ...
    // Queda como: J002 pagó ₡450 al Banco (Compra de la propiedad BICITEC).
    private String FormatearTransaccion(String Registro) {

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

        // Si el registro no trae el formato esperado se muestra tal como llegó.
        if (Origen.isEmpty() || Destino.isEmpty() || Monto.isEmpty()) {
            return Registro.replace("|", " - ");
        }

        // 450.0 -> 450
        try {
            Monto = String.valueOf((long) Double.parseDouble(Monto));
        } catch (NumberFormatException e) {
            // Se deja el monto como llegó.
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
            Linea = Linea + " (" + Descripcion + ")";
        }

        return Linea + ".";
    }

    // Traduce los mensajes de la Pico (ESPERANDO_RFID;J001, RFID_OK;J001...) a instrucciones claras.
    // Devuelve null para los avisos que no hace falta mostrar.
    private String TraducirMensajeHardware(String MensajeHardware) {

        String[] Partes = MensajeHardware.split(";");

        String Tipo = Partes[0];
        String Jugador = Partes.length > 1 ? Partes[1] : "";

        if (Tipo.equals("ESPERANDO_RFID")) {
            return "Acerque la tarjeta RFID de " + Jugador + " al lector.";
        }
        else if (Tipo.equals("RFID_OK")) {
            return "Tarjeta de " + Jugador + " validada.";
        }
        else if (Tipo.equals("RFID_INCORRECTO")) {
            return "Esa tarjeta no es de " + Jugador + ". Intente de nuevo.";
        }
        else if (Tipo.equals("RETIRAR_RFID")) {
            return "Retire la tarjeta del lector.";
        }
        else if (Tipo.equals("ESPERANDO_BOTON")) {
            return "Presione el botón para lanzar los dados.";
        }
        else if (Tipo.equals("PAGO_OK")) {
            return "Pago autorizado.";
        }
        else if (Tipo.equals("ERROR")) {
            return "Error del hardware: " + MensajeHardware.substring("ERROR;".length()).replace(";", " ");
        }

        // RFID_RETIRADO, DADOS y otros avisos internos no se muestran.
        return null;
    }

    private void agregarHistorialSimulado(String mensaje) {
        if (!areaHistorial.getText().isEmpty()) {
            areaHistorial.append("\n");
        }
        areaHistorial.append(mensaje);

        areaHistorial.setCaretPosition(areaHistorial.getDocument().getLength());
    }

    private void actualizarEtiquetaRonda() {
        if (partidaPorRondas) {
            lblNumeroRonda.setText("Ronda: " + numeroRondaSimulada + " / " + maxRondasSimulado);
        }
        else {
            lblNumeroRonda.setText("Ronda: " + numeroRondaSimulada + " | Sin límite");
        }
    }

    private String obtenerCaraDado(int valor) {
        switch (valor) {
            case 1:
                return "⚀";
            case 2:
                return "⚁";
            case 3:
                return "⚂";
            case 4:
                return "⚃";
            case 5:
                return "⚄";
            case 6:
                return "⚅";
            default:
                return "-";
        }
    }

    private void actualizarTarjetasJugadoresSimulados() {
        for (int i = 0; i < 4; i++) {
            lblSaldoJugadores[i].setText("Saldo: ₡" + saldosJugadoresSimulados[i]);

            if (!jugadoresActivosSimulados[i]) {
                lblEstadoJugadores[i].setText("Eliminado");
                tarjetasJugadores[i].setEnabled(false);
                tarjetasJugadores[i].setBackground(new Color(210, 210, 210));
                lblSaldoJugadores[i].setForeground(Color.GRAY);
                lblEstadoJugadores[i].setForeground(Color.GRAY);
            }

            else if (pierdeTurnoSimulado[i]) {
                lblEstadoJugadores[i].setText("En D3");
                tarjetasJugadores[i].setBackground(new Color(235, 235, 235));
                lblSaldoJugadores[i].setForeground(Color.BLACK);
                lblEstadoJugadores[i].setForeground(Color.BLACK);
            }

            else {
                lblEstadoJugadores[i].setText("Activo");
                tarjetasJugadores[i].setBackground(new Color(245, 245, 245));
                lblSaldoJugadores[i].setForeground(Color.BLACK);
                lblEstadoJugadores[i].setForeground(Color.BLACK);
            }
        }
    }

    private void resaltarCasillaJugadorActualSimulado() {
        
        for (int i = 0; i < casillasVisuales.length; i++) {
            if (casillasVisuales[i] != null) {
                casillasVisuales[i].setBorder(BorderFactory.createEtchedBorder());
            }
        }
        int posicionActual = posicionesJugadoresSimulados[jugadorActualSimulado];

        Color colorJugador = fichasJugadores[jugadorActualSimulado].getBackground();

        casillasVisuales[posicionActual].setBorder(BorderFactory.createLineBorder(colorJugador, 4));
        
    }

    private void iniciarEscuchaServidor() {

        Thread hiloReceptor = new Thread(() -> {

            while (true) {

                String mensaje = cliente.recibirRespuesta();

                // La conexión terminó.
                if (mensaje == null) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(
                            this,
                            "Se perdió la conexión con el servidor."
                        );
                    });
                    break;
                }

                // Recibir el estado real de la partida.
                if (mensaje.startsWith("ESTADO;")) {

                    actualizarFichasDesdeEstado(mensaje);

                }
                // El servidor notificó un cambio.
                else if (mensaje.equals("ACTUALIZAR_ESTADO")) {

                    cliente.enviarSolicitud("CONSULTAR_ESTADO");
                    cliente.enviarSolicitud("CONSULTAR_TRANSACCIONES");

                }

                else if (mensaje.startsWith("DADOS;")) {

                    // Separar los valores recibidos.
                    String[] datos = mensaje.split(";");

                    SwingUtilities.invokeLater(() -> {

                        // Verificar que recibimos los datos correctos.
                        if (datos.length == 4) {

                            // Agregar el lanzamiento al historial.
                            String QuienLanzo = indiceTurnoEnLinea >= 0
                                    ? String.format("J%03d", indiceTurnoEnLinea + 1)
                                    : "El jugador";

                            agregarHistorialSimulado(QuienLanzo + " lanzó " + datos[1] + " y " + datos[2]
                                    + " (total " + datos[3] + ").");

                            try {

                                int dado1 = Integer.parseInt(datos[1]);
                                int dado2 = Integer.parseInt(datos[2]);

                                // Mostrar las caras correspondientes.
                                if (dado1 >= 1 && dado1 <= 6
                                        && dado2 >= 1 && dado2 <= 6) {

                                    lblDado1.setText(obtenerCaraDado(dado1));
                                    lblDado2.setText(obtenerCaraDado(dado2));

                                    // El próximo ESTADO trae la posición final del que lanzó:
                                    // se anima avanzando estos pasos casilla por casilla.
                                    pasosDadosPendientes = dado1 + dado2;
                                    jugadorDadosPendiente = indiceTurnoEnLinea;
                                }

                            } catch (NumberFormatException ex) {

                                System.out.println("Valores de dados inválidos.");

                            }
                        }
                    });

                    // Actualizar las posiciones del tablero.
                    cliente.enviarSolicitud("CONSULTAR_ESTADO");
                }

                else if(mensaje.startsWith("HISTORIAL;")) {
                    String contenido = mensaje.substring("HISTORIAL;".length());

                    if (!contenido.equals("SIN_TRANSACCIONES") && !contenido.isBlank()) {
                        String[] registros = contenido.split(";");

                        SwingUtilities.invokeLater(() -> {
                            for (String registro : registros) {
                            if (registro.isBlank()) {
                                continue;
                            }
                            int separador = registro.indexOf("|");

                            String identificador = separador >= 0 ? registro.substring(0, separador).trim() : registro.trim();

                            if (transaccionesMostradasEnLinea.add(identificador)) {
                                agregarHistorialSimulado(FormatearTransaccion(registro));
                            }
                        }
                    });
                }
            }

                else if (mensaje.startsWith("EVENTO;")) {

                    String[] datos = mensaje.split(";", 4);

                    if (datos.length == 4) {

                        String jugadorEvento = datos[1];
                        String identificadorCarta = datos[2];
                        String descripcionCarta = datos[3];

                        SwingUtilities.invokeLater(() -> {

                            agregarHistorialSimulado(
                                    jugadorEvento + " sacó la carta "
                                    + identificadorCarta
                                    + ": " + descripcionCarta
                            );

                        });
                    }
                }

                // Un pago (compra o alquiler) espera la tarjeta RFID del jugador que paga.
                else if (mensaje.startsWith("PAGO_PENDIENTE;")) {

                    String[] DatosPago = mensaje.split(";", 3);

                    if (DatosPago.length == 3) {

                        SwingUtilities.invokeLater(() -> {

                            agregarHistorialSimulado(
                                    DatosPago[1] + " debe pagar: " + DatosPago[2] + "."
                            );

                        });
                    }
                }

                // Este jugador debe pagar (alquiler o carta): inicia el pago automáticamente.
                // El servidor pide la tarjeta RFID, cobra y después se habilita Terminar turno.
                else if (mensaje.equals("COBRAR_PAGO")) {

                    cliente.enviarSolicitud("REALIZAR_PAGO");
                }

                // Instrucciones del hardware (RFID y botón) para este jugador.
                else if (mensaje.startsWith("HARDWARE;")) {

                    String TextoHardware = TraducirMensajeHardware(
                            mensaje.substring("HARDWARE;".length())
                    );

                    // Algunos avisos internos de la Pico no se muestran.
                    if (TextoHardware != null) {

                        SwingUtilities.invokeLater(() -> {
                            agregarHistorialSimulado(TextoHardware);
                        });
                    }
                }

                // Un jugador perdió la conexión con el servidor.
                else if (mensaje.startsWith("JUGADOR_DESCONECTADO;")) {

                    String JugadorDesconectado = mensaje.substring("JUGADOR_DESCONECTADO;".length());

                    SwingUtilities.invokeLater(() -> {
                        agregarHistorialSimulado(JugadorDesconectado + " se desconectó y sale de la partida.");
                    });
                }

                // Fin de la partida: FIN;GANADOR;ID, FIN;GANADOR;SIN_GANADOR o FIN;EMPATE;ID1,ID2
                else if (mensaje.startsWith("FIN;")) {

                    String[] DatosFin = mensaje.split(";");

                    String TextoFin;

                    if (DatosFin.length == 3 && DatosFin[1].equals("EMPATE")) {
                        TextoFin = "Partida terminada en empate entre " + DatosFin[2].replace(",", ", ") + ".";
                    }
                    else if (DatosFin.length == 3 && !DatosFin[2].equals("SIN_GANADOR")) {
                        TextoFin = "Partida terminada. Ganador: " + DatosFin[2] + ".";
                    }
                    else {
                        TextoFin = "Partida terminada sin ganador.";
                    }

                    SwingUtilities.invokeLater(() -> {
                        agregarHistorialSimulado(TextoFin);
                    });
                }

                // Mostrar otras respuestas del servidor.
                else {

                    SwingUtilities.invokeLater(() -> {
                        agregarHistorialSimulado(mensaje);
                    });
                }
            }

        });

        hiloReceptor.setDaemon(true);
        hiloReceptor.start();

        // Solicitar el estado inicial.
        cliente.enviarSolicitud("CONSULTAR_ESTADO");
        cliente.enviarSolicitud("CONSULTAR_TRANSACCIONES");
    }


    private void actualizarFichasDesdeEstado(String estado) {

        if (estado == null || !estado.startsWith("ESTADO;")) {
            return;
        }

        String[] campos = estado.split(";", -1);

        // Ahora recibimos 13 campos desde el servidor.
        if (campos.length != 13) {
            System.out.println(
                    "Formato ESTADO inválido: " + campos.length
            );
            return;
        }

        String[] jugadores = campos[8].split("\\|");

        SwingUtilities.invokeLater(() -> {

            // Identificación del jugador y del turno.
            String identificadorLocal = campos[1];
            String identificadorTurno = campos[6];

            lblNombre.setText(
                    "Jugador: " + identificadorLocal
            );

            lblTurno.setText(
                    "Turno actual: " + identificadorTurno
            );

            if (identificadorTurno.matches("J00[1-4]")) {
                indiceTurnoEnLinea = Integer.parseInt(identificadorTurno.substring(1)) - 1;
            }

            // Información real del jugador.
            lblSaldo.setText("Saldo: ₡" + campos[3]);
            lblPosicion.setText("Posición: " + campos[4]);
            lblPropiedades.setText(
                    "Propiedades: " + campos[5]
            );

            // Permisos calculados por el servidor.
            boolean puedeTirar =
                    Boolean.parseBoolean(campos[10]);

            boolean puedeComprar =
                    Boolean.parseBoolean(campos[11]);

            boolean puedeTerminar =
                    Boolean.parseBoolean(campos[12]);

            btnTirarDados.setEnabled(puedeTirar);
            btnComprar.setEnabled(puedeComprar);
            btnTerminarTurno.setEnabled(puedeTerminar);

            // Sincronizar las fichas de todos los jugadores.
            for (String registro : jugadores) {

                String[] datos = registro.split(",");

                if (datos.length != 4
                        || !datos[0].matches("J00[1-4]")) {
                    continue;
                }

                int indiceJugador = Integer.parseInt(datos[0].substring(1)) - 1;

                lblSaldoJugadores[indiceJugador].setText("Saldo: ₡" + datos[3]);

                boolean activo = Boolean.parseBoolean(datos[2]);

                lblEstadoJugadores[indiceJugador].setText(activo ? "Activo" : "Eliminado");

                if (datos[2].equalsIgnoreCase("false")) {

                    retirarFichaJugadorSimulado(indiceJugador);

                } else if (datos[2].equalsIgnoreCase("true")) {

                    try {

                        int posicion = Integer.parseInt(datos[1]);

                        if (posicion >= 0
                                && posicion < panelesFichas.length) {

                            moverFichaDesdeEstado(
                                    indiceJugador,
                                    posicion
                            );
                        }

                    } catch (NumberFormatException ex) {

                        System.out.println(
                                "Posición inválida recibida."
                        );
                    }
                }
            }
            // -------------------------------------------------
            // SINCRONIZAR PROPIETARIOS DE LAS CASILLAS
            // -------------------------------------------------

            String[] propiedades = campos[9].split("\\|");

            for (String registroPropiedad : propiedades) {

                String[] datosPropiedad = registroPropiedad.split(",");

                if (datosPropiedad.length != 2
                        || !datosPropiedad[0].matches("P\\d{2}")) {
                    continue;
                }

                int posicion = Integer.parseInt(
                        datosPropiedad[0].substring(1)
                );

                if (posicion < 0
                        || posicion >= etiquetasPropietarios.length) {
                    continue;
                }

                JLabel lblDueno = etiquetasPropietarios[posicion];
                JLabel lblPrecio = etiquetasPrecios[posicion];

                if (lblDueno == null || lblPrecio == null) {
                    continue;
                }

                String identificadorDueno = datosPropiedad[1];

                // Propiedad disponible.
                if (identificadorDueno.equals("SIN_PROPIETARIO")) {

                    lblDueno.setText(" ");
                    lblDueno.setOpaque(false);
                    lblDueno.setBorder(null);

                    lblPrecio.setVisible(true);
                }

                // Propiedad comprada.
                else if (identificadorDueno.matches("J00[1-4]")) {

                    int indiceDueno = Integer.parseInt(
                            identificadorDueno.substring(1)
                    ) - 1;

                    lblDueno.setText(
                            "Dueño: J" + (indiceDueno + 1)
                    );

                    lblDueno.setOpaque(true);

                    lblDueno.setBackground(
                            fichasJugadores[indiceDueno].getBackground()
                    );

                    lblDueno.setForeground(Color.WHITE);

                    lblDueno.setBorder(
                            BorderFactory.createEmptyBorder(3, 5, 3, 5)
                    );

                    // Ocultar el precio cuando ya tiene dueño.
                    lblPrecio.setVisible(false);
                }
            }
        });
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

    // Decide cómo mostrar la posición que llegó en un ESTADO:
    // - Si la ficha se está animando, solo se actualiza el destino final.
    // - Si es el jugador que acaba de lanzar, avanza casilla por casilla.
    // - Si no, se coloca directo (el primer ESTADO, cuando la ficha aún no está en el tablero).
    private void moverFichaDesdeEstado(int jugador, int posicion) {
        if (animacionesFichas[jugador] != null) {
            destinosFichas[jugador] = posicion;
            return;
        }
        if (jugador == jugadorDadosPendiente && pasosDadosPendientes > 0) {
            int pasos = pasosDadosPendientes;
            pasosDadosPendientes = 0;
            jugadorDadosPendiente = -1;
            animarFicha(jugador, pasos, posicion);
            return;
        }
        if (posicion != posicionesVisuales[jugador]) {
            marcarPosicionJugador(jugador, posicion);
        }
    }

    // Avanza la ficha "pasos" casillas, una cada MS_POR_CASILLA.
    // Si al terminar no quedó en "destinoFinal" (una carta la movió o cayó en
    // "Ir al D3"), espera MS_ANTES_DE_SALTO y salta a la casilla final.
    private void animarFicha(int jugador, int pasos, int destinoFinal) {
        detenerAnimacionFicha(jugador);

        if (posicionesVisuales[jugador] == -1 || pasos <= 0) {
            marcarPosicionJugador(jugador, destinoFinal);
            return;
        }

        destinosFichas[jugador] = destinoFinal;
        int[] pasosRestantes = {pasos};

        Timer animacion = new Timer(MS_POR_CASILLA, null);
        animacion.addActionListener(e -> {

            // La ficha fue retirada (jugador eliminado) mientras se movía.
            if (posicionesVisuales[jugador] == -1) {
                detenerAnimacionFicha(jugador);
                return;
            }

            if (pasosRestantes[0] > 0) {
                pasosRestantes[0]--;
                marcarPosicionJugador(jugador, (posicionesVisuales[jugador] + 1) % panelesFichas.length);

                if (pasosRestantes[0] == 0) {
                    if (posicionesVisuales[jugador] == destinosFichas[jugador]) {
                        detenerAnimacionFicha(jugador);
                    } else {
                        animacion.setDelay(MS_ANTES_DE_SALTO);
                    }
                }
                return;
            }

            // Salto final provocado por una carta o por "Ir al D3".
            int destino = destinosFichas[jugador];
            detenerAnimacionFicha(jugador);
            marcarPosicionJugador(jugador, destino);
        });

        animacionesFichas[jugador] = animacion;
        animacion.start();
    }

    private void detenerAnimacionFicha(int jugador) {
        if (animacionesFichas[jugador] != null) {
            animacionesFichas[jugador].stop();
            animacionesFichas[jugador] = null;
        }
    }

    private void retirarFichaJugadorSimulado(int jugador) {
        detenerAnimacionFicha(jugador);
        int posicion = posicionesVisuales[jugador];

        if (posicion == -1) {
            return;
        }
        JPanel panel = panelesFichas[posicion];
        panel.remove(fichasJugadores[jugador]);

        panel.revalidate();
        panel.repaint();

        posicionesVisuales[jugador] = -1;
    }
    // Método principal
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            VentanaJuego ventana =
                new VentanaJuego();

            ventana.setVisible(true);
        });
    }
}
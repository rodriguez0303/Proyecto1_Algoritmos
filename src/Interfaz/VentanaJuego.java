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
import javax.swing.JDialog;
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
/**
 * Ventana principal del tablero del Monopoly TEC.
 * Construye la representaciÃ³n grÃ¡fica, gestiona la simulaciÃ³n local y
 * muestra el estado de las partidas en lÃ­nea recibido desde el servidor.
 */
public class VentanaJuego extends JFrame {

    /**
     * Componentes visuales del tablero y de las fichas de los jugadores.
     */
    private JPanel [] casillasVisuales = new JPanel[24];
    private JPanel[] panelesFichas = new JPanel[24];
    private JPanel[] tarjetasJugadores = new JPanel[4];
    private JPanel panelContenidoCentro;

    private JLabel[] lblNombreJugadores = new JLabel[4];
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

    /**
     * Pantalla final y conexiÃ³n con el cliente que atiende los mensajes del servidor.
     */
    private VentanaFinPartida ventanaFinPartida;
    private Cliente cliente;

    private JTextArea lblPropiedades;
    private JTextArea areaHistorial;
    private JTextArea lblEstado;

    private Random generador = new Random();

    private String idParaPatrimonioFinal = null;
    private String historialTransaccionesFinal = "";

    // true cuando la tarjeta ya se validó y la Pico espera el botón: "Tirar dados" pasa a ser "Lanzar dados".
    private boolean EsperandoBotonDados = false;

    // Identificadores de los jugadores de la partida en línea (J001, J002...), según el último ESTADO.
    private String[] JugadoresPartida;

    // Carta de evento recibida que se mostrará en el tablero cuando la ficha llegue a la casilla.
    private String CartaPorMostrar = null;

    // true cuando el servidor usa el hardware RFID (se detecta al recibir el primer mensaje HARDWARE;).
    private boolean ModoHardwareEnLinea = false;

    // Posición del jugador de esta ventana según el último ESTADO (para el mensaje de compra).
    private int PosicionLocalEnLinea = -1;

    // Aviso "Confirmar compra" mientras se espera la tarjeta RFID (null si no hay ninguno abierto).
    private JDialog DialogoCompra = null;

    private String[] nombresJugadoresEnLinea = new String[4];
    private String identificadorLocalEnLinea = "";
    /**
     * Orden visual de las 24 casillas del tablero, numeradas del 0 al 23.
     */
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
    /**
     * Textos de eventos empleados exclusivamente en el modo simulado.
     */
    private String[] cartasEventoSimuladas = {
        "Recibe ₡100 por beca del TEC.",
        "Paga ₡100 por romper algo de laboratorio.",
        "Avanza 3 posiciones.",
        "Vas directamente al Edificio D3.",
        "Vas directamente a la Salida."
    };

    /**
     * Evita repetir en el historial grÃ¡fico una transacciÃ³n ya recibida.
     */
    private final java.util.Set<String> transaccionesMostradasEnLinea = new java.util.HashSet<>();

    /**
     * Datos internos del modo local: rondas, cartas, posiciones y jugadores.
     */
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

    /**
     * Saldo y propiedades del modo local; el modo en lÃ­nea usa al servidor como autoridad.
     */
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
    /**
     * Crea un tablero con la configuraciÃ³n predeterminada de simulaciÃ³n.
     * TambiÃ©n se utiliza para inicializar la vista en modo en lÃ­nea.
     */
    public VentanaJuego() {
        this(true, 5);
    }

    /**
     * Construye el tablero y prepara sus controles, casillas y fichas.
     * @param partidaPorRondas indica si la simulaciÃ³n tiene lÃ­mite de rondas.
     * @param maxRondasSimulado lÃ­mite utilizado por el modo simulado.
     */
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

        JPanel panelResumenJugadores = new JPanel(new GridLayout(0, 2, 5, 5));

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

                // La tarjeta ya se validó y la Pico espera el botón:
                // este botón funciona igual que el botón físico.
                if (EsperandoBotonDados) {

                    EsperandoBotonDados = false;

                    btnTirarDados.setEnabled(false);

                    cliente.enviarSolicitud("LANZAR_DADOS");

                    return;
                }

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

                String TextoCompra = TextoConfirmarCompra();

                // Con RFID: se pide la tarjeta de una vez y el aviso solo tiene "Cancelar";
                // acercar la tarjeta funciona como "Aceptar".
                if (ModoHardwareEnLinea) {

                    btnComprar.setEnabled(false);

                    cliente.enviarSolicitud("COMPRAR_PROPIEDAD");

                    MostrarDialogoCompraRfid(TextoCompra);

                    return;
                }

                // Digital: confirmar con Aceptar o Cancelar antes de comprar.
                Object[] Opciones = {"Cancelar", "Aceptar"};

                int Respuesta = JOptionPane.showOptionDialog(
                        this,
                        TextoCompra,
                        "Confirmar compra",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        Opciones,
                        Opciones[1]
                );

                if (Respuesta == 1) {

                    btnComprar.setEnabled(false);

                    cliente.enviarSolicitud("COMPRAR_PROPIEDAD");
                }

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

    /**
     * Vincula el tablero a una conexiÃ³n cliente-servidor y desactiva los controles
     * que solo corresponden a la simulaciÃ³n local.
     * @param clienteConectado cliente ya conectado y autenticado en la sala.
     */
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

            tarjetasJugadores[i].setVisible(false);
        }

        lblEstado.setText("Modo en línea: sincronizando con el servidor...");

        iniciarEscuchaServidor();
    }

    /**
     * Determina el color del grupo de propiedades para una posiciÃ³n del tablero.
     * @param posicion Ã­ndice de casilla.
     * @return color asignado al grupo de propiedades.
     */
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

    /**
     * Selecciona el color de fondo de una casilla de la representaciÃ³n grÃ¡fica.
     * @param posicion Ã­ndice de casilla.
     * @return color que debe utilizar la casilla.
     */
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

    /**
     * Refresca los datos visibles del jugador activo en la simulaciÃ³n local.
     */
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

    /**
     * Presenta el resultado calculado por la simulaciÃ³n local.
     * @param motivo razÃ³n por la que terminÃ³ la partida.
     * @param ganador Ã­ndice del jugador ganador, cuando corresponde.
     */
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

    /**
     * Compara el patrimonio de los participantes en la simulaciÃ³n.
     * @return Ã­ndice del jugador que tiene el mayor patrimonio segÃºn la lÃ³gica local.
     */
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

    /**
     * Obtiene la siguiente carta del conjunto de eventos de la simulaciÃ³n.
     * @return descripciÃ³n del evento correspondiente.
     */
    private String obtenerCartaEventosSimulada() {
        String carta = cartasEventoSimuladas[indiceCartaEventosSimulada];

        indiceCartaEventosSimulada = (indiceCartaEventosSimulada + 1) % cartasEventoSimuladas.length;

        return carta;
    }

    /**
     * Comprueba si una posiciÃ³n corresponde a una casilla de evento.
     * @param posicion casilla consultada.
     * @return true si la casilla es de evento.
     */
    private boolean esCasillaEventoSimulada(int posicion) {
        return posicion == 3 ||
        posicion == 9 ||
        posicion == 15 ||
        posicion == 21;
    }

    /**
     * Calcula el patrimonio de un jugador del modo local.
     * @param jugador Ã­ndice del participante.
     * @return suma del saldo y del valor de sus propiedades.
     */
    private double calcularPatrimonioSim(int jugador) {
        double patrimonio = saldosJugadoresSimulados[jugador];

        for (int i = 0; i < propietariosSimulados.length; i++) {
            if (propietariosSimulados[i] == jugador) {
                patrimonio += preciosPropiedadesSimulados[i];
            }
        }
        return patrimonio;
    }

    /**
     * Devuelve al tablero las propiedades de un jugador eliminado en la simulaciÃ³n.
     * @param jugador Ã­ndice del participante.
     */
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

    /**
     * Construye una descripciÃ³n de las propiedades del jugador simulado.
     * @param jugador Ã­ndice del participante.
     * @return texto con las propiedades que posee.
     */
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

    /**
     * Determina si la casilla es una propiedad comprable en el modo simulado.
     * @param posicion casilla consultada.
     * @return true cuando es una propiedad.
     */
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

    /**
     * Cuenta los participantes que continÃºan activos en el modo simulado.
     * @return cantidad de jugadores activos.
     */
    private int contarJugadoresActivos() {
        int cantidad = 0;

        for (boolean activo : jugadoresActivosSimulados) {
            if (activo){
                cantidad++;
            }
        }
        return cantidad;
    }

    /**
     * Construye la tarjeta lateral de informaciÃ³n para un participante.
     * @param jugador Ã­ndice del jugador.
     * @return panel Swing con su informaciÃ³n.
     */
    private JPanel crearTarjetaJugador(int jugador) {
        JPanel tarjeta = new JPanel();

        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));

        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                        fichasJugadores[jugador].getBackground(), 2), 
                        BorderFactory.createEmptyBorder(5, 7, 5,7)));
        lblNombreJugadores[jugador] = new JLabel("Jugador " + (jugador + 1));

        lblNombreJugadores[jugador].setFont(lblNombreJugadores[jugador].getFont().deriveFont(Font.BOLD));

        lblSaldoJugadores[jugador] = new JLabel("Saldo: ₡1500");

        lblEstadoJugadores[jugador] = new JLabel("Activo");

        tarjeta.add(lblNombreJugadores[jugador]);
        tarjeta.add(lblSaldoJugadores[jugador]);
        tarjeta.add(lblEstadoJugadores[jugador]);

        tarjetasJugadores[jugador] = tarjeta;

        return tarjeta;
    }

    // Abre la pantalla final (ganador, empate o sin ganador). Se llama desde el hilo de Swing.
    /**
     * Abre la ventana de resultados de una partida en lÃ­nea.
     * Asocia las acciones de consultar transacciones, reiniciar y finalizar.
     * @param TextoGanador descripciÃ³n del resultado que se presentarÃ¡.
     */
    private void AbrirPantallaFinal(String TextoGanador) {

        // Evitar abrir la pantalla final dos veces.
        if (ventanaFinPartida != null) {
            return;
        }

        // Desactivar acciones de juego.
        btnTirarDados.setEnabled(false);
        btnComprar.setEnabled(false);
        btnTerminarTurno.setEnabled(false);

        boolean esAnfitrion = identificadorLocalEnLinea.equals("J001");

        ventanaFinPartida = new VentanaFinPartida(TextoGanador,
            idParaPatrimonioFinal == null
            ? "No corresponde" : "Calculando...",
            historialTransaccionesFinal,
            esAnfitrion, () -> cliente.enviarSolicitud("REINICIAR_PARTIDA"),
            () -> cliente.enviarSolicitud("CERRAR_SERVIDOR"));

        ventanaFinPartida.SetAccionConsultar(Consulta -> cliente.enviarSolicitud(Consulta));

        ventanaFinPartida.SetJugadores(JugadoresPartida);

        ventanaFinPartida.setVisible(true);
    }

    /**
     * Agrega un mensaje al historial visible y desplaza la vista al final.
     * @param mensaje texto que se desea registrar.
     */
    private void agregarHistorialSimulado(String mensaje) {
        if (!areaHistorial.getText().isEmpty()) {
            areaHistorial.append("\n");
        }
        areaHistorial.append(mensaje);

        areaHistorial.setCaretPosition(areaHistorial.getDocument().getLength());
    }

    /**
     * Actualiza la etiqueta de ronda a partir del estado del modo simulado.
     */
    private void actualizarEtiquetaRonda() {
        if (partidaPorRondas) {
            lblNumeroRonda.setText("Ronda: " + numeroRondaSimulada + " / " + maxRondasSimulado);
        }
        else {
            lblNumeroRonda.setText("Ronda: " + numeroRondaSimulada + " | Sin límite");
        }
    }

    /**
     * Obtiene el carÃ¡cter visual correspondiente al resultado de un dado.
     * @param valor nÃºmero de la cara del dado.
     * @return representaciÃ³n visual de la cara.
     */
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

    /**
     * Actualiza las tarjetas laterales con saldos y estados de los jugadores.
     */
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

    /**
     * Destaca la posiciÃ³n del participante con el turno en la simulaciÃ³n.
     */
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

    /**
     * Procesa mensajes de red de manera continua en un hilo receptor.
     * Interpreta estados, movimientos, eventos, compras y finalizaciÃ³n; los cambios
     * de componentes Swing se delegan al hilo de eventos cuando corresponde.
     */
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

                // ----------------------
                // REINICIO DE PARTIDA
                //-----------------------

                if (mensaje.equals("PARTIDA_INICIADA")) {
                    SwingUtilities.invokeLater(() -> {
                        if (ventanaFinPartida != null) {
                            ventanaFinPartida.dispose();
                            ventanaFinPartida = null;
                        }
                        VentanaJuego nuevaVentana = new VentanaJuego();

                        nuevaVentana.activarModoEnLinea(cliente);
                        nuevaVentana.setVisible(true);
                        dispose();
                    });
                    break;
                }

                // ----------------------------------
                // CIERRE DEL SERVIDOR
                // ----------------------------------

                if (mensaje.equals("SERVIDOR_CERRADO")) {
                    SwingUtilities.invokeLater(() -> {
                        if (ventanaFinPartida != null) {
                            ventanaFinPartida.dispose();
                            ventanaFinPartida = null;
                        }
                        JOptionPane.showMessageDialog(this,
                            "El anfitirión ha finalizado la partida.\n" + "El servidor se ha cerrado.",
                            "Partida finalizada",
                            JOptionPane.INFORMATION_MESSAGE);

                        cliente.desconectar();

                        dispose();
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

                else if (mensaje.startsWith("JUGADOR_DESCONECTADO")) {
                    String[] datos = mensaje.split(";", 2);

                    if (datos.length == 2 && datos[1].matches("J00[1-4]")) {
                        String identificador = datos[1];
                        int indiceJugador = Integer.parseInt(identificador.substring(1)) - 1;

                        SwingUtilities.invokeLater(() -> {
                            retirarFichaJugadorSimulado(indiceJugador);

                            lblEstadoJugadores[indiceJugador].setText("Desconectado / Eliminado");

                            tarjetasJugadores[indiceJugador].setEnabled(false);

                            tarjetasJugadores[indiceJugador].setBackground(new Color(210, 210, 210));

                            lblEstadoJugadores[indiceJugador].setForeground(Color.GRAY);

                            lblSaldoJugadores[indiceJugador].setForeground(Color.GRAY);

                            agregarHistorialSimulado(identificador + " se desconectó y quedó eliminado.");
                        });
                    }
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

                    SwingUtilities.invokeLater(() -> {
                        historialTransaccionesFinal = contenido.equals("SIN_TRANSACCIONES") ? "" : contenido.replace(";", "\n").replace("|", "|");

                        if (ventanaFinPartida != null) {
                            ventanaFinPartida.actualizarTransacciones(historialTransaccionesFinal);
                        }
                    });

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
                                agregarHistorialSimulado(FormateadorTransacciones.formatear(registro));
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

                            // También se muestra en el tablero cuando la ficha llegue a la casilla de evento.
                            CartaPorMostrar = jugadorEvento + " sacó la carta " + identificadorCarta
                                    + ":\n\n" + descripcionCarta;

                        });
                    }
                }
                // -------------------------------------------------
                // FIN DE PARTIDA
                // -------------------------------------------------

                else if (mensaje.startsWith("FIN;")) {

                    String[] datos = mensaje.split(";", 3);

                    if (datos.length == 3 &&
                        (datos[1].equals("GANADOR") ||
                        datos[1].equals("EMPATE"))) {

                        String primerId = datos[2].split(",")[0].trim();

                        idParaPatrimonioFinal = primerId.matches("J00[1-4]") ? primerId : null;

                        String resultado;

                        if (datos[1].equals("EMPATE")) {
                            StringBuilder nombresEmpatados = new StringBuilder();

                            String[] idsEmpatados = datos[2].split(",");

                            for (String id : idsEmpatados) {
                                if (nombresEmpatados.length() > 0) {
                                    nombresEmpatados.append(", ");
                                }

                                nombresEmpatados.append(obtenerNombreJugador(id.trim()));
                            }

                            resultado = "Empate entre " + nombresEmpatados;
                        } else if (datos[2].equals("SIN_GANADOR")) {
                            resultado = "Sin ganador";
                        } else {
                            resultado = obtenerNombreJugador(datos[2].trim());
                        }

                        SwingUtilities.invokeLater(() -> {
                            AbrirPantallaFinal(resultado);
                        });

                        // Recuperar las transacciones y el estado definitivos.
                        // Del estado calculamos el patrimonio del ganador
                        // o el patrimonio común en caso de empate.
                        cliente.enviarSolicitud("CONSULTAR_TRANSACCIONES");
                        cliente.enviarSolicitud("CONSULTAR_ESTADO");
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

                    String MensajeHardware = mensaje.substring("HARDWARE;".length());

                    String TextoHardware = TraductorMensajesHardware.traducir(MensajeHardware);

                    SwingUtilities.invokeLater(() -> {

                        // El servidor está usando el hardware RFID (cambia cómo se confirma una compra).
                        ModoHardwareEnLinea = true;

                        // La tarjeta aceptó el pago: se cierra el aviso "Confirmar compra" (si estaba abierto).
                        if (MensajeHardware.startsWith("PAGO_OK;")) {
                            CerrarDialogoCompra();
                        }

                        // Tarjeta validada: se puede lanzar con el botón físico o con el de la ventana.
                        if (MensajeHardware.startsWith("ESPERANDO_BOTON;")) {

                            EsperandoBotonDados = true;

                            btnTirarDados.setText("Lanzar dados");
                            btnTirarDados.setEnabled(true);
                        }

                        // Ya se lanzaron los dados (con cualquiera de los dos botones).
                        else if (MensajeHardware.startsWith("BOTON_DIGITAL;")
                                || MensajeHardware.startsWith("DADOS;")) {

                            EsperandoBotonDados = false;

                            btnTirarDados.setText("Tirar dados");
                            btnTirarDados.setEnabled(false);
                        }

                        // Algunos avisos internos de la Pico no se muestran.
                        if (TextoHardware != null) {
                            agregarHistorialSimulado(TextoHardware);
                        }
                    });
                }

                // Resultado de una consulta de la pantalla final (por jugador / tipo / orden).
                else if (mensaje.equals("COMPRA_CANCELADA")) {

                    // El jugador se arrepintió: no se cobró nada y la propiedad sigue disponible.
                    SwingUtilities.invokeLater(() -> {

                        CerrarDialogoCompra();

                        agregarHistorialSimulado("Compra cancelada: no se cobró nada.");
                    });
                }

                // Error del hardware al validar una compra: se cierra el aviso y se muestra el error.
                else if (mensaje.startsWith("No se pudo validar el pago")) {

                    SwingUtilities.invokeLater(() -> {

                        CerrarDialogoCompra();

                        agregarHistorialSimulado(mensaje);
                    });
                }

                else if (mensaje.startsWith("CONSULTA;")) {

                    String Registros = mensaje.substring("CONSULTA;".length());

                    if (ventanaFinPartida != null) {
                        ventanaFinPartida.MostrarConsulta(Registros);
                    }
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


    /**
     * Interpreta un mensaje ESTADO del servidor y sincroniza el tablero grÃ¡fico.
     * Actualiza posiciones, turno, propietarios y datos econÃ³micos sin alterar
     * las reglas de negocio almacenadas en el servidor.
     * @param estado cadena de estado enviada por el servidor.
     */
    private void actualizarFichasDesdeEstado(String estado) {

        if (estado == null || !estado.startsWith("ESTADO;")) {
            return;
        }

        String[] campos = estado.split(";", -1);

        // Ahora recibimos 13 campos desde el servidor.
        if (campos.length != 14) {
            System.out.println(
                    "Formato ESTADO inválido: " + campos.length
            );
            return;
        }

        String patrimonioActual = calcularPatrimonioFinal(estado, campos[1]);

        String[] jugadores = campos[8].split("\\|");
        
        int maxRondasReales = Integer.parseInt(campos[13]);

        // El servidor envia saldo y propietarios de las casillas.
        // Sumarlos permite obtener el mismo patrimonio que Juego.java
        // sin modificar la logica del servidor.
        String patrimonioCalculado = calcularPatrimonioFinal(
                estado, idParaPatrimonioFinal
        );

        SwingUtilities.invokeLater(() -> {

            // Identificación del jugador y del turno.
            String identificadorLocal = campos[1];
            String identificadorTurno = campos[6];
            identificadorLocalEnLinea = identificadorLocal;

            if (patrimonioActual != null) {
                lblPatrimonio.setText("Patrimonio: " + patrimonioActual);
            }

            // Actualizar la pantalla final si recibimos el estado
            // solicitado tras FIN;GANADOR o FIN;EMPATE.
            if (patrimonioCalculado != null && ventanaFinPartida != null) {
                ventanaFinPartida.actualizarPatrimonio(patrimonioCalculado);
            }

            lblNombre.setText(
                    "Jugador: " + campos[2] + " (" + identificadorLocal + ")"
            );

            lblTurno.setText(
                    "Turno actual: " + identificadorTurno
            );

            if (maxRondasReales == 0) {
                lblNumeroRonda.setText("Ronda: " + campos[7] + " / Sin límite");

            } else {
                lblNumeroRonda.setText("Ronda: " + campos[7] +" / " + maxRondasReales);
            }

            if (identificadorTurno.matches("J00[1-4]")) {
                indiceTurnoEnLinea = Integer.parseInt(identificadorTurno.substring(1)) - 1;
            }

            // Información real del jugador.
            lblSaldo.setText("Saldo: ₡" + campos[3]);
            lblPosicion.setText("Posición: " + campos[4]);

            try {
                PosicionLocalEnLinea = Integer.parseInt(campos[4]);
            } catch (NumberFormatException ex) {
                PosicionLocalEnLinea = -1;
            }
            lblPropiedades.setText("Propiedades: " + obtenerNombresPropiedades(campos[5]));

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

            // Identificadores de los jugadores de esta partida (para el filtro de la pantalla final).
            String IdsPartida = "";

            // Sincronizar las fichas de todos los jugadores.
            for (String registro : jugadores) {

                String[] datos = registro.split(",");

                if (datos.length != 5 || !datos[0].matches("J00[1-4]")) {
                    continue;
                }

                IdsPartida = IdsPartida.isEmpty() ? datos[0] : IdsPartida + "," + datos[0];

                int indiceJugador = Integer.parseInt(datos[0].substring(1)) - 1;

                String nombreJugador = datos[4];

                nombresJugadoresEnLinea[indiceJugador] = nombreJugador;

                lblNombreJugadores[indiceJugador].setText(nombreJugador + " (" + datos[0] + ")");

                tarjetasJugadores[indiceJugador].setVisible(true);

                if (datos[0].equals(identificadorTurno)) {
                    lblTurno.setText("Turno actual: " + nombreJugador + " (" + identificadorTurno + ")");
                }

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

            if (!IdsPartida.isEmpty()) {
                JugadoresPartida = IdsPartida.split(",");
            }

            // Al cambiar el turno, el marco pasa a la casilla del nuevo jugador
            // (si su ficha se está animando, el marco la sigue desde marcarPosicionJugador).
            if (indiceTurnoEnLinea >= 0
                    && animacionesFichas[indiceTurnoEnLinea] == null
                    && posicionesVisuales[indiceTurnoEnLinea] != -1) {

                ResaltarCasilla(
                        posicionesVisuales[indiceTurnoEnLinea],
                        fichasJugadores[indiceTurnoEnLinea].getBackground()
                );
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
    
    /**
     * Convierte los Ã­ndices de propiedades incluidos en el estado a sus nombres.
     * @param propiedades representaciÃ³n textual de las posiciones del tablero.
     * @return lista legible de nombres de propiedades.
     */
    private String obtenerNombresPropiedades(String propiedades) {

        if (propiedades == null
                || propiedades.isBlank()
                || propiedades.equals("SIN_PROPIEDADES")) {

            return "ninguna";
        }

        StringBuilder nombres = new StringBuilder();

        for (String identificador : propiedades.split(",")) {

            String id = identificador.trim();

            if (id.matches("P\\d{2}")) {

                int posicion = Integer.parseInt(id.substring(1));

                if (posicion >= 0 && posicion < nombresCasillas.length) {

                    if (nombres.length() > 0) {
                        nombres.append(", ");
                    }

                    nombres.append(nombresCasillas[posicion]);
                }
            }
        }

        return nombres.length() == 0 ? "ninguna" : nombres.toString();
    }

    /**
     * Coloca una ficha en su casilla visual y actualiza el resaltado del tablero.
     * @param jugador Ã­ndice de la ficha.
     * @param posicion Ã­ndice de la casilla de destino.
     */
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

        // En línea, el marco de color sigue a la ficha del jugador que tiene el turno.
        if (modoEnLinea && jugador == indiceTurnoEnLinea) {
            ResaltarCasilla(posicion, ficha.getBackground());
        }
    }

    // Marca una sola casilla con un borde del color del jugador (las demás vuelven al borde normal).
    /**
     * Resalta una casilla con el borde del color del participante.
     * @param Posicion Ã­ndice de la casilla.
     * @param ColorJugador color visual del participante.
     */
    private void ResaltarCasilla(int Posicion, Color ColorJugador) {

        for (int i = 0; i < casillasVisuales.length; i++) {
            if (casillasVisuales[i] != null) {
                casillasVisuales[i].setBorder(BorderFactory.createEtchedBorder());
            }
        }

        if (Posicion >= 0 && Posicion < casillasVisuales.length && casillasVisuales[Posicion] != null) {
            casillasVisuales[Posicion].setBorder(BorderFactory.createLineBorder(ColorJugador, 4));
        }
    }

    // Texto de confirmación de compra, por ejemplo: "¿Está seguro de comprar Bosque de Bambúes por ₡250?"
    /**
     * Genera el texto de confirmaciÃ³n de compra de la propiedad actual.
     * @return mensaje que se mostrarÃ¡ al jugador.
     */
    private String TextoConfirmarCompra() {

        if (PosicionLocalEnLinea >= 0 && PosicionLocalEnLinea < nombresCasillas.length) {

            return "¿Está seguro de comprar " + nombresCasillas[PosicionLocalEnLinea]
                    + " por ₡" + (int) preciosPropiedadesSimulados[PosicionLocalEnLinea] + "?";
        }

        return "¿Está seguro de comprar esta propiedad?";
    }

    /**
     * Obtiene el nombre visible asociado al identificador de un participante.
     * @param identificador cÃ³digo del jugador, como J001.
     * @return nombre registrado o texto identificador de respaldo.
     */
    private String obtenerNombreJugador(String identificador) {

        if (identificador == null
                || !identificador.matches("J00[1-4]")) {
            return identificador;
        }

        int indice = Integer.parseInt(identificador.substring(1)) - 1;

        String nombre = nombresJugadoresEnLinea[indice];

        if (nombre == null || nombre.isBlank()) {
            return identificador;
        }

        return nombre + " (" + identificador + ")";
    }

    // Aviso de compra con RFID: solo lo ve el jugador que compra.
    // Acercar la tarjeta acepta la compra; "Cancelar" (o cerrar el aviso) cancela la validación.
    /**
     * Abre el diÃ¡logo para validar una compra con tarjeta RFID.
     * La compra o cancelaciÃ³n real se comunica al servidor.
     * @param TextoCompra descripciÃ³n de la propiedad pendiente.
     */
    private void MostrarDialogoCompraRfid(String TextoCompra) {

        CerrarDialogoCompra();

        JOptionPane PanelCompra = new JOptionPane(
                TextoCompra + "\n\nAcerque su tarjeta RFID para aceptar la compra.",
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                new Object[] {"Cancelar"},
                "Cancelar"
        );

        JDialog Dialogo = PanelCompra.createDialog(this, "Confirmar compra");
        Dialogo.setModal(false);

        // Se dispara al pulsar "Cancelar" o al cerrar el aviso con la X.
        PanelCompra.addPropertyChangeListener(JOptionPane.VALUE_PROPERTY, e -> {

            // Si el aviso ya se cerró porque la compra terminó, no hay nada que cancelar.
            if (DialogoCompra == Dialogo) {

                DialogoCompra = null;

                cliente.enviarSolicitud("CANCELAR_COMPRA");
            }
        });

        DialogoCompra = Dialogo;

        Dialogo.setVisible(true);
    }

    // Cierra el aviso de compra (la tarjeta se validó, se canceló o hubo un error).
    /**
     * Cierra el diÃ¡logo de compra que pudiera permanecer abierto.
     */
    private void CerrarDialogoCompra() {

        if (DialogoCompra != null) {

            JDialog Dialogo = DialogoCompra;

            DialogoCompra = null;

            Dialogo.dispose();
        }
    }

    // Muestra en el tablero la carta de evento que salió (a todos los jugadores).
    // Se llama cuando la ficha llega a la casilla de evento, antes de que la carta la mueva.
    // La ventana no es modal: no bloquea el juego (por ejemplo, el pago con la tarjeta).
    /**
     * Presenta la carta recibida desde el servidor cuando corresponde mostrarla.
     */
    private void MostrarCartaPendiente() {

        if (CartaPorMostrar == null) {
            return;
        }

        String Texto = CartaPorMostrar;
        CartaPorMostrar = null;

        JOptionPane PanelCarta = new JOptionPane(Texto, JOptionPane.INFORMATION_MESSAGE);
        JDialog DialogoCarta = PanelCarta.createDialog(this, "Carta de Evento");
        DialogoCarta.setModal(false);
        DialogoCarta.setVisible(true);
    }

    // Decide cómo mostrar la posición que llegó en un ESTADO:
    // - Si la ficha se está animando, solo se actualiza el destino final.
    // - Si es el jugador que acaba de lanzar, avanza casilla por casilla.
    // - Si no, se coloca directo (el primer ESTADO, cuando la ficha aún no está en el tablero).
    /**
     * Decide entre animar o colocar directamente una ficha a partir de ESTADO.
     * @param jugador Ã­ndice del participante.
     * @param posicion casilla de destino reportada por el servidor.
     */
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
    /**
     * Mueve gradualmente una ficha por el tablero mediante un Timer de Swing.
     * @param jugador Ã­ndice de la ficha.
     * @param pasos nÃºmero de casillas que debe recorrer.
     * @param destinoFinal casilla de destino confirmada por el servidor.
     */
    private void animarFicha(int jugador, int pasos, int destinoFinal) {
        detenerAnimacionFicha(jugador);

        if (posicionesVisuales[jugador] == -1 || pasos <= 0) {
            marcarPosicionJugador(jugador, destinoFinal);
            MostrarCartaPendiente();
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

                    // Llegó a la casilla de los dados: si salió una carta, se muestra ahora.
                    MostrarCartaPendiente();

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

    /**
     * Detiene la animaciÃ³n activa de un participante, si existe.
     * @param jugador Ã­ndice de la ficha.
     */
    private void detenerAnimacionFicha(int jugador) {
        if (animacionesFichas[jugador] != null) {
            animacionesFichas[jugador].stop();
            animacionesFichas[jugador] = null;
        }
    }

    /**
     * Retira visualmente la ficha de un participante eliminado o desconectado.
     * @param jugador Ã­ndice del participante.
     */
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

    /**
     * Calcula el patrimonio total de un participante a partir de ESTADO.
     * Utiliza el saldo y el valor de las propiedades recibidos del servidor.
     * @param estado estado completo de la partida.
     * @param idJugador identificador del jugador consultado.
     * @return patrimonio convertido a texto para la pantalla final.
     */
    private String calcularPatrimonioFinal(String estado, String idJugador) {

        String[] campos = estado.split(";", -1);

        if (campos.length != 14 || idJugador == null) {
            return null;
        }

        double patrimonio = 0;
        boolean encontrado = false;

        // Obtener el saldo final del jugador.
        String[] jugadores = campos[8].split("\\|");

        for (String registro : jugadores) {

            String[] datos = registro.split(",");

            if (datos.length == 5 &&
                    datos[0].equals(idJugador)) {

                try {
                    patrimonio = Double.parseDouble(datos[3]);
                    encontrado = true;
                } catch (NumberFormatException e) {
                    return null;
                }

                break;
            }
        }

        if (!encontrado) {
            return null;
        }

        // Sumar el valor de sus propiedades.
        String[] propiedades = campos[9].split("\\|");

        for (String registro : propiedades) {

            String[] datos = registro.split(",");

            if (datos.length != 2 ||
                    !datos[1].equals(idJugador) ||
                    !datos[0].matches("P\\d{2}")) {
                continue;
            }

            int posicion = Integer.parseInt(
                    datos[0].substring(1)
            );

            if (posicion >= 0 &&
                    posicion < preciosPropiedadesSimulados.length) {

                patrimonio += preciosPropiedadesSimulados[posicion];
            }
        }

        return "₡" + String.format(
                java.util.Locale.US, "%,.2f", patrimonio
        );
    }

    // Método principal
    /**
     * Abre el tablero de manera independiente para pruebas de interfaz.
     * @param args argumentos de consola; no se utilizan.
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            VentanaJuego ventana =
                new VentanaJuego();

            ventana.setVisible(true);
        });
    }
}
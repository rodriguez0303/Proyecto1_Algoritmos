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
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Component;
import java.awt.Color;
import java.awt.Font;

import java.util.Random;
import java.util.Arrays;


// Clase principal de la interfaz gráfica del juego
public class VentanaPrincipal extends JFrame {

    private JPanel [] casillasVisuales = new JPanel[24];
    private JPanel[] panelesFichas = new JPanel[24];

    private JLabel[] etiquetasPropietarios = new JLabel[24];
    private JLabel[] fichasJugadores = new JLabel[4];

    private Random generador = new Random();

    private String[] nombresCasillas = {
    "Salida",        // 0
    "Propiedad 1",   // 1
    "Propiedad 2",   // 2
    "Evento",        // 3
    "Propiedad 3",   // 4
    "Propiedad 4",   // 5
    "Edificio D3",      // 6
    "Propiedad 5",   // 7
    "Propiedad 6",   // 8
    "Evento",        // 9
    "Propiedad 7",  // 10
    "Propiedad 8",  // 11
    "Especial",      // 12
    "Propiedad 9",  // 13
    "Propiedad 10",  // 14
    "Evento",        // 15
    "Propiedad 11",  // 16
    "Propiedad 12",  // 17
    "Especial",      // 18
    "Propiedad 13",  // 19
    "Propiedad 14",  // 20
    "Evento",        // 21
    "Propiedad 15",  // 22
    "Propiedad 16"   // 23
    };
    private String[] cartasEventoSimuladas = {
        "Recibe ₡100 por beca del TEC.",
        "Paga ₡100 por romper algo de laboratio.",
        "Avanza 3 posiciones.",
        "Vas directamente al Edificio D3.",
        "Vas directamente a la Salida."
    };

    private int indiceCartaEventosSimulada = 0;
    private int[] posicionesJugadoresSimulados = {0, 0, 0, 0};
    private int [] posicionesVisuales = {-1, -1, -1, -1};
    private int jugadorActualSimulado = 0;
    private int[] propietariosSimulados = new int[24];

    private double[] saldosJugadoresSimulados = {1500, 1500, 1500, 1500};

    private static final double PRECIO_PROPIEDAD_SIMULADO = 200;
    private static final double ALQUILER_SIMULADO = 100;

    private boolean dadosLanzadosSimulados = false;
    private boolean[] jugadoresActivosSimulados = { true, true, true, true};

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

                    if (numeroCasilla == 0) {
                        casilla.setBackground(new Color(180, 230, 180));
                    }
                    else if (numeroCasilla == 3 ||
                            numeroCasilla == 9 ||
                            numeroCasilla == 15 ||
                            numeroCasilla == 21) {
                        casilla.setBackground(new Color(255, 230, 160));
                    }
                    else if (numeroCasilla == 6 ||
                            numeroCasilla == 12 ||
                            numeroCasilla == 18) {
                        casilla.setBackground(new Color(180, 220, 245));
                    }
                    else {
                        casilla.setBackground(new Color(235, 235, 235));
                    }

                    casillasVisuales[numeroCasilla] = casilla;
                    panelesFichas[numeroCasilla] = panelFichas;
                    
                    casilla.setBorder(BorderFactory.createEtchedBorder());

                    JLabel textoCasilla = new JLabel(nombresCasillas[numeroCasilla]);
                    JLabel lblPropietario = new JLabel(" ");

                    lblPropietario.setPreferredSize(tamanoPropietario);
                    lblPropietario.setMinimumSize(tamanoPropietario);

                    etiquetasPropietarios[numeroCasilla] = lblPropietario;

                    casilla.add(textoCasilla, BorderLayout.NORTH);
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

        JLabel lblTituloCentro = new JLabel("Monopoly TEC", JLabel.CENTER);

        lblTituloCentro.setFont(new Font("Serif", Font.BOLD, 42));

        panelCentro.add(lblTituloCentro, BorderLayout.CENTER);


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

        JLabel lblNombre = new JLabel("Jugador actual: J1");
        JLabel lblSaldo = new JLabel("Saldo: ₡1500");
        JLabel lblPatrimonio = new JLabel("Patrimonio: ₡1500");
        JLabel lblPosicion = new JLabel("Posición: 0");
        JLabel lblTurno = new JLabel("Turno actual: J1");

        JTextArea lblPropiedades = new JTextArea("Propiedades: ninguna");

        lblPropiedades.setEditable(false);
        lblPropiedades.setLineWrap(true);
        lblPropiedades.setWrapStyleWord(true);
        lblPropiedades.setOpaque(false);
        lblPropiedades.setFocusable(false);
        lblPropiedades.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        lblPropiedades.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblPatrimonio.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblTurno.setOpaque(true);
        lblTurno.setForeground(Color.WHITE);

        lblTurno.setBackground(fichasJugadores[jugadorActualSimulado].getBackground());

        lblTurno.setBorder(BorderFactory.createEmptyBorder(4, 7, 4, 7));

        // -------------------------------------------------
        // INFORMACIÓN DE LOS DADOS
        // -------------------------------------------------

        JPanel panelDado1 = new JPanel(new BorderLayout());
        JPanel panelDado2 = new JPanel(new BorderLayout());

        JLabel tituloDado1 = new JLabel("Dado 1", JLabel.CENTER);
        JLabel tituloDado2 = new JLabel("Dado 2", JLabel.CENTER);

        JLabel lblDado1 = new JLabel("-", JLabel.CENTER);
        JLabel lblDado2 = new JLabel("-", JLabel.CENTER);

        lblDado1.setFont(new Font("SansSerif", Font.BOLD, 42));
        lblDado2.setFont(new Font("SansSerif", Font.BOLD, 42));

        panelDado1.add(tituloDado1, BorderLayout.NORTH);
        panelDado1.add(lblDado1, BorderLayout.CENTER);

        panelDado2.add(tituloDado2, BorderLayout.NORTH);
        panelDado2.add(lblDado2, BorderLayout.CENTER);

        panelDados.add(panelDado1);
        panelDados.add(panelDado2);

        // -------------------------------------------------
        // BOTONES
        // -------------------------------------------------

        JButton btnTirarDados = new JButton("Tirar dados");
        JButton btnComprar = new JButton("Comprar propiedad");
        btnComprar.setEnabled(false);
        JButton btnTerminarTurno = new JButton("Terminar turno");

        Dimension tamanoBoton = new Dimension(180, 30);

        btnTirarDados.setMaximumSize(tamanoBoton);
        btnComprar.setMaximumSize(tamanoBoton);
        btnTerminarTurno.setMaximumSize(tamanoBoton);

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

        panelJugador.add(lblNombre);
        panelJugador.add(lblSaldo);
        panelJugador.add(lblPatrimonio);
        panelJugador.add(lblPosicion);
        panelJugador.add(lblTurno);
        panelJugador.add(lblPropiedades);

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

            lblDado1.setText(obtenerCaraDado(dado1));
            lblDado2.setText(obtenerCaraDado(dado2));

            posicionesJugadoresSimulados[jugadorActualSimulado] = (posicionesJugadoresSimulados[jugadorActualSimulado] + total) % 24;

            int nuevaPosicion = posicionesJugadoresSimulados[jugadorActualSimulado];

            String mensajeCasilla;

            int propietario = propietariosSimulados[nuevaPosicion];

            if (esCasillaEventoSimulada(nuevaPosicion)) {

                btnComprar.setEnabled(false);

                String carta = obtenerCartaEventosSimulada();

                if (carta.equals("Recibe ₡100 por una beca del TEC.")) {
                    saldosJugadoresSimulados[jugadorActualSimulado] += 100;

                    lblSaldo.setText("Saldo: ₡" + saldosJugadoresSimulados[jugadorActualSimulado]);

                    lblPatrimonio.setText("Patrimonio: " + calcularPatrimonioSim(jugadorActualSimulado));
            
                }

                else if (carta.equals("Paga ₡100 por romper algo de laboratorio.")) {

                    if (saldosJugadoresSimulados[jugadorActualSimulado] >= 100) {
                        saldosJugadoresSimulados[jugadorActualSimulado] -= 100;

                        lblSaldo.setText("Saldo: ₡" + saldosJugadoresSimulados[jugadorActualSimulado]);

                        lblPatrimonio.setText("Patrimonio: ₡" + calcularPatrimonioSim(jugadorActualSimulado));
                    }
                    else {
                        jugadoresActivosSimulados[jugadorActualSimulado] = false;

                        carta = "No pudo pagar ₡100 por lo que rompió y fue eliminado.";
                    }
                }

                mensajeCasilla = "Carta de evento: " + carta;
            }

            else if (!esPropiedadSimulada(nuevaPosicion)) {
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

                if (saldosJugadoresSimulados[jugadorActualSimulado] >= ALQUILER_SIMULADO) {
                    saldosJugadoresSimulados[jugadorActualSimulado] -= ALQUILER_SIMULADO;
                    saldosJugadoresSimulados[propietario] += ALQUILER_SIMULADO;

                    lblSaldo.setText("Saldo: ₡" + saldosJugadoresSimulados[jugadorActualSimulado]);

                    lblPatrimonio.setText("Patrimonio: ₡" + calcularPatrimonioSim(jugadorActualSimulado));

                    mensajeCasilla = "Pagó ₡" + ALQUILER_SIMULADO + " de alquiler a J" + (propietario + 1) + ".";
                }
                else {
                    jugadoresActivosSimulados[jugadorActualSimulado] = false;

                    mensajeCasilla = "J" + (jugadorActualSimulado + 1) + " no pudo pagar alquiler y fue eliminado.";
                }
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
            if (saldosJugadoresSimulados[jugadorActualSimulado] < PRECIO_PROPIEDAD_SIMULADO) {
                lblEstado.setText("J" + (jugadorActualSimulado + 1) + " no tiene saldo suficiente.");
                return;
            }

            saldosJugadoresSimulados[jugadorActualSimulado] -= PRECIO_PROPIEDAD_SIMULADO;

            lblSaldo.setText("Saldo: ₡" + saldosJugadoresSimulados[jugadorActualSimulado]);

            propietariosSimulados[posicionActual] = jugadorActualSimulado;

            lblPropiedades.setText(obtenerPropiedadesJugadorSim(jugadorActualSimulado));

            etiquetasPropietarios[posicionActual].setText("Dueño: J" + (jugadorActualSimulado + 1));
            etiquetasPropietarios[posicionActual].setOpaque(true);
        
            etiquetasPropietarios[posicionActual].setBackground(fichasJugadores[jugadorActualSimulado].getBackground());
            etiquetasPropietarios[posicionActual].setForeground(Color.WHITE);

            etiquetasPropietarios[posicionActual].setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));

            lblEstado.setText("J" + (jugadorActualSimulado +1) + " compró " + nombresCasillas[posicionActual] + ".");

            lblPatrimonio.setText("Patrimonio: ₡" + calcularPatrimonioSim(jugadorActualSimulado));

            btnComprar.setEnabled(false);
        });


        btnTerminarTurno.addActionListener(e -> {
            int intentos = 0;

            do {jugadorActualSimulado = (jugadorActualSimulado + 1 ) % 4;
                intentos++;
            } while (!jugadoresActivosSimulados[jugadorActualSimulado] && intentos < 4);
            
            if (contarJugadoresActivos() == 1) {
                int ganador = -1;

                for (int i = 0; i < jugadoresActivosSimulados.length; i++) {
                    if (jugadoresActivosSimulados[i]) {
                        ganador = i;
                        break;
                    }
                }
                lblEstado.setText("La partida terminó. El ganador fue J" + (ganador + 1) + ".");

                btnTirarDados.setEnabled(false);
                btnComprar.setEnabled(false);
                btnTerminarTurno.setEnabled(false);

                return;
            }

            dadosLanzadosSimulados = false;
            btnTirarDados.setEnabled(true);
            btnComprar.setEnabled(false);

            lblTurno.setText("Turno actual: J" + (jugadorActualSimulado + 1));
            lblNombre.setText("Jugador actual: J" + (jugadorActualSimulado + 1));

            lblTurno.setBackground(fichasJugadores[jugadorActualSimulado].getBackground());

            lblPropiedades.setText(obtenerPropiedadesJugadorSim(jugadorActualSimulado));

            lblPatrimonio.setText("Patrimonio: ₡" + calcularPatrimonioSim(jugadorActualSimulado));

            lblSaldo.setText("Saldo: ₡" + saldosJugadoresSimulados[jugadorActualSimulado]);

            lblPosicion.setText("Posición: " + posicionesJugadoresSimulados[jugadorActualSimulado]);

            lblDado1.setText("-");
            lblDado2.setText("-");

            lblEstado.setText("Turno de J"+ (jugadorActualSimulado + 1));
        });


        // -------------------------------------------------
        // AGREGAR PANELES A LA VENTANA
        // -------------------------------------------------

        add(panelTablero, BorderLayout.CENTER);
        add(panelJugador, BorderLayout.EAST);
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
                patrimonio += PRECIO_PROPIEDAD_SIMULADO;
            }
        }
        return patrimonio;
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
package Interfaz;

import LogicaJuego.Cliente;
import Red.IniciarServidor;

import javax.swing.JOptionPane;
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


/**
 * Pantalla inicial de Monopoly TEC.
 * Permite elegir una partida simulada, crear un servidor o unirse a una partida en lÃ­nea.
 */
public class VentanaInicio extends JFrame {

    /**
     * Controles para seleccionar el tipo de partida y configurar sus lÃ­mites.
     */
    private JRadioButton rbPorRondas;
    private JRadioButton rbNormal;

    private JSpinner spRondas;
    private JSpinner spJugadores;

    /**
     * Construye la pantalla de inicio y registra las acciones de los botones.
     * La creaciÃ³n del servidor y la conexiÃ³n del cliente usan hilos independientes
     * para no bloquear el hilo de eventos de Swing.
     */
    public VentanaInicio() {

        // -------------------------------------------------
        // CONFIGURACIÓN DE LA VENTANA
        // -------------------------------------------------

        setTitle("Monopoly TEC");

        setSize(500, 500);

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

        spRondas = new JSpinner(new SpinnerNumberModel(5, 1, null, 1));

        spRondas.setMaximumSize(new Dimension(70, 30));

        panelRondas.add(lblRondas);
        panelRondas.add(spRondas);
        panelRondas.setOpaque(false);

        
        // -------------------------------------------------
        // CANTIDAD DE JUGADORES
        // -------------------------------------------------

        JPanel panelJugadores = new JPanel();
        panelJugadores.setLayout(new BoxLayout(panelJugadores, BoxLayout.X_AXIS));

        panelJugadores.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelJugadores.setOpaque(false);

        JLabel lblJugadores = new JLabel("Cantidad de jugadores: ");

        spJugadores = new JSpinner(new SpinnerNumberModel(2, 2, 4, 1));

        spJugadores.setMaximumSize(new Dimension(70, 30));

        panelJugadores.add(lblJugadores);
        panelJugadores.add(spJugadores);

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

        JButton btnCrearServidor = new JButton("Crear partida en línea");

        btnCrearServidor.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCrearServidor.setMaximumSize(new Dimension(230, 35));

        btnCrearServidor.setBackground(new Color(35, 90, 65));
        btnCrearServidor.setForeground(Color.WHITE);

        btnCrearServidor.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnCrearServidor.setFocusPainted(false);

        JButton btnConectar = new JButton("Unirse a partida en línea");

        btnConectar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnConectar.setMaximumSize(new Dimension(230, 35));

        btnConectar.setBackground(new Color(60, 100, 160));
        btnConectar.setForeground(Color.WHITE);

        btnConectar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnConectar.setFocusPainted(false);
        
        // -------------------------------------------------
        // CREAR PARTIDA EN LÍNEA
        // -------------------------------------------------

        btnCrearServidor.addActionListener(e -> {

            int cantidadJugadores = (int) spJugadores.getValue();

            int maxRondas = rbPorRondas.isSelected() ? (int) spRondas.getValue() : 0;

            // Evitar iniciar varios servidores desde esta ventana.
            btnCrearServidor.setEnabled(false);

            // Ejecutar el servidor en un hilo independiente
            // para que la interfaz gráfica no se congele.
            Thread hiloServidor = new Thread(() -> {

                IniciarServidor.iniciarServidor(cantidadJugadores, maxRondas, true);

            }, "Servidor-Monopoly");

            hiloServidor.setDaemon(true);
            hiloServidor.start();

            JOptionPane.showMessageDialog(
                    this,
                    "Inicializando servidor para "
                    + cantidadJugadores + " jugadores.\n"
                    + "Rondas: "
                    + (maxRondas == 0 ? "Indefinidas" : maxRondas)
                    + "\n\nCuando el servidor esté listo, "
                    + "podrás conectarte desde esta ventana."
            );
        });

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

        
        btnConectar.addActionListener(e -> {

            // Solicitar la dirección IP del servidor.
            String ip = JOptionPane.showInputDialog(
                this,
                "Ingrese la IP del servidor:",
                "127.0.0.1"
            );

            if (ip == null) {
                return;
            }

            ip = ip.trim();

            if (ip.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this, "La dirección IP no puede estar vacía."
                );
                return;
            }

            // Solicitar el identificador del jugador.
            String id = JOptionPane.showInputDialog(
                this,
                "Ingrese su identificador (J001, J002, J003 o J004):",
                "J001"
            );

            if (id == null) {
                return;
            }

            final String idJugador = id.trim().toUpperCase();
            final String ipServidor = ip;

            if (!idJugador.matches("J00[1-4]")) {
                JOptionPane.showMessageDialog(
                    this, "Identificador no válido."
                );
                return;
            }

            String nombreIngresado = JOptionPane.showInputDialog(this, "Ingrese su nombre de jugador:", "Jugador " + idJugador.substring(3));

            if (nombreIngresado == null) {
                return;
            }

            final String nombreJugador = nombreIngresado.trim();

            if (nombreJugador.isEmpty() || nombreJugador.length() > 30 || nombreJugador.contains(";") || nombreJugador.contains(",") || nombreJugador.contains("|") || nombreJugador.contains("\n") || nombreJugador.contains("\r")) {
                JOptionPane.showMessageDialog(this, "Nombre no válido. Utilice entre 1 y 30 caracteres.");
                return;
            }

            btnConectar.setEnabled(false);

            // Conectar en otro hilo para no bloquear Swing.
            Thread hiloConexion = new Thread(() -> {

                Cliente nuevoCliente = new Cliente(
                    idJugador,
                    nombreJugador,
                    ipServidor,
                    5000
                );

                if (!nuevoCliente.conectar()) {

                    SwingUtilities.invokeLater(() -> {
                        btnConectar.setEnabled(true);

                        JOptionPane.showMessageDialog(
                            this,
                            "No se pudo conectar al servidor."
                        );
                    });

                    return;
                }

                // Identificarse ante el servidor.
                nuevoCliente.enviarSolicitud(
                    "CONECTAR;" + idJugador + ";" + nombreJugador);

                String respuesta = nuevoCliente.recibirRespuesta();

                SwingUtilities.invokeLater(() -> {

                    if (!"Conexión válida".equals(respuesta)) {

                        nuevoCliente.desconectar();
                        btnConectar.setEnabled(true);

                        JOptionPane.showMessageDialog(
                            this,
                            "Conexión rechazada: " + respuesta
                        );

                        return;
                    }

                    // Abrir el tablero conectado al servidor.
                    VentanaSalaEspera sala = new VentanaSalaEspera(nuevoCliente, idJugador);

                    sala.setVisible(true);
                    sala.iniciarEscuchaServidor();
                    
                    dispose();
                });

            });

            hiloConexion.setDaemon(true);
            hiloConexion.start();
        });

        // -------------------------------------------------
        // AGREGAR COMPONENTES
        // -------------------------------------------------

        panelOpciones.add(panelJugadores);
        panelOpciones.add(Box.createVerticalStrut(15));
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

        panelOpciones.add(Box.createVerticalStrut(10));
        
        panelOpciones.add(btnCrearServidor);

        panelOpciones.add(Box.createVerticalStrut(10));

        panelOpciones.add(btnConectar);

        add(
            panelOpciones,
            BorderLayout.CENTER
        );
    }


    /**
     * Inicia la aplicaciÃ³n mostrando la pantalla de selecciÃ³n de partida.
     * @param args argumentos de consola; no se utilizan.
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            VentanaInicio ventana =
                new VentanaInicio();

            ventana.setVisible(true);

        });
    }
}
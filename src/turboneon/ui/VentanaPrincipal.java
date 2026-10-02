package turboneon.ui;

import turboneon.modelo.Carrera;
import turboneon.red.ClienteJuego;
import turboneon.red.ServidorJuego;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class VentanaPrincipal extends JFrame {
    private JComponent actual;

    public VentanaPrincipal() {
        super("TURBO NEÓN RUSH  |  El juego de carreras DJE²");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 720));
        setPreferredSize(new Dimension(1280, 820));
        getContentPane().setBackground(Tema.FONDO);
        mostrarMenu();
        pack();
        setLocationRelativeTo(null);
    }

    private void mostrar(JComponent p) {
        if (actual instanceof Pantalla) ((Pantalla) actual).detener();
        actual = p;
        getContentPane().removeAll();
        getContentPane().add(p, BorderLayout.CENTER);
        revalidate();
        repaint();
        SwingUtilities.invokeLater(p::requestFocusInWindow);
    }

    public void mostrarMenu() { mostrar(new MenuPanel(this)); }

    // ------------------------------------------------------------ acciones del menú
    public void menuLocal2() { seleccionar(ConfigPartida.local(2, 0)); }

    public void menuCPU() {
        String[] op = { "1 jugador  vs  1 CPU", "1 jugador  vs  2 CPU", "2 jugadores  vs  1 CPU" };
        int r = JOptionPane.showOptionDialog(this, "¿Contra cuántos rivales quieres correr?", "Contra la computadora",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, op, op[0]);
        if (r == 0) seleccionar(ConfigPartida.local(1, 1));
        else if (r == 1) seleccionar(ConfigPartida.local(1, 2));
        else if (r == 2) seleccionar(ConfigPartida.local(2, 1));
    }

    public void menuAnfitrion() {
        String[] op = { "2 jugadores en total", "3 jugadores en total", "4 jugadores en total" };
        int r = JOptionPane.showOptionDialog(this,
                "Tú serás el anfitrión. Los demás se conectan desde otras computadoras.\n¿Cuántos jugadores habrá?",
                "Crear partida en red", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, op, op[0]);
        if (r >= 0 && r <= 2) seleccionar(ConfigPartida.anfitrion(r + 2));
    }

    public void menuCliente() {
        JTextField ip = new JTextField("192.168.1.", 14), puerto = new JTextField("5555", 6), nombre = new JTextField("Jugador", 14);
        JPanel p = new JPanel(new GridLayout(3, 2, 8, 8));
        p.add(new JLabel("IP del anfitrión:")); p.add(ip);
        p.add(new JLabel("Puerto:")); p.add(puerto);
        p.add(new JLabel("Tu nombre:")); p.add(nombre);
        int r = JOptionPane.showConfirmDialog(this, p, "Unirse a partida en red", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return;
        try {
            int pt = Integer.parseInt(puerto.getText().trim());
            if (pt < 1024 || pt > 65535) throw new NumberFormatException("fuera de rango");
            String host = ip.getText().trim();
            if (host.isEmpty()) throw new IllegalArgumentException("IP vacía");
            String nom = nombre.getText().trim().isEmpty() ? "Jugador" : nombre.getText().trim();
            seleccionar(ConfigPartida.cliente(host, pt, nom));
        } catch (IllegalArgumentException ex) {   // NumberFormatException también cae aquí
            JOptionPane.showMessageDialog(this, "Revisa la IP y el puerto (1024 a 65535).", "Datos no válidos", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void mostrarManual() {
        String html = "<html><body style='width:520px;font-family:sans-serif;color:#E8EEFC'>"
            + "<h2 style='color:#00E5FF'>MANUAL DEL PILOTO LOCO</h2>"
            + "<p><b style='color:#FF2BD6'>1. El objetivo:</b> llega primero a la META. Fácil, ¿no? ...Hasta que aparecen los conos.</p>"
            + "<p><b style='color:#FF2BD6'>2. Tu carril:</b> tu carro tiene 3 sub-carriles. Sube y baja para esquivar. "
            + "<b>J1: W/S · J2: ↑/↓</b> (si juegas solo, W/S o ↑/↓).</p>"
            + "<p><b style='color:#FF2BD6'>3. NITRO:</b> mantén <b>D</b> (J1) o <b>→</b> (J2) para salir disparado. "
            + "Se gasta rápido y se recarga solo cuando lo sueltas.</p>"
            + "<p><b style='color:#FF9A1F'>Ojo con los obstáculos:</b><br>"
            + "• <b>Cono</b>: te frena. Un regaño suave.<br>"
            + "• <b>Aceite</b>: ¡patinas y pierdes el control un ratito!<br>"
            + "• <b>Roca</b>: casi te detiene. Duele hasta en el alma.<br>"
            + "• <b style='color:#00E5FF'>Rayo turbo</b>: ¡sí, tómalo! Recarga tu nitro.</p>"
            + "<p><b style='color:#B6FF3B'>4. Carros:</b> Deportivo (equilibrado), Camioneta (resiste golpes), "
            + "Fórmula X (la más veloz, pero frágil) y Buggy Loco (ágil como gato).</p>"
            + "<p><b style='color:#B6FF3B'>5. En red:</b> uno crea la partida y dice su IP a los demás (hasta 4 jugadores en total). "
            + "Si alguien se desconecta, ¡la CPU se sube a su carro!</p>"
            + "<p><i>ESC vuelve al menú. Ganar no es lo único… pero se siente genial.</i></p></body></html>";
        JOptionPane.showMessageDialog(this, new JLabel(html), "Cómo jugar", JOptionPane.PLAIN_MESSAGE);
    }

    // ------------------------------------------------------------ flujo de partida
    private void seleccionar(ConfigPartida cfg) {
        mostrar(new SeleccionPanel(cfg, () -> comenzar(cfg), this::mostrarMenu));
    }

    private void comenzar(ConfigPartida cfg) {
        switch (cfg.modo) {
            case ANFITRION: abrirAnfitrion(cfg); break;
            case CLIENTE:   conectar(cfg); break;
            default:        mostrar(new CarreraPanel(this, cfg, cfg.crearCarrera(System.nanoTime()), null, null, 0));
        }
    }

    public void revancha(ConfigPartida cfg) {
        mostrar(new CarreraPanel(this, cfg, cfg.crearCarrera(System.nanoTime()), null, null, 0));
    }

    private void abrirAnfitrion(ConfigPartida cfg) {
        int esperados = cfg.slots.size() - 1;
        ServidorJuego srv = new ServidorJuego(esperados);
        EsperaPanel espera = new EsperaPanel("ESPERANDO JUGADORES", () -> { srv.cerrar(); mostrarMenu(); });
        List<String> ips = ServidorJuego.direccionesLocales();
        String direcciones = ips.isEmpty() ? "(no se detectó red)" : String.join("   ·   ", ips);
        espera.setDetalle("Jugadores conectados: 0 de " + esperados,
                "Dile a tus amigos que usen la IP:  " + direcciones,
                "Puerto:  " + cfg.puerto);
        srv.setOyente(new ServidorJuego.Oyente() {
            @Override public void clienteConectado(int conectados, int esp, String nombre) {
                SwingUtilities.invokeLater(() -> espera.setDetalle("Jugadores conectados: " + conectados + " de " + esp,
                        nombre == null ? "Un jugador se salió, seguimos esperando..." : nombre + " se unió a la partida",
                        "Tu IP:  " + direcciones + "     Puerto:  " + cfg.puerto));
            }
            @Override public void todosListos() {
                SwingUtilities.invokeLater(() -> {
                    List<ServidorJuego.ClienteRemoto> cl = srv.getClientes();
                    for (int i = 0; i < cl.size(); i++) {
                        ConfigPartida.Slot s = cfg.slots.get(i + 1);
                        s.nombre = cl.get(i).getNombre();
                        s.vehiculo = cl.get(i).getVehiculo();
                        s.remoto = cl.get(i).getControlador();
                    }
                    long semilla = System.nanoTime();
                    Carrera c = cfg.crearCarrera(semilla);
                    srv.enviarInicio(semilla, c);
                    mostrar(new CarreraPanel(VentanaPrincipal.this, cfg, c, srv, null, 0));
                });
            }
        });
        mostrar(espera);
        try {
            srv.iniciar(cfg.puerto);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "No se pudo abrir el puerto " + cfg.puerto
                    + ".\n¿Ya hay otra partida abierta?\n" + e.getMessage(), "Error de red", JOptionPane.ERROR_MESSAGE);
            mostrarMenu();
            return;
        }
    }

    private void conectar(ConfigPartida cfg) {
        ClienteJuego cli = new ClienteJuego();
        EsperaPanel espera = new EsperaPanel("CONECTANDO", () -> { cli.cerrar(); mostrarMenu(); });
        espera.setDetalle("Conectando a " + cfg.ip + ":" + cfg.puerto);
        mostrar(espera);
        ConfigPartida.Slot yo = cfg.slots.get(0);
        Thread hilo = new Thread(() -> {
            try {
                cli.conectar(cfg.ip, cfg.puerto, yo.nombre, yo.vehiculo, new ClienteJuego.Oyente() {
                    @Override public void inicio(long semilla, int miIndice, String[] nombres, int[] vehiculos) {
                        SwingUtilities.invokeLater(() -> {
                            Carrera c = ConfigPartida.crearCarreraCliente(semilla, nombres, vehiculos);
                            mostrar(new CarreraPanel(VentanaPrincipal.this, cfg, c, null, cli, miIndice));
                        });
                    }
                    @Override public void desconectado(String motivo) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(VentanaPrincipal.this, motivo, "Conexión perdida", JOptionPane.WARNING_MESSAGE);
                            mostrarMenu();
                        });
                    }
                });
                SwingUtilities.invokeLater(() -> {
                    espera.setTitulo("CONECTADO");
                    espera.setDetalle("Esperando a que el anfitrión inicie la carrera...");
                });
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "No se pudo conectar a " + cfg.ip + ":" + cfg.puerto
                            + "\n(" + e.getMessage() + ")\nRevisa la IP, que el anfitrión ya esté esperando y el firewall.",
                            "Error de conexión", JOptionPane.ERROR_MESSAGE);
                    mostrarMenu();
                });
            }
        }, "cliente-conectar");
        hilo.setDaemon(true);
        hilo.start();
    }
}

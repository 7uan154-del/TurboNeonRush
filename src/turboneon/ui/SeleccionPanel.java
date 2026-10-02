package turboneon.ui;

import turboneon.modelo.Vehiculo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class SeleccionPanel extends JPanel implements Pantalla {
    private final ConfigPartida cfg;
    private final Runnable alTerminar;
    private final List<Integer> humanos = new ArrayList<>();
    private final TarjetaVehiculo[] tarjetas = new TarjetaVehiculo[Vehiculo.TOTAL];
    private final JTextField campoNombre = new JTextField(14);
    private final BotonNeon btnSiguiente = new BotonNeon("SIGUIENTE", Tema.LIMA);
    private final Timer timer;
    private int actual;
    private int elegido;
    private double t;

    public SeleccionPanel(ConfigPartida cfg, Runnable alTerminar, Runnable alVolver) {
        this.cfg = cfg;
        this.alTerminar = alTerminar;
        for (int i = 0; i < cfg.slots.size(); i++) if (cfg.slots.get(i).tipo == ConfigPartida.Tipo.HUMANO) humanos.add(i);

        setLayout(new BorderLayout());
        setBackground(Tema.FONDO);

        JPanel norte = new JPanel();
        norte.setOpaque(false);
        norte.setPreferredSize(new Dimension(10, 150));
        add(norte, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(1, Vehiculo.TOTAL, 18, 0));
        centro.setOpaque(false);
        centro.setBorder(new EmptyBorder(6, 50, 6, 50));
        for (int i = 0; i < Vehiculo.TOTAL; i++) {
            final int id = i;
            tarjetas[i] = new TarjetaVehiculo(Vehiculo.crear(i));
            tarjetas[i].addMouseListener(new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) { elegir(id); }
            });
            centro.add(tarjetas[i]);
        }
        add(centro, BorderLayout.CENTER);

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 14));
        sur.setOpaque(false);
        JLabel lbl = new JLabel("Nombre:");
        lbl.setForeground(Tema.TEXTO);
        lbl.setFont(Tema.fuente(Font.BOLD, 15f));
        campoNombre.setFont(Tema.fuente(Font.BOLD, 16f));
        campoNombre.setBackground(Tema.PANEL);
        campoNombre.setForeground(Color.WHITE);
        campoNombre.setCaretColor(Tema.CIAN);
        campoNombre.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.CIAN, 2), new EmptyBorder(6, 8, 6, 8)));
        BotonNeon volver = new BotonNeon("VOLVER AL MENÚ", Tema.ROJO);
        volver.setPreferredSize(new Dimension(230, 46));
        btnSiguiente.setPreferredSize(new Dimension(280, 46));
        volver.addActionListener(e -> alVolver.run());
        btnSiguiente.addActionListener(e -> siguiente());
        sur.add(lbl); sur.add(campoNombre); sur.add(volver); sur.add(btnSiguiente);
        add(sur, BorderLayout.SOUTH);

        cargarJugador();
        timer = new Timer(30, e -> { t += 0.03; repaint(); });
        timer.start();
    }

    private ConfigPartida.Slot slotActual() { return cfg.slots.get(humanos.get(actual)); }

    private void cargarJugador() {
        ConfigPartida.Slot s = slotActual();
        campoNombre.setText(s.nombre);
        boolean ultimo = actual == humanos.size() - 1;
        btnSiguiente.setText(ultimo ? "¡A CORRER!" : "SIGUIENTE JUGADOR");
        Color c = turboneon.modelo.Config.COLORES[humanos.get(actual) % turboneon.modelo.Config.COLORES.length];
        for (TarjetaVehiculo tj : tarjetas) tj.setColorJugador(c);
        elegir(s.vehiculo);
    }

    private void elegir(int id) {
        elegido = id;
        for (int i = 0; i < tarjetas.length; i++) tarjetas[i].setSeleccionada(i == id);
    }

    private void siguiente() {
        ConfigPartida.Slot s = slotActual();
        String n = campoNombre.getText().trim();
        if (!n.isEmpty()) s.nombre = n.length() > 14 ? n.substring(0, 14) : n;
        s.vehiculo = elegido;
        actual++;
        if (actual >= humanos.size()) {
            cfg.completarCPU();
            alTerminar.run();
        } else {
            cargarJugador();
        }
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        Tema.calidad(g);
        int w = getWidth();
        Tema.fondoSynth(g, w, getHeight(), t);
        g.setColor(new Color(5, 3, 15, 150));
        g.fillRect(0, 0, w, getHeight());
        int idx = humanos.get(actual);
        Color c = turboneon.modelo.Config.COLORES[idx % turboneon.modelo.Config.COLORES.length];
        String titulo = humanos.size() == 1 && cfg.modo != ConfigPartida.Modo.LOCAL
                ? "ELIGE TU CARRO" : "JUGADOR " + (idx + 1) + "  ·  ELIGE TU CARRO";
        Tema.brillo(g, titulo, w / 2.0, 66, Tema.fuente(Font.BOLD | Font.ITALIC, 44f), c, true);
        g.setFont(Tema.fuente(Font.PLAIN, 16f));
        g.setColor(Tema.TEXTO);
        Tema.centrado(g, "Controles:  " + slotActual().teclas.etiqueta, w / 2.0, 102);
        g.setColor(Tema.APAGADO);
        g.setFont(Tema.fuente(Font.PLAIN, 14f));
        Tema.centrado(g, "Haz clic en un carro para elegirlo", w / 2.0, 128);
        g.dispose();
    }

    @Override public void detener() { timer.stop(); }

    /** Tarjeta con el dibujo del vehículo y sus estadísticas. */
    private class TarjetaVehiculo extends JPanel {
        private final Vehiculo v;
        private boolean sel, encima;
        private Color color = Tema.CIAN;

        TarjetaVehiculo(Vehiculo v) {
            this.v = v;
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { encima = true; }
                @Override public void mouseExited(MouseEvent e) { encima = false; }
            });
        }

        void setColorJugador(Color c) { color = c; }
        void setSeleccionada(boolean s) { sel = s; }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            Tema.calidad(g);
            int w = getWidth(), h = Math.min(getHeight(), 450);
            g.translate(0, (getHeight() - h) / 2);
            RoundRectangle2D forma = new RoundRectangle2D.Double(4, 4, w - 8, h - 8, 26, 26);
            g.setColor(Tema.alpha(Tema.PANEL, 235));
            g.fill(forma);
            if (sel) {
                g.setColor(Tema.alpha(color, 60));
                g.setStroke(new BasicStroke(9f));
                g.draw(forma);
            }
            g.setColor(sel ? color : (encima ? Tema.APAGADO : new Color(0x2A3355)));
            g.setStroke(new BasicStroke(sel ? 3f : 2f));
            g.draw(forma);

            double bob = Math.sin(t * 3 + v.getId()) * 3;
            v.dibujar(g, w / 2.0, 92 + bob, Math.min(w * 0.62, 190), color, sel, t);

            g.setFont(Tema.fuente(Font.BOLD, 22f));
            g.setColor(sel ? color : Color.WHITE);
            Tema.centrado(g, v.getNombre(), w / 2.0, 168);
            g.setFont(Tema.fuente(Font.PLAIN, 13f));
            g.setColor(Tema.APAGADO);
            Tema.parrafo(g, v.getDescripcion(), 20, 192, w - 40, 17);

            int y = 250;
            barra(g, "VELOCIDAD", v.getVelocidadMax() / 600.0, y, w);
            barra(g, "ACELERACIÓN", v.getAceleracion() / 480.0, y + 36, w);
            barra(g, "MANEJO", v.getManejo() / 10.0, y + 72, w);
            barra(g, "RESISTENCIA", Math.min(1, (1 / v.getResistencia()) / 1.9), y + 108, w);
            g.dispose();
        }

        private void barra(Graphics2D g, String nombre, double valor, int y, int w) {
            g.setFont(Tema.fuente(Font.BOLD, 11f));
            g.setColor(Tema.APAGADO);
            g.drawString(nombre, 22, y);
            int bw = w - 44;
            g.setColor(new Color(0x1E2645));
            g.fillRoundRect(22, y + 6, bw, 10, 10, 10);
            g.setColor(color);
            g.fillRoundRect(22, y + 6, (int) (bw * Math.max(0.05, Math.min(1, valor))), 10, 10, 10);
        }
    }
}

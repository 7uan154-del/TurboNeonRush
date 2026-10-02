package turboneon.ui;

import turboneon.modelo.*;
import turboneon.red.ClienteJuego;
import turboneon.red.ServidorJuego;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.List;

public class CarreraPanel extends JPanel implements Pantalla {
    private static final Color ORO = new Color(0xFFD84A), PLATA = new Color(0xC8CCD8), BRONCE = new Color(0xFF9A55);

    private static class Entrada {
        ControladorHumano h; TeclasJugador teclas; Corredor r;
    }

    private final VentanaPrincipal ventana;
    private final ConfigPartida cfg;
    private final Carrera carrera;
    private final ServidorJuego servidor;
    private final ClienteJuego cliente;
    private final List<Entrada> entradas = new ArrayList<>();
    private final Set<Integer> pulsadas = new HashSet<>();
    private final String[] etiquetas;
    private final Timer timer;
    private long ultimo = System.nanoTime();
    private double t;
    private String aviso = "";
    private double avisoT;

    public CarreraPanel(VentanaPrincipal ventana, ConfigPartida cfg, Carrera carrera,
                        ServidorJuego servidor, ClienteJuego cliente, int miIndice) {
        this.ventana = ventana; this.cfg = cfg; this.carrera = carrera;
        this.servidor = servidor; this.cliente = cliente;
        setBackground(Tema.FONDO);
        setFocusable(true);

        List<Corredor> cs = carrera.getCorredores();
        etiquetas = new String[cs.size()];
        if (cliente != null) {
            Entrada e = new Entrada();
            e.h = new ControladorHumano(); e.teclas = cfg.slots.get(0).teclas; e.r = cs.get(miIndice);
            entradas.add(e);
        } else {
            for (int i = 0; i < cfg.slots.size(); i++) {
                if (cfg.slots.get(i).tipo != ConfigPartida.Tipo.HUMANO) continue;
                Entrada e = new Entrada();
                e.h = (ControladorHumano) cs.get(i).getControlador();
                e.teclas = cfg.slots.get(i).teclas; e.r = cs.get(i);
                entradas.add(e);
            }
        }
        for (int i = 0; i < cs.size(); i++) {
            etiquetas[i] = cs.get(i).getControlador() instanceof ControladorCPU ? "CPU" : "ONLINE";
        }
        for (Entrada e : entradas) etiquetas[e.r.getIndice()] = e.teclas.etiqueta;

        if (servidor != null) {
            servidor.setOyente(new ServidorJuego.Oyente() {
                @Override public void clienteDesconectado(ServidorJuego.ClienteRemoto c) {
                    SwingUtilities.invokeLater(() -> {
                        Corredor r = carrera.getCorredores().get(c.getIndice());
                        r.setControlador(new ControladorCPU(0.8, System.nanoTime()));
                        etiquetas[c.getIndice()] = "CPU";
                        aviso = r.getNombre() + " se desconectó. ¡La CPU toma su lugar!";
                        avisoT = 4;
                    });
                }
            });
        }
        if (cliente != null) {
            cliente.setAlEstado(s -> SwingUtilities.invokeLater(() -> s.aplicar(carrera)));
            cliente.setAlDesconectar(msg -> SwingUtilities.invokeLater(() -> {
                detener();
                JOptionPane.showMessageDialog(ventana, msg, "Conexión perdida", JOptionPane.WARNING_MESSAGE);
                ventana.mostrarMenu();
            }));
        }

        addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (pulsadas.add(e.getKeyCode())) procesarTecla(e.getKeyCode(), true);
            }
            @Override public void keyReleased(KeyEvent e) {
                pulsadas.remove(e.getKeyCode());
                procesarTecla(e.getKeyCode(), false);
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { requestFocusInWindow(); }
        });

        timer = new Timer(15, e -> paso());
        timer.start();
    }

    private void procesarTecla(int k, boolean abajo) {
        if (abajo) {
            if (k == KeyEvent.VK_ESCAPE) { ventana.mostrarMenu(); return; }
            if (carrera.getEstado() == Carrera.Estado.TERMINADA) {
                if (k == KeyEvent.VK_ENTER) { ventana.mostrarMenu(); return; }
                if (k == KeyEvent.VK_R && !cfg.esRed()) { ventana.revancha(cfg); return; }
            }
        }
        for (Entrada e : entradas) {
            if (abajo && TeclasJugador.contiene(e.teclas.arriba, k)) e.h.arriba();
            if (abajo && TeclasJugador.contiene(e.teclas.abajo, k)) e.h.abajo();
            boolean nitro = false;
            for (int n : e.teclas.nitro) if (pulsadas.contains(n)) nitro = true;
            e.h.setNitro(nitro);
        }
    }

    private void paso() {
        long ahora = System.nanoTime();
        double dt = Math.min(0.05, (ahora - ultimo) / 1e9);
        ultimo = ahora;
        t += dt;
        if (cliente == null) {
            carrera.actualizar(dt);
            if (servidor != null) servidor.enviarEstado(carrera);
        } else {
            for (Entrada e : entradas) {
                e.h.actualizar(e.r, dt, null);
                cliente.enviarInput(e.h.getDeseado(), e.h.isNitro());
            }
            for (Corredor r : carrera.getCorredores()) r.tickVisual(dt);
        }
        if (avisoT > 0) avisoT -= dt;
        repaint();
    }

    // ------------------------------------------------------------------ dibujo
    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        Tema.calidad(g);
        int W = getWidth(), H = getHeight();
        g.setPaint(new GradientPaint(0, 0, new Color(0x0A0D1A), 0, H, new Color(0x160B2B)));
        g.fillRect(0, 0, W, H);
        cabecera(g, W);

        List<Corredor> cs = carrera.getCorredores();
        int n = cs.size(), m = 14, top = 72;
        int bandH = (H - top - m - (n - 1) * m) / n;
        List<Corredor> orden = carrera.clasificacion();
        for (int i = 0; i < n; i++) {
            banda(g, cs.get(i), orden.indexOf(cs.get(i)) + 1, n, m, top + i * (bandH + m), W - 2 * m, bandH);
        }
        superposiciones(g, W, H, orden);
        g.dispose();
    }

    private void cabecera(Graphics2D g, int W) {
        Tema.brillo(g, "TURBO NEÓN RUSH", 18, 44, Tema.fuente(Font.BOLD | Font.ITALIC, 26f), Tema.CIAN, false);
        int x0 = 340, x1 = W - 210, y = 38;
        g.setColor(new Color(255, 255, 255, 40));
        g.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(x0, y, x1, y);
        g.setStroke(new BasicStroke(1f));
        for (int i = 0; i < 6; i++) for (int j = 0; j < 2; j++) {
            g.setColor((i + j) % 2 == 0 ? Color.WHITE : Color.BLACK);
            g.fillRect(x1 + 12 + i * 5, y - 10 + j * 10, 5, 10);
        }
        for (Corredor r : carrera.getCorredores()) {
            double p = Math.max(0, Math.min(1, r.getX() / carrera.getPista().getLongitud()));
            double px = x0 + (x1 - x0) * p;
            g.setColor(Tema.alpha(r.getColor(), 70));
            g.fill(new Ellipse2D.Double(px - 16, y - 16, 32, 32));
            g.setColor(r.getColor());
            g.fill(new Ellipse2D.Double(px - 11, y - 11, 22, 22));
            g.setColor(Color.BLACK);
            g.setFont(Tema.fuente(Font.BOLD, 13f));
            Tema.centrado(g, String.valueOf(r.getIndice() + 1), px, y + 5);
        }
        g.setFont(Tema.mono(Font.BOLD, 24f));
        g.setColor(Color.WHITE);
        String s = formato(carrera.getTiempo());
        g.drawString(s, W - 18 - g.getFontMetrics().stringWidth(s), 46);
    }

    private void banda(Graphics2D g0, Corredor r, int pos, int n, int x, int y, int w, int h) {
        Graphics2D g = (Graphics2D) g0.create();
        Color col = r.getColor();
        Shape forma = new RoundRectangle2D.Double(x, y, w, h, 26, 26);
        g.setColor(new Color(0x0D1222));
        g.fill(forma);
        g.setClip(forma);

        double pad = h < 170 ? 26 : 30, roadY = y + pad, roadH = h - 2 * pad, curb = 6;
        double laneH = (roadH - 2 * curb) / 3.0, laneY0 = roadY + curb;
        double carPx = x + w * 0.20;
        double L = carrera.getPista().getLongitud();
        double cx = r.getX();

        // asfalto
        g.setPaint(new GradientPaint(0, (float) roadY, new Color(0x1B2138), 0, (float) (roadY + roadH), new Color(0x121728)));
        g.fill(new Rectangle2D.Double(x, roadY, w, roadH));

        // pianos rojo/blanco
        double base = Math.floor((cx - (carPx - x)) / 24) * 24;
        for (int k = 0; k < w / 24 + 3; k++) {
            double wx = base + k * 24;
            double sx = carPx + (wx - cx);
            g.setColor((((long) Math.floor(wx / 24)) & 1) == 0 ? new Color(0xE5324B) : new Color(0xEDEFF7));
            g.fill(new Rectangle2D.Double(sx, roadY, 24, curb));
            g.fill(new Rectangle2D.Double(sx, roadY + roadH - curb, 24, curb));
        }
        // líneas discontinuas de carril
        double baseD = Math.floor((cx - (carPx - x)) / 52) * 52;
        g.setColor(new Color(255, 255, 255, 90));
        for (int k = 1; k <= 2; k++) {
            double ly = laneY0 + laneH * k;
            for (int d = 0; d < w / 52 + 3; d++) {
                g.fill(new Rectangle2D.Double(carPx + (baseD + d * 52 - cx), ly - 1.5, 30, 3));
            }
        }
        // salida y meta
        double sxSalida = carPx - cx;
        if (sxSalida > x - 10 && sxSalida < x + w) {
            g.setColor(new Color(255, 255, 255, 200));
            g.fill(new Rectangle2D.Double(sxSalida - 3, roadY, 6, roadH));
        }
        double sxMeta = carPx + (L - cx);
        if (sxMeta > x - 80 && sxMeta < x + w + 20) {
            g.setColor(Tema.alpha(Tema.LIMA, 50));
            g.fill(new Rectangle2D.Double(sxMeta - 90, roadY, 90, roadH));
            double cell = roadH / 9.0;
            for (int c = 0; c < 4; c++) for (int f = 0; f < 9; f++) {
                g.setColor((c + f) % 2 == 0 ? Color.WHITE : new Color(0x111111));
                g.fill(new Rectangle2D.Double(sxMeta + c * cell, roadY + f * cell, cell, cell));
            }
            g.setFont(Tema.fuente(Font.BOLD | Font.ITALIC, 22f));
            g.setColor(Tema.LIMA);
            g.drawString("META", (float) (sxMeta - 80), (float) (roadY + roadH / 2 + 8));
        }
        // líneas de velocidad
        double vel = r.getVelocidad();
        for (int i = 0; i < 9; i++) {
            double sx = x + w - ((cx * 1.7 + i * 173) % (w + 160));
            double sy = roadY + curb + (((i * 53) % 100) / 100.0) * (roadH - 2 * curb);
            g.setColor(new Color(255, 255, 255, (int) Math.min(60, vel / 10.0)));
            g.fill(new Rectangle2D.Double(sx, sy, 30 + (i % 3) * 22 + (r.isNitroActivo() ? 40 : 0), 1.6));
        }
        // obstáculos
        for (Obstaculo o : carrera.getPista().getObstaculos()) {
            double sx = carPx + (o.getX() - cx);
            if (sx < x - 70) continue;
            if (sx > x + w + 70) break;
            o.dibujar(g, sx, laneY0 + (o.getCarril() + 0.5) * laneH, laneH * 0.62, t);
        }
        // el carro
        double cy = laneY0 + (r.getCarrilY() + 0.5) * laneH;
        double cw = Math.min(160, laneH * 1.75);
        Graphics2D gc = (Graphics2D) g.create();
        if (r.getStun() > 0) gc.rotate(Math.sin(t * 38) * 0.15, carPx, cy);
        r.getVehiculo().dibujar(gc, carPx, cy, cw, col, r.isNitroActivo(), t);
        gc.dispose();
        if (r.getMensajeT() > 0) {
            float a = (float) Math.min(1, r.getMensajeT() / 0.5);
            double rise = (1.2 - r.getMensajeT()) * 26;
            Color mc = r.getUltimoTipo() == 4 ? Tema.CIAN : Tema.NARANJA;
            Tema.brillo(g, r.getMensaje(), carPx + cw * 0.2, cy - cw * 0.3 - rise, Tema.fuente(Font.BOLD | Font.ITALIC, 22f),
                    Tema.alpha(mc, (int) (255 * a)), true);
        }

        // HUD
        g.setClip(null);
        g.setFont(Tema.fuente(Font.BOLD, 16f));
        g.setColor(col);
        g.drawString(r.getNombre().toUpperCase(), x + 18, (float) (y + 21));
        int nw = g.getFontMetrics().stringWidth(r.getNombre().toUpperCase());
        g.setFont(Tema.fuente(Font.PLAIN, 12f));
        g.setColor(Tema.APAGADO);
        g.drawString(r.getVehiculo().getNombre() + "  ·  " + etiquetas[r.getIndice()], x + 18 + nw + 14, (float) (y + 21));
        g.setFont(Tema.fuente(Font.BOLD | Font.ITALIC, 22f));
        Color cp = pos == 1 ? ORO : pos == 2 ? PLATA : BRONCE;
        String ps = "#" + pos + " de " + n;
        g.setColor(cp);
        g.drawString(ps, x + w - 20 - g.getFontMetrics().stringWidth(ps), (float) (y + 23));
        g.setFont(Tema.mono(Font.BOLD, 15f));
        g.setColor(Color.WHITE);
        g.drawString(String.format(Locale.ROOT, "%3d km/h", (int) (vel * 0.36)), x + 18, (float) (y + h - 9));
        g.setFont(Tema.fuente(Font.BOLD, 11f));
        g.setColor(Tema.APAGADO);
        g.drawString("NITRO", x + 140, (float) (y + h - 9));
        g.setColor(new Color(0x1E2645));
        g.fillRoundRect(x + 186, y + h - 19, 150, 10, 10, 10);
        g.setColor(r.isNitroActivo() ? Tema.NARANJA : Tema.CIAN);
        g.fillRoundRect(x + 186, y + h - 19, (int) (150 * r.getNitro() / 100.0), 10, 10, 10);
        if (r.getStun() > 0) {
            g.setFont(Tema.fuente(Font.BOLD, 13f));
            g.setColor(Tema.ROJO);
            g.drawString("SIN CONTROL", x + 360, (float) (y + h - 9));
        }
        // borde de neón
        g.setColor(Tema.alpha(col, 50));
        g.setStroke(new BasicStroke(6f));
        g.draw(forma);
        g.setColor(Tema.alpha(col, 200));
        g.setStroke(new BasicStroke(2f));
        g.draw(forma);
        g.dispose();
    }

    private void superposiciones(Graphics2D g, int W, int H, List<Corredor> orden) {
        Carrera.Estado est = carrera.getEstado();
        if (est == Carrera.Estado.CUENTA_ATRAS) {
            g.setColor(new Color(0, 0, 0, 110));
            g.fillRect(0, 72, W, H - 72);
            int num = (int) Math.ceil(carrera.getCuenta());
            double frac = carrera.getCuenta() - (num - 1);
            float tam = (float) (150 * (0.8 + 0.4 * frac));
            Tema.brillo(g, String.valueOf(Math.max(1, num)), W / 2.0, H / 2.0 + 50, Tema.fuente(Font.BOLD | Font.ITALIC, tam),
                    Tema.alpha(Tema.CIAN, (int) (255 * Math.min(1, frac * 3))), true);
            g.setFont(Tema.fuente(Font.PLAIN, 18f));
            g.setColor(Tema.TEXTO);
            Tema.centrado(g, "Esquiva conos, aceite y rocas  ·  Recoge los rayos para recargar el NITRO", W / 2.0, H / 2.0 + 100);
        } else if (est == Carrera.Estado.EN_CURSO && carrera.getTiempo() < 0.9) {
            Tema.brillo(g, "¡YA!", W / 2.0, H / 2.0 + 40, Tema.fuente(Font.BOLD | Font.ITALIC, 120f), Tema.LIMA, true);
        } else if (est == Carrera.Estado.TERMINADA) {
            int n = orden.size();
            int bw = 560, bh = 170 + n * 52;
            int bx = (W - bw) / 2, by = Math.max(80, (H - bh) / 2);
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 72, W, H - 72);
            RoundRectangle2D caja = new RoundRectangle2D.Double(bx, by, bw, bh, 30, 30);
            g.setColor(Tema.alpha(Tema.PANEL, 245));
            g.fill(caja);
            g.setColor(Tema.CIAN);
            g.setStroke(new BasicStroke(3f));
            g.draw(caja);
            g.setStroke(new BasicStroke(1f));
            Tema.brillo(g, "¡META!", W / 2.0, by + 58, Tema.fuente(Font.BOLD | Font.ITALIC, 48f), Tema.LIMA, true);
            g.setFont(Tema.fuente(Font.BOLD, 18f));
            g.setColor(Tema.TEXTO);
            Tema.centrado(g, "¡Gana " + orden.get(0).getNombre() + "!", W / 2.0, by + 90);
            for (int i = 0; i < n; i++) {
                Corredor r = orden.get(i);
                int yy = by + 130 + i * 52;
                g.setColor(Tema.alpha(r.getColor(), 40));
                g.fillRoundRect(bx + 24, yy - 28, bw - 48, 42, 16, 16);
                g.setFont(Tema.fuente(Font.BOLD | Font.ITALIC, 26f));
                g.setColor(i == 0 ? ORO : i == 1 ? PLATA : BRONCE);
                g.drawString("#" + (i + 1), bx + 40, yy);
                g.setFont(Tema.fuente(Font.BOLD, 20f));
                g.setColor(r.getColor());
                g.drawString(r.getNombre(), bx + 110, yy);
                g.setFont(Tema.mono(Font.BOLD, 20f));
                g.setColor(Color.WHITE);
                String ts = r.isTerminado() ? formato(r.getTiempoFin()) : "-- sin llegar --";
                g.drawString(ts, bx + bw - 40 - g.getFontMetrics().stringWidth(ts), yy);
            }
            g.setFont(Tema.fuente(Font.PLAIN, 15f));
            g.setColor(Tema.APAGADO);
            Tema.centrado(g, cfg.esRed() ? "ENTER: volver al menú" : "R: revancha   ·   ENTER: volver al menú",
                    W / 2.0, by + bh - 20);
        }
        if (avisoT > 0) {
            g.setFont(Tema.fuente(Font.BOLD, 18f));
            int tw = g.getFontMetrics().stringWidth(aviso);
            g.setColor(new Color(0, 0, 0, 200));
            g.fillRoundRect((W - tw) / 2 - 20, H - 70, tw + 40, 40, 20, 20);
            g.setColor(Tema.NARANJA);
            Tema.centrado(g, aviso, W / 2.0, H - 44);
        }
    }

    private static String formato(double s) {
        int m = (int) (s / 60);
        return String.format(Locale.ROOT, "%02d:%05.2f", m, s - 60 * m);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    @Override
    public void detener() {
        timer.stop();
        if (servidor != null) servidor.cerrar();
        if (cliente != null) cliente.cerrar();
    }
}

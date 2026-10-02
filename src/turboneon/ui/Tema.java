package turboneon.ui;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public final class Tema {
    private Tema() {}

    public static final Color FONDO = new Color(0x080B14);
    public static final Color PANEL = new Color(0x111830);
    public static final Color CIAN = new Color(0x00E5FF);
    public static final Color MAGENTA = new Color(0xFF2BD6);
    public static final Color LIMA = new Color(0xB6FF3B);
    public static final Color NARANJA = new Color(0xFF9A1F);
    public static final Color ROJO = new Color(0xFF4D5E);
    public static final Color TEXTO = new Color(0xE8EEFC);
    public static final Color APAGADO = new Color(0x8D9AB8);

    public static Font fuente(int estilo, float tam) {
        return new Font(Font.SANS_SERIF, estilo, 12).deriveFont(estilo, tam);
    }

    public static Font mono(int estilo, float tam) {
        return new Font(Font.MONOSPACED, estilo, 12).deriveFont(estilo, tam);
    }

    public static Color alpha(Color c, int a) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.max(0, Math.min(255, a)));
    }

    public static void calidad(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    public static void centrado(Graphics2D g, String s, double cx, double y) {
        FontMetrics fm = g.getFontMetrics();
        g.drawString(s, (float) (cx - fm.stringWidth(s) / 2.0), (float) y);
    }

    public static void brillo(Graphics2D g, String s, double x, double y, Font f, Color c, boolean centrado) {
        g.setFont(f);
        double px = x;
        if (centrado) px = x - g.getFontMetrics().stringWidth(s) / 2.0;
        for (int r = 6; r >= 2; r -= 2) {
            g.setColor(alpha(c, 22));
            for (int a = 0; a < 8; a++) {
                double ang = a * Math.PI / 4;
                g.drawString(s, (float) (px + Math.cos(ang) * r), (float) (y + Math.sin(ang) * r));
            }
        }
        g.setColor(c);
        g.drawString(s, (float) px, (float) y);
    }

    public static int parrafo(Graphics2D g, String s, int x, int y, int ancho, int interlineado) {
        FontMetrics fm = g.getFontMetrics();
        StringBuilder linea = new StringBuilder();
        for (String palabra : s.split(" ")) {
            if (fm.stringWidth(linea + palabra) > ancho && linea.length() > 0) {
                g.drawString(linea.toString(), x, y);
                y += interlineado;
                linea.setLength(0);
            }
            linea.append(palabra).append(' ');
        }
        g.drawString(linea.toString(), x, y);
        return y;
    }

    public static void fondoSynth(Graphics2D g, int w, int h, double t) {
        int hor = (int) (h * 0.58);
        g.setPaint(new GradientPaint(0, 0, new Color(0x05030F), 0, hor, new Color(0x3A0F55)));
        g.fill(new Rectangle2D.Double(0, 0, w, hor));
        double r = Math.min(w, h) * 0.20;
        double cx = w / 2.0, cy = hor - r * 0.35;
        g.setPaint(new GradientPaint(0, (float) (cy - r), new Color(0xFFD84A), 0, (float) (cy + r), new Color(0xFF2BD6)));
        g.fill(new Ellipse2D.Double(cx - r, cy - r, r * 2, r * 2));
        g.setColor(new Color(0x2A0B45));
        for (int i = 0; i < 7; i++) {
            double yy = cy + r * 0.05 + i * r * 0.14;
            g.fill(new Rectangle2D.Double(cx - r, yy, r * 2, 1.5 + i * 1.6));
        }
        g.setPaint(new GradientPaint(0, hor, new Color(0x16062B), 0, h, new Color(0x05030F)));
        g.fill(new Rectangle2D.Double(0, hor, w, h - hor));
        g.setColor(alpha(MAGENTA, 140));
        g.setStroke(new BasicStroke(1.4f));
        for (int i = -24; i <= 24; i++) g.drawLine(w / 2, hor, (int) (w / 2.0 + i * w * 0.10), h);
        int n = 14;
        for (int k = 0; k < n; k++) {
            double z = ((k + (t * 0.7) % 1.0) / n);
            int y = (int) (hor + (h - hor) * z * z);
            g.setColor(alpha(MAGENTA, (int) (40 + 150 * z)));
            g.drawLine(0, y, w, y);
        }
        g.setColor(alpha(CIAN, 200));
        g.setStroke(new BasicStroke(2f));
        g.drawLine(0, hor, w, hor);
        g.setStroke(new BasicStroke(1f));
    }
}

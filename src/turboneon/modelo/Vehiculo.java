package turboneon.modelo;

import java.awt.*;
import java.awt.geom.*;

public abstract class Vehiculo {
    public static final int TOTAL = 4;

    public static Vehiculo crear(int id) {
        switch (Math.floorMod(id, TOTAL)) {
            case 0:  return new Deportivo();
            case 1:  return new Camioneta();
            case 2:  return new Formula();
            default: return new Buggy();
        }
    }

    private final int id;

    protected Vehiculo(int id) { this.id = id; }

    public int getId() { return id; }
    public abstract String getNombre();
    public abstract String getDescripcion();
    public abstract double getVelocidadMax();
    public abstract double getAceleracion();
    public abstract double getManejo();
    public abstract double getResistencia();
    protected abstract void dibujarCarroceria(Graphics2D g, Color acento);

    public final void dibujar(Graphics2D g0, double cx, double cy, double ancho,
                              Color acento, boolean nitro, double t) {
        Graphics2D g = (Graphics2D) g0.create();
        g.translate(cx, cy);
        double s = ancho / 100.0;
        g.scale(s, s);
        g.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 40));
        g.fill(new Ellipse2D.Double(-66, -32, 132, 64));
        g.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 60));
        g.fill(new Ellipse2D.Double(-58, -27, 116, 54));
        if (nitro) dibujarLlama(g, t);
        dibujarCarroceria(g, acento);
        g.dispose();
    }

    private void dibujarLlama(Graphics2D g, double t) {
        double len = 40 + 14 * Math.sin(t * 50) + 6 * Math.sin(t * 83);
        Path2D.Double p = new Path2D.Double();
        p.moveTo(-46, -8); p.lineTo(-46 - len, 0); p.lineTo(-46, 8); p.closePath();
        g.setPaint(new GradientPaint(-46f, 0f, new Color(255, 230, 120), (float) (-46 - len), 0f, new Color(255, 80, 0, 0)));
        g.fill(p);
        double l2 = len * 0.55;
        Path2D.Double q = new Path2D.Double();
        q.moveTo(-46, -4); q.lineTo(-46 - l2, 0); q.lineTo(-46, 4); q.closePath();
        g.setPaint(new GradientPaint(-46f, 0f, Color.WHITE, (float) (-46 - l2), 0f, new Color(80, 200, 255, 0)));
        g.fill(q);
    }

    protected static void rueda(Graphics2D g, double cx, double cy, double w, double h) {
        g.setColor(new Color(0x0A0A10));
        g.fill(new RoundRectangle2D.Double(cx - w / 2, cy - h / 2, w, h, 4, 4));
        g.setColor(new Color(0x3A3F55));
        g.fill(new RoundRectangle2D.Double(cx - w / 2 + 3, cy - h / 2 + 1.5, w - 6, h - 3, 2, 2));
    }

    protected static Color claro(Color c, double f) {
        return new Color(Math.min(255, (int) (c.getRed() + (255 - c.getRed()) * f)),
                         Math.min(255, (int) (c.getGreen() + (255 - c.getGreen()) * f)),
                         Math.min(255, (int) (c.getBlue() + (255 - c.getBlue()) * f)));
    }

    protected static Color oscuro(Color c, double f) {
        return new Color((int) (c.getRed() * f), (int) (c.getGreen() * f), (int) (c.getBlue() * f));
    }

    protected static void faros(Graphics2D g, double x, double y1, double y2) {
        g.setColor(new Color(255, 245, 160));
        g.fill(new Ellipse2D.Double(x - 3, y1 - 2.5, 6, 5));
        g.fill(new Ellipse2D.Double(x - 3, y2 - 2.5, 6, 5));
    }
}

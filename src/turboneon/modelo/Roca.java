package turboneon.modelo;

import java.awt.*;
import java.awt.geom.*;

public class Roca extends Obstaculo {
    private static final double[] RADIOS = { 0.50, 0.42, 0.52, 0.40, 0.55, 0.44, 0.50, 0.41 };

    public Roca(double x, int carril) { super(x, carril); }
    @Override public int getTipo() { return 3; }

    @Override
    public void aplicar(Corredor c) {
        c.reducirVelocidad(0.65);
        c.aturdir(0.5);
        c.registrarGolpe(getTipo());
    }

    @Override
    public void dibujar(Graphics2D g, double cx, double cy, double tam, double t) {
        Path2D.Double p = new Path2D.Double();
        for (int i = 0; i < RADIOS.length; i++) {
            double a = i * Math.PI * 2 / RADIOS.length;
            double r = RADIOS[i] * tam * 1.05;
            double px = cx + Math.cos(a) * r, py = cy + Math.sin(a) * r;
            if (i == 0) p.moveTo(px, py); else p.lineTo(px, py);
        }
        p.closePath();
        g.setColor(new Color(0, 0, 0, 70));
        g.fill(new Ellipse2D.Double(cx - tam * 0.62, cy - tam * 0.5, tam * 1.24, tam * 1.1));
        g.setPaint(new GradientPaint((float) (cx - tam * 0.4), (float) (cy - tam * 0.4), new Color(0x9AA1B5),
                (float) (cx + tam * 0.4), (float) (cy + tam * 0.4), new Color(0x3B4054)));
        g.fill(p);
        g.setColor(new Color(0x23273A));
        g.setStroke(new BasicStroke(2f));
        g.draw(p);
        g.setColor(new Color(0x23273A));
        g.draw(new Line2D.Double(cx - tam * 0.1, cy - tam * 0.3, cx + tam * 0.05, cy));
        g.draw(new Line2D.Double(cx + tam * 0.05, cy, cx + tam * 0.25, cy + tam * 0.2));
        g.setStroke(new BasicStroke(1f));
    }
}

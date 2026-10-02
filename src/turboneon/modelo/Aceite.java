package turboneon.modelo;

import java.awt.*;
import java.awt.geom.*;

public class Aceite extends Obstaculo {
    public Aceite(double x, int carril) { super(x, carril); }
    @Override public int getTipo() { return 2; }

    @Override
    public void aplicar(Corredor c) {
        c.reducirVelocidad(0.15);
        c.aturdir(0.75);
        c.registrarGolpe(getTipo());
    }

    @Override
    public void dibujar(Graphics2D g, double cx, double cy, double tam, double t) {
        double w = tam * 1.5, h = tam * 0.95;
        g.setColor(new Color(0x07060C));
        g.fill(new Ellipse2D.Double(cx - w / 2, cy - h / 2, w, h));
        float fase = (float) (0.5 + 0.5 * Math.sin(t * 3 + cx * 0.01));
        g.setPaint(new GradientPaint((float) (cx - w / 2), (float) cy, new Color(120, 60, 255, 120),
                (float) (cx + w / 2), (float) cy, new Color(0, 229, 255, (int) (60 + 90 * fase))));
        g.fill(new Ellipse2D.Double(cx - w * 0.35, cy - h * 0.3, w * 0.7, h * 0.6));
        g.setColor(new Color(255, 255, 255, 70));
        g.fill(new Ellipse2D.Double(cx - w * 0.2, cy - h * 0.25, w * 0.22, h * 0.16));
    }
}

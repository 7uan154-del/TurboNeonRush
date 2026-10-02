package turboneon.modelo;

import java.awt.*;
import java.awt.geom.*;

public class Turbo extends Obstaculo {
    public Turbo(double x, int carril) { super(x, carril); }
    @Override public int getTipo() { return 4; }
    @Override public boolean esBonus() { return true; }

    @Override
    public void aplicar(Corredor c) {
        c.agregarNitro(40);
        c.impulso(90);
        c.registrarGolpe(getTipo());
    }

    @Override
    public void dibujar(Graphics2D g, double cx, double cy, double tam, double t) {
        double r = tam * 0.5 * (1 + 0.1 * Math.sin(t * 6));
        g.setColor(new Color(0, 229, 255, 45));
        g.fill(new Ellipse2D.Double(cx - r * 1.5, cy - r * 1.5, r * 3, r * 3));
        g.setColor(new Color(0, 229, 255));
        g.setStroke(new BasicStroke(3f));
        g.draw(new Ellipse2D.Double(cx - r, cy - r, r * 2, r * 2));
        g.setStroke(new BasicStroke(1f));
        Path2D.Double rayo = new Path2D.Double();
        rayo.moveTo(cx + r * 0.15, cy - r * 0.75); rayo.lineTo(cx - r * 0.45, cy + r * 0.1);
        rayo.lineTo(cx - r * 0.05, cy + r * 0.1); rayo.lineTo(cx - r * 0.2, cy + r * 0.75);
        rayo.lineTo(cx + r * 0.45, cy - r * 0.15); rayo.lineTo(cx + r * 0.05, cy - r * 0.15);
        rayo.closePath();
        g.setColor(new Color(255, 230, 60));
        g.fill(rayo);
    }
}

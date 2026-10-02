package turboneon.modelo;

import java.awt.*;
import java.awt.geom.*;

public class Cono extends Obstaculo {
    public Cono(double x, int carril) { super(x, carril); }
    @Override public int getTipo() { return 1; }

    @Override
    public void aplicar(Corredor c) {
        c.reducirVelocidad(0.40);
        c.registrarGolpe(getTipo());
    }

    @Override
    public void dibujar(Graphics2D g, double cx, double cy, double tam, double t) {
        double s = tam * 0.8;
        g.setColor(new Color(255, 120, 0, 50));
        g.fill(new Ellipse2D.Double(cx - s * 0.9, cy - s * 0.9, s * 1.8, s * 1.8));
        g.setColor(new Color(0x1B1F2E));
        g.fill(new RoundRectangle2D.Double(cx - s / 2, cy - s / 2, s, s, 6, 6));
        g.setColor(new Color(0xFF6A00));
        g.fill(new Ellipse2D.Double(cx - s * 0.42, cy - s * 0.42, s * 0.84, s * 0.84));
        g.setColor(Color.WHITE);
        g.fill(new Ellipse2D.Double(cx - s * 0.28, cy - s * 0.28, s * 0.56, s * 0.56));
        g.setColor(new Color(0xFF6A00));
        g.fill(new Ellipse2D.Double(cx - s * 0.15, cy - s * 0.15, s * 0.3, s * 0.3));
    }
}

package turboneon.modelo;

import java.awt.*;
import java.awt.geom.*;

public class Buggy extends Vehiculo {
    public Buggy() { super(3); }
    @Override public String getNombre() { return "BUGGY LOCO"; }
    @Override public String getDescripcion() { return "Pequeño, saltarín y muy ágil. Esquiva hasta su sombra."; }
    @Override public double getVelocidadMax() { return 500; }
    @Override public double getAceleracion() { return 460; }
    @Override public double getManejo() { return 9.0; }
    @Override public double getResistencia() { return 1.0; }

    @Override
    protected void dibujarCarroceria(Graphics2D g, Color a) {
        rueda(g, -28, -19, 24, 10); rueda(g, -28, 19, 24, 10);
        rueda(g, 28, -19, 24, 10);  rueda(g, 28, 19, 24, 10);
        g.setPaint(new GradientPaint(0, -12, claro(a, 0.35), 0, 12, oscuro(a, 0.6)));
        g.fill(new RoundRectangle2D.Double(-38, -13, 76, 26, 14, 14));
        g.setColor(new Color(255, 212, 0, 210));
        g.fill(new Rectangle2D.Double(-34, -3, 68, 6));
        g.setColor(new Color(0x12162A));
        g.fill(new RoundRectangle2D.Double(-12, -8, 16, 16, 4, 4));
        g.setColor(new Color(0xC8CCD8));
        g.setStroke(new BasicStroke(2.4f));
        g.draw(new Rectangle2D.Double(-16, -11, 24, 22));
        g.draw(new Line2D.Double(-16, -11, 8, 11));
        g.draw(new Line2D.Double(8, -11, -16, 11));
        g.setStroke(new BasicStroke(1f));
        g.setColor(new Color(255, 245, 160));
        g.fill(new Ellipse2D.Double(32, -9, 7, 7)); g.fill(new Ellipse2D.Double(32, 2, 7, 7));
        g.setColor(new Color(255, 40, 60));
        g.fill(new Rectangle2D.Double(-38, -9, 3, 5)); g.fill(new Rectangle2D.Double(-38, 4, 3, 5));
    }
}

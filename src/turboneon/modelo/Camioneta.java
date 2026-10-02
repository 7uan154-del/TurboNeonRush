package turboneon.modelo;

import java.awt.*;
import java.awt.geom.*;

public class Camioneta extends Vehiculo {
    public Camioneta() { super(1); }
    @Override public String getNombre() { return "CAMIONETA"; }
    @Override public String getDescripcion() { return "Un tanque con ruedas. Los obstáculos le hacen cosquillas."; }
    @Override public double getVelocidadMax() { return 470; }
    @Override public double getAceleracion() { return 300; }
    @Override public double getManejo() { return 5.0; }
    @Override public double getResistencia() { return 0.55; }

    @Override
    protected void dibujarCarroceria(Graphics2D g, Color a) {
        rueda(g, -30, -19, 24, 9); rueda(g, -30, 19, 24, 9);
        rueda(g, 30, -19, 24, 9);  rueda(g, 30, 19, 24, 9);
        g.setPaint(new GradientPaint(0, -18, claro(a, 0.3), 0, 18, oscuro(a, 0.6)));
        g.fill(new RoundRectangle2D.Double(-50, -18, 100, 36, 10, 10));
        g.setColor(oscuro(a, 0.35));
        g.fill(new RoundRectangle2D.Double(-46, -14, 52, 28, 4, 4));
        g.setColor(new Color(255, 255, 255, 60));
        for (int i = 0; i < 5; i++) g.fill(new Rectangle2D.Double(-42 + i * 10, -13, 2, 26));
        g.setColor(oscuro(a, 0.8));
        g.fill(new RoundRectangle2D.Double(8, -15, 32, 30, 8, 8));
        g.setColor(new Color(0x0E1424));
        g.fill(new RoundRectangle2D.Double(14, -11, 16, 22, 4, 4));
        g.setColor(new Color(90, 216, 255, 170));
        g.fill(new Rectangle2D.Double(31, -11, 6, 22));
        g.setColor(new Color(0x1B1F2E));
        g.fill(new Rectangle2D.Double(50, -12, 3, 24));
        faros(g, 48, -12, 12);
        g.setColor(new Color(255, 40, 60));
        g.fill(new Rectangle2D.Double(-50, -15, 2, 5)); g.fill(new Rectangle2D.Double(-50, 10, 2, 5));
    }
}

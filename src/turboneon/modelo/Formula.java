package turboneon.modelo;

import java.awt.*;
import java.awt.geom.*;

public class Formula extends Vehiculo {
    public Formula() { super(2); }
    @Override public String getNombre() { return "FÓRMULA X"; }
    @Override public String getDescripcion() { return "Velocidad pura. Frágil como una galleta."; }
    @Override public double getVelocidadMax() { return 585; }
    @Override public double getAceleracion() { return 300; }
    @Override public double getManejo() { return 5.5; }
    @Override public double getResistencia() { return 1.5; }

    @Override
    protected void dibujarCarroceria(Graphics2D g, Color a) {
        rueda(g, -34, -15, 18, 9); rueda(g, -34, 15, 18, 9);
        rueda(g, 30, -15, 16, 8);  rueda(g, 30, 15, 16, 8);
        g.setColor(oscuro(a, 0.45));
        g.fill(new RoundRectangle2D.Double(-24, -10, 32, 20, 6, 6));
        Path2D.Double cuerpo = new Path2D.Double();
        cuerpo.moveTo(-46, -5); cuerpo.lineTo(10, -7); cuerpo.lineTo(48, -2);
        cuerpo.lineTo(48, 2); cuerpo.lineTo(10, 7); cuerpo.lineTo(-46, 5); cuerpo.closePath();
        g.setPaint(new GradientPaint(0, -7, claro(a, 0.4), 0, 7, oscuro(a, 0.7)));
        g.fill(cuerpo);
        g.setColor(new Color(0x12162A));
        g.fill(new RoundRectangle2D.Double(40, -21, 9, 42, 3, 3));
        g.fill(new RoundRectangle2D.Double(-50, -19, 7, 38, 3, 3));
        g.setColor(a);
        g.fill(new Rectangle2D.Double(40, -21, 9, 3)); g.fill(new Rectangle2D.Double(40, 18, 9, 3));
        g.fill(new Rectangle2D.Double(-50, -19, 7, 3)); g.fill(new Rectangle2D.Double(-50, 16, 7, 3));
        g.setColor(Color.WHITE);
        g.fill(new Ellipse2D.Double(-9, -5, 10, 10));
        g.setColor(a);
        g.fill(new Rectangle2D.Double(-6, -5, 3, 10));
        g.setColor(new Color(255, 40, 60));
        g.fill(new Rectangle2D.Double(-46, -2, 3, 4));
    }
}

package turboneon.modelo;

import java.awt.*;
import java.awt.geom.*;

public class Deportivo extends Vehiculo {
    public Deportivo() { super(0); }
    @Override public String getNombre() { return "DEPORTIVO"; }
    @Override public String getDescripcion() { return "Equilibrado y elegante. Bueno en todo, rey de nada."; }
    @Override public double getVelocidadMax() { return 520; }
    @Override public double getAceleracion() { return 360; }
    @Override public double getManejo() { return 6.5; }
    @Override public double getResistencia() { return 1.0; }

    @Override
    protected void dibujarCarroceria(Graphics2D g, Color a) {
        rueda(g, -30, -17, 20, 7); rueda(g, -30, 17, 20, 7);
        rueda(g, 28, -17, 20, 7);  rueda(g, 28, 17, 20, 7);
        Path2D.Double cuerpo = new Path2D.Double();
        cuerpo.moveTo(-48, -13); cuerpo.lineTo(10, -15); cuerpo.lineTo(38, -11); cuerpo.lineTo(50, -5);
        cuerpo.lineTo(50, 5); cuerpo.lineTo(38, 11); cuerpo.lineTo(10, 15); cuerpo.lineTo(-48, 13);
        cuerpo.closePath();
        g.setPaint(new GradientPaint(0, -15, claro(a, 0.35), 0, 15, oscuro(a, 0.6)));
        g.fill(cuerpo);
        g.setColor(new Color(255, 255, 255, 190));
        g.fill(new Rectangle2D.Double(-46, -2, 92, 4));
        Path2D.Double cabina = new Path2D.Double();
        cabina.moveTo(-14, -9); cabina.lineTo(6, -10); cabina.lineTo(18, -6);
        cabina.lineTo(18, 6); cabina.lineTo(6, 10); cabina.lineTo(-14, 9); cabina.closePath();
        g.setColor(new Color(0x0E1424)); g.fill(cabina);
        g.setColor(new Color(90, 216, 255, 170));
        g.fill(new Rectangle2D.Double(8, -7, 9, 14));
        g.setColor(oscuro(a, 0.3));
        g.fill(new RoundRectangle2D.Double(-50, -15, 6, 30, 3, 3));
        faros(g, 48, -8, 8);
        g.setColor(new Color(255, 40, 60));
        g.fill(new Rectangle2D.Double(-48, -11, 3, 6)); g.fill(new Rectangle2D.Double(-48, 5, 3, 6));
    }
}
